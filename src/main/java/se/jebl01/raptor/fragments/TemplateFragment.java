package se.jebl01.raptor.fragments;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/*
 * Stub implementation for reflection based template fragments. Subclasses need only implement the actual
 * reflection call in getValue(source).
 */
public interface TemplateFragment<S>
{
    public abstract String getFragment(final S source);
    
    public static abstract class ReflectionTemplateFragment<S> implements TemplateFragment<S>
    {
        private static final Logger log = LoggerFactory.getLogger(MethodFragment.class);
        
        final String fragmentName;

        protected ReflectionTemplateFragment(String fragmentName)
        {
            this.fragmentName = fragmentName;
        }
        
        protected abstract Object getValue(S source) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException;
        
        @Override
        public String getFragment(final S source)
        {
            try
            {
                final Object object = getValue(source);
                String fragment = "";
                
                if(object == null)
                {
                    log.info("Null value returned for: " + source + " in " + getClass().getSimpleName() + " and " + this);
                }
                
                else if(Optional.class.isAssignableFrom(object.getClass()))
                {
                    final Optional<?> optional = (Optional<?>)object;
                    if(optional.isPresent()) fragment = optional.get().toString();
                }
                else
                {
                    fragment = object.toString();
                }
                
                return fragment;
            }
            catch(final Exception e)
            {
                log.warn("Failed to get value", e);
                return "";
            }
        }
    }
}