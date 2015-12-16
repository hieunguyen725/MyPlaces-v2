package controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
 * This is an activity that will the user to search for nearby places
 * according to their device location using GPS connection.
 */
public class NearbySearchActivity extends AppCompatActivity {

    public static final String TAG = "NearbySearchActivity";
    private static final String API_KEY = "&key=AIzaSyCYoO7HjswFyU9zN" +
            "WR7kP_kJoWs_IlQIuI";
    private static final String BASE_URL = "https://maps.googleapis.com" +
            "/maps/api/place/nearbysearch/json?";

    private static List<Place> sCurrentPlaces;

    private ProgressBar mProgressBar;
    private Location mLocation;

    /**
     * On Create method to initialize and inflate the activity's
     * user interface. Also to check for device location permission
     * during runtime, as required for API 23.
     * @param savedInstanceState Bundle containing the data most recently
     *                           saved data through onSaveInstanceState,
     *                           null if nothing was saved.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_search);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        checkPermission();
        mProgressBar = (ProgressBar) findViewById(R.id.nearbySearch_progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Display the current list of places when on resume
     * is called.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (sCurrentPlaces != null) {
            displayList();
        }
    }

    /**
     * If the device location permission is granted, use the system
     * location service to obtain the last known or current
     * location of the device. Also to update the location if changed.
     */
    private void getLocation() {
        if(checkPermission()) {
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            final LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    mLocation = location;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.i(TAG, "location status changed");
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.i(TAG, "location provider enabled");
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.i(TAG, "location provider disabled");
                }
            };
            // Check whether if the GPS provider exists and if it is enabled in the device
            // If the GPS is enabled, request location updates.
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)
                    && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // request location update with the location listener to use onLocationChanged(),
                // given the name of the provider, minimum time interval as 5 seconds,
                // and minimum distance as 10 meters between location updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5000, 10, locationListener);
            }
            mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    /**
     * Check for device location permission during runtime. Return true
     * if the device location permission is already granted, else request
     * for permission and return false. Runtime permission check is required
     * in API 23.
     */
    private boolean checkPermission() {
        int gpsPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (gpsPermission == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "permissions granted");
            return true;
        } else {
            Log.i(TAG, "Device location access permission was denied");
            String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
            // request for device location permission with a result id code as 0
            ActivityCompat.requestPermissions(this, permission, 0);
            return false;
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
        getMenuInflater().inflate(R.menu.menu_nearby_search, menu);
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
     * Search button on click listener, get the user's current location
     * and their location inputs. Then use those information to make
     * a web service request to the Google Places to retrieve possible
     * places.
     * @param view reference of the widget that was clicked on.
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
        if (isOnline()) {
            getLocation();
            // If the current location is available, retrieve the user's
            // inputs and make the web service request.
            if (mLocation != null) {
                EditText keyword = (EditText) findViewById(R.id.nearbySearch_keyword);
                EditText radius = (EditText) findViewById(R.id.nearbySearch_radius);
                if (keyword.getText().toString().equals("") ||
                        radius.getText().toString().equals("") ||
                        !radius.getText().toString().matches("\\d+")) {
                    Toast.makeText(this, "Invalid Keyword/Radius",
                            Toast.LENGTH_LONG).show();
                } else {
                    // If the required inputs are not blank, retrieve the information
                    // and start the web service request.
                    String locationText = "&location=" + mLocation.getLatitude()
                            + "," + mLocation.getLongitude();
                    double metersPerMile = 1609.34;
                    int myRadius = (int) (Double.parseDouble(radius.getText().
                            toString()) * metersPerMile);
                    String radiusText = "&radius=" + myRadius;
                    String keywordText = "&keyword=" + keyword.getText().
                            toString().replace(" ", "_");
                    String URL = BASE_URL + locationText + radiusText
                            + keywordText + API_KEY;
                    SearchTask task = new SearchTask();
                    task.execute(URL);
                }
            } else {
                Toast.makeText(this, "Device location is not enabled",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Network connection is not available",
                    Toast.LENGTH_LONG).show();
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
     * inputs.
     */
    private class SearchTask extends AsyncTask<String, String, List<Place>> {

        /**
         * Preparing to execute task.
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
         * user's request inputs. Null if no matching content.
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
                            searchParse(content, "nearby");
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
        ListView listView = (ListView) findViewById(R.id.nearbySearch_listview);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place place = (Place) parent.getItemAtPosition(position);

                // View page info activity
                Intent intent = new Intent(NearbySearchActivity.this, PlaceInfoActivity.class);
                intent.putExtra("placeID", place.getPlaceID());
                intent.putExtra("placeSource", place.getContentResource());
                startActivity(intent);
            }
        });
    }
}
