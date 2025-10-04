package com.tkfc.core.toolkit;

import com.tkfc.core.function.SerializableClosure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;


/**
 * @author 0neBean
 * spring 工具类
 */
@Slf4j
public class SpringUtil {

    private static boolean APPLICATION_CONTEXT_INIT_STATUS = Boolean.TRUE;
    private final static Queue<SerializableClosure> READY_TASK_QUEUE = new ConcurrentLinkedDeque<>();
    private final static int READY_TASK_QUEUE_EMPTY_SIZE = 0;
    private static ApplicationContext applicationContext;

    /**
     * 装载 ApplicationContext
     *
     * @param applicationContext spring  上下文
     * @author 0neBean
     * @since 2022/4/27 12:05
     */
    public static void reloadContext(ApplicationContext applicationContext) {
        if (Objects.isNull(SpringUtil.applicationContext)) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    /**
     * 初始化方法
     *
     * @param mainClass main class
     * @param args      vm 参数
     * @author 0neBean
     * @since 2022/4/27 11:07
     */
    public static ApplicationContext init(Class<?> mainClass, String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(mainClass, args);
        reloadContext(applicationContext);
        APPLICATION_CONTEXT_INIT_STATUS = Boolean.FALSE;
        handleReadyTasks();
        log.info("hey , application startup done -_-!!!");
        return applicationContext;
    }

    /**
     * 获取 applicationContext
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        while (APPLICATION_CONTEXT_INIT_STATUS) {
            Thread.yield();
        }
        return applicationContext;
    }

    /**
     * 获取 spring bean
     *
     * @param name name
     * @return Object
     */
    public static Object getBean(String name) {
        ClassLoader defaultClassLoader = ClassUtils.getDefaultClassLoader();
        if (Objects.isNull(defaultClassLoader)) {
            return null;
        }
        Class<?> clazz = null;
        try {
            clazz = defaultClassLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            log.error("getBean got error = ", e);
        }
        if (Objects.isNull(clazz)) {
            return null;
        } else {
            Object bean = null;
            try {
                bean = getApplicationContext().getBean(clazz);
            } catch (BeansException ignore) {}
            return bean;
        }
    }

    /**
     * 获取 spring bean
     *
     * @param clazz 类型
     * @param <T>   泛型
     * @return T
     */
    public static <T> T getBean(Class<T> clazz) {
        T bean = null;
        try {
            bean = getApplicationContext().getBean(clazz);
        } catch (BeansException ignore) {}
        return bean;
    }

    /**
     * 获取 spring bean
     *
     * @param name  name
     * @param clazz 类型
     * @param <T>   泛型
     * @return T
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        T bean = null;
        try {
            bean = getApplicationContext().getBean(name, clazz);
        } catch (BeansException ignore) {}
        return bean;
    }

    /**
     * 返回一个 spring 完成任务
     *
     * @return com.tkfc.core.function.SerializableClosure
     * @author 0neBean
     * @since 2022/4/27 11:48
     */
    public static SerializableClosure pollReadyTask() {
        return READY_TASK_QUEUE.poll();
    }


    /**
     * 获取启动任务的数量
     *
     * @author 0neBean
     * @since 2022/4/27 11:13
     */
    public static Integer getReadyTasksSize() {
        return READY_TASK_QUEUE.size();
    }

    /**
     * 添加任务到 完成启动队列
     *
     * @param closure 闭包
     * @author 0neBean
     * @since 2022/4/27 11:13
     */
    public static void pushReadyTask(SerializableClosure closure) {
        READY_TASK_QUEUE.offer(closure);
    }

    /**
     * 处理spring完成后任务
     *
     * @author 0neBean
     * @since 2022/4/28 0:30
     */
    private static void handleReadyTasks() {
        log.info("handle spring ready tasks");
        while (getReadyTasksSize() > READY_TASK_QUEUE_EMPTY_SIZE) {
            SerializableClosure closure = pollReadyTask();
            closure.accept();
        }
    }

}