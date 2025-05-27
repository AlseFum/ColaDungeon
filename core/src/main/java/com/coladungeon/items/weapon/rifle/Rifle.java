/*
 * Cola Dungeon
 */

package com.coladungeon.items.weapon.rifle;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.Lightning;
import com.coladungeon.items.Item;
import com.coladungeon.items.weapon.ammo.Ammo;
import com.coladungeon.items.weapon.gun.Gun;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.tiles.DungeonTilemap;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Rifle extends Gun {

    // 确保 AmmoType 枚举的有效性
    static {
        try {
            // 验证 NORMAL 类型是否存在
            Ammo.AmmoType normal = Ammo.AmmoType.NORMAL;
            if (normal == null) {
                GLog.w("警告: AmmoType.NORMAL 为 null，可能导致存档问题");
            }
        } catch (Exception e) {
            GLog.w("警告: AmmoType 枚举初始化异常，可能导致存档问题");
        }
    }

    // 初始化方法，确保默认值正确设置
    private void init() {
        try {
            // 确保弹药类型被正确初始化
            if (defaultAmmoType == null) {
                defaultAmmoType = Ammo.AmmoType.NORMAL;
            }
            if (loadedAmmoType == null) {
                loadedAmmoType = defaultAmmoType;
            }
            
            // 确保弹药数量合理
            if (ammo < 0) ammo = 0;
            if (ammo > maxAmmo) ammo = maxAmmo;
            
        } catch (Exception e) {
            GLog.w("步枪初始化异常: " + e.getMessage());
            // 强制设置默认值
            defaultAmmoType = Ammo.AmmoType.NORMAL;
            loadedAmmoType = Ammo.AmmoType.NORMAL;
            ammo = Math.min(Math.max(0, ammo), maxAmmo);
        }
    }

    private static final int SHOTS_PER_BURST = 3; // 每次射击发射的子弹数
    private static final int SPRAY_AMMO_COST = 10; // 扫射消耗的弹药量
    private static final float SPRAY_CHANCE = 0.4f; // 扫射命中周围格子的基础概率

    private static final String AC_SHOOT = "点射";
    private static final String AC_SPRAY = "扫射";
    private static final String AC_RELOAD = "装弹";

    {
        image = ItemSpriteSheet.CROSSBOW; // 暂时使用十字弩的图标
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch = 1.2f;

        DLY = 0.75f; // 射击间隔
        RCH = 8; // 射程
        ACC = 1.1f; // 命中率
        
        defaultAction = AC_SHOOT; // 默认动作为射击
        
        maxAmmo = 30;
        ammo = maxAmmo;
        reloadTime = 1.5f;
        defaultAmmoType = Ammo.AmmoType.NORMAL;
        loadedAmmoType = defaultAmmoType;
        
        usesTargeting = true;
        
        // 调用初始化方法确保一致性
        init();
    }

    @Override
    public String name() {
        return "步枪";
    }

    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("一把自动步枪，可以连续发射多发子弹。\n\n");
        
        desc.append("_被动效果:_\n");
        desc.append("- 基础命中率提升10%\n");
        desc.append("- 射程8格\n\n");
        
        desc.append("_主动技能 - 点射:_\n");
        desc.append("- 一次发射3发子弹\n");
        desc.append("- 每发子弹独立计算伤害\n");
        desc.append("- 消耗3发弹药\n\n");
        
        desc.append("_主动技能 - 扫射:_\n");
        desc.append("- 以目标为中心进行范围扫射\n");
        desc.append("- 中心点必定命中\n");
        desc.append("- 周围8格有40%几率命中敌人\n");
        desc.append("- 消耗10发弹药\n\n");
        
        desc.append("_弹药系统:_\n");
        desc.append("- 最大弹药：").append(maxAmmo).append("发\n");
        desc.append("- 当前弹药：").append(ammo).append("发\n");
        desc.append("- 装弹时间：1秒\n\n");
        
        desc.append("_升级效果:_\n");
        desc.append("- 基础伤害：").append(min()).append("-").append(max()).append("\n");
        desc.append("- 力量需求：").append(STRReq()).append("\n");
        desc.append("- 每级提升：\n");
        desc.append("  * 最小伤害+2\n");
        desc.append("  * 最大伤害+4\n");
        desc.append("  * 力量需求+0.5");
        
        return desc.toString();
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SHOOT);
        actions.add(AC_SPRAY);
        actions.add(AC_RELOAD);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_SHOOT)) return "点射";
        if (action.equals(AC_SPRAY)) return "扫射";
        if (action.equals(AC_RELOAD)) return "装弹";
        return super.actionName(action, hero);
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        
        if (action.equals(AC_SHOOT)) {
            if (ammo < SHOTS_PER_BURST) {
                GLog.w("弹药不足！需要%d发子弹。", SHOTS_PER_BURST);
                return;
            }
            
            // 进入选择目标模式
            GameScene.selectCell(shooter);
            
        } else if (action.equals(AC_SPRAY)) {
            if (ammo < SPRAY_AMMO_COST) {
                GLog.w("弹药不足！需要%d发子弹。", SPRAY_AMMO_COST);
                return;
            }
            
            // 进入选择目标模式
            GameScene.selectCell(sprayer);
            
        } else if (action.equals(AC_RELOAD)) {
            if (ammo >= maxAmmo) {
                GLog.w("弹药已满！");
                return;
            }
            reload();
        }
    }

    private void reload() {
        ammo = maxAmmo;
        GLog.p("重新装填完成！弹药：%d/%d", ammo, maxAmmo);
        Item.curUser.spend(1f);
        Item.curUser.busy();
        Item.curUser.sprite.operate(Item.curUser.pos);
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
    }

    private CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                fire(target);
            }
        }
        
        @Override
        public String prompt() {
            return "选择点射目标";
        }
    };

    private CellSelector.Listener sprayer = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                spray(target);
            }
        }
        
        @Override
        public String prompt() {
            return "选择扫射中心点";
        }
    };

    @Override
    protected void fire(int targetPos) {
        if (ammo < SHOTS_PER_BURST) {
            GLog.w("弹药不足！需要%d发子弹。", SHOTS_PER_BURST);
            return;
        }

        final Ballistica shot = new Ballistica(Item.curUser.pos, targetPos, Ballistica.PROJECTILE);
        int cell = shot.collisionPos;

        ammo -= SHOTS_PER_BURST;

        Item.curUser.sprite.zap(cell);
        Sample.INSTANCE.play(Assets.Sounds.HIT);

        Item.curUser.sprite.parent.add(new Lightning(Item.curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(cell), null));

        Char enemy = Actor.findChar(cell);
        if (enemy != null) {
            for (int i = 0; i < SHOTS_PER_BURST; i++) {
                int damage = damageRoll(Item.curUser);
                enemy.damage(damage, this);
                
                if (i < SHOTS_PER_BURST - 1) {
                    float pitch = 0.8f + Random.Float(0.4f);
                    Sample.INSTANCE.play(Assets.Sounds.HIT, pitch);
                }
            }
        }
        
        Item.curUser.spendAndNext(delayFactor(Item.curUser));
    }

    private void spray(int targetPos) {
        if (ammo < SPRAY_AMMO_COST) {
            GLog.w("弹药不足！需要%d发子弹。", SPRAY_AMMO_COST);
            return;
        }

        ammo -= SPRAY_AMMO_COST;

        Item.curUser.sprite.zap(targetPos);
        Sample.INSTANCE.play(Assets.Sounds.HIT);

        boolean[] passable = Dungeon.level.passable;
        
        Char centerEnemy = Actor.findChar(targetPos);
        if (centerEnemy != null) {
            int damage = damageRoll(Item.curUser);
            centerEnemy.damage(damage, this);
            Item.curUser.sprite.parent.add(new Lightning(Item.curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(targetPos), null));
        }

        int[] neighbours = PathFinder.NEIGHBOURS8;
        for (int i = 0; i < neighbours.length; i++) {
            int pos = targetPos + neighbours[i];
            if (passable[pos]) {
                Char enemy = Actor.findChar(pos);
                if (enemy != null && Random.Float() < SPRAY_CHANCE) {
                    int damage = damageRoll(Item.curUser);
                    enemy.damage(damage, this);
                    Item.curUser.sprite.parent.add(new Lightning(
                        DungeonTilemap.raisedTileCenterToWorld(targetPos),
                        DungeonTilemap.raisedTileCenterToWorld(pos),
                        null));
                    Sample.INSTANCE.play(Assets.Sounds.HIT, 0.8f + Random.Float(0.4f));
                }
            }
        }

        Item.curUser.spendAndNext(delayFactor(Item.curUser));
    }

    @Override
    public int STRReq(int lvl) {
        return 8 + Math.round(lvl * 0.5f); // 较低的力量需求
    }

    @Override
    public int min(int lvl) {
        return 3 + 2 * lvl; // 较低的单发伤害
    }

    @Override
    public int max(int lvl) {
        return 8 + 4 * lvl; // 较低的单发伤害
    }

    @Override
    public String status() {
        return ammo + "/" + maxAmmo;
    }

    private static final String AMMO = "ammo";
    
    @Override
    public void storeInBundle(Bundle bundle) {
        try {
            super.storeInBundle(bundle);
        } catch (Exception e) {
            GLog.w("存储步枪状态时出错: " + e.getMessage());
        }
        
        // 确保存储弹药数量
        bundle.put(AMMO, ammo);
        
        // 明确存储弹药类型，以防父类没有存储或存储出错
        try {
            if (loadedAmmoType != null) {
                bundle.put("loadedAmmoType", loadedAmmoType.name());
            } else {
                bundle.put("loadedAmmoType", Ammo.AmmoType.NORMAL.name());
            }
            
            if (defaultAmmoType != null) {
                bundle.put("defaultAmmoType", defaultAmmoType.name());
            } else {
                bundle.put("defaultAmmoType", Ammo.AmmoType.NORMAL.name());
            }
        } catch (Exception e) {
            GLog.w("存储弹药类型时出错，使用默认值");
            bundle.put("loadedAmmoType", "NORMAL");
            bundle.put("defaultAmmoType", "NORMAL");
        }
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        try {
            super.restoreFromBundle(bundle);
        } catch (IllegalArgumentException e) {
            // 如果是 AmmoType 枚举解析错误，设置默认值
            if (e.getMessage() != null && e.getMessage().contains("AmmoType")) {
                GLog.w("存档中的弹药类型无效，已重置为默认值");
                loadedAmmoType = Ammo.AmmoType.NORMAL;
                defaultAmmoType = Ammo.AmmoType.NORMAL;
            } else {
                // 其他错误则继续抛出
                throw e;
            }
        }
        
        // 确保从 bundle 中读取 ammo 值
        if (bundle.contains(AMMO)) {
            ammo = bundle.getInt(AMMO);
        } else {
            ammo = 0; // 默认值
        }
        
        // 调用初始化方法确保一致性
        init();
    }
} 