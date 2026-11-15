package StrukDat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CustomStackTest {
    private CustomStack<String> stack;

    @BeforeEach
    void setUp() {
        stack = new CustomStack<>();
    }

    @Test
    void testPrinsipLifoPushPopPeek() {
        stack.push("LOG-1");
        stack.push("LOG-2");
        stack.push("LOG-3");

        assertEquals(3, stack.size());
        assertEquals("LOG-3", stack.peek());
        assertEquals("LOG-3", stack.pop());
        assertEquals("LOG-2", stack.peek());
        assertEquals(2, stack.size());
        assertTrue(stack.validateInvariant());
    }

    @Test
    void testInspeksiNonDestruktifTidakMengubahUrutan() {
        stack.push("OPERASI-AWAL");
        stack.push("OPERASI-TENGAH");
        stack.push("OPERASI-AKHIR");

        int sizeSebelum = stack.size();
        String topSebelum = stack.peek();

        // Baca seluruh isi stack secara non-destruktif
        DynamicArray<String> hasilBaca = stack.getElementsPreserved();

        // Pastikan ukuran dan urutan asli stack tidak berubah (Skenario 6 UTS)
        assertEquals(sizeSebelum, stack.size());
        assertEquals(topSebelum, stack.peek());
        assertEquals("OPERASI-AKHIR", hasilBaca.get(0));
        assertEquals("OPERASI-AWAL", hasilBaca.get(2));
        assertTrue(stack.validateInvariant());
    }

    @Test
    void testPopPadaStackKosongMelemparException() {
        assertTrue(stack.isEmpty());
        assertThrows(IllegalStateException.class, () -> stack.pop());
        assertTrue(stack.validateInvariant());
    }
}