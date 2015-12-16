package controller;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hieunguyen725.myplaces.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import model.database.PlacesDataSource;
import model.Place;
import model.parsers.JSONParser;
import model.service.MyConnection;

/**
 * This is an activity that will allow the user the view
 * a place's information such as its name, address, location type,
 * phone number, description and more.
 */
public class PlaceInfoActivity extends AppCompatActivity {

    public static final String TAG = "PlaceInfoActivity";
    private static final String API_KEY = "&key=AIzaSyCYoO7HjswFyU9zNWR" +
            "7kP_kJoWs_IlQIuI";
    private static final String BASE_URL = "https://maps.googleapis.com" +
            "/maps/api/place/details/json?";
    private static final String BASE_IMAGE_URL = "https://maps.googleapis.com" +
            "/maps/api/place/photo?maxwidth=400&photoreference=";

    private ProgressBar mProgressBar;
    private Place mCurrentPlace;
    private Location mLocation;

    /**
     * On Create method to initialize and inflate the activity's
     * user interface. Also to retrieve the information of the current
     * place to display.
     * @param savedInstanceState Bundle containing the data most recently
     *                           saved data through onSaveInstanceState,
     *                           null if nothing was saved.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mProgressBar = (ProgressBar) findViewById(R.id.placeInfo_progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        retrieveInfo();
    }

    /**
     * Retrieve the information of the current place to display.
     * If the place's content source is from an online web service, then make
     * a http web service request and display the information.
     *  Otherwise retrieve the place's information
     * from getIntent() and display the information.
     */
    private void retrieveInfo() {
        String contentSource = getIntent().getStringExtra("placeSource");
        if (contentSource.equals("onlineContent")) {
            if (isOnline()) {
                String placeID = "placeid=" + getIntent().getStringExtra("placeID");
                String URL = BASE_URL + placeID + API_KEY;
                Log.i(TAG, "URL = " + URL);
                SearchTask task = new SearchTask();
                task.execute(URL);
            } else {
                Toast.makeText(this, "Network connection is not available",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Bundle extras = getIntent().getExtras();
            mCurrentPlace = new Place();
            mCurrentPlace.setUsername(LogInActivity.sUser);
            mCurrentPlace.setPlaceID(extras.getString("placeID"));
            mCurrentPlace.setName(extras.getString("placeName"));
            mCurrentPlace.setAddress(extras.getString("placeAddress"));
            mCurrentPlace.setMainType(extras.getString("placeType"));
            mCurrentPlace.setPhoneNumber(extras.getString("placePhone"));
            mCurrentPlace.setDescription(extras.getString("placeDescription"));
            displayInfo();
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
     * Initialize the option menu content for this activity
     * @param menu The option menu object to be inflated with the menu layout
     * @return true to display the menu, false to not display the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place_info, menu);
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
     * This is a class that represents a search task and extends AsyncTask.
     * This class will search for, retrieve and parse a place's information
     * by making http web service request to the Google Places.
     */
    private class SearchTask extends AsyncTask<String, String, Place> {

        /**
         * Prepare to execute task.
         */
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "Starting task");
            mProgressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Use a connection to make a web service request, retrieve the JSON
         * content to parse and load the place's images if there are any.
         * @param params a String of URL to request the place's information.
         * @return A place that containing its information from the web service.
         *  null if no matching information content.
         */
        @Override
        protected Place doInBackground(String... params) {
            MyConnection myConnection = new MyConnection();
            String content = null;
            try {
                content = myConnection.retrieveData(params[0]);
                if (content != null) {
                    Log.i(TAG, "content is not null");
                    Log.i(TAG, "content length: " + content.length());
                    Place place = new JSONParser().infoParse(content);
                    // If the place is available and have images references
                    // then load the place's images.
                    if (place != null && place.getImageReferences() != null) {
                        Log.i(TAG, place.getImageReferences().toString());
                        place = loadImages(place);
                    }
                    return place;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Load the images content and add the images to the place's images list.
         * @param place the current place to display information and add images to.
         * @return a place with images added.
         */
        private Place loadImages(Place place) {
            try {
                place.setPlaceImages(new ArrayList<Bitmap>());
                for (String reference : place.getImageReferences()) {
                    String imageURL = BASE_IMAGE_URL + reference + API_KEY;
                    Log.i(TAG, imageURL);
                    // Retrieve imageURL content and store it into the inputStream
                    // for decoding to generate the bitmap object. Then add the result
                    // to the place's images list.
                    InputStream inputStream = (InputStream)
                            new URL(imageURL).getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Log.i(TAG, "Bitmap height = " + bitmap.getHeight());
                    place.getPlaceImages().add(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return place;
        }


        /**
         * Given a place with its information, display the
         * place information if not null.
         * @param place a place containing its information.
         */
        @Override
        protected void onPostExecute(Place place) {
            if (place != null) {
                Log.i(TAG, place.toString());
                mCurrentPlace = place;
                displayInfo();
            } else {
                Log.i(TAG, "can't parse, place is null");
            }
            mProgressBar.setVisibility(View.INVISIBLE);
            Log.i(TAG, "task finished");
        }
    }

    /**
     * Display the place information to the user interface.
     */
    private void displayInfo() {
        TextView placeName = (TextView) findViewById(R.id.placeInfo_name_tv);
        TextView placeAddress = (TextView) findViewById(R.id.placeInfo_address_tv);
        TextView placeType = (TextView) findViewById(R.id.placeInfo_placeType_tv);
        TextView placePhone = (TextView) findViewById(R.id.placeInfo_phone_tv);
        TextView placeDescription = (TextView) findViewById(R.id.placeInfo_description_tv);
        TextView openingHours = (TextView) findViewById(R.id.placeInfo_opening_hours_tv);
        TextView reviews = (TextView) findViewById(R.id.placeInfo_reviews_tv);

        // Display the place's basic information.
        placeName.setText("Name: " + mCurrentPlace.getName());
        placeAddress.setText("Address: " + mCurrentPlace.getAddress());
        placeType.setText("Place type: " + mCurrentPlace.getMainType());
        placePhone.setText("Phone: " + mCurrentPlace.getPhoneNumber());

        // Calculate the average height of the place's images if they are not
        // not null then add image to the layout.
        if (mCurrentPlace.getPlaceImages() != null) {
            int height = getAverageSize(mCurrentPlace);
            Log.i(TAG, mCurrentPlace.getPlaceImages().size() + "");
            LinearLayout imagesHolder = (LinearLayout) findViewById(R.id.placeInfo_images_list);
            for (Bitmap image : mCurrentPlace.getPlaceImages()) {
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(image);
                imageView.setLayoutParams(new ActionBar.LayoutParams(height + 250, height));
                imagesHolder.addView(imageView);
            }
        }
        if (!mCurrentPlace.getDescription().equals("Not available")) {
            placeDescription.setText("Description: " + mCurrentPlace.getDescription());
        } else {
            placeDescription.setHeight(0);
            placeDescription.setWidth(0);
            placeDescription.setPadding(0, 0, 0, 0);
        }
        if (mCurrentPlace.getOpeningHours() != null) {
            openingHours.setText("Opening hours: \n\n" + mCurrentPlace.getOpeningHours());
        }
        if (mCurrentPlace.getReviews() != null) {
            reviews.setText("Reviews: \n\n" + mCurrentPlace.getReviews());
        }
    }

    /**
     * Calculate the place's images height to get the average
     * size of the images for a better display.
     * @param place a place containing the images to calculate
     * @return the average height of the images as an integer.
     */
    private int getAverageSize(Place place) {
        int totalHeight = 0;
        for (Bitmap image : place.getPlaceImages()) {
            totalHeight += image.getHeight();
        }
        return (int) totalHeight / place.getPlaceImages().size();
    }

    /**
     * Web on click button listener, launch the place's website
     * in a fully functional website mode if the place has a
     * website.
     * @param view reference of the widget that was clicked on.
     */
    public void viewWebOnClick(View view) {
        if (!mCurrentPlace.getWebsiteURL().equals("Not available")) {
            // parse the website URL then launch it as an intent
            // to view a fully functional website.
            Uri URI = Uri.parse(mCurrentPlace.getWebsiteURL());
            Intent intent = new Intent(Intent.ACTION_VIEW, URI);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Place has no website", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Save button on click listener, create a place a data source
     * to connect to the SQLite database and save the current place.
     * @param view reference to the widget that was clicked on.
     */
    public void saveButtonOnClick(View view) {
        mCurrentPlace.setUsername(LogInActivity.sUser);
        PlacesDataSource placesDataSource = new PlacesDataSource(this);
        placesDataSource.create(mCurrentPlace);
        Toast.makeText(this, "Place Saved", Toast.LENGTH_LONG).show();
    }

    /**
     * Direction button on click listener, launch a google map direction
     * browser or application to get the direction from the user's current location
     * to the current place's address.
     *
     * @param view  reference to the widget that was clicked on.
     */
    public void directionButtonOnClick(View view) {
        if (isOnline()) {
            getLocation();
            if (mLocation == null) {
                Toast.makeText(this, "Device location is not enabled",
                        Toast.LENGTH_LONG).show();
            } else {
                String startAddr = "saddr=" + mLocation.getLatitude() + ","
                        + mLocation.getLongitude();
                String destinationAddr = "&daddr=" + mCurrentPlace.getAddress();
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?" + startAddr + destinationAddr));
                intent.setClassName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "Network connection is not available",
                    Toast.LENGTH_LONG).show();
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





}
