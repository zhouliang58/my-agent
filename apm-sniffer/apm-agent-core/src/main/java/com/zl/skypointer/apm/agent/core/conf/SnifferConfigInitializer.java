package com.zl.skypointer.apm.agent.core.conf;

import com.zl.skypointer.apm.agent.core.boot.AgentPackageNotFoundException;
import com.zl.skypointer.apm.agent.core.boot.AgentPackagePath;
import com.zl.skypointer.apm.agent.core.logging.api.ILog;
import com.zl.skypointer.apm.agent.core.logging.api.LogManager;
import com.zl.skypointr.apm.util.ConfigInitializer;
import com.zl.skypointr.apm.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * 初始化探针配置
 *
 * @author zhouliang
 * @version v1.0 2018/1/4 23:16
 * @since JDK1.8
 */
public class SnifferConfigInitializer {
    private static final ILog logger = LogManager.getLogger(SnifferConfigInitializer.class);
    private static String CONFIG_FILE_NAME = "/config/agent.config";
    private static String ENV_KEY_PREFIX = "skypointer.";

    /**
     * Try to locate `agent.config`, which should be in the /config dictionary of agent package.
     * <p>
     * Also try to override the config by system.env and system.properties. All the keys in these two places should
     * start with {@link #ENV_KEY_PREFIX}. e.g. in env `skypointer.agent.application_code=yourAppName` to override
     * `agent.application_code` in config file.
     * <p>
     * At the end, `agent.application_code` and `collector.servers` must be not blank.
     */
    public static void initialize() throws ConfigNotFoundException, AgentPackageNotFoundException {
        InputStream configFileStream;

        try {
            configFileStream = loadConfigFromAgentFolder();
            Properties properties = new Properties();
            properties.load(configFileStream);
            ConfigInitializer.initialize(properties, Config.class);
        } catch (Exception e) {
            logger.error(e, "Failed to read the config file, skypointer is going to run in default config.");
        }

        try {
            overrideConfigBySystemEnv();
        } catch (Exception e) {
            logger.error(e, "Failed to read the system env.");
        }

        if (StringUtil.isEmpty(Config.Agent.APPLICATION_CODE)) {
            throw new ExceptionInInitializerError("`agent.application_code` is missing.");
        }
        if (StringUtil.isEmpty(Config.Collector.SERVERS)) {
            throw new ExceptionInInitializerError("`collector.servers` is missing.");
        }
    }

    /**
     * Override the config by system env. The env key must start with `skypointer`, the reuslt should be as same as in
     * `agent.config`
     * <p>
     * such as:
     * Env key of `agent.application_code` shoule be `skypointer.agent.application_code`
     *
     * @return the config file {@link InputStream}, or null if not needEnhance.
     */
    private static void overrideConfigBySystemEnv() throws IllegalAccessException {
        Properties properties = new Properties();
        Properties systemProperties = System.getProperties();
        Iterator<Map.Entry<Object, Object>> entryIterator = systemProperties.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<Object, Object> prop = entryIterator.next();
            if (prop.getKey().toString().startsWith(ENV_KEY_PREFIX)) {
                String realKey = prop.getKey().toString().substring(ENV_KEY_PREFIX.length());
                properties.put(realKey, prop.getValue());
            }
        }

        Map<String, String> envs = System.getenv();
        for (String envKey : envs.keySet()) {
            if (envKey.startsWith(ENV_KEY_PREFIX)) {
                String realKey = envKey.substring(ENV_KEY_PREFIX.length());
                properties.setProperty(realKey, envs.get(envKey));
            }
        }

        if (!properties.isEmpty()) {
            ConfigInitializer.initialize(properties, Config.class);
        }
    }

    /**
     * Load the config file, where the agent jar is.
     *
     * @return the config file {@link InputStream}, or null if not needEnhance.
     */
    private static InputStream loadConfigFromAgentFolder() throws AgentPackageNotFoundException, ConfigNotFoundException {
        File configFile = new File(AgentPackagePath.getPath(), CONFIG_FILE_NAME);
        if (configFile.exists() && configFile.isFile()) {
            try {
                logger.info("Config file found in {}.", configFile);

                return new FileInputStream(configFile);
            } catch (FileNotFoundException e) {
                throw new ConfigNotFoundException("Fail to load agent.config", e);
            }
        }
        throw new ConfigNotFoundException("Fail to load agent config file.");
    }
}

