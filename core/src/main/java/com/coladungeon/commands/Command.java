package com.coladungeon.commands;

import com.coladungeon.utils.GLog;

public interface Command {
    String name();
    String usage();
    void execute(String[] args) throws Exception;
    default void print(String text){ GLog.i(text); }
}


