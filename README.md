# Android_Aop_Authority v1.0
Android权限控制，通过Aop切面，减少代码侵入，权限申请简洁明了。
后续会增加一些定制化的功能，开放如当用户勾选不再提示时的回调处理。
## 使用说明
该库使用范围：在Activity及Fragment中申明权限。（支持Androidx）
该库必须配合 [aspectjx](https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx) 使用。
## 快速集成
* 项目**根目录**添加 
  ```  
  dependencies {
      classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.4'
  }
  ```
* **app中的 build.gradle** 添加
  ```
  apply plugin: 'com.hujiang.android-aspectjx'
  
  dependencies {
      implementation 'org.aspectj:aspectjrt:1.8.+'
  }
  ```
## 使用方法
在需要权限的方法前，加上
```
@RequirePermission(permissions = [Manifest.permission.ACCESS_FINE_LOCATION])
```
如需处理拒绝回调，及设置页面返回拒绝回调，实现```IPermissionRefuseListener```即可
