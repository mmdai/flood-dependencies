package cn.flood.base.easypoi.utils;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.entity.params.ExcelForEachParams;
import cn.afterturn.easypoi.excel.export.styler.IExcelExportStyler;
import org.apache.poi.ss.usermodel.*;

import java.util.List;

/**
 * excel导出样式设置工具类
 * 基础样式、自适应行高、隔行背景色
 */
public class ExcelStyleUtil implements IExcelExportStyler {
    private ExcelStyleUtil(){}

    private static final short STRING_FORMAT = (short) BuiltinFormats.getBuiltinFormat("TEXT");
    private static final short FONT_SIZE_TEN = 10;
    private static final short FONT_SIZE_ELEVEN = 11;
    private static final short FONT_SIZE_TWELVE = 12;
    private static final short height = 30;

    /**
     * 大标题样式
     */
    private CellStyle headerStyle;
    /**
     * 每列标题样式
     */
    private CellStyle titleStyle;
    /**
     * 数据行样式
     */
    private CellStyle styles;

    public ExcelStyleUtil(Workbook workbook) {
        this.init(workbook);
    }

    /**
     * 初始化样式
     * @param workbook
     */
    private void init(Workbook workbook) {
        this.headerStyle = initHeaderStyle(workbook);
        this.titleStyle = initTitleStyle(workbook,true,FONT_SIZE_ELEVEN);
        this.styles = initStyles(workbook);
    }

    @Override
    public CellStyle getHeaderStyle(short i) {
        return headerStyle;
    }

    @Override
    public CellStyle getTitleStyle(short i) {
        return titleStyle;
    }

    @Override
    public CellStyle getTemplateStyles(boolean b, ExcelForEachParams excelForEachParams) {
        return null;
    }

    @Override
    public CellStyle getStyles(boolean b, ExcelExportEntity excelExportEntity) {
        return styles;
    }

    @Override
    public CellStyle getStyles(Cell cell, int i, ExcelExportEntity entity, Object o, Object o1) {
        return getStyles(true, entity);
    }

    /**
     * 获取样式
     * @param style 1 大标题样式 2 表头样式 3 内容样式
     */
    public static CellStyle getStyles(Workbook workbook,int style) {
        CellStyle cellStyle = null;
        switch (style){
            case 1:
                cellStyle = initHeaderStyle(workbook);
                break;
            case 2:
                cellStyle = initTitleStyle(workbook,true,FONT_SIZE_ELEVEN);
                break;
            case 3:
                cellStyle = initStyles(workbook);
                break;
            default:
                cellStyle = initStyles(workbook);
                break;
        }
        cellStyle.setDataFormat(STRING_FORMAT);
        return cellStyle;
    }

    /**
     * 初始化--大标题样式
     */
    private static CellStyle initHeaderStyle(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);
        style.setFont(getFont(workbook, FONT_SIZE_TWELVE, true));
        return style;
    }

    /**
     * 初始化--每列标题样式
     */
    private static CellStyle initTitleStyle(Workbook workbook,boolean isBold,short size) {
        CellStyle style = getBaseCellStyle(workbook);
        style.setFont(getFont(workbook, size, isBold));
        //背景色
        style.setFillForegroundColor(IndexedColors.TAN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * 初始化--数据行样式
     */
    private static CellStyle initStyles(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);
        style.setFont(getFont(workbook, FONT_SIZE_TEN, false));
        style.setDataFormat(STRING_FORMAT);
        return style;
    }

    /**
     * 设置隔行背景色
     */
    public static CellStyle getRowBackground(Workbook workbook) {
        CellStyle style = getBaseCellStyle(workbook);
        style.setFont(getFont(workbook, FONT_SIZE_TEN,false));
        //背景色
        style.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setDataFormat(STRING_FORMAT);
        return style;
    }

    /**
     * 基础样式
     */
    private static CellStyle getBaseCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        //下边框
        style.setBorderBottom(BorderStyle.THIN);
        //左边框
        style.setBorderLeft(BorderStyle.THIN);
        //上边框
        style.setBorderTop(BorderStyle.THIN);
        //右边框
        style.setBorderRight(BorderStyle.THIN);
        //水平居中
        style.setAlignment(HorizontalAlignment.CENTER);
        //上下居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //设置自动换行
        style.setWrapText(true);
        return style;
    }

    /**
     * 字体样式
     * @param size   字体大小
     * @param isBold 是否加粗
     */
    private static Font getFont(Workbook workbook, short size, boolean isBold) {
        Font font = workbook.createFont();
        //字体样式
        font.setFontName("宋体");
        //是否加粗
        font.setBold(isBold);
        //字体大小
        font.setFontHeightInPoints(size);
        return font;
    }

    /**
     * 根据type设置workbook
     * @param workbook
     * @param type 类型（0 默认 1 设置隔行背景 2 自适应行高 3 设置隔行背景同时自适应行高）
     * @param autoRowHeight 需要自适应行高的行号
     */
    public static void setStyleByType(Workbook workbook,String sheetName,int type,List<Integer> autoRowHeight){
        if (sheetName != null && sheetName.length()>0){
            Sheet sheet = workbook.getSheet(sheetName);
            setSheetStyleByType(workbook,sheet,type,autoRowHeight);
        }else {
            int sheetNum = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetNum; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                setSheetStyleByType(workbook,sheet,type,autoRowHeight);
            }
        }
    }

    public static void setSheetStyleByType(Workbook workbook,Sheet sheet,int type,List<Integer> autoRowHeight){
        if (type==1 || type == 3){ // 1和3，设置隔行背景
            setRowBackground(workbook,sheet);
        }
        int lastRowNum = sheet.getLastRowNum();
        for(int j = 0; j <= lastRowNum; j++) {
            Row row = sheet.getRow(j);
            row.setHeightInPoints(height); // 默认行高
            if (type==2 || type == 3){ // 2和3，设置自适应行高
                if (autoRowHeight != null && autoRowHeight.contains(j)){ // 不为空，则只有指定行号的行需要自适应行高
                    autoRowHeight(row);
                }else { // 为null，则表示全部行都需要自适应行高
                    autoRowHeight(row);
                }
            }
        }
    }

    /**
     * 偶数行设置背景色
     */
    public static void setRowBackground(Workbook workbook,Sheet sheet){
        CellStyle styles = getRowBackground(workbook);
        for(int i = 0; i <= sheet.getLastRowNum(); i ++) {
            if (i%2==0 && i>0){ // 标题用全局的标题样式，就不单独设置样式了，所以排除标题
                Row row = sheet.getRow(i);
                for(int j = 0; j < row.getPhysicalNumberOfCells(); j ++) {
                    Cell cell = row.getCell(j);
                    cell.setCellStyle(styles);
                }
            }
        }
    }

    /**
     * 设置自适应行高
     */
    public static void autoRowHeight(Row row){
        //根据内容长度设置行高
        int enterCnt = 0;
        for(int j = 0; j < row.getPhysicalNumberOfCells(); j ++) {
            Cell cell = row.getCell(j);
            if (cell != null){
                int rwsTemp = row.getCell(j).toString().length();
                //这里取每一行中的每一列字符长度最大的那一列的字符
                if (rwsTemp > enterCnt) {
                    enterCnt = rwsTemp;
                }
            }
        }
        row.setHeightInPoints(height); // 设置默认行高为35
        //如果字符长度大于35，根据内容来设置相应的行高
        if (enterCnt>height){
            long d = Math.round((double) enterCnt / (double) height)+2;
            row.setHeightInPoints(enterCnt*d);
        }
    }
}
