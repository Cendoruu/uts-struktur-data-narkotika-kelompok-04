# ADR 002: Strategi Pencarian dan Pengurutan Data Putusan

* **Status:** Diterima (*Accepted*)

* **Tanggal:** 2026-11-15

* **Kelompok:** Kelompok 04 

## 1. Konteks dan Permasalahan

Aplikasi pengolahan 50 putusan pengadilan narkotika membutuhkan dua operasi pengolahan utama pada struktur data penyimpanan `DynamicArray<Putusan>`:

1. **Pencarian (*Search*):** Mencari satu record putusan berdasarkan `caseId` (contoh: `P001`) atau `nomorPerkara` (contoh: `506/Pid.Sus/2023/PN Mdn`), serta melaporkan jumlah langkah perbandingan (*step count*) yang dilakukan hingga data ditemukan atau dinyatakan tidak ada.

2. **Pengurutan (*Sorting*):** Menghasilkan tampilan daftar putusan yang terurut berdasarkan lama pidana penjara (`vonisBulan`) dari yang terendah hingga tertinggi (*ascending*), **tanpa merusak *invariant* maupun urutan penyimpanan asli** di dalam struktur data utama.

## 2. Opsi yang Dipertimbangkan

### A. Strategi Pencarian

1. **Pencarian Sekuensial (*Linear Search*):** Menelusuri elemen pada `DynamicArray<Putusan>` secara berurutan dari indeks $0$ hingga $\text{size} - 1$.

2. **Pencarian Biner (*Binary Search*):** Membagi ruang pencarian menjadi dua bagian secara berulang, tetapi mensyaratkan data utama dalam keadaan terurut berdasarkan kunci pencarian (`caseId` atau `nomorPerkara`).

### B. Strategi Pengurutan dan Manajemen Memori

1. **Pengurutan di Tempat (*In-Place Sorting*) langsung pada `mainStorage`:** Mengubah langsung posisi elemen di dalam penyimpanan utama.

2. **Penyalinan Sebelum Pengurutan (*Copy-on-Sort*) dengan *Insertion Sort*:** Menyalin referensi objek `Putusan` ke dalam instansi `DynamicArray<Putusan>` baru terlebih dahulu, lalu mengurutkan larik salinan tersebut menggunakan algoritma *Insertion Sort*.

## 3. Keputusan Arsitektur

Kami memutuskan untuk menerapkan:

1. **Pencarian Sekuensial (*Linear Search*)** dengan pencacah langkah eksplisit (`steps++`) pada method `PutusanService.search(String keyword)`.

2. **Strategi *Copy-on-Sort* menggunakan algoritma *Insertion Sort*** pada method `PutusanService.getSortedByVonisAscending()`.

### Alasan Pemilihan

* **Mendukung Dua Kunci Pencarian Sekaligus:** Pengguna dapat mencari berdasarkan `caseId` maupun `nomorPerkara`. Dengan *Linear Search*, satu kali penelusuran dapat memeriksa kedua atribut tersebut sekaligus tanpa perlu menyimpan dua struktur indeks terpisah.

* **Menjaga Urutan Asli Penyimpanan Utama:** Data di dalam `mainStorage` mempertahankan urutan kronologis pemuatan awal dari CSV (`P001` hingga `P050`), yang dibutuhkan untuk pengujian posisi batas (awal, tengah, dan akhir) pada Skenario 2 dan Skenario 3. Karena itu, pengurutan dilakukan pada `DynamicArray` salinan sehingga `mainStorage` tetap utuh.

* **Efisiensi *Insertion Sort* untuk Skala** $N = 50$**:** Untuk ukuran data $N = 50$, *Insertion Sort* tidak memerlukan rekursi bertingkat seperti *Merge Sort* atau *Quick Sort*, mudah diimplementasikan langsung menggunakan operasi `get` dan `set` milik `DynamicArray<T>`, serta bersifat stabil (*stable sort*) dalam mempertahankan urutan relatif record dengan lama vonis yang sama.

## 4. Analisis Kompleksitas Algoritma

| Operasi | Kasus Terbaik (*Best Case*) | Kasus Rata-Rata (*Average Case*) | Kasus Terburuk (*Worst Case*) | Ruang Tambahan (*Auxiliary Space*) | 
| ----- | ----- | ----- | ----- | ----- | 
| **Pencarian (`search`)** | $O(1)$ (posisi `P001` / indeks $0$) | $O(n)$ ($\approx n/2$ langkah) | $O(n)$ ($50$ langkah untuk `P050` atau data tidak ada) | $O(1)$ | 
| **Pengurutan (`getSortedByVonisAscending`)** | $O(n)$ (data sudah terurut) | $O(n^2)$ | $O(n^2)$ | $O(n)$ (untuk `DynamicArray` salinan) | 

## 5. Konsekuensi

### Keuntungan (+)

* Jumlah langkah pencarian (`steps`) terukur secara deterministik dan mudah diverifikasi secara manual (indeks $0$ membutuhkan $1$ langkah, indeks $49$ membutuhkan $50$ langkah).

* Tampilan terurut berdasarkan `vonisBulan` dapat ditampilkan atau diekspor kapan saja tanpa mengacak posisi elemen pada `mainStorage` maupun merusak *invariant* struktur data utama.

* Seluruh proses penyalinan dan pengurutan tetap menggunakan `DynamicArray<Putusan>` buatan sendiri tanpa koleksi bawaan `java.util`.

### Keterbatasan (-)

* Pencarian pada dataset berskala sangat besar (puluhan ribu record) akan melambat secara linier ($O(n)$), dan pengurutan memiliki kompleksitas kuadratik ($O(n^2)$). Namun, untuk batas operasional tugas ini ($50$ putusan), eksekusi berlangsung dalam hitungan mikrodetik.