package com.coladungeon.utils;
import java.util.BitSet;

public class BitStore {
    private final BitSet bitSet; // 使用比特位存储开关状态
    private final int size;      // 配置项总数量

    /**
     * 创建指定大小的配置存储器
     * @param size 配置项数量
     */
    public BitStore(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        this.size = size;
        this.bitSet = new BitSet(size);
    }

    /**
     * 设置指定位置的开关状态
     * @param index 配置项位置 (0-based)
     * @param isOpen true=开启, false=关闭
     */
    public void set(int index, boolean isOpen) {
        checkIndex(index);
        bitSet.set(index, isOpen);
    }

    /**
     * 获取指定位置的开关状态
     * @param index 配置项位置 (0-based)
     * @return true=开启, false=关闭
     */
    public boolean get(int index) {
        checkIndex(index);
        return bitSet.get(index);
    }

    /**
     * 返回配置项总数
     */
    public int size() {
        return size;
    }

    /**
     * 获取配置的压缩十六进制表示（按8位分组）
     */
    public String toHexString() {
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < size; i += 8) {
            int byteValue = 0;
            int bitsToProcess = Math.min(8, size - i);
            for (int j = 0; j < bitsToProcess; j++) {
                if (bitSet.get(i + j)) {
                    byteValue |= (1 << j);
                }
            }
            hex.append(String.format("%02X", byteValue));
        }
        return hex.toString();
    }

    // 索引范围检查
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
}