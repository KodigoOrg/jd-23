package com.springpro.iocpractica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

    private final NotificacionService notificacion;

    public PedidoService(@Qualifier("emailNotificador") NotificacionService notificacion) {
        this.notificacion = notificacion;
    }

    public void procesar(Long pedidoId) {
        log.info("Procesando pedido: {}", pedidoId);
        notificacion.enviarConfirmacion(pedidoId);
        log.debug("Notificacion enviada para pedido: {}", pedidoId);
    }
}
