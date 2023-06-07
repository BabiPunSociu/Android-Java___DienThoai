package com.example.adapterview;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import  android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class MyDB extends SQLiteOpenHelper {
    private static  final String TableName = "ContactTable";
    private static final String id = "id";
    private static final String name = "FullName";
    private static final String phone = "PhoneNumber";
    private static final String images = "Image";

    public MyDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreate = "Create table if not exists " + TableName + "("
                + id + " Integer Primary key, "
                + images + " Text, "
                + name + " Text not null, "
                + phone + " Text not null )";
        db.execSQL(sqlCreate);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+ TableName);
        onCreate(db);
    }

    public ArrayList<Contact> getAllContact()
    {
        ArrayList<Contact> list = new ArrayList<>();
        String sql = "Select * from " + TableName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor != null)
            while (cursor.moveToNext())
            {
                Contact contact = new Contact(cursor.getInt(0), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3));
                list.add(contact);
            }
        return list;
    }

    // Them mot contact vao bang TableContact
    public void addContact(Contact contact)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues  values = new ContentValues();
        values.put(id, contact.getId());
        values.put(images, contact.getImages());
        values.put(name, contact.getName());
        values.put(phone, contact.getPhone());
        db.insert(TableName, null, values);
        db.close();
    }

    public void updateContact(int id, Contact contact)
    {

    }
}
