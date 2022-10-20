package io.jenkins.plugins.OnboardingTask;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;

public class OnboardingTaskBuilder extends Builder implements SimpleBuildStep {

    private Category chosenCategory;

    @DataBoundConstructor
    public OnboardingTaskBuilder(Category category){
        this.chosenCategory = category;
    }

    public Category getChosenCategory(){
        return chosenCategory;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
            listener.getLogger().println("Chosen Category is ...");
    }


    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override //name for this instance
        public String getDisplayName(){
            //You will see this as part of the build step
            return "Onboarding Task";
        }



    }
}
