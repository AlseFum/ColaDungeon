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

import com.coladungeon.Assets;
import com.coladungeon.Badges;
import com.coladungeon.actors.hero.abilities.ArmorAbility;
import com.coladungeon.messages.Messages;
import com.watabou.utils.DeviceCompat;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Builder class for creating HeroClass instances.
 * Provides a fluent API for configuring hero classes.
 */
public class HeroClassBuilder {
    private String id;
    private HeroSubClass[] subClasses = new HeroSubClass[0];
    private Supplier<String> title;
    private Supplier<String> desc;
    private Supplier<String> shortDesc;
    private Supplier<String> unlockMsg;
    private Supplier<Boolean> unlocked = () -> DeviceCompat.isDebug();
    private Supplier<String> spritesheet = () -> Assets.Sprites.WARRIOR;
    private Supplier<String> splashArt = () -> Assets.Splashes.WARRIOR;
    private Supplier<ArmorAbility[]> abilities = () -> new ArmorAbility[0];
    private Supplier<Badges.Badge> masteryBadge = () -> null;
    private Consumer<Hero> initializer;

    public HeroClassBuilder(String id) {
        this.id = id;
        // 默认使用消息系统
        this.title = () -> Messages.get(HeroClass.class, id + "_title");
        this.desc = () -> Messages.get(HeroClass.class, id + "_desc");
        this.shortDesc = () -> Messages.get(HeroClass.class, id + "_desc_short");
        this.unlockMsg = () -> Messages.get(HeroClass.class, id + "_unlock");
    }

    public HeroClassBuilder subClasses(HeroSubClass... subClasses) {
        this.subClasses = subClasses;
        return this;
    }

    public HeroClassBuilder title(String title) {
        return title(() -> title);
    }

    public HeroClassBuilder title(Supplier<String> title) {
        this.title = title;
        return this;
    }

    public HeroClassBuilder desc(String desc) {
        return desc(() -> desc);
    }

    public HeroClassBuilder desc(Supplier<String> desc) {
        this.desc = desc;
        return this;
    }

    public HeroClassBuilder shortDesc(String shortDesc) {
        return shortDesc(() -> shortDesc);
    }

    public HeroClassBuilder shortDesc(Supplier<String> shortDesc) {
        this.shortDesc = shortDesc;
        return this;
    }

    public HeroClassBuilder unlockMsg(String unlockMsg) {
        return unlockMsg(() -> unlockMsg);
    }

    public HeroClassBuilder unlockMsg(Supplier<String> unlockMsg) {
        this.unlockMsg = unlockMsg;
        return this;
    }

    public HeroClassBuilder unlocked(boolean unlocked) {
        return unlocked(() -> DeviceCompat.isDebug() || unlocked);
    }

    public HeroClassBuilder unlocked(Supplier<Boolean> unlocked) {
        this.unlocked = () -> DeviceCompat.isDebug() || unlocked.get();
        return this;
    }

    public HeroClassBuilder spritesheet(String spritesheet) {
        return spritesheet(() -> spritesheet);
    }

    public HeroClassBuilder spritesheet(Supplier<String> spritesheet) {
        this.spritesheet = spritesheet;
        return this;
    }

    public HeroClassBuilder splashArt(String splashArt) {
        return splashArt(() -> splashArt);
    }

    public HeroClassBuilder splashArt(Supplier<String> splashArt) {
        this.splashArt = splashArt;
        return this;
    }

    public HeroClassBuilder abilities(ArmorAbility... abilities) {
        return abilities(() -> abilities);
    }

    public HeroClassBuilder abilities(Supplier<ArmorAbility[]> abilities) {
        this.abilities = abilities;
        return this;
    }

    public HeroClassBuilder masteryBadge(Badges.Badge badge) {
        return masteryBadge(() -> badge);
    }

    public HeroClassBuilder masteryBadge(Supplier<Badges.Badge> masteryBadge) {
        this.masteryBadge = masteryBadge;
        return this;
    }

    public HeroClassBuilder initializer(Consumer<Hero> initializer) {
        this.initializer = initializer;
        return this;
    }

    public HeroClass build() {
        HeroClass heroClass = new HeroClass(id);
        heroClass.subClasses = subClasses;
        heroClass.titleSupplier = title;
        heroClass.descSupplier = desc;
        heroClass.shortDescSupplier = shortDesc;
        heroClass.unlockMsgSupplier = unlockMsg;
        heroClass.unlockedSupplier = unlocked;
        heroClass.spritesheetSupplier = spritesheet;
        heroClass.splashArtSupplier = splashArt;
        heroClass.armorAbilitiesSupplier = abilities;
        heroClass.masteryBadgeSupplier = masteryBadge;
        heroClass.initializer = initializer;
        return heroClass;
    }

    public HeroClass register() {
        HeroClass heroClass = build();
        HeroClassSheet.register(heroClass);
        return heroClass;
    }
} 