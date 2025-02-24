package com.eyinfo.storage_demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.eyinfo.android_pure_utils.events.Action1;
import com.eyinfo.android_pure_utils.events.Func1;
import com.eyinfo.storage.realm.PageInfo;
import com.eyinfo.storage.realm.RealmManager;
import com.eyinfo.storage_demo.beans.User;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends Activity {

    private int currentPage = 1;
    private Long lastTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        findViewById(R.id.add_data_btn).setOnClickListener(this::onAddDataClick);
        findViewById(R.id.update_data_btn).setOnClickListener(this::onUpdateDataClick);
        findViewById(R.id.query_data_btn).setOnClickListener(this::onQueryDataClick);
        findViewById(R.id.count_data_btn).setOnClickListener(this::onCountDataClick);
        findViewById(R.id.with_page_data_btn).setOnClickListener(this::onWithPageDataClick);
        findViewById(R.id.with_sort_page_data_btn).setOnClickListener(this::onWithSortPageDataClick);
        findViewById(R.id.delete_data_btn).setOnClickListener(this::onDeleteDataClick);
    }

    public void onAddDataClick(View view) {
        RealmManager.getInstance().insertOrUpdate(getAllUsers());
    }

    public void onUpdateDataClick(View view) {
        User user = RealmManager.getInstance().findById(User.class, 1000L);
        user.setName("张三2222");
        RealmManager.getInstance().insertOrUpdate(user);
    }

    public void onQueryDataClick(View view) {
        try {
            User user = RealmManager.getInstance().findById(User.class, 1000L);
            User one = RealmManager.getInstance().findOne(User.class, new Func1<RealmQuery<User>, RealmResults<User>>() {
                @Override
                public RealmResults<User> call(RealmQuery<User> userRealmQuery) {
                    userRealmQuery.equalTo("id", 1000L);
                    return userRealmQuery.findAll();
                }
            });
            List<User> list = RealmManager.getInstance().findList(User.class, new Func1<RealmQuery<User>, RealmResults<User>>() {
                @Override
                public RealmResults<User> call(RealmQuery<User> userRealmQuery) {
                    userRealmQuery.like("phone", "12345*");
                    return userRealmQuery.findAll();
                }
            });
            List<User> users = RealmManager.getInstance().findAll(User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCountDataClick(View view) {
        int count = RealmManager.getInstance().count(User.class, new Func1<RealmQuery<User>, Long>() {
            @Override
            public Long call(RealmQuery<User> userRealmQuery) {
                return userRealmQuery.count();
            }
        });
    }

    public void onWithPageDataClick(View view) {
        PageInfo<User> pageInfo = RealmManager.getInstance().queryPage(User.class, currentPage, 2, new Func1<RealmQuery<User>, RealmResults<User>>() {
            @Override
            public RealmResults<User> call(RealmQuery<User> userRealmQuery) {
                userRealmQuery.like("phone", "12345*");
                return userRealmQuery.findAll();
            }
        });
        currentPage++;
    }

    public void onWithSortPageDataClick(View view) {
        PageInfo<User> pageInfo = RealmManager.getInstance().queryPage(User.class, 1, lastTimestamp, new Action1<RealmQuery<User>>() {

            @Override
            public void call(RealmQuery<User> userRealmQuery) {
                userRealmQuery.like("phone", "12345*");
            }
        });
        lastTimestamp = pageInfo.getLastTimestamp();
    }

    public void onDeleteDataClick(View view) {
        RealmManager.getInstance().delete(User.class, new Func1<RealmQuery<User>, RealmResults<User>>() {

            @Override
            public RealmResults<User> call(RealmQuery<User> userRealmQuery) {
                userRealmQuery.isNull("id");
                return userRealmQuery.findAll();
            }
        });
    }

    private User buildUser(Long id, String name, int age, String phone) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setAge(age);
        user.setPhone(phone);
        return user;
    }

    private List<User> getAllUsers() {
        List<User> items = new ArrayList<>();
        items.add(buildUser(1000L, "张三", 18, "123456"));
        items.add(buildUser(1001L, "李四", 19, "123457"));
        items.add(buildUser(1002L, "王五", 20, "123488"));
        items.add(buildUser(1003L, "赵六", 21, "123459"));
        return items;
    }
}
