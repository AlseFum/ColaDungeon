package com.cola;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import java.util.HashMap;

public class CustomMissileWeapon extends MissileWeapon {

    {
        image = ItemSpriteSheet.THROWING_KNIFE; // Set the image for the weapon
        tier = 2; // Set the tier of the weapon
        hitSound = Assets.Sounds.HIT_SLASH; // Set the hit sound
        hitSoundPitch = 1.0f; // Set the pitch of the hit sound
    }

    // Additional methods and properties can be added here

    public static class CMWTpl {
        public String key = "defaultKey";
        public String label = "defaultLabel";
        public int image = ItemSpriteSheet.THROWING_KNIFE;
        public int tier = 2;
        public String hitSound = Assets.Sounds.HIT_SLASH;
        public float hitSoundPitch = 1.0f;
    }

    public static class Factory {
        private CMWTpl baking = new CMWTpl();

        public Factory(String key) {
            baking.key = key;
        }

        public Factory setLabel(String label) {
            baking.label = label;
            return this;
        }

        public Factory setImage(int image) {
            baking.image = image;
            return this;
        }

        public Factory setTier(int tier) {
            baking.tier = tier;
            return this;
        }

        public Factory setHitSound(String hitSound) {
            baking.hitSound = hitSound;
            return this;
        }

        public Factory setHitSoundPitch(float pitch) {
            baking.hitSoundPitch = pitch;
            return this;
        }

        public Factory register() {
            // Register the weapon template
            weapon_records.put(baking.key, baking);
            return this;
        }

        public static HashMap<String, CMWTpl> weapon_records = new HashMap<>();

        public CustomMissileWeapon make() {
            CustomMissileWeapon weapon = new CustomMissileWeapon();
            weapon.image = baking.image;
            weapon.tier = baking.tier;
            weapon.hitSound = baking.hitSound;
            weapon.hitSoundPitch = baking.hitSoundPitch;
            return weapon;
        }
    }
}
