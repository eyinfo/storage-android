package com.eyinfo.storage_demo;

import android.app.Application;

import com.eyinfo.storage_demo.modules.UserModules;
import com.eyinfo.storage_realm.ConfigurationRealm;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ConfigurationRealm.getInstance().init(
                getApplicationContext(),
                "storage.realm",
                new UserModules()
        );
    }
}
