package fu.alfie.com.sqliteexdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class BasicSQLiteActivity extends AppCompatActivity {

    private int version = 1; //資料庫 版本
    private TextView textView;
    private EditText databaseEx,tableEx,nameEx,valueEx;
    private String database,table,name,value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_sqlite);
        databaseEx = (EditText)findViewById(R.id.editText);
        tableEx = (EditText)findViewById(R.id.editText2);
        nameEx = (EditText)findViewById(R.id.editText3);
        valueEx = (EditText)findViewById(R.id.editText4);
        textView = (TextView) findViewById(R.id.textView3);
    }


    public void selectTable(View view) {
        textView.setText(""); //先將前次查詢結果清除
        database = String.valueOf(databaseEx.getText());
        table = String.valueOf(tableEx.getText());
        SQLhelper helper = new SQLhelper(this, database, null, version);
        SQLiteDatabase db = helper.getReadableDatabase();
        helper.onCreate(db); //重新建表格
        String query = "select * from "+table;
        Cursor cursor = db.rawQuery(query, null);  //Cursor像是一個指標,它是每個Row的集合,從表格選取所有的欄位
        cursor.moveToFirst();  //將指標移動到第一行
        if (cursor.getCount() == 0) {  //判斷是否有資料
            textView.setText("Data Not Found");
        }else{  //將資料列印出來
            do {
                textView.append("name = " + cursor.getString(cursor.getColumnIndex("name")) +
                             ", value = " + cursor.getInt(cursor.getColumnIndex("value"))+"\n");
            } while (cursor.moveToNext());//判斷是否有下一筆資料
            db.close();
        }
    }

    public void insertTable(View view) {
        database = String.valueOf(databaseEx.getText());
        table = String.valueOf(tableEx.getText());
        name = String.valueOf(nameEx.getText());
        value = String.valueOf(valueEx.getText());
        SQLhelper helper = new SQLhelper(this, database, null, version);
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.onCreate(db);  //重新建表格
        ContentValues contentValues = new ContentValues();  //ContentValues和HashMap相似不同的是ContentValues只能存基本型態在對應的欄位加入資料
        contentValues.put("name", name);
        contentValues.put("value", value);
        db.insert(table, null, contentValues); //在對應的table加入資料
        db.close();
    }

    public void updateTable(View view) {
        database = String.valueOf(databaseEx.getText());
        table = String.valueOf(tableEx.getText());
        name = String.valueOf(nameEx.getText());
        value = String.valueOf(valueEx.getText());
        SQLhelper helper = new SQLhelper(this, database, null, version);
        SQLiteDatabase db = helper.getWritableDatabase();
        helper.onCreate(db);  //重新建表格
        ContentValues contentValues = new ContentValues(); //new ContentValues物件存放update的資料
        contentValues.put("name", name);
        contentValues.put("value", value);
        db.update(table, contentValues, "name = ?", new String[]{name}); // Table_name,修改的資料,條件判斷,條件判斷的值
        db.close();
    }

    public void deleteTable(View view) {
        database = String.valueOf(databaseEx.getText());
        table = String.valueOf(tableEx.getText());
        name = String.valueOf(nameEx.getText());
        value = String.valueOf(valueEx.getText());
        SQLhelper helper = new SQLhelper(this, database, null, version);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(table, "name = ? and value = ?", new String[]{name,value}); //Table_name,刪除的條件,刪除的值
        db.close(); //關閉資料庫
    }

    public class SQLhelper extends SQLiteOpenHelper {


        public SQLhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {  //創造新表格
            String create = "create table IF NOT EXISTS " + table + " ( " +
                    "_id INTEGER PRIVATE KEY ," +
                    "name TEXT ," +
                    "value INTEGER )";
            db.execSQL(create);  //執行創建語法
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exitst " + table); //版本不同時，刪除舊表格
            onCreate(db); //創建新表格
        }
    }
}
