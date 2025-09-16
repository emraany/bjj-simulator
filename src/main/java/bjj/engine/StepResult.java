package bjj.engine;

import bjj.domain.State;

public final class StepResult {
    private final Outcome outcome;
    private final State newState;
    private final long durationMs;
    private final String moveId;

    public StepResult(Outcome outcome, State newState, long durationMs, String moveId) {
        this.outcome = outcome;
        this.newState = newState;
        this.durationMs = durationMs;
        this.moveId = moveId;
    }

    public Outcome outcome() {
        return outcome;
    }

    public State newState() {
        return newState;
    }

    public long durationMs() {
        return durationMs;
    }

    public String moveId() {
        return moveId;
    }
}
