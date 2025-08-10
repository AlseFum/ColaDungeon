package com.coladungeon.commands;

import com.coladungeon.utils.GLog;

import java.util.Arrays;

public final class CommandDispatcher {
    private CommandDispatcher(){}
    public static void dispatch(String line){
        if (line == null) return;
        line = line.trim();
        if (line.isEmpty()) return;

        String[] parts = line.split("\\s+");
        String name = parts[0];
        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

        Command command = CommandRegistry.get(name);
        if (command == null){
            GLog.w("未知命令：" + name);
            return;
        }
        try {
            command.execute(args);
        } catch (Exception ex){
            GLog.w("命令执行异常：" + ex.getMessage());
        }
    }
}


