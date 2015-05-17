package com.example.andre.teleducgcm;


import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GcmIntentService extends IntentService {
    private static final String LOGS = "logs";
    int NOTIFICATION_ID = 1;
    int contador = 1;
    String mensagemsub = "";
    //Map<String,AtomicInteger> materias = new HashMap<>();

    String mensagem = "";

    private static final String TAG = "GcmIntentService";
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        Context ctx = getApplicationContext();
        SharedPreferences prefs = ctx.getSharedPreferences("teleduc", MODE_PRIVATE);
        Editor editor = prefs.edit();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        /*
        if(!prefs.contains("numeroMaterias")) {
            editor.putInt("numeroMaterias", 0);
        }
        */

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.i(TAG, "Error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.i(TAG, "Deleted: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                String notice = extras.getString("Notice");
                String group = extras.getString("group");

                //Log.i(TAG,"Valores: " + materias.values().toString());
                //Log.i(TAG,"Conteudo: " + materias.entrySet().toString());

                if(prefs.contains(group)){
                    //materias.get(group).incrementAndGet();
                    int numeroMensagens = prefs.getInt(group, 0);
                    numeroMensagens++;
                    editor.putInt(group, numeroMensagens);
                    editor.commit();
                    Log.i(LOGS, group);
                    Log.i(LOGS, "Essa matéria já está cadastrado!");
                    Log.i(LOGS, "Contem: " + numeroMensagens +" mensagens");
                } else {
                    editor.putInt(group, 1);
                    Log.i(LOGS, "Foi criado uma nova matéria: "+ group);
                    int numeroMaterias = prefs.getInt("numeroMaterias", 0);
                    numeroMaterias++;
                    editor.putInt("numeroMaterias", numeroMaterias);
                    editor.commit();
                }

                int nMensagensMateria = prefs.getInt(group, 0);
                Log.i(LOGS, "Quantidade de mensagens:  " + nMensagensMateria);

                if(prefs.getInt("numeroMaterias", 0) == 1) {
                    if (nMensagensMateria == 1) {
                        mensagemsub = group + " mandou " + nMensagensMateria + " nova mensagem";
                    } else {
                        mensagemsub = group + " mandou " + nMensagensMateria + " novas mensagens";

                    }
                } else{
                    mensagemsub = prefs.getInt("numeroMaterias", 0) + " matérias mandaram novas mensagens";
                }
                //salvarVariaveis(group);

                //Computa o total de mensagens enviadas
                int numeroTotalMensagens = prefs.getInt("numeroTotalMensagens", 0);
                numeroTotalMensagens++;
                editor.putInt("numeroTotalMensagens", numeroTotalMensagens);
                editor.commit();

                sendNotification(notice, group, mensagemsub);

                Log.i(TAG, "Received: " + extras.toString());
            }
        }

            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    /*public String salvarVariaveis(String group){
        if(materias.containsKey(group)){
            materias.get(group).incrementAndGet();
            Log.i(LOGS, group);
            Log.i(LOGS, "Essa matéria já está cadastrado!");
            Log.i(LOGS, "Contem: " + this.materias.get(group)+" mensagens");
        } else {
            materias.put(group,new AtomicInteger(0));
            Log.i(LOGS, "Foi criado uma nova matéria: "+ group);
        }
        int x = Integer.parseInt(materias.get(group).toString());
        Log.i(LOGS, "Quantidade de mensagens:  " + materias.get(group));

        String qtdmat = ""+materias.get(group);

        if(materias.size()==1) {
            if (x == 1) {
                mensagemsub = group + " mandou " + materias.get(group) + " nova mensagem";
            } else {
                mensagemsub = group + " mandou " + materias.get(group) + " novas mensagens";

            }
        } else{
            mensagemsub = materias.size() + " matérias mandaram novas mensagens";
        }
        return mensagemsub;
    }
    */


    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
   /* public String enviar(String group){
        String y = group;

        Log.i(TAG,"Valores: " + materias.values().toString());
        Log.i(TAG,"Seila" + materias.entrySet().toString());



        if(materias.containsKey(y)){
            materias.get(y).incrementAndGet();
            Log.i(LOGS, y);
            Log.i(LOGS, "Essa matéria já está cadastrado!");
            Log.i(LOGS, "Contem: " + materias.get(y)+" mensagens");
        } else {
            materias.put(y,new AtomicInteger(0));
            Log.i(LOGS, "Foi criado uma nova matéria: "+ y);
        }
        int x = Integer.parseInt(materias.get(y).toString());
        Log.i(LOGS, "Quantidade de mensagens:  "+ materias.get(y));



        if(materias.size()==1) {
            if (x == 1) {
                mensagemsub = y + " mandou " + materias.get(y) + " nova mensagem";
            } else {
                mensagemsub = y + " mandou " + materias.get(y) + " novas mensagens";

            }
        } else{
            mensagemsub = materias.size() + " matérias mandaram novas mensagens";
        }
        return mensagemsub;
    }*/

    private void sendNotification(String notice, String group, String mensagemsub) {
        String x = mensagemsub;

        Context ctx = getApplicationContext();
        SharedPreferences prefs = ctx.getSharedPreferences("teleduc", MODE_PRIVATE);

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, JanelaMensagem.class);
        notificationIntent.putExtra("notice",notice);
        notificationIntent.putExtra("group",group);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        int numeroTotalMensagens = prefs.getInt("numeroTotalMensagens", 0);
        if(numeroTotalMensagens == 1){
            mensagem = " nova mensagem";
        } else{
            mensagem = " novas mensagens";

        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(numeroTotalMensagens + mensagem)
                        .setSmallIcon(R.drawable.icone_teleduc)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notice))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setVibrate(new long[] { 0, 100, 200, 300 })
                        .setSubText(x)
                        .setContentText(notice)
                        .setAutoCancel(true)
                        .setLights(Color.YELLOW,100,100);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        contador++;
    }
}