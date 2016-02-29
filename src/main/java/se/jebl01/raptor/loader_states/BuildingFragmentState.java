package se.jebl01.raptor.loader_states;

import se.jebl01.raptor.fragments.StringFragment;
import se.jebl01.raptor.fragments.TemplateFragment;

import java.util.Map;
import java.util.function.Consumer;

public class BuildingFragmentState<T> extends BuildingState<T> {

    private final boolean eraseEscapeChars;

    public BuildingFragmentState(final char[] text, final int offset, final Consumer<TemplateFragment<T>> callback, Map<String, TemplateFragment<T>> reflectiveFragments, boolean eraseEscapeChars) {
        super(text, offset, callback, reflectiveFragments);
        this.eraseEscapeChars = eraseEscapeChars;
    }

    @Override
    public BuildingState gotChar(final char c) {
        if (c == STOP_TOKEN) {
            final String fragmentName = new String(text, offset, length);
            if (reflectiveFragments.containsKey(fragmentName)) {
                callback.accept(reflectiveFragments.get(fragmentName));
            }
            return new BuildingStringFragmentState<>(text, offset + length + 1, callback, reflectiveFragments, eraseEscapeChars);
        }

        if (isStartToken(c)) {
            final StringFragment<T> stringFragment = new StringFragment<T>(new String(text, offset, length - 1), false);
            return new BuildingCompanionatedFragmentState<>(text, offset + length + 1, callback, reflectiveFragments, stringFragment);
        }

        previous = c;
        length++;
        return this;
    }
}
