package io.jenkins.plugins;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import org.springframework.http.HttpHeaders;

import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Item;
import hudson.model.Job;
import hudson.util.FormValidation;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;

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
    private Credentials connectionCredentials;

    public OnboardingTask() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    @POST
    public FormValidation doTestConnection(@QueryParameter("username") String username,
                                           @QueryParameter("password") Secret password,
                                           @QueryParameter("url") String url) throws IOException  {
        try {
            if (!nameFormatCheck(username)){
                return FormValidation.warning("You need to enter a valid name. Letters only, no spaces.");
            }else {
                if (!checkForValidURL(url)) {
                    return FormValidation.warning("Your URL is not correct. Please enter a valid URL.");
                }
                else{
                    //You can use https://onboardingtask.free.beeceptor.com/ to test
                    URL newURL = new URL(url);
                    connectionCredentials = new Credentials(username, password, newURL);
                    save();

                    //This runs an HTTP request on this objects URL, username and password and returns an HTTP response code
                    int httpRequestResponseCode = sendHTTPRequest();

                    if (httpRequestResponseCode != 200) {
                        return FormValidation.warning(
                                "Connection refused, Http warning code: " + httpRequestResponseCode + "."
                                + " Please check your URL and credentials and try again");
                    }
                }
            }
            return FormValidation.ok("Success!");
        } catch (Exception e) {
            return FormValidation.error("Client error : " + e.getMessage());
        }
    }

    /**
     * This runs an HTTP request on this objects URL, username and password and returns an HTTP response code
     * @return the int returned represents the response code from the HTTP request
     */
    private int sendHTTPRequest() throws IOException {
        int responseCode = 0;
            HttpURLConnection httpURLConnection = (HttpURLConnection) connectionCredentials.getUrl().openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty(HttpHeaders.AUTHORIZATION, connectionCredentials.getFormattedCredentials());
            responseCode = httpURLConnection.getResponseCode();
        return responseCode;
    }

    /**
     * This tests if the URL is valid in that it should not generate any exceptions when used
     * @param url this is from the url input box
     * @return if it is a valid URL, this means the URL should not generate a URLMalformedException
     */
    private boolean checkForValidURL(String url){
        try {
            URL obj = new URL(url);
            obj.toURI();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    /**
     * This checks the username to make sure it only has letters only, no spaces or symbols
     * @param username this is the username typed into the username box
     * @return warning if not valid, ok if valid
     */
    public FormValidation doCheckUsername(@QueryParameter String username) {
        //Valid username is letters only
        if (!nameFormatCheck(username)) {
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
    public void connectionCredentials(Credentials credentials){
        if(credentials.getUsername().length() != 0 &&nameFormatCheck(credentials.getUsername())) {
            this.connectionCredentials = credentials;
            save();
        }
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

    public String getName() { return name; }
    public String getDescription(){ return description; }
    public Credentials getConnectionCredentials(){
        return this.connectionCredentials;
    }


}
