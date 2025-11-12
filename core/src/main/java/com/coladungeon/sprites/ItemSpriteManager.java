package com.coladungeon.sprites;

import java.util.HashMap;

import com.watabou.gltextures.SmartTexture;
import com.watabou.utils.RectF;

/**
 * Backward-compat wrapper. Logic moved to SpriteRegistry.
 */
public class ItemSpriteManager {

    // Keep a shared reference so existing code can continue to mutate/read the map.
    public static HashMap<String, Integer> texture_id_map = SpriteRegistry.ITEM_TEXTURE_ID_MAP;

    public static Segment getSegment(int id) {
        SpriteRegistry.ItemSegment seg = SpriteRegistry.getItemSegment(id);
        return seg == null ? null : new Segment(seg);
    }

    public static int ByName(String name) {
        return SpriteRegistry.itemByName(name);
    }

    public static Segment registerTexture(String texture, int size) {
        return new Segment(SpriteRegistry.registerItemTexture(texture, size));
    }

    public static Segment registerTexture(String texture) {
        return registerTexture(texture, 16);
    }

    public static ImageMapping getImageMapping(int id) {
        SpriteRegistry.ImageMapping m = SpriteRegistry.getItemImageMapping(id);
        return m == null ? null : new ImageMapping(m.texture, m.rect, m.height, m.size);
    }

    public static ImageMapping getImageMapping(String label) {
        SpriteRegistry.ImageMapping m = SpriteRegistry.getItemImageMapping(label);
        return m == null ? null : new ImageMapping(m.texture, m.rect, m.height, m.size);
    }

    public static void initDynamicTextures() {
        SpriteRegistry.initDynamicTextures();
    }

    public static ImageMapping mapImage(int image) {
        SpriteRegistry.ImageMapping m = SpriteRegistry.mapItemImage(image);
        return m == null ? null : new ImageMapping(m.texture, m.rect, m.height, m.size);
    }

    public static class Segment {
        private final SpriteRegistry.ItemSegment delegate;

        public Segment(SpriteRegistry.ItemSegment delegate) {
            this.delegate = delegate;
        }

        public ImageMapping get(int id) {
            SpriteRegistry.ImageMapping m = delegate.get(id);
            return new ImageMapping(m.texture, m.rect, m.height, m.size);
        }

        public Segment label(String label) {
            delegate.label(label);
            return this;
        }

        public Segment span(int size) {
            delegate.span(size);
            return this;
        }

        public ImageMapping get(String label) {
            SpriteRegistry.ImageMapping m = delegate.get(label);
            return m == null ? null : new ImageMapping(m.texture, m.rect, m.height, m.size);
        }

        public static int getByName(String name) {
            return SpriteRegistry.itemByName(name);
        }
    }

    public static class ImageMapping extends SpriteRegistry.ImageMapping {
        public ImageMapping(SmartTexture texture, RectF rect, float height) {
            super(texture, rect, height);
        }
        public ImageMapping(SmartTexture texture, RectF rect, float height, int size) {
            super(texture, rect, height, size);
        }
    }
}
