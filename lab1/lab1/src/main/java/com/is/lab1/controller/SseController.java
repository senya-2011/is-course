package com.is.lab1.controller;

import com.is.lab1.service.SseService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Контроллер для Server-Sent Events.
 */
@RestController
@RequestMapping("/sse")
public class SseController {
  private final SseService sseService;

  /**
   * Конструктор.
   *
   * @param sseService SSE сервис
   */
  public SseController(SseService sseService) {
    this.sseService = sseService;
  }

  /**
   * Обрабатывает запросы подписки на SSE.
   *
   * @return SSE emitter
   */
  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter sse() {
    return sseService.subscribe();
  }
}
