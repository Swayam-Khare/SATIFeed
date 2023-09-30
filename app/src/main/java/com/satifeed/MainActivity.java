package com.satifeed;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.satifeed.db.DatabaseHelper;
import com.satifeed.service.NotifyService;

public class MainActivity extends AppCompatActivity {

    Button studentBtn, teacherBtn;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studentBtn = findViewById(R.id.imStudentBtn);
        teacherBtn = findViewById(R.id.imTeacherBtn);
        dbHelper = new DatabaseHelper(this);

        createNotificationChannel();

        startService(new Intent(this, NotifyService.class));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notices")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        dbHelper.updateData(dbHelper.getWritableDatabase(), task.getResult());
                    }
                });

        studentBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
            intent.putExtra("faculty", "No");
            startActivity(intent);
        });

        teacherBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notice";
            String description = "College Notices";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(Notification.CATEGORY_MESSAGE, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}