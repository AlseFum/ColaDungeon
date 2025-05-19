/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.coladungeon.sprites;

import com.watabou.gltextures.TextureCache;
import com.watabou.gltextures.SmartTexture;
import java.util.ArrayList;
import java.util.HashMap;
import com.watabou.utils.RectF;

import com.watabou.noosa.TextureFilm;

/**
 * Manages custom sprite mappings for items
 */
public class ItemSpriteManager {
    private static ArrayList<Segment> segments = new ArrayList<>();
    public static HashMap<String, Integer> texture_id_map = new HashMap<>();
    private static int latestLocation = 120000;

    public static Segment getSegment(int id) {
        for (Segment s : segments) {
            if (s.id_start <= id && id <= s.id_start + s.id_size) {
                return s;
            }
        }
        return null;
    }

    public static int ByName(String name) {
        return texture_id_map.get(name);
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
        return getSegment(id).get(id);
    }

    public static ImageMapping getImageMapping(String label) {
        return getSegment(texture_id_map.get(label)).get(label);
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
            return new ImageMapping(cache, film.get(where), film.height(where));
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

    }

    static {
        registerTexture("minecraft/misc.png", 32)
            .span(144).label("skel");
        registerTexture("minecraft/bread.png", 16)
            .label("bread");
        registerTexture("minecraft/golden_apple.png", 16)
            .label("golden_apple");

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

    public static ImageMapping mapImage(int image) {
        if (image < 114514) {
            System.out.println("mapImage should not receive image:int<114514");
            return null;
            // return new ImageMapping(
            // TextureCache.get(Assets.Sprites.ITEMS),
            // ItemSpriteSheet.film.get(image),
            // ItemSpriteSheet.film.height(image));
        } else {
            if (getSegment(image) != null) {
                return getSegment(image).get(image);
            } else {
                System.out.println("Invalid image segment for " + image);
            }
            if (image == 114515) {
                return new ImageMapping(TextureCache.get("minecraft/bread.png"), new RectF(0f, 0, 1f, 1), 16);
            } else {
                return new ImageMapping(TextureCache.get("minecraft/golden_apple.png"), new RectF(0.25f, 0, 0.75f, 1),
                        16);
            }
        }
    }
}
