package com.coladungeon.utils;

import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.RectF;

import java.util.ArrayList;
import java.util.HashMap;

public class TextureManager {
    protected static final ArrayList<TextureSegment> segments = new ArrayList<>();
    protected static HashMap<String, Integer> textureIdMap = new HashMap<>();
    protected static int latestLocation = 120000;

    public static TextureSegment getSegment(int id) {
        for (TextureSegment s : segments) {
            if (s.idStart <= id && id <= s.idStart + s.idSize) {
                if(s.cache.bitmap == null){
                    s.load();
                }
                return s;
            }
        }
        return null;
    }

    public static int getTextureIdByName(String name) {
        return textureIdMap.get(name);
    }

    public static TextureSegment registerTexture(String texture, int size) {
        TextureSegment s = new TextureSegment(texture, latestLocation, size);
        segments.add(s);
        latestLocation += 1000;
        return s;
    }

    public static TextureSegment registerTexture(String texture) {
        return registerTexture(texture, 16);
    }

    public static ImageMapping getImageMapping(int id) {
        TextureSegment s = getSegment(id);
        if (s == null) {
            return null;
        }
        return s.get(id);
    }

    public static ImageMapping getImageMapping(String label) {
        return getSegment(textureIdMap.get(label)).get(label);
    }

    public static class TextureSegment {
        SmartTexture cache;
        String path;
        int idStart;
        int idSize;
        int size;
        int cols;
        TextureFilm film;

        public TextureSegment(String texture, int idStart, int size) {
            this.path = texture;
            this.cache = TextureCache.get(texture);
            this.film = new TextureFilm(cache, size, size);
            this.idStart = idStart;
            this.idSize = 0;
            this.size = size;
            this.cols = (int) (cache.width / size);
        }

        public void load() {
            this.cache = TextureCache.get(path);
            this.film = new TextureFilm(cache, this.size, this.size);
        }

        private TextureSegment settle(int id) {
            int x = id % cols;
            int y = id / cols;
            film.add(id, x * size, y * size, (x + 1) * size, (y + 1) * size);
            return this;
        }

        public ImageMapping get(int id) {
            int where = id >= idStart ? id - idStart : id;
            if (film.get(id) == null) {
                settle(where);
            }
            return new ImageMapping(cache, film.get(where), film.height(where), size);
        }

        public TextureSegment label(String label) {
            textureIdMap.put(label, idStart + idSize);
            settle(idSize);
            idSize++;
            return this;
        }

        public TextureSegment span(int size) {
            this.idSize += size;
            return this;
        }

        public ImageMapping get(String label) {
            return get(textureIdMap.get(label));
        }
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
            this.size = 16;
        }

        public ImageMapping(SmartTexture texture, RectF rect, float height, int size) {
            this.rect = rect;
            this.height = height;
            this.texture = texture;
            this.size = size;
        }
    }
}
