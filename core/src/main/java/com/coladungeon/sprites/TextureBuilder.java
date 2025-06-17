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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.watabou.gltextures.SmartTexture;
import com.watabou.utils.RectF;
import com.coladungeon.sprites.ItemSpriteManager.ImageMapping;
import com.watabou.gltextures.TextureCache;

/**
 * A builder class for dynamically creating textures that can be used with ImageMapping.
 * Provides methods for drawing shapes, lines, and pixels to a custom texture.
 */
public class TextureBuilder {
    private Pixmap pixmap;
    private int width;
    private int height;
    private static int nextId = 999000; // Starting ID for dynamically created textures
    
    /**
     * Create a new TextureBuilder with specified dimensions
     */
    public TextureBuilder(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixmap = new Pixmap(width, height, Format.RGBA8888);
        this.pixmap.setColor(0, 0, 0, 0); // Transparent by default
        this.pixmap.fill();
    }
    
    /**
     * Set the current drawing color (ARGB format, same as used in the game)
     */
    public TextureBuilder setColor(int argb) {
        // Convert from Noosa ARGB to libGdx RGBA
        int rgba = (argb << 8) | (argb >>> 24);
        float a = ((rgba >>> 24) & 0xFF) / 255f;
        float r = ((rgba >>> 16) & 0xFF) / 255f;
        float g = ((rgba >>> 8) & 0xFF) / 255f;
        float b = (rgba & 0xFF) / 255f;
        pixmap.setColor(r, g, b, a);
        return this;
    }
    
    /**
     * Set color using separate RGBA components (0.0-1.0 range)
     */
    public TextureBuilder setColor(float r, float g, float b, float a) {
        pixmap.setColor(r, g, b, a);
        return this;
    }
    
    /**
     * Fill the entire texture with the current color
     */
    public TextureBuilder fill() {
        pixmap.fill();
        return this;
    }
    
    /**
     * Draw a filled rectangle
     */
    public TextureBuilder fillRect(int x, int y, int width, int height) {
        pixmap.fillRectangle(x, y, width, height);
        return this;
    }
    
    /**
     * Draw a rectangle outline
     */
    public TextureBuilder drawRect(int x, int y, int width, int height) {
        pixmap.drawRectangle(x, y, width, height);
        return this;
    }
    
    /**
     * Draw a filled circle
     */
    public TextureBuilder fillCircle(int x, int y, int radius) {
        pixmap.fillCircle(x, y, radius);
        return this;
    }
    
    /**
     * Draw a circle outline
     */
    public TextureBuilder drawCircle(int x, int y, int radius) {
        pixmap.drawCircle(x, y, radius);
        return this;
    }
    
    /**
     * Draw a line
     */
    public TextureBuilder drawLine(int x1, int y1, int x2, int y2) {
        pixmap.drawLine(x1, y1, x2, y2);
        return this;
    }
    
    /**
     * Set a single pixel
     */
    public TextureBuilder setPixel(int x, int y) {
        pixmap.drawPixel(x, y);
        return this;
    }
    
    /**
     * Build the texture and return an ImageMapping with a unique ID
     */
    public ImageMapping build() {
        SmartTexture texture = new SmartTexture(pixmap);
        RectF rect = new RectF(0, 0, 1, 1); // Full texture
        return new ImageMapping(texture, rect, height);
    }
    
    /**
     * Build and register the texture with ItemSpriteManager
     * @param name Name to register the texture with
     * @return The ImageMapping for this texture
     */
    public ImageMapping buildAndRegister(String name) {
        // Generate a unique ID for this texture
        int id = nextId++;
        
        // Create a unique key for this texture
        String key = "dynamic_texture_" + id;
        
        // Store the texture in TextureCache
        SmartTexture texture = TextureCache.create(key, width, height);
        
        // Copy our Pixmap data to the texture
        texture.bitmap(pixmap);
        
        // Create a mapping for the full texture
        RectF rect = new RectF(0, 0, 1, 1);
        
        // Register with ItemSpriteManager
        ItemSpriteManager.texture_id_map.put(name, id);
        
        return new ImageMapping(texture, rect, height);
    }
    
    /**
     * Create a texture at the specified size with a simple checkerboard pattern for testing
     */
    public static ImageMapping createCheckerboard(String name, int size, int squareSize, int color1, int color2) {
        TextureBuilder builder = new TextureBuilder(size, size);
        
        for (int x = 0; x < size; x += squareSize) {
            for (int y = 0; y < size; y += squareSize) {
                builder.setColor(((x / squareSize + y / squareSize) % 2 == 0) ? color1 : color2);
                builder.fillRect(x, y, 
                        Math.min(squareSize, size - x), 
                        Math.min(squareSize, size - y));
            }
        }
        
        return builder.buildAndRegister(name);
    }
    
    /**
     * Creates a BFG texture with a futuristic design
     */
    public static ImageMapping createBFGTexture() {
        TextureBuilder builder = new TextureBuilder(16, 16);
        
        // Main body of the gun (dark gray)
        builder.setColor(0xFF333333);
        builder.fillRect(3, 4, 13, 12);
        
        // Energy core (bright green)
        builder.setColor(0xFF00FF00);
        builder.fillCircle(8, 8, 2);
        
        // Barrel (light gray)
        builder.setColor(0xFF666666);
        builder.fillRect(1, 7, 3, 9);
        
        // Handle (dark gray)
        builder.setColor(0xFF333333);
        builder.fillRect(10, 12, 13, 15);
        
        // Energy vents (cyan)
        builder.setColor(0xFF00FFFF);
        builder.fillRect(5, 3, 6, 4);
        builder.fillRect(8, 3, 9, 4);
        builder.fillRect(11, 3, 12, 4);
        
        // Highlights (white)
        builder.setColor(0xFFFFFFFF);
        builder.drawLine(3, 6, 12, 6);
        builder.drawLine(3, 10, 12, 10);
        
        return builder.buildAndRegister("bfg");
    }
} 