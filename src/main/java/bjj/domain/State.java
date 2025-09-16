package bjj.domain;

import java.util.*;

public final class State {
    private final Position position;
    private final Set<ControlFlag> flags;
    private final long timelineMs;
    private final double riskBuffer;

    public State(Position position, Set<ControlFlag> flags, long timelineMs, double riskBuffer) {
        this.position = position;
        this.flags = Collections.unmodifiableSet(new HashSet<>(flags));
        this.timelineMs = timelineMs;
        this.riskBuffer = riskBuffer;
    }

    public Position position() {
        return position;
    }

    public Set<ControlFlag> flags() {
        return flags;
    }

    public long timelineMs() {
        return timelineMs;
    }

    public double riskBuffer() {
        return riskBuffer;
    }

    public State with(Position newPos, Set<ControlFlag> newFlags, long addMs, double newRiskBuffer) {
        return new State(newPos, newFlags, this.timelineMs + addMs, newRiskBuffer);
    }
}
