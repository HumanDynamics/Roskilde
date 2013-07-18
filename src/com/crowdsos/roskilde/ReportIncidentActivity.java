package com.crowdsos.roskilde;

import java.util.Locale;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ReportIncidentActivity extends Activity {

	private ImageButton titleButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_incident);
		titleButton = (ImageButton) findViewById(R.id.report_incident_title);
		titleButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		LinearLayout layoutView = ((LinearLayout)findViewById(R.id.report_incident_layout));
		for (int i = 0; i < layoutView.getChildCount(); i++) {
			View rowView = layoutView.getChildAt(i);
			if (rowView instanceof LinearLayout) {
				LinearLayout row = (LinearLayout) rowView;
				for (int j = 0; j < row.getChildCount(); j++) {
					View childView = row.getChildAt(j);
					if (childView instanceof ImageButton && 
						childView.getContentDescription() != null && 
						!childView.getContentDescription().equals(getString(R.string.report_incident))) {
						childView.setOnClickListener(createIncidentListener);
					}
				}
			}
		}
	}
	
	protected OnClickListener createIncidentListener = new OnClickListener() {
		public void onClick(View v) {
			Intent incidentDetailIntent = new Intent(ReportIncidentActivity.this, ReportIncidentDetailActivity.class);
			incidentDetailIntent.putExtra("type", v.getContentDescription().toString());
			//incidentDetailIntent.setDataAndNormalize(Uri.fromParts("type", v.getContentDescription().toString().toLowerCase(Locale.ENGLISH).replace(" ", "_"), ""));
			startActivity(incidentDetailIntent);
		};
	};
}
