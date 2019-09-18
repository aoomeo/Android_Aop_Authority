# Android_Aop_Authority v0.0.4
Android权限控制，通过Aop切面，减少代码侵入，权限申请简洁明了。
## 使用说明
该库使用范围：在Activity及Fragment中申明权限。（支持Androidx）
该库必须配合 [aspectjx](https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx) 使用。

## 优点
* 不会强强制引入Androidx及AndroidSupport包。

## 0.0.4版本更新说明
* 优化了部分代码，清楚了一些冗余代码。
* 优化了当申明权限时，用户勾选不再提示时的处理方案。现支持使用AlertDialog或自行处理。
* 支持混淆，混淆规则请看 **混淆须知**

## 快速集成
 ```implementation 'com.aoomeo.android:permissionaop:0.0.4'```
* 项目**根目录**添加 
  ```  
  dependencies {
      classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.4'
  }
  ```
* **app中的 build.gradle** 添加
  ```
  apply plugin: 'android-aspectjx'
  ```
## 使用方法
**RequirePermission**

|param|type|default|description|
|-----|----|-------|-----------|
|permissions|String[]|none|申明的权限数组|
|negativeText|String|"取消"|AlertDialog中的negativeButton's Text|
|negativeTextColor|String|""|AlertDialog中的negativeButton's TextColor,TextUtils.isEmpty()检测|
|positiveText|String|"去设置"|AlertDialog中的positiveButton's Text|
|positiveTextColor|String|""|AlertDialog中的positiveButton's TextColor,TextUtils.isEmpty()检测|
|title|String|"提示"|AlertDialog中的标题|
|tips|String[]|"当前操作缺少必要的权限。\n请点击\"设置\"-\"权限\"打开所需权限。"|提示，支持数组，使用StringBuilder.append|


在需要权限的方法前，加上
```
@RequirePermission(permissions = [Manifest.permission.ACCESS_FINE_LOCATION])
```
如需处理拒绝回调，及设置页面返回拒绝回调，实现```IPermissionRefuseListener```即可

**IPermissionRefuseListener**

|function|return|params|description|
|-----|----|-------|-----------|
|permissionRefused|void|void|用户拒绝权限回调|
|permissionForbidden|boolean|void|用户勾选不再提醒后的回调处理，return true:自定义处理;return fasle,默认弹窗|
|permissionRefusedBySetting|void|void|用户跳到设置页后的回调|


**IPermissionRefuseListener.permissionForbidden()**
用户自定义处理：勾选不再提醒后的情况，给出的一丁点建议。
```
 //弹窗 --> 希望用户去设置页给予权限。下面是跳转设置的方法
 private void startAppSettings(Activity activity) {
        Intent intent;
        try {
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, REQUEST_SETTING_CALL_BACK_CODE);
        } catch (ActivityNotFoundException e) {
            intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            activity.startActivityForResult(intent, REQUEST_SETTING_CALL_BACK_CODE);
        }
}

//弹窗 --> 用户点击取消按钮
//调用已实现的IPermissionRefuseListener.permissionRefused()
```

## 混淆须知
因为使用了反射来处理权限，所以对于一些源生类（Fragment & ActivityCompat）需要全部保留。
```
-keep class androidx.fragment.app.Fragment {*;}
-keep class android.app.Fragment {*;}
-keep class android.support.v4.app.Fragment {*;}

-keep class android.support.v4.app.ActivityCompat {*;}
-keep class androidx.core.app.ActivityCompat{*;}
-keepclassmembers class * {
    @com.aoomeo.android.permissionaop.RequirePermission <methods>;
}
```
