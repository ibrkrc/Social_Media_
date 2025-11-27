package com.donemprojesi.sosyalmedya.service;

import com.donemprojesi.sosyalmedya.domain.Post;
import com.donemprojesi.sosyalmedya.domain.User;
import com.donemprojesi.sosyalmedya.repository.IPostRepository;
import com.donemprojesi.sosyalmedya.repository.IUserRepository;

import java.util.List;

public class PostService {

    // Bağımlılıklar (Dependencies)
    private final IPostRepository postRepository;
    private final IUserRepository userRepository; // Post'ların yazarlarını bulmak için

    /**
     * PostService oluşturulurken, ihtiyaç duyduğu depolar (repository)
     * ona dışarıdan verilir (Dependency Injection).
     * @param postRepository Post veritabanı işlemleri için
     * @param userRepository Kullanıcı veritabanı işlemleri için
     */
    public PostService(IPostRepository postRepository, IUserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /**
     * Yeni bir gönderi oluşturur ve veritabanına kaydeder.
     * @param content Gönderinin metni
     * @param author Gönderiyi oluşturan User nesnesi
     * @return Oluşturulan ve kaydedilen Post nesnesi
     */
    public Post createPost(String content, User author) {
        // 1. Domain nesnesini oluştur (Post.java constructor'ı kullanılır)
        Post newPost = new Post(content, author);

        // 2. Repository katmanına "kaydet" emri ver
        postRepository.save(newPost);

        System.out.println("Yeni gönderi oluşturuldu: " + newPost.getPostId());
        return newPost;
    }

    /**
     * Belirli bir kullanıcının (profil sahibinin) tüm gönderilerini getirir.
     * @param user Gönderileri istenen kullanıcı
     * @return O kullanıcıya ait gönderilerin listesi (en yeniden eskiye)
     */
    public List<Post> getPostsForUser(User user) {
        // Doğrudan 'postRepository'ye gidip yazar ID'sine göre arama yaparız
        return postRepository.findAllByAuthorId(user.getUserId());
    }

    /**
     * Bir kullanıcının "Haber Akışını" (takip ettiği kişilerin gönderileri) getirir.
     * @param user Akışı görüntüleyen kullanıcı
     * @return Takip edilen kişilerin gönderilerinin listesi (en yeniden eskiye)
     */
    public List<Post> getNewsFeedForUser(User user) {
        // NOT: 'SqlitePostRepository'deki 'findNewsFeedForUser' metodu,
        // şu anda 'user.getFollowing()' listesini (bellekteki listeyi) kullanıyor.
        // Bir sonraki adımımız, 'following' (takip) ilişkisini de veritabanına
        // taşımak olacaktır. Şimdilik bu yöntem çalışacaktır.
        return postRepository.findNewsFeedForUser(user.getUserId());
    }

    // (Gelecekte buraya 'deletePost', 'commentOnPost' gibi metotlar eklenebilir)
}