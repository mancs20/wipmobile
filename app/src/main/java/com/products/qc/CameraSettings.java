package com.products.qc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by manuel on 2/22/2018.
 */

public class CameraSettings implements Parcelable{
    private String cameraName;
    private String cameraIP;
    private String cameraUser;
    private String cameraPassword;
   /* private Context context;
    private SharedPreferences sharedPrefs;

    public CameraSettings(Context context) {
        this.setContext(context);
        sharedPrefs = context.getSharedPreferences("CamerasIP", 0);
    }*/

   //TODO delete commented tests
    public CameraSettings(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cameraName);
        dest.writeString(cameraIP);
        dest.writeString(cameraPassword);
        dest.writeString(cameraUser);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<CameraSettings> CREATOR = new Parcelable.Creator<CameraSettings>() {
        public CameraSettings createFromParcel(Parcel in) {
            return new CameraSettings(in);
        }

        public CameraSettings[] newArray(int size) {
            return new CameraSettings[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private CameraSettings(Parcel in) {
        cameraName = in.readString();
        cameraIP = in.readString();
        cameraUser = in.readString();
        cameraPassword = in.readString();
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getCameraIP() {
        return cameraIP;
    }

    /*public ArrayList<CameraSettings> getCamerasFromSharedPreferences(){
        ArrayList<CameraSettings> cameras = new ArrayList<>();
        //SharedPreferences sharedPref = getActivity.(getBaseContext().MODE_PRIVATE);
        //SharedPreferences sharedPref = context.getSharedPreferences(Context.MODE_PRIVATE);
        //SharedPreferences sharedPref = getActivity() .getPreferences(Context.MODE_PRIVATE);

        if (sharedPrefs.contains("CamerasIP")){
            Gson gson = new Gson();
            String json = sharedPrefs.getString("CamerasIP", null);
            Type type = new TypeToken<ArrayList<CameraSettings>>() {}.getType();
            cameras = gson.fromJson(json, type);
        }else{
            cameras = null;
        }
        return cameras;
    }*/

    public static ArrayList<CameraSettings> getCamerasFromSharedPreferences(Context context){
        ArrayList<CameraSettings> cameras;
        SharedPreferences sharedPrefs = context.getSharedPreferences("CamerasIP", Context.MODE_PRIVATE);

        if (sharedPrefs.contains("CamerasIP")){
            Gson gson = new Gson();
            String json = sharedPrefs.getString("CamerasIP", null);
            Type type = new TypeToken<ArrayList<CameraSettings>>() {}.getType();
            cameras = gson.fromJson(json, type);
        }else{
            cameras = null;
        }
        return cameras;
    }

    public static void saveCamerasToSharedPreferences(Context context, ArrayList<CameraSettings> cameras){
        SharedPreferences sharedPrefs = context.getSharedPreferences("CamerasIP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cameras);
        editor.putString("CamerasIP", json);
        editor.commit();
    }

    public void setCameraIP(String cameraIP) {
        this.cameraIP = cameraIP;
    }

    public String getCameraUser() {
        return cameraUser;
    }

    public void setCameraUser(String cameraUser) {
        this.cameraUser = cameraUser;
    }

    public String getCameraPassword() {
        return cameraPassword;
    }

    public void setCameraPassword(String cameraPassword) {
        this.cameraPassword = cameraPassword;
    }


    /*public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
        sharedPrefs = context.getSharedPreferences("CamerasIP", context);
    }*/
}
