package clofi.runningplanet.common.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import clofi.runningplanet.common.dto.ExceptionResult;
import clofi.runningplanet.common.exception.BadRequestException;
import clofi.runningplanet.common.exception.ConflictException;
import clofi.runningplanet.common.exception.ForbiddenException;
import clofi.runningplanet.common.exception.InternalServerException;
import clofi.runningplanet.common.exception.NotFoundException;
import clofi.runningplanet.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
		HttpHeaders headers, HttpStatusCode status,
		WebRequest request) {
		return ResponseEntity.status(METHOD_NOT_ALLOWED)
			.body(new ExceptionResult(ex.getMessage()));
	}

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler(IllegalArgumentException.class)
	public ExceptionResult illegalInputException(IllegalArgumentException exception) {
		return new ExceptionResult(exception.getMessage());
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ExceptionResult> handleNotFoundException(NotFoundException exception) {
		ExceptionResult exceptionResult = new ExceptionResult(exception.getMessage());
		return ResponseEntity.status(NOT_FOUND).body(exceptionResult);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ExceptionResult> handleBadRequestException(BadRequestException exception) {
		ExceptionResult exceptionResult = new ExceptionResult(exception.getMessage());
		return ResponseEntity.badRequest().body(exceptionResult);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ExceptionResult> handleConflictException(ConflictException exception) {
		ExceptionResult exceptionResult = new ExceptionResult(exception.getMessage());
		return ResponseEntity.status(CONFLICT).body(exceptionResult);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ExceptionResult> handleUnauthorizedException(UnauthorizedException exception) {
		ExceptionResult exceptionResult = new ExceptionResult(exception.getMessage());
		return ResponseEntity.status(UNAUTHORIZED).body(exceptionResult);
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ExceptionResult> handleForbiddenException(ForbiddenException exception) {
		ExceptionResult exceptionResult = new ExceptionResult(exception.getMessage());
		return ResponseEntity.status(FORBIDDEN).body(exceptionResult);
	}

	@ExceptionHandler(InternalServerException.class)
	public ResponseEntity<ExceptionResult> handleInternalServerException(InternalServerException exception) {
		ExceptionResult exceptionResult = new ExceptionResult(exception.getMessage());
		return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(exceptionResult);
	}
}
