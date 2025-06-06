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

import com.coladungeon.Chrome;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.hero.Talent;
import com.coladungeon.items.Generator;
import com.coladungeon.items.Item;
import com.coladungeon.items.artifacts.Artifact;
import com.coladungeon.items.artifacts.CloakOfShadows;
import com.coladungeon.items.artifacts.HolyTome;
import com.coladungeon.items.bags.Bag;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.GameScene;
import com.coladungeon.scenes.PixelScene;
import com.coladungeon.ui.InventorySlot;
import com.coladungeon.ui.QuickSlotButton;
import com.coladungeon.ui.RenderedTextBlock;
import com.coladungeon.ui.Window;
import com.watabou.noosa.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WndQuickBag extends Window {

	private static Item bag;

	public WndQuickBag(Bag bag){
		super(0, 0, Chrome.get(Chrome.Type.TOAST_TR));

		if( WndBag.INSTANCE != null ){
			WndBag.INSTANCE.hide();
		}
		WndBag.INSTANCE = this;

		WndQuickBag.bag = bag;

		float width = 0, height = 0;
		int maxWidth = PixelScene.landscape() ? 240 : 135;
		int left = 0;
		int top = 10;

		ArrayList<Item> items = new ArrayList<>();

		for (Item i : bag == null ? Dungeon.hero.belongings : bag){
			if (i.defaultAction() == null){
				continue;
			}
			if (i instanceof Bag) {
				continue;
			}
			if (i instanceof Artifact
					&& !i.isEquipped(Dungeon.hero)
					&& (!(i instanceof CloakOfShadows) || !Dungeon.hero.hasTalent(Talent.LIGHT_CLOAK))
					&& (!(i instanceof HolyTome) || !Dungeon.hero.hasTalent(Talent.LIGHT_READING))){
				continue;
			}
			items.add(i);
		}

		Collections.sort(items, quickBagComparator);

		int btnWidth = 16;
		int btnHeight = 20;

		//height of the toolbar and status pane, plus a little extra
		int targetHeight = PixelScene.uiCamera.height - 100;
		int rows = (int)Math.ceil(items.size() / (float)((maxWidth+1) / (btnWidth+1)));
		int expectedHeight = rows * btnHeight + (rows-1);
		while (expectedHeight > targetHeight && btnHeight > 16){
			btnHeight--;
			expectedHeight -= rows;
		}

		for (Item i : items){
			InventorySlot slot = new InventorySlot(i){
				@Override
				protected void onClick() {
					if (Dungeon.hero == null || !Dungeon.hero.isAlive() || !Dungeon.hero.belongings.contains(item)){
						Game.scene().addToFront(new WndUseItem(WndQuickBag.this, item));
						return;
					}

					hide();
					item.execute(Dungeon.hero);
					if (item.usesTargeting && bag != null){
						int idx = Dungeon.quickslot.getSlot(WndQuickBag.bag);
						if (idx != -1){
							QuickSlotButton.useTargeting(idx);
							bag.quickUseItem = item;
						}
					}
				}

				@Override
				protected boolean onLongClick() {
					Game.scene().addToFront(new WndUseItem(WndQuickBag.this, item));
					return true;
				}

				@Override
				protected String hoverText() {
					return null; //no tooltips here
 				}
			};
			slot.showExtraInfo(false);
			slot.setRect(left, top, btnWidth, btnHeight);
			add(slot);

			if (width < slot.right()) width = slot.right();
			if (height < slot.bottom()) height = slot.bottom();

			left += btnWidth+1;

			if (left + btnWidth > maxWidth){
				left = 0;
				top += btnHeight+1;
			}
		}

		RenderedTextBlock txtTitle;
		txtTitle = PixelScene.renderTextBlock( Messages.titleCase(Messages.get(this, "title")), 8 );
		txtTitle.hardlight( TITLE_COLOR );
		if (txtTitle.width() > width) width = txtTitle.width();

		txtTitle.setPos(
				(width - txtTitle.width())/2f,
				(10 - txtTitle.height()) / 2f - 1);
		PixelScene.align(txtTitle);
		add( txtTitle );

		resize((int)width, (int)height);

		int bottom = GameScene.uiCamera.height;

		//offset to be above the toolbar
		offset(0, (int) (bottom/2 - 30 - height/2));

	}

	public static final Comparator<Item> quickBagComparator = new Comparator<Item>() {
		@Override
		public int compare( Item lhs, Item rhs ) {
			if (lhs.isEquipped(Dungeon.hero) && !rhs.isEquipped(Dungeon.hero)){
				return -1;
			} else if (!lhs.isEquipped(Dungeon.hero) && rhs.isEquipped(Dungeon.hero)){
				return 1;
			} else {
				return Generator.Category.order(lhs) - Generator.Category.order(rhs);
			}
		}
	};

	@Override
	public void hide() {
		super.hide();
		if (WndBag.INSTANCE == this){
			WndBag.INSTANCE = null;
		}
	}

}
