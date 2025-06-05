package com.coladungeon.items.material;

import java.util.HashSet;
public class Aspect {
    public String id;
    //名字可以相同
    public String name;
    public String desc;
    public Aspect(String id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }
    public HashSet<Aspect> aspects;
    public Aspect newAspect(String id, String name, String desc){
        Aspect aspect = new Aspect(id, name, desc);
        aspects.add(aspect);
        return aspect;
    }
    public Aspect $(String id){
        for(Aspect aspect : aspects){
            if(aspect.id.equals(id)){
                return aspect;
            }
        }
        return null;
    }
    static {

    }
    
}