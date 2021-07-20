/**  
* <p>Title: FileRequestBody.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月23日  
* @version 1.0  
*/  
package cn.flood.okhttp.http;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import java.io.File;
import java.io.IOException;

/**  
* <p>Title: FileRequestBody</p>  
* <p>Description: 文件请求对象</p>  
* @author mmdai  
* @date 2019年7月23日  
*/
public class FileRequestBody extends RequestBody {
	
	private final File file;
    private final MediaType mediaType;

    public FileRequestBody(File file, MediaType mediaType) {
        this.file = file;
        this.mediaType = mediaType;
    }

    /**
     * Returns the number of bytes that will be written to {@code out} in a call to {@link #writeTo},
     * or -1 if that count is unknown.
     */
    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    /**
     * Returns the Content-Type header for this body.
     */
    @Override
    public MediaType contentType() {
        return this.mediaType;
    }

    /**
     * Writes the content of this request to {@code out}.
     */
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {
            source = Okio.source(file);
            sink.writeAll(source);
        } finally {
            Util.closeQuietly(source);
        }
    }

}
