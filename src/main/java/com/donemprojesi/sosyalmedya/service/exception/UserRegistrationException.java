package com.donemprojesi.sosyalmedya.service.exception;

// 'Exception' sınıfından kalıtım alarak kendi özel hata sınıfımızı oluşturuyoruz.
public class UserRegistrationException extends Exception {
    public UserRegistrationException(String message) {
        super(message);
    }
}