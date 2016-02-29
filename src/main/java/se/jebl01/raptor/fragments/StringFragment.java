package se.jebl01.raptor.fragments;


public final class StringFragment<T> implements TemplateFragment<T> {
    private final String value;

    public StringFragment(String value, final boolean eraseEscapeChars) {
        if(eraseEscapeChars) {
            value = value.replace("\\}", "}");
        }
        this.value = value;
    }

    public String getFragment(final T source) {
        return this.value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " length: " + this.value.length();
    }
}