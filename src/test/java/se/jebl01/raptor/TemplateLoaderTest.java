package se.jebl01.raptor;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import se.jebl01.raptor.fragments.TemplateFragmentProvider;

public class TemplateLoaderTest {
  
  @Test
  public void canLoadTemplateAndFormat() throws IOException{
    String expected = loadTemplateFile(getClass(), "expected.html");
    HtmlPage htmlPage = new HtmlPage("The Header", "A paragraph");
    
    Template<HtmlPage> template = TemplateLoader.load("template.html", HtmlPage.class);
    
    StringBuilder sb = new StringBuilder();
    template.formatTo(htmlPage, sb);
    
    assertEquals(expected, sb.toString());
  }
  
  public static class HtmlPage{
    
    @TemplateFragmentProvider(fragmentName="HEADING")
    private final String header;
    
    private final String paragraph;
    
    public HtmlPage(String header, String paragraph){
      this.header = header;
      this.paragraph = paragraph;
    }
    
    @TemplateFragmentProvider(fragmentName="PARAGRAPH")
    public String getParagraph(){
      return this.paragraph;
    }
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
