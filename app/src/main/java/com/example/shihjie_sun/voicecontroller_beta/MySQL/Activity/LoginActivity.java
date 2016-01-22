package com.example.shihjie_sun.voicecontroller_beta.MySQL.Activity;


import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.example.shihjie_sun.voicecontroller_beta.Database.MyDBHelper;
import com.example.shihjie_sun.voicecontroller_beta.Database.SessionManager;
import com.example.shihjie_sun.voicecontroller_beta.MainActivity;
import com.example.shihjie_sun.voicecontroller_beta.MySQL.App.AppConfig;
import com.example.shihjie_sun.voicecontroller_beta.MySQL.App.AppController;
import com.example.shihjie_sun.voicecontroller_beta.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ShihJie_Sun on 2015/12/16.
 */
public class LoginActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private SignInButton googleSignInButton;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private MyDBHelper db;

    // google api connection
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
/*
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog() ////打印logcat，當然也可以定位到dropbox，通過文件保存相應的log
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll()
                .penaltyLog()
                .build());
*/
        processViews();

        configGoogleApiClient();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new MyDBHelper(getApplicationContext(), "mydata.db", null, 4);
        Log.d(TAG, "Database tables created!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {


            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                Log.d(TAG, "Login Response: " + "onClick_Login()");
                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        com.example.shihjie_sun.voicecontroller_beta.MySQL.Activity.RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

        //Link to Google plus connect
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mGoogleApiClient.connect();
                Log.d("googleButton","googleButton");
            }
        });

    }

    private void processViews() {
        inputEmail = (EditText) findViewById(R.id.emaill);
        inputPassword = (EditText) findViewById(R.id.passwordd);
        btnLogin = (Button) findViewById(R.id.btnLoginn);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreenn);
        googleSignInButton = (SignInButton)findViewById(R.id.google_signInBtn);
    }

    private synchronized void configGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    /**
     * function to verify login details in mysql db
     */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        Log.d(TAG, "Login Response: " + "checkLogin()");
        pDialog.setMessage("Logging in ...");
        showDialog();
        //shutdown in there
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {
            //to here
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                Log.d(TAG, "Login Response: " + "!!!!!!!!!!!!!!!!!!!!!");
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onConnected(Bundle arg0) {

        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Log.d("google", "google"+"firstttttt");

                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

                String plusId = currentPerson.getId();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                Log.d("google", "google" + "");
                db.addUser(personName, personEmail, plusId, personPhotoUrl);

                login();
                finish();

            }
    } catch (Exception e) {
        e.printStackTrace();
            Log.d("google", "google_error" + "");
    }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "User is onConnectionSuspended!",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Log.d("google", "google"+"fuckkkkkkkkkkkkkkkkk");
        if (!mIntentInProgress && arg0.hasResolution()) {
            try {
                Log.d("google", "google"+"tryyyyyyyyyyyyyyyyy");
                mIntentInProgress = true;
                arg0.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                Log.d("google", "google"+"catchhhhhhhhhhhhhhhhhhhhh");
                // The intent was canceled before it was sent. Return to the
                // default
                // state and attempt to connect to get an updated
                // ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }


        }

        // Log.e("error", "error code" + arg0.getResolution());
        Toast.makeText(this, "User is onConnectionFailed!", Toast.LENGTH_LONG)
                .show();
        login();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                Log.d("google", "google" + "fuckkkkkkkkkkkkkkkkk_againnnnnnnnnnnnnn");
                //mGoogleApiClient.connect();
                login();
            }
        }
    }

    public void login() {

        Intent i = new Intent(getApplicationContext(),
                com.example.shihjie_sun.voicecontroller_beta.MainActivity.class);
        startActivity(i);
        finish();
    }
}

