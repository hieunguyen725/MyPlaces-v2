package com.example.hieunguyen725.myplaces;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import java.util.Random;

import controller.RegisterActivity;

/**
 * Author: Hieu Nguyen
 *
 * This is a test class for Register Activity.
 * It will perform different tests when register a user such as
 * a simple valid register and invalid inputs.
 */
public class RegisterActivityTest extends ActivityInstrumentationTestCase2<RegisterActivity> {
    private Solo solo;
    private String username;
    private String password;

    /**
     * Construct a new RegisterActivity test.
     */
    public RegisterActivityTest() {
        super(RegisterActivity.class);
    }

    /**
     * Set up and initialize values for the tests.
     * @throws Exception
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
        int num = new Random().nextInt(10000000);
        username = "testusername" + num;
        password = "testpassword";
    }

    /**
     * Test launching the register activity
     */
    public void testLaunchActivity() {
        solo.unlockScreen();
        boolean title = solo.searchText("Create Account");
        boolean confirmPass = solo.searchEditText("Confirm Password");
        assertTrue("Test Launch RegisterActivity failed", title && confirmPass);
    }

    /**
     * Test registering a valid account.
     */
    public void testRegister() {
        solo.enterText(0, username);
        solo.enterText(1, password);
        solo.enterText(2, password);
        solo.clickOnButton("Create");
        boolean created = solo.searchText("Account created, please log in.");
        assertTrue("Test Create new account failed", created);
    }

    /**
     * Test for registering with password and confirm
     * password not matching.
     */
    public void testInvalidPassword() {
        solo.enterText(0, username);
        solo.enterText(1, password);
        solo.enterText(2, password + "1");
        solo.clickOnButton("Create");
        boolean unmatch = solo.searchText("Password/Confirm Password do not match");
        assertTrue("Test unmatch password failed", unmatch);
    }

    /**
     * Test for registering with invalid input length.
     */
    public void testInputLength() {
        String user = "hieu";
        String pass = "123123";
        solo.enterText(0, user);
        solo.enterText(1, pass);
        solo.enterText(2, pass);
        solo.clickOnButton("Create");
        boolean invalidLength = solo.searchText("Username/Password must be at least 5 characters");
        assertTrue("Test invalid input length failed", invalidLength);
    }

    /**
     * Test registering with empty input.
     */
    public void testEmptyInput() {
        solo.enterText(0, username);
        solo.enterText(1, password);
        solo.clickOnButton("Create");
        boolean empty = solo.searchText("Please fill in all inputs");
        assertTrue("Test empty input failed", empty);
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
