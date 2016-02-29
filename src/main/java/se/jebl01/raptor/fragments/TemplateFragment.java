package se.jebl01.raptor.fragments;

/*
 * Stub implementation for reflection based fragments reflectiveFragments. Subclasses need only implement the actual
 * reflection call in getValue(source).
 */
public interface TemplateFragment<T> {
    String getFragment(final T source);
}