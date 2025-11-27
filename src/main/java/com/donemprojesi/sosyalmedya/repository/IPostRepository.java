package com.donemprojesi.sosyalmedya.repository;

import com.donemprojesi.sosyalmedya.domain.Post;
import com.donemprojesi.sosyalmedya.domain.User;
import java.util.List;
import java.util.Optional;

public interface IPostRepository {

    /**
     * Yeni bir gönderiyi veritabanına kaydeder.
     * @param post Kaydedilecek Post nesnesi
     * @return Kaydedilen Post nesnesi
     */
    Post save(Post post);

    /**
     * Belirli bir kullanıcının tüm gönderilerini zaman sırasına göre bulur.
     * @param userId Yazarın (author) ID'si
     * @return O yazara ait gönderilerin listesi
     */
    List<Post> findAllByAuthorId(String userId);

    /**
     * Bir kullanıcının takip ettiği kişilerin gönderilerini (haber akışı) bulur.
     * @param user Habar akışı istenen kullanıcı
     * @return Takip edilen kişilerin gönderilerinin listesi (zaman sırasına göre)
     */
    List<Post> findNewsFeedForUser(String userId);

    // (Gelecekte buraya 'delete(Post post)', 'findById(String postId)' eklenebilir)
}