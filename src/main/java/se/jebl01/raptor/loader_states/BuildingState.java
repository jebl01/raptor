package se.jebl01.raptor.loader_states;

import se.jebl01.raptor.fragments.TemplateFragment;

import java.util.Map;
import java.util.function.Consumer;


public abstract class BuildingState<T> {
    public static final String START_TOKEN = "$«"; //note length must be = 2
    public static final char STOP_TOKEN = '»';

    protected int length = 0;
    protected final char[] text;
    protected final int offset;
    protected char previous = '_';
    protected final Consumer<TemplateFragment<T>> callback;
    protected final Map<String, TemplateFragment<T>> reflectiveFragments;

    public BuildingState(char[] text, int offset, Consumer<TemplateFragment<T>> callback, Map<String, TemplateFragment<T>> reflectiveFragments) {
        this.text = text;
        this.offset = offset;
        this.callback = callback;
        this.reflectiveFragments = reflectiveFragments;
    }

    public abstract BuildingState gotChar(char c);

    public void flush() {};

    protected boolean isStartToken(char c) {
        return new String(new char[]{previous, c}).equals(START_TOKEN);
    }
}
