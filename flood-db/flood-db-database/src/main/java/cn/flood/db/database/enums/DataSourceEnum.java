/**  
* <p>Title: DataSourceEnum.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2018年12月12日  
* @version 1.0  
*/  
package cn.flood.db.database.enums;

import java.util.stream.Stream;
/**  
* <p>Title: DataSourceEnum</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2018年12月12日  
*/
public enum DataSourceEnum {
	DB0(0),
	DB1(1),
	DB2(2),
	DB3(3),
	DB4(4),
	DB5(5),
	DB6(6),
	DB7(7),
	DB8(8),
	DB9(9);
	
	// 成员变量
    private int index;
	
	// 构造方法
	DataSourceEnum(int index) {
        this.index = index;
    }

    public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}



	public static DataSourceEnum valueOfEnum(int index) {
        return Stream.of(DataSourceEnum.values()).filter(eu -> eu.index == index).findFirst().orElse(null);
    }
}
