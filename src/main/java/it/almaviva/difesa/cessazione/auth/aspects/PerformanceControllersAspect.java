package it.almaviva.difesa.cessazione.auth.aspects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Enumeration;

@Aspect
@Slf4j
@Component
public class PerformanceControllersAspect {

    @Around(value = "publicControllersMethods()")
    public Object logControllersPerfomance(ProceedingJoinPoint joinPoint) throws Throwable {

        Object result = joinPoint.proceed();

        if(log.isDebugEnabled()) {
            long startTime = Calendar.getInstance().getTimeInMillis();
            long endTime = Calendar.getInstance().getTimeInMillis();

            long time = endTime - startTime;

            String info = String.format("Performance Controller Indicator => CONTROLLER EXECUTION TIME : %1$s = %2$s " +
                    "(milliseconds)", joinPoint.getSignature().toShortString(), time);
            log.debug(info);
        }
        return result;
    }

    @Before(value = "publicControllersMethods() && ( args(request,response,*) || args(request,response) || args(request,response,*,*))", argNames = "joinPoint,request,response")
    public void logBeforeControllers(JoinPoint joinPoint, HttpServletRequest request, HttpServletResponse response) {

        if(log.isDebugEnabled()) {

            if (null != request) {
                log.debug("Performance Controller Indicator => CONTROLLER PATH INFO : " + request.getServletPath());
            }
            String info = String.format("Performance Controller Indicator => CONTROLLER NAME START : %1$s ",
                    joinPoint.getSignature().toShortString());
            log.debug(info);
            if (null != request) {
                log.debug("Start Header Section of request ");
                log.debug("Method Type : " + request.getMethod());
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    String headerValue = request.getHeader(headerName);
                    log.debug("Header Name: " + headerName + " Header Value : " + headerValue);
                }
                log.debug("End Header Section of request ");
            }
            log.debug("End Header Section of request ");
        }
    }

    @AfterReturning(value = "publicControllersMethods()", returning = "returnValue")
    public void logAfterControllers(JoinPoint joinPoint, Object returnValue) {

        if(log.isDebugEnabled()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            try {
                log.debug("\nResponse object: \n" + mapper.writeValueAsString(returnValue));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
            log.debug(joinPoint.getTarget().getClass().getSimpleName() + " " + joinPoint.getSignature().getName() + " END");

            String info = String.format("Performance Service Indicator => CONTROLLER NAME END : %1$s",
                    joinPoint.getSignature().toShortString());
            log.debug(info);
        }
    }

    @Pointcut(value = "execution(public * it.almaviva.difesa.cessazione.auth.controller..*.*(..))")
    public void publicControllersMethods() {
        /* Tutti i metodi pubblici di ogni controller*/
    }

}
