package com.fm.common.exception;

import com.fm.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FmException extends RuntimeException {

    private ExceptionEnum exceptionEnum;

}
