package com.eyinfo.storage_realm.transaction;

import com.eyinfo.android_pure_utils.observable.ObservableComponent;
import com.eyinfo.android_pure_utils.snow.Butterfly;
import com.eyinfo.android_pure_utils.utils.GlobalUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;

public class InsertOrUpdateTransaction {

    private <T extends RealmObject> boolean hasDefaultField(T element, boolean hasField, String... fieldNames) {
        if (hasField) {
            return true;
        }
        List<String> fields = Arrays.asList(fieldNames);
        Class<? extends RealmObject> aClass = element.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        if (declaredFields != null) {
            for (Field field : declaredFields) {
                String name = field.getName();
                if (fields.contains(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private <T extends RealmObject> void bindBasicAttribute(Collection<T> params) {
        boolean hasField = false;
        for (T element : params) {
            if (hasDefaultField(element, hasField, "id", "createTime", "modifyTime")) {
                hasField = true;
                Object id = GlobalUtils.getPropertiesValue(element, "id", false);
                if (id == null || GlobalUtils.getPropertiesValue(element, "createTime", false) == null) {
                    if (id == null) {
                        GlobalUtils.setPropertiesValue(element, "id", Butterfly.getInstance().nextId());
                    }
                    GlobalUtils.setPropertiesValue(element, "createTime", System.currentTimeMillis());
                }
                GlobalUtils.setPropertiesValue(element, "modifyTime", System.currentTimeMillis());
            }
        }
    }

    public <T extends RealmObject> void insertOrUpdate(Collection<T> elements) {
        ObservableComponent<Collection<T>, Collection<T>> component = new ObservableComponent<Collection<T>, Collection<T>>() {

            @Override
            protected Collection<T> subscribeWith(Collection<T>... collections) throws Exception {
                Collection<T> param = collections[0];
                bindBasicAttribute(param);
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new InsertOrUpdateTransactionBuilder(param));
                realm.close();
                return param;
            }
        };
        component.build(elements);
    }

    private static class InsertOrUpdateTransactionBuilder<T extends RealmObject> implements Realm.Transaction {

        private Collection<T> elements;

        public InsertOrUpdateTransactionBuilder(Collection<T> elements) {
            this.elements = elements;
        }

        @Override
        public void execute(Realm realm) {
            realm.insertOrUpdate(elements);
        }
    }
}
