package org.registryviewer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class RegistryViewer {

    private static final Logger logger = LoggerFactory.getLogger(RegistryViewer.class);

    public static void main(String[] args) throws Exception {
        logger.debug("Starting application context");
        ApplicationContext applicationContext = SpringApplication.run(RegistryViewer.class);
        logger.debug("Application context was started");
    }
}
