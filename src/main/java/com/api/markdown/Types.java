/*
 *
 *  Copyright 2015-2016 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package com.api.markdown;

import com.fasterxml.classmate.ResolvedType;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.LocalGregorianCalendar;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class Types {
    private static final Set<String> TYPE_LOOKUP = new HashSet<>(
            Arrays.asList(Date.class.getName(),
                    java.sql.Date.class.getName(),
                    String.class.getName(),
                    Character.class.getName(),
                    Object.class.getName(),
                    Object.class.getName(),
                    Long.class.getName(),
                    Integer.class.getName(),
                    Short.class.getName(),
                    Double.class.getName(),
                    Float.class.getName(),
                    Boolean.class.getName(),
                    Byte.class.getName(),
                    BigDecimal.class.getName(),
                    BigInteger.class.getName(),
                    Currency.class.getName(),
                    UUID.class.getName(),
                    boolean.class.getName(),
                    byte.class.getName(),
                    char.class.getName(),
                    short.class.getName(),
                    int.class.getName(),
                    long.class.getName(),
                    float.class.getName(),
                    double.class.getName()
            ));

    private static final Set<Class<?>> numbers = new HashSet<>(Arrays.asList(
            byte.class,
            short.class,
            int.class,
            long.class,
            float.class,
            double.class
    ));

    public static Object number(String className, String value) {
        if (byte.class.getName().equals(className) || Byte.class.getName().equals(className)) {
            return Byte.valueOf(value);
        }
        if (short.class.getName().equals(className) || Short.class.getName().equals(className)) {
            return Short.valueOf(value);
        }
        if (int.class.getName().equals(className) || Integer.class.getName().equals(className)) {
            return Integer.valueOf(value);
        }
        if (long.class.getName().equals(className) || Long.class.getName().equals(className)) {
            return Long.valueOf(value);
        }
        if (float.class.getName().equals(className) || Float.class.getName().equals(className)) {
            return Float.valueOf(value);
        }
        if (double.class.getName().equals(className) || Double.class.getName().equals(className)) {
            return Double.valueOf(value);
        }
        return null;
    }

    //获取Number的详细类型
    public static Object getNumberDetailTypeName(String className) {
        if (byte.class.getName().equals(className) || Byte.class.getName().equals(className)) {
            return Byte.class.getName();
        }
        if (short.class.getName().equals(className) || Short.class.getName().equals(className)) {
            return Short.class.getName();
        }
        if (int.class.getName().equals(className) || Integer.class.getName().equals(className)) {
            return Integer.class.getName();
        }
        if (long.class.getName().equals(className) || Long.class.getName().equals(className)) {
            return Long.class.getName();
        }
        if (float.class.getName().equals(className) || Float.class.getName().equals(className)) {
            return Float.class.getName();
        }
        if (double.class.getName().equals(className) || Double.class.getName().equals(className)) {
            return Double.class.getName();
        }
        if (BigDecimal.class.getName().equals(className)) {
            return BigDecimal.class.getName();
        }
        if (BigInteger.class.getName().equals(className)) {
            return BigInteger.class.getName();
        }
        return null;
    }

    public static boolean isNumber(Class<?> clazz) {
        return numbers.contains(clazz) || Number.class.isAssignableFrom(clazz);
    }

    public static boolean isDate(Class<?> clazz) {
        return Date.class.isAssignableFrom(clazz) ||
                java.sql.Date.class.isAssignableFrom(clazz) ||
                BaseCalendar.Date.class.isAssignableFrom(clazz) ||
                LocalGregorianCalendar.Date.class.isAssignableFrom(clazz);
    }

    public static boolean isBoolean(Class<?> clazz) {
        return boolean.class.equals(clazz) || Boolean.class.equals(clazz);
    }

    public static boolean isByte(Class<?> clazz) {
        return byte.class.equals(clazz) || Byte.class.equals(clazz);
    }

    public static boolean isShort(Class<?> clazz) {
        return short.class.equals(clazz) || Short.class.equals(clazz);
    }

    public static boolean isInteger(Class<?> clazz) {
        return int.class.equals(clazz) || Integer.class.equals(clazz);
    }

    public static boolean isLong(Class<?> clazz) {
        return long.class.equals(clazz) || Long.class.equals(clazz);
    }

    public static boolean isFloat(Class<?> clazz) {
        return float.class.equals(clazz) || Float.class.equals(clazz);
    }

    public static boolean isDouble(Class<?> clazz) {
        return double.class.equals(clazz) || Double.class.equals(clazz);
    }

    public static boolean isBigDecimal(Class<?> clazz) {
        return BigDecimal.class.equals(clazz);
    }

    public static boolean isBigInteger(Class<?> clazz) {
        return BigInteger.class.equals(clazz);
    }

    public static boolean isString(Class<?> clazz) {
        return CharSequence.class.isAssignableFrom(clazz) || Character.class.isAssignableFrom(clazz);
    }

    public static boolean isVoid(Class<?> clazz) {
        return Void.class.equals(clazz);
    }

    public static boolean isBaseType(ResolvedType type) {
        return isBaseType(type.getBriefDescription());
    }

    public static boolean isBaseType(String name) {
        return TYPE_LOOKUP.contains(name);
    }

    public static boolean isJavaType(Class<?> type) {
        return type != null && type.getClassLoader() == null;
    }


    public static boolean isJavaType(String typeName) {
        try {
            return isJavaType(Class.forName(typeName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
