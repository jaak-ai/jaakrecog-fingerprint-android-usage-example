package com.jaakit.examplenativefingerlib.credentials;

import org.json.JSONException;

import java.io.IOException;

public interface ValidateCredentials {
    String validateCredentials(String apiToken) throws JSONException, IOException;

}
