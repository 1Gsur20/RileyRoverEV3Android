package com.example.rileyroverev3android;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.widget.*;

import lejos.hardware.Bluetooth;
import lejos.utility.Delay;

public class MainActivity extends AppCompatActivity {

    //Composants de la vue de cette actvité
    private SeekBar vitesse;
    private TextView vitesseindicateur;
    private ImageButton tournerDroite;
    private ImageButton tournerGauche;
    private ImageButton klaxon;
    private ImageButton avancer;
    private ImageButton reculer;
    private Button arret;
    private Button frein;
    private Button auto_manuel;
    private Button urgent;
    private TextView monitor;

    private static final int
    //Etats de la machine
        AVANCE=1,
        RECUL=2,
        ARRET=3,
        TOURNE_DROITE=16,
        TOURNE_GAUCHE=17,
        KLAXONNE=18,
        MODE_AUTOMATIQUE=19,
        MODE_MANUEL=20,
    //Signal de l'arrêt de l'application
        ARRET_APPLI=15,
    //Différentes vitesses possibles pour la voiture
        VITESSE0=4,
        VITESSE1=5,
        VITESSE2=6,
        VITESSE3=7,
        VITESSE4=8,
        VITESSE5=9,
        VITESSE6=10,
        VITESSE7=11,
        VITESSE8=12,
        VITESSE9=13,
        VITESSE10=14;

    private BluetoothConnection bluetooth;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetooth = new BluetoothConnection();

        // To notify user for permission to enable bt, if needed
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("Voulez-vous activer le Bluetooth ?");
        builder.setPositiveButton("Accepter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                bluetooth.setBtPermission(true);
                bluetooth.reply();
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                bluetooth.setBtPermission(false);
                bluetooth.reply();
            }
        });

        // Create the AlertDialog
        AlertDialog btPermissionAlert = builder.create();

        Context context = getApplicationContext();
        //CharSequence text1 = getString(R.string.bt_enabled);
        CharSequence text1 = getString(R.string.bt_disabled);
        CharSequence text2 = getString(R.string.bt_failed);


        Toast btDisabledToast = Toast.makeText(context, text1, Toast.LENGTH_LONG);
        Toast btFailedToast = Toast.makeText(context, text2, Toast.LENGTH_LONG);

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.PREFERENCESEV3), Context.MODE_PRIVATE);

        if(!bluetooth.initBT()){
            // User did not enable Bluetooth
            btDisabledToast.show();
            Intent intent = new Intent(MainActivity.this, AccueilConnectionActivity.class);
            startActivity(intent);
        }

        Toast.makeText(context,getString(R.string.CLEEV3), Toast.LENGTH_LONG).show();

        String adresseMac = getIntent().getExtras().getString("adressemac");

        if(!bluetooth.connectToEV3(adresseMac)){
            //Cannot connect to given mac address, return to connect activity
            btFailedToast.show();
            Intent intent = new Intent(MainActivity.this, AccueilConnectionActivity.class);
            startActivity(intent);
        }


        this.vitesse = (SeekBar) findViewById(R.id.vitesse);
        this.vitesseindicateur = (TextView) findViewById(R.id.vitesseindicateur);
        this.tournerDroite = (ImageButton) findViewById(R.id.tourneDroite);
        this.tournerGauche = (ImageButton) findViewById(R.id.tourneGauche);
        this.klaxon = (ImageButton) findViewById(R.id.klaxon);
        this.arret = (Button) findViewById(R.id.arret);
        this.frein = (Button) findViewById(R.id.frein);
        this.avancer = (ImageButton) findViewById(R.id.avancer);
        this.reculer = (ImageButton) findViewById(R.id.reculer);
        this.auto_manuel = (Button) findViewById(R.id.auto_manual);
        this.urgent = (Button) findViewById(R.id.urgent);
        this.monitor = (TextView) findViewById(R.id.Monitor);

        this.avancer.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View myView, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        try{
                            bluetooth.writeMessage((byte) AVANCE);
                            return true;
                        }catch(InterruptedException e){
                            e.printStackTrace();
                            return false;
                        }
                    case MotionEvent.ACTION_UP:
                        try{
                            bluetooth.writeMessage((byte) ARRET);
                            return true;
                        }catch(InterruptedException e){
                            e.printStackTrace();
                            return false;
                        }
                    default:
                        break;
                }
                return false;
            }
        });

        /*this.monitor.(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                monitor.setText(Long.toString(battery));
                return true;
            }
        });*/

        this.reculer.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View myView, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        try{
                            bluetooth.writeMessage((byte) RECUL);
                            return true;
                        }catch(InterruptedException e){
                            e.printStackTrace();
                            return false;
                        }
                    case MotionEvent.ACTION_UP:
                        try{
                            bluetooth.writeMessage((byte) ARRET);
                            return true;
                        }catch(InterruptedException e){
                            e.printStackTrace();
                            return false;
                        }
                    default:
                        break;
                }
                return false;
            }
        });

        this.arret.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View myView, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        try{
                            bluetooth.writeMessage((byte) ARRET_APPLI);
                            Intent intent = new Intent(MainActivity.this, AccueilConnectionActivity.class);
                            startActivity(intent);
                            return true;
                        }catch(InterruptedException e) {
                            e.printStackTrace();
                            return false;
                        }
                    default:
                        break;
                }
                return false;
            }
        });

        this.frein.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View myView, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        try{
                            bluetooth.writeMessage((byte) ARRET);
                            return true;
                        }catch(InterruptedException e) {
                            e.printStackTrace();
                            return false;
                        }
                    default:
                        break;
                }
                return false;
            }
        });


        this.auto_manuel.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View myView, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        if(auto_manuel.getText().equals("Automatique")){
                            auto_manuel.setText("Manuel");
                            try{

                                bluetooth.writeMessage((byte) MODE_AUTOMATIQUE);

                                disableButtons();

                                return true;
                            }catch(InterruptedException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }else if (auto_manuel.getText().equals("Manuel")){
                            auto_manuel.setText("Automatique");
                            try{
                                bluetooth.writeMessage((byte) MODE_MANUEL);

                                enableButtons();

                                return true;
                            }catch(InterruptedException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                }
                return false;
            }
        });

        this.vitesse.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                //vitesseindicateur.setText(progressValue);
                switch(progressValue){
                    case 0:
                        try{
                            bluetooth.writeMessage((byte) VITESSE0);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try{
                            bluetooth.writeMessage((byte) VITESSE1);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try{
                            bluetooth.writeMessage((byte) VITESSE2);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        try{
                            bluetooth.writeMessage((byte) VITESSE3);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        try{
                            bluetooth.writeMessage((byte) VITESSE4);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        break;
                    case 5:
                        try{
                            bluetooth.writeMessage((byte) VITESSE5);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        break;
                    case 6:
                        try{
                            bluetooth.writeMessage((byte) VITESSE6);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        break;
                    case 7:
                        try{
                            bluetooth.writeMessage((byte) VITESSE7);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        break;
                    case 8:
                        try{
                            bluetooth.writeMessage((byte) VITESSE8);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        break;
                    case 9:
                        try{
                            bluetooth.writeMessage((byte) VITESSE9);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        break;
                    case 10:
                        try{
                            bluetooth.writeMessage((byte) VITESSE10);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        this.tournerDroite.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View myView, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        try{
                            bluetooth.writeMessage((byte) TOURNE_DROITE);
                            return true;
                        }catch(InterruptedException e) {
                            e.printStackTrace();
                            return false;
                        }
                    case MotionEvent.ACTION_UP:
                        try{
                            bluetooth.writeMessage((byte) ARRET);
                            return true;
                        }catch(InterruptedException e){
                            e.printStackTrace();
                            return false;
                        }
                    default:
                        break;
                }
                return false;
            }
        });
        this.tournerGauche.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View myView, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        try{
                            bluetooth.writeMessage((byte) TOURNE_GAUCHE);
                            return true;
                        }catch(InterruptedException e) {
                            e.printStackTrace();
                            return false;
                        }
                    case MotionEvent.ACTION_UP:
                        try{
                            bluetooth.writeMessage((byte) ARRET);
                            return true;
                        }catch(InterruptedException e){
                            e.printStackTrace();
                            return false;
                        }
                    default:
                        break;
                }
                return false;
            }
        });

        this.klaxon.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View myView, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        try{
                            bluetooth.writeMessage((byte) KLAXONNE);
                            return true;
                        }catch(InterruptedException e) {
                            e.printStackTrace();
                            return false;
                        }
                    case MotionEvent.ACTION_UP:
                        try{
                            bluetooth.writeMessage((byte) ARRET);
                            return true;
                        }catch(InterruptedException e){
                            e.printStackTrace();
                            return false;
                        }
                    default:
                        break;
                }
                return false;
            }
        });

        this.urgent.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View myView, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        if(auto_manuel.getVisibility() == View.GONE && auto_manuel.getText().equals("Automatique")){
                            try{

                                bluetooth.writeMessage((byte) MODE_AUTOMATIQUE);

                                auto_manuel.setVisibility(View.VISIBLE);
                                arret.setVisibility(View.VISIBLE);

                                return true;
                            }catch(InterruptedException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }else if (auto_manuel.getVisibility() == View.GONE && auto_manuel.getText().equals("Manuel")){

                            try{

                                bluetooth.writeMessage((byte) MODE_MANUEL);

                                auto_manuel.setVisibility(View.VISIBLE);
                                arret.setVisibility(View.VISIBLE);

                                enableButtons();

                                return true;
                            }catch(InterruptedException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }else{

                            try{

                                bluetooth.writeMessage((byte) ARRET);

                                auto_manuel.setVisibility(View.GONE);
                                arret.setVisibility(View.GONE);

                                disableButtons();

                                return true;
                            }catch(InterruptedException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                }
                return false;
            }
        });

        // Création d'un thread pour écouter la brique EV3.
        this.runOnUiThread(new Thread(){
            public void run() {
                //while(true){
                    try {
                        Thread.sleep(10000);
                        monitor.setText("Batterie: "+ Integer.toString(((bluetooth.readMessage() - 6500) * 100) / (8400 - 6500)) + "%");
                    } catch(InterruptedException e){
                        e.printStackTrace();
                    }
                //}
            }
        });

    }

    public void disableButtons(){
        this.avancer.setClickable(false);
        this.avancer.setVisibility(View.GONE);
        this.reculer.setClickable(false);
        this.reculer.setVisibility(View.GONE);
        this.tournerDroite.setClickable(false);
        this.tournerDroite.setVisibility(View.GONE);
        this.tournerGauche.setClickable(false);
        this.tournerGauche.setVisibility(View.GONE);
        this.klaxon.setClickable(false);
        this.klaxon.setVisibility(View.GONE);
        this.vitesse.setClickable(false);
        this.vitesse.setVisibility(View.GONE);
        this.vitesseindicateur.setVisibility(View.GONE);
    }

    public void enableButtons(){
        this.avancer.setClickable(true);
        this.avancer.setVisibility(View.VISIBLE);
        this.reculer.setClickable(true);
        this.reculer.setVisibility(View.VISIBLE);
        this.tournerDroite.setClickable(true);
        this.tournerDroite.setVisibility(View.VISIBLE);
        this.tournerGauche.setClickable(true);
        this.tournerGauche.setVisibility(View.VISIBLE);
        this.klaxon.setClickable(true);
        this.klaxon.setVisibility(View.VISIBLE);
        this.vitesse.setClickable(true);
        this.vitesse.setVisibility(View.VISIBLE);
        this.vitesseindicateur.setVisibility(View.VISIBLE);
    }
}
