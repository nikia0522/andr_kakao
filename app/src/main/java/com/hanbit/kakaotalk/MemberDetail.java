package com.hanbit.kakaotalk;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MemberDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_detail);
        final Context context = MemberDetail.this;
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        TextView name = (TextView) findViewById(R.id.name);
        TextView email = (TextView) findViewById(R.id.email);
        TextView phone = (TextView) findViewById(R.id.phone);
        TextView address = (TextView) findViewById(R.id.addr);

        final String seq = this.getIntent().getStringExtra("seq");
        Toast.makeText(context, "불러온 seq 값: " + seq, Toast.LENGTH_LONG);
        final DetailMember dm=new DetailMember(context);
        Member m= (Member) new Service.IGet(){
            @Override
            public Object execute(Object O) {
                return dm.execute(seq);
            }
        }.execute(null);

        findViewById(R.id.updateBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MemberUpdate.class));
            }
        });
        findViewById(R.id.listBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MemberList.class));
            }
        });
        name.setText(m.getName());
        email.setText(m.getEmail());
        phone.setText(m.getPhone());
        address.setText(m.getAddr());

    }

    private abstract class DetailQuery extends Index.QueryFactory {
        SQLiteOpenHelper helper;

        public DetailQuery(Context context) {
            super(context);
            helper = new Index.SqLiteHelper(context);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }

    }

    private class DetailMember extends DetailQuery {

        public DetailMember(Context context) {
            super(context);
        }

        public Member execute(String seq) {
            String sql = (String.format("SELECT * FROM %s WHERE %s = '%s';", Cons.MEM_TBL, Cons.SEQ, seq));
            Cursor cursor = super.getDatabase().rawQuery(sql, null);
            Member member = null;
            if (cursor.moveToFirst()) {

                member = new Member();
                member.setSeq(cursor.getString(cursor.getColumnIndex(Cons.SEQ)));
                member.setName(cursor.getString(cursor.getColumnIndex(Cons.NAME)));
                member.setEmail(cursor.getString(cursor.getColumnIndex(Cons.EMAIL)));
                member.setPass(cursor.getString(cursor.getColumnIndex(Cons.PASS)));
                member.setAddr(cursor.getString(cursor.getColumnIndex(Cons.ADDR)));
                member.setPhone(cursor.getString(cursor.getColumnIndex(Cons.PHONE)));
                member.setProfileImage(cursor.getString(cursor.getColumnIndex(Cons.PROFILE_IMG)));
            }
            return member;
        }
    }
}
