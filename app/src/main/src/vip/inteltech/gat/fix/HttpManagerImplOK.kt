package vip.inteltech.gat.fix

import android.util.Log
import okhttp3.*
import org.apache.commons.lang3.StringUtils
import org.xutils.HttpManager
import org.xutils.common.Callback
import org.xutils.common.util.KeyValue
import org.xutils.common.util.ParameterizedTypeUtil
import org.xutils.http.HttpMethod
import org.xutils.http.RequestParams
import org.xutils.http.body.BodyItemWrapper
import org.xutils.http.loader.LoaderFactory
import vip.inteltech.gat.comm.Constants
import vip.inteltech.gat.inter.HttpCallbackOK
import vip.inteltech.gat.utils.Contents
import vip.inteltech.gat.utils.Utils
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

/**
 * Created by Steven Hua on 2018/7/19.
 */
object HttpManagerImplOK : HttpManager {
    private val MEDIA_TYPE_GZIP = MediaType.parse("application/x-gzip; charset=utf-8")
    private val MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8")
    private val MEDIA_TYPE_MULTI = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8")
    private val MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8")
    private val MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream")
    private val interceptor = OKHttpRetryInterceptor.Builder().executionCount(3).retryInterval(1000).build()
    private val CLIENT = OkHttpClient.Builder()
            .dns(DNSIPv4())
            .retryOnConnectionFailure(true)
            .addInterceptor(interceptor)
            .connectionPool(ConnectionPool())
            .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
            .followRedirects(true)
            .followSslRedirects(true)
//            .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getTrustManager())
//            .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
            .connectTimeout(Constants.NET_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.NET_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.NET_TIMEOUT, TimeUnit.SECONDS)
            .build()

    override fun <T : Any?> requestSync(method: HttpMethod, entity: RequestParams, resultType: Class<T>): T? {
        return requestSync(method, entity, DefaultSyncCallback(resultType))
    }

    override fun <T : Any?> requestSync(method: HttpMethod, entity: RequestParams, callback: Callback.TypedCallback<T>): T? {
        val pam: Request = prepareParams(entity, method)
        val response = CLIENT.newCall(pam).execute()
        if (response == null || !response.isSuccessful) {
            return null
        }
        val stream = response.body()?.byteStream() ?: return null
        val res = LoaderFactory.getLoader(callback.loadType, entity).load(stream)
        return res as T
    }

    override fun <T : Any?> getSync(entity: RequestParams, resultType: Class<T>): T? {
        return requestSync(HttpMethod.GET, entity, resultType)
    }

    override fun <T : Any?> postSync(entity: RequestParams, resultType: Class<T>): T? {
        return requestSync(HttpMethod.POST, entity, resultType)
    }

    override fun <T : Any?> get(entity: RequestParams, callback: Callback.CommonCallback<T>): Callback.Cancelable? {
        return request(HttpMethod.GET, entity, callback)
    }

    override fun <T : Any?> post(entity: RequestParams, callback: Callback.CommonCallback<T>): Callback.Cancelable? {
        return request(HttpMethod.POST, entity, callback)
    }

    override fun <T : Any?> request(method: HttpMethod, entity: RequestParams, callback: Callback.CommonCallback<T>): Callback.Cancelable? {
        val pam: Request = prepareParams(entity, method)
        CLIENT.newCall(pam).enqueue(object : HttpCallbackOK() {
            override fun onFailure(call: Call?, e: IOException?) {
                callback.onError(e, true)
            }

            override fun onResponse(call: Call?, response: Response?) {
                val stream = response?.body()?.byteStream()
                if (stream == null) {
                    onFailure(call, IOException("Result NULL"))
                    return
                }
                val loadType = ParameterizedTypeUtil.getParameterizedType(callback.javaClass, Callback.CommonCallback::class.java, 0)
                val res = LoaderFactory.getLoader(loadType, entity).load(stream)
                callback.onSuccess(res as T)
            }
        })
        return null
    }

    fun postSyncWithGZip(url: String, data: ByteArray?): ByteArray? {
        if (data == null) {
            return null
        }
        try {
            val bytes = Utils.GZip(data) ?: return null
            val body = RequestBody.create(MEDIA_TYPE_GZIP, bytes)
            val request = createBuilder(url, null).post(body).build()
            val response = CLIENT.newCall(request).execute()
            val res = if (response?.body() == null) null else response.body()!!.bytes()
            return Utils.unGZip(res)
        } catch (throwable: Throwable) {
            Log.e(HttpManagerImplOK::class.java.name, "Error！", throwable)
            return null
        }

    }

    private fun prepareParams(entity: RequestParams, method: HttpMethod): Request {
        val pms = StringUtils.join(entity.queryStringParams?.map { entry -> "${entry.key}=${entry.value}" }, "&")
        var uri = entity.uri
        lateinit var pam: Request
        if (method == HttpMethod.GET) {
            if (!uri.contains('?')) {
                uri = "$uri?"
            }
            if (!uri.endsWith('?')) {
                uri = "$uri&"
            }
            if (pms != null) {
                uri = "$uri$pms"
            }
            pam = createBuilder(uri, entity).get().build()
        } else {
            val mb = MultipartBody.Builder()
            entity.queryStringParams.forEach { x -> mb.addFormDataPart(x.key, x.valueStr) }
            entity.bodyParams.forEach { x -> mb.addFormDataPart(x.key, x.valueStr) }
            entity.fileParams.forEach { x ->
                val wrap = x.value as BodyItemWrapper
                mb.addFormDataPart(x.key, wrap.fileName, RequestBody.create(MediaType.parse(wrap.contentType), wrap.value as File))
            }
            pam = createBuilder(uri, entity).post(mb.build()).build()
        }
        return pam
    }

    private fun createBuilder(url: String, entity: RequestParams?): Request.Builder {
        val fullUrl = if (StringUtils.startsWithIgnoreCase(url, "HTTP://") || StringUtils.startsWithIgnoreCase(url, "HTTPS://")) url else Contents.Ip + url
        val builder = Request.Builder().url(fullUrl)
                .addHeader(Constants.USER_AGENT_KEY, Constants.USER_AGENT)
        if (entity != null) {
            val map = mutableMapOf<String, String>()
            entity.headers.forEach {
                val h = it as KeyValue
                map[h.key] = h.valueStr
            }
            builder.headers(Headers.of(map))
        }
        return builder
    }
}

private class DefaultSyncCallback<T>(private val resultType: Class<T>) : Callback.TypedCallback<T> {

    override fun getLoadType(): Type {
        return resultType
    }

    override fun onSuccess(result: T) {

    }

    override fun onError(ex: Throwable, isOnCallback: Boolean) {

    }

    override fun onCancelled(cex: Callback.CancelledException) {

    }

    override fun onFinished() {

    }
}