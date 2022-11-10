package cn.flood.base.proto.converter;

import cn.flood.base.proto.ProtostuffUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Author daimm
 * @Date 2020/09/01
 * @Description feign调用中protostuff编解码器
 **/
public class ProtostuffHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final MediaType PROTOBUF;

    static {
        PROTOBUF = new MediaType("application", "x-protobuf", StandardCharsets.UTF_8);
    }

    public ProtostuffHttpMessageConverter() {
        super(new MediaType[]{PROTOBUF});
    }


    @Override
    protected boolean supports(Class<?> aClass) {
       return Object.class.isAssignableFrom(aClass);
    }

    @Override
    protected Object readInternal(Class<?> aClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = PROTOBUF;
        }
        if (!PROTOBUF.isCompatibleWith(contentType)) {
            logger.error("不支持的解码格式，请用x-protobuf作为contentType");
        }
        try {
            return ProtostuffUtils.deserialize(inputMessage.getBody(), aClass);
        } catch (Exception var7) {
            throw new IllegalStateException("Could not read Protobuf message: " + var7.getMessage(), var7);
        }
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        MediaType contentType = outputMessage.getHeaders().getContentType();
        if (contentType == null) {
            contentType = PROTOBUF;
        }

        Charset charset = contentType.getCharset();
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }

        if (!PROTOBUF.isCompatibleWith(contentType)) {
            logger.error("不支持的编码格式，请用x-protobuf作为contentType");
        }
        FileCopyUtils.copy(ProtostuffUtils.serializer(object), outputMessage.getBody());

    }

}
