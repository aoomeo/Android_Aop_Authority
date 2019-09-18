package com.aoomeo.android.permissionaop;

public interface IPermissionRefuseListener {
    /**
     * users refuse the permissions which you require
     */
    void permissionRefused();

    /**
     * return boolean
     * true : handle the situation which users refuse permissions with the selection
     * of never mind again by yourself
     * false : not handle the situation by yourself , you could set dialog's title, content
     * or other configs
     */
    boolean permissionForbidden();

    /**
     * users refuse the permission which you require from system setting
     */
    void permissionRefusedBySetting();


}
