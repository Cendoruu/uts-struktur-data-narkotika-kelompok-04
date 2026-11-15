package View;

import StrukDat.DynamicArray;
import Model.Putusan;
import Controller.CSVImporter;
import Controller.Exporter;
import Controller.PutusanController;

import java.util.Scanner;

public class Main {
    private static final String CSV_PATH = "data/putusan-50.csv";

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("    APLIKASI STRUKTUR DATA PUTUSAN NARKOTIKA (50 RECORD MAHKAMAH) ");
        System.out.println("==================================================================");

        DynamicArray<Putusan> data = CSVImporter.loadCSV(CSV_PATH);
        DynamicArray<Putusan> lastQueryResult = new DynamicArray<>();
        PutusanController controller = new PutusanController(data);
        Scanner scanner = new Scanner(System.in);

        boolean running = true;

        while (running) {
            System.out.println("\n---------------- MENU UTAMA ----------------");
            System.out.println("1. Tambah Putusan");
            System.out.println("2. Tampilkan Semua Ringkasan Putusan");
            System.out.println("3. Cari Putusan (by CaseId / Nomor Perkara) + Step Count");
            System.out.println("4. Filter Putusan (Jenis, Berat, Vonis)");
            System.out.println("5. Tampilkan Agregasi Statistik Data");
            System.out.println("6. Tampilkan Putusan Terurut (Vonis Ascending)");
            System.out.println("7. Lihat Riwayat Audit Log (Custom Stack LIFO)");
            System.out.println("8. Hapus Putusan berdasarkan Indeks");
            System.out.println("9. Ekspor Storage ke File CSV Baru");
            System.out.println("10. Jalankan 7 Skenario Uji Coba");
            System.out.println("0. Keluar Application");
            System.out.print("Pilih menu (0-9): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    controller.addData();
                    break;
                case "2":
                    DynamicArray<Putusan> storage = controller.getMainStorage();
                    System.out.println("\n=== DAFTAR PUTUSAN (" + storage.size() + " RECORD) ===");
                    for (int i = 0; i < storage.size(); i++) {
                        System.out.printf("[%2d] %s\n", i, storage.get(i));
                    }
                    break;
                case "3":
                    System.out.print("Masukkan CaseId (contoh: P001) atau nomor perkara: ");
                    String key = scanner.nextLine();
                    Putusan res = controller.search(key);
                    if  (res != null) {
                        lastQueryResult.clear();
                        lastQueryResult.add(res);
                        System.out.println("\nDetail Perkara:");
                        System.out.println("Case ID       : " + res.getCaseId());
                        System.out.println("No. Perkara   : " + res.getNomorPerkara());
                        System.out.println("Pengadilan    : " + res.getPengadilan());
                        System.out.println("Terdakwa Kode : " + res.getTerdakwaKode());
                        System.out.println("Narkotika     : " + res.getJenisNarkotika() + " (" + res.getBeratBarangBuktiGram() + " gram)");
                        System.out.println("Pasal Terbukti: " + res.getPasalTerbukti());
                        System.out.println("Vonis         : " + res.getVonisBulan() + " Bulan");
                        System.out.println("Denda         : Rp" + res.getDendaRupiah());
                        System.out.println("Source Pages  : " + res.getSourcePages());
                    }
                    break;
                case "4":
                    System.out.print("Jenis Narkotika (Ekstasi/Ganja/Sabu, tekan Enter untuk semua): ");
                    String jn = scanner.nextLine();
                    System.out.print("Minimal Berat (gram, default 0): ");
                    String minBStr = scanner.nextLine();
                    double minB = minBStr.isEmpty() ? 0 : Double.parseDouble(minBStr);
                    System.out.print("Maksimal Berat (gram, default 99999): ");
                    String maxBStr = scanner.nextLine();
                    double maxB = maxBStr.isEmpty() ? 99999 : Double.parseDouble(maxBStr);
                    System.out.print("Minimal Vonis (bulan, default 0): ");
                    String minVStr = scanner.nextLine();
                    int minV = minVStr.isEmpty() ? 0 : Integer.parseInt(minVStr);
                    System.out.print("Maksimal Vonis (bulan, default 999): ");
                    String maxVStr = scanner.nextLine();
                    int maxV = maxVStr.isEmpty() ? 999 : Integer.parseInt(maxVStr);

                    System.out.print("Pasal Terbukti (contoh: 114 ayat (1), tekan Enter untuk semua): ");
                    String pasalFilter = scanner.nextLine();

                    DynamicArray<Putusan> filtered = controller.filter(jn, minB, maxB, pasalFilter, minV, maxV);
                    System.out.println("\n=== HASIL FILTERING (" + filtered.size() + " RECORD MATCH) ===");
                    for (int i = 0; i < filtered.size(); i++) {
                        System.out.println(filtered.get(i));
                    }
                    break;
                case "5":
                    controller.printAggregationReport();
                    break;
                case "6":
                    DynamicArray<Putusan> sorted = controller.getSortedByVonisAscending();
                    System.out.println("\n=== TAMPILAN TERURUT DARI VONIS TERENDAH KE TINGGI ===");
                    for (int i = 0; i < sorted.size(); i++) {
                        System.out.printf("[%2d] Vonis: %3d bln | %s\n", i + 1, sorted.get(i).getVonisBulan(), sorted.get(i));
                    }
                    break;
                case "7":
                    controller.displayAuditHistory();
                    break;
                case "8":
                    System.out.print("Masukkan indeks yang ingin dihapus (0 - " + (controller.getMainStorage().size() - 1) + "): ");
                    int idx = Integer.parseInt(scanner.nextLine());
                    Putusan del = controller.getMainStorage().remove(idx);
                    controller.logAudit("HAPUS", "Menghapus " + del.getCaseId() + " dari indeks " + idx);
                    System.out.println("Berhasil menghapus: " + del.getCaseId());
                    break;
                case "9":
                    String out = "data/ekspor-putusan.csv";
                    Exporter.exportToCsv(controller.getMainStorage(), out);
                    break;
                case "10":
                    controller.runAllScenarios();
                    break;
                case "0":
                    running = false;
                    System.out.println("Program Keluar...");
                    break;
                default:
                    System.out.println("Pilihan menu tidak valid.");
            }
        }
        scanner.close();
    }
}
