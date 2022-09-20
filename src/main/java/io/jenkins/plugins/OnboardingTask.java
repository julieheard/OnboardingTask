package io.jenkins.plugins;

import hudson.Extension;

import hudson.ExtensionList;
import hudson.model.Item;
import hudson.model.Job;
import hudson.util.FormFieldValidator;
import hudson.util.FormValidation;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.*;
import org.kohsuke.stapler.verb.POST;

import javax.servlet.ServletException;
import java.io.IOException;
import java.security.Security;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * I have taken the global configuration plugin archetype and built on top of it
 */
@Extension
public class OnboardingTask extends GlobalConfiguration {

    /** @return the singleton instance */
    public static OnboardingTask get() {
        return ExtensionList.lookupSingleton(OnboardingTask.class);
    }

    private String name;
    private String description;
    private String url;
    private String username;
    private Secret password;

    public OnboardingTask() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    @POST
    public FormValidation doTestConnection(@QueryParameter("username") final String username,
                                           @QueryParameter("password") final String password,
                                           @AncestorInPath Job job) throws IOException, ServletException {
        try {
            if (job == null) {
                Jenkins.get().checkPermission(Jenkins.ADMINISTER);
            } else {
                job.checkPermission(Item.CONFIGURE);
            }

            return FormValidation.ok("Success");
        } catch (Exception e) {
            return FormValidation.error("Client error : "+e.getMessage());
        }
    }

    public FormValidation doCheckUsername(@QueryParameter String username) {
        //Valid username is letters only
        if (!nameFormatCheck(username)){
                return FormValidation.warning("Please enter a valid username (letters only).");
        }
        return FormValidation.ok();
    }


    /**
     * Checks if a name is a valid format.
     * A valid name contains only letters, no numbers or symbols.
     * @param name, this is entered by the user
     * @return true if the name is valid
     */
    private boolean nameFormatCheck(String name){
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }


    @DataBoundSetter
    public void setName(String name) {
        if(name.length()!=0 && nameFormatCheck(name)) {
            this.name = name;
            save();
        }
    }

    @DataBoundSetter
    public void setDescription(String description) {
        this.description = description;
        save();
    }

    @DataBoundSetter
    public void setURL(String url) {
        this.url = url;
        save();
    }

    @DataBoundSetter
    public void setUsername(String username) {
        //Username can only contain letters
        if(nameFormatCheck(username)) {
            this.username = username;
            save();
        }
    }

    @DataBoundSetter
    public void setPassword(Secret password) {
        this.password = password;
        save();
    }

    public String getName() { return name; }
    public String getDescription(){ return description; }
    public String getURL() { return url; }
    public String getUsername() { return username; }
    public Secret getPassword() { return password; }

}
