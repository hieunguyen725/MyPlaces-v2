package model.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import controller.NearbySearchActivity;
import model.Place;

/**
 * Author: Hieu Nguyen
 *
 * This is class representing a helper class
 * to parse JSON text content retrieved from the
 * http reponse.
 */
public class JSONParser {

    /**
     * Given the JSON string content and a search type, parse the content
     * and return a list of all places from the content.
     * @param jsonContent the JSON string content received from the http
     *               reponse.
     * @param searchType the type of search (related search or nearby search)
     * @return A list of all places parsed from the JSON content, null if none.
     */
    public List<Place> searchParse(String jsonContent, String searchType) {
        List<Place> resultPlaces = new ArrayList<Place>();
        Log.i(NearbySearchActivity.TAG, "starting to parse");
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);
            JSONArray results = jsonObject.getJSONArray("results");
            // if there is one or more place in the result,
            // start parsing the new places.
            if (results.length() > 0) {
                Log.i(NearbySearchActivity.TAG, "results > 0");
                // For each place in the result, parse that result place
                // and add it to the list of places.
                for (int i = 0; i < results.length(); i++) {
                    Place newPlace = new Place();
                    JSONObject result = results.getJSONObject(i);
                    newPlace.setPlaceID(result.getString("place_id"));
                    newPlace.setName(result.getString("name"));
                    if (searchType.equalsIgnoreCase("nearby")) {
                       newPlace.setAddress(result.getString("vicinity"));
                    } else if (searchType.equalsIgnoreCase("related")) {
                        newPlace.setAddress(result.getString("formatted_address"));
                    }
                    // Get the location to parse the latitude and longitude.
                    JSONObject location = result.getJSONObject("geometry").getJSONObject("location");
                    newPlace.setLat(location.getDouble("lat"));
                    newPlace.setLng(location.getDouble("lng"));
                    newPlace.setIconURL(result.getString("icon"));
                    newPlace.setMainType(result.getJSONArray("types").getString(0).replace("_", " "));
                    newPlace.setContentResource("onlineContent");
                    resultPlaces.add(newPlace);
                }
                return resultPlaces;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Given the JSON String content of information about a place, parse the
     * content a return a place with the content's values.
     * @param jsonContent the JSON String content of information about a place.
     * @return a place with parsed values from the JSON, null if error.
     */
    public Place infoParse(String jsonContent) {
        Place newPlace = new Place();
        try {
            // Retrieve basic information about the place.
            JSONObject jsonObject = new JSONObject(jsonContent);
            JSONObject resultInfo = jsonObject.getJSONObject("result");
            newPlace.setPlaceID(resultInfo.getString("place_id"));
            newPlace.setName(resultInfo.getString("name"));
            newPlace.setAddress(resultInfo.getString("formatted_address"));
            JSONObject location = resultInfo.getJSONObject("geometry").getJSONObject("location");
            newPlace.setLat(location.getDouble("lat"));
            newPlace.setLng(location.getDouble("lng"));
            newPlace.setIconURL(resultInfo.getString("icon"));
            newPlace.setMainType(resultInfo.getJSONArray("types").getString(0).replace("_", " "));
            // Retrieve extra information about the place.
            if (resultInfo.has("photos")) {
                newPlace.setImageReferences(new ArrayList<String>());
                JSONArray photos = resultInfo.getJSONArray("photos");
                for (int i = 0; i < photos.length(); i++) {
                    JSONObject photo = photos.getJSONObject(i);
                    newPlace.getImageReferences().add(photo.getString("photo_reference"));
                }
            }
            if (resultInfo.has("website")) {
                String websiteURL = resultInfo.getString("website");
                newPlace.setWebsiteURL(websiteURL);
            }
            if (resultInfo.has("opening_hours")) {
                JSONObject openingHours = resultInfo.getJSONObject("opening_hours");
                JSONArray weekdayText = openingHours.getJSONArray("weekday_text");
                String weekdayHours = "";
                for (int i = 0; i < weekdayText.length(); i++) {
                    weekdayHours += weekdayText.getString(i) + "\n";
                }
                newPlace.setOpeningHours(weekdayHours);
            }
            if (resultInfo.has("reviews")) {
                JSONArray reviews = resultInfo.getJSONArray("reviews");
                String reviewsText = "";
                // for each review, retrieve the review text and concatenate
                // them into a string of reviews with a number in front of
                // each review.
                for (int i = 0; i < reviews.length(); i++) {
                    JSONObject review = reviews.getJSONObject(i);
                    reviewsText += (i + 1) + ".   " + review.getString("text") + "\n\n";
                }
                newPlace.setReviews(reviewsText);
            }
            if (resultInfo.has("formatted_phone_number")) {
                String phone = resultInfo.getString("formatted_phone_number");
                newPlace.setPhoneNumber(phone);
            }
            newPlace.setContentResource("onlineContent");
            return newPlace;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
