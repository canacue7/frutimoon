package com.inn.frutimoon.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface DashboardService {

    ResponseEntity<Map<String, Object>> getCount();

}
