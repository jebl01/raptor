package se.jebl01.raptor.loader_states;

import se.jebl01.raptor.fragments.CompanionatedTemplateFragment;
import se.jebl01.raptor.fragments.ReflectionTemplateFragment;
import se.jebl01.raptor.fragments.StringFragment;
import se.jebl01.raptor.fragments.TemplateFragment;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by jesblo on 2016-02-29.
 */
public class BuildingCompanionatedFragmentState<T> extends BuildingState<T> {
    private BuildingState innerBuildingState;

    protected StringFragment<T> leftStringFragment;
    protected ReflectionTemplateFragment<T> reflectiveFragment;
    protected StringFragment<T> rightStringFragment;

    public BuildingCompanionatedFragmentState(final char[] text, final int offset, final Consumer<TemplateFragment<T>> callback, final Map<String, TemplateFragment<T>> reflectiveFragments, StringFragment<T> stringFragment) {
        super(text, offset, callback, reflectiveFragments);

        this.leftStringFragment = stringFragment;

        innerBuildingState = new BuildingFragmentState<>(text, offset, this::fragmentCallback, reflectiveFragments, true);
    }

    private void fragmentCallback(TemplateFragment<T> fragment) {
        if (StringFragment.class.isAssignableFrom(fragment.getClass())) {
            //TODO: assert this.rightStringFragment is null
            this.rightStringFragment = (StringFragment<T>) fragment;
        } else if (ReflectionTemplateFragment.class.isAssignableFrom(fragment.getClass())) {
            //TODO: assert this.reflectiveFragment is null
            this.reflectiveFragment = (ReflectionTemplateFragment<T>) fragment;
        }
    }

    @Override
    public BuildingState gotChar(final char c) {

        if (c == STOP_TOKEN && previous != '\\' && innerBuildingState.getClass().isAssignableFrom(BuildingStringFragmentState.class)) {
            innerBuildingState.flush();
            callback.accept(new CompanionatedTemplateFragment<>(leftStringFragment, reflectiveFragment, rightStringFragment));
            return new BuildingStringFragmentState<>(text, offset + length + 1, callback, reflectiveFragments, false);
        }

        innerBuildingState = innerBuildingState.gotChar(c);

        if (innerBuildingState.getClass().isAssignableFrom(BuildingCompanionatedFragmentState.class)) {
            throw new RuntimeException("nested companion fragment not allowed!");
        }

        previous = c;
        length++;
        return this;
    }
}
