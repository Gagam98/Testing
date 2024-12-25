package mp.p02.home1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import java.io.IOException;
import java.io.InputStream;

public class home_ItemDetailActivity extends AppCompatActivity {

    private ImageView itemImageView;
    private TextView itemTitleTextView;
    private TextView itemContentTextView;
    private DrawerLayout drawerLayout;
    private home_ItemDatabaseHelper databaseHelper;
    private int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_item_detail);

        databaseHelper = new home_ItemDatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        itemImageView = findViewById(R.id.itemImageView);
        itemTitleTextView = findViewById(R.id.itemTitleTextView);
        itemContentTextView = findViewById(R.id.itemContentTextView);
        drawerLayout = findViewById(R.id.drawer_layout);

        Intent intent = getIntent();
        itemId = intent.getIntExtra("item_id", -1);

        if (itemId == -1 || !databaseHelper.doesItemExist(itemId)) {
            Toast.makeText(this, "Invalid item ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String title = intent.getStringExtra("item_title");
        String content = intent.getStringExtra("item_content");
        String imageUriString = intent.getStringExtra("item_image_uri");

        itemTitleTextView.setText(title != null ? title : "No Title");
        itemContentTextView.setText(content != null ? content : "No Content");

        if (imageUriString != null) {
            try {
                Uri imageUri = Uri.parse(imageUriString);
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                itemImageView.setImageBitmap(bitmap);
                if (inputStream != null) inputStream.close();
            } catch (IOException | SecurityException e) {
                itemImageView.setImageResource(R.drawable.button1);
                e.printStackTrace();
            }
        } else {
            itemImageView.setImageResource(R.drawable.button1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
