package model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import model.Place;

/**
 * Author: Hieu Nguyen
 *
 * This is a class representing a source of places data. It helps
 * with connecting the application's SQLite database to retrieve
 * and insert new data for the Place table.
 */
public class PlacesDataSource {
    public static final String TAG = "PlacesDataSource";

    private SQLiteOpenHelper mSQLiteOpenHelper;
    private SQLiteDatabase mSQLiteDatabase;

    /**
     * String array representing all the columns for in the
     * places table of the database.
     */
    private static final String[] allColumns = {
            MyDatabase.PlacesTable.Columns.PLACE_ID,
            MyDatabase.PlacesTable.Columns.USERNAME,
            MyDatabase.PlacesTable.Columns.NAME,
            MyDatabase.PlacesTable.Columns.ADDRESS,
            MyDatabase.PlacesTable.Columns.MAIN_TYPE,
            MyDatabase.PlacesTable.Columns.DESCRIPTION,
            MyDatabase.PlacesTable.Columns.PHONE_NUMBER,
            MyDatabase.PlacesTable.Columns.ICON_URL,
            MyDatabase.PlacesTable.Columns.CONTENT_RESOURCE
    };

    /**
     * Constructing a new PlacesDataSource.
     * @param context reference to the application's context.
     */
    public PlacesDataSource(Context context) {
        mSQLiteOpenHelper = new MyDatabase(context);
        mSQLiteDatabase = mSQLiteOpenHelper.getWritableDatabase();
    }


    /**
     * Close the database.
     */
    public void close() {
        Log.i(TAG, "Database closed");
        mSQLiteOpenHelper.close();
    }

    /**
     * Given a place, insert the new place with its values
     * into the Place table.
     * @param place reference to the place to be insert into
     *              the database table.
     */
    public void create(Place place) {
        ContentValues values = new ContentValues();
        values.put(MyDatabase.PlacesTable.Columns.PLACE_ID, place.getPlaceID());
        values.put(MyDatabase.PlacesTable.Columns.USERNAME, place.getUsername());
        values.put(MyDatabase.PlacesTable.Columns.NAME, place.getName());
        values.put(MyDatabase.PlacesTable.Columns.ADDRESS, place.getAddress());
        values.put(MyDatabase.PlacesTable.Columns.MAIN_TYPE, place.getMainType());
        values.put(MyDatabase.PlacesTable.Columns.DESCRIPTION, place.getDescription());
        values.put(MyDatabase.PlacesTable.Columns.PHONE_NUMBER, place.getPhoneNumber());
        values.put(MyDatabase.PlacesTable.Columns.ICON_URL, place.getIconURL());
        values.put(MyDatabase.PlacesTable.Columns.CONTENT_RESOURCE, place.getContentResource());
        mSQLiteDatabase.insert(MyDatabase.PlacesTable.NAME, null, values);
    }

    /**
     * Given a selection String to limit the table's tuples, find all the place
     * belonging the a username then return the places.
     * @param selection a username selection to limit the table's tuples.
     * @return  a list of all the places with its username matching
     *          the username.
     */
    public List<Place> findUserPlaces(String selection) {
        List<Place> places = new ArrayList<Place>();
        Cursor cursor = mSQLiteDatabase.query(MyDatabase.PlacesTable.NAME, allColumns,
                selection, null, null, null, null);
        Log.i(TAG, "Returned " + cursor.getCount() + " rows");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Place place = new Place();
                place.setPlaceID(cursor.getString(cursor.getColumnIndex(MyDatabase.PlacesTable.Columns.PLACE_ID)));
                place.setUsername(cursor.getString(cursor.getColumnIndex(MyDatabase.PlacesTable.Columns.USERNAME)));
                place.setName(cursor.getString(cursor.getColumnIndex(MyDatabase.PlacesTable.Columns.NAME)));
                place.setAddress(cursor.getString(cursor.getColumnIndex(MyDatabase.PlacesTable.Columns.ADDRESS)));
                place.setMainType(cursor.getString(cursor.getColumnIndex(MyDatabase.PlacesTable.Columns.MAIN_TYPE)));
                place.setDescription(cursor.getString(cursor.getColumnIndex(MyDatabase.PlacesTable.Columns.DESCRIPTION)));
                place.setPhoneNumber(cursor.getString(cursor.getColumnIndex(MyDatabase.PlacesTable.Columns.PHONE_NUMBER)));
                place.setIconURL(cursor.getString(cursor.getColumnIndex(MyDatabase.PlacesTable.Columns.ICON_URL)));
                place.setContentResource(cursor.getString(cursor.getColumnIndex(MyDatabase.PlacesTable.Columns.CONTENT_RESOURCE)));
                places.add(place);
            }
        }
        return places;
    }

    /**
     * Given a username and placeID, delete that place for the user from
     * the sqlite database.
     *
     * @param username the username that saved the place.
     * @param placeID the placeID of the place.
     */
    public void deletePlace(String username, String placeID) {
        String selection = MyDatabase.PlacesTable.Columns.PLACE_ID + "=? AND "
                + MyDatabase.PlacesTable.Columns.USERNAME + "=?";
        String[] arguments = new String[]{placeID, username};
        mSQLiteDatabase.delete(MyDatabase.PlacesTable.NAME, selection, arguments);
    }
}

