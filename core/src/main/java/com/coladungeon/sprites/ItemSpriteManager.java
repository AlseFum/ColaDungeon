package com.coladungeon.sprites;

import java.util.ArrayList;
import java.util.HashMap;

import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.RectF;

public class ItemSpriteManager {

    private static final ArrayList<Segment> segments = new ArrayList<>();
    public static HashMap<String, Integer> texture_id_map = new HashMap<>();
    private static int latestLocation = 120000;

    public static Segment getSegment(int id) {
        
        for (Segment s : segments) {
            if (s.id_start <= id && id <= s.id_start + s.id_size) {
                if (s.cache.bitmap == null) {
                    s.load();
                }
                return s;
            }
        }
        return null;
    }

    public static int ByName(String name) {
        Integer res = texture_id_map.get(name);
        if(res == null){
            System.out.println("Invalid texture name: " + name);
            return ItemSpriteSheet.SOMETHING;
        }
        return res;
    }

    public static Segment registerTexture(String texture, int size) {
        Segment s = new Segment(texture, latestLocation, size);
        segments.add(s);
        latestLocation += 1000;
        return s;
    }

    public static Segment registerTexture(String texture) {
        return registerTexture(texture, 16);
    }

    public static ImageMapping getImageMapping(int id) {
        Segment s = getSegment(id);
        if (s == null) {
            return null;
        }
        return s.get(id);
    }

    public static ImageMapping getImageMapping(String label) {
        return getSegment(texture_id_map.get(label)).get(label);
    }

    public static class Segment {
        SmartTexture cache;
        String path;
        int id_start;
        int id_size;
        int size;
        int cols;

        TextureFilm film;

        public Segment(String texture, int id_start, int size) {
            this.path = texture;
            this.cache = TextureCache.get(texture);
            this.film = new TextureFilm(cache, size, size);
            this.id_start = id_start;
            this.id_size = 0;
            this.size = size;
            this.cols = (int) (cache.width / size);
        }
        public void load(){
            this.cache = TextureCache.get(path);
            this.film = new TextureFilm(cache, this.size, this.size);
        }

        private Segment settle(int id) {
            // only local index
            int x = id % cols;
            int y = id / cols;
            film.add(id, x * size, y * size, (x + 1) * size, (y + 1) * size);
            return this;
        }

        public ImageMapping get(int id) {
            int where = id >= id_start ? id - id_start : id;
            if (film.get(id) == null) {
                settle(where);
            }
            return new ImageMapping(cache, film.get(where), film.height(where), size);
        }

        public Segment label(String label) {
            texture_id_map.put(label, id_start + id_size);
            settle(id_size);
            id_size++;
            return this;
        }

        public Segment span(int size) {
            // warn: it's not a good idea to use this method
            // only use if you know what you are doing
            // now it's for dev only
            this.id_size += size;
            return this;
        }

        public ImageMapping get(String label) {
            return get(texture_id_map.get(label));
        }

        public static int getByName(String name) {
            return ItemSpriteManager.ByName(name);
        }
    }

    /**
     * 初始化动态生成的贴图
     */
    public static void initDynamicTextures() {
        // 创建动态生成的金苹果贴图
        DynamicSpriteExample.createGoldenApple("golden_apple");
    }

    static {
        registerTexture("minecraft/misc.png", 16)
                .span(144).label("skel");
        registerTexture("minecraft/bread.png", 16)
                .label("bread");
        registerTexture("effects/misc.png", 16)
                .label("square")
                .label("cross")
                .label("slash");
        registerTexture("sprites/gun.png", 16)
                .label("gun");
        registerTexture("effects/gunfire.png", 16)
                .label("gunfire");
        //registerTexture("cola/arksupply.png", 64).label("arksupply");
    }

    public static class ImageMapping {

        public SmartTexture texture;
        public RectF rect;
        public float height;
        public int size;

        public ImageMapping(SmartTexture texture, RectF rect, float height) {
            this.rect = rect;
            this.height = height;
            this.texture = texture;
            this.size = 16; // 默认大小
        }

        public ImageMapping(SmartTexture texture, RectF rect, float height, int size) {
            this.rect = rect;
            this.height = height;
            this.texture = texture;
            this.size = size;
        }
    }

    public static ImageMapping mapImage(int image) {
        if (image < 114514) {
            return null;

        } else {
            if (getSegment(image) != null) {
                Segment s = getSegment(image);

                ImageMapping im = s.get(image);

                return im;
            } else {
                System.out.println("Invalid image segment for " + image);
                
            }
            if (image == 114515) {
                return new ImageMapping(TextureCache.get("minecraft/bread.png"), new RectF(0f, 0, 1f, 1), 16);
            } else if (image == 114516) {
                // 直接创建金苹果的动态贴图，每次都随机生成不同外观
                int size = 16;
                TextureBuilder builder = new TextureBuilder(size, size);

                // 使用java.util.Random生成随机颜色
                java.util.Random rand = new java.util.Random();

                // 随机金色色调 (在金色范围内随机)
                int r = 220 + rand.nextInt(35); // 220-255
                int g = 180 + rand.nextInt(40); // 180-220
                int b = 10 + rand.nextInt(50);  // 10-60
                int goldColor = 0xFF000000 | (r << 16) | (g << 8) | b;

                // 随机深金色，比主体颜色暗一些
                r = Math.max(150, r - 40 - rand.nextInt(20));
                g = Math.max(120, g - 40 - rand.nextInt(20));
                b = Math.max(5, b - 5 - rand.nextInt(10));
                int darkGoldColor = 0xFF000000 | (r << 16) | (g << 8) | b;

                // 随机高光色，明亮的金色或白色
                r = Math.min(255, r + 20 + rand.nextInt(20));
                g = Math.min(255, g + 20 + rand.nextInt(20));
                b = Math.min(255, b + 40 + rand.nextInt(60));
                int highlightColor = 0xFF000000 | (r << 16) | (g << 8) | b;

                // 随机叶子颜色
                int leafR = 20 + rand.nextInt(60);  // 20-80
                int leafG = 150 + rand.nextInt(105); // 150-255
                int leafB = 20 + rand.nextInt(60);  // 20-80
                int leafColor = 0xFF000000 | (leafR << 16) | (leafG << 8) | leafB;

                // 随机茎颜色
                int stemR = 90 + rand.nextInt(80);  // 90-170
                int stemG = 40 + rand.nextInt(80);  // 40-120
                int stemB = 5 + rand.nextInt(40);   // 5-45
                int stemColor = 0xFF000000 | (stemR << 16) | (stemG << 8) | stemB;

                // 随机苹果大小和位置
                int appleSize = 5 + rand.nextInt(3);  // 5-7
                int offsetX = rand.nextInt(3) - 1;    // -1 to 1
                int offsetY = rand.nextInt(3) - 1;    // -1 to 1

                // 绘制苹果主体
                builder.setColor(goldColor);
                builder.fillCircle(size / 2 + offsetX, size / 2 + 1 + offsetY, appleSize);

                // 添加阴影效果 - 随机位置
                builder.setColor(darkGoldColor);
                int shadowX = size / 2 - 2 + rand.nextInt(3) - 1;
                int shadowY = size / 2 + 2 + rand.nextInt(3) - 1;
                builder.fillCircle(shadowX, shadowY, 2 + rand.nextInt(2));

                // 添加高光 - 随机位置
                builder.setColor(highlightColor);
                int highlightX = size / 2 + 2 + rand.nextInt(3) - 1;
                int highlightY = size / 2 - 1 + rand.nextInt(3) - 1;
                builder.fillCircle(highlightX, highlightY, 1 + rand.nextInt(2));

                // 随机梗的位置和大小
                int stemWidth = 1 + rand.nextInt(2);
                int stemHeight = 2 + rand.nextInt(2);
                int stemPosX = size / 2 - stemWidth / 2 + offsetX;
                int stemPosY = size / 2 - 5 + rand.nextInt(2) - 1;

                // 添加苹果梗
                builder.setColor(stemColor);
                builder.fillRect(stemPosX, stemPosY, stemWidth, stemHeight);

                // 随机叶子位置和大小
                int leafSize = 1 + rand.nextInt(2);
                int leafX = stemPosX + stemWidth + rand.nextInt(2);
                int leafY = stemPosY + rand.nextInt(2);

                // 添加叶子
                builder.setColor(leafColor);
                builder.fillCircle(leafX, leafY, leafSize);

                // 添加1-3个随机位置的金色闪光效果
                int numSparkles = 1 + rand.nextInt(3);
                for (int i = 0; i < numSparkles; i++) {
                    int sparkleX = rand.nextInt(size);
                    int sparkleY = rand.nextInt(size);

                    // 确保闪光在苹果上
                    double dist = Math.sqrt(Math.pow(sparkleX - (size / 2 + offsetX), 2)
                            + Math.pow(sparkleY - (size / 2 + offsetY), 2));
                    if (dist <= appleSize) {
                        // 随机闪光颜色 - 明亮的金黄色或白色
                        int sparkleR = 240 + rand.nextInt(15);
                        int sparkleG = 240 + rand.nextInt(15);
                        int sparkleB = 190 + rand.nextInt(65);
                        int sparkleColor = 0xBF000000 | (sparkleR << 16) | (sparkleG << 8) | sparkleB; // 75%透明度

                        builder.setColor(sparkleColor);
                        builder.fillCircle(sparkleX, sparkleY, 1);
                    }
                }

                // 构建贴图并返回
                return builder.build();
            } else {
                return new ImageMapping(TextureCache.get("minecraft/golden_apple.png"), new RectF(0.25f, 0, 0.75f, 1),
                        16);
            }
        }
    }
}
