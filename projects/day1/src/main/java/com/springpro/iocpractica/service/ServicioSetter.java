package com.springpro.iocpractica.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Tipo 2: Setter Injection (dependencias opcionales). */
@Service
public class ServicioSetter {

    private NotificacionService notificacion;

    @Autowired(required = false)
    public void setNotificacion(@Qualifier("emailNotificador") NotificacionService notificacion) {
        this.notificacion = notificacion;
    }

    public NotificacionService getNotificacion() {
        return notificacion;
    }
}
