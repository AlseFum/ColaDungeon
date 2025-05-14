package com.cola;

import com.watabou.gltextures.TextureCache;
import com.watabou.gltextures.SmartTexture;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.watabou.utils.RectF;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ItemSpriteManager {
    private static final Map<String, Segment> textureMap = new HashMap<>();
    private static int latestLocation = 120000;
    private static final ArrayList<String> imageList = new ArrayList<>();

    public static void registerTexture(String texture, int size) {
        if (!textureMap.containsKey(texture)) {
            Segment segment = new Segment(texture, latestLocation, size);
            latestLocation += size;
            textureMap.put(texture, segment);
            imageList.add(texture);
        }
    }

    public static int getImage(String imagename) {
        Segment segment = textureMap.get(imagename);
        return segment != null ? segment.id_start : -1;
    }

    public static int getImageIndex(String imagename) {
        return imageList.indexOf(imagename);
    }

    public static int registerAndGetIndex(String texture, int size) {
        registerTexture(texture, size);
        return getImageIndex(texture);
    }

    public static class Segment {
        String texture;
        SmartTexture cache;
        int id_start;
        int id_size;

        public Segment(String texture, int id_start, int id_size) {
            this.texture = texture;
            this.cache = TextureCache.get(texture);
            this.id_start = id_start;
            this.id_size = id_size;
        }
    }

    public static class ImageMapping {
        public String path;
        public RectF rect;
        public float height;

        public ImageMapping(String path, RectF rect, float height) {
            this.path = path;
            this.rect = rect;
            this.height = height;
        }
    }

    public static ImageMapping mapImage(int image) {
        if (image < 114514) {
            return new ImageMapping(
                    Assets.Sprites.ITEMS,
                    ItemSpriteSheet.film.get(image),
                    ItemSpriteSheet.film.height(image));
        } else if (image == 114515) {
            return new ImageMapping("minecraft/bread.png", new RectF(0.25f, 0, 0.75f, 1), 16);
        } else {
            return new ImageMapping("minecraft/golden_apple.png", new RectF(0.25f, 0, 0.75f, 1), 16);
        }
    }
}
