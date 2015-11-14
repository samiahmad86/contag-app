package com.contag.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.contag.app.R;
import com.contag.app.config.Constants;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class InstagramActivity extends BaseActivity {

    private static final String TAG = InstagramActivity.class.getName();

    /**
     * *
     * YOUR LINKEDIN APP INFO HERE
     * *******
     */
    private static final String CLIENT_ID = "25a23d669c6e4bbbb9e1d1d16f04e8e6";
    private static final String CLIENT_SECRET = "be587b07228b46afac85c18bdb032801";
    //This is any string we want to use. This will be used for avoid CSRF attacks. You can generate one here: http://strongpasswordgenerator.com/
    private static final String STATE = "E3ZYKC1T6H2yP4z";
    private static final String REDIRECT_URI = "http://www.contagapp.com";

    private static final String API_URL = "https://api.instagram.com/v1";
    private static final String OAUTH_ACCESS_TOKEN_PARAM = "oauth2_access_token";

    private static final String SCOPES = "basic";
    /**
     * *****************************************
     */

    //These are constants used for build the urls
    private static final String INSTAGRAM_AUTHORIZATION_URL = "https://api.instagram.com/oauth/authorize/";
    private static final String INSTAGRAM_ACCESS_TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private static final String SECRET_KEY_PARAM = "client_secret";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE_VALUE = "code";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String SCOPE_PARAM = "scope";
    private static final String STATE_PARAM = "state";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";
    private static final String DISPLAY = "display=touch";
    /*---------------------------------------*/
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    private WebView webView;
    private ProgressBar pb;

    private long instagramID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_login);
        webView = (WebView) findViewById(R.id.wv_social_login);
        pb = (ProgressBar) findViewById(R.id.pb_social_login);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);

        instagramID = getIntent().getLongExtra(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, 0);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //This method will be executed each time a page finished loading.
                //The only we do is dismiss the progressDialog, in case we are showing any.
                if (pb != null) {
                    pb.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
                //This method will be called when the Auth proccess redirect to our RedirectUri.
                //We will check the url looking for our RedirectUri.
                log(TAG, authorizationUrl);
                if (authorizationUrl.startsWith(REDIRECT_URI)) {
                    String parts[] = authorizationUrl.split("=");
                    new AccessTokenRequest().execute(getAccessTokenUrl(), parts[1]);
                    return true;
                }
                return false;
            }
        });

        String authUrl = getInstagramAuthorizationUrl();
        pb.setVisibility(View.VISIBLE);
        webView.loadUrl(authUrl);
    }


    /**
     * Method that generates the url for get the access token from the Service
     *
     * @return Url
     */
    private static String getAccessTokenUrl() {
        return INSTAGRAM_ACCESS_TOKEN_URL
                + QUESTION_MARK
                + CLIENT_ID_PARAM + EQUALS + CLIENT_ID
                + AMPERSAND
                + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI
                + AMPERSAND
                + SECRET_KEY_PARAM + EQUALS + CLIENT_SECRET
                + AMPERSAND
                + GRANT_TYPE_PARAM + EQUALS + GRANT_TYPE;
    }

    /**
     * Method that generates the url for get the authorization token from the Service
     *
     * @return Url
     */
    private static String getInstagramAuthorizationUrl() {
        return INSTAGRAM_AUTHORIZATION_URL
                + QUESTION_MARK + RESPONSE_TYPE_PARAM + EQUALS + RESPONSE_TYPE_VALUE
                + AMPERSAND + CLIENT_ID_PARAM + EQUALS + CLIENT_ID
                + AMPERSAND + SCOPE_PARAM + EQUALS + SCOPES
                + AMPERSAND + STATE_PARAM + EQUALS + STATE
                + AMPERSAND + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI;
    }

    private static String getProfileUrl(String accessToken) {
        return CLIENT_ID_PARAM + EQUALS + CLIENT_ID
                + AMPERSAND
                + SECRET_KEY_PARAM + EQUALS + CLIENT_SECRET
                + AMPERSAND
                + GRANT_TYPE_PARAM + EQUALS + GRANT_TYPE
                + AMPERSAND
                + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI
                + AMPERSAND
                + RESPONSE_TYPE_VALUE + EQUALS + accessToken;
    }


    private class AccessTokenRequest extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            if (urls.length > 0) {
                String url = urls[0];
                String token = urls[1];
                HttpsURLConnection httpsURLConnection = null;
                try {
                    URL requestURL = new URL(url);
                    httpsURLConnection = (HttpsURLConnection) requestURL.openConnection();
                    httpsURLConnection.setRequestMethod("POST");
                    httpsURLConnection.setDoInput(true);
                    httpsURLConnection.setDoOutput(true);
//                    httpsURLConnection.setConnectTimeout(5000);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpsURLConnection.getOutputStream());
                    log(TAG, "hey there");
                    outputStreamWriter.write(getProfileUrl(token));
                    outputStreamWriter.flush();
//                    httpsURLConnection.connect();
                    InputStream is = httpsURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    String result = sb.toString();
                    //Convert the string result to a JSON Object
                    JSONObject jsonObject = (JSONObject) new JSONTokener(result).nextValue();
                    log(TAG, jsonObject.toString());
                    return jsonObject;
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException e) {
                    Log.e("Authorize", "Error Http response " + e.getLocalizedMessage());
                } catch (JSONException e) {
                    Log.e("Authorize", "Error Parsing Http response " + e.getLocalizedMessage());
                } finally {
                    if (httpsURLConnection != null) {
                        httpsURLConnection.disconnect();
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            log(TAG, jsonObject.toString());
            try {
                Bundle args = new Bundle();
                args.putLong(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, instagramID);
                JSONObject usr = jsonObject.getJSONObject("user");
                args.putString(Constants.Keys.KEY_PLATFORM_TOKEN, jsonObject.getString("access_token"));
                args.putString(Constants.Keys.KEY_PLATFORM_PERMISSION, SCOPES);
                args.putString(Constants.Keys.KEY_USER_PLATFORM_USERNAME, usr.getString("full_name"));
                args.putString(Constants.Keys.KEY_PLATFORM_ID, usr.getString("username"));
                Intent intent = new Intent();
                intent.putExtra(Constants.Keys.KEY_BUNDLE, args);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

    }


}
