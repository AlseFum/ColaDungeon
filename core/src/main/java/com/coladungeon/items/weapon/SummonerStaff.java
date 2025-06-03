package com.coladungeon.items.weapon;

import com.coladungeon.actors.Actor;
import com.coladungeon.actors.Char;
import com.coladungeon.actors.buffs.Buff;
import com.coladungeon.actors.buffs.Invisibility;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.actors.mobs.Elemental;
import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.effects.particles.ElmoParticle;
import com.coladungeon.items.Item;
import com.coladungeon.items.scrolls.ScrollOfTeleportation;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.coladungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import com.coladungeon.Dungeon;
import com.coladungeon.scenes.CellSelector;
import com.coladungeon.Assets;
import com.coladungeon.actors.Char.Alignment;
import com.coladungeon.actors.mobs.Elemental.FrostElemental;
import java.util.ArrayList;

public class SummonerStaff extends Weapon {

    public static final String AC_SUMMON = "SUMMON";
    
    {
        image = ItemSpriteSheet.MAGES_STAFF;
        
        defaultAction = AC_SUMMON;
        usesTargeting = true;
        
        unique = true;
        bones = false;
    }
    
    public static Class<? extends Elemental> summonClass = FrostAllyElemental.class;
    
    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SUMMON);
        return actions;
    }
    
    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        
        if (action.equals(AC_SUMMON)) {
            curUser = hero;
            curItem = this;
            GameScene.selectCell(summoner);
        }
    }
    
    private static CellSelector.Listener summoner = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null) {
                if (target == curUser.pos) {
                    GLog.w(Messages.get(SummonerStaff.class, "self_target"));
                    return;
                }
                
                ArrayList<Integer> spawnPoints = new ArrayList<>();
                
                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                    int p = target + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
                        spawnPoints.add(p);
                    }
                }
                
                if (!spawnPoints.isEmpty()) {
                    Elemental elemental = Reflection.newInstance(summonClass);
                    GameScene.add(elemental);
                    elemental.setSummonedALly();
                    ScrollOfTeleportation.appear(elemental, Random.element(spawnPoints));
                    
                    curUser.sprite.operate(curUser.pos);
                    curUser.spendAndNext(Actor.TICK);
                    
                    Sample.INSTANCE.play(Assets.Sounds.CHARMS);
                    curUser.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
                    
                } else {
                    GLog.w(Messages.get(SummonerStaff.class, "no_space"));
                }
            }
        }
        
        @Override
        public String prompt() {
            return Messages.get(SummonerStaff.class, "prompt");
        }
    };
    

    public static class FrostAllyElemental extends FrostElemental{
        {
            alignment = Char.Alignment.ALLY;
        }
        @Override
        public boolean act() {
            super.act();
            if(HP<=0){
                die(null);
            }
            return true;
        }
        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            HP = -1;
        }
    }
    @Override
    public int min(int lvl) {
        return 1;
    }
    
    @Override
    public int max(int lvl) {
        return 4 + lvl;
    }
    
    @Override
    public int STRReq(int lvl) {
        return 12;
    }
    
    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }
    
    private static final String SUMMON_CLASS = "summon_class";
    
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SUMMON_CLASS, summonClass);
    }
    
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        summonClass = bundle.getClass(SUMMON_CLASS);
    }
} 