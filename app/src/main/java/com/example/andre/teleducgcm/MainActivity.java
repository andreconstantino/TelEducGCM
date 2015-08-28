package com.example.andre.teleducgcm;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String EXTRA_MESSAGE = "message";
    public static final String GROUP_ID = "groupid";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String TAG = "GCMRelated";
    Map<String,AtomicInteger> materias = new HashMap<>();
    private static final String LOGS = "logs";
    String mensagemsub="";


    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.register);
        Button adicionarmateria = (Button)findViewById(R.id.addmateria);
        final EditText x = (EditText) findViewById(R.id.editText);


        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            regid = getRegistrationId(getApplicationContext());
            if(!regid.isEmpty()){
                button.setEnabled(false);
            }else{
                button.setEnabled(true);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Check device for Play Services APK.
                if (checkPlayServices()) {
                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    regid = getRegistrationId(getApplicationContext());

                    if (regid.isEmpty()) {
                        button.setEnabled(false);
                        new RegisterApp(getApplicationContext(), gcm, getAppVersion(getApplicationContext())).execute();
                    }else{
                        Toast.makeText(getApplicationContext(), "Device already Registered", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i(TAG, "No valid Google Play Services APK found.");
                }
            }
        });
        adicionarmateria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOGS, "###########################################################");
                String y = x.getText().toString();


                if(materias.containsKey(y)){
                    materias.get(y).incrementAndGet();
                    Log.i(LOGS, y);
                    Log.i(LOGS, "Ja possui");
                    Log.i(LOGS, "Contem: " + materias.get(y));

                } else {
                    materias.put(y,new AtomicInteger(1));
                    Log.i(LOGS, "Foi criado um novo chamado : "+ y);
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
                    mensagemsub = materias.size() + " materias mandaram novas mensagens";
                }

                Log.i(LOGS, "Mensagem sub:  "+mensagemsub);
                Log.i(LOGS, "###########################################################");
                Log.i(LOGS, "-----------------------------------------------------------");
            }

        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registro não encontrado.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(getApplicationContext());
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}
