2025-09-18T21:29:37.548+09:00 DEBUG 26859 --- [vip-batch-optimizer] [0.0-8080-exec-2] org.hibernate.SQL                        : 
    update
        members 
    set
        created_at=?,
        grade=?,
        last_evaluation_date=?,
        next_evaluation_date=?,
        updated_at=?,
        user_id=? 
    where
        id=?
Hibernate: 
    update
        members 
    set
        created_at=?,
        grade=?,
        last_evaluation_date=?,
        next_evaluation_date=?,
        updated_at=?,
        user_id=? 
    where
        id=?
2025-09-18T21:29:37.548+09:00 DEBUG 26859 --- [vip-batch-optimizer] [0.0-8080-exec-2] org.hibernate.SQL                        : 
    update
        members 
    set
        created_at=?,
        grade=?,
        last_evaluation_date=?,
        next_evaluation_date=?,
        updated_at=?,
        user_id=? 
    where
        id=?
Hibernate: 
    update
        members 
    set
        created_at=?,
        grade=?,
        last_evaluation_date=?,
        next_evaluation_date=?,
        updated_at=?,
        user_id=? 
    where
        id=?
2025-09-18T21:29:37.548+09:00 DEBUG 26859 --- [vip-batch-optimizer] [0.0-8080-exec-2] org.hibernate.SQL                        : 
    update
        members 
    set
        created_at=?,
        grade=?,
        last_evaluation_date=?,
        next_evaluation_date=?,
        updated_at=?,
        user_id=? 
    where
        id=?
Hibernate: 
    update
        members 
    set
        created_at=?,
        grade=?,
        last_evaluation_date=?,
        next_evaluation_date=?,
        updated_at=?,
        user_id=? 
    where
        id=?
2025-09-18T21:29:37.548+09:00 DEBUG 26859 --- [vip-batch-optimizer] [0.0-8080-exec-2] org.hibernate.SQL                        : 
    update
        members 
    set
        created_at=?,
        grade=?,
        last_evaluation_date=?,
        next_evaluation_date=?,
        updated_at=?,
        user_id=? 
    where
        id=?
Hibernate: 
    update
        members 
    set
        created_at=?,
        grade=?,
        last_evaluation_date=?,
        next_evaluation_date=?,
        updated_at=?,
        user_id=? 
    where
        id=?
2025-09-18T21:29:37.550+09:00  INFO 26859 --- [vip-batch-optimizer] [0.0-8080-exec-2] c.burgerking.common.aop.LoggingAspect    : [END] com.burgerking.membership.web.MembershipController.MembershipController.runNonOptimizedBatch()() - 실행 시간: 896ms
2025-09-18T21:29:37.556+09:00  INFO 26859 --- [vip-batch-optimizer] [0.0-8080-exec-3] c.burgerking.common.aop.LoggingAspect    : [START] com.burgerking.membership.web.MembershipController.MembershipController.runOptimizedBatch()()
2025-09-18T21:29:37.556+09:00  INFO 26859 --- [vip-batch-optimizer] [0.0-8080-exec-3] c.burgerking.common.aop.LoggingAspect    : [START] com.burgerking.membership.service.MembershipService.MembershipService.runOptimizedBatch()()
Error launching optimized batch job: Existing transaction detected in JobRepository. Please fix this and try again (e.g. remove @Transactional annotations from client).
2025-09-18T21:29:37.564+09:00 ERROR 26859 --- [vip-batch-optimizer] [0.0-8080-exec-3] c.burgerking.common.aop.LoggingAspect    : [EXCEPTION] com.burgerking.membership.service.MembershipService.MembershipService.runOptimizedBatch()() - 예외: Failed to launch optimized batch job - 실행 시간: 8ms
2025-09-18T21:29:37.564+09:00 ERROR 26859 --- [vip-batch-optimizer] [0.0-8080-exec-3] c.burgerking.common.aop.LoggingAspect    : [EXCEPTION] com.burgerking.membership.web.MembershipController.MembershipController.runOptimizedBatch()() - 예외: Failed to launch optimized batch job - 실행 시간: 8ms
2025-09-18T21:29:37.565+09:00  INFO 26859 --- [vip-batch-optimizer] [0.0-8080-exec-3] c.burgerking.common.aop.LoggingAspect    : [START] com.burgerking.common.exception.GlobalExceptionHandler.GlobalExceptionHandler.handleGeneralException(..)()
2025-09-18T21:29:37.566+09:00  INFO 26859 --- [vip-batch-optimizer] [0.0-8080-exec-3] c.burgerking.common.aop.LoggingAspect    : [END] com.burgerking.common.exception.GlobalExceptionHandler.GlobalExceptionHandler.handleGeneralException(..)() - 실행 시간: 1ms
2025-09-18T21:29:37.587+09:00  WARN 26859 --- [vip-batch-optimizer] [0.0-8080-exec-3] .m.m.a.ExceptionHandlerExceptionResolver : Resolved [java.lang.RuntimeException: Failed to launch optimized batch job]
