package com.coladungeon.items;

import com.coladungeon.Assets;
import com.coladungeon.Badges;
import com.coladungeon.Dungeon;
import com.coladungeon.Statistics;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Blindness;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Degrade;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.hero.Talent;
import com.coladungeon.effects.Speck;
import com.coladungeon.items.bags.Bag;
import com.coladungeon.items.food.CustomFood;
import com.coladungeon.items.weapon.missiles.MissileWeapon;
import com.coladungeon.items.weapon.missiles.darts.Dart;
import com.coladungeon.items.weapon.missiles.darts.TippedDart;
import com.coladungeon.journal.Catalog;
import com.coladungeon.journal.Notes;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.sprites.MissileSprite;
import com.coladungeon.ui.QuickSlotButton;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Item implements Bundlable {

    protected static final String TXT_TO_STRING_LVL = "%s %+d";
    protected static final String TXT_TO_STRING_X = "%s x%d";

    protected static final float TIME_TO_THROW = 1.0f;
    protected static final float TIME_TO_PICK_UP = 1.0f;
    protected static final float TIME_TO_DROP = 1.0f;

    public static final String AC_DROP = "DROP";
    public static final String AC_THROW = "THROW";

    protected String defaultAction;
    public boolean usesTargeting;

    //TODO should these be private and accessed through methods?
    public int image = 0;
    public int icon = -1; //used as an identifier for items with randomized images

    public boolean stackable = false;
    protected int quantity = 1;
    public boolean dropsDownHeap = false;

    private int level = 0;

    public boolean levelKnown = false;

    public boolean cursed;
    public boolean cursedKnown;

    // Unique items persist through revival
    public boolean unique = false;

    // These items are preserved even if the hero's inventory is lost via unblessed ankh
    // this is largely set by the resurrection window, items can override this to always be kept
    public boolean keptThoughLostInvent = false;

    // whether an item can be included in heroes remains
    public boolean bones = false;

    public static final Comparator<Item> itemComparator = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return Generator.Category.order(lhs) - Generator.Category.order(rhs);
        }
    };

    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = new ArrayList<>();
        actions.add(AC_DROP);
        actions.add(AC_THROW);
        // 使用反射获取所有带有@Action注解的方法
        for (java.lang.reflect.Method method : this.getClass().getDeclaredMethods()) {
            Action actionAnnotation = method.getAnnotation(Action.class);
            if (actionAnnotation != null && !actionAnnotation.isHidden()) {
                actions.add(actionAnnotation.raw());
            }
        }
        return actions;
    }

    public String actionName(String action, Hero hero) {
        // 使用反射获取所有带有@Action注解的方法
        for (java.lang.reflect.Method method
                : this.getClass().getDeclaredMethods()) {
            Action actionAnnotation = method.getAnnotation(Action.class);
            if (actionAnnotation != null && actionAnnotation.raw().equals(action)) {
                return actionAnnotation.title().isEmpty() ? action : actionAnnotation.title();
            }
        }
        return Messages.get(this, "ac_" + action);
    }

    public final boolean doPickUp(Hero hero) {
        return doPickUp(hero, hero.pos);
    }

    public boolean doPickUp(Hero hero, int pos) {
        if (collect(hero.belongings.backpack)) {

            GameScene.pickUp(this, pos);
            Sample.INSTANCE.play(Assets.Sounds.ITEM);
            hero.spendAndNext(TIME_TO_PICK_UP);
            return true;

        } else {
            return false;
        }
    }

    public void doDrop(Hero hero) {
        hero.spendAndNext(TIME_TO_DROP);
        int pos = hero.pos;
        Dungeon.level.drop(detachAll(hero.belongings.backpack), pos).sprite.drop(pos);
    }

    //resets an item's properties, to ensure consistency between runs
    public void reset() {
        keptThoughLostInvent = false;
    }

    public boolean keptThroughLostInventory() {
        return keptThoughLostInvent;
    }

    public void doThrow(Hero hero) {
        GameScene.selectCell(thrower);
    }

    public void execute(Hero hero, String action) {

        GameScene.cancel();
        curUser = hero;
        curItem = this;

        if (action.equals(AC_DROP)) {

            if (hero.belongings.backpack.contains(this) || isEquipped(hero)) {
                doDrop(hero);
            }

        } else if (action.equals(AC_THROW)) {

            if (hero.belongings.backpack.contains(this) || isEquipped(hero)) {
                doThrow(hero);
            }

        } else {
            for (java.lang.reflect.Method method : this.getClass().getDeclaredMethods()) {
                Action actionAnnotation = method.getAnnotation(Action.class);
                if (actionAnnotation != null && actionAnnotation.raw().equals(action)) {
                    try {
                        method.invoke(this, hero, action);
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    
    public String defaultAction() {
        // 首先检查是否设置了defaultAction字段
        if (defaultAction != null) {
            return defaultAction;
        }
        
        // 如果没有设置defaultAction，查找带有@Action(isDefault = true)注解的方法
        for (java.lang.reflect.Method method : this.getClass().getDeclaredMethods()) {
            Action actionAnnotation = method.getAnnotation(Action.class);
            if (actionAnnotation != null && actionAnnotation.isDefault()) {
                return actionAnnotation.raw();
            }
        }
        
        return null;
    }

    public void execute(Hero hero) {
        String action = defaultAction();
        if (action != null) {
            execute(hero, defaultAction());
        }
    }

    protected void onThrow(int cell) {
        Heap heap = Dungeon.level.drop(this, cell);
        if (!heap.isEmpty()) {
            heap.sprite.drop(cell);
        }
    }

    //takes two items and merges them (if possible)
    public Item merge(Item other) {
        if (isSimilar(other)) {
            quantity += other.quantity;
            other.quantity = 0;
        }
        return this;
    }

    public boolean collect(Bag container) {

        if (quantity <= 0) {
            return true;
        }

        ArrayList<Item> items = container.items;

        if (items.contains(this)) {
            return true;
        }

        for (Item item : items) {
            if (item instanceof Bag && ((Bag) item).canHold(this)) {
                if (collect((Bag) item)) {
                    return true;
                }
            }
        }

        if (!container.canHold(this)) {
            return false;
        }

        if (stackable) {
            for (Item item : items) {
                if (isSimilar(item)) {
                    item.merge(this);
                    item.updateQuickslot();
                    if (Dungeon.hero != null && Dungeon.hero.isAlive()) {
                        Badges.validateItemLevelAquired(this);
                        Talent.onItemCollected(Dungeon.hero, item);
                        if (isIdentified()) {
                            Catalog.setSeen(getClass());
                            Statistics.itemTypesDiscovered.add(getClass());
                        }
                    }
                    if (TippedDart.lostDarts > 0) {
                        Dart d = new Dart();
                        d.quantity(TippedDart.lostDarts);
                        TippedDart.lostDarts = 0;
                        if (!d.collect()) {
                            //have to handle this in an actor as we can't manipulate the heap during pickup
                            Actor.add(new Actor() {
                                {
                                    actPriority = VFX_PRIO;
                                }

                                @Override
                                protected boolean act() {
                                    Dungeon.level.drop(d, Dungeon.hero.pos).sprite.drop();
                                    Actor.remove(this);
                                    return true;
                                }
                            });
                        }
                    }
                    return true;
                }
            }
        }

        if (Dungeon.hero != null && Dungeon.hero.isAlive()) {
            Badges.validateItemLevelAquired(this);
            Talent.onItemCollected(Dungeon.hero, this);
            if (isIdentified()) {
                Catalog.setSeen(getClass());
                Statistics.itemTypesDiscovered.add(getClass());
            }
        }

        items.add(this);
        Dungeon.quickslot.replacePlaceholder(this);
        Collections.sort(items, itemComparator);
        updateQuickslot();
        return true;

    }

    public final boolean collect() {
        return collect(Dungeon.hero.belongings.backpack);
    }

    //returns a new item if the split was sucessful and there are now 2 items, otherwise null
    public Item split(int amount) {
        if (amount <= 0 || amount >= quantity()) {
            return null;
        } else {
            //pssh, who needs copy constructors?
            Item split = Reflection.newInstance(getClass());

            if (split == null) {
                return null;
            }

            Bundle copy = new Bundle();
            this.storeInBundle(copy);
            split.restoreFromBundle(copy);
            split.quantity(amount);
            quantity -= amount;

            return split;
        }
    }

    public Item duplicate() {
        Item dupe = Reflection.newInstance(getClass());
        if (dupe == null) {
            return null;
        }
        Bundle copy = new Bundle();
        this.storeInBundle(copy);
        dupe.restoreFromBundle(copy);
        return dupe;
    }

    public final Item detach(Bag container) {

        if (quantity <= 0) {

            return null;

        } else if (quantity == 1) {

            if (stackable) {
                Dungeon.quickslot.convertToPlaceholder(this);
            }

            return detachAll(container);

        } else {

            Item detached = split(1);
            updateQuickslot();
            if (detached != null) {
                detached.onDetach();
            }
            return detached;

        }
    }

    public final Item detachAll(Bag container) {
        Dungeon.quickslot.clearItem(this);

        for (Item item : container.items) {
            if (item == this) {
                container.items.remove(this);
                item.onDetach();
                container.grabItems(); //try to put more items into the bag as it now has free space
                updateQuickslot();
                return this;
            } else if (item instanceof Bag) {
                Bag bag = (Bag) item;
                if (bag.contains(this)) {
                    return detachAll(bag);
                }
            }
        }

        updateQuickslot();
        return this;
    }

    public boolean isSimilar(Item item) {
        if (this instanceof CustomFood && item instanceof CustomFood) {
            return ((CustomFood) this).id.equals(((CustomFood) item).id);
        }
        return getClass() == item.getClass();
    }

    protected void onDetach() {
    }

    //returns the true level of the item, ignoring all modifiers aside from upgrades
    public final int trueLevel() {
        return level;
    }

    //returns the persistant level of the item, only affected by modifiers which are persistent (e.g. curse infusion)
    public int level() {
        return level;
    }

    //returns the level of the item, after it may have been modified by temporary boosts/reductions
    //note that not all item properties should care about buffs/debuffs! (e.g. str requirement)
    public int buffedLvl() {
        //only the hero can be affected by Degradation
        if (Dungeon.hero != null && Dungeon.hero.buff(Degrade.class) != null
                && (isEquipped(Dungeon.hero) || Dungeon.hero.belongings.contains(this))) {
            return Degrade.reduceLevel(level());
        } else {
            return level();
        }
    }

    public void level(int value) {
        level = value;

        updateQuickslot();
    }

    public Item upgrade() {

        this.level++;

        updateQuickslot();

        return this;
    }

    final public Item upgrade(int n) {
        for (int i = 0; i < n; i++) {
            upgrade();
        }

        return this;
    }

    public Item degrade() {

        this.level--;

        return this;
    }

    final public Item degrade(int n) {
        for (int i = 0; i < n; i++) {
            degrade();
        }

        return this;
    }

    public int visiblyUpgraded() {
        return levelKnown ? level() : 0;
    }

    public int buffedVisiblyUpgraded() {
        return levelKnown ? buffedLvl() : 0;
    }

    public boolean visiblyCursed() {
        return cursed && cursedKnown;
    }

    public boolean isUpgradable() {
        return true;
    }

    public boolean isIdentified() {
        return levelKnown && cursedKnown;
    }

    public boolean isEquipped(Hero hero) {
        return false;
    }

    public final Item identify() {
        return identify(true);
    }

    public Item identify(boolean byHero) {

        if (byHero && Dungeon.hero != null && Dungeon.hero.isAlive()) {
            Catalog.setSeen(getClass());
            Statistics.itemTypesDiscovered.add(getClass());
        }

        levelKnown = true;
        cursedKnown = true;
        Item.updateQuickslot();

        return this;
    }

    public void onHeroGainExp(float levelPercent, Hero hero) {
        //do nothing by default
    }

    public static void evoke(Hero hero) {
        hero.sprite.emitter().burst(Speck.factory(Speck.EVOKE), 5);
    }

    public String title() {

        String name = name();

        if (visiblyUpgraded() != 0) {
            name = Messages.format(TXT_TO_STRING_LVL, name, visiblyUpgraded());
        }

        if (quantity > 1) {
            name = Messages.format(TXT_TO_STRING_X, name, quantity);
        }

        return name;

    }

    public String name() {
        return trueName();
    }

    public final String trueName() {
        return Messages.get(this, "name");
    }

    public int image() {
        return image;
    }

    public ItemSprite.Glowing glowing() {
        return null;
    }

    public Emitter emitter() {
        return null;
    }

    public String info() {

        if (Dungeon.hero != null) {
            Notes.CustomRecord note;
            if (this instanceof EquipableItem) {
                note = Notes.findCustomRecord(((EquipableItem) this).customNoteID);
            } else {
                note = Notes.findCustomRecord(getClass());
            }
            if (note != null) {
                //we swap underscore(0x5F) with low macron(0x2CD) here to avoid highlighting in the item window
                return Messages.get(this, "custom_note", note.title().replace('_', 'ˍ')) + "\n\n" + desc();
            }
        }

        return desc();
    }

    public String desc() {
        return Messages.get(this, "desc");
    }

    public int quantity() {
        return quantity;
    }

    public Item quantity(int value) {
        quantity = value;
        return this;
    }

    //item's value in gold coins
    public int value() {
        return 0;
    }

    //item's value in energy crystals
    public int energyVal() {
        return 0;
    }

    public Item virtual() {
        Item item = Reflection.newInstance(getClass());
        if (item == null) {
            return null;
        }

        item.quantity = 0;
        item.level = level;
        return item;
    }

    public Item random() {
        return this;
    }

    public String status() {
        return quantity != 1 ? Integer.toString(quantity) : null;
    }

    public static void updateQuickslot() {
        GameScene.updateItemDisplays = true;
    }

    private static final String QUANTITY = "quantity";
    private static final String LEVEL = "level";
    private static final String LEVEL_KNOWN = "levelKnown";
    private static final String CURSED = "cursed";
    private static final String CURSED_KNOWN = "cursedKnown";
    private static final String QUICKSLOT = "quickslotpos";
    private static final String KEPT_LOST = "kept_lost";

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(QUANTITY, quantity);
        bundle.put(LEVEL, level);
        bundle.put(LEVEL_KNOWN, levelKnown);
        bundle.put(CURSED, cursed);
        bundle.put(CURSED_KNOWN, cursedKnown);
        if (Dungeon.quickslot.contains(this)) {
            bundle.put(QUICKSLOT, Dungeon.quickslot.getSlot(this));
        }
        bundle.put(KEPT_LOST, keptThoughLostInvent);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        quantity = bundle.getInt(QUANTITY);
        levelKnown = bundle.getBoolean(LEVEL_KNOWN);
        cursedKnown = bundle.getBoolean(CURSED_KNOWN);

        int level = bundle.getInt(LEVEL);
        if (level > 0) {
            upgrade(level);
        } else if (level < 0) {
            degrade(-level);
        }

        cursed = bundle.getBoolean(CURSED);

        //only want to populate slot on first load.
        if (Dungeon.hero == null) {
            if (bundle.contains(QUICKSLOT)) {
                Dungeon.quickslot.setSlot(bundle.getInt(QUICKSLOT), this);
            }
        }

        keptThoughLostInvent = bundle.getBoolean(KEPT_LOST);
    }

    public int targetingPos(Hero user, int dst) {
        return throwPos(user, dst);
    }

    public int throwPos(Hero user, int dst) {
        return new Ballistica(user.pos, dst, Ballistica.PROJECTILE).collisionPos;
    }

    public void throwSound() {
        Sample.INSTANCE.play(Assets.Sounds.MISS, 0.6f, 0.6f, 1.5f);
    }

    public void cast(final Hero user, final int dst) {

        final int cell = throwPos(user, dst);
        user.sprite.zap(cell);
        user.busy();

        throwSound();

        Char enemy = Actor.findChar(cell);
        QuickSlotButton.target(enemy);

        final float delay = castDelay(user, dst);

        if (enemy != null) {
            ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                    reset(user.sprite,
                            enemy.sprite,
                            this,
                            new Callback() {
                        @Override
                        public void call() {
                            curUser = user;
                            Item i = Item.this.detach(user.belongings.backpack);
                            if (i != null) {
                                i.onThrow(cell);
                            }
                            if (curUser.hasTalent(Talent.IMPROVISED_PROJECTILES)
                                    && !(Item.this instanceof MissileWeapon)
                                    && curUser.buff(Talent.ImprovisedProjectileCooldown.class) == null) {
                                if (enemy != null && enemy.alignment != curUser.alignment) {
                                    Sample.INSTANCE.play(Assets.Sounds.HIT);
                                    Buff.affect(enemy, Blindness.class, 1f + curUser.pointsInTalent(Talent.IMPROVISED_PROJECTILES));
                                    Buff.affect(curUser, Talent.ImprovisedProjectileCooldown.class, 50f);
                                }
                            }
                            if (user.buff(Talent.LethalMomentumTracker.class) != null) {
                                user.buff(Talent.LethalMomentumTracker.class).detach();
                                user.next();
                            } else {
                                user.spendAndNext(delay);
                            }
                        }
                    });
        } else {
            ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                    reset(user.sprite,
                            cell,
                            this,
                            new Callback() {
                        @Override
                        public void call() {
                            curUser = user;
                            Item i = Item.this.detach(user.belongings.backpack);
                            user.spend(delay);
                            if (i != null) {
                                i.onThrow(cell);
                            }
                            user.next();
                        }
                    });
        }
    }

    public float castDelay(Char user, int dst) {
        return TIME_TO_THROW;
    }

    protected static Hero curUser = null;
    protected static Item curItem = null;

    public void setCurrent(Hero hero) {
        curUser = hero;
        curItem = this;
    }

    protected static CellSelector.Listener thrower = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                curItem.cast(curUser, target);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(Item.class, "prompt");
        }
    };

    public int price() {
        return 0;
    }

    public int getMaxStack() {
        return 1;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Action {

        String raw();  // 动作的标识符

        String title() default "";  // 动作的显示名称

        boolean isHidden() default false;

        boolean isDefault() default false;
    }
}
