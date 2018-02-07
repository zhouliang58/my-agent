package com.zl.skypointer.apm.collector.boot;

import com.zl.skypointer.apm.collector.boot.config.ApplicationConfigLoader;
import com.zl.skypointer.apm.collector.boot.config.ConfigFileNotFoundException;
import com.zl.skypointer.apm.collector.core.module.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouliang
 * 2018-02-06 18:55
 */
public class CollectorBootStartUp {
    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(CollectorBootStartUp.class);

        ApplicationConfigLoader configLoader = new ApplicationConfigLoader();
        ModuleManager manager = new ModuleManager();
        try {
            ApplicationConfiguration applicationConfiguration = configLoader.load();
            manager.init(applicationConfiguration);
        } catch (ConfigFileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (ModuleNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (ProviderNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (ServiceNotProvidedException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
        }

    }
}
