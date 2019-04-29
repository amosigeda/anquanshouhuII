package vip.inteltech.gat.fix;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import android.text.TextUtils;

import org.xutils.cache.DiskCacheEntity;
import org.xutils.common.util.IOUtil;
import org.xutils.http.RequestParams;
import org.xutils.http.loader.Loader;
import org.xutils.http.request.UriRequest;

/**
 * Created by Steven Hua on 2016/12/15.
 */

public class ByteArrayObjectLoader extends Loader<byte[]> {
    private String charset = "UTF-8";
    private byte[] result = null;


    @Override
    public Loader<byte[]> newInstance() {
        return new ByteArrayObjectLoader();
    }

    @Override
    public void setParams(final RequestParams params) {
    }

    @Override
    public byte[] load(InputStream in) throws Throwable {
        result = IOUtil.readBytes(in);
        return result;
    }

    @Override
    public byte[] load(UriRequest request) throws Throwable {
        request.sendRequest();
        return this.load(request.getInputStream());
    }

    @Override
    public byte[] loadFromCache(DiskCacheEntity cacheEntity) throws Throwable {
        if (cacheEntity != null) {
            String text = cacheEntity.getTextContent();
            if (!TextUtils.isEmpty(text)) {
                return text.getBytes();
            }
        }

        return null;
    }

    @Override
    public void save2Cache(UriRequest request) {
        try {
            saveStringCache(request, new String(result, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
