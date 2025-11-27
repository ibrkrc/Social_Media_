package com.donemprojesi.sosyalmedya.repository;

import com.donemprojesi.sosyalmedya.domain.User;
import java.util.Optional;

// "I" harfi, bunun bir Interface (Arayüz) olduğunu belirtir (yaygın bir standarttır)
public interface IUserRepository {

    /**
     * Yeni bir kullanıcıyı kaydeder.
     * @param user Kaydedilecek User nesnesi
     * @return Kaydedilen User nesnesi
     */
    User save(User user);

    /**
     * Verilen 'username' (kullanıcı adı) ile bir kullanıcı arar.
     * @param username Aranacak kullanıcı adı
     * @return Kullanıcıyı bulursa 'Optional' içinde döner, bulamazsa boş 'Optional' döner.
     */
    Optional<User> findByUsername(String username);

    /**
     * Verilen 'id' ile bir kullanıcı arar.
     * @param id Aranacak kullanıcı ID'si
     * @return Kullanıcıyı bulursa 'Optional' içinde döner, bulamazsa boş 'Optional' döner.
     */
    Optional<User> findById(String id);

    // (Gelecekte buraya 'delete', 'update' gibi metotlar eklenebilir)

    /**
     * Bir takip ilişkisini veritabanına kaydeder.
     * @param followerId Takip eden kullanıcının ID'si
     * @param followedId Takip edilen kullanıcının ID'si
     */
    void follow(String followerId, String followedId);

    // (Daha sonra buraya 'unfollow' da eklenebilir)

    /**
     * Bir kullanıcının başka bir kullanıcıyı zaten takip edip etmediğini kontrol eder.
     * @param followerId Takip eden
     * @param followedId Takip edilen
     * @return Takip ediyorsa true, etmiyorsa false
     */
    boolean isFollowing(String followerId, String followedId);
}