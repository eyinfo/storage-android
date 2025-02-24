package com.eyinfo.storage_demo.beans;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.RealmField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@RealmClass
public class User extends RealmObject {

    /**
     * 数据id
     */
    @PrimaryKey
    private Long id;

    /**
     * 创建时间
     */
    @RealmField("createTime")
    private Long createTime;

    /**
     * 修改时间
     */
    @RealmField("modifyTime")
    private Long modifyTime;

    @RealmField("name")
    private String name;

    @RealmField("age")
    private int age;

    @RealmField("phone")
    private String phone;
}
