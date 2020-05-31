package com.example.rileyroverev3android;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;


import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.util.Log;
import android.widget.Toast;

/*
 Permet la connexion bluetooth avec la brique EV3
 */
public class BluetoothConnection {

    //UUID correspondant au bluetooth de l'appareil android
    private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    //Adaptateur bluetooth de l'appareil
    BluetoothAdapter localAdapter;
    //Socket de l'EV3
    BluetoothSocket socket_ev3_1,socket_nxt2;
    boolean success=false;
    private boolean btPermission=false;
    private boolean alertReplied=false;

    public void reply(){
        this.alertReplied = true;
    }
    public void setBtPermission(boolean btPermission) {
        this.btPermission = btPermission;
    }

    /*
    Initialise la connexion bluetooth de l'appareil
    Renvoi true si la connexion bluetooth est effective
     */
    public boolean initBT(){
        localAdapter=BluetoothAdapter.getDefaultAdapter();
        return localAdapter.isEnabled();
    }

    /*
    Permet la connexion avec la brique EV3
    @param : macAdd, l'adresse mac du boitier
    Renvoi true si la connexion est effective
     */
    public  boolean connectToEV3(String macAdd){
        BluetoothDevice ev3_1 = localAdapter.getRemoteDevice(macAdd);
        try {
            socket_ev3_1 = ev3_1.createRfcommSocketToServiceRecord(UUID
                    .fromString(SPP_UUID));
            socket_ev3_1.connect();
            success = true;
        } catch (IOException e) {
            Log.d("Bluetooth","Err: Device not found or cannot connect " + macAdd);
            success=false;
        }
        return success;
    }

    /*
    Permet d'écrire un message et de l'envoyer en bluetooth
     */
    public void writeMessage(byte msg) throws InterruptedException{

        BluetoothSocket connexionSocket = socket_ev3_1;

        if(connexionSocket!=null){
            try {

                OutputStreamWriter out=new OutputStreamWriter(connexionSocket.getOutputStream());
                out.write(msg);
                out.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            //Erreur
        }
    }

    public int readMessage() throws InterruptedException{

        BluetoothSocket connexionSocket = socket_ev3_1;
        int msg = 0;
        char[] test = new char[300];
        String Encode = "re";

        if(connexionSocket!=null){
            try {
                DataInputStream in=new DataInputStream(connexionSocket.getInputStream());
                msg = in.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return msg;
    }

}