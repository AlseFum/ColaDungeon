package com.coladungeon.items.weapon.drone;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.items.Item;
import com.coladungeon.items.weapon.Weapon;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.DroneSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.sprites.MissileSprite;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

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
    private static final String AC_ACTIVATE = "ACTIVATE";

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

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ACTIVATE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_ACTIVATE)) {
            if (hero.belongings.backpack.contains(this)) {
                GameScene.selectCell(activator);
            }
        }
    }

    protected static CellSelector.Listener activator = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                ((Drone)curItem).activateAt(target);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(Drone.class, "prompt");
        }
    };

    protected void activateAt(final int cell) {
        curUser.sprite.zap(cell);
        curUser.busy();

        ((MissileSprite)curUser.sprite.parent.recycle(MissileSprite.class)).
                reset(curUser.sprite,
                        cell,
                        this,
                        new Callback() {
                            @Override
                            public void call() {
                                Char ch = Actor.findChar(cell);
                                int targetCell = cell;
                                
                                if (ch != null) {
                                    // 如果扔到了角色身上，就掉在脚下
                                    targetCell = ch.pos;
                                }

                                // 在目标位置生成DroneActor
                                DroneActor drone = new DroneActor();
                                drone.pos = targetCell;
                                Dungeon.level.occupyCell(drone);
                                
                                // 将drone添加到游戏中
                                GameScene.add(drone, 1f);

                                // 创建并给予控制器
                                DroneController controller = new Drone.DroneController();
                                controller.setDrone(drone);
                                if (controller.collect(curUser.belongings.backpack)) {
                                    GLog.i("You activated the drone.");
                                    detach(curUser.belongings.backpack);
                                } else {
                                    GLog.w("No space for controller.");
                                    // 如果无法获得控制器，drone就会变成普通的扔出状态
                                    onThrow(targetCell);
                                }
                                
                                curUser.spendAndNext(1f);
                            }
                        });
    }

    public static class DroneController extends Item {

        private int dronePos = -1;
        private DroneActor controlledDrone = null;

        {
            image = ItemSpriteSheet.WAND_HOLDER; // 使用一个临时的sprite
            stackable = false;
            unique = true;
            defaultAction = AC_RECALL;
        }

        private static final String AC_RECALL = "RECALL";

        @Override
        public ArrayList<String> actions(Hero hero) {
            ArrayList<String> actions = super.actions(hero);
            actions.add(AC_RECALL);
            return actions;
        }

        @Override
        public void execute(Hero hero, String action) {
            super.execute(hero, action);

            if (action.equals(AC_RECALL)) {
                if (controlledDrone != null && controlledDrone.isAlive()) {
                    // 召回drone
                    Drone drone = new Drone();
                    if (drone.collect(hero.belongings.backpack)) {
                        Sample.INSTANCE.play(Assets.Sounds.ITEM);
                        GLog.i("You recalled the drone.");
                        controlledDrone.destroy();
                        controlledDrone.sprite.die();
                        detach(hero.belongings.backpack);
                    }
                } else {
                    GLog.w("No drone to recall.");
                }
            }
        }

        public void setDrone(DroneActor drone) {
            this.controlledDrone = drone;
            if (drone != null) {
                this.dronePos = drone.pos;
            } else {
                this.dronePos = -1;
            }
        }

        private static final String DRONE_POS = "drone_pos";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DRONE_POS, dronePos);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            dronePos = bundle.getInt(DRONE_POS);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public boolean isIdentified() {
            return true;
        }
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