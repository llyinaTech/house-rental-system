package com.llyinatech.houserental.exception;

import lombok.Getter;
import lombok.Setter;
import java.io.Serial;

/**
 * 业务异常类
 */
@Setter
@Getter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

}
