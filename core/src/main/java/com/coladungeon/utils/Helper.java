package com.coladungeon.utils;

import java.util.function.Function;

import com.coladungeon.effects.MagicMissile;
import com.coladungeon.effects.particles.SparkParticle;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class Helper {

    public static void println(Object... objs) {
        log(objs);
    }

    public static void log(Object... objs) {
        for (var o : objs) {
            System.out.println(o);
        }
    }

    public static class ParticleHelper {

        /**
         * 在指定位置生成一个简单的像素粒子效果
         *
         * @param x 粒子生成的x坐标
         * @param y 粒子生成的y坐标
         * @param color 粒子的颜色
         * @param size 粒子的大小
         * @param lifespan 粒子的生命周期
         * @param speedX 粒子x方向的速度
         * @param speedY 粒子y方向的速度
         * @param accX 粒子x方向的加速度
         * @param accY 粒子y方向的加速度
         * @return 生成的粒子对象
         */
        public static PixelParticle createPixelParticle(float x, float y, int color, float size, float lifespan,
                float speedX, float speedY, float accX, float accY) {
            PixelParticle particle = new PixelParticle();
            particle.reset(x, y, color, size, lifespan);
            particle.speed.set(speedX, speedY);
            particle.acc.set(accX, accY);
            return particle;
        }

        /**
         * 在指定位置生成一个简单的像素粒子效果（简化版）
         *
         * @param x 粒子生成的x坐标
         * @param y 粒子生成的y坐标
         * @param color 粒子的颜色
         * @return 生成的粒子对象
         */
        public static PixelParticle createSimplePixelParticle(float x, float y, int color) {
            return createPixelParticle(x, y, color, 4, 0.5f,
                    Random.Float(-10, 10), Random.Float(-10, 10),
                    0, 0);
        }

        /**
         * 在指定位置生成一个火花粒子效果
         *
         * @param x 粒子生成的x坐标
         * @param y 粒子生成的y坐标
         * @return 生成的粒子对象
         */
        public static SparkParticle createSparkParticle(float x, float y) {
            SparkParticle particle = new SparkParticle();
            particle.reset(x, y);
            return particle;
        }

        public static MagicMissile.MagicParticle createMagicParticle(float x, float y) {
            MagicMissile.MagicParticle particle = new MagicMissile.MagicParticle();
            particle.reset(x, y);
            return particle;
        }
    }

    public static class Result<T> {

        private T value;
        private boolean ok;

        private Result() {
            this.ok = false;
        }

        public static <T> Result<T> ok(T value) {
            Result<T> result = new Result<>();
            result.value = value;
            result.ok = true;
            return result;
        }

        public static <T> Result<T> no() {
            return new Result<>();
        }

        public boolean isOk() {
            return ok;
        }

        public T unwrap() {
            return value;
        }

        public T unwrap(T defaultValue) {
            return ok ? value : defaultValue;
        }

        public <U> Result<U> then(Function<T, Result<U>> function) {
            return ok ? function.apply(value) : Result.no();
        }
    }

    public static class ParticleFactory {

        /**
         * 创建一个爆炸效果的粒子系统
         *
         * @param centerX 爆炸中心x坐标
         * @param centerY 爆炸中心y坐标
         * @param color 粒子颜色
         * @param count 粒子数量
         * @param radius 爆炸半径
         * @param duration 持续时间
         * @return 粒子数组
         */
        public static PixelParticle[] createExplosion(float centerX, float centerY, int color, int count, float radius, float duration) {
            PixelParticle[] particles = new PixelParticle[count];
            for (int i = 0; i < count; i++) {
                float angle = Random.Float((float) (Math.PI * 2));
                float distance = Random.Float(radius);
                float x = centerX + (float) Math.cos(angle) * distance;
                float y = centerY + (float) Math.sin(angle) * distance;

                float speed = Random.Float(50, 100);
                float speedX = (float) Math.cos(angle) * speed;
                float speedY = (float) Math.sin(angle) * speed;

                particles[i] = ParticleHelper.createPixelParticle(x, y, color, Random.Float(2, 4), duration,
                        speedX, speedY, -speedX * 2, -speedY * 2);
            }
            return particles;
        }

        /**
         * 创建一个魔法漩涡效果
         *
         * @param centerX 漩涡中心x坐标
         * @param centerY 漩涡中心y坐标
         * @param color 粒子颜色
         * @param count 粒子数量
         * @param radius 漩涡半径
         * @param duration 持续时间
         * @return 粒子数组
         */
        public static PixelParticle[] createMagicVortex(float centerX, float centerY, int color, int count, float radius, float duration) {
            PixelParticle[] particles = new PixelParticle[count];
            for (int i = 0; i < count; i++) {
                float angle = (float) (i * Math.PI * 2 / count);
                float x = centerX + (float) Math.cos(angle) * radius;
                float y = centerY + (float) Math.sin(angle) * radius;

                float speed = 50;
                float speedX = (float) Math.cos(angle + Math.PI / 2) * speed;
                float speedY = (float) Math.sin(angle + Math.PI / 2) * speed;

                particles[i] = ParticleHelper.createPixelParticle(x, y, color, 3, duration,
                        speedX, speedY, 0, 0);
            }
            return particles;
        }

        /**
         * 创建一个魔法弹道效果
         *
         * @param startX 起始x坐标
         * @param startY 起始y坐标
         * @param endX 目标x坐标
         * @param endY 目标y坐标
         * @param color 粒子颜色
         * @param count 粒子数量
         * @return 粒子数组
         */
        public static PixelParticle[] createMagicTrail(float startX, float startY, float endX, float endY, int color, int count) {
            PixelParticle[] particles = new PixelParticle[count];
            PointF direction = new PointF(endX - startX, endY - startY).normalize();

            for (int i = 0; i < count; i++) {
                float t = i / (float) count;
                float x = startX + (endX - startX) * t;
                float y = startY + (endY - startY) * t;

                float offset = Random.Float(-5, 5);
                x += direction.y * offset;
                y -= direction.x * offset;

                particles[i] = ParticleHelper.createPixelParticle(x, y, color, 2, 0.5f,
                        direction.x * 50, direction.y * 50, 0, 0);
            }
            return particles;
        }

        /**
         * 创建一个魔法光环效果
         *
         * @param centerX 光环中心x坐标
         * @param centerY 光环中心y坐标
         * @param color 粒子颜色
         * @param count 粒子数量
         * @param radius 光环半径
         * @param duration 持续时间
         * @return 粒子数组
         */
        public static PixelParticle[] createMagicAura(float centerX, float centerY, int color, int count, float radius, float duration) {
            PixelParticle[] particles = new PixelParticle[count];
            for (int i = 0; i < count; i++) {
                float angle = (float) (i * Math.PI * 2 / count);
                float x = centerX + (float) Math.cos(angle) * radius;
                float y = centerY + (float) Math.sin(angle) * radius;

                particles[i] = ParticleHelper.createPixelParticle(x, y, color, 2, duration,
                        0, 0, 0, 0);
            }
            return particles;
        }
    }
}
