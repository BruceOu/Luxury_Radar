package haifanou.com.luxuryradar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by ouhaifan on 3/22/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME="shirtsearch.db";
    public static final String TABLE_NAME="shirt_infor_table";
    public static final String COL_1="no";
    public static final String COL_2="shirtId";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ TABLE_NAME+" (no INTEGER PRIMARY KEY AUTOINCREMENT, shirtId TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("Example", "Upgrading database; this will drop and recreate the tables.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String shirtId){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_2, shirtId);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if(result==-1) {
            return false;
        }else{
            return true;
        }
    }

    //retutrn a list containing all the shirtId that user recently searched, from the most recently one to the oldest one
    public List<String> getAllShirtId() {
        SQLiteDatabase db=this.getWritableDatabase();
        List<String> list = new ArrayList<>();
        Cursor cursor=db.rawQuery("select * from "+TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        Collections.reverse(list);
        return list;
    }
}
