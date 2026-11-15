package Controller;

import Model.Putusan;
import StrukDat.DynamicArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PutusanAppIntegrationTest {
    private PutusanController controller;

    @BeforeEach
    void setUp() {
        DynamicArray<Putusan> data = CSVImporter.loadCSV("data/putusan-50.csv");
        controller = new PutusanController(data);
    }

    @Test
    void testSkenario1Muat50RecordDanInvariant() {
        DynamicArray<Putusan> storage = controller.getMainStorage();
        assertEquals(50, storage.size(), "Dataset harus memuat tepat 50 record");
        assertTrue(storage.validateInvariant());
    }

    @Test
    void testSkenario2PencarianBatasAdaDanTidakAda() {
        assertNotNull(controller.search("P001")); // Batas awal
        assertNotNull(controller.search("P050")); // Batas akhir
        assertNotNull(controller.search("506/Pid.Sus/2023/PN Mdn")); // Nomor perkara
        assertNull(controller.search("TIDAK-ADA-999")); // Tidak ada
    }

    @Test
    void testSkenario3Dan4HapusDanPulihkanTigaRecord() {
        DynamicArray<Putusan> storage = controller.getMainStorage();

        Putusan pertama = storage.remove(0);
        int idxTengah = storage.size() / 2;
        Putusan tengah = storage.remove(idxTengah);
        Putusan terakhir = storage.remove(storage.size() - 1);

        assertEquals(47, storage.size());
        assertTrue(storage.validateInvariant());

        // Kembalikan ketiga record
        storage.insert(0, pertama);
        storage.insert(idxTengah, tengah);
        storage.add(terakhir);

        assertEquals(50, storage.size());
        assertEquals("P001", storage.get(0).getCaseId());
        assertEquals("P050", storage.get(49).getCaseId());
        assertTrue(storage.validateInvariant());
    }

    @Test
    void testSkenario5FilterGabungan() {
        DynamicArray<Putusan> hasil = controller.filter("Sabu", 1.0, 100.0, "", 60, 240);
        for (int i = 0; i < hasil.size(); i++) {
            Putusan p = hasil.get(i);
            assertEquals("Sabu", p.getJenisNarkotika());
            assertTrue(p.getBeratBarangBuktiGram() >= 1.0 && p.getBeratBarangBuktiGram() <= 100.0);
            assertTrue(p.getVonisBulan() >= 60 && p.getVonisBulan() <= 240);
        }
    }

    @Test
    void testSkenario7TolakRecordInvalidTanpaMerusakStruktur() {
        String barisRusak = "P999,Perkara_Salah,PN Malang"; // Kolom kurang dari 16
        assertThrows(Exception.class, () -> CSVImporter.parseRecord(barisRusak));

        // Pastikan penyimpanan utama tetap utuh
        assertEquals(50, controller.getMainStorage().size());
        assertTrue(controller.getMainStorage().validateInvariant());
    }

    @Test
    void testUrutanVonisTidakMerusakUrutanAsli() {
        DynamicArray<Putusan> terurut = controller.getSortedByVonisAscending();
        assertEquals(50, terurut.size());

        for (int i = 0; i < terurut.size() - 1; i++) {
            assertTrue(terurut.get(i).getVonisBulan() <= terurut.get(i + 1).getVonisBulan());
        }

        // Data asli pada storage utama tetap dimulai dari P001
        assertEquals("P001", controller.getMainStorage().get(0).getCaseId());
        assertTrue(controller.getMainStorage().validateInvariant());
    }
}