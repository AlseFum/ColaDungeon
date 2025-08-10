package com.coladungeon.commands;

import com.coladungeon.Dungeon;
import com.coladungeon.actors.hero.Hero;
import com.coladungeon.utils.GLog;

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
}


