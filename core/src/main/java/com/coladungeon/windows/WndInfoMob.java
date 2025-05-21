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

import com.coladungeon.actors.mobs.Mob;
import com.coladungeon.actors.traits.Trait;
import com.coladungeon.messages.Messages;
import com.coladungeon.scenes.PixelScene;
import com.coladungeon.sprites.CharSprite;
import com.coladungeon.ui.BuffIndicator;
import com.coladungeon.ui.HealthBar;
import com.coladungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class WndInfoMob extends WndTitledMessage {
	
	public WndInfoMob( Mob mob ) {
		super( new MobTitle( mob ), mob.info() );
		
		// 检查这个怪物是否有特质
		ArrayList<Trait> traits = mob.traits().getTraits();
		
		// 再次测试强制添加特质
		if (traits == null || traits.isEmpty()) {
			Mob.testAddTraits(mob);
			// 重新获取特质
			traits = mob.traits().getTraits();
		}
		
		// Add traits section if the mob has any traits
		if (traits != null && !traits.isEmpty()) {
			addTraitsSection(mob);
		}
	}
	
	private void addTraitsSection(Mob mob) {
		float yPos = height + 2;
		
		// 添加标题
		RenderedTextBlock traitsHeader = PixelScene.renderTextBlock(mob.name() + "的特质", 9);
		traitsHeader.hardlight(TITLE_COLOR);
		add(traitsHeader);
		traitsHeader.setPos((width - traitsHeader.width())/2, yPos);
		yPos = traitsHeader.bottom() + 2;
		
		// 添加分隔线
		ColorBlock separator = new ColorBlock(width, 1, 0xFF222222);
		add(separator);
		separator.y = yPos;
		yPos += 2;
		
		// 添加特质列表
		ArrayList<Trait> traits = mob.traits().getTraits();
		
		for (Trait trait : traits) {
			float traitValue = mob.getTraitValue(trait);
			
			String traitText = trait.name();
			if (traitValue != 1.0f) {
				traitText += " (" + traitValue + ")";
			}
			
			RenderedTextBlock text = PixelScene.renderTextBlock(traitText, 6);
			text.hardlight(0xCCCCCC);
			add(text);
			text.setPos(2, yPos);
			yPos = text.bottom() + 2;
		}
		
		// 调整窗口大小以适应特质部分，确保至少增加一些空间
		yPos += 2; // 额外的底部间距
		int newHeight = (int)yPos;
		
		resize(width, newHeight);
	}
	
	private static class MobTitle extends Component {

		private static final int GAP	= 2;
		
		private CharSprite image;
		private RenderedTextBlock name;
		private HealthBar health;
		private BuffIndicator buffs;
		
		public MobTitle( Mob mob ) {
			
			name = PixelScene.renderTextBlock( Messages.titleCase( mob.name() ), 9 );
			name.hardlight( TITLE_COLOR );
			add( name );
			
			image = mob.sprite();
			add( image );

			health = new HealthBar();
			health.level(mob);
			add( health );

			buffs = new BuffIndicator( mob, false );
			add( buffs );
		}
		
		@Override
		protected void layout() {
			
			image.x = 0;
			image.y = Math.max( 0, name.height() + health.height() - image.height() );

			float w = width - image.width() - GAP;
			int extraBuffSpace = 0;

			//Tries to make space for up to 11 visible buffs
			do {
				name.maxWidth((int)w - extraBuffSpace);
				buffs.setSize(w - name.width() - 8, 8);
				extraBuffSpace += 8;
			} while (extraBuffSpace <= 40 && !buffs.allBuffsVisible());

			name.setPos(x + image.width() + GAP,
					image.height() > name.height() ? y +(image.height() - name.height()) / 2 : y);

			health.setRect(image.width() + GAP, name.bottom() + GAP, w, health.height());

			buffs.setPos(name.right(), name.bottom() - BuffIndicator.SIZE_SMALL-2);

			height = Math.max(image.y + image.height(), health.bottom());
		}
	}
}
