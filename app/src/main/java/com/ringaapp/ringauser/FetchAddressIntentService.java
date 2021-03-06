package com.ringaapp.ringauser;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by kusuma on 11/25/2017.
 */

public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "FetchAddressIS";

    /**
     *
     * The receiver where results are forwarded from this service.
     */
    protected ResultReceiver mReceiver;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public FetchAddressIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(AppUtils.LocationConstants.RECEIVER);

        // Check if receiver was properly registered.
        if (mReceiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }
        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA);

        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.
        if (location == null) {
            errorMessage = "no location data provided";
            Log.wtf(TAG, errorMessage);
            deliverResultToReceiver(AppUtils.LocationConstants.FAILURE_RESULT, errorMessage, null);
            return;
        }


        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;

        try {

            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, we get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "no service avil";
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage ="inavlid latlng";
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "no address ";
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(AppUtils.LocationConstants.FAILURE_RESULT, errorMessage, null);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));

            }
            deliverResultToReceiver(AppUtils.LocationConstants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"), addressFragments), address);
            //TextUtils.split(TextUtils.join(System.getProperty("line.separator"), addressFragments), System.getProperty("line.separator"));

        }
    }

    /**
     * Sends a resultCode and message to the receiver.
     */
    private void deliverResultToReceiver(int resultCode, String message, Address address) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString(AppUtils.LocationConstants.RESULT_DATA_KEY, message);

            bundle.putString(AppUtils.LocationConstants.LOCATION_DATA_AREA, address.getSubLocality());

            bundle.putString(AppUtils.LocationConstants.LOCATION_DATA_CITY, address.getLocality());
            bundle.putString(AppUtils.LocationConstants.LOCATION_DATA_STREET, address.getAddressLine(0));

            mReceiver.send(resultCode, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
