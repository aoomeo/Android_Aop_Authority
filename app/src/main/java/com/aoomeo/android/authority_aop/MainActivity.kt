package com.aoomeo.android.authority_aop

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.aoomeo.android.permissionaop.IPermissionRefuseListener
import com.aoomeo.android.permissionaop.RequirePermission

class MainActivity : AppCompatActivity(), IPermissionRefuseListener {
    override fun permissionRefused() {
        Toast.makeText(this@MainActivity, "拒绝权限", Toast.LENGTH_SHORT).show()
    }

    override fun permissionRefusedBySetting() {
        Toast.makeText(this@MainActivity, "拒绝权限 -- from 设置页面", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView: TextView = findViewById(R.id.testButton)
        textView.setOnClickListener(View.OnClickListener {
            test()
        })

        val fragment = TestFragment()
        val fragmentBT =
            if (supportFragmentManager != null) supportFragmentManager.beginTransaction() else null
        if (fragmentBT != null) {
            fragmentBT.add(R.id.fragment, fragment)
            fragmentBT.commit()
        }
    }

    @RequirePermission(permissions = [Manifest.permission.ACCESS_FINE_LOCATION])
    fun test() {
        Toast.makeText(this@MainActivity, "点击了test", Toast.LENGTH_SHORT).show()
    }

}
