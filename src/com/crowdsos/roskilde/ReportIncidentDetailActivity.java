package com.crowdsos.roskilde;

import java.util.Locale;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ReportIncidentDetailActivity extends Activity {

	private static final int CAMERA_REQUEST_CODE = 1;
	
	private EditText mIncidentDetailsText;
	private String mType;
	private LocationManager mLocationManager;
	private Location mBestLocation;
	private LocationListener mLocationListener = new BestLocationUpdater();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_incident_detail);
		mIncidentDetailsText = (EditText) findViewById(R.id.incident_details_textview);
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		if (getIntent().getStringExtra("type") != null) {
			//incidentDetailsTextView.setText(getIntent().getData().getSchemeSpecificPart());
			LinearLayout headerLayout = (LinearLayout) findViewById(R.id.incident_header_layout);
			mType = getIntent().getStringExtra("type");
			headerLayout.addView(getIncidentHeader(mType));
		}
		
		findViewById(R.id.incident_details_title).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mLocationManager.removeUpdates(mLocationListener);
				finish();				
			}
		});

		findViewById(R.id.camera_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
			}
		});
		
		findViewById(R.id.submit_incident_button).setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(final View v) {
				ReportIncidentDetailActivity.this.setVisible(false);
				v.setEnabled(false);
				new Thread() {
					@Override
					public void run() {
						if (mBestLocation == null) {
							mBestLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						}
						boolean success = true;
						try {
							PDSClient pds = new PDSClient(ReportIncidentDetailActivity.this);
							pds.reportIncident(mType, mIncidentDetailsText.getText().toString(), mBestLocation);			
						} catch (Exception ex) {
							Log.e("ReportIncidentDetailActivity", ex.getMessage());
							success = false;
						}
						final String toastMessage = (success)? "Incident submitted":"Error occurred while submitting incident";
						runOnUiThread(new Runnable() {							
							@Override
							public void run() {
								Toast.makeText(ReportIncidentDetailActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
							}
						});
						if (getParent() != null) {
							getParent().finishFromChild(ReportIncidentDetailActivity.this);
						}
						mLocationManager.removeUpdates(mLocationListener);
						finish();
					}
				}.start();				
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
			Bundle extras = data.getExtras();
			Bitmap image = (Bitmap) extras.get("data");
			// TODO: add image to incident report, upload to server. Referrals and facebook login are a higher priority right now.
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mLocationListener);
	}
	
	@Override
	protected void onStop() {
		mLocationManager.removeUpdates(mLocationListener);
		super.onStop();
	}
	
	protected ImageView getIncidentHeader(String type) {
		ImageView header = new ImageView(this);
		Drawable image;
		IncidentFactory incidentFactory = new IncidentFactory(this);
		image = getResources().getDrawable(incidentFactory.getIncidentType(type).getHeaderId());
		header.setImageDrawable(image);
		header.setAdjustViewBounds(true);
		header.setScaleType(ScaleType.FIT_START);
		try {
			header.setBackground(null);
		} catch (NoSuchMethodError error) {
			Log.w("ReportIncidentDetailActivity", "Couldn't set background on header");
		}
		return header;
	}
	
	private class BestLocationUpdater implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (location != null && (mBestLocation == null || location.getAccuracy() < mBestLocation.getAccuracy())) {
				mBestLocation = location;				
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
