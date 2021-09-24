package com.jaakit.examplenativefingerlib.credentials;

import android.content.Context;

import com.auth0.android.jwt.JWT;
import com.jaakit.examplenativefingerlib.R;
import com.jaakit.fingeracequisition.network.CallApiImpl;
import com.jaakit.fingeracequisition.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class ValidateCredentialsImpl extends Thread implements ValidateCredentials {

    private final CallApiImpl callApi = new CallApiImpl();
    private final Context context;

    public ValidateCredentialsImpl(Context context) {
        this.context = context;
    }

    @Override
    public String validateCredentials(String apiKey) throws JSONException, IOException {

        String url = context.getText(R.string.url_api_session).toString() ;

        String response = callApi.callPost(url, new JSONObject().put("apiKey", apiKey));

        CredentialsResponse credentialsResponse = new CredentialsResponse(new JSONObject(response));

       // Utils utils=new Utils(context);
        //utils.deleteTokenFromCache();
        JWT jwt = new JWT(credentialsResponse.getJWT());

        Date expiresAt = jwt.getExpiresAt();
        Date currentTime = Calendar.getInstance().getTime();

        if (currentTime.after(expiresAt)) {
            return "false";
        }


        return credentialsResponse.getJWT();

    }


}
