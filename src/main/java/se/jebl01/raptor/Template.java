package se.jebl01.raptor;

import java.io.IOException;
import java.util.List;

import se.jebl01.raptor.fragments.TemplateFragment;

public final class Template<S>
{
    private final List<TemplateFragment<S>> fragments;
    private final String name;
    
    /**
     * Construct a new {@link Template} with the given name and the give fragments.
     * 
     * @param name The name of the template
     * @param fragments The fragments
     */
    public Template(final String name, final List<TemplateFragment<S>> fragments)
    {
        this.name = name;
        this.fragments = fragments;
    }

    /**
     * Format this {@link Template} with the given source to the supplied {@link Appendable}.
     *   
     * @param source The source in where the values reside.
     * @param appendable The {@link Appendable} whom to format to
     * @throws IOException
     */
    public void formatTo(final S source, final Appendable appendable) throws IOException
    {
        for(final TemplateFragment<S> fragment : this.fragments)
        {
            appendable.append(fragment.getFragment(source));
        }
    }

    public String getName()
    {
        return this.name;
    }
}