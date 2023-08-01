package cn.flood.base.easypoi.entity;


import java.util.ArrayList;
import java.util.List;

/**
 * 单元格
 */
public class ColEntity {
    /**
     * 单元格内容
     */
    private String content;
    /**
     * 字段名称，用户导出表格时反射调用
     */
    private String fieldName;
    /**
     * 这个单元格的集合
     */
    private List<ColEntity> cellList = new ArrayList<ColEntity>();
    /**
     * 总行数
     */
    private int totalRow;
    /**
     * 总列数
     */
    private int totalCol;
    /**
     * excel第几行
     */
    private int row;
    /**
     * excel第几列
     */
    private int col;
    /**
     * excel 跨多少行
     */
    private int rLen;
    /**
     * excel跨多少列
     */
    private int cLen;
    /**
     * 是否有子节点
     */
    private boolean hasChildren;
    /**
     * 树的级别 从0开始
     */
    private int treeStep;
    /**
     * 树的id
     */
    private String id;
    /**
     * 树的父级id
     */
    private String pid;
    /**
     * 列宽
     */
    private int width;

    public ColEntity() {}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public List<ColEntity> getCellList() {
        return cellList;
    }

    public void setCellList(List<ColEntity> cellList) {
        this.cellList = cellList;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public int getTotalCol() {
        return totalCol;
    }

    public void setTotalCol(int totalCol) {
        this.totalCol = totalCol;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRLen() {
        return rLen;
    }

    public void setRLen(int rLen) {
        this.rLen = rLen;
    }

    public int getCLen() {
        return cLen;
    }

    public void setCLen(int cLen) {
        this.cLen = cLen;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public int getTreeStep() {
        return treeStep;
    }

    public void setTreeStep(int treeStep) {
        this.treeStep = treeStep;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}