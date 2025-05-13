package com.cola.util;

import java.util.function.Function;

public class Either<A, B> {
    private final A left;
    private final B right;

    public Either(Object value, Class<A> leftClass, Class<B> rightClass) {
        if (leftClass.isInstance(value)) {
            this.left = leftClass.cast(value);
            this.right = null;
        } else if (rightClass.isInstance(value)) {
            this.left = null;
            this.right = rightClass.cast(value);
        } else {
            throw new IllegalArgumentException("Value does not match either type");
        }
    }

    public static <A, B> Either<A, B> left(A value, Class<A> leftClass, Class<B> rightClass) {
        return new Either<>(value, leftClass, rightClass);
    }

    public static <A, B> Either<A, B> right(B value, Class<A> leftClass, Class<B> rightClass) {
        return new Either<>(value, leftClass, rightClass);
    }

    public boolean isLeft() {
        return left != null;
    }

    public boolean isRight() {
        return right != null;
    }

    public A getLeft() {
        return left;
    }

    public B getRight() {
        return right;
    }

    public <C> C fold(Function<? super A, ? extends C> leftFunc, Function<? super B, ? extends C> rightFunc) {
        if (isLeft()) {
            return leftFunc.apply(left);
        } else {
            return rightFunc.apply(right);
        }
    }
}
