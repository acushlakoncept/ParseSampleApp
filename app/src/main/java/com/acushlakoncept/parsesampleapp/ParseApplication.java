package com.acushlakoncept.parsesampleapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;

public class ParseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		/*
		 * Add Parse initialization code here
		 */

		Parse.initialize(this, getResources().getString(R.string.parse_application_id),
				getResources().getString(R.string.parse_client_key));

		ParseACL defaultACL = new ParseACL();

		// If you would like all objects to be private by default, remove this
		// line.
		defaultACL.setPublicReadAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);
	}
}
