package com.coladungeon;

import com.badlogic.gdx.Gdx;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * 统一存档管理器
 * 
 * 文件结构：
 * - global.json      全局数据（图鉴、成就、排行榜等）
 * - save-001.json    存档槽1
 * - save-002.json    存档槽2
 * - ...
 * - save-042.json    存档槽42（最多42个）
 * 
 * 每个存档文件包含：
 * - 游戏主数据（玩家、深度、金币等）
 * - 所有地图数据（合并存储）
 * - 元数据（用于快速显示）
 * 
 * 使用示例：
 * <pre>
 * // 保存游戏
 * SaveManager.saveGame(1, gameBundle);
 * 
 * // 加载游戏
 * Bundle data = SaveManager.loadGame(1);
 * 
 * // 保存全局数据
 * SaveManager.saveGlobal(globalBundle);
 * 
 * // 获取存档列表
 * List&lt;SaveInfo&gt; saves = SaveManager.listSaves();
 * </pre>
 */
public class SaveManager {
    
    // 配置
    public static final int MAX_SLOTS = 42;
    private static final String GLOBAL_FILE = "global.json";
    private static final String SAVE_FILE_PATTERN = "save-%03d.json";

    private static final String LEVELS_KEY = "levels";
    
    // 存档信息缓存
    private static final HashMap<Integer, SaveInfo> saveInfoCache = new HashMap<>();
    
    /**
     * 存档信息（用于快速显示存档列表）
     */
    public static class SaveInfo {
        public int slot;
        public String heroClass;
        public int heroLevel;
        public int depth;
        public int gold;
        public long playTime;      // 游戏时长（毫秒）
        public long lastPlayed;    // 最后游玩时间
        public boolean exists;
        
        public SaveInfo(int slot) {
            this.slot = slot;
            this.exists = false;
        }
        
        /**
         * 从Bundle提取元数据
         */
        public void extractFromBundle(Bundle bundle) {
            // 从Bundle中提取基本信息
            this.heroClass = bundle.contains("heroClass") ? bundle.getString("heroClass") : "Unknown";
            this.heroLevel = bundle.contains("heroLevel") ? bundle.getInt("heroLevel") : 1;
            this.depth = bundle.contains("depth") ? bundle.getInt("depth") : 1;
            this.gold = bundle.contains("gold") ? bundle.getInt("gold") : 0;
            this.playTime = bundle.contains("playTime") ? bundle.getLong("playTime") : 0L;
            this.lastPlayed = bundle.contains("lastPlayed") ? bundle.getLong("lastPlayed") : System.currentTimeMillis();
            this.exists = true;
        }
        
        /**
         * 保存元数据到Bundle
         */
        public void saveToBundle(Bundle bundle) {
            bundle.put("heroClass", heroClass);
            bundle.put("heroLevel", heroLevel);
            bundle.put("depth", depth);
            bundle.put("gold", gold);
            bundle.put("playTime", playTime);
            bundle.put("lastPlayed", lastPlayed);
        }
    }
    
    // ========================================
    // 游戏存档管理
    // ========================================
    
    /**
     * 保存游戏数据
     * 
     * @param slot 存档槽（1-42）
     * @param bundle 游戏数据Bundle
     * @throws IOException 保存失败
     */
    public static void saveGame(int slot, Bundle bundle) throws IOException {
        validateSlot(slot);
        
        String filename = String.format(SAVE_FILE_PATTERN, slot);
        
        try {
            // 如果新数据中没有levels信息，则尝试保留旧的levels
            if (!bundle.contains(LEVELS_KEY)) {
                Bundle existing = loadGameOrNull(slot);
                if (existing != null && existing.contains(LEVELS_KEY)) {
                    bundle.put(LEVELS_KEY, existing.getBundle(LEVELS_KEY));
                }
            }

            // 添加元数据
            bundle.put("slot", slot);
            bundle.put("lastPlayed", System.currentTimeMillis());
            bundle.put("version", Game.versionCode);
            
            // 写入文件
            FileUtils.bundleToFile(filename, bundle);
            
            // 更新缓存
            SaveInfo info = new SaveInfo(slot);
            info.extractFromBundle(bundle);
            saveInfoCache.put(slot, info);
            
            log("SAVE", filename, true);
        } catch (IOException e) {
            log("SAVE", filename, false);
            throw e;
        }
    }

    /**
     * 更新单个地图数据
     */
    public static void saveLevel(int slot, int depth, int branch, Bundle levelBundle) throws IOException {
        validateSlot(slot);

        Bundle bundle = loadGameOrNew(slot);

        Bundle levels = bundle.getBundle(LEVELS_KEY);
        if (levels == null || levels.isNull()) {
            levels = new Bundle();
        }
        levels.put(levelKey(depth, branch), levelBundle);
        bundle.put(LEVELS_KEY, levels);

        saveGame(slot, bundle);
    }
    
    /**
     * 加载游戏数据
     * 
     * @param slot 存档槽（1-42）
     * @return 游戏数据Bundle
     * @throws IOException 加载失败
     */
    public static Bundle loadGame(int slot) throws IOException {
        validateSlot(slot);
        
        String filename = String.format(SAVE_FILE_PATTERN, slot);
        
        try {
            Bundle bundle = FileUtils.bundleFromFile(filename);
            
            // 更新缓存
            SaveInfo info = new SaveInfo(slot);
            info.extractFromBundle(bundle);
            saveInfoCache.put(slot, info);
            
            log("LOAD", filename, true);
            return bundle;
        } catch (IOException e) {
            log("LOAD", filename, false);
            throw e;
        }
    }

    /**
     * 加载地图数据
     */
    public static Bundle loadLevel(int slot, int depth, int branch) throws IOException {
        validateSlot(slot);

        Bundle game = loadGame(slot);
        Bundle levels = game.getBundle(LEVELS_KEY);
        if (levels == null || levels.isNull()) {
            throw new IOException("No level data stored");
        }

        Bundle level = levels.getBundle(levelKey(depth, branch));
        if (level == null || level.isNull()) {
            throw new IOException("Level data missing for depth " + depth + ", branch " + branch);
        }

        return level;
    }
    
    /**
     * 删除游戏存档
     * 
     * @param slot 存档槽（1-42）
     * @return 是否成功删除
     */
    public static boolean deleteGame(int slot) {
        validateSlot(slot);
        
        String filename = String.format(SAVE_FILE_PATTERN, slot);
        
        boolean success = FileUtils.deleteFile(filename);
        
        if (success) {
            saveInfoCache.remove(slot);
        }
        
        log("DELETE", filename, success);
        return success;
    }
    
    /**
     * 检查存档是否存在
     * 
     * @param slot 存档槽（1-42）
     * @return 是否存在
     */
    public static boolean saveExists(int slot) {
        validateSlot(slot);
        
        String filename = String.format(SAVE_FILE_PATTERN, slot);
        return FileUtils.fileExists(filename);
    }
    
    /**
     * 复制存档到新槽位
     * 
     * @param fromSlot 源槽位
     * @param toSlot 目标槽位
     * @return 是否成功
     */
    public static boolean copySave(int fromSlot, int toSlot) {
        validateSlot(fromSlot);
        validateSlot(toSlot);
        
        try {
            Bundle data = loadGame(fromSlot);
            saveGame(toSlot, data);
            log("COPY", fromSlot + " -> " + toSlot, true);
            return true;
        } catch (IOException e) {
            log("COPY", fromSlot + " -> " + toSlot, false);
            return false;
        }
    }
    
    // ========================================
    // 全局数据管理
    // ========================================
    
    /**
     * 保存全局数据（图鉴、成就、排行榜等）
     * 
     * @param bundle 全局数据Bundle
     * @throws IOException 保存失败
     */
    public static void saveGlobal(Bundle bundle) throws IOException {
        try {
            bundle.put("lastSaved", System.currentTimeMillis());
            bundle.put("version", Game.versionCode);
            
            FileUtils.bundleToFile(GLOBAL_FILE, bundle);
            log("SAVE_GLOBAL", GLOBAL_FILE, true);
        } catch (IOException e) {
            log("SAVE_GLOBAL", GLOBAL_FILE, false);
            throw e;
        }
    }
    
    /**
     * 加载全局数据
     * 
     * @return 全局数据Bundle，如果不存在返回空Bundle
     */
    public static Bundle loadGlobal() {
        try {
            Bundle bundle = FileUtils.bundleFromFile(GLOBAL_FILE);
            log("LOAD_GLOBAL", GLOBAL_FILE, true);
            return bundle;
        } catch (IOException e) {
            log("LOAD_GLOBAL", GLOBAL_FILE + " (not found, creating new)", true);
            return new Bundle();
        }
    }
    
    /**
     * 检查全局数据是否存在
     */
    public static boolean globalExists() {
        return FileUtils.fileExists(GLOBAL_FILE);
    }
    
    // ========================================
    // 存档信息查询
    // ========================================
    
    /**
     * 获取存档信息（快速，从缓存或文件头读取）
     * 
     * @param slot 存档槽
     * @return 存档信息，不存在返回null
     */
    public static SaveInfo getSaveInfo(int slot) {
        validateSlot(slot);
        
        // 先查缓存
        if (saveInfoCache.containsKey(slot)) {
            return saveInfoCache.get(slot);
        }
        
        // 读取文件
        if (!saveExists(slot)) {
            return null;
        }
        
        try {
            Bundle bundle = loadGame(slot);
            SaveInfo info = new SaveInfo(slot);
            info.extractFromBundle(bundle);
            saveInfoCache.put(slot, info);
            return info;
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * 列出所有存档
     * 
     * @return 存档信息列表（按槽位排序）
     */
    public static List<SaveInfo> listSaves() {
        List<SaveInfo> saves = new ArrayList<>();
        
        for (int slot = 1; slot <= MAX_SLOTS; slot++) {
            SaveInfo info = getSaveInfo(slot);
            if (info != null && info.exists) {
                saves.add(info);
            }
        }
        
        return saves;
    }
    
    /**
     * 列出所有存档（按最后游玩时间排序）
     */
    public static List<SaveInfo> listSavesByLastPlayed() {
        List<SaveInfo> saves = listSaves();
        Collections.sort(saves, (a, b) -> Long.compare(b.lastPlayed, a.lastPlayed));
        return saves;
    }
    
    /**
     * 列出所有存档（按深度排序）
     */
    public static List<SaveInfo> listSavesByDepth() {
        List<SaveInfo> saves = listSaves();
        Collections.sort(saves, (a, b) -> Integer.compare(b.depth, a.depth));
        return saves;
    }
    
    /**
     * 获取第一个空槽位
     * 
     * @return 空槽位编号，如果全满返回-1
     */
    public static int getFirstEmptySlot() {
        for (int slot = 1; slot <= MAX_SLOTS; slot++) {
            if (!saveExists(slot)) {
                return slot;
            }
        }
        return -1;
    }
    
    /**
     * 获取存档数量
     */
    public static int getSaveCount() {
        int count = 0;
        for (int slot = 1; slot <= MAX_SLOTS; slot++) {
            if (saveExists(slot)) {
                count++;
            }
        }
        return count;
    }
    
    // ========================================
    // 备份与导入导出
    // ========================================
    
    /**
     * 导出存档到剪贴板（Base64编码）
     * 
     * @param slot 存档槽
     * @return 是否成功
     */
    public static boolean exportToClipboard(int slot) {
        try {
            Bundle bundle = loadGame(slot);
            String json = bundle.toString();
            
            // 简单Base64编码（实际项目应该压缩）
            String encoded = "COLADUNGEON_SAVE:" + encodeBase64(json);
            
            Gdx.app.getClipboard().setContents(encoded);
            log("EXPORT", "slot " + slot + " to clipboard", true);
            return true;
        } catch (Exception e) {
            log("EXPORT", "slot " + slot + " to clipboard", false);
            return false;
        }
    }
    
    /**
     * 从剪贴板导入存档
     * 
     * @return 导入到的槽位，失败返回-1
     */
    public static int importFromClipboard() {
        try {
            String data = Gdx.app.getClipboard().getContents();
            
            if (data == null || !data.startsWith("COLADUNGEON_SAVE:")) {
                return -1;
            }
            
            // 解码
            String json = decodeBase64(data.substring("COLADUNGEON_SAVE:".length()));
            
            // 解析Bundle（简化版，实际需要更复杂的解析）
            // 这里假设Bundle有一个fromString方法，实际需要实现
            Bundle bundle = Bundle.read(new java.io.ByteArrayInputStream(json.getBytes()));
            
            // 找空槽位
            int slot = getFirstEmptySlot();
            if (slot == -1) {
                return -1;
            }
            
            saveGame(slot, bundle);
            log("IMPORT", "from clipboard to slot " + slot, true);
            return slot;
        } catch (Exception e) {
            log("IMPORT", "from clipboard", false);
            return -1;
        }
    }
    
    // ========================================
    // 辅助方法
    // ========================================
    
    /**
     * 验证槽位编号
     */
    private static void validateSlot(int slot) {
        if (slot < 1 || slot > MAX_SLOTS) {
            throw new IllegalArgumentException("Invalid slot: " + slot + " (must be 1-" + MAX_SLOTS + ")");
        }
    }
    
    /**
     * 简单的Base64编码
     */
    private static String encodeBase64(String str) {
        try {
            return java.util.Base64.getEncoder().encodeToString(str.getBytes("UTF-8"));
        } catch (Exception e) {
            return str;
        }
    }
    
    /**
     * 简单的Base64解码
     */
    private static String decodeBase64(String str) {
        try {
            return new String(java.util.Base64.getDecoder().decode(str), "UTF-8");
        } catch (Exception e) {
            return str;
        }
    }
    
    /**
     * 记录日志
     */
    private static void log(String operation, String target, boolean success) {
        String status = success ? "✓" : "✗";
        System.out.println(String.format("[SaveManager] %s %s: %s", status, operation, target));
    }
    
    /**
     * 清除缓存
     */
    public static void clearCache() {
        saveInfoCache.clear();
    }
    
    /**
     * 获取所有存档文件的总大小
     */
    public static long getTotalSize() {
        long total = 0;
        
        // 全局文件
        total += FileUtils.fileLength(GLOBAL_FILE);
        
        // 所有存档
        for (int slot = 1; slot <= MAX_SLOTS; slot++) {
            String filename = String.format(SAVE_FILE_PATTERN, slot);
            total += FileUtils.fileLength(filename);
        }
        
        return total;
    }
    
    /**
     * 打印存档信息（调试用）
     */
    public static void printSaveInfo() {
        System.out.println("=== SaveManager Info ===");
        System.out.println("Total saves: " + getSaveCount() + "/" + MAX_SLOTS);
        System.out.println("Total size: " + formatSize(getTotalSize()));
        System.out.println();
        
        List<SaveInfo> saves = listSaves();
        for (SaveInfo info : saves) {
            System.out.println(String.format("Slot %02d: %s Lv.%d | Depth %d | %s gold | Last played: %s",
                info.slot,
                info.heroClass,
                info.heroLevel,
                info.depth,
                info.gold,
                formatTimestamp(info.lastPlayed)));
        }
        
        System.out.println("========================");
    }
    
    /**
     * 格式化文件大小
     */
    private static String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / 1024.0 / 1024.0);
        }
    }
    
    /**
     * 格式化时间戳
     */
    private static String formatTimestamp(long time) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new java.util.Date(time));
    }
    
    // ========================================
    // 迁移工具（兼容旧版存档）
    // ========================================
    
    /**
     * 从旧版本存档迁移
     * 旧版本：game1/game.dat, game1/depth1.dat, ...
     * 新版本：save-001.json（包含所有数据）
     * 
     * @param oldSlot 旧存档槽（game1, game2, ...）
     * @param newSlot 新存档槽（1, 2, ...）
     * @return 是否成功迁移
     */
    public static boolean migrateFromLegacy(int oldSlot, int newSlot) {
        try {
            // 读取旧版主文件
            String oldGameFile = String.format("game%d/game.dat", oldSlot);
            Bundle mainBundle = FileUtils.bundleFromFile(oldGameFile);
            
            // 合并所有深度文件
            Bundle levelsBundle = new Bundle();
            for (int depth = 1; depth <= 30; depth++) {
                String depthFile = String.format("game%d/depth%d.dat", oldSlot, depth);
                if (FileUtils.fileExists(depthFile)) {
                    try {
                        Bundle levelData = FileUtils.bundleFromFile(depthFile);
                        levelsBundle.put("depth_" + depth + "_0", levelData);
                    } catch (IOException e) {
                        // 深度文件可能不存在，跳过
                    }
                }
            }
            
            // 合并数据
            mainBundle.put("levels", levelsBundle);
            
            // 保存为新格式
            saveGame(newSlot, mainBundle);
            
            log("MIGRATE", String.format("game%d -> save-%03d", oldSlot, newSlot), true);
            return true;
        } catch (Exception e) {
            log("MIGRATE", String.format("game%d -> save-%03d", oldSlot, newSlot), false);
            return false;
        }
    }
    
    /**
     * 批量迁移所有旧存档
     * 
     * @return 成功迁移的数量
     */
    public static int migrateAllLegacySaves() {
        int count = 0;
        
        for (int slot = 1; slot <= MAX_SLOTS; slot++) {
            String oldGameFile = String.format("game%d/game.dat", slot);
            if (FileUtils.fileExists(oldGameFile) && !saveExists(slot)) {
                if (migrateFromLegacy(slot, slot)) {
                    count++;
                }
            }
        }
        
        log("MIGRATE_ALL", count + " saves migrated", true);
        return count;
    }

    private static String levelKey(int depth, int branch) {
        return "depth_" + depth + "_" + branch;
    }

    private static Bundle loadGameOrNull(int slot) {
        try {
            return loadGame(slot);
        } catch (IOException e) {
            return null;
        }
    }

    private static Bundle loadGameOrNew(int slot) throws IOException {
        Bundle bundle = loadGameOrNull(slot);
        if (bundle == null) {
            bundle = new Bundle();
            bundle.put("slot", slot);
        }
        return bundle;
    }
}

