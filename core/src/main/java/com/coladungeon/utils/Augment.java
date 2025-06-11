package com.coladungeon.utils;

import java.util.ArrayList;

public class Augment {

    public short priority;
    public Type type;
    public float value;
    public Pipe pipe = null;

    public static enum Type {
        add,
        mul,
        pipe
    }

    @FunctionalInterface
    public interface Pipe {

        float pipe(float value);
    }

    public static Augment Add(int v) {
        return new Augment(Type.add, v, null);
    }

    public static Augment Mul(int v) {
        return new Augment(Type.mul, v, null);
    }

    public static Augment Pipe(Pipe p) {
        return new Augment(Type.pipe, 0, p);
    }

    public Augment(Type t, int v) {
        this(t, v, null);
    }

    public Augment(Type t, int v, Pipe p) {
        this(t, v, p, (short) 0);
    }

    public Augment(Type t, int v, Pipe p, short priority) {
        this.type = t;
        this.priority = priority;
        this.value = v;
        this.pipe = p;
    }

    public static int process(ArrayList<Object> augments, int _dmg) {
        return augments.stream()
                .filter(r -> r instanceof Augment)
                .map(r -> (Augment) r)
                .sorted((a1, a2) -> Short.compare(a1.priority, a2.priority))
                .reduce(_dmg, (dmg, augment) -> {
                    switch (augment.type) {
                        case add -> {
                            return dmg + (int) augment.value;
                        }
                        case mul -> {
                            return (int) (dmg * augment.value);
                        }
                        case pipe -> {
                            return (int) augment.pipe.pipe(dmg);
                        }
                        default -> {
                            return dmg;
                        }
                    }
                }, (a, b) -> b);
    }

}
