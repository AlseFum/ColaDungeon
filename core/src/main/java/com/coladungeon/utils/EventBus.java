package com.coladungeon.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class EventBus {

    // 处理器包装类，存储处理函数及其优先级
    private static class PriorityHandler {

        final Function<Object, Object> handler;
        final int priority;

        PriorityHandler(Function<Object, Object> handler, int priority) {
            this.handler = handler;
            this.priority = priority;
        }
    }

    // 事件总线 - 存储事件ID到处理器的映射
    private final static HashMap<String, ArrayList<PriorityHandler>> eventBus = new HashMap<>();

    // 默认优先级值
    public static final int DEFAULT_PRIORITY = 0;
    public static void on(String event_id, Function<Object, Object> fn) {
        register(event_id, fn, DEFAULT_PRIORITY);
    }

    public static void on(String event_id, Function<Object, Object> fn, int priority) {
        register(event_id, fn, priority);
    }   
    // 使用默认优先级注册处理器
    public static void register(String event_id,
            Function<Object, Object> fn) {
        register(event_id, fn, DEFAULT_PRIORITY);
    }

    // 使用指定优先级注册处理器
    public static void register(String event_id, Function<Object, Object> fn, int priority) {
        ArrayList<PriorityHandler> handlers = eventBus.computeIfAbsent(event_id, k -> new ArrayList<>());
        handlers.add(new PriorityHandler(fn, priority));

        // 按优先级排序（降序）
        handlers.sort(Comparator.comparing((PriorityHandler h) -> h.priority).reversed());
    }

    // 注销处理器
    public static void unregister(String event_id, Function<Object, Object> fn) {
        if (!eventBus.containsKey(event_id)) {
            return;
        }

        List<PriorityHandler> handlers = eventBus.get(event_id);
        handlers.removeIf(ph -> ph.handler.equals(fn));
    }

    // 触发事件并收集处理结果
    private static ArrayList<Object> collect(String event_id, Object args) {
        ArrayList<Object> result = new ArrayList<>();
        if (!eventBus.containsKey(event_id)) {
            return result;
        }

        for (PriorityHandler ph : eventBus.get(event_id)) {
            try {
                Object res = ph.handler.apply(args);
                if (res != null) {
                    result.add(res);
                }
            } catch (Exception e) {
                // 基本错误处理
                System.err.println("Error in event handler: " + e.getMessage());
            }
        }
        return result;
    }

    // 简单的事件数据类 - 使用Map存储属性
    public static class EventData {

        private final Map<String, Object> data = new HashMap<>();

        public EventData put(String key, Object value) {
            data.put(key, value);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> T get(String key) {
            return (T) data.get(key);
        }

        public <T> T getOr(String key, T defaultValue) {
            Object value = data.get(key);
            return value != null ? (T) value : defaultValue;
        }

        public boolean has(String key) {
            return data.containsKey(key);
        }
    }

    public static ArrayList<Object> fire(String event_id, EventData eventData) {
        return collect(event_id, eventData);
    }

    // 便捷方法：创建事件数据并触发事件
    public static ArrayList<Object> fire(String event_id, Object... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Must provide key-value pairs");
        }

        EventData data = new EventData();
        for (int i = 0; i < keyValues.length; i += 2) {
            if (!(keyValues[i] instanceof String)) {
                throw new IllegalArgumentException("Keys must be strings");
            }
            data.put((String) keyValues[i], keyValues[i + 1]);
        }

        return fire(event_id, data);
    }
}
