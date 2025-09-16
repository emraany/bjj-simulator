package bjj.engine;

import bjj.domain.Move;
import bjj.domain.MoveFamily;

import java.util.EnumMap;
import java.util.Map;

public final class Analytics {
    private final Map<MoveFamily, Integer> succ = new EnumMap<>(MoveFamily.class);
    private final Map<MoveFamily, Integer> part = new EnumMap<>(MoveFamily.class);
    private final Map<MoveFamily, Integer> fail = new EnumMap<>(MoveFamily.class);
    private long totalMs = 0;

    public Analytics() {
        for (MoveFamily f : MoveFamily.values()) {
            succ.put(f, 0);
            part.put(f, 0);
            fail.put(f, 0);
        }
    }

    public void record(Move move, StepResult r) {
        MoveFamily f = move.family();
        String o = String.valueOf(r.outcome());
        if ("SUCCESS".equals(o)) {
            succ.put(f, succ.get(f) + 1);
        } else if ("PARTIAL".equals(o)) {
            part.put(f, part.get(f) + 1);
        } else {
            fail.put(f, fail.get(f) + 1);
        }
        totalMs = r.newState().timelineMs();
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("Total time: ").append(totalMs).append(" ms\n");
        sb.append(String.format("%-12s %-8s %-8s %-8s %-8s\n", "Family", "Succ", "Part", "Fail", "Rate"));
        for (MoveFamily f : MoveFamily.values()) {
            int s = succ.get(f);
            int p = part.get(f);
            int fa = fail.get(f);
            int tot = s + p + fa;
            double rate = tot == 0 ? 0.0 : (s / (double) tot);
            sb.append(String.format("%-12s %-8d %-8d %-8d %-8.2f\n", f, s, p, fa, rate));
        }
        return sb.toString();
    }
}
