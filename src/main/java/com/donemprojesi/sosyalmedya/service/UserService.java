package com.donemprojesi.sosyalmedya.service;

import com.donemprojesi.sosyalmedya.domain.User;
import com.donemprojesi.sosyalmedya.repository.IUserRepository;
import com.donemprojesi.sosyalmedya.service.exception.UserRegistrationException;
import java.util.Optional;

public class UserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String email, String password) throws UserRegistrationException {

        // --- YENİ EKLENEN GÜVENLİK KONTROLÜ ---
        // Girdilerin boş olup olmadığını kontrol et ve boşlukları temizle
        if (username == null || username.trim().isEmpty()) {
            throw new UserRegistrationException("Kullanıcı adı boş olamaz.");
        }
        if (password == null || password.isEmpty()) {
            throw new UserRegistrationException("Şifre boş olamaz.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new UserRegistrationException("E-posta boş olamaz.");
        }

        // Kullanıcı adındaki ve e-postadaki baş/son boşlukları temizle
        username = username.trim();
        email = email.trim();
        // --- KONTROLÜN SONU ---
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new UserRegistrationException("Bu kullanıcı adı zaten alınmış: " + username);
        }
        User newUser = new User(username, email, password);
        userRepository.save(newUser);
        System.out.println("Kullanıcı başarıyla oluşturuldu: " + username);
        return newUser;
    }

    /**
     * Bir kullanıcının sisteme giriş yapmasını sağlar.
     * (ÇÖKME HATASI DÜZELTİLDİ)
     */
    public Optional<User> loginUser(String username, String password) {

        // 1. Repository'den kullanıcıyı bul
        Optional<User> userOptional = userRepository.findByUsername(username);

        // 2. KULLANICI BULUNAMADIYSA, boş dön (Çökmemesi için kontrol burada!)
        if (!userOptional.isPresent()) {
            return Optional.empty();
        }

        // 3. Kullanıcıyı al ve şifreyi kontrol et
        User user = userOptional.get();
        if (user.checkPassword(password)) {
            // Şifre doğru
            return Optional.of(user);
        } else {
            // Şifre yanlış
            return Optional.empty();
        }
    }

    /**
     * Bir kullanıcının başka bir kullanıcıyı takip etmesini sağlar (Veritabanı versiyonu).
     * @param currentUserId Takip eden kullanıcının ID'si
     * @param targetUserId Takip edilen kullanıcının ID'si
     * @return İşlem başarılıysa true
     */
    /**
     * Bir kullanıcının başka bir kullanıcıyı takip etmesini sağlar.
     * Zaten takip ediyorsa uyarı verir.
     */
    public boolean followUser(String currentUserId, String targetUserId) {

        Optional<User> currentUserOpt = userRepository.findById(currentUserId);
        Optional<User> targetUserOpt = userRepository.findById(targetUserId);

        // 1. Kullanıcılar var mı diye kontrol et
        if (!currentUserOpt.isPresent() || !targetUserOpt.isPresent()) {
            System.out.println("Hata: Kullanıcılardan biri bulunamadı.");
            return false;
        }

        User currentUser = currentUserOpt.get();
        User targetUser = targetUserOpt.get();

        // 2. YENİ KONTROL: Zaten takip ediyor mu diye kontrol et
        if (userRepository.isFollowing(currentUserId, targetUserId)) {
            System.out.println(targetUser.getUsername() + " kişisini zaten takip ediyorsunuz.");
            return false; // Yeni bir eylem yapılmadı
        }

        // 3. Takip etmiyorsa, takip ilişkisini oluştur
        userRepository.follow(currentUserId, targetUserId);
        System.out.println(currentUser.getUsername() + ", " + targetUser.getUsername() + " kişisini takip etmeye başladı.");
        return true;
    }
}