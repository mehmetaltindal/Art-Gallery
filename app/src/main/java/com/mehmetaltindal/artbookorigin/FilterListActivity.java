package com.mehmetaltindal.artbookorigin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class FilterListActivity extends AppCompatActivity {
    ListView listView2;
    ArrayAdapter arrayAdapter;
    ArrayList<String> nameArray;
    ArrayList<Integer> idArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_list);

        listView2 = findViewById(R.id.listView2);

        nameArray = new ArrayList<String>();
        idArray = new ArrayList<Integer>();

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_2,nameArray);
        listView2.setAdapter(arrayAdapter);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent2 = new Intent(FilterListActivity.this,FilterActivity.class);
                intent2.putExtra("artId",idArray.get(position));
                intent2.putExtra("info","old");
                startActivity(intent2);


            }
        });

        getData2();

    }




    public void getData2(){
        System.out.println("merhaba2");
        try {
            SQLiteDatabase database2 = this.openOrCreateDatabase("Arts2",MODE_PRIVATE,null);

            Cursor cursor2 = database2.rawQuery("SELECT * FROM arts2", null);
            int nameIx = cursor2.getColumnIndex("artname");
            int idIx = cursor2.getColumnIndex("id");

            while (cursor2.moveToNext()) {
                nameArray.add(cursor2.getString(nameIx));
                idArray.add(cursor2.getInt(idIx));

            }

            arrayAdapter.notifyDataSetChanged();

            cursor2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflater
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        menuInflater.inflate(R.menu.filter_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_art_item) {
            Intent intent2 = new Intent(FilterListActivity.this,Main2Activity.class);
            intent2.putExtra("info","new");
            startActivity(intent2);
        }
        if (item.getItemId() == R.id.add_filter_item)
        {
            Intent intent2 = new Intent(FilterListActivity.this,FilterActivity.class);
            intent2.putExtra("info","new");
            startActivity(intent2);
        }

        return super.onOptionsItemSelected(item);
    }
}