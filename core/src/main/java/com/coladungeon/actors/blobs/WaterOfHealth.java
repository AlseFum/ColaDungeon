package com.coladungeon.actors.blobs;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Healing;
import com.coladungeon.actors.buffs.Hunger;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.BlobEmitter;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.FloatingText;
import com.coladungeon.effects.Speck;
import com.coladungeon.effects.particles.ShadowParticle;
import com.coladungeon.effects.particles.ShaftParticle;
import com.coladungeon.items.Ankh;
import com.coladungeon.items.Item;
import com.coladungeon.items.Waterskin;
import com.coladungeon.items.potions.PotionOfHealing;
import com.coladungeon.items.scrolls.ScrollOfRemoveCurse;
import com.coladungeon.items.trinkets.VialOfBlood;
import com.coladungeon.journal.Notes.Landmark;
import com.coladungeon.messages.Messages;
import com.coladungeon.sprites.CharSprite;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class WaterOfHealth extends WellWater {
	
	@Override
	protected boolean affectHero( Hero hero ) {
		
		if (!hero.isAlive()) return false;
		
		Sample.INSTANCE.play( Assets.Sounds.DRINK );

		PotionOfHealing.cure( hero );
		hero.belongings.uncurseEquipped();
		hero.buff( Hunger.class ).satisfy( Hunger.STARVING );

		if (VialOfBlood.delayBurstHealing()){
			Healing healing = Buff.affect(hero, Healing.class);
			healing.setHeal(hero.HT, 0, VialOfBlood.maxHealPerTurn());
			healing.applyVialEffect();
		} else {
			hero.HP = hero.HT;
			hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 4);
			hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(hero.HT), FloatingText.HEALING);
		}
		
		CellEmitter.get( hero.pos ).start( ShaftParticle.FACTORY, 0.2f, 3 );

		Dungeon.hero.interrupt();
	
		GLog.p( Messages.get(this, "procced") );
		
		return true;
	}
	
	@Override
	protected Item affectItem( Item item, int pos ) {
		if (item instanceof Waterskin && !((Waterskin)item).isFull()) {
			((Waterskin)item).fill();
			CellEmitter.get( pos ).start( Speck.factory( Speck.HEALING ), 0.4f, 4 );
			Sample.INSTANCE.play( Assets.Sounds.DRINK );
			return item;
		} else if ( item instanceof Ankh && !(((Ankh) item).isBlessed())){
			((Ankh) item).bless();
			CellEmitter.get( pos ).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
			Sample.INSTANCE.play( Assets.Sounds.DRINK );
			return item;
		} else if (ScrollOfRemoveCurse.uncursable(item)) {
			if (ScrollOfRemoveCurse.uncurse( null, item )){
				CellEmitter.get( pos ).start( ShadowParticle.UP, 0.05f, 10 );
			}
			Sample.INSTANCE.play( Assets.Sounds.DRINK );
			return item;
		}
		return null;
	}
	
	@Override
	public Landmark landmark() {
		return Landmark.WELL_OF_HEALTH;
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.start( Speck.factory( Speck.HEALING ), 0.5f, 0 );
	}
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}
