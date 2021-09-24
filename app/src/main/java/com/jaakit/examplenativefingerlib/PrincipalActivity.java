package com.jaakit.examplenativefingerlib;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
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

import com.jaakit.examplenativefingerlib.credentials.ValidateCredentials;
import com.jaakit.examplenativefingerlib.credentials.ValidateCredentialsImpl;
import com.jaakit.examplenativefingerlib.utils.Utils;


import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PrincipalActivity extends AppCompatActivity {

    private ValidateCredentials validateCredentials;
    private String token,jwtoken;
    private Context context;
    private EditText edtToken;
    private ImageView imgPhoto;
    private TextView txtResul;
    private String root;
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        edtToken=findViewById(R.id.edtToken);
        imgPhoto=findViewById(R.id.imgPhoto);
        txtResul=findViewById(R.id.txtREsult);
        root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();



        Button button=findViewById(R.id.btnTokentIngress);
        button.setOnClickListener(view -> {
            token=edtToken.getText().toString();
            new TokenAsyncTask().execute(token);
        });
        }

    private class TokenAsyncTask extends AsyncTask<String, Void, String> {


        protected String doInBackground(String... strings) {
            try {

                validateCredentials=new ValidateCredentialsImpl(context);
                jwtoken=validateCredentials.validateCredentials(strings[0]);
                Log.e("Getting token:",jwtoken);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return jwtoken;
        }



        protected void onPostExecute(String result) {

            try {
                Log.e("INTENT  CODE XXXXXXXX",""+result);

                Intent myIntent = new Intent(context,
                        Class.forName("com.jaakit.fingeracequisition.FingerActivity"));
                myIntent.putExtra("jwtoken",result);
                startActivityForResult(myIntent,101);

            }catch ( ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         try {
             Log.e("RESULT  CODE resultCode",""+resultCode);
             Log.e("RESULT  CODE requestCode",""+requestCode);

             if ((requestCode == 101) && (resultCode == RESULT_OK)) {
                 Log.e("EXTRACTING DATA","############");

                 String fingerLeftWsq=   data.getStringExtra("fingerLeftWsq");
                 String fingerRigthWsq=   data.getStringExtra("fingerRigthWsq");
                 Log.e("fingerLeftWsq DATA",":"+fingerLeftWsq);
                 Log.e("fingerRigthWsq DATA",":"+fingerRigthWsq);
                 if(fingerLeftWsq.length()==0){
                     fingerLeftWsq=" ";
                 }
                 if(fingerRigthWsq.length()==0){
                     fingerRigthWsq=" ";
                 }
                 byte[] bytesWsqLeft = Base64.decode(fingerLeftWsq, 1);
                 InputStream inputStreamLeftFinger = new ByteArrayInputStream(bytesWsqLeft);
                 File dirLeft = new File(root + "/wsq_left_finger.wsq");
                //
                 byte[] bytesWsqRigth = Base64.decode(fingerRigthWsq, 1);
                 InputStream inputStreamRigthFinger = new ByteArrayInputStream(bytesWsqRigth);
                 File dirRigth = new File(root + "/wsq_rigth_finger.wsq");

                 txtResul.setText("wsq left :"+dirLeft.getAbsoluteFile()
                         +"\n"+
                         "wsq rigth string size :"+dirRigth.getAbsoluteFile());

                 Utils.copyInputStreamToFile(inputStreamLeftFinger, dirLeft);
                 Utils.copyInputStreamToFile(inputStreamRigthFinger, dirRigth);


             }else{
                 String fingerLeftError=   data.getStringExtra("fingerLeftError");
                 String fingerRigthError=   data.getStringExtra("fingerRigthError");
                 Log.e("fingerLeftError DATA",""+fingerLeftError);
                 Log.e("fingerRigthError DATA",""+fingerRigthError);

                 Error error=(Error) data.getSerializableExtra("error");
                 txtResul.setText(" wsq error message :"+fingerLeftError+"/n");


            }
        } catch (Exception e) {
            e.printStackTrace();
             txtResul.setText("No wsq received");

        }
    }

    public Bitmap getBitmapFromUri(Uri uri) throws FileNotFoundException, IOException{
        InputStream input = this.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig= Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
         BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inDither = true; //optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//
        input = this.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

}