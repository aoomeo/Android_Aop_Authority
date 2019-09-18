package com.aoomeo.android.permissionaop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    String[] permissions();

    String negativeText() default "取消";

    String negativeTextColor() default "";

    String positiveText() default "去设置";

    String positiveTextColor() default "";

    String title() default "提示";

    String[] tips() default {"当前操作缺少必要的权限。\n请点击\"设置\"-\"权限\"打开所需权限。"};

}
