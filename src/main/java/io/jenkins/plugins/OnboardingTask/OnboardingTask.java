package io.jenkins.plugins.OnboardingTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;
import org.springframework.http.HttpHeaders;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.FormValidation;
import hudson.util.PersistedList;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;


/**
 * I have taken the global configuration plugin archetype and built on top of it
 */
@Extension
public class OnboardingTask extends GlobalConfiguration {

    private String temporaryPayload;
    private String name;
    private String description;
    private PersistedList<Category> categories = new PersistedList<Category>(this);


    public static OnboardingTask get() {
        return ExtensionList.lookupSingleton(OnboardingTask.class);
    }

    public OnboardingTask() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }


    /**
     * This method does exactly the same as doTestConnection() but also sends a payload.
     * There is a global temporaryPayload String which is set here and then erased when the payload is sent
     * in sendHTTPRequest()
     */
    @POST
    public FormValidation doTestConnectionWithPayload(@QueryParameter("username") String username,
                                                      @QueryParameter("password") Secret password,
                                                      @QueryParameter("url") String url) {
        temporaryPayload = password.getPlainText();
        return doTestConnection(username, password, url);
    }

    @POST
    public FormValidation doTestConnection(@QueryParameter("username") String username,
                                           @QueryParameter("password") Secret password,
                                           @QueryParameter("url") String url) {
        try {
            //I have grouped the validation calls and error message results into validateQueryParams
            String errorMessage = validateQueryParams(url, username);
            //If errors, return warning, otherwise safe to send HTTP request and return success
            if (errorMessage.length() != 0) {
                return FormValidation.warning(errorMessage);
            } else {

                //This runs an HTTP request on this objects URL, username and password and returns an HTTP response code
                int httpRequestResponseCode = sendHTTPRequest(new URL(url), username, password);

                if (httpRequestResponseCode != 200) {
                    return FormValidation.warning(
                            "Connection refused, Http warning code: " + httpRequestResponseCode + "."
                            + " Please check your URL and credentials and try again");
                }
            }
            return FormValidation.ok("Success!");
        } catch (Exception e) {
            return FormValidation.error("Client error : " + e.getMessage());
        }
    }

    /**
     * This runs an HTTP request on this objects URL, username and password and returns an HTTP response code
     *
     * @return the int returned represents the response code from the HTTP request
     */
    private int sendHTTPRequest(URL url, String username, Secret password) throws IOException {
        int responseCode = 0;
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty(HttpHeaders.AUTHORIZATION, getFormattedCredentials(username, password));

        //If the payload has data in it, add the payload to the HTTP connection
        try {
            if (temporaryPayload.length() != 0) {
                //This adds a payload to the HTTP request
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                os.write(temporaryPayload.getBytes(Charset.forName("UTF-8")));
                os.flush();
                os.close();
                temporaryPayload = ""; //Set payload back to empty
            }
        } catch (NullPointerException e) {
            //This means the String is empty so no payload needs to be added
        }

        responseCode = httpURLConnection.getResponseCode();
        return responseCode;
    }

    /**
     * Formats the username and password for basic authentication
     *
     * @return "Basic username : password" where "username : passsword" is a base 64 encoded string
     */
    public String getFormattedCredentials(String username, Secret password) {
        String credentials = Base64.getEncoder().encodeToString(
                (username + ":" + password).getBytes(Charset.forName("UTF-8")));
        return "Basic " + credentials;
    }

    private String validateQueryParams(String url, String username) {
        String errorMessage = "";
        if (!nameFormatCheck(username)) {
            errorMessage = errorMessage + "You need to enter a valid name. Letters only, no " + "spaces. ";
        }
        if (!checkForValidURL(url)) {
            errorMessage = errorMessage + "Your URL is not correct. Please enter a valid URL. ";
        }
        return errorMessage;
    }

    /**
     * This tests if the URL is valid in that it should not generate any exceptions when used
     *
     * @param url this is from the url input box
     * @return if it is a valid URL, this means the URL should not generate a URLMalformedException
     */
    public boolean checkForValidURL(String url) {
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
    private boolean nameFormatCheck(String name) {
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public synchronized PersistedList<Category> getCategories(){
        return categories;
    }

    @DataBoundSetter
    public synchronized void setCategories(PersistedList<Category> categories){
        this.categories = categories;
        save();
    }

   // public List<Category.DescriptorImpl> getCategoryDescriptors(){
    //    return ExtensionList.lookup(Category.DescriptorImpl.class);
   // }

    @DataBoundSetter
    public void setName(String name) {
        if (name.length() != 0 && nameFormatCheck(name)) {
            this.name = name;
            save();
        }
    }

    @DataBoundSetter
    public void setDescription(String description) {
        this.description = description;
        save();
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }


}