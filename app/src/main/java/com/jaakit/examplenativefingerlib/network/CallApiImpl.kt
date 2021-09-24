package com.jaakit.fingeracequisition.network

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.*

class CallApiImpl {
    @Throws(IOException::class)
    fun callPost(url: String?, data: JSONObject): String? {
        val client = OkHttpClient()
        val mediaType:MediaType= MediaType.parse("application/json; charset=utf-8")!!

        val body: RequestBody =
            RequestBody.create(mediaType, data.toString())
        val request: Request = Request.Builder().url(url).post(body).build()
        client.newCall(request).execute().use { response ->
            return Objects.requireNonNull(
                response.body()
            )?.string()
        }
    }

}