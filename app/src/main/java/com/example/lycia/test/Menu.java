package com.example.lycia.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lycia.test.persistance.ReadWriteData;

import java.io.IOException;

public class Menu extends AppCompatActivity {

    private Button replay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        loadConfig();
    }

    @Override
    protected void onResume() {
        super.onResume();

        replay = (Button) findViewById(R.id.replay);
        if(MainActivity.loadPlay)
            replay.setVisibility(View.VISIBLE);
        else
            replay.setVisibility(View.INVISIBLE);
    }

    public void play(View view) {
        MainActivity.loadPlay = false;
        startActivity(new Intent(this, MainActivity.class));
    }

    public void apropos(View view) {
        startActivity(new Intent(this, Apropos.class));
    }

    public void scors(View view) {
        startActivity(new Intent(this, ScorsList.class));
    }

    public void replay(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void level(View view) {

        String levels[] = {"Level 1", "Level 2", "Level 3"};

        //AlertDialog pour afficher une alert au cas ou le delais du jeux est terminée
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //attribuer un titre a cette alert dialog fenetre
        dialog.setTitle("Level");
        dialog.setSingleChoiceItems(levels, (MainActivity.level - 1), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.level = i + 1;
            }
        });
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ReadWriteData.openOutputStream("config", Menu.this);
                ReadWriteData.writeToFile(MainActivity.level + "\n");
                ReadWriteData.writeToFile(MainActivity.loadPlay + "\n");
                ReadWriteData.writeToFile(MainActivity.scorsValue + "\n");
                ReadWriteData.writeToFile(MainActivity.counterValue + "\n");
                ReadWriteData.closeOutputStream();
            }
        });
        dialog.setCancelable(false);//desactiver la possibilité de fermer la fenetre de dialog
        dialog.create();//Creer le dialog
        dialog.show();//afficher le dialog
    }

    private void loadConfig() {
        ReadWriteData.openInputStream("config", this);
        ReadWriteData.readFromFile();
        if (ReadWriteData.getBufferedReader() != null) {
            try {
                MainActivity.level = Integer.parseInt(ReadWriteData.getBufferedReader().readLine());
                MainActivity.loadPlay = Boolean.parseBoolean(ReadWriteData.getBufferedReader().readLine());
                MainActivity.scorsValue = Integer.parseInt(ReadWriteData.getBufferedReader().readLine());
                MainActivity.counterValue = Integer.parseInt(ReadWriteData.getBufferedReader().readLine());
                MainActivity.playAudio = Boolean.parseBoolean(ReadWriteData.getBufferedReader().readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ReadWriteData.closeinputStream();
        }
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
                if(item.isChecked()) {
                    item.setChecked(false);
                    Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG).show();
                    MainActivity.playAudio = false;
                } else {
                    item.setChecked(true);
                    MainActivity.playAudio = true;
                    Toast.makeText(this, item.getTitle(), Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.close :
                System.exit(0);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
