/*
 * Copyright (c) 2014 Intellibins authors
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *   * Neither the name of The Intern nor the names of its contributors may
 * be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE LISTED COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.intellibins.recycle.activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.intellibins.recycle.LocUtils;
import com.intellibins.recycle.R;
import com.intellibins.recycle.RecycleApp;
import com.intellibins.recycle.RecycleMachine;
import com.intellibins.recycle.model.Loc;
import com.intellibins.recycle.userlocation.IUserLocation;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    //private static LatLng mLatLng = new LatLng(40.7680441, -73.9823722); //Columbus Circle

    private static LatLng mLatLng = new LatLng(40.715522, -74.002452);
            //New York City Department of Health and Mental Hygiene

    private static final float ZOOM = 17f;

    private static final int MAX_LOCATION = 10;

    private Subscription subscription;

    @Inject
    IUserLocation mUserLocation;

    Func1<Location, Observable<List<Loc>>> findClosestBins
            = location -> RecycleApp.getRecycleMachine(MapsActivity.this).finBin().getLocs()
            .toSortedList(
                    LocUtils.compare(mLatLng.latitude, mLatLng.longitude));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        final Location lastLocation = Utils.getUserLocationFromPreference(this);
//        if (lastLocation != null) {
//            mLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
//        }
        setUpMapIfNeeded(mLatLng);

        RecycleMachine machine = RecycleApp.getRecycleMachine(this);
        mUserLocation = machine.locateUser();
        mUserLocation.start();

        Location mockLocation = new Location("");
        mockLocation.setLatitude(mLatLng.latitude);
        mockLocation.setLongitude(mLatLng.longitude);
        Observable<Location> mockObservable = Observable.just(mockLocation);

        subscription = mockObservable
                .flatMap(findClosestBins)
                .flatMap(Observable::from)
                .take(MAX_LOCATION).onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Loc>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(Loc loc) {
                        if (mMap != null) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(loc.latitude, loc.longitude))
                                    .title(loc.name)
                                    .icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        }
                    }
                });

//        subscription = mUserLocation
//                .observe()
//                .take(1)
//                .flatMap(findClosestBins)
//                .flatMap(Observable::from)
//                .take(MAX_LOCATION).onBackpressureBuffer()
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded(mLatLng);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserLocation.stop();
        subscription.unsubscribe();
    }

    /**
     * Sets up the map if it is possible to do so and the map has not already been instantiated.
     * This will ensure that we only ever
     * call {@link #setUpMap(LatLng latLng)} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded(LatLng latLng) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap(latLng);
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(LatLng latLng) {
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));
    }
}
