# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-ignorewarnings
-dontwarn android.**
-keep class android.** {*;}
-keep class com.android.** {*;}

-dontwarn com.amap.api.**
-dontwarn com.a.a.**
-dontwarn com.autonavi.**
-dontwarn com.aps.**
-dontwarn com.iflytek.**


-keep class com.amap.api.**  {*;}
-keep class com.autonavi.**  {*;}
-keep class com.a.a.**  {*;}
-keep class com.aps.** {*;}

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}


-keepattributes *Annotation*

-dontwarn com.google.**

-dontwarn android.support.v4.**
-keep class android.support.v4.** {*;}
-keep interface android.support.annotation.** {*;}
-keep interface android.support.v4.app.** {*;}
-keep public class * extends android.support.v4.**
-keep class * extends java.lang.annotation.Annotation {*;}

#-keep public class * extends android.app.Fragment

-dontwarn org.apache.http.**
-keep class org.apache.http.** {*;}
-keep interface org.apache.http.** {*;}
-keep class * extends org.apache.http.**
-keep class * implements org.apache.http.**
-keepclassmembers class * {
    @org.apache.http.** <methods>;
}

-dontwarn android.**
-keep class android.** {*;}
-keep class com.android.** {*;}

-dontwarn com.amap.api.**
-dontwarn com.a.a.**
-dontwarn com.autonavi.**
-dontwarn com.aps.**
-dontwarn com.iflytek.**
-dontwarn org.codehaus.**
-dontwarn okio.**


-keep class com.amap.api.**  {*;}
-keep class com.autonavi.**  {*;}
-keep class com.a.a.**  {*;}
-keep class com.aps.** {*;}

#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

-keep class pl.droidsonroids.gif.** {*;}
-keep class com.google.** {*;}

-keep class org.simple.** { *; }
-keep interface org.simple.** { *; }
-keepclassmembers class * {
    @org.simple.eventbus.** <methods>;
}

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}

-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }

################### region for xUtils
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,*Annotation*,Synthetic,EnclosingMethod

-keep public class org.xutils.** {
    public protected *;
}
-keep public interface org.xutils.** {
    public protected *;
}
-keepclassmembers class * extends org.xutils.** {
    public protected *;
}
-keepclassmembers class * extends org.xutils.http.RequestParams {*;}
-keepclassmembers class * {
   void *(android.view.View);
   *** *Click(...);
   *** *Event(...);
   @org.xutils.** <methods>;
}
-keep class org.xutils.http.cookie.** {*;}
#################### end region

-keep class com.czt.mp3recorder.util.**  {*;}
-keep interface com.github.mikephil.charting.** {*;}
-keep interface com.inuker.bluetooth.library.** {*;}
-keep class com.github.mikephil.charting.** {*;}
-keep class com.inuker.bluetooth.library.** {*;}
-keep class jp.co.toshiba.semicon.hcsdp.brighton.collectorsleepapp.** {*;}

-assumenosideeffects class android.util.Log {public static *** d(...);}
-assumenosideeffects class android.util.Log {public static *** i(...);}

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

-keepattributes Exceptions,InnerClasses,Signature,*Annotation*
-keepnames class * implements java.io.Serializable
-keep public class com.androidquery.**{*;}
-keep public class com.tencent.analytics.sdk.** {*;}

-dontwarn oauth.signpost.**
-dontwarn com.orhanobut.**
-dontwarn com.tencent.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.readystatesoftware.**

#jar
-keep class com.ytb.**{ *;}
-keep class android.support.v4.app.NotificationCompat**{public *;}
-dontwarn dalvik.system.VMStack
-keep class com.tencent.**{ *;}
-keep class a.**{*;}
-keep class b.**{*;}
-keep class kunxiang01.bus.**{*;}
-keep class yanzhenjie.nohttp.**{*;}
-keep class re.na.ack.sb.**{*;}

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}