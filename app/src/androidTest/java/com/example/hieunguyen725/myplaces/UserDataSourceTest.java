package com.example.hieunguyen725.myplaces;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import java.util.List;
import java.util.Random;

import controller.MainActivity;
import model.User;
import model.database.UserDataSource;

/**
 * Author: Hieu Nguyen
 *
 * Test class for model - UserDataSource
 * This class will test the User data source from the SQLite database, using
 * the main activity to access the context. Throughout the tests, it will
 * also use other model classes as part of setting up the test cases.
 */
public class UserDataSourceTest extends ActivityInstrumentationTestCase2<MainActivity>{

    /**
     * Construct a new UserDataSource test.
     */
    public UserDataSourceTest() {
        super(MainActivity.class);

    }

    /**
     * Test case for constructing a new UserDataSource.
     */
    public void testConstructor() {
        Context context = this.getInstrumentation()
                .getTargetContext().getApplicationContext();
        UserDataSource userDataSource = new UserDataSource(context);
        assertNotNull("Failed to construct UserDataSource", userDataSource);
    }

    /**
     * Test case for creating a new user in the SQLite
     * database using the UserDataSource
     */
    public void testCreate() {
        Context context = this.getInstrumentation()
                .getTargetContext().getApplicationContext();
        UserDataSource userDataSource = new UserDataSource(context);
        Random rand = new Random();
        int number = rand.nextInt(10000000);
        String username = "testing" + number;
        String password = "123123";
        User user = new User(username, password);
        userDataSource.create(user);
        List<User> retrieveUsers = userDataSource.findAllUser();
        boolean contains = false;
        for (User currentUser : retrieveUsers) {
            if (currentUser.getUsername().equals(username)
                    && currentUser.getPassword().equals(password)) {
                contains = true;
                break;
            }
        }
        assertTrue("Failed to create user in database", contains);
    }

    /**
     * Test case for finding all the current users in the SQLite
     * database using the UserDataSource
     */
    public void testFindAll() {
        Context context = this.getInstrumentation()
                .getTargetContext().getApplicationContext();
        UserDataSource userDataSource = new UserDataSource(context);
        List<User> retrieveUsers = userDataSource.findAllUser();
        assertTrue("Failed to retrieve find all list", retrieveUsers.size() > 0);
    }
}
