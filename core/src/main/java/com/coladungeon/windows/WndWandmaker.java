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

package com.coladungeon.windows;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.mobs.npcs.Wandmaker;
import com.coladungeon.items.Item;
import com.coladungeon.items.quest.CorpseDust;
import com.coladungeon.items.quest.Embers;
import com.coladungeon.messages.Messages;
import com.coladungeon.plants.Rotberry;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.scenes.PixelScene;
import com.coladungeon.sprites.ItemSprite;
import com.coladungeon.ui.ItemButton;
import com.coladungeon.ui.RedButton;
import com.coladungeon.ui.RenderedTextBlock;
import com.coladungeon.ui.Window;
import com.coladungeon.utils.GLog;

public class WndWandmaker extends Window {

	private static final int WIDTH		= 120;
	private static final int BTN_SIZE	= 32;
	private static final int BTN_GAP	= 5;
	private static final int GAP		= 2;

	Wandmaker wandmaker;
	Item questItem;

	public WndWandmaker( final Wandmaker wandmaker, final Item item ) {
		
		super();

		this.wandmaker = wandmaker;
		this.questItem = item;
		
		IconTitle titlebar = new IconTitle();
		titlebar.icon(new ItemSprite(item.image(), null));
		titlebar.label(Messages.titleCase(item.name()));
		titlebar.setRect(0, 0, WIDTH, 0);
		add( titlebar );

		String msg = "";
		if (item instanceof CorpseDust){
			msg = Messages.get(this, "dust");
		} else if (item instanceof Embers){
			msg = Messages.get(this, "ember");
		} else if (item instanceof Rotberry.Seed){
			msg = Messages.get(this, "berry");
		}

		RenderedTextBlock message = PixelScene.renderTextBlock( msg, 6 );
		message.maxWidth(WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		add( message );

		ItemButton btnWand1 = new ItemButton(){
			@Override
			protected void onClick() {
				if (Dungeon.hero.belongings.contains(questItem) && item() != null) {
					GameScene.show(new RewardWindow(item()));
				} else {
					hide();
				}
			}
		};
		btnWand1.item(Wandmaker.Quest.wand1);
		btnWand1.setRect( (WIDTH - BTN_GAP) / 2 - BTN_SIZE, message.top() + message.height() + BTN_GAP, BTN_SIZE, BTN_SIZE );
		add( btnWand1 );
		
		ItemButton btnWand2 = new ItemButton(){
			@Override
			protected void onClick() {
				if (Dungeon.hero.belongings.contains(questItem) && item() != null) {
					GameScene.show(new RewardWindow(item()));
				} else {
					hide();
				}
			}
		};
		btnWand2.item(Wandmaker.Quest.wand2);
		btnWand2.setRect( btnWand1.right() + BTN_GAP, btnWand1.top(), BTN_SIZE, BTN_SIZE );
		add(btnWand2);
		
		resize(WIDTH, (int) btnWand2.bottom());
	}
	
	private void selectReward( Item reward ) {

		if (reward == null){
			return;
		}

		hide();

		questItem.detach( Dungeon.hero.belongings.backpack );

		reward.identify(false);
		if (reward.doPickUp( Dungeon.hero )) {
			GLog.i( Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", reward.name())) );
		} else {
			Dungeon.level.drop( reward, wandmaker.pos ).sprite.drop();
		}
		
		wandmaker.yell( Messages.get(this, "farewell", Messages.titleCase(Dungeon.hero.name())) );
		wandmaker.destroy();
		
		wandmaker.sprite.die();
		
		Wandmaker.Quest.complete();
	}

	private class RewardWindow extends WndInfoItem {

		public RewardWindow( Item item ) {
			super(item);

			RedButton btnConfirm = new RedButton(Messages.get(WndSadGhost.class, "confirm")){
				@Override
				protected void onClick() {
					RewardWindow.this.hide();

					selectReward( item );
				}
			};
			btnConfirm.setRect(0, height+2, width/2-1, 16);
			add(btnConfirm);

			RedButton btnCancel = new RedButton(Messages.get(WndSadGhost.class, "cancel")){
				@Override
				protected void onClick() {
					hide();
				}
			};
			btnCancel.setRect(btnConfirm.right()+2, height+2, btnConfirm.width(), 16);
			add(btnCancel);

			resize(width, (int)btnCancel.bottom());
		}
	}

}
