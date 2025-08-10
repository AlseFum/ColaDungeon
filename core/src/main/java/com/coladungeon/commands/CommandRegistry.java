package com.coladungeon.commands;

import java.util.*;

public final class CommandRegistry {
    private static final Map<String, Command> NAME_TO_COMMAND = new LinkedHashMap<>();
    private CommandRegistry() {}
    public static void register(Command command){
        if (command == null || command.name() == null) return;
        NAME_TO_COMMAND.put(command.name().toLowerCase(Locale.ENGLISH), command);
    }
    public static Command get(String name){
        if (name == null) return null;
        return NAME_TO_COMMAND.get(name.toLowerCase(Locale.ENGLISH));
    }
    public static Collection<Command> all(){ return Collections.unmodifiableCollection(NAME_TO_COMMAND.values()); }
    public static void clear(){ NAME_TO_COMMAND.clear(); }
}


