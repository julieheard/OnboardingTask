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
import org.kohsuke.stapler.DataBoundSetter;
import java.io.IOException;

public class OnboardingTaskBuilder extends Builder implements SimpleBuildStep {

    private Category chosenCategory;

    @DataBoundConstructor
    public OnboardingTaskBuilder(Category chosenCategory){
        this.chosenCategory = chosenCategory;
    }

    public Category getChosenCategory(){
        return chosenCategory;
    }

    @DataBoundSetter
    public void setChosenCategory(Category chosenCategory){
        this.chosenCategory = chosenCategory;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        //Would put run.addAction(new OnboardingTaskACTION (params)); here when ACTION class is made
        listener.getLogger().println("Chosen Category is ...");
    }


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
            //You will see this as part of the build step
            return "Onboarding Task";
        }

        //This populates the dropdown box on the build step with category objects from the OnboardingTask Global config page
        public ListBoxModel doFillCategoryItems() {
            ListBoxModel listBox = new ListBoxModel();

            listBox.add("TestItem", "TestItem");
            for (Category item : ExtensionList.lookupSingleton(OnboardingTask.class).getCategories()) {
                listBox.add(item.getName(), item.getName());
            }
            return listBox;
        }

    }

}
