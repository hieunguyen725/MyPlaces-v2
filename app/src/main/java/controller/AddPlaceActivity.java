package controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hieunguyen725.myplaces.R;

import model.database.PlacesDataSource;
import model.Place;

/**
 * Author: Hieu Nguyen
 *
 * This is an activity that will allow the user to add a new place
 * to their places list. The new location will contain a name, address
 * location type, phone number and a short description.
 */
public class AddPlaceActivity extends AppCompatActivity {

    private static final String ICON_URL = "https://maps.gstatic.com/mapfiles/" +
            "place_api/icons/geocode-71.png";

    /**
     * On Create method to initialize and inflate the activity's
     * user interface.
     * @param savedInstanceState Bundle containing the data most recently
     *                           saved data through onSaveInstanceState,
     *                           null if nothing was saved.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
    }

    /**
     * Initialize the option menu content for this activity.
     * @param menu The option menu object to be inflated with the menu layout.
     * @return true to display the menu, false to not display the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_place, menu);
        return true;
    }

    /**
     * Handle selected menu option by the user.
     * @param item The selected menu item by the user.
     * @return true if menu item process was taken, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.log_out) {
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
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Save button on click listener, listen for the save button click
     * to retrieve the data from the user's input and save the new place
     * to the SQLite database.
     * @param view the reference or view of the widget
     *             that was clicked
     */
    public void saveButtonOnClick(View view) {
        EditText nameInput = (EditText) findViewById(R.id.addPlace_name_input);
        EditText addressInput = (EditText) findViewById(R.id.addPlace_address_input);
        EditText typeInput = (EditText) findViewById(R.id.addPlace_type_input);
        EditText phoneInput = (EditText) findViewById(R.id.addPlace_phone_input);
        EditText descriptionInput = (EditText) findViewById(R.id.addPlace_description_input);

        // retrieve the user's inputs
        String name = nameInput.getText().toString();
        String address = addressInput.getText().toString();
        String type = typeInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String description = descriptionInput.getText().toString();

        // check whether any input is blank, if create a new place
        // it into the database
        if (name.equals("") || address.equals("") | type.equals("")
                || phone.equals("") || description.equals("")) {
            Toast.makeText(this, "Please fill in all inputs", Toast.LENGTH_LONG).show();
        } else {
            // concatenate the user's input hashCode integers as a string for placeID, this way,
            // only the exact same places will have the same ID and the SQLite table will be able
            // to recognize that same place ID to avoid adding the same place
            String placeID = "" + name.hashCode() + address.hashCode() + type.hashCode()
                    + phone.hashCode() + description.hashCode();
            Place place = new Place(placeID, LogInActivity.sUser, name, address, type,
                    ICON_URL, description, phone);

            // store the new place to the database
            place.setContentResource("userContent");
            PlacesDataSource placesDataSource = new PlacesDataSource(this);
            placesDataSource.create(place);
            Toast.makeText(this, "Place Saved", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
