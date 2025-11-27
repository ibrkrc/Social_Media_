package com.donemprojesi.sosyalmedya.service;

import com.donemprojesi.sosyalmedya.domain.Comment;
import com.donemprojesi.sosyalmedya.domain.Post;
import com.donemprojesi.sosyalmedya.domain.User;
import com.donemprojesi.sosyalmedya.repository.ICommentRepository;
import com.donemprojesi.sosyalmedya.repository.IPostRepository;
import com.donemprojesi.sosyalmedya.repository.IUserRepository;

import java.util.List;

public class CommentService {

    // Bağımlılıklar (Dependencies)
    private final ICommentRepository commentRepository;
    private final IUserRepository userRepository;
    private final IPostRepository postRepository;

    /**
     * CommentService oluşturulurken, ihtiyaç duyduğu tüm depolar (repository)
     * ona dışarıdan verilir (Dependency Injection).
     */
    public CommentService(ICommentRepository commentRepository, IUserRepository userRepository, IPostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    /**
     * Yeni bir yorum oluşturur ve veritabanına kaydeder.
     * @param content Yorumun metni
     * @param author Yorumu yapan User nesnesi
     * @param parentPost Yorumun yapıldığı Post nesnesi
     * @return Oluşturulan ve kaydedilen Comment nesnesi
     */
    public Comment createComment(String content, User author, Post parentPost) {
        // 1. Domain nesnesini oluştur (Comment.java constructor'ı kullanılır)
        Comment newComment = new Comment(content, author, parentPost);

        // 2. Repository katmanına "kaydet" emri ver
        commentRepository.save(newComment);

        System.out.println("Yeni yorum oluşturuldu: " + newComment.getCommentId());
        return newComment;
    }

    /**
     * Belirli bir gönderiye ait tüm yorumları veritabanından getirir.
     * @param postId Yorumları istenen gönderinin ID'si
     * @return O gönderiye ait yorumların listesi (eskiden yeniye)
     */
    public List<Comment> getCommentsForPost(String postId) {
        // 1. Yorumları 'commentRepository' kullanarak veritabanından al.
        //    (Bu yorumların 'author' alanı dolu, 'parentPost' alanı null gelir)
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        // 2. (İyileştirme) Yorumların 'parentPost' alanını da doldurabiliriz,
        //    ancak bu, tüm yorumların aynı posta ait olduğunu bildiğimiz için
        //    şu anda gerekli değil. Yorumun yazar (author) bilgisi
        //    SqliteCommentRepository'deki mapRowToComment metodu sayesinde
        //    zaten doldurulmuştu.

        return comments;
    }
}