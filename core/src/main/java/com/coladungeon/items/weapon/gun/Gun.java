/*
 * Cola Dungeon
 */

package com.coladungeon.items.weapon.gun;

import java.util.ArrayList;
import java.util.List;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Burning;
import com.coladungeon.actors.buffs.Chill;
import com.coladungeon.actors.buffs.Poison;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.Lightning;
import com.coladungeon.effects.particles.BlastParticle;
import com.coladungeon.effects.particles.FlameParticle;
import com.coladungeon.effects.particles.SnowParticle;
import com.coladungeon.items.Item;
import com.coladungeon.items.bags.AmmoHolder;
import com.coladungeon.items.weapon.Weapon;
import com.coladungeon.items.weapon.ammo.Ammo;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.tiles.DungeonTilemap;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

/**
 * Gun是所有枪械类武器的基类，实现了通用的射击和装弹逻辑。
 * 
 * 弹药系统特性：
 * 1. 每把枪有自己的弹药容量(maxAmmo)和当前弹药数量(ammo)
 * 2. 枪支可以使用不同类型的弹药，弹药类型存储在loadedAmmoType中
 * 3. 射击时会根据loadedAmmoType产生不同的效果
 * 4. 装弹时，玩家可以从背包中选择要装填的弹药类型
 * 5. 不同弹药类型具有不同的伤害倍率和特殊效果
 */
public abstract class Gun extends Weapon {

    protected static final String AC_RELOAD = "装弹";
    protected static final String AC_SHOOT = "射击";
    
    protected int ammo = 0;
    protected int maxAmmo = 1;
    protected float reloadTime = 1f;
    protected Ammo.AmmoType defaultAmmoType = Ammo.AmmoType.NORMAL;
    protected Ammo.AmmoType loadedAmmoType = Ammo.AmmoType.NORMAL;
    
    {
        image = ItemSpriteSheet.CROSSBOW; // 默认使用十字弩的图标
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch = 1.2f;
        
        usesTargeting = true;
        defaultAction = AC_SHOOT;
    }
    
    // 子类必须实现的方法
    protected abstract void fire(int targetPos);
    
    // 子类可以覆盖的方法，用于添加特殊动作
    protected void addGunActions(Hero hero, ArrayList<String> actions) {
        // 默认只有射击和装弹动作
        // 子类可以覆盖此方法添加更多动作
    }
    
    // 子类可以覆盖的方法，用于处理特殊动作
    protected void executeGunAction(Hero hero, String action) {
        // 默认不处理任何特殊动作
        // 子类可以覆盖此方法处理自己的特殊动作
    }
    
    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SHOOT);
        actions.add(AC_RELOAD);
        addGunActions(hero, actions);
        return actions;
    }
    
    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        
        if (action.equals(AC_SHOOT)) {
            if (ammo <= 0) {
                GLog.w("弹药不足！");
                return;
            }
            
            GameScene.selectCell(shooter);
            
        } else if (action.equals(AC_RELOAD)) {
            if (ammo >= maxAmmo) {
                GLog.w("弹药已满！");
                return;
            }
            
            // 检查是否有可用的弹药
            List<Ammo> availableAmmo = findAllAmmo();
            if (availableAmmo.isEmpty()) {
                GLog.w("没有可用的弹药！");
                return;
            }
            
            // 如果只有一种弹药，直接使用
            if (availableAmmo.size() == 1) {
                reload(availableAmmo.get(0));
            } else {
                // 否则显示选择窗口
                showAmmoSelectionWindow(availableAmmo);
            }
        } else {
            executeGunAction(hero, action);
        }
    }
    
    protected void showAmmoSelectionWindow(List<Ammo> availableAmmo) {
        ArrayList<String> ammoNames = new ArrayList<>();
        for (Ammo ammo : availableAmmo) {
            ammoNames.add(ammo.name() + " (" + ammo.quantity() + ")");
        }
        
        GameScene.show(new WndOptions("选择弹药",
                "选择要装填的弹药类型：",
                ammoNames.toArray(new String[0])) {
            @Override
            protected void onSelect(int index) {
                if (index >= 0 && index < availableAmmo.size()) {
                    reload(availableAmmo.get(index));
                }
            }
        });
    }
    
    protected void reload(Ammo ammoItem) {
        int needAmmo = maxAmmo - ammo;
        int availableAmmo = Math.min(needAmmo, ammoItem.quantity());
        
        ammo += availableAmmo;
        loadedAmmoType = ammoItem.getType(); // 记录装填的弹药类型
        
        ammoItem.quantity(ammoItem.quantity() - availableAmmo);
        if (ammoItem.quantity() <= 0) {
            ammoItem.detach(Dungeon.hero.belongings.backpack);
        }
        
        GLog.p("装填完成！弹药：%d/%d (%s)", ammo, maxAmmo, loadedAmmoType.getName());
        curUser.spend(reloadTime);
        curUser.busy();
        curUser.sprite.operate(curUser.pos);
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
    }
    
    protected List<Ammo> findAllAmmo() {
        List<Ammo> result = new ArrayList<>();
        // 首先从弹药包中查找
        for (AmmoHolder holder : Dungeon.hero.belongings.getAllItems(AmmoHolder.class)) {
            for (Item item : holder.items) {
                if (item instanceof Ammo) {
                    result.add((Ammo)item);
                }
            }
        }
        // 如果弹药包中没有，再从背包中查找
        for (Ammo ammo : Dungeon.hero.belongings.getAllItems(Ammo.class)) {
            result.add(ammo);
        }
        return result;
    }
    
    protected Ammo findAmmo() {
        // 首先从弹药包中查找
        for (AmmoHolder holder : Dungeon.hero.belongings.getAllItems(AmmoHolder.class)) {
            for (Item item : holder.items) {
                if (item instanceof Ammo && ((Ammo)item).getType() == defaultAmmoType) {
                    return (Ammo)item;
                }
            }
        }
        // 如果弹药包中没有，再从背包中查找
        for (Ammo ammo : Dungeon.hero.belongings.getAllItems(Ammo.class)) {
            if (ammo.getType() == defaultAmmoType) {
                return ammo;
            }
        }
        return null;
    }
    
    protected CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                fire(target);
            }
        }
        
        @Override
        public String prompt() {
            return "选择射击目标";
        }
    };
    
    protected void shootBeam(int targetPos) {
        final Ballistica shot = new Ballistica(curUser.pos, targetPos, Ballistica.PROJECTILE);
        int cell = shot.collisionPos;
        
        curUser.sprite.zap(cell);
        Sample.INSTANCE.play(hitSound, 1, hitSoundPitch);
        
        // 添加射击光束特效
        curUser.sprite.parent.add(new Lightning(
            curUser.sprite.center(),
            DungeonTilemap.raisedTileCenterToWorld(cell),
            null
        ));
    }
    
    // 根据弹药类型应用特殊效果
    protected void applyAmmoEffects(Char target, int damage) {
        if (target == null) return;
        
        switch (loadedAmmoType) {
            case NORMAL:
                // 普通弹药没有特殊效果
                break;
            case PIERCING:
                // 穿甲弹无视部分护甲
                int armorReduction = Math.min(5, target.drRoll());
                if (armorReduction > 0) {
                    damage += armorReduction;
                    target.damage(armorReduction, this);
                    target.sprite.showStatus(0xCCCCCC, "护甲穿透!");
                }
                break;
            case EXPLOSIVE:
                // 爆炸弹造成范围伤害
                CellEmitter.center(target.pos).burst(BlastParticle.FACTORY, 30);
                Sample.INSTANCE.play(Assets.Sounds.BLAST);
                
                for (int i : PathFinder.NEIGHBOURS8) {
                    int pos = target.pos + i;
                    Char ch = Actor.findChar(pos);
                    if (ch != null && ch != Dungeon.hero) {
                        ch.damage(Math.max(1, damage/3), this);
                    }
                }
                break;
            case INCENDIARY:
                // 燃烧弹点燃目标
                Buff.affect(target, Burning.class).reignite(target);
                CellEmitter.get(target.pos).burst(FlameParticle.FACTORY, 20);
                break;
            case FROST:
                // 冰冻弹减速目标
                Buff.affect(target, Chill.class, 5f);
                CellEmitter.get(target.pos).burst(SnowParticle.FACTORY, 20);
                break;
            case POISON:
                // 毒气弹造成中毒
                Buff.affect(target, Poison.class).set(3);
                // 使用其他可用的粒子效果
                CellEmitter.get(target.pos).burst(FlameParticle.FACTORY, 20);
                break;
        }
    }
    
    // 获取弹药伤害倍率
    protected float getAmmoDamageMultiplier() {
        return loadedAmmoType.getDamageMultiplier();
    }
    
    protected void consumeAmmo(int amount) {
        ammo = Math.max(0, ammo - amount);
        if (ammo <= 0) {
            GLog.w("弹药耗尽！");
        }
    }
    
    @Override
    public String status() {
        return ammo + "/" + maxAmmo + " " + loadedAmmoType.getName().substring(0, 1);
    }
    
    private static final String AMMO = "ammo";
    private static final String MAX_AMMO = "maxAmmo";
    private static final String DEFAULT_AMMO_TYPE = "defaultAmmoType";
    private static final String LOADED_AMMO_TYPE = "loadedAmmoType";
    
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(AMMO, ammo);
        bundle.put(MAX_AMMO, maxAmmo);
        bundle.put(DEFAULT_AMMO_TYPE, defaultAmmoType.name());
        bundle.put(LOADED_AMMO_TYPE, loadedAmmoType.name());
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        ammo = bundle.getInt(AMMO);
        maxAmmo = bundle.getInt(MAX_AMMO);
        
        // 安全地恢复 defaultAmmoType
        try {
            String defaultAmmoTypeName = bundle.getString(DEFAULT_AMMO_TYPE);
            defaultAmmoType = Ammo.AmmoType.valueOf(defaultAmmoTypeName);
        } catch (Exception e) {
            GLog.w("无法恢复默认弹药类型，使用NORMAL");
            defaultAmmoType = Ammo.AmmoType.NORMAL;
        }
        
        // 安全地恢复 loadedAmmoType
        try {
            String loadedAmmoTypeName = bundle.getString(LOADED_AMMO_TYPE);
            loadedAmmoType = Ammo.AmmoType.valueOf(loadedAmmoTypeName);
        } catch (Exception e) {
            GLog.w("无法恢复已装填弹药类型，使用默认类型");
            loadedAmmoType = defaultAmmoType;
        }
    }
    
    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_SHOOT)) return "射击";
        if (action.equals(AC_RELOAD)) return "装弹";
        return super.actionName(action, hero);
    }
    
    /**
     * 公共方法：将弹药填充到最大
     * 这个方法提供给外部类使用，不需要访问protected字段
     */
    public void fillAmmo() {
        this.ammo = this.maxAmmo;
    }
    
    /**
     * 公共方法：设置弹药类型
     * 这个方法允许在不重新装填的情况下设置弹药类型
     */
    public void setAmmoType(Ammo.AmmoType type) {
        this.loadedAmmoType = type;
    }
    
    /**
     * 公共方法：完全准备枪械
     * 填充弹药并设置弹药类型
     */
    public void prepareGun(Ammo.AmmoType type) {
        this.ammo = this.maxAmmo;
        this.loadedAmmoType = type;
    }
} 