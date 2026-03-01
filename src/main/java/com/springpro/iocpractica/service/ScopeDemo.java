package com.springpro.iocpractica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ScopeDemo implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ScopeDemo.class);

    private final ApplicationContext context;

    public ScopeDemo(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(String... args) {
        StringBuilder s1 = context.getBean("singletonBean", StringBuilder.class);
        StringBuilder s2 = context.getBean("singletonBean", StringBuilder.class);
        log.info("[SCOPE DEMO] Singleton — misma instancia: {}", s1 == s2);

        StringBuilder p1 = context.getBean("prototypeBean", StringBuilder.class);
        StringBuilder p2 = context.getBean("prototypeBean", StringBuilder.class);
        log.info("[SCOPE DEMO] Prototype — misma instancia: {}", p1 == p2);
    }
}
