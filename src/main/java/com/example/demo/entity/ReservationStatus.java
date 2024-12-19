package com.example.demo.entity;

public enum ReservationStatus {
    PENDING {
        @Override
        public boolean isValidTransition(ReservationStatus currentStatus) {
            return currentStatus == PENDING;
        }
    },
    APPROVED {
        @Override
        public boolean isValidTransition(ReservationStatus currentStatus) {
            return currentStatus == PENDING;
        }
    },
    CANCELED {
        @Override
        public boolean isValidTransition(ReservationStatus currentStatus) {
            return currentStatus != EXPIRED;
        }
    },
    EXPIRED {
        @Override
        public boolean isValidTransition(ReservationStatus currentStatus) {
            return currentStatus == PENDING;
        }
    };

    public abstract boolean isValidTransition(ReservationStatus currentStatus);
}
