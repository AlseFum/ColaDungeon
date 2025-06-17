package com.coladungeon.items.supply;

import java.util.ArrayList;

import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Item;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.utils.Helper;
import com.coladungeon.tiles.DungeonTilemap;
import com.coladungeon.effects.particles.BlastParticle;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;

import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.coladungeon.utils.helper.PEBuilder;
import com.coladungeon.Dungeon;

public class testitem extends Item {

    public static final String AC_USE = "USE";

    {
        defaultAction = AC_USE;
    }

    public void test(Integer target) {
        PointF p = DungeonTilemap.tileToWorld(target);
        PEBuilder builder = new PEBuilder()
                .at(target)
                .beforeRun((b, part) -> {
                    for (int i = 0; i < 16; i++) {
                        TileBorderParticle particle = new TileBorderParticle();
                        float progress = i / 16f;
                        if (progress < 0.25f) {  // 上边缘
                            particle.x = p.x + progress * 4 * DungeonTilemap.SIZE;
                            particle.y = p.y;
                        } else if (progress < 0.5f) {  // 右边缘
                            particle.x = p.x + DungeonTilemap.SIZE;
                            particle.y = p.y + (progress - 0.25f) * 4 * DungeonTilemap.SIZE;
                        } else if (progress < 0.75f) {  // 下边缘
                            particle.x = p.x + DungeonTilemap.SIZE - (progress - 0.5f) * 4 * DungeonTilemap.SIZE;
                            particle.y = p.y + DungeonTilemap.SIZE;
                        } else {  // 左边缘
                            particle.x = p.x;
                            particle.y = p.y + DungeonTilemap.SIZE - (progress - 0.75f) * 4 * DungeonTilemap.SIZE;
                        }
                        b.emitter.add(particle);
                    }
                })
                .emitFn(PEBuilder.only_reset(TileBorderParticle.class))
                .lightMode(true)
                .start(0.05f, 16)
                .run();

        curUser.sprite.parent.add(builder.emitter);
    }

    // 地砖边缘粒子类
    public static class TileBorderParticle extends PixelParticle {

        private int index;
        private float baseX;
        private float baseY;

        public TileBorderParticle() {
            super();
            color(0xFFFFFF);  // 白色
            lifespan = 1f;
            size(1);  // 1像素大小
        }

        public void reset(float x, float y, int index) {
            revive();
            this.index = index;
            this.baseX = x;
            this.baseY = y;
            left = lifespan;

            // // 根据索引计算粒子在地砖边缘的位置
            // float progress = index / 16f;  // 16是总粒子数
            // if (progress < 0.25f) {  // 上边缘
            //     this.x = baseX + progress * 4 * DungeonTilemap.SIZE;
            //     this.y = baseY;
            // } else if (progress < 0.5f) {  // 右边缘
            //     this.x = baseX + DungeonTilemap.SIZE;
            //     this.y = baseY + (progress - 0.25f) * 4 * DungeonTilemap.SIZE;
            // } else if (progress < 0.75f) {  // 下边缘
            //     this.x = baseX + DungeonTilemap.SIZE - (progress - 0.5f) * 4 * DungeonTilemap.SIZE;
            //     this.y = baseY + DungeonTilemap.SIZE;
            // } else {  // 左边缘
            //     this.x = baseX;
            //     this.y = baseY + DungeonTilemap.SIZE - (progress - 0.75f) * 4 * DungeonTilemap.SIZE;
            // }
        }

        @Override
        public void update() {
            super.update();
            // // 保持粒子在地砖边缘移动
            // float progress = (index / 16f + left / lifespan) % 1f;
            // if (progress < 0.25f) {  // 上边缘
            //     this.x = baseX + progress * 4 * DungeonTilemap.SIZE;
            //     this.y = baseY;
            // } else if (progress < 0.5f) {  // 右边缘
            //     this.x = baseX + DungeonTilemap.SIZE;
            //     this.y = baseY + (progress - 0.25f) * 4 * DungeonTilemap.SIZE;
            // } else if (progress < 0.75f) {  // 下边缘
            //     this.x = baseX + DungeonTilemap.SIZE - (progress - 0.5f) * 4 * DungeonTilemap.SIZE;
            //     this.y = baseY + DungeonTilemap.SIZE;
            // } else {  // 左边缘
            //     this.x = baseX;
            //     this.y = baseY + DungeonTilemap.SIZE - (progress - 0.75f) * 4 * DungeonTilemap.SIZE;
            // }
        }
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_USE)) {
            curUser = hero;
            curItem = this;
            GameScene.selectCell(new CellSelector.Listener() {
                @Override
                public void onSelect(Integer target) {
                    if (target != null) {
                        test(target);
                    }
                }

                @Override
                public String prompt() {
                    return "选择目标位置";
                }
            });
        }
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_USE);
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_USE)) {
            return "使用";
        }
        return super.actionName(action, hero);
    }

    @Override
    public String name() {
        return "测试物品";
    }

    @Override
    public String desc() {
        return "使用后可以选择一个位置";
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return 0;
    }
}
