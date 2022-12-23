package com.github.ynverxe.mmts.common;

import java.util.Map;

@SuppressWarnings("rawtypes")
public final class HierarchyMapSearchUtil {

    private HierarchyMapSearchUtil() {
        throw new UnsupportedOperationException("cannot instantiate this class");
    }

    public static <V> V findBySuperclasses(Class clazz, Map<Class, V> map) {
        Class parent = clazz;
        V value = findByInterfaces(clazz, map);

        while ((parent = parent.getSuperclass()) != null && value == null) {
            value = map.get(parent);

            if (value == null) value = findByInterfaces(parent, map);
        }

        return value;
    }

    public static  <V> V findByInterfaces(Class clazz, Map<Class, V> map) {
        V value = null;

        Class[] interfaces = clazz.getInterfaces();

        for (int i = 0; i < interfaces.length && value == null; i++) {
            Class anInterface = interfaces[i];

            value = map.get(anInterface);

            if (value == null) value = findByInterfaces(anInterface, map);
        }

        return value;
    }

}