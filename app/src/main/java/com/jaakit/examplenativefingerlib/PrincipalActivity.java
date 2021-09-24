package com.jaakit.examplenativefingerlib;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaakit.examplenativefingerlib.credentials.ValidateCredentials;
import com.jaakit.examplenativefingerlib.credentials.ValidateCredentialsImpl;
import com.jaakit.examplenativefingerlib.utils.Utils;


import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private ValidateCredentials validateCredentials;
    private String token,jwtoken;
    private Context context;
    private EditText edtToken;
    private TextView txtResul;
    private String root;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        edtToken=findViewById(R.id.edtToken);
        txtResul=findViewById(R.id.txtREsult);
        root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();



        Button button=findViewById(R.id.btnTokentIngress);
        button.setOnClickListener(view -> {
            token=edtToken.getText().toString();
            new TokenAsyncTask().execute(token);
        });
        checkPermissions();
        }

    @SuppressLint("StaticFieldLeak")
    private class TokenAsyncTask extends AsyncTask<String, Void, String> {


        protected String doInBackground(String... strings) {
            try {

                validateCredentials=new ValidateCredentialsImpl(context);
                jwtoken=validateCredentials.validateCredentials(strings[0]);
                Log.d("Getting token:",jwtoken);

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return jwtoken;
        }



        protected void onPostExecute(String result) {

            try {
                Log.d("INTENT  CODE XXXXXXXX",""+result);

                Intent myIntent = new Intent(context,
                        Class.forName("com.jaakit.fingeracequisition.FingerActivity"));
                myIntent.putExtra("jwtoken",result);
                startActivityForResult(myIntent,101);

            }catch ( ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         try {
             Log.d("RESULT  CODE resultCode",""+resultCode);
             Log.d("RESULT  CODE requestCode",""+requestCode);


             if ((requestCode == 101) && (resultCode == RESULT_OK)) {

                 String fingerLeftWsq=   data.getStringExtra("fingerLeftWsq");
                 String fingerRigthWsq=   data.getStringExtra("fingerRigthWsq");
                 Log.d("fingerLeftWsq data",":"+fingerLeftWsq);
                 Log.d("fingerRigthWsq data",":"+fingerRigthWsq);
                 if(fingerLeftWsq.isEmpty() || fingerRigthWsq.isEmpty()){
                     txtResul.setText(

                             " finger wsq  rigth finger path :"+fingerLeftWsq.length()
                                     +"\n"+
                                     " finger rigth wsq path :"+fingerRigthWsq.length());

                 }else{

                     byte[] bytesWsqLeft = Base64.decode(fingerLeftWsq, 1);
                     InputStream inputStreamLeftFinger = new ByteArrayInputStream(bytesWsqLeft);
                     File dirLeft = new File(root + "/wsq_left_finger.wsq");
                     Utils.copyInputStreamToFile(inputStreamLeftFinger, dirLeft);

                     byte[] bytesWsqRigth = Base64.decode(fingerRigthWsq, 1);
                     InputStream inputStreamRigthFinger = new ByteArrayInputStream(bytesWsqRigth);
                     File dirRigth = new File(root + "/wsq_rigth_finger.wsq");
                     Utils.copyInputStreamToFile(inputStreamRigthFinger, dirRigth);
                     txtResul.setText(
                             " finger left wsq path :"+dirLeft.getAbsolutePath()+"\n"+
                              " finger rigth wsq path :"+dirRigth.getAbsolutePath());

                 }




             }else{
                 String fingerLeftError=   data.getStringExtra("fingerLeftError");
                 String fingerRigthError=   data.getStringExtra("fingerRigthError");
                 Log.d("fingerLeftError DATA",""+fingerLeftError);
                 Log.d("fingerRigthError DATA",""+fingerRigthError);
                 txtResul.setText(" wsq error message for left finger :"+fingerLeftError+"\n"+
                         " wsq error message for left right:"+fingerRigthError);


            }
        } catch (Exception e) {
            e.printStackTrace();
             txtResul.setText("No wsq received");

        }
    }

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
             final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                break;
        }
    }

}