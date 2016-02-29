package se.jebl01.raptor.fragments;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MethodFragment<T> extends ReflectionTemplateFragment<T> {
    private final Method method;

    public MethodFragment(String fragmentName, String delimiter, final Method method) {
        super(fragmentName, delimiter);
        this.method = method;
    }

    @Override
    public Object getValue(final T source) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        this.method.setAccessible(true);
        return this.method.invoke(source);
    }
}