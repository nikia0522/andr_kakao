package com.hanbit.kakaotalk;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class Index extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        final Context context= Index.this;
        Handler handler=new Handler();
        SqLiteHelper helper=new SqLiteHelper(context);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(context,Main.class));
                finish();
            }
        },2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    public static class SqLiteHelper extends SQLiteOpenHelper {

        public SqLiteHelper(Context context) {
            super(context, "hanbit.db", null, 1);
            this.getWritableDatabase();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = String.format("CREATE TABLE IF NOT EXISTS " +
                    " %s( " +
                    "       %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "       %s TEXT, %s TEXT, %s TEXT, %s TEXT," +
                    "       %s TEXT, %s TEXT" +
                    " ); ", Cons.MEM_TBL, Cons.SEQ, Cons.PASS, Cons.NAME, Cons.EMAIL, Cons.PHONE, Cons.ADDR, Cons.PROFILE_IMG);
            db.execSQL(sql);
            //insertValues(db);

        }

        public void insertValues(SQLiteDatabase db){
            for (int i = 1; i < 7; i++) {
                db.execSQL(String.format(" INSERT INTO %s(%s,%s,%s,%s,%s,%s) " +
                                " VALUES ('%s','%s','%s','%s','%s','%s'); ", Cons.MEM_TBL, Cons.PASS, Cons.NAME, Cons.EMAIL, Cons.PHONE, Cons.ADDR, Cons.PROFILE_IMG, "1", "홍길동" + i,
                        "hong" + i + "test.com", "010-1234-567" + i, "서울" + i, "default_img"));
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
        public static abstract class QueryFactory {
            Context context;
            public QueryFactory(Context context) {
                this.context = context;
            }

            public abstract SQLiteDatabase getDatabase();
        }
    }

