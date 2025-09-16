package bjj.data;

import bjj.domain.Move;

import java.util.Collection;
import java.util.Optional;

public interface MoveCatalog {
    Collection<Move> all();

    Optional<Move> byId(String id);
}
