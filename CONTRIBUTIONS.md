# Catatan Kontribusi & Jejak Proses GitHub (`CONTRIBUTIONS.md`)

## 1. Identitas Pengembang & Informasi Repositori

* **Nama Lengkap:** `[Firjatullah Mahrus Triadi]`

* **NIM:** `[202510370110107]`

* **Akun GitHub:** [`@Cendoruu`](https://github.com/Cendoruu)

* **Kelas / Kelompok:** `[B]` / Kelompok 04

* **URL Repositori:** <https://github.com/Cendoruu/uts-struktur-data-narkotika-kelompok-04>

* **Daftar Issues:** <https://github.com/Cendoruu/uts-struktur-data-narkotika-kelompok-04/issues>

* **Daftar Pull Requests:** <https://github.com/Cendoruu/uts-struktur-data-narkotika-kelompok-04/pulls>

* **Riwayat Commit (`master`):** <https://github.com/Cendoruu/uts-struktur-data-narkotika-kelompok-04/commits/main>

* **Tag Rilis Final:** [`uts-v1.0`](https://github.com/Cendoruu/uts-struktur-data-narkotika-kelompok-04/releases/tag/uts-v1.0)

> **Klarifikasi Pengerjaan:**
>
> Sesuai ketentuan akademik, tidak ada pembuatan akun tambahan maupun rekayasa *commit* atas nama pihak lain. Disiplin alur kerja GitHub tetap diterapkan secara konsisten melalui pemisahan GitHub Issues, pengembangan pada branch fitur terpisah, penggabungan ke branch `master` melalui Pull Request (PR) yang terpisah berdasarkan ruang lingkup pekerjaan, disertai pemeriksaan mandiri (*self-review* komentar teknis) sebelum setiap PR di-*merge*.

## 2. Ringkasan Bukti Kontribusi per Area (Bagian 5.3 Panduan UTS)

| Area Kontribusi | Penanggung Jawab | Artefak & Berkas Terkait di Repositori | Bukti yang Dapat Diaudit | 
| ----- | ----- | ----- | ----- | 
| **1. Ekstraksi Dataset & Provenance** | `@Cendoruu` | • `data/putusan-50.csv`  • `docs/data-provenance.csv`  • `src/main/java/View/View.GenerateProvenance.java` | 50 putusan pengadilan narkotika (`P001`–`P050`) diekstrak secara manual dari direktori Mahkamah Agung RI dengan anonimisasi identitas terdakwa serta pemetaan nomor halaman sumber per atribut (`extractor` & `reviewer`: `Cendoruu` / `[Nama Anda]`). | 
| **2. Implementasi Struktur Data & Logika Aplikasi** | `@Cendoruu` | • `src/main/java/StrukDat/StrukturData.java`  • `src/main/java/StrukDat/DynamicArray.java`  • `src/main/java/StrukDat/CustomStack.java`  • `src/main/java/Model/Putusan.java`  • `src/main/java/Controller/CSVImporter.java`  • `src/main/java/Controller/PutusanController.java`  • `src/main/java/Controller/ProvenanceController.java`  • `src/main/java/Controller/Exporter.java`  • `src/main/java/View/Main.java` | Implementasi struktur data utama (`DynamicArray<T>`) dan sekunder (`CustomStack<T>`) dari nol tanpa koleksi `java.util`, validasi *invariant* (`validateInvariant()`), *linear search* dengan *step counter*, filter multi-kriteria, *copy-on-sort* (*Insertion Sort*), dan audit log LIFO non-destruktif. | 
| **3. Pengujian Otomatis & Penemuan Bug** | `@Cendoruu` | • `src/test/java/StrukDat/DynamicArrayTest.java`  • `src/test/java/StrukDat/CustomStackTest.java`  • `src/test/java/Controller/PutusanAppIntegrationTest.java`  • `pom.xml`  • `docs/trace/` & `docs/demo/` | Pengujian unit dan integrasi berbasis JUnit 5 untuk seluruh operasi batas (*boundary*), *invariant*, dan 7 skenario wajib UTS, serta pencatatan perbaikan *bug* teknis selama pengembangan. | 
| **4. Dokumentasi Arsitektur & Transparansi AI** | `@Cendoruu` | • `README.md`  • `docs/adr/001-pemilihan-struktur-data.md`  • `docs/adr/002-strategi-pencarian-dan-pengurutan.md`  • `AI_USAGE.md`  • `CONTRIBUTIONS.md` | Dokumentasi lengkap keputusan arsitektur (ADR 001 & 002), panduan kompilasi/pengujian Maven, serta log audit penggunaan AI generatif. | 

## 3. Tautan Issues, Pull Requests, Review, dan Commit Utama (Bagian 8 Panduan UTS)

Seluruh perubahan menuju branch `main` dilakukan melalui alur *Issue* $\rightarrow$ *Branch* $\rightarrow$ *Pull Request* $\rightarrow$ *Technical Self-Review* $\rightarrow$ *Merge*:

| No | Ruang Lingkup Pekerjaan | GitHub Issue | Branch & Pull Request | Ringkasan Review & Commit Utama | 
| ----- | ----- | ----- | ----- | ----- | 
| **1** | **Develop** | [Issue #1](https://github.com/Cendoruu/uts-struktur-data-narkotika-kelompok-04/issues/1)  *(Memasukkan aplikasi )* | `Develop`  [PR #1](https://github.com/Cendoruu/uts-struktur-data-narkotika-kelompok-04/pull/1) | •  Mengimplementasikan aplikasi yang di-Build di Develop ke Branch utama | 
| **2** | **Dokumentasi** | [Issue #2](https://github.com/Cendoruu/uts-struktur-data-narkotika-kelompok-04/issues/2)  *(Melengkapi dokumentasi aplikasi)* | `Develop`  [PR #2](https://github.com/Cendoruu/uts-struktur-data-narkotika-kelompok-04/pull/2) | • Melengkapi dokumentasi yang diperlukan untuk transparansi aplikasi | 
|  |  |

## 4. Log Bug yang Ditemukan & Diperbaiki Selama Pengujian

1. **Bug Format Output Pencetakan Impor CSV (`CSVImporter.java`):**

    * *Gejala:* Pesan `"[INFO IMPOR SELESAI] Berhasil memuat: %d record, Ditolak: %d record.\n", successCount, failCount` tercetak mentah sebagai literal teks di konsol saat menjalankan `Main.java`.

    * *Akar Masalah:* Pemanggilan `System.out.println(...)` membungkus seluruh argumen format ke dalam satu `String`.

    * *Perbaikan:* Mengubah pemanggilan menjadi `System.out.printf(...)` sehingga jumlah record valid (`50`) dan ditolak (`0`) tercetak sesuai nilai variabel aktual.

2. **Ketidaksesuaian Tipe Generik pada Struktur Audit Log (`PutusanController.java`):**

    * *Gejala:* Error kompilasi `Required type: DynamicArray<AuditEntry>, Provided: DynamicArray<Putusan>` saat membaca isi riwayat audit dari `CustomStack`.

    * *Perbaikan:* Menyelaraskan deklarasi tipe generik penyimpanan log menjadi `CustomStack<AuditEntry>` sehingga setiap aktivitas CRUD, pencarian, filter, dan ekspor tersimpan bersama *timestamp* dan detail operasi.

3. **Sinkronisasi Dependensi JUnit 5 & Struktur Direktori Test (`pom.xml` & `src/test/java/`):**

    * *Gejala:* Anotasi `@Test` dan `@BeforeEach` tidak dikenali saat menjalankan pengujian dan kelas `PutusanAppIntegrationTest` sempat berada di luar *Test Sources Root*.

    * *Perbaikan:* Menggunakan artefak `org.junit.jupiter:junit-jupiter:5.10.2` pada `pom.xml`, melakukan *Reload Maven Project*, serta menempatkan `PutusanAppIntegrationTest.java` ke dalam package `src/test/java/Controller/` agar sejajar dengan `src/main/java/Controller/`.

## 5. Daftar Periksa Kesiapan Rilis (`uts-v1.0`)

* \[x\] Seluruh kode pada `src/main/java` dan `src/test/java` dapat dikompilasi dan lulus seluruh tes (`mvn test` / JUnit 5 Runner).

* \[x\] Tidak ada penggunaan koleksi `java.util` (`ArrayList`, `LinkedList`, `Stack`, `Queue`, `Vector`, `Deque`) sebagai penyimpanan inti.

* \[x\] Dataset `data/putusan-50.csv` memuat tepat 50 record valid dan `docs/data-provenance.csv` lengkap terverifikasi.

* \[x\] Dokumentasi `README.md`, `docs/adr/` (2 berkas ADR), `AI_USAGE.md`, dan `CONTRIBUTIONS.md` lengkap dan konsisten.

* \[x\] Tag rilis final **`uts-v1.0`** dibuat pada *commit* terakhir branch `main` yang didemonstrasikan.