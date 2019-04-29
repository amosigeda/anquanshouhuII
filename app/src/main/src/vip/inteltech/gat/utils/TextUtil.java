package vip.inteltech.gat.utils;

import android.text.TextUtils;

public class TextUtil {
	public static String MaxTextLengthChange(int max, String str){
		if(TextUtils.isEmpty(str)){
			return str;
		}else{
			if(str.length() >= max){
				return str.substring(0, max) + "...";
			}else{
				return str;
			}
		}
	}
}
