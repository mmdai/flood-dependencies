package cn.flood.base.easypoi.utils;

import cn.flood.base.easypoi.entity.ColEntity;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * excel poi 处理工具
 * 概念-> 表头数据:报表的表头
 * 行内数据:表头以下的数据
 * 功能:动态生成单级，多级Excel表头，多个sheet，纵向合并单元格
 * 备注：tree型结构数据的root节点的id默认为零（0）
 */
public class ExcelPoiUtil<T> {

    /**
     * excel 对象
     */
    private HSSFWorkbook workbook;
    /**
     * 表格标题
     */
    private String title;
    /**
     * 表头样式
     */
    private CellStyle styleHead;
    /**
     * 主体样式
     */
    private CellStyle styleBody;
    /**
     * 日期格式化,默认yyyy-MM-dd HH:mm:ss
     */
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public HSSFWorkbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(HSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CellStyle getStyleHead() {
        return styleHead;
    }

    public void setStyleHead(CellStyle styleHead) {
        this.styleHead = styleHead;
    }

    public CellStyle getStyleBody() {
        return styleBody;
    }

    public void setStyleBody(CellStyle styleBody) {
        this.styleBody = styleBody;
    }

    public SimpleDateFormat getSdf() {
        return sdf;
    }

    public void setSdf(SimpleDateFormat sdf) {
        this.sdf = sdf;
    }

    /**
     * 无参数 初始化 对象
     */
    public ExcelPoiUtil() {
        this.title = "sheet1";
        this.workbook = new HSSFWorkbook();
        init();
    }

    public ExcelPoiUtil(String title) {
        this.title = title;
        this.workbook = new HSSFWorkbook();
        init();
    }

    /**
     * 内部统一调用的样式初始化
     */
    private void init() {
        this.styleHead = ExcelStyleUtil.getStyles(workbook,2);
        this.styleBody = ExcelStyleUtil.getStyles(workbook,3);
    }

    /**
     * 返回workbook
     * @param listTpamsColEntity 表头数据
     * @param datas           行内数据
     */
    public HSSFWorkbook exportWorkbook(List<ColEntity> listTpamsColEntity, List<T> datas) throws Exception {
        splitDataToSheets(this.title,datas, listTpamsColEntity,null, false);
        ExcelStyleUtil.setStyleByType(this.workbook,null,0,null);
        return this.workbook;
    }

    /**
     * 返回workbook
     * @param listTpamsColEntity 表头数据
     * @param datas           行内数据
     * @param mergeIndex      需要纵向合并的单元格列号（默认有横向合并）
     */
    public HSSFWorkbook exportWorkbook(List<ColEntity> listTpamsColEntity, List<T> datas,List<Integer> mergeIndex) throws Exception {
        splitDataToSheets(this.title,datas, listTpamsColEntity, mergeIndex,false);
        ExcelStyleUtil.setStyleByType(this.workbook,null,0,null);
        return this.workbook;
    }

    /**
     * 返回workbook
     * @param listTpamsColEntity 表头数据
     * @param datas           行内数据
     * @param type            类型（0 默认 1 设置隔行背景 2 自适应行高 3 设置隔行背景同时自适应行高）
     * @param autoRowHeight   需要自适应行高的行号
     */
    public HSSFWorkbook exportWorkbook(List<ColEntity> listTpamsColEntity, List<T> datas,int type,List<Integer> autoRowHeight) throws Exception {
        splitDataToSheets(this.title,datas, listTpamsColEntity,null, false);
        ExcelStyleUtil.setStyleByType(this.workbook,null,type,autoRowHeight);
        return this.workbook;
    }

    /**
     * 返回workbook
     * @param listTpamsColEntity 表头数据
     * @param datas           行内数据
     * @param type            类型（0 默认 1 设置隔行背景 2 自适应行高 3 设置隔行背景同时自适应行高）
     * @param autoRowHeight   需要自适应行高的行号
     * @param mergeIndex      需要纵向合并的单元格列号（默认有横向合并）
     */
    public HSSFWorkbook exportWorkbook(List<ColEntity> listTpamsColEntity, List<T> datas,int type,List<Integer> autoRowHeight,List<Integer> mergeIndex) throws Exception {
        splitDataToSheets(this.title,datas, listTpamsColEntity,mergeIndex, false);
        ExcelStyleUtil.setStyleByType(this.workbook,null,type,autoRowHeight);
        return this.workbook;
    }

    /**
     * 返回workbook（多个sheet），这里全部sheet都用的同一个样式
     * @param titles          表头数据（key为sheet名称，value为表头数据）
     * @param datas           行内数据（key为sheet名称，value为行内数据）
     * @param type            样式类型，每个sheet都用这个（0 默认 1 设置隔行背景 2 自适应行高 3 设置隔行背景同时自适应行高）
     * @param autoRowHeight  需要自适应行高的行号，每个sheet都用这个
     * @param mergeIndex     需要纵向合并的单元格列号（默认有横向合并），每个sheet都用这个
     */
    public HSSFWorkbook exportWorkbook(Map<String,List<ColEntity>> titles, Map<String,List<T>> datas,int type,List<Integer> autoRowHeight,List<Integer> mergeIndex) throws Exception {
        for (String sheetName : titles.keySet()) {
            List<ColEntity> colEntityList = titles.get(sheetName);
            List<T> tList = datas.get(sheetName);
            splitDataToSheets(sheetName,tList, colEntityList,mergeIndex,false);
        }
        ExcelStyleUtil.setStyleByType(this.workbook,null,type,autoRowHeight);
        return this.workbook;
    }

    /**
     * 返回workbook（多个sheet），这里多个sheet都有不同样式
     * @param titles          表头数据（key为sheet名称，value为表头数据）
     * @param datas           行内数据（key为sheet名称，value为行内数据）
     * @param types           每个sheet的类型，key为sheet名称（0 默认 1 设置隔行背景 2 自适应行高 3 设置隔行背景同时自适应行高）
     * @param autoRowHeights  每个sheet需要自适应行高的行号
     * @param mergeIndexs     每个sheet需要纵向合并的单元格列号（默认有横向合并）
     */
    public HSSFWorkbook exportWorkbook(Map<String,List<ColEntity>> titles, Map<String,List<T>> datas,Map<String,Integer> types,
                                       Map<String,List<Integer>> autoRowHeights,Map<String,List<Integer>> mergeIndexs) throws Exception {
        for (String sheetName : titles.keySet()) {
            List<ColEntity> colEntityList = titles.get(sheetName);
            List<T> tList = datas.get(sheetName);
            int type = types == null ? 0 : types.get(sheetName) == null ? 0 : types.get(sheetName);
            List<Integer> autoRowHeight = autoRowHeights == null ? null : autoRowHeights.get(sheetName);
            List<Integer> mergeIndex = mergeIndexs == null ? null : mergeIndexs.get(sheetName);
            splitDataToSheets(sheetName,tList, colEntityList,mergeIndex,false);
            ExcelStyleUtil.setStyleByType(this.workbook,sheetName,type,autoRowHeight);
        }
        return this.workbook;
    }

    /**
     * 保存excel到本机指定的路径
     * @param workbook
     * @param filePath
     */
    public void save(HSSFWorkbook workbook, String filePath) {
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            workbook.write(fOut);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (null != fOut) {
                fOut.close();
            }
        } catch (Exception e1) {
        }
    }

    /**
     * 导出Excel,适用于web导出excel
     * @param sheet           excel
     * @param data            行内数据
     * @param headerCellList 表头数据
     * @param mergeIndex      需要纵向合并的单元格列号（默认有横向合并）
     * @param rowFlag         输出展示数据的结构(表头下面行的数据)
     */
    private void writeSheet(HSSFSheet sheet, List<T> data, List<ColEntity> headerCellList,List<Integer> mergeIndex,boolean rowFlag) throws Exception {
        sheet = createHead(sheet, headerCellList.get(0).getTotalRow(), headerCellList.get(0).getTotalCol());
        createHead(headerCellList, sheet, 0);
        writeSheetContent(headerCellList, data, sheet, headerCellList.get(0).getTotalRow(),mergeIndex, rowFlag);
    }

    /**
     * 拆分sheet，因为每个sheet不能超过65535，否则会报异常
     * @param sheetName       sheet名称
     * @param data            行内数据
     * @param headerCellList 表头数据
     * @param mergeIndex      需要纵向合并的单元格列号（默认有横向合并）
     * @param rowFlag         输出展示数据的结构(表头下面行的数据)
     */
    private void splitDataToSheets(String sheetName,List<T> data, List<ColEntity> headerCellList,List<Integer> mergeIndex,boolean rowFlag) throws Exception {
        int dataCount = data.size();
        int maxColEntity = 65535;
        int pieces = dataCount / maxColEntity;
        for (int i = 1; i <= pieces; i++) {
            HSSFSheet sheet = this.workbook.createSheet(sheetName + i);
            List<T> subList = data.subList((i - 1) * maxColEntity, i * maxColEntity);
            writeSheet(sheet, subList, headerCellList,mergeIndex,rowFlag);
        }
        HSSFSheet sheet = this.workbook.createSheet(sheetName);
        writeSheet(sheet, data.subList(pieces * maxColEntity, dataCount), headerCellList,mergeIndex,rowFlag);
    }

    /**
     * 把数据写入到单元格
     * @param headerCellList 表头数据
     * @param datas           行内数据
     * @param sheet           工作表（excel分页）
     * @param mergeIndex      需要纵向合并的单元格列号（默认有横向合并）
     * @throws Exception void
     */
    private void writeSheetContent(List<ColEntity> headerCellList, List<T> datas, HSSFSheet sheet, int rowIndex,List<Integer> mergeIndex, boolean rowFlag) throws Exception {
        boolean isMerge = false;// 是否纵向合并单元格
        if (mergeIndex != null && !mergeIndex.isEmpty()) isMerge = true;
        HSSFRow row = null;
        List<ColEntity> listCol = new ArrayList<>();
        rowFlag = false;
        if (rowFlag) {//暂时没有用 后面扩展用
            for (int i = 0, index = rowIndex; i < datas.size(); i++, index++) {
                row = sheet.createRow(index);//创建行
                for (int j = 0; j < headerCellList.size(); j++) {
                    createColl(row, j, headerCellList.get(j).getFieldName(), datas.get(i));
                }
            }
        } else {
            getColEntityList(headerCellList, listCol);
            Map<Integer,  Map<Integer, String>> mergeMaps = new HashMap<>();// 需要合并的列：key 列号，value为单元格内容
            Map<Integer, String> mergeMap = null;// 需要合并的行：key 行号 value 为单元格内容
            for (int i = 0, index = rowIndex; i < datas.size(); i++, index++) {
                row = sheet.createRow(index);//创建行
                for (int j = 0; j < listCol.size(); j++) {
                    ColEntity c = listCol.get(j);
                    //数据列
                    HSSFCell col = createCol(row, c, datas.get(i));
                    if (col.toString().length()>0){
                        // 需要合并 并且 当前单元格所在的列包含在要合并的列中
                        if (isMerge && mergeIndex.contains(c.getCol())){
                            if (mergeMaps.get(c.getCol()) != null){ // 如果要合并的列已经有了，则直接去拿该列的数据
                                mergeMap = mergeMaps.get(c.getCol());
                            }else {
                                mergeMap = new HashMap<>();
                            }
                            // 当前行号为key，当前单元格内容为value
                            mergeMap.put(index,col.toString()); // 将当前单元格的内容添加到当前行号中
                            mergeMaps.put(c.getCol(),mergeMap);
                        }
                    }
                }
            }
            if (isMerge) mergedCells(mergeMaps,sheet);
        }
    }

    /**
     * 纵向合并单元格
     * @param mergeMaps 需要合并的列：key 要合并的列号，value为单元格内容
     * @param sheet
     */
    private void mergedCells(Map<Integer,  Map<Integer, String>> mergeMaps,HSSFSheet sheet){
        for (Integer colNum : mergeMaps.keySet()) { // 遍历要合并的列，获取每一列的每一行
            Map<Integer, String> mergeMap = mergeMaps.get(colNum);// 当前这列每一行的内容：key为行号，value为单元格内容
            // 根据mergeMap的value，也就是单元格内容进行分组，每一组都是需要合并在一起的单元格（要合并的区域）
            Map<String, List<Map.Entry<Integer,String>>>result = mergeMap.entrySet().stream().collect(Collectors.groupingBy(c -> c.getValue()));
            System.out.println("\n合并的列号："+colNum);
            System.out.println("合并的区域："+result);
            for (String key : result.keySet()) {
                // list为这一组要合并的几个单元格
                List<Map.Entry<Integer, String>> list = result.get(key);
                int start = list.get(0).getKey(); // 开始合并的行号
                int end = list.get(list.size()-1).getKey(); // 结束合并的行号
                System.out.println("第"+colNum+"列开始合并的行号："+start+"\t第"+colNum+"列结束合并的行号："+"\t"+end+"。");
                if (start < end){ // 开始合并的行号必须小于结束合并的行号
                    sheet.addMergedRegion(new CellRangeAddress(start, end, colNum,colNum));
                }
            }
        }
    }

    /**
     * 根据list 来创建单元格 暂时没有用
     * @param row
     * @param j
     * @param finame
     * @param t
     */
    private void createColl(HSSFRow row, int j, String finame, T t) {
        HSSFCell cell = row.createCell(j);  //创建单元格
        cell.setCellStyle(this.styleBody); //设置单元格样式
        String text = "";
        if (t instanceof List) {
            List<Map> temp = (List<Map>) t;
            if (j >= temp.size()) {
                return;
            }
            text = String.valueOf(temp.get(j).get(finame) == null ? "" : temp.get(j).get(finame));
        }
        HSSFRichTextString richString = new HSSFRichTextString(text);
        cell.setCellValue(richString);
    }

    /**
     * 把ColEntity的ColEntityList整理成一个list<ColEntity> 过滤表头的脏数据
     * @param list    表头数据
     * @param listCol 返回新的list
     */
    private void getColEntityList(List<ColEntity> list, List<ColEntity> listCol) {
        for (ColEntity ColEntity : list) {
            if (ColEntity.getFieldName() != null) {
                listCol.add(ColEntity);
            }
            List<ColEntity> listChildren = ColEntity.getCellList();
            if (listChildren.size() > 0) {
                getColEntityList(listChildren, listCol);
            }
        }
    }

    /**
     * 创建行
     * @param row         Excel对应的行
     * @param tpamsColEntity 当前单元格属性
     * @param v
     * @param j
     * @return
     * @throws Exception
     */
    public int createRowVal(HSSFRow row, ColEntity tpamsColEntity, T v, int j) throws Exception {
        //遍历标题
        if (tpamsColEntity.getCellList() != null && tpamsColEntity.getCellList().size() > 0) {
            for (int i = 0; i < tpamsColEntity.getCellList().size(); i++) {
                createRowVal(row, tpamsColEntity.getCellList().get(i), v, j);
            }
        } else {
            createCol(row, tpamsColEntity, v);
        }
        return j;
    }

    /**
     * 创建单元格
     * @param row         Excel对应的行
     * @param colEntity 当前单元格对象
     * @param v
     * @throws Exception
     */
    public HSSFCell createCol(HSSFRow row, ColEntity colEntity, T v) throws Exception {
        HSSFCell cell = row.createCell(colEntity.getCol());  //创建单元格
        cell.setCellStyle(this.styleBody); //设置单元格样式
        final Object[] value = {null};
        if (v instanceof Map) {
            Map m = (Map) v;
            m.forEach((k, val) -> {
                if (k.equals(colEntity.getFieldName()) && !colEntity.isHasChildren()) {
                    value[0] = val;
                }
            });
        } else {
            Class<?> cls = v.getClass();// 拿到该类
            Field[] fields = cls.getDeclaredFields();// 获取实体类的所有属性，返回Field数组
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true); // 设置些属性是可以访问的
                if (colEntity.getFieldName().equals(f.getName()) && !colEntity.isHasChildren()){
                    value[0] = f.get(v);
                }
                if (value[0] instanceof Date) {
                    value[0] = parseDate((Date) value[0]);
                }
            }
        }
        if (value[0] != null) {
            HSSFRichTextString richString = new HSSFRichTextString(value[0].toString());
            cell.setCellValue(richString);
        }
        return cell;
    }

    /**
     * 时间转换
     * @param date
     * @return String
     */
    private String parseDate(Date date) {
        String dateStr = "";
        try {
            dateStr = this.sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }


    /**
     * 根据数据的行数和列数，在excel创建单元格cell
     * @param sheetCo excel分页
     * @param r       excel 行数
     * @param c       excel 列数
     * @return
     */
    public HSSFSheet createHead(HSSFSheet sheetCo, int r, int c) {
        for (int i = 0; i < r; i++) {
            HSSFRow row = sheetCo.createRow(i);
            for (int j = 0; j < c; j++) {
                HSSFCell cell = row.createCell(j);
            }
        }
        return sheetCo;
    }

    /**
     * 使用递归 在excel写入表头数据 支持单级，多级表头的创建
     * @param cellList 表头数据
     * @param sheetCo  哪个分页
     * @param rowIndex 当前Excel的第几行
     */
    public void createHead(List<ColEntity> cellList, HSSFSheet sheetCo, int rowIndex) {
        HSSFRow row = sheetCo.getRow(rowIndex);
        int len = cellList.size();//当前行 有多少列
        for (int i = 0; i < len; i++) {//i是headers的索引，n是Excel的索引 多级表头
            ColEntity colEntity = cellList.get(i);
            //创建这一行的第几列单元格
            int r = colEntity.getRow();
            int rLen = colEntity.getRLen();
            int c = colEntity.getCol();
            int cLen = colEntity.getCLen();
            int endR = r + rLen;
            //解决表头导出时多一行问题
            if(endR > r){
                endR--;
            }
            int endC = c + cLen;
            if (endC > c) {
                endC--;
            }
            HSSFCell cell = row.getCell(c);
            HSSFRichTextString text = new HSSFRichTextString(colEntity.getContent());
            cell.setCellStyle(this.styleHead); //设置表头样式
            cell.setCellValue(text);
            // 合并单元格
            CellRangeAddress cra = new CellRangeAddress(r, endR, c, endC);
            //todo debug
            if (cra.getNumberOfCells() > 1) {
                sheetCo.addMergedRegion(cra);
            }
            sheetCo.setColumnWidth(c,colEntity.getWidth()*256);// 设置列宽
            // 使用RegionUtil类为合并后的单元格添加边框
            RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheetCo); // 下边框
            RegionUtil.setBorderLeft(BorderStyle.THIN, cra, sheetCo); // 左边框
            RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheetCo); // 有边框
            if (colEntity.isHasChildren()) {
                rowIndex = r + 1;
                createHead(colEntity.getCellList(), sheetCo, rowIndex);
            }
        }
    }

    /**
     * 转换成ColEntity对象
     * 支持List<T>的数据结构:map String ，只能是单级的数据
     * @param list 需要转换的数据
     */
    public List<ColEntity> colEntityTransformer(List<T> list) {
        List<ColEntity> lc = new ArrayList<>();
        if (list.get(0) instanceof Map) {
            final int[] i = {1};
            for (Map<String, String> m : (List<Map<String, String>>) list) {
                m.forEach((k, val) -> {
                    ColEntity tpamsColEntity = new ColEntity();
                    tpamsColEntity.setId(String.valueOf(i[0]));
                    tpamsColEntity.setPid("0");
                    tpamsColEntity.setContent(k);
                    tpamsColEntity.setFieldName(val);
                    tpamsColEntity.setWidth(20);
                    lc.add(tpamsColEntity);
                    i[0]++;
                });
            }
        } else {
            int i = 1;
            for (String s : (List<String>) list) {
                ColEntity tpamsColEntity = new ColEntity();
                tpamsColEntity.setId(String.valueOf(i));
                tpamsColEntity.setPid("0");
                tpamsColEntity.setContent(s);
                tpamsColEntity.setFieldName(null);
                tpamsColEntity.setWidth(20);
                lc.add(tpamsColEntity);
                i++;
            }
        }
        setParm(lc, "0");//处理一下
        List<ColEntity> s = ExcelTreeUtil.buildByRecursive(lc, "0");
        setColNum(lc, s);
        return s;
    }

    /**
     * 转换成ColEntity对象 返回tree数据结构
     * 支持：List<map>、某个具体对象（entity）数据的转换
     * @param list  需要转换的数据
     * @param parm {id,pid,content,fieldName,width} 这几个字段对应的是封装表头实体类的字段，
     *             比如我用的是TitleEntity封装的表头，则这五个key对应的value就是TitleEntity的字段名。
     *             也就是说这五个key对应的value需要和第一个参数传入的 T（某个具体对象） 的字段对应。
     *             id：       当前节点id 字段的名称       主键
     *             pid：      当前节点的父节点id          字段名称
     *             content：  当前节点所在单元格的内容     字段名称
     *             fieldName：填充行内数据时，映射的字段名  字段名称
     *             width：    列宽                      字段名称
     * @param rootid    rootid的值
     */
    public List<ColEntity> colEntityTransformer(List<T> list, Map<String,String> parm,String rootid) throws Exception {
        List<ColEntity> lc = new ArrayList<>();
        if (list.get(0) instanceof Map) {
            for (Map m : (List<Map>) list) {
                ColEntity colEntity = new ColEntity();
                m.forEach((k, val) -> {
                    if (parm.get("id").equals(k)) {
                        colEntity.setId(String.valueOf(val));
                    }
                    if (parm.get("pid").equals(k)) {
                        colEntity.setPid((String) val);
                    }
                    if (parm.get("content").equals(k)) {
                        colEntity.setContent((String) val);
                    }
                    if (parm.get("fieldName") != null && parm.get("fieldName").equals(k)) {
                        colEntity.setFieldName((String) val);
                    }
                    if (parm.get("width") != null && parm.get("width").equals(k)) {
                        colEntity.setWidth(Integer.parseInt(val.toString()));
                    }
                });
                lc.add(colEntity);
            }
        } else {
            for (T t : list) { // 反射
                ColEntity colEntity = new ColEntity();
                Class cls = t.getClass();
                Field[] fs = cls.getDeclaredFields();
                for (int i = 0; i < fs.length; i++) {
                    Field f = fs[i];
                    f.setAccessible(true); // 设置些属性是可以访问的
                    if (parm.get("id").equals(f.getName()) && f.get(t) != null) {
                        colEntity.setId(f.get(t).toString());
                    }
                    if (parm.get("pid").equals(f.getName()) && f.get(t) != null) {
                        colEntity.setPid(f.get(t).toString());
                    }
                    if (parm.get("content").equals(f.getName()) && f.get(t) != null) {
                        colEntity.setContent(f.get(t).toString());
                    }
                    if (f.get(t) != null && parm.get("fieldName") != null && parm.get("fieldName").equals(f.getName())) {
                        colEntity.setFieldName(f.get(t).toString());
                    }
                    if (parm.get("width").equals(f.getName()) && f.get(t) != null) {
                        colEntity.setWidth(Integer.parseInt(f.get(t).toString()));
                    }
                }
                lc.add(colEntity);
            }
        }
        setParm(lc, rootid); // 处理基础参数
        List<ColEntity> s = ExcelTreeUtil.buildByRecursive(lc, rootid); // 构建树结构
        setColNum(lc, s);
        return s;
    }

    /**
     * 设置基础的参数
     * @param list
     */
    public static void setParm(List<ColEntity> list, String rootid) {
        int row = 0; //excel第几行
        int rLen = 0; //excel 跨多少行
        int totalRow = ExcelTreeUtil.getMaxStep(list);
        int totalCol = ExcelTreeUtil.getDownChildren(list, rootid);
        for (int i = 0; i < list.size(); i++) {
            ColEntity poit = list.get(i);
            int tree_step = ExcelTreeUtil.getTreeStep(list, poit.getPid(), 0);//往上遍历tree
            poit.setTreeStep(tree_step);
            poit.setRow(tree_step);//设置第几行
            //判断是否有节点
            boolean hasCh = ExcelTreeUtil.hasChild(list, poit);
            poit.setHasChildren(hasCh);
            if (hasCh) {
                poit.setRLen(0);//设置跨多少行
            } else {
                if (tree_step < totalRow) {
                    rLen = totalRow - tree_step;
                }
                poit.setRLen(rLen);
            }
            poit.setTotalRow(totalRow);
            poit.setTotalCol(totalCol);
        }
    }

    /**
     * 设置基础的参数
     * @param list     所有list数据，一条一条
     * @param treeList 转成tree结构的list
     */
    public static void setColNum(List<ColEntity> list, List<ColEntity> treeList) {
        //int col = pcIndex;//excel第几列
        //int cLen ;//xcel跨多少列
        List<ColEntity> new_list = new ArrayList<>();//新的遍历list
        for (int i = 0; i < treeList.size(); i++) {
            ColEntity poit = treeList.get(i);
            //String temp_id = ExcelTreeUtil.getStepParentId(list,poit.getId() ,1);
            int col = ExcelTreeUtil.getParentCol(list, poit.getPid()).getCol();
            int brotherCol = ExcelTreeUtil.getBrotherChilNum(list, poit);
            poit.setCol(col + brotherCol);
            int cLen = ExcelTreeUtil.getDownChildren(list, poit.getId());
            if (cLen <= 1) {
                cLen = 0;
            }
            //else  cLen--;
            poit.setCLen(cLen);//设置跨多少列
            if (poit.getCellList().size() > 0) {
                new_list.addAll(poit.getCellList());
            }
        }
        if (new_list.size() > 0) {
            setColNum(list, new_list);
        }
    }
}
