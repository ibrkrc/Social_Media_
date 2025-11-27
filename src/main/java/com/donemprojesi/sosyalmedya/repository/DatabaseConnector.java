package com.donemprojesi.sosyalmedya.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {

    private static final String DATABASE_URL = "jdbc:sqlite:sosyalmedya.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            System.out.println("Veritabanı bağlantı hatası: " + e.getMessage());
        }
        return conn;
    }

    /**
     * Uygulama ilk başladığında veritabanı tablolarımızı oluşturacak olan metot.
     */
    public static void initializeDatabase() {

        // 1. users tablosu
        String sqlCreateUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + " userId TEXT PRIMARY KEY,"
                + " username TEXT NOT NULL UNIQUE,"
                + " email TEXT NOT NULL UNIQUE,"
                + " passwordHash TEXT NOT NULL,"
                + " bio TEXT,"
                + " profilePictureUrl TEXT"
                + ");";

        // 2. posts tablosu
        String sqlCreatePostsTable = "CREATE TABLE IF NOT EXISTS posts ("
                + " postId TEXT PRIMARY KEY,"
                + " content TEXT NOT NULL,"
                + " timestamp INTEGER NOT NULL,"
                + " authorId TEXT NOT NULL,"
                + " FOREIGN KEY (authorId) REFERENCES users(userId) ON DELETE CASCADE"
                + ");";

        // 3. followers (takipleşme) tablosu (EKSİK OLAN BUYDU)
        String sqlCreateFollowersTable = "CREATE TABLE IF NOT EXISTS followers ("
                + " followerId TEXT NOT NULL,"
                + " followedId TEXT NOT NULL,"
                + " PRIMARY KEY (followerId, followedId),"
                + " FOREIGN KEY (followerId) REFERENCES users(userId) ON DELETE CASCADE,"
                + " FOREIGN KEY (followedId) REFERENCES users(userId) ON DELETE CASCADE"
                + ");";

        // --- YENİ EKLENEN KOD ---
        // 4. comments (yorumlar) tablosu
        String sqlCreateCommentsTable = "CREATE TABLE IF NOT EXISTS comments ("
                + " commentId TEXT PRIMARY KEY,"
                + " content TEXT NOT NULL,"
                + " timestamp INTEGER NOT NULL,"

                // Yorumu kimin yazdığı (Kullanıcıya bağlı)
                + " authorId TEXT NOT NULL,"
                // Yorumun hangi gönderiye yazıldığı (Gönderiye bağlı)
                + " postId TEXT NOT NULL,"

                // Yabancı Anahtarlar (Veri Bütünlüğü İçin)
                + " FOREIGN KEY (authorId) REFERENCES users(userId) ON DELETE CASCADE,"
                + " FOREIGN KEY (postId) REFERENCES posts(postId) ON DELETE CASCADE"
                + ");";

        // 'try-with-resources' bloğu: 'conn' ve 'stmt' otomatik kapatılır
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // --- ÜÇ TABLOYU DA ÇALIŞTIR ---
            stmt.execute(sqlCreateUsersTable);
            stmt.execute(sqlCreatePostsTable);
            stmt.execute(sqlCreateFollowersTable); // Bu satır eksikti
            stmt.execute(sqlCreateCommentsTable);

            System.out.println("Veritabanı tabloları başarıyla hazırlandı (veya zaten hazırdı).");

        } catch (SQLException e) {
            System.out.println("Tablo oluşturma hatası: " + e.getMessage());
        }
    }
}