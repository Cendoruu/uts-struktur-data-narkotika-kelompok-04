package Controller;

import StrukDat.DynamicArray;
import Model.Putusan;

import java.io.BufferedReader;
import java.io.FileReader;

import java.time.LocalDate;

public class CSVImporter {
    public static DynamicArray<Putusan> loadCSV(String filePath) {
        DynamicArray<Putusan> dataset = new DynamicArray<>(55);
        int lineNum = 0;
        int succesCount = 0;
        int failCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                lineNum++;
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                try {
                    String[] cols = parseCsvLine(line);
                    if (cols.length < 16) {
                        throw new IllegalArgumentException("Jumlah kolom kurang dari 16 (" + cols.length + ")");
                    }

                    String caseId = cols[0];
                    String nomorPerkara = cols[1];
                    String pengadilan = cols[2];
                    LocalDate tanggalPutusan = LocalDate.parse(cols[3]);
                    String terdakwaKode =  cols[4];
                    String jenisNarkotika = cols[5];
                    double beratBarangBuktiGram = Double.parseDouble(cols[6]);
                    String pasalDakwaan = cols[7];
                    String pasalTerbukti = cols[8];
                    int tuntutanBulan =  Integer.parseInt(cols[9]);
                    int vonisBulan =  Integer.parseInt(cols[10]);
                    long dendaRupiah = Long.parseLong(cols[11]);
                    String barangBukti = cols[12];
                    String keadaanMemberatkan = cols[13];
                    String keadaanMeringankan = cols[14];
                    String sourcePages = cols[15];

                    Putusan p = new Putusan (caseId, nomorPerkara, pengadilan, tanggalPutusan, terdakwaKode, jenisNarkotika, beratBarangBuktiGram,
                            pasalDakwaan, pasalTerbukti, tuntutanBulan, vonisBulan, dendaRupiah, barangBukti, keadaanMemberatkan,
                            keadaanMeringankan, sourcePages);

                    dataset.add(p);
                    succesCount++;
                } catch (Exception e) {
                    failCount++;
                    System.err.printf("[PERINGATAN IMPOR] Baris %d ditolak: %s (Pesan: %s)\n", lineNum, line, e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.printf("Gagal membaca file CSV: " + e.getMessage());
        }

        System.out.println("\"[INFO IMPOR SELESAI] Berhasil memuat: %d record, Ditolak: %d record.\\n\", successCount, failCount");
        return dataset;
    }

    //Parsing CSV manual untuk menangani koma di dalam tanda petik ganda
    private static String[] parseCsvLine(String line) {
        DynamicArray<String> tokens = new DynamicArray<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());

        String[] result = new String[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            result[i] = tokens.get(i);
        }
        return result;
    }
}
