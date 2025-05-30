package com.coladungeon.items.weapon.bfg;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.blobs.Blob;
import com.coladungeon.actors.blobs.Fire;
import com.coladungeon.actors.blobs.Freezing;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.particles.FlameParticle;
import com.coladungeon.effects.particles.RainbowParticle;
import com.coladungeon.effects.particles.SmokeParticle;
import com.coladungeon.effects.particles.SnowParticle;
import com.coladungeon.effects.particles.SparkParticle;
import com.coladungeon.items.Item;
import com.coladungeon.items.wands.Wand;
import com.coladungeon.items.wands.WandOfFireblast;
import com.coladungeon.items.wands.WandOfFrost;
import com.coladungeon.items.wands.WandOfLightning;
import com.coladungeon.items.weapon.gun.Gun;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
public class BigFockingGun extends Gun {

    private static final String AC_LOAD = "装填法杖";
    private static final String AC_UNLOAD = "卸下法杖";

    private Wand loadedWand = null;

    {
        // 使用原生的武器贴图
        image = ItemSpriteSheet.GREATAXE;
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch = 0.8f;

        defaultAction = AC_FIRE;

        maxAmmo = 1;
        ammo = 0;
        reloadTime = 2.0f;
        //defaultAmmoType = Ammo.AmmoType.NORMAL;

        DLY = 1.5f;
        RCH = 12;
        ACC = 2f;
    }

    @Override
    protected void addSubActions(Hero hero, ArrayList<String> actions) {
        actions.remove(Gun.AC_RELOAD);
        if (loadedWand == null) {
            actions.add(AC_LOAD);
        } else {
            actions.add(AC_UNLOAD);
        }
    }

    @Override
    protected void executeSubAction(Hero hero, String action) {
        switch (action) {
            case AC_LOAD:
                GameScene.selectItem(new WndBag.ItemSelector() {
                    @Override
                    public String textPrompt() {
                        return "选择要装填的法杖";
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return item instanceof Wand;
                    }

                    @Override
                    public void onSelect(Item item) {
                        if (item != null && item instanceof Wand) {
                            loadedWand = (Wand) item;
                            item.detach(curUser.belongings.backpack);
                            GLog.i("你将" + item.name() + "装填进BigFockingGun。");
                            ammo=1;
                            updateQuickslot();
                        }
                    }
                });
                break;
            case AC_UNLOAD:
                if (loadedWand != null) {
                    loadedWand.collect();
                    GLog.i("你从BFG中卸下了" + loadedWand.name() + "。");
                    loadedWand = null;
                    ammo=0;
                    updateQuickslot();
                }
                break;
        }
    }

    @Override
    protected void fire(int targetPos) {
        if (loadedWand == null) {
            GLog.w("需要装填法杖才能发射！");
            return;
        }

        final Ballistica shot = new Ballistica(curUser.pos, targetPos, Ballistica.PROJECTILE);
        int cell = shot.collisionPos;

        curUser.sprite.zap(cell);
        Sample.INSTANCE.play(hitSound, 1.2f, hitSoundPitch);

        curUser.sprite.operate(curUser.pos, () -> {
            boolean hit = false;
            Char ch = Actor.findChar(cell);

            if (ch != null) {
                int dmg = Math.round(Random.NormalIntRange(min(), max()));
                // 基础伤害加上法杖等级加成
                dmg += loadedWand.level() * 5;
                ch.damage(dmg, this);
                hit = true;
            }

            // 根据装填的法杖类型产生额外效果
            wandEffect(cell, hit);

            // 特效
            CellEmitter.center(cell).burst(RainbowParticle.BURST, 15);
            // consumeAmmo(1);、
            ammo=0;
            loadedWand=null;
            curUser.spendAndNext(DLY);
        });
    }

    private void wandEffect(int cell, boolean hit) {
        switch (loadedWand) {
            case WandOfFireblast w -> {
                // 火焰效果：增加火焰粒子和烟雾粒子
                GameScene.add(Blob.seed(cell, 50, Fire.class));
                for (int i : PathFinder.NEIGHBOURS8) {
                    GameScene.add(Blob.seed(cell + i, 30, Fire.class));
                }
                // 火焰粒子效果
                CellEmitter.get(cell).burst(FlameParticle.FACTORY, 30);
                // 烟雾粒子效果
                CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 20);
            }
            case WandOfFrost w -> {
                // 冰冻效果：增加冰晶粒子和雪花粒子
                GameScene.add(Blob.seed(cell, 50, Freezing.class));
                for (int i : PathFinder.NEIGHBOURS8) {
                    GameScene.add(Blob.seed(cell + i, 30, Freezing.class));
                }
                // 冰晶粒子效果
                CellEmitter.get(cell).burst(SnowParticle.FACTORY, 25);
                // 雪花粒子效果
                CellEmitter.get(cell).burst(SnowParticle.FACTORY, 15);
            }
            case WandOfLightning w -> {
                // 闪电效果：增加闪电链和电火花粒子
                for (Char ch : Actor.chars()) {
                    if (Dungeon.level.distance(cell, ch.pos) <= 4 && ch.isAlive()) {
                        ch.damage(Random.NormalIntRange(w.level() * 5, w.level() * 10), w);
                        // 电火花粒子效果
                        ch.sprite.centerEmitter().burst(SparkParticle.FACTORY, 5);
                    }
                }
                // 闪电中心粒子效果
                CellEmitter.center(cell).burst(SparkParticle.FACTORY, 10);
            }
            // 可以继续添加其他法杖效果...
            default -> {} // 默认情况，不执行任何操作
        }
    }

    @Override
    public String name() {
        if (loadedWand != null) {
            return "BFG (" + loadedWand.name() + ")";
        }
        return "BFG";
    }

    @Override
    public String desc() {
        StringBuilder desc = new StringBuilder();
        desc.append("一把巨大的能量武器，可以装填法杖来产生强大的效果。\n\n");

        if (loadedWand != null) {
            desc.append("_当前装填:_ ").append(loadedWand.name()).append("\n");
            desc.append("_法杖等级:_ +").append(loadedWand.level()).append("\n\n");
        } else {
            desc.append("_当前未装填法杖_\n\n");
        }

        desc.append("_武器属性:_\n");
        desc.append("- 基础命中率提升100%\n");
        desc.append("- 射程12格\n");
        desc.append("- 根据法杖类型产生额外效果\n\n");

        desc.append("_主动技能:_\n");
        desc.append("- 装填法杖：将任意法杖装入武器\n");
        desc.append("- 射击：结合法杖效果进行强力攻击\n\n");

        desc.append("_弹药系统:_\n");
        desc.append("- 最大弹药：").append(maxAmmo).append("发\n");
        desc.append("- 当前弹药：").append(ammo).append("发\n");
        desc.append("- 装弹时间：2秒\n\n");

        desc.append("_升级效果:_\n");
        desc.append("- 基础伤害：").append(min()).append("-").append(max()).append("\n");
        desc.append("- 力量需求：").append(STRReq()).append("\n");
        desc.append("- 每级提升：\n");
        desc.append("  * 最小伤害+3\n");
        desc.append("  * 最大伤害+5\n");
        desc.append("  * 力量需求+1");

        return desc.toString();
    }

    @Override
    public int STRReq(int lvl) {
        return 12 + lvl;
    }

    @Override
    public int min(int lvl) {
        return 15 + 3 * lvl;
    }

    @Override
    public int max(int lvl) {
        return 25 + 5 * lvl;
    }

    private static final String LOADED_WAND = "loadedWand";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LOADED_WAND, loadedWand);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        loadedWand = (Wand) bundle.get(LOADED_WAND);
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_FIRE)) {
            return "射击";
        }
        if (action.equals(AC_LOAD)) {
            return "装填法杖";
        }
        if (action.equals(AC_UNLOAD)) {
            return "卸下法杖";
        }
        if (action.equals(AC_RELOAD)) {
            return "装弹";
        }
        return super.actionName(action, hero);
    }
}
