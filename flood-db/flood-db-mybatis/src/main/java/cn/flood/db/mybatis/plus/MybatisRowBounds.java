package cn.flood.db.mybatis.plus;

import cn.flood.db.mybatis.plus.plugins.page.Page;
import org.apache.ibatis.session.RowBounds;

/**
 * @author mmdai
 */
public class MybatisRowBounds extends RowBounds {

    private Page<?> page;

    public MybatisRowBounds(Page<?> page) {
//        super((page.getPageNo()-1)*page.getPageSize(), page.getPageSize());
        this.page = page;
    }

    public Page<?> getPage() {
        return page;
    }
}
