package com.acushlakoncept.parsesampleapp;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainFeedActivity extends ListActivity{

	public static final String TAG = MainFeedActivity.class.getSimpleName();

	protected ProgressBar mProgressBar;
    private Toolbar mToolbar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

        //for push notifications
        ParseAnalytics.trackAppOpened(getIntent());
        PushService.setDefaultPushCallback(this, MainFeedActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();

	}

	@Override
	public void onResume() {
		super.onResume();
		getLatestPosts();
	}

	protected void getLatestPosts() {
		mProgressBar.setVisibility(View.VISIBLE);

		/*
		 * Use ParseQuery to get latest posts
		 */
        ParseQuery<ParseObject> myQuery = new ParseQuery<ParseObject>(AddLinkActivity.POSTS);
        myQuery.setLimit(100);
        myQuery.orderByDescending("createdAt");

        myQuery.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> results, ParseException e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (e == null) {
                    ArrayList<HashMap<String, String>> articles = new ArrayList<HashMap<String, String>>();
                    for (ParseObject result : results) {
                        HashMap<String, String> article = new HashMap<String, String>();
                        article.put(AddLinkActivity.KEY_NOTES,
                                result.getString(AddLinkActivity.KEY_NOTES));
                        article.put(AddLinkActivity.KEY_URL,
                                result.getString(AddLinkActivity.KEY_URL));
                        articles.add(article);
                    }
                    SimpleAdapter adapter = new SimpleAdapter(
                            MainFeedActivity.this, articles,
                            android.R.layout.simple_list_item_2, new String[]{
                            AddLinkActivity.KEY_NOTES,
                            AddLinkActivity.KEY_URL}, new int[]{
                            android.R.id.text1, android.R.id.text2});
                    setListAdapter(adapter);

                } else {
                    Log.e("App", "Error: " + e.getMessage());
                }
            }
        });

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		TextView urlLabel = (TextView) v.findViewById(android.R.id.text2);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(urlLabel.getText().toString()));
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.addButton:
				startActivity(new Intent(this, AddLinkActivity.class));
				return true;
			case R.id.followButton:
				//startActivity(new Intent(this, SelectUsersActivity.class));
				return true;
			case R.id.logoutButton:
			/*
			 * Log current user out using ParseUser.logOut()
			 */
				ParseUser.logOut();
				Intent intent = new Intent(this, LoginOrSignUpActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
            case R.id.refreshButton:
                //Action to refresh list of URLs added
                getLatestPosts();
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}