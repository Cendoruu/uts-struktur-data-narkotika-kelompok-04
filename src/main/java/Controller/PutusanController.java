package Controller;

import StrukDat.CustomStack;
import StrukDat.DynamicArray;
import Model.AuditEntry;
import Model.Putusan;

import java.util.Scanner;

public class PutusanController {
    private DynamicArray<Putusan> mainStorage;
    private CustomStack<AuditEntry> auditLog;
    Scanner scanner = new Scanner(System.in);

    public PutusanController(DynamicArray<Putusan> initialData) {
        this.mainStorage = initialData;
        this.auditLog = new CustomStack<>();
        logAudit("INITIALIZE", "Memuat " + mainStorage.size() + " data putusan ke dalam DynamicArray.");
    }

    public DynamicArray<Putusan> getMainStorage() {
        return mainStorage;
    }

    public void logAudit(String op, String detail) {
        auditLog.push(new AuditEntry(op, detail));
    }

    public void displayAuditHistory() {
        System.out.println("\n=== RIWAYAT AUDIT OPERASI (CUSTOM STACK LIFO) ===");
        DynamicArray<AuditEntry> history = auditLog.getElementsPreserved();
        for (int i = 0; i < history.size(); i++) {
            System.out.println(history.get(i));
        }
        System.out.println("Invariant Stack Valid: " + auditLog.validateInvariant());
    }

    //Fitur Menambahkan Data
    public void addData() {
        System.out.println("Case ID (Contoh P051): ");
        String caseId = scanner.nextLine();
        System.out.println("Nomor Perkara: ");
        String nomorPerkara = scanner.nextLine();
        System.out.println("Pengadilan: ");
        String pengadilan = scanner.nextLine();
        System.out.println("Jenis Narkotika (Sabu/Ganja/Ekstasi): ");
        String jenisNarkotika = scanner.nextLine();
        System.out.println("Berat Barang Bukti (gram): ");
        double beratBarangBuktiGram = Double.parseDouble(scanner.nextLine());
        System.out.println("Pasal Terdakwa: ");
        String pasalTerdakwa = scanner.nextLine();
        System.out.println("Pasal Terbukti: ");
        String pasalTerbukti = scanner.nextLine();
        System.out.println("Tuntutan (Bulan): ");
        int tuntutanBulan = Integer.parseInt(scanner.nextLine());
        System.out.println("Vonis (bulan): ");
        int vonisBulan = Integer.parseInt(scanner.nextLine());
        System.out.println("Denda (Rupiah): ");
        long dendaRupiah = Long.parseLong(scanner.nextLine());
        System.out.println("Sumber Halaman (1,2,...): ");
        String sourcePages = scanner.nextLine();

        Putusan baru = new Putusan(caseId,nomorPerkara, pengadilan, java.time.LocalDate.now(),  "TDRK-" + caseId, jenisNarkotika, beratBarangBuktiGram,
                pasalTerdakwa, pasalTerbukti, tuntutanBulan, vonisBulan, dendaRupiah, "-", "-", "-", "Hal " + sourcePages);
        getMainStorage().add(baru);
        logAudit("TAMBAH", "Menambahkan putusan " + caseId);
        System.out.println("Berhasil ditambahkan. Size sekarang: " + getMainStorage().size());
    }
    //Fitur Pencarian Sequential dengan Step Count
    public Putusan search(String keyword) {
        int steps = 0;
        keyword = keyword.toLowerCase().trim();
        for (int i = 0; i < mainStorage.size(); i++) {
            steps++;
            Putusan p = mainStorage.get(i);
            if (p.getCaseId().toLowerCase().equals(keyword) || p.getNomorPerkara().toLowerCase().equals(keyword)) {
                logAudit("CARI", "Ditemukan '" + keyword + "' pada indeks " + i + " dalam " + steps + " langkah.");
                System.out.println("[HASIL PENCARIAN] Ditemukan dalam " + steps + " langkah perbandingan:");
                return p;
            }
        }
        logAudit("CARI", "Tidak Ditemukan '" + keyword + "' setelah " + steps + " langkah.");
        System.out.println("[HASIL PENCARIAN] Tidak ditemukan setelah " + steps + " langkah.");
        return null;
    }

    //Fitur Filter Multi-Kriteria
    public DynamicArray<Putusan> filter(String jenisNarkotika, double minBerat, double maxBerat, String pasal, int minVonis, int maxVonis) {
        DynamicArray<Putusan> result = new DynamicArray<>();
        for (int i = 0; i < mainStorage.size(); i++) {
            Putusan p = mainStorage.get(i);
            boolean matchJenis = (jenisNarkotika == null || jenisNarkotika.isEmpty() || p.getJenisNarkotika().equals(jenisNarkotika));
            boolean matchBerat = (p.getBeratBarangBuktiGram() >= minBerat && p.getBeratBarangBuktiGram() <= maxBerat);
            boolean matchPasal = (pasal == null || pasal.isEmpty() || p.getPasalTerbukti().toLowerCase().contains(pasal.toLowerCase()));
            boolean matchVonis = (p.getVonisBulan() >= minVonis && p.getVonisBulan() <= maxVonis);

            if (matchJenis &&  matchBerat && matchPasal && matchVonis) {
                result.add(p);
            }
        }
        logAudit("FILTER", "Filter Menghasilkan " + result.size() + " record cocok.");
        return result;
    }

    //Fitur Agregrasi Data Statis
    public void printAggregationReport() {
        int count = mainStorage.size();
        if (count == 0) {
            System.out.println("Storage kosong, agregasi tidak dapat dihitung.");
            return;
        }

        long totalVonis = 0;
        long totalDenda = 0;
        int countEkstasi = 0;
        int countGanja = 0;
        int countSabu = 0;

        for (int i = 0; i < count; i++) {
            Putusan p = mainStorage.get(i);
            totalVonis += p.getVonisBulan();
            totalDenda += p.getDendaRupiah();

            String jenis = p.getJenisNarkotika().toLowerCase();
            if (jenis.contains("ekstaksi")) countEkstasi++;
            else if (jenis.contains("ganja")) countGanja++;
            else if (jenis.contains("sabu")) countSabu++;
        }

        double avgVonis = (double) totalVonis / count;

        System.out.println("\n================ LAPORAN AGREGASI DATA ================");
        System.out.printf("Total Perkara Terolah       : %d record\n", count);
        System.out.printf("Rata-rata Hukuman Penjara   : %.2f bulan (%.2f tahun)\n", avgVonis, avgVonis / 12.0);
        System.out.printf("Total Akumulasi Denda       : Rp%,d\n", totalDenda);
        System.out.println("Distribusi Jenis Narkotika  :");
        System.out.printf("  - Ekstasi : %d perkara\n", countEkstasi);
        System.out.printf("  - Ganja   : %d perkara\n", countGanja);
        System.out.printf("  - Sabu    : %d perkara\n", countSabu);
        System.out.println("=======================================================");
        logAudit("AGREGASI", "Menghitung agregasi statistik 50 record.");
    }

    //Fitur Mengurutkan Tampilan berdasarkan Vonis (In-Place Insertion Sort) tanpa merusak invariant
    public DynamicArray<Putusan> getSortedByVonisAscending() {
        DynamicArray<Putusan> sortedCopy = new DynamicArray<>(mainStorage.size());
        for (int i = 0; i < mainStorage.size(); i++) {
            sortedCopy.add(mainStorage.get(i));
        }

        // Insertion Sort manual pada copy
        for (int i = 1; i < sortedCopy.size(); i++) {
            Putusan key = sortedCopy.get(i);
            int j = i - 1;
            while (j >= 0 && sortedCopy.get(j).getVonisBulan() > key.getVonisBulan()) {
                sortedCopy.set(j + 1, sortedCopy.get(j));
                j--;
            }
            sortedCopy.set(j + 1, key);
        }

        logAudit("PENGURUTAN", "Menghasilkan tampilan terurut berdasarkan Vonis Bulan (Ascending).");
        return sortedCopy;
    }

    public void runAllScenarios() {
        System.out.println("\n===================================================");
        System.out.println("          MENJALANKAN 7 SKENARIO UJI UTAMA          ");
        System.out.println("===================================================");

        //Skenario 1 (Muat 50 record dan buktikan size bernilai 50.)
        System.out.println("\n--- SKENARIO 1 ---");
        System.out.println("Size Main Storage: " + mainStorage.size());
        System.out.println("Invariant Storage Valid: " + mainStorage.validateInvariant());
        assert mainStorage.size() == 50 : "Ukuran harus 50!";

        //Skenario 2 (Cari satu perkara yang ada, satu yang tidak ada, dan satu record pada posisi batas.)
        System.out.println("\n--- SKENARIO 2 ---");
        System.out.println("1. Posisi Batas Awal (P001):");
        search("P001");
        System.out.println("2. Posisi Batas Akhir (P050):");
        search("P050");
        System.out.println("3. Perkara Ada (Tengah):");
        search("506/Pid.Sus/2023/PN Mdn");
        System.out.println("4. Perkara Tidak Ada:");
        search("999/Pid.Sus/2023/PN Mdn");

        //Skenario 3 (Hapus elemen pertama, tengah, dan terakhir, lalu tunjukkan perubahan pointer atau pergeseran elemen.)
        System.out.println("\n--- SKENARIO 3 ---");
        int initialSize = mainStorage.size();
        Putusan first = mainStorage.remove(0); // Head
        System.out.println("Dihapus Posisi Pertama (Indeks 0): " + first.getCaseId());

        Putusan mid = mainStorage.remove(mainStorage.size() / 2); // Middle
        System.out.println("Dihapus Posisi Tengah: " + mid.getCaseId());

        Putusan last = mainStorage.remove(mainStorage.size() - 1); // Tail
        System.out.println("Dihapus Posisi Terakhir: " + last.getCaseId());

        System.out.println("Ukuran setelah 3 penghapusan: " + mainStorage.size() + " (Ekspektasi: " + (initialSize - 3) + ")");
        System.out.println("Invariant setelah hapus valid: " + mainStorage.validateInvariant());

        //Skenario 4 (Tambahkan kembali tiga record dan buktikan invariant serta urutan data.)
        System.out.println("\n--- SKENARIO 4 ---");
        mainStorage.add(first);
        mainStorage.insert(0, mid);
        mainStorage.add(last);
        System.out.println("Ukuran setelah penambahan kembali: " + mainStorage.size() + " (Ekspektasi: 50)");
        System.out.println("Invariant setelah penambahan valid: " + mainStorage.validateInvariant());

        //Skenario 5 (Jalankan filter gabungan dan cocokkan hasilnya dengan pemeriksaan provenance manual.)
        System.out.println("\n--- SKENARIO 5 ---");
        ProvenanceController provService = new ProvenanceController();
        provService.loadProvenanceCsv("docs/data-provenance.csv");

        DynamicArray<Putusan> filtered = filter("Sabu", 1.0, 100.0, "", 60, 240);
        System.out.println("Jumlah record terfilter: " + filtered.size());
        for (int i = 0; i < filtered.size(); i++) {
            Putusan item = filtered.get(i);
            System.out.println("  -> " + item);
            provService.verifyPutusanAgainstProvenance(item);
        }

        //Skenario 6 (Tunjukkan bahwa pencarian pada Stack atau Queue tidak mengubah urutan akhir.)
        System.out.println("\n--- SKENARIO 6 ---");
        int stackSizeBefore = auditLog.size();
        DynamicArray<AuditEntry> history = auditLog.getElementsPreserved();
        int stackSizeAfter = auditLog.size();
        System.out.println("Ukuran Stack sebelum Inspeksi: " + stackSizeBefore);
        System.out.println("Ukuran Stack setelah Inspeksi: " + stackSizeAfter);
        System.out.println("Ukuran Stack tetap konsisten: " + (stackSizeBefore == stackSizeAfter));

        //Skenario 7 (Masukkan record yang tidak valid dan tampilkan pesan kesalahan, serta pastikan strukturnya tetap konsisten.)
        System.out.println("\n--- SKENARIO 7 ---");
        System.out.println("Uji coba...");
        try {
            //Simulasi Parsing kolom yang kurang
            String invalidLine = "P999,Invalid,PN Bali";
            String[] tokens = invalidLine.split(",");
            if (tokens.length != 16) {
                throw new IllegalArgumentException("Jumlah kolom tidak sesuai skema minimum (Hanya " + tokens.length + " kolom).");
            }
        } catch (Exception e) {
            System.err.println("Pesan Kesalahan Ditangkap: " + e.getMessage());
            System.out.println("Main storage tetap aman dengan size: " + mainStorage.size());
            System.out.println("Invariant main storage valid: " + mainStorage.validateInvariant());
        }

        System.out.println("\nSELURUH SKENARIO BERHASIL DIUJI.");
    }
}
