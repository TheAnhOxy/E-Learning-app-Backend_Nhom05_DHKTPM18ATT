package com.elearning.utils;

import java.util.Map;

public class MapUtil {

    public static <T> T getObject(Map<String, ?> params, String key, Class<T> tClass){
        Object obj = params.getOrDefault(key, null);
        if(obj != null && !obj.toString().isEmpty()){
            try {
                if (tClass.equals(Long.class)) {
                    return tClass.cast(Long.parseLong(obj.toString()));
                }else if(tClass.equals(Integer.class)) {
                    return tClass.cast(Integer.parseInt(obj.toString()));
                }else if(tClass.equals(Double.class)) {
                    return tClass.cast(Double.parseDouble(obj.toString()));
                }else if(tClass.equals(Float.class)) {
                    return tClass.cast(Float.parseFloat(obj.toString()));
                }else if(tClass.equals(Boolean.class)) {
                    return tClass.cast(Boolean.parseBoolean(obj.toString()));
                }else if(tClass.equals(String.class)) {
                    return tClass.cast(obj.toString());
                }
            } catch (NumberFormatException e) {
                throw new RuntimeException(e.toString());
            }
        }
        return null;
    }

}
