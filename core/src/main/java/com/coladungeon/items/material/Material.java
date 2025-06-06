package com.coladungeon.items.material;

import com.coladungeon.items.Item;
import java.util.ArrayList;
import java.util.HashMap;
public class Material extends Item{

    public static record AspectEntry(Aspect aspect, int level){}
    public ArrayList<AspectEntry> aspects=new ArrayList<>();
    public void addAspect(Aspect aspect, int level){
        aspects.add(new AspectEntry(aspect, level));
    }
    public void removeAspect(Aspect aspect){
        aspects.removeIf(entry -> entry.aspect.equals(aspect));
    }
    public int getAspectLevel(Aspect aspect){
        return aspects.stream()
            .filter(entry -> entry.aspect.equals(aspect))
            .map(AspectEntry::level)
            .findFirst()
            .orElse(0);
    }
    public static int sumAspect(ArrayList<AspectEntry> aspects,Aspect a_for){
        return aspects.stream().filter(entry -> entry.aspect.equals(a_for)).mapToInt(AspectEntry::level).sum();
    }
    public static ArrayList<AspectEntry> sumAspect(ArrayList<Material> materials) {
        // 使用Map来存储和累加aspects，避免重复遍历
        HashMap<Aspect, Integer> aspectMap = new HashMap<>();
        
        // 遍历所有材料
        for (Material material : materials) {
            // 遍历每个材料的aspects
            for (AspectEntry entry : material.aspects) {
                // 使用Map的merge方法来累加level
                aspectMap.merge(entry.aspect, entry.level, Integer::sum);
            }
        }
        
        // 将Map转换回ArrayList<AspectEntry>
        ArrayList<AspectEntry> result = new ArrayList<>();
        aspectMap.forEach((aspect, level) -> 
            result.add(new AspectEntry(aspect, level))
        );
        
        return result;
    }
}