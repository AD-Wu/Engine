// package com.x.aop;
//
// import com.google.common.base.Stopwatch;
// import java.util.concurrent.TimeUnit;
// import org.aspectj.lang.JoinPoint;
// import org.aspectj.lang.ProceedingJoinPoint;
// import org.aspectj.lang.annotation.After;
// import org.aspectj.lang.annotation.AfterReturning;
// import org.aspectj.lang.annotation.AfterThrowing;
// import org.aspectj.lang.annotation.Around;
// import org.aspectj.lang.annotation.Aspect;
// import org.aspectj.lang.annotation.Before;
// import org.aspectj.lang.annotation.Pointcut;
// import org.springframework.stereotype.Component;
//
// /**
//  * TODO
//  *
//  * @author chunquanw
//  * @date 2021/11/12 20:57
//  */
// @Aspect
// @Component
// public class AlgsAspect {
//
//     @Pointcut("execution( * com.x.practice..*(..) )")
//     public void pointCut() {}
//
//     /**
//      * 前置通知
//      *
//      * @param point 连接点
//      */
//     @Before("pointCut()")
//     public void before(JoinPoint point) {
//         // TODO 第2执行
//     }
//
//     /**
//      * 环绕通知很强大，也存在风险，特别是通知的执行顺序，可能会随着版本变化而变化
//      *
//      * @param point 连接点，即被代理的目标，可以回调目标方法
//      * @throws Throwable
//      */
//     @Around("pointCut()")
//     public void around(ProceedingJoinPoint point) throws Throwable {
//         // TODO 第1执行
//         String name = point.getSignature().getName();
//         Object[] args = point.getArgs();
//         if (args != null && args.length > 0) {
//             System.out.println("------------------- " + name + "(" + args[0] + ")" + " START -------------------");
//         } else {
//             System.out.println("------------------- " + name + "() START -------------------");
//         }
//
//         // TODO 目标方法执行
//         Stopwatch stopwatch = Stopwatch.createStarted();
//         point.proceed(point.getArgs());
//         long microSeconds = stopwatch.stop().elapsed(TimeUnit.MICROSECONDS);
//         long millSeconds = TimeUnit.MILLISECONDS.convert(microSeconds, TimeUnit.MICROSECONDS);
//         System.out.printf("%s%d微秒\n","耗时:",microSeconds);
//         System.out.printf("%s%d毫秒\n","耗时:",millSeconds);
//         // TODO 第5执行
//         if (args != null && args.length > 0) {
//             System.out.println("------------------- " + name + "(" + args[0] + ")" + " E N D -------------------\n");
//         } else {
//             System.out.println("------------------- " + name + "() E N D -------------------\n ");
//         }
//     }
//
//     @After("pointCut()")
//     public void after() {
//         // TODO 第4执行
//     }
//
//     @AfterReturning("pointCut()")
//     public void afterReturning() {
//         // TODO 第3执行
//     }
//
//     @AfterThrowing("pointCut()")
//     public void afterThrowing() {
//         System.out.println("after throwing");
//     }
//
//
// }
