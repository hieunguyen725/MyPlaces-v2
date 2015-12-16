package com.example.hieunguyen725.myplaces;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import java.util.Random;

import controller.LogInActivity;

/**
 * Author: Hieu Nguyen
 *
 * This is a test class for Login Activity.
 * It will perform different tests when the user visit the login
 * activity, such as launching the create account button, log in
 * with a valid account, and an invalid account.
 */
public class LogInActivityTest extends ActivityInstrumentationTestCase2<LogInActivity> {
    private Solo solo;

    /**
     * Construct a new LoginActivity test.
     */
    public LogInActivityTest() {
        super(LogInActivity.class);
    }

    /**
     * Set up and initialize solo object for the tests.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * Test launching the Login activity.
     */
    public void testLaunchActivity() {
        solo.unlockScreen();
        boolean title = solo.searchText("MyPlaces");
        boolean login = solo.searchButton("Log In");
        boolean createAccount = solo.searchText("Create Account");
        assertTrue("Test launch log in activity failed", title && login && createAccount);
    }

    /**
     * Test launching the register page via the create
     * account button.
     */
    public void testCreateAccountButton() {
        solo.clickOnButton("Create Account");
        boolean title = solo.searchText("Register");
        assertTrue("Test launch register via button failed", title);
    }

    /**
     * Test log in with a valid user account.
     */
    public void testValidAccountLogIn() {
        int num = new Random().nextInt(1000000);
        String username = "user" + num;
        String password = "password";
        solo.clickOnButton("Create Account");
        solo.enterText(0, username);
        solo.enterText(1, password);
        solo.enterText(2, password);
        solo.clickOnButton("Create");
        solo.enterText(0, username);
        solo.enterText(1, password);
        solo.clickOnButton("Log In");
        boolean loggedIn = solo.searchText("Logging in");
        solo.clickOnMenuItem("Log Out");
        assertTrue("Test log in with valid account failed", loggedIn);
    }

    /**
     * Test log in with an invalid user account.
     */
    public void testInvalidAccountLogIn() {
        int num = new Random().nextInt(1000000);
        String username = "user" + num;
        String password = "password";
        solo.enterText(0, username);
        solo.enterText(1, password);
        solo.clickOnButton("Log In");
        boolean invalid = solo.searchText("Incorrect Username/Password");
        assertTrue("Test log in with invalid account failed", invalid);
    }

    /**
     * Finish and close the testing activity/
     * @throws Exception
     */
    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
