package com.cliente.dto;

import java.util.HashMap;
import java.util.Map;

public class ClientErrorResponseDTO {
    private Map<String, String> errors;

    public ClientErrorResponseDTO(Map<String, Object> data) {
        this.errors = new HashMap<>();

        if (data.containsKey("errors")) {
            Object errorsObject = data.get("errors");
            if (errorsObject instanceof Map<?, ?> nestedErrors) {
                for (Map.Entry<?, ?> entry : nestedErrors.entrySet()) {
                    this.errors.put(entry.getKey().toString(), entry.getValue().toString());
                }
            }
        }

        else if (data.containsKey("error")) {
            this.errors.put("error", data.get("error").toString());
        }
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}

