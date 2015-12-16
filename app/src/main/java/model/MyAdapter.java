package model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hieunguyen725.myplaces.R;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Author: Hieu Nguyen
 *
 * This is a class representing a custom ArrayAdapter
 * for the places, extending ArrayAdapter.
 */
public class MyAdapter extends ArrayAdapter<Place> {

    public static final String TAG = "MyAdapter";

    /**
     * Construct a custom array adapter given the application's
     * context and a list of current places.
     * @param context reference to the application context.
     * @param places reference to the current list of places.
     */
    public MyAdapter(Context context, List<Place> places) {
        super(context, R.layout.row_layout_2, places);
    }

    /**
     *  Get the view and display it for the data for the item at the
     *  given position.
     * @param position position of the current item to display
     *                 the view.
     * @param convertView the old view to reuse.
     * @param parent parent ViewGroup for the view to attach to.
     * @return a view of the current item corresponding to the item's
     *         position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater theInflater = LayoutInflater.from(getContext());
        View theView = theInflater.inflate(R.layout.row_layout_2, parent, false);
        Place place = getItem(position);
        TextView placeName = (TextView) theView.findViewById(R.id.row_layout_2_placeName);
        placeName.setText(place.getName());
        TextView placeAddress = (TextView) theView.findViewById(R.id.row_layout_2_placeAddress);
        placeAddress.setText(place.getAddress());
        if (place.getIcon() != null) {
            Log.i(TAG, "Image at position " + position + " is not null");
            ImageView placeIcon = (ImageView) theView.findViewById(R.id.row_layout_2_image);
            placeIcon.setImageBitmap(place.getIcon());
        } else {
            Log.i(TAG, "Image at position " + position + " is null");
            GetIcon task = new GetIcon();
            task.execute(new PlaceViewContainer(place, theView));
        }
        return theView;
    }

    /**
     * This class represents a container for a place
     * corresponding to its view.
     */
    public class PlaceViewContainer {
        public Place mPlace;
        public View mView;

        /**
         * Constructing a new place and view container.
         * @param place the current place the view is displaying.
         * @param view the current view corresponding to its place.
         */
        public PlaceViewContainer(Place place, View view) {
            this.mPlace = place;
            this.mView = view;
        }
    }

    /**
     * This is a class that represents a get icon task and extends AsyncTask.
     * This class will get icon for the given place in the container and
     * display the icon on the place's view.
     */
    private class GetIcon extends AsyncTask<PlaceViewContainer, String, PlaceViewContainer> {

        /**
         * Preparing to start task.
         */
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "Starting task");
        }

        /**
         * Given the place and view container, retrieve the icon for the place
         * from the web.
         * @param params Place and view container to get the icon for.
         * @return a container with the icon set, otherwise null.
         */
        @Override
        protected PlaceViewContainer doInBackground(PlaceViewContainer... params) {
            PlaceViewContainer container = params[0];
            if (container != null) {
                return container = getIcons(container);
            }
            return null;
        }

        /**
         * Given the container, retrieve and set the icon for its place.
         * @param container Place and view container to get the icon for.
         * @return a container with the icon set for its place.
         */
        private PlaceViewContainer getIcons(PlaceViewContainer container) {
            try {
                String iconURL = container.mPlace.getIconURL();
                InputStream inputStream = (InputStream)
                        new URL(iconURL).getContent();
                // decode the inputstream and set the place's icon using
                // the decoded bitmap.
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                container.mPlace.setIcon(bitmap);
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return container;
        }

        /**
         * Given the container with the place's icon already set,
         * display the place's icon to the list's item.
         * @param container Place and view container to get the place and
         *                  its icon for display.
         */
        @Override
        protected void onPostExecute(PlaceViewContainer container) {
            ImageView placeIcon = (ImageView) container.mView.findViewById(R.id.row_layout_2_image);
            placeIcon.setImageBitmap(container.mPlace.getIcon());
            Log.i(TAG, "Task finished");
        }
    }

}
