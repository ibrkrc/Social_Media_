package com.donemprojesi.sosyalmedya.repository;

import com.donemprojesi.sosyalmedya.domain.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryUserRepository implements IUserRepository {

    private static final Map<String, User> database = new HashMap<>();

    // ******************************************************
    // *** İLK HATA BURADAYDI - ŞİMDİ DÜZELTİLDİ ***
    // ******************************************************
    @Override
    public User save(User user) {
        // Kullanıcıyı 'username' ile değil, 'userId' ile kaydetmeliyiz.
        // ÇÜNKÜ: 'findById' metodu 'userId' kullanarak arama yapıyor.
        database.put(user.getUserId(), user);
        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        // Veritabanındaki tüm değerleri (User nesneleri) dolaş
        return database.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Optional<User> findById(String id) {
        // 'save' metodu 'userId' ile kaydettiği için
        // bu metot da 'id' ile doğrudan veriyi bulabilir.
        return Optional.ofNullable(database.get(id));
    }
    @Override
    public void follow(String followerId, String followedId) {
        // Bu sınıfı artık kullanmadığımız için bu metodun içini
        // boş bırakabiliriz. Sadece derleme hatasını düzeltmek için eklendi.
    }
    @Override
    public boolean isFollowing(String followerId, String followedId){
        return false;
    }

}
