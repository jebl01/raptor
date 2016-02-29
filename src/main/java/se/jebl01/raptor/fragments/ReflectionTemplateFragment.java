package se.jebl01.raptor.fragments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class ReflectionTemplateFragment<T> implements TemplateFragment<T> {
    private static final Logger log = LoggerFactory.getLogger(MethodFragment.class);

    final String fragmentName;
    final String delimiter;

    protected ReflectionTemplateFragment(String fragmentName, String delimiter) {
        this.fragmentName = fragmentName;
        this.delimiter = delimiter;
    }

    protected abstract Object getValue(T source) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;

    String getFragmentFromObject(final Object object, final T source) {
        String fragment = "";

        if (object == null) {
            log.info("Null value returned for: " + source + " in " + getClass().getSimpleName() + " and " + this);
        } else if (Optional.class.isAssignableFrom(object.getClass())) {
            final Optional<?> optional = (Optional<?>) object;
            if (optional.isPresent()) fragment = optional.get().toString();
        } else if (Iterable.class.isAssignableFrom(object.getClass())) {
            final Iterable<?> iterable = (Iterable<?>) object;
            fragment = StreamSupport.stream(iterable.spliterator(), false)
                .map(Object::toString)
                .collect(Collectors.joining(delimiter));
        } else {
            fragment = object.toString();
        }

        return fragment;
    }

    @Override
    public String getFragment(final T source) {
        try {
            final Object object = getValue(source);
            return getFragmentFromObject(object, source);
        } catch (final Exception e) {
            log.warn("Failed to get value", e);
            return "";
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " name: " + this.fragmentName;
    }
}
