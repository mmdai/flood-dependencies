package cn.flood.base.easypoi.utils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;
import cn.afterturn.easypoi.excel.imports.ExcelImportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;

/**
 * easypoi操作工具类
 */
public class EasyExcelUtil {

    public EasyExcelUtil(){}

    // ********************************************* 导出 *********************************************

    /**
     * excel 根据 type 导出
     * @param list 数据
     * @param title   标题
     * @param sheetName sheet名称
     * @param pojoClass pojo类型
     * @param fileName 文件名称
     * @param type 类型（0 无 1 设置隔行背景 2 自适应行高 3 设置隔行背景同时自适应行高）
     * @param autoRowHeight 需要自适应行高的行号，为空则表示全部行都需要自适应
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, int type,List<Integer> autoRowHeight, HttpServletResponse response){
        ExportParams params = new ExportParams(title, sheetName, ExcelType.HSSF);
        params.setStyle(ExcelStyleUtil.class);
        Workbook workbook = ExcelExportUtil.exportExcel(params,pojoClass,list);
        if (workbook != null);
        ExcelStyleUtil.setStyleByType(workbook,null,type,autoRowHeight);
        downLoadExcel(fileName, response, workbook);
    }

    /**
     * excel 根据 type 导出
     * @param list 数据
     * @param pojoClass pojo类型
     * @param fileName 文件名称
     * @param type 类型（0 无 1 设置隔行背景 2 自适应行高 3 设置隔行背景同时自适应行高）
     * @param autoRowHeight 需要自适应行高的行号，为空则表示全部行都需要自适应
     */
    public static void exportExcel(List<?> list, Class<?> pojoClass, String fileName, int type,List<Integer> autoRowHeight, HttpServletResponse response){
        ExportParams params = new ExportParams();
        params.setTitleHeight((short) 15);
        params.setStyle(ExcelStyleUtil.class);
        params.setType(ExcelType.HSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(params,pojoClass,list);
        if (workbook != null);
        ExcelStyleUtil.setStyleByType(workbook,null,type,autoRowHeight);
        downLoadExcel(fileName, response, workbook);
    }

    /**
     * excel 导出
     * @param list           数据
     * @param title          标题
     * @param sheetName      sheet名称
     * @param pojoClass      pojo类型
     * @param fileName       文件名称
     */
    public static void exportExcel(List<?> list, String title, String sheetName, Class<?> pojoClass, String fileName, HttpServletResponse response){
        ExportParams exportParams = new ExportParams(title, sheetName, ExcelType.HSSF);
        exportParams.setTitleHeight((short) 15);
        exportParams.setStyle(ExcelStyleUtil.class);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,pojoClass,list);
        downLoadExcel(fileName, response, workbook);
    }

    /**
     * excel 导出
     * @param list           数据
     * @param pojoClass      pojo类型
     * @param fileName       文件名称
     */
    public static void exportExcel(List<?> list, Class<?> pojoClass, String fileName, HttpServletResponse response){
        ExportParams exportParams = new ExportParams();
        exportParams.setTitleHeight((short) 15);
        exportParams.setStyle(ExcelStyleUtil.class);
        exportParams.setType(ExcelType.HSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,pojoClass,list);
        downLoadExcel(fileName, response, workbook);
    }

    /**
     * list map 导出
     * @param list     数据
     * @param fileName 文件名称
     */
    public static void exportExcel(List<Map<String, Object>> list, String fileName, HttpServletResponse response){
        Workbook workbook = ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
        downLoadExcel(fileName, response, workbook);
    }

    /**
     * excel 导出，自定义表头导出
     * @param entityList     自定义的表头数据
     * @param list           数据
     * @param fileName       文件名称
     */
    public static void exportExcel(List<ExcelExportEntity> entityList,List<?> list, String fileName, HttpServletResponse response){
        ExportParams exportParams = new ExportParams();
        exportParams.setTitleHeight((short) 15);
        exportParams.setStyle(ExcelStyleUtil.class);
        exportParams.setType(ExcelType.HSSF);
        List<Map<String, Object>> result = objectToMap(list);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, entityList, result);
        downLoadExcel(fileName, response,workbook);
    }

    /**
     * 设置注解参数
     * @param annotation  注解
     * @param entity
     */
    private static void annotationParams(Excel annotation,ExcelExportEntity entity){
        if (annotation.addressList()){
            entity.setAddressList(annotation.addressList());
            entity.setReplace(annotation.replace());
        }
        entity.setReplace(annotation.replace());
        entity.setOrderNum(Integer.parseInt(annotation.orderNum()));
        entity.setGroupName(annotation.groupName());
        entity.setNeedMerge(annotation.needMerge());
        entity.setMergeVertical(annotation.mergeVertical());
    }

    /**
     * 动态导出列，根据Excel注解获取列的字段注释（表头名）、宽度
     * @param clazz
     * @param fields 选择要导出的列
     * @param changeHead 要更改表头的列，格式是{"字段1":"更改的表头1","字段2":"更改的表头2"}
     */
    public static List<ExcelExportEntity> dynamicExport(Class<?> clazz,String fields, Map<String,String> changeHead) {
        List<ExcelExportEntity> beanList = new ArrayList<>();
        String[] split = fields.split(",");
        int length = split.length;
        try {
            for (int i = 0; i < length; i++) {
                Field f = clazz.getDeclaredField(split[i]);
                Excel annotation = f.getAnnotation((Excel.class));
                String comment = annotation.name();
                if (changeHead != null && Objects.nonNull(changeHead.get(f.getName()))){
                    comment = changeHead.get(f.getName()).toString();
                }
                Double width = annotation.width();
                ExcelExportEntity entity = new ExcelExportEntity(comment, f.getName(), width.intValue());
                annotationParams(annotation,entity);
                beanList.add(entity);
            }
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }
        return beanList;
    }

    /**
     * 动态导出列（选择要忽略的列），根据Excel注解获取列的字段注释（表头名）、宽度
     * @param clazz
     * @param fields 选择要忽略的列
     * @param changeHead 要更改表头的列，格式是{"字段1":"更改的表头1","字段2":"更改的表头2"}
     */
    public static List<ExcelExportEntity> dynamicIgnoreExport(Class<?> clazz, String fields, Map<String,String> changeHead) {
        List<ExcelExportEntity> beanList = new ArrayList<>();
        String[] split = fields.split(",");
        int length = split.length;
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field f : declaredFields) {
            Excel annotation = f.getAnnotation((Excel.class));
            if (annotation != null){
                boolean flag = false;
                for (int i = 0; i < length; i++) {
                    if (f.getName().equals(split[i])){
                        flag = true;
                        break;
                    }
                }
                if (flag) continue;
                String comment = annotation.name();
                if (changeHead != null && Objects.nonNull(changeHead.get(f.getName()))){
                    comment = changeHead.get(f.getName()).toString();
                }
                Double width = annotation.width();
                ExcelExportEntity entity = new ExcelExportEntity(comment, f.getName(), width.intValue());
                annotationParams(annotation,entity);
                beanList.add(entity);
            }
        }
        return beanList;
    }

    /**
     * 实体bean转map，以map的形式导出表格
     * @param list 数据
     */
    public static <T> List<Map<String, Object>> objectToMap(List<T> list){
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> map = null;
        try {
            for (T item : list) {
                map = new HashMap<>();
                Class<?> clazz = item.getClass();
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Object value = field.get(item);
                    map.put(fieldName, value);
                }
                result.add(map);
            }
            return result;
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载
     * @param fileName 文件名称
     * @param response
     * @param workbook excel数据
     */
    public static void downLoadExcel(String fileName, HttpServletResponse response, Workbook workbook){
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8"); //设置编码字符
            response.setContentType("application/octet-stream"); //设置内容类型为下载类型
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出Excel，并在最后追加图片
     * @param sheetName  sheet名称
     * @param workbook   HSSFWorkbook对象
     * @param imgBase64  图片base64
     */
    public static Workbook getHSSFWorkbook(String sheetName,Workbook workbook, String imgBase64,int[] anchors) throws IOException {
        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if (workbook == null) {
            workbook = new HSSFWorkbook();
        }
        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        Sheet sheet = workbook.getSheet(sheetName);
        /*生成图表*/
        if(!StringUtils.isEmpty(imgBase64)) {
            String[] imgUrlArr = imgBase64.split("base64,");  //拆分base64编码后部分
            byte[] buffer = Base64.getDecoder().decode(imgUrlArr[1]);
            String picPath = System.getProperty("user.dir")+"\\upload\\excel\\pic.png"; // 图片临时路径
            File file = new File(picPath); //图片文件
            try {
                //生成图片
                OutputStream out = new FileOutputStream(file);//图片输出流
                out.write(buffer);
                out.flush();//清空流
                out.close();//关闭流
                ByteArrayOutputStream outStream = new ByteArrayOutputStream(); // 将图片写入流中
                BufferedImage bufferImg = ImageIO.read(new File(picPath));
                ImageIO.write(bufferImg, "PNG", outStream);
                Drawing<?> patri = sheet.createDrawingPatriarch(); // 利用HSSFPatriarch将图片写入EXCEL
                //HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 0, 8, (short) 10, 36);
                //ClientAnchor anchor = patri.createAnchor(0, 0, 0, 0, 0, 8, 13, 36);
                ClientAnchor anchor = patri.createAnchor(anchors[0], anchors[1], anchors[2], anchors[3], anchors[4], anchors[5], anchors[6], anchors[7]);
                patri.createPicture(anchor, workbook.addPicture(outStream.toByteArray(), HSSFWorkbook.PICTURE_TYPE_PNG));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (file.exists()) {
                file.delete();//删除图片
            }
        }
        return workbook;
    }


    // ********************************************* 导入 *********************************************

    /**
     * excel 导入
     * @param filePath   excel文件路径
     * @param titleRows  标题行
     * @param headerRows 表头行
     * @param pojoClass  pojo类型
     */
    public static <T> List<T> importExcel(String filePath, Integer titleRows, Integer headerRows, Class<T> pojoClass) throws IOException {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setNeedSave(false);
        try {
            return ExcelImportUtil.importExcel(new File(filePath), pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new IOException("模板不能为空");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * excel 导入
     * @param inputStream 文件输入流
     * @param titleRows   标题行
     * @param headerRows  表头行
     * @param needVerify  是否检验excel内容
     * @param pojoClass   pojo类型
     */
    public static <T> List<T> importExcel(InputStream inputStream, Integer titleRows, Integer headerRows, boolean needVerify, Class<T> pojoClass) throws IOException {
        if (inputStream == null) {
            return null;
        }
        ImportParams params = new ImportParams();
        params.setTitleRows(titleRows);
        params.setHeadRows(headerRows);
        params.setNeedSave(false);
        params.setNeedVerify(needVerify);
        try {
            return ExcelImportUtil.importExcel(inputStream, pojoClass, params);
        } catch (NoSuchElementException e) {
            throw new IOException("excel文件不能为空");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 上传文件，返回一个workbook
     */
    public static Workbook importExcel(InputStream inputStream, String fileName) throws IOException {
        Workbook workbook = null;
        if(fileName.endsWith("xls")){
            workbook = new HSSFWorkbook(inputStream);
        }else if(fileName.endsWith("xlsx")){
            workbook = new XSSFWorkbook(inputStream);
        }else {
            throw new RuntimeException("请确认你上传的文件类型");
        }
        return workbook;
    }

    /**
     * 读取指定sheet的数据
     * @param sheetName 要读取的sheetName
     * @param titleRows 表头行数
     * @param headRows 标题行数
     * @param startRows 表头之前有多少行不要的数据，从1开始，忽略空行；不指定时默认为0
     * @param readRows 要读取多少行数据，从0开始，比如读取十行，值就是9; 不指定时默认为0
     * @param pojoClass 实体
     */
    public static <T> List<T> importExcel(InputStream inputStream,String fileName,String sheetName,Integer titleRows,Integer headRows, Integer startRows,Integer readRows,Class<T> pojoClass) throws Exception {
        Workbook workbook = importExcel(inputStream,fileName);
        int numberOfSheets = workbook.getNumberOfSheets();
        List<T> list = null;
        for (int i = 0; i < numberOfSheets; i++) {
            String name = workbook.getSheetName(i).trim();
            if (name.equals(sheetName) || name.endsWith(sheetName)){
                ImportParams params = new ImportParams();
                params.setTitleRows(titleRows);
                params.setHeadRows(headRows);
                params.setStartRows(startRows);
                params.setReadRows(readRows);
                //第几个sheet页
                params.setStartSheetIndex(i);
                final ExcelImportService excelImportService = new ExcelImportService();
                ExcelImportResult<T> result = excelImportService.importExcelByIs(inputStream, pojoClass, params, false);
                list = result.getList();
                break;
            }
        }
        return list;
    }
}
