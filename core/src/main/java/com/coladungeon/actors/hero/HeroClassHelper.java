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

package com.coladungeon.actors.hero;

/**
 * Utility class to help with HeroClass conversions from enum to class.
 * 
 * Since HeroClass is now a class instead of an enum, you need to:
 * 
 * 1. Replace switch statements with if-else statements
 *    Example: 
 *    - Before: switch (heroClass) { case WARRIOR: ... }
 *    - After: if (heroClass.equals(HeroClass.WARRIOR)) { ... }
 *
 * 2. When using HeroClass in switch, use HeroClass.C constants 
 *    Example:
 *    - Before: switch (HeroClass.toC(heroClass)) { case HeroClass.C.WARRIOR: ... }
 * 
 * 3. Use ordinal() when you need numeric value
 *    Example:
 *    - Use heroClass.ordinal() to get the numeric value
 */
public class HeroClassHelper {
    
    /**
     * Helper method for cleaner code when branching based on HeroClass
     * 
     * @param heroClass The hero class to test
     * @param expected The expected hero class
     * @return true if classes match
     */
    public static boolean is(HeroClass heroClass, HeroClass expected) {
        if (heroClass == null || expected == null) return false;
        return heroClass.equals(expected);
    }
    
    /**
     * Helper method to get the constant value for switch statements
     * 
     * @param heroClass The hero class
     * @return Constant value suitable for switch statements
     */
    public static int toC(HeroClass heroClass) {
        return HeroClass.toC(heroClass);
    }
    
    /**
     * Helper method to convert from constant back to HeroClass
     * 
     * @param constant The constant value
     * @return Corresponding HeroClass
     */
    public static HeroClass fromC(int constant) {
        return HeroClass.fromC(constant);
    }
} 