	package com.products.qc;

import java.io.File;

import com.products.qc.FullScreenImageAdapter;
import com.products.qc.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.app.ActionBar;

public class FullScreenViewActivity extends AppCompatActivity {

	private Utils utils;
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_screen_view);

		viewPager = (ViewPager) findViewById(R.id.pager);

		utils = new Utils(getApplicationContext());

		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);

		adapter = new FullScreenImageAdapter(FullScreenViewActivity.this,
				utils.getFilePaths(this));

		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(position);
		
		SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    	
    	String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));
    	
    	ActionBar ab = getSupportActionBar();
    	ab.setTitle(currentPallet);
	}
		    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.full_screen_view, menu);
        
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	switch (item.getItemId()) {
	        case R.id.action_status:
	            ActionBarMethods.status(this);
	            return true;
	        case R.id.action_restart:
	        	RestartDialogFragment restart_dialog = new RestartDialogFragment(this);		
	    		restart_dialog.show(this.getFragmentManager(), "display");
	            return true;
	        case R.id.action_freight:
	        	ActionBarMethods.freight(this);
	        	return true;
			case R.id.action_signout:
				AppConstant.signout = true;
				this.finish();
				return true;
			case R.id.action_main_menu:
				AppConstant.mainMenu = true;
				this.finish();
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
    }
	
	public void removePic(View view){
		
		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);
		//utils = new Utils(getApplicationContext());
		//deleteFile(utils.getFilePaths().get(position));
		File file = new File(utils.getFilePaths(this).get(position));
		String pictureName = file.getName();
		QueryRepository.deletePictureByName(this, pictureName);
		
		file.delete();
		this.finish();
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		if (AppConstant.restarting || AppConstant.freighting || AppConstant.resampling ||
				AppConstant.mainMenu || AppConstant.signout)
			finish();
	}
}