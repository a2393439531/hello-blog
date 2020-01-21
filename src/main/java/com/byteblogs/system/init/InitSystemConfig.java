package com.byteblogs.system.init;

import com.byteblogs.common.cache.ConfigCache;
import com.byteblogs.common.config.WebConfigurer;
import com.byteblogs.helloblog.config.dao.ConfigDao;
import com.byteblogs.helloblog.config.domain.po.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author: byteblogs
 * @date: 2019/09/03 12:09
 */
@Slf4j
@Component
@DependsOn({"dataSource"})
@Import({InitSystemConfig.class})
public class InitSystemConfig implements ImportBeanDefinitionRegistrar {

    @Autowired
    private ConfigDao configDao;

    @PostConstruct
    public void init() {
        final List<Config> configList = configDao.selectList(null);
        configList.forEach(config -> {
            log.debug("config_key: {}, config_vlaue: {}", config.getConfigKey(), config.getConfigValue());
            ConfigCache.putConfig(config.getConfigKey(), config.getConfigValue());
        });
    }

    /**
     * 后置初始化bean
     *
     * @param annotationMetadata
     * @param beanDefinitionRegistry
     */
    @Override
    public void registerBeanDefinitions(final AnnotationMetadata annotationMetadata, final BeanDefinitionRegistry beanDefinitionRegistry) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(WebConfigurer.class);
        final BeanDefinition beanDefinition = builder.getBeanDefinition();

        beanDefinitionRegistry.registerBeanDefinition("webConfigurer", beanDefinition);
    }
}