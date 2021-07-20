/**  
* <p>Title: PageDTO.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年4月18日  
* @version 1.0  
*/  
package cn.flood.page;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**  
* <p>Title: PageDTO</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年4月18日  
*/
@Data
@ToString
public class PageDTO implements Serializable {
	
	/** serialVersionUID*/  
	private static final long serialVersionUID = -7826768548777861694L;

	private Integer pageno = 1;
	
	private Integer pagesize = 10;

}
