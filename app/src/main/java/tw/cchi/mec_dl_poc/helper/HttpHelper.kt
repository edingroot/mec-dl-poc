package tw.cchi.mec_dl_poc.helper

import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import tw.cchi.mec_dl_poc.config.Constants
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class HttpHelper {
    companion object {
        val JSON = MediaType.parse("application/json; charset=utf-8")
    }

    private val httpClient = OkHttpClient()

    suspend fun initUdpStream(dlUdpPort: Int): Pair<Int, Int>? {
        return suspendCoroutine { cont ->
            val url = "%s://%s:%d/upd_streaming/init"
                .format(Constants.MEC_SERVER_PROTOCOL, Constants.MEC_SERVER_IP, Constants.MEC_SERVER_PORT)

            val reqJson = JSONObject()
            reqJson.put("dl_udp_port", dlUdpPort)

            httpPostJson(url, reqJson, object: Callback {
                override fun onResponse(call: Call?, response: Response) {
                    val responseData = response.body()?.string()
                    try {
                        val json = JSONObject(responseData)
                        val responsePair = Pair(
                            json.getInt("ul_udp_port"),
                            json.getInt("ul_udp_timeout")
                        )
                        cont.resume(responsePair)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call?, e: IOException?) {
                    cont.resume(null)
                }
            })
        }
    }

    private fun httpGet(url: String, callback: Callback): Call {
        val request = Request.Builder()
            .url(url)
            .build()

        val call = httpClient.newCall(request)
        call.enqueue(callback)
        return call
    }

    private fun httpPost(url: String, parameters: HashMap<String, String>, callback: Callback): Call {
        val builder = FormBody.Builder()
        val it = parameters.entries.iterator()
        while (it.hasNext()) {
            val pair = it.next() as Map.Entry<*, *>
            builder.add(pair.key.toString(), pair.value.toString())
        }

        val formBody = builder.build()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()


        val call = httpClient.newCall(request)
        call.enqueue(callback)
        return call
    }

    private fun httpPostJson(url: String, json: JSONObject, callback: Callback): Call {
        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create(JSON, json.toString()))
            .build()

        val call = httpClient.newCall(request)
        call.enqueue(callback)
        return call
    }
}