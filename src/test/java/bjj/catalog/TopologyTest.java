package bjj.catalog;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class TopologyTest {
    @Test
    void catalogHasRequiredCoverage() throws Exception {
        MoveCatalogLoader.Catalog cat = MoveCatalogLoader.loadFromResources();
        Map<String, List<MoveCatalogLoader.MoveDTO>> byFrom = new HashMap<>();
        for (MoveCatalogLoader.MoveDTO m : cat.moves) {
            byFrom.computeIfAbsent(m.from, k -> new ArrayList<>()).add(m);
        }

        List<String> issues = new ArrayList<>();

        String[] positions = {
                "STANDING",
                "CLOSED_GUARD_TOP", "CLOSED_GUARD_BOTTOM",
                "OPEN_GUARD_TOP", "OPEN_GUARD_BOTTOM",
                "HALF_GUARD_TOP", "HALF_GUARD_BOTTOM",
                "SIDE_CONTROL_TOP", "SIDE_CONTROL_BOTTOM",
                "MOUNT_TOP", "MOUNT_BOTTOM",
                "BACK_CONTROL_TOP", "BACK_CONTROL_BOTTOM",
                "TURTLE_TOP", "TURTLE_BOTTOM",
                "KNEE_ON_BELLY_TOP", "KNEE_ON_BELLY_BOTTOM",
                "NORTH_SOUTH_TOP", "NORTH_SOUTH_BOTTOM"
        };

        for (String p : positions) {
            if (byFrom.getOrDefault(p, List.of()).isEmpty()) {
                issues.add("no moves from " + p);
            }
        }

        if (countFamily(byFrom.get("STANDING"), "ENTRY") < 1) {
            issues.add("STANDING needs ENTRY");
        }

        String[] tops = {
                "CLOSED_GUARD_TOP", "OPEN_GUARD_TOP", "HALF_GUARD_TOP", "SIDE_CONTROL_TOP",
                "MOUNT_TOP", "BACK_CONTROL_TOP", "TURTLE_TOP", "KNEE_ON_BELLY_TOP", "NORTH_SOUTH_TOP"
        };
        for (String p : tops) {
            List<MoveCatalogLoader.MoveDTO> ms = byFrom.getOrDefault(p, List.of());
            if (countFamily(ms, "PASS") + countFamily(ms, "TRANSITION") < 1) {
                issues.add(p + " needs advance option");
            }
            if (countFamily(ms, "SUBMISSION") < 1) {
                issues.add(p + " needs a finish");
            }
        }

        String[] bottoms = {
                "CLOSED_GUARD_BOTTOM", "OPEN_GUARD_BOTTOM", "HALF_GUARD_BOTTOM", "SIDE_CONTROL_BOTTOM",
                "MOUNT_BOTTOM", "BACK_CONTROL_BOTTOM", "TURTLE_BOTTOM", "KNEE_ON_BELLY_BOTTOM", "NORTH_SOUTH_BOTTOM"
        };
        for (String p : bottoms) {
            List<MoveCatalogLoader.MoveDTO> ms = byFrom.getOrDefault(p, List.of());
            if (countFamily(ms, "SWEEP") + countFamily(ms, "ESCAPE") < 1) {
                issues.add(p + " needs get-out option");
            }
            if (countFamily(ms, "SUBMISSION") + countFamily(ms, "SWEEP") < 1) {
                issues.add(p + " needs offense or improvement");
            }
        }

        if (!issues.isEmpty()) {
            fail(String.join("\n", issues));
        }
    }

    private static int countFamily(List<MoveCatalogLoader.MoveDTO> ms, String fam) {
        if (ms == null)
            return 0;
        int c = 0;
        for (MoveCatalogLoader.MoveDTO m : ms)
            if (fam.equals(m.family))
                c++;
        return c;
    }
}
