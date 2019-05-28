package se.maj7.imagegallerythree;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;

    private ArrayList<ImageItem> mImages = new ArrayList<>();
    private JSONSerializer mSerializer;

    private RecyclerView recyclerView;
    private GalleryAdapter mAdapter;

    private String mImageTitle;
    private Uri mTempURI;
    String mCurrentPhotoPath;

    private static final int IMAGE_FROM_GALLERY = 0;
    private static final int IMAGE_FROM_CAMERA = 1;
    private static final int DELETE_IMAGE = 2;

    private static final int CARD_VIEW = 0;
    private static final int GRID_VIEW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Don't move menu when keyboard is shown

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Load images from file

        mSerializer = new JSONSerializer("images.json", this);

        loadImages();

        // Menu buttons

        setupMenuButtons();

        // RecyclerView

        // Get view mode, Card/Grid
        sharedPreferences = this.getSharedPreferences("se.maj7.imagegallerythree", Context.MODE_PRIVATE);
        int viewMode = sharedPreferences.getInt("view_mode", 0);

        // Setup RecyclerView
        setupRecyclerView(viewMode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        boolean addNewImage = false;

        if (requestCode == IMAGE_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            mTempURI = uri;
            addNewImage = true;
        }

        if (requestCode == IMAGE_FROM_CAMERA && resultCode == RESULT_OK && data != null) {
            mTempURI = addImageToGallery();
            addNewImage = true;
        }

        if (requestCode == DELETE_IMAGE && resultCode == RESULT_OK && data != null) {
            int position = data.getIntExtra("position", -1);
            if (position >= 0) {
                deleteImage(position);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

        if (addNewImage) {
            addTitleDialog();
        }

    }

    private void setupRecyclerView(int viewMode) {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mAdapter = new GalleryAdapter(this, viewMode, mImages);

        // Remove previously added item decorations
        while (recyclerView.getItemDecorationCount() > 0) {
            recyclerView.removeItemDecorationAt(0);
        }

        // Set layout, Card/Grid

        if (viewMode == CARD_VIEW) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        } else if (viewMode == GRID_VIEW) {
            // Different spanCount for landscape/portrait
            //Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            //int rotation = display.getRotation();
            //int spanCount = (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) ? 2:3;

            int spanCount = 2;

            // Grid spacing
            int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
            boolean includeEdge = true;
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), spanCount));
        }

        recyclerView.setAdapter(mAdapter);
    }

    private void setupMenuButtons() {
        ImageView viewList = (ImageView) findViewById(R.id.imageHolderList);
        viewList.setOnClickListener(this);
        ImageView viewGrid = (ImageView) findViewById(R.id.imageGrid);
        viewGrid.setOnClickListener(this);
        ImageView getImageCamera = (ImageView) findViewById(R.id.imageCamera);
        getImageCamera.setOnClickListener(this);
        ImageView getImageGallery = (ImageView) findViewById(R.id.imageGallery);
        getImageGallery.setOnClickListener(this);
    }

    // Menu button actions

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageHolderList:
                sharedPreferences.edit().putInt("view_mode", 0).apply();
                setupRecyclerView(CARD_VIEW);
                break;
            case R.id.imageGrid:
                sharedPreferences.edit().putInt("view_mode", 1).apply();
                setupRecyclerView(GRID_VIEW);
                break;
            case R.id.imageCamera:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        startActivityForResult(takePictureIntent, IMAGE_FROM_CAMERA);
                    }
                }
                break;
            case R.id.imageGallery:
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
                startActivityForResult(chooserIntent, IMAGE_FROM_GALLERY);
                break;
        }
    }

    // Data

    private void loadImages() {
        try {
            mImages = mSerializer.load();
        } catch (IOException e) {
            Log.i("ERROR","Could not load images");
        }
    }

    private void saveImages() {
        try {
            mSerializer.save(mImages);
        } catch (IOException e) {
            Log.i("ERROR","Could not save images");
        }
    }

    public void deleteImage(int position) {
        mImages.remove(position);
        saveImages();
        mAdapter.notifyDataSetChanged();
    }

    private void sortImages() {
        Collections.sort(mImages, new Comparator<ImageItem>() {
            @Override
            public int compare(ImageItem o1, ImageItem o2) { return o1.getDate().compareTo(o2.getDate()); }
        });

        // Latest first
        Collections.reverse(mImages);
    }

    public void addImage() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mTempURI);
            Bitmap scaledBitmap = ImageProcessor.scaleDownBitmap(bitmap, 400);
            String bitmapString = ImageProcessor.getStringFromBitmap(scaledBitmap);

            ImageItem image = new ImageItem(mImageTitle, mTempURI.toString(), bitmapString);
            mImages.add(image);
            sortImages();
            saveImages();
            mAdapter.notifyDataSetChanged();
        } catch (IOException e) {
            Log.i("ERROR","Could not add image");
        }
    }

    // For image from camera
    private File createImageFile() throws IOException {
        File storageDir = Environment.getExternalStorageDirectory();
        File image = File.createTempFile(
                "example",  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // For image from camera
    private Uri addImageToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        return contentUri;
    }

    public void setImageTitle(String title) {
        mImageTitle = title;
        addImage();
    }

    // Image view detail activity

    public void showPicture(String uriString, String title, int position) {
        Intent intent = new Intent(GalleryActivity.this, ImageActivity.class);
        intent.putExtra("uri", uriString);
        intent.putExtra("title", title);
        intent.putExtra("position", position);
        //Bundle bndlAnimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_left, R.anim.still).toBundle();
        GalleryActivity.this.startActivityForResult(intent, DELETE_IMAGE);
    }

    // Image title dialog

    private void addTitleDialog() {
        TitleDialog dialog = new TitleDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

    // Dp to pixel converter

    public static int dpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
}
