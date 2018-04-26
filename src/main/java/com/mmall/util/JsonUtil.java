package com.mmall.util;

import com.mmall.pojo.User;
import jdk.internal.org.objectweb.asm.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //including all property of object
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);

        //cancel the auto date transforming
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS,false);

        //ignore null object error
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

        //unify the format of all date
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //ignore the un match property circumstance
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static <T> String obj2String(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof  String ? (String) obj: objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Parse object to string error");
            return null;
        }
    }

    public static <T> String obj2StringPretty(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof  String ? (String) obj: objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Parse object to string error");
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class<T> clazz){
        if(StringUtils.isEmpty(str) || clazz == null){
            return null;
        }
        try {
            return clazz.equals(String.class)?(T)str:objectMapper.readValue(str,clazz);
        } catch (Exception e) {
            log.warn("Parse string to object error");
            return null;
        }
    }

    public static <T> T string2Obj(String str, org.codehaus.jackson.type.TypeReference<T> typeReference){
        if(StringUtils.isEmpty(str) || typeReference == null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)?str:objectMapper.readValue(str,typeReference));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class collectionClass,Class... elementClasses){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[]args){
        User u1 = new User();
        u1.setId(1);
        u1.setEmail("www.123@gmail.com");
        User u2 = new User();
        u2.setId(2);
        u2.setEmail("User2@gmail.com");
        List<User> userList = new ArrayList<>();
        userList.add(u1);
        userList.add(u2);

        String userListString = JsonUtil.obj2StringPretty(userList);
        String userJson = JsonUtil.obj2String(u1);

        log.info("userjson: {}",userListString);
        List<User> reList = JsonUtil.string2Obj(userListString,List.class,User.class);
        System.out.println(reList.get(0));
        System.out.println(reList.get(1));

    }


}
