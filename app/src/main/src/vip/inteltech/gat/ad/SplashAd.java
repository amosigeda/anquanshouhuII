package vip.inteltech.gat.ad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SplashAd {
    public interface ResponseStrategy<T> {
        T parseJson(String json) throws JSONException;
    }

    public static class Strategy401 implements ResponseStrategy<AdBean401> {
        @Override
        public AdBean401 parseJson(String json) throws JSONException {
            JSONArray responseArray = new JSONArray(json);
            AdBean401 adBean401 = null;
            if (responseArray.length() > 0) {
                adBean401 = new AdBean401();


	            for (int index=0;index<responseArray.length();index++){
		            JSONObject imgJsonObj = responseArray.optJSONObject(index);
		            if (imgJsonObj!=null && imgJsonObj.optJSONObject("img")!=null){
			            JSONObject urlJsonObj = imgJsonObj.optJSONObject("img");
			            String url = urlJsonObj.optString("url");
			            adBean401.setImgUrl(url);
		            }
                }
            }
            return adBean401;
        }
    }

    public static class Strategy402 implements ResponseStrategy<AdBean402> {
        @Override
        public AdBean402 parseJson(String json) throws JSONException {
            JSONArray responseArray = new JSONArray(json);
            AdBean402 adBean402 = null;
            if (responseArray.length() > 0) {
                adBean402 = new AdBean402();
                List<String> listUrls = new ArrayList<>();

                for (int index=0;index<responseArray.length();index++){
	                JSONObject imgJsonObj_1 = responseArray.optJSONObject(index);
	                if(imgJsonObj_1!=null && imgJsonObj_1.optJSONObject("img")!=null){                                 //就算是text也可以忽略
		                JSONObject urlJsonObj_1 = imgJsonObj_1.optJSONObject("img");
		                String url_1 = urlJsonObj_1.optString("url");
		                listUrls.add(url_1);
	                }
                }
                adBean402.setImgUrls(listUrls);
            }
            return adBean402;
        }
    }

}
