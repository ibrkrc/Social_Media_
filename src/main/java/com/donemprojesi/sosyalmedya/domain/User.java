package com.donemprojesi.sosyalmedya.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class User {

    private String userId;
    private String username;
    private String email;
    private String passwordHash; // Gerçekte hash'lenmiş şifre tutulur

    private String fullName;
    private String bio;
    private String profilePictureUrl;

    // İlişkisel Veri Yapıları (Şimdilik SQL'de değil, bellekte tutulacaklar)
    private List<Post> posts;
    private List<User> followers;
    private List<User> following;
    private List<Notification> notifications;


    // --- YENİ CONSTRUCTOR (Veritabanından Yüklemek İçin) ---
    // SqliteUserRepository'nin 'mapRowToUser' metodu bu constructor'ı kullanacak.
    public User(String userId, String username, String email, String passwordHash, String bio, String profilePictureUrl) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.bio = (bio != null) ? bio : "Merhaba!"; // DB'den null gelirse varsayılan ata
        this.profilePictureUrl = (profilePictureUrl != null) ? profilePictureUrl : "default.png";

        // Listeleri her zaman boş başlat
        this.posts = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    // --- ESKİ CONSTRUCTOR (Yeni Kullanıcı Kaydı İçin) - GÜNCELLENDİ ---
    // Bu metot artık diğer constructor'ı 'this()' ile çağırıyor.
    public User(String username, String email, String password) {
        this(
                UUID.randomUUID().toString(), // 1. userId
                username,                     // 2. username
                email,                        // 3. email
                BCrypt.hashpw(password, BCrypt.gensalt()), // 4. passwordHash (Güvenli hash oluştur)
                "Merhaba, bu benim yeni hesabım!", // 5. bio
                "default.png"                 // 6. profilePictureUrl
        );
        // Not: 'setPassword' metodunu çağırmaya gerek kalmadı.
    }

    // --- Getter ve Setter Metotları ---

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getBio() { return bio; }
    public String getProfilePictureUrl() { return profilePictureUrl; }

    // YENİ EKLENEN GETTER (SqliteUserRepository'nin 'save' metodu için gerekli)
    public String getPasswordHash() { return this.passwordHash; }

    // Şifre doğrulama (hiç değişmedi)
    /**
     * Şifre doğrulama (GÜVENLİ)
     * @param plainTextPassword Kullanıcının girdiği düz şifre
     * @return Şifre doğruysa true
     */
    public boolean checkPassword(String plainTextPassword) {
        // BCrypt, girilen düz şifreyi veritabanındaki hash ile karşılaştırır.
        try {
            return BCrypt.checkpw(plainTextPassword, this.passwordHash);
        } catch (IllegalArgumentException e) {
            // Hash geçersizse (örn: eski "hashed_123" formatı) hata verir.
            System.out.println("Geçersiz hash formatı ile giriş denemesi: " + this.username);
            return false;
        }
    }

    // --- Diğer Metotlar (Değişmedi) ---
    // (createPost, commentOnPost, followUser, addFollower, receiveNotification, getNewsFeed,
    // getPosts, getFollowing, getFollowers, getNotifications, equals, hashCode)
    // BU METOTLARIN HEPSİ OLDUĞU GİBİ KALABİLİR (Onları silme, sadece bu yeni kodları ekle/değiştir)

    // ... (createPost, commentOnPost, followUser ve diğer tüm eski metotların burada olmalı) ...
    // ... (Sadece constructor'ları ve getter'ları güncelledik)

    // --- Önceki kodundaki metotları buraya kopyala ---
    // (Aşağıdaki metotlar sende zaten vardı, eksikse ekle)
    public Post createPost(String content) {
        Post newPost = new Post(content, this);
        this.posts.add(newPost);
        return newPost;
    }

    public Comment commentOnPost(Post postToComment, String content) {
        Comment newComment = new Comment(content, this, postToComment);
        postToComment.addComment(newComment);

        if (!postToComment.getAuthor().equals(this)) {
            Notification notification = new Notification(NotificationType.COMMENT, this, postToComment.getAuthor());
            postToComment.getAuthor().receiveNotification(notification);
        }
        return newComment;
    }

    public void followUser(User userToFollow) {
        if (!this.following.contains(userToFollow)) {
            this.following.add(userToFollow);
            userToFollow.addFollower(this);
            Notification notification = new Notification(NotificationType.NEW_FOLLOWER, this, userToFollow);
            userToFollow.receiveNotification(notification);
        }
    }

    protected void addFollower(User follower) {
        if (!this.followers.contains(follower)) {
            this.followers.add(follower);
        }
    }

    public void receiveNotification(Notification notification) {
        this.notifications.add(0, notification);
    }

    public List<Post> getNewsFeed() {
        List<Post> newsFeed = new ArrayList<>();
        for (User followedUser : this.following) {
            newsFeed.addAll(followedUser.getPosts());
        }
        newsFeed.sort((p1, p2) -> p2.getTimestamp().compareTo(p1.getTimestamp()));
        return newsFeed;
    }

    public List<Post> getPosts() { return posts; }
    public List<User> getFollowers() { return followers; }
    public List<User> getFollowing() { return following; }
    public List<Notification> getNotifications() { return notifications; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }
    // --- Metotların sonu ---
}