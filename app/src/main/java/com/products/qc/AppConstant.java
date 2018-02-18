package com.products.qc;

import java.util.Arrays;
import java.util.List;
 
public class AppConstant {
 
    // Number of columns of Grid View
    public static final int NUM_OF_COLUMNS = 8;
 
    // Gridview image padding
    public static final int GRID_PADDING = 2; // in dp
 
    // SD card image directory
    public static final String PHOTO_ALBUM = "Pictures";
 
    // supported file formats
    public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg",
            "png");
    
    public static boolean restarting = false;
    
    public static boolean freighting = false;
    
    public static boolean resampling = false;
    
    public static boolean sendData = false;
    
    public static boolean dataSended = false;

    public static boolean closing = false;

    public static String palletTag = "";

    public static String currentLot = "";

    public static boolean signout = false;

    public static String user="";

    public static String password="";

    public static boolean mainMenu = false;
}
