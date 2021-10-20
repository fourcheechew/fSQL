package me.forty.sql.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright (c) 2021 - Tranquil, LLC.
 *
 * @author 42 on Oct, 19, 2021 - 9:12 PM
 * @project fSQL
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

    String value();

}
