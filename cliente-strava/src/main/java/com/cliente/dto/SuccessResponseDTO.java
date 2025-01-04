package com.cliente.dto;

import java.util.Map;

public class SuccessResponseDTO {
    private Map<String, Object> data;

    public SuccessResponseDTO(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Object getValue(String key) {
        return data.get(key);
    }
}

