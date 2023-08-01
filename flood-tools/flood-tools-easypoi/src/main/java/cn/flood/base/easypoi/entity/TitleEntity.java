package cn.flood.base.easypoi.entity;


/**
 * 表头的实体类： 在具体的项目里，可以是你从数据库里查询出来的数据
 */
public class TitleEntity {
    /**
     * id
     */
    public  String id;
    /**
     * 父级id
     */
    public  String pid;
    /**
     * 表头内容
     */
    public  String content;
    /**
     * 映射的字段名
     */
    public  String fieldName;
    /**
     * 列宽
     */
    public int width = 20;

    private TitleEntity(){}

    public TitleEntity(String id, String pid, String content, String fieldName, int width) {
        this.id = id;
        this.pid = pid;
        this.content = content;
        this.fieldName = fieldName;
        this.width = width;
    }

    public TitleEntity(String id, String pid, String content, String fieldName) {
        this.id = id;this.pid = pid;this.content = content;this.fieldName = fieldName;
    }

    public TitleEntity(String id, String pid, String content) {
        this.id = id;this.pid = pid;this.content = content;
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
