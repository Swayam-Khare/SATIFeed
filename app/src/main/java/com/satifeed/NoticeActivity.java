package com.satifeed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.satifeed.db.DatabaseHelper;
import com.satifeed.db.entity.Notice;
import com.satifeed.fragments.NoticeFragment;
import com.satifeed.fragments.SyllabusFragment;
import com.satifeed.fragments.TimeTableFragment;
import com.satifeed.service.NotifyService;
import com.satifeed.utils.DataUtil;

import java.util.HashMap;

public class NoticeActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    FirebaseFirestore db;
    ActivityResultLauncher<String> mGetContent;
    DatabaseHelper dbHelper;
    NavigationView navigationView;
    BottomNavigationView bottomMenu;
    Toolbar toolbar;
    FloatingActionButton fab;
    FirebaseAuth fAuth;
    TextView filePath;
    Uri fileUri;
    boolean isFaculty;
    ProgressBar progressBar2;
    FirebaseStorage storage;
    StorageReference storageRef;
    boolean fileSelected;
    NoticeFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bottomMenu = findViewById(R.id.bottomMenu);
        toolbar = findViewById(R.id.mainToolbar);
        dbHelper = new DatabaseHelper(this);

        // Set up references
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        bottomMenu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.inboxOpt) {
                currentFragment = new NoticeFragment(this);
                loadFragment(currentFragment);
            } else if (itemId == R.id.timeOpt) {
                loadFragment(new TimeTableFragment(this));
            } else {
                loadFragment(new SyllabusFragment(this));
            }

            return true;
        });

        // Set up navigation drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.calendarOpt){
                openPDF(new Notice(1008, "calendar", "academic_calendar"));
            } else if (itemId == R.id.instagramOpt) {
                openLink(0);
            }
            else if (itemId == R.id.facebookOpt) {
                openLink(1);
            }
            else if (itemId == R.id.websiteOpt) {
                openLink(2);
            }
            else if (itemId == R.id.youtubeOpt) {
                openLink(3);
            }
            else if (itemId == R.id.linkedinOpt) {
                openLink(4);
            }

            return true;
        });

        currentFragment = new NoticeFragment(this);
        loadFragment(currentFragment);

        // If faculty logged in, then show the FAB else hide it
        Intent i = getIntent();
        if (i.getStringExtra("faculty").equals("Yes")) {
            isFaculty = true;
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(this::setListener);
        } else {
            isFaculty = false;
            fab.setVisibility(View.GONE);
        }


        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    fileUri = uri;
                    if (fileUri != null) {
                        filePath.setText("File path: " + uri.getPath());
                        fileSelected = true;
                    }
                });

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        // Create ActionBarDrawerToggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.OpenDrawer, R.string.CloseDrawer);

        // Set DrawerListener
        drawerLayout.addDrawerListener(toggle);

        // Sync the toggle state
        toggle.syncState();
    }

    private void openLink(int i) {
        String url = null;
        switch (i){
            case 0:
                url = "https://www.instagram.com/satiengg.in/";
                break;
            case 1:
                url = "https://www.facebook.com/satiengg.in";
                break;
            case 2:
                url = "http://www.satiengg.in/";
                break;
            case 3:
                url = "https://www.youtube.com/@sati.vidisha";
                break;
            default:
                url = "https://www.linkedin.com/school/satiengg/";
                break;
        }

        Intent intentWeb = new Intent(Intent.ACTION_VIEW);
        intentWeb.setData(Uri.parse(url));
        startActivity(intentWeb);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentFragment = new NoticeFragment(NoticeActivity.this);
        loadFragment(currentFragment);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.noticeFrameLayout, fragment);
        ft.commit();
    }

    private void setListener(View view) {
        fileSelected = false;

        // Create the "Add Notice" view from the add_notice_layout.xml layout file
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogView = inflater.inflate(R.layout.add_notice_layout, null);

        MaterialAlertDialogBuilder alertBuilder = new MaterialAlertDialogBuilder(
                NoticeActivity.this, R.style.MyDialogTheme);
        alertBuilder.setView(dialogView);

        // Get the file name from the EditText
        final EditText nameField = dialogView.findViewById(R.id.fileName);
        filePath = dialogView.findViewById(R.id.filePath);
        final Button selectBtn = dialogView.findViewById(R.id.selectFileBtn);
        progressBar2 = dialogView.findViewById(R.id.progressBar2);

        selectBtn.setOnClickListener(v2 -> mGetContent.launch("application/pdf"));

        alertBuilder
                .setCancelable(false)
                .setPositiveButton("Add", (di, i) -> {
                })
                .setNegativeButton("Cancel", (di, i) -> di.cancel());

        final AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view2 -> {
            if (TextUtils.isEmpty(nameField.getText())) {
                nameField.setError("Name is required");
                return;
            }
            if (!fileSelected) {
                Toast.makeText(this, "Please select a PDF file", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar2.setVisibility(View.VISIBLE);
            String name = nameField.getText().toString().trim() + "_" + System.currentTimeMillis();

            uploadFile(name, alertDialog);
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isFaculty) {
            new MenuInflater(this).inflate(R.menu.notice_menu, menu);
        }
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.logoutOpt) {
            fAuth.signOut();
            finish();
        }
        else if (itemId == R.id.refreshOpt){
            currentFragment = new NoticeFragment(this);
            loadFragment(currentFragment);
        }
        return true;
    }

    public void handleNoticeClick(Notice notice, int position) {
        if (isFaculty) {
            generateAlertBox(notice, position);
        } else {
            openPDF(notice);
        }
    }

    private void openPDF(Notice notice) {
        Intent i = new Intent(NoticeActivity.this, ViewPdfActivity.class);
        i.putExtra("filename", notice.getFile());
        startActivity(i);
    }

    private void generateAlertBox(Notice notice, int position) {
        // Create the "Add Notice" view from the add_notice_layout.xml layout file
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View dialogView = inflater.inflate(R.layout.notice_popup, null);

        MaterialAlertDialogBuilder alertBuilder = new MaterialAlertDialogBuilder(
                NoticeActivity.this, R.style.MyDialogTheme);
        alertBuilder.setView(dialogView);

        TextView noticeName = dialogView.findViewById(R.id.noticeName);
        TextView dateText = dialogView.findViewById(R.id.dateText);
        TextView timeText = dialogView.findViewById(R.id.timeText);

        noticeName.setText(notice.getName());
        dateText.setText(DataUtil.formatDate(notice.getFile()));
        timeText.setText(DataUtil.formatTime(notice.getFile()));

        alertBuilder
                .setCancelable(true)
                .setPositiveButton("Delete", (di, i) -> {
                })
                .setNegativeButton("Open", (di, i) -> {
                });

        final AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(pBtn -> {
            // TODO: Handle progress Bar

            deleteNotice(notice, alertDialog, position);
        });

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(nBtn -> {
            openPDF(notice);
            alertDialog.dismiss();
        });
    }

    private void uploadFile(String name, AlertDialog alertDialog) {

        // Upload File to the cloud
        StorageReference riversRef = storageRef.child("notice/" + name + ".pdf");
        UploadTask uploadTask = riversRef.putFile(fileUri);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Notice uploaded successfully", Toast.LENGTH_LONG).show();
                dbHelper.insertNotice(name.split("_")[0], name);
                sendToFirestore(name);
                currentFragment = new NoticeFragment(NoticeActivity.this);
                loadFragment(currentFragment);
            } else {
                Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
            progressBar2.setVisibility(View.GONE);
            alertDialog.dismiss();
        });
    }

    private void sendToFirestore(String name) {
        HashMap<String, String> noticeData = new HashMap<>();
        noticeData.put("name", name);

        db.collection("notices")
                .document(name)
                .set(noticeData)
                .addOnSuccessListener(aVoid -> Log.d("NoticeActivity.java", "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("NoticeActivity.java", "Error writing document", e));

    }

    private void deleteNotice(Notice notice, AlertDialog alertDialog, int position) {
        StorageReference riversRef = storageRef.child("notice/" + notice.getFile() + ".pdf");
        riversRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Notice deleted successfully", Toast.LENGTH_SHORT).show();
//                dbHelper.deleteNotice(notice);
                currentFragment.deleteNotice(notice, position);
                deleteFromFirebase(notice.getFile());
//                currentFragment = new NoticeFragment(NoticeActivity.this);
//                loadFragment(currentFragment);
                alertDialog.dismiss();
                // TODO: Handle the progress bar visibility
            }
        }).addOnFailureListener(exception -> {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            // TODO: Handle the progress bar visibility
        });
    }

    private void deleteFromFirebase(String file) {
        db.collection("notices").document(file).delete();
    }

    public void handleTimeClick(Notice notice) {
        openPDF(notice);
    }
}