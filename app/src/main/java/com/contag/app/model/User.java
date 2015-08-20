package com.contag.app.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Kartikey on 8/18/2015.
 */
@Table(databaseName = Database.NAME)
public class User extends BaseModel{
    @Column
    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    String name;
    @Column
    String mobile_number;
    @Column
    int is_mobile_verified;
    @Column
    String gender;
    @Column
    String personal_email;
    @Column
    String work_email;
    @Column
    String work_mobile_number;
    @Column
    String work_landline_number;
    @Column
    String work_address;
    @Column
    String blood_group;
    @Column
    String date_of_birth;
    @Column
    String marrital_status;
    @Column
    String marriage_anniversary;
}
