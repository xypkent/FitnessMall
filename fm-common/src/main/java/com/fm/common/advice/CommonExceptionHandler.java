package com.fm.common.advice;

import com.fm.common.exception.FmException;
import com.fm.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(FmException.class)
    public ResponseEntity<ExceptionResult> handlerException(FmException e){
        return ResponseEntity.status(e.getExceptionEnum().getCode())
                .body(new ExceptionResult(e.getExceptionEnum()));
    }
}
