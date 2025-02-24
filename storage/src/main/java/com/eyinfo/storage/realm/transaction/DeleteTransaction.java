package com.eyinfo.storage.realm.transaction;

import com.eyinfo.android_pure_utils.events.Func1;
import com.eyinfo.android_pure_utils.observable.ObservableComponent;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class DeleteTransaction {
    public <T extends RealmObject> void delete(Class<T> clazz, Func1<RealmQuery<T>, RealmResults<T>> func) {
        ObservableComponent<DeleteParams, DeleteParams> component = new ObservableComponent<DeleteParams, DeleteParams>() {
            @Override
            protected DeleteParams subscribeWith(DeleteParams... deleteParams) throws Exception {
                DeleteParams<T> param = deleteParams[0];
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new DeleteTransactionBuilder(param));
                realm.close();
                return super.subscribeWith(deleteParams);
            }
        };
        DeleteParams deleteParams = new DeleteParams();
        deleteParams.clazz = clazz;
        deleteParams.func = func;
        component.build(deleteParams);
    }

    private static class DeleteTransactionBuilder<T extends RealmObject> implements Realm.Transaction {

        private DeleteParams<T> param;

        public DeleteTransactionBuilder(DeleteParams<T> param) {
            this.param = param;
        }

        @Override
        public void execute(Realm realm) {
            RealmQuery<T> realmQuery = realm.where(param.clazz);
            RealmResults<T> results = param.func.call(realmQuery);
            results.deleteAllFromRealm();
        }
    }

    private static class DeleteParams<T extends RealmObject> {
        private Class<T> clazz;
        Func1<RealmQuery<T>, RealmResults<T>> func;
    }
}
