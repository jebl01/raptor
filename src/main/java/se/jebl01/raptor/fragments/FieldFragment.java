package se.jebl01.raptor.fragments;

import java.lang.reflect.Field;

import se.jebl01.raptor.fragments.TemplateFragment.ReflectionTemplateFragment;


public final class FieldFragment<S> extends ReflectionTemplateFragment<S>
{
    private Field field;

    public FieldFragment(String fragmentName, final Field field)
    {
        super(fragmentName);
        this.field = field;
    }

    @Override
    public Object getValue(final S source) throws IllegalArgumentException, IllegalAccessException
    {
        this.field.setAccessible(true);
        return this.field.get(source);
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + ":" + this.fragmentName;
    }
}
