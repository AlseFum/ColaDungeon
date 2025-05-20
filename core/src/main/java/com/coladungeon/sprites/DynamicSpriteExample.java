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

import com.coladungeon.sprites.ItemSpriteManager.ImageMapping;
import com.watabou.gltextures.SmartTexture;
import com.watabou.utils.RectF;
import com.watabou.utils.Random;
import com.watabou.noosa.Image;

/**
 * Example class showing how to use TextureBuilder to create dynamic sprites
 */
public class DynamicSpriteExample {

    /**
     * Creates a simple potion-like sprite with random colors
     * 
     * @return An ImageMapping that can be used for item sprites
     */
    public static ImageMapping createPotionSprite(String name) {
        int size = 16; // Standard item size
        TextureBuilder builder = new TextureBuilder(size, size);
        
        // Choose random colors for the potion
        int liquidColor = Random.Int(0xFF000000, 0xFFFFFFFF);
        int bottleColor = 0x88FFFFFF; // Transparent white
        int capColor = Random.Int(0xFF000000, 0xFFFFFFFF);
        
        // Draw the potion bottle (transparent glass bottle)
        builder.setColor(bottleColor);
        builder.fillRect(4, 3, 8, 10);
        
        // Draw the liquid inside
        builder.setColor(liquidColor);
        builder.fillRect(5, 8, 6, 4);
        
        // Draw the cap
        builder.setColor(capColor);
        builder.fillRect(3, 1, 10, 2);
        
        // Add shine effect
        builder.setColor(1.0f, 1.0f, 1.0f, 0.5f); // Semi-transparent white
        builder.fillRect(10, 5, 1, 3);
        
        return builder.buildAndRegister(name);
    }
    
    /**
     * Creates a gem-like sprite with random colors and facets
     * 
     * @return An ImageMapping that can be used for item sprites
     */
    public static ImageMapping createGemSprite(String name) {
        int size = 16;
        TextureBuilder builder = new TextureBuilder(size, size);
        
        // Choose a random color for the gem
        int baseColor = Random.Int(0xFF000000, 0xFFFFFFFF);
        int highlightColor = 0xFFFFFFFF; // White highlight
        
        // Basic gem shape
        builder.setColor(baseColor);
        builder.fillCircle(size/2, size/2, 5);
        
        // Add facets
        builder.setColor(highlightColor);
        for (int i = 0; i < 3; i++) {
            int x = Random.Int(size/2 - 3, size/2 + 3);
            int y = Random.Int(size/2 - 3, size/2 + 3);
            int facetSize = Random.Int(1, 3);
            builder.fillCircle(x, y, facetSize);
        }
        
        return builder.buildAndRegister(name);
    }
    
    /**
     * Creates a golden apple sprite
     * 
     * @return An ImageMapping for the golden apple
     */
    public static ImageMapping createGoldenApple(String name) {
        int size = 16;
        TextureBuilder builder = new TextureBuilder(size, size);
        
        // Apple base (golden color)
        int goldColor = 0xFFFFD700; // Gold color
        int darkGoldColor = 0xFFDAA520; // Darker gold for shading
        int highlightColor = 0xFFFFF8DC; // Very light gold for highlights
        
        // Draw the main apple body
        builder.setColor(goldColor);
        builder.fillCircle(size/2, size/2 + 1, 6);
        
        // Add shading to give 3D effect
        builder.setColor(darkGoldColor);
        builder.fillCircle(size/2 - 2, size/2 + 2, 3);
        
        // Add highlight
        builder.setColor(highlightColor);
        builder.fillCircle(size/2 + 2, size/2 - 1, 2);
        
        // Add stem
        int stemColor = 0xFF8B4513; // Brown
        builder.setColor(stemColor);
        builder.fillRect(size/2 - 1, size/2 - 5, 2, 3);
        
        // Add leaf
        int leafColor = 0xFF32CD32; // Lime green
        builder.setColor(leafColor);
        builder.fillCircle(size/2 + 2, size/2 - 4, 2);
        
        // Add golden sparkle effects
        builder.setColor(0xFFFFFFCC); // Very light yellow
        builder.fillCircle(size/2 - 3, size/2 - 2, 1);
        builder.fillCircle(size/2 + 4, size/2 + 3, 1);
        
        return builder.buildAndRegister(name);
    }
    
    /**
     * Creates a weapon-like sprite (sword)
     * 
     * @return An ImageMapping that can be used for item sprites
     */
    public static ImageMapping createWeaponSprite(String name) {
        int size = 16;
        TextureBuilder builder = new TextureBuilder(size, size);
        
        // Choose colors
        int bladeColor = 0xFFCCCCCC; // Silver
        int handleColor = 0xFF8B4513; // Brown
        int guardColor = 0xFFFFD700; // Gold
        
        // Draw the blade
        builder.setColor(bladeColor);
        builder.fillRect(7, 1, 2, 10);
        builder.fillRect(6, 2, 4, 1);
        
        // Draw the guard (cross piece)
        builder.setColor(guardColor);
        builder.fillRect(4, 11, 8, 1);
        
        // Draw the handle
        builder.setColor(handleColor);
        builder.fillRect(7, 12, 2, 3);
        
        return builder.buildAndRegister(name);
    }
    
    /**
     * Creates an Image based on a registered ImageMapping
     * This can be used directly in the UI for display
     */
    public static Image createImageFromMapping(String mappingName) {
        ImageMapping mapping = ItemSpriteManager.getImageMapping(mappingName);
        
        if (mapping != null) {
            Image img = new Image(mapping.texture);
            img.frame(mapping.rect);
            return img;
        }
        
        return null;
    }
    
    /**
     * Example method showing how to use different dynamic sprites
     */
    public static void createExampleSprites() {
        // Create different types of sprites
        createPotionSprite("dynamic_potion");
        createGemSprite("dynamic_gem");
        createWeaponSprite("dynamic_weapon");
        createGoldenApple("golden_apple");
        
        // Create a checkerboard pattern for testing
        TextureBuilder.createCheckerboard("checkerboard", 16, 4, 0xFF000000, 0xFFFFFFFF);
    }
} 