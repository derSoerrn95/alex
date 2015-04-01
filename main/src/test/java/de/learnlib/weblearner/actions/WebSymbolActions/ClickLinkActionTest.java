package de.learnlib.weblearner.actions.WebSymbolActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.learnlib.weblearner.core.entities.ExecuteResult;
import de.learnlib.weblearner.core.entities.Project;
import de.learnlib.weblearner.core.learner.connectors.WebSiteConnector;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static de.learnlib.weblearner.core.entities.ExecuteResult.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClickLinkActionTest {

    private static final Long PROJECT_ID = 42L;

    private ClickLinkAction c;

    @Before
    public void setUp() {
        c = new ClickLinkAction();
        c.setProject(new Project(PROJECT_ID));
        c.setValue("Click Me");
    }

    @Test
    public void testJSON() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(c);
        ClickLinkAction c2 = mapper.readValue(json, ClickLinkAction.class);

        assertEquals(c.getValue(), c2.getValue());
    }

    @Test
    public void testJSONFile() throws IOException, URISyntaxException {
        ObjectMapper mapper = new ObjectMapper();

        File file = new File(getClass().getResource("/actions/websymbolactions/ClickLinkTestData.json").toURI());
        WebSymbolAction obj = mapper.readValue(file, WebSymbolAction.class);

        assertTrue(obj instanceof ClickLinkAction);
        ClickLinkAction objAsAction = (ClickLinkAction) obj;
        assertEquals("Click Me", objAsAction.getValue());
    }

    @Test
    public void shouldReturnOKIfLinkCouldBeClicked() {
        WebSiteConnector connector = mock(WebSiteConnector.class);
        WebElement element = mock(WebElement.class);
        given(connector.getLinkByText("Click Me")).willReturn(element);

        assertEquals(OK, c.execute(connector));
        verify(element).click();
    }

    @Test
    public void shouldReturnFailedIfLinkCouldNotBeClicked() {
        WebSiteConnector connector = mock(WebSiteConnector.class);
        when(connector.getLinkByText("Click Me")).thenThrow(new NoSuchElementException(""));

        assertEquals(ExecuteResult.FAILED, c.execute(connector));
    }

}
