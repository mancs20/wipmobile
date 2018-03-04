package com.products.qc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manuel on 2/21/2018.
 */

public class CamerasSettingAdapter extends RecyclerView.Adapter<CamerasSettingAdapter.ViewHolder>{
    //private String[] mCamerasDataSet;
    private List<CameraSettings> mCamerasDataSet;
    private Activity activity;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CamerasSettingAdapter(ArrayList<CameraSettings> myCamerasDataset, Activity activity) {
        mCamerasDataSet = myCamerasDataset;
        this.activity = activity;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView nameTextView;
        private TextView ipTextView;
        private Button cameraRemoveButton;
        private ConstraintLayout cameraSettingLayout; // View cameraSettingLayout;
        public ViewHolder(View v) {
            super(v);
            nameTextView = (TextView) v.findViewById(R.id.cameraName);
            ipTextView = (TextView) v.findViewById(R.id.cameraIP);
            cameraRemoveButton = (Button) v.findViewById(R.id.buttonRemoveCamera);
            cameraSettingLayout = (ConstraintLayout) v.findViewById(R.id.cameraSettingLayout);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CamerasSettingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.camera_setting, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CamerasSettingAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.nameTextView.setText(mCamerasDataSet.get(position).getCameraName());
        holder.ipTextView.setText(mCamerasDataSet.get(position).getCameraIP());
        holder.cameraRemoveButton.setOnClickListener(removeListener(position));
        holder.cameraSettingLayout.setOnClickListener(cameraEditListener(position));
    }

    private View.OnClickListener removeListener(final int position){
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
                alertbox.setMessage(R.string.dialog_sure);
                alertbox.setPositiveButton(R.string.button_yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0,
                                                int arg1) {

                                // Get the clicked item label
                                String itemLabel = mCamerasDataSet.get(position).getCameraName();
                                // Remove the item on remove/button click
                                mCamerasDataSet.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,mCamerasDataSet.size());
                                // Show the removed item label

                                ArrayList<CameraSettings> cameras = CameraSettings.getCamerasFromSharedPreferences(v.getContext());
                                cameras.remove(position);
                                CameraSettings.saveCamerasToSharedPreferences(v.getContext(),cameras);

                                Toast.makeText(activity,"Removed : " + itemLabel,Toast.LENGTH_SHORT).show();
                            }
                        });
                alertbox.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertbox.show();
            }
        };
    }

    private View.OnClickListener cameraEditListener(final int position){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraSettings camera = mCamerasDataSet.get(position);
                Intent intent = new Intent(v.getContext(), OneCameraSettingActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("cameraObject", camera);
                b.putInt("cameraId", position);
                intent.putExtras(b);
                activity.startActivity(intent);
            }
        };
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mCamerasDataSet.size();
    }
}
