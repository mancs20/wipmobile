package com.products.qc;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @SuppressWarnings("deprecation")
	public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
        	//set camera to continually auto-focus
        	//Camera.Parameters params = mCamera.getParameters();
        	//*EDIT*//params.setFocusMode("continuous-picture");
        	//It is better to use defined constraints as opposed to String, thanks to AbdelHady
        	//params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        	//mCamera.setParameters(params);
//            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setRotation(90);
//            mCamera.setParameters(parameters);
//        	mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    	releaseCamera();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d("", "Error starting camera preview: " + e.getMessage());
        }
    }

    public void refreshCamera(Camera camera) {
    	if (mHolder.getSurface() == null) {
    	    // preview surface does not exist
    	    return;
    	}
    	// stop preview before making changes
    	try {
    	    mCamera.stopPreview();
    	} catch (Exception e) {
    	    // ignore: tried to stop a non-existent preview
    	}
    	// set preview size and make any resize, rotate or
    	// reformatting changes here
    	// start preview with new settings
    	setCamera(camera);
    	try {
    	        mCamera.setPreviewDisplay(mHolder);
    	        mCamera.startPreview();
    	} catch (Exception e) {
    	       Log.d("", "Error starting camera preview: " + e.getMessage());
    	}
	}
    
    public void setCamera(Camera camera) {
        //method to set a camera instance
        mCamera = camera;
    }
    
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
    
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
    	//releaseCamera();
    	int cameraId = getCameraId();
    	
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance
        }
        catch (Exception e){
        	int x=0;
            // Camera is not available (in use or does not exist) Fail to connect to camera service
        }
        return c; // returns null if camera is unavailable
    }

    public static int getCameraId(){
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera)
    {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation)
        {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        }
        else
        { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}