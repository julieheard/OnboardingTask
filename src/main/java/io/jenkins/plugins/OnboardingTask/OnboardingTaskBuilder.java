package io.jenkins.plugins.OnboardingTask;

import hudson.*;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

public class OnboardingTaskBuilder extends Builder implements SimpleBuildStep {

    private String category;

    @DataBoundConstructor
    public OnboardingTaskBuilder(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        //Would put run.addAction(new OnboardingTaskACTION (params)); here when ACTION class is made
        listener.getLogger().println("Chosen Category is ..." + this.getCategory());
    }


    //This 'symbol' shows up in the pipeline step as the step name.
    //Also makes the generated pipeline step script more compact
    @Symbol("Onboarding Task Plugin")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override //name for this instance
        public String getDisplayName(){
            //this is the text for the build step
            return "Category Selector";
        }

        //This populates the dropdown box on the build step with category objects from the OnboardingTask Global config page
        public ListBoxModel doFillCategoryItems() {
            ListBoxModel listBox = new ListBoxModel();

            for (Category item : ExtensionList.lookupSingleton(OnboardingTask.class).getCategories()) {
                listBox.add(item.getName(), item.getName());
            }
            return listBox;
        }

    }

}
