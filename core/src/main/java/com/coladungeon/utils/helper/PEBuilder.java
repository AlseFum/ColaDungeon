package com.coladungeon.utils.helper;

import java.util.function.BiConsumer;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.coladungeon.tiles.DungeonTilemap;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;

public class PEBuilder {

    public Emitter emitter = new Emitter();

    public static record EmitConfig(
            Emitter.Factory factory, float interval, int quantity) {

    }
    //placeholder
    public static Emitter.Factory selfFactory = new Emitter.Factory() {
    };
    public static PixelParticle selfParticle = new PixelParticle();
    public EmitConfig emitcfg;
    public ParticleBuilder particle = new ParticleBuilder();
    public FactoryBuilder factory = new FactoryBuilder();
    private BiConsumer<PEBuilder, Class<? extends PixelParticle>> beforeRun;

    public PEBuilder() {
    }

    public PEBuilder beforeRun(BiConsumer<PEBuilder, Class<? extends PixelParticle>> beforeRun) {
        this.beforeRun = beforeRun;
        return this;
    }

    //---------------------
    public PEBuilder at(Integer target) {
        PointF p = DungeonTilemap.tileToWorld(target);
        emitter.pos(p);
        return this;
    }

    public PEBuilder at(float x, float y) {
        emitter.pos(x, y);
        return this;
    }

    public PEBuilder at(float x, float y, float width, float height) {
        emitter.pos(x, y, width, height);
        return this;
    }

    public PEBuilder at(PointF p) {
        emitter.pos(p);
        return this;
    }

    //---------------------
    public PEBuilder burst(Emitter.Factory factory, int quantity) {
        emitcfg = new EmitConfig(factory, 0, quantity);
        return this;
    }

    public PEBuilder burst(int quantity) {
        return burst(selfFactory, quantity);
    }

    public PEBuilder pour(Emitter.Factory factory, float interval) {
        emitcfg = new EmitConfig(factory, interval, 1);
        return this;
    }

    public PEBuilder pour(float interval) {
        return pour(selfFactory, interval);
    }

    public PEBuilder start(Emitter.Factory factory, float interval, int quantity) {
        emitcfg = new EmitConfig(factory, interval, quantity);
        return this;
    }

    public PEBuilder start(float interval, int quantity) {
        return start(selfFactory, interval, quantity);
    }

    //---------------------
    public PEBuilder run() {

        if (emitcfg.factory() == selfFactory) {
            start(factory.build(), emitcfg.interval(), emitcfg.quantity());
        }
        if (beforeRun != null) {
            beforeRun.accept(this, particle.build());
        }

        emitter.start(emitcfg.factory(), emitcfg.interval(), emitcfg.quantity());

        return this;
    }

    public PEBuilder kill() {
        emitter.kill();
        return this;
    }

    public PEBuilder revive() {
        emitter.revive();
        return this;
    }

    public PEBuilder autoKill(boolean autoKill) {
        emitter.autoKill = autoKill;
        return this;
    }

    public PEBuilder fillTarget(boolean fillTarget) {
        emitter.fillTarget = fillTarget;
        return this;
    }
//---------------------

    public PEBuilder emitFn(FactoryBuilder.EmitFunction emitFunction) {
        factory.emitFunction = emitFunction;
        return this;
    }

    public PEBuilder lightMode(boolean lightMode) {
        emitter.lightMode = lightMode;
        return this;
    }
    //---------------------

    public static class ParticleBuilder {

        public Class<? extends PixelParticle> build() {
            return PixelParticle.class;
        }
    }

    public static class FactoryBuilder {

        private EmitFunction emitFunction;
        private LightModeFunction lightModeFunction;

        @FunctionalInterface
        public interface EmitFunction {

            void emit(Emitter emitter, int index, float x, float y);
        }

        @FunctionalInterface
        public interface LightModeFunction {

            boolean lightMode();
        }

        public FactoryBuilder() {
        }

        public FactoryBuilder emit(EmitFunction emitFunction) {
            this.emitFunction = emitFunction;
            return this;
        }

        public FactoryBuilder lightMode(LightModeFunction lightModeFunction) {
            this.lightModeFunction = lightModeFunction;
            return this;
        }

        public FactoryBuilder lightMode(boolean lightMode) {
            this.lightModeFunction = () -> lightMode;
            return this;
        }

        public Emitter.Factory build() {
            return new Emitter.Factory() {
                @Override
                public void emit(Emitter emitter, int index, float x, float y) {
                    if (emitFunction != null) {
                        emitFunction.emit(emitter, index, x, y);
                    }
                }

                @Override
                public boolean lightMode() {
                    return lightModeFunction != null ? lightModeFunction.lightMode() : false;
                }
            };
        }
    }
    //---------------------
    public static FactoryBuilder.EmitFunction only_reset(Class<? extends PixelParticle> particleClass) {
        return (emitter, index, x, y) -> {
            try {
                Object particle = emitter.recycle(particleClass);
                if (particle != null) {
                    Method resetMethod = particleClass.getMethod("reset", float.class, float.class, int.class);
                    resetMethod.invoke(particle, x, y, index);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                // 处理反射异常
                e.printStackTrace();
            }
        };
    }
}
