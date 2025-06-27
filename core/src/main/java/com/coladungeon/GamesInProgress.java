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

package com.coladungeon;

import com.badlogic.gdx.Gdx;
import com.coladungeon.actors.hero.HeroClass;
import com.coladungeon.actors.hero.HeroSubClass;
import com.coladungeon.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;

public class GamesInProgress {
	
	public static final int MAX_SLOTS = 42;
	
	//null means we have loaded info and it is empty, no entry means unknown.
	private static HashMap<Integer, Info> slotStates = new HashMap<>();
	public static int curSlot;
	
	public static HeroClass selectedClass;
	
	private static final String GAME_FOLDER = "game%d";
	private static final String GAME_FILE	= "game.dat";
	private static final String DEPTH_FILE	= "depth%d.dat";
	private static final String DEPTH_BRANCH_FILE	= "depth%d-branch%d.dat";
	
	public static boolean gameExists( int slot ){
		return FileUtils.dirExists(gameFolder(slot))
				&& FileUtils.fileLength(gameFile(slot)) > 1;
	}
	
	public static String gameFolder( int slot ){
		return Messages.format(GAME_FOLDER, slot);
	}
	
	public static String gameFile( int slot ){
		return gameFolder(slot) + "/" + GAME_FILE;
	}
	
	public static String depthFile( int slot, int depth, int branch ) {
		if (branch == 0) {
			return gameFolder(slot) + "/" + Messages.format(DEPTH_FILE, depth);
		} else {
			return gameFolder(slot) + "/" + Messages.format(DEPTH_BRANCH_FILE, depth, branch);
		}
	}
	
	public static int firstEmpty(){
		for (int i = 1; i <= MAX_SLOTS; i++){
			if (check(i) == null) return i;
		}
		return -1;
	}
	
	public static ArrayList<Info> checkAll(){
		ArrayList<Info> result = new ArrayList<>();
		for (int i = 1; i <= MAX_SLOTS; i++){
			Info curr = check(i);
			if (curr != null) result.add(curr);
		}
		switch (CDSettings.gamesInProgressSort()){
			case "level": default:
				Collections.sort(result, levelComparator);
				break;
			case "last_played":
				Collections.sort(result, lastPlayedComparator);
				break;
		}

		return result;
	}
	
	public static Info check( int slot ) {
		
		if (slotStates.containsKey( slot )) {
			
			return slotStates.get( slot );
			
		} else if (!gameExists( slot )) {
			
			slotStates.put(slot, null);
			return null;
			
		} else {
			
			Info info;
			try {
				
				Bundle bundle = FileUtils.bundleFromFile(gameFile(slot));

				info = new Info();
					info.slot = slot;
					Dungeon.preview(info, bundle);

			} catch (IOException e) {
				info = null;
			} catch (Exception e){
				ColaDungeon.reportException( e );
				info = null;
			}
			
			slotStates.put( slot, info );
			return info;
			
		}
	}

	public static void set(int slot) {
		Info info = new Info();
		info.slot = slot;

		info.lastPlayed = Dungeon.lastPlayed;
		
		info.depth = Dungeon.depth;
		info.challenges = Dungeon.challenges;

		info.seed = Dungeon.seed;
		info.customSeed = Dungeon.customSeedText;
		info.daily = Dungeon.daily;
		info.dailyReplay = Dungeon.dailyReplay;
		
		info.level = Dungeon.hero.lvl;
		info.str = Dungeon.hero.STR;
		info.strBonus = Dungeon.hero.STR() - Dungeon.hero.STR;
		info.exp = Dungeon.hero.exp;
		info.hp = Dungeon.hero.HP;
		info.ht = Dungeon.hero.HT;
		info.shld = Dungeon.hero.shielding();
		info.heroClass = Dungeon.hero.heroClass;
		info.subClass = Dungeon.hero.subClass;
		info.armorTier = Dungeon.hero.tier();
		
		info.goldCollected = Statistics.goldCollected;
		info.maxDepth = Statistics.deepestFloor;

		slotStates.put( slot, info );
	}
	
	public static void setUnknown( int slot ) {
		slotStates.remove( slot );
	}
	
	public static void delete( int slot ) {
		slotStates.put( slot, null );
	}
	
	/**
	 * 将指定存档复制到剪贴板
	 * 存档会被压缩并编码为Base64字符串，便于分享和备份
	 */
	public static boolean copyToClipboard(int slot) {
		if (!gameExists(slot)) {
			return false;
		}

		try {
			// 读取主存档文件
			Bundle gameBundle = FileUtils.bundleFromFile(gameFile(slot));
			
			// 创建一个包含所有存档数据的容器Bundle
			Bundle fullSave = new Bundle();
			fullSave.put("game", gameBundle);
			
			// 读取所有深度文件
			Bundle depthsBundle = new Bundle();
			Info info = check(slot);
			if (info != null && info.maxDepth > 0) {
				for (int d = 1; d <= info.maxDepth; d++) {
					// 主分支
					String depthFile = depthFile(slot, d, 0);
					if (FileUtils.fileExists(depthFile)) {
						Bundle depthBundle = FileUtils.bundleFromFile(depthFile);
						depthsBundle.put("depth_" + d + "_0", depthBundle);
					}
					
					// 其他分支（1-4）
					for (int b = 1; b <= 4; b++) {
						String branchFile = depthFile(slot, d, b);
						if (FileUtils.fileExists(branchFile)) {
							Bundle branchBundle = FileUtils.bundleFromFile(branchFile);
							depthsBundle.put("depth_" + d + "_" + b, branchBundle);
						}
					}
				}
			}
			fullSave.put("depths", depthsBundle);
			
			// 将Bundle转换为压缩的Base64字符串
			String saveData = bundleToCompressedString(fullSave);
			
			// 添加版本和格式标识
			String clipboardData = "COLADUNGEON_SAVE_V1:" + saveData;
			
			// 复制到剪贴板
			Gdx.app.getClipboard().setContents(clipboardData);
			
			return true;
		} catch (IOException e) {
			ColaDungeon.reportException(e);
			return false;
		}
	}
	
	/**
	 * 从剪贴板导入存档
	 * 返回导入到的存档槽位，失败时返回-1
	 */
	public static int importFromClipboard() {
		try {
			String clipboardData = Gdx.app.getClipboard().getContents();
			if (clipboardData == null || !clipboardData.startsWith("COLADUNGEON_SAVE_V1:")) {
				return -1;
			}
			
			// 移除格式标识
			String saveData = clipboardData.substring("COLADUNGEON_SAVE_V1:".length());
			
			// 从压缩字符串恢复Bundle
			Bundle fullSave = bundleFromCompressedString(saveData);
			
			// 找到一个空的存档槽位
			int newSlot = firstEmpty();
			if (newSlot == -1) {
				return -1;
			}
			
			// 恢复主存档文件
			Bundle gameBundle = fullSave.getBundle("game");
			FileUtils.bundleToFile(gameFile(newSlot), gameBundle);
			
			// 恢复深度文件
			Bundle depthsBundle = fullSave.getBundle("depths");
			if (depthsBundle != null) {
				ArrayList<String> depthKeys = depthsBundle.getKeys();
				for (String key : depthKeys) {
					if (key.startsWith("depth_")) {
						// 解析depth_d_b格式的键
						String[] parts = key.split("_");
						if (parts.length == 3) {
							int depth = Integer.parseInt(parts[1]);
							int branch = Integer.parseInt(parts[2]);
							Bundle depthBundle = depthsBundle.getBundle(key);
							FileUtils.bundleToFile(depthFile(newSlot, depth, branch), depthBundle);
						}
					}
				}
			}
			
			// 刷新存档状态
			setUnknown(newSlot);
			
			return newSlot;
		} catch (Exception e) {
			ColaDungeon.reportException(e);
			return -1;
		}
	}
	
	/**
	 * 将Bundle压缩并编码为Base64字符串
	 */
	private static String bundleToCompressedString(Bundle bundle) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzos = new GZIPOutputStream(baos);
		
		String jsonString = bundle.toString();
		gzos.write(jsonString.getBytes("UTF-8"));
		gzos.close();
		
		byte[] compressed = baos.toByteArray();
		return Base64.getEncoder().encodeToString(compressed);
	}
	
	/**
	 * 从压缩的Base64字符串恢复Bundle
	 */
	private static Bundle bundleFromCompressedString(String compressedData) throws IOException {
		byte[] compressed = Base64.getDecoder().decode(compressedData);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
		GZIPInputStream gzis = new GZIPInputStream(bais);
		
		// 读取解压缩的数据
		ByteArrayOutputStream decompressed = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = gzis.read(buffer)) != -1) {
			decompressed.write(buffer, 0, len);
		}
		gzis.close();
		
		String jsonString = new String(decompressed.toByteArray(), "UTF-8");
		
		// 重新构造Bundle
		return Bundle.read(new ByteArrayInputStream(jsonString.getBytes("UTF-8")));
	}
	
	public static class Info {
		public int slot;

		public int depth;
		public int version;
		public int challenges;

		public long seed;
		public String customSeed;
		public boolean daily;
		public boolean dailyReplay;
		public long lastPlayed;

		public int level;
		public int str;
		public int strBonus;
		public int exp;
		public int hp;
		public int ht;
		public int shld;
		public HeroClass heroClass;
		public HeroSubClass subClass;
		public int armorTier;
		
		public int goldCollected;
		public int maxDepth;
	}
	
	public static final Comparator<GamesInProgress.Info> levelComparator = new Comparator<GamesInProgress.Info>() {
		@Override
		public int compare(GamesInProgress.Info lhs, GamesInProgress.Info rhs ) {
			if (rhs.level != lhs.level){
				return (int)Math.signum( rhs.level - lhs.level );
			} else {
				return lastPlayedComparator.compare(lhs, rhs);
			}
		}
	};

	public static final Comparator<GamesInProgress.Info> lastPlayedComparator = new Comparator<GamesInProgress.Info>() {
		@Override
		public int compare(GamesInProgress.Info lhs, GamesInProgress.Info rhs ) {
			return (int)Math.signum( rhs.lastPlayed - lhs.lastPlayed );
		}
	};
}
