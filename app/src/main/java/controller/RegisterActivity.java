package controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hieunguyen725.myplaces.R;

import java.util.List;

import model.database.UserDataSource;
import model.User;

/**
 * Author: Hieu Nguyen
 *
 * This is an activity that will allow the user to register for
 * a new user account by putting in their new username and password.
 * This class will then store the new data into the database.
 */
public class RegisterActivity extends AppCompatActivity {


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
        setContentView(R.layout.activity_register);
    }

    /**
     * Initialize the option menu content for this activity
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
     * @param view reference to the widget that was clicked on.
     */
    public void createButtonOnClick(View view) {
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
            // check to see if the user is already exist in the database
            UserDataSource dataSource = new UserDataSource(this);
            List<User> users = dataSource.findAllUser();
            boolean validAccount = true;
            for (User user : users) {
                if (user.getUsername().equals(username.getText().toString())) {
                    validAccount = false;
                }
            }
            if (validAccount) {
                dataSource.create(new User(username.getText().toString(),
                        password.getText().toString()));
                finish();
                Toast.makeText(this, "Account created, please log in.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Username already exist", Toast.LENGTH_LONG).show();
            }
        }
    }
}
