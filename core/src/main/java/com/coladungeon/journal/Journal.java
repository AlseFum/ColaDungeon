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

package com.coladungeon.journal;

import com.coladungeon.ColaDungeon;
import com.coladungeon.SaveManager;
import com.watabou.utils.Bundle;

import java.io.IOException;

public class Journal {
	
	public static final String JOURNAL_FILE = "journal.dat";
	
	private static boolean loaded = false;
	
	public static void loadGlobal(){
		if (loaded){
			return;
		}
		
		Bundle global = SaveManager.loadGlobal();
		Bundle journalBundle = global.getBundle("journal");
		if (journalBundle == null || journalBundle.isNull()) {
			// 兼容旧版数据
			journalBundle = legacyLoad();
			if (journalBundle == null) {
				journalBundle = new Bundle();
			}
			global.put("journal", journalBundle);
			try {
				SaveManager.saveGlobal(global);
			} catch (IOException ignored) {
			}
		}

		Catalog.restore( journalBundle );
		Bestiary.restore( journalBundle );
		Document.restore( journalBundle );
		
		loaded = true;
	}
	
	//package-private
	static boolean saveNeeded = false;

	public static void saveGlobal(){
		saveGlobal(false);
	}

	public static void saveGlobal(boolean force){
		if (!force && !saveNeeded){
			return;
		}
		
		Bundle bundle = new Bundle();
		
		Catalog.store(bundle);
		Bestiary.store(bundle);
		Document.store(bundle);
		
		try {
			Bundle global = SaveManager.loadGlobal();
			global.put("journal", bundle);
			SaveManager.saveGlobal( global );
			saveNeeded = false;
		} catch (IOException e) {
			ColaDungeon.reportException(e);
		}
		
	}

	private static Bundle legacyLoad() {
		try {
			return com.watabou.utils.FileUtils.bundleFromFile( JOURNAL_FILE );
		} catch (IOException e) {
			return null;
		}
	}

}
