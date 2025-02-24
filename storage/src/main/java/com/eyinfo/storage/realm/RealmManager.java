package com.eyinfo.storage.realm;

import com.eyinfo.android_pure_utils.events.Action1;
import com.eyinfo.android_pure_utils.events.Func1;
import com.eyinfo.android_pure_utils.utils.ConvertUtils;
import com.eyinfo.android_pure_utils.utils.GlobalUtils;
import com.eyinfo.storage.realm.transaction.DeleteTransaction;
import com.eyinfo.storage.realm.transaction.InsertOrUpdateTransaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmManager {
    private static volatile RealmManager instance;

    public static RealmManager getInstance() {
        if (instance == null) {
            synchronized (RealmManager.class) {
                if (instance == null) {
                    instance = new RealmManager();
                }
            }
        }
        return instance;
    }

    /**
     * 新增或更新
     *
     * @param element 待插入或更新的实体
     * @param <T>
     */
    public <T extends RealmObject> void insertOrUpdate(T element) {
        InsertOrUpdateTransaction transaction = new InsertOrUpdateTransaction();
        List<T> elements = new ArrayList<>();
        elements.add(element);
        transaction.insertOrUpdate(elements);
    }

    /**
     * 待新增或更新的实体集合
     *
     * @param elements 待插入或更新的实体集合
     * @param <T>
     */
    public <T extends RealmObject> void insertOrUpdate(Collection<T> elements) {
        InsertOrUpdateTransaction transaction = new InsertOrUpdateTransaction();
        transaction.insertOrUpdate(elements);
    }

    /**
     * 根据id查询
     *
     * @param clazz 实体类型
     * @param id    主键id
     * @param <T>
     * @return
     */
    public <T extends RealmObject> T findById(Class<T> clazz, Long id) {
        Realm realm = Realm.getDefaultInstance();
        T element = realm.where(clazz).equalTo("id", id).findFirst();
        T result = realm.copyFromRealm(element);
        realm.close();
        return result;
    }

    /**
     * 查询单个
     *
     * @param clazz 实体类型
     * @param func
     * @param <T>
     * @return
     */
    public <T extends RealmObject> T findOne(Class<T> clazz, Func1<RealmQuery<T>, RealmResults<T>> func) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<T> realmQuery = realm.where(clazz);
        RealmResults<T> results = func.call(realmQuery);
        T element = results.isEmpty() ? null : realm.copyFromRealm(results.first());
        realm.close();
        return element;
    }

    /**
     * 查询列表
     *
     * @param clazz 实体类型
     * @param func
     * @param <T>
     * @return
     */
    public <T extends RealmObject> List<T> findList(Class<T> clazz, Func1<RealmQuery<T>, RealmResults<T>> func) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<T> realmQuery = realm.where(clazz);
        RealmResults<T> results = func.call(realmQuery);
        List<T> models = realm.copyFromRealm(results);
        realm.close();
        return models;
    }

    /**
     * 查询所有
     *
     * @param clazz 实体类型
     * @param <T>
     * @return
     */
    public <T extends RealmObject> List<T> findAll(Class<T> clazz) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<T> results = realm.where(clazz).findAll();
        List<T> models = realm.copyFromRealm(results);
        realm.close();
        return models;
    }

    /**
     * 删除数据
     *
     * @param clazz 实体类型
     * @param func
     * @param <T>
     * @return
     */
    public <T extends RealmObject> void delete(Class<T> clazz, Func1<RealmQuery<T>, RealmResults<T>> func) {
        DeleteTransaction deleteTransaction = new DeleteTransaction();
        deleteTransaction.delete(clazz, func);
    }

    /**
     * 统计记录数
     *
     * @param clazz 实体类型
     * @param func
     * @param <T>
     * @return
     */
    public <T extends RealmObject> int count(Class<T> clazz, Func1<RealmQuery<T>, Long> func) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<T> realmQuery = realm.where(clazz);
        Long count = func.call(realmQuery);
        realm.close();
        return count == null ? 0 : count.intValue();
    }

    /**
     * 分页查询,适合小数据量场景
     *
     * @param clazz 实体类型
     * @param page  起始页码
     * @param limit 每页记录数
     * @param func
     * @param <T>
     * @return
     */
    public <T extends RealmObject> PageInfo<T> queryPage(Class<T> clazz, int page, int limit, Func1<RealmQuery<T>, RealmResults<T>> func) {
        int pageNumber = Math.max(page, 0);
        pageNumber = pageNumber > 0 ? (pageNumber - 1) : pageNumber;

        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setPage(page);
        pageInfo.setLimit(limit);

        Realm realm = Realm.getDefaultInstance();
        RealmQuery<T> realmQuery = realm.where(clazz);
        RealmResults<T> results = func.call(realmQuery);

        int startIndex = pageNumber * limit;
        int size = results.size();
        int endIndex = Math.min(startIndex + limit, size);
        if (startIndex >= endIndex) {
            realm.close();
            return pageInfo;
        }
        List<T> list = realm.copyFromRealm(results.subList(startIndex, endIndex));
        realm.close();

        pageInfo.setTotal(size);
        pageInfo.setList(list);
        return pageInfo;
    }

    /**
     * 分页查询,适合大数据量场景
     *
     * @param clazz         实体类型
     * @param limit         每页记录数
     * @param lastTimestamp 上一页最后一条数据的时间戳
     * @param func
     * @param <T>
     * @return
     */
    public <T extends RealmObject> PageInfo<T> queryPage(Class<T> clazz, int limit, Long lastTimestamp, Action1<RealmQuery<T>> call) {
        PageInfo<T> pageInfo = new PageInfo<>();

        Realm realm = Realm.getDefaultInstance();
        RealmQuery<T> query = realm.where(clazz);
        call.call(query);
        pageInfo.setTotal((int) query.count());
        if (lastTimestamp != null) {
            query.greaterThan("createTime", lastTimestamp);
        }
        RealmResults<T> results = query
                .sort("createTime", Sort.ASCENDING)
                .limit(limit)
                .findAll();

        List<T> list = realm.copyFromRealm(results);
        if (!Objects.isNull(list) && !list.isEmpty()) {
            T last = list.get(list.size() - 1);
            Object lastTime = GlobalUtils.getPropertiesValue(last, "createTime", false);
            if (lastTime != null) {
                long createTime = ConvertUtils.toLong(lastTime);
                pageInfo.setLastTimestamp(createTime);
            }
        } else {
            pageInfo.setTotal(0);
        }

        pageInfo.setLimit(limit);
        pageInfo.setList(list);
        return pageInfo;
    }
}
