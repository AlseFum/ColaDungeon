package com.coladungeon.ui;

import com.coladungeon.Assets;
import com.coladungeon.actors.hero.HeroSubClass;
import com.coladungeon.actors.hero.abilities.ArmorAbility;
import com.coladungeon.actors.hero.spells.ClericSpell;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.RectF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Function;

/**
 * Manages hero icon mappings and textures with dynamic creation support
 */
public class HeroIconManager {
    private static ArrayList<Segment> segments = new ArrayList<>();
    public static HashMap<String, Integer> icon_id_map = new HashMap<>();
    private static int latestLocation = 130000;

    public static Segment getSegment(int id) {
        // First check existing segments
        for (Segment s : segments) {
            if (s.id_start <= id && id < s.id_start + s.id_size) {
                return s;
            }
        }

        // If no segment found, create a new one for action indicators and special icons
        if (id >= 100 && id < 130) {
            return registerTexture(Assets.Interfaces.HERO_ICONS, 16)
                .span(30)  // Ensure enough space for action indicators
                .label("ACTION_INDICATORS");
        }

        return null;
    }

    public static int byName(String name) {
        return icon_id_map.getOrDefault(name, NONE);
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
        // Special handling for NONE icon
        if (id == NONE) {
            return new ImageMapping(
                TextureCache.get(Assets.Interfaces.HERO_ICONS), 
                new RectF(0, 0, 1, 1), 
                16
            );
        }

        Segment segment = getSegment(id);
        if (segment == null) {
            // Fallback to NONE icon if no segment found
            return getImageMapping(NONE);
        }
        return segment.get(id);
    }

    public static ImageMapping getImageMapping(String label) {
        return getImageMapping(icon_id_map.getOrDefault(label, NONE));
    }

    public static class Segment {
        SmartTexture cache;
        int id_start;
        int id_size;
        int size;
        int cols;

        TextureFilm film;

        public Segment(String texture, int id_start, int size) {
            this.cache = TextureCache.get(texture);
            this.film = new TextureFilm(cache, size, size);
            this.id_start = id_start;
            this.id_size = 0;
            this.size = size;
            this.cols = (int) (cache.width / size);
        }

        private Segment settle(int id) {
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
            return new ImageMapping(cache, film.get(where), film.height(where));
        }

        public Segment label(String label) {
            icon_id_map.put(label, id_start + id_size);
            settle(id_size);
            id_size++;
            return this;
        }

        public Segment span(int size) {
            this.id_size += size;
            return this;
        }

        public ImageMapping get(String label) {
            return get(icon_id_map.get(label));
        }
    }

    public static class ImageMapping {
        public SmartTexture texture;
        public RectF rect;
        public float height;

        public ImageMapping(SmartTexture texture, RectF rect, float height) {
            this.rect = rect;
            this.height = height;
            this.texture = texture;
        }
    }

    // Static constants to match HeroIcon
    public static final int NONE = 127;

    // Subclasses
    public static final int BERSERKER   = 0;
    public static final int GLADIATOR   = 1;
    public static final int BATTLEMAGE  = 2;
    public static final int WARLOCK     = 3;
    public static final int ASSASSIN    = 4;
    public static final int FREERUNNER  = 5;
    public static final int SNIPER      = 6;
    public static final int WARDEN      = 7;
    public static final int CHAMPION    = 8;
    public static final int MONK        = 9;
    public static final int PRIEST      = 10;
    public static final int PALADIN     = 11;

    // Abilities
    public static final int HEROIC_LEAP     = 16;
    public static final int SHOCKWAVE       = 17;
    public static final int ENDURE          = 18;
    public static final int ELEMENTAL_BLAST = 19;
    public static final int WILD_MAGIC      = 20;
    public static final int WARP_BEACON     = 21;
    public static final int SMOKE_BOMB      = 22;
    public static final int DEATH_MARK      = 23;
    public static final int SHADOW_CLONE    = 24;
    public static final int SPECTRAL_BLADES = 25;
    public static final int NATURES_POWER   = 26;
    public static final int SPIRIT_HAWK     = 27;
    public static final int CHALLENGE       = 28;
    public static final int ELEMENTAL_STRIKE= 29;
    public static final int FEINT           = 30;
    public static final int ASCENDED_FORM   = 31;
    public static final int TRINITY         = 32;
    public static final int POWER_OF_MANY   = 33;
    public static final int RATMOGRIFY      = 34;

    // Cleric Spells
    public static final int GUIDING_LIGHT   = 40;
    public static final int HOLY_WEAPON     = 41;
    public static final int HOLY_WARD       = 42;
    public static final int HOLY_INTUITION  = 43;
    public static final int SHIELD_OF_LIGHT = 44;
    public static final int RECALL_GLYPH    = 45;
    public static final int SUNRAY          = 46;
    public static final int DIVINE_SENSE    = 47;
    public static final int BLESS           = 48;
    public static final int CLEANSE         = 49;
    public static final int RADIANCE        = 50;
    public static final int HOLY_LANCE      = 51;
    public static final int HALLOWED_GROUND = 52;
    public static final int MNEMONIC_PRAYER = 53;
    public static final int SMITE           = 54;
    public static final int LAY_ON_HANDS    = 55;
    public static final int AURA_OF_PROTECTION = 56;
    public static final int WALL_OF_LIGHT   = 57;
    public static final int DIVINE_INTERVENTION = 58;
    public static final int JUDGEMENT       = 59;
    public static final int FLASH           = 60;
    public static final int BODY_FORM       = 61;
    public static final int MIND_FORM       = 62;
    public static final int SPIRIT_FORM     = 63;
    public static final int BEAMING_RAY     = 64;
    public static final int LIFE_LINK       = 65;
    public static final int STASIS          = 66;

    // Action Indicators
    public static final int BERSERK         = 104;
    public static final int COMBO           = 105;
    public static final int PREPARATION     = 106;
    public static final int MOMENTUM        = 107;
    public static final int SNIPERS_MARK    = 108;
    public static final int WEAPON_SWAP     = 109;
    public static final int MONK_ABILITIES  = 110;

    // Static initialization for default hero icons
    static {
        registerTexture(Assets.Interfaces.HERO_ICONS, 16)
            // Subclasses
            .label("BERSERKER")
            .label("GLADIATOR")
            .label("BATTLEMAGE")
            .label("WARLOCK")
            .label("ASSASSIN")
            .label("FREERUNNER")
            .label("SNIPER")
            .label("WARDEN")
            .label("CHAMPION")
            .label("MONK")
            .label("PRIEST")
            .label("PALADIN")
            // Abilities
            .label("HEROIC_LEAP")
            .label("SHOCKWAVE")
            .label("ENDURE")
            .label("ELEMENTAL_BLAST")
            .label("WILD_MAGIC")
            .label("WARP_BEACON")
            .label("SMOKE_BOMB")
            .label("DEATH_MARK")
            .label("SHADOW_CLONE")
            .label("SPECTRAL_BLADES")
            .label("NATURES_POWER")
            .label("SPIRIT_HAWK")
            .label("CHALLENGE")
            .label("ELEMENTAL_STRIKE")
            .label("FEINT")
            .label("ASCENDED_FORM")
            .label("TRINITY")
            .label("POWER_OF_MANY")
            .label("RATMOGRIFY")
            // Cleric Spells
            .label("GUIDING_LIGHT")
            .label("HOLY_WEAPON")
            .label("HOLY_WARD")
            .label("HOLY_INTUITION")
            .label("SHIELD_OF_LIGHT")
            .label("RECALL_GLYPH")
            .label("SUNRAY")
            .label("DIVINE_SENSE")
            .label("BLESS")
            .label("CLEANSE")
            .label("RADIANCE")
            .label("HOLY_LANCE")
            .label("HALLOWED_GROUND")
            .label("MNEMONIC_PRAYER")
            .label("SMITE")
            .label("LAY_ON_HANDS")
            .label("AURA_OF_PROTECTION")
            .label("WALL_OF_LIGHT")
            .label("DIVINE_INTERVENTION")
            .label("JUDGEMENT")
            .label("FLASH")
            .label("BODY_FORM")
            .label("MIND_FORM")
            .label("SPIRIT_FORM")
            .label("BEAMING_RAY")
            .label("LIFE_LINK")
            .label("STASIS")
            // Action Indicators
            .label("BERSERK")
            .label("COMBO")
            .label("PREPARATION")
            .label("MOMENTUM")
            .label("SNIPERS_MARK")
            .label("WEAPON_SWAP")
            .label("MONK_ABILITIES");
    }

    // Utility methods
    public static int getIconForSubclass(HeroSubClass subClass) {
        try {
            return byName(subClass.name());
        } catch (Exception e) {
            return NONE;
        }
    }

    public static int getIconForArmorAbility(ArmorAbility ability) {
        try {
            return byName(ability.name());
        } catch (Exception e) {
            return NONE;
        }
    }

    public static int getIconForClericSpell(ClericSpell spell) {
        try {
            return byName(spell.name());
        } catch (Exception e) {
            return NONE;
        }
    }

    /**
     * Builder for creating dynamic hero icons
     */
    public static class HeroIconBuilder {
        private int size = 16;
        private String texturePath = Assets.Interfaces.HERO_ICONS;
        private Function<Random, Integer> colorGenerator;
        private String iconName;

        /**
         * Set the size of the icon
         * @param size Icon size in pixels
         * @return This builder
         */
        public HeroIconBuilder size(int size) {
            this.size = size;
            return this;
        }

        /**
         * Set a custom texture path
         * @param texturePath Path to the texture file
         * @return This builder
         */
        public HeroIconBuilder texture(String texturePath) {
            this.texturePath = texturePath;
            return this;
        }

        /**
         * Set a color generator function
         * @param colorGenerator Function to generate icon color
         * @return This builder
         */
        public HeroIconBuilder colorGenerator(Function<Random, Integer> colorGenerator) {
            this.colorGenerator = colorGenerator;
            return this;
        }

        /**
         * Set the icon name for registration
         * @param iconName Name to register the icon under
         * @return This builder
         */
        public HeroIconBuilder name(String iconName) {
            this.iconName = iconName;
            return this;
        }

        /**
         * Create a dynamic hero icon with a random color
         * @return The registered icon ID
         */
        public int create() {
            if (iconName == null) {
                throw new IllegalStateException("Icon name must be set");
            }

            // Create a new texture
            SmartTexture texture = TextureCache.get(texturePath);
            
            // Generate color if color generator is provided
            Random rand = new Random();
            int color = colorGenerator != null 
                ? colorGenerator.apply(rand) 
                : 0xFF000000 | (rand.nextInt(256) << 16) | (rand.nextInt(256) << 8) | rand.nextInt(256);

            // Create a new segment for this icon
            Segment segment = registerTexture(texturePath, size)
                .label(iconName);

            // Get the mapping for the new icon
            ImageMapping mapping = segment.get(iconName);

            // Here you could add more complex icon generation logic
            // For now, we're just registering the icon

            return byName(iconName);
        }

        /**
         * Predefined color generators
         */
        public static class ColorGenerators {
            /**
             * Generate a gold-like color
             * @return Color generator function
             */
            public static Function<Random, Integer> goldColor() {
                return (rand) -> {
                    int r = 220 + rand.nextInt(35); // 220-255
                    int g = 180 + rand.nextInt(40); // 180-220
                    int b = 10 + rand.nextInt(50);  // 10-60
                    return 0xFF000000 | (r << 16) | (g << 8) | b;
                };
            }

            /**
             * Generate a heroic blue color
             * @return Color generator function
             */
            public static Function<Random, Integer> heroicBlue() {
                return (rand) -> {
                    int r = 50 + rand.nextInt(50);   // 50-100
                    int g = 100 + rand.nextInt(100); // 100-200
                    int b = 200 + rand.nextInt(55);  // 200-255
                    return 0xFF000000 | (r << 16) | (g << 8) | b;
                };
            }
        }
    }

    /**
     * Create a new HeroIconBuilder
     * @return A new HeroIconBuilder instance
     */
    public static HeroIconBuilder builder() {
        return new HeroIconBuilder();
    }

    /**
     * Utility method to create a dynamic hero icon with a random color
     * @param iconName Name to register the icon under
     * @return The registered icon ID
     */
    public static int createDynamicIcon(String iconName) {
        return builder()
            .name(iconName)
            .create();
    }

    /**
     * Utility method to create a dynamic hero icon with a specific color generator
     * @param iconName Name to register the icon under
     * @param colorGenerator Function to generate icon color
     * @return The registered icon ID
     */
    public static int createDynamicIcon(String iconName, Function<Random, Integer> colorGenerator) {
        return builder()
            .name(iconName)
            .colorGenerator(colorGenerator)
            .create();
    }
} 