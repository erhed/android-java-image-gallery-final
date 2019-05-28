package se.maj7.imagegallerythree;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private ArrayList<ImageItem> mImages;
    private GalleryActivity mActivity;
    private int mMode;

    private static final int CARD_VIEW = 0;
    private static final int GRID_VIEW = 1;

    public GalleryAdapter(GalleryActivity activity, int viewMode, ArrayList<ImageItem> images) {

        mActivity = activity;
        mMode = viewMode;
        mImages = images;
    }

    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;

        if (mMode == CARD_VIEW) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);
        } else if (mMode == GRID_VIEW) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_grid, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);
        }

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Image item
        ImageItem item = mImages.get(position);

        if (mMode == CARD_VIEW) {
            // Title
            holder.mTitle.setText(item.getTitle());
            // Date
            holder.mDateCreated.setText(item.getDateString());
             // Thumbnail
            Bitmap thumb = ImageProcessor.getBitmapFromString(item.getThumb());
            holder.mImageCard.setImageBitmap(thumb);
        }

        if (mMode == GRID_VIEW) {
            // Thumbnail
            Bitmap thumb = ImageProcessor.getBitmapFromString(item.getThumb());
            holder.mImageGrid.setImageBitmap(thumb);
        }
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView mTitle;
        TextView mDateCreated;
        ImageView mImageCard;
        ImageView mImageGrid;

        public ViewHolder(View view) {
            super(view);

            mTitle = (TextView) view.findViewById(R.id.textListTitle);
            mDateCreated = (TextView) view.findViewById(R.id.textListDate);
            mImageCard = (ImageView) view.findViewById(R.id.imageHolderList);
            mImageGrid = (ImageView) view.findViewById(R.id.imageHolderGrid);

            view.setClickable(true);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ImageItem image = mImages.get(getAdapterPosition());
            mActivity.showPicture(image.getImageUriString(), image.getTitle(), getAdapterPosition());
        }
    }
}
