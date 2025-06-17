package com.coladungeon.items.weapon.gun;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Rifle extends Gun {

    private static final int SHOTS_PER_BURST = 3; // 每次点射发射的子弹数
    private static final int SHOTS_PER_SPRAY = 10; // 扫射消耗的弹药量
    private static final float SPRAY_CHANCE = 0.4f; // 扫射命中周围格子的基础概率

    private static final String AC_BLAST = "点射";
    private static final String AC_SPRAY = "扫射";

    {
        image = ItemSpriteSheet.CROSSBOW; // 暂时使用十字弩的图标
        defaultAction = AC_BLAST; // 默认动作为点射

        maxAmmo = 30;
        ammo = maxAmmo;
        reloadTime = 1.5f;

        usesTargeting = true;
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
        desc.append("- 对目标及其周围8格进行扫射\n");
        desc.append("- 中心目标必定命中\n");
        desc.append("- 周围目标40%概率命中\n");
        desc.append("- 消耗10发弹药\n\n");

        desc.append("_弹药系统:_\n");
        desc.append("- 最大弹药：").append(maxAmmo).append("发\n");
        desc.append("- 当前弹药：").append(ammo).append("发\n");
        desc.append("- 装弹时间：").append(reloadTime).append("秒");

        return desc.toString();
    }

    @Override
    protected void addSubActions(Hero hero, ArrayList<String> actions) {
        actions.add(AC_BLAST);
        actions.add(AC_SPRAY);
    }

    @Override
    public String subActionName(String action, Hero hero) {
        if (action.equals(AC_BLAST)) {
            return "点射";
        }
        if (action.equals(AC_SPRAY)) {
            return "扫射";
        }
        return null;
    }

    @Override
    protected void executeSubAction(Hero hero, String action) {
        if (action.equals(AC_BLAST)) {
            if (ammo < SHOTS_PER_BURST) {
                GLog.w("弹药不足！需要%d发子弹。", SHOTS_PER_BURST);
                return;
            }
            GameScene.selectCell(blask_callback);

        } else if (action.equals(AC_SPRAY)) {
            if (ammo < SHOTS_PER_SPRAY) {
                GLog.w("弹药不足！需要%d发子弹。", SHOTS_PER_SPRAY);
                return;
            }
            GameScene.selectCell(spray_callback);
        }
    }

    public CellSelector.Listener blask_callback = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                blast(target);
            }
        }

        @Override
        public String prompt() {
            return "选择点射目标";
        }
    };

    public CellSelector.Listener spray_callback = new CellSelector.Listener() {
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

    private void blast(int targetPos) {
        if (ammo < SHOTS_PER_BURST) {
            GLog.w("弹药不足！需要%d发子弹。", SHOTS_PER_BURST);
            return;
        }
        curUser.sprite.zap(targetPos);
        for (int i = 0; i < SHOTS_PER_BURST; i++) {
            fire(targetPos, false); // 使用fire而不是shoot，并且不消耗行动点
            float pitch = 0.8f + Random.Float(0.4f);
            Sample.INSTANCE.play(Assets.Sounds.HIT, pitch);
            consumeAmmo(1);
            }
            curUser.spendAndNext(delayFactor(curUser));
        
    }

    private void spray(int targetPos) {
        if (ammo < SHOTS_PER_SPRAY) {
            GLog.w("弹药不足！需要%d发子弹。", SHOTS_PER_SPRAY);
            return;
        }

        curUser.sprite.zap(targetPos);
        for (int i = 0; i < SHOTS_PER_SPRAY; i++) {
            // 计算实际射击位置
            int actualTargetPos = targetPos;
            if (Random.Float() < SPRAY_CHANCE) { // 30%概率射偏
                // 获取周围8格的位置
                int[] neighbors = PathFinder.NEIGHBOURS8;
                int randomNeighbor = neighbors[Random.Int(neighbors.length)];
                actualTargetPos = targetPos + randomNeighbor;
            }
            
            fire(actualTargetPos, false); // 使用fire而不是shoot，并且不消耗行动点
            float pitch = 0.8f + Random.Float(0.4f);
            Sample.INSTANCE.play(Assets.Sounds.HIT, pitch);
            consumeAmmo(1);
        }
        curUser.spendAndNext(delayFactor(curUser));
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
}
