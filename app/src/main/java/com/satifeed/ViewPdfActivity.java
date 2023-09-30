package com.satifeed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ViewPdfActivity extends AppCompatActivity {

    PDFView pdfView;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        pdfView = findViewById(R.id.pdfView);
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Intent intent = getIntent();
        String fileName = intent.getStringExtra("filename");

        StorageReference pathReference = storageRef.child("notice/" + fileName + ".pdf");

        File localFile;
        try {
            localFile = File.createTempFile("temp", "pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File finalFile = localFile;
        pathReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            pdfView.fromFile(finalFile).spacing(2).load();
        }).addOnFailureListener(exception -> {
//            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, fileName, Toast.LENGTH_SHORT).show();
        });   }
}