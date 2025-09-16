package bjj.catalog;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CatalogValidationTest {

    @Test
    void loadsAndValidatesCatalog() throws Exception {
        MoveCatalogLoader.Catalog cat = MoveCatalogLoader.loadFromResources();
        assertNotNull(cat);
        assertNotNull(cat.moves);
        assertFalse(cat.moves.isEmpty());
    }
}
