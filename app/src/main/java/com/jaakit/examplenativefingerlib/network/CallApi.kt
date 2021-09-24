package com.jaakit.fingeracequisition.network

import org.json.JSONObject
import java.io.IOException

interface CallApi {
    @Throws(IOException::class)
    fun callPost(url: String?, data: JSONObject?): String?

}