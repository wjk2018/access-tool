package com.cnbi.util;

import sun.misc.Unsafe;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName MetaObjectUtil
 * @Description 反射相关工具类
 * @Author Wangjunkai
 * @Date 2020/5/25 11:51
 **/

public class MetaObjectUtil {

    private static Unsafe unsafe = null;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static void setObjectField(Object obj, Object value, String field) throws NoSuchFieldException {
        long l = unsafe.objectFieldOffset(obj.getClass().getDeclaredField(field));
        unsafe.getAndSetObject(obj, l, value);
    }

    public static void invokeMathod(Object obj, String methodName, Class[] valueType, Object[] value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        obj.getClass().getMethod(methodName, valueType).invoke(obj, value);
    }

    public static Object getObjectValue(Object obj, String field) throws NoSuchFieldException {
        long l = unsafe.objectFieldOffset(obj.getClass().getDeclaredField(field));
        return unsafe.getObject(obj, l);
    }
}