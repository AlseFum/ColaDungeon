package com.coladungeon.actors.hero;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import com.coladungeon.Assets;
import com.coladungeon.ColaDungeon;
import com.coladungeon.Dungeon;
import com.coladungeon.GamesInProgress;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.ArtifactRecharge;
import com.coladungeon.actors.buffs.Barrier;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.CounterBuff;
import com.coladungeon.actors.buffs.EnhancedRings;
import com.coladungeon.actors.buffs.FlavourBuff;
import com.coladungeon.actors.buffs.Haste;
import com.coladungeon.actors.buffs.Invisibility;
import com.coladungeon.actors.buffs.PhysicalEmpower;
import com.coladungeon.actors.buffs.Recharging;
import com.coladungeon.actors.buffs.RevealedArea;
import com.coladungeon.actors.buffs.Roots;
import com.coladungeon.actors.buffs.ScrollEmpower;
import com.coladungeon.actors.buffs.WandEmpower;
import com.coladungeon.actors.hero.abilities.ArmorAbility;
import com.coladungeon.actors.hero.abilities.Ratmogrify;
import com.coladungeon.actors.hero.spells.DivineSense;
import com.coladungeon.actors.hero.spells.RecallInscription;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.Flare;
import com.coladungeon.effects.FloatingText;
import com.coladungeon.effects.SpellSprite;
import com.coladungeon.effects.particles.LeafParticle;
import com.coladungeon.items.BrokenSeal;
import com.coladungeon.items.Item;
import com.coladungeon.items.armor.Armor;
import com.coladungeon.items.armor.ClothArmor;
import com.coladungeon.items.artifacts.CloakOfShadows;
import com.coladungeon.items.artifacts.HolyTome;
import com.coladungeon.items.artifacts.HornOfPlenty;
import com.coladungeon.items.rings.Ring;
import com.coladungeon.items.scrolls.Scroll;
import com.coladungeon.items.scrolls.ScrollOfRecharging;
import com.coladungeon.items.scrolls.ScrollOfUpgrade;
import com.coladungeon.items.stones.Runestone;
import com.coladungeon.items.stones.StoneOfIntuition;
import com.coladungeon.items.trinkets.ShardOfOblivion;
import com.coladungeon.items.wands.Wand;
import com.coladungeon.items.weapon.SpiritBow;
import com.coladungeon.items.weapon.Weapon;
import com.coladungeon.items.weapon.melee.Gloves;
import com.coladungeon.items.weapon.melee.MeleeWeapon;
import com.coladungeon.items.weapon.missiles.MissileWeapon;
import com.coladungeon.levels.Level;
import com.coladungeon.levels.Terrain;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.CharSprite;
import com.coladungeon.ui.BuffIndicator;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import com.coladungeon.utils.EventBus;

public class Talent implements Bundlable {
	//#region contents
	private static final HashMap<String, Talent> talentRegistry = new HashMap<>();
	
	//Warrior T1
	public static final Talent HEARTY_MEAL = new Talent(0,3,"HEARTY_MEAL");
	public static final Talent VETERANS_INTUITION = new Talent(1,3,"VETERANS_INTUITION");
	public static final Talent PROVOKED_ANGER = new Talent(2,2,"PROVOKED_ANGER");
	public static final Talent IRON_WILL = new Talent(3,2,"IRON_WILL");
	//Warrior T2
	public static final Talent IRON_STOMACH = new Talent(4,2,"IRON_STOMACH");
	public static final Talent LIQUID_WILLPOWER = new Talent(5,2,"LIQUID_WILLPOWER");
	public static final Talent RUNIC_TRANSFERENCE = new Talent(6,2,"RUNIC_TRANSFERENCE");
	public static final Talent LETHAL_MOMENTUM = new Talent(7,2,"LETHAL_MOMENTUM");
	public static final Talent IMPROVISED_PROJECTILES = new Talent(8,2,"IMPROVISED_PROJECTILES");
	//Warrior T3
	public static final Talent HOLD_FAST = new Talent(9, 3,"HOLD_FAST");
	public static final Talent STRONGMAN = new Talent(10, 3,"STRONGMAN");
	//Berserker T3
	public static final Talent ENDLESS_RAGE = new Talent(11, 3,"ENDLESS_RAGE");
	public static final Talent DEATHLESS_FURY = new Talent(12, 3,"DEATHLESS_FURY");
	public static final Talent ENRAGED_CATALYST = new Talent(13, 3,"ENRAGED_CATALYST");
	//Gladiator T3
	public static final Talent CLEAVE = new Talent(14, 3,"CLEAVE");
	public static final Talent LETHAL_DEFENSE = new Talent(15, 3,"LETHAL_DEFENSE");
	public static final Talent ENHANCED_COMBO = new Talent(16, 3,"ENHANCED_COMBO");
	//Heroic Leap T4
	public static final Talent BODY_SLAM = new Talent(17, 4,"BODY_SLAM");
	public static final Talent IMPACT_WAVE = new Talent(18, 4,"IMPACT_WAVE");
	public static final Talent DOUBLE_JUMP = new Talent(19, 4,"DOUBLE_JUMP");
	//Shockwave T4
	public static final Talent EXPANDING_WAVE = new Talent(20, 4,"EXPANDING_WAVE");
	public static final Talent STRIKING_WAVE = new Talent(21, 4,"STRIKING_WAVE");
	public static final Talent SHOCK_FORCE = new Talent(22, 4,"SHOCK_FORCE");
	//Endure T4
	public static final Talent SUSTAINED_RETRIBUTION = new Talent(23, 4,"SUSTAINED_RETRIBUTION");
	public static final Talent SHRUG_IT_OFF = new Talent(24, 4,"SHRUG_IT_OFF");
	public static final Talent EVEN_THE_ODDS = new Talent(25, 4,"EVEN_THE_ODDS");

	//Mage T1
	public static final Talent EMPOWERING_MEAL = new Talent(32,2,"EMPOWERING_MEAL");
	public static final Talent SCHOLARS_INTUITION = new Talent(33,2,"SCHOLARS_INTUITION");
	public static final Talent LINGERING_MAGIC = new Talent(34,2,"LINGERING_MAGIC");
	public static final Talent BACKUP_BARRIER = new Talent(35,2,"BACKUP_BARRIER");
	//Mage T2
	public static final Talent ENERGIZING_MEAL = new Talent(36,2,"ENERGIZING_MEAL");
	public static final Talent INSCRIBED_POWER = new Talent(37,2,"INSCRIBED_POWER");
	public static final Talent WAND_PRESERVATION = new Talent(38,2,"WAND_PRESERVATION");
	public static final Talent ARCANE_VISION = new Talent(39,2,"ARCANE_VISION");
	public static final Talent SHIELD_BATTERY = new Talent(40,2,"SHIELD_BATTERY");
	//Mage T3
	public static final Talent DESPERATE_POWER = new Talent(41, 3,"DESPERATE_POWER");
	public static final Talent ALLY_WARP = new Talent(42, 3,"ALLY_WARP");
	//Battlemage T3
	public static final Talent EMPOWERED_STRIKE = new Talent(43, 3,"EMPOWERED_STRIKE");
	public static final Talent MYSTICAL_CHARGE = new Talent(44, 3,"MYSTICAL_CHARGE");
	public static final Talent EXCESS_CHARGE = new Talent(45, 3,"EXCESS_CHARGE");
	//Warlock T3
	public static final Talent SOUL_EATER = new Talent(46, 3,"SOUL_EATER");
	public static final Talent SOUL_SIPHON = new Talent(47, 3,"SOUL_SIPHON");
	public static final Talent NECROMANCERS_MINIONS = new Talent(48, 3,"NECROMANCERS_MINIONS");
	//Elemental Blast T4
	public static final Talent BLAST_RADIUS = new Talent(49, 4,"BLAST_RADIUS");
	public static final Talent ELEMENTAL_POWER = new Talent(50, 4,"ELEMENTAL_POWER");
	public static final Talent REACTIVE_BARRIER = new Talent(51, 4,"REACTIVE_BARRIER");
	//Wild Magic T4
	public static final Talent WILD_POWER = new Talent(52, 4,"WILD_POWER");
	public static final Talent FIRE_EVERYTHING = new Talent(53, 4,"FIRE_EVERYTHING");
	public static final Talent CONSERVED_MAGIC = new Talent(54, 4,"CONSERVED_MAGIC");
	//Warp Beacon T4
	public static final Talent TELEFRAG = new Talent(55, 4,"TELEFRAG");
	public static final Talent REMOTE_BEACON = new Talent(56, 4,"REMOTE_BEACON");
	public static final Talent LONGRANGE_WARP = new Talent(57, 4,"LONGRANGE_WARP");

	//Rogue T1
	public static final Talent CACHED_RATIONS = new Talent(64,2,"CACHED_RATIONS");
	public static final Talent THIEFS_INTUITION = new Talent(65,2,"THIEFS_INTUITION");
	public static final Talent SUCKER_PUNCH = new Talent(66,2,"SUCKER_PUNCH");
	public static final Talent PROTECTIVE_SHADOWS = new Talent(67,2,"PROTECTIVE_SHADOWS");
	//Rogue T2
	public static final Talent MYSTICAL_MEAL = new Talent(68,2,"MYSTICAL_MEAL");
	public static final Talent INSCRIBED_STEALTH = new Talent(69,2,"INSCRIBED_STEALTH");
	public static final Talent WIDE_SEARCH = new Talent(70,2,"WIDE_SEARCH");
	public static final Talent SILENT_STEPS = new Talent(71,2,"SILENT_STEPS");
	public static final Talent ROGUES_FORESIGHT = new Talent(72,2,"ROGUES_FORESIGHT");
	//Rogue T3
	public static final Talent ENHANCED_RINGS = new Talent(73, 3,"ENHANCED_RINGS");
	public static final Talent LIGHT_CLOAK = new Talent(74, 3,"LIGHT_CLOAK");
	//Assassin T3
	public static final Talent ENHANCED_LETHALITY = new Talent(75, 3,"ENHANCED_LETHALITY");
	public static final Talent ASSASSINS_REACH = new Talent(76, 3,"ASSASSINS_REACH");
	public static final Talent BOUNTY_HUNTER = new Talent(77, 3,"BOUNTY_HUNTER");
	//Freerunner T3
	public static final Talent EVASIVE_ARMOR = new Talent(78, 3,"EVASIVE_ARMOR");
	public static final Talent PROJECTILE_MOMENTUM = new Talent(79, 3,"PROJECTILE_MOMENTUM");
	public static final Talent SPEEDY_STEALTH = new Talent(80, 3,"SPEEDY_STEALTH");
	//Smoke Bomb T4
	public static final Talent HASTY_RETREAT = new Talent(81, 4,"HASTY_RETREAT");
	public static final Talent BODY_REPLACEMENT = new Talent(82, 4,"BODY_REPLACEMENT");
	public static final Talent SHADOW_STEP = new Talent(83, 4,"SHADOW_STEP");
	//Death Mark T4
	public static final Talent FEAR_THE_REAPER = new Talent(84, 4,"FEAR_THE_REAPER");
	public static final Talent DEATHLY_DURABILITY = new Talent(85, 4,"DEATHLY_DURABILITY");
	public static final Talent DOUBLE_MARK = new Talent(86, 4,"DOUBLE_MARK");
	//Shadow Clone T4
	public static final Talent SHADOW_BLADE = new Talent(87, 4,"SHADOW_BLADE");
	public static final Talent CLONED_ARMOR = new Talent(88, 4,"CLONED_ARMOR");
	public static final Talent PERFECT_COPY = new Talent(89, 4,"PERFECT_COPY");

	//Huntress T1
	public static final Talent NATURES_BOUNTY = new Talent(96,2,"NATURES_BOUNTY");
	public static final Talent SURVIVALISTS_INTUITION = new Talent(97,2,"SURVIVALISTS_INTUITION");
	public static final Talent FOLLOWUP_STRIKE = new Talent(98,2,"FOLLOWUP_STRIKE");
	public static final Talent NATURES_AID = new Talent(99,2,"NATURES_AID");
	//Huntress T2
	public static final Talent INVIGORATING_MEAL = new Talent(100,2,"INVIGORATING_MEAL");
	public static final Talent LIQUID_NATURE = new Talent(101,2,"LIQUID_NATURE");
	public static final Talent REJUVENATING_STEPS = new Talent(102,2,"REJUVENATING_STEPS");
	public static final Talent HEIGHTENED_SENSES = new Talent(103,2,"HEIGHTENED_SENSES");
	public static final Talent DURABLE_PROJECTILES = new Talent(104,2,"DURABLE_PROJECTILES");
	//Huntress T3
	public static final Talent POINT_BLANK = new Talent(105, 3,"POINT_BLANK");
	public static final Talent SEER_SHOT = new Talent(106, 3,"SEER_SHOT");
	//Sniper T3
	public static final Talent FARSIGHT = new Talent(107, 3,"FARSIGHT");
	public static final Talent SHARED_ENCHANTMENT = new Talent(108, 3,"SHARED_ENCHANTMENT");
	public static final Talent SHARED_UPGRADES = new Talent(109, 3,"SHARED_UPGRADES");
	//Warden T3
	public static final Talent DURABLE_TIPS = new Talent(110, 3,"DURABLE_TIPS");
	public static final Talent BARKSKIN = new Talent(111, 3,"BARKSKIN");
	public static final Talent SHIELDING_DEW = new Talent(112, 3,"SHIELDING_DEW");
	//Spectral Blades T4
	public static final Talent FAN_OF_BLADES = new Talent(113, 4,"FAN_OF_BLADES");
	public static final Talent PROJECTING_BLADES = new Talent(114, 4,"PROJECTING_BLADES");
	public static final Talent SPIRIT_BLADES = new Talent(115, 4,"SPIRIT_BLADES");
	//Natures Power T4
	public static final Talent GROWING_POWER = new Talent(116, 4,"GROWING_POWER");
	public static final Talent NATURES_WRATH = new Talent(117, 4,"NATURES_WRATH");
	public static final Talent WILD_MOMENTUM = new Talent(118, 4,"WILD_MOMENTUM");
	//Spirit Hawk T4
	public static final Talent EAGLE_EYE = new Talent(119, 4,"EAGLE_EYE");
	public static final Talent GO_FOR_THE_EYES = new Talent(120, 4,"GO_FOR_THE_EYES");
	public static final Talent SWIFT_SPIRIT = new Talent(121, 4,"SWIFT_SPIRIT");

	//Duelist T1
	public static final Talent STRENGTHENING_MEAL = new Talent(128,2,"STRENGTHENING_MEAL");
	public static final Talent ADVENTURERS_INTUITION = new Talent(129,2,"ADVENTURERS_INTUITION");
	public static final Talent PATIENT_STRIKE = new Talent(130,2,"PATIENT_STRIKE");
	public static final Talent AGGRESSIVE_BARRIER = new Talent(131,2,"AGGRESSIVE_BARRIER");
	//Duelist T2
	public static final Talent FOCUSED_MEAL = new Talent(132,2,"FOCUSED_MEAL");
	public static final Talent LIQUID_AGILITY = new Talent(133,2,"LIQUID_AGILITY");
	public static final Talent WEAPON_RECHARGING = new Talent(134,2,"WEAPON_RECHARGING");
	public static final Talent LETHAL_HASTE = new Talent(135,2,"LETHAL_HASTE");
	public static final Talent SWIFT_EQUIP = new Talent(136,2,"SWIFT_EQUIP");
	//Duelist T3
	public static final Talent PRECISE_ASSAULT = new Talent(137, 3,"PRECISE_ASSAULT");
	public static final Talent DEADLY_FOLLOWUP = new Talent(138, 3,"DEADLY_FOLLOWUP");
	//Champion T3
	public static final Talent VARIED_CHARGE = new Talent(139, 3,"VARIED_CHARGE");
	public static final Talent TWIN_UPGRADES = new Talent(140, 3,"TWIN_UPGRADES");
	public static final Talent COMBINED_LETHALITY = new Talent(141, 3,"COMBINED_LETHALITY");
	//Monk T3
	public static final Talent UNENCUMBERED_SPIRIT = new Talent(142, 3,"UNENCUMBERED_SPIRIT");
	public static final Talent MONASTIC_VIGOR = new Talent(143, 3,"MONASTIC_VIGOR");
	public static final Talent COMBINED_ENERGY = new Talent(144, 3,"COMBINED_ENERGY");
	//Challenge T4
	public static final Talent CLOSE_THE_GAP = new Talent(145, 4,"CLOSE_THE_GAP");
	public static final Talent INVIGORATING_VICTORY = new Talent(146, 4,"INVIGORATING_VICTORY");
	public static final Talent ELIMINATION_MATCH = new Talent(147, 4,"ELIMINATION_MATCH");
	//Elemental Strike T4
	public static final Talent ELEMENTAL_REACH = new Talent(148, 4,"ELEMENTAL_REACH");
	public static final Talent STRIKING_FORCE = new Talent(149, 4,"STRIKING_FORCE");
	public static final Talent DIRECTED_POWER = new Talent(150, 4,"DIRECTED_POWER");
	//Feint T4
	public static final Talent FEIGNED_RETREAT = new Talent(151, 4,"FEIGNED_RETREAT");
	public static final Talent EXPOSE_WEAKNESS = new Talent(152, 4,"EXPOSE_WEAKNESS");
	public static final Talent COUNTER_ABILITY = new Talent(153, 4,"COUNTER_ABILITY");

	//Cleric T1
	public static final Talent SATIATED_SPELLS = new Talent(160,2,"SATIATED_SPELLS");
	public static final Talent HOLY_INTUITION = new Talent(161,2,"HOLY_INTUITION");
	public static final Talent SEARING_LIGHT = new Talent(162,2,"SEARING_LIGHT");
	public static final Talent SHIELD_OF_LIGHT = new Talent(163,2,"SHIELD_OF_LIGHT");
	//Cleric T2
	public static final Talent ENLIGHTENING_MEAL = new Talent(164,2,"ENLIGHTENING_MEAL");
	public static final Talent RECALL_INSCRIPTION = new Talent(165,2,"RECALL_INSCRIPTION");
	public static final Talent SUNRAY = new Talent(166,2,"SUNRAY");
	public static final Talent DIVINE_SENSE = new Talent(167,2,"DIVINE_SENSE");
	public static final Talent BLESS = new Talent(168,2,"BLESS");
	//Cleric T3
	public static final Talent CLEANSE = new Talent(169, 3,"CLEANSE");
	public static final Talent LIGHT_READING = new Talent(170, 3,"LIGHT_READING");
	//Priest T3
	public static final Talent HOLY_LANCE = new Talent(171, 3,"HOLY_LANCE");
	public static final Talent HALLOWED_GROUND = new Talent(172, 3,"HALLOWED_GROUND");
	public static final Talent MNEMONIC_PRAYER = new Talent(173, 3,"MNEMONIC_PRAYER");
	//Paladin T3
	public static final Talent LAY_ON_HANDS = new Talent(174, 3,"LAY_ON_HANDS");
	public static final Talent AURA_OF_PROTECTION = new Talent(175, 3,"AURA_OF_PROTECTION");
	public static final Talent WALL_OF_LIGHT = new Talent(176, 3,"WALL_OF_LIGHT");
	//Ascended Form T4
	public static final Talent DIVINE_INTERVENTION = new Talent(177, 4,"DIVINE_INTERVENTION");
	public static final Talent JUDGEMENT = new Talent(178, 4,"JUDGEMENT");
	public static final Talent FLASH = new Talent(179, 4,"FLASH");
	//Trinity T4
	public static final Talent BODY_FORM = new Talent(180, 4,"BODY_FORM");
	public static final Talent MIND_FORM = new Talent(181, 4,"MIND_FORM");
	public static final Talent SPIRIT_FORM = new Talent(182, 4,"SPIRIT_FORM");
	//Power of Many T4
	public static final Talent BEAMING_RAY = new Talent(183, 4,"BEAMING_RAY");
	public static final Talent LIFE_LINK = new Talent(184, 4,"LIFE_LINK");
	public static final Talent STASIS = new Talent(185, 4,"STASIS");

	//universal T4
	public static final Talent HEROIC_ENERGY = new Talent(26, 4,"HEROIC_ENERGY"); //See icon() and title() for special logic for this one
	//Ratmogrify T4
	public static final Talent RATSISTANCE = new Talent(215, 4,"RATSISTANCE");
	public static final Talent RATLOMACY = new Talent(216, 4,"RATLOMACY");
	public static final Talent RATFORCEMENTS = new Talent(217, 4,"RATFORCEMENTS");

	public static class ImprovisedProjectileCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.15f, 0.2f, 0.5f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }
	};
	public static class LethalMomentumTracker extends FlavourBuff{};
	public static class StrikingWaveTracker extends FlavourBuff{};
	public static class WandPreservationCounter extends CounterBuff{{revivePersists = true;}};
	public static class EmpoweredStrikeTracker extends FlavourBuff{
		//blast wave on-hit doesn't resolve instantly, so we delay detaching for it
		public boolean delayedDetach = false;
	};
	public static class ProtectiveShadowsTracker extends Buff {
		float barrierInc = 0.5f;

		@Override
		public boolean act() {
			//barrier every 2/1 turns, to a max of 3/5
			if (((Hero)target).hasTalent(Talent.PROTECTIVE_SHADOWS) && target.invisible > 0){
				Barrier barrier = Buff.affect(target, Barrier.class);
				if (barrier.shielding() < 1 + 2*((Hero)target).pointsInTalent(Talent.PROTECTIVE_SHADOWS)) {
					barrierInc += 0.5f * ((Hero) target).pointsInTalent(Talent.PROTECTIVE_SHADOWS);
				}
				if (barrierInc >= 1){
					barrierInc = 0;
					barrier.incShield(1);
				} else {
					barrier.incShield(0); //resets barrier decay
				}
			} else {
				detach();
			}
			spend( TICK );
			return true;
		}

		private static final String BARRIER_INC = "barrier_inc";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( BARRIER_INC, barrierInc);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			barrierInc = bundle.getFloat( BARRIER_INC );
		}
	}
	public static class BountyHunterTracker extends FlavourBuff{};
	public static class RejuvenatingStepsCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0f, 0.35f, 0.15f); }
		public float iconFadePercent() { return GameMath.gate(0, visualcooldown() / (15 - 5*Dungeon.hero.pointsInTalent(REJUVENATING_STEPS)), 1); }
	};
	public static class RejuvenatingStepsFurrow extends CounterBuff{{revivePersists = true;}};
	public static class SeerShotCooldown extends FlavourBuff{
		public int icon() { return target.buff(RevealedArea.class) != null ? BuffIndicator.NONE : BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.7f, 0.4f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 20); }
	};
	public static class SpiritBladesTracker extends FlavourBuff{};
	public static class PatientStrikeTracker extends Buff {
		public int pos;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		@Override
		public boolean act() {
			if (pos != target.pos) {
				detach();
			} else {
				spend(TICK);
			}
			return true;
		}
		private static final String POS = "pos";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(POS, pos);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			pos = bundle.getInt(POS);
		}
	};
	public static class AggressiveBarrierCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }
	};
	public static class LiquidAgilEVATracker extends FlavourBuff{};
	public static class LiquidAgilACCTracker extends FlavourBuff{
		public int uses;

		{ type = buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }

		private static final String USES = "uses";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(USES, uses);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			uses = bundle.getInt(USES);
		}
	};
	public static class LethalHasteCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 100); }
	};
	public static class SwiftEquipCooldown extends FlavourBuff{
		public boolean secondUse;
		public boolean hasSecondUse(){
			return secondUse;
		}

		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) {
			if (hasSecondUse()) icon.hardlight(0.85f, 0f, 1.0f);
			else                icon.hardlight(0.35f, 0f, 0.7f);
		}
		public float iconFadePercent() { return GameMath.gate(0, visualcooldown() / 20f, 1); }

		private static final String SECOND_USE = "second_use";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(SECOND_USE, secondUse);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			secondUse = bundle.getBoolean(SECOND_USE);
		}
	};
	public static class DeadlyFollowupTracker extends FlavourBuff{
		public int object;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
		private static final String OBJECT    = "object";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(OBJECT, object);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			object = bundle.getInt(OBJECT);
		}
	}
	public static class PreciseAssaultTracker extends FlavourBuff{
		{ type = buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(1f, 1f, 0.0f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	};
	public static class VariedChargeTracker extends Buff{
		public Class weapon;

		private static final String WEAPON    = "weapon";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(WEAPON, weapon);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			weapon = bundle.getClass(WEAPON);
		}
	}
	public static class CombinedLethalityAbilityTracker extends FlavourBuff{
		public MeleeWeapon weapon;
	};
	public static class CombinedEnergyAbilityTracker extends FlavourBuff{
		public boolean monkAbilused = false;
		public boolean wepAbilUsed = false;

		private static final String MONK_ABIL_USED  = "monk_abil_used";
		private static final String WEP_ABIL_USED   = "wep_abil_used";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(MONK_ABIL_USED, monkAbilused);
			bundle.put(WEP_ABIL_USED, wepAbilUsed);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			monkAbilused = bundle.getBoolean(MONK_ABIL_USED);
			wepAbilUsed = bundle.getBoolean(WEP_ABIL_USED);
		}
	}
	public static class CounterAbilityTacker extends FlavourBuff{}
	public static class SatiatedSpellsTracker extends Buff{
		@Override
		public int icon() {
			return BuffIndicator.SPELL_FOOD;
		}
	}
	//used for metamorphed searing light
	public static class SearingLightCooldown extends FlavourBuff{
		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}
		@Override
		public void tintIcon(Image icon) { icon.hardlight(0f, 0f, 1f); }
		@Override
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 20); }
	}
	//#endregion
    //#region BasicClass
	public  int icon;
	public  int maxPoints;
	public  String name;

	// tiers 1/2/3/4 start at levels 2/7/13/21
	public static int[] tierLevelThresholds = new int[]
		{0,2, 7, 13, 21, 31};

	Talent(int icon) {
		this(icon, 2);
	}
	Talent(int icon, int maxPoints) {
		this(icon, 2,"UnknownYet");
	}
	Talent(int icon, int maxPoints, String _name){
		this.icon = icon;
		this.maxPoints = maxPoints;
		this.name=_name;
		registerTalent(this);
	}
	

	public int icon(){
		if (this == HEROIC_ENERGY){
			if (Ratmogrify.useRatroicEnergy){
				return 218;
			}
			HeroClass cls = Dungeon.hero != null 
				? Dungeon.hero.heroClass 
				: GamesInProgress.selectedClass;
			if (cls == HeroClassSheet.WARRIOR) {
					return 26;
			} else if (cls == HeroClassSheet.MAGE) {
					return 58;
			} else if (cls == HeroClassSheet.ROGUE) {
					return 90;
			} else if (cls == HeroClassSheet.HUNTRESS) {
					return 122;
			} else if (cls == HeroClassSheet.DUELIST) {
					return 154;
			} else if (cls == HeroClassSheet.CLERIC) {
					return 186;
			} else {
				return 26; // default to warrior
			}
		} else {
			return icon;
		}
	}

	public int maxPoints(){
		return maxPoints;
	}

	public String title(){
		if (this == HEROIC_ENERGY && Ratmogrify.useRatroicEnergy){
			return Messages.get(this, name() + ".rat_title");
		}
		return Messages.get(this, name() + ".title");
	}

	public final String desc(){
		return desc(false);
	}

	public String desc(boolean metamorphed){
		if (metamorphed){
			String metaDesc = Messages.get(this, name() + ".meta_desc");
			if (!metaDesc.equals(Messages.NO_TEXT_FOUND)){
				return Messages.get(this, name() + ".desc") + "\n\n" + metaDesc;
			}
		}
		return Messages.get(this, name() + ".desc");
	}

	public String name() {
		return name;
	}

	public static Talent valueOf(String name) {
		Talent talent = talentRegistry.get(name);
		if (talent == null) {
			throw new IllegalArgumentException("No talent constant with name " + name);
		}
		return talent;
	}

	private static void registerTalent(Talent talent) {
		talentRegistry.put(talent.name(), talent);
	}
	static {
		EventBus.on("upgrade_talent", (Object args) -> {
			if (!(args instanceof EventBus.EventData)) return null;
			EventBus.EventData e = (EventBus.EventData) args;
			Hero hero = e.get("hero");
			Talent talent = e.get("talent");
			//for metamorphosis
		if (talent == IRON_WILL && hero.heroClass != HeroClassSheet.WARRIOR){
			Buff.affect(hero, BrokenSeal.WarriorShield.class);
		}

		if (talent == VETERANS_INTUITION && hero.pointsInTalent(VETERANS_INTUITION) == 2){
			if (hero.belongings.armor() != null && !ShardOfOblivion.passiveIDDisabled())  {
				hero.belongings.armor.identify();
			}
		}
		if (talent == THIEFS_INTUITION && hero.pointsInTalent(THIEFS_INTUITION) == 2){
			if (hero.belongings.ring instanceof Ring && !ShardOfOblivion.passiveIDDisabled()) {
				hero.belongings.ring.identify();
			}
			if (hero.belongings.misc instanceof Ring && !ShardOfOblivion.passiveIDDisabled()) {
				hero.belongings.misc.identify();
			}
			for (Item item : Dungeon.hero.belongings){
				if (item instanceof Ring){
					((Ring) item).setKnown();
				}
			}
		}
		if (talent == THIEFS_INTUITION && hero.pointsInTalent(THIEFS_INTUITION) == 1){
			if (hero.belongings.ring instanceof Ring) hero.belongings.ring.setKnown();
			if (hero.belongings.misc instanceof Ring) ((Ring) hero.belongings.misc).setKnown();
		}
		if (talent == ADVENTURERS_INTUITION && hero.pointsInTalent(ADVENTURERS_INTUITION) == 2){
			if (hero.belongings.weapon() != null && !ShardOfOblivion.passiveIDDisabled()){
				hero.belongings.weapon().identify();
			}
		}

		if (talent == PROTECTIVE_SHADOWS && hero.invisible > 0){
			Buff.affect(hero, Talent.ProtectiveShadowsTracker.class);
		}

		if (talent == LIGHT_CLOAK && hero.heroClass == HeroClassSheet.ROGUE){
			for (Item item : Dungeon.hero.belongings.backpack){
				if (item instanceof CloakOfShadows){
					if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
						((CloakOfShadows) item).activate(Dungeon.hero);
					}
				}
			}
		}

		if (talent == HEIGHTENED_SENSES || talent == FARSIGHT || talent == DIVINE_SENSE){
			Dungeon.observe();
		}

		if (talent == TWIN_UPGRADES || talent == DESPERATE_POWER
				|| talent == STRONGMAN || talent == DURABLE_PROJECTILES){
			Item.updateQuickslot();
		}

		if (talent == UNENCUMBERED_SPIRIT && hero.pointsInTalent(talent) == 3){
			Item toGive = new ClothArmor().identify();
			if (!toGive.collect()){
				Dungeon.level.drop(toGive, hero.pos).sprite.drop();
			}
			toGive = new Gloves().identify();
			if (!toGive.collect()){
				Dungeon.level.drop(toGive, hero.pos).sprite.drop();
			}
		}

		if (talent == LIGHT_READING && hero.heroClass == HeroClassSheet.CLERIC){
			for (Item item : Dungeon.hero.belongings.backpack){
				if (item instanceof HolyTome){
					if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
						((HolyTome) item).activate(Dungeon.hero);
					}
				}
			}
		}

		//if we happen to have spirit form applied with a ring of might
		if (talent == SPIRIT_FORM){
			Dungeon.hero.updateHT(false);
		}
		return null;
		});
		
		// 注册食物事件监听器
		EventBus.on("talent.food.eaten", (Object args) -> {
			if (!(args instanceof EventBus.EventData)) return null;
			EventBus.EventData e = (EventBus.EventData) args;
			Hero hero = e.get("hero");
			float foodVal = e.get("foodVal");
			Item foodSource = e.get("foodSource");
			
			if (hero.hasTalent(HEARTY_MEAL)){
				//3/5 HP healed, when hero is below 30% health
				if (hero.HP/(float)hero.HT <= 0.3f) {
					int healing = 1 + 2 * hero.pointsInTalent(HEARTY_MEAL);
					hero.HP = Math.min(hero.HP + healing, hero.HT);
					hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healing), FloatingText.HEALING);
				}
			}
			if (hero.hasTalent(IRON_STOMACH)){
				if (hero.cooldown() > 0) {
					Buff.affect(hero, WarriorFoodImmunity.class, hero.cooldown());
				}
			}
			if (hero.hasTalent(EMPOWERING_MEAL)){
				//2/3 bonus wand damage for next 3 zaps
				Buff.affect( hero, WandEmpower.class).set(1 + hero.pointsInTalent(EMPOWERING_MEAL), 3);
				ScrollOfRecharging.charge( hero );
			}
			if (hero.hasTalent(ENERGIZING_MEAL)){
				//5/8 turns of recharging
				Buff.prolong( hero, Recharging.class, 2 + 3*(hero.pointsInTalent(ENERGIZING_MEAL)) );
				ScrollOfRecharging.charge( hero );
				SpellSprite.show(hero, SpellSprite.CHARGE);
			}
			if (hero.hasTalent(MYSTICAL_MEAL)){
				//3/5 turns of recharging
				ArtifactRecharge buff = Buff.affect( hero, ArtifactRecharge.class);
				if (buff.left() < 1 + 2*(hero.pointsInTalent(MYSTICAL_MEAL))){
					Buff.affect( hero, ArtifactRecharge.class).set(1 + 2*(hero.pointsInTalent(MYSTICAL_MEAL))).ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
				}
				ScrollOfRecharging.charge( hero );
				SpellSprite.show(hero, SpellSprite.CHARGE, 0, 1, 1);
			}
			if (hero.hasTalent(INVIGORATING_MEAL)){
				//effectively 1/2 turns of haste
				Buff.prolong( hero, Haste.class, 0.67f+hero.pointsInTalent(INVIGORATING_MEAL));
			}
			if (hero.hasTalent(STRENGTHENING_MEAL)){
				//3 bonus physical damage for next 2/3 attacks
				Buff.affect( hero, PhysicalEmpower.class).set(3, 1 + hero.pointsInTalent(STRENGTHENING_MEAL));
			}
			if (hero.hasTalent(FOCUSED_MEAL)){
				if (hero.heroClass == HeroClassSheet.DUELIST){
					//0.67/1 charge for the duelist
					Buff.affect( hero, MeleeWeapon.Charger.class ).gainCharge((hero.pointsInTalent(FOCUSED_MEAL)+1)/3f);
					ScrollOfRecharging.charge( hero );
				} else {
					// lvl/3 / lvl/2 bonus dmg on next hit for other classes
					Buff.affect( hero, PhysicalEmpower.class).set(Math.round(hero.lvl / (4f - hero.pointsInTalent(FOCUSED_MEAL))), 1);
				}
			}
			if (hero.hasTalent(SATIATED_SPELLS)){
				if (hero.heroClass == HeroClassSheet.CLERIC) {
					Buff.affect(hero, SatiatedSpellsTracker.class);
				} else {
					//3/5 shielding, delayed up to 10 turns
					int amount = 1 + 2*hero.pointsInTalent(SATIATED_SPELLS);
					Barrier b = Buff.affect(hero, Barrier.class);
					if (b.shielding() <= amount){
						b.setShield(amount);
						b.delay(Math.max(10-b.cooldown(), 0));
					}
				}
			}
			if (hero.hasTalent(ENLIGHTENING_MEAL)){
				if (hero.heroClass == HeroClassSheet.CLERIC) {
					HolyTome tome = hero.belongings.getItem(HolyTome.class);
					if (tome != null) {
						tome.directCharge( 0.5f * (1+hero.pointsInTalent(ENLIGHTENING_MEAL)));
						ScrollOfRecharging.charge(hero);
					}
				} else {
					//2/3 turns of recharging
					ArtifactRecharge buff = Buff.affect( hero, ArtifactRecharge.class);
					if (buff.left() < 1 + (hero.pointsInTalent(ENLIGHTENING_MEAL))){
						Buff.affect( hero, ArtifactRecharge.class).set(1 + (hero.pointsInTalent(ENLIGHTENING_MEAL))).ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
					}
					Buff.prolong( hero, Recharging.class, 1 + (hero.pointsInTalent(ENLIGHTENING_MEAL)) );
					ScrollOfRecharging.charge( hero );
					SpellSprite.show(hero, SpellSprite.CHARGE);
				}
			}
			return null;
		});
		
		// 注册药水事件监听器
		EventBus.on("talent.potion.used", (Object args) -> {
			if (!(args instanceof EventBus.EventData)) return null;
			EventBus.EventData e = (EventBus.EventData) args;
			Hero hero = e.get("hero");
			int cell = e.get("cell");
			float factor = e.get("factor");
			
			if (hero.hasTalent(LIQUID_WILLPOWER)){
				if (hero.heroClass == HeroClassSheet.WARRIOR) {
					BrokenSeal.WarriorShield shield = hero.buff(BrokenSeal.WarriorShield.class);
					if (shield != null) {
						// 50/75% of total shield
						int shieldToGive = Math.round(factor * shield.maxShield() * 0.25f * (1 + hero.pointsInTalent(LIQUID_WILLPOWER)));
						hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldToGive), FloatingText.SHIELDING);
						shield.supercharge(shieldToGive);
					}
				} else {
					// 5/7.5% of max HP
					int shieldToGive = Math.round( factor * hero.HT * (0.025f * (1+hero.pointsInTalent(LIQUID_WILLPOWER))));
					hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldToGive), FloatingText.SHIELDING);
					Buff.affect(hero, Barrier.class).setShield(shieldToGive);
				}
			}
			if (hero.hasTalent(LIQUID_NATURE)){
				ArrayList<Integer> grassCells = new ArrayList<>();
				for (int i : PathFinder.NEIGHBOURS9){
					grassCells.add(cell+i);
				}
				Random.shuffle(grassCells);
				for (int grassCell : grassCells){
					Char ch = Actor.findChar(grassCell);
					if (ch != null && ch.alignment == Char.Alignment.ENEMY){
						//1/2 turns of roots
						Buff.affect(ch, Roots.class, factor * hero.pointsInTalent(LIQUID_NATURE));
					}
					if (Dungeon.level.map[grassCell] == Terrain.EMPTY ||
							Dungeon.level.map[grassCell] == Terrain.EMBERS ||
							Dungeon.level.map[grassCell] == Terrain.EMPTY_DECO){
						Level.set(grassCell, Terrain.GRASS);
						GameScene.updateMap(grassCell);
					}
					CellEmitter.get(grassCell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
				}
				// 4/6 cells total
				int totalGrassCells = (int) (factor * (2 + 2 * hero.pointsInTalent(LIQUID_NATURE)));
				while (grassCells.size() > totalGrassCells){
					grassCells.remove(0);
				}
				for (int grassCell : grassCells){
					int t = Dungeon.level.map[grassCell];
					if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
							|| t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
							&& Dungeon.level.plants.get(grassCell) == null){
						Level.set(grassCell, Terrain.HIGH_GRASS);
						GameScene.updateMap(grassCell);
					}
				}
				Dungeon.observe();
			}
			if (hero.hasTalent(LIQUID_AGILITY)){
				Buff.prolong(hero, LiquidAgilEVATracker.class, hero.cooldown() + Math.max(0, factor-1));
				if (factor >= 0.5f){
					Buff.prolong(hero, LiquidAgilACCTracker.class, 5f).uses = Math.round(factor);
				}
			}
			return null;
		});
		
		// 注册卷轴事件监听器
		EventBus.on("talent.scroll.used", (Object args) -> {
			if (!(args instanceof EventBus.EventData)) return null;
			EventBus.EventData e = (EventBus.EventData) args;
			Hero hero = e.get("hero");
			int pos = e.get("pos");
			float factor = e.get("factor");
			Class<?extends Item> cls = e.get("cls");
			
			if (hero.hasTalent(INSCRIBED_POWER)){
				// 2/3 empowered wand zaps
				Buff.affect(hero, ScrollEmpower.class).reset((int) (factor * (1 + hero.pointsInTalent(INSCRIBED_POWER))));
			}
			if (hero.hasTalent(INSCRIBED_STEALTH)){
				// 3/5 turns of stealth
				Buff.affect(hero, Invisibility.class, factor * (1 + 2*hero.pointsInTalent(INSCRIBED_STEALTH)));
				Sample.INSTANCE.play( Assets.Sounds.MELD );
			}
			if (hero.hasTalent(RECALL_INSCRIPTION) && Scroll.class.isAssignableFrom(cls) && cls != ScrollOfUpgrade.class){
				if (hero.heroClass == HeroClassSheet.CLERIC){
					Buff.prolong(hero, RecallInscription.UsedItemTracker.class, hero.pointsInTalent(RECALL_INSCRIPTION) == 2 ? 300 : 10).item = cls;
				} else {
					// 10/15%
					if (Random.Int(20) < 1 + hero.pointsInTalent(RECALL_INSCRIPTION)){
						Reflection.newInstance(cls).collect();
						GLog.p("refunded!");
					}
				}
			}
			return null;
		});
		
		// 注册符石事件监听器
		EventBus.on("talent.runestone.used", (Object args) -> {
			if (!(args instanceof EventBus.EventData)) return null;
			EventBus.EventData e = (EventBus.EventData) args;
			Hero hero = e.get("hero");
			int pos = e.get("pos");
			Class<?extends Item> cls = e.get("cls");
			
			if (hero.hasTalent(RECALL_INSCRIPTION) && Runestone.class.isAssignableFrom(cls)){
				if (hero.heroClass == HeroClassSheet.CLERIC){
					Buff.prolong(hero, RecallInscription.UsedItemTracker.class, hero.pointsInTalent(RECALL_INSCRIPTION) == 2 ? 300 : 10).item = cls;
				} else {
					//don't trigger on 1st intuition use
					if (cls.equals(StoneOfIntuition.class) && hero.buff(StoneOfIntuition.IntuitionUseTracker.class) != null){
						return null;
					}
					// 10/15%
					if (Random.Int(20) < 1 + hero.pointsInTalent(RECALL_INSCRIPTION)){
						Reflection.newInstance(cls).collect();
						GLog.p("refunded!");
					}
				}
			}
			return null;
		});
		
		// 注册神器事件监听器
		EventBus.on("talent.artifact.used", (Object args) -> {
			if (!(args instanceof EventBus.EventData)) return null;
			EventBus.EventData e = (EventBus.EventData) args;
			Hero hero = e.get("hero");
			
			if (hero.hasTalent(ENHANCED_RINGS)){
				Buff.prolong(hero, EnhancedRings.class, 3f*hero.pointsInTalent(ENHANCED_RINGS));
			}

			if (Dungeon.hero.heroClass != HeroClassSheet.CLERIC
					&& Dungeon.hero.hasTalent(Talent.DIVINE_SENSE)){
				Buff.prolong(Dungeon.hero, DivineSense.DivineSenseTracker.class, Dungeon.hero.cooldown()+1);
			}

			// 10/20/30%
			if (Dungeon.hero.heroClass != HeroClassSheet.CLERIC
					&& Dungeon.hero.hasTalent(Talent.CLEANSE)
					&& Random.Int(10) < Dungeon.hero.pointsInTalent(Talent.CLEANSE)){
				boolean removed = false;
				for (Buff b : Dungeon.hero.buffs()) {
					if (b.type == Buff.buffType.NEGATIVE) {
						b.detach();
						removed = true;
					}
				}
				if (removed && Dungeon.hero.sprite != null) {
					new Flare( 6, 32 ).color(0xFF4CD2, true).show( Dungeon.hero.sprite, 2f );
				}
			}
			return null;
		});
		
		// 注册装备事件监听器
		EventBus.on("talent.item.equipped", (Object args) -> {
			if (!(args instanceof EventBus.EventData)) return null;
			EventBus.EventData e = (EventBus.EventData) args;
			Hero hero = e.get("hero");
			Item item = e.get("item");
			
			boolean identify = false;
			if (hero.pointsInTalent(VETERANS_INTUITION) == 2 && item instanceof Armor){
				identify = true;
			}
			if (hero.hasTalent(THIEFS_INTUITION) && item instanceof Ring){
				if (hero.pointsInTalent(THIEFS_INTUITION) == 2){
					identify = true;
				}
				((Ring) item).setKnown();
			}
			if (hero.pointsInTalent(ADVENTURERS_INTUITION) == 2 && item instanceof Weapon){
				identify = true;
			}

			if (identify && !ShardOfOblivion.passiveIDDisabled()){
				item.identify();
			}
			return null;
		});
		
		// 注册收集事件监听器
		EventBus.on("talent.item.collected", (Object args) -> {
			if (!(args instanceof EventBus.EventData)) return null;
			EventBus.EventData e = (EventBus.EventData) args;
			Hero hero = e.get("hero");
			Item item = e.get("item");
			
			if (hero.pointsInTalent(THIEFS_INTUITION) == 2){
				if (item instanceof Ring) ((Ring) item).setKnown();
			}
			return null;
		});
		
		// 注册攻击事件监听器
		EventBus.on("talent.attack.proc", (Object args) -> {
			if (!(args instanceof EventBus.EventData)) return null;
			EventBus.EventData e = (EventBus.EventData) args;
			Hero hero = e.get("hero");
			Char enemy = e.get("enemy");
			int dmg = e.get("dmg");

			if (hero.hasTalent(Talent.PROVOKED_ANGER)
				&& hero.buff(ProvokedAngerTracker.class) != null){
				dmg += 1 + hero.pointsInTalent(Talent.PROVOKED_ANGER);
				hero.buff(ProvokedAngerTracker.class).detach();
			}

			if (hero.hasTalent(Talent.LINGERING_MAGIC)
					&& hero.buff(LingeringMagicTracker.class) != null){
				dmg += Random.IntRange(hero.pointsInTalent(Talent.LINGERING_MAGIC) , 2);
				hero.buff(LingeringMagicTracker.class).detach();
			}

			if (hero.hasTalent(Talent.SUCKER_PUNCH)
					&& enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
					&& enemy.buff(SuckerPunchTracker.class) == null){
				dmg += Random.IntRange(hero.pointsInTalent(Talent.SUCKER_PUNCH) , 2);
				Buff.affect(enemy, SuckerPunchTracker.class);
			}

			if (hero.hasTalent(Talent.FOLLOWUP_STRIKE) && enemy.isAlive() && enemy.alignment == Char.Alignment.ENEMY) {
				if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
					Buff.prolong(hero, FollowupStrikeTracker.class, 5f).object = enemy.id();
				} else if (hero.buff(FollowupStrikeTracker.class) != null
						&& hero.buff(FollowupStrikeTracker.class).object == enemy.id()){
					dmg += 1 + hero.pointsInTalent(FOLLOWUP_STRIKE);
					hero.buff(FollowupStrikeTracker.class).detach();
				}
			}

			if (hero.buff(Talent.SpiritBladesTracker.class) != null
					&& Random.Int(10) < 3*hero.pointsInTalent(Talent.SPIRIT_BLADES)){
				SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
				if (bow != null) dmg = bow.proc( hero, enemy, dmg );
				hero.buff(Talent.SpiritBladesTracker.class).detach();
			}

			if (hero.hasTalent(PATIENT_STRIKE)){
				if (hero.buff(PatientStrikeTracker.class) != null
						&& !(hero.belongings.attackingWeapon() instanceof MissileWeapon)){
					hero.buff(PatientStrikeTracker.class).detach();
					dmg += Random.IntRange(hero.pointsInTalent(Talent.PATIENT_STRIKE), 2);
				}
			}

			if (hero.hasTalent(DEADLY_FOLLOWUP) && enemy.alignment == Char.Alignment.ENEMY) {
				if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
					if (!(hero.belongings.attackingWeapon() instanceof SpiritBow.SpiritArrow)) {
						Buff.prolong(hero, DeadlyFollowupTracker.class, 5f).object = enemy.id();
					}
				} else if (hero.buff(DeadlyFollowupTracker.class) != null
						&& hero.buff(DeadlyFollowupTracker.class).object == enemy.id()){
					dmg = Math.round(dmg * (1.0f + .1f*hero.pointsInTalent(DEADLY_FOLLOWUP)));
				}
			}
			
			// 更新伤害值
			e.put("dmg", dmg);
			return null;
		});
	}
	public static void onTalentUpgraded( Hero hero, Talent talent ){
		EventBus.fire("upgrade_talent", 
			"hero", hero,
			"talent", talent
		);
	}

	public static class CachedRationsDropped extends CounterBuff{{revivePersists = true;}};
	public static class NatureBerriesDropped extends CounterBuff{{revivePersists = true;}};

	public static void onFoodEaten( Hero hero, float foodVal, Item foodSource ){
		EventBus.fire("talent.food.eaten",
			"hero", hero,
			"foodVal", foodVal,
			"foodSource", foodSource
		);
	}

	public static class WarriorFoodImmunity extends FlavourBuff{
		{ actPriority = HERO_PRIO+1; }
	}

	public static float itemIDSpeedFactor( Hero hero, Item item ){
		// 1.75x/2.5x speed with Huntress talent
		float factor = 1f + 0.75f*hero.pointsInTalent(SURVIVALISTS_INTUITION);

		// Affected by both Warrior(1.75x/2.5x) and Duelist(2.5x/inst.) talents
		if (item instanceof MeleeWeapon){
			factor *= 1f + 1.5f*hero.pointsInTalent(ADVENTURERS_INTUITION); //instant at +2 (see onItemEquipped)
			factor *= 1f + 0.75f*hero.pointsInTalent(VETERANS_INTUITION);
		}
		// Affected by both Warrior(2.5x/inst.) and Duelist(1.75x/2.5x) talents
		if (item instanceof Armor){
			factor *= 1f + 0.75f*hero.pointsInTalent(ADVENTURERS_INTUITION);
			factor *= 1f + hero.pointsInTalent(VETERANS_INTUITION); //instant at +2 (see onItemEquipped)
		}
		// 3x/instant for Mage (see Wand.wandUsed())
		if (item instanceof Wand){
			factor *= 1f + 2.0f*hero.pointsInTalent(SCHOLARS_INTUITION);
		}
		// 2x/instant for Rogue (see onItemEqupped), also id's type on equip/on pickup
		if (item instanceof Ring){
			factor *= 1f + hero.pointsInTalent(THIEFS_INTUITION);
		}
		return factor;
	}

	public static void onPotionUsed( Hero hero, int cell, float factor ){
		EventBus.fire("talent.potion.used",
			"hero", hero,
			"cell", cell,
			"factor", factor
		);
	}

	public static void onScrollUsed( Hero hero, int pos, float factor, Class<?extends Item> cls ){
		EventBus.fire("talent.scroll.used",
			"hero", hero,
			"pos", pos,
			"factor", factor,
			"cls", cls
		);
	}

	public static void onRunestoneUsed( Hero hero, int pos, Class<?extends Item> cls ){
		EventBus.fire("talent.runestone.used",
			"hero", hero,
			"pos", pos,
			"cls", cls
		);
	}

	public static void onArtifactUsed( Hero hero ){
		EventBus.fire("talent.artifact.used",
			"hero", hero
		);
	}

	public static void onItemEquipped( Hero hero, Item item ){
		EventBus.fire("talent.item.equipped",
			"hero", hero,
			"item", item
		);
	}

	public static void onItemCollected( Hero hero, Item item ){
		EventBus.fire("talent.item.collected",
			"hero", hero,
			"item", item
		);
	}

	public static int onAttackProc( Hero hero, Char enemy, int dmg ){
		// 创建一个包装器来处理返回值
		EventBus.EventData eventData = new EventBus.EventData()
			.put("hero", hero)
			.put("enemy", enemy)
			.put("dmg", dmg);
		
		EventBus.fire("talent.attack.proc", eventData);
		
		// 返回可能被修改的伤害值
		return eventData.or("dmg", dmg);
	}

	public static class ProvokedAngerTracker extends FlavourBuff{
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.WEAPON; }
		public void tintIcon(Image icon) { icon.hardlight(1.43f, 1.43f, 1.43f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	}
	public static class LingeringMagicTracker extends FlavourBuff{
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.WEAPON; }
		public void tintIcon(Image icon) { icon.hardlight(1.43f, 1.43f, 0f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	}
	public static class SuckerPunchTracker extends Buff{};
	public static class FollowupStrikeTracker extends FlavourBuff{
		public int object;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0f, 0.75f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
		private static final String OBJECT    = "object";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(OBJECT, object);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			object = bundle.getInt(OBJECT);
		}
	};

	public static final int MAX_TALENT_TIERS = 4;

	public static void initClassTalents( Hero hero ){
		initClassTalents( hero.heroClass, hero.talents, hero.metamorphedTalents );
	}

	public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents){
		initClassTalents( cls, talents, new LinkedHashMap<>());
	}

	public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents, LinkedHashMap<Talent, Talent> replacements ){
		// 清空现有天赋
		talents.clear();
		
		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}
		ArrayList<Talent> tierTalents = new ArrayList<>();
		//tier 1
			   if (cls == HeroClassSheet.WARRIOR) {
				Collections.addAll(tierTalents, HEARTY_MEAL, VETERANS_INTUITION, PROVOKED_ANGER, IRON_WILL);
		} else if (cls == HeroClassSheet.MAGE) {
				Collections.addAll(tierTalents, EMPOWERING_MEAL, SCHOLARS_INTUITION, LINGERING_MAGIC, BACKUP_BARRIER);
		} else if (cls == HeroClassSheet.ROGUE) {
				Collections.addAll(tierTalents, CACHED_RATIONS, THIEFS_INTUITION, SUCKER_PUNCH, PROTECTIVE_SHADOWS);
		} else if (cls == HeroClassSheet.HUNTRESS) {
				Collections.addAll(tierTalents, NATURES_BOUNTY, SURVIVALISTS_INTUITION, FOLLOWUP_STRIKE, NATURES_AID);
		} else if (cls == HeroClassSheet.DUELIST) {
				Collections.addAll(tierTalents, STRENGTHENING_MEAL, ADVENTURERS_INTUITION, PATIENT_STRIKE, AGGRESSIVE_BARRIER);
		} else if (cls == HeroClassSheet.CLERIC) {
				Collections.addAll(tierTalents, SATIATED_SPELLS, HOLY_INTUITION, SEARING_LIGHT, SHIELD_OF_LIGHT);
		} else if(
			cls!=null&&
			cls.classTalentsTier1!=null
			&&!cls.classTalentsTier1.isEmpty()){
			Collections.addAll(tierTalents, cls.classTalentsTier1.toArray(new Talent[0]));
		} else {
			// Default to warrior
			Collections.addAll(tierTalents, HEARTY_MEAL, VETERANS_INTUITION, PROVOKED_ANGER, IRON_WILL);
		}

		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(0).put(talent, 0);
		}
		tierTalents.clear();

		//tier 2
		if (cls == HeroClassSheet.WARRIOR) {
				Collections.addAll(tierTalents, IRON_STOMACH, LIQUID_WILLPOWER, RUNIC_TRANSFERENCE, LETHAL_MOMENTUM, IMPROVISED_PROJECTILES);
		} else if (cls == HeroClassSheet.MAGE) {
				Collections.addAll(tierTalents, ENERGIZING_MEAL, INSCRIBED_POWER, WAND_PRESERVATION, ARCANE_VISION, SHIELD_BATTERY);
		} else if (cls == HeroClassSheet.ROGUE) {
				Collections.addAll(tierTalents, MYSTICAL_MEAL, INSCRIBED_STEALTH, WIDE_SEARCH, SILENT_STEPS, ROGUES_FORESIGHT);
		} else if (cls == HeroClassSheet.HUNTRESS) {
				Collections.addAll(tierTalents, INVIGORATING_MEAL, LIQUID_NATURE, REJUVENATING_STEPS, HEIGHTENED_SENSES, DURABLE_PROJECTILES);
		} else if (cls == HeroClassSheet.DUELIST) {
				Collections.addAll(tierTalents, FOCUSED_MEAL, LIQUID_AGILITY, WEAPON_RECHARGING, LETHAL_HASTE, SWIFT_EQUIP);
		} else if (cls == HeroClassSheet.CLERIC) {
				Collections.addAll(tierTalents, ENLIGHTENING_MEAL, RECALL_INSCRIPTION, SUNRAY, DIVINE_SENSE, BLESS);
		} else if(cls!=null&&cls.classTalentsTier2!=null&&!cls.classTalentsTier2.isEmpty()){
			Collections.addAll(tierTalents, cls.classTalentsTier2.toArray(new Talent[0]));
		} else {
			// Default to warrior
			Collections.addAll(tierTalents, IRON_STOMACH, LIQUID_WILLPOWER, RUNIC_TRANSFERENCE, LETHAL_MOMENTUM, IMPROVISED_PROJECTILES);
		}

		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(1).put(talent, 0);
		}
		tierTalents.clear();

		//tier 3
		//??where
		if (cls == HeroClassSheet.WARRIOR) {
				Collections.addAll(tierTalents, HOLD_FAST, STRONGMAN);
		} else if (cls == HeroClassSheet.MAGE) {
				Collections.addAll(tierTalents, DESPERATE_POWER, ALLY_WARP);
		} else if (cls == HeroClassSheet.ROGUE) {
				Collections.addAll(tierTalents, ENHANCED_RINGS, LIGHT_CLOAK);
		} else if (cls == HeroClassSheet.HUNTRESS) {
				Collections.addAll(tierTalents, POINT_BLANK, SEER_SHOT);
		} else if (cls == HeroClassSheet.DUELIST) {
				Collections.addAll(tierTalents, PRECISE_ASSAULT, DEADLY_FOLLOWUP);
		} else if (cls == HeroClassSheet.CLERIC) {
				Collections.addAll(tierTalents, CLEANSE, LIGHT_READING);
		} else {
			// Default to warrior
			Collections.addAll(tierTalents, HOLD_FAST, STRONGMAN);
		}

		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(2).put(talent, 0);
		}
		tierTalents.clear();

		//tier4
		//TBD
		
	}

	public static void initSubclassTalents( Hero hero ){
		initSubclassTalents( hero.subClass, hero.talents );
	}

	public static void initSubclassTalents( HeroSubClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		if (cls == HeroSubClass.NONE) return;

		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		ArrayList<Talent> tierTalents = new ArrayList<>();

		//tier 3
		if (cls == HeroSubClass.BERSERKER) {
				Collections.addAll(tierTalents, ENDLESS_RAGE, DEATHLESS_FURY, ENRAGED_CATALYST);
		} else if (cls == HeroSubClass.GLADIATOR) {
				Collections.addAll(tierTalents, CLEAVE, LETHAL_DEFENSE, ENHANCED_COMBO);
		} else if (cls == HeroSubClass.BATTLEMAGE) {
				Collections.addAll(tierTalents, EMPOWERED_STRIKE, MYSTICAL_CHARGE, EXCESS_CHARGE);
		} else if (cls == HeroSubClass.WARLOCK) {
				Collections.addAll(tierTalents, SOUL_EATER, SOUL_SIPHON, NECROMANCERS_MINIONS);
		} else if (cls == HeroSubClass.ASSASSIN) {
				Collections.addAll(tierTalents, ENHANCED_LETHALITY, ASSASSINS_REACH, BOUNTY_HUNTER);
		} else if (cls == HeroSubClass.FREERUNNER) {
				Collections.addAll(tierTalents, EVASIVE_ARMOR, PROJECTILE_MOMENTUM, SPEEDY_STEALTH);
		} else if (cls == HeroSubClass.SNIPER) {
				Collections.addAll(tierTalents, FARSIGHT, SHARED_ENCHANTMENT, SHARED_UPGRADES);
		} else if (cls == HeroSubClass.WARDEN) {
				Collections.addAll(tierTalents, DURABLE_TIPS, BARKSKIN, SHIELDING_DEW);
		} else if (cls == HeroSubClass.CHAMPION) {
				Collections.addAll(tierTalents, VARIED_CHARGE, TWIN_UPGRADES, COMBINED_LETHALITY);
		} else if (cls == HeroSubClass.MONK) {
				Collections.addAll(tierTalents, UNENCUMBERED_SPIRIT, MONASTIC_VIGOR, COMBINED_ENERGY);
		} else if (cls == HeroSubClass.PRIEST) {
				Collections.addAll(tierTalents, HOLY_LANCE, HALLOWED_GROUND, MNEMONIC_PRAYER);
		} else if (cls == HeroSubClass.PALADIN) {
				Collections.addAll(tierTalents, LAY_ON_HANDS, AURA_OF_PROTECTION, WALL_OF_LIGHT);
		} else {
			// Default to berserker
			Collections.addAll(tierTalents, ENDLESS_RAGE, DEATHLESS_FURY, ENRAGED_CATALYST);
		}
		
		for (Talent talent : tierTalents){
			talents.get(2).put(talent, 0);
		}
		tierTalents.clear();

	}

	public static void initArmorTalents( Hero hero ){
		initArmorTalents( hero.armorAbility, hero.talents);
	}

	public static void initArmorTalents(ArmorAbility abil, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		if (abil == null) return;

		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		for (Talent t : abil.talents()){
			talents.get(3).put(t, 0);
		}
	}

	private static final String TALENT_TIER = "talents_tier_";

	public static void storeTalentsInBundle( Bundle bundle, Hero hero ){
		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = new Bundle();

			for (Talent talent : tier.keySet()){
				if (tier.get(talent) > 0){
					tierBundle.put(talent.name(), tier.get(talent));
				}
				if (tierBundle.contains(talent.name())){
					tier.put(talent, Math.min(tierBundle.getInt(talent.name()), talent.maxPoints()));
				}
			}
			bundle.put(TALENT_TIER+(i+1), tierBundle);
		}

		Bundle replacementsBundle = new Bundle();
		for (Talent t : hero.metamorphedTalents.keySet()){
			replacementsBundle.put(t.name(), hero.metamorphedTalents.get(t));
		}
		bundle.put("replacements", replacementsBundle);
	}

	private static final HashSet<String> removedTalents = new HashSet<>();
	static{
		//v2.4.0
		removedTalents.add("TEST_SUBJECT");
		removedTalents.add("TESTED_HYPOTHESIS");
	}

	private static final HashMap<String, String> renamedTalents = new HashMap<>();
	static{
		//v2.4.0
		renamedTalents.put("SECONDARY_CHARGE",          "VARIED_CHARGE");
	}

	public static void restoreTalentsFromBundle( Bundle bundle, Hero hero ){
		if (bundle.contains("replacements")){
			Bundle replacements = bundle.getBundle("replacements");
			for (String key : replacements.getKeys()){
				String value = replacements.getString(key);
				if (renamedTalents.containsKey(key)) key = renamedTalents.get(key);
				if (renamedTalents.containsKey(value)) value = renamedTalents.get(value);
				if (!removedTalents.contains(key) && !removedTalents.contains(value)){
					try {
						hero.metamorphedTalents.put(Talent.valueOf(key), Talent.valueOf(value));
					} catch (Exception e) {
						ColaDungeon.reportException(e);
					}
				}
			}
		}

		if (hero.heroClass != null)     initClassTalents(hero);
		if (hero.subClass != null)      initSubclassTalents(hero);
		if (hero.armorAbility != null)  initArmorTalents(hero);

		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = bundle.contains(TALENT_TIER+(i+1)) ? bundle.getBundle(TALENT_TIER+(i+1)) : null;

			if (tierBundle != null){
				for (String tName : tierBundle.getKeys()){
					int points = tierBundle.getInt(tName);
					if (renamedTalents.containsKey(tName)) tName = renamedTalents.get(tName);
					if (!removedTalents.contains(tName)) {
						try {
							Talent talent = Talent.valueOf(tName);
							if (tier.containsKey(talent)) {
								tier.put(talent, Math.min(points, talent.maxPoints()));
							}
						} catch (Exception e) {
							ColaDungeon.reportException(e);
						}
					}
				}
			}
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put("name", name);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		// 由于Talent是不可变的，这个方法实际上不会被调用
	}

}
