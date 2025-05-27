/*
 * Cola Dungeon
 */

package com.coladungeon.items.weapon.dart;

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
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class BigDart extends Gun {

    private static final String AC_POWER_SHOT = "蓄力射击";
    
    {
        image = ItemSpriteSheet.THROWING_KNIFE; // 暂时使用飞刀的图标
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch = 1.4f;
        
        defaultAction = AC_SHOOT;
        
        maxAmmo = 5;
        ammo = maxAmmo;
        reloadTime = 1.5f;
        defaultAmmoType = Ammo.AmmoType.PIERCING;
        
        DLY = 1.0f;
        RCH = 6;
        ACC = 1.2f;
    }
    
    @Override
    protected void addGunActions(Hero hero, ArrayList<String> actions) {
        actions.add(AC_POWER_SHOT);
    }
    
    @Override
    protected void executeGunAction(Hero hero, String action) {
        if (action.equals(AC_POWER_SHOT)) {
            if (ammo < 2) {
                GLog.w("弹药不足！需要至少2发弹药进行蓄力射击！");
                return;
            }
            GameScene.selectCell(new CellSelector.Listener() {
                @Override
                public void onSelect(Integer target) {
                    if (target != null) {
                        powerShot(target);
                    }
                }
                
                @Override
                public String prompt() {
                    return "选择蓄力射击目标";
                }
            });
        }
    }
    
    private void powerShot(int targetPos) {
        if (ammo < 2) return;
        
        final Ballistica shot = new Ballistica(curUser.pos, targetPos, Ballistica.PROJECTILE);
        int cell = shot.collisionPos;
        
        curUser.sprite.zap(cell);
        Sample.INSTANCE.play(hitSound, 1.2f, hitSoundPitch);
        
        curUser.sprite.operate(curUser.pos, () -> {
            // 创建持续伤害Actor
            DartDamageActor damageActor = new DartDamageActor(cell);
            Actor.add(damageActor);
            
            // 特效
            CellEmitter.center(cell).burst(PurpleParticle.BURST, 10);
            
            consumeAmmo(2);
            curUser.spendAndNext(DLY * 1.5f);
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
                Buff.affect(ch, Cripple.class, 3f);
            }
            
            CellEmitter.center(cell).burst(PurpleParticle.BURST, 5);
            consumeAmmo(1);
            curUser.spendAndNext(DLY);
        });
    }
    
    @Override
    public String name() {
        return "巨型飞镖";
    }
    
    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("一把发射巨型飞镖的武器，飞镖能够穿透敌人的护甲并造成持续伤害。\n\n");
        
        desc.append("_被动效果:_\n");
        desc.append("- 命中敌人时会减缓其移动速度\n");
        desc.append("- 穿透护甲造成额外伤害\n\n");
        
        desc.append("_主动技能 - 普通射击:_\n");
        desc.append("- 发射一枚巨型飞镖\n");
        desc.append("- 造成单体伤害并减速\n");
        desc.append("- 消耗1发弹药\n\n");
        
        desc.append("_主动技能 - 蓄力射击:_\n");
        desc.append("- 发射一枚特殊处理的飞镖\n");
        desc.append("- 在目标位置产生持续伤害区域\n");
        desc.append("- 区域内的敌人每回合受到伤害\n");
        desc.append("- 持续5回合\n");
        desc.append("- 消耗2发弹药\n\n");
        
        desc.append("_弹药系统:_\n");
        desc.append("- 最大弹药：").append(maxAmmo).append("发\n");
        desc.append("- 当前弹药：").append(ammo).append("发\n");
        desc.append("- 装弹时间：1.5秒\n\n");
        
        desc.append("_升级效果:_\n");
        desc.append("- 基础伤害：").append(min()).append("-").append(max()).append("\n");
        desc.append("- 力量需求：").append(STRReq()).append("\n");
        desc.append("- 每级提升：\n");
        desc.append("  * 最小伤害+3\n");
        desc.append("  * 最大伤害+5");
        
        return desc.toString();
    }
    
    @Override
    public int STRReq(int lvl) {
        return 10 + lvl;
    }
    
    @Override
    public int min(int lvl) {
        return 8 + 3 * lvl;
    }
    
    @Override
    public int max(int lvl) {
        return 16 + 5 * lvl;
    }
    
    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_SHOOT)) return "射击";
        if (action.equals(AC_POWER_SHOT)) return "蓄力射击";
        if (action.equals(AC_RELOAD)) return "装弹";
        return super.actionName(action, hero);
    }
    
    // 持续伤害Actor类
    public static class DartDamageActor extends Actor {
        
        private int pos;
        private int duration = 5;
        private float damageTimer = 1f;
        
        public DartDamageActor(int pos) {
            this.pos = pos;
        }
        
        @Override
        protected boolean act() {
            if (duration <= 0) {
                Actor.remove(this);
                return true;
            }
            
            // 每回合对区域内的敌人造成伤害
            if (damageTimer <= 0) {
                damageTimer = 1f;
                
                // 对中心点和周围一圈造成伤害
                for (int i : PathFinder.NEIGHBOURS9) {
                    int cell = pos + i;
                    if (cell >= 0 && cell < Dungeon.level.length()) {
                        Char ch = Actor.findChar(cell);
                        if (ch != null && ch != Dungeon.hero) {
                            int dmg = Random.NormalIntRange(3, 8);
                            ch.damage(dmg, this);
                            
                            // 特效
                            CellEmitter.get(cell).burst(PurpleParticle.BURST, 2);
                        }
                    }
                }
                
                duration--;
            }
            
            damageTimer -= 1f;
            spend(TICK);
            return true;
        }
        
        private static final String POS = "pos";
        private static final String DURATION = "duration";
        private static final String DAMAGE_TIMER = "damageTimer";
        
        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, pos);
            bundle.put(DURATION, duration);
            bundle.put(DAMAGE_TIMER, damageTimer);
        }
        
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            pos = bundle.getInt(POS);
            duration = bundle.getInt(DURATION);
            damageTimer = bundle.getFloat(DAMAGE_TIMER);
        }
    }
} 