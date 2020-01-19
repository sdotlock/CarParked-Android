/**
 *  FROM TUTORIAL https://docs.mapbox.com/help/tutorials/android-location-listening/#initialize-the-locationengine
 *  Demonstrating Java InterOperability.
 */


package com.example.carparker;
import android.location.Location;
import android.widget.Toast;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;


import java.lang.ref.WeakReference;

  class MainActivityLocationCallback
        implements LocationEngineCallback<LocationEngineResult> {

    private final WeakReference<MainActivity> activityWeakReference;

    MainActivityLocationCallback(MainActivity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    /**
     * The LocationEngineCallback interface's method which fires when the device's location has changed.
     *
     * @param result the LocationEngineResult object which has the last known location within it.
     */

    @Override
    public void onSuccess(LocationEngineResult result) {
        MainActivity activity = activityWeakReference.get();

        if (activity != null) {
            Location location = result.getLastLocation();

            if (location == null) {
                return;
            }

            // Pass the new location to the Maps SDK's LocationComponent
            if (activity.map != null && result.getLastLocation() != null) {
                activity.map.getLocationComponent().forceLocationUpdate(result.getLastLocation());
            }
        }
    }

    /**
     * The LocationEngineCallback interface's method which fires when the device's location can not be captured
     *
     * @param exception the exception message
     */
    @Override
    public void onFailure(Exception exception) {
        MainActivity activity = activityWeakReference.get();
        if (activity != null) {
            Toast.makeText(activity, exception.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}

