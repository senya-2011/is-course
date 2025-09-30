package com.is.lab1.service;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseService {

  private final Set<SseEmitter> emitters = new CopyOnWriteArraySet<>();

  public SseEmitter subscribe() {
    SseEmitter emitter = new SseEmitter(0L);
    emitters.add(emitter);
    emitter.onCompletion(() -> emitters.remove(emitter));
    emitter.onTimeout(() -> emitters.remove(emitter));
    emitter.onError(e -> emitters.remove(emitter));
    try {
      emitter.send(SseEmitter.event().name("hello").data("connected"));
    } catch (IOException ignored) {
      // Ignore connection errors
    }
    return emitter;
  }

  public void broadcast(String eventName) {
    for (SseEmitter emitter : emitters) {
      try {
        emitter.send(SseEmitter.event().name(eventName).data("1"));
      } catch (IOException e) {
        emitters.remove(emitter);
      }
    }
  }
}


