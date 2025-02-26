package com.eyinfo.storage_demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.eyinfo.android_pure_utils.events.Action1;
import com.eyinfo.storage_demo.beans.UserOB;
import com.eyinfo.storage_demo.beans.UserOB_;
import com.eyinfo.storage_objectbox.PageInfo;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.query.QueryBuilder;

public class MainOBActivity extends Activity {

    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_ob_activity);

        findViewById(R.id.add_data_btn).setOnClickListener(this::onAddDataClick);
        findViewById(R.id.update_data_btn).setOnClickListener(this::onUpdateDataClick);
        findViewById(R.id.query_data_btn).setOnClickListener(this::onQueryDataClick);
        findViewById(R.id.count_data_btn).setOnClickListener(this::onCountDataClick);
        findViewById(R.id.with_page_data_btn).setOnClickListener(this::onWithPageDataClick);
        findViewById(R.id.delete_data_btn).setOnClickListener(this::onDeleteDataClick);
    }

    public void onAddDataClick(View view) {
        try {
            OBManager.getInstance().insertOrUpdate(UserOB.class, getUsers());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onUpdateDataClick(View view) {
        UserOB userOB = OBManager.getInstance().findById(UserOB.class, 1000L);
        userOB.setName("老玩童");
        OBManager.getInstance().insertOrUpdate(UserOB.class, userOB);
    }

    public void onQueryDataClick(View view) {
        UserOB userOB = OBManager.getInstance().findById(UserOB.class, 1000L);
        UserOB one = OBManager.getInstance().findOne(UserOB.class, new Action1<QueryBuilder<UserOB>>() {
            @Override
            public void call(QueryBuilder<UserOB> userOBQueryBuilder) {
                userOBQueryBuilder.equal(UserOB_.name, "22王药师", QueryBuilder.StringOrder.CASE_INSENSITIVE);
            }
        });
        List<UserOB> list = OBManager.getInstance().findList(UserOB.class, new Action1<QueryBuilder<UserOB>>() {
            @Override
            public void call(QueryBuilder<UserOB> userOBQueryBuilder) {
                userOBQueryBuilder.contains(UserOB_.name, "11", QueryBuilder.StringOrder.CASE_INSENSITIVE);
            }
        });
        List<UserOB> all = OBManager.getInstance().findAll(UserOB.class);
    }

    public void onCountDataClick(View view) {
        int count = OBManager.getInstance().count(UserOB.class, null);
    }

    public void onWithPageDataClick(View view) {
        PageInfo<UserOB> pageInfo = OBManager.getInstance().queryPage(UserOB.class, currentPage, 1, new Action1<QueryBuilder<UserOB>>() {

            @Override
            public void call(QueryBuilder<UserOB> userOBQueryBuilder) {
                userOBQueryBuilder.contains(UserOB_.name, "11", QueryBuilder.StringOrder.CASE_INSENSITIVE);
            }
        });
        currentPage++;
    }

    public void onDeleteDataClick(View view) {
        OBManager.getInstance().delete(UserOB.class, new Action1<QueryBuilder<UserOB>>() {

            @Override
            public void call(QueryBuilder<UserOB> userOBQueryBuilder) {
                userOBQueryBuilder.contains(UserOB_.name, "11", QueryBuilder.StringOrder.CASE_INSENSITIVE);
            }
        });
    }

    private UserOB buildUser(Long id, int age, String name) {
        UserOB userOB = new UserOB();
        userOB.setId(id);
        userOB.setAge(age);
        userOB.setName(name);
        return userOB;
    }

    private List<UserOB> getUsers() {
        List<UserOB> users = new ArrayList<>();
        users.add(buildUser(1000L, 10, "11张三"));
        users.add(buildUser(1001L, 12, "11李四"));
        users.add(buildUser(1002L, 15, "22王药师"));
        return users;
    }
}
