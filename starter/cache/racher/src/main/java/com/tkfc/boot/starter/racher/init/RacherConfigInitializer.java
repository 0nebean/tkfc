package com.tkfc.boot.starter.racher.init;

import com.tkfc.core.toolkit.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RacherConfigInitializer {

    //apollo config
    private final static String EXCLUDE_KEY = "spring.autoconfigure.exclude";
    private final static String SPRING_DATA_SOURCE_CLASS_NAME = "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration";

    public static void init() {
        String exclude = System.getProperty(EXCLUDE_KEY);
        if (StringUtil.isNotBlank(exclude)) {
            exclude = exclude + ",";
        }else{
            exclude = "";
        }
        exclude = exclude + SPRING_DATA_SOURCE_CLASS_NAME;
        System.getProperties().setProperty(EXCLUDE_KEY, exclude);
        log.info("{} is setting {}", EXCLUDE_KEY, exclude);
    }
}
