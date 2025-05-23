package com.coladungeon.levels.themes;

import com.coladungeon.levels.Level;
import com.coladungeon.levels.DeadEndLevel;
import com.coladungeon.levels.SewerLevel;
import com.coladungeon.levels.SewerBossLevel;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class ThemePack implements Bundlable {
    // Theme pack data
    public Class<? extends Level> normalLevel;
    public Class<? extends Level> BossLevel;

    // Default constructor
    public ThemePack() {
    }
    
    // Constructor with level classes
    public ThemePack(Class<? extends Level> normalLevel, Class<? extends Level> bossLevel) {
        this.normalLevel = normalLevel;
        this.BossLevel = bossLevel;
    }
    
    /**
     * Creates a new instance of the normal level
     * 
     * @return A new instance of the normal level, or DeadEndLevel if instantiation
     *         fails
     */
    public Level getNormalLevel() {
        try {
            return normalLevel.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return new DeadEndLevel();
        }
    }
    
    /**
     * Creates a new instance of the boss level
     * 
     * @return A new instance of the boss level, or DeadEndLevel if instantiation
     *         fails
     */
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
