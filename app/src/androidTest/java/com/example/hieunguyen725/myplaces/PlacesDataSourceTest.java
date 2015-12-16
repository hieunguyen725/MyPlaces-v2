package com.example.hieunguyen725.myplaces;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import java.util.List;
import java.util.Random;

import controller.MainActivity;
import model.Place;
import model.database.PlacesDataSource;

/**
 * Author: Hieu Nguyen
 *
 * Test class for model - PlaceDataSource
 * This class will test the places data source from the SQLite database, using
 * the main activity to access the context. Throughout the tests, it will
 * also use other model classes as part of setting up the test cases.
 */
public class PlacesDataSourceTest extends ActivityInstrumentationTestCase2<MainActivity>{

    /**
     * Construct a new PlacesDataSource test.
     */
    public PlacesDataSourceTest() {
        super(MainActivity.class);
    }

    /**
     * Test case for constructing a new PlaceDataSource.
     */
    public void testConstructor() {
        Context context = this.getInstrumentation()
                .getTargetContext().getApplicationContext();
        PlacesDataSource placesDataSource = new PlacesDataSource(context);
        assertNotNull("Failed to construct UserDataSource", placesDataSource);
    }

    /**
     * Test case for creating a new place for a username in the SQLite
     * database using the UserDataSource
     */
    public void testCreate() {
        Context context = this.getInstrumentation()
                .getTargetContext().getApplicationContext();
        PlacesDataSource placesDataSource = new PlacesDataSource(context);
        Random rand = new Random();
        int num = rand.nextInt(10000000);
        String placeName = "testplace" + num;
        String username = "testing123";
        Place place = new Place();
        place.setUsername(username);
        place.setName(placeName);
        placesDataSource.create(place);
        List<Place> retrievePlaces = placesDataSource
                .findUserPlaces("username = " + "'" + username + "'");
        boolean contains = false;
        for (Place currentPlace : retrievePlaces) {
            if (currentPlace.getName().equals(placeName) &&
                    currentPlace.getUsername().equals(username)) {
                contains = true;
            }
        }
        assertTrue("Failed to create place in database", contains);
    }

    /**
     * Test case for finding all the current places that is saved
     * by a username in the SQLite database.
     */
    public void testFinalUserPlaces() {
        Context context = this.getInstrumentation()
                .getTargetContext().getApplicationContext();
        PlacesDataSource placesDataSource = new PlacesDataSource(context);
        String username = "testing123";
        List<Place> userPlaces = placesDataSource
                .findUserPlaces("username = " + "'" + username + "'");
        assertTrue("Failed to retrieve find all list", userPlaces.size() > 0);
    }

    /**
     * Test case for deleting a given place for a username
     * from the SQLite database.
     */
    public void testDelete() {
        Context context = this.getInstrumentation()
                .getTargetContext().getApplicationContext();
        PlacesDataSource placesDataSource = new PlacesDataSource(context);
        String username = "testing123";
        String placeName = "testplace123";
        String placeID = "" + username.hashCode() + placeName.hashCode();
        Place place = new Place();
        place.setUsername(username);
        place.setName(placeName);
        place.setPlaceID(placeID);
        placesDataSource.create(place);
        int sizeBefore = placesDataSource
                .findUserPlaces("username = " + "'" + username + "'").size();
        placesDataSource.deletePlace(username, placeID);
        int sizeAfter = placesDataSource
                .findUserPlaces("username = " + "'" + username + "'").size();
        assertTrue("Failed to delete place", sizeAfter == (sizeBefore - 1));
    }
}
