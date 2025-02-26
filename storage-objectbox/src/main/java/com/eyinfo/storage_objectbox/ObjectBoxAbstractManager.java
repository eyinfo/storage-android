package com.eyinfo.storage_objectbox;

import android.content.Context;

import com.eyinfo.android_pure_utils.events.Action1;
import com.eyinfo.storage.files.FileUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.BoxStoreBuilder;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

public class ObjectBoxAbstractManager {

    private Map<String, BoxStore> boxStoreMap = new HashMap<>();

    /**
     * 初始化BoxStore
     *
     * @param applicationContext 上下文
     * @param builder            由MyObjectBox.builder()获取
     * @param dbKey              数据库唯一标识
     */
    protected void init(Context applicationContext, BoxStoreBuilder builder, String dbKey) {
        BoxStore boxStore = boxStoreMap.get(dbKey);
        if (boxStore != null) {
            return;
        }
        boxStore = builder.androidContext(applicationContext)
                .name(dbKey)
                .baseDirectory(FileUtils.getDir(applicationContext, "objectbox"))
                .build();
        boxStoreMap.put(dbKey, boxStore);
    }

    /**
     * 获取BoxStore
     *
     * @param dbKey 数据库唯一标识
     * @return BoxStore
     */
    private BoxStore get(String dbKey) {
        return boxStoreMap.get(dbKey);
    }

    /**
     * 插入或更新
     *
     * @param dbKey    数据库唯一标识
     * @param clazz    模型类型
     * @param elements 待插入或更新的实体集合
     * @param <T>
     */
    protected <T> void insertOrUpdate(String dbKey, Class<T> clazz, Collection<T> elements) {
        BoxStore boxStore = get(dbKey);
        if (boxStore == null) {
            return;
        }
        Box<T> box = boxStore.boxFor(clazz);
        box.put(elements);
    }

    /**
     * 插入或更新
     *
     * @param dbKey   数据库唯一标识
     * @param clazz   模型类型
     * @param element 待插入或更新的实体
     * @param <T>
     */
    protected <T> void insertOrUpdate(String dbKey, Class<T> clazz, T element) {
        List<T> elements = new ArrayList<>();
        elements.add(element);
        insertOrUpdate(dbKey, clazz, elements);
    }

    /**
     * 根据id查询
     *
     * @param dbKey 数据库唯一标识
     * @param clazz 模型类型
     * @param id    主键id
     * @param <T>
     * @return
     */
    protected <T> T findById(String dbKey, Class<T> clazz, Long id) {
        BoxStore boxStore = get(dbKey);
        if (boxStore == null) {
            return null;
        }
        Box<T> box = boxStore.boxFor(clazz);
        return box.get(id);
    }

    /**
     * 根据条件查询
     *
     * @param dbKey 数据库唯一标识
     * @param clazz 模型类型
     * @param call  查询条件
     * @param <T>
     * @return
     */
    protected <T> T findOne(String dbKey, Class<T> clazz, Action1<QueryBuilder<T>> call) {
        BoxStore boxStore = get(dbKey);
        if (boxStore == null) {
            return null;
        }
        Box<T> box = boxStore.boxFor(clazz);
        QueryBuilder<T> queryBuilder = box.query();
        call.call(queryBuilder);
        Query<T> build = queryBuilder.build();
        return build.findFirst();
    }

    /**
     * 根据条件查询数据集合
     *
     * @param dbKey 数据库唯一标识
     * @param clazz 模型类型
     * @param call  查询条件
     * @param <T>
     * @return
     */
    protected <T> List<T> findList(String dbKey, Class<T> clazz, Action1<QueryBuilder<T>> call) {
        BoxStore boxStore = get(dbKey);
        if (boxStore == null) {
            return null;
        }
        Box<T> box = boxStore.boxFor(clazz);
        QueryBuilder<T> queryBuilder = box.query();
        if (call != null) {
            call.call(queryBuilder);
        }
        Query<T> build = queryBuilder.build();
        return build.find();
    }

    /**
     * 根据条件查询数据集合
     *
     * @param dbKey 数据库唯一标识
     * @param clazz 模型类型
     * @param <T>
     * @return
     */
    protected <T> List<T> findAll(String dbKey, Class<T> clazz) {
        return findList(dbKey, clazz, null);
    }

    /**
     * 根据条件删除数据
     *
     * @param dbKey 数据库唯一标识
     * @param clazz 模型类型
     * @param call  查询条件
     * @param <T>
     */
    protected <T> void delete(String dbKey, Class<T> clazz, Action1<QueryBuilder<T>> call) {
        BoxStore boxStore = get(dbKey);
        if (boxStore == null) {
            return;
        }
        Box<T> box = boxStore.boxFor(clazz);
        QueryBuilder<T> queryBuilder = box.query();
        if (call != null) {
            call.call(queryBuilder);
        }
        Query<T> build = queryBuilder.build();
        long[] ids = build.findIds();
        box.remove(ids);
    }

    /**
     * 查询总数
     *
     * @param dbKey 数据库唯一标识
     * @param clazz 模型类型
     * @param call  查询条件
     * @param <T>
     * @return
     */
    protected <T> int count(String dbKey, Class<T> clazz, Action1<QueryBuilder<T>> call) {
        BoxStore boxStore = get(dbKey);
        if (boxStore == null) {
            return 0;
        }
        Box<T> box = boxStore.boxFor(clazz);
        QueryBuilder<T> queryBuilder = box.query();
        if (call != null) {
            call.call(queryBuilder);
        }
        Query<T> build = queryBuilder.build();
        return (int) build.count();
    }

    /**
     * 分页查询
     *
     * @param dbKey 数据库唯一标识
     * @param clazz 模型类型
     * @param page  起始页码
     * @param limit 每页数量
     * @param call  查询条件
     * @param <T>
     * @return
     */
    protected <T> PageInfo<T> queryPage(String dbKey, Class<T> clazz, int page, int limit, Action1<QueryBuilder<T>> call) {
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setPage(page);
        pageInfo.setLimit(limit);

        BoxStore boxStore = get(dbKey);
        if (boxStore == null) {
            return pageInfo;
        }

        long pageNumber = Math.max(page, 0);
        pageNumber = pageNumber > 0 ? (pageNumber - 1) : pageNumber;

        Box<T> box = boxStore.boxFor(clazz);
        QueryBuilder<T> queryBuilder = box.query();
        call.call(queryBuilder);
        Query<T> build = queryBuilder.build();

        pageInfo.setTotal((int) build.count());
        pageInfo.setList(build.find(pageNumber * limit, limit));
        return pageInfo;
    }
}
