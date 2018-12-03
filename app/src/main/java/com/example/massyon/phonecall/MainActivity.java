package com.example.massyon.phonecall;





import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.ImageButton;
import android.widget.Toast;
import android.app.AlertDialog.Builder;

import android.app.AlertDialog;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RequestCall= 1;

    private TelephonyManager mTelephonyManager;
    private MyPhoneCallListener mListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_fire_missiles)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();*/
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if (isTelephonyEnabled()) {
            Log.d(TAG, getString(R.string.telephony_enabled));

            checkForPhonePermission();

            mListener = new MyPhoneCallListener();
            mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
        } else {
            Toast.makeText(this,
                    R.string.telephony_not_enabled, Toast.LENGTH_LONG).show();
            Log.d(TAG, getString(R.string.telephony_not_enabled));

            disableCallButton();
        }
    }


    private boolean isTelephonyEnabled() {
        if (mTelephonyManager != null) {
            if (mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                return true;
            }
        }
        return false;
    }


    private void checkForPhonePermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, getString(R.string.permission_not_granted));

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    RequestCall);
        } else {

            enableCallButton();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case RequestCall: {
                if (permissions[0].equalsIgnoreCase(Manifest.permission.CALL_PHONE)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableCallButton();
                } else {

                    Log.d(TAG, getString(R.string.failure_permission));
                    Toast.makeText(this, getString(R.string.failure_permission),
                            Toast.LENGTH_LONG).show();

                    disableCallButton();
                }
            }
        }
    }


    public void callNumber1(View view) {
        TextView text = (TextView) findViewById(R.id.text_main1);


        String phoneNumber = String.format("tel: %s", text.getText().toString());


        Log.d(TAG, getString(R.string.dial_number) + phoneNumber);
        Toast.makeText(this, getString(R.string.dial_number) + phoneNumber,
                Toast.LENGTH_LONG).show();


        Intent callIntent = new Intent(Intent.ACTION_CALL);

        callIntent.setData(Uri.parse(phoneNumber));


        if (callIntent.resolveActivity(getPackageManager()) != null) {
            checkForPhonePermission();
            startActivity(callIntent);
        } else {
            Log.e(TAG, "Can't resolve app for ACTION_CALL Intent.");
        }
    }
    public void callNumber2(View view) {

        TextView text = (TextView) findViewById(R.id.text_main2);


        String phoneNumber = String.format("tel: %s", text.getText().toString());

        Log.d(TAG, getString(R.string.dial_number) + phoneNumber);
        Toast.makeText(this, getString(R.string.dial_number) + phoneNumber,
                Toast.LENGTH_LONG).show();


        Intent callIntent = new Intent(Intent.ACTION_CALL);

        callIntent.setData(Uri.parse(phoneNumber));


        if (callIntent.resolveActivity(getPackageManager()) != null) {
            checkForPhonePermission();
            startActivity(callIntent);

        } else {
            Log.e(TAG, "Can't resolve app for ACTION_CALL Intent.");
        }
    }



    private class MyPhoneCallListener extends PhoneStateListener {
        private boolean returningFromOffHook = false;

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            String message = getString(R.string.phone_status);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:

                    message = message +
                            getString(R.string.ringing) + incomingNumber;
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:

                    message = message + getString(R.string.offhook);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    returningFromOffHook = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:

                    message = message + getString(R.string.idle);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    if (returningFromOffHook) {

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                            Log.i(TAG, getString(R.string.restarting_app));

                            Intent intent = getPackageManager()
                                    .getLaunchIntentForPackage(getPackageName());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                    break;
                default:
                    message = message + "Phone off";
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    Log.i(TAG, message);
                    break;
            }
        }
    }


    private void disableCallButton() {
        Toast.makeText(this, R.string.phone_disabled, Toast.LENGTH_LONG).show();
        ImageButton callButton = (ImageButton) findViewById(R.id.phone_icon);
        ImageButton callButton1 = (ImageButton) findViewById(R.id.phone_icon1);
        callButton.setVisibility(View.INVISIBLE);
        if (isTelephonyEnabled()) {
            Button retryButton = (Button) findViewById(R.id.button_retry);
            retryButton.setVisibility(View.VISIBLE);
        }
    }


    private void enableCallButton() {
        ImageButton callButton = (ImageButton) findViewById(R.id.phone_icon);
        ImageButton callButton1 = (ImageButton) findViewById(R.id.phone_icon1);
        callButton.setVisibility(View.VISIBLE);
    }



    public void retryApp(View view) {
        enableCallButton();
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isTelephonyEnabled()) {
            mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_NONE);
        }
    }
}