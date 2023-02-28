/**
 * <p>Title: PageDTO.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2018</p>
 *
 * @author mmdai
 * @date 2019年4月18日
 * @version 1.0
 */
package cn.flood.base.core.page;

import java.io.Serializable;
import lombok.ToString;

/**
 * <p>Title: PageDTO</p>  
 * <p>Description: </p>  
 * @author mmdai
 * @date 2019年4月18日
 */
@ToString
public class PageDTO implements Serializable {

  /** serialVersionUID*/
  private static final long serialVersionUID = -7826768548777861694L;

  private Integer pageno = 1;

  private Integer pagesize = 10;

  public Integer getPageno() {
    return pageno;
  }

  public void setPageno(Integer pageno) {
    this.pageno = pageno;
  }

  public Integer getPagesize() {
    return pagesize > 100 ? 100 : pagesize;
  }

  public void setPagesize(Integer pagesize) {
    this.pagesize = pagesize;
  }
}
