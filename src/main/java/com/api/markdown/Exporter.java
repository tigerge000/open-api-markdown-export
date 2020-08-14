package com.api.markdown;


import com.api.markdown.model.*;
import com.api.markdown.model.Model;
import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMethod;
import io.swagger.annotations.*;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.api.markdown.DependencyProvider.*;
import static jdk.nashorn.internal.runtime.ScriptObject.isArray;

/**
 *
 */
public class Exporter {
    private final TypeResolver typeResolver = new TypeResolver();
    private final Class<?> exportedInterface;
    private final ResolvedMethod[] resolvedMethods;

    public Exporter(Class<?> exportedInterface) {
        if (null == exportedInterface || !exportedInterface.isInterface()) {
            throw new RuntimeException("Not match interface");
        }
        this.exportedInterface = exportedInterface;
        ResolvedType resolvedInterface = typeResolver.resolve(this.exportedInterface);
        MemberResolver memberResolver = new MemberResolver(typeResolver);
        memberResolver.setIncludeLangObject(false);
        ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(resolvedInterface, null, null);
        resolvedMethods = typeWithMembers.getMemberMethods();
        if (null == resolvedMethods || resolvedMethods.length <= 0) {
            throw new RuntimeException("Not match method");
        }
        exportedInterface.getDeclaredMethods();
    }


    public ApiGroup export() {
        ApiGroup apiGroup = new ApiGroup();
        apiGroup.setType(exportedInterface.getName());
        apiGroup.setGroup(resolveGroup(exportedInterface.getAnnotation(Api.class)));
        List<MethodApi> apis = new ArrayList<>();
        for (ResolvedMethod method : resolvedMethods) {
            MethodApi methodApi = buildMethodApi(method);
            if (null != methodApi) {
                apis.add(methodApi);
            }
        }
        apiGroup.setApis(apis);
        simpleModels(apiGroup);
        return apiGroup;
    }


    private static void simpleModels(ApiGroup group) {
        group.getApis().forEach(api -> {
            Map<String, Model> models = api.getModels();
            ReturnType returnType = api.getReturnType();
            Map<String, Model> using = new HashMap<>();
            parseMember(models, using, returnType);
            api.getParameters().forEach(parameter -> parseMember(models, using, parameter));

            api.setModels(using);
        });
    }


    private static void parseMember(Map<String, Model> models, Map<String, Model> using, Member member) {
        if (DataType.MAP.equals(member.getDataType())) {
            parseModel(models, using, member.getTypeName());
            parseModel(models, using, member.getTypeValue());
        } else if (DataType.ARRAY.equals(member.getDataType())
                || DataType.ENUM.equals(member.getDataType())
                || DataType.OBJECT.equals(member.getDataType())) {
            parseModel(models, using, member.getTypeName());
        }
    }

    private static void parseModel(Map<String, Model> models, Map<String, Model> using, String typeName) {
        if (using.containsKey(typeName)) {
            return;
        }

        Model model = models.get(typeName);

        if (null == model) {
            return;
        }
        using.put(typeName, model);
        parseMember(models, using, model);
        List<ModelProperty> properties = model.getProperties();
        if (null != properties) {
            properties.forEach(property -> parseMember(models, using, property));
        }
    }

    private MethodApi buildMethodApi(ResolvedMethod resolvedMethod) {
        if (resolvedMethod.isStatic()) {
            return null;
        }

        MethodApi methodApi = new MethodApi();
        methodApi.setName(resolvedMethod.getName());
        String abbreviate = NameAbbreviator.abbreviate(resolvedMethod.getDeclaringType().getErasedType().getName()).toLowerCase();
        if (abbreviate.endsWith("service")) {
            abbreviate = abbreviate.substring(0, abbreviate.indexOf("service"));
        }
        ApiOperation apiOperation = resolvedMethod.getRawMember().getAnnotation(ApiOperation.class);
        //如果存在httpMethod注解，就设置为路由地址
        methodApi.setApiName(apiOperation.httpMethod() == null ? abbreviate + "#" + resolvedMethod.getName().toLowerCase() : apiOperation.httpMethod());

        if (null != apiOperation) {
            methodApi.setDesc(apiOperation.value());
        }

        Map<String, Model> models = new HashMap<>();

        // 返回值
        methodApi.setReturnType(resolveReturnType(models, resolvedMethod.getReturnType()));
        // 参数
        methodApi.setParameters(resolveParameter(models, resolvedMethod));
        // 用到的模型
        methodApi.setModels(models);
        return methodApi;
    }

    //解析返回值参数
    private ReturnType resolveReturnType(Map<String, Model> models, ResolvedType returnType) {
//        if (!isReturnTypeInstanceOfPlainResult(returnType)) {
//            throw new IllegalArgumentException("返回类型不是指定类型类型");
//        }
        ReturnType rtt = new ReturnType();

        if(!CollectionUtils.isEmpty(returnType.getTypeBindings().getTypeParameters())){
            ResolvedType innerType = returnType.getTypeBindings().getTypeParameters().get(0);
            returnType = innerType;
        }

        initMember(rtt, returnType);
        resolveModels(models, returnType);
        return rtt;
    }



    //判断返回类型是不是PlainResult类型
    private boolean isReturnTypeInstanceOfPlainResult(ResolvedType returnType) {
        String className = returnType.getErasedType().getName();
        return className.equals(Constants.PLAIN_RESULT);

    }


    //解析apiModel类型
    private void resolveModels(Map<String, Model> models, ResolvedType resolvedType) {
        if (null == resolvedType) {
            return;
        }
        if (isMapType(resolvedType.getErasedType())) {
            resolveModels(models, DependencyProvider.mapValueType(resolvedType));
            resolveModels(models, DependencyProvider.mapKeyType(resolvedType));
        }
        if (isArray(resolvedType.getErasedType()) || resolvedType.isArray()) {
            resolveModels(models, resolvedType.getArrayElementType());
        }
        if (isCollection(resolvedType.getErasedType())) {
            resolveModels(models, collectionElementType(resolvedType));
        }

        Class<?> type = resolvedType.getErasedType();
        String typeName = NameAbbreviator.abbreviate(resolvedType.getBriefDescription());
        //如果已经被model解析过或者是私有类型或者是基础类型
        if (models.containsKey(typeName) || resolvedType.isPrimitive() || Types.isBaseType(resolvedType)) {
            return;
        }

        Model model = new Model();
        models.put(typeName, model);
        initMember(model, resolvedType);
        DataType dataType = model.getDataType();

        ApiModel apiModel = type.getAnnotation(ApiModel.class);
        if (apiModel != null) {
            //TODO 这里可以设置默认值
            model.setDescription(apiModel.description());
            model.setName(apiModel.value());
        }

        String modelName = model.getName();
        if (null == modelName || modelName.trim().length() <= 0) {
            model.setName(type.getSimpleName());
        }

        if (DataType.OBJECT.equals(dataType)) {
            model.setProperties(resolveModelProperty(models, resolvedType));
        }

        resolveModels(models, DependencyProvider.resolve(resolvedType, false));
    }

    //解析Object类型的成员
    private List<ModelProperty> resolveModelProperty(Map<String, Model> models, ResolvedType clazz) {
        List<ModelProperty> properties = new ArrayList<>();

        MemberResolver memberResolver = new MemberResolver(typeResolver);
        memberResolver.setIncludeLangObject(false);
        ResolvedTypeWithMembers typeWithMembers = memberResolver.resolve(clazz, null, null);
        ResolvedField[] fields = typeWithMembers.getMemberFields();

        for (ResolvedField resolvedField : fields) {
            //获取Member的class类型
            ResolvedType fieldResolvedType = resolvedField.getType();
            ModelProperty property = new ModelProperty();
            initMember(property, fieldResolvedType);

            Field field = resolvedField.getRawMember();
            property.setName(field.getName());

            ApiModelProperty fieldAnnotation = field.getAnnotation(ApiModelProperty.class);
            if (null != fieldAnnotation) {
                property.setRequired(fieldAnnotation.required());
                property.setHidden(fieldAnnotation.hidden());
                String description = fieldAnnotation.value();
                if (description.trim().length() <= 0) {
                    description = fieldAnnotation.notes();
                }
                property.setDescription(description);
            }
            properties.add(property);
            //enum类型单独处理
            if (!property.getDataType().equals(DataType.ENUM)) {
                resolveModels(models, fieldResolvedType);
            }

        }
        return properties;
    }

    private static boolean notAnyMore(Member member) {
        return DataType.NUMBER.equals(member.getDataType())
                || DataType.ENUM.equals(member.getDataType())
                || DataType.BOOLEAN.equals(member.getDataType())
                || DataType.STRING.equals(member.getDataType());
    }


    private static void initMember(Member member, ResolvedType resolvedType) {
        member.setTypeName(getTypeName(resolvedType));
        member.setClassName(resolvedType.getErasedType().getName());
        member.setDataType(deduceDataType(resolvedType));
        if (DataType.MAP.equals(member.getDataType())) {
            member.setTypeName(getTypeName(DependencyProvider.mapKeyType(resolvedType)));
            member.setTypeValue(getTypeName(DependencyProvider.mapValueType(resolvedType)));
        }
        if (DataType.ARRAY.equals(member.getDataType())) {
            member.setTypeName(getTypeName(collectionElementType(resolvedType)));
        }
    }



    private static String getTypeName(ResolvedType resolvedType) {
        if (Types.isBaseType(resolvedType)) {
            return deduceDataType(resolvedType).toString();
        }
        return NameAbbreviator.abbreviate(resolvedType.getBriefDescription());
    }

    //推断
    private static DataType deduceDataType(ResolvedType resolvedType) {
        if (isCollection(resolvedType.getErasedType())
                || resolvedType.isArray() || DependencyProvider.isArray(resolvedType.getErasedType())) {
            return DataType.ARRAY;
        }
        if (Types.isVoid(resolvedType.getErasedType())) {
            return DataType.VOID;
        }
        if (isMapType(resolvedType.getErasedType())) {
            return DataType.MAP;
        }
        if (resolvedType.getErasedType().isEnum()) {
            return DataType.ENUM;
        }
        if (Types.isNumber(resolvedType.getErasedType())) {
            DataType dataType = deduceNumberDeatilType(resolvedType.getErasedType());
            if (dataType == null)
                return DataType.NUMBER;
            else
                return dataType;
        }
        if (Types.isBoolean(resolvedType.getErasedType())) {
            return DataType.BOOLEAN;
        }
        if (Types.isString(resolvedType.getErasedType())) {
            return DataType.STRING;
        } else if (Types.isDate(resolvedType.getErasedType())) {
            return DataType.DATE;
        }
        return DataType.OBJECT;
    }

    /**
     * 判断Number的具体类型
     * @param clazz
     * @return
     */
    private static DataType deduceNumberDeatilType(Class<?> clazz) {
        if (Types.isShort(clazz)) {
            return DataType.SHORT;
        } else if (Types.isByte(clazz)) {
            return DataType.BYTE;
        } else if (Types.isInteger(clazz)) {
            return DataType.Integer;
        } else if (Types.isLong(clazz)) {
            return DataType.Long;
        } else if (Types.isFloat(clazz)) {
            return DataType.Float;
        } else if (Types.isDouble(clazz)) {
            return DataType.DOUBLE;
        } else if (Types.isBigDecimal(clazz)) {
            return DataType.BIG_DECIMAL;
        } else if (Types.isBigInteger(clazz)) {
            return DataType.BIG_INTEGER;
        } else
            return null;
    }

    private List<Parameter> resolveParameter(Map<String, Model> models, ResolvedMethod resolvedMethod) {
        List<Parameter> parameters = new ArrayList<>();

        // FIXME Java8 -parameter
        String defaultParameterName = "arg";


        int argumentCount = resolvedMethod.getArgumentCount();
        Method method = resolvedMethod.getRawMember();
        ApiImplicitParam apiImplicitParam = method.getAnnotation(ApiImplicitParam.class);
        ApiImplicitParams apiImplicitParams = method.getAnnotation(ApiImplicitParams.class);
        boolean apiImplicitParamsEnable = null != apiImplicitParams && apiImplicitParams.value().length == argumentCount;
        boolean apiImplicitParamEnable = null != apiImplicitParam && argumentCount == 1;


        for (int i = 0; i < argumentCount; ++i) {
            ResolvedType argumentType = resolvedMethod.getArgumentType(i);
            Parameter parameter = new Parameter();
            initMember(parameter, argumentType);
            parameter.setIndex(i);
            resolveModels(models, argumentType);

            ApiParam apiParam = getParameterAnnotation(ApiParam.class, resolvedMethod.getRawMember(), i);
            if (null != apiParam) {
                parameter.setName(apiParam.name());
                parameter.setDescription(apiParam.value());
                parameter.setRequired(apiParam.required());
            } else if (apiImplicitParamEnable || apiImplicitParamsEnable) {
                ApiImplicitParam implicitParam = apiImplicitParam;
                if (apiImplicitParamsEnable) {
                    implicitParam = apiImplicitParams.value()[i];
                }
                if (null != implicitParam) {
                    parameter.setName(implicitParam.name());
                    parameter.setDescription(implicitParam.value());
                }
            }
            if (null == parameter.getName() || parameter.getName().trim().length() <= 0) {
                parameter.setName(defaultParameterName + i);
            }

            parameters.add(parameter);
        }

        return parameters;
    }

    private static String resolveGroup(Api apiAnnotation) {
        String name = null;
        if (null != apiAnnotation) {
            String[] tags = apiAnnotation.tags();
            if (tags.length > 0) {
                name = tags[0];
            }
            if (null == name || name.trim().length() <= 0) {
                String value = apiAnnotation.value();
                if (!value.isEmpty()) {
                    name = value.trim();
                }
            }
        }
        if (null == name || name.trim().length() <= 0) {
            name = ApiGroup.DEFAULT_GROUP;
        }

        return name;
    }

    private static <A extends Annotation> A getParameterAnnotation(Class<A> annotationType, Method method, int index) {
        Annotation[] anns = getParameterAnnotations(method, index);
        if (null == anns) {
            return null;
        }
        for (Annotation ann : anns) {
            if (annotationType.isInstance(ann)) {
                return (A) ann;
            }
        }
        return null;
    }

    public static Annotation[] getParameterAnnotations(Method method, int parameterIndex) {
        Annotation[][] annotationArray = method.getParameterAnnotations();

        return (parameterIndex >= 0 && parameterIndex < annotationArray.length ?
                (annotationArray[parameterIndex]) : null);
    }

    interface Constants {

        String PLAIN_RESULT = "";

    }
}
