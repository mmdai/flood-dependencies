package cn.flood.base.core.exception;


import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;


/**
 * 
 * 平台统一异常类
 * <p>
 * Created on 2017年6月19日 
 * <p>
 * @author mmdai
 * @date 2017年6月19日
 */

/*
 * 使用方式1：
 * java：throw new CoreException("A00000"); 
 * properties：A00000=加载蒙圈了
 * 前台显示：加载蒙圈了
 * 
 * 使用方式2：
 * java：throw new CoreException("A00000", "出错了"); 
 * properties：A00000=加载蒙圈了
 * 前台显示：出错了
 * 
 * 使用方式3：
 * java：throw new CoreException("A00000", new String[] {"spider", "spider"}); 
 * properties：A00000={0}加载蒙圈了,{1}失败
 * 前台显示：spider加载蒙圈了,spider失败
 */
public class HostException extends CoreException implements MessageSourceResolvable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -343834700911745848L;
	private final DefaultMessageSourceResolvable messageSourceResolvable;
	
	public HostException(String code) {
		super(code);
		messageSourceResolvable = new DefaultMessageSourceResolvable(code);
	}
	
	public HostException(String code, Object[] args) {
		super(code);
		messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, args);
	}

	public HostException(String code, Throwable error) {
		super(code, error);
		messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, null, error.getMessage());
	}
	
	public HostException(String code, Object[] args, Throwable error) {
		super(code, error);
		messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, args, error.getMessage());
	}

	public HostException(String code, String msg) {
		super(code);
		messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, null, msg);
	}
	
	public HostException(String code, Object[] args, String msg) {
		super(code);
		messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, args, msg);
	}

	public HostException(String code, String msg, Throwable error) {
		super(code, error);
		messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, new Object[]{error.getClass().getName()}, msg);
	}
	
	public HostException(String code, Object[] args, String msg, Throwable error) {
		super(code, error);
		messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, args, msg);
	}

	@Override
    public String getCode() {
		return messageSourceResolvable.getCode();
	}
	
	@Override
	public String[] getCodes() {
		return messageSourceResolvable.getCodes();
	}
	
	@Override
	public Object[] getArguments() {
		return messageSourceResolvable.getArguments();
	}
	
	@Override
	public String getDefaultMessage() {
		return messageSourceResolvable.getDefaultMessage();
	}
}
