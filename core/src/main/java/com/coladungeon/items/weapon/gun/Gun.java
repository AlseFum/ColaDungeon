package com.coladungeon.items.weapon.gun;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.weapon.Weapon;
import com.coladungeon.items.weapon.ammo.Ammo;
import com.coladungeon.items.weapon.ammo.Cartridge;
import com.coladungeon.items.weapon.ammo.CartridgeAltFire;
import com.coladungeon.items.weapon.ammo.CartridgeEffect;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteManager;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Gun extends Weapon {

    protected static final String AC_RELOAD = "装弹";
    protected static final String AC_FIRE = "开火";

    protected int ammo = 8;
    protected int maxAmmo = 8;
    public CartridgeEffect car_effect = CartridgeEffect.Normal;
    public Cartridge cartridge;
    protected float reloadTime = 1f;
    public int tier=1;
    {
        image = ItemSpriteManager.ByName("gun");
        cartridge = new Cartridge(maxAmmo, car_effect);
        usesTargeting = true;
        defaultAction = AC_FIRE;
    }
    
    private Gun _this = this;

    @Override
    public String name() {
        return "枪(debug用)";
    }

    @Override
    public String status() {
        return ammo + "/" + maxAmmo;
    }

    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("枪械武器基类，可远程射击和装填弹药。\n");
        desc.append("近战伤害区间：").append(min(0)).append("-").append(max(0)).append("。\n");
        desc.append("当前弹药类型：").append(cartridge != null ? cartridge.cartridgeType : "无").append("。\n");
        desc.append("弹药量：").append(ammo).append("/一般最大弹药量：").append(maxAmmo).append("。\n");
        desc.append("增益：");
        if (cartridge != null && cartridge.onHit != null && cartridge.onHit != CartridgeEffect.Normal) {
            desc.append(cartridge.onHit.name);
        } else {
            desc.append("无");
        }
        return desc.toString();
    }

    protected void execute_ac_fire(Hero hero, String action) {
        if (ammo <= 0) {
            GLog.w("弹药不足！");
            return;
        }
        GameScene.selectCell(shooter);
    }
    public void fire(int targetPos){
        fire(targetPos,true);
    }
    public void fire(int targetPos,boolean shouldSpend) {
        HitResult[] hitResults = fire_hits(curUser, targetPos, Ballistica.PROJECTILE);
        for (HitResult hitResult : hitResults) {
            Object target = hitResult.who();
            if (target instanceof Char targetChar) {
                boolean hit = Random.Float() < accuracyFactor(curUser, targetChar);
                if (hit) {
                    int baseDamage = fire_damage(curUser, targetChar);
                    int actualDamage = fire_proc(curUser, targetChar, baseDamage);
                    targetChar.damage(actualDamage, this);
                    Sample.INSTANCE.play(Assets.Sounds.HIT);
                }else{
                }
            }else{
            }
            // Apply the cartridge's effect
            if(cartridge.onHit!=null){
            cartridge.onHit.onHit.apply(curUser, hitResult.where(), (int) (cartridge.power * getAmmoPowerMultiplier()), 0);
            }
        }
        if(shouldSpend){
            curUser.spendAndNext(1f);
        }
    }

    protected int fire_proc(Char shooter, Char target, int damage) {
        // Subtract the target's defense from the damage
        int defense = target != null && target instanceof Char targetChar ? targetChar.drRoll() : 0;
        int actualDamage = Math.max(damage - defense, 0);
        if(actualDamage<=0){
            GLog.w("刮痧了Pong友");
        }
        return actualDamage;
    }

    protected CellSelector.Listener shooter = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                consumeAmmo(1);
                if (cartridge instanceof CartridgeAltFire catf) {
                    catf.fire(curUser, target, catf, _this);
                } else {
                    fire(target);
                }
                updateQuickslot();
            }
        }

        @Override
        public String prompt() {
            return aiming_prompt();
        }
    };

    protected void execute_ac_reload(Hero hero, String action) {
        if (ammo >= maxAmmo) {
            GLog.w("弹药已满！");
            return;
        }
        // 检查是否有可用的弹药
        List<Ammo> availableAmmo = findAmmo();
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
        updateQuickslot();
    }

    protected void showAmmoSelectionWindow(List<Ammo> availableAmmo) {
        ArrayList<String> ammoNames = new ArrayList<>();
        for (Ammo _ammo : availableAmmo) {
            ammoNames.add(_ammo.name() + " (" + _ammo.quantity() + ")");
        }
        GameScene.show(new WndOptions("选择弹药",
                "选择要装填的弹药类型：",
                ammoNames.toArray(new String[0])) {
            @Override
            protected void onSelect(int index) {
                if (index >= 0 && index < availableAmmo.size()) {
                    reload(availableAmmo.get(index));
                }
                updateQuickslot();
            }
        });
    }

    protected List<Ammo> findAmmo() {
        return Dungeon.hero.belongings.getAllItems(Ammo.class)
                .stream()
                .filter(this::canLoad)
                .collect(Collectors.toList());
    }

    public boolean canLoad(Ammo ammo) {
        return true;
    }

    public void reload(Ammo ammoItem) {
        cartridge
                = ammoItem.cartridge instanceof Cartridge
                && ammoItem.cartridge.onHit != CartridgeEffect.Supply
                        ? ammoItem.cartridge
                        : new Cartridge(maxAmmo, car_effect);
        ammo = ammoItem.full_reload
                ? maxAmmo
                : ammoItem.amount;
        ammoItem.quantity(ammoItem.quantity() - 1);
        if (ammoItem.quantity() <= 0) {
            ammoItem.detach(Dungeon.hero.belongings.backpack);
        }

        curUser.spend(reloadTime);
        curUser.busy();
        curUser.sprite.attack(curUser.pos);
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
    }

    public String aiming_prompt() {
        return "选择射击目标";
    }

    @Override
    public float accuracyFactor(Char owner, Char target) {
        return 1f;
    }

    protected float getAmmoPowerMultiplier() {
        return 1.0f + 0.4f * level();
    }

    protected void consumeAmmo(int amount) {
        ammo = Math.max(0, ammo - amount);
        if (ammo <= 0) {
            GLog.w("弹药耗尽！");
        }
    }

    private static final String AMMO = "ammo";
    private static final String MAX_AMMO = "maxAmmo";

    @Override
    public int min(int lvl) {
        return 2 + lvl;
    }

    @Override
    public int max(int lvl) {
        return 8 + 2 * lvl;
    }

    @Override
    public int STRReq(int lvl) {
        return 8 + lvl;
    }

    public int fire_damage(Char hero, Char target) {
        return (int) (getAmmoPowerMultiplier() * cartridge.power);
    }

    public HitResult[] fire_hits(Char shooter, int targetPos, int projectileType) {
        Ballistica shot = new Ballistica(shooter.pos, targetPos, projectileType);
        return new HitResult[]{new HitResult(shot.collisionPos, Actor.findChar(shot.collisionPos))};
    }

    public static record HitResult(int where, Char who) {

    }

    public static class ShotResult {

        public final boolean hit;           // 是否命中
        public final boolean blocked;       // 是否被阻挡
        public final int damage;            // 造成的伤害
        public final int distance;          // 射击距离
        public final Char target;           // 目标
        public final int collisionPos;      // 碰撞位置
        public final float hitChance;       // 命中率

        public ShotResult(boolean hit, boolean blocked, int damage, int distance,
                Char target, int collisionPos, float hitChance) {
            this.hit = hit;
            this.blocked = blocked;
            this.damage = damage;
            this.distance = distance;
            this.target = target;
            this.collisionPos = collisionPos;
            this.hitChance = hitChance;
        }
    }
    public static ShotResult shoot(Gun gun, Char shooter, int targetPos, Cartridge cartridge, int projectileType) {
        System.out.println("[Gun]shoot Deprecated!");
        Ballistica shot = new Ballistica(shooter.pos, targetPos, projectileType);

        // 检查是否被阻挡
        boolean blocked = shot.collisionPos != targetPos;
        int collisionPos = shot.collisionPos;
        Char target = Actor.findChar(collisionPos);

        boolean hit = !blocked
                && Random.Float() < gun.accuracyFactor(shooter, target);

        // 计算伤害
        int damage = gun.fire_damage(shooter, target);

        // 应用伤害
        //FIXME 应有能复杂的机制
        if (target != null) {
            target.damage(damage, gun);
        }

        if (shooter instanceof Hero shooterHero) {
            cartridge.onHit.onHit.apply(shooterHero, collisionPos, (int) (cartridge.power * gun.getAmmoPowerMultiplier()), damage);
        }
        shooter.sprite.attack(shooter.pos);
        Sample.INSTANCE.play(gun.hitSound, 1, gun.hitSoundPitch);
        return new ShotResult(hit, blocked, damage, shot.dist, target, collisionPos, gun.accuracyFactor(shooter, target));
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(AMMO, ammo);
        bundle.put(MAX_AMMO, maxAmmo);
        // if (cartridge != null && car) {
        //     bundle.put("cartridgeEffect", cartridge.onHit.name);
        //     bundle.put("cartridgePower", cartridge.power);
        //     // Add more fields if needed
        // }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        ammo = bundle.getInt(AMMO);
        maxAmmo = bundle.getInt(MAX_AMMO);
        // Retrieve cartridgeType from cartridgeEffect
        // String cartridgeType = bundle.getString("cartridgeEffect");
        // int cartridgePower = bundle.getInt("cartridgePower");
        // cartridge = new Cartridge(cartridgePower, CartridgeEffect.valueOf(cartridgeType));
        // Initialize more fields if needed
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_FIRE);
        actions.add(AC_RELOAD);
        addSubActions(hero, actions);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_FIRE)) {
            return "射击";
        }
        if (action.equals(AC_RELOAD)) {
            return "装弹";
        }
        if (subActionName(action, hero) != null) {
            return subActionName(action, hero);
        }
        return super.actionName(action, hero);
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        switch (action) {
            case AC_FIRE ->
                execute_ac_fire(hero, action);
            case AC_RELOAD ->
                execute_ac_reload(hero, action);
            default ->
                executeSubAction(hero, action);
        }
    }

    public String subActionName(String action, Hero hero) {
        return null;
    }

    protected void addSubActions(Hero hero, ArrayList<String> actions) {
    }

    protected void executeSubAction(Hero hero, String action) {
    }

}
