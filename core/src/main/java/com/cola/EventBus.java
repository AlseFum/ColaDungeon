package com.cola;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class EventBus {
    public static HashMap<String, ArrayList<Function<Object, Object>>> eventBus = new HashMap<>();

    public static void register(
            String event_id, Function<Object, Object> fn) {
        eventBus.computeIfAbsent(event_id, k -> new ArrayList<>()).add(fn);
    }

    public static void unregister(
            String event_id, Function<Object, Object> fn) {
        eventBus.get(event_id).remove(fn);
    }
    public static ArrayList<Object> collect(String event_id,Object args) {
        ArrayList<Object> result = new ArrayList<>();
        for (Function<Object, Object> fn : eventBus.get(event_id)) {
            result.add(fn.apply(args));
        }
        return result;
    }
}
