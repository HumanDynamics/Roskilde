package com.crowdsos.roskilde;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.mit.media.openpds.client.PreferencesWrapper;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends ActionBarActivity {

	private GoogleMap mMap;
	private LocationManager mLocationManager;
	private Location mLocation;
	private LocationListener mLocationListener = new BestLocationUpdater();
	private Map<String, Incident> mMarkerIncidentMap = new HashMap<String, Incident>();
	private ShareActionProvider mShareActionProvider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		
		PreferencesWrapper prefs = new PreferencesWrapper(this);
		
		if (prefs.getAccessToken() == null || prefs.getAccessToken() == "") {
			Intent loginIntent = new Intent(this, LoginActivity.class);
			// TODO: left off here - the idea is we want the login activity to pop a webview that will eventually redirect to a URI that it will have an intent filter for. 
			// We then want to be able to close the initial instance of the login activity, and show the main activity. In order to do this, we need to make sure we end up in the 
			// same instance of the login activity
			loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(loginIntent);
			finish();
			return;
		}

		Intent updatePlayServicesIntent = new Intent(this, UpdatePlayServicesActivity.class);
		try {
		    MapsInitializer.initialize(this);
		} catch (GooglePlayServicesNotAvailableException e) {
			//Intent updatePlayServicesIntent = new Intent(this, UpdatePlayServicesActivity.class);
			startActivity(updatePlayServicesIntent);
			finish();
			return;
		}
		// Configure the map
		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		LatLng initialCenter = new LatLng(55.63,12.08);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initialCenter, 15));
		mMap.getUiSettings().setZoomControlsEnabled(false);
		mMap.getUiSettings().setRotateGesturesEnabled(false);
		mMap.setMyLocationEnabled(true);	
		
		
		// Load incidents and put them on the map
		LoadIncidentsTask incidentsLoader = new LoadIncidentsTask(this, null) {
			protected void onPostExecute(java.util.List<Incident> result) {
				addIncidentsToMap(result);
			};
		};
		
		incidentsLoader.execute("");
		
		// Handle marker clicks
		mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				if (mMarkerIncidentMap.containsKey(marker.getId())) {
					Intent incidentDetailsIntent = new Intent(MainActivity.this, IncidentDetailActivity.class);
					incidentDetailsIntent.putExtra("incident", mMarkerIncidentMap.get(marker.getId()).getAsJsonObject().toString());
					startActivity(incidentDetailsIntent);
				}
				return false;
			}
		});
		
		// Add the overlay
		BitmapDescriptor overlayImage = BitmapDescriptorFactory.fromResource(R.drawable.map_overlay_small);

		LatLng northEast = new LatLng(55.630349,12.108712);
		LatLng southWest = new LatLng(55.608751,12.055754);
		GroundOverlayOptions overlayOptions = new GroundOverlayOptions();
		overlayOptions.image(overlayImage);
		overlayOptions.positionFromBounds(new LatLngBounds(southWest, northEast));
		mMap.addGroundOverlay(overlayOptions);
		mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				if (position.zoom > 16) {
					mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
				}
			}
			
		});
		
		// Set up location listener to update center of map
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mLocationListener.onLocationChanged(mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

		// Handle clicks on the bottom buttons		
		findViewById(R.id.report_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent reportIntent = new Intent(MainActivity.this, ReportIncidentActivity.class);
				startActivity(reportIntent);				
			}
		});
		
		findViewById(R.id.list_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent listIntent = new Intent(MainActivity.this, IncidentListActivity.class);
				startActivity(listIntent);
			}
		});
	}

	private void addIncidentsToMap(List<Incident> incidents) {
		for (Incident incident : incidents) {
			MarkerOptions marker = new MarkerOptions();
			marker.position(new LatLng(incident.location.lat, incident.location.lng));
			marker.title(incident.type);
			marker.icon(BitmapDescriptorFactory.fromResource(incident.incidentType.getMarkerId()));
			mMarkerIncidentMap.put(mMap.addMarker(marker).getId(), incident);
		}
	}
	
	@Override
	protected void onStop() {
		mLocationManager.removeUpdates(mLocationListener);
		super.onStop();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, mLocationListener);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		PreferencesWrapper prefs = new PreferencesWrapper(this);
		
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.referral_subject));
		shareIntent.putExtra(Intent.EXTRA_TEXT, "http://refer.crowdsos.net/~saper/crowdsos/refer.php?by=" + prefs.getUUID());

		menu.findItem(R.id.action_share).setIntent(Intent.createChooser(shareIntent, "Refer a friend"));		
		
		return true;
	}
	
	private class BestLocationUpdater implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (location != null && (mLocation == null || location.getAccuracy() < mLocation.getAccuracy())) {
				mLocation = location;	
				LatLng latLong = new LatLng(location.getLatitude(), location.getLongitude());
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 15));
				mLocationManager.removeUpdates(this);
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}
		
	}

}
