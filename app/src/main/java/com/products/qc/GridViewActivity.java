package com.products.qc;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.products.qc.PalletReaderContract.PalletEntry;
import com.products.qc.ProductReaderContract.ProductEntry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
 
public class GridViewActivity extends ActionBarActivity {
 
    private Utils utils;
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    
    static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_TAKE_PHOTO = 1;
	SharedPreferences sharedPref;
	
	private OnClickListener btnPictureOnClickListener;
    private OnClickListener btnNextOnClickListener;
	
    private static final int IMAGE_REQUEST_CODE = 1;
	// your authority, must be the same as in your manifest file 
	private static final String CAPTURE_IMAGE_FILE_PROVIDER = "com.products.qc.fileprovider";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        
        //Menu menu1 = (Menu) findViewById(R.id.action_bar);
        //MenuItem logoutMI= menu1.add(0,0,0,"Logout");
        //logoutMI.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        
        sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    	
    	String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));
    	
    	ActionBar ab = getSupportActionBar();
    	ab.setTitle(currentPallet);
    	
    	
    	final GridViewActivity activity = this;
    	btnPictureOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, CameraActivity.class);
			    startActivity(intent);
				//dispatchTakePictureIntent();
			}
		};
		btnNextOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activity.next(v);
			}
		};
		Button btnPicture = (Button)this.findViewById(R.id.button_picture);
		Button btnNext = (Button)this.findViewById(R.id.button_next);
		btnPicture.setOnClickListener(btnPictureOnClickListener);
		btnNext.setOnClickListener(btnNextOnClickListener);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.grid_view, menu);
        
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
 
    public void createGridView() {
    	gridView = (GridView) findViewById(R.id.grid_view);
    	 
        utils = new Utils(this);
 
        // Initilizing Grid View
        InitilizeGridLayout();
 
        // loading all image paths from SD card
        imagePaths = utils.getFilePaths(this);
 
        // Gridview adapter
        adapter = new GridViewImageAdapter(GridViewActivity.this, imagePaths,
                columnWidth, btnPictureOnClickListener, btnNextOnClickListener);
 
        // setting grid view adapter
        gridView.setAdapter(adapter);
	}

	private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());
 
        columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);
 
        gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }
	
    /*public void openCamera(View view) {
		dispatchTakePictureIntent();
	}*/
    
    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(1));
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = ((Context)this).getDir("images", Context.MODE_PRIVATE);
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    
    private File createMyImageFile() throws IOException {
    	SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        
        int currenPallet = sharedPref.getInt(getString(R.string.saved_current_pallet), 0);
        //int palletId = QueryRepository.getPalletIdByPalletId(currenPallet, this);
        int samplingId = sharedPref.getInt(getString(R.string.saved_current_sampling), 0);
        String lot = sharedPref.getString(getString(R.string.saved_lot), "");
        String tag = String.valueOf(currenPallet);
        
        int productId = QueryRepository.getProductIdByPalletId(this);
        Cursor product = QueryRepository.getVarietySizeByProductId(this, productId);
        
        String variety = product.getString(product.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_VARIETY));
        String size = String.valueOf(product.getInt(product.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_SIZE)));
        
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String pictureNumber = format.format(date);
        
		String nombreFoto = lot + "-" + tag + "-" + variety + "-" + pictureNumber + ".jpg";
		nombreFoto = nombreFoto.replaceAll(" ", "");
		nombreFoto = nombreFoto.replaceAll("/", "");
		QueryRepository.savePicture(this, nombreFoto, samplingId);
		
		Context context = (Context)this;
		
		File path = new File(this.getFilesDir(), nombreFoto);
	    //if (!path.exists()) path.mkdirs();
	    
	    FileOutputStream fos = openFileOutput(nombreFoto, Context.MODE_WORLD_WRITEABLE);
		fos.close();
	    
		path = new File(this.getFilesDir(), nombreFoto);
	    
        return path;
    }

	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
	    // Ensure that there's a camera activity to handle the intent
	    //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        //File photoFile = null;
	        //try {
	        //    photoFile = createMyImageFile();
	        //} catch (IOException ex) {
	            // Error occurred while creating the File
	        //    Toast.makeText(GridViewActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
	        //}
	        // Continue only if the File was successfully created
	        //if (photoFile != null) {
	        	/*Uri imageUri = FileProvider.getUriForFile(this, CAPTURE_IMAGE_FILE_PROVIDER, photoFile);
	        	takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
	    	    startActivityForResult(takePictureIntent, IMAGE_REQUEST_CODE);*/
	    	    
	            //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	            //        Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, 2);
	        //}
	    //}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		//if(requestCode == 2 && data.getExtras() != null){
			/*Bitmap foto = (Bitmap)data.getExtras().get("data");
			
			SharedPreferences sharedPref = this.getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            
            int currenPallet = sharedPref.getInt(getString(R.string.saved_current_pallet), 0);
            int palletId = QueryRepository.getPalletIdByPalletId(currenPallet, this);
            String lot = sharedPref.getString(getString(R.string.saved_lot), "");
            String tag = String.valueOf(currenPallet);
            
            int productId = QueryRepository.getProductIdByPalletId(this);
            Cursor product = QueryRepository.getVarietySizeByProductId(this, productId);
            
            String variety = product.getString(product.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_VARIETY));
            String size = String.valueOf(product.getInt(product.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_SIZE)));
            
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            String pictureNumber = format.format(date);
            
			String nombreFoto = lot + "-" + tag + "-" + variety + "-" + pictureNumber + ".jpg";
			nombreFoto = nombreFoto.replaceAll(" ", "");
			nombreFoto = nombreFoto.replaceAll("/", "");
			QueryRepository.savePicture(this, nombreFoto, palletId);
			
			try {
				Context context = (Context)this;
				String directory = context.getDir("images", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + nombreFoto;
				FileOutputStream out = new FileOutputStream(directory);
				foto.compress(Bitmap.CompressFormat.JPEG, 100, out);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			//dispatchTakePictureIntent();
		//}
	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	    createGridView();
	    if (AppConstant.restarting || AppConstant.freighting || AppConstant.resampling ||
                AppConstant.mainMenu || AppConstant.signout)
			finish();
	}
	
	public void next(View view){
		Intent intent = new Intent(this, BrixPressureMeasurementsActivity.class);
	    startActivity(intent);
	}
}