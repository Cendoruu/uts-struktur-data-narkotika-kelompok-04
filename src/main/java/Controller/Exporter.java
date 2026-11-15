package Controller;

import StrukDat.DynamicArray;
import Model.Putusan;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Exporter {
    public static void exportToCsv(DynamicArray<Putusan> data, String outputPath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {
            bw.write("Caseid,nomorPerkara,pengadilan,tanggalPutusan,terdakwaKode,jenisNarkotika,beratBarangBuktiGram,pasalDakwaan,pasalTerbukti,tuntutanBulan,vonisBulan,dendaRupiah,barangBukti,keadaanMemberatkan,keadaanMeringankan,sourcePages");
            bw.newLine();

            for (int i = 0; i < data.size(); i++) {
                Putusan p = data.get(i);
                String line = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%.2f,\"%s\",\"%s\",%d,%d,%d,\"%s\",\"%s\",\"%s\",\"%s\"",
                        p.getCaseId(), p.getNomorPerkara(), p.getPengadilan(), p.getTanggalPutusan().toString(),
                        p.getTerdakwaKode(), p.getJenisNarkotika(), p.getBeratBarangBuktiGram(),
                        p.getPasalDakwaan(), p.getPasalTerbukti(), p.getTuntutanBulan(), p.getVonisBulan(),
                        p.getDendaRupiah(), p.getBarangBukti(), p.getKeadaanMemberatkan(), p.getKeadaanMeringankan(), p.getSourcePages());
                bw.write(line);
                bw.newLine();
            }
            System.out.println("[INFO EKSPOR] Berhasil mengekspor " + data.size() + " record ke: " + outputPath);
        } catch (IOException e) {
            System.err.println("Gagal mengekspor file: " + e.getMessage());
        }
    }

    public static void exportReportToTxt(String reportContent, String outputPath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath))) {
            bw.write(reportContent);
            System.out.println("[INFO EKSPOR] Laporan berhasil disimpan ke: " + outputPath);
        } catch (IOException e) {
            System.err.println("Gagal mengekspor laporan teks: " + e.getMessage());
        }
    }
}
