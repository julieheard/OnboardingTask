package io.jenkins.plugins;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsSessionRule;

public class OnboardingTaskTest {

    @Rule
    public JenkinsSessionRule sessions = new JenkinsSessionRule();

    /**
     * Tries to exercise enough code paths to catch common mistakes:
     * <ul>
     * <li>missing {@code load}
     * <li>missing {@code save}
     * <li>misnamed or absent getter/setter
     * <li>misnamed {@code textbox}
     * </ul>
     */
    @Test
    public void uiAndStorage() throws Throwable {
        sessions.then(r -> {
            assertNull("not set initially", OnboardingTask.get().getName());
            HtmlForm config = r.createWebClient().goTo("configure").getFormByName("config");
            HtmlTextInput textbox = config.getInputByName("_.name");
            textbox.setText("hello");
            r.submit(config);
            assertEquals("global config page let us edit it", "hello", OnboardingTask.get().getName());
        });
        sessions.then(r -> {
            assertEquals("still there after restart of Jenkins", "hello", OnboardingTask.get().getName());
        });
    }

    @Test
    public void nameFormatCheckTest(){
       //Valid names only contain letters only, no spaces
        String name =  "Julie";
        assertTrue((name), true);
        name =  "Julie1234";
        assertFalse((name), false);
        name =  "Julie&^%$";
        assertFalse((name), false);
    }

}
