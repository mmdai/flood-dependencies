/**  
* <p>Title: BasicHeader.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2018</p>   
* @author mmdai  
* @date 2019年7月23日  
* @version 1.0  
*/  
package cn.flood.okhttp.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.flood.lang.Assert;

/**  
* <p>Title: BasicHeader</p>  
* <p>Description: 消息头</p>  
* @author mmdai  
* @date 2019年7月23日  
*/
public class BasicHeader implements Header, Serializable, Cloneable {

	/** serialVersionUID*/  
	private static final long serialVersionUID = -526349751963264528L;
	
	private final String name;
    private final String value;

    public BasicHeader(String name, String value) {
        Assert.hasLength(name, "name may not be null.");
        Assert.notNull(value, "Value may not be null.");
        this.name = name;
        this.value = value;
    }

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getValue() {
		return this.value;
	}
	
	/**
     * Clone it.
     *
     * @see Cloneable
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Represent standard headeroutput
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        int length = this.name.length() + 2;
        if (this.value != null) length += this.value.length();
        StringBuilder result = new StringBuilder(length);
        result.append(this.name).append(" :");
        if (this.value != null) result.append(this.value);
        return result.toString();
    }

    public static StandardBuilder standard() {
        return StandardBuilder.create();
    }

    /**
     * 标准的消息头Builder
     *
     */
    public static class StandardBuilder {

        private final List<String> headerFields;
        private final List<String> headerValues;

        StandardBuilder() {
            this.headerFields = new ArrayList<>();
            this.headerValues = new ArrayList<>();
        }

        public static StandardBuilder create() {
            return new StandardBuilder();
        }

        public StandardBuilder accept(String value) {
            this.tryDo(Header.ACCEPT, value);
            return this;
        }

        public StandardBuilder acceptCharset(String value) {
            this.tryDo(Header.ACCEPT_CHARSET, value);
            return this;
        }

        public StandardBuilder acceptEncoding(String value) {
            this.tryDo(Header.ACCEPT_ENCODING, value);
            return this;
        }

        public StandardBuilder acceptLanguage(String value) {
            this.tryDo(Header.ACCEPT_LANGUAGE, value);
            return this;
        }

        public StandardBuilder acceptRanges(String value) {
            this.tryDo(Header.ACCEPT_RANGES, value);
            return this;
        }

        public StandardBuilder age(String value) {
            this.tryDo(Header.AGE, value);
            return this;
        }

        public StandardBuilder allow(String value) {
            this.tryDo(Header.ALLOW, value);
            return this;
        }

        public StandardBuilder cacheControl(String value) {
            this.tryDo(Header.CACHE_CONTROL, value);
            return this;
        }

        public StandardBuilder connection(String value) {
            this.tryDo(Header.CONNECTION, value);
            return this;
        }

        public StandardBuilder contentEncoding(String value) {
            this.tryDo(Header.CONTENT_ENCODING, value);
            return this;
        }

        public StandardBuilder contentLanguage(String value) {
            this.tryDo(Header.CONTENT_LANGUAGE, value);
            return this;
        }

        public StandardBuilder contentLength(String value) {
            this.tryDo(Header.CONTENT_LENGTH, value);
            return this;
        }

        public StandardBuilder contentLocation(String value) {
            this.tryDo(Header.CONTENT_LOCATION, value);
            return this;
        }

        public StandardBuilder contentMD5(String value) {
            this.tryDo(Header.CONTENT_MD5, value);
            return this;
        }

        public StandardBuilder contentRange(String value) {
            this.tryDo(Header.CONTENT_RANGE, value);
            return this;
        }

        public StandardBuilder contentType(String value) {
            this.tryDo(Header.CONTENT_TYPE, value);
            return this;
        }

        public StandardBuilder contentDisposition(String value) {
            this.tryDo(Header.CONTENT_DISPOSITION, value);
            return this;
        }

        public StandardBuilder userAgent(String value) {
            this.tryDo(Header.USER_AGENT, value);
            return this;
        }

        public List<Header> build() {
            List<Header> headerList = new LinkedList<>();
            Header header;
            for (int i = 0, size = this.headerFields.size(); i < size; i++) {
                header = new BasicHeader(this.headerFields.get(i), this.headerValues.get(i));
                headerList.add(header);
            }
            return headerList;
        }

        private void tryDo(String name, String value) {
            int index = this.headerFields.indexOf(name);
            if (index == -1) {
                this.headerFields.add(name);
                this.headerValues.set(this.headerFields.size() - 1, value);
            } else {
                this.headerValues.set(index, value);
            }
        }

    }

}
