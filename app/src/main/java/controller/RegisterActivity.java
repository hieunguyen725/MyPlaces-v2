package controller;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hieunguyen725.myplaces.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Author: Hieu Nguyen
 *
 * This is an activity that will allow the user to register for
 * a new user account by putting in their new username and password.
 * This class will then store the new data onto Parse.
 */
public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "RegisterActivity";

    /**
     * On Create method to initialize and inflate the activity's
     * user interface.
     *
     * @param savedInstanceState Bundle containing the data most recently
     *                           saved data through onSaveInstanceState,
     *                           null if nothing was saved.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    /**
     * Initialize the option menu content for this activity
     *
     * @param menu The option menu object to be inflated with the menu layout
     * @return true to display the menu, false to not display the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return true;
    }

    /**
     * Handle selected menu option by the user
     *
     * @param item The selected menu item by the user
     * @return true if menu item process was taken, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    /**
     * Retrieve the user's input including username and password, validate the
     * new username then store the new username and password is the new account
     * is valid.
     *
     * @param view reference to the widget that was clicked on.
     */
    public void createButtonOnClick(View view) {
        if (isOnline()) {
            EditText username = (EditText) findViewById(R.id.register_username);
            EditText password = (EditText) findViewById(R.id.register_password);
            EditText confirmPassword = (EditText) findViewById(R.id.register_confirm_password);

            // validate the user's inputs
            if (password.getText().toString().equals("") ||
                    confirmPassword.getText().toString().equals("") ||
                    username.getText().toString().equals("")) {
                Toast.makeText(this, "Please fill in all inputs", Toast.LENGTH_LONG).show();
            } else if (username.getText().toString().length() < 5 || password.getText().toString().length() < 5) {
                Toast.makeText(this, "Username/Password must be at least 5 characters", Toast.LENGTH_LONG).show();
            } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                Toast.makeText(this, "Password/Confirm Password do not match", Toast.LENGTH_LONG).show();
            } else {
                ParseUser user = new ParseUser();
                user.setUsername(username.getText().toString());
                user.setPassword(password.getText().toString());
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplication(), "Account created, please log in.",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplication(), "Username already exist",
                                    Toast.LENGTH_LONG).show();
                            Log.d(TAG, e.toString());
                        }
                    }
                });
            }
        } else {
            Toast.makeText(this, "No network connection available", Toast.LENGTH_LONG).show();
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
}
