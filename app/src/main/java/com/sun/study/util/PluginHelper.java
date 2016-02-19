package com.sun.study.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.morgoo.droidplugin.pm.PluginManager;
import com.sun.study.framework.dialog.TipDialog;
import com.sun.study.framework.dialog.ToastTip;
import com.sun.study.model.AppInfoEntity;

/**
 * Created by sunfusheng on 16/2/18.
 */
public class PluginHelper {

    private Activity mActivity;

    public PluginHelper(Activity activity) {
        mActivity = activity;
    }

    // Apk是否安装
    public boolean isApkInstall(AppInfoEntity entity) {
        PackageInfo info;
        try {
            info = PluginManager.getInstance().getPackageInfo(entity.getPackageName(), 0);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return info != null;
    }

    // 安装Apk, 耗时较长, 需要使用异步线程
    public boolean installApk(final AppInfoEntity entity) {
        if (!com.morgoo.droidplugin.pm.PluginManager.getInstance().isConnected()) {
            ToastTip.show("连接失败");
            return false;
        }

        if (isApkInstall(entity)) {
            ToastTip.show("已安装");
            return false;
        }

        try {
            ToastTip.show("正在安装...");
            int result = PluginManager.getInstance().installPackage(entity.getApkPath(), 0);
            boolean isRequestPermission = (result == PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION);
            if (isRequestPermission) {
                ToastTip.show("权限不足");
                return false;
            }
            ToastTip.show("安装成功");
        } catch (RemoteException e) {
            e.printStackTrace();
            ToastTip.show("安装失败");
            return false;
        }

        return true;
    }

    // 卸载Apk
    public void uninstallApk(final AppInfoEntity entity) {
        new TipDialog(mActivity).show("提示", "确定要卸载" + entity.getAppName() + "么？", new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                try {
                    PluginManager.getInstance().deletePackage(entity.getPackageName(), 0);
                    Toast.makeText(mActivity, "卸载完成", Toast.LENGTH_SHORT).show();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 打开Apk
    public void startApk(final AppInfoEntity entity) {
        PackageManager pm = mActivity.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(entity.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mActivity.startActivity(intent);
        } else {
            Toast.makeText(mActivity, ""+entity.getPackageName(), Toast.LENGTH_LONG).show();
        }
    }

}
