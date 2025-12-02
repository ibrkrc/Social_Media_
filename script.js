// --- 1. MOCK DATA (Sanki Backend'den gelen veriler) ---
const rawData = [
    {
        id: 1,
        username: "kodinnggg",
        profilePic: "https://randomuser.me/api/portraits/men/32.jpg",
        postImage: "https://images.unsplash.com/photo-1542831371-29b0f74f9713?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80",
        likes: 120,
        description: "Kod yazarken kahve içmeyi unutmayın! ☕ #coding",
        time: "10 DAKİKA ÖNCE"
    },
    {
        id: 2,
        username: "muhendis_bey",
        profilePic: "https://randomuser.me/api/portraits/men/45.jpg",
        postImage: "https://images.unsplash.com/photo-1498050108023-c5249f4df085?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80",
        likes: 85,
        description: "Bugün proje teslim günü... Herkese başarılar! 💻",
        time: "45 DAKİKA ÖNCE"
    },
    {
        id: 3,
        username: "gezen_kadin",
        profilePic: "https://randomuser.me/api/portraits/women/65.jpg",
        postImage: "https://images.unsplash.com/photo-1472214103451-9374bd1c798e?ixlib=rb-4.0.3&auto=format&fit=crop&w=1000&q=80",
        likes: 940,
        description: "Doğa yürüyüşü gibisi yok. 🌲🏕️",
        time: "2 SAAT ÖNCE"
    }
];

// --- 2. CLASS YAPILARI (OOP) ---

// Post Sınıfı: Her bir postun özelliklerini tanımlar
class Post {
    constructor(data) {
        this.id = data.id;
        this.username = data.username;
        this.profilePic = data.profilePic;
        this.postImage = data.postImage;
        this.likes = data.likes;
        this.description = data.description;
        this.time = data.time;
        this.isLiked = false; // Başlangıçta beğenilmemiş
    }

    // Postun HTML çıktısını üreten metot
    getHTML() {
        return `
        <div class="post" id="post-${this.id}">
            <div class="info">
                <div class="user">
                    <img src="${this.profilePic}" class="profile-pic" alt="profil">
                    <p class="username">${this.username}</p>
                </div>
                <i class="fa-solid fa-ellipsis"></i>
            </div>
            
            <img src="${this.postImage}" class="post-image" alt="post">
            
            <div class="post-content">
                <div class="reaction-wrapper">
                    <i class="fa-regular fa-heart" onclick="uiManager.handleLike(${this.id})"></i>
                    <i class="fa-regular fa-comment"></i>
                    <i class="fa-regular fa-paper-plane"></i>
                    <i class="fa-regular fa-bookmark" style="margin-left: auto;"></i>
                </div>
                <p class="likes" id="likes-text-${this.id}">${this.likes} beğenme</p>
                <p class="description">
                    <span>${this.username}</span> ${this.description}
                </p>
                <p class="post-time">${this.time}</p>
            </div>
        </div>
        `;
    }
}

// Arayüz Yöneticisi Sınıfı
class UIManager {
    constructor(containerId) {
        this.container = document.getElementById(containerId);
        this.posts = []; // Post nesnelerini tutacağımız liste
    }

    // Veriyi alır, Post nesnelerine çevirir ve listeye ekler
    loadPosts(dataArray) {
        this.posts = dataArray.map(data => new Post(data));
        this.render();
    }

    // Postları ekrana çizer
    render() {
        this.container.innerHTML = ""; // Önceki içeriği temizle
        
        this.posts.forEach(post => {
            this.container.innerHTML += post.getHTML();
        });
    }

    // Beğeni olayını yöneten metot
    handleLike(postId) {
        // İlgili post nesnesini bul
        const post = this.posts.find(p => p.id === postId);
        
        // HTML elemanlarını seç
        const postElement = document.getElementById(`post-${postId}`);
        const likeIcon = postElement.querySelector('.fa-heart');
        const likesText = document.getElementById(`likes-text-${postId}`);

        if (!post.isLiked) {
            // Beğen
            post.likes++;
            post.isLiked = true;
            likeIcon.classList.remove('fa-regular'); // İçi boş kalbi sil
            likeIcon.classList.add('fa-solid');    // İçi dolu kalp ekle
            likeIcon.style.color = 'red';          // Rengi kırmızı yap
        } else {
            // Beğeniyi Geri Al
            post.likes--;
            post.isLiked = false;
            likeIcon.classList.remove('fa-solid');
            likeIcon.classList.add('fa-regular');
            likeIcon.style.color = 'black';
        }

        // Yazıyı güncelle
        likesText.innerText = `${post.likes} beğenme`;
    }
}

// --- 3. UYGULAMAYI BAŞLAT ---

// Yöneticiden bir örnek (instance) oluştur
const uiManager = new UIManager('posts-container');

// Sayfa yüklendiğinde postları yükle
document.addEventListener('DOMContentLoaded', () => {
    uiManager.loadPosts(rawData);
});

// --- TAKİP ET BUTONU FONKSİYONU ---
function toggleFollow(button) {
    if (button.innerText === "Takip Et") {
        button.innerText = "Takip Ediliyor";
        button.style.color = "#262626"; // Siyah renk
    } else {
        button.innerText = "Takip Et";
        button.style.color = "#0095f6"; // Mavi renk
    }
}