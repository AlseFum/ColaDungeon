package com.coladungeon.mod.ArknightsMod;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.Statistics;
import com.coladungeon.actors.mobs.npcs.Ghost;
import com.coladungeon.effects.Ripple;
import com.coladungeon.levels.Level;
import com.coladungeon.levels.RegularLevel;
import com.coladungeon.levels.Terrain;
import com.coladungeon.levels.painters.Painter;
import com.coladungeon.levels.painters.SewerPainter;
import com.coladungeon.levels.themes.sewer.SewerLevel;
import com.coladungeon.levels.traps.AlarmTrap;
import com.coladungeon.levels.traps.ChillingTrap;
import com.coladungeon.levels.traps.ConfusionTrap;
import com.coladungeon.levels.traps.FlockTrap;
import com.coladungeon.levels.traps.GatewayTrap;
import com.coladungeon.levels.traps.OozeTrap;
import com.coladungeon.levels.traps.ShockingTrap;
import com.coladungeon.levels.traps.SummoningTrap;
import com.coladungeon.levels.traps.TeleportationTrap;
import com.coladungeon.levels.traps.ToxicTrap;
import com.coladungeon.levels.traps.WornDartTrap;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;


public class LungmenLevel extends RegularLevel {

    {
        color1 = 0x48763c;
        color2 = 0x59994a;
    }

    public static final String[] SEWER_TRACK_LIST
            = new String[]{Assets.Music.SEWERS_1,
                Assets.Music.SEWERS_2,
                Assets.Music.SEWERS_2,
                Assets.Music.SEWERS_1,
                Assets.Music.SEWERS_3,
                Assets.Music.SEWERS_3};
    public static final float[] SEWER_TRACK_CHANCES = new float[]{1f, 1f, 0.5f, 0.25f, 1f, 0.5f};
	
    public void playLevelMusic() {
        if (Ghost.Quest.active() || Statistics.amuletObtained) {
            if (Statistics.amuletObtained && Dungeon.depth == 1) {
                Music.INSTANCE.play(Assets.Music.THEME_FINALE, true);
            } else {
                Music.INSTANCE.play(Assets.Music.SEWERS_TENSE, true);
            }
        } else {
            Music.INSTANCE.playTracks(SEWER_TRACK_LIST, SEWER_TRACK_CHANCES, false);
        }
    }

    @Override
    protected int standardRooms(boolean forceMax) {
        if (forceMax) {
            return 6;
        }
        //4 to 6, average 5
        return 4 + Random.chances(new float[]{1, 3, 1});
    }

    @Override
    protected int specialRooms(boolean forceMax) {
        if (forceMax) {
            return 2;
        }
        //1 to 2, average 1.8
        return 1 + Random.chances(new float[]{1, 4});
    }

    @Override
    protected Painter painter() {
        return new SewerPainter()
                .setWater(feeling == Feeling.WATER ? 0.85f : 0.30f, 5)
                .setGrass(feeling == Feeling.GRASS ? 0.80f : 0.20f, 4)
                .setTraps(nTraps(), trapClasses(), trapChances());
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_SEWERS;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_SEWERS;
    }

    @Override
    protected Class<?>[] trapClasses() {
        return Dungeon.depth == 1
                ? new Class<?>[]{WornDartTrap.class}
                : new Class<?>[]{
                    ChillingTrap.class, ShockingTrap.class, ToxicTrap.class, WornDartTrap.class,
                    AlarmTrap.class, OozeTrap.class,
                    ConfusionTrap.class, FlockTrap.class, SummoningTrap.class, TeleportationTrap.class, GatewayTrap.class};
    }

    @Override
    protected float[] trapChances() {
        return Dungeon.depth == 1
                ? new float[]{1}
                : new float[]{
                    4, 4, 4, 4,
                    2, 2,
                    1, 1, 1, 1, 1};
    }

    @Override
    protected void createMobs() {
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        addSewerVisuals(this, visuals);
        return visuals;
    }

    public static void addSewerVisuals(Level level, Group group) {
        for (int i = 0; i < level.length(); i++) {
            if (level.map[i] == Terrain.WALL_DECO) {
                group.add(new Sink(i));
            }
        }
    }

    @Override
    public String tileName(int tile) {
        return switch (tile) {
            case Terrain.WATER -> Messages.get(SewerLevel.class, "water_name");
            default -> super.tileName(tile);
        };
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.EMPTY_DECO:
                return Messages.get(SewerLevel.class, "empty_deco_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(SewerLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc(tile);
        }
    }

    private static class Sink extends Emitter {

        private int pos;
        private float rippleDelay = 0;

        private static final Emitter.Factory factory = new Factory() {

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                WaterParticle p = (WaterParticle) emitter.recycle(WaterParticle.class);
                p.reset(x, y);
            }
        };

        public Sink(int pos) {
            super();

            this.pos = pos;

            PointF p = DungeonTilemap.tileCenterToWorld(pos);
            pos(p.x - 2, p.y + 3, 4, 0);

            pour(factory, 0.1f);
        }

        @Override
        public void update() {
            if (visible = (pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[pos])) {

                super.update();

                if (!isFrozen() && (rippleDelay -= Game.elapsed) <= 0) {
                    Ripple ripple = GameScene.ripple(pos + Dungeon.level.width());
                    if (ripple != null) {
                        ripple.y -= DungeonTilemap.SIZE / 2;
                        rippleDelay = Random.Float(0.4f, 0.6f);
                    }
                }
            }
        }
    }

    public static final class WaterParticle extends PixelParticle {

        public WaterParticle() {
            super();

            acc.y = 50;
            am = 0.5f;

            color(ColorMath.random(0xb6ccc2, 0x3b6653));
            size(2);
        }

        public void reset(float x, float y) {
            revive();

            this.x = x;
            this.y = y;

            speed.set(Random.Float(-2, +2), 0);

            left = lifespan = 0.4f;
        }
    }
}
