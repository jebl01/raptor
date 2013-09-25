package se.jebl01.raptor.fragments;



public final class StringFragment<S> implements TemplateFragment<S>
{
    private final String value;

    public StringFragment(final String value)
    {
        this.value = value;
    }

    @Override
    public String getFragment(final S source)
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}