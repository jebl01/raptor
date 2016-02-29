package se.jebl01.raptor.fragments;

import java.lang.reflect.Field;


public final class FieldFragment<T> extends ReflectionTemplateFragment<T> {
    private Field field;

    public FieldFragment(String fragmentName, String delimiter, final Field field) {
        super(fragmentName, delimiter);
        this.field = field;
    }

    @Override
    public Object getValue(final T source) throws IllegalArgumentException, IllegalAccessException {
        this.field.setAccessible(true);
        return this.field.get(source);
    }
}
