package com.coladungeon.utils;

public class Tuple2<A, B> {
    private final A first;
    private final B second;

    public Tuple2(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A first() {
        return first;
    }

    public B second() {
        return second;
    }

    public Tuple2<A, B> first(A first) {
        return new Tuple2<>(first, this.second);
    }

    public Tuple2<A, B> second(B second) {
        return new Tuple2<>(this.first, second);
    }
}
