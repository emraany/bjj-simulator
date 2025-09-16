package bjj.engine;

import bjj.domain.MoveFamily;

import java.util.EnumMap;
import java.util.Map;

public final class SimParams {
    private final int resistance;
    private final Map<MoveFamily, Integer> skillByFamily;
    private final int fatigueRate;

    public SimParams(int resistance) {
        this(resistance, defaultSkills(), 0);
    }

    public SimParams(int resistance, Map<MoveFamily, Integer> skillByFamily, int fatigueRate) {
        this.resistance = clamp01(resistance);
        this.skillByFamily = new EnumMap<>(skillByFamily);
        this.fatigueRate = clamp01(fatigueRate);
    }

    public int resistance() {
        return resistance;
    }

    public Map<MoveFamily, Integer> skillByFamily() {
        return skillByFamily;
    }

    public int fatigueRate() {
        return fatigueRate;
    }

    private static Map<MoveFamily, Integer> defaultSkills() {
        Map<MoveFamily, Integer> map = new EnumMap<>(MoveFamily.class);
        for (MoveFamily fam : MoveFamily.values()) {
            map.put(fam, 50);
        }
        return map;
    }

    private static int clamp01(int v) {
        if (v < 0)
            return 0;
        if (v > 100)
            return 100;
        return v;
    }
}
