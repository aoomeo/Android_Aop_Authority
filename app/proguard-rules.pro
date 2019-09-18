# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\sdk\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

 #忽略警告，避免打包时某些警告出现
-ignorewarnings

#指定压缩级别
-optimizationpasses 5

#包名不混合大小写
-dontusemixedcaseclassnames


#不跳过非公共的库的类
-dontskipnonpubliclibraryclasses

#关闭预校验
-dontpreverify

#混淆时记录日志
-verbose
-printmapping proguardMapping.txt

#混淆时采用的算法
-optimizations !code/simplification/cast,!field/*,!class/merging/*

#保护注解
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
#保留行号
-keepattributes SourceFile,LineNumberTable

#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员
-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt
#混淆前后的映射
-printmapping mapping.txt

-keep class androidx.fragment.app.Fragment {*;}
-keep class android.app.Fragment {*;}
-keep class android.support.v4.app.Fragment {*;}

-keep class android.support.v4.app.ActivityCompat {*;}
-keep class androidx.core.app.ActivityCompat{*;}
-keepclassmembers class * {
    @com.aoomeo.android.permissionaop.RequirePermission <methods>;
}


