package com.springpro.iocpractica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("smsNotificador")
public class SmsNotificacionService implements NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificacionService.class);

    @Override
    public void enviarConfirmacion(Long pedidoId) {
        log.info("[SMS] Confirmacion enviada para pedido: {}", pedidoId);
    }
}
