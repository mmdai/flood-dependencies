/**  
* <p>Title: InputStreamRequestBody.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月23日  
* @version 1.0  
*/  
package cn.flood.okhttp.http;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.flood.io.IOUtils;
/**  
* <p>Title: InputStreamRequestBody</p>  
* <p>Description: Request body封装</p>  
* @author mmdai  
* @date 2019年7月23日  
*/
public class InputStreamRequestBody extends RequestBody {

    private final byte[] content;
    private final int byteCount;
    private final MediaType mediaType;

    public InputStreamRequestBody(InputStream content, MediaType mediaType){
        this.mediaType = mediaType;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            try {
				IOUtils.copy(content, baos);
			} catch (IOException e) {
			}
            this.content = baos.toByteArray();
            this.byteCount = this.content.length;
        } finally {
            IOUtils.closeQuietly(content);
        }
    }

    /**
     * Returns the number of bytes that will be written to {@code out} in a call to {@link #writeTo},
     * or -1 if that count is unknown.
     */
    @Override
    public long contentLength() throws IOException {
        return this.byteCount;
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
        sink.write(this.content, 0, this.byteCount);
    }
}
