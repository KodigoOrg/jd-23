package com.springpro.iocpractica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServicioB {

    private static final Logger log = LoggerFactory.getLogger(ServicioB.class);

    private final ServicioComun comun;

    public ServicioB(ServicioComun comun) {
        this.comun = comun;
    }

    public String helper() {
        log.info("ServicioB.helper() ejecutado");
        return "B -> " + comun.helper();
    }
}
