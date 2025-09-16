package bjj.domain;

import java.util.*;

public final class Move {
    private final String id;
    private final String name;
    private final Position fromPosition;
    private final Position toPosition;
    private final MoveFamily family;
    private final int difficulty;
    private final Set<String> skillTags;
    private final Set<ControlFlag> requiredFlags;
    private final Set<ControlFlag> forbiddenFlags;
    private final long minMs;
    private final long typMs;
    private final long maxMs;
    private final double scale;
    private final String counterMoveId;
    private final double counterChance;

    public Move(
            String id,
            String name,
            Position fromPosition,
            Position toPosition,
            MoveFamily family,
            int difficulty,
            Set<String> skillTags,
            Set<ControlFlag> requiredFlags,
            Set<ControlFlag> forbiddenFlags,
            long minMs,
            long typMs,
            long maxMs,
            double scale,
            String counterMoveId,
            double counterChance) {
        this.id = id;
        this.name = name;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.family = family;
        this.difficulty = difficulty;
        this.skillTags = Collections.unmodifiableSet(new HashSet<>(skillTags));
        this.requiredFlags = Collections.unmodifiableSet(new HashSet<>(requiredFlags));
        this.forbiddenFlags = Collections.unmodifiableSet(new HashSet<>(forbiddenFlags));
        this.minMs = minMs;
        this.typMs = typMs;
        this.maxMs = maxMs;
        this.scale = scale;
        this.counterMoveId = counterMoveId;
        this.counterChance = counterChance;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Position fromPosition() {
        return fromPosition;
    }

    public Position toPosition() {
        return toPosition;
    }

    public MoveFamily family() {
        return family;
    }

    public int difficulty() {
        return difficulty;
    }

    public Set<String> skillTags() {
        return skillTags;
    }

    public Set<ControlFlag> requiredFlags() {
        return requiredFlags;
    }

    public Set<ControlFlag> forbiddenFlags() {
        return forbiddenFlags;
    }

    public long minMs() {
        return minMs;
    }

    public long typMs() {
        return typMs;
    }

    public long maxMs() {
        return maxMs;
    }

    public double scale() {
        return scale;
    }

    public String counterMoveId() {
        return counterMoveId;
    }

    public double counterChance() {
        return counterChance;
    }
}
