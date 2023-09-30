package com.satifeed.db.entity;

public class Notice {
    // Constants for database
    public static final String TABLE_NAME = "notices";
    public static final String COLUMN_ID = "notice_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_FILE = "file";

    // Variables to store each notice name and its file name
    private int id;
    private String name;
    private String file;

    private Notice(){};

    public Notice(int id, String name, String file) {
        this.id = id;
        this.name = name;
        this.file = file;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    // SQL query to create the notices table
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME
            + " TEXT, " + COLUMN_FILE + " TEXT)";
}
