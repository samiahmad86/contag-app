package com.contag.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LinkedInActivity extends BaseActivity {

    public static final String TAG = LinkedInActivity.class.getName();

    /****
     * YOUR LINKEDIN APP INFO HERE
     *********/
    private static final String API_KEY = "75d4o58mh5icy4";
    private static final String SECRET_KEY = "GDYRndUPSs01FVbp";
    //This is any string we want to use. This will be used for avoid CSRF attacks. You can generate one here: http://strongpasswordgenerator.com/
    private static final String STATE = "E3ZYKC1T6H2yP4z";
    private static final String REDIRECT_URI = "http://contagapp.com";

    private static final String PROFILE_URL = "https://api.linkedin.com/v1/people/~";
    private static final String OAUTH_ACCESS_TOKEN_PARAM = "oauth2_access_token";

    private static final String SCOPES = "r_basicprofile";
    /*********************************************/

    //These are constants used for build the urls
    private static final String LINKENDIN_AUTHORIZATION_URL = "https://www.linkedin.com/uas/oauth2/authorization";
    private static final String LINKEDIN_ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/accessToken";
    private static final String SECRET_KEY_PARAM = "client_secret";
    private static final String RESPONSE_TYPE_PARAM = "response_type";
    private static final String GRANT_TYPE_PARAM = "grant_type";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE_VALUE = "code";
    private static final String CLIENT_ID_PARAM = "client_id";
    private static final String SCOPE_PARAM = "scope";
    private static final String STATE_PARAM = "state";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";
    /*---------------------------------------*/
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    private WebView webView;
    private ProgressBar pb;
    private String accessToken = null, accessSecret = null;
    private long linkedInID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log(TAG, "fuck me");
        setContentView(R.layout.activity_social_login);
        webView = (WebView) findViewById(R.id.wv_social_login);
        pb = (ProgressBar) findViewById(R.id.pb_social_login);
        linkedInID = getIntent().getLongExtra(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, 0);
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
                if (authorizationUrl.startsWith(REDIRECT_URI)) {
                    Log.i("Authorize", "");
                    Uri uri = Uri.parse(authorizationUrl);
                    Log.i("Authorize", uri.toString());
                    //We take from the url the authorizationToken and the state token. We have to check that the state token returned by the Service is the same we sent.
                    //If not, that means the request may be a result of CSRF and must be rejected.
                    String stateToken = uri.getQueryParameter(STATE_PARAM);
                    if (stateToken == null || !stateToken.equals(STATE)) {
                        Log.e("Authorize", "State token doesn't match");
                        return true;
                    }

                    //If the user doesn't allow authorization to our application, the authorizationToken Will be null.
                    String authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE);
                    if (authorizationToken == null) {
                        Log.i("Authorize", "The user doesn't allow authorization.");
                        return true;
                    }
                    Log.i("Authorize", "Auth token received: " + authorizationToken);

                    //Generate URL for requesting Access Token
                    accessSecret = authorizationToken;
                    String accessTokenUrl = getAccessTokenUrl(authorizationToken);
                    //We make the request in a AsyncTask
                    new AccessTokenRequest().execute(accessTokenUrl);

                } else {
                    //Default behaviour
                    Log.i("Authorize", "Redirecting to: " + authorizationUrl);
                    webView.loadUrl(authorizationUrl);
                }
                return true;
            }
        });

        String authUrl = getLinkendinAuthorizationUrl();
        pb.setVisibility(View.VISIBLE);
        webView.loadUrl(authUrl);
    }


    /**
     * Method that generates the url for get the access token from the Service
     *
     * @return Url
     */
    private static String getAccessTokenUrl(String authorizationToken) {
        return LINKEDIN_ACCESS_TOKEN_URL
                + QUESTION_MARK
                + GRANT_TYPE_PARAM + EQUALS + GRANT_TYPE
                + AMPERSAND
                + RESPONSE_TYPE_VALUE + EQUALS + authorizationToken
                + AMPERSAND
                + CLIENT_ID_PARAM + EQUALS + API_KEY
                + AMPERSAND
                + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI
                + AMPERSAND
                + SECRET_KEY_PARAM + EQUALS + SECRET_KEY;
    }

    /**
     * Method that generates the url for get the authorization token from the Service
     *
     * @return Url
     */
    private static String getLinkendinAuthorizationUrl() {
        return LINKENDIN_AUTHORIZATION_URL
                + QUESTION_MARK + RESPONSE_TYPE_PARAM + EQUALS + RESPONSE_TYPE_VALUE
                + AMPERSAND + CLIENT_ID_PARAM + EQUALS + API_KEY
                + AMPERSAND + SCOPE_PARAM + EQUALS + SCOPES
                + AMPERSAND + STATE_PARAM + EQUALS + STATE
                + AMPERSAND + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI;
    }

    private static String getProfileUrl(String accessToken) {
        return PROFILE_URL
                + QUESTION_MARK
                + OAUTH_ACCESS_TOKEN_PARAM + EQUALS + accessToken;
    }


    private class AccessTokenRequest extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            if (urls.length > 0) {
                String url = urls[0];
                HttpsURLConnection urlConnection = null;
                try {
                    URL requestURL = new URL(url);
                    urlConnection = (HttpsURLConnection) requestURL.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.connect();
                    urlConnection.setConnectTimeout(5000);
                    InputStream is = urlConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    String result = sb.toString();
                    //Convert the string result to a JSON Object
                    JSONObject resultJson = new JSONObject(result);
                    //Extract data from JSON Response
                    int expiresIn = resultJson.has("expires_in") ? resultJson.getInt("expires_in") : 0;

                    String accessToken = resultJson.has("access_token") ? resultJson.getString("access_token") : null;
                    Log.e("Token", "" + accessToken);
                    if (expiresIn > 0 && accessToken != null) {

                        return accessToken;
                    }

                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException e) {
                    Log.e("Authorize", "Error Http response " + e.getLocalizedMessage());
                } catch (JSONException e) {
                    Log.e("Authorize", "Error Parsing Http response " + e.getLocalizedMessage());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String accessToken) {
            if (accessToken != null) {
                LinkedInActivity.this.accessToken = accessToken;
                log(TAG, "executing profile request");
                new ProfileRequest().execute(getProfileUrl(accessToken));
            }
        }

    }

    private class ProfileRequest extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.GONE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            if (urls.length > 0) {
                String url = urls[0];
                HttpsURLConnection urlConnection = null;
                try {
                    URL requestURL = new URL(url);
                    urlConnection = (HttpsURLConnection) requestURL.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("x-li-format", "json");
                    urlConnection.connect();
                    urlConnection.setConnectTimeout(5000);
                    InputStream is = urlConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    String result = sb.toString();
                    //Convert the string result to a JSON Object
                    log(TAG, result);
                    return  new JSONObject(result);


                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException e) {
                    Log.e("Authorize", "Error Http response " + e.getLocalizedMessage());
                } catch (JSONException e) {
                    Log.e("Authorize", "Error Parsing Http response " + e.getLocalizedMessage());
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject data) {
            if (data != null) {
                pb.setVisibility(View.GONE);
                log(TAG, data.toString());
                try {
                    JSONObject jsonObject = data.getJSONObject("siteStandardProfileRequest");
                    Bundle args = new Bundle();
                    args.putLong(Constants.Keys.KEY_SOCIAL_PLATFORM_ID, linkedInID);
                    String url = jsonObject.getString("url");
                    int idStartIndex = url.indexOf("id=");
                    int idEndIndex = url.indexOf("&", idStartIndex);
                    args.putString(Constants.Keys.KEY_USER_PLATFORM_USERNAME, data.getString("firstName") + " " + data.getString("lastName"));
                    args.putString(Constants.Keys.KEY_PLATFORM_ID, url.substring(idStartIndex + 3, idEndIndex));
                    args.putString(Constants.Keys.KEY_PLATFORM_SECRET, accessSecret);
                    args.putString(Constants.Keys.KEY_PLATFORM_TOKEN, accessToken);
                    args.putString(Constants.Keys.KEY_PLATFORM_PERMISSION, SCOPES);
                    args.putString(LinkedInActivity.TAG, data.toString());
                    Intent intent = new Intent();
                    intent.putExtra(Constants.Keys.KEY_BUNDLE, args);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
//                    new SendData().execute(data.getJSONObject("siteStandardProfileRequest"));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }


            } else {
                log(TAG, "something went wrong");
            }
        }
    }


}
