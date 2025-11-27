package com.donemprojesi.sosyalmedya.ui;
import com.donemprojesi.sosyalmedya.repository.DatabaseConnector;
import com.donemprojesi.sosyalmedya.domain.Post;
import com.donemprojesi.sosyalmedya.domain.User;
import com.donemprojesi.sosyalmedya.domain.Notification;
import com.donemprojesi.sosyalmedya.repository.IUserRepository;
import com.donemprojesi.sosyalmedya.repository.InMemoryUserRepository;
import com.donemprojesi.sosyalmedya.repository.SqliteUserRepository;
import com.donemprojesi.sosyalmedya.service.UserService;
import com.donemprojesi.sosyalmedya.service.exception.UserRegistrationException;
import com.donemprojesi.sosyalmedya.service.PostService;
import com.donemprojesi.sosyalmedya.repository.IPostRepository; // Yeni
import com.donemprojesi.sosyalmedya.repository.SqlitePostRepository; // Yeni
import com.donemprojesi.sosyalmedya.domain.Comment; // Yeni
import com.donemprojesi.sosyalmedya.repository.ICommentRepository; // Yeni
import com.donemprojesi.sosyalmedya.repository.SqliteCommentRepository; // Yeni
import com.donemprojesi.sosyalmedya.service.CommentService; // Yeni

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    // --- Katmanları Başlatma ---
    // Tüm katmanlara erişim 'static' olacak ki 'main' metodu içinden erişilebilsin.

    // 1. Repository Katmanı (Veritabanımız)
    // Eski Kod : private static IUserRepository userRepository = new InMemoryUserRepository();

    private static IUserRepository userRepository = new SqliteUserRepository();

    // 2. Service Katmanı (İş Mantığımız)
    // Servis'e hangi veritabanını kullanacağını söylüyoruz (Dependency Injection)
    private static UserService userService = new UserService(userRepository);

    // --- POST (YENİ EKLENDİ) ---
    // SqlitePostRepository, IUserRepository'ye ihtiyaç duyduğu için ona 'userRepository' veriyoruz.
    private static IPostRepository postRepository = new SqlitePostRepository(userRepository);
    private static PostService postService = new PostService(postRepository, userRepository);

    // --- COMMENT (YENİ EKLENDİ) ---
    // SqliteCommentRepository, IUserRepository'ye ihtiyaç duyduğu için ona 'userRepository' veriyoruz.
    private static ICommentRepository commentRepository = new SqliteCommentRepository(userRepository);
    private static CommentService commentService = new CommentService(commentRepository, userRepository, postRepository);

    // 3. UI Katmanı (Arayüz)
    private static Scanner scanner = new Scanner(System.in);

    // --- Oturum Yönetimi ---
    // Hangi kullanıcının giriş yaptığını tutan değişken
    private static User loggedInUser = null;

    /**
     * Uygulamanın Ana Giriş Noktası
     */
    public static void main(String[] args) {

        // **********************************
        // *** YENİ EKLENEN SATIR ***
        // **********************************
        // Uygulama başlamadan önce veritabanı ve tablolar hazır olsun
        DatabaseConnector.initializeDatabase();

        // Test etmeyi kolaylaştırmak için birkaç sahte veri oluşturalım
        // createDummyData(); // (Bunu SQL'e geçince güncelleyeceğiz, şimdilik kapatabilirsin)

        System.out.println("Sosyal Medya Platformuna Hoş Geldiniz!"); // (Artık Teknofest demiyoruz :) )

        // Ana menü döngüsünü başlat
        runMainMenu();

        // Program bittiğinde kaynakları serbest bırak
        scanner.close();


        // Test etmeyi kolaylaştırmak için birkaç sahte veri oluşturalım
        createDummyData();

        System.out.println("Teknofest Sosyal Medya Platformuna Hoş Geldiniz!");

        // Ana menü döngüsünü başlat
        runMainMenu();

        // Program bittiğinde kaynakları serbest bırak
        scanner.close();
    }

    /**
     * Kullanıcı giriş yapmadığında gösterilen ana menü
     */
    private static void runMainMenu() {
        while (loggedInUser == null) { // Kullanıcı giriş yapana kadar bu menüde kal
            System.out.println("\n--- Ana Menü ---");
            System.out.println("1. Giriş Yap");
            System.out.println("2. Kayıt Ol");
            System.out.println("0. Çıkış");
            System.out.print("Seçiminiz: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleLogin();
                    // Eğer giriş başarılıysa (loggedInUser null değilse),
                    // runUserMenu()'yü tetikle
                    if (loggedInUser != null) {
                        runUserMenu();
                    }
                    break;
                case "2":
                    handleRegister();
                    break;
                case "0":
                    System.out.println("Uygulamadan çıkılıyor...");
                    return; // Döngüden çık ve main metodunu bitir
                default:
                    System.out.println("Geçersiz seçim!");
            }
        }
    }

    /**
     * Kullanıcı başarıyla giriş yaptığında gösterilen menü
     */
    private static void runUserMenu() {
        System.out.println("\nHoş geldin, " + loggedInUser.getUsername() + "!");

        while (loggedInUser != null) { // Kullanıcı çıkış yapana kadar bu menüde kal
            System.out.println("\n--- Kullanıcı Menüsü ---");
            System.out.println("1. Haber Akışını Görüntüle");
            System.out.println("2. Yeni Gönderi Oluştur");
            System.out.println("3. Bir Kullanıcıyı Takip Et");
            System.out.println("4. Bildirimleri Görüntüle (" + loggedInUser.getNotifications().size() + ")");

            // --- GÜNCELLEME ---
            System.out.println("5. Gönderilerimi Yönet (Gör ve Yorum Yap)"); // Metin değişti
            // --- GÜNCELLEME SONU ---
            System.out.println("9. Çıkış Yap (Ana Menüye Dön)");
            System.out.print("Seçiminiz: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleViewNewsFeed();
                    break;
                case "2":
                    handleCreatePost();
                    break;
                case "3":
                    handleFollowUser();
                    break;
                case "4":
                    handleViewNotifications();
                    break;
                // --- GÜNCELLEME ---
                case "5":
                    // handleViewMyPosts(); // Bu metodu değiştireceğiz
                    handlePostManagement(); // Yeni, daha kapsamlı bir metot
                    break;
                // --- GÜNCELLEME SONU ---
                case "9":
                    System.out.println("Çıkış yapılıyor...");
                    loggedInUser = null; // Oturumu sonlandır
                    // 'return' ile bu döngüden çıkar ve runMainMenu() döngüsüne geri döneriz.
                    return;
                default:
                    System.out.println("Geçersiz seçim!");
            }
        }
    }

    // --- Menü İşleyici Metotlar ---

    private static void handleLogin() {
        System.out.println("\n--- Giriş Yap ---");
        System.out.print("Kullanıcı Adı: ");
        String username = scanner.nextLine().trim(); // .trim() eklendi
        System.out.print("Şifre: ");
        String password = scanner.nextLine();

        Optional<User> userOptional = userService.loginUser(username, password);

        if (userOptional.isPresent()) {
            loggedInUser = userOptional.get(); // Oturum açıldı
            System.out.println("Giriş başarılı!");
        } else {
            System.out.println("Hata: Kullanıcı adı veya şifre yanlış.");
        }
    }

    private static void handleRegister() {
        System.out.println("\n--- Yeni Kullanıcı Kaydı ---");
        System.out.print("Kullanıcı Adı: ");
        String username = scanner.nextLine();
        System.out.print("E-posta: ");
        String email = scanner.nextLine();
        System.out.print("Şifre: ");
        String password = scanner.nextLine();

        try {
            userService.registerUser(username, email, password);
            System.out.println("Kayıt başarılı! Lütfen giriş yapın.");
        } catch (UserRegistrationException e) {
            System.out.println("Kayıt hatası: " + e.getMessage());
        }
    }

    private static void handleCreatePost() {
        System.out.println("\n--- Yeni Gönderi Oluştur ---");
        System.out.println("Gönderi içeriğini yazın (bitirmek için enter):");
        String content = scanner.nextLine();

        if (content.isEmpty()) {
            System.out.println("Gönderi boş olamaz.");
            return;
        }

//        // 1. Domain nesnesi (Post) oluşturulur
//        Post newPost = loggedInUser.createPost(content);
//
//        // 2. ÖNEMLİ: User nesnesinin (ve içindeki yeni postun)
//        //    veritabanında (repository) güncellenmesi gerekir.
//        userRepository.save(loggedInUser);
        postService.createPost(content, loggedInUser);

        System.out.println("Gönderi başarıyla oluşturuldu!");
    }

    /**
     * Bir kullanıcının "Haber Akışını" (takip ettiği kişilerin gönderileri)
     * interaktif olarak listeler. Kullanıcı bir gönderi seçip yorum yapabilir.
     */
    private static void handleViewNewsFeed() {
        System.out.println("\n--- Haber Akışı ---");

        // 1. Servis'ten haber akışını çek
        List<Post> newsFeed = postService.getNewsFeedForUser(loggedInUser);

        if (newsFeed.isEmpty()) {
            System.out.println("Haber akışınız boş. Gönderi paylaşmak için yeni kişileri takip edin!");
            return;
        }

        // 2. Gönderileri numaralandırarak listele
        // (Bu, 'handlePostManagement' içindekiyle aynı mantık)
        for (int i = 0; i < newsFeed.size(); i++) {
            Post post = newsFeed.get(i);

            String authorUsername = (post.getAuthor() != null)
                    ? post.getAuthor().getUsername()
                    : "[Silinmiş Kullanıcı]";

            System.out.println("\n--- [" + (i + 1) + "] ---"); // Örn: [1], [2]
            System.out.println("Gönderen: " + authorUsername);
            System.out.println("İçerik: " + post.getContent());
            System.out.println("Tarih: " + post.getTimestamp());
            // Yorum sayısını da getirebiliriz (bunu CommentService'e ekleyebiliriz)
            // Şimdilik yoruma kapalı.
        }

        // 3. Kullanıcıdan bir gönderi seçmesini iste
        System.out.println("\n--------------------");
        System.out.print("Yorumları görmek veya yorum yapmak için bir gönderi numarası seçin (Çıkmak için 0): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) {
                return; // Menüden çık
            }

            // 4. Seçilen gönderi için Yorum Menüsünü aç
            // (Dizi index'i 0'dan başladığı için (choice-1))
            if (choice > 0 && choice <= newsFeed.size()) {
                Post selectedPost = newsFeed.get(choice - 1);

                // --- EN GÜZEL KISIM ---
                // 'handleCommentMenu' metodunu burada YENİDEN KULLANIYORUZ
                handleCommentMenu(selectedPost);
            } else {
                System.out.println("Geçersiz gönderi numarası.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Lütfen geçerli bir sayı girin.");
        }
    }

    private static void handleFollowUser() {
        System.out.println("\n--- Kullanıcı Takip Et ---");
        System.out.print("Takip etmek istediğiniz kullanıcının adı: ");

        // 1. Ham veriyi al
        String targetUsernameInput = scanner.nextLine();
        System.out.println("--- DEBUG (Main): Girilen ham veri: [" + targetUsernameInput + "]");

        // 2. Veriyi temizle
        String targetUsername = targetUsernameInput.trim();
        System.out.println("--- DEBUG (Main): .trim() sonrası veri: [" + targetUsername + "]");


        if (targetUsername.equals(loggedInUser.getUsername())) {
            System.out.println("Kendinizi takip edemezsiniz.");
            return;
        }

        // 3. Temizlenmiş veriyle Repository'yi çağır
        System.out.println("--- DEBUG (Main): Repository 'findByUsername' çağrılıyor...");
        Optional<User> targetUserOpt = userRepository.findByUsername(targetUsername);

        if (!targetUserOpt.isPresent()) {
            System.out.println("Hata: " + targetUsername + " adında bir kullanıcı bulunamadı.");
            return;
        }

        // Kullanıcı bulunduysa 'follow' işlemini çağır
        userService.followUser(loggedInUser.getUserId(), targetUserOpt.get().getUserId());
    }

    private static void handleViewNotifications() {
        System.out.println("\n--- Bildirimler ---");
        List<Notification> notifications = loggedInUser.getNotifications();

        if (notifications.isEmpty()) {
            System.out.println("Yeni bildiriminiz yok.");
            return;
        }

        for (Notification n : notifications) {
            System.out.println("[" + n.getTimestamp() + "] " + n.getMessage());
            // (İsteğe bağlı: n.setRead(true); ve sonra userRepository.save(loggedInUser);)
        }
    }

    private static void handleViewMyPosts() {
        System.out.println("\n--- Gönderilerim ---");

        List<Post> myPosts = postService.getPostsForUser(loggedInUser);

        if (myPosts.isEmpty()) {
            System.out.println("Hiç gönderiniz yok.");
            return;
        }

        // Sadece bu yardımcı metot kalmalı.
        // Eski 'for' döngüsü buradan silinmeli.
        printPostList(myPosts);
    }
    /**
     * Verilen bir Post listesini konsola formatlı bir şekilde yazdırır.
     * @param posts Yazdırılacak gönderi listesi
     */
    private static void printPostList(List<Post> posts) {
        for (Post post : posts) {
            System.out.println("--------------------");
            // Yazarın (author) null olma ihtimaline karşı kontrol
            String authorUsername = (post.getAuthor() != null)
                    ? post.getAuthor().getUsername()
                    : "[Silinmiş Kullanıcı]";

            System.out.println("Gönderen: " + authorUsername);
            System.out.println("İçerik: " + post.getContent());
            System.out.println("Tarih: " + post.getTimestamp());
            System.out.println("Beğeni: " + post.getLikeCount() + " | Yorum: " + post.getCommentCount());
        }
    }

    /**
     * Uygulamayı test edebilmek için sahte kullanıcılar ve veriler oluşturur.
     */
    private static void createDummyData() {
        try {
            // 1. Kullanıcıları oluştur
            User user1 = userService.registerUser("admin", "admin@mail.com", "123");
            User user2 = userService.registerUser("taha", "taha@mail.com", "123");
            User user3 = userService.registerUser("teknofest", "info@teknofest.com", "123");

            // 2. Birkaç gönderi oluştur
            // 'userRepository.save' demeyi unutma!
            user1.createPost("İlk gönderim! Sosyal medya projesi harika.");
            userRepository.save(user1);

            user3.createPost("Savaşan İHA yarışmasına başvurular başladı!");
            userRepository.save(user3);

            // 3. Takipleşme ekle
            // 'taha' 'teknofest'i takip etsin
            userService.followUser(user2.getUserId(), user3.getUserId());

        } catch (UserRegistrationException e) {
            System.out.println("Sahte veri oluşturulurken hata: " + e.getMessage());
        }
    }
    /**
     * Kullanıcının kendi gönderilerini listelemesini, seçmesini ve
     * onlara yorum yapmasını/yorumları görmesini sağlayan ana menü.
     */
    private static void handlePostManagement() {
        System.out.println("\n--- Gönderi Yönetimi ---");

        // 1. Önce kullanıcının kendi gönderilerini çekelim
        List<Post> myPosts = postService.getPostsForUser(loggedInUser);

        if (myPosts.isEmpty()) {
            System.out.println("Hiç gönderiniz yok.");
            return;
        }

        System.out.println("Gönderileriniz:");
        // Gönderileri numaralandırarak basalım
        for (int i = 0; i < myPosts.size(); i++) {
            Post post = myPosts.get(i);
            System.out.println("\n--- [" + (i + 1) + "] ---"); // Örn: [1], [2]
            System.out.println("İçerik: " + post.getContent());
            System.out.println("Tarih: " + post.getTimestamp());
            // Yorum sayısını da getirelim (bu bir sonraki iyileştirme olabilir)
        }

        // 2. Kullanıcıdan bir gönderi seçmesini isteyelim
        System.out.println("\n--------------------");
        System.out.print("Yorumları görmek veya yorum yapmak için bir gönderi numarası seçin (Çıkmak için 0): ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == 0) {
                return; // Menüden çık
            }

            // Dizi index'i 0'dan başladığı için (choice-1)
            if (choice > 0 && choice <= myPosts.size()) {
                Post selectedPost = myPosts.get(choice - 1);
                // Seçilen gönderi için Yorum Menüsünü aç
                handleCommentMenu(selectedPost);
            } else {
                System.out.println("Geçersiz gönderi numarası.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Lütfen geçerli bir sayı girin.");
        }
    }

    /**
     * Belirli bir gönderi için "Yorumları Gör" ve "Yorum Yap" seçeneklerini sunar.
     * @param post Üzerinde işlem yapılacak Post nesnesi
     */
    private static void handleCommentMenu(Post post) {
        while (true) {
            System.out.println("\n--- Yorum Menüsü (Gönderi: \"" + post.getContent().substring(0, Math.min(post.getContent().length(), 20)) + "...\") ---");
            System.out.println("1. Yorumları Gör");
            System.out.println("2. Yorum Yap");
            System.out.println("0. Geri (Gönderi Yönetimine Dön)");
            System.out.print("Seçiminiz: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleViewComments(post);
                    break;
                case "2":
                    handleCreateComment(post);
                    break;
                case "0":
                    return; // Bu menüden çık, handlePostManagement'e dön
                default:
                    System.out.println("Geçersiz seçim.");
            }
        }
    }

    /**
     * Belirli bir gönderiye yeni bir yorum ekler.
     * @param post Yorum yapılacak Post nesnesi
     */
    private static void handleCreateComment(Post post) {
        System.out.println("\n--- Yorum Yap ---");
        System.out.print("Yorumunuzu yazın: ");
        String content = scanner.nextLine();

        if (content.isEmpty()) {
            System.out.println("Yorum boş olamaz.");
            return;
        }

        // 'CommentService'i kullanarak yorumu oluştur ve kaydet
        commentService.createComment(content, loggedInUser, post);
        System.out.println("Yorumunuz başarıyla eklendi!");
    }

    /**
     * Bir gönderiye ait tüm yorumları veritabanından çeker ve listeler.
     * @param post Yorumları gösterilecek Post nesnesi
     */
    private static void handleViewComments(Post post) {
        System.out.println("\n--- Gönderi Yorumları ---");

        List<Comment> comments = commentService.getCommentsForPost(post.getPostId());

        if (comments.isEmpty()) {
            System.out.println("Bu gönderiye henüz hiç yorum yapılmamış.");
            return;
        }

        for (Comment comment : comments) {
            // Yorum yazarının null olma ihtimaline karşı kontrol
            String authorUsername = (comment.getAuthor() != null)
                    ? comment.getAuthor().getUsername()
                    : "[Silinmiş Kullanıcı]";

            System.out.println("--------------------");
            System.out.println(authorUsername + " (" + comment.getTimestamp() + "):");
            System.out.println("  " + comment.getContent());
        }
    }
}