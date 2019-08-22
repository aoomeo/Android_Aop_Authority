package com.aoomeo.android.permissionaop;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Aspect
public class PermissionHandler {

    private final static String TAG = PermissionHandler.class.getSimpleName();
    private ProceedingJoinPoint pointMethod; //方法
    private RequirePermission aspectJAnnotation;
    private final static int REQUEST_PERMISSION_CODE = 0xff01;
    private final static int REQUEST_SETTING_CALL_BACK_CODE = 0xff02;

    @Pointcut("this(android.app.Activity)") //this(Type) : 判断该JoinPoint所在的类是否是Type类型
    public void isActivity() {
        Log.d(TAG, "isActivity");
    }

    @Pointcut("this(android.support.v4.app.Fragment) || this(android.app.Fragment) || this(androidx.fragment.app.Fragment)")
    public void isFragment() {
    }

    @Pointcut("execution(@com.aoomeo.android.permissionaop.RequirePermission  * *(..))")
    public void isPermissionAnnotation() {
    }

    /**
     * call 和 execution的区别
     * call:
     * | call before     |
     * | Pointcut{       |
     * | Pointcut Method |
     * | }               |
     * | call after      |
     * <p>
     * execution:
     * | Pointcut{          |
     * | execution before   |
     * | Pointcut Method    |
     * | execution.after  } |
     **/


    @Around("(isActivity()||isFragment()) && isPermissionAnnotation()")
    public void aroundAspectJ(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.d("aroundAspectJ", "test");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            this.aspectJAnnotation = methodSignature.getMethod().getAnnotation(RequirePermission.class);
            Activity activity;
            if (joinPoint.getTarget() instanceof Activity) {
                activity = (Activity) joinPoint.getTarget();
            } else {
                try {
                    Method[] methods = joinPoint.getTarget().getClass().getMethods();
                    Method getActivity = null;
                    for (Method method : methods) {
                        if (method.getName().equals("getActivity")) {
                            getActivity = method;
                        }
                        Log.d(TAG, "methods: " + method.getName());
                    }
                    if (getActivity == null) {
                        throw new Exception("no such getActivity");
                    }
                    activity = (FragmentActivity) getActivity.invoke(joinPoint.getTarget());
                } catch (Exception e) {
                    throw new IllegalArgumentException("@RequirePermission must be used in Activity or Fragment");
                }
            }
            if (checkPermissionHandler(activity, aspectJAnnotation.permissions())) { //有权限时，直接执行这段代码
                joinPoint.proceed();
            } else {
                this.pointMethod = joinPoint; //无权限时，将切点代码保存，开启一个新的Activity
                Intent intent = new Intent();
                intent.setClass(activity, PermissionAopActivity.class);
                activity.startActivity(intent);
            }
        } else {
            joinPoint.proceed();
        }
    }

    @After("execution(* com.aoomeo.android.permissionaop.PermissionAopActivity.onCreate(..))")
    public void onPermissionActivityCreate(JoinPoint joinPoint) throws Throwable { //使用新的Activity去请求权限
        ActivityCompat.requestPermissions((Activity) joinPoint.getTarget(), aspectJAnnotation.permissions(), REQUEST_PERMISSION_CODE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Around("execution(* com.aoomeo.android.permissionaop.PermissionAopActivity.onRequestPermissionsResult(..))")
    public void onRequestPermissionsResult(final JoinPoint joinPoint) throws Throwable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Object[] objects = joinPoint.getArgs();
            int resultCode = (int) objects[0];   //resultCode
            if (resultCode == REQUEST_PERMISSION_CODE) {
                final Activity activity = (Activity) joinPoint.getTarget();
                int[] grantResults = (int[]) objects[2];  //grantResults
                boolean isPermissionsGranted = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isPermissionsGranted = false;
                        break;
                    }
                }
                if (isPermissionsGranted) {
                    activity.finish();
                    permissionGranted();
                    return;
                } else {
                    for (String permission : Arrays.asList((String[]) objects[1])) {
                        if (!activity.shouldShowRequestPermissionRationale(permission)) {
                            int length = aspectJAnnotation.tips().length;
                            StringBuilder messageBuilder = new StringBuilder("");
                            if (length > 0) {
                                for (int i = 0; i < length; i++) {
                                    messageBuilder.append(aspectJAnnotation.tips()[i]);
                                }
                            }
                            AlertDialog alertDialog = new AlertDialog.Builder((Context) joinPoint.getTarget())
                                    .setTitle(aspectJAnnotation.dialogTitle())
                                    .setMessage(messageBuilder.toString())
                                    .setNegativeButton(aspectJAnnotation.dialogCancelText(), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            activity.finish();
                                            permissionRefused();
                                        }
                                    }).setPositiveButton(aspectJAnnotation.dialogSureText(), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startAppSettings((Activity) joinPoint.getTarget());
                                        }
                                    }).create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                            break;
                        } else {
                            activity.finish();
                            permissionRefused();
                        }
                    }
                }
                return;
            }
        }
        ((Activity) joinPoint.getTarget()).finish();
        permissionGranted();
    }

    @Around("execution(*  com.aoomeo.android.permissionaop.PermissionAopActivity.onActivityResult(..))")
    public void onActivityResult(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object[] objects = proceedingJoinPoint.getArgs();
        int resultCode = (int) objects[0];
        if (resultCode == REQUEST_SETTING_CALL_BACK_CODE) {
            final Activity activity = (Activity) proceedingJoinPoint.getTarget();
            if (checkPermissionHandler((Activity) proceedingJoinPoint.getTarget(), aspectJAnnotation.permissions())) {
                permissionGranted();
                activity.finish();
            } else {
                permissionRefusedBySetting();
                activity.finish();
            }
        } else {
            proceedingJoinPoint.proceed();
        }
    }

    private void permissionGranted() throws Throwable {
        if (pointMethod != null) {
            pointMethod.proceed();
            pointMethod = null;
            System.gc();
        }
    }

    private void permissionRefused() {
        if (pointMethod != null && pointMethod.getTarget() != null
                && IPermissionRefuseListener.class.isAssignableFrom(pointMethod.getTarget().getClass())) {
            ((IPermissionRefuseListener) pointMethod.getTarget()).permissionRefused();
        }
    }

    private void permissionRefusedBySetting() {
        if (pointMethod != null && pointMethod.getTarget() != null
                && IPermissionRefuseListener.class.isAssignableFrom(pointMethod.getTarget().getClass())) {
            ((IPermissionRefuseListener) pointMethod.getTarget()).permissionRefusedBySetting();
        }
    }

    // 循环遍历查看权限数组
    private boolean checkPermissionHandler(Context context, String... permissions) {
        List<String> permissionList = Arrays.asList(permissions);
        for (String permission : permissionList) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

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
}
