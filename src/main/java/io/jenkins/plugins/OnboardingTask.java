package io.jenkins.plugins;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;

import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.FormValidation;
import hudson.util.Secret;
import jenkins.model.GlobalConfiguration;

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
    private ConnectionData connectionData;

    public OnboardingTask() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    @POST
    public FormValidation doTestConnectionWithPayload(@QueryParameter("username") String username,
                                                      @QueryParameter("password") Secret password,
                                                      @QueryParameter("url") String url) throws IOException {
        try {
            String errorMessage = validateQueryParams(url, username);
            if (errorMessage.length() != 0) {
                return FormValidation.warning(errorMessage);
            } else {

                setConnectionData(username, password, url);
                connectionData.setPayload("Secret Text");

                //This runs an HTTP request on this objects URL, username and password and returns an HTTP response code
                int httpRequestResponseCode = sendHTTPRequest();

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

    @POST
    public FormValidation doTestConnection(@QueryParameter("username") String username,
                                           @QueryParameter("password") Secret password,
                                           @QueryParameter("url") String url) throws IOException {
        try {
            String errorMessage = validateQueryParams(url, username);
            if (errorMessage.length() != 0) {
                return FormValidation.warning(errorMessage);
            } else {
                setConnectionData(username, password, url);

                //This runs an HTTP request on this objects URL, username and password and returns an HTTP response code
                int httpRequestResponseCode = sendHTTPRequest();

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
    private int sendHTTPRequest() throws IOException {
        int responseCode = 0;
        HttpURLConnection httpURLConnection = (HttpURLConnection) connectionData.getUrl().openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty(HttpHeaders.AUTHORIZATION, connectionData.getFormattedCredentials());

        //If the payload has data in it, add the payload to the HTTP connection
        if (connectionData.getPayload().length() != 0) {
            //This adds a payload to the HTTP request
            httpURLConnection.setDoOutput(true);
            OutputStream os = httpURLConnection.getOutputStream();
            os.write(connectionData.getPayload().getBytes());
            os.flush();
            os.close();
        }

        responseCode = httpURLConnection.getResponseCode();
        return responseCode;
    }

    /**
     * This sets the global connectionData object based on the query params
     * @param username
     * @param password
     * @param url This URL has been checked for format in validateQueryParams() using checkForValidURL()
     *            before it gets here so should never throw a malformedURL exception
     * @throws MalformedURLException
     */
    private void setConnectionData(String username, Secret password, String url) throws MalformedURLException {
        URL newURL = new URL(url);
        connectionData = new ConnectionData(username, password, newURL);
        save();
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

    public String getName() {
        return name;
    }

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

    public ConnectionData getConnectionData() {
        return this.connectionData;
    }

    @DataBoundSetter
    public void setConnectionData(ConnectionData connectionData) {
        if (connectionData.getUsername().length() != 0 && nameFormatCheck(connectionData.getUsername())) {
            this.connectionData = connectionData;
            save();
        }
    }

}