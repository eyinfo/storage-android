package com.eyinfo.storage_realm;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.eyinfo.android_pure_utils.ObjectManager;
import com.eyinfo.storage.files.FileUtils;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ConfigurationRealm {
    private static volatile ConfigurationRealm instance;

    public static ConfigurationRealm getInstance() {
        if (instance == null) {
            synchronized (ConfigurationRealm.class) {
                if (instance == null) {
                    instance = new ConfigurationRealm();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化Realm
     *
     * @param applicationContext 应用上下文
     * @param dbName             数据库名称
     */
    public void init(Context applicationContext, String dbName, Object module) {
        try {
            Realm.init(applicationContext);

            PackageInfo packageInfo = ObjectManager.getPackageInfo(applicationContext);
            int versionCode = packageInfo.versionCode;
            config(applicationContext, dbName, versionCode, module);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Realm配置
     *
     * @param applicationContext 应用上下文
     * @param dbName             数据库名称
     * @param buildNo            构建号
     */
    private void config(Context applicationContext, String dbName, int buildNo, Object module) {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(dbName)
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(false)
                .compactOnLaunch()
                .directory(FileUtils.getRootDir(applicationContext))
                .schemaVersion(buildNo)
                .modules(module)
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
