package com.example.shihjie_sun.voicecontroller_beta.Note;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shihjie_sun.voicecontroller_beta.R;

/**
 * Created by ShihJie_Sun on 2016/1/15.
 */
public class NoteActivity extends Activity implements OnItemSelectedListener {

    private RadioButton half_hour;
    private RadioButton one_hour;
    private RadioButton two_hours;
    private RadioGroup rgroup;

    private Button btnAddNewCategory;
    private TextView txtCategory;
    private Spinner spinnerFood;
    // array list for spinner adapter
    private ArrayList<Category> categoriesList;
    ProgressDialog pDialog;

    // API urls
    // Url to create new category
    private String URL_NEW_CATEGORY = "http://10.78.21.36/note_api/new_category.php";
    // Url to get all categories
    private String URL_CATEGORIES = "http://10.78.21.36/note_api/get_categories.php";

    public double reload_time;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        btnAddNewCategory = (Button) findViewById(R.id.btnAddNewCategory);
        spinnerFood = (Spinner) findViewById(R.id.spinFood);
        txtCategory = (TextView) findViewById(R.id.txtCategory);
        half_hour = (RadioButton) findViewById(R.id.half_hour);
        one_hour = (RadioButton) findViewById(R.id.one_hour);
        two_hours = (RadioButton) findViewById(R.id.two_hours);
        rgroup = (RadioGroup) findViewById(R.id.rgroup);

        rgroup.setOnCheckedChangeListener(radio_listener);

        categoriesList = new ArrayList<Category>();

        // spinner item select listener
        spinnerFood.setOnItemSelectedListener(this);

        // Add new category click event
        btnAddNewCategory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (txtCategory.getText().toString().trim().length() > 0) {

                    // new category name
                    String newCategory = txtCategory.getText().toString();

                    // Call Async task to create new category
                    new AddNewCategory().execute(newCategory);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter category name", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        new GetCategories().execute();

    }

    private RadioGroup.OnCheckedChangeListener radio_listener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
// TODO Auto-generated method stub

            switch (checkedId) {
                case R.id.half_hour:

                    reload_time = 0.5;
                    break;
                case R.id.one_hour:

                    reload_time = 1;
                    break;
                case R.id.two_hours:

                    reload_time = 2;
                    break;
            }
        }
    };

    /**
     * Adding spinner data
     * */
    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();

        txtCategory.setText("");

        for (int i = 0; i < categoriesList.size(); i++) {
            lables.add(categoriesList.get(i).getName());

        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerFood.setAdapter(spinnerAdapter);
    }

    /**
     * Async task to get all food categories
     * */
    private class GetCategories extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NoteActivity.this);
            pDialog.setMessage("Fetching food categories..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(URL_CATEGORIES, ServiceHandler.GET);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray categories = jsonObj
                                .getJSONArray("categories");

                        for (int i = 0; i < categories.length(); i++) {
                            JSONObject catObj = (JSONObject) categories.get(i);
                            Category cat = new Category(catObj.getInt("id"),
                                    catObj.getString("name"));
                            categoriesList.add(cat);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinner();
        }

    }

    /**
     * Async task to create a new food category
     * */
    private class AddNewCategory extends AsyncTask<String, Void, Void> {

        boolean isNewCategoryCreated = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NoteActivity.this);
            pDialog.setMessage("Creating new category..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... arg) {

            String newCategory = arg[0];

            // Preparing post params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", newCategory));

            ServiceHandler serviceClient = new ServiceHandler();

            String json = serviceClient.makeServiceCall(URL_NEW_CATEGORY,
                    ServiceHandler.POST, params);

            Log.d("Create Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    boolean error = jsonObj.getBoolean("error");
                    // checking for error node in json
                    if (!error) {
                        // new category created successfully
                        isNewCategoryCreated = true;
                    } else {
                        Log.e("Create Category Error: ", "> " + jsonObj.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (isNewCategoryCreated) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // fetching all categories
                        new GetCategories().execute();
                    }
                });
            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        Toast.makeText(
                getApplicationContext(),
                parent.getItemAtPosition(position).toString() + " Selected" ,
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}