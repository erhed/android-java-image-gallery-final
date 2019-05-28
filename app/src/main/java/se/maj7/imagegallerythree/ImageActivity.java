package se.maj7.imagegallerythree;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageActivity extends AppCompatActivity {

    ImageView backButton;
    ImageView deleteButton;

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // Status bar color

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // Get data

        Bundle extras = getIntent().getExtras();
        String uriString = extras.getString("uri");
        String title = extras.getString("title");
        position = extras.getInt("position");

        // Back button

        backButton = (ImageView) findViewById(R.id.detailBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //overridePendingTransition(0, R.anim.slide_in_right);
            }
        });

        // Delete button

        deleteButton = (ImageView) findViewById(R.id.detailDeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("position", position);
                setResult(GalleryActivity.RESULT_OK, data);
                finish();
            }
        });

        // Title

        TextView titleText = (TextView) findViewById(R.id.detailTextTitle);
        titleText.setText(title);

        // Image

        ImageView image = (ImageView) findViewById(R.id.imageDetailView);
        image.setImageURI(Uri.parse(uriString));
    }
}
