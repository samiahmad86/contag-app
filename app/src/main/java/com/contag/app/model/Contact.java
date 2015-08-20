package com.contag.app.model;

import com.contag.app.config.Database;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Kartikey on 8/18/2015.
 */

@Table(databaseName = Database.NAME)
public class Contact extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public int id;
    @Column
    public String name;
}