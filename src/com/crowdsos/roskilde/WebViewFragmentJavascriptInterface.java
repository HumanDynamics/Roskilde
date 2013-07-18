package com.crowdsos.roskilde;

import edu.mit.media.openpds.client.PreferencesWrapper;
import android.app.Activity;
import android.support.v4.view.ViewPager;


public class WebViewFragmentJavascriptInterface {
	
	protected Activity mActivity;
	
	public WebViewFragmentJavascriptInterface(Activity activity) {
		mActivity = activity;
	}
	
	public void setAuhtorizationCredentials(String accessToken, String refreshToken, long tokenExpirationTime) {
		PreferencesWrapper prefs = new PreferencesWrapper(mActivity);
		
		prefs.setAccessToken(accessToken);
		prefs.setRefreshToken(refreshToken);
		prefs.setTokenExpirationTime(tokenExpirationTime);
	}
}