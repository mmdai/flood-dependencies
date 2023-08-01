package com.main;

import cn.flood.base.easypoi.entity.ColEntity;
import cn.flood.base.easypoi.entity.MsgClient;
import cn.flood.base.easypoi.entity.TitleEntity;
import com.google.common.collect.ImmutableMap;
import cn.flood.base.easypoi.utils.EasyExcelUtil;
import cn.flood.base.easypoi.utils.ExcelPoiUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.*;

/**
 * 测试
 */
public class TestMain {

    static Map<String,List<ColEntity>> heads = new HashMap<>();// 最终导出的多个sheet的表头
    static Map<String,List<Map<String, String>>> datas = new HashMap<>();// 最终导出的多个sheet的内容
    static Map<String,Integer> types = new HashMap<>();// 最终导出的每个sheet的样式类型
    static Map<String,List<Integer>> autoRowHeights = new HashMap<>();// 最终导出的每个sheet的需要自适应行高的行号
    static Map<String,List<Integer>> mergeIndexs = new HashMap<>();// 最终导出的每个sheet的需要纵向合并的单元格列号

    public static void main(String[] args) throws Exception {
        单级表头();
        多级表头Map();
        多级表头Obj();
        多级表头Obj1();
        多级表头Obj2();
        纵向合并单元格();
        // 多个sheet导出
        ExcelPoiUtil excelTool = new ExcelPoiUtil();
        //HSSFWorkbook workbook = excelTool.exportWorkbook(heads, datas, 0, null, null); // 这里多个sheet都用的同一个样式
        HSSFWorkbook workbook = excelTool.exportWorkbook(heads, datas, types, null, mergeIndexs);
        excelTool.save(workbook,"C:\\Users\\daimi\\Downloads\\多个sheet.xlsx");
        String path ="C:\\Users\\daimi\\Downloads\\test.xlsx";
        List<MsgClient> list = EasyExcelUtil.importExcel(path, 0, 1, MsgClient.class);
        System.out.println(list.size());
    }

    public static void 单级表头() throws Exception {
        //单级的表头==============================================================
        Map<String, String> map = new HashMap<String, String>();
        map.put("登录名", "u_login_id");
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("用户名", "u_name");
        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("角色", "u_role");
        Map<String, String> map3 = new HashMap<String, String>();
        map3.put("部门", "u_dep");
        Map<String, String> map4 = new HashMap<String, String>();
        map4.put("用户类型", "u_type");
        List<Map<String, String>> titleList = new ArrayList<>();
        titleList.add(map);
        titleList.add(map1);
        titleList.add(map2);
        titleList.add(map3);
        titleList.add(map4);
        //单级的 行内数据
        List<Map<String, String>> rowList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("u_login_id", "登录名" + i);
            m.put("u_name", "张三" + i);
            m.put("u_role", "角色" + i);
            m.put("u_dep", "部门" + i);
            m.put("u_type", "用户类型" + i);
            rowList.add(m);
        }
        ExcelPoiUtil excelTool = new ExcelPoiUtil("单级表头的表格");
        List<ColEntity> titleData = excelTool.colEntityTransformer(titleList);
        HSSFWorkbook workbook = excelTool.exportWorkbook(titleData, rowList,1,null);
        excelTool.save(workbook,"C:\\Users\\daimi\\Downloads\\单级表头.xlsx");
    }

    public static void 多级表头Map() throws Exception {
        List<Map<String,String>> titleList=new ArrayList<>();
        Map<String,String> titleMap=new HashMap<String,String>();
        titleMap.put("id","11");titleMap.put("pid","0");titleMap.put("content","登录名");titleMap.put("fieldName","u_login_id");titleMap.put("width","20");
        Map<String,String> titleMap1=new HashMap<String,String>();
        titleMap1.put("id","1");titleMap1.put("pid","0");titleMap1.put("content","姓名");titleMap1.put("fieldName","u_name");titleMap1.put("width","20");
        Map<String,String> titleMap2=new HashMap<String,String>();
        titleMap2.put("id","2");titleMap2.put("pid","0");titleMap2.put("content","角色加部门");titleMap2.put("fieldName",null);titleMap2.put("width","20");
        Map<String,String> titleMap3=new HashMap<String,String>();
        titleMap3.put("id","3");titleMap3.put("pid","2");titleMap3.put("content","角色");titleMap3.put("fieldName","u_role");titleMap3.put("width","15");
        Map<String,String> titleMap4=new HashMap<String,String>();
        titleMap4.put("id","4");titleMap4.put("pid","2");titleMap4.put("content","部门");titleMap4.put("fieldName","u_dep");titleMap4.put("width","15");
        Map<String,String> titleMap5=new HashMap<String,String>();
        titleMap5.put("id","22");titleMap5.put("pid","0");titleMap5.put("content","角色加部门1");titleMap5.put("fieldName",null);titleMap5.put("width","20");
        Map<String,String> titleMap6=new HashMap<String,String>();
        titleMap6.put("id","22_1");titleMap6.put("pid","22");titleMap6.put("content","角色1");titleMap6.put("fieldName","u_role");titleMap6.put("width","10");
        Map<String,String> titleMap7=new HashMap<String,String>();
        titleMap7.put("id","22_2");titleMap7.put("pid","22");titleMap7.put("content","部门1");titleMap7.put("fieldName","u_dep");titleMap7.put("width","10");
        titleList.add(titleMap); titleList.add(titleMap1); titleList.add(titleMap2); titleList.add(titleMap3); titleList.add(titleMap4);
        titleList.add(titleMap5); titleList.add(titleMap6); titleList.add(titleMap7);
        // 单级的 行内数据
        List<Map<String, String>> rowList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("u_login_id", "登录名zx" + i);
            m.put("u_name", "张三" + i);
            m.put("u_role", "角色" + i);
            m.put("u_dep", "部门" + i);
            m.put("u_type", "用户类型" + i);
            rowList.add(m);
        }
        ExcelPoiUtil excelTool = new ExcelPoiUtil("List<Map>多级表头");
        Map<String,String> param = ImmutableMap.<String, String>builder().put("id", "id").put("pid", "pid")
                .put("content", "content").put("fieldName", "fieldName").put("width", "width").build();
        List<ColEntity> titleData = excelTool.colEntityTransformer(titleList,param, "0");
        //HSSFWorkbook workbook = excelTool.exportWorkbook(titleData, rowList);
        //excelTool.save(workbook,"C:\\Users\\Administrator\\Desktop\\多级表头Map.xlsx");
        heads.put("List<Map>多级表头",titleData);// 每个sheet的表头，sheet名称为key
        datas.put("List<Map>多级表头",rowList);// 每个sheet的内容，sheet名称为key
        types.put("List<Map>多级表头",0);// 每个sheet的样式类型，sheet名称为key
    }

    public static void 多级表头Obj() throws Exception {
        List<TitleEntity> titleList = new ArrayList<>();
        titleList.add(new TitleEntity("0", null, "总表"));
        titleList.add(new TitleEntity("11", "0", "登录名2", "u_login_id",15));
        titleList.add(new TitleEntity("1", "0", "姓名", "u_name",15));
        titleList.add(new TitleEntity("2", "0", "角色加部门"));
        titleList.add(new TitleEntity("3", "2", "角色", "u_role"));
        titleList.add(new TitleEntity("4", "2", "部门", "u_dep"));
        titleList.add(new TitleEntity("33", "0", "角色加部门1", null,15));
        titleList.add(new TitleEntity("33_1", "33", "角色33", "u_role",15));
        titleList.add(new TitleEntity("33_2", "33_1", "部门33", "u_dep",15));
        titleList.add(new TitleEntity("44", "0", "角色加部门2", null,10));
        titleList.add(new TitleEntity("44_1", "44", "角色44", "u_role",10));
        titleList.add(new TitleEntity("44_2", "44", "部门44", "u_dep",10));
        titleList.add(new TitleEntity("1_1", "1", "姓名1", "u_name",15));
        titleList.add(new TitleEntity("44_3", "44_2", "44_2", "u_dep",10));
        //单级的 行内数据
        List<Map<String, String>> rowList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("u_login_id", "登录名" + i);
            m.put("u_name", "张三" + i);
            m.put("u_role", "角色" + i);
            m.put("u_dep", "部门" + i);
            m.put("u_type", "用户类型" + i);
            rowList.add(m);
        }
        ExcelPoiUtil excelTool = new ExcelPoiUtil("实体类（entity）多级表头表格");
        Map<String,String> param = ImmutableMap.<String, String>builder().put("id", "id").put("pid", "pid")
                .put("content", "content").put("fieldName", "fieldName").put("width", "width").build();
        List<ColEntity> titleData = excelTool.colEntityTransformer(titleList, param, "0");
        //HSSFWorkbook workbook = excelTool.exportWorkbook(titleData, rowList);
        //excelTool.save(workbook,"C:\\Users\\Administrator\\Desktop\\多级表头Obj.xlsx");
        heads.put("实体类（entity）多级表头表格",titleData);// 每个sheet的表头，sheet名称为key
        datas.put("实体类（entity）多级表头表格",rowList);// 每个sheet的内容，sheet名称为key
        types.put("实体类（entity）多级表头表格",0);// 每个sheet的样式类型，sheet名称为key
    }

    public static void 多级表头Obj1() throws Exception {
        List<TitleEntity> titleList = new ArrayList<>();
        titleList.add(new TitleEntity("title", null, "这里是title"));
        titleList.add(new TitleEntity("一级1", "title", "一级1"));
        titleList.add(new TitleEntity("一级2", "title", "一级2"));
        titleList.add(new TitleEntity("二级1", "一级1", "二级1"));
        titleList.add(new TitleEntity("二级2", "一级2", "二级2"));
        titleList.add(new TitleEntity("三级1", "二级1", "三级1"));
        titleList.add(new TitleEntity("三级2", "二级2", "三级2"));
        titleList.add(new TitleEntity("四级1", "三级1", "四级1", "fieldName1"));
        titleList.add(new TitleEntity("四级2", "三级1", "四级2", "fieldName2"));
        titleList.add(new TitleEntity("四级3", "三级2", "四级3", "fieldName3"));
        titleList.add(new TitleEntity("四级4", "三级2", "四级4", "fieldName4"));
        //单级的 行内数据
        List<Map<String, String>> rowList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("fieldName1", "四级1_" + i);
            m.put("fieldName2", "四级2_" + i);
            m.put("fieldName3", "四级3_" + i);
            m.put("fieldName4", "四级4_" + i);
            rowList.add(m);
        }
        ExcelPoiUtil excelTool = new ExcelPoiUtil("实体类（entity）多级表头表格");
        Map<String,String> param = ImmutableMap.<String, String>builder().put("id", "id").put("pid", "pid")
                .put("content", "content").put("fieldName", "fieldName").put("width", "width").build();
        List<ColEntity> titleData = excelTool.colEntityTransformer(titleList, param, "title");
        //HSSFWorkbook workbook = excelTool.exportWorkbook(titleData, rowList);
        //excelTool.save(workbook,"C:\\Users\\Administrator\\Desktop\\多级表头Obj1.xlsx");
        heads.put("实体类（entity）多级表头表格1",titleData);// 每个sheet的表头，sheet名称为key
        datas.put("实体类（entity）多级表头表格1",rowList);// 每个sheet的内容，sheet名称为key
        types.put("实体类（entity）多级表头表格1",0);// 每个sheet的样式类型，sheet名称为key
    }

    public static void 多级表头Obj2() throws Exception {
        List<TitleEntity> titleList = new ArrayList<>();
        titleList.add(new TitleEntity("title", null, "这里是title"));
        // 固定的五项表头
        titleList.add(new TitleEntity("项目", "title", "项目"));
        titleList.add(new TitleEntity("评分规则", "项目", "评分规则"));
        titleList.add(new TitleEntity("评分标准", "评分规则", "评分标准"));
        titleList.add(new TitleEntity("所在单位", "评分标准", "所在单位", "unit",15));
        titleList.add(new TitleEntity("所在部门", "评分标准", "所在部门", "dept",15));
        // 动态表头（实际项目需要根据数据库数据添加）
        int count = 0;
        for (int i = 0; i < 2; i++) {
            String xmId = "项目"+(i+1);
            TitleEntity xm = new TitleEntity(xmId, "title", xmId);
            titleList.add(xm);
            for (int j = 0; j < 2; j++) {
                String gzId = "项目"+(i+1)+"-"+"规则"+(j+1);
                String bzId = "项目"+(i+1)+"-"+"标准"+(j+1);
                TitleEntity gz = new TitleEntity(gzId, xmId, gzId);
                TitleEntity bz = new TitleEntity(bzId, gzId, String.valueOf(j));
                TitleEntity sl = new TitleEntity(bzId+"_sl"+j, bzId, "数量", "sl"+count, 10);
                TitleEntity df = new TitleEntity(bzId+"_df"+j, bzId, "得分", "df"+count, 10);
                titleList.add(gz);
                titleList.add(bz);
                titleList.add(sl);
                titleList.add(df);
                count++;
            }
        }
        // 填充数据
        List<Map<String, String>> rowList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Map<String, String> m = new HashMap<String, String>();
            if (i<4){
                m.put("unit", "A单位");
                m.put("dept", "部门" + i);
            }else {
                m.put("unit", "B单位");
                m.put("dept", "部门" + i);
            }
            for (int j = 0; j < count; j++) {
                m.put("sl"+j, String.valueOf(j));
                m.put("df"+j, String.valueOf(j+i));
            }
            rowList.add(m);
        }
        ExcelPoiUtil excelTool = new ExcelPoiUtil("实体类（entity）多级表头表格");
        Map<String,String> param = ImmutableMap.<String, String>builder().put("id", "id").put("pid", "pid")
                .put("content", "content").put("fieldName", "fieldName").put("width", "width").build();
        List<ColEntity> titleData = excelTool.colEntityTransformer(titleList, param, "title");
        //HSSFWorkbook workbook = excelTool.exportWorkbook(titleData, rowList,Arrays.asList(0));
        //excelTool.save(workbook,"C:\\Users\\Administrator\\Desktop\\多级表头Obj2.xlsx");
        heads.put("实体类（entity）多级表头表格2",titleData);// 每个sheet的表头，sheet名称为key
        datas.put("实体类（entity）多级表头表格2",rowList);// 每个sheet的内容，sheet名称为key
        types.put("实体类（entity）多级表头表格2",0);// 每个sheet的样式类型，sheet名称为key
        mergeIndexs.put("实体类（entity）多级表头表格2",Arrays.asList(0));// 每个sheet的默认行高，sheet名称为key
    }

    public static void 纵向合并单元格() throws Exception {
        List<Map<String,String>> titleList=new ArrayList<>();
        Map<String,String> titleMap=new HashMap<String,String>();
        titleMap.put("id","11");titleMap.put("pid","0");titleMap.put("content","登录名");titleMap.put("fieldName","u_login_id");titleMap.put("width","20");
        Map<String,String> titleMap1=new HashMap<String,String>();
        titleMap1.put("id","1");titleMap1.put("pid","0");titleMap1.put("content","姓名");titleMap1.put("fieldName","u_name");titleMap1.put("width","20");
        Map<String,String> titleMap2=new HashMap<String,String>();
        titleMap2.put("id","2");titleMap2.put("pid","0");titleMap2.put("content","角色加部门");titleMap2.put("fieldName",null);titleMap2.put("width","20");
        Map<String,String> titleMap3=new HashMap<String,String>();
        titleMap3.put("id","3");titleMap3.put("pid","2");titleMap3.put("content","角色");titleMap3.put("fieldName","u_role");titleMap3.put("width","15");
        Map<String,String> titleMap4=new HashMap<String,String>();
        titleMap4.put("id","4");titleMap4.put("pid","2");titleMap4.put("content","部门");titleMap4.put("fieldName","u_dep");titleMap4.put("width","15");
        Map<String,String> titleMap5=new HashMap<String,String>();
        titleMap5.put("id","22");titleMap5.put("pid","0");titleMap5.put("content","角色加部门1");titleMap5.put("fieldName",null);titleMap5.put("width","20");
        Map<String,String> titleMap6=new HashMap<String,String>();
        titleMap6.put("id","22_1");titleMap6.put("pid","22");titleMap6.put("content","角色1");titleMap6.put("fieldName","u_role");titleMap6.put("width","10");
        Map<String,String> titleMap7=new HashMap<String,String>();
        titleMap7.put("id","22_2");titleMap7.put("pid","22");titleMap7.put("content","部门1");titleMap7.put("fieldName","u_dep");titleMap7.put("width","10");
        titleList.add(titleMap); titleList.add(titleMap1); titleList.add(titleMap2); titleList.add(titleMap3); titleList.add(titleMap4);
        titleList.add(titleMap5); titleList.add(titleMap6); titleList.add(titleMap7);
        // 单级的 行内数据
        List<Map<String, String>> rowList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("u_login_id", "登录名zx" + i);
            if (i<6){
                m.put("u_login_id", "登录名zx1");
                m.put("u_name", i<3?"张三":"李四");

            }else {
                m.put("u_login_id", "登录名zx2");
                m.put("u_name", i<9?"王五":"赵六");
            }
            m.put("u_role", "角色" + i);
            m.put("u_dep", "部门" + i);
            m.put("u_type", "用户类型" + i);
            rowList.add(m);
        }
        ExcelPoiUtil excelTool = new ExcelPoiUtil("纵向合并单元格");
        Map<String,String> param = ImmutableMap.<String, String>builder().put("id", "id").put("pid", "pid")
                .put("content", "content").put("fieldName", "fieldName").put("width", "width").build();
        List<ColEntity> titleData = excelTool.colEntityTransformer(titleList,param, "0");
        //HSSFWorkbook workbook = excelTool.exportWorkbook(titleData, rowList,Arrays.asList(0,1));
        //excelTool.save(workbook,"C:\\Users\\Administrator\\Desktop\\纵向合并单元格.xlsx");
        heads.put("纵向合并单元格",titleData);// 每个sheet的表头，sheet名称为key
        datas.put("纵向合并单元格",rowList);// 每个sheet的内容，sheet名称为key
        types.put("纵向合并单元格",0);// 每个sheet的样式类型，sheet名称为key
        mergeIndexs.put("纵向合并单元格",Arrays.asList(0,1));// 每个sheet的默认行高，sheet名称为key
    }
}
