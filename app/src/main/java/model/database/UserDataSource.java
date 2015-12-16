package model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import model.User;

/**
 * Author: Hieu Nguyen
 *
 * This is a class representing a source of places data. It helps
 * with connecting the application's SQLite database to retrieve
 * and insert new data for the User table.
 */
public class UserDataSource {
    public static final String TAG = "UserDataSource";

    SQLiteOpenHelper mSQLiteOpenHelper;
    SQLiteDatabase mSQLiteDatabase;

    /**
     * String array representing all the columns for in the
     * User table of the database.
     */
    private static final String[] allColumns = {
            MyDatabase.UserTable.Columns.USERNAME,
            MyDatabase.UserTable.Columns.PASSWORD
    };

    /**
     * Constructing a new UserDataSource.
     * @param context reference to the application's context.
     */
    public UserDataSource(Context context) {
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
     * Given a user, insert the new user with its values
     * into the User table.
     * @param user reference to the user to be insert into
     *              the database table.
     */
    public void create(User user) {
        ContentValues values = new ContentValues();
        values.put(MyDatabase.UserTable.Columns.USERNAME, user.getUsername());
        values.put(MyDatabase.UserTable.Columns.PASSWORD, user.getPassword());
        mSQLiteDatabase.insert(MyDatabase.UserTable.NAME, null, values);
    }

    /**
     * Find all the users in the User table from the database schema and
     * return them.
     * @return a list of all the users from the User table.
     */
    public List<User> findAllUser() {
        List<User> users = new ArrayList<User>();
        Cursor cursor = mSQLiteDatabase.query(MyDatabase.UserTable.NAME, allColumns,
        null, null, null, null, null);
        Log.i(TAG, "Returned " + cursor.getCount() + " rows");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                User user = new User(
                        cursor.getString(cursor.getColumnIndex(
                                MyDatabase.UserTable.Columns.USERNAME)),
                        cursor.getString(cursor.getColumnIndex(
                                MyDatabase.UserTable.Columns.PASSWORD)));
                users.add(user);
            }
        }
        return users;
    }
}
