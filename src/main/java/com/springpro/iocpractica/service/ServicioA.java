package com.springpro.iocpractica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServicioA {

    private static final Logger log = LoggerFactory.getLogger(ServicioA.class);

    private final ServicioComun comun;

    public ServicioA(ServicioComun comun) {
        this.comun = comun;
    }

    public String operacion() {
        log.info("ServicioA.operacion() ejecutada");
        return "A -> " + comun.helper();
    }
}
