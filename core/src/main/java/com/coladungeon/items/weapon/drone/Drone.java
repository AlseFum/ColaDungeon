package com.coladungeon.items.weapon.drone;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.items.Item;
import com.coladungeon.items.weapon.Weapon;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.DroneSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Drone extends Weapon {

    public int pos = 0;

    {
        image = ItemSpriteSheet.ARMOR_HOLDER; // 使用一个存在的sprite
        defaultAction = AC_THROW;
        unique = true;
    }

    @Override
    public int min(int lvl) {
        return 1;
    }

    @Override
    public int max(int lvl) {
        return 3;
    }

    @Override
    public int STRReq(int lvl) {
        return 8;
    }

    private static final String AC_THROW = "THROW";

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public Item random() {
        return this;
    }

    @Override
    protected void onThrow(int cell) {
        Char ch = Actor.findChar(cell);
        
        if (ch != null) {
            // 如果扔到了角色身上，就掉在脚下
            cell = ch.pos;
        }

        // 在目标位置生成DroneActor
        DroneActor drone = new DroneActor();
        drone.pos = cell;
        Dungeon.level.occupyCell(drone);
        
        // 将drone添加到游戏中
        GameScene.add(drone, 1f);
    }

    private static final String POS = "pos";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(POS, pos);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        pos = bundle.getInt(POS);
    }

    public static class DroneActor extends Mob {

        {
            spriteClass = DroneSprite.class; // 使用专门的DroneSprite
            
            HP = HT = 20;
            defenseSkill = 5;
            
            alignment = Alignment.ALLY;
            
            WANDERING = new Wandering();
            state = WANDERING;
        }
        
        @Override
        public int attackSkill(Char target) {
            return 10;
        }
        
        @Override
        public int damageRoll() {
            return Random.NormalIntRange(2, 4);
        }
        
        @Override
        protected boolean act() {
            if (HP <= 0) {
                die(null);
                return true;
            }
            
            return super.act();
        }
        
        @Override
        public void die(Object cause) {
            super.die(cause);
            
            // 死亡时掉落Drone物品
            Drone drone = new Drone();
            Dungeon.level.drop(drone, pos);
        }
        
        @Override
        public boolean interact(Char ch) {
            if (ch instanceof Hero && Dungeon.level.adjacent(ch.pos, pos)) {
                Hero hero = (Hero)ch;
                // 生成drone物品并让角色捡起
                Drone drone = new Drone();
                if (drone.collect(hero.belongings.backpack)) {
                    // 播放捡起音效
                    Sample.INSTANCE.play(Assets.Sounds.ITEM);
                    // 显示捡起提示
                    GLog.i("You picked up the drone.");
                    // 移除drone actor
                    destroy();
                    sprite.die();
                    return true;
                }
            }
            return super.interact(ch);
        }
        
        private class Wandering extends Mob.Wandering {
            @Override
            public boolean act(boolean enemyInFOV, boolean justAlerted) {
                if (!enemyInFOV) {
                    // 没有敌人时随机移动
                    return super.act(false, false);
                } else {
                    // 有敌人时进入战斗状态
                    state = HUNTING;
                    target = enemy.pos;
                    return true;
                }
            }
        }
        
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put("state_name", state.toString());
        }
        
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            String stateName = bundle.getString("state_name");
            if ("WANDERING".equals(stateName)) {
                state = WANDERING;
            } else if ("HUNTING".equals(stateName)) {
                state = HUNTING;
            }
        }
    }
} 