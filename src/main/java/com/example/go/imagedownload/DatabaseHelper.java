package com.example.go.imagedownload;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
public class DatabaseHelper { // 데이터 베이스 클래스

    // DB관련 상수 선언
    private static final String dbName = "imageTest6.db";
    private static final String tableName = "Image";
    public static final int dbVersion = 1;

    // DB관련 객체 선언
    private OpenHelper opener; // DB opener
    private SQLiteDatabase db; // DB controller

    // 부가적인 객체들
    private Context context;

    // 생성자
    public DatabaseHelper(Context context) {
        this.context = context;
        this.opener = new OpenHelper(context, "/mnt/sdcard/" + dbName, null, dbVersion);
        db = opener.getWritableDatabase();
    }

    // Opener of DB and Table
    private class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
            super(context, name, null, version);
            // TODO Auto-generated constructor stub
        }

        // 생성된 DB가 없을 경우에 한번만 호출됨
        @Override
        public void onCreate(SQLiteDatabase arg0) {
            String createSql = "create table " + tableName + " ("
                    + "src text primary key not null, " + "image blob)";
            arg0.execSQL(createSql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            clear();
            onCreate(arg0);
            // TODO Auto-generated method stub
        }
    }

    // 데이터 추가
    public void insertData(ImageItem item) {
        ContentValues values = new ContentValues();
        values.put("src", item.getTitle());
        values.put("image", bitmapToByteArray(item.getImage()));

        try {
            db.insert(tableName, null, values);
        }catch(Exception e){}
    }

    // 데이터 추가
    public void clear() {
        String sql = "drop table " + tableName ;
        try {
            db.execSQL(sql);
        }catch(Exception e){}
    }


    // 데이터 전체 검색
    public ArrayList<ImageItem> selectAll() {
        String sql = "select * from " + tableName + ";";
        Cursor results = db.rawQuery(sql, null);

        results.moveToFirst();
        ArrayList<ImageItem> infos = new ArrayList<ImageItem>();

        while (!results.isAfterLast()) {
            ImageItem data = new ImageItem(byteArrayToBitmap(results.getBlob(1)),results.getString(0));
            infos.add(data);
            results.moveToNext();
        }
        results.close();
        return infos;
    }
    //비트맵에서 바이트 배열로 변환
    public byte[] bitmapToByteArray( Bitmap bitmap ) {

        if(bitmap == null)
            return null;

        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }

    // 바이트 배열에서 비트맵으로 변환
    public Bitmap byteArrayToBitmap( byte[] byteArray ) {
        if(byteArray == null)
            return null;
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length) ;
        return bitmap ;
    }

}