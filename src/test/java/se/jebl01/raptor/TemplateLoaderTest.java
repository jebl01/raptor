package se.jebl01.raptor;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import se.jebl01.raptor.fragments.TemplateFragmentProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * Created by jesblo on 2016-02-29.
 */
public class TemplateLoaderTest {
    @Test
    public void canLoadTemplateAndFormat() throws IOException {
        String expected = loadTemplateFile(getClass(), "expected.html");
        HtmlPage htmlPage = new HtmlPage("HTML", "The Header", "A paragraph", Arrays.asList("test1", "test2", "test3"));

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

        @TemplateFragmentProvider("EMPTY")
        private final Optional<String> empty = Optional.empty();

        @TemplateFragmentProvider("NOT_EMPTY")
        private final Optional<String> not_empty = Optional.of("something");

        private final String paragraph;

        @TemplateFragmentProvider(value = "ITEMS", delimiter = "\n\t\t\t")
        private final Collection<String> items;

        @TemplateFragmentProvider(value = "NUMBERS", delimiter = ", ")
        private final Collection<Integer> numbers = Arrays.asList(1,2,3,4,5,6);

        public HtmlPage(String docType, String header, String paragraph, Collection<String> items){
            super(docType);

            this.header = header;
            this.paragraph = paragraph;
            this.items = items;
        }

        @TemplateFragmentProvider("PARAGRAPH")
        public String getParagraph(){
            return this.paragraph;
        }
    }

    public static String loadTemplateFile(final Class<?> fromPackage, final String name)
    {
        final String path = fromPackage.getPackage().getName().replace('.', '/') + '/';
        String filePath = path + name;
        final InputStream is = TemplateLoader.class.getClassLoader().getResourceAsStream(filePath);
        if(is == null)
        {
            throw new IllegalArgumentException("Unable to find fragments: " + filePath);
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
