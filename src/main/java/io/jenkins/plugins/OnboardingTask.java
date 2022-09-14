package io.jenkins.plugins;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.FormValidation;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

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


    public String getName() {
        return name;
    }
    public String getDescription(){ return description; }
    public String getURL() {
        return url;
    }
    public String getUsername() {
        return username;
    }
    public Secret getPassword() {
        return password;
    }

    /**
     * Together with {@link #getName}, binds to entry in {@code config.jelly}.
     * @param name the new value of this field
     */
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
        this.username = username;
        save();
    }

    @DataBoundSetter
    public void setPassword(Secret password) {
        this.password = password;
        save();
    }

    public FormValidation doCheckName(@QueryParameter String name) {

        //Empty name check
        if (StringUtils.isEmpty(name)) {
            return FormValidation.warning("Please specify a name.");
        }else{
            //Check format of name (letters and hyphens only)
            if(!nameFormatCheck(name)){
                return FormValidation.warning("Please enter a valid name (letters and hyphens only).");
            }
        }
        return FormValidation.ok();
    }

    public FormValidation doCheckUsername(@QueryParameter String username) {
        if (!nameFormatCheck(username)){
                return FormValidation.warning("Please enter a valid username (letters and hyphens only).");
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

}
