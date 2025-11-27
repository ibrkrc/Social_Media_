package com.donemprojesi.sosyalmedya.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Post {

    private String postId;
    private String content;
    private Date timestamp;

    // Değişiklik: Artık 'User' nesnesini değil, sadece 'authorId'yi tutabiliriz.
    // Ancak OOP yaklaşımı için 'User' nesnesini tutmak daha iyidir.
    // Bu 'author' nesnesi, PostService tarafından doldurulacak.
    private User author;

    // İlişkiler (Bunlar hâlâ bellekte, veritabanına henüz taşınmadı)
    private List<Comment> comments;
    private List<User> likes;

    /**
     * Constructor 1: YENİ bir Post oluşturmak için (Uygulama içinden)
     * @param content Gönderi içeriği
     * @param author Gönderiyi oluşturan User nesnesi
     */
    public Post(String content, User author) {
        this.postId = UUID.randomUUID().toString();
        this.content = content;
        this.author = author;
        this.timestamp = new Date(); // Şu anki zaman
        this.comments = new ArrayList<>();
        this.likes = new ArrayList<>();
    }

    /**
     * Constructor 2: Veritabanından bir Post yüklemek için
     * @param postId Veritabanından gelen ID
     * @param content Veritabanından gelen içerik
     * @param timestamp Veritabanından gelen zaman damgası (long)
     * @param author Bu gönderinin 'User' nesnesi (daha sonra eklenecek)
     */
    public Post(String postId, String content, long timestamp, User author) {
        this.postId = postId;
        this.content = content;
        this.timestamp = new Date(timestamp); // Long değeri Date nesnesine çevir
        this.author = author; // Bu başlangıçta null olabilir, Service doldurur
        this.comments = new ArrayList<>();
        this.likes = new ArrayList<>();
    }


    // --- Metotlar ---

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public boolean likePost(User user) {
        if (!this.likes.contains(user)) {
            this.likes.add(user);
            if (!this.author.equals(user)) {
                Notification notification = new Notification(NotificationType.LIKE, user, this.author);
                this.author.receiveNotification(notification);
            }
            return true;
        }
        return false;
    }

    public void unlikePost(User user) {
        this.likes.remove(user);
    }

    // --- Getter ve Setter Metotları ---

    public String getPostId() { return postId; }
    public String getContent() { return content; }
    public Date getTimestamp() { return timestamp; }
    public User getAuthor() { return author; }
    public List<Comment> getComments() { return comments; }
    public int getLikeCount() { return this.likes.size(); }
    public int getCommentCount() { return this.comments.size(); }

    // Yazar nesnesini sonradan (Service katmanında) atamak için bir setter
    public void setAuthor(User author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return postId.equals(post.postId);
    }

    @Override
    public int hashCode() {
        return postId.hashCode();
    }
}