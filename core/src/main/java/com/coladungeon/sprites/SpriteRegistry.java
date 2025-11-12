package com.coladungeon.sprites;

import com.coladungeon.Assets;
import com.coladungeon.utils.EventBus;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.RectF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Unified sprite registry:
 * - Static (compile-time) items by ID via ItemSpriteSheet
 * - Dynamic (runtime) items by key via ItemSpriteManager
 * - Dynamic mobs by key with explicit fallback
 *
 * Rules:
 * - Any dynamic fetch MUST provide a fallback static id (items) or texture (mobs)
 * - If key not found, fallback is used
 */
public final class SpriteRegistry {

    private SpriteRegistry() {}

    // ---------------------------
    // Item dynamic atlas (moved from ItemSpriteManager)
    // ---------------------------

    // dynamic item segments and ids
    private static final ArrayList<ItemSegment> itemSegments = new ArrayList<>();
    public static final HashMap<String, Integer> ITEM_TEXTURE_ID_MAP = new HashMap<>();
    private static int latestItemLocation = 120000;

    public static ItemSegment getItemSegment(int id) {
        for (ItemSegment s : itemSegments) {
            if (s.id_start <= id && id <= s.id_start + s.id_size) {
                if (s.cache.bitmap == null) {
                    s.load();
                }
                return s;
            }
        }
        return null;
    }

    public static int itemByName(String name) {
        Integer res = ITEM_TEXTURE_ID_MAP.get(name);
        if (res == null) {
            System.out.println("Invalid texture name: " + name);
            return ItemSpriteSheet.SOMETHING;
        }
        return res;
    }

    public static ItemSegment registerItemTexture(String texture, int size) {
        ItemSegment s = new ItemSegment(texture, latestItemLocation, size);
        itemSegments.add(s);
        latestItemLocation += 1000;
        return s;
    }

    public static ItemSegment registerItemTexture(String texture) {
        return registerItemTexture(texture, 16);
    }

    public static ImageMapping getItemImageMapping(int id) {
        ItemSegment s = getItemSegment(id);
        if (s == null) {
            return null;
        }
        return s.get(id);
    }

    public static ImageMapping getItemImageMapping(String label) {
        Integer id = ITEM_TEXTURE_ID_MAP.get(label);
        if (id == null) return null;
        ItemSegment s = getItemSegment(id);
        return s != null ? s.get(label) : null;
    }

    public static class ItemSegment {
        SmartTexture cache;
        String path;
        int id_start;
        int id_size;
        int size;
        int cols;

        TextureFilm film;

        public ItemSegment(String texture, int id_start, int size) {
            this.path = texture;
            this.cache = TextureCache.get(texture);
            this.film = new TextureFilm(cache, size, size);
            this.id_start = id_start;
            this.id_size = 0;
            this.size = size;
            this.cols = (int) (cache.width / size);
        }

        public void load() {
            this.cache = TextureCache.get(path);
            this.film = new TextureFilm(cache, this.size, this.size);
        }

        private ItemSegment settle(int id) {
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

        public ItemSegment label(String label) {
            ITEM_TEXTURE_ID_MAP.put(label, id_start + id_size);
            settle(id_size);
            id_size++;
            return this;
        }

        /**
         * Reserve slots in current segment.
         * Use with care; intended for development utilities.
         */
        public ItemSegment span(int size) {
            this.id_size += size;
            return this;
        }

        public ImageMapping get(String label) {
            Integer id = ITEM_TEXTURE_ID_MAP.get(label);
            return id == null ? null : get(id);
        }

        public static int getByName(String name) {
            return SpriteRegistry.itemByName(name);
        }
    }

    /**
     * Dynamic image mapping container.
     */
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

    /**
     * Map an integer image id to a dynamic ImageMapping if it's in dynamic range.
     * Falls back to special hardcoded examples for some ids (legacy behavior).
     */
    public static ImageMapping mapItemImage(int image) {
        if (image < 114514) {
            return null;
        } else {
            ItemSegment seg = getItemSegment(image);
            if (seg != null) {
                return seg.get(image);
            } else {
                System.out.println("Invalid image segment for " + image);
            }
            if (image == 114515) {
                return new ImageMapping(TextureCache.get("minecraft/bread.png"), new RectF(0f, 0, 1f, 1), 16);
            } else if (image == 114516) {
                int size = 16;
                TextureBuilder builder = new TextureBuilder(size, size);

                java.util.Random rand = new java.util.Random();

                int r = 220 + rand.nextInt(35);
                int g = 180 + rand.nextInt(40);
                int b = 10 + rand.nextInt(50);
                int goldColor = 0xFF000000 | (r << 16) | (g << 8) | b;

                r = Math.max(150, r - 40 - rand.nextInt(20));
                g = Math.max(120, g - 40 - rand.nextInt(20));
                b = Math.max(5, b - 5 - rand.nextInt(10));
                int darkGoldColor = 0xFF000000 | (r << 16) | (g << 8) | b;

                r = Math.min(255, r + 20 + rand.nextInt(20));
                g = Math.min(255, g + 20 + rand.nextInt(20));
                b = Math.min(255, b + 40 + rand.nextInt(60));
                int highlightColor = 0xFF000000 | (r << 16) | (g << 8) | b;

                int leafR = 20 + rand.nextInt(60);
                int leafG = 150 + rand.nextInt(105);
                int leafB = 20 + rand.nextInt(60);
                int leafColor = 0xFF000000 | (leafR << 16) | (leafG << 8) | leafB;

                int stemR = 90 + rand.nextInt(80);
                int stemG = 40 + rand.nextInt(80);
                int stemB = 5 + rand.nextInt(40);
                int stemColor = 0xFF000000 | (stemR << 16) | (stemG << 8) | stemB;

                int appleSize = 5 + rand.nextInt(3);
                int offsetX = rand.nextInt(3) - 1;
                int offsetY = rand.nextInt(3) - 1;

                builder.setColor(goldColor);
                builder.fillCircle(size / 2 + offsetX, size / 2 + 1 + offsetY, appleSize);

                builder.setColor(darkGoldColor);
                int shadowX = size / 2 - 2 + rand.nextInt(3) - 1;
                int shadowY = size / 2 + 2 + rand.nextInt(3) - 1;
                builder.fillCircle(shadowX, shadowY, 2 + rand.nextInt(2));

                builder.setColor(highlightColor);
                int highlightX = size / 2 + 2 + rand.nextInt(3) - 1;
                int highlightY = size / 2 - 1 + rand.nextInt(3) - 1;
                builder.fillCircle(highlightX, highlightY, 1 + rand.nextInt(2));

                int stemWidth = 1 + rand.nextInt(2);
                int stemHeight = 2 + rand.nextInt(2);
                int stemPosX = size / 2 - stemWidth / 2 + offsetX;
                int stemPosY = size / 2 - 5 + rand.nextInt(2) - 1;

                builder.setColor(stemColor);
                builder.fillRect(stemPosX, stemPosY, stemWidth, stemHeight);

                int leafSize = 1 + rand.nextInt(2);
                int leafX = stemPosX + stemWidth + rand.nextInt(2);
                int leafY = stemPosY + rand.nextInt(2);

                builder.setColor(leafColor);
                builder.fillCircle(leafX, leafY, leafSize);

                int numSparkles = 1 + rand.nextInt(3);
                for (int i = 0; i < numSparkles; i++) {
                    int sparkleX = rand.nextInt(size);
                    int sparkleY = rand.nextInt(size);

                    double dist = Math.sqrt(Math.pow(sparkleX - (size / 2 + offsetX), 2)
                            + Math.pow(sparkleY - (size / 2 + offsetY), 2));
                    if (dist <= appleSize) {
                        int sparkleR = 240 + rand.nextInt(15);
                        int sparkleG = 240 + rand.nextInt(15);
                        int sparkleB = 190 + rand.nextInt(65);
                        int sparkleColor = 0xBF000000 | (sparkleR << 16) | (sparkleG << 8) | sparkleB;

                        builder.setColor(sparkleColor);
                        builder.fillCircle(sparkleX, sparkleY, 1);
                    }
                }

                return builder.build();
            } else {
                return new ImageMapping(TextureCache.get("minecraft/golden_apple.png"), new RectF(0.25f, 0, 0.75f, 1),
                        16);
            }
        }
    }

    /**
     * 初始化动态生成的贴图（示例）
     */
    public static void initDynamicTextures() {
        DynamicSpriteExample.createGoldenApple("golden_apple");
    }

    static {
        registerItemTexture("minecraft/misc.png", 16)
                .span(144).label("skel");
        registerItemTexture("minecraft/bread.png", 16)
                .label("bread");
        registerItemTexture("effects/misc.png", 16)
                .label("square")
                .label("cross")
                .label("slash");
        registerItemTexture("sprites/gun.png", 16)
                .label("gun");
        registerItemTexture("effects/gunfire.png", 16)
                .label("gunfire");
        registerItemTexture("cola/mask16.png", 16)
                .label("mask16");
        registerItemTexture("cola/mask32.png", 32)
                .label("mask32");
        registerItemTexture("cola/mask64.png", 64)
                .label("mask64");
        registerItemTexture("cola/arksupply.png", 64).label("arksupply");

        // Keep legacy event name for compatibility with existing listeners
        EventBus.fire("ItemSpriteManager:init");
    }

    // ---------------------------
    // Item side
    // ---------------------------

    /**
     * Returns a dynamic mapping by key with enforced fallback to a static item id.
     * If key is missing, returns mapping built from ItemSpriteSheet for elseId.
     */
    public static ImageMapping itemMapping(String key, int elseId){
        Objects.requireNonNull(key, "key");
        ImageMapping m = getItemImageMapping(key);
        if (m != null) return m;
        // fallback to static sheet id
        return staticItemMapping(elseId);
    }

    /**
     * Returns a static mapping built from ItemSpriteSheet film for a given id.
     */
    public static ImageMapping staticItemMapping(int id){
        RectF rect = ItemSpriteSheet.film.get(id);
        if (rect == null){
            // If invalid id, fallback to SOMETHING
            rect = ItemSpriteSheet.film.get(ItemSpriteSheet.SOMETHING);
        }
        SmartTexture tx = TextureCache.get(Assets.Sprites.ITEMS);
        float h = ItemSpriteSheet.film.height(id != 0 ? id : ItemSpriteSheet.SOMETHING);
        return new ImageMapping(tx, rect, h, ItemSpriteSheet.SIZE);
    }

    /**
     * Returns a resolved image id for backward compatibility:
     * - if dynamic key exists, returns its assigned dynamic id
     * - else returns fallback static id
     *
     * This is useful when code path expects an integer image id.
     */
    public static int itemImageId(String key, int elseId){
        Integer dyn = ITEM_TEXTURE_ID_MAP.get(key);
        return dyn != null ? dyn : elseId;
    }

    // ---------------------------
    // Mob side
    // ---------------------------

    public static final class MobDef {
        public final Object texture;  // path or Assets.Sprites.* handle
        public final int frameWidth;
        public final int frameHeight;
        public final Map<String, String> meta = new HashMap<>();

        public MobDef(Object texture, int frameWidth, int frameHeight){
            this.texture = texture;
            this.frameWidth = frameWidth;
            this.frameHeight = frameHeight;
        }
    }

    private static final Map<String, MobDef> mobDefs = new HashMap<>();

    /**
     * Registers a dynamic mob sprite definition.
     * Callers MUST ensure they also provide a fallback when fetching.
     */
    public static void registerMob(String key, MobDef def){
        if (key == null || def == null) return;
        mobDefs.put(key, def);
    }

    /**
     * Resolves mob texture handle by key, or returns fallbackTexture if key missing.
     * This only returns the texture object; framing remains the responsibility of caller.
     */
    public static Object mobTextureOr(Object fallbackTexture, String key){
        if (key != null){
            MobDef def = mobDefs.get(key);
            if (def != null && def.texture != null){
                return def.texture;
            }
        }
        return fallbackTexture;
    }

    /**
     * Returns a TextureFilm for a mob using a dynamic key if present; otherwise builds from fallback texture+size.
     */
    public static TextureFilm mobFilm(String key, Object fallbackTexture, int fallbackFrameW, int fallbackFrameH){
        if (key != null){
            MobDef def = mobDefs.get(key);
            if (def != null && def.texture != null){
                SmartTexture tx = TextureCache.get(def.texture);
                return new TextureFilm(tx, def.frameWidth, def.frameHeight);
            }
        }
        SmartTexture tx = TextureCache.get(fallbackTexture);
        return new TextureFilm(tx, fallbackFrameW, fallbackFrameH);
    }
}




