package bjj.engine;

import bjj.domain.*;

import java.util.*;
import java.util.stream.Collectors;

public final class Simulator {

    public List<Move> eligibleMoves(State state, Collection<Move> all) {
        List<Move> list = new ArrayList<>();
        for (Move mv : all) {
            if (mv.fromPosition() != state.position())
                continue;
            if (!state.flags().containsAll(mv.requiredFlags()))
                continue;
            boolean anyForbidden = false;
            for (ControlFlag f : mv.forbiddenFlags()) {
                if (state.flags().contains(f)) {
                    anyForbidden = true;
                    break;
                }
            }
            if (anyForbidden)
                continue;
            list.add(mv);
        }
        return list;
    }

    public StepResult step(State state, Move move, SimParams params, Random rng) {
        long dur = DurationSampler.triangular(rng, move.minMs(), move.typMs(), move.maxMs());

        int userSkill = params.skillByFamily().getOrDefault(move.family(), 50);
        double diffTerm = (userSkill - move.difficulty()) / move.scale();
        double resistanceAdj = (params.resistance() - 50.0) / 12.0;
        double fatigueAdj = (params.fatigueRate() / 100.0) * state.riskBuffer() * 2.0;

        double p = Probability.sigmoid(diffTerm - resistanceAdj - fatigueAdj);

        double roll = rng.nextDouble();
        Outcome out;
        State next;

        if (roll < p) {
            out = Outcome.SUCCESS;
            next = state.with(move.toPosition(), state.flags(), dur, Math.max(0.0, state.riskBuffer() - 0.05));
        } else {
            double roll2 = rng.nextDouble();
            if (roll2 < move.counterChance()) {
                out = Outcome.FAIL_COUNTER;
                next = state.with(state.position(), state.flags(), dur, Math.min(1.0, state.riskBuffer() + 0.15));
            } else {
                if (rng.nextDouble() < 0.20) {
                    out = Outcome.PARTIAL;
                    next = state.with(state.position(), state.flags(), dur, state.riskBuffer());
                } else {
                    out = Outcome.FAIL;
                    next = state.with(state.position(), state.flags(), dur, Math.min(1.0, state.riskBuffer() + 0.05));
                }
            }
        }
        return new StepResult(out, next, dur, move.id());
    }

    public String formatEligible(List<Move> moves) {
        return moves.stream()
                .map(m -> m.id() + " | " + m.name() + " | " + m.family() + " | diff " + m.difficulty())
                .collect(Collectors.joining("\n"));
    }
}
