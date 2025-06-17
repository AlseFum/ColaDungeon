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

package com.coladungeon.items.stones;

import java.util.ArrayList;

import com.coladungeon.Assets;
import com.coladungeon.Dungeon;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.effects.CellEmitter;
import com.coladungeon.effects.Speck;
import com.coladungeon.items.Generator;
import com.coladungeon.items.Item;
import com.coladungeon.sprites.ItemSpriteSheet;
import com.coladungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class StoneOfGeneration extends Runestone {
	
	{
		image = ItemSpriteSheet.STONE_BLINK;
	}
	
	// 存储当前选择的物品类别
	private Generator.Category selectedCategory = Generator.Category.POTION;
	
	private static final String CATEGORY = "category";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CATEGORY, selectedCategory != null ? selectedCategory.name() : "null");
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		String catName = bundle.getString(CATEGORY);
		if (!catName.equals("null")) {
			try {
				selectedCategory = Generator.Category.valueOf(catName);
			} catch (Exception e) {
				GLog.w("Error restoring category: " + e.getMessage());
				selectedCategory = null;
			}
		}
	}
	
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_SELECT);
		return actions;
	}
	
	private static final String AC_SELECT = "SELECT";
	
	@Override
	public String actionName(String action, Hero hero) {
		if (action.equals(AC_SELECT)) {
			return "选择类别";
		}
		return super.actionName(action, hero);
	}
	
	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		
		if (action.equals(AC_SELECT)) {
			cycleCategory();
			updateQuickslot();
		}
	}
	
	// 循环切换物品类别
	private void cycleCategory() {
		Generator.Category[] categories = Generator.Category.values();
		int currentIndex = -1;
		
		if (selectedCategory != null) {
			for (int i = 0; i < categories.length; i++) {
				if (categories[i] == selectedCategory) {
					currentIndex = i;
					break;
				}
			}
		}
		
		// 选择下一个类别
		currentIndex = (currentIndex + 1) % categories.length;
		selectedCategory = categories[currentIndex];
		
		GLog.p("Category selected: " + selectedCategory.name());
	}
	
	@Override
	protected void activate(int cell) {
		// 检查类别是否有效
		if (selectedCategory == null) {
			// 如果没有选择类别，随机选一个
			Generator.Category[] categories = Generator.Category.values();
			if (categories.length > 0) {
				selectedCategory = categories[Random.Int(categories.length)];
			} else {
				// 如果没有可用类别，显示错误并返回
				GLog.w("No item categories available!");
				CellEmitter.get(cell).burst(Speck.factory(Speck.SMOKE), 3);
				return;
			}
		}
		
		// 尝试生成物品
		Item generatedItem = null;
		try {
			generatedItem = Generator.random(selectedCategory);
		} catch (Exception e) {
			GLog.w("Error generating item: " + e.getMessage());
			CellEmitter.get(cell).burst(Speck.factory(Speck.SMOKE), 3);
			return;
		}
		
		// 检查物品是否成功生成
		if (generatedItem != null) {
			try {
				// 尝试放置物品
				Dungeon.level.drop(generatedItem, cell).sprite.drop();
				
				// 视觉和音效
				CellEmitter.center(cell).burst(Speck.factory(Speck.STAR), 4);
				Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
				
				// 提示信息
				GLog.i("A " + generatedItem.name() + " has been generated from the " + selectedCategory.name() + " category!");
			} catch (Exception e) {
				// 如果无法放置物品，显示错误
				GLog.w("Could not place the generated item: " + e.getMessage());
				CellEmitter.get(cell).burst(Speck.factory(Speck.SMOKE), 3);
			}
		} else {
			// 如果生成失败，尝试使用其他方法生成默认物品
			GLog.w("Failed to generate an item from " + selectedCategory.name() + ". Trying a different category...");
			CellEmitter.get(cell).burst(Speck.factory(Speck.SMOKE), 3);
			
			// 尝试生成金币作为备选
			try {
				Item gold = new com.coladungeon.items.Gold(Random.IntRange(10, 30));
				Dungeon.level.drop(gold, cell).sprite.drop();
				CellEmitter.center(cell).burst(Speck.factory(Speck.STAR), 2);
				GLog.i("Generated some gold instead!");
			} catch (Exception e) {
				GLog.w("Could not generate any items at all.");
			}
		}
	}
	
	@Override
	public String name() {
		if (selectedCategory != null) {
			return "Stone of Generation (" + selectedCategory.name() + ")";
		}
		return "Stone of Generation";
	}
	
	@Override
	public String desc() {
		String desc = "This magical stone can conjure items from thin air. Throw it to create a random item from the selected category, or tap to change the category.";
		if (selectedCategory != null) {
			desc += "\n\nCurrently set to generate items from: " + selectedCategory.name();
		}
		return desc;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public int value() {
		return 30 * quantity;
	}
} 