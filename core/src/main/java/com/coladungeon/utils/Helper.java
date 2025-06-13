package com.coladungeon.utils;

import com.coladungeon.effects.MagicMissile;
import com.coladungeon.effects.particles.SparkParticle;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class Helper{
    public static void log(Object... objs){
        for(var o :objs){
            System.out.println(o);
        }
    }
    public static class ParticleHelper{
        
        /**
         * 在指定位置生成一个简单的像素粒子效果
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
         * @param x 粒子生成的x坐标
         * @param y 粒子生成的y坐标
         * @return 生成的粒子对象
         */
        public static SparkParticle createSparkParticle(float x, float y) {
            SparkParticle particle = new SparkParticle();
            particle.reset(x, y);
            return particle;
        }
        
        /**
         * 在指定位置生成一个魔法粒子效果
         * @param x 粒子生成的x坐标
         * @param y 粒子生成的y坐标
         * @return 生成的粒子对象
         */
        public static MagicMissile.MagicParticle createMagicParticle(float x, float y) {
            MagicMissile.MagicParticle particle = new MagicMissile.MagicParticle();
            particle.reset(x, y);
            return particle;
        }
    }
}