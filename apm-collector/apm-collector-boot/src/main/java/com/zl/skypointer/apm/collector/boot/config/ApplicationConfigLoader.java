package com.zl.skypointer.apm.collector.boot.config;

import com.zl.skypointer.apm.collector.core.module.ApplicationConfiguration;
import com.zl.skypointer.apm.collector.core.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhouliang
 * 2018-02-06 19:02
 */
public class ApplicationConfigLoader implements ConfigLoader<ApplicationConfiguration> {
    private final Logger logger = LoggerFactory.getLogger(ApplicationConfigLoader.class);

    private final Yaml yaml = new Yaml();

    @Override public ApplicationConfiguration load() throws ConfigFileNotFoundException {
        ApplicationConfiguration configuration = new ApplicationConfiguration();
        this.loadConfig(configuration);
        this.loadDefaultConfig(configuration);
        return configuration;
    }

    /**
     * 从 apm-collector-core 的 application.yml 加载自定义配置。
     * @param configuration
     * @throws ConfigFileNotFoundException
     */
    private void loadConfig(ApplicationConfiguration configuration) throws ConfigFileNotFoundException {
        try {
            Reader applicationReader = ResourceUtils.read("application.yml");
            Map<String, Map<String, Map<String, ?>>> moduleConfig = yaml.loadAs(applicationReader, Map.class);
            moduleConfig.forEach((moduleName, providerConfig) -> {
                if (providerConfig.size() > 0) {
                    logger.info("Get a module define from application.yml, module name: {}", moduleName);
                    ApplicationConfiguration.ModuleConfiguration moduleConfiguration = configuration.addModule(moduleName);
                    providerConfig.forEach((name, propertiesConfig) -> {
                        logger.info("Get a provider define belong to {} module, provider name: {}", moduleName, name);
                        Properties properties = new Properties();
                        if (propertiesConfig != null) {
                            propertiesConfig.forEach((key, value) -> {
                                properties.put(key, value);
                                logger.info("The property with key: {}, value: {}, in {} provider", key, value, name);
                            });
                        }
                        moduleConfiguration.addProviderConfiguration(name, properties);
                    });
                } else {
                    logger.warn("Get a module define from application.yml, but no provider define, use default, module name: {}", moduleName);
                }
            });
        } catch (FileNotFoundException e) {
            throw new ConfigFileNotFoundException(e.getMessage(), e);
        }
    }

    /**
     * 从 apm-collector-core 的 application-default.yml 加载默认配置。
     * @param configuration
     * @throws ConfigFileNotFoundException
     */
    private void loadDefaultConfig(ApplicationConfiguration configuration) throws ConfigFileNotFoundException {
        try {
            Reader applicationReader = ResourceUtils.read("application-default.yml");
            Map<String, Map<String, Map<String, ?>>> moduleConfig = yaml.loadAs(applicationReader, Map.class);
            moduleConfig.forEach((moduleName, providerConfig) -> {
                if (!configuration.has(moduleName)) {
                    logger.warn("The {} module did't define in application.yml, use default", moduleName);
                    ApplicationConfiguration.ModuleConfiguration moduleConfiguration = configuration.addModule(moduleName);
                    providerConfig.forEach((name, propertiesConfig) -> {
                        Properties properties = new Properties();
                        if (propertiesConfig != null) {
                            propertiesConfig.forEach(properties::put);
                        }
                        moduleConfiguration.addProviderConfiguration(name, properties);
                    });
                }
            });
        } catch (FileNotFoundException e) {
            throw new ConfigFileNotFoundException(e.getMessage(), e);
        }
    }
}
