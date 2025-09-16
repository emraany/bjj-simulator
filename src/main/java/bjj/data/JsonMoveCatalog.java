package bjj.data;

import bjj.catalog.MoveCatalogLoader;
import bjj.domain.*;

import java.io.IOException;
import java.util.*;

public final class JsonMoveCatalog implements MoveCatalog {
    private final Map<String, Move> moves;

    public JsonMoveCatalog() {
        try {
            MoveCatalogLoader.Catalog cat = MoveCatalogLoader.loadFromResources();
            Map<String, Move> m = new LinkedHashMap<>();
            for (MoveCatalogLoader.MoveDTO dto : cat.moves) {
                Position from = Position.valueOf(dto.from);

                // Handle "END" specially: submissions end the match instead of mapping to a
                // position
                Position to = null;
                if (dto.outcomes != null && dto.outcomes.success != null && dto.outcomes.success.to != null) {
                    if (!"END".equals(dto.outcomes.success.to)) {
                        to = Position.valueOf(dto.outcomes.success.to);
                    }
                }

                MoveFamily fam = MoveFamily.valueOf(dto.family);

                // Difficulty derived from success probability
                int difficulty = (int) Math.round(100 - (dto.prob.success * 100));

                Move move = new Move(
                        dto.id,
                        dto.name,
                        from,
                        to,
                        fam,
                        difficulty,
                        new HashSet<>(dto.tags == null ? List.of() : dto.tags),
                        Set.of(), // requiredFlags placeholder
                        Set.of(), // forbiddenFlags placeholder
                        dto.duration.min * 1000L,
                        ((dto.duration.min + dto.duration.max) / 2) * 1000L,
                        dto.duration.max * 1000L,
                        10.0,
                        null, // counterMoveId placeholder
                        0.0 // counterChance placeholder
                );

                m.put(dto.id, move);
            }
            this.moves = Collections.unmodifiableMap(m);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load moves.json", e);
        }
    }

    @Override
    public Collection<Move> all() {
        return moves.values();
    }

    @Override
    public Optional<Move> byId(String id) {
        return Optional.ofNullable(moves.get(id));
    }
}
