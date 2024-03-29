package com.lodz.android.notifyreaddemo.ui.splash

import android.Manifest
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.lodz.android.corekt.anko.*
import com.lodz.android.corekt.utils.UiHandler
import com.lodz.android.notifyreaddemo.BuildConfig
import com.lodz.android.notifyreaddemo.R
import com.lodz.android.notifyreaddemo.ui.login.LoginActivity
import com.lodz.android.pandora.base.activity.AbsActivity
import permissions.dispatcher.*

@RuntimePermissions
class SplashActivity : AbsActivity() {

    private val mTitleTv by bindView<TextView>(R.id.title_tv)

    override fun getAbsLayoutId(): Int = R.layout.activity_splash

    override fun findViews(savedInstanceState: Bundle?) {
        super.findViews(savedInstanceState)
        mTitleTv.setText(BuildConfig.APP_NAME)
    }

    override fun initData() {
        super.initData()
        if (!isTopAndBottomActivityTheSame()) {
            // 非常规启动则关闭当前页面
            finish()
            return
        }
        UiHandler.postDelayed(1000) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 6.0以上的手机对权限进行动态申请
                onRequestPermissionWithPermissionCheck()//申请权限
            } else {
                init()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    /** 权限申请成功 */
    @NeedsPermission(
        Manifest.permission.READ_PHONE_STATE,// 手机状态
        Manifest.permission.READ_SMS,// 读取短信
        Manifest.permission.RECEIVE_SMS,// 接收短信
        Manifest.permission.WRITE_EXTERNAL_STORAGE,// 存储卡读写
        Manifest.permission.READ_EXTERNAL_STORAGE// 存储卡读写
    )
    fun onRequestPermission() {
        if (!isPermissionGranted(Manifest.permission.READ_PHONE_STATE)) {
            return
        }
        if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return
        }
        if (!isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return
        }
        if (!isPermissionGranted(Manifest.permission.READ_SMS)) {
            return
        }
        if (!isPermissionGranted(Manifest.permission.RECEIVE_SMS)) {
            return
        }
        init()
    }

    /** 用户拒绝后再次申请前告知用户为什么需要该权限 */
    @OnShowRationale(
        Manifest.permission.READ_PHONE_STATE,// 手机状态
        Manifest.permission.READ_SMS,// 读取短信
        Manifest.permission.RECEIVE_SMS,// 接收短信
        Manifest.permission.WRITE_EXTERNAL_STORAGE,// 存储卡读写
        Manifest.permission.READ_EXTERNAL_STORAGE// 存储卡读写
    )
    fun onShowRationaleBeforeRequest(request: PermissionRequest) {
        request.proceed()//请求权限
    }

    /** 被拒绝 */
    @OnPermissionDenied(
        Manifest.permission.READ_PHONE_STATE,// 手机状态
        Manifest.permission.READ_SMS,// 读取短信
        Manifest.permission.RECEIVE_SMS,// 接收短信
        Manifest.permission.WRITE_EXTERNAL_STORAGE,// 存储卡读写
        Manifest.permission.READ_EXTERNAL_STORAGE// 存储卡读写
    )
    fun onDenied() {
        onRequestPermissionWithPermissionCheck()//申请权限
    }

    /** 被拒绝并且勾选了不再提醒 */
    @OnNeverAskAgain(
        Manifest.permission.READ_PHONE_STATE,// 手机状态
        Manifest.permission.READ_SMS,// 读取短信
        Manifest.permission.RECEIVE_SMS,// 接收短信
        Manifest.permission.WRITE_EXTERNAL_STORAGE,// 存储卡读写
        Manifest.permission.READ_EXTERNAL_STORAGE// 存储卡读写
    )
    fun onNeverAskAgain() {
        toastShort(R.string.splash_check_permission_tips)
        showPermissionCheckDialog()
        goAppDetailSetting()
    }

    /** 显示权限核对弹框 */
    private fun showPermissionCheckDialog() {
        AlertDialog.Builder(getContext())
            .setMessage(R.string.splash_check_permission_title)
            .setCancelable(false)
            .setPositiveButton(
                R.string.splash_check_permission_confirm,
                DialogInterface.OnClickListener { dialog, which ->
                    onRequestPermissionWithPermissionCheck()
                    dialog.dismiss()
                })
            .setNegativeButton(
                R.string.splash_check_permission_unconfirmed,
                DialogInterface.OnClickListener { dialog, which ->
                    goAppDetailSetting()
                })
            .create()
            .show()
    }

    /** 初始化 */
    private fun init() {
        LoginActivity.start(getContext())
        finish()
    }
}