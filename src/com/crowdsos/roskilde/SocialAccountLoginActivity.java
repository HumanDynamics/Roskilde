package com.crowdsos.roskilde;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class SocialAccountLoginActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social_account_login);
		getSupportFragmentManager().beginTransaction().add(R.id.social_account_login_layout, WebViewFragment.Create("http://grant.crowdsos.net/accounts/facebook/login/?method=oauth2", "", this, null)).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.social_account_login, menu);
		return true;
	}

}
