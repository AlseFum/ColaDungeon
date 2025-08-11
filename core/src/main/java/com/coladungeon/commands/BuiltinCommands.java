package com.coladungeon.commands;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.items.Generator;
import com.coladungeon.items.Item;
import com.coladungeon.event.misc;
import com.coladungeon.utils.GLog;
import com.watabou.utils.Reflection;

public final class BuiltinCommands {
    private static boolean installed = false;
    private BuiltinCommands(){}

    public static void installDefaults(){
        if (installed) return;
        installed = true;
        CommandRegistry.register(new Help());
        CommandRegistry.register(new Echo());
        CommandRegistry.register(new Heal());
        CommandRegistry.register(new Where());
        CommandRegistry.register(new Give());
        CommandRegistry.register(new Explode());
    }

    private static class Give implements Command {
        @Override public String name() { return "give"; }
        @Override public String usage() { return "give <item> [amount]"; }
        @Override public void execute(String[] args) {
            if (args.length == 0) {
                GLog.w("用法：" + usage());
                return;
            }

            Hero hero = Dungeon.hero;
            if (hero == null) {
                GLog.w("无角色。");
                return;
            }

            String itemName = args[0].toLowerCase();
            int amount = args.length > 1 ? Integer.parseInt(args[1]) : 1;

            Item item = null;
            
            // 尝试从生成器中获取物品
            for (Generator.Category cat : Generator.Category.values()) {
                if (cat.classes == null) continue;
                for (Class<?> cls : cat.classes) {
                    if (cls.getSimpleName().toLowerCase().contains(itemName)) {
                        item = (Item) Reflection.newInstance(cls);
                        break;
                    }
                }
                if (item != null) break;
            }

            // 如果找不到物品
            if (item == null) {
                GLog.w("未找到物品：" + itemName);
                return;
            }

            // 设置数量（如果物品可堆叠）
            if (item.stackable) {
                item.quantity(amount);
            }

            // 添加到背包
            if (item.collect(hero.belongings.backpack)) {
                GLog.i("已添加 " + (item.stackable ? amount + " 个 " : "") + item.name());
            } else {
                GLog.w("背包已满。");
            }
        }
    }

    private static class Help implements Command{
        @Override public String name() { return "help"; }
        @Override public String usage() { return "help [command]"; }
        @Override public void execute(String[] args) {
            if (args.length == 0){
                StringBuilder sb = new StringBuilder();
                sb.append("可用命令：");
                boolean first = true;
                for (Command c : CommandRegistry.all()){
                    if (!first) sb.append(", ");
                    sb.append(c.name());
                    first = false;
                }
                GLog.i(sb.toString());
            } else {
                Command c = CommandRegistry.get(args[0]);
                if (c == null){
                    GLog.w("未知命令："+args[0]);
                } else {
                    GLog.i(c.name() + " 用法：" + c.usage());
                }
            }
        }
    }

    private static class Echo implements Command{
        @Override public String name() { return "echo"; }
        @Override public String usage() { return "echo <text...>"; }
        @Override public void execute(String[] args) {
            if (args.length == 0){
                GLog.i("");
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i=0;i<args.length;i++){
                    if (i>0) sb.append(' ');
                    sb.append(args[i]);
                }
                GLog.i(sb.toString());
            }
        }
    }

    private static class Heal implements Command{
        @Override public String name() { return "heal"; }
        @Override public String usage() { return "heal [amount|max]"; }
        @Override public void execute(String[] args) {
            Hero hero = Dungeon.hero;
            if (hero == null){
                GLog.w("无角色。");
                return;
            }
            if (args.length == 0 || "max".equalsIgnoreCase(args[0])){
                hero.HP = hero.HT;
            } else {
                try {
                    int delta = Integer.parseInt(args[0]);
                    hero.HP = Math.min(hero.HT, Math.max(0, hero.HP + delta));
                } catch (Exception ex){
                    GLog.w("参数错误："+args[0]);
                    return;
                }
            }
            GLog.i("生命：" + hero.HP + "/" + hero.HT);
        }
    }

    private static class Where implements Command{
        @Override public String name() { return "where"; }
        @Override public String usage() { return "where"; }
        @Override public void execute(String[] args) {
            if (Dungeon.hero == null){
                GLog.i("未在地城中。");
            } else {
                GLog.i("层数：" + Dungeon.depth + ", 位置：" + Dungeon.hero.pos);
            }
        }
    }
    public static class Explode implements Command{
        @Override public String name() { return "explode"; }
        @Override public String usage() { return "explode <cell>"; }
        @Override public void execute(String[] args) {
            if (args.length == 0) {
                GLog.w("用法：" + usage());
                return;
            }
            try {
                int cell = Integer.parseInt(args[0]);
                misc.explode(cell, true, 3, Dungeon.hero);
            } catch (Exception ex) {
                GLog.w("参数错误：" + args[0]);
            }
        }
    }
}


