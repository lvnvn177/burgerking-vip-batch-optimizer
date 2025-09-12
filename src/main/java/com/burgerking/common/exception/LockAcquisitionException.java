package com.burgerking.common.exception;


/**
 * 락 획득 실패 시 발생하는 예외
 */
public class LockAcquisitionException extends BusinessException{
    
    public LockAcquisitionException(String message) {
        super(LockErrorCodes.LOCK_ACQUISITION_FAILED, message);
    }
}
