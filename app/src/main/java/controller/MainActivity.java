package controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.hieunguyen725.myplaces.R;

/**
 * Author: Hieu Nguyen
 *
 * Main activity, this activity will choose which activity
 * to show the user throughout the application.
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    protected static Intent sCurrentIntent;

    /**
     * On Create method to initialize and inflate the activity's
     * user interface, also to choose which user's intent activity to
     * start next.
     * @param savedInstanceState Bundle containing the data most recently
     *                           saved data through onSaveInstanceState,
     *                           null if nothing was saved.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check whether if user's intent activity is null, if it is
        // start the related search activity, else start the user's
        // intent activity.
        if (sCurrentIntent == null) {
            Intent intentNearby = new Intent(this, RelatedSearchActivity.class);
            startActivity(intentNearby);
        } else {
            startActivity(sCurrentIntent);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

}
