package com.springpro.iocpractica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServicioComun {

    private static final Logger log = LoggerFactory.getLogger(ServicioComun.class);

    public String helper() {
        log.debug("ServicioComun.helper() invocado");
        return "COMUN";
    }
}
