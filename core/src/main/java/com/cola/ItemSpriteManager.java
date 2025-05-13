package com.cola;

import com.watabou.gltextures.TextureCache;
import com.watabou.gltextures.SmartTexture;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemSpriteManager {
    private static final Map<String, Segment> textureMap = new HashMap<>();
    private static int latestLocation = 120000;
    private static final ArrayList<String> imageList = new ArrayList<>();

    public static void registerTexture(String texture, int size) {
        Segment segment = new Segment(texture, latestLocation, size);
        latestLocation += size;
        textureMap.put(texture, segment);
        imageList.add(texture);
    }

    public static int getImage(String imagename) {
        Segment segment = textureMap.get(imagename);
        return segment != null ? segment.id_start : -1;
    }

    public static int getImageIndex(String imagename) {
        return imageList.indexOf(imagename);
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
}
