package controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hieunguyen725.myplaces.R;

import java.io.IOException;
import java.util.List;

import model.MyAdapter;
import model.Place;
import model.parsers.JSONParser;
import model.service.MyConnection;

/**
 * Author: Hieu Nguyen
 *
 * This is an activity that will the user to input a text phrase
 * then search and display a list of possible locations matching
 * their search phrase.
 */
public class RelatedSearchActivity extends AppCompatActivity {

    public static final String TAG = "RelatedSearchActivity";
    private static final String API_KEY = "&key=AIzaSyCYoO7HjswFyU9zNWR" +
            "7kP_kJoWs_IlQIuI";
    private static final String BASE_URL = "https://maps.googleapis.com" +
            "/maps/api/place/textsearch/json?";

    private static List<Place> sCurrentPlaces;

    private ProgressBar mProgressBar;

    /**
     * On Create method to initialize and inflate the activity's
     * user interface.
     * @param savedInstanceState Bundle containing the data most recently
     *                           saved data through onSaveInstanceState,
     *                           null if nothing was saved.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_search);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mProgressBar = (ProgressBar) findViewById(R.id.relatedSearch_progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * onResume method to re-display the list of locations.
     */
    protected void onResume() {
        super.onResume();
        if (sCurrentPlaces != null) {
            displayList();
        }
    }

    /**
     * Initialize the option menu content for this activity
     * @param menu The option menu object to be inflated with the menu layout
     * @return true to display the menu, false to not display the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_related_search, menu);
        return true;
    }

    /**
     * Handle selected menu option by the user
     * @param item The selected menu item by the user
     * @return true if menu item process was taken, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nearby_search:
                // User chose the nearby search activity, start the nearby
                // search activity
                Intent intentNearby = new Intent(this, NearbySearchActivity.class);
                MainActivity.sCurrentIntent = intentNearby;
                startActivity(intentNearby);
                return true;

            case R.id.related_search:
                // User chose the related search activity, start the related
                // search activity
                Intent intentRelated = new Intent(this, RelatedSearchActivity.class);
                MainActivity.sCurrentIntent = intentRelated;
                startActivity(intentRelated);
                return true;

            case R.id.my_places:
                // User chose my places search activity, start my places activity
                Intent intentPlaces = new Intent(this, MyPlacesActivity.class);
                MainActivity.sCurrentIntent = intentPlaces;
                startActivity(intentPlaces);
                return true;

            case R.id.log_out:
                Intent logout = new Intent(this, LogInActivity.class);
                MainActivity.sCurrentIntent = null;
                LogInActivity.sUser = null;
                SharedPreferences sharedPreferences =
                        this.getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.LOGGEDIN), false);
                editor.commit();
                startActivity(logout);
                finish();
                return true;

            default:
                // If we got here, the sUser's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Search button on click listener, retrieve the user's text phrase
     * inputs then connect to the web service to get the user's inputs if
     * there is connection. After that, retrieve a list of possible locations
     * from the http reponse then display it to the user.
     * @param view
     */
    public void searchButtonOnClick(View view) {
        // Once the search button is clicked, hide the soft keyboard
        // from the screen.
        InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        // Hide the keyboard from the screen unless it is forced to
        // open up as required for edit text for input.
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        // if online, get the input and make the request
        if (isOnline()) {
            EditText searchPhrase = (EditText) findViewById(R.id.relatedSearch_searchPhrase);
            if (searchPhrase.getText().toString().equals("")) {
                Toast.makeText(this, "Invalid search phrase", Toast.LENGTH_LONG).show();
            } else {
                String queryText = "query=" + searchPhrase.getText().toString().replace(" ", "%20");
                String URL = BASE_URL + queryText +  API_KEY;
                SearchTask task = new SearchTask();
                task.execute(URL);
            }
        } else {
            Toast.makeText(this, "Network connection is not available", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check whether the device is connected to a network.
     * @return true network is available and is connected/connecting,
     * false otherwise.
     */
    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * A class representing a search locations task extending AsyncTask.
     * This class will be making web service request to the Google Places
     * and generate a list of possible nearby locations matching the user's
     * text phrase input.
     */
    private class SearchTask extends AsyncTask<String, String, List<Place>> {

        /**
         * Preparing to start task.
         */
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "Starting task");
            mProgressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Use the connection to make a http request to the
         * web service.
         * @param params a String URL for making the service request.
         * @return a list of possible nearby location matching the
         * user's text phrase input. Null if no matching content.
         */
        @Override
        protected List<Place> doInBackground(String... params) {
            MyConnection myConnection = new MyConnection();
            String content = null;
            try {
                content = myConnection.retrieveData(params[0]);
                if (content != null) {
                    // Get a list of places by parsing the JSON
                    // text content.
                    List<Place> places = new JSONParser().
                            searchParse(content, "related");
                    return places;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Given the list of possible places, display the places if
         * they are not null.
         * @param places the list of matching places from the
         *               web service request.
         */
        @Override
        protected void onPostExecute(List<Place> places) {
            if (places != null) {
                sCurrentPlaces = places;
                displayList();
            } else {
                Log.i(TAG, "can't parse, places is null");
            }
            mProgressBar.setVisibility(View.INVISIBLE);
            Log.i(TAG, "task finished");
        }
    }

    /**
     * Display the user's current list of places on the user interface
     */
    private void displayList() {
        ListAdapter listAdapter = new MyAdapter(this, sCurrentPlaces);
        ListView listView = (ListView) findViewById(R.id.relatedSearch_viewlist);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place place = (Place) parent.getItemAtPosition(position);

                // View page info activity
                Intent intent = new Intent(RelatedSearchActivity.this, PlaceInfoActivity.class);
                intent.putExtra("placeID", place.getPlaceID());
                intent.putExtra("placeSource", place.getContentResource());
                startActivity(intent);
            }
        });
    }
}
