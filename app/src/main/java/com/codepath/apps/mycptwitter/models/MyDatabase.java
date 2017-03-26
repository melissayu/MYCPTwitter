package com.codepath.apps.mycptwitter.models;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by melissa on 3/24/17.
 */

@Database(name = MyDatabase.NAME, version = MyDatabase.VERSION)
public class MyDatabase {
    public static final String NAME = "MyDataBase";

    public static final int VERSION = 1;
}