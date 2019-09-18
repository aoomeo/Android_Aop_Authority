# Android_Aop_Authority v0.0.3
Android权限控制，通过Aop切面，减少代码侵入，权限申请简洁明了。
后续会增加一些定制化的功能，开放如当用户勾选不再提示时的回调处理。
## 使用说明
该库使用范围：在Activity及Fragment中申明权限。（支持Androidx）
该库必须配合 [aspectjx](https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx) 使用。
## 0.0.3版本更新说明
* 优化了请求权限时，需跳到一个透明的Activity。（这种实现方式好像有点傻哦！！）
* 通过反射，优化了对于Androidx库的依赖。（现在可以放心使用啦，不会强制引入Androidx）

## 快速集成
 ```implementation 'com.aoomeo.android:permissionaop:0.0.3'```
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
在需要权限的方法前，加上
```
@RequirePermission(permissions = [Manifest.permission.ACCESS_FINE_LOCATION])
```
如需处理拒绝回调，及设置页面返回拒绝回调，实现```IPermissionRefuseListener```即可

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
