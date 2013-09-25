package se.jebl01.raptor.fragments;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.Test;

public class QueryFragmentTest
{
    @Test
    public void canGetStringFragment()
    {
        final String expected = "foo";
        final TemplateFragment<Object> fragment = new StringFragment<Object>(expected);
        
        assertEquals(expected, fragment.getFragment(new Object()));
    }

    @Test
    public void canGetFieldReflectionFragment() throws SecurityException, NoSuchFieldException, IllegalArgumentException
    {
        final FragmentTest test = new FragmentTest();
        final Field field = test.getClass().getDeclaredField("field");
        final TemplateFragment<FragmentTest> fragment = new FieldFragment<FragmentTest>("fieldFragmentName", field);
        assertEquals(test.field, fragment.getFragment(test));
    }

    @Test
    public void canGetMethodReflectionFragment() throws SecurityException, IllegalArgumentException, NoSuchMethodException
    {
        final FragmentTest test = new FragmentTest();
        final Method method = test.getClass().getDeclaredMethod("method", new Class[0]);
        final TemplateFragment<FragmentTest> fragment = new MethodFragment<FragmentTest>("methodFragmentName", method);
        assertEquals(test.method(), fragment.getFragment(test));
    }
    
    public static final class FragmentTest
    {
      @TemplateFragmentProvider(fragmentName="fieldFragmentName")
      private final String field = "testfield";
      
      @TemplateFragmentProvider(fragmentName="methodFragmentName")
      public String method(){
        return "testmethod";
      }
    }
}