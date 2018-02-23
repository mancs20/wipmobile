package com.products.qc;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by manuel on 2/21/2018.
 */

public class CamerasSettingAdapter extends RecyclerView.Adapter<CamerasSettingAdapter.ViewHolder> {
    //private String[] mCamerasDataSet;
    private List<CameraSetting> mCamerasDataSet;
    private Context mContext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CamerasSettingAdapter(List<CameraSetting> myCamerasDataset, Context context) {
        mCamerasDataSet = myCamerasDataset;
        mContext = context;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView nameTextView;
        public TextView ipTextView;
        public Button cameraRemoveButton;
        public ConstraintLayout cameraSettingLayout;
        public ViewHolder(View v) {
            super(v);
            nameTextView = v.findViewById(R.id.cameraName);
            ipTextView = v.findViewById(R.id.cameraIP);
            cameraRemoveButton = v.findViewById(R.id.buttonRemoveCamera);
            cameraSettingLayout = v.findViewById(R.id.cameraSettingLayout);
        }
    }



    // Create new views (invoked by the layout manager)
    @Override
    public CamerasSettingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.camera_setting, parent, false);
        //View v = LayoutInflater.from(mContext).inflate(R.layout.camera_setting,parent,false);
        //ViewHolder vh = new ViewHolder(v);
        return new ViewHolder(v);
        //return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.nameTextView.setText(mCamerasDataSet.get(position).getCameraName());
        holder.ipTextView.setText(mCamerasDataSet.get(position).getCameraIP());

        // Set a click listener for item remove button
        holder.cameraRemoveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Get the clicked item label

                String itemLabel = mCamerasDataSet.get(holder.getAdapterPosition()).getCameraName();

                // Remove the item on remove/button click
                mCamerasDataSet.remove(holder.getAdapterPosition());

                /*
                    public final void notifyItemRemoved (int position)
                        Notify any registered observers that the item previously located at position
                        has been removed from the data set. The items previously located at and
                        after position may now be found at oldPosition - 1.

                        This is a structural change event. Representations of other existing items
                        in the data set are still considered up to date and will not be rebound,
                        though their positions may be altered.

                    Parameters
                        position : Position of the item that has now been removed
                */
                notifyItemRemoved(holder.getAdapterPosition());

                /*
                    public final void notifyItemRangeChanged (int positionStart, int itemCount)
                        Notify any registered observers that the itemCount items starting at
                        position positionStart have changed. Equivalent to calling
                        notifyItemRangeChanged(position, itemCount, null);.

                        This is an item change event, not a structural change event. It indicates
                        that any reflection of the data in the given position range is out of date
                        and should be updated. The items in the given range retain the same identity.

                    Parameters
                        positionStart : Position of the first item that has changed
                        itemCount : Number of items that have changed
                */
                notifyItemRangeChanged(holder.getAdapterPosition(),mCamerasDataSet.size());

                // Show the removed item label
                Toast.makeText(mContext,"Removed : " + itemLabel,Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mCamerasDataSet.size();
    }
}
