package controller;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hieunguyen725.myplaces.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import model.MyAdapter;
import model.Place;
import model.parse.com.ParsePlace;

/**
 * Author: Hieu Nguyen
 *
 * This is an activity that will allow the user to view the places
 * that they saved through searching or manually added.
 */
public class MyPlacesActivity extends AppCompatActivity {

    public static final String TAG = "MyPlacesActivity";

    private ProgressBar myProgressBar;
    private List<Place> mCurrentPlaces;

    private ListAdapter mListAdapter;

    /**
     * On Create method to initialize and inflate the activity's
     * user interface. Also initialize a new data source then load
     * the user's places from Parse.
     * @param savedInstanceState Bundle containing the data most recently
     *                           saved data through onSaveInstanceState,
     *                           null if nothing was saved.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        myProgressBar = (ProgressBar) findViewById(R.id.myPlaces_progressBar);
        myProgressBar.setVisibility(View.INVISIBLE);
        loadPlaceData();
    }

    /**
     * Load places data from the Parse.
     */
    private void loadPlaceData() {
        if (isOnline()) {
            ParseQuery<ParsePlace> query = ParseQuery.getQuery(ParsePlace.class);
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParsePlace>() {
                @Override
                public void done(List<ParsePlace> objects, ParseException e) {
                    if (e == null) {
                        mCurrentPlaces = new ArrayList<Place>();
                        for (ParsePlace place : objects) {
                            mCurrentPlaces.add(place.createModel());
                        }
                        displayList();
                    } else {
                        Log.d(TAG, "Error: " + e.getMessage());
                    }
                }
            });
        } else {
            Toast.makeText(this, "No network connection available", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Reload places data from Parse when on resume called.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadPlaceData();
    }

    /**
     * Initialize the option menu content for this activity
     * @param menu The option menu object to be inflated with the menu layout
     * @return true to display the menu, false to not display the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_places, menu);
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
                if (isOnline()) {
                    ParseUser.logOut();
                    Intent logout = new Intent(this, LogInActivity.class);
                    startActivity(logout);
                    finish();
                } else {
                    Toast.makeText(this, "Sorry, please turn on network connection " +
                                    "to completely log out.", Toast.LENGTH_LONG).show();
                }
                return true;

            default:
                // If we got here, the sUser's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
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
     *  Create or build a context menu for the current view
     *
     * @param menu the context menu that is being built
     * @param v the view for which the context menu is being built
     * @param menuInfo Extra information about the item for which the
     *                 context menu should be shown.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        Log.d(TAG, "onCreateContextMenu called");
        if (v.getId() == R.id.myPlaces_listView) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(mCurrentPlaces.get(info.position).getName());
            menu.add(Menu.NONE, 0, 0, "Share");
            menu.add(Menu.NONE, 1, 1, "Delete");
        }
    }

    /**
     * Call when the context menu item is selected, get the selected item
     * to see if the user want to share or delete a place.
     *
     * @param item the context menu item that was selected
     * @return whether the call was proceed.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuIndex = item.getItemId();
        final String placeName = mCurrentPlaces.get(info.position).getName();
        String placeID = mCurrentPlaces.get(info.position).getPlaceID();
        String placeAddress = mCurrentPlaces.get(info.position).getAddress();
        if (isOnline()) {
            if (menuIndex == 0) {
                String content = placeName + "\n" + placeAddress;
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, content);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            } else if (menuIndex == 1) {
                ParseQuery<ParsePlace> query = ParseQuery.getQuery(ParsePlace.class);
                query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                query.whereEqualTo("placeID", placeID);
                query.getFirstInBackground(new GetCallback<ParsePlace>() {
                    @Override
                    public void done(ParsePlace object, ParseException e) {
                        if (e == null) {
                            object.deleteInBackground();
                            mCurrentPlaces.remove(info.position);
                            displayList();
                            Toast.makeText(getApplication(), placeName + " is deleted",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Log.d(TAG, "Unable to delete " + placeName);
                        }
                    }
                });
            }
        } else {
            Toast.makeText(this, "No network connection available", Toast.LENGTH_LONG);
        }
        return true;
    }

    /**
     * Display the user's current list of places on the user interface
     */
    private void displayList() {
        ListAdapter listAdapter = new MyAdapter(this, mCurrentPlaces);
        ListView listView = (ListView) findViewById(R.id.myPlaces_listView);
        listView.setAdapter(listAdapter);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place place = (Place) parent.getItemAtPosition(position);

                // View page info activity on item click
                Intent intent = new Intent(MyPlacesActivity.this, PlaceInfoActivity.class);
                intent.putExtra("placeID", place.getPlaceID());
                intent.putExtra("placeSource", place.getContentResource());

                // if the place content source was manually added by the user
                // then put the extra information with the intent
                if (place.getContentResource().equals("userContent")) {
                    intent.putExtra("placeName", place.getName());
                    intent.putExtra("placeAddress", place.getAddress());
                    intent.putExtra("placeType", place.getMainType());
                    intent.putExtra("placePhone", place.getPhoneNumber());
                    intent.putExtra("placeDescription", place.getDescription());
                }
                startActivity(intent);
            }
        });
    }

    /**
     * Add place button on click listener, launch a new activity
     * for the user to add a new place.
     * @param view reference to the widget that was clicked on.
     */
    public void addPlaceOnClick(View view) {
        Intent intent = new Intent(this, AddPlaceActivity.class);
        startActivity(intent);
    }


}
