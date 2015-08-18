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
/*
    name = models.CharField(null=False, max_length=255)
    contag = models.CharField(null=False, max_length=8)
    mobile_number = models.CharField(max_length=100, null=False)
    landline_number = models.CharField(max_length=100, null=True)
    emergency_contact_number = models.CharField(max_length=100, null=True)
    is_mobile_verified = models.BooleanField(default=False)
    gender = models.CharField(max_length=1, default='f')
    personal_email = models.CharField(max_length=255, null=True)
    registered_with = models.CharField(max_length=255, null=False)
    address = models.CharField(max_length=500, null=True)
    work_email = models.CharField(max_length=255, null=True)
    work_mobile_number = models.CharField(max_length=100, null=True)
    work_landline_number = models.CharField(max_length=100, null=True)
    work_address = models.CharField(max_length=100, null=True)
    website = models.CharField(max_length=100, null=True)
    designation = models.CharField(max_length=100, null=True)
    work_facebook_page = models.CharField(max_length=100, null=True)
    android_app_link = models.CharField(max_length=100, null=True)
    ios_app_link = models.CharField(max_length=100, null=True)
    avatar_url = models.CharField(max_length=500, null=True)
    blood_group = models.CharField(max_length=55, null=True)
    date_of_birth = models.DateField()
    marital_status = models.BooleanField(default=False)
    marriage_anniversary = models.DateField()
*/
}
