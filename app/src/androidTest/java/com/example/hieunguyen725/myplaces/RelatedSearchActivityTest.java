package com.example.hieunguyen725.myplaces;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import controller.RelatedSearchActivity;

/**
 * Author: Hieu Nguyen
 * This is a test class for Related Search activity.
 * It will perform different tests base on the user's searches, such as
 * an address or location name search. Tests also include searches
 * for a places around a location and invalid search.
 */
public class RelatedSearchActivityTest extends ActivityInstrumentationTestCase2<RelatedSearchActivity> {
    private Solo solo;

    /**
     * Construct a new Related Search activity test.
     */
    public RelatedSearchActivityTest() {
        super(RelatedSearchActivity.class);
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
     * Test launching a Related Search activity.
     */
    public void testLaunchActivity() {
        solo.unlockScreen();
        boolean navBarMenu = solo.searchText("Search") &&
                solo.searchText("Nearby") && solo.searchText("My Places");
        boolean searchInput = solo.searchEditText("Search location");
        assertTrue("Failed to launch Related Search activity", navBarMenu && searchInput);
    }

    /**
     * Test searching a street or location address.
     */
    public void testSearchAddress() {
        solo.enterText(0, "400 Broad St, Seattle, WA 98109");
        solo.clickOnButton("Search");
        boolean found = solo.searchText("400 Broad St, Seattle, WA 98109");
        assertTrue("Failed to test search with an address", found);
    }

    /**
     * Test searching a place name.
     */
    public void testSearchPlaceName() {
        solo.enterText(0, "Space Needle");
        solo.clickOnButton("Search");
        boolean found = solo.searchText("400 Broad St, Seattle, WA 98109") &&
                solo.searchText("Space Needle");
        assertTrue("Failed to test search with a place name", found);
    }

    /**
     * Test searching for places around a location,
     * then view the info about a place, also testing
     * list display on different orientations.
     */
    public void testSearchAround() {
        solo.enterText(0, "Food around UW");
        solo.clickOnButton("Search");
        boolean found = solo.searchText("The 8") && solo.searchText("Reboot");
        assertTrue("Failed to test search places around a location", found);
        solo.clickOnText("Reboot");
        solo.sleep(2000);
        boolean info = solo.searchText("View Website") && solo.searchText("Save");
        assertTrue("Failed to test search places around a location and view info", info);
        solo.goBack();
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.sleep(2000);
        assertTrue("Failed to test search places around a location in landscape orientation", found);
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.sleep(2000);
    }

    /**
     * Test searching with an invalid input.
     */
    public void testInValidSearch() {
        solo.enterText(0, "");
        solo.clickOnButton("Search");
        boolean invalid = solo.searchText("Invalid search phrase");
        assertTrue("Failed to test invalid search input", invalid);
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
