package se.jebl01.raptor;

import se.jebl01.raptor.fragments.TemplateFragment;

import java.io.IOException;
import java.util.List;

public final class Template<T> {
    private final List<TemplateFragment<T>> fragments;
    private final String name;

    /**
     * Construct a new {@link Template} with the given name and the give reflectiveFragments.
     *
     * @param name      The name of the fragments
     * @param fragments The reflectiveFragments
     */
    public Template(final String name, final List<TemplateFragment<T>> fragments) {
        this.name = name;
        this.fragments = fragments;
    }

    /**
     * Format this {@link Template} with the given source to the supplied {@link Appendable}.
     *
     * @param source     The source in where the values reside.
     * @param appendable The {@link Appendable} whom to format to
     * @throws IOException
     */
    public void formatTo(final T source, final Appendable appendable) throws IOException {
        for (final TemplateFragment<T> fragment : this.fragments) {
            appendable.append(fragment.getFragment(source));
        }
    }

    public String getName() {
        return this.name;
    }
}