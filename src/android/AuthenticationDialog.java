 package com.msopentech.authDialog;

import org.apache.cordova.*;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.AuthSchemeBase;
import org.json.JSONArray;
import org.json.JSONException;


import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

 public class AuthenticationDialog extends CordovaPlugin {

    private CallbackContext callback;

    private String uri;
    private String userName;
    private String password;
    private String domain;
 
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        callback = callbackContext;

        uri = args.get(0).toString();
        userName =  args.get(1).toString();
        password =  args.get(2).toString();
        domain =  args.get(3).toString();

        return request();
    }


    private boolean request() {



        DefaultHttpClient httpclient = new DefaultHttpClient();

        // register ntlm auth scheme
        AuthSchemeRegistry authSchemeRegistry = httpclient.getAuthSchemes();
        authSchemeRegistry.register("ntlm", new NTLMSchemeFactory());

        AuthScope authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);

        NTCredentials creds = new NTCredentials(userName, password, "", domain);

        httpclient.getCredentialsProvider().setCredentials(
                // Limit the credentials only to the specified domain and port
                authScope,
                // Specify credentials, most of the time only user/pass is needed
                creds
        );

        HttpGet get = new HttpGet(uri);
        try {
            HttpResponse response = httpclient.execute(get);

            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());

            int statusCode = response.getStatusLine().getStatusCode();

            PluginResult res;

            if(!(statusCode == 200 || statusCode == 405)){

                res = new PluginResult(PluginResult.Status.ERROR);
                callback.sendPluginResult(res);
                return false;

            }

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            res = new PluginResult(PluginResult.Status.OK, result.toString());

            callback.sendPluginResult(res);
            return  true;

        } catch (IOException e) {
            e.printStackTrace();
        }



        return  false;
    }

}