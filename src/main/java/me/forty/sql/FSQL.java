package me.forty.sql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.util.StringUtils;
import me.forty.sql.annotation.Column;
import me.forty.sql.clazz.FSQLClass;
import me.forty.sql.utility.UUIDSerializer;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2021 - Tranquil, LLC.
 *
 * @author 42 on Oct, 19, 2021 - 9:12 PM
 * @project fSQL
 */
public class FSQL {

    public static Gson MODDED_GSON = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDSerializer())
            .serializeNulls()
            .create();

    private MysqlDataSource source;
    private Connection con;

    public FSQL(String host, int port, String db, String user, String password) {
        try {
            this.source = new MysqlDataSource();

            source.setServerName(host);
            source.setPort(port);
            source.setDatabaseName(db);
            source.setUser(user);
            source.setPassword(password);
            source.setAllowMultiQueries(true);

            this.con = source.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insert(FSQLClass fsqlClass) {
        try {
            String columnData = StringUtils.joinWithSerialComma(fsqlClass.getColumns());
            String valueData = StringUtils.joinWithSerialComma(fsqlClass.getValues().stream().map(obj -> FSQL.MODDED_GSON.toJson(obj)).collect(Collectors.toList()));

            Statement stmt = con.createStatement();
            stmt.execute("INSERT INTO " + fsqlClass.getTable() + " (" + columnData + ") VALUES (" + valueData + ");");
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(String filter, FSQLClass fsqlClass) {
        try {
            int index = 0;

            List<String> changes = new ArrayList<>();
            for (String column : fsqlClass.getColumns()) {
                changes.add(column + " = '" + FSQL.MODDED_GSON.toJson(fsqlClass.getValues().get(index)) + "'");
                index++;
            }

            int i = fsqlClass.getColumns().indexOf(filter);
            if (i == -1 || fsqlClass.getValues().get(i) == null) {
                throw new RuntimeException("filter does not exist!!");
            }

            Statement stmt = con.createStatement();
            stmt.execute("UPDATE " + fsqlClass.getTable() + " SET " + StringUtils.joinWithSerialComma(changes) + " WHERE " + filter + " = '" + FSQL.MODDED_GSON.toJson(fsqlClass.getValues().get(i)) + "';");
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> Object fetch(String filter, T t) {
        try {

            FSQLClass fsqlClass = FSQLClass.fromClass(t);
            Class clazz = t instanceof Class ? (Class) t : t.getClass();

            if (fsqlClass != null) {
                Object instance = clazz.newInstance();

                int i = fsqlClass.getColumns().indexOf(filter);
                if (i == -1 || fsqlClass.getValues().get(i) == null)
                    throw new RuntimeException("filter does not exist!!");


                Statement stmt = con.createStatement();
                ResultSet set = stmt.executeQuery("SELECT * FROM " + fsqlClass.getTable() + (filter == null ? "" : " WHERE " + filter + " = '" + FSQL.MODDED_GSON.toJson(fsqlClass.getValues().get(i))) + "';");

                for (Field field : clazz.getFields()) {
                    if (!field.isAnnotationPresent(Column.class)) continue;

                    field.set(clazz, FSQL.MODDED_GSON.fromJson(set.getString(field.getAnnotation(Column.class).value()), field.get(clazz).getClass()));
                }

                stmt.close();

                return instance;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
