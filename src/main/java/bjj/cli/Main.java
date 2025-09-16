package bjj.cli;

import bjj.data.MoveCatalog;
import bjj.domain.Move;
import bjj.domain.MoveFamily;
import bjj.domain.Position;
import bjj.domain.State;
import bjj.engine.Analytics;
import bjj.engine.SimParams;
import bjj.engine.Simulator;
import bjj.engine.StepResult;
import bjj.data.JsonMoveCatalog;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Long seed = askSeed(sc);
        Random rng = seed == null ? new Random() : new Random(seed);

        MoveCatalog catalog = new JsonMoveCatalog();
        Simulator sim = new Simulator();
        Analytics analytics = new Analytics();

        int resistance = askResistance(sc);
        boolean advanced = askYesNo(sc, "Advanced settings (per-family skills + fatigue)? (y/N): ");

        Map<MoveFamily, Integer> skills = advanced ? askSkills(sc) : defaultSkills();
        int fatigue = advanced ? askFatigue(sc) : 0;

        SimParams params = new SimParams(resistance, skills, fatigue);

        Position start = chooseStart(sc);
        State state = new State(start, Set.of(), 0L, 0.0);

        System.out.println("=== BJJ Simulator ===");
        if (seed != null)
            System.out.println("Seed: " + seed);
        System.out.println("Resistance: " + resistance + (advanced ? " | Fatigue: " + fatigue : ""));
        if (advanced) {
            System.out.print("Skills: ");
            boolean first = true;
            for (MoveFamily f : MoveFamily.values()) {
                if (!first)
                    System.out.print(", ");
                System.out.print(f + "=" + skills.get(f));
                first = false;
            }
            System.out.println();
        }
        System.out.println("Starting position: " + state.position());

        List<String> history = new ArrayList<>();
        boolean endedBySubmission = false;

        while (true) {
            List<Move> eligible = sim.eligibleMoves(state, catalog.all());
            if (eligible.isEmpty()) {
                System.out.println("No eligible moves. Ending session.");
                break;
            }

            System.out.println("\nEligible moves:");
            for (int i = 0; i < eligible.size(); i++) {
                Move m = eligible.get(i);
                System.out.printf("%d) %s [%s diff %d]\n", i + 1, m.name(), m.family(), m.difficulty());
            }
            System.out.println("0) Quit");
            System.out.println("(r) Set resistance");
            if (advanced) {
                System.out.println("(s) Set skills");
                System.out.println("(f) Set fatigue");
            }
            System.out.println("(g) Set seed");

            System.out.print("Choose: ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("r")) {
                resistance = askResistance(sc);
                params = new SimParams(resistance, skills, fatigue);
                System.out.println("Resistance set to " + resistance);
                continue;
            }
            if (advanced && input.equalsIgnoreCase("s")) {
                skills = askSkills(sc);
                params = new SimParams(resistance, skills, fatigue);
                System.out.println("Skills updated.");
                continue;
            }
            if (advanced && input.equalsIgnoreCase("f")) {
                fatigue = askFatigue(sc);
                params = new SimParams(resistance, skills, fatigue);
                System.out.println("Fatigue set to " + fatigue);
                continue;
            }
            if (input.equalsIgnoreCase("g")) {
                seed = askSeed(sc);
                rng = seed == null ? new Random() : new Random(seed);
                System.out.println(seed == null ? "Seed cleared (randomized)." : ("Seed set to " + seed));
                continue;
            }

            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (Exception e) {
                continue;
            }
            if (choice == 0)
                break;
            if (choice < 1 || choice > eligible.size())
                continue;

            Move selected = eligible.get(choice - 1);
            StepResult result = sim.step(state, selected, params, rng);
            state = result.newState();

            analytics.record(selected, result);

            String line = String.format("%s -> %s (%d ms) | pos: %s | t=%d",
                    selected.name(), result.outcome(), result.durationMs(), state.position(), state.timelineMs());
            history.add(line);
            System.out.println("-> " + line);

            if ("SUCCESS".equals(String.valueOf(result.outcome())) && selected.family() == MoveFamily.SUBMISSION) {
                System.out.println("Submission. Opponent tapped. Session over.");
                endedBySubmission = true;
                break;
            }
        }

        System.out.println("\nSession ended. Final position: " + state.position());
        System.out.println("Total time elapsed: " + state.timelineMs() + " ms");
        if (endedBySubmission) {
            System.out.println("End reason: SUBMISSION");
        }
        if (!history.isEmpty()) {
            System.out.println("\nHistory:");
            for (String h : history)
                System.out.println(" - " + h);
        }

        System.out.println("\nAnalytics:");
        System.out.println(analytics.render());
    }

    private static Long askSeed(Scanner sc) {
        System.out.print("Seed (blank = random): ");
        String s = sc.nextLine().trim();
        if (s.isEmpty())
            return null;
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return null;
        }
    }

    private static int askResistance(Scanner sc) {
        System.out.print("Set resistance 0 (easy) to 100 (hard) [default 50]: ");
        String s = sc.nextLine().trim();
        try {
            int v = Integer.parseInt(s);
            if (v < 0)
                v = 0;
            if (v > 100)
                v = 100;
            return v;
        } catch (Exception e) {
            return 50;
        }
    }

    private static Map<MoveFamily, Integer> askSkills(Scanner sc) {
        Map<MoveFamily, Integer> map = new EnumMap<>(MoveFamily.class);
        for (MoveFamily fam : MoveFamily.values()) {
            System.out.printf("Skill for %s (0–100, default 50): ", fam);
            String s = sc.nextLine().trim();
            int val;
            try {
                val = Integer.parseInt(s);
            } catch (Exception e) {
                val = 50;
            }
            if (val < 0)
                val = 0;
            if (val > 100)
                val = 100;
            map.put(fam, val);
        }
        return map;
    }

    private static int askFatigue(Scanner sc) {
        System.out.print("Set fatigue rate 0 (none) to 100 (max) [default 0]: ");
        String s = sc.nextLine().trim();
        try {
            int v = Integer.parseInt(s);
            if (v < 0)
                v = 0;
            if (v > 100)
                v = 100;
            return v;
        } catch (Exception e) {
            return 0;
        }
    }

    private static Map<MoveFamily, Integer> defaultSkills() {
        Map<MoveFamily, Integer> map = new EnumMap<>(MoveFamily.class);
        for (MoveFamily fam : MoveFamily.values())
            map.put(fam, 50);
        return map;
    }

    private static boolean askYesNo(Scanner sc, String prompt) {
        System.out.print(prompt);
        String s = sc.nextLine().trim();
        return s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes");
    }

    private static Position chooseStart(Scanner sc) {
        Position[] all = Position.values();
        System.out.println("Choose starting position:");
        for (int i = 0; i < all.length; i++) {
            System.out.printf("%d) %s\n", i + 1, all[i]);
        }
        System.out.print("Enter number: ");
        try {
            int idx = Integer.parseInt(sc.nextLine());
            if (idx >= 1 && idx <= all.length)
                return all[idx - 1];
        } catch (Exception ignored) {
        }
        return Position.CLOSED_GUARD_BOTTOM;
    }
}
