package vip.inteltech.gat.fix

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import vip.inteltech.gat.comm.Constants
import vip.inteltech.gat.utils.AppContext
import java.io.IOException
import java.io.InterruptedIOException
import java.net.UnknownHostException

class OKHttpRetryInterceptor internal constructor(builder: Builder) : Interceptor {
    var executionCount: Int = 0//最大重试次数
    /**
     * retry间隔时间
     */
    val retryInterval: Long//重试的间隔

    init {
        this.executionCount = builder.executionCount
        this.retryInterval = builder.retryInterval
    }


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()
        var response = doRequest(chain, request)
        var retryNum = 0
        while ((response == null || !response.isSuccessful) && retryNum <= executionCount) {
            Log.d(OKHttpRetryInterceptor::class.java.name, "intercept Request is not successful - $retryNum")
            val nextInterval = retryInterval
            try {
                Log.d(OKHttpRetryInterceptor::class.java.name, "Wait for $nextInterval")
                Thread.sleep(nextInterval)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                throw InterruptedIOException()
            }

            retryNum++
            response = doRequest(chain, request)
        }
        return response
    }

    private fun doRequest(chain: Interceptor.Chain, request: Request): Response? {
        var response: Response? = null
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            Log.e(OKHttpRetryInterceptor::class.java.name, "Chain process error:", e)
            if (e is UnknownHostException) {
                AppContext.getEventBus().post(Constants.DEFAULT_OBJECT, Constants.EVENT_NETWORK_ERROR)
            }
        }

        return response
    }

    class Builder {
        var executionCount: Int = 0
        var retryInterval: Long = 0

        init {
            executionCount = 3
            retryInterval = 1000
        }

        fun executionCount(executionCount: Int): OKHttpRetryInterceptor.Builder {
            this.executionCount = executionCount
            return this
        }

        fun retryInterval(retryInterval: Long): OKHttpRetryInterceptor.Builder {
            this.retryInterval = retryInterval
            return this
        }

        fun build(): OKHttpRetryInterceptor {
            return OKHttpRetryInterceptor(this)
        }
    }

}
