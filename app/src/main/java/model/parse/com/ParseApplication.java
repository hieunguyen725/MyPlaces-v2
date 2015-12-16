package model.parse.com;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by hieunguyen725 on 12/15/2015.
 */
public class ParseApplication extends Application {
    public static final String APPLICATION_ID = "9W0bygbfeGmmvpABKDJZCmY64SIj4ZN3VkFNk5vC";
    public static final String CLIENT_KEY = "VpEK29Z1eGeJmQwZMzRxzm7c7njUQaRcntuRLbdx";

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(ParsePlace.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
    }
}
