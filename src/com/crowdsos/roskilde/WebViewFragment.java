package com.crowdsos.roskilde;


import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebStorage.QuotaUpdater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

@SuppressLint("ValidFragment")
public class WebViewFragment extends Fragment {
	
	public static final String ARG_URL = "url";

	private String mUrl;
	private FrameLayout mView;
	private WebView mWebView;
	private View mLoadingStatusView;
	private String mTitle;
	private Activity mActivity;
	private WebViewFragmentJavascriptInterface mJavascriptInterface;
	
	public static WebViewFragment Create(String url, String title, Activity activity) {
		return Create(url, title, activity, new WebViewFragmentJavascriptInterface(activity));
	}
	
	public static WebViewFragment Create(String url, String title, Activity activity, WebViewFragmentJavascriptInterface jsInterface) {
		WebViewFragment fragment = new WebViewFragment(url, title, activity);
		fragment.mJavascriptInterface = jsInterface;
		
		return fragment;
	}
	
	public WebViewFragment() {
		super();
		mUrl = mTitle = "";
	}
	
	protected WebViewFragment(String url, String title, Activity activity) {
		mUrl = url;
		mTitle = title;
		mActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getArguments() != null && getArguments().containsKey(ARG_URL)) {
			mUrl = getArguments().getString(ARG_URL);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = (FrameLayout) inflater.inflate(R.layout.webview_fragment_layout, container, false); 
		mLoadingStatusView = mView.findViewById(R.id.loading_status);
	
		// NOTE: code below for programmatically adding the webview may be necessary to avoid a 
		// memory leak related to using the main activity as the context when constructing a webview
		// This is what occurs when the webview is specified declaratively in the layout xml
		
		mWebView = (WebView) mView.findViewById(R.id.fragment_webview);
		//mWebView.setVisibility(View.VISIBLE);
		//mWebView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebView.setWebViewClient(new WebViewClient() {			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				mWebView.setVisibility(View.GONE);
				mLoadingStatusView.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {		
				mWebView.setVisibility(View.VISIBLE);
				mLoadingStatusView.setVisibility(View.GONE);
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				//view.loadData(getString(R.string.problem_contacting_server), "text/html", "UTF-8");
			}		
		});
					
		mWebView.addJavascriptInterface(mJavascriptInterface, "android");
		
		if (savedInstanceState != null) {
			mWebView.restoreState(savedInstanceState);
		} else {
			mWebView.loadUrl(mUrl);
		}		
		
		//mView.addView(mWebView);
		return mView;
	}	
	
	public String getTitle() {
		return mTitle;
	}		
}
