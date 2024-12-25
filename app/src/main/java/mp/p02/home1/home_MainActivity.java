package mp.p02.home1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class home_MainActivity extends AppCompatActivity {

    private static final int WRITE_POST_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE = 100; // 권한 요청 코드
    private static final int BANNER_IMAGE_CHANGE_INTERVAL = 4000; // 4초
    private ImageView bannerImageView;
    private Handler handler = new Handler();
    private int currentImageIndex = 0;

    // 배너에 사용할 이미지 배열
    private int[] bannerImages = {R.drawable.dog1, R.drawable.dog2, R.drawable.dog3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_main);

        // 권한 요청
        requestPermissions();

        // Set click listener for the banner image
        bannerImageView = findViewById(R.id.bannerImageView);
        bannerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to BannerContentActivity
                Intent intent = new Intent(home_MainActivity.this, home_BannerContentActivity.class);
                startActivity(intent);
            }
        });

        // 시작 시 배너 이미지 변경 시작
        startBannerImageSwitching();

        // Set click listeners for each image
        ImageView image1 = findViewById(R.id.button1);
        ImageView image2 = findViewById(R.id.button2);
        ImageView image3 = findViewById(R.id.button3);
        ImageView image4 = findViewById(R.id.button4);

        // Image click listeners
        image1.setOnClickListener(v -> Toast.makeText(home_MainActivity.this, "Image 1 Clicked!", Toast.LENGTH_SHORT).show());
        image2.setOnClickListener(v -> Toast.makeText(home_MainActivity.this, "Image 2 Clicked!", Toast.LENGTH_SHORT).show());
        image3.setOnClickListener(v -> Toast.makeText(home_MainActivity.this, "Image 3 Clicked!", Toast.LENGTH_SHORT).show());
        image4.setOnClickListener(v -> Toast.makeText(home_MainActivity.this, "Image 4 Clicked!", Toast.LENGTH_SHORT).show());

        // FloatingActionButton click listener
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            // Switch to WritePostActivity
            Intent intent = new Intent(home_MainActivity.this, home_WritePostActivity.class);
            startActivityForResult(intent, WRITE_POST_REQUEST_CODE);
        });

        // Recent Items Button click listener
        Button recentButton = findViewById(R.id.recent_button);
        recentButton.setOnClickListener(v -> {
            // Switch to RecentItemsActivity
            Intent intent = new Intent(home_MainActivity.this, home_RecentItemsActivity.class);
            startActivity(intent);
        });

        // "전체보기" Button click listener
        Button viewAllButton = findViewById(R.id.view_all_button);
        viewAllButton.setOnClickListener(v -> {
            // Switch to RecentItemsActivity
            Intent intent = new Intent(home_MainActivity.this, home_RecentItemsActivity.class);
            startActivity(intent);
        });
    }

    private void startBannerImageSwitching() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 이미지 변경
                bannerImageView.setImageResource(bannerImages[currentImageIndex]);
                currentImageIndex = (currentImageIndex + 1) % bannerImages.length; // 다음 이미지 인덱스로 전환

                // 일정 시간 후 다시 실행
                handler.postDelayed(this, BANNER_IMAGE_CHANGE_INTERVAL);
            }
        }, BANNER_IMAGE_CHANGE_INTERVAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 종료될 때 핸들러 콜백 제거
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WRITE_POST_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // WritePostActivity로부터 전달된 데이터 받기
            String title = data.getStringExtra("title");
            String content = data.getStringExtra("content");
            String imageUriString = data.getStringExtra("imageUri");

            // 로그로 데이터 확인
            Log.d("MainActivity", "Title: " + title);
            Log.d("MainActivity", "Content: " + content);
            Log.d("MainActivity", "ImageUri: " + imageUriString);

            // Null 값 체크 및 처리
            if (title == null || content == null) {
                Toast.makeText(this, "데이터가 올바르게 전달되지 않았습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 데이터를 RecentItemsActivity로 전달
            Intent intent = new Intent(home_MainActivity.this, home_RecentItemsActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.putExtra("imageUri", imageUriString);
            startActivity(intent);
        } else {
            Log.d("MainActivity", "onActivityResult: 데이터가 전달되지 않음.");
        }
    }

    // 권한 요청 메서드
    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 이상
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
            }
        } else {
            // Android 12 이하
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
