package com.springpro.iocpractica.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Tipo 3: Field Injection (solo para referencia — prohibido en produccion). */
@Service
public class ServicioCampo {

    @Autowired
    @Qualifier("emailNotificador")
    private NotificacionService notificacion;

    public NotificacionService getNotificacion() {
        return notificacion;
    }
}
