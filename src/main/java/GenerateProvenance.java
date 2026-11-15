import Model.Putusan;
import Controller.CSVImporter;
import Controller.ProvenanceController;
import StrukDat.DynamicArray;

public class GenerateProvenance {
    public static void main(String[] args) {
        DynamicArray<Putusan> data = CSVImporter.loadCSV("data/putusan-50.csv");
        ProvenanceController.generateInitialTemplate(
                data,
                "docs/trace/data-provenance.csv",
                "Firja",
                "Firja"
        );
    }
}
