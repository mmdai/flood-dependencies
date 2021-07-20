package cn.flood.utils;


import cn.flood.lang.ReflectBeans;
import cn.flood.lang.StringUtils;
import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 *  聚合工具类
 *  支持方法链
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CollectBy implements Cloneable {
    //是否冲突.  e.g. 字段名会变成 r.exist_field_name(带前缀r.), 默认不解决冲突 即没有r.
    public boolean conflict = false;
    //是否覆盖.  right override left
    public boolean override = true;
    //groupBy by 返回的数据集是否包含其他维度字段(如果维度不唯一, 就返回第一个)
    public boolean groupOnly = true;
    //是否克隆出对象, 不会操作原对象
    public boolean leftClone = false;
    
    private List left;
    private List right;
    private String select;
    private String groupBy;
    private Map on;
    private Map where;
    private Map ext;
    private EachRow row;
    private Object param;
    private List<Map> result;
    
    public List<Map> list() {
    	return result;
    }
    
    public <F> List<F> list(Class<F> dest) {
    	return ReflectBeans.convert(dest, result);
    }
    
    /**
     * 建议用lambda实现
     */
    public interface EachRow {
        void each(Map<String, Object> row, Object param);
    }
    
    public CollectBy table(List left) {
        try {
            CollectBy clone = (CollectBy) this.clone();
            if (left != null && left.size() > 0 && !(left.get(0) instanceof Map)) {
            	left = ReflectBeans.beanToList(left);
            }
            clone.left = left;
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CollectBy table(List left, List right, String on) {
        try {
            CollectBy clone = (CollectBy) this.clone();
            if (left != null && left.size() > 0 && !(left.get(0) instanceof Map)) {
            	left = ReflectBeans.beanToList(left);
            }
            clone.left = left;
            if (right != null && right.size() > 0 && !(right.get(0) instanceof Map)) {
            	right = ReflectBeans.beanToList(right);
            }
            clone.right = right;
            clone.on = JSON.parseObject(on);
            clone.param = param;
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public CollectBy table(List left, List right, Map on) {
        try {
            CollectBy clone = (CollectBy) this.clone();
            if (left != null && left.size() > 0 && !(left.get(0) instanceof Map)) {
            	left = ReflectBeans.beanToList(left);
            }
            clone.left = left;
            if (right != null && right.size() > 0 && !(right.get(0) instanceof Map)) {
            	right = ReflectBeans.beanToList(right);
            }
            clone.right = right;
            clone.on = on;
            clone.param = param;
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CollectBy override(boolean override) {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.override = override;
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public CollectBy param(Object param) {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.param = param;
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public CollectBy select(String select) {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.select = select;
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CollectBy groupBy(String groupBy) {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.groupBy = groupBy;
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CollectBy where(String where) {
        return where(JSON.parseObject(where));
    }

    public CollectBy where(Map where) {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.where = where;
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CollectBy ext(String ext) {
        return ext(JSON.parseObject(ext));
    }

    /**
     *
     * @param ext
     * @return
     */
    public CollectBy ext(Map ext) {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.ext = ext;
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param row
     * @return
     */
    public CollectBy each(EachRow row) {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.row = row;
            if (clone.result != null) {
            	for (Map data : clone.result) {
            		row.each(data, clone.param);
            	}
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return
     */
    public CollectBy group() {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.group(left, select, groupBy, where, ext, row);
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return
     */
    public CollectBy innerJoin() {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.innerJoin(left, right, select, on, where, ext, row);
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return
     */
    public CollectBy leftJoin() {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.leftJoin(left, right, select, on, where, ext, row);
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CollectBy fullJoin() {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.fullJoin(left, right, select, on, where, ext, row);
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public CollectBy group(EachRow row) {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.group(left, select, groupBy, where, ext, row);
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * lamada -> by.table(left, right, "{'dept_id':'dept_id'}").innerJoin(service::each)
     * @param row
     * @return
     */
    public CollectBy innerJoin(EachRow row) {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.innerJoin(left, right, select, on, where, ext, row);
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * lamada -> by.table(left, right, "{'dept_id':'dept_id'}").leftJoin(service::each)
     * @param row
     * @return
     */
    public CollectBy leftJoin(EachRow row) {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.leftJoin(left, right, select, on, where, ext, row);
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * lamada -> by.table(left, right, "{'dept_id':'dept_id'}").fullJoin(service::each)
     * @param row
     * @return
     */
    public CollectBy fullJoin(EachRow row) {
        try {
            CollectBy clone = (CollectBy) this.clone();
            clone.fullJoin(left, right, select, on, where, ext, row);
            return clone;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Map> group(List<Map> list, String select, String groupBy) {
        return group(list, select, groupBy, null, null, null);
    }

    public List<Map> group(List<Map> list, String select, String groupBy, Map<String, Object> where) {
        return group(list, select, groupBy, where, null, null);
    }

    public List<Map> group(List<Map> list, String select, String groupBy, Map<String, Object> where, Map ext) {
        return group(list, select, groupBy, where, ext, null);
    }

    public List<Map> innerJoin(List<Map> left, List<Map> right, String select, Map on, Map where) {
        return innerJoin(left, right, select, on, where, null, null);
    }

    public List<Map> innerJoin(List<Map> left, List<Map> right, String select, Map on, Map where, Map ext) {
        return innerJoin(left, right, select, on, where, ext, null);
    }

    public List<Map> leftJoin(List<Map> left, List<Map> right, String select, Map on, Map where) {
        return leftJoin(left, right, select, on, where, null, null);
    }

    public List<Map> leftJoin(List<Map> left, List<Map> right, String select, Map on, Map where, Map ext) {
        return leftJoin(left, right, select, on, where, ext, null);
    }

    /**
     * 分组
     *
     * @param left    集合
     * @param select  [count|sum](field)[[as ]name]或者直接field相当于sum(field) [as ]field, 允许多个字段','分隔
     * @param groupBy 需要分组的多个字段 ','分隔
     * @param where   where条件,value='-'开头的 统计当前key的所有值
     * @param ext     将key-values加到所有行数据中, 实现自定义扩展字段
     * @return
     */
    public List<Map> group(List<Map> left, String select, String groupBy, Map<String, Object> where, Map ext, EachRow row) {
        Map<String, Map> groupUnique = new HashMap();
        String[] inter = intersect(left, select, groupBy);
        select = inter[0];//度量
        groupBy = inter[1];//维度
        if (groupBy == null || groupBy.length() == 0) return null;
        groupBy = groupBy.replaceAll("[^\\s]+\\.", "").toLowerCase();
        String[] groups = groupBy.split(", *");
        //metric e.g.:count(field) as name, sum(field) as name, field
        List<Map> sels = parseSelect(select, ext);
        List<Map> result = new ArrayList();
        for (Map data : left) {
            //过滤条件
            if (where(data, where, null)) continue;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < groups.length; i++) {
                if (i > 0) sb.append(",");
                String gp = groups[i];
                sb.append(StringUtils.checkNull(data.get(gp)));
            }
            String key = sb.toString();
            if (groupUnique.containsKey(key)) {
                Map un = groupUnique.get(key);
                for (Map sel : sels) {
                    String method = (String) sel.get("method");
                    String field = (String) sel.get("field");
                    String asName = (String) sel.get("asName");
                    if ("count".equals(method)) {
                        Integer origin = StringUtils.checkInt(un.get(asName), 1);
                        Integer now = origin + 1;
                        un.put(asName, now);
                    } else if (data.containsKey(field)) {
                        Integer origin = StringUtils.checkInt(un.get(asName), 0);
                        Integer now = StringUtils.checkInt(data.get(field), 0);
                        un.put(asName, origin + now);
                    }
                }
            } else {
                Map un = new LinkedHashMap();
                if (groupOnly) {
                    for (int i = 0; i < groups.length; i++) {
                        String gp = groups[i];
                        if (!data.containsKey(gp)) continue;
                        un.put(gp, data.get(gp));
                    }
                } else {
                    for (Iterator it = data.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry entry = (Map.Entry) it.next();
                        un.put(entry.getKey(), entry.getValue());
                    }
                }
                for (Map sel : sels) {
                    String method = (String) sel.get("method");
                    String field = (String) sel.get("field");
                    String asName = (String) sel.get("asName");
                    if ("count".equals(method)) {
                        Integer origin = 0;
                        Integer now = origin + 1;
                        un.put(asName, now);
                    } else if (data.containsKey(field)) {
                        Integer origin = 0;
                        Integer now = StringUtils.checkInt(data.get(field), 0);
                        un.put(asName, origin + now);
                    }
                }
                if (ext != null) un.putAll(ext);
                result.add(un);
                groupUnique.put(key, un);
            }
        }
        if (row != null) {
            for (Map un : result) {
                row.each(un, param);
            }
        }
        this.result = result;
        return result;
    }

    /**
     *
     * @param left
     * @param right
     * @param select [count|sum](field)[[as ]name]或者直接field相当于sum(field) [as ]field, 允许多个字段','分隔
     * @param on  关联
     * @param where where条件,value='-'开头的 统计当前key的所有值
     * @param ext 将key-values加到所有行数据中, 实现自定义扩展字段
     * @param row 数据处理 ->
     * @return
     */
    public List<Map> innerJoin(List<Map> left, List<Map> right, String select, Map on, Map where, Map ext, EachRow row) {
        List<Map> result = new ArrayList();
        if (on == null || on.isEmpty() || left == null || left.isEmpty() || right == null || right.isEmpty()) {
            if ((left == null || left.isEmpty()) && (right == null || right.isEmpty())) {
            	//nothing to do
            } else if (left == null || left.isEmpty()) {
            	//nothing to do
            } else if (right == null || right.isEmpty()) {
                //nothing to do
            } else {
            	List list = new ArrayList();
            	for (Map data : left) {
            		if (where(data, where, "l")) continue;
            		list.add(data);
            	}
            	if (!list.isEmpty()) {
            		left.clear();
            		left.addAll(list);
            	}
            	result.addAll(list);
            	
            	list = new ArrayList();
            	for (Map data : right) {
            		if (where(data, where, "r")) continue;
            		list.add(data);
            	}
            	result.addAll(list);
            }
            for (Map rw : result) {
                if (ext != null) rw.putAll(ext);
                if (row != null) row.each(rw, param);
            }
        } else {
        	Map<String, List<Map>> leftUnique = new LinkedHashMap();
            for (Map ld : left) {
                if (where(ld, where, "l")) continue;
                StringBuffer value = new StringBuffer();
                for (Iterator it = on.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = (String) entry.getKey();
                    if (value.length() > 0) value.append(",");
                    value.append(ld.get(key));
                }
                String key = value.toString();
                List<Map> data;
                if (leftUnique.containsKey(key)) {
                    data = leftUnique.get(key);
                } else {
                    data = new ArrayList();
                    leftUnique.put(key, data);
                }
                data.add(ld);
            }
            Map<String, List<Map>> rightUnique = new LinkedHashMap();
            for (Map rd : right) {
                if (where(rd, where, "r")) continue;
                StringBuffer value = new StringBuffer();
                for (Iterator it = on.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = (String) entry.getValue();
                    if (value.length() > 0) value.append(",");
                    value.append(rd.get(key));
                }
                String key = value.toString();
                List<Map> data;
                if (rightUnique.containsKey(key)) {
                    data = rightUnique.get(key);
                } else {
                    data = new ArrayList();
                    rightUnique.put(key, data);
                }
                data.add(rd);
            }
            //笛卡尔积
            for (Iterator it = leftUnique.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                List<Map> ldList = (List<Map>) entry.getValue();
                List<Map> rdList = rightUnique.get(key);
                if (ldList == null || ldList.isEmpty()) continue;
                if (rdList == null || rdList.isEmpty()) continue;
                for (Map ld : ldList) {
                    for (int i = 0; i < rdList.size(); i++) {
                    	Map rd = rdList.get(i);
                    	Map rw = null;
                    	if (i == 0) {
                    		rw = merge(ld, rd, this.leftClone);
                    	} else {
                    		rw = merge(ld, rd, true);
                    	}
                        if (ext != null) rw.putAll(ext);
                        if (row != null) row.each(rw, param);
                        result.add(rw);
                    }
                }
            }
        }
        //避免出现多行引用同一个对象在打印json时出错, 起到distinct的效果
        fliterField(result, select, ext);
        this.result = result;
        if (!leftClone && left != null) {
            left.clear();
            left.addAll(result);
            this.result = left;
        }
        return result;
    }

    /**
     * left join
     * @param left 左边集合
     * @param right 右边集合
     * @param select [count|sum](field)[[as ]name]或者直接field相当于sum(field) [as ]field, 允许多个字段','分隔
     * @param on  关联
     * @param where where条件,value='-'开头的 统计当前key的所有值
     * @param ext 将key-values加到所有行数据中, 实现自定义扩展字段
     * @param row 数据处理 ->
     * @return
     */
    public List<Map> leftJoin(List<Map> left, List<Map> right, String select, Map on, Map where, Map ext, EachRow row) {
        List<Map> result = new ArrayList();
        if (on == null || on.isEmpty() || left == null || left.isEmpty() || right == null || right.isEmpty()) {
            if ((left == null || left.isEmpty()) && (right == null || right.isEmpty())) {
            	//nothing to do
            } else if (left == null || left.isEmpty()) {
            	//nothing to do
            } else if (right == null || right.isEmpty()) {
            	List list = new ArrayList();
            	for (Map data : left) {
            		if (where(data, where, "l")) continue;
            		list.add(data);
            	}
            	if (!list.isEmpty()) {
            		left.clear();
            		left.addAll(list);
            	}
            	result.addAll(list);
            } else {
            	List list = new ArrayList();
            	for (Map data : left) {
            		if (where(data, where, "l")) continue;
            		list.add(data);
            	}
            	if (!list.isEmpty()) {
            		left.clear();
            		left.addAll(list);
            	}
            	result.addAll(list);
            	
            	list = new ArrayList();
            	for (Map data : right) {
            		if (where(data, where, "r")) continue;
            		list.add(data);
            	}
            	result.addAll(list);
            }
            for (Map rw : result) {
                if (ext != null) rw.putAll(ext);
                if (row != null) row.each(rw, param);
            }
        } else {
            Map<String, List<Map>> leftUnique = new LinkedHashMap();
            for (Map ld : left) {
                if (where(ld, where, "l")) continue;
                StringBuffer value = new StringBuffer();
                for (Iterator it = on.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = (String) entry.getKey();
                    if (value.length() > 0) value.append(",");
                    value.append(ld.get(key));
                }
                String key = value.toString();
                List<Map> data;
                if (leftUnique.containsKey(key)) {
                    data = leftUnique.get(key);
                } else {
                    data = new ArrayList();
                    leftUnique.put(key, data);
                }
                data.add(ld);
            }
            Map<String, List<Map>> rightUnique = new LinkedHashMap();
            for (Map rd : right) {
                if (where(rd, where, "r")) continue;
                StringBuffer value = new StringBuffer();
                for (Iterator it = on.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = (String) entry.getValue();
                    if (value.length() > 0) value.append(",");
                    value.append(rd.get(key));
                }
                String key = value.toString();
                List<Map> data;
                if (rightUnique.containsKey(key)) {
                    data = rightUnique.get(key);
                } else {
                    data = new ArrayList();
                    rightUnique.put(key, data);
                }
                data.add(rd);
            }
            //笛卡尔积
            for (Iterator it = leftUnique.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                List<Map> ldList = (List<Map>) entry.getValue();
                List<Map> rdList = rightUnique.get(key);
                if (ldList == null || ldList.isEmpty()) continue;
                for (Map ld : ldList) {
                    if (rdList == null || rdList.isEmpty()) {
                        Map rw = new LinkedHashMap();
                        rw.putAll(ld);
                        if (ext != null) rw.putAll(ext);
                        if (row != null) row.each(rw, param);
                        result.add(rw);
                    } else {
                        for (int i = 0; i < rdList.size(); i++) {
                        	Map rd = rdList.get(i);
                        	Map rw = null;
                        	if (i == 0) {
                        		rw = merge(ld, rd, this.leftClone);
                        	} else {
                        		rw = merge(ld, rd, true);
                        	}
                            if (ext != null) rw.putAll(ext);
                            if (row != null) row.each(rw, param);
                            result.add(rw);
                        }
                    }
                }
            }
        }
        fliterField(result, select, ext);
        this.result = result;
        return result;
    }

    /**
     *
     * @param left
     * @param right
     * @param select [count|sum](field)[[as ]name]或者直接field相当于sum(field) [as ]field, 允许多个字段','分隔
     * @param on  关联
     * @param where where条件,value='-'开头的 统计当前key的所有值
     * @param ext 将key-values加到所有行数据中, 实现自定义扩展字段
     * @param row 数据处理 ->
     * @return
     */
    public List<Map> fullJoin(List<Map> left, List<Map> right, String select, Map on, Map where, Map ext, EachRow row) {
        List<Map> result = new ArrayList();
        if (on == null || on.isEmpty() || left == null || left.isEmpty() || right == null || right.isEmpty()) {
            if ((left == null || left.isEmpty()) && (right == null || right.isEmpty())) {
            	//nothing to do
            } else if (left == null || left.isEmpty()) {
            	List list = new ArrayList();
            	for (Map data : right) {
            		if (where(data, where, "r")) continue;
            		list.add(data);
            	}
            	result.addAll(list);
            } else if (right == null || right.isEmpty()) {
            	List list = new ArrayList();
            	for (Map data : left) {
            		if (where(data, where, "l")) continue;
            		list.add(data);
            	}
            	if (!list.isEmpty()) {
            		left.clear();
            		left.addAll(list);
            	}
            	result.addAll(list);
            } else {
            	List list = new ArrayList();
            	for (Map data : left) {
            		if (where(data, where, "l")) continue;
            		list.add(data);
            	}
            	if (!list.isEmpty()) {
            		left.clear();
            		left.addAll(list);
            	}
            	result.addAll(list);
            	
            	list = new ArrayList();
            	for (Map data : right) {
            		if (where(data, where, "r")) continue;
            		list.add(data);
            	}
            	result.addAll(list);
            }
            for (Map rw : result) {
                if (ext != null) rw.putAll(ext);
                if (row != null) row.each(rw, param);
            }
        } else {
            Map<String, List<Map>> leftUnique = new LinkedHashMap();
            for (Map ld : left) {
                if (where(ld, where, "l")) continue;
                StringBuffer value = new StringBuffer();
                for (Iterator it = on.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = (String) entry.getKey();
                    if (value.length() > 0) value.append(",");
                    value.append(ld.get(key));
                }
                String key = value.toString();
                List<Map> data;
                if (leftUnique.containsKey(key)) {
                    data = leftUnique.get(key);
                } else {
                    data = new ArrayList();
                    leftUnique.put(key, data);
                }
                data.add(ld);
            }
            Map<String, List<Map>> rightUnique = new LinkedHashMap();
            for (Map rd : right) {
                if (where(rd, where, "r")) continue;
                StringBuffer value = new StringBuffer();
                for (Iterator it = on.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = (String) entry.getValue();
                    if (value.length() > 0) value.append(",");
                    value.append(rd.get(key));
                }
                String key = value.toString();
                List<Map> data;
                if (rightUnique.containsKey(key)) {
                    data = rightUnique.get(key);
                } else {
                    data = new ArrayList();
                    rightUnique.put(key, data);
                }
                data.add(rd);
            }
            // full join
            for (Iterator it = leftUnique.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                List<Map> ldList = (List<Map>) entry.getValue();
                List<Map> rdList = rightUnique.get(key);
                if (ldList == null || ldList.isEmpty()) continue;
                for (Map ld : ldList) {
                    if (rdList == null || rdList.isEmpty()) {
                        Map rw = new LinkedHashMap();
                        rw.putAll(ld);
                        if (ext != null) rw.putAll(ext);
                        if (row != null) row.each(rw, param);
                        result.add(rw);
                    } else {
                        for (int i = 0; i < rdList.size(); i++) {
                        	Map rd = rdList.get(i);
                        	Map rw = null;
                        	if (i == 0) {
                        		rw = merge(ld, rd, this.leftClone);
                        	} else {
                        		rw = merge(ld, rd, true);
                        	}
                            if (ext != null) rw.putAll(ext);
                            if (row != null) row.each(rw, param);
                            result.add(rw);
                        }
                    }
                }
            }
            for (Iterator it = rightUnique.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                List<Map> ldList = (List<Map>) entry.getValue();
                List<Map> rdList = leftUnique.get(key);
                if (ldList == null || ldList.isEmpty()) continue;
                for (Map ld : ldList) {
                    if (rdList == null || rdList.isEmpty()) {
                        Map rw = new LinkedHashMap();
                        rw.putAll(ld);
                        if (ext != null) rw.putAll(ext);
                        if (row != null) row.each(rw, param);
                        result.add(rw);
                    }
                }
            }
        }
        fliterField(result, select, ext);
        this.result = result;
        if (!leftClone && left != null) {
        	left.clear();
        	left.addAll(result);
        	this.result = left;
        }
        return result;
    }
    
    /**
     * 筛选字段
     * @param result
     * @param select
     * @param ext
     */
    private void fliterField(List<Map> result, String select, Map ext) {
    	if (result == null || result.isEmpty()) return;
        //筛选字段
        List<Map> fields = parseSelect(select, ext);
        if (fields != null) {
            Map<String, Map> unique = new HashMap();
            for (Map mp : fields) {
                String field = (String) mp.get("field");
                unique.put(field, mp);
            }
            
            Map<String, Object> struct = new HashMap();
            struct.putAll(result.get(0));
            
            for (Map rw : result) {
                Map data = new LinkedHashMap();
                for (Iterator it2 = rw.entrySet().iterator(); it2.hasNext(); ) {
                    Map.Entry entry2 = (Map.Entry) it2.next();
                    String column = (String) entry2.getKey();
                    if (!unique.containsKey(column)) continue;
                    Map mp = unique.get(column);
                    String asName = (String) mp.get("asName");
                    if (!column.equalsIgnoreCase(asName)) {
                    	String string = (String) mp.get("string");
                    	if ("1".equals(string)) {
                    		data.put(asName, column);
                    	} else {
                    		data.put(asName, entry2.getValue());
                    	}
                    	if (!struct.containsKey(asName)) struct.put(asName, null);
                    } else {
                		data.put(asName, entry2.getValue());
                	}
                }
                rw.clear();
                rw.putAll(data);
            }
            Map rs = struct;
            List<Map> lou = new ArrayList();
            for (Map mp : fields) {
                String asName = (String) mp.get("asName");
                if (!rs.containsKey(asName)) {
                    lou.add(mp);
                }
            }
            for (Map l : lou) {
            	for (Map rw : result) {
                    String column = (String) l.get("asName");
                    String value = (String) l.get("field");
                    rw.put(column, value);
                }
            }
        }
    }
    
    private boolean where(Map data, Map<String, Object> where, String fix) {
        boolean breakflg = false;//true的话 该条data数据将被外层调用时过滤
        if (where == null || where.isEmpty()) return breakflg;
        for (Iterator it = where.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Object> entry = (Map.Entry) it.next();
            String key = entry.getKey();
            if (key == null) continue;
            key = key.toLowerCase();
            if (!StringUtils.isCheckNull(fix)) {
                if (key.startsWith(fix)) {
                    key = key.replaceAll("^" + fix, "");
                }
            }
            if (!data.containsKey(key)) {
                breakflg = true;
                break;
            }
            Object value = entry.getValue();
            String left = StringUtils.checkNull(data.get(key));
            if (value == null) {
                if (left.length() == 0) continue;
            } else if (value instanceof String && ((String) value).startsWith("-")) {
                continue;  //表示不做限制
            } else if (value instanceof String && ((String) value).contains(",")) {
                if (!((String) value).contains(left)) breakflg = true;
            } else if (value instanceof Object[]) {
                Object[] v = (Object[]) value;
                if (v.length == 0) continue;
                breakflg = true;
                for (Object o : v) {
                    String right = StringUtils.checkNull(o);
                    //全部不匹配才跳出
                    if (left.equals(right)) {
                        breakflg = false;
                        break;
                    }
                }
            } else if (value instanceof Collection) {
                Collection v = (Collection) value;
                if (v.size() == 0) continue;
                breakflg = true;
                for (Object o : v) {
                    String right = StringUtils.checkNull(o);
                    //全部不匹配才跳出
                    if (left.equals(right)) {
                        breakflg = false;
                        break;
                    }
                }
            } else {
                String right = StringUtils.checkNull(value);
                if (!left.equals(right)) breakflg = true;
            }
            if (breakflg) break;
        }
        return breakflg;
    }

    private List<Map> parseSelect(String select, Map ext) {
        if (select == null) return null;
        select = select.trim().replaceAll("^(?i)(select) *", "");
        if (select.equals("*")) return null;
        String[] sp = select.split(", *");
        List result = new ArrayList();
        for (String metric : sp) {
            String method = null;//count, sum
            String field = null;
            String asName = null;
            if (metric != null) metric = metric.toLowerCase();
            if (metric.contains("(")) {
                String temp = metric.replaceAll("^\\s*([^\\(\\)\\s]+)\\s*\\(\\s*([^\\(\\)\\s]+)\\s*\\)(\\s+(as\\s+)?([^\\(\\)\\s]+))?\\s*$", "$1,$2,$5");
                String[] split = temp.split(", *");
                method = split[0].toLowerCase();
                field = split.length > 1 ? split[1] : "1";
                asName = split.length > 2 ? split[2] : "as_" + method;
            } else if (metric.contains(" ")) {
                String temp = metric.replaceAll(" +((?i)as *)?", ",");
                String[] split = temp.split(",");
                field = split[0];
                asName = split[1];
            } else {
                field = metric;
                asName = metric;
            }
            Map m = new HashMap();
            if (field != null && field.startsWith("'") && field.endsWith("'")) {
            	m.put("string", "1");
            }
            if (field != null) field = field.replaceAll("^'(.*)'$", "$1");
            if (asName != null) asName = asName.replaceAll("^'(.*)'$", "$1");
            m.put("method", method);
            m.put("field", field);
            m.put("asName", asName);
            result.add(m);
        }
        if (ext != null) {
            for (Iterator it = ext.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                String field = (String) entry.getKey();
                Map map = new HashMap();
                map.put("field", field);
                map.put("asName", field);
                result.add(map);
            }
        }
        return result;
    }

    /**
     * 合并
     * @param a
     * @param b
     * @return
     */
    private Map merge(Map a, Map b, boolean leftClone) {
        Map c = b;
        if (!override) {
            c = new LinkedHashMap();
            c.putAll(b);
            for (Iterator it = a.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = (String) entry.getKey();
                if (c.containsKey(key)) {
                    if (conflict) {
                        Object value = c.get(key);
                        c.remove(key);
                        c.put("r." + key, value);
                    } else {
                        c.remove(key);
                    }
                }
            }
        }
        Map row = a;
        if (leftClone) {
            row = new LinkedHashMap();
            row.putAll(a);
        }
        row.putAll(c);
        return row;
    }

    private String[] intersect(List<Map> list, String select, String group) {
        if (select == null || list == null || list.isEmpty()) return null;
        select = select.trim().replaceAll("^(?i)(select) *", "");
        String[] sp = select.split(", ");
        Map data = list.get(0);
        StringBuffer groupBy = new StringBuffer();
        StringBuffer selectBy = new StringBuffer();
        for (String f : sp) {
            if (data.containsKey(f)) {
                if (groupBy.length() > 0) groupBy.append(",");
                groupBy.append(f);
            } else {
                if (selectBy.length() > 0) selectBy.append(",");
                selectBy.append(f);
            }
        }
        if (group != null) {
            String[] gsp = group.split(", *");
            Set<String> set = new LinkedHashSet();
            for (String g : gsp) {
                set.add(g);
            }
            gsp = groupBy.toString().split(", *");
            for (String g : gsp) {
                set.add(g);
            }
            groupBy = new StringBuffer();
            for (String f : set) {
                if (groupBy.length() > 0) groupBy.append(",");
                groupBy.append(f);
            }
        }
        return new String[]{selectBy.toString(), groupBy.toString()};
    }
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        List left = JSON.parseArray(new StringBuffer().append("[{\"org_level\":\"RVP\",\"dept_id\":\"R100015558\",\"psid\":\"100015558\",\"name\":\"赵文欣\",\"store_count\":1919,\"bgm_name\":\"全国\",\"rvp_name\":\"赵文欣\"},{\"org_level\":\"RVP\",\"dept_id\":\"00100081\",\"psid\":\"100052049\",\"name\":\"陈光全\",\"store_count\":325,\"bgm_name\":\"全国\",\"rvp_name\":\"陈光全\"},{\"org_level\":\"RVP\",\"dept_id\":\"R100055169\",\"psid\":\"100055169\",\"name\":\"汪涛\",\"store_count\":1463,\"bgm_name\":\"全国\",\"rvp_name\":\"汪涛\"},{\"org_level\":\"RVP\",\"dept_id\":\"R100055433\",\"psid\":\"100055433\",\"name\":\"王越\",\"store_count\":964,\"bgm_name\":\"全国\",\"rvp_name\":\"王越\"},{\"org_level\":\"RVP\",\"dept_id\":\"R100102942\",\"psid\":\"100102942\",\"name\":\"包莹瑞\",\"store_count\":1475,\"bgm_name\":\"全国\",\"rvp_name\":\"包莹瑞\"}]").toString(), Map.class);
        List right = JSON.parseArray(new StringBuffer().append("[{\"org_level\":\"RVP\",\"dept_id\":\"R100055169\",\"primeSource\":\"大神卡\",\"primeType\":\"一店一码-其  他\",\"primeNumber\":\"552\"},{\"dept_id\":\"00100081\",\"primesource\":\"宅神卡\",\"primetype\":\"宅急送\",\"primenumber\":\"4,106\"}]").toString(), Map.class);
        CollectBy by = new CollectBy();
        System.out.println(left.size());
        System.out.println(right.size());
        List result = by.table(left,right,"{'dept_id':'dept_id','org_level':'org_level'}").leftJoin().list();
        System.out.println(result.size());
        System.out.println(JSON.toJSON(result));
        
        
//        List result7 = by.select("tc, f3 d3, f1, f2, 'f666' f6").table(list, list2, "{'f1':'f1'}").where("{'f2': 'f222'}")
//                .ext("{'f5': 'f555'}").fullJoin().list();
//        System.out.println(JSON.toJSON(result7));
        
        long end = System.currentTimeMillis();
        System.out.println("用时:" + (end - start) + "毫秒");
    }
}