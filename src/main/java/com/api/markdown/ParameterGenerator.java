package com.api.markdown;

import com.alibaba.fastjson.JSON;
import com.api.markdown.model.*;
import com.api.markdown.sample.OpenDemoService;
import com.api.markdown.vo.ModelVO;
import com.api.markdown.vo.ParameterVO;
import com.api.markdown.vo.ReturnTypeVO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author 飞狐 on 2019/05/05
 */
public class ParameterGenerator {


    public static void generate(Class<?> clazz) {
        ApiGroup export = new Exporter(clazz).export();
        export.getApis().forEach(ParameterGenerator::parseMethod);
    }

    private static void parseMethod(MethodApi method) {
        String name = method.getName();


        Map<String, Model> models = method.getModels();
        List<ParameterVO> vos = new ArrayList<>();
        method.getParameters().sort(Comparator.comparingInt(Parameter::getIndex));
        method.getParameters().forEach(parameter -> {
            ParameterVO vo = new ParameterVO();
            vo.setName(parameter.getName());
            vo.setClassName(parameter.getClassName());
            vo.setNotes(parameter.getDescription());

            ModelVO mo = parseModel(parameter, models);
            mo.setName(parameter.getName());
            mo.setNotes(parameter.getDescription());
            mo.setRequire(1);

            vo.setModel(mo);
            vos.add(vo);
        });
        System.out.println(name + ":");
        System.out.println("参数:\n" + JSON.toJSONString(vos));

        ReturnType returnType = method.getReturnType();
        ReturnTypeVO returnTypeVO = new ReturnTypeVO();
        returnTypeVO.setClassName(returnType.getClassName());
        returnTypeVO.setModel(parseModel(returnType, models));

        System.out.println("返回值:\n" + JSON.toJSONString(returnTypeVO));
    }

    private static ModelVO parseModel(Member member, Map<String, Model> models) {
        ModelVO vo = new ModelVO();
        vo.setClassName(member.getClassName());


        DataType memberDataType = member.getDataType();
        if (DataType.OBJECT.equals(memberDataType)) {
            vo.setType(0);
            Model model = models.get(member.getTypeName());

            List<ModelVO> properties = new ArrayList<>();
            if (!Types.isJavaType(member.getClassName())) {
                model.getProperties().forEach(property -> {
                    ModelVO p = parseModel(property, models);
                    p.setName(property.getName());
                    p.setNotes(property.getDescription());
                    p.setRequire(property.isRequired() ? 1 : 0);
                    properties.add(p);
                });
            }
            vo.setProperties(properties);

            return vo;
        }
        if (DataType.ARRAY.equals(memberDataType)) {
            vo.setType(-4);
        }
        if (DataType.MAP.equals(memberDataType)) {
            vo.setType(0);
        }
        if (DataType.BOOLEAN.equals(memberDataType)) {
            vo.setType(-1);
        }
        if (DataType.NUMBER.equals(memberDataType)) {
            vo.setType(-2);
        }
        if (DataType.STRING.equals(memberDataType) || DataType.ENUM.equals(memberDataType)) {
            vo.setType(-3);
        }
        return vo;
    }

    public static void main(String[] args) {
        generate(OpenDemoService.class);

    }
}
