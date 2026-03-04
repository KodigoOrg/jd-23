package com.springpro.iocpractica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("emailNotificador")
public class EmailNotificacionService implements NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificacionService.class);

    @Override
    public void enviarConfirmacion(Long pedidoId) {
        log.info("[EMAIL] Confirmacion enviada para pedido: {}", pedidoId);
    }
}
