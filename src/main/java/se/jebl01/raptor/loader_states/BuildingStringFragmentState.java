package se.jebl01.raptor.loader_states;

import se.jebl01.raptor.fragments.StringFragment;
import se.jebl01.raptor.fragments.TemplateFragment;

import java.util.Map;
import java.util.function.Consumer;

public class BuildingStringFragmentState<T> extends BuildingState<T> {
    private final boolean eraseEscapeChars;

    public BuildingStringFragmentState(final char[] text, final int offset, final Consumer<TemplateFragment<T>> callback, Map<String, TemplateFragment<T>> reflectiveFragments, boolean eraseEscapeChars) {
        super(text, offset, callback, reflectiveFragments);
        this.eraseEscapeChars = eraseEscapeChars;
    }

    public BuildingState gotChar(final char c) {
        if (isStartToken(c)) {
            callback.accept(new StringFragment<>(new String(text, offset, length - 1), eraseEscapeChars));
            return new BuildingFragmentState<>(text, offset + length + 1, callback, reflectiveFragments, false);
        }
        previous = c;
        length++;
        return this;
    }

    @Override
    public void flush() {
        if (length != 0) {
            callback.accept(new StringFragment<>(new String(text, offset, length), eraseEscapeChars));
        }
    }
}
