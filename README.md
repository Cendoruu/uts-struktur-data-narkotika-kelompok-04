# Aplikasi Pengolahan Data Putusan Pengadilan Tindak Pidana Narkotika

Aplikasi berbasis konsol (*Command-Line Interface* / CLI) yang dibangun menggunakan bahasa pemrograman Java berorientasi objek untuk menyimpan, menelusuri, menyaring, mengurutkan, mengaudit, dan mengekspor 50 data putusan pengadilan tindak pidana narkotika dari Direktori Putusan Mahkamah Agung Republik Indonesia.



---

## 1. Identitas Pengembang & Informasi Repositori

* **Nama Lengkap:** Firjatullah Mahrus Triadi
* **NIM:** `202510370110107`
* **Kelas / Kelompok:** Kelas B / Kelompok 04
* **Akun GitHub:** [`@Cendoruu`](https://github.com/Cendoruu)
* **URL Repositori:** <https://github.com/Cendoruu/uts-struktur-data-narkotika-kelompok-04>
* **Tag Rilis Final:** `uts-v1.0`

---

## 2. Pilihan Struktur Data & Arsitektur Sistem

Aplikasi menerapkan pola arsitektur **MVC (*Model-View-Controller*)** yang dipadukan dengan paket khusus struktur data (`StrukDat`):

1. **Struktur Data Utama — `DynamicArray<T>` (`StrukDat/DynamicArray.java`):**
    * Mengimplementasikan antarmuka generik `StrukturData<T>` berbasis array mentah `Object[]` dengan mekanisme penggandaan kapasitas otomatis (`resize`).
    * Menyimpan seluruh **50 objek `Putusan`** secara berurutan sehingga mendukung pengaksesan indeks langsung dalam waktu konstan $O(1)$ serta pengujian pergeseran elemen pada batas awal, tengah, dan akhir.
    * Dilengkapi method `validateInvariant()` untuk menjamin array internal tidak `null`, ukuran memenuhi $0 \le \text{size} \le \text{capacity}$, dan tidak terdapat referensi `null` di rentang indeks aktif.

2. **Struktur Data Sekunder — `CustomStack<T>` (`StrukDat/CustomStack.java`):**
    * Menerapkan disiplin *Last-In, First-Out* (LIFO) untuk merekam riwayat aktivitas pengguna (*audit log* bertipe `AuditEntry`).
    * Menyediakan method inspeksi non-destruktif (`getElementsPreserved()`) yang memungkinkan seluruh riwayat audit dibaca dari puncak (`top`) ke dasar tanpa menghapus elemen maupun mengubah urutan tumpukan.
    * Dilengkapi `validateInvariant()` untuk memastikan indeks puncak memenuhi $-1 \le \text{top} < \text{capacity}$.

> Penjelasan rinci mengenai alasan pemilihan struktur data serta pemetaan operasinya terhadap kontrak `StrukturData<T>` terdokumentasi pada [`docs/adr/001-pemilihan-struktur-data.md`](docs/adr/001-pemilihan-struktur-data.md) dan [`docs/adr/002-strategi-pencarian-dan-pengurutan.md`](docs/adr/002-strategi-pencarian-dan-pengurutan.md).

---

## 3. Struktur Direktori Repositori

```text
uts-struktur-data-narkotika-kelompok-04/
├── src/
│   ├── main/java/
│   │   ├── Model/
│   │   │   └── Putusan.java                   # Model data 16 atribut putusan narkotika
│   │   ├── StrukDat/
│   │   │   ├── StrukturData.java              # Kontrak antarmuka generik struktur data
│   │   │   ├── DynamicArray.java              # Struktur data utama (Array Dinamis)
│   │   │   └── CustomStack.java               # Struktur data sekunder (Stack LIFO Audit)
│   │   ├── Controller/
│   │   │   ├── CSVImporter.java               # Parser & validator CSV (menolak baris invalid)
│   │   │   ├── PutusanController.java         # Logika CRUD, search, filter, sort, agregasi, audit
│   │   │   ├── ProvenanceController.java      # Pencocokan silang metadata provenance per atribut
│   │   │   └── Exporter.java                  # Ekspor hasil pencarian/laporan ke CSV
│   │   └── View/
│   │       ├── Main.java                      # Antarmuka CLI utama (Menu 1–9)
│   │       └── GenerateProvenance.java        # Utilitas pembangkit templat provenance
│   └── test/java/
│       ├── StrukDat/
│       │   ├── DynamicArrayTest.java          # Unit test DynamicArray, boundary & invariant
│       │   └── CustomStackTest.java           # Unit test CustomStack & inspeksi non-destruktif
│       └── Controller/
│           └── PutusanAppIntegrationTest.java # Integration test 7 skenario wajib UTS
├── data/
│   ├── putusan-50.csv                         # 50 dataset putusan narkotika teranonimisasi
│   └── ekspor-putusan.csv                     # Berkas keluaran hasil ekspor aplikasi
├── docs/
│   ├── data-provenance.csv                    # Metadata nomor halaman sumber per atribut
│   ├── adr/
│   │   ├── 001-pemilihan-struktur-data.md
│   │   └── 002-strategi-pencarian-dan-pengurutan.md
│   ├── trace/                                 # Bukti log trace eksekusi manual
│   └── demo/                                  # Bukti demonstrasi pengujian
├── AI_USAGE.md                                # Log audit transparansi penggunaan AI
├── CONTRIBUTIONS.md                           # Catatan kontribusi & jejak proses GitHub
├── README.md                                  # Dokumentasi utama proyek
└── pom.xml                                    # Konfigurasi Maven & JUnit Jupiter 5.10.2
```

---

## 4. Skema Dataset Putusan (`data/putusan-50.csv`)

Setiap baris record di dalam `data/putusan-50.csv` merepresentasikan satu objek `Putusan` dengan **16 atribut wajib** yang telah melalui anonimisasi identitas terdakwa dan verifikasi silang terhadap dokumen sumber pada `docs/data-provenance.csv`:

| No | Nama Atribut | Tipe Data | Keterangan |
| :-: | :--- | :--- | :--- |
| 1 | `caseId` | `String` | Kode unik internal (`P001` s.d. `P050`) |
| 2 | `nomorPerkara` | `String` | Nomor register perkara pengadilan |
| 3 | `pengadilan` | `String` | Nama Pengadilan Negeri pemutus perkara |
| 4 | `tanggalPutusan` | `LocalDate` | Tanggal putusan dibacakan (`YYYY-MM-DD`) |
| 5 | `terdakwaKode` | `String` | Kode anonim terdakwa (`TDW-001` s.d. `TDW-050`) |
| 6 | `jenisNarkotika` | `String` | Jenis zat narkotika (misal: `Sabu`, `Ganja`, `Ekstasi`) |
| 7 | `beratBarangBuktiGram` | `double` | Berat bersih (netto) barang bukti dalam satuan gram |
| 8 | `pasalDakwaan` | `String` | Pasal UU Narkotika yang didakwakan penuntut umum |
| 9 | `pasalTerbukti` | `String` | Pasal yang dinyatakan terbukti secara sah oleh hakim |
| 10 | `tuntutanBulan` | `int` | Lama tuntutan pidana penjara JPU dalam bulan |
| 11 | `vonisBulan` | `int` | Lama putusan pidana penjara hakim dalam bulan |
| 12 | `dendaRupiah` | `long` | Besaran pidana denda dalam satuan Rupiah |
| 13 | `barangBukti` | `String` | Ringkasan rincian barang bukti utama |
| 14 | `keadaanMemberatkan` | `String` | Ringkasan pertimbangan hakim yang memberatkan |
| 15 | `keadaanMeringankan` | `String` | Ringkasan pertimbangan hakim yang meringankan |
| 16 | `sourcePages` | `String` | Pemetaan nomor halaman PDF untuk tiap kelompok atribut |

---

## 5. Analisis Kompleksitas Operasi & Algoritma

| Komponen / Fitur | Algoritma / Mekanisme | *Best Case* | *Average Case* | *Worst Case* | Ruang Tambahan |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **`DynamicArray.add(T)`** | Penambahan di akhir + *auto-resize* $2\times$ | $O(1)$ | $O(1)$ | $O(n)$ | $O(n)$ saat *resize* |
| **`DynamicArray.get(int)`** | Pengaksesan langsung indeks array | $O(1)$ | $O(1)$ | $O(1)$ | $O(1)$ |
| **`DynamicArray.insert(int, T)`** | Penyisipan + pergeseran elemen ke kanan | $O(1)$ | $O(n)$ | $O(n)$ | $O(1)$ |
| **`DynamicArray.remove(int)`** | Penghapusan + pergeseran elemen ke kiri | $O(1)$ | $O(n)$ | $O(n)$ | $O(1)$ |
| **`CustomStack.push` / `pop`** | Operasi tumpukan pada indeks `top` | $O(1)$ | $O(1)$ | $O(1)$ | $O(1)$ |
| **`getElementsPreserved()`** | Salin isi tumpukan dari `top` ke `0` | $O(n)$ | $O(n)$ | $O(n)$ | $O(n)$ |
| **Pencarian (`search`)** | *Linear Search* dengan pencacah langkah (`steps`) | $O(1)$ | $O(n)$ | $O(n)$ | $O(1)$ |
| **Filter Multi-Kriteria** | Pemindaian sekuensial 4 parameter sekaligus | $O(n)$ | $O(n)$ | $O(n)$ | $O(k)$ hasil filter |
| **Pengurutan Vonis** | *Copy-on-Sort* menggunakan *Insertion Sort* | $O(n)$ | $O(n^2)$ | $O(n^2)$ | $O(n)$ larik salinan |

---

## 6. Prasyarat, Cara Build, Cara Test, dan Cara Menjalankan

### A. Prasyarat Lingkungan
* **Java Development Kit (JDK):** Versi **17** atau lebih baru.
* **Apache Maven:** Versi **3.8+** (atau gunakan *Maven Wrapper* `.\mvnw.cmd` / `./mvnw` bawaan proyek, maupun panel Maven pada IntelliJ IDEA).

### B. Cara Kompilasi (*Build*)
Buka terminal pada direktori utama (*root*) proyek, lalu jalankan:
```bash
# Menggunakan Maven Wrapper di Windows (PowerShell / CMD)
.\mvnw.cmd clean compile

# Menggunakan Maven Wrapper di Linux / macOS
./mvnw clean compile

# Atau jika Maven sudah terdaftar di PATH sistem
mvn clean compile
```

### C. Cara Menjalankan Pengujian Otomatis (*Test*)
Pengujian menggunakan **JUnit Jupiter 5.10.2** yang mencakup pengujian unit pada `DynamicArrayTest` dan `CustomStackTest`, serta pengujian integrasi 7 skenario wajib UTS pada `PutusanAppIntegrationTest`:
```bash
# Windows (PowerShell / CMD)
.\mvnw.cmd test

# Linux / macOS
./mvnw test

# Atau melalui IntelliJ IDEA:
# Klik kanan pada folder src/test/java -> pilih "Run 'All Tests'"
```

### D. Cara Menjalankan Aplikasi Utama
Jalankan kelas utama `View.Main` untuk membuka antarmuka menu interaktif berbasis konsol:
```bash
# Windows (PowerShell / CMD)
.\mvnw.cmd exec:java -Dexec.mainClass="View.Main"

# Linux / macOS
./mvnw exec:java -Dexec.mainClass="View.Main"

# Atau melalui IntelliJ IDEA:
# Buka src/main/java/View/Main.java -> klik tombol Run (Shift + F10)
```

---

## 7. Contoh Perintah & Skenario Pengujian CLI

Saat `View.Main` dijalankan, program otomatis memuat 50 record dari `data/putusan-50.csv`, memverifikasi `validateInvariant()`, dan menampilkan **9 menu utama**:

```text
[INFO IMPOR SELESAI] Berhasil memuat: 50 record, Ditolak: 0 record.
=== SISTEM MANAJEMEN PUTUSAN NARKOTIKA (KELOMPOK 04) ===
1. Tampilkan Semua Putusan (Urutan Penyimpanan Utama)
2. Tambah Putusan Baru
3. Hapus Putusan Berdasarkan Indeks
4. Cari Putusan (Case ID / Nomor Perkara + Step Count)
5. Filter Putusan Multi-Kriteria & Cek Provenance
6. Statistik & Agregasi Putusan
7. Urutkan Putusan Berdasarkan Vonis (Ascending - Non-Destruktif)
8. Tampilkan Riwayat Audit Log (Stack LIFO) & Ekspor CSV
9. Jalankan Simulasi Otomatis 7 Skenario Wajib UTS
0. Keluar
Pilih menu:
```

### Contoh Penggunaan Fitur Utama
* **Mencari Putusan (Menu `4`):**
  Masukkan `P001` (batas awal) atau `P050` (batas akhir) untuk melihat detail putusan beserta jumlah langkah perbandingan (`Langkah pencarian: 1` untuk `P001`, dan `Langkah pencarian: 50` untuk `P050`).
* **Menjalankan Seluruh 7 Skenario Wajib UTS Sekaligus (Menu `9`):**
  Memilih angka `9` akan mengeksekusi simulasi berurutan untuk:
    1. Verifikasi pemuatan tepat **50 record** dan status `validateInvariant() == true`.
    2. Pencarian data ada, tidak ada, dan posisi batas.
    3. Penghapusan elemen pertama (`indeks 0`), tengah (`indeks 24`), dan terakhir (`indeks 48`) beserta log pergeseran elemen.
    4. Penambahan kembali ketiga record tersebut dan verifikasi pemulihan urutan serta *invariant*.
    5. Filter gabungan (jenis narkotika, rentang berat barang bukti, pasal terbukti, dan rentang vonis) yang dicocokkan dengan `docs/data-provenance.csv`.
    6. Inspeksi non-destruktif pada `CustomStack` untuk membuktikan urutan audit log tidak berubah setelah dibaca.
    7. Pengujian penolakan baris CSV tidak valid tanpa menghentikan program maupun merusak *invariant* penyimpanan utama.

---

## 8. Dokumen Terkait
* **Catatan Kontribusi & Jejak GitHub:** [`CONTRIBUTIONS.md`](CONTRIBUTIONS.md)
* **Log Transparansi Penggunaan AI:** [`AI_USAGE.md`](AI_USAGE.md)
* **Metadata Sumber & Halaman PDF:** [`docs/data-provenance.csv`](docs/data-provenance.csv)
* **Architecture Decision Records (ADR):** [`docs/adr/001-pemilihan-struktur-data.md`](docs/adr/001-pemilihan-struktur-data.md) & [`docs/adr/002-strategi-pencarian-dan-pengurutan.md`](docs/adr/002-strategi-pencarian-dan-pengurutan.md)