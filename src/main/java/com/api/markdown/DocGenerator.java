package com.api.markdown;

import com.api.markdown.model.*;
import com.api.markdown.sample.OpenDemoService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author 飞狐 on 2019/04/23
 */
public class DocGenerator {


    public static void main(String[] args) throws IOException {
        DocGenerator.printMdText(OpenDemoService.class);
    }

    /**
     * 生成一个文件
     *
     * @param clazz 你的接口
     */
    public static void buildAsFile(Class<?> clazz) throws IOException {

        File directory = new File(System.getProperty("user.home") + "/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory.getAbsolutePath() + "/" + clazz.getSimpleName() + ".md");
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(file);

        generate(clazz, out);
        out.flush();
        out.close();

        System.out.println(file.getAbsolutePath());
    }

    /**
     * 直接将md内容打印在控制台
     *
     * @param clazz 你的接口
     */
    public static void printMdText(Class<?> clazz) throws IOException {
        generate(clazz, System.out);
    }

    /**
     * 直接将md内容打印在控制台
     *
     * @param clazz 你的接口
     */
    public static void printMdText(List<Class<?>> clazzs) throws IOException {
        generate(clazzs, System.out);
    }



    public static void generate(Class<?> clazz, OutputStream out) throws IOException {
        ApiGroup export = new Exporter(clazz).export();

        String prefix = md5(export.getType());
        List<MethodApi> apis = export.getApis();
        for (int index = 0; index < apis.size(); index++) {
            dealMethod(apis.get(index), prefix, index + 1, out);
        }
    }

    public static void generate(List<Class<?>> clazzs, OutputStream out) throws IOException {
        List<ApiGroup> apiGroups = new ArrayList<>();
        clazzs.forEach(item -> {
            ApiGroup export = new Exporter(item).export();
            apiGroups.add(export);
        });
        for (int i = 0; i < apiGroups.size(); i++) {
            ApiGroup export = apiGroups.get(i);
            String prefix = md5(export.getType());
            List<MethodApi> apis = export.getApis();
            out.write(println("## "  + export.getGroup()));
            for (int index = 0; index < apis.size(); index++) {
                dealMethod(apis.get(index), prefix, index + 1, out);
            }
        }


    }

    private static void dealMethod(MethodApi api, String prefix, int index, OutputStream out) throws IOException {
        prefix = md5(prefix + api.getName());

        out.write(println("### " + " " + index + "." + api.getDesc()));
        out.write(println("#### API路由地址"));
        out.write(println("> " + api.getApiName()));
        out.write(println());
        out.write(println("#### 请求参数列表"));
        dealParameter(api, prefix, out);
        out.write(println());
        out.write(println("#### 返回参数"));
        dealReturnType(api, prefix, out);
        out.write(println("#### 参数详情"));
        dealModels(api.getModels(), prefix, out);
        out.write(println("\n---"));
    }

    private static void dealReturnType(MethodApi api, String prefix, OutputStream out) throws IOException {
        String type = type(api.getModels(), prefix, api.getReturnType());
        out.write(println(type));
    }


    private static void dealParameter(MethodApi api, String prefix, OutputStream out) throws IOException {
        List<Parameter> parameters = api.getParameters();
        out.write(println());
        out.write(println("|参数名|参数类型|必填|说明|"));
        out.write(println("|---|---|---|---|"));
        parameters.sort(Comparator.comparingInt(Parameter::getIndex));
        for (Parameter parameter : parameters) {
            StringBuilder builder = new StringBuilder("|");
            // 参数名
            builder.append(parameter.getName()).append("|");
            // 参数类型
            builder.append(type(api.getModels(), prefix, parameter)).append("|");
            // 必填
            builder.append(parameter.isRequired()).append("|");
            // 说明
            builder.append(parameter.getDescription()).append("|");
            out.write(println(builder.toString()));
        }
    }

    private static void dealModels(Map<String, Model> models, String prefix, OutputStream out) throws IOException {
        for (Map.Entry<String, Model> entry : models.entrySet()) {
            dealModel(models, prefix, entry.getValue(), out);
        }
    }

    private static void dealModel(Map<String, Model> models, String prefix, Model model, OutputStream out) throws IOException {
        out.write(println());
        out.write(println("> " + model.getDescription()));
        out.write(println());
        out.write(println("**<span id=\"" + prefix + md5(model.getTypeName()) + "\">" + model.getName() + "</span>**"));
        out.write(println());
        out.write(println("|字段名|字段类型|必填|说明|"));
        out.write(println("|---|---|---|---|"));
        for (ModelProperty property : model.getProperties()) {
            if (property.isHidden())
                continue;
            StringBuilder builder = new StringBuilder("|");
            // 参数名
            builder.append(property.getName()).append("|");
            // 参数类型
            builder.append(type(models, prefix, property)).append("|");
            // 必填
            builder.append(property.isRequired()).append("|");
            // 说明
            builder.append(property.getDescription()).append("|");

            out.write(println(builder.toString()));
        }
        out.write(println());
    }

    //获取字段类型
    public static String getTypeName(Map<String, Model> models, String typeId) {
        Model model = models.get(typeId);
        if (model.getName() == null || model.getName().trim().length() <= 0) {
            //没有设置别名
            String[] temp = model.getTypeName().split(".");
            return temp[temp.length - 1];
        } else {
            return model.getName();
        }

    }

    private static String type(Map<String, Model> models, String prefix, Member member) {
        DataType memberDataType = member.getDataType();
        if (DataType.OBJECT.equals(memberDataType)) {
            return "[" + getTypeName(models, member.getTypeName()) + "]" +
                    "(#" + prefix + md5(member.getTypeName()) + ")";
        }
        if (DataType.ARRAY.equals(memberDataType)) {
            Model model = models.get(member.getTypeName());
            if (null != model) {
                return "Array&lt;" + type(models, prefix, model) + "&gt;";
            } else {
                return "Array&lt;" + member.getTypeName() + "&gt;";
            }
        }
        if (DataType.MAP.equals(memberDataType)) {
            String key = member.getTypeName();
            String val = member.getTypeValue();

            Model kModel = models.get(key);
            Model vModel = models.get(val);

            String k;
            String v;
            if (null == kModel) {
                k = key;
            } else {
                k = type(models, prefix, kModel);
            }
            if (null == vModel) {
                v = val;
            } else {
                v = type(models, prefix, vModel);
            }

            return "Map&lt;" + k + "," + v + "&gt;";
        }

        return memberDataType.toString();
    }


    private static byte[] println(String... msg) {
        if (msg.length <= 0) {
            return "\n".getBytes(StandardCharsets.UTF_8);
        }
        return (msg[0] + "\n").getBytes(StandardCharsets.UTF_8);
    }

    private final static char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        int t;
        for (int i = 0; i < bytes.length; i++) {
            t = bytes[i];
            if (t < 0)
                t += 256;
            sb.append(hexDigits[(t >>> 4)]);
            sb.append(hexDigits[(t % 16)]);
        }
        return sb.toString();
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(System.getProperty("MD5.algorithm", "MD5"));
            return bytesToHex(md.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
