package com.eyinfo.storage_demo;

import android.content.Context;

import com.eyinfo.android_pure_utils.events.Action1;
import com.eyinfo.storage_demo.beans.MyObjectBox;
import com.eyinfo.storage_objectbox.ObjectBoxAbstractManager;
import com.eyinfo.storage_objectbox.PageInfo;

import java.util.List;

import io.objectbox.query.QueryBuilder;

public class OBManager extends ObjectBoxAbstractManager {

    private static volatile OBManager instance;

    public static OBManager getInstance() {
        if (instance == null) {
            synchronized (OBManager.class) {
                if (instance == null) {
                    instance = new OBManager();
                }
            }
        }
        return instance;
    }

    private final String dbKey = "storage.objectbox";

    public void init(Context applicationContext) {
        super.init(applicationContext, MyObjectBox.builder(), dbKey);
    }

    public <T> void insertOrUpdate(Class<T> clazz, List<T> elements) {
        super.insertOrUpdate(dbKey, clazz, elements);
    }

    public <T> void insertOrUpdate(Class<T> clazz, T element) {
        super.insertOrUpdate(dbKey, clazz, element);
    }

    public <T> T findById(Class<T> clazz, Long id) {
        return super.findById(dbKey, clazz, id);
    }

    public <T> T findOne(Class<T> clazz, Action1<QueryBuilder<T>> call) {
        return super.findOne(dbKey, clazz, call);
    }

    public <T> List<T> findList(Class<T> clazz, Action1<QueryBuilder<T>> call) {
        return super.findList(dbKey, clazz, call);
    }

    public <T> List<T> findAll(Class<T> clazz) {
        return super.findAll(dbKey, clazz);
    }

    public <T> int count(Class<T> clazz, Action1<QueryBuilder<T>> call) {
        return super.count(dbKey, clazz, call);
    }

    public <T> PageInfo<T> queryPage(Class<T> clazz, int page, int limit, Action1<QueryBuilder<T>> call) {
        return super.queryPage(dbKey, clazz, page, limit, call);
    }

    public <T> void delete(Class<T> clazz, Action1<QueryBuilder<T>> call) {
        super.delete(dbKey, clazz, call);
    }
}
