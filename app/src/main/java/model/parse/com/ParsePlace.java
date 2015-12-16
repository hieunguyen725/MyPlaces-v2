package model.parse.com;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import model.Place;

/**
 * Created by hieunguyen725 on 12/15/2015.
 */
@ParseClassName("Place")
public class ParsePlace extends ParseObject {

    public ParsePlace() {
        super();
    }

    public void setData(Place place) {
        put("placeID", place.getPlaceID());
        put("username", place.getUsername());
        put("name", place.getName());
        put("address", place.getAddress());
        put("mainType", place.getMainType());
        put("description", place.getDescription());
        put("phoneNumber", place.getIconURL());
        put("iconURL", place.getIconURL());
        put("contentResource", place.getContentResource());
    }

    public String getPlaceID() {
        return getString("placeID");
    }

    public String getName() {
        return getString("name");
    }

    public String getUsername() {
        return getString("username");
    }

    public String getAddress() {
        return getString("address");
    }

    public String getMainType() {
        return getString("mainType");
    }

    public String getDescription() {
        return getString("description");
    }

    public String getPhoneNumber() {
        return getString("phoneNumber");
    }

    public String getIconURL() {
        return getString("iconURL");
    }

    public String getContentResource() {
        return getString("contentResource");
    }

    public Place createModel() {
        Place place = new Place(getPlaceID(), getUsername(),
                getName(), getAddress(), getMainType(),
                getIconURL(), getDescription(), getPhoneNumber());
        place.setContentResource(getContentResource());
        return place;
    }

}
