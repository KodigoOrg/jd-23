package com.springpro.iocpractica.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Tipo 1: Constructor Injection (estandar de produccion). */
@Service
public class ServicioConstructor {

    private final NotificacionService notificacion;

    public ServicioConstructor(@Qualifier("emailNotificador") NotificacionService notificacion) {
        this.notificacion = notificacion;
    }

    public NotificacionService getNotificacion() {
        return notificacion;
    }
}
