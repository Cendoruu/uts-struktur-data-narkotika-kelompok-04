package StrukDat;

/**
 * Struktur Data Sekunder: Custom Stack Generik untuk Audit Log.
 */
public class CustomStack<T> {
    private Object[] elements;
    private int top;
    private static final int DEFAULT_CAPACITY = 10;

    public CustomStack() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.top = -1;
    }

    public void push(T value) {
        if (top + 1 == elements.length) {
            resize(elements.length * 2);
        }
        elements[++top] = value;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack kosong, tidak dapat melakukan pop.");
        }
        T item = (T) elements[top];
        elements[top--] = null;
        return item;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) return null;
        return (T) elements[top];
    }

    public int size() {
        return top + 1;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    private void resize(int newCapacity) {
        Object[] newArray = new Object[newCapacity];
        for (int i = 0; i <= top; i++) {
            newArray[i] = elements[i];
        }
        elements = newArray;
    }

    public boolean validateInvariant() {
        if (elements == null) return false;
        if (top < -1 || top >= elements.length) return false;
        for (int i = 0; i <= top; i++) {
            if (elements[i] == null) return false;
        }
        return true;
    }

    /**
     * Inspeksi Non-Destruktif: Mengembalikan elemen dalam bentuk DynamicArray tanpa merusak Stack.
     */
    @SuppressWarnings("unchecked")
    public DynamicArray<T> getElementsPreserved() {
        DynamicArray<T> result = new DynamicArray<>(size());
        for (int i = top; i >= 0; i--) {
            result.add((T) elements[i]);
        }
        return result;
    }
}