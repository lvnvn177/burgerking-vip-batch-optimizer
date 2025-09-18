 6793 --- [vip-batch-optimizer] [  restartedMain] o.s.b.d.a.OptionalLiveReloadServer       : LiveReload server is running on port 35729
2025-09-18T20:14:41.414+09:00  INFO 6793 --- [vip-batch-optimizer] [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-09-18T20:14:41.421+09:00  INFO 6793 --- [vip-batch-optimizer] [  restartedMain] c.b.VipBatchOptimizerApplication         : Started VipBatchOptimizerApplication in 2.502 seconds (process running for 2.687)
2025-09-18T20:14:47.128+09:00  INFO 6793 --- [vip-batch-optimizer] [0.0-8080-exec-3] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2025-09-18T20:14:47.129+09:00  INFO 6793 --- [vip-batch-optimizer] [0.0-8080-exec-3] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2025-09-18T20:14:47.130+09:00  INFO 6793 --- [vip-batch-optimizer] [0.0-8080-exec-3] o.s.web.servlet.DispatcherServlet        : Completed initialization in 0 ms
2025-09-18T20:14:47.484+09:00  INFO 6793 --- [vip-batch-optimizer] [0.0-8080-exec-5] o.springdoc.api.AbstractOpenApiResource  : Init duration for springdoc-openapi is: 166 ms
2025-09-18T20:15:37.473+09:00  INFO 6793 --- [vip-batch-optimizer] [0.0-8080-exec-6] c.burgerking.common.aop.LoggingAspect    : [START] com.burgerking.membership.web.MembershipController.MembershipController.generateTestData(..)()
2025-09-18T20:15:37.494+09:00  INFO 6793 --- [vip-batch-optimizer] [0.0-8080-exec-6] c.burgerking.common.aop.LoggingAspect    : [START] com.burgerking.membership.service.MembershipService.MembershipService.generateTestData(..)()
2025-09-18T20:15:37.495+09:00  INFO 6793 --- [vip-batch-optimizer] [0.0-8080-exec-6] c.burgerking.common.aop.LoggingAspect    : [START] com.burgerking.membership.util.MembershipTestDataGenerator.MembershipTestDataGenerator.generateMembersAndOrders(..)()
2025-09-18T20:15:37.552+09:00 DEBUG 6793 --- [vip-batch-optimizer] [0.0-8080-exec-6] org.hibernate.SQL                        : 
    insert 
    into
        members
        (created_at, grade, updated_at, user_id) 
    values
        (?, ?, ?, ?)
Hibernate: 
    insert 
    into
        members
        (created_at, grade, updated_at, user_id) 
    values
        (?, ?, ?, ?)
2025-09-18T20:15:37.566+09:00  WARN 6793 --- [vip-batch-optimizer] [0.0-8080-exec-6] o.h.engine.jdbc.spi.SqlExceptionHelper   : SQL Error: 1364, SQLState: HY000
2025-09-18T20:15:37.566+09:00 ERROR 6793 --- [vip-batch-optimizer] [0.0-8080-exec-6] o.h.engine.jdbc.spi.SqlExceptionHelper   : Field 'last_evaluation_date' doesn't have a default value
2025-09-18T20:15:37.570+09:00 ERROR 6793 --- [vip-batch-optimizer] [0.0-8080-exec-6] c.burgerking.common.aop.LoggingAspect    : [EXCEPTION] com.burgerking.membership.util.MembershipTestDataGenerator.MembershipTestDataGenerator.generateMembersAndOrders(..)() - 예외: could not execute statement [Field 'last_evaluation_date' doesn't have a default value] [insert into members (created_at,grade,updated_at,user_id) values (?,?,?,?)] - 실행 시간: 75ms
2025-09-18T20:15:37.570+09:00 ERROR 6793 --- [vip-batch-optimizer] [0.0-8080-exec-6] c.burgerking.common.aop.LoggingAspect    : [EXCEPTION] com.burgerking.membership.service.MembershipService.MembershipService.generateTestData(..)() - 예외: could not execute statement [Field 'last_evaluation_date' doesn't have a default value] [insert into members (created_at,grade,updated_at,user_id) values (?,?,?,?)] - 실행 시간: 76ms
2025-09-18T20:15:37.571+09:00 ERROR 6793 --- [vip-batch-optimizer] [0.0-8080-exec-6] c.burgerking.common.aop.LoggingAspect    : [EXCEPTION] com.burgerking.membership.web.MembershipController.MembershipController.generateTestData(..)() - 예외: could not execute statement [Field 'last_evaluation_date' doesn't have a default value] [insert into members (created_at,grade,updated_at,user_id) values (?,?,?,?)] - 실행 시간: 98ms
2025-09-18T20:15:37.572+09:00  INFO 6793 --- [vip-batch-optimizer] [0.0-8080-exec-6] c.burgerking.common.aop.LoggingAspect    : [START] com.burgerking.common.exception.GlobalExceptionHandler.GlobalExceptionHandler.handleGeneralException(..)()
2025-09-18T20:15:37.573+09:00  INFO 6793 --- [vip-batch-optimizer] [0.0-8080-exec-6] c.burgerking.common.aop.LoggingAspect    : [END] com.burgerking.common.exception.GlobalExceptionHandler.GlobalExceptionHandler.handleGeneralException(..)() - 실행 시간: 1ms
2025-09-18T20:15:37.576+09:00  WARN 6793 --- [vip-batch-optimizer] [0.0-8080-exec-6] .m.m.a.ExceptionHandlerExceptionResolver : Resolved [org.springframework.orm.jpa.JpaSystemException: could not execute statement [Field 'last_evaluation_date' doesn't have a default value] [insert into members (created_at,grade,updated_at,user_id) values (?,?,?,?)]]