package com.donemprojesi.sosyalmedya.repository;

import com.donemprojesi.sosyalmedya.domain.Post;
import com.donemprojesi.sosyalmedya.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlitePostRepository implements IPostRepository {

    // Post'ları veritabanından okurken 'User' bilgilerine de ihtiyacımız olacak.
    // 'authorId'yi 'User' nesnesine dönüştürmek için 'userRepository' kullanılır.
    // Bu, 'Dependency Injection' (Bağımlılık Enjeksiyonu) için iyi bir örnektir.
    private final IUserRepository userRepository;

    public SqlitePostRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Post save(Post post) {
        String sql = "INSERT INTO posts (postId, content, timestamp, authorId) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, post.getPostId());
            pstmt.setString(2, post.getContent());

            // Post'taki 'Date' nesnesini SQL'de saklamak için 'long' (epoch time) çeviririz
            pstmt.setLong(3, post.getTimestamp().getTime());

            pstmt.setString(4, post.getAuthor().getUserId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Post save hatası: " + e.getMessage());
        }
        return post;
    }

    @Override
    public List<Post> findAllByAuthorId(String userId) {
        List<Post> posts = new ArrayList<>();
        // Gönderileri en yeniden eskiye doğru sırala (ORDER BY ... DESC)
        String sql = "SELECT * FROM posts WHERE authorId = ? ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Veritabanı satırını bir Post nesnesine dönüştür
                Post post = mapRowToPost(rs);
                posts.add(post);
            }

        } catch (SQLException e) {
            System.out.println("findAllByAuthorId hatası: " + e.getMessage());
        }
        return posts;
    }

    /**
     * Bir kullanıcının haber akışını (takip ettiklerinin gönderileri)
     * doğrudan veritabanından çeker.
     * @param userId Akışı görüntüleyen kullanıcının ID'si
     * @return Gönderi listesi
     */
    @Override
    public List<Post> findNewsFeedForUser(String userId) {
        List<Post> newsFeed = new ArrayList<>();

        // BU YENİ VE GÜÇLÜ SQL SORGUSU:
        // 'posts' tablosundan şunları seç:
        // 'authorId' (gönderi yazarı), 'followers' tablosunda
        // 'followerId'si BİZİM KULLANICI ID'miz olan satırların 'followedId' (takip edilen)
        // listesi içinde olan gönderileri.
        String sql = "SELECT * FROM posts WHERE authorId IN "
                + "(SELECT followedId FROM followers WHERE followerId = ?) "
                + "ORDER BY timestamp DESC";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Sorgudaki '?' işaretini kullanıcının ID'si ile doldur
            pstmt.setString(1, userId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                newsFeed.add(mapRowToPost(rs)); // mapRowToPost metodumuz zaten vardı
            }

        } catch (SQLException e) {
            System.out.println("findNewsFeedForUser (SQL) hatası: " + e.getMessage());
        }

        return newsFeed;
    }

    /**
     * Veritabanı satırını (ResultSet) bir Post nesnesine dönüştürür.
     */
    private Post mapRowToPost(ResultSet rs) throws SQLException {
        String postId = rs.getString("postId");
        String content = rs.getString("content");
        long timestamp = rs.getLong("timestamp");
        String authorId = rs.getString("authorId");

        // Yazarın (Author) kim olduğunu bulmak için 'userRepository' kullanılır
        // Bu, N+1 problemine yol açabilir ancak bu proje için yeterince verimlidir.
        User author = userRepository.findById(authorId)
                .orElse(null); // Yazar bulunamazsa (silinmişse) null ata

        // Post.java'ya eklediğimiz yeni constructor'ı kullanır
        Post post = new Post(postId, content, timestamp, author);

        return post;
    }
}