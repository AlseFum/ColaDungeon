package com.coladungeon.actors.hero;

import com.coladungeon.Dungeon;
import com.coladungeon.items.weapon.melee.MagesStaff;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.ui.HeroIcon;
import com.watabou.noosa.Game;

public enum HeroSubClass {

	NONE(HeroIcon.NONE),

	BERSERKER(HeroIcon.BERSERKER),
	GLADIATOR(HeroIcon.GLADIATOR),

	BATTLEMAGE(HeroIcon.BATTLEMAGE),
	WARLOCK(HeroIcon.WARLOCK),
	
	ASSASSIN(HeroIcon.ASSASSIN),
	FREERUNNER(HeroIcon.FREERUNNER),
	
	SNIPER(HeroIcon.SNIPER),
	WARDEN(HeroIcon.WARDEN),

	CHAMPION(HeroIcon.CHAMPION),
	MONK(HeroIcon.MONK),

	PRIEST(HeroIcon.PRIEST),
	PALADIN(HeroIcon.PALADIN);

	int icon;

	HeroSubClass(int icon){
		this.icon = icon;
	}
	
	public String title() {
		switch(this) {
			case NONE: return "无";
			case BERSERKER: return "狂战士";
			case GLADIATOR: return "角斗士";
			case BATTLEMAGE: return "战斗法师";
			case WARLOCK: return "术士";
			case ASSASSIN: return "刺客";
			case FREERUNNER: return "自由奔跑者";
			case SNIPER: return "狙击手";
			case WARDEN: return "守望者";
			case CHAMPION: return "勇士";
			case MONK: return "武僧";
			case PRIEST: return "祭司";
			case PALADIN: return "圣骑士";
			default: return name();
		}
	}

	public String shortDesc() {
		switch(this) {
			case NONE: return "无特殊能力";
			case BERSERKER: return "受伤时进入狂暴状态，提高攻击力";
			case GLADIATOR: return "连击可以触发特殊效果";
			case BATTLEMAGE: return "近战攻击可以触发法杖效果";
			case WARLOCK: return "攻击敌人时可以吸取生命值";
			case ASSASSIN: return "从隐身状态攻击造成额外伤害";
			case FREERUNNER: return "移动速度更快，闪避率提高";
			case SNIPER: return "远程攻击更准确，造成更多伤害";
			case WARDEN: return "获得草药强化效果，可以在草丛中隐身";
			case CHAMPION: return "可以挑战敌人进行决斗";
			case MONK: return "空手战斗获得额外效果";
			case PRIEST: return "可以使用治疗和增益法术";
			case PALADIN: return "近战能力和神圣魔法的结合";
			default: return name() + "_short_desc";
		}
	}

	public String desc() {
		//Include the staff effect description in the battlemage's desc if possible
		if (this == BATTLEMAGE){
			String desc = Messages.get(this, name() + "_desc");
			if (Game.scene() instanceof GameScene){
				MagesStaff staff = Dungeon.hero.belongings.getItem(MagesStaff.class);
				if (staff != null && staff.wandClass() != null){
					desc += "\n\n" + Messages.get(staff.wandClass(), "bmage_desc");
					desc = desc.replaceAll("_", "");
				}
			}
			return desc;
		} else {
			return Messages.get(this, name() + "_desc");
		}
	}

	public int icon(){
		return icon;
	}

}
