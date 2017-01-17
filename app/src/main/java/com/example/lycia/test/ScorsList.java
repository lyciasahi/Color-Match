package com.example.lycia.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.lycia.test.persistance.ReadWriteData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScorsList extends AppCompatActivity {

    private ListView scorsList;
    private List<Integer> scors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scors_list);

        scorsList = (ListView) findViewById(R.id.scorsList);
    }

    @Override
    protected void onResume() {
        super.onResume();

        scors = new ArrayList<>();

        ReadWriteData.openInputStream("scorsList", this);
        ReadWriteData.readFromFile();
        if(ReadWriteData.getBufferedReader() != null) {
            try {
                String scor;
                while ((scor = ReadWriteData.getBufferedReader().readLine()) != null)
                    scors.add(Integer.parseInt(scor));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ReadWriteData.closeinputStream();
        }
        Collections.sort(scors);
        Collections.reverse(scors);

        Integer[] Tscors = new Integer[scors.size()];
        Tscors = scors.toArray(Tscors);

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, Tscors);
        scorsList.setAdapter(adapter);

        scorsList.refreshDrawableState();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.son:
                if(item.isChecked())
                    MainActivity.playAudio = true;
                else
                    MainActivity.playAudio = false;
                return true;
            case R.id.close :
                System.exit(0);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
