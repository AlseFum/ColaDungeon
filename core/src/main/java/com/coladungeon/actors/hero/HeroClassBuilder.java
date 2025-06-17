package com.coladungeon.actors.hero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.coladungeon.Assets;
import com.coladungeon.Badges;
import com.coladungeon.actors.hero.abilities.ArmorAbility;
import com.coladungeon.messages.Messages;
import com.watabou.utils.DeviceCompat;

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
    public ArrayList<Talent> classTalentsTier1 = new ArrayList<>();
    public ArrayList<Talent> classTalentsTier2 = new ArrayList<>();
    // public ArrayList<Talent> subclassTalents = new ArrayList<>();
    // public ArrayList<Talent> armorTalents = new ArrayList<>();

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

    public HeroClassBuilder initializer(Consumer<Hero> _initializer) {
        this.initializer = (hero) -> {
            hero.heroClass = HeroClassSheet.registeredClasses.get(id);
            HeroClassSheet.initCommon(hero);
            _initializer.accept(hero);
        };
        return this;
    }

    public HeroClassBuilder classTalentsTier1(Talent... talents) {
        Collections.addAll(this.classTalentsTier1, talents);
        return this;
    }

    public HeroClassBuilder classTalentsTier2(Talent... talents) {
        Collections.addAll(this.classTalentsTier2, talents);
        return this;
    }

    // public HeroClassBuilder subclassTalents(Talent... talents) {
    // Collections.addAll(this.subclassTalents, talents);
    // return this;
    // }

    // public HeroClassBuilder armorTalents(Talent... talents) {
    // Collections.addAll(this.armorTalents, talents);
    // return this;
    // }

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
        heroClass.classTalentsTier1 = classTalentsTier1;
        heroClass.classTalentsTier2 = classTalentsTier2;
        // heroClass.subclassTalents = subclassTalents;
        // heroClass.armorTalents = armorTalents;
        return heroClass;
    }

    public HeroClass register() {
        HeroClass heroClass = build();
        HeroClassSheet.register(heroClass);
        return heroClass;
    }
}