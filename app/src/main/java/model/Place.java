package model;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Author: Hieu Nguyen
 *
 * This is class representing a place model, including
 * the place's information such as its name, address
 * phone number and images.
 */
public class Place {

    // Required fields in model.database
    private String mPlaceID;
    private String mUserName;
    private String mName;
    private String mAddress;
    private String mType;
    private String mIconURL;
    private String mDescription;
    private String mPhoneNumber;
    private String mContentResource;

    // Required fields, but not in model.database
    private Bitmap mIcon;
    private double mLat;
    private double mLng;

    // Optional fields
    private List<Bitmap> mPlaceImages;
    private List<String> mImageReferences;
    private String mWebsiteURL;
    private String mOpeningHours;
    private String mReviews;

    /**
     * Construct a new place with the given parameter values.
     * @param placeID id of this place.
     * @param username the username this place belong to.
     * @param name name of this place.
     * @param address address of this place.
     * @param mainType the type of place.
     * @param iconURL icon URL for this place.
     * @param description description of this place.
     * @param phoneNumber phone number of this place.
     */
    public Place(String placeID, String username, String name, String address, String mainType,
                 String iconURL, String description, String phoneNumber) {
        this.mPlaceID = placeID;
        this.mUserName = username;
        this.mName = name;
        this.mAddress = address;
        this.mType = mainType;
        this.mIconURL = iconURL;
        this.mPhoneNumber = phoneNumber;
        this.mDescription = description;
        this.mWebsiteURL = "Not available";
    }

    /**
     * Construct a new place with no information.
     */
    public Place() {
        this.mPhoneNumber = "Not available";
        this.mDescription = "Not available";
        this.mWebsiteURL = "Not available";
    }

    /**
     * Get this place's website URL.
     * @return the website URL.
     */
    public String getWebsiteURL() {
        return mWebsiteURL;
    }

    /**
     * Set this place's website URL.
     * @param websiteURL the website URL for this place.
     */
    public void setWebsiteURL(String websiteURL) {
        this.mWebsiteURL = websiteURL;
    }

    /**
     * Get this place's ID.
     * @return the place's ID.
     */
    public String getPlaceID() {
        return mPlaceID;
    }

    /**
     * Set this place's ID.
     * @param placeID the ID for this place.
     */
    public void setPlaceID(String placeID) {
        this.mPlaceID = placeID;
    }

    /**
     * Get this place's username.
     * @return the username this place belongs to.
     */
    public String getUsername() {
        return mUserName;
    }

    /**
     * Set this place's username.
     * @param username set the username for this place.
     */
    public void setUsername(String username) {
        this.mUserName = username;
    }

    /**
     * Get this place's name.
     * @return the name of  this place.
     */
    public String getName() {
        return mName;
    }

    /**
     * Set this place's name.
     * @param name the name for this place.
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Get this place's address.
     * @return the address of this place.
     */
    public String getAddress() {
        return mAddress;
    }

    /**
     * Set this place's address.
     * @param address the address for this place.
     */
    public void setAddress(String address) {
        this.mAddress = address;
    }

    /**
     * Get this place's type.
     * @return the type of place.
     */
    public String getMainType() {
        return mType;
    }

    /**
     * Set this place's type.
     * @param mainType the type for this place.
     */
    public void setMainType(String mainType) {
        this.mType = mainType;
    }

    /**
     * Get this place's latitude.
     * @return the latitude of this place.
     */
    public double getLat() {
        return mLat;
    }

    /**
     * Set this place's latitude.
     * @param lat the latitude for this place.
     */
    public void setLat(double lat) {
        this.mLat = lat;
    }

    /**
     * Get this place's longitude.
     * @return the longitude of this place.
     */
    public double getLng() {
        return mLng;
    }

    /**
     * Set this place's longitude.
     * @param lng the longitude for this place.
     */
    public void setLng(double lng) {
        this.mLng = lng;
    }

    /**
     * Get this place's phone number.
     * @return the phone number of this place.
     */
    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    /**
     * Set this place's phone number.
     * @param phoneNumber the phone number for this place.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }

    /**
     * Get this place's description.
     * @return the description of this place.
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * Set this place's description.
     * @param description the description for this place.
     */
    public void setDescription(String description) {
        this.mDescription = description;
    }

    /**
     * Get this place's reviews.
     * @return the reviews of this place.
     */
    public String getReviews() {
        return mReviews;
    }

    /**
     * Set this place's reviews.
     * @param reviews the reviews for this place.
     */
    public void setReviews(String reviews) {
        this.mReviews = reviews;
    }

    /**
     * Get this place's list of images.
     * @return the list of images of this place.
     */
    public List<Bitmap> getPlaceImages() {
        return mPlaceImages;
    }

    /**
     * Set this place's images.
     * @param placeImages a list of images for this place.
     */
    public void setPlaceImages(List<Bitmap> placeImages) {
        this.mPlaceImages = placeImages;
    }

    /**
     * Get this place's list of image references.
     * @return the list of image references of this place.
     */
    public List<String> getImageReferences() {
        return mImageReferences;
    }

    /**
     * Set this place's image references.
     * @param imageReferences the list of image references for this place.
     */
    public void setImageReferences(List<String> imageReferences) {
        this.mImageReferences = imageReferences;
    }

    /**
     * Get this place's icon.
     * @return the icon of this place.
     */
    public Bitmap getIcon() {
        return mIcon;
    }

    /**
     * Set this place's icon.
     * @param icon the icon for this place.
     */
    public void setIcon(Bitmap icon) {
        this.mIcon = icon;
    }

    /**
     * Get this place's icon URL.
     * @return the icon URL of this place.
     */
    public String getIconURL() {
        return mIconURL;
    }

    /**
     * Set this place's icon URL.
     * @param iconURL the icon URL for this place.
     */
    public void setIconURL(String iconURL) {
        this.mIconURL = iconURL;
    }

    /**
     * Get this place's opening hours.
     * @return the opening hours of this place.
     */
    public String getOpeningHours() {
        return mOpeningHours;
    }

    /**
     * Set this place's opening hours.
     * @param openingHours the opening hours for this place.
     */
    public void setOpeningHours(String openingHours) {
        this.mOpeningHours = openingHours;
    }

    /**
     * Get this place's content resource.
     * @return the content resource of this place.
     */
    public String getContentResource() {
        return mContentResource;
    }

    /**
     * Set this place's content resource.
     * @param contentResource the content resource for this place.
     */
    public void setContentResource(String contentResource) {
        this.mContentResource = contentResource;
    }

    /**
     * Create and return a string representation of this place.
     * @return a string representation of this place.
     */
    @Override
    public String toString() {
        return mName + "\n" + mAddress + "\n" + mType + "\n" + mWebsiteURL;
    }

}
