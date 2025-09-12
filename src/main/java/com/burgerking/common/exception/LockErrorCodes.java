package com.burgerking.common.exception;

/**
 * 분산 락 관련 에러 코드를 정의하는 열거형
 * common 모듈에 위치하여 여러 도메인에서 공통으로 사용할 수 있습니다.
 */
public enum LockErrorCodes implements ErrorCode {
    LOCK_ACQUISITION_FAILED("락 획득에 실패했습니다."),
    LOCK_RELEASE_FAILED("락 해제에 실패했습니다."),
    LOCK_TIMEOUT("락 획득 대기 시간이 초과되었습니다."),
    LOCK_INTERRUPTED("락 획득 과정이 중단되었습니다."),
    LOCK_ALREADY_HELD("이미 다른 프로세스에 의해 락이 점유되어 있습니다."),
    INVALID_LOCK_KEY("유효하지 않은 락 키입니다.");

    private final String message;

    LockErrorCodes(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}