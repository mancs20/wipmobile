package com.products.qc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Base64;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.products.qc.PictureReaderContract.PictureEntry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class Utils {
 
    private Context _context;
 
    // constructor
    public Utils(Context context) {
        this._context = context;
    }
 
    // Reading file paths from SDCard
    public ArrayList<String> getFilePaths(Activity activity) {
        ArrayList<String> filePaths = new ArrayList<String>();
 
        //File directory = new File(
        //        android.os.Environment.getExternalStorageDirectory()
        //                + File.separator + AppConstant.PHOTO_ALBUM);
        
        File directory = _context.getDir("images", Context.MODE_PRIVATE);
        SharedPreferences sharedPref = activity.getSharedPreferences(
        		activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int currenPallet = sharedPref.getInt(activity.getString(R.string.saved_current_pallet), 0);
        //int palletId = QueryRepository.getPalletIdByPalletId(currenPallet, activity);
        int samplingId = sharedPref.getInt(activity.getString(R.string.saved_current_sampling), 0);
        Cursor pictures = QueryRepository.getAllPictureBySampling(activity, samplingId);
        // check for directory
        //if (directory.isDirectory()) {
            // getting list of file paths
            //File[] listFiles = directory.listFiles();
 
            // Check for count
            if (pictures.getCount() > 0) {
 
                // loop through all files
                for (int i = 0; i < pictures.getCount(); i++) {
                	String pictureName = pictures.getString(pictures.getColumnIndexOrThrow(PictureEntry.COLUMN_NAME_NAME));
                    // get file path
                    String filePath = directory.getAbsolutePath() + File.separator + pictureName;
 
                    // check for supported file extension
                    //if (IsSupportedFile(filePath)) {
                        // Add image path to array list
                        filePaths.add(filePath);
                    //}
                    pictures.moveToNext();
                }
            }/* else {
                // image directory is empty
                Toast.makeText(
                        _context,
                        AppConstant.PHOTO_ALBUM
                                + " is empty. Please load some images in it !",
                        Toast.LENGTH_LONG).show();
            }
 
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(_context);
            alert.setTitle("Error!");
            alert.setMessage(AppConstant.PHOTO_ALBUM
                    + " directory path is not valid! Please set the image directory name AppConstant.java class");
            alert.setPositiveButton("OK", null);
            alert.show();
        }*/
 
        return filePaths;
    }
 
    // Check supported file extensions
    private boolean IsSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());
 
        if (AppConstant.FILE_EXTN
                .contains(ext.toLowerCase(Locale.getDefault())))
            return true;
        else
            return false;
 
    }
 
    /*
     * getting screen width
     */
    @SuppressLint("NewApi") public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
 
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }
}

