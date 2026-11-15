package Controller;

import StrukDat.DynamicArray;
import Model.ProvenanceRecord;
import Model.Putusan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ProvenanceController {
    private DynamicArray<ProvenanceRecord> records;

    public ProvenanceController() {
        this.records = new DynamicArray<>(300);
    }

    public void loadProvenanceCsv(String filePath) {
        records.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean headerSkipped = false;
            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                String[] cols = CSVImporter.parseCsvLine(line);
                if (cols.length >= 8) {
                    records.add(new ProvenanceRecord(
                            cols[0], cols[1], cols[2], cols[3],
                            cols[4], cols[5], cols[6], cols[7]
                    ));
                }
            }
            System.out.println("[INFO PROVENANCE] Memuat " + records.size() + " baris atribut provenance.");
        } catch (IOException e) {
            System.err.println("[PERINGATAN PROVENANCE] Berkas belum ditemukan: " + e.getMessage());
        }
    }
    public boolean verifyPutusanAgainstProvenance(Putusan p) {
        int matchedAttributes = 0;
        boolean allValid = true;

        System.out.println("  Pemeriksaan Provenance untuk " + p.getCaseId() + " (" + p.getNomorPerkara() + "):");
        for (int i = 0; i < records.size(); i++) {
            ProvenanceRecord pr = records.get(i);
            if (!pr.getCaseId().equalsIgnoreCase(p.getCaseId())) continue;

            matchedAttributes++;
            String actualValue = extractFieldValue(p, pr.getFieldname());
            boolean valueMatches = actualValue != null && actualValue.equalsIgnoreCase(pr.getValue().trim());
            boolean crossChecked = !pr.getExtractor().equalsIgnoreCase(pr.getReviewer());
            boolean isVerified = pr.getStatus().equalsIgnoreCase("Verified");

            if (!valueMatches || !crossChecked || isVerified) {
                allValid = false;
            }

            System.out.printf("    - %-21s | CSV: %-18s | Prov: %-18s | Hal: %-3s | Status: %s (%s)\n",
                    pr.getFieldname(), actualValue, pr.getValue(), pr.getSourcePage(),
                    pr.getStatus(), (valueMatches && crossChecked && isVerified) ? "COCOK" : "PERLU CEK");
        }

        if (matchedAttributes == 0) {
            System.out.println("   -> Belum ada entry provenance untuk " + p.getCaseId());
            return false;
        }
        return allValid;
    }

    private String extractFieldValue(Putusan p, String fieldname) {
        switch (fieldname) {
            case "nomorPerkara": return p.getNomorPerkara();
            case "pengadilan": return p.getPengadilan();
            case "terdakwaKode": return p.getTerdakwaKode();
            case "jenisNarkotika": return p.getJenisNarkotika();
            case "beratBarangBuktiGram": return String.valueOf(p.getBeratBarangBuktiGram());
            case "pasalDakwaan": return p.getPasalDakwaan();
            case "pasalTerbukti": return p.getPasalTerbukti();
            case "tuntutanBulan": return String.valueOf(p.getTuntutanBulan());
            case "vonisBulan": return String.valueOf(p.getVonisBulan());
            case "dendaRupiah": return String.valueOf(p.getDendaRupiah());
            default: return null;
        }
    }

    public static void generateInitialTemplate(DynamicArray<Putusan> dataset, String outputPath,
                                               String anggota1, String anggota2) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {
            bw.write("case_id,field_name,value,source_file,source_page,extractor,reviewer,status");
            bw.newLine();

            for (int i = 0; i < dataset.size(); i++) {
                Putusan p = dataset.get(i);
                String extractor = (i < 25) ? anggota1 : anggota2;
                String reviewer = (i < 25) ? anggota2 : anggota1;
                String sourceFile = "putusan_" + p.getCaseId().substring(1) + ".pdf";

                // Ekstrak nomor halaman dari format "Hal 1,2,27,28"
                String rawPages = p.getSourcePages().replace("Hal", "").trim();
                String[] pages = rawPages.split(",");
                String pageIdentitas = pages.length > 0 ? pages[0].trim() : "1";
                String pageBukti = pages.length > 1 ? pages[1].trim() : "2";
                String pageTuntutan = pages.length > 2 ? pages[2].trim() : "-";
                String pageAmar = pages.length > 3 ? pages[3].trim() : "-";

                writeRow(bw, p.getCaseId(), "nomorPerkara", p.getNomorPerkara(), sourceFile, pageIdentitas, extractor, reviewer, "verified");
                writeRow(bw, p.getCaseId(), "jenisNarkotika", p.getJenisNarkotika(), sourceFile, pageBukti, extractor, reviewer, "verified");
                writeRow(bw, p.getCaseId(), "beratBarangBuktiGram", String.valueOf(p.getBeratBarangBuktiGram()), sourceFile, pageBukti, extractor, reviewer, "verified");
                writeRow(bw, p.getCaseId(), "tuntutanBulan", String.valueOf(p.getTuntutanBulan()), sourceFile, pageTuntutan, extractor, reviewer, "verified");
                writeRow(bw, p.getCaseId(), "pasalTerbukti", p.getPasalTerbukti(), sourceFile, pageAmar, extractor, reviewer, "verified");
                writeRow(bw, p.getCaseId(), "vonisBulan", String.valueOf(p.getVonisBulan()), sourceFile, pageAmar, extractor, reviewer, "verified");
                writeRow(bw, p.getCaseId(), "dendaRupiah", String.valueOf(p.getDendaRupiah()), sourceFile, pageAmar, extractor, reviewer, "verified");
            }
            System.out.println("[INFO] Kerangka provenance berhasil dibuat di: " + outputPath);
        } catch (Exception e) {
            System.err.println("Gagal membuat file provenance: " + e.getMessage());
        }
    }

    private static void writeRow(BufferedWriter bw, String caseId, String field, String val,
                                 String file, String page, String ext, String rev, String status) throws IOException {
        bw.write(String.format("%s,%s,\"%s\",%s,%s,%s,%s,%s",
                caseId, field, val, file, page, ext, rev, status));
        bw.newLine();
    }
}
