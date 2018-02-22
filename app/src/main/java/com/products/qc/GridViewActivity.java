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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AppCompatActivity;
 
public class GridViewActivity extends AppCompatActivity {
 
    private Utils utils;
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;
    private String nombreFoto;
    
    static final int REQUEST_IMAGE_CAPTURE = 1;
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
//				Intent intent = new Intent(activity, CameraActivity.class);
//			    startActivity(intent);
				dispatchTakePictureIntent();
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
        String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));
        if (currentPallet.equals("0") || Utils.sampledCount(this) == 0)
            menu.removeItem(R.id.action_send_data);
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
            case R.id.action_send_data:
                if (Utils.requiredSample(this)) {
                    final Activity activity = this;
                    new AlertDialog.Builder(this)
                            .setTitle("Send Data")
                            .setMessage("Quality Control uncompleted")
                            .setNegativeButton(android.R.string.cancel, null) // dismisses by default
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {
                                    QControlCaller c = new QControlCaller(activity);
                                    c.start();
                                }
                            })
                            .create()
                            .show();
                } else {
                    QControlCaller c = new QControlCaller(this);
                    c.start();
                }
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
    
    private File createMyImageFile() throws IOException {
    	SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        
        int currenPallet = sharedPref.getInt(getString(R.string.saved_current_pallet), 0);
        //int palletId = QueryRepository.getPalletIdByPalletId(currenPallet, this);

        String lot = sharedPref.getString(getString(R.string.saved_lot), "");
        String tag = String.valueOf(currenPallet);
        
        int productId = QueryRepository.getProductIdByPalletId(this);
        Cursor product = QueryRepository.getVarietySizeByProductId(this, productId);
        
        String variety = product.getString(product.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_VARIETY));
//        String size = String.valueOf(product.getInt(product.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_SIZE)));
        
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String pictureNumber = format.format(date);
        
		nombreFoto = lot + "-" + tag + "-" + variety + "-" + pictureNumber + ".jpg";
		nombreFoto = nombreFoto.replaceAll(" ", "");
		nombreFoto = nombreFoto.replaceAll("/", "");

		
//		Context context = (Context)this;
		
//		File path = new File(this.getFilesDir(), nombreFoto);
	    //if (!path.exists()) path.mkdirs();
	    
//	    FileOutputStream fos = openFileOutput(nombreFoto, Context.MODE_WORLD_WRITEABLE);
//		fos.close();
	    
//		path = new File(context.getDir("images", Context.MODE_PRIVATE), nombreFoto);
//        File directory = getDir("images", MODE_PRIVATE);
//
//        directory.mkdirs();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File[] files = storageDir.listFiles();
//        for (File file : files)
//            file.length();
//        if (!storageDir.exists())
//            storageDir.mkdirs();
//        return File.createTempFile(
//                nombreFoto,  /* prefix */
//                ".jpg",         /* suffix */
//                directory
//        );
//        File image = File.createTempFile(
//                nombreFoto,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );

//        File storageDir = getDir("images", Context.MODE_PRIVATE);
        File image = new File(storageDir, nombreFoto);
//        if (!image.exists())
//            image.createNewFile();
        return image;
    }

	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createMyImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(GridViewActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
//                Uri imageUri = FileProvider.getUriForFile(this, CAPTURE_IMAGE_FILE_PROVIDER, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            int samplingId = sharedPref.getInt(getString(R.string.saved_current_sampling), 0);
            QueryRepository.savePicture(this, nombreFoto, samplingId);
            dispatchTakePictureIntent();
        }
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