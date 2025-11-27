package com.donemprojesi.sosyalmedya.repository;

import com.donemprojesi.sosyalmedya.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SqliteUserRepository implements IUserRepository {

    /**
     * Bir kullanıcıyı veritabanına kaydeder veya günceller.
     * SQL'deki 'INSERT OR REPLACE' komutu sayesinde,
     * verilen 'userId' zaten varsa satırı günceller, yoksa yeni satır ekler.
     */
    @Override
    public User save(User user) {
        String sql = "INSERT OR REPLACE INTO users (userId, username, email, passwordHash, bio, profilePictureUrl) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getEmail());

            // User sınıfına eklediğimiz 'getPasswordHash()' metodunu kullanır
            pstmt.setString(4, user.getPasswordHash());

            pstmt.setString(5, user.getBio());
            pstmt.setString(6, user.getProfilePictureUrl());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("User save/update hatası: " + e.getMessage());
        }
        return user;
    }

    /**
     * Kullanıcıyı 'username' (kullanıcı adı) ile veritabanından bulur.
     */
    @Override
    public Optional<User> findByUsername(String username) {
        // 4. Repository'ye gelen veriyi kontrol et
        System.out.println("--- DEBUG (Repo): 'findByUsername' metodu şununla çağrıldı: [" + username + "]");

        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) { // Sadece bir kez çağır
                System.out.println("--- DEBUG (Repo): Başarılı! Veritabanında eşleşme bulundu.");
                User user = mapRowToUser(rs);
                return Optional.of(user);
            } else {
                System.out.println("--- DEBUG (Repo): Başarısız! Veritabanında eşleşme bulunamadı.");
            }

        } catch (SQLException e) {
            System.out.println("findByUsername hatası: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Kullanıcıyı 'userId' (benzersiz ID) ile veritabanından bulur.
     */
    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE userId = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = mapRowToUser(rs);
                return Optional.of(user);
            }

        } catch (SQLException e) {
            System.out.println("findById hatası: " + e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public void follow(String followerId, String followedId) {
        // 'INSERT OR IGNORE': Eğer bu ilişki (bu satır) zaten varsa,
        // (PK çakışması nedeniyle) hata verme, sadece görmezden gel.
        // Bu, bir kullanıcıyı iki kez takip etmeye çalışmanın hata vermesini engeller.
        String sql = "INSERT OR IGNORE INTO followers (followerId, followedId) VALUES (?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, followerId);
            pstmt.setString(2, followedId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Follow işlemi hatası: " + e.getMessage());
        }
    }

    @Override
    public boolean isFollowing(String followerId, String followedId) {
        // 'SELECT 1' -> Veri çekmekle uğraşma, sadece '1' döndür (var mı yok mu kontrolü)
        // 'LIMIT 1' -> Bir tane bulduğun an aramayı durdur (performans için)
        String sql = "SELECT 1 FROM followers WHERE followerId = ? AND followedId = ? LIMIT 1";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, followerId);
            pstmt.setString(2, followedId);

            ResultSet rs = pstmt.executeQuery();

            // Eğer rs.next() true ise, en az bir satır bulundu demektir (yani takip ediyor)
            return rs.next();

        } catch (SQLException e) {
            System.out.println("isFollowing check hatası: " + e.getMessage());
            // Hata durumunda, varsayılan olarak 'etmiyor' kabul edelim
            return false;
        }
    }

    /**
     * Veritabanından gelen bir satırı (ResultSet) tam bir User nesnesine dönüştüren
     * yardımcı metot.
     * @param rs Veritabanından gelen sonuç seti
     * @return Bir User nesnesi
     * @throws SQLException
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        // Bu metot, User sınıfına eklediğimiz YENİ constructor'ı kullanır.
        return new User(
                rs.getString("userId"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("passwordHash"),
                rs.getString("bio"),
                rs.getString("profilePictureUrl")
        );
    }
}