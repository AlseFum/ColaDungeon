/*
 * Cola Dungeon
 */

package com.coladungeon.items.weapon.chakram;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Cripple;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.particles.PurpleParticle;
import com.coladungeon.items.Item;
import com.coladungeon.items.weapon.Weapon;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.sprites.MissileSprite;
import com.coladungeon.tiles.DungeonTilemap;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.Visual;
import com.watabou.noosa.tweeners.PosTweener;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class Chakram extends Weapon {

    private static final String AC_THROW = "投掷";
    private static final String AC_POWER_THROW = "蓄力投掷";
    
    {
        image = ItemSpriteSheet.THROWING_KNIFE; // 暂时使用飞刀的图标
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch = 1.4f;
        
        defaultAction = AC_THROW;
        usesTargeting = true;
        
        DLY = 1.0f;
        RCH = 6;
        ACC = 1.2f;
    }
    
    // 飞镖状态枚举
    public enum ChakramState {
        AVAILABLE,   // 可用状态
        THROWN,      // 普通投掷
        POWER_THROWN // 蓄力投掷
    }

    // 当前飞镖状态
    private ChakramState currentState = ChakramState.AVAILABLE;
    
    // 飞镖投掷的目标位置
    private int thrownPosition = -1;

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        
        // 根据不同状态显示不同的动作
        switch (currentState) {
            case AVAILABLE:
                actions.add("投掷");
                actions.add("蓄力投掷");
                break;
            case THROWN:
                // 普通投掷状态下不可操作
                actions.clear();
                break;
            case POWER_THROWN:
                // 蓄力投掷状态下可以召回
                actions.add("召回");
                actions.add("重新充能");
                break;
        }
        
        return actions;
    }
    
    @Override
    public void execute(Hero hero, String action) {
        switch (currentState) {
            case AVAILABLE:
                if (action.equals("投掷")) {
                    GameScene.selectCell(thrower);
                } else if (action.equals("蓄力投掷")) {
                    GameScene.selectCell(powerThrower);
                } else {
                    super.execute(hero, action);
                }
                break;
            case POWER_THROWN:
                if (action.equals("召回")) {
                    // 强制返回飞镖
                    currentState = ChakramState.AVAILABLE;
                    thrownPosition = -1;
                    GLog.p("强制召回飞镖！");
                } else if (action.equals("重新充能")) {
                    GLog.p("飞镖加速充能...");
                    // 可以添加额外的充能逻辑
                }
                break;
        }
    }
    
    private CellSelector.Listener thrower = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                throwChakram(target, false);
            }
        }
        
        @Override
        public String prompt() {
            return "选择投掷目标";
        }
    };
    
    private CellSelector.Listener powerThrower = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                throwChakram(target, true);
            }
        }
        
        @Override
        public String prompt() {
            return "选择蓄力投掷目标";
        }
    };
    
    private void throwChakram(int targetPos, boolean isPowerThrow) {
        Hero curUser = Dungeon.hero;
        final Ballistica shot = new Ballistica(curUser.pos, targetPos, Ballistica.PROJECTILE);
        int cell = shot.collisionPos;
        
        // 更新飞镖状态
        currentState = isPowerThrow ? ChakramState.POWER_THROWN : ChakramState.THROWN;
        thrownPosition = cell;

        curUser.sprite.zap(cell);
        
        // 使用标准的MissileSprite
        ((MissileSprite)curUser.sprite.parent.recycle(MissileSprite.class)).
            reset(curUser.sprite, cell, this, new Callback() {
                @Override
                public void call() {
                    onThrowComplete(cell, isPowerThrow);
                }
            });
        
        // 创建ChakramActor管理飞镖状态
        ChakramActor chakramActor = new ChakramActor(this, curUser.pos, cell, isPowerThrow);
        Actor.add(chakramActor);
        
        Sample.INSTANCE.play(hitSound, 1f, hitSoundPitch);
        curUser.spendAndNext(DLY * (isPowerThrow ? 1.5f : 1f));
    }
    
    private void onThrowComplete(int cell, boolean isPowerThrow) {
        Char enemy = Actor.findChar(cell);
        
        if (enemy != null) {
            // 计算伤害
            int dmg = damageRoll(curUser);
            if (isPowerThrow) {
                dmg *= 1.5f;
            }
            
            // 造成伤害
            enemy.damage(dmg, this);
            
            // 减速效果
            Buff.affect(enemy, Cripple.class, isPowerThrow ? 5f : 3f);
            
            // 特效
            enemy.sprite.burst(0xCC99FFFF, isPowerThrow ? 10 : 5);
            
            // 如果是蓄力投掷，创建持续伤害区域
            if (isPowerThrow) {
                CellEmitter.center(cell).burst(PurpleParticle.BURST, 10);
            }
        }
    }
    
    @Override
    public int min(int lvl) {
        return 3 + 2 * lvl;
    }
    
    @Override
    public int max(int lvl) {
        return 6 + 4 * lvl;
    }
    
    @Override
    public int STRReq(int lvl) {
        return 9 + lvl;
    }
    
    @Override
    public String name() {
        return "巨型飞镖";
    }
    
    @Override
    public String desc() {
        switch (currentState) {
            case THROWN:
                return "飞镖正在飞行中。\n\n" +
                       "_当前状态:_\n" +
                       "- 飞镖已离开，无法控制\n" +
                       "- 正在飞向目标\n" +
                       "- 即将命中敌人\n" +
                       "- 等待自动返回";
            case POWER_THROWN:
                return "飞镖正处于蓄力投掷状态。\n\n" +
                       "_当前状态:_\n" +
                       "- 飞镖已离开，无法控制\n" +
                       "- 在目标区域持续造成伤害\n" +
                       "- 区域内敌人每回合受到伤害\n" +
                       "- 等待自动返回";
            default:
                StringBuilder desc = new StringBuilder();
                desc.append("一种能够自动返回使用者手中的巨型飞镖，投掷时能够穿透敌人的护甲并造成持续伤害。\n\n");
                
                desc.append("_被动效果:_\n");
                desc.append("- 命中敌人时会减缓其移动速度\n");
                desc.append("- 穿透护甲造成额外伤害\n");
                desc.append("- 投掷后会自动返回使用者手中\n\n");
                
                desc.append("_主动技能 - 普通投掷:_\n");
                desc.append("- 投掷飞镖造成单体伤害\n");
                desc.append("- 减速目标3回合\n");
                desc.append("- 飞镖会自动返回\n\n");
                
                desc.append("_主动技能 - 蓄力投掷:_\n");
                desc.append("- 蓄力投掷造成1.5倍伤害\n");
                desc.append("- 减速目标5回合\n");
                desc.append("- 在目标位置产生持续伤害区域\n");
                desc.append("- 区域内的敌人每回合受到伤害\n");
                desc.append("- 持续5回合\n");
                
                return desc.toString();
        }
    }
    
    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals("投掷")) return "投掷";
        if (action.equals("蓄力投掷")) return "蓄力投掷";
        if (action.equals("召回")) return "召回";
        if (action.equals("重新充能")) return "重新充能";
        return super.actionName(action, hero);
    }
    
    // 飞镖Sprite类
    public static class DartSprite extends MissileSprite {
        private Callback customCallback;
        private static final float SPEED = 360f; // 飞镖速度比普通投掷物快50%
        private static final int ANGULAR_SPEED = 1080; // 旋转速度
        
        public void reset(Visual from, Visual to, Item item, Callback listener) {
            customCallback = listener;
            reset(from.center(), to.center(), item, new Callback() {
                @Override
                public void call() {
                    if (customCallback != null) {
                        customCallback.call();
                    }
                    kill(); // 确保sprite被清理
                }
            });
        }
        
        @Override
        public void reset(PointF from, PointF to, Item item, Callback listener) {
            super.reset(from, to, item, listener);
            
            // 设置自定义的速度和旋转
            speed.normalize().scale(SPEED);
            angularSpeed = ANGULAR_SPEED;
        }
    }
    
    // 飞镖Actor，管理飞镖状态和返回
    public static class ChakramActor extends Actor {
        private Chakram chakram;
        private int initialPos;
        private int targetPos;
        private int remainingTurns;
        private boolean isPowerThrow;

        public ChakramActor(Chakram chakram, int initialPos, int targetPos, boolean isPowerThrow) {
            this.chakram = chakram;
            this.initialPos = initialPos;
            this.targetPos = targetPos;
            this.isPowerThrow = isPowerThrow;
            // 普通投掷3回合，蓄力投掷5回合
            this.remainingTurns = isPowerThrow ? 5 : 3;
        }

        @Override
        protected boolean act() {
            remainingTurns--;

            // 如果是蓄力投掷，在目标区域造成持续伤害
            if (isPowerThrow) {
                for (int i : PathFinder.NEIGHBOURS9) {
                    int cell = targetPos + i;
                    if (cell >= 0 && cell < Dungeon.level.length()) {
                        Char ch = Actor.findChar(cell);
                        if (ch != null && ch != Dungeon.hero) {
                            int dmg = Random.NormalIntRange(3, 8);
                            ch.damage(dmg, this);
                            CellEmitter.get(cell).burst(PurpleParticle.BURST, 2);
                        }
                    }
                }
            }

            // 飞镖返回
            if (remainingTurns <= 0) {
                Hero hero = Dungeon.hero;
                
                // 使用MissileSprite返回
                ((MissileSprite)hero.sprite.parent.recycle(MissileSprite.class)).
                    reset(DungeonTilemap.tileCenterToWorld(targetPos), 
                           hero.sprite.center(), 
                           chakram, 
                           new Callback() {
                               @Override
                               public void call() {
                                   // 重置飞镖状态
                                   chakram.currentState = ChakramState.AVAILABLE;
                                   chakram.thrownPosition = -1;
                               }
                           });

                // 移除Actor
                Actor.remove(this);
                return true;
            }

            spend(TICK);
            return true;
        }
    }
} 