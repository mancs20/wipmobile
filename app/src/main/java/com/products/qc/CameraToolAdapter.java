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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manuel on 3/3/2018.
 */

public class CameraToolAdapter extends RecyclerView.Adapter<CameraToolAdapter.ViewHolder> {

    private List<CameraSettings> mCamerasDataSet;
    private Activity activity;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CameraToolAdapter(ArrayList<CameraSettings> myCamerasDataset, Activity activity) {
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
            ViewGroup parent = (ViewGroup) cameraRemoveButton.getParent();
            parent.removeView(cameraRemoveButton);
            cameraSettingLayout = (ConstraintLayout) v.findViewById(R.id.cameraSettingLayout);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CameraToolAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.camera_setting, parent, false);
        return new CameraToolAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CameraToolAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.nameTextView.setText(mCamerasDataSet.get(position).getCameraName());
        holder.ipTextView.setText(mCamerasDataSet.get(position).getCameraIP());
        holder.cameraSettingLayout.setOnClickListener(cameraViewListener(position));
    }

    private View.OnClickListener cameraViewListener(final int position){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraSettings camera = mCamerasDataSet.get(position);
                Intent intent = new Intent(v.getContext(), OneCameraSettingActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("cameraObject", camera);
                intent.putExtras(b);
                activity.startActivity(intent);
                activity.finish();
            }
        };
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mCamerasDataSet.size();
    }
}
