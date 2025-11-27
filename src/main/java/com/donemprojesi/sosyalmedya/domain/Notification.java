package com.donemprojesi.sosyalmedya.domain;

import java.util.Date;
import java.util.UUID;

// Bildirim türlerini yönetmek için bir Enum (Numaralandırma)
enum NotificationType {
    LIKE,
    COMMENT,
    NEW_FOLLOWER
}

public class Notification {

    private String notificationId;
    private NotificationType type;
    private User sender;      // Bildirimi tetikleyen (beğenen, takip eden...)
    private User recipient;   // Bildirimi alan
    private Date timestamp;
    private boolean isRead;

    public Notification(NotificationType type, User sender, User recipient) {
        this.notificationId = UUID.randomUUID().toString();
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.timestamp = new Date();
        this.isRead = false; // Başlangıçta okunmadı
    }

    // --- Getter Metotları ---

    public String getMessage() {
        // Bildirim mesajını dinamik olarak oluşturalım
        switch (type) {
            case LIKE:
                return sender.getUsername() + " gönderini beğendi.";
            case COMMENT:
                return sender.getUsername() + " gönderine yorum yaptı.";
            case NEW_FOLLOWER:
                return sender.getUsername() + " seni takip etmeye başladı.";
            default:
                return "Yeni bir bildiriminiz var.";
        }
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}