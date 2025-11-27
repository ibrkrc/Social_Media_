package com.donemprojesi.sosyalmedya.domain;

import java.util.Date;
import java.util.UUID;

public class Comment {

    private String commentId;
    private String content;
    private Date timestamp;

    // OOP yaklaşımı için tam nesneleri tutuyoruz.
    // Bu nesneler Service katmanı tarafından doldurulacak.
    private User author;
    private Post parentPost;

    /**
     * Constructor 1: YENİ bir Comment oluşturmak için (Uygulama içinden)
     * @param content Yorumun metni
     * @param author Yorumu yapan User
     * @param parentPost Yorumun yapıldığı Post
     */
    public Comment(String content, User author, Post parentPost) {
        this.commentId = UUID.randomUUID().toString();
        this.content = content;
        this.author = author;
        this.parentPost = parentPost;
        this.timestamp = new Date();
    }

    /**
     * Constructor 2: Veritabanından bir Comment yüklemek için
     * @param commentId DB'den gelen ID
     * @param content DB'den gelen içerik
     * @param timestamp DB'den gelen zaman (long)
     * @param author Yorumu yapan User (Service tarafından eklenecek)
     * @param parentPost Yorumun ait olduğu Post (Service tarafından eklenecek)
     */
    public Comment(String commentId, String content, long timestamp, User author, Post parentPost) {
        this.commentId = commentId;
        this.content = content;
        this.timestamp = new Date(timestamp); // Long'u Date'e çevir
        this.author = author;
        this.parentPost = parentPost;
    }


    // --- Getter ve Setter Metotları ---

    public String getCommentId() {
        return commentId;
    }

    public String getContent() {
        return content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public User getAuthor() {
        return author;
    }

    public Post getParentPost() {
        return parentPost;
    }

    // Yorumun yazarını ve gönderisini Service katmanında atamak için:
    public void setAuthor(User author) {
        this.author = author;
    }

    public void setParentPost(Post parentPost) {
        this.parentPost = parentPost;
    }
}