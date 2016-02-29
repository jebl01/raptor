package se.jebl01.raptor;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.jebl01.raptor.fragments.FieldFragment;
import se.jebl01.raptor.fragments.MethodFragment;
import se.jebl01.raptor.fragments.TemplateFragment;
import se.jebl01.raptor.fragments.TemplateFragmentProvider;
import se.jebl01.raptor.loader_states.BuildingState;
import se.jebl01.raptor.loader_states.BuildingStringFragmentState;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TemplateLoader {
    private static final Logger log = LoggerFactory.getLogger(MethodFragment.class);

    public static <T> Template<T> load(final String template, final Class<T> clazz) {
        return new Template<>(template, processTemplate(loadTemplateFile(clazz, template), getFragmentsProviders(clazz), clazz));
    }

    private static <T> List<TemplateFragment<T>> processTemplate(final String source, final Map<String, TemplateFragment<T>> reflectiveFragments, final Class<T> clazz) {
        final List<TemplateFragment<T>> fragments = new ArrayList<>();
        final char[] chars = source.toCharArray();
        BuildingState buildingState = new BuildingStringFragmentState<T>(chars, 0, fragments::add, reflectiveFragments, false);

        for (char c : chars) {
            buildingState = buildingState.gotChar(c);
        }
        buildingState.flush();

        log.debug("creating {} template", clazz.getSimpleName());
        log.debug("fragments:");
        fragments.forEach(fragment -> log.debug("\t{}", fragment));

        return fragments;
    }

    private static <T> Map<String, TemplateFragment<T>> getFragmentsProviders(final Class<T> clazz) {
        final Map<String, TemplateFragment<T>> fragments = new HashMap<>();
        fragments.putAll(findFields(clazz, new HashMap<>()));
        fragments.putAll(findMethods(clazz, new HashMap<>()));
        return fragments;
    }

    private static <T> Map<String, TemplateFragment<T>> findMethods(final Class<?> clazz, Map<String, TemplateFragment<T>> fragments) {
        if (clazz != null) {
            for (final Method method : clazz.getMethods()) {
                if (method.isAnnotationPresent(TemplateFragmentProvider.class)) {
                    final TemplateFragmentProvider fragmentAnnotation = method.getAnnotation(TemplateFragmentProvider.class);
                    String fragmentName = fragmentAnnotation.value();
                    String delimiter = fragmentAnnotation.delimiter();
                    fragments.put(fragmentName, new MethodFragment<T>(fragmentName, delimiter, method));
                }
            }
            return findMethods(clazz.getSuperclass(), fragments);
        }
        return fragments;
    }

    private static <T> Map<String, TemplateFragment<T>> findFields(final Class<?> clazz, Map<String, TemplateFragment<T>> fragments) {
        if (clazz != null) {
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(TemplateFragmentProvider.class)) {
                    final TemplateFragmentProvider fragmentAnnotation = field.getAnnotation(TemplateFragmentProvider.class);
                    String fragmentName = fragmentAnnotation.value();
                    String delimiter = fragmentAnnotation.delimiter();
                    fragments.put(fragmentName, new FieldFragment<T>(fragmentName, delimiter, field));
                }
            }
            return findFields(clazz.getSuperclass(), fragments);
        }
        return fragments;
    }

    private static String loadTemplateFile(final Class<?> fromPackage, final String name) {
        final String path = fromPackage.getPackage().getName().replace('.', '/') + '/';
        final String filePath = path + name;
        final InputStream is = TemplateLoader.class.getClassLoader().getResourceAsStream(filePath);
        if (is == null) {
            throw new IllegalArgumentException("Unable to find fragments: " + filePath);
        }
        try {
            return IOUtils.toString(is, "UTF-8");
        } catch (final IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}