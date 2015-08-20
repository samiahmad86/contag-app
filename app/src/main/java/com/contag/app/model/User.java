package com.contag.app.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by tanay on 20/8/15.
 */
public class User extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public int id;
    @Column
    public String name;
    @Column
    public String mobile_number;
    @Column
    public int is_mobile_verified;
    @Column
    public String gender;
    @Column
    public String personal_email;
    @Column
    public String work_email;
    @Column
    public String work_mobile_number;
    @Column
    public String work_landline_number;
    @Column
    public String work_address;
    @Column
    public String blood_group;
    @Column
    public String date_of_birth;
    @Column
    public String marital_status;
    @Column
    public String marriage_anniversary;
}
