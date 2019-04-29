package vip.inteltech.gat.fix

import okhttp3.Dns
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.regex.Pattern

/**
 * Created by Steven Hua on 2018/7/18.
 */
class DNSIPv4 : Dns {
    companion object {
        val PATTERN = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")!!
    }

    @Throws(UnknownHostException::class)
    override fun lookup(hostname: String?): List<InetAddress> {
        if (hostname == null) throw UnknownHostException("hostname == null")
        try {
            val adds = InetAddress.getAllByName(hostname)
            return adds.filter { x -> PATTERN.matcher(x.hostAddress).matches() }
        } catch (e: NullPointerException) {
            val unknownHostException = UnknownHostException("Broken system behaviour for dns lookup of $hostname")
            unknownHostException.initCause(e)
            throw unknownHostException
        }
    }
}
