package cn.flood.idempotent.autoconfigure.exception;

import cn.flood.exception.CoreException;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Idempotent Exception If there is a custom global exception, you need to inherit the
 * custom global exception.
 *
 * @author daimm
 */
public class IdempotentException extends CoreException {

	private final DefaultMessageSourceResolvable messageSourceResolvable;

	public IdempotentException(String code) {
		super(code);
		messageSourceResolvable = new DefaultMessageSourceResolvable(code);
	}

	public IdempotentException(String code, Object[] args) {
		super(code);
		messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, args);
	}

	public IdempotentException(String code, Throwable error) {
		super(code, error);
		messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, null, error.getMessage());
	}

	public IdempotentException(String code, Object[] args, Throwable error) {
		super(code, error);
		messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, args, error.getMessage());
	}

	public IdempotentException(String code, String msg) {
		super(code);
		messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, null, msg);
	}

	public IdempotentException(String code, Object[] args, String msg) {
		super(code);
		messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, args, msg);
	}

	public IdempotentException(String code, String msg, Throwable error) {
		super(code, error);
		messageSourceResolvable = new DefaultMessageSourceResolvable(new String[]{code}, new Object[]{error.getClass().getName()}, msg);
	}

	public IdempotentException(String code, Object[] args, String msg, Throwable error) {
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
