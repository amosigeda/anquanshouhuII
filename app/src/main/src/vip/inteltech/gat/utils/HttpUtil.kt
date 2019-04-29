package vip.inteltech.gat.utils

import com.alibaba.fastjson.JSONObject
import org.xutils.http.RequestParams
import org.xutils.x
import vip.inteltech.gat.fix.HttpManagerImplOK
import vip.inteltech.gat.inter.HttpCallback

/**
 * By HH
 */
object HttpUtil {
    var TAG = HttpUtil::class.java.name

    fun <T> post(url: String, json: JSONObject, handler: HttpCallback<T>) {

    }

    fun <T> post(url: String, params: Map<String, Any>?, handler: HttpCallback<T>) {
    }

    operator fun <T> get(url: String, params: Map<String, String>?, handler: HttpCallback<T>) {
        val pms = createParams(url)
        params?.forEach { entry -> pms.addQueryStringParameter(entry.key, entry.value) }
        x.http().get(pms, handler)
    }

    fun postSync(url: String, json: JSONObject): JSONObject? {
        val res = postSync(url, json.toJSONString(), JSONObject::class.java)
        return if (res == null) null else res
    }

    fun <T> postSync(url: String, data: String, clazz: Class<T>): T? {
        val pms = createParams(url)
        pms.bodyContent = data
        return x.http().postSync(pms, clazz)
    }

    private fun createParams(url: String): RequestParams {
        val params = RequestParams(url)
        params.isCancelFast = true
        params.isAutoResume = true
        params.charset = "utf-8"
        params.maxRetryCount = 3
        return params
    }

    fun postSyncWithGZip(url: String, data: ByteArray?): ByteArray? {
        val http = x.http()
        if (http is HttpManagerImplOK) {
            return http.postSyncWithGZip(url, data)
        } else {
            throw Exception("Not Impl!")
        }
    }
}