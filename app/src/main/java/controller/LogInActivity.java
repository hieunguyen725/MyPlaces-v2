package controller;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hieunguyen725.myplaces.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Author: Hieu Nguyen
 *
 * This is an activity that will allow the user to log into the
 * application. If the user does not have an account, the activity
 * allow them to move onto another activity to register.
 */
public class LogInActivity extends AppCompatActivity {

    private EditText mUserName;
    private EditText mPassword;

    /**
     * On Create method to initialize and inflate the activity's
     * user interface
     * @param savedInstanceState Bundle containing the data most recently
     *                           saved data through onSaveInstanceState,
     *                           null if nothing was saved.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
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
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
        return true;
    }

    /**
     * Handle selected menu option by the user
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
     * Log in button listener, validate the user's username and password
     * from Parse once the login button is clicked. If it is a
     * valid user, allow them to log in and move to the next activity.
     * @param view reference of the widget that was clicked
     */
    public void loginOnClick(View view) {
        // Once the button is clicked, hide the soft keyboard
        // from the screen.
        InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        // Hide the keyboard from the screen unless it is forced to
        // open up as required for edit text for input.
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        if (isOnline()) {
            mUserName = (EditText) findViewById(R.id.login_username);
            mPassword = (EditText) findViewById(R.id.login_password);

            ParseUser.logInInBackground(mUserName.getText().toString(), mPassword.getText().toString(),
                    new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                Intent intent = new Intent(getApplication(), MainActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(getApplication(), "Logging in",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplication(), "Incorrect Username/Password",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "No network connection available", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Create account button listener, allow the user to move
     * into register activity to create an account once the button
     * is clicked.
     * @param view reference of the widget that was clicked
     */
    public void createAccountOnClick(View view) {
        mUserName = (EditText) findViewById(R.id.login_username);
        mPassword = (EditText) findViewById(R.id.login_password);
        mUserName.setText("");
        mPassword.setText("");
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
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
