package Model;

public class ProvenanceRecord {
    private String caseId;
    private String fieldname;
    private String value;
    private String sourceFile;
    private String sourcePage;
    private String extractor;
    private String reviewer;
    private String status;

    public ProvenanceRecord(String caseId, String fieldname, String value, String sourceFile,
                            String sourcePage, String extractor, String reviewer, String status) {
        this.caseId = caseId;
        this.fieldname = fieldname;
        this.value = value;
        this.sourceFile = sourceFile;
        this.sourcePage = sourcePage;
        this.extractor = extractor;
        this.reviewer = reviewer;
        this.status = status;
    }

    public String getCaseId() { return caseId; }
    public String getFieldname() { return fieldname; }
    public String getValue() { return value; }
    public String getSourceFile() { return sourceFile; }
    public String getSourcePage() { return sourcePage; }
    public String getExtractor() { return extractor; }
    public String getReviewer() { return reviewer; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("[%s] %-21s = %-25s | %s (Hal %s) | Ext: %s, Rev: %s [%s]",
                caseId, fieldname, value, sourceFile, sourcePage, extractor, reviewer, status);
    }
}
