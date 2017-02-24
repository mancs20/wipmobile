/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.products.qc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.products.qc.ProductReaderContract.ProductEntry;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class CameraActivity extends ActionBarActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    public static final int MEDIA_TYPE_IMAGE = 1;
    Activity activity;
    private SurfaceHolder mHolder;
    private boolean takingPicture = false;

    private PictureCallback mPicture = new PictureCallback() {
    	
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            /*File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d("", "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("", "Error accessing file: " + e.getMessage());
            }*/
        	
    		//Bitmap foto = (Bitmap)data.getExtras().get("data");
			
			SharedPreferences sharedPref = activity.getSharedPreferences(
                    getString(R.string.preference_file_key), activity.MODE_PRIVATE);
            
            int currenPallet = sharedPref.getInt(getString(R.string.saved_current_pallet), 0);
            //int palletId = QueryRepository.getPalletIdByPalletId(currenPallet, activity);
            int samplingId = sharedPref.getInt(getString(R.string.saved_current_sampling), 0);
            String lot = sharedPref.getString(getString(R.string.saved_lot), "");
            String tag = String.valueOf(currenPallet);
            
            int productId = QueryRepository.getProductIdByPalletId(activity);
            Cursor product = QueryRepository.getVarietySizeByProductId(activity, productId);
            
            String variety = product.getString(product.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_VARIETY));
            String size = String.valueOf(product.getInt(product.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME_SIZE)));
            
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String pictureNumber = format.format(date);
            
			String nombreFoto = lot + "-" + tag + "-" + variety + "-" + pictureNumber + ".jpg";
			nombreFoto = nombreFoto.replaceAll(" ", "");
			nombreFoto = nombreFoto.replaceAll("/", "");
			QueryRepository.savePicture(activity, nombreFoto, samplingId);
			
			try {
				Context context = (Context)activity;
				String directory = context.getDir("images", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + nombreFoto;
				FileOutputStream out = new FileOutputStream(directory);
				out.write(data);
				out.close();
				rotatePicture(nombreFoto);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			mPreview.refreshCamera(mCamera);
			takingPicture = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Create an instance of Camera
        mCamera = CameraPreview.getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        
        final MediaPlayer mp = new MediaPlayer();
        
        activity = this;
        
        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.button_picture);
        captureButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	if (!takingPicture) {
                		takingPicture = true;
	                	if(mp.isPlaying()) {  
	                        mp.stop();
	                    } 
	
	                    try {
	                        mp.reset();
	                        AssetFileDescriptor afd;
	                        afd = getAssets().openFd("camera.mp3");
	                        mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
	                        mp.prepare();
	                        mp.start();
	                    } catch (IllegalStateException e) {
	                        e.printStackTrace();
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                	
	                    // get an image from the camera
	                    mCamera.takePicture(null, null, mPicture);
                	}
                }
            }
        );
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        
        String currentPallet = String.valueOf(sharedPref.getInt(getString(R.string.saved_current_pallet), 0));    	
    	
    	ActionBar ab = getSupportActionBar();
    	ab.setTitle(currentPallet);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }
    
    private void releaseCamera() {
        // stop and release camera
    	if (mCamera != null) {
    		mCamera.release();
    		mCamera = null;
    	}
    }
    
    public void onResume() {
    	super.onResume();
    	if (mCamera == null) {
    		mCamera = mPreview.getCameraInstance();		
			mPreview.refreshCamera(mCamera);
	    }
	}
    
    private void rotatePicture(String pictureName) throws IOException {
    	// Rotate Picture
    	File f = new File(this.getDir("images", Context.MODE_PRIVATE).getAbsolutePath() + File.separator + pictureName);
    	Matrix mat = new Matrix();
        mat.postRotate(90);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
        FileOutputStream fos = new FileOutputStream(f);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.close();
    }
}