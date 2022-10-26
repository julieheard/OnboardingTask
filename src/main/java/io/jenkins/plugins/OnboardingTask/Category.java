package io.jenkins.plugins.OnboardingTask;

import java.util.UUID;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

public class Category extends AbstractDescribableImpl<Category> {

    private String name;
    private UUID uuid;

    @DataBoundConstructor
    public Category(String name, String uuid) {
        this.name = name;
        this.uuid = java.util.UUID.randomUUID();
    }

    public String getUuid() {
        return String.valueOf(uuid);
    }

    public String getName() {
        return name;
    }

    @DataBoundSetter
    public void setName(String name){
        this.name = name;
    }

    @Override
    public Descriptor<Category> getDescriptor() {
        return ExtensionList.lookupSingleton(DescriptorImpl.class);
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<Category> {
        //Things like doCheckName would be in here
    }


}
