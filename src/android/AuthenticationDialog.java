 package com.msopentech.authDialog;

import org.apache.cordova.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

 public class AuthenticationDialog extends CordovaPlugin {

    private CallbackContext callback;

    private String uri;
    private String userName;
    private String password;
 
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        callback = callbackContext;

        uri = args.get(0).toString();
        userName =  args.get(1).toString();
        password =  args.get(2).toString();

        //self.callbackId = command.callbackId;

        return true;
    }


    private void request() {

        DefaultHttpClient httpclient = new DefaultHttpClient();
        // register ntlm auth scheme
        httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
        httpclient.getCredentialsProvider().setCredentials(
                // Limit the credentials only to the specified domain and port
                new AuthScope("masconsult.eu", -1),
                // Specify credentials, most of the time only user/pass is needed
                new NTCredentials(userName, password, "", "")
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


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}