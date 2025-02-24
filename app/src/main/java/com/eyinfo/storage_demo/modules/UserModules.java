package com.eyinfo.storage_demo.modules;

import com.eyinfo.storage_demo.beans.User;

import io.realm.annotations.RealmModule;

@RealmModule(classes = {User.class})
public class UserModules {
}
