package com.airtribe.chronos.response;


import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

    /**
     * Standard API response wrapper for all endpoints.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class APIResponse<T> {
        private boolean success;          // true if request handled successfully
        private LocalDateTime timestamp;  // time when response is generated
        private T data;                   // the actual payload
        private String errorMessage;      // optional error details

        public static <T> APIResponse<T> success(T data) {
            return APIResponse.<T>builder()
                    .success(true)
                    .timestamp(LocalDateTime.now())
                    .data(data)
                    .build();
        }

        public static <T> APIResponse<T> failure(String errorMessage) {
            return APIResponse.<T>builder()
                    .success(false)
                    .timestamp(LocalDateTime.now())
                    .errorMessage(errorMessage)
                    .build();
        }
    }


