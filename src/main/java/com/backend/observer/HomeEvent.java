package com.backend.observer;

import com.backend.core.SmartDevice;

import java.time.LocalDateTime;

public record HomeEvent(HomeEventType type, SmartDevice smartDevice, LocalDateTime timestamp) {
    public SmartDevice sourceDevice() {
        return this.smartDevice;
    }

    public static class Builder {
        private HomeEventType type;
        private SmartDevice smartDevice;
        private LocalDateTime timestamp;

        public Builder addType(HomeEventType var1) {
            this.type = var1;
            return this;
        }

        public Builder addSmartDevice(SmartDevice var1) {
            this.smartDevice = var1;
            return this;
        }

        public Builder addTimestamp(LocalDateTime var1) {
            this.timestamp = var1;
            return this;
        }

        public HomeEvent build() {
            return new HomeEvent(this.type, this.smartDevice, this.timestamp);
        }
    }
}

