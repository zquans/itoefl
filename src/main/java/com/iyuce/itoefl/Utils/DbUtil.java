package com.iyuce.itoefl.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iyuce.itoefl.Common.Constants;

import java.util.ArrayList;

/**
 * Created by LeBang on 2017/2/15
 */
public class DbUtil {

    public static DbHelper getHelper(Context context, String path, int version) {
        return new DbHelper(context, path, version);
    }

    public static void createTable(SQLiteDatabase database, String table, String column_1, String column_2) {
        String create = "create table " + table + "(_id integer primary key autoincrement," + column_1 + " text," + column_2 + " text)";
        database.execSQL(create);
    }

    public static void dropTable(SQLiteDatabase database) {
        String drop = "DROP TABLE " + Constants.TABLE_USER;
        database.execSQL(drop);
    }

    public static void insert(SQLiteDatabase database, String table, String colum_1, String column_2, String value_1, String value_2) {
        String insert = "insert into " + table + "(" + colum_1 + "," + column_2 + ") values('" + value_1 + "','" + value_2 + "')";
        database.execSQL(insert);
    }

    public static void delete(SQLiteDatabase database, String table, String column, String value) {
        String delete = "delete from " + table + " where " + column + " = " + value;
        database.execSQL(delete);
    }

    public static void update(SQLiteDatabase database, String table, String column, String value, String column_where, String value_where) {
        String update = "update " + table + " set " + column + " = " + value
                + " where " + column_where + " = " + value_where;
        database.execSQL(update);
    }

    //API方法,返回新增成功的 row ID,若发生错误返回-1
    public static long insert(SQLiteDatabase database, String table, String column, String value) {
        ContentValues mValues = new ContentValues();
        mValues.put(column, value);
        return database.insert(table, null, mValues);
    }

    //API方法,返回删除成功的数量,null是子句
    public static int delete(SQLiteDatabase database, String table, String column, String... where) {
        return database.delete(table, column + "?", where);
    }

    //API方法,返回修改成功的数量,null是子句
    public static int update(SQLiteDatabase database, String table, String column, String value, String where) {
        ContentValues values = new ContentValues();
        values.put(column, value);
        return database.update(table, values, where, null);
    }

    public static String queryToString(SQLiteDatabase database, String table, int row, int column) {
        String target = "";
        Cursor cursor = database.query(table, null, null, null, null, null, null);
        //判断游标是否为空
        if (cursor != null) {
            cursor.move(row);
            target = cursor.getString(column);
        }
        return target;
    }

    //重载方法,还可以加group,order,having子句条件
    public static ArrayList<String> queryToArrayList(SQLiteDatabase database, String table, String order_by, String column_name) {
        ArrayList<String> mList = new ArrayList<>();
        Cursor cursor = database.query(table, null, null, null, null, null, order_by);
        //开启事务批量操作
        database.beginTransaction();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String target = cursor.getString(cursor.getColumnIndex(column_name));
                mList.add(target);
            }
        }
        //批量操作成功,关闭事务
        database.setTransactionSuccessful();
        database.endTransaction();
        return mList;
    }

    //重载方法
    public static ArrayList<String> queryToArrayList(SQLiteDatabase database, String table, String order_by, int column) {
        ArrayList<String> mList = new ArrayList<>();
        Cursor cursor = database.query(table, null, null, null, null, null, order_by);
        //开启事务批量操作
        database.beginTransaction();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String target = cursor.getString(column);
                mList.add(target);
            }
        }
        //批量操作成功,关闭事务
        database.setTransactionSuccessful();
        database.endTransaction();
        return mList;
    }

    public static ArrayList<String> cursorToArrayList(Cursor cursor) {
        ArrayList<String> mList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String target = cursor.getString(0);
                mList.add(target);
            }
        }
        return mList;
    }
}