package com.example.lycia.test;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lycia.test.persistance.ReadWriteData;
import com.example.lycia.test.view.ColorMatchGridViewAdapter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private GridView gridview;//tableau de couleurs
    private ProgressBar counterProgresBar;//La bar de calcule du temps
    private TextView scorsView;//affichage de scors
    private MediaPlayer media;// activer son
    private MediaPlayer media1;

    private static Context context ; //

    private static Thread tempsThread;//permet de calculer le temps et d'afficher les alert

    private static boolean stopCondition;//utiliser pour indiquer au tempsTread que l'activity et terminer
    public static boolean loadPlay = false;

    public static int counterValue;//counteur de temps
    public static int scorsValue;//variable ou en stocke le scors
    public static int level = 1;
    public static boolean playAudio = true;

    public void animation(final View view) {
        Random rand = new Random();

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.ABSOLUTE, 25, Animation.ABSOLUTE, 25);

        TranslateAnimation trAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, rand.nextInt()%150,
                Animation.RELATIVE_TO_SELF, getWindowManager().getDefaultDisplay().getHeight());
        TranslateAnimation trAnimation1 = new TranslateAnimation(Animation.RELATIVE_TO_SELF, rand.nextInt()%40,
                Animation.RELATIVE_TO_SELF, rand.nextInt()%40);

        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new AccelerateInterpolator());
        animSet.addAnimation(rotateAnimation);
        rotateAnimation.setDuration(700);
        animSet.addAnimation(trAnimation);
        rotateAnimation.setDuration(20);
        animSet.addAnimation(trAnimation1);
        trAnimation.setStartOffset(20);
        trAnimation.setDuration(500);
        animSet.setFillAfter(false);
        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((ImageView)view).setImageResource(0);
                ((ImageView)view).setId(0);
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animSet);

    }

    public void showAlert(String title) {/* Fonction permet de creer et d'afficher une fenetre d'alert */

        //AlertDialog pour afficher une alert au cas ou le delais du jeux est terminé
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        //attribuer un titre a cette alert dialog fenetre
        dialog.setTitle(title);
        dialog.setMessage("Your score is : " + scorsValue);//Afficher un message
        //ajouter un button Retry pour donnée au joueur la possibilité de rejouer
        dialog.setNegativeButton("Replay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int index) {
                    recreate();//returner a la methode create() du Life Cycle de cette activity.
                }
            });
        //ajouter un button Exite pour quitter le jeux
        dialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int index) {
                    finish();//terminer l'activity
                }
            });
        dialog.setCancelable(false);//desactiver la possibilité de fermer la fenetre de dialog
        dialog.create();//Creer le dialog
        dialog.show();//afficher le dialog
    }

    //Fonction permet de supprimer les Item dans les positions "positions[]" et ont les mêmes coleurs
    private void removeItems(long[] ids, int[] positions) {
        //Tableau pour stocker le premier ensemble des Items ont la même coleur
        Map<Integer, Long> sames1 = new Hashtable<>();
        //Pour stocker la deuxieme ensemble des Items ont la même coleur
        Map<Integer, Long> sames2 = new Hashtable<>();

        //boucler sur le tableau des Items pour detecter les sont de même couleur
        for(int i = 0; i < 4; i++) {
            if(ids[i] != 0) {//si l'id est différent de 0
                for (int j = i + 1; j < 4; j++) { //boucler sur le reste du tableau pour comparer l'actuel Item
                                                  // avec le reste du tableau
                    if(ids[i] == ids[j]) { // si l'Item i == l'Item j
                        if(sames1.size() == 0) { // si le premier ensemble des Items ont la même coleur est vide
                            // alors en stocke les deux Items
                            sames1.put(positions[j], ids[j]);
                            sames1.put(positions[i], ids[i]);
                        } else if(sames1.values().contains(ids[j])) {// sinon et si le premier ensemble des Items ont la même coleur
                                                                    // n'est pas vide
                            //alors on ajoute le nouvau Items au ensemble
                            sames1.put(positions[j], ids[j]);
                        } else if(sames2.size() == 0){ // si non et si le deuxieme ensemble des Items ont la même coleur est vide
                            // alors on ajoute les deux autre Items de même couleur dans la le premier ensemble des Items ont la
                            // même coleur
                            sames2.put(positions[i], ids[i]);
                            sames2.put(positions[j], ids[j]);
                        }
                        //puis on mise ajour l'id de l'item j pour ne pas traiter de nouveau dans les pochaines bocle
                        ids[j] = 0;
                    }
                }
                //même pour l'item i
                ids[i] = 0;
            }
        }

        //Mise a jour de scors
        if(sames1.size() == 2 && sames2.size() == 0) {
            //si sames1 contient 2 elements et sames2 est vide alors on incrémente le scors par 20
            scorsValue += 20;
        } else if(sames1.size() == 3) {
            //si sames1 contient 3 elements alors on incrémente le scors par 60
            scorsValue += 60;
        } else if(sames2.size() == 2 || sames1.size() == 4){
            //si sames1 contient 4 elements ou bien sames1 contient 2 elements et sames2 contient 2 elements alors
            // on incrémente le scors par 120
            scorsValue += 120;
        } else {
            //sinon alors on n'a pas de Item de même couleurs on degrade le counteurValue par 5
            counterValue -= 5*level;
            if(playAudio) {
                media1.start();
            }
        }

        //Creation des iterateurs permet de parcourir les ensembles same1 et same2
        final Iterator<Integer> it1 = sames1.keySet().iterator();
        final Iterator<Integer> it2 = sames2.keySet().iterator();

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        while(it1.hasNext()) {//s'il existe des Items alors
                            //prendre l'image couleur de cette Item
                            ImageView v = (ImageView) gridview.getChildAt(it1.next());
                            //applique l'animation sur cette image
                            animation(v);
                            //si aussi l'ensemble same2 n'est pas vide alors on fait la même chose que la premiere
                            if(it2.hasNext()) {
                                v = (ImageView) gridview.getChildAt(it2.next());
                                animation(v);
                            }
                        }
                        if(playAudio) {
                            media.start();
                        }
                        loadPlay = true;
                        if(!check()) {
                            loadPlay = false;
                            stopCondition = true;
                            saveScor();
                            showAlert("Game Over");
                        }
                    }
                });
            }
        }).run();

        saveConfig();
        ReadWriteData.openOutputStream("gridStatus", context);
        for(int i = 0; i < 100; i++) {
            ReadWriteData.writeToFile((gridview.getChildAt(i)).getId() + "\n");
        }
        ReadWriteData.closeOutputStream();
    }


    private void clickEffect(int position) {
        long items_ids[] = new long[4]; //Stocker les IDs des 4 items autoure la case selectionnée
        int items_positions[] = new int[4]; //Stocker les positions des 4 items autoure la case selectionnée
        int pos = position;

        if((gridview.getChildAt((position))).getId() == 0){
            while (gridview.getChildAt((pos)).getId() == 0 && (pos + 1)%10 != 0) {
                pos++;
            }
            items_ids[0] = gridview.getChildAt((pos)).getId();
            items_positions[0] = pos;

            pos = position;

            while (gridview.getChildAt((pos)).getId() == 0 && pos%10 != 0) {
                pos--;
            }
            items_ids[1] = gridview.getChildAt((pos)).getId();
            items_positions[1] = pos;

            pos = position;

            while (pos < 100 && gridview.getChildAt((pos)).getId() == 0) {
                pos += 10;
            }
            items_ids[2] = gridview.getChildAt(((pos >= 100) ? (pos - 10) : pos)).getId();
            items_positions[2] = pos >= 100 ? pos - 10 : pos;

            pos = position;

            while (pos >= 0 && gridview.getChildAt((pos)).getId() == 0) {
                pos -= 10;
            }
            items_ids[3] = gridview.getChildAt((pos < 0 ? pos + 10 : pos)).getId();
            items_positions[3] = pos < 0 ? pos + 10 : pos;

            removeItems(items_ids, items_positions);
        } else {
            counterValue -= 5*level;
            if(playAudio) {
                media1.start();
            }
        }
    }

    public boolean check() {
        for(int position = 0; position < 100; position++) {
            if((gridview.getChildAt((position))).getId() == 0) {
                List<Integer> l = new ArrayList<>();
                int pos = position + 1;
                while(pos%10 != 0 && (gridview.getChildAt((pos))).getId() == 0)
                    pos++;
                if(pos%10 != 0 && (gridview.getChildAt((pos))).getId() != 0)
                    l.add((gridview.getChildAt((pos))).getId());

                pos = position - 1;
                while((pos+1)%10 != 0 && (gridview.getChildAt((pos))).getId() == 0)
                    pos--;
                if((pos + 1)%10 != 0 && (gridview.getChildAt((pos))).getId() != 0)
                    l.add((gridview.getChildAt((pos))).getId());

                pos = position + 10;
                while(pos < 100 && (gridview.getChildAt((pos))).getId() == 0)
                    pos += 10;
                if(pos < 100 && (gridview.getChildAt((pos))).getId() != 0)
                    l.add((gridview.getChildAt((pos))).getId());

                pos = position - 10;
                while(pos >= 0 && (gridview.getChildAt((pos))).getId() == 0)
                    pos -= 10;
                if(pos >= 0 && (gridview.getChildAt((pos))).getId() != 0)
                    l.add((gridview.getChildAt((pos))).getId());

                for(int i = 0; i < l.size(); i++) {
                    for(int j = i+1; j < l.size(); j++) {
                        if(l.get(i).equals(l.get(j))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        gridview = (GridView) findViewById(R.id.gridview);
        scorsView = (TextView) findViewById(R.id.scors);
        counterProgresBar = (ProgressBar) findViewById(R.id.progress_timer);

        media = MediaPlayer.create(context, R.raw.beep9);
        media1 = MediaPlayer.create(context, R.raw.beep7);

        tempsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (counterValue > 0 && !stopCondition) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            scorsView.setText(scorsValue + "");

                            counterValue -= level;

                            counterProgresBar.setProgress(counterValue);
                            counterProgresBar.setProgressDrawable(getResources().getDrawable(R.drawable.cell_shape));
                        }
                    });
                    try {
                        tempsThread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(counterValue <= 0) {
                    loadPlay = false;
                    saveScor();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scorsView.setText(scorsValue + "");

                            counterProgresBar.setProgress(counterValue);
                            counterProgresBar.setProgressDrawable(getResources().getDrawable(R.drawable.cell_shape));

                            showAlert("Time out");
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!loadPlay) {
            scorsValue = 0;
            counterValue = 100;
        }

        stopCondition = false;

        counterProgresBar.setProgress(counterValue);
        counterProgresBar.setProgressDrawable(getResources().getDrawable(R.drawable.cell_shape));

        gridview.setAdapter(new ColorMatchGridViewAdapter(context));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                clickEffect(position);
            }
        });

        if(!tempsThread.isAlive())
            tempsThread.start();
    }

    protected void saveConfig() {
        ReadWriteData.openOutputStream("config", context);
        ReadWriteData.writeToFile(level + "\n");
        ReadWriteData.writeToFile(loadPlay + "\n");
        ReadWriteData.writeToFile(scorsValue + "\n");
        ReadWriteData.writeToFile(counterValue + "\n");
        ReadWriteData.writeToFile(playAudio + "\n");
        ReadWriteData.closeOutputStream();
    }

    protected void saveScor() {
        ReadWriteData.addToFile(scorsValue + "\n", context, "scorsList");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCondition = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveConfig();
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