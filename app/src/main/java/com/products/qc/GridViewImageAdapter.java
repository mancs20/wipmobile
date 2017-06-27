package com.products.qc;
 
import com.products.qc.FullScreenViewActivity;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
 
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.camera2.params.RggbChannelVector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
 
public class GridViewImageAdapter extends BaseAdapter {
	private ImageView view0;
    private Activity _activity;
    private ArrayList<String> _filePaths = new ArrayList<>();
    private int imageWidth;
    private ArrayList<View> selectedItems = new ArrayList<View>();
    private ArrayList<View> items = new ArrayList<View>();
    private HashMap<View, OnImageClickListener> onImageClickListeners = new HashMap<View, OnImageClickListener>();
    private HashMap<View, OnImageLongClickListener> onImageLongClickListeners = new HashMap<View, OnImageLongClickListener>();
    private HashMap<View, Integer> imagePosicions = new HashMap<View, Integer>();
    private OnClickListener btnPictureOnClickListener;
    private OnClickListener btnNextOnClickListener;
 
    public GridViewImageAdapter(Activity activity, ArrayList<String> filePaths,
            int imageWidth, OnClickListener btnPictureOnClickListener, 
            OnClickListener btnNextOnClickListener) {
    	view0 = null;
        this._activity = activity;
        this._filePaths = filePaths;
        this.imageWidth = imageWidth;
        this.btnPictureOnClickListener = btnPictureOnClickListener;
        this.btnNextOnClickListener = btnNextOnClickListener;
    }
 
    @Override
    public int getCount() {
        return this._filePaths.size();
    }
 
    @Override
    public Object getItem(int position) {
        return this._filePaths.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ImageView imageView;
    	
    	if (position == 0) {
    		if (view0 == null) {
    			if (convertView == null) {
    	            imageView = new ImageView(_activity);
    	        } else {
    	            imageView = (ImageView) convertView;
    	        }
    	 
    	        // get screen dimensions
    	        Bitmap image = decodeFile(_filePaths.get(position), imageWidth, imageWidth);
    	 
    	        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    	        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
    	                imageWidth));
    	        imageView.setImageBitmap(image);
    	 
    	        // image view click listener
    	        OnImageClickListener onImageClickListener = new OnImageClickListener(position);
    	        imageView.setOnClickListener(onImageClickListener);
    	        onImageClickListeners.put(imageView, onImageClickListener);
    	        
    	        // image view click listener
    	        OnImageLongClickListener onImageLongClickListener = new OnImageLongClickListener(position);
    	        imageView.setOnLongClickListener(onImageLongClickListener);
    	        onImageLongClickListeners.put(imageView, onImageLongClickListener);
    	        
    	        imagePosicions.put(imageView, position);
    	        
    	        items.add(imageView);
    	        view0 = imageView;
    	        return view0;
    		}
    		else {
    			return view0;
    		}
    	}
    	else {
    		if (convertView == null) {
	            imageView = new ImageView(_activity);
	        } else {
	            imageView = (ImageView) convertView;
	        }
	 
	        // get screen dimensions
	        Bitmap image = decodeFile(_filePaths.get(position), imageWidth,
	                imageWidth);
	 
	        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
	                imageWidth));
	        imageView.setImageBitmap(image);
	 
	        // image view click listener
	        OnImageClickListener onImageClickListener = new OnImageClickListener(position);
	        imageView.setOnClickListener(onImageClickListener);
	        onImageClickListeners.put(imageView, onImageClickListener);
	        
	        // image view click listener
	        OnImageLongClickListener onImageLongClickListener = new OnImageLongClickListener(position);
	        imageView.setOnLongClickListener(onImageLongClickListener);
	        onImageLongClickListeners.put(imageView, onImageLongClickListener);
	        
	        imagePosicions.put(imageView, position);
	        
	        items.add(imageView);
	        return imageView;
    	}
    }
 
    class OnImageClickListener implements OnClickListener {
 
        int _postion;
 
        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
        }
 
        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(_activity, FullScreenViewActivity.class);
            i.putExtra("position", _postion);
            _activity.startActivity(i);
        }
    }
    
    class OnImageLongClickListener implements OnLongClickListener {
    	 
        int _postion;
 
        // constructor
        public OnImageLongClickListener(int position) {
            this._postion = position;
        }
 
        @Override
        public boolean onLongClick(View v) {
    		v.setPadding(4, 4, 4, 4);
    		v.setBackgroundColor(Color.rgb(0, 167, 251));
    		selectedItems.add(v);
    		
    		final Button btnPicture = (Button)_activity.findViewById(R.id.button_picture);
    		final Button btnNext = (Button)_activity.findViewById(R.id.button_next);
    		
    		btnPicture.setText("Remove");
    		btnNext.setText("Cancel");
    		btnPicture.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					for (View view : selectedItems) {
						int pos = imagePosicions.get(view);
						File file = new File(_filePaths.get(pos));
						String pictureName = file.getName();
						QueryRepository.deletePictureByName(_activity, pictureName);
						
						file.delete();
						items.remove(view);
					}
					((GridViewActivity)_activity).createGridView();
					//if (items.isEmpty()) {
						resetGridView(btnNext, btnPicture);
					//}
				}
			});
    		btnNext.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					for (View view : items) {
						view.setOnClickListener(onImageClickListeners.get(view));
						view.setOnLongClickListener(onImageLongClickListeners.get(view));
						view.setPadding(0, 0, 0, 0);
					}
					selectedItems.clear();
					btnNext.setText("Next");
	            	btnNext.setOnClickListener(btnNextOnClickListener);
	            	btnPicture.setText("Picture");
	            	btnPicture.setOnClickListener(btnPictureOnClickListener);
				}
			});
        	
        	for (View view : items) {
        		view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (selectedItems.contains(v)) {
			        		v.setPadding(0, 0, 0, 0);
			        		selectedItems.remove(v);
			        	} else {
			        		v.setPadding(4, 4, 4, 4);
			        		v.setBackgroundColor(Color.rgb(0, 167, 251));
			        		selectedItems.add(v);
			        	}
						if (selectedItems.isEmpty()) {
							resetGridView(btnNext, btnPicture);
						}
					}
				});
        		view.setOnLongClickListener(new OnLongClickListener() {
					
					@Override
					public boolean onLongClick(View v) {
						if (selectedItems.contains(v)) {
			        		v.setPadding(0, 0, 0, 0);
			        		selectedItems.remove(v);
			        	} else {
			        		v.setPadding(4, 4, 4, 4);
			        		v.setBackgroundColor(Color.rgb(0, 167, 251));
			        		selectedItems.add(v);
			        	}
						if (selectedItems.isEmpty()) {
							resetGridView(btnNext, btnPicture);
						}
		        		return true;
					}
				});
        	}
        	
        	//ActionBar ab = _activity.getSupportActionBar();
        	//ab.setTitle("Selection");
        	
            return true;
        }
    }
 
    /*
     * Resizing image size
     */
    public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {
 
            File f = new File(filePath);
 
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
 
            final int REQUIRED_WIDTH = WIDTH / 2;
            final int REQUIRED_HIGHT = HIGHT / 2;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;
 
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void resetGridView(Button btnNext, Button btnPicture) {
    	for (View view : items) {
			view.setOnClickListener(onImageClickListeners.get(view));
			view.setOnLongClickListener(onImageLongClickListeners.get(view));
		}
		//ActionBar ab = _activity.getSupportActionBar();
		SharedPreferences sharedPref = _activity.getSharedPreferences(
				_activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    	
    	String currentPallet = String.valueOf(sharedPref.getInt(_activity.getString(R.string.saved_current_pallet), 0));
    	//ab.setTitle(currentPallet);
    	btnNext.setText("Next");
    	btnNext.setOnClickListener(btnNextOnClickListener);
    	btnPicture.setText("Picture");
    	btnPicture.setOnClickListener(btnPictureOnClickListener);
    }
}