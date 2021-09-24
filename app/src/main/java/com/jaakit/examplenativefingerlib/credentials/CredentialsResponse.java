package com.jaakit.examplenativefingerlib.credentials;


import org.json.JSONException;
import org.json.JSONObject;

public class CredentialsResponse {

    private String JWT;

    public CredentialsResponse(JSONObject jsonObject) throws JSONException {

        this.JWT = jsonObject.getString("jwt");

    }

    public String getJWT() {
        return JWT;
    }
}
