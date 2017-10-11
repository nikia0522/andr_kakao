package com.hanbit.kakaotalk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MemberList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_list);
        final Context context = MemberList.this;
        final ListView listView=(ListView) findViewById(R.id.listView);
        final FriendList friendList=new FriendList(context);
        ArrayList<Member> friends=(ArrayList<Member>)new Service.IList() {
            @Override
            public ArrayList<?> execute(Object O) {
                return friendList.execute();
            }
        }.execute(null);
        listView.setAdapter(new MemberAdaptor(context, friends));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int i, long l) {
                Member m= (Member) listView.getItemAtPosition(i);
                Toast.makeText(context,"id:"+m.getName(),Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(context,MemberDetail.class);
                intent.putExtra("seq",m.getSeq());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> p, View v, int i, long l) {
                final Member m= (Member) listView.getItemAtPosition(i);
                new AlertDialog.Builder(context)
                        .setTitle(m.getName()+"을 정말 삭제하시겠습니까?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final DeleteMember dm=new DeleteMember(context);
                                new Service.IDelete() {
                                @Override
                                public void execute(Object O) {
                                    Log.d("삭제할 ID: ",m.getSeq());
                                    dm.execute(m.getSeq());
                                    }
                                }.execute(null);
                                startActivity(new Intent(context, MemberList.class));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(context, MemberList.class));
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    private abstract class ListQuery extends Index.QueryFactory{
        SQLiteOpenHelper helper;
        public ListQuery(Context context) {
            super(context);
            helper=new Index.SqLiteHelper(context);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }
    }
    private class FriendList extends ListQuery{
        public FriendList(Context context) {
            super(context);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return super.getDatabase();
        }
        public ArrayList<Member> execute(){
            ArrayList<Member> list=new ArrayList<>();
            String sql=String.format("SELECT * FROM %s ;",Cons.MEM_TBL);
            Cursor cursor=super.getDatabase().rawQuery(sql, null);
            Member member=null;
            if(cursor.moveToFirst()){
                do{
                    member=new Member();
                    member.setSeq(cursor.getString(cursor.getColumnIndex(Cons.SEQ)));
                    member.setName(cursor.getString(cursor.getColumnIndex(Cons.NAME)));
                    member.setEmail(cursor.getString(cursor.getColumnIndex(Cons.EMAIL)));
                    member.setPass(cursor.getString(cursor.getColumnIndex(Cons.PASS)));
                    member.setAddr(cursor.getString(cursor.getColumnIndex(Cons.ADDR)));
                    member.setPhone(cursor.getString(cursor.getColumnIndex(Cons.PHONE)));
                    member.setProfileImage(cursor.getString(cursor.getColumnIndex(Cons.PROFILE_IMG)));
                    list.add(member);
                }while(cursor.moveToNext());
            }
            return list;
        }
    }
    private abstract class DeleteQuery extends Index.QueryFactory{
        SQLiteOpenHelper helper;
        public DeleteQuery(Context context) {
            super(context);
            helper=new Index.SqLiteHelper(context);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getWritableDatabase();
        }
    }
    private class DeleteMember extends DeleteQuery{

        public DeleteMember(Context context) {
            super(context);
        }
        public void execute(String seq){
            super.getDatabase().execSQL(String.format("DELETE FROM %s WHERE %s='%s';",Cons.MEM_TBL, Cons.SEQ, seq));
        }
    }

    class MemberAdaptor extends BaseAdapter{
        ArrayList<Member> list;
        LayoutInflater inflater;
        public MemberAdaptor(Context context, ArrayList<Member>list) {
            this.list=list;
            this.inflater=LayoutInflater.from(context);
        }
        private int[] photos={
                R.drawable.cupcake,
                R.drawable.eclair,
                R.drawable.donut,
                R.drawable.gingerbread,
                R.drawable.lollipop,
                R.drawable.froyo

                /*R.drawable.donut,
                R.drawable.eclair,
                R.drawable.gingerbread,
                R.drawable.honeycomb,
                R.drawable.froyo*/
        };
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }






        
        @Override
        public View getView(int i, View v, ViewGroup g) {
            ViewHolder viewHolder;
            if(v==null){
                v=inflater.inflate(R.layout.member_adapter,null);
                viewHolder=new ViewHolder();
                viewHolder.imageView= (ImageView) v.findViewById(R.id.imageView);
                viewHolder.name= (TextView) v.findViewById(R.id.name);
                viewHolder.phone= (TextView) v.findViewById(R.id.phone);
                v.setTag(viewHolder);
            }else{
                viewHolder= (ViewHolder) v.getTag();
            }



            Log.d("리스트 사이즈 ",String.valueOf(this.getCount()));
            Log.d("배열 사이즈 ",String.valueOf(photos.length));
            viewHolder.imageView.setImageResource(photos[i]);
            viewHolder.name.setText(list.get(i).getName());
            viewHolder.phone.setText(list.get(i).getPhone());
            return v;
        }
    }
    static class ViewHolder{
        ImageView imageView;
        TextView name;
        TextView phone;

    }
}
