package com.satifeed.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.satifeed.db.entity.Notice;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notices_db";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Notice.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Notice.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertNotice(String name, String file){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Notice.COLUMN_NAME, name);
        values.put(Notice.COLUMN_FILE, file);

        db.insert(Notice.TABLE_NAME, null, values);
        db.close();
    }

    public boolean containsNotice(String file){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Notice.TABLE_NAME,
                new String[]{Notice.COLUMN_ID,
                        Notice.COLUMN_NAME,
                        Notice.COLUMN_FILE
                },
                Notice.COLUMN_FILE + "=?",
                new String[]{file},
                null, null, null, null
        );

        return cursor.moveToFirst();
    }

    public Notice getNotice(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        // select * from TABLE_NAME where id = id
        Cursor cursor = db.query(Notice.TABLE_NAME,
                    new String[]{Notice.COLUMN_ID,
                            Notice.COLUMN_NAME,
                            Notice.COLUMN_FILE
                    },
                Notice.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null
        );

        Notice notice = null;

        if (cursor != null){
            cursor.moveToFirst();

            notice = new Notice(
                    cursor.getInt(cursor.getColumnIndexOrThrow(Notice.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Notice.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Notice.COLUMN_FILE))
            );

            cursor.close();
        }

        return notice;
    }

    public ArrayList<Notice> getAllNotices(){
        ArrayList<Notice> notices = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Notice.TABLE_NAME + " ORDER BY "
                + Notice.COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do{
                Notice notice = new Notice(
                        cursor.getInt(cursor.getColumnIndexOrThrow(Notice.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Notice.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Notice.COLUMN_FILE))
                );

                notices.add(notice);

            }while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return notices;
    }

    public void deleteNotice(Notice notice){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Notice.TABLE_NAME, Notice.COLUMN_ID + "=?",
                new String[]{String.valueOf(notice.getId())});

        db.close();
    }

    public void deleteNotice(String file){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Notice.TABLE_NAME, Notice.COLUMN_FILE + "=?",
                new String[]{file});

        db.close();
    }

    public void updateData(SQLiteDatabase sqLiteDatabase,QuerySnapshot result) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Notice.TABLE_NAME);
        onCreate(sqLiteDatabase);

        for (QueryDocumentSnapshot document : result) {
            String file = (String) document.getData().get("name");
            String name = file.split("_")[0];

            insertNotice(name, file);
        }

    }
}
