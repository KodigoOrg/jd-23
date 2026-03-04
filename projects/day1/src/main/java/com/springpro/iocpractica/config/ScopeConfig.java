package com.springpro.iocpractica.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ScopeConfig {

    private static final Logger log = LoggerFactory.getLogger(ScopeConfig.class);

    @Bean
    @Scope("singleton")
    public StringBuilder singletonBean() {
        log.info("[SCOPE] Creando bean SINGLETON — una sola vez");
        return new StringBuilder("singleton-instance");
    }

    @Bean
    @Scope("prototype")
    public StringBuilder prototypeBean() {
        log.info("[SCOPE] Creando bean PROTOTYPE — nueva instancia cada vez");
        return new StringBuilder("prototype-instance");
    }
}
