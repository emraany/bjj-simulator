package bjj.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.InputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class MoveCatalogLoader {
    public static Catalog loadFromResources() throws IOException {
        try (InputStream in = MoveCatalogLoader.class.getClassLoader().getResourceAsStream("catalog/moves.json")) {
            if (in == null)
                throw new IOException("catalog/moves.json not found on classpath");
            Catalog cat = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
                    .readValue(in, Catalog.class);
            validate(cat);
            return cat;
        }
    }

    public static void validate(Catalog cat) {
        if (cat == null)
            throw new IllegalArgumentException("null catalog");
        if (cat.version == null || cat.version.isBlank())
            throw new IllegalArgumentException("missing version");
        if (cat.moves == null || cat.moves.isEmpty())
            throw new IllegalArgumentException("empty moves");
        Set<String> ids = new HashSet<>();
        for (MoveDTO m : cat.moves) {
            if (m.id == null || m.id.isBlank())
                throw new IllegalArgumentException("move id missing");
            if (!ids.add(m.id))
                throw new IllegalArgumentException("duplicate id: " + m.id);
            if (m.name == null || m.name.isBlank())
                throw new IllegalArgumentException("name missing for: " + m.id);
            if (!isValidFamily(m.family))
                throw new IllegalArgumentException("invalid family: " + m.family + " in " + m.id);
            if (!isValidPosition(m.from))
                throw new IllegalArgumentException("invalid from position: " + m.from + " in " + m.id);
            if (m.prob == null)
                throw new IllegalArgumentException("prob missing for: " + m.id);
            if (m.prob.success < 0 || m.prob.success > 1)
                throw new IllegalArgumentException("prob.success out of range in " + m.id);
            if (m.prob.partial < 0 || m.prob.partial > 1)
                throw new IllegalArgumentException("prob.partial out of range in " + m.id);
            if (m.outcomes == null || m.outcomes.success == null || m.outcomes.fail == null)
                throw new IllegalArgumentException("outcomes missing in " + m.id);
            if (!isValidOutcome(m.outcomes.success.to))
                throw new IllegalArgumentException("invalid success.to in " + m.id);
            if (m.outcomes.partial != null && !isValidOutcome(m.outcomes.partial.to))
                throw new IllegalArgumentException("invalid partial.to in " + m.id);
            if (!isValidOutcome(m.outcomes.fail.to))
                throw new IllegalArgumentException("invalid fail.to in " + m.id);
            if ("SUBMISSION".equals(m.family)) {
                if (!"END".equals(m.outcomes.success.to))
                    throw new IllegalArgumentException("submission must END on success: " + m.id);
                if ("END".equals(toOrNull(m.outcomes.partial)))
                    throw new IllegalArgumentException("partial cannot END: " + m.id);
                if ("END".equals(m.outcomes.fail.to))
                    throw new IllegalArgumentException("fail cannot END: " + m.id);
            } else {
                if ("END".equals(m.outcomes.success.to))
                    throw new IllegalArgumentException("only submissions may END: " + m.id);
            }
            if (m.duration == null)
                throw new IllegalArgumentException("duration missing in " + m.id);
            if (m.duration.min < 1 || m.duration.max < 1 || m.duration.min > m.duration.max)
                throw new IllegalArgumentException("invalid duration in " + m.id);
            if (m.points < 0)
                throw new IllegalArgumentException("points negative in " + m.id);
        }
        Set<String> referencedPositions = new HashSet<>();
        for (MoveDTO m : cat.moves) {
            addIfPosition(referencedPositions, m.outcomes.success.to);
            if (m.outcomes.partial != null)
                addIfPosition(referencedPositions, m.outcomes.partial.to);
            addIfPosition(referencedPositions, m.outcomes.fail.to);
        }
        Set<String> fromPositions = cat.moves.stream().map(x -> x.from).collect(Collectors.toSet());
        Set<String> allMentioned = new HashSet<>(fromPositions);
        allMentioned.addAll(referencedPositions);
        if (allMentioned.isEmpty())
            throw new IllegalArgumentException("no positions referenced");
    }

    private static void addIfPosition(Set<String> set, String to) {
        if (to != null && !"END".equals(to) && isValidPosition(to))
            set.add(to);
    }

    private static boolean isValidOutcome(String to) {
        return "END".equals(to) || isValidPosition(to);
    }

    private static boolean isValidFamily(String f) {
        return f != null && switch (f) {
            case "ENTRY", "PASS", "SWEEP", "ESCAPE", "TRANSITION", "SUBMISSION" -> true;
            default -> false;
        };
    }

    private static boolean isValidPosition(String p) {
        if (p == null)
            return false;
        return switch (p) {
            case "STANDING",
                    "CLOSED_GUARD_TOP", "CLOSED_GUARD_BOTTOM",
                    "OPEN_GUARD_TOP", "OPEN_GUARD_BOTTOM",
                    "HALF_GUARD_TOP", "HALF_GUARD_BOTTOM",
                    "SIDE_CONTROL_TOP", "SIDE_CONTROL_BOTTOM",
                    "MOUNT_TOP", "MOUNT_BOTTOM",
                    "BACK_CONTROL_TOP", "BACK_CONTROL_BOTTOM",
                    "TURTLE_TOP", "TURTLE_BOTTOM",
                    "KNEE_ON_BELLY_TOP", "KNEE_ON_BELLY_BOTTOM",
                    "NORTH_SOUTH_TOP", "NORTH_SOUTH_BOTTOM" ->
                true;
            default -> false;
        };
    }

    public static final class Catalog {
        public String version;
        public List<MoveDTO> moves;
    }

    public static final class MoveDTO {
        public String id;
        public String name;
        public String family;
        public String from;
        public Prob prob;
        public Outcomes outcomes;
        public int points;
        public List<String> tags;
        public Duration duration;
    }

    public static final class Prob {
        public double success;
        public double partial;
    }

    public static final class Outcomes {
        public Outcome success;
        public Outcome partial;
        public Outcome fail;
    }

    public static final class Outcome {
        public String to;
    }

    public static final class Duration {
        public int min;
        public int max;
    }

    private static String toOrNull(Outcome o) {
        return o == null ? null : o.to;
    }
}
