package se.jebl01.raptor.fragments;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CompanionatedTemplateFragment<T> implements TemplateFragment<T> {
    private static final Logger log = LoggerFactory.getLogger(CompanionatedTemplateFragment.class);

    private final StringFragment<T> leftStringFragment;
    private final ReflectionTemplateFragment<T> reflectiveFragment;
    private final StringFragment<T> rightStringFragment;

    public CompanionatedTemplateFragment(final StringFragment<T> leftStringFragment, final ReflectionTemplateFragment<T> reflectiveFragment, final StringFragment<T> rightStringFragment) {
        Objects.requireNonNull(leftStringFragment);
        Objects.requireNonNull(reflectiveFragment);
        Objects.requireNonNull(rightStringFragment);

        this.leftStringFragment = leftStringFragment;
        this.reflectiveFragment = reflectiveFragment;
        this.rightStringFragment = rightStringFragment;
    }

    public String getFragment(final T source) {
        try {
            final Object value = reflectiveFragment.getValue(source);

            if (Iterable.class.isAssignableFrom(value.getClass())) {
                final Iterable<?> iterable = (Iterable<?>) value;

                return StreamSupport.stream(iterable.spliterator(), false)
                    .map(v -> new StringBuilder()
                        .append(leftStringFragment.getFragment(source))
                        .append(v.toString())
                        .append(rightStringFragment.getFragment(source)).toString())
                    .collect(Collectors.joining(reflectiveFragment.delimiter));
            } else if (Optional.class.isAssignableFrom(value.getClass())) {
                final Optional<?> optionalValue = (Optional<?>) value;

                return optionalValue.map(v -> new StringBuilder()
                    .append(leftStringFragment.getFragment(source))
                    .append(v.toString())
                    .append(rightStringFragment.getFragment(source)).toString()).orElse("");
            } else {
                return new StringBuilder()
                    .append(leftStringFragment.getFragment(source))
                    .append(value.toString())
                    .append(rightStringFragment.getFragment(source)).toString();
            }

        } catch (Exception e) {
            log.warn("Failed to get value", e);
            return "";
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + leftStringFragment + ", " + reflectiveFragment + ", " + rightStringFragment + "]"
;    }
}
