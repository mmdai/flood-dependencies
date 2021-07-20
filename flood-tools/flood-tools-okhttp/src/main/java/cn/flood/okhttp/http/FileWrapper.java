/**  
* <p>Title: FileWrapper.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月23日  
* @version 1.0  
*/  
package cn.flood.okhttp.http;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.File;
import java.io.InputStream;

import cn.flood.lang.Assert;
import cn.flood.okhttp.exception.HttpClientException;
import cn.flood.okhttp.utils.Utils;
/**  
* <p>Title: FileWrapper</p>  
* <p>Description: 文件包装类</p>  
* @author mmdai  
* @date 2019年7月23日  
*/
public class FileWrapper {
	
	private InputStream content;
    private File file;
    private String filename;

    private MediaType mediaType;

    FileWrapper(Builder builder) {
        this.mediaType = builder.mediaType;
        this.file = builder.file;
        this.filename = builder.filename;
        this.content = builder.content;
    }

    public String getFilename() {
        return filename;
    }

    public static Builder create() {
        return new Builder();
    }

    public RequestBody requestBody() {
        if (this.file != null) {
            return new FileRequestBody(this.file, this.mediaType);
        }
        return new InputStreamRequestBody(this.content, this.mediaType);
    }

    public static class Builder {

        private InputStream content;

        private File file;
        private String filename;

        private MediaType mediaType;


        public Builder file(File file) {
            Assert.notNull(file, "File may not be null.");
            if (!file.exists()) {
                throw new HttpClientException("File does not exist.");
            }
            this.file = file;
            return this;
        }

        public Builder filename(String filename) {
            Assert.hasLength(filename, "Filename may not be null.");
            this.filename = filename;
            return this;
        }

        public Builder stream(InputStream stream) {
            Assert.notNull(stream, "Stream may not be null.");
            this.content = stream;
            return this;
        }

        public Builder contentType(String contentType) {
            Assert.hasLength(contentType, "ContentType may not be null.");
            this.mediaType = MediaType.parse(contentType);
            return this;
        }

        public Builder mediaType(MediaType mediaType) {
            Assert.notNull(mediaType, "Media may not be null.");
            this.mediaType = mediaType;
            return this;
        }

        public FileWrapper build() {
            if (this.file != null) {
                if (this.filename == null) {
                    this.filename = file.getName();
                }
            } else if (this.content != null) {
                if (this.filename == null) {
                    throw new HttpClientException("Filename may not be null");
                }
            } else {
                throw new HttpClientException("The content is null.");
            }
            if (this.mediaType == null) {
                this.mediaType = Utils.guessMediaType(this.filename);
            }
            return new FileWrapper(this);
        }

    }

}
