package io.jenkins.plugins.sample;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * I have taken the global configuration plugin archetype and built on top of it
 */
@Extension
public class SampleConfiguration extends GlobalConfiguration {

    /** @return the singleton instance */
    public static SampleConfiguration get() {
        return ExtensionList.lookupSingleton(SampleConfiguration.class);
    }

    private String name;
    private String description;

    public SampleConfiguration() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    /** @return the currently configured label, if any */
    public String getName() {
        return name;
    }
    public String getDescription(){ return description; }

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

    public FormValidation doCheckName(@QueryParameter String value) {

        //Empty name check
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning("Please specify a name.");
        }else{
            //Check format of name (letters and hyphens only)
            if(!nameFormatCheck(value)){
                return FormValidation.warning("Please enter a valid name (letters and hyphens only).");
            }
        }
        return FormValidation.ok();
    }

    private boolean nameFormatCheck(String name){
        Pattern pattern = Pattern.compile("^[A-Za-z\\s-]+$");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

}
