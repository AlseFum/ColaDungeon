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
import com.coladungeon.items.weapon.Weapon;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.sprites.MissileSprite;
import com.coladungeon.tiles.DungeonTilemap;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

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
        AVAILABLE, // 可用状态
        THROWN, // 普通投掷
        POWER_THROWN // 蓄力投掷
    }

    // 当前飞镖状态
    private ChakramState currentState = ChakramState.AVAILABLE;
    public Class<? extends Gizmo> fly_sprite = MissileSprite.class;
    public Class<? extends Gizmo> still_sprite = MissileSprite.class;
    // // 飞镖投掷的目标位置
    // private int thrownPosition = -1;

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);

        return switch (currentState) {
            case AVAILABLE -> {
                actions.add("投掷");
                actions.add("蓄力投掷");
                yield actions;
            }
            case THROWN -> {
                actions.clear();
                yield actions;
            }
            case POWER_THROWN -> {
                yield actions;
            }
        };
    }

    @Override
    public void execute(Hero hero, String action) {
        switch (currentState) {
            case AVAILABLE -> {
                switch (action) {
                    case "投掷" ->
                        {GameScene.selectCell(thrower);
                        defaultAction = AC_THROW;}
                    case "蓄力投掷" ->
                        {GameScene.selectCell(powerThrower);
                        defaultAction=AC_POWER_THROW;
                        }
                        
                    default ->
                        super.execute(hero, action);
                }
            }
            case POWER_THROWN -> {
                if (action.equals("召回")) {
                    // 强制返回飞镖
                    currentState = ChakramState.AVAILABLE;
                    // thrownPosition = -1;
                    GLog.p("强制召回飞镖！");
                }
            }
        }
    }

    public CellSelector.Listener thrower = new CellSelector.Listener() {
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

    private final CellSelector.Listener powerThrower = new CellSelector.Listener() {
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
        Hero _hero = Dungeon.hero;
        final Ballistica shot = new Ballistica(_hero.pos, targetPos, Ballistica.STOP_TARGET);
        int cell = shot.collisionPos;

        // 更新飞镖状态
        currentState = isPowerThrow ? ChakramState.POWER_THROWN : ChakramState.THROWN;
        // thrownPosition = cell;

        _hero.sprite.zap(cell);

        // 将ArrayList<Integer>转换为int[]
        int[] path = shot.path.stream().mapToInt(Integer::intValue).toArray();

        // 创建FlyActor管理飞镖飞行状态
        FlyActor flyActor = new FlyActor(this, _hero.pos, cell, isPowerThrow, path);
        Actor.add(flyActor);

        // 使用标准的MissileSprite
        ((MissileSprite) _hero.sprite.parent.recycle(fly_sprite)).
                reset(_hero.sprite, cell, this, () -> flyActor.onMissileComplete());

        Sample.INSTANCE.play(hitSound, 1f, hitSoundPitch);
        _hero.spendAndNext(DLY * (isPowerThrow ? 1.5f : 1f));
    }

    // 飞镖飞行Actor
    public static class FlyActor extends Actor {

        private final Chakram chakram;
        private final int targetPos;
        private final int remainingTurns;
        private final boolean isPowerThrow;
        private boolean missileComplete = false; // 飞镖是否到达目标
        private final int[] path; // 飞行路径

        public FlyActor(Chakram chakram, int initialPos, int targetPos, boolean isPowerThrow, int[] path) {
            this.chakram = chakram;
            this.targetPos = targetPos;
            this.isPowerThrow = isPowerThrow;
            this.path = path;
            // 普通投掷3回合，蓄力投掷5回合
            this.remainingTurns = isPowerThrow ? 5 : 2;
        }

        public void onMissileComplete() {
            missileComplete = true;

            // 处理整个路径上的伤害
            for (int cell : path) {
                Char target = Actor.findChar(cell);
                chakram.flying_effect(target, isPowerThrow);
            }

            // 创建StillActor显示飞镖在空中停留的状态
            StillActor stillActor = new StillActor(chakram, targetPos, isPowerThrow, remainingTurns);
            Actor.add(stillActor);
        }

        @Override
        protected boolean act() {
            if (!missileComplete) {
                spend(TICK);
                return true;
            }
            Actor.remove(this);
            return true;
        }

        // 设置返回状态
        public void setReturning() {
        }
    }

    // 飞镖停留Actor
    public static class StillActor extends Actor {

        private final Chakram chakram;
        private final int pos;
        private final boolean isPowerThrow;
        private final ItemSprite sprite;
        private int remainingTurns;

        public StillActor(Chakram chakram, int pos, boolean isPowerThrow, int remainingTurns) {
            this.chakram = chakram;
            this.pos = pos;
            this.isPowerThrow = isPowerThrow;
            this.remainingTurns = remainingTurns;

            sprite = new ItemSprite(chakram);
            sprite.visible = true;
            sprite.place(pos);

            // 添加到场景
            Dungeon.hero.sprite.parent.add(sprite);
        }

        @Override
        protected boolean act() {
            remainingTurns--;

            if (remainingTurns <= 0) {
                // 创建返回路径
                Ballistica returnPath = new Ballistica(pos,
                        Dungeon.hero.pos,
                        Ballistica.STOP_TARGET);
                int[] path = returnPath.path.stream().mapToInt(Integer::intValue).toArray();

                // 使用MissileSprite显示返回动画
                ((MissileSprite) Dungeon.hero.sprite.parent.recycle(MissileSprite.class)).
                        reset(DungeonTilemap.tileCenterToWorld(pos),
                                Dungeon.hero.sprite.center(),
                                chakram,
                                () -> {
                                    chakram.currentState = ChakramState.AVAILABLE;
                                    for (int cell : path) {
                                        Char target = Actor.findChar(cell);
                                        if (target != null && target != Dungeon.hero) {
                                            chakram.flying_effect(target, isPowerThrow);
                                        }
                                    }
                                });

                sprite.killAndErase();
                Actor.remove(this);
                return true;
            }
            //静止时的效果
            chakram.still_effect(pos, isPowerThrow);
            spend(TICK);
            return true;
        }
    }

    //TOBE OVERRIDE
    public void flying_effect(Char target, boolean isPowerThrow) {
        if (target != null && target != Dungeon.hero) {
            chakram_proc(target, target.pos, isPowerThrow);
        }
    }

    //TOBE OVERRIDE
    public void still_effect(int pos, boolean isPowerThrow) {
        for (int i : PathFinder.NEIGHBOURS9) {
            int cell = pos + i;
            Char target = Actor.findChar(cell);
            if (target != null && target != Dungeon.hero && target.isAlive()) {
                chakram_proc(target, cell, isPowerThrow);
            }
        }
    }

    // 应用飞镖效果
    private void chakram_proc(Char target, int pos, boolean isPowerThrow) {
        // 特效

        int dmg = damageRoll(Dungeon.hero);
        if (isPowerThrow) {
            dmg = Math.round(dmg * 1.5f);
        }

        // 造成伤害
        target.damage(dmg, this);

        // 如果目标还活着，应用减速效果和视觉特效
        if (target.isAlive()) {
            // 减速效果
            Buff.affect(target, Cripple.class, isPowerThrow ? 5f : 3f);
        }
        // 命中特效
        target.sprite.burst(0xCC99FFFF, isPowerThrow ? 10 : 5);

        // 场域特效（无论是否命中都会显示）
        if (isPowerThrow) {
            CellEmitter.center(pos).burst(PurpleParticle.BURST, 10);
        }
    }

    @Override
    public int min(int lvl) {
        return 5 + 3 * lvl;  // 提高基础伤害和升级收益
    }

    @Override
    public int max(int lvl) {
        return 10 + 5 * lvl;  // 提高基础伤害和升级收益
    }

    @Override
    public int STRReq(int lvl) {
        return 10 + lvl;
    }

    @Override
    public String name() {
        return "巨型飞镖";
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_THROW)) {
            return "投掷";
        }
        if (action.equals(AC_POWER_THROW)) {
            return "蓄力投掷";
        }
        return super.actionName(action, hero);
    }

    @Override
    public String desc() {
        switch (currentState) {
            case THROWN:
                return "飞镖正在飞行中。\n\n"
                        + "_当前状态:_\n"
                        + "- 飞镖已离开，无法控制\n"
                        + "- 正在飞向目标\n"
                        + "- 即将命中敌人\n"
                        + "- 等待自动返回";
            case POWER_THROWN:
                return "飞镖正处于蓄力投掷状态。\n\n"
                        + "_当前状态:_\n"
                        + "- 飞镖已离开，无法控制\n"
                        + "- 在目标区域持续造成伤害\n"
                        + "- 区域内敌人每回合受到伤害\n"
                        + "- 等待自动返回";
            default:
                StringBuilder desc = new StringBuilder();
                desc.append("一种能够自动返回使用者手中的巨型飞镖。\n\n");
                desc.append("力量需求：").append(STRReq(0)).append("\n");
                desc.append("伤害：").append(min(0)).append("-").append(max(0)).append("\n\n");
                desc.append("_特殊效果：_\n");
                desc.append("- 投掷时能够穿透敌人的护甲\n");
                desc.append("- 命中后会自动返回使用者手中\n");
                desc.append("- 蓄力投掷时会在目标区域持续造成伤害");
                return desc.toString();
        }
    }
}
