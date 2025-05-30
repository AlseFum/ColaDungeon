package com.coladungeon.items.weapon.proj;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Cripple;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.particles.PurpleParticle;
import com.coladungeon.items.weapon.gun.Gun;
import com.coladungeon.items.weapon.ammo.Ammo;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.coladungeon.actors.blobs.Blob;
import com.coladungeon.actors.blobs.ToxicGas;
import com.coladungeon.actors.blobs.Fire;
import com.coladungeon.actors.blobs.Freezing;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Proj extends Gun {

    private static final String AC_SPECIAL = "特殊射击";
    
    {
        image = ItemSpriteSheet.THROWING_KNIFE; // 暂时使用飞刀的图标
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch = 1.4f;
        
        defaultAction = AC_FIRE;
        
        maxAmmo = 10;
        ammo = maxAmmo;
        reloadTime = 1.0f;
        
        DLY = 0.5f;
        RCH = 8;
        ACC = 1.5f;
    }
    
    @Override
    protected void addSubActions(Hero hero, ArrayList<String> actions) {
        actions.add(AC_SPECIAL);
    }
    
    @Override
    protected void executeSubAction(Hero hero, String action) {
        if (action.equals(AC_SPECIAL)) {
            if (ammo < 3) {
                GLog.w("弹药不足！需要至少3发弹药进行特殊射击！");
                return;
            }
            GameScene.selectCell(new CellSelector.Listener() {
                @Override
                public void onSelect(Integer target) {
                    if (target != null) {
                        specialShot(target);
                    }
                }
                
                @Override
                public String prompt() {
                    return "选择特殊射击目标";
                }
            });
        }
    }
    
    private void specialShot(int targetPos) {
        if (ammo < 3) return;
        
        final Ballistica shot = new Ballistica(curUser.pos, targetPos, Ballistica.PROJECTILE);
        int cell = shot.collisionPos;
        
        curUser.sprite.zap(cell);
        Sample.INSTANCE.play(hitSound, 1.2f, hitSoundPitch);
        
        curUser.sprite.operate(curUser.pos, () -> {
            // 创建持续效果Actor
            ProjEffectActor effectActor = new ProjEffectActor(cell);
            Actor.add(effectActor);
            
            // 特效
            CellEmitter.center(cell).burst(PurpleParticle.BURST, 15);
            
            consumeAmmo(3);
            curUser.spendAndNext(DLY * 2f);
        });
    }
    
    @Override
    protected void fire(int targetPos) {
        if (ammo <= 0) {
            GLog.w("弹药不足！");
            return;
        }
        
        final Ballistica shot = new Ballistica(curUser.pos, targetPos, Ballistica.PROJECTILE);
        int cell = shot.collisionPos;
        
        curUser.sprite.zap(cell);
        Sample.INSTANCE.play(hitSound, 1f, hitSoundPitch);
        
        curUser.sprite.operate(curUser.pos, () -> {
            Char ch = Actor.findChar(cell);
            if (ch != null) {
                int dmg = Math.round(Random.NormalIntRange(min(), max()));
                ch.damage(dmg, this);
                
                // 减速效果
                Buff.affect(ch, Cripple.class, 2f);
            }
            
            // 生成小范围Blob效果
            GameScene.add(Blob.seed(cell, 10, ToxicGas.class));
            
            CellEmitter.center(cell).burst(PurpleParticle.BURST, 8);
            consumeAmmo(1);
            curUser.spendAndNext(DLY);
        });
    }
    
    @Override
    public String name() {
        return "投射器";
    }
    
    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("一种特殊的投射武器，可以产生持续的区域效果。\n\n");
        
        desc.append("_被动效果:_\n");
        desc.append("- 命中时产生毒气\n");
        desc.append("- 高精准度和射程\n\n");
        
        desc.append("_主动技能 - 普通射击:_\n");
        desc.append("- 发射一枚投射物\n");
        desc.append("- 造成单体伤害并产生毒气\n");
        desc.append("- 消耗1发弹药\n\n");
        
        desc.append("_主动技能 - 特殊射击:_\n");
        desc.append("- 发射一枚特殊投射物\n");
        desc.append("- 在目标位置产生持续的混合效果区域\n");
        desc.append("- 随机产生毒气、火焰或冰冻效果\n");
        desc.append("- 持续8回合\n");
        desc.append("- 消耗3发弹药\n\n");
        
        desc.append("_弹药系统:_\n");
        desc.append("- 最大弹药：").append(maxAmmo).append("发\n");
        desc.append("- 当前弹药：").append(ammo).append("发\n");
        desc.append("- 装弹时间：1秒\n\n");
        
        desc.append("_升级效果:_\n");
        desc.append("- 基础伤害：").append(min()).append("-").append(max()).append("\n");
        desc.append("- 力量需求：").append(STRReq()).append("\n");
        desc.append("- 每级提升：\n");
        desc.append("  * 最小伤害+2\n");
        desc.append("  * 最大伤害+4");
        
        return desc.toString();
    }
    
    @Override
    public int STRReq(int lvl) {
        return 8 + lvl;
    }
    
    @Override
    public int min(int lvl) {
        return 6 + 2 * lvl;
    }
    
    @Override
    public int max(int lvl) {
        return 12 + 4 * lvl;
    }
    
    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_FIRE)) return "射击";
        if (action.equals(AC_SPECIAL)) return "特殊射击";
        if (action.equals(AC_RELOAD)) return "装弹";
        return super.actionName(action, hero);
    }
    
    // 持续效果Actor类
    public static class ProjEffectActor extends Actor {
        
        private int pos;
        private int duration = 8;
        private float effectTimer = 1f;
        
        public ProjEffectActor(int pos) {
            this.pos = pos;
        }
        
        @Override
        protected boolean act() {
            if (duration <= 0) {
                Actor.remove(this);
                return true;
            }
            
            // 每回合产生不同的Blob效果
            if (effectTimer <= 0) {
                effectTimer = 1f;
                
                // 在中心点和周围产生随机Blob效果
                for (int i : PathFinder.NEIGHBOURS9) {
                    int cell = pos + i;
                    if (cell >= 0 && cell < Dungeon.level.length()) {
                        // 随机选择Blob类型
                        switch(Random.Int(3)) {
                            case 0:
                                GameScene.add(Blob.seed(cell, 15, ToxicGas.class));
                                break;
                            case 1:
                                GameScene.add(Blob.seed(cell, 15, Fire.class));
                                break;
                            case 2:
                                GameScene.add(Blob.seed(cell, 15, Freezing.class));
                                break;
                        }
                        
                        // 特效
                        CellEmitter.get(cell).burst(PurpleParticle.BURST, 3);
                    }
                }
                
                duration--;
            }
            
            effectTimer -= 1f;
            spend(TICK);
            return true;
        }
        
        private static final String POS = "pos";
        private static final String DURATION = "duration";
        private static final String EFFECT_TIMER = "effectTimer";
        
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, pos);
            bundle.put(DURATION, duration);
            bundle.put(EFFECT_TIMER, effectTimer);
        }
        
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            pos = bundle.getInt(POS);
            duration = bundle.getInt(DURATION);
            effectTimer = bundle.getFloat(EFFECT_TIMER);
        }
    }
} 