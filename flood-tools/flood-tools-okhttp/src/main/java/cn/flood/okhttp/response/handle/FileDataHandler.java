/**  
* <p>Title: FileDataHandler.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月25日  
* @version 1.0  
*/  
package cn.flood.okhttp.response.handle;

import java.io.File;
import java.io.IOException;

import cn.flood.io.FileUtils;
import cn.flood.lang.Assert;
import cn.flood.lang.StringUtils;
import cn.flood.okhttp.utils.Utils;
import okhttp3.Response;

/**  
* <p>Title: FileDataHandler</p>  
* <p>Description: </p>  
* @author mmdai  
* @date 2019年7月25日  
*/
public class FileDataHandler implements DataHandler<File> {

    /**
     * 保存的文件目录
     */
    private final String dirPath;

    /**
     * 保存的文件名
     */
    private String filename;

    public FileDataHandler(String dirPath) {
        Assert.notNull(dirPath, "DirPath may not be null.");
        this.dirPath = dirPath;
    }

    public FileDataHandler(String dirPath, String filename) {
        this(dirPath);
        this.filename = filename;
    }

    /**
     * 返回保存的文件目录
     *
     * @return 保存的文件目录
     */
    public String getDirPath() {
        return dirPath;
    }

    /**
     * 返回保存的文件名
     *
     * @return 保存的文件名
     */
    public String getFilename() {
        return filename;
    }

    /**
     * 设置保存的文件名
     *
     * @param filename 保存的文件名
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * 得到相应结果后,将相应数据转为需要的数据格式
     *
     * @param response 需要转换的对象
     * @return 存储的文件信息
     * @throws IOException 出现异常
     */
    @Override
    public File handle(final Response response) throws IOException {
        String name = this.filename;
        if (StringUtils.isEmpty(name)) name = Utils.getFilename(response);
        File saveFile = new File(this.dirPath, name);
        try {
			FileUtils.copyStream(response.body().byteStream(), saveFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return saveFile;
    }

}
