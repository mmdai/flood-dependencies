package cn.flood; /**
* <p>Title: TransactionBanner.java</p>
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年3月21日  
* @version 1.0  
*/

import cn.flood.banner.AbastractBanner;

/**  
* <p>Title: TransactionBanner</p>
* <p>Description: </p>  
* @author mmdai  
* @date 2019年3月21日  
*/
public class TransactionBanner extends AbastractBanner {

	protected String getTitle() {
        return "(PLATFORM : TRANSACTION-SERVER)";
    }

}
