package StrukDat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DynamicArrayTest {
    private DynamicArray<String> array;

    @BeforeEach
    void setUp() {
        array = new DynamicArray<>(2); // Kapasitas awal kecil untuk memicu resize
    }

    @Test
    void testAddDanAutoResizeMenjagaInvariant() {
        array.add("P001");
        array.add("P002");
        array.add("P003"); // Memicu perbesaran kapasitas otomatis

        assertEquals(3, array.size());
        assertTrue(array.capacity() >= 3);
        assertEquals("P001", array.get(0));
        assertEquals("P003", array.get(2));
        assertTrue(array.validateInvariant());
    }

    @Test
    void testInsertDanPergeseranElemen() {
        array.add("P001");
        array.add("P003");
        array.insert(1, "P002"); // Sisipkan di tengah

        assertEquals(3, array.size());
        assertEquals("P001", array.get(0));
        assertEquals("P002", array.get(1));
        assertEquals("P003", array.get(2));
        assertTrue(array.validateInvariant());
    }

    @Test
    void testRemoveAwalTengahAkhir() {
        array.add("A");
        array.add("B");
        array.add("C");
        array.add("D");
        array.add("E");

        // Hapus pertama (indeks 0)
        assertEquals("A", array.remove(0));
        assertEquals("B", array.get(0)); // Elemen bergeser ke kiri

        // Hapus tengah (indeks 1 -> "C")
        assertEquals("C", array.remove(1));
        assertEquals("D", array.get(1));

        // Hapus terakhir
        assertEquals("E", array.remove(array.size() - 1));
        assertEquals(2, array.size());
        assertTrue(array.validateInvariant());
    }

    @Test
    void testBoundaryException() {
        array.add("P001");
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(5));
        assertTrue(array.validateInvariant());
    }
}