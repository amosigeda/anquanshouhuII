package vip.inteltech.gat.fix;

import java.io.InputStream;

import android.text.TextUtils;

import org.xutils.cache.DiskCacheEntity;
import org.xutils.common.util.IOUtil;
import org.xutils.http.RequestParams;
import org.xutils.http.loader.Loader;
import org.xutils.http.request.UriRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by Steven Hua on 2016/12/15.
 */

public class FastJSONObjectLoader extends Loader<JSONObject> {
    private String charset = "UTF-8";
    private String resultStr = null;


    @Override
    public Loader<JSONObject> newInstance() {
        return new FastJSONObjectLoader();
    }

    @Override
    public void setParams(final RequestParams params) {
        if (params != null) {
            String charset = params.getCharset();
            if (!TextUtils.isEmpty(charset)) {
                this.charset = charset;
            }
        }
    }

    @Override
    public JSONObject load(InputStream in) throws Throwable {
        resultStr = IOUtil.readStr(in, charset);
        return JSON.parseObject(resultStr);
    }

    @Override
    public JSONObject load(UriRequest request) throws Throwable {
        request.sendRequest();
        return this.load(request.getInputStream());
    }

    @Override
    public JSONObject loadFromCache(DiskCacheEntity cacheEntity) throws Throwable {
        if (cacheEntity != null) {
            String text = cacheEntity.getTextContent();
            if (!TextUtils.isEmpty(text)) {
                return JSON.parseObject(text);
            }
        }

        return null;
    }

    @Override
    public void save2Cache(UriRequest request) {
        saveStringCache(request, resultStr);
    }
}
