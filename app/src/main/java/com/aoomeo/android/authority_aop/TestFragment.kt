package com.aoomeo.android.authority_aop

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.aoomeo.android.permissionaop.IPermissionRefuseListener
import com.aoomeo.android.permissionaop.RequirePermission

class TestFragment : Fragment(), IPermissionRefuseListener {

    override fun permissionRefused() {
        Toast.makeText(activity, "拒绝权限", Toast.LENGTH_SHORT).show()
    }

    override fun permissionRefusedBySetting() {
        Toast.makeText(activity, "拒绝权限 -- from 设置页面", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_layout, container, false)
        val button: Button = view.findViewById(R.id.button)
        button.setOnClickListener(View.OnClickListener {
            test()
        })
        return view
    }

    @RequirePermission(permissions = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun test() {
        Toast.makeText(activity, "点击了test", Toast.LENGTH_SHORT).show()
    }
}