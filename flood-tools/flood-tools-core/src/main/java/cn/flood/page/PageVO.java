package cn.flood.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 
* <p>Title: PageResult</p>  
* <p>Description: 分页实体类</p>  
* @author mmdai  
* @date 2018年11月8日
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO<T> implements Serializable {

	private static final long serialVersionUID = -275582248840137389L;
	//总数
	private Long total;
	//当前页结果集
	private List<T> data;
	
	//PageResult.<T>builder().data(list).count(total).build();
	public static <T> PageVO<T> of(Long total, List<T> list) {
		return new PageVO<T>(total, list);
	}
}
