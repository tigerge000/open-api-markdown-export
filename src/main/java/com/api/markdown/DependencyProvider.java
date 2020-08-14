package com.api.markdown;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 飞狐 on 2019/04/15
 */
public class DependencyProvider {

    private static <T extends Collection> ResolvedType elementType(ResolvedType container, Class<T> collectionType) {
        List<ResolvedType> resolvedTypes = container.typeParametersFor(collectionType);
        if (resolvedTypes.size() == 1) {
            return resolvedTypes.get(0);
        }
        return new TypeResolver().resolve(Object.class);
    }

    public static ResolvedType mapValueType(ResolvedType container) {
        List<ResolvedType> resolvedTypes = container.typeParametersFor(Map.class);
        if (null != resolvedTypes && resolvedTypes.size() == 2) {
            return resolvedTypes.get(1);
        }
        return null;
    }

    public static ResolvedType mapKeyType(ResolvedType resolvedType) {
        List<ResolvedType> resolvedTypes = resolvedType.typeParametersFor(Map.class);
        if (null != resolvedTypes && resolvedTypes.size() == 2) {
            return resolvedTypes.get(0);
        }
        return null;
    }

    public static ResolvedType collectionElementType(ResolvedType type) {
        if (List.class.isAssignableFrom(type.getErasedType())) {
            return elementType(type, List.class);
        } else if (Set.class.isAssignableFrom(type.getErasedType())) {
            return elementType(type, Set.class);
        } else if (type.isArray()) {
            return type.getArrayElementType();
        } else if ((Collection.class.isAssignableFrom(type.getErasedType()) && !isMapType(type.getErasedType()))) {
            return elementType(type, Collection.class);
        } else {
            return null;
        }
    }

    public static boolean isContainerType(Class<?> type) {
        return isCollection(type) || isArray(type);
    }

    public static boolean isMapType(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }

    public static boolean isCollection(Class<?> type) {
        return List.class.isAssignableFrom(type) ||
                Set.class.isAssignableFrom(type) ||
                (Collection.class.isAssignableFrom(type) && !isMapType(type));
    }

    public static boolean isArray(Class<?> type) {
        return type.isArray();
    }

    public static ResolvedType resolve(ResolvedType resolvedType, boolean fq) {
        if (null == resolvedType) {
            return null;
        }

        List<ResolvedType> typeParameters = resolvedType.getTypeParameters();
        if (null != typeParameters && !typeParameters.isEmpty()) {
            ResolvedType rt = typeParameters.get(0);
            if (fq) {
                ResolvedType fqRt = resolve(rt, true);
                if (null == fqRt) {
                    return rt;
                } else {
                    return fqRt;
                }
            } else {
                return rt;
            }
        }
        return null;
    }
}
