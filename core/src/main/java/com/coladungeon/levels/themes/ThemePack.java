package com.coladungeon.levels.themes;

import com.coladungeon.levels.Level;
import com.coladungeon.levels.DeadEndLevel;
import com.coladungeon.levels.themes.sewer.SewerLevel;
import com.coladungeon.levels.themes.sewer.SewerBossLevel;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import java.util.function.BiFunction;

public class ThemePack implements Bundlable {
    // Theme pack data
    public Class<? extends Level> normalLevel;
    public Class<? extends Level> BossLevel;
    
    // Weight function: (depth, branch) -> weight (0=unavailable, >0=available)
    public BiFunction<Integer, Integer, Short> available;

    public ThemePack() {
        this.available = (depth, branch) -> (short) 1;
    }
    
    public ThemePack(Class<? extends Level> normalLevel, Class<? extends Level> bossLevel) {
        this.normalLevel = normalLevel;
        this.BossLevel = bossLevel;
        this.available = (depth, branch) -> (short) 1;
    }
    
    public ThemePack(Class<? extends Level> normalLevel, Class<? extends Level> bossLevel, 
                     BiFunction<Integer, Integer, Short> available) {
        this.normalLevel = normalLevel;
        this.BossLevel = bossLevel;
        this.available = available != null ? available : (depth, branch) -> (short) 1;
    }
    
    public short getWeight(int depth, int branch) {
        return available.apply(depth, branch);
    }
    
    public boolean isAvailable(int depth, int branch) {
        return available.apply(depth, branch) > 0;
    }
    
    public Level getNormalLevel() {
        try {
            return normalLevel.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return new DeadEndLevel();
        }
    }
    public Level getBossLevel() {
        try {
            return BossLevel.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return new DeadEndLevel();
        }
    }
    // Bundle keys for serialization
    private static final String NORMAL_LEVEL = "normal_level";
    private static final String BOSS_LEVEL = "boss_level";
    
    @Override
    public void storeInBundle(Bundle bundle) {
        if (normalLevel != null) {
        bundle.put(NORMAL_LEVEL, normalLevel.getName());
        }
        if (BossLevel != null) {
        bundle.put(BOSS_LEVEL, BossLevel.getName());
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void restoreFromBundle(Bundle bundle) {
        try {
            if (bundle.contains(NORMAL_LEVEL)) {
            String normalLevelName = bundle.getString(NORMAL_LEVEL);
            normalLevel = (Class<? extends Level>) Class.forName(normalLevelName);
            } else {
                normalLevel = SewerLevel.class;
            }
            
            if (bundle.contains(BOSS_LEVEL)) {
                String bossLevelName = bundle.getString(BOSS_LEVEL);
            BossLevel = (Class<? extends Level>) Class.forName(bossLevelName);
            } else {
                BossLevel = SewerBossLevel.class;
            }
        } catch (ClassNotFoundException e) {
            // Fallback to sewer theme if classes can't be found
            normalLevel = SewerLevel.class;
            BossLevel = SewerBossLevel.class;
        }
    }
}
