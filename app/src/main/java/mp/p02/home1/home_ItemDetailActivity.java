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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.InputStream;

public class home_ItemDetailActivity extends AppCompatActivity {

    private ImageView itemImageView;
    private TextView itemTitleTextView;
    private TextView itemContentTextView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView; // NavigationView 선언
    private ImageView menuImage; // 사이드바 버튼 이미지
    private home_ItemDatabaseHelper databaseHelper;
    private int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_item_detail);

        // DatabaseHelper 초기화
        databaseHelper = new home_ItemDatabaseHelper(this);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 활성화
        }

        // View 초기화
        itemImageView = findViewById(R.id.itemImageView);
        itemTitleTextView = findViewById(R.id.itemTitleTextView);
        itemContentTextView = findViewById(R.id.itemContentTextView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view); // NavigationView 초기화
        menuImage = findViewById(R.id.menu_image); // menu_3dots 이미지 초기화

        // menuImage 클릭 시 DrawerLayout 열기/닫기
        menuImage.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // NavigationView 메뉴 아이템 클릭 리스너 설정
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_edit) {
                    Toast.makeText(home_ItemDetailActivity.this, "수정 메뉴 클릭됨", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_delete) {
                    // 삭제 로직 추가
                    if (databaseHelper.deleteItem(home_ItemDetailActivity.this.itemId)) {
                        Toast.makeText(home_ItemDetailActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        finish(); // 액티비티 종료
                    } else {
                        Toast.makeText(home_ItemDetailActivity.this, "삭제 실패.", Toast.LENGTH_SHORT).show();
                    }
                } else if (itemId == R.id.nav_report) {
                    Toast.makeText(home_ItemDetailActivity.this, "신고 메뉴 클릭됨", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(home_ItemDetailActivity.this, "알 수 없는 메뉴 클릭됨", Toast.LENGTH_SHORT).show();
                }

                drawerLayout.closeDrawer(GravityCompat.START); // 클릭 후 사이드바 닫기
                return true;
            }
        });

        // Intent에서 데이터 가져오기
        Intent intent = getIntent();
        itemId = intent.getIntExtra("item_id", -1);

        if (itemId == -1 || !databaseHelper.doesItemExist(itemId)) {
            Toast.makeText(this, "Invalid item ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 데이터 설정
        String title = intent.getStringExtra("item_title");
        String content = intent.getStringExtra("item_content");
        String imageUriString = intent.getStringExtra("item_image_uri");

        itemTitleTextView.setText(title != null ? title : "No Title");
        itemContentTextView.setText(content != null ? content : "No Content");

        // 이미지 설정
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
            finish(); // 뒤로가기 버튼 클릭 시 액티비티 종료
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
