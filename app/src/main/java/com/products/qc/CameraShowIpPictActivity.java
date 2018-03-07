package com.products.qc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class CameraShowIpPictActivity extends AppCompatActivity {

    /*private String ftpUser = "gduser";
    private String ftpPass = "gdsand54";
    private String ftpAdd = "ftp://198.71.51.207";*/
    private String picName;
    private Bitmap picture;
    private final static String ALBUM_APP_NAME = "ftp";
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_show_ip_pict);
        activity = this;
        Bundle b = getIntent().getExtras();
        ImageView pictureView = (ImageView) findViewById(R.id.pictureView);
        if (b != null){
            byte[] byteArray = b.getByteArray("image");
            picName = b.getString("picName");
            if (byteArray != null && picName != null){
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null){
                    actionBar.setTitle(picName);
                }
                picture = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                pictureView.setImageBitmap(picture);
            }else{
                Toast.makeText(this,R.string.toast_error_sendindg_data, Toast.LENGTH_LONG).show();
                finish();
            }
        }else{
            Toast.makeText(this,R.string.toast_error_sendindg_data, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.upload_bar_button, menu);
        return true;
    }

    public void upLoadPicture(MenuItem item){
        File picFile = saveImage(picture);
        if (picFile != null) {
            callUploadFile(picFile);
        } else {
            Toast.makeText(getBaseContext(),R.string.toast_error_saving_file, Toast.LENGTH_SHORT).show();
        }
    }

    public void callUploadFile(File picFile){
        new UploadFilesTask().execute(picFile);
    }


    public File saveImage(Bitmap image) {
        String state = Environment.getExternalStorageState();
        /* Checks if external storage is available for read and write */
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            try {
                picName = picName + ".jpg";
                File dirApp = getPrivateAlbumStorageDir(getBaseContext(), ALBUM_APP_NAME);
                if (dirApp != null) {
                    OutputStream fOut;

                    File file = new File(dirApp, picName);
                    boolean success = file.createNewFile();
                    if (success) {
                        fOut = new FileOutputStream(file);
                        // 100 means no compression, the lower you go, the stronger the compression
                        image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                        fOut.flush();
                        fOut.close();

                        MediaStore.Images.Media.insertImage(getBaseContext().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

                        return file;
                    }else {
                        return null;
                    }
                } else {
                    return null;
                }

            } catch (Exception e) {
                //Log.e("saveToExternalStorage()", e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    public File getPrivateAlbumStorageDir(Context context, String name) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), name);
        if (!file.mkdirs()) {
            //Log.e("WIP_TAG", "Directory not created");
        }
        return file;
    }

    @SuppressLint("StaticFieldLeak")
    private class UploadFilesTask extends AsyncTask<File, Void, Boolean> {
        ProgressDialog pd;
        String ftpAddres;
        int ftpPort;
        String ftpFolder;
        String ftpUser;
        String ftpPassword;
        File file;
        Boolean presetOk = true;


        protected void onPreExecute(){
            pd = ProgressDialog.show(activity, "Please wait", "Uploading Picture ...", true);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
            String ftpAddresRaw = sharedPref.getString(FtpSettingsFragment.KEY_FTP_PREF_ADDRESS, "");
            if (ftpAddresRaw.startsWith("ftp://")){
                int id = ftpAddresRaw.indexOf(':') + 3;
                ftpAddres = ftpAddresRaw.substring(id);
            }else{
                presetOk = false;
            }
            try {
                ftpPort = Integer.parseInt(sharedPref.getString(FtpSettingsFragment.KEY_FTP_PREF_PORT, "21"));
            } catch(NumberFormatException nfe) {
                presetOk = false;
            }
            ftpFolder = sharedPref.getString(FtpSettingsFragment.KEY_FTP_PREF_FOLDER, "");
            ftpUser = sharedPref.getString(FtpSettingsFragment.KEY_FTP_PREF_USER, "");
            ftpPassword = sharedPref.getString(FtpSettingsFragment.KEY_FTP_PREF_PASSWORD, "");
        }
        protected Boolean doInBackground(File... files) {
            boolean status = false;
            if (presetOk) {
                file = files[0];
                try {
                    FTPClient client = new FTPClient();
                    client.connect(ftpAddres, ftpPort);
                    if (FTPReply.isPositiveCompletion(client.getReplyCode())) {
                        status = client.login(ftpUser, ftpPassword); //this is the login credentials of your ftpserver. Ensure to use valid username and password otherwise it throws exception
                        client.setFileType(FTP.BINARY_FILE_TYPE);
                        client.enterLocalPassiveMode();
                        client.changeWorkingDirectory(ftpFolder);
                        if (client.getReplyCode() == 550) {
                            //create directory
                            client.makeDirectory(ftpFolder);
                        }
                        client.changeWorkingDirectory(ftpFolder);

                        try {
                            FileInputStream srcFileStream = new FileInputStream(file);
                            status = client.storeFile(picName, srcFileStream);
                            srcFileStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        client.logout();
                        client.disconnect();   //after file upload, don't forget to disconnect from FtpServer.
                    }else{
                        return status;
                    }
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            return status;
        }

        /*protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }*/

        protected void onPostExecute(Boolean result) {
                pd.dismiss();
            if (result){
                file.delete();
                Toast.makeText(getBaseContext(), R.string.toast_file_uploaded_ftp, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), CameraToolActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(activity, R.string.toast_error_ftp_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }

}


