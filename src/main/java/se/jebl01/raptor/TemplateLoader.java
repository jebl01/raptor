package se.jebl01.raptor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;

import se.jebl01.raptor.fragments.FieldFragment;
import se.jebl01.raptor.fragments.MethodFragment;
import se.jebl01.raptor.fragments.StringFragment;
import se.jebl01.raptor.fragments.TemplateFragment;
import se.jebl01.raptor.fragments.TemplateFragmentProvider;

public final class TemplateLoader
{
    private final static String TOKEN_DELIMITER = "%";

    public static <T> Template<T> load(final String template, final Class<T> clazz)
    {
        return new Template<T>(template, processTemplate(loadTemplateFile(clazz, template), getFragmentsProviders(clazz)));
    }

    private static <T> List<TemplateFragment<T>> processTemplate(final String xml, final Map<String, TemplateFragment<T>> fragments)
    {
        final StringTokenizer tokenizer = new StringTokenizer(xml, TOKEN_DELIMITER, true);
        final List<TemplateFragment<T>> template = new ArrayList<TemplateFragment<T>>();
        String token;
        while(tokenizer.hasMoreTokens())
        {
            token = tokenizer.nextToken();
            if(TOKEN_DELIMITER.equals(token))
            {
                token = tokenizer.nextToken();
                if(fragments.containsKey(token))
                {
                    template.add(fragments.get(token));
                }
                // Discard next token delimiter
                tokenizer.nextToken();
            }
            else
            {
                template.add(new StringFragment<T>(token));
            }
        }
        return template;
    }

    private static <T> Map<String, TemplateFragment<T>> getFragmentsProviders(final Class<T> clazz)
    {
        final Map<String, TemplateFragment<T>> fragments = new HashMap<String, TemplateFragment<T>>();
        fragments.putAll(findFields(clazz));
        fragments.putAll(findMethods(clazz));
        return fragments;
    }

    private static <T> Map<String, TemplateFragment<T>> findMethods(final Class<T> clazz)
    {
        final Map<String, TemplateFragment<T>> fragments = new HashMap<String, TemplateFragment<T>>();
        for(final Method method : clazz.getMethods())
        {
            if(method.isAnnotationPresent(TemplateFragmentProvider.class))
            {
                final TemplateFragmentProvider fragmentAnnotation = method.getAnnotation(TemplateFragmentProvider.class);
                String fragmentName = fragmentAnnotation.fragmentName();
                fragments.put(fragmentName, new MethodFragment<T>(fragmentName, method));
            }
        }
        return fragments;
    }

    private static <T> Map<String, TemplateFragment<T>> findFields(final Class<T> clazz)
    {
        final Map<String, TemplateFragment<T>> fragments = new HashMap<String, TemplateFragment<T>>();
        for(final Field field : clazz.getDeclaredFields())
        {
            if(field.isAnnotationPresent(TemplateFragmentProvider.class))
            {
                final TemplateFragmentProvider fragmentAnnotation = field.getAnnotation(TemplateFragmentProvider.class);
                String fragmentName = fragmentAnnotation.fragmentName();
                fragments.put(fragmentName, new FieldFragment<T>(fragmentName, field));
            }
        }
        return fragments;
    }

    private static String loadTemplateFile(final Class<?> fromPackage, final String name)
    {
        final String path = fromPackage.getPackage().getName().replace('.', '/') + '/';
        String filePath = path + name;
        final InputStream is = TemplateLoader.class.getClassLoader().getResourceAsStream(filePath);
        if(is == null)
        {
            throw new IllegalArgumentException("Unable to find template: " + filePath);
        }
        try
        {
          
            return IOUtils.toString(is, "UTF-8");
        }
        catch(final IOException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}