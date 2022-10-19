package io.jenkins.plugins.OnboardingTask;

import java.util.UUID;

import hudson.Extension;
import hudson.ExtensionList;
import org.kohsuke.stapler.DataBoundConstructor;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;


public class Category extends AbstractDescribableImpl<Category> {

    private UUID uuid;
    private String categoryName;

    @DataBoundConstructor
    public Category(String categoryName, UUID uuid) {
        this.categoryName = categoryName;
        this.uuid = java.util.UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCategoryName() {
        return categoryName;
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
