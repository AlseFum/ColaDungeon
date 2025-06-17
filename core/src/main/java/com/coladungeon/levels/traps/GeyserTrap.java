package com.coladungeon.levels.traps;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.blobs.Fire;
import com.coladungeon.actors.buffs.Burning;
import com.coladungeon.effects.Splash;
import com.coladungeon.items.wands.WandOfBlastWave;
import com.coladungeon.mechanics.Ballistica;
import com.coladungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class GeyserTrap extends Trap {

	{
		color = TEAL;
		shape = DIAMOND;
	}

	public int centerKnockBackDirection = -1;
	public Object source = this;

	@Override
	public void activate() {
		Splash.at( DungeonTilemap.tileCenterToWorld( pos ), -PointF.PI/2, PointF.PI/2, 0x5bc1e3, 100, 0.01f);
		Sample.INSTANCE.play(Assets.Sounds.GAS, 1f, 0.75f);

		Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
		PathFinder.buildDistanceMap( pos, BArray.not( Dungeon.level.solid, null ), 2 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] == 2 && Random.Int(3) > 0){
				Dungeon.level.setCellToWater(true, i);
				if (fire != null){
					fire.clear(i);
				}
			} else if (PathFinder.distance[i] < 2){
				Dungeon.level.setCellToWater(true, i);
				if (fire != null){
					fire.clear(i);
				}
			}
		}

		for (int i : PathFinder.NEIGHBOURS8){
			Char ch = Actor.findChar(pos + i);
			if (ch != null){

				//does the equivalent of a bomb's damage against fiery enemies.
				if (Char.hasProp(ch, Char.Property.FIERY)){
					int dmg = Random.NormalIntRange(5 + scalingDepth(), 10 + scalingDepth()*2);
					dmg *= 0.67f;
					if (!ch.isImmune(GeyserTrap.class)){
						ch.damage(dmg, this);
					}
				}

				if (ch.isAlive()) {
					if (ch.buff(Burning.class) != null){
						ch.buff(Burning.class).detach();
					}

					//trace a ballistica to our target (which will also extend past them)
					Ballistica trajectory = new Ballistica(pos, ch.pos, Ballistica.STOP_TARGET);
					//trim it to just be the part that goes past them
					trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
					//knock them back along that ballistica
					WandOfBlastWave.throwChar(ch, trajectory, 2, true, true, source);
				}
			}
		}

		Char ch = Actor.findChar(pos);
		if (ch != null){
			int targetpos = -1;
			if (centerKnockBackDirection != -1){
				targetpos = centerKnockBackDirection;
			} else if (ch == Dungeon.hero){
				//if it is the hero, random direction that isn't into a hazard
				ArrayList<Integer> candidates = new ArrayList<>();
				for (int i : PathFinder.NEIGHBOURS8){
					//add as a candidate if both cells on the trajectory are safe
					if (!Dungeon.level.avoid[pos + i] && !Dungeon.level.avoid[pos + i + i]){
						candidates.add(pos + i);
					}
				}
				if (!candidates.isEmpty()){
					targetpos = Random.element(candidates);
				}
			} else {
				//random direction if it isn't the hero
				targetpos = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
			}

			//does the equivalent of a bomb's damage against fiery enemies.
			if (Char.hasProp(ch, Char.Property.FIERY)){
				int dmg = Random.NormalIntRange(5 + scalingDepth(), 10 + scalingDepth()*2);
				if (!ch.isImmune(GeyserTrap.class)){
					ch.damage(dmg, this);
				}
			}

			if (ch.isAlive() && targetpos != -1){
				if (ch.buff(Burning.class) != null){
					ch.buff(Burning.class).detach();
				}
				//trace a ballistica in the direction of our target
				Ballistica trajectory = new Ballistica(pos, targetpos, Ballistica.MAGIC_BOLT);
				//knock them back along that ballistica
				WandOfBlastWave.throwChar(ch, trajectory, 2, true, true, source);
			}
		}
	}
}
