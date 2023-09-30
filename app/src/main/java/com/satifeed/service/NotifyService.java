package com.satifeed.service;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.satifeed.MainActivity;
import com.satifeed.NoticeActivity;
import com.satifeed.R;
import com.satifeed.ViewPdfActivity;
import com.satifeed.db.DatabaseHelper;

public class NotifyService extends Service {

    DatabaseHelper dbHelper;
    FirebaseFirestore db;
    boolean firstStart;
    Intent mIntent;
    int mFlags;
    int mStartId;

    public NotifyService() {
        firstStart = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("NotifyService", "No permission for notification");
        db = FirebaseFirestore.getInstance();
        dbHelper = new DatabaseHelper(this);

        mIntent = intent;
        mFlags = flags;
        mStartId = startId;

        Intent intent2 = new Intent(this, MainActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(this, 0,
                intent2, PendingIntent.FLAG_IMMUTABLE);

        db.collection("notices")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("NoticeActivity.java", "listen:error", e);
                        return;
                    }

                    Log.d("NotifyService", "inside listener");

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        String file = (String) dc.getDocument().getData().get("name");
                        String name = file.split("_")[0];
                        Log.d("NotifyService", "inside for loop");

                        switch (dc.getType()) {
                            case ADDED:
                                if (!dbHelper.containsNotice(file)){
                                    Log.d("NotifyService", "file name: " + file);
                                    dbHelper.insertNotice(name, file);
                                    sendNotification(pendingIntent1, name);
                                }
                                break;
                            case MODIFIED:
                                Log.d("NoticeActivity.java", "Modified city: " + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                dbHelper.deleteNotice(file);
                                break;
                        }
                    }
//                    sendNotification(pendingIntent1);
                });

        return START_STICKY;
    }

    private void sendNotification(PendingIntent pendingIntent, String name) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationCompat.CATEGORY_MESSAGE)
                .setSmallIcon(R.drawable.iconsati)
                .setContentTitle("SATI Feed")
                .setContentText("New Notice: " + name)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("NotifyService", "No permission for notification");
            return;
        }
        notificationManager.notify(1008, builder.build());
    }

    @Override
    public void onDestroy() {
        onStartCommand(mIntent, mFlags, mStartId);
        super.onDestroy();
    }
}


//                                    Intent intent1 = new Intent(this, ViewPdfActivity.class);
//                                    intent1.putExtra("filename", file);
//                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                                            intent1, PendingIntent.FLAG_IMMUTABLE);