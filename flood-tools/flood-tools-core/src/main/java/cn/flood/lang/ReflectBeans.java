package cn.flood.lang;

import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 反射类
 * <p>Description: </p>
 * 对象拷贝(只依赖method的拷贝)
 * 复制原则:src对象getXX方法值为null的属性不会注入到dest对象相应的setXX方法中(防止null值覆盖了dest原有数据)
 * boolean类型将不参与拷贝(防止默认为false) 这需要规约Entity中不要有布尔类型数据
 * @author mmdai
 * @date 2018年11月30日
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ReflectBeans {

    public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public final static String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * 将Map里的数据传入Bean的set方法内
     *
     * @param srcMap<cloumName, ObjectValue>
     * @param dest
     */
    public static void mapToBean(Map srcMap, Object dest) {
        if (dest == null || srcMap == null || srcMap.isEmpty()) {
            return;
        }
        // 获取所有方法
        //java.lang.reflect.Method[] ms = dest.getClass().getDeclaredMethods();
        Method[] ms = dest.getClass().getMethods(); //继承过来的方法也要参与拷贝，比如继承自IEntity
        for (Method method : ms) {
            // 过滤所有get方法
            String methodName = method.getName();
            if (methodName.startsWith("set")) {
                // 根据set方法获取属性名称
                StringBuffer fieldName = new StringBuffer(methodName.substring(3));
                if (fieldName.length() == 0) {
                    continue;
                }
                // 转换第一个字符的大小写
                char ch = fieldName.charAt(0);
                ch = (char) (ch >= 'A' && ch <= 'Z' ? ch + 32 : ch - 32);
                fieldName.setCharAt(0, ch);
                if (!srcMap.containsKey(fieldName.toString())) {
                    //兼容下划线
                    String field = fieldName.toString();
                    String fullFieldName = NameUtil.fullName(field);
                    if (srcMap.containsKey(fullFieldName)) {
                        fieldName = new StringBuffer(fullFieldName);
                    } else if (srcMap.containsKey(field.toLowerCase())) {
                        fieldName = new StringBuffer(field.toLowerCase());
                    } else {
                        continue;
                    }
                }
                Object value = srcMap.get(fieldName.toString());
                try {
                    Class[] types = method.getParameterTypes();
                    if (types.length != 1) {
                        continue;    //setXX()就传一个参数
                    }
                    Class fieldType = types[0];
                    //如果除对象类型的其他类如常量类型传入的是空值就不参与反射
                    if (value instanceof Map) {
                        System.out.println();
                    }
                    if (!(value instanceof Collection) && !(value instanceof Map)) {
                        if (StringUtils.isEmpty(value) && !fieldType.getName().startsWith("java")) {
                            continue;
                        }
                    }
                    value = exchangeProperty(fieldType, value);
                    method.invoke(dest, new Object[]{value});
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.err.println("error:" + dest.getClass().getName() + "." + methodName + " value:" + value);
                }
            }
        }
    }

    public static List<Map> beanToList(List voList) {
        return beanToList(voList, false);
    }

    public static List<Map> beanToList(List voList, boolean addLine) {
    	List list = new ArrayList();
    	for (Object src : voList) {
    		Map map = beanToMap(src, null, addLine);
    		list.add(map);
    	}
        return list;
    }
    
    public static Map beanToMap(Object src) {
        return beanToMap(src, null, false);
    }

    public static Map beanToMap(Object src, boolean addLine) {
        return beanToMap(src, null, addLine);
    }

    public static Map beanToMap(Object src, Map dest) {
        return beanToMap(src, dest, false);
    }

    public static Map beanToMap(Object src, Map dest, boolean addLine) {
        return beanToMap(src, dest, addLine, false);
    }

    /**
     * Bean转换成Map
     * 转换原则:src对象getXX方法值为null的属性不会注入到dest对象相应的setXX方法中(防止null值覆盖了dest原有数据)
     * boolean类型将不参与拷贝(防止默认为false)
     *
     * @param src
     * @param addLine     Map的key是加上"_"
     * @param containNull 是否忽略null值属性
     * @return
     */
    public static Map beanToMap(Object src, Map map, boolean addLine, boolean containNull) {
        Map srcMap = null;
        if (map == null) {
            if (src == null) return null;
            if (src instanceof LinkedHashMap) {
                return (LinkedHashMap) src;
            }
            if (addLine) {
                srcMap = new LinkedHashMap();
            } else {
            	srcMap = new LinkedHashMap();
            }
            if (src instanceof Map) {
                srcMap.putAll((Map) src);
                return srcMap;
            }
        } else {
            srcMap = map;
        }
        //java.lang.reflect.Method[] ms = src.getClass().getDeclaredMethods();
        Method[] ms = src.getClass().getMethods(); //继承过来的方法也要参与拷贝，比如继承自IEntity
        //实现冒泡排序 目标是把List放在最后
		for (int i = 0; i < ms.length - 1; i++) {
			for (int j = 0; j < ms.length - 1 - i; j++) {
				Method a = ms[j];
				Method b = ms[j + 1];
				if ("java.lang.List".equalsIgnoreCase(a.getReturnType().getName())) {
					ms[j] = b;
					ms[j + 1] = a;
				}
			}
		}
        for (Method method : ms) {
            String methodName = method.getName();
            if (methodName.startsWith("set")
                    || "getClass".equals(methodName)
                    || "getEnv".equals(methodName)
                    || "getString".equals(methodName)) continue;
            // 根据set方法获取属性名称
            StringBuffer fieldName = new StringBuffer();
            // 取得所有的除set方法
            if (methodName.startsWith("get")) {
                fieldName.append(methodName.substring(3));
            } else {
                continue;
            }
            if (fieldName.length() == 0) {
                continue;
            }
            if (method.getParameterTypes() != null && method.getParameterTypes().length > 0) {
                continue;
            }
            // 转换第一个字符为小写
            char ch = fieldName.charAt(0);
            ch = (char) (ch >= 'A' && ch <= 'Z' ? ch + 32 : ch - 32);
            fieldName.setCharAt(0, ch);
            try {
                Object value = method.invoke(src);
                // 如果值为null 或是布尔类型 则不要put到Map中
                if (!containNull && value == null) continue;
                if (value instanceof Boolean) continue;

                if (value != null && value instanceof String) {
                    value = ((String) value).trim();
                } else if (value != null && value instanceof Date) {
                    if (!(value instanceof Timestamp)) {
                        value = new Timestamp(((Date) value).getTime());
                    }
                } else if (value != null && value instanceof Collection) {
                	List list = new ArrayList();
                	for (Object co : (Collection)value) {
                		Map m = beanToMap(co, null, addLine, containNull);
                		list.add(m);
                	}
                	value = list;
                }
                if (addLine) {
                    srcMap.put(addLine(fieldName.toString()), value);
                } else {
                    srcMap.put(fieldName.toString(), value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return srcMap;
    }

    /**
     * 对象拷贝(依赖method的拷贝)
     * 复制原则:src对象getXX方法值为null的属性不会注入到dest对象相应的setXX方法中(防止null值覆盖了dest原有数据)
     * boolean类型将不参与拷贝(防止默认为false) 这需要规约Entity中不要有布尔类型数据
     *
     * @param src
     * @param dest
     */
    public static <T> T copy(Object src, T dest) {
        if (src == null) return null;
        Map srcMap = beanToMap(src);
        mapToBean(srcMap, dest);
        return dest;
    }

    public static <T> T copy(Object src, Class<T> dest) {
        if (src == null) return null;
        Map srcMap = beanToMap(src);
        T to = null;
        try {
            to = dest.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapToBean(srcMap, to);
        return to;
    }

    /**
     * 属性转换
     *
     * @param fieldType  目的类型
     * @param fieldValue 传入的值
     * @return 返回值转成@param fieldType 类型
     */
    private static Object exchangeProperty(Class fieldType, Object fieldValue) {
        if (fieldType == null || StringUtils.isEmpty(fieldValue)) {
            if (fieldValue instanceof Collection
                    || fieldValue instanceof Map) {
                return fieldValue;
            }
            return null;
        }
        String type = fieldType.getName();
        if (fieldValue instanceof String) {
            fieldValue = ((String) fieldValue).trim();
            if ("java.lang.String".equals(type)) {
                //类型相同就直接赋值.
            } else if ("java.lang.Long".equals(type) || "long".equals(type)) {
                fieldValue = new Long(fieldValue.toString());
            } else if ("java.lang.Integer".equals(type) || "int".equals(type)) {
                fieldValue = new Integer(fieldValue.toString());
            } else if ("java.math.BigDecimal".equals(type)) {
                fieldValue = new BigDecimal(fieldValue.toString());
            } else if ("java.lang.Float".equals(type) || "float".equals(type)) {
                fieldValue = new Float(fieldValue.toString());
            } else if ("java.lang.Double".equals(type) || "double".equals(type)) {
                fieldValue = new Double(fieldValue.toString());
            } else if ("java.sql.Timestamp".equals(type)
                    || "java.sql.Date".equals(type)
                    || "java.sql.Time".equals(type)
                    || "java.util.Date".equals(type)) {
                String regex = "^(\\d+)\\D(\\d+)\\D(\\d+)(\\D+(\\d+)\\D(\\d+)\\D(\\d+).*)?$";
                if (!((String) fieldValue).matches(regex)) return null;
                if (((String) fieldValue).matches("^(\\d+)\\D(\\d+)\\D(\\d+)$")) {
                    fieldValue = ((String) fieldValue).replaceAll(regex, "$1-$2-$3");
                } else {
                    fieldValue = ((String) fieldValue).replaceAll(regex, "$1-$2-$3 $4:$5:$6");
                }
                Date dateValue = stringToDate((String) fieldValue);
                if (dateValue == null) return null;
                if ("java.sql.Timestamp".equals(type)) {
                    fieldValue = new Timestamp(dateValue.getTime());
                } else if ("java.sql.Date".equals(type)) {
                    fieldValue = new java.sql.Date(dateValue.getTime());
                } else if ("java.sql.Time".equals(type)) {
                    fieldValue = new java.sql.Time(dateValue.getTime());
                } else {
                    fieldValue = dateValue;
                }
            } //else if...
        } else if (fieldValue instanceof Number) {
            if (fieldType.equals(fieldValue.getClass())) {
                //类型相同就直接赋值.
            } else if ("java.lang.Long".equals(type) || "long".equals(type)) {
                fieldValue = new Long(fieldValue.toString());
            } else if ("java.lang.Integer".equals(type) || "int".equals(type)) {
                fieldValue = new Integer(fieldValue.toString());
            } else if ("java.math.BigDecimal".equals(type)) {
                fieldValue = new BigDecimal(fieldValue.toString());
            } else if ("java.lang.Float".equals(type) || "float".equals(type)) {
                fieldValue = new Float(fieldValue.toString());
            } else if ("java.lang.Double".equals(type) || "double".equals(type)) {
                fieldValue = new Double(fieldValue.toString());
            } else if ("java.lang.String".equals(type)) {
                fieldValue = fieldValue.toString();
            }
        } else if (fieldValue instanceof Date) {
            if (fieldType.equals(fieldValue.getClass())) {
                //类型相同就直接赋值.
            } else if ("java.sql.Timestamp".equals(type)) {
                fieldValue = new Timestamp(((Date) fieldValue).getTime());
            } else if ("java.sql.Date".equals(type)) {
                fieldValue = new java.sql.Date(((Date) fieldValue).getTime());
            } else if ("java.sql.Time".equals(type)) {
                fieldValue = new java.sql.Time(((Date) fieldValue).getTime());
            } else if ("java.lang.String".equals(type)) {
                fieldValue = dateTimeToString((Date) fieldValue);
            }
        } else if (fieldValue instanceof Clob) {
            Clob clob_obj = (Clob) fieldValue;
            try {
                fieldValue = clob_obj.getSubString(1, (int) clob_obj.length());
            } catch (Exception e) {
                try {
                    fieldValue = clob_obj.getSubString(1, Integer.MAX_VALUE);
                } catch (Exception e1) {
                }
            }
        }
        return fieldValue;
    }

    /**
     * 把包含日期值转换为字符串
     *
     * @param date 日期（日期+时间）
     * @return 字符串
     */
    public static String dateTimeToString(Date date) {
        String DateString = "";
        if (date == null) {
            DateString = "";
        } else {
            SimpleDateFormat formatDate = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS, Locale.getDefault());
            DateString = formatDate.format(date);
        }
        return DateString;
    }

    /**
     * 将字符型数字转为Date类型
     *
     * @param dateString 要转换的字符
     * @return Date类型的日期
     * @throws ParseException
     */
    private static Date stringToDate(String dateString) {
        if (dateString != null && dateString.trim().length() > 0) {
            String pattern = YYYY_MM_DD_HH_MM_SS;
            if (dateString.matches("^(\\d+)\\D(\\d+)\\D(\\d+)$")) {
                pattern = YYYY_MM_DD;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
            try {
                Date date = sdf.parse(dateString);
                return date;
            } catch (ParseException e) {
            }
        }
        return null;
    }

    private static Pattern AZ = Pattern.compile("[A-Z]");

    public static String addLine(String columName) {
        Matcher m = AZ.matcher(columName);
        StringBuffer b = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(b, "_" + m.group());
        }
        m.appendTail(b);
        return b.toString();
    }

    /**
     * 转换List<Map> 为List<LinkedHashMap> 此Map可忽略Key的大小写
     * forShort 是否缩写(true为去掉下划线)
     *
     * @return
     */
    public static List convShort(List mapList) {
        if (mapList == null || mapList.size() == 0) {
            return new ArrayList();
        }
        List list = new ArrayList();
        for (Iterator iterator = mapList.iterator(); iterator.hasNext(); ) {
            list.add(convShort((Map) iterator.next()));
        }
        mapList.clear();
        mapList.addAll(list);
        return mapList;
    }

    public static Map convShort(Map dataMap) {
        if (dataMap == null) {
            return new LinkedHashMap();
        }
        LinkedHashMap map = new LinkedHashMap();
        for (Iterator it2 = dataMap.entrySet().iterator(); it2.hasNext(); ) {
            Map.Entry entity = (Map.Entry) it2.next();
            String columName = (String) entity.getKey();
            columName = NameUtil.fieldName(columName);
            map.put(columName, entity.getValue());
        }
        return map;
    }

    public static Map convLine(Map dataMap) {
        if (dataMap == null) {
            return new LinkedHashMap();
        } else {
            LinkedHashMap map = new LinkedHashMap();
            Iterator it2 = dataMap.entrySet().iterator();

            while (it2.hasNext()) {
                Map.Entry entity = (Map.Entry) it2.next();
                String columName = (String) entity.getKey();
                columName = NameUtil.fullName(columName);
                map.put(columName, entity.getValue());
            }
            return map;
        }
    }

    public static List convLine(List mapList) {
        if (mapList != null && mapList.size() != 0) {
            ArrayList list = new ArrayList();
            Iterator iterator = mapList.iterator();

            while (iterator.hasNext()) {
                list.add(convLine((Map) iterator.next()));
            }

            mapList.clear();
            mapList.addAll(list);
            return mapList;
        } else {
            return new ArrayList();
        }
    }

    /**
     *
     * @param dest
     * @param src
     * @param <F>
     * @return
     */
    public static <F> F convert(Class<F> dest, Map src) {
        F obj = null;
        try {
            src = convShort(src);
            obj = dest.newInstance();
            ReflectBeans.mapToBean(src, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     *
     * @param dest
     * @param src
     * @param <F>
     * @return
     */
    public static <F> List<F> convert(Class<F> dest, List<Map> src) {
        List<F> resultList = new ArrayList();
        try {
            src = convShort(src);
            for (Map srcMap : src) {
                F obj = dest.newInstance();
                ReflectBeans.mapToBean(srcMap, obj);
                resultList.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
