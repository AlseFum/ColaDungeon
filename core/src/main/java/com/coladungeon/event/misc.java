package com.coladungeon.event;

//here, is some atomic events need to be dispatched and triggered
import com.coladungeon.Assets;
import com.coladungeon.Badges;
import com.coladungeon.CDSettings;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.particles.BlastParticle;
import com.coladungeon.effects.particles.SmokeParticle;
import com.coladungeon.items.Heap;
import com.coladungeon.items.Item;
import com.coladungeon.items.Recipe;
import com.coladungeon.items.potions.PotionOfFrost;
import com.coladungeon.items.potions.PotionOfHealing;
import com.coladungeon.items.potions.PotionOfInvisibility;
import com.coladungeon.items.potions.PotionOfLiquidFlame;
import com.coladungeon.items.quest.GooBlob;
import com.coladungeon.items.quest.MetalShard;
import com.coladungeon.items.scrolls.ScrollOfMirrorImage;
import com.coladungeon.items.scrolls.ScrollOfRage;
import com.coladungeon.items.scrolls.ScrollOfRecharging;
import com.coladungeon.items.scrolls.ScrollOfRemoveCurse;
import com.coladungeon.journal.Catalog;
import com.coladungeon.messages.Languages;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.CharSprite;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import com.coladungeon.items.bombs.Bomb.ConjuredBomb;
import java.util.ArrayList;

public class misc{
    public static void explode(int cell,boolean isDestructive,int explosionRange,Object source){
		//We're blowing up, so no need for a fuse anymore.
		//

		Sample.INSTANCE.play( Assets.Sounds.BLAST );

		if (isDestructive) {

			ArrayList<Integer> affectedCells = new ArrayList<>();
			ArrayList<Char> affectedChars = new ArrayList<>();
			
			if (Dungeon.level.heroFOV[cell]) {
				CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);
			}
			
			boolean terrainAffected = false;
			boolean[] explodable = new boolean[Dungeon.level.length()];
			BArray.not( Dungeon.level.solid, explodable);
			BArray.or( Dungeon.level.flamable, explodable, explodable);
			PathFinder.buildDistanceMap( cell, explodable, explosionRange );
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] != Integer.MAX_VALUE) {
					affectedCells.add(i);
					Char ch = Actor.findChar(i);
					if (ch != null) {
						affectedChars.add(ch);
					}
				}
			}

			for (int i : affectedCells){
				if (Dungeon.level.heroFOV[i]) {
					CellEmitter.get(i).burst(SmokeParticle.FACTORY, 4);
				}

				if (Dungeon.level.flamable[i]) {
					Dungeon.level.destroy(i);
					GameScene.updateMap(i);
					terrainAffected = true;
				}

				//destroys items / triggers bombs caught in the blast.
				Heap heap = Dungeon.level.heaps.get(i);
				if (heap != null) {
					heap.explode();
				}
			}
			
			for (Char ch : affectedChars){

				//if they have already been killed by another bomb
				if(!ch.isAlive()){
					continue;
				}

				int dmg = Random.NormalIntRange(4 + Dungeon.scalingDepth(), 12 + 3*Dungeon.scalingDepth());
				dmg -= ch.drRoll();

				if (dmg > 0) {
					ch.damage(dmg, source);
				}
				
				if (ch == Dungeon.hero && !ch.isAlive()) {
					if (source instanceof ConjuredBomb){
						Badges.validateDeathFromFriendlyMagic();
					}
					GLog.n(Messages.get(source, "ondeath"));
					Dungeon.fail(source);
				}
			}
			
			if (terrainAffected) {
				Dungeon.observe();
			}
		}
	}
}