package controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hieunguyen725.myplaces.R;

import java.util.List;

import model.User;
import model.database.UserDataSource;

/**
 * Author: Hieu Nguyen
 *
 * This is an activity that will allow the user to log into the
 * application. If the user does not have an account, the activity
 * allow them to move onto another activity to register.
 */
public class LogInActivity extends AppCompatActivity {

    protected static String sUser;

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
        SharedPreferences sharedPreferences =
                this.getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        boolean loggedIn = sharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false);
        if (loggedIn) {
            sUser = sharedPreferences.getString(getString(R.string.LOGGEDIN_USER), "");
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
     * from the database once the login button is clicked. If it is a
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


        mUserName = (EditText) findViewById(R.id.login_username);
        mPassword = (EditText) findViewById(R.id.login_password);


        // Check if the user exists in the database
        UserDataSource dataSource = new UserDataSource(this);
        List<User> users = dataSource.findAllUser();
        boolean validUser = false;
        for (User user : users) {
            if (user.getUsername().equals(mUserName.getText().toString())
                    && user.getPassword().equals(mPassword.getText().toString())) {
                validUser = true;
            }
        }
        // if it is a valid user, allow them to log in
        if (validUser) {
            SharedPreferences sharedPreferences =
                    this.getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.LOGGEDIN), true);
            editor.putString(getString(R.string.LOGGEDIN_USER), mUserName.getText().toString());
            editor.commit();
            sUser = mUserName.getText().toString();
            Intent intent = new Intent(this, MainActivity.class);
            Toast.makeText(this, "Logging in", Toast.LENGTH_LONG).show();
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Incorrect Username/Password", Toast.LENGTH_LONG).show();
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
}
