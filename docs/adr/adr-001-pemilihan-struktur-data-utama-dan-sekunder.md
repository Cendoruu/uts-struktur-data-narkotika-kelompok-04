# ADR 001: Pemilihan Struktur Data Utama dan Sekunder

* **Status:** Diterima (*Accepted*)

* **Tanggal:** 2026-11-15

* **Kelompok:** Kelompok 04 

## 1. Konteks dan Permasalahan

Aplikasi pengolahan 50 putusan pengadilan tindak pidana narkotika wajib menyimpan seluruh objek `Putusan` di dalam satu **struktur data utama** yang dibangun dari nol menggunakan parameter tipe generik `<T>`, tanpa memanfaatkan kelas koleksi bawaan `java.util` (`ArrayList`, `LinkedList`, `Stack`, `Queue`, `Vector`, maupun `Deque`). Selain itu, aplikasi memerlukan satu **struktur data sekunder** untuk merekam riwayat operasi pengguna (*audit log*) serta menjaga konsistensi *invariant* selama seluruh siklus hidup program.

Pola akses utama pada aplikasi meliputi:

1. Membaca seluruh data secara berurutan (indeks $0$ hingga $N - 1$) untuk fitur tampil, filter multi-kriteria, dan agregasi statistik.

2. Mengakses dan menghapus elemen pada posisi batas (awal, tengah, dan akhir) untuk pengujian pergeseran data.

3. Menyalin dan mengurutkan tampilan berdasarkan lama vonis tanpa mengubah urutan penyimpanan asli.

4. Merekam aktivitas pengguna secara kronologis dan menampilkannya kembali secara non-destruktif.

## 2. Opsi yang Dipertimbangkan

### A. Kandidat Struktur Data Utama

1. **Array Dinamis (`DynamicArray<T>`)** berbasis array mentah `Object[]` dengan mekanisme penggandaan kapasitas otomatis (`resize`).

2. **Singly Linked List** berbasis node berantai dengan pointer `head` dan `tail`.

3. **Stack / Queue** sebagai penyimpanan arsip utama.

### B. Kandidat Struktur Data Sekunder

1. **Stack (`CustomStack<T>`)** dengan prinsip *Last-In, First-Out* (LIFO) untuk *audit log*.

2. **Queue** dengan prinsip *First-In, First-Out* (FIFO) untuk antrean validasi.

## 3. Keputusan Arsitektur

Kami memutuskan untuk menggunakan:

1. **`DynamicArray<T>`** sebagai **struktur data utama** yang mengimplementasikan kontrak antarmuka generik `StrukturData<T>`.

2. **`CustomStack<T>`** sebagai **struktur data sekunder** untuk pencatatan riwayat audit (*audit trail*).

### Alasan Pemilihan

* **Akses Indeks dalam Waktu Konstan (**$O(1)$**):** `DynamicArray<T>` memungkinkan pengambilan data pada indeks ke-$i$ secara langsung dalam $O(1)$, jauh lebih efisien dibandingkan *Singly Linked List* yang memerlukan penelusuran berantai dari `head` dengan kompleksitas $O(n)$ untuk setiap akses indeks.

* **Kesesuaian dengan Pengurutan dan Pengujian Batas:** Proses penyalinan referensi objek ke larik sementara untuk pengurutan serta pengujian penghapusan posisi awal ($0$), tengah ($\lfloor n/2 \rfloor$), dan akhir ($n - 1$) dapat dieksekusi langsung menggunakan indeks tanpa manipulasi pointer yang rawan memutus rantai referensi.

* **Kesesuaian LIFO untuk Audit Log:** Pada fitur audit operasi pengguna, aktivitas yang paling baru dieksekusi merupakan informasi pertama yang ingin diperiksa. `CustomStack<T>` menyediakan operasi `push()` dalam waktu $O(1)$ dan meletakkan riwayat terbaru tepat di posisi `top`.

## 4. Pemetaan Operasi ke Kontrak `StrukturData<T>` dan *Invariant*

### A. Pemetaan `DynamicArray<T>`

Kelas `DynamicArray<T>` mengimplementasikan seluruh kontrak `StrukturData<T>` secara langsung tanpa penyimpangan tanda tangan *method*:

| Kontrak `StrukturData<T>` | Implementasi di `DynamicArray<T>` | Kompleksitas Waktu | 
| ----- | ----- | ----- | 
| `void add(T value)` | Menambahkan elemen di indeks `size`, memicu `resize()` jika penuh | $O(1)$ *amortized* | 
| `T get(int position)` | Mengambil elemen pada `elements[position]` setelah validasi batas | $O(1)$ | 
| `T remove(int position)` | Menghapus elemen dan menggeser elemen di kanannya ke kiri | $O(n)$ | 
| `int size()` | Mengembalikan jumlah elemen aktif | $O(1)$ | 
| `boolean isEmpty()` | Memeriksa apakah $\text{size} == 0$ | $O(1)$ | 
| `boolean contains(T value)` | Pencarian sekuensial menggunakan `Objects.equals` | $O(n)$ | 
| `void clear()` | Mengatur seluruh elemen aktif menjadi `null` dan $\text{size} = 0$ | $O(n)$ | 

*Method tambahan khusus Array Dinamis:* `insert(int index, T value)`, `set(int position, T value)`, dan `resize(int newCapacity)`.

### B. Pemetaan dan Penyimpangan pada `CustomStack<T>`

Sesuai Panduan UTS Bagian 3.2, `CustomStack<T>` tidak mengimplementasikan `StrukturData<T>` secara langsung karena kontrak `get(int position)` dan `remove(int position)` berbasis indeks acak melanggar disiplin akses LIFO pada tumpukan. Pemetaan operasinya adalah sebagai berikut:

* `add(T value)` dipetakan menjadi `push(T value)` untuk menambah elemen di puncak (`top`).

* `remove(int position)` dipetakan menjadi `pop()` yang hanya menghapus elemen pada indeks `top`.

* `get(int position)` dipetakan menjadi `peek()` untuk melihat elemen puncak, serta `getElementsPreserved()` untuk membaca seluruh riwayat dari `top` ke indeks $0$ secara non-destruktif tanpa mengubah pointer `top`.

### C. Penjagaan *Invariant* (`validateInvariant()`)

* **`DynamicArray<T>`:** Array internal tidak bernilai `null`, berlaku pertidaksamaan $0 \le \text{size} \le \text{capacity}$, serta seluruh indeks aktif $0 \le i < \text{size}$ dipastikan tidak menyimpan referensi `null`.

* **`CustomStack<T>`:** Indeks puncak memenuhi $-1 \le \text{top} < \text{capacity}$, dan seluruh elemen pada rentang $0 \le i \le \text{top}$ tidak bernilai `null`.

## 5. Konsekuensi

### Keuntungan (+)

* Iterasi pembacaan 50 record untuk filter, agregasi, dan ekspor berjalan sangat cepat karena elemen tersimpan berdampingan di memori (*cache-friendly*).

* Kapasitas larik bertambah secara otomatis apabila pengguna menambahkan putusan baru melebihi 50 record awal.

* Inspeksi riwayat audit pada `CustomStack<T>` dijamin tidak merusak urutan kronologis operasi.

### Keterbatasan (-)

* Operasi penyisipan (`insert`) dan penghapusan (`remove`) di awal atau tengah `DynamicArray<T>` memerlukan pergeseran elemen sebesar $O(n)$. Namun, untuk skala dataset $N = 50$ hingga beberapa ratus record, biaya pergeseran ini sangat kecil dan dapat diabaikan.