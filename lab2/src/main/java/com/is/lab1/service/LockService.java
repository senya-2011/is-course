package com.is.lab1.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LockService {

  private final JdbcTemplate jdbcTemplate;

  public LockService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Transactional
  public void lockKey(String key) {
    jdbcTemplate.query("select pg_advisory_xact_lock(hashtext(?))", rs -> null, key);
  }
}


