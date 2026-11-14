package Model;

import java.time.LocalDateTime;

public class AuditEntry {
    private LocalDateTime timestamp;
    private String namaOperasi;
    private String rincian;

    public AuditEntry(String namaOperasi, String rincian) {
        this.timestamp = LocalDateTime.now();
        this.namaOperasi = namaOperasi;
        this.rincian = rincian;
    }

    @Override
    public String toString() {
        return String.format("[%s] OPERASI: %s ->", timestamp.toString(), namaOperasi, rincian);
    }
}
