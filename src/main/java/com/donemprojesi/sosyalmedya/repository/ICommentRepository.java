package com.donemprojesi.sosyalmedya.repository;

import com.donemprojesi.sosyalmedya.domain.Comment;
import java.util.List;

public interface ICommentRepository {

    /**
     * Yeni bir yorumu veritabanına kaydeder.
     * @param comment Kaydedilecek Comment nesnesi
     * @return Kaydedilen Comment nesnesi
     */
    Comment save(Comment comment);

    /**
     * Belirli bir gönderiye (post) ait tüm yorumları bulur.
     * @param postId Yorumların ait olduğu gönderinin ID'si
     * @return O gönderiye ait yorumların listesi (eskiden yeniye)
     */
    List<Comment> findAllByPostId(String postId);
}