package clofi.runningplanet.common.exception;

public class InternalServerException extends RuntimeException {

	private static final String ERROR_MESSAGE = "서버 내부에 문제가 발생했습니다.";

	public InternalServerException() {
		super(ERROR_MESSAGE);
	}
}
