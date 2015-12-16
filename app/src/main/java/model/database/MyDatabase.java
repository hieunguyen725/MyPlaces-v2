package model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Author: Hieu Nguyen
 *
 * This is class representing the database schema including
 * the users and places table, extending SQLiteOpenHelper.
 */
public class MyDatabase extends SQLiteOpenHelper {

    private static final String TAG = "MyDatabase";

    private static final String DATABASE_NAME =  "myplaces.db";
    private static final int DATABASE_VERSION = 7;

    /**
     * This is a class representing the User table in the
     * database schema.
     */
    public static final class UserTable {
        public static final String NAME = "user";

        /**
         * This is a class representing the columns of the User
         * table in the database schema.
         */
        public static final class Columns {
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
        }
    }

    /**
     * Query String to create User table, having
     * the username as the primary key.
     */
    private static final String CREATE_USER_TABLE =
            "CREATE TABLE " + UserTable.NAME + " (" +
                    UserTable.Columns.USERNAME + " TEXT PRIMARY KEY, " +
                    UserTable.Columns.PASSWORD + " TEXT " +
                    ")";

    /**
     * This is class representing a Places table in the
     * database schema.
     */
    public static final class PlacesTable {
        public static final String NAME = "places";

        /**
         * This is a class representing the columns of the Places
         * table in the database schema.
         */
        public static final class Columns {
            public static final String PLACE_ID = "placeID";
            public static final String USERNAME = "username";
            public static final String NAME = "name";
            public static final String ADDRESS = "address";
            public static final String MAIN_TYPE = "mainType";
            public static final String DESCRIPTION = "description";
            public static final String PHONE_NUMBER = "phoneNumber";
            public static final String ICON_URL = "iconURL";
            public static final String CONTENT_RESOURCE = "contentResource";
        }
    }

    /**
     * Query String to create a places table, having
     * the placeID and username as the composite
     * primary keys.
     */
    private static final String CREATE_PLACES_TABLE =
            "CREATE TABLE " + PlacesTable.NAME + " (" +
                    PlacesTable.Columns.PLACE_ID + " TEXT, " +
                    PlacesTable.Columns.USERNAME + " TEXT, " +
                    PlacesTable.Columns.NAME + " TEXT, " +
                    PlacesTable.Columns.ADDRESS + " TEXT, " +
                    PlacesTable.Columns.MAIN_TYPE + " TEXT, " +
                    PlacesTable.Columns.DESCRIPTION + " TEXT, " +
                    PlacesTable.Columns.PHONE_NUMBER + " TEXT, " +
                    PlacesTable.Columns.ICON_URL + " TEXT, " +
                    PlacesTable.Columns.CONTENT_RESOURCE + " TEXT, \n" +
                    "PRIMARY KEY (" +
                    PlacesTable.Columns.PLACE_ID + ", " +
                    PlacesTable.Columns.USERNAME + ")" +
                    ")";


    /**
     * A constructor of the my database schema taking the
     * application's context as parameter.
     * @param context the application's context.
     */
    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * onCreate method, call to create new tables for the database,
     * not manually called on.
     *
     * @param sqLiteDatabase reference to the application's
     *                       SQLite database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_PLACES_TABLE);
        Log.i(TAG, "Tables have been created");
    }

    /**
     * onUpgrade method, call to update the database schema by comparing
     * the version numbers, if the version number is higher than the previous
     * one, do an update by dropping the existing tables and create new tables
     * by calling onCreate method.
     * Not manually called on.
     * @param sqLiteDatabase reference to the application's SQLite database.
     * @param oldVersion old version number of the database schema.
     * @param newVersion new version number of the database schema.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserTable.NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlacesTable.NAME);
        onCreate(sqLiteDatabase);
    }
}
