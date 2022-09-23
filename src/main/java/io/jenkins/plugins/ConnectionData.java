package io.jenkins.plugins;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;

import org.kohsuke.stapler.DataBoundConstructor;
import hudson.Extension;
import hudson.util.Secret;

@Extension
public class ConnectionData {

    private String username;
    private Secret password;
    private URL url;
    private String payload;

    public ConnectionData(){}

    @DataBoundConstructor
    public ConnectionData(String username, Secret password, URL url) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.payload = ""; //default behaviour is to add payload after setting initial fields
    }

    /**
     * Formats the username and password for basic authentication
     * @return "Basic username : password" where "username : passsword" is a base 64 encoded string
     */
    public String getFormattedCredentials(){
        String credentials =
                Base64.getEncoder().encodeToString((username + ":" + password).getBytes(
                        Charset.forName("UTF-8")));
        return "Basic " + credentials;
    }

    public URL getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public Secret getPassword(){
        return password;
    }

    public String getPayload(){ return payload; }

    public void setPayload(String payload){this.payload = payload;}

}
