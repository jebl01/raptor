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
    HtmlPage htmlPage = new HtmlPage("HTML", "The Header", "A paragraph");
    
    Template<HtmlPage> template = TemplateLoader.load("template.html", HtmlPage.class);
    
    StringBuilder sb = new StringBuilder();
    template.formatTo(htmlPage, sb);
    
    assertEquals(expected, sb.toString());
  }
  
  public static class Html {    
    @TemplateFragmentProvider("DOCTYPE")
    private final String docType;
    
    public Html(String docType) {
      this.docType = docType;
    }
  }
  
  public static class HtmlPage extends Html {
    
    @TemplateFragmentProvider("HEADING")
    private final String header;
    
    private final String paragraph;
    
    public HtmlPage(String docType, String header, String paragraph){
      super(docType);
      
      this.header = header;
      this.paragraph = paragraph;
    }
    
    @TemplateFragmentProvider("PARAGRAPH")
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
