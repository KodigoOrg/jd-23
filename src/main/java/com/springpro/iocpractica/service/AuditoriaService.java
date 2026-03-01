package com.springpro.iocpractica.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);

    public AuditoriaService() {
        log.info("[CICLO DE VIDA] 1. Constructor — instancia creada");
    }

    @PostConstruct
    public void init() {
        log.info("[CICLO DE VIDA] 2. @PostConstruct — dependencias inyectadas, inicializacion personalizada");
    }

    @Override
    public void afterPropertiesSet() {
        log.info("[CICLO DE VIDA] 3. InitializingBean.afterPropertiesSet() — segunda fase de inicializacion");
    }

    @PreDestroy
    public void cleanup() {
        log.info("[CICLO DE VIDA] 4. @PreDestroy — liberando recursos antes de destruir el bean");
    }

    public void registrarEvento(String evento) {
        log.info("[AUDITORIA] Evento registrado: {}", evento);
    }
}
