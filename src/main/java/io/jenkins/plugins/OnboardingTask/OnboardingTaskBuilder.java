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
import java.util.*;

//TODO: bugFix = when you select a cetegory from the build settings,
//               if you close and reopen it goes back to the first item in the categories list
//               If you see the output the correct category is saved despite the wrong one showing on refresh
public class OnboardingTaskBuilder extends Builder implements SimpleBuildStep {

    private Category category;

    @DataBoundConstructor
    public OnboardingTaskBuilder(String category) {
        List<Category> categories = ExtensionList.lookupSingleton(OnboardingTask.class).getCategories();
        for (Category c :categories) {
            if(c.getName().equals(category)){
                this.category = c;
                break;
            }
        }
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        ((DescriptorImpl) getDescriptor()).addBuild(run, category);
        ((DescriptorImpl) getDescriptor()).printBuildQueue(listener);
    }


    //This 'symbol' shows up in the pipeline step as the step name.
    //Also makes the generated pipeline step script more compact
    @Symbol("Onboarding Task Plugin")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

        private Queue<BuildAndCategory> buildQueue = new LinkedList<>();
        private HashMap<String, String> categoriesLatestJob = new HashMap<>();

        public void addBuild(Run<?, ?> run, Category category) {
            if (Objects.nonNull(buildQueue) && buildQueue.size() >= 5) {
                buildQueue.remove();
            }
            buildQueue.add(new BuildAndCategory(run.getExternalizableId(), category));
            categoriesLatestJob.put(category.getName(), run.getParent().getFullName());
            ExtensionList.lookupSingleton(OnboardingTask.class).setLastJobRun(buildQueue.peek().buildId);
            save();
        }

        public Queue<BuildAndCategory> getBuildQueue() {
            return buildQueue;
        }
        public HashMap<String, String> getCategoriesLatestJob(){
            return categoriesLatestJob;
        }

        public String getBuildUrl(String id) {
            Run<?, ?> run = Run.fromExternalizableId(id);
            return run != null ? run.getUrl() : null;
        }

        public String getLastJobName(Category category) {
            return categoriesLatestJob.getOrDefault(category.getUuid().toString(), null);
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

        /**
         * This prints the last 5 items in the Queue for each of the categories selected in the job settings.
         * If the job has 3 categories selected in the job settings, it will run this 3 times.
         * @param listener allows us to print info to the console section of Jenkins
         */
        private void printBuildQueue(TaskListener listener) {
            listener.getLogger().println("Here is the detail for the last 5 jobs: ");

            Iterator<BuildAndCategory> iterator = buildQueue.iterator();
            while (iterator.hasNext()) {
                BuildAndCategory temp = iterator.next();
                listener.getLogger().println(
                        "\t Build ID : " + temp.buildId +
                                "\t Category name : " + temp.category.getName() +
                                "\t Category UUID : " + temp.category.getUuid()
                );
            }
        }
        
    }

    private static class BuildAndCategory {
        private String buildId;
        private Category category;

        public BuildAndCategory(String buildId, Category category) {
            this.buildId = buildId;
            this.category = category;
        }

        public String getBuildId() {
            return buildId;
        }

        public void setBuildId(String buildId) {
            this.buildId = buildId;
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }
    }

}
