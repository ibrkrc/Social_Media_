package com.donemprojesi.sosyalmedya.repository;

import com.donemprojesi.sosyalmedya.domain.Comment;
import com.donemprojesi.sosyalmedya.domain.Post;
import com.donemprojesi.sosyalmedya.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqliteCommentRepository implements ICommentRepository {

    // Yorumları nesneye dönüştürürken yazar (User) ve gönderi (Post)
    // bilgilerine ihtiyacımız olacak. Ancak bu, 'dairesel bağımlılık'
    // (circular dependency) yaratabilir.
    // Çözüm: Repository sadece ID'leri okur, Service katmanı
    // bu ID'leri kullanarak nesneleri (User, Post) bulur ve atar.

    // Şimdilik daha basit bir yaklaşım kullanalım:
    private final IUserRepository userRepository;
    // PostRepository'ye de ihtiyaç duyacağız ama bu yapıyı karmaşıklaştırır.
    // Şimdilik sadece author'u (yazarı) dolduralım.

    public SqliteCommentRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public Comment save(Comment comment) {
        String sql = "INSERT INTO comments (commentId, content, timestamp, authorId, postId) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, comment.getCommentId());
            pstmt.setString(2, comment.getContent());
            pstmt.setLong(3, comment.getTimestamp().getTime());
            pstmt.setString(4, comment.getAuthor().getUserId());
            pstmt.setString(5, comment.getParentPost().getPostId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Comment save hatası: " + e.getMessage());
        }
        return comment;
    }

    @Override
    public List<Comment> findAllByPostId(String postId) {
        List<Comment> comments = new ArrayList<>();
        // Yorumları eskiden yeniye doğru sırala (ORDER BY ... ASC)
        String sql = "SELECT * FROM comments WHERE postId = ? ORDER BY timestamp ASC";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, postId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Veritabanı satırını bir Comment nesnesine dönüştür
                comments.add(mapRowToComment(rs));
            }

        } catch (SQLException e) {
            System.out.println("findAllByPostId hatası: " + e.getMessage());
        }
        return comments;
    }

    /**
     * Veritabanı satırını (ResultSet) bir Comment nesnesine dönüştürür.
     */
    private Comment mapRowToComment(ResultSet rs) throws SQLException {
        String commentId = rs.getString("commentId");
        String content = rs.getString("content");
        long timestamp = rs.getLong("timestamp");
        String authorId = rs.getString("authorId");
        String postId = rs.getString("postId"); // Bu bilgiye de sahibiz

        // Yazarın (Author) kim olduğunu bulmak için 'userRepository' kullanılır
        User author = userRepository.findById(authorId)
                .orElse(null); // Yazar bulunamazsa (silinmişse) null ata

        // Not: parentPost nesnesini (yorumun ait olduğu gönderi) burada doldurmuyoruz.
        // Bunu Service katmanı yapacak.

        // Comment.java'ya eklediğimiz yeni constructor'ı kullanırız
        Comment comment = new Comment(commentId, content, timestamp, author, null);

        return comment;
    }
}