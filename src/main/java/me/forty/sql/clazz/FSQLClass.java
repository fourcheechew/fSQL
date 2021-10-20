package me.forty.sql.clazz;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.forty.sql.annotation.Column;
import me.forty.sql.annotation.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2021 - Tranquil, LLC.
 *
 * @author 42 on Oct, 19, 2021 - 10:23 PM
 * @project fSQL
 */

@Data
public class FSQLClass {

    @Getter private static List<FSQLClass> registeredClasses = new ArrayList<FSQLClass>();

    private Class actualClass;
    private String table;
    private List<String> columns = new ArrayList<String>();
    private List<Object> values = new ArrayList<Object>();

    public static FSQLClass fromClass(Object object) {

        Class clazz = object.getClass();

        try {
            if (!clazz.isAnnotationPresent(Table.class)) {
                throw new IllegalAccessException("Class must contain the @Table annotation to be inserted into sql.");
            }
            if (!clazz.isAnnotationPresent(NoArgsConstructor.class)) {
                throw new IllegalAccessException("Class must contain the @NoArgsConstructor annotation to be inserted into sql.");
            }

            FSQLClass fsqlClass = new FSQLClass();

            for (Field field : clazz.getFields()) {
                if (!field.isAnnotationPresent(Column.class)) continue;
                if (!field.isAccessible()) field.setAccessible(true);

                Column column = field.getAnnotation(Column.class);
                fsqlClass.getColumns().add(column.value());
                fsqlClass.getValues().add(field.get(clazz));
            }

            fsqlClass.setTable(((Table)clazz.getAnnotation(Table.class)).value());
            fsqlClass.setActualClass(clazz);

            if (!FSQLClass.getRegisteredClasses().contains(fsqlClass)) {
                FSQLClass.getRegisteredClasses().add(fsqlClass);
            }

            return fsqlClass;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
