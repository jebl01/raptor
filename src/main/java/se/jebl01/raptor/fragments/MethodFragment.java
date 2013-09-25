package se.jebl01.raptor.fragments;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import se.jebl01.raptor.fragments.TemplateFragment.ReflectionTemplateFragment;

public final class MethodFragment<S> extends ReflectionTemplateFragment<S>
{
    private final Method method;

    public MethodFragment(String fragmentName, final Method method)
    {
        super(fragmentName);
        this.method = method;
    }

    @Override
    public Object getValue(final S source) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        this.method.setAccessible(true);
        return this.method.invoke(source, new Object[0]);
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + ":" + this.method.getName();
    }
}