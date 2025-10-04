package com.tkfc.dictionary.config;


import com.tkfc.dictionary.builder.DictionaryBuildFactory;
import com.tkfc.dictionary.dto.DictionaryDto;
import com.tkfc.dictionary.util.DictionaryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 初始化字典资源
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/4/1 19:10
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DictionaryConfigure implements ApplicationListener<ContextRefreshedEvent> {

    //当一个ApplicationContext被初始化或刷新触发 加载字典到内存中
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("DictionaryConfigure initializing! load dictionary value to tool at {}", event.getTimestamp());
        List<DictionaryDto> dictionaryList = DictionaryBuildFactory.buildDictionary();
        log.info("load dictionary to tool , dicDictionaryAll size = {}", dictionaryList.size());
        DictionaryUtil.init(dictionaryList);
    }

}
