package StrukDat;

import java.util.Objects;

/**
 * Struktur Data Utama: Array Dinamis Generik tanpa java.util.
 */
public class DynamicArray<T> implements StrukturData<T> {
    private Object[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Kapasitas tidak boleh negatif!");
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }

    @Override
    public void add(T value) {
        ensureCapacity(size + 1);
        elements[size++] = value;
    }

    public void insert(int index, T value) {
        if (index < 0 || index > size) {
            throw new IllegalArgumentException("Indeks penyisipan di luar jangkauan: " + index);
        }
        ensureCapacity(size + 1);
        for  (int i = size; i > index; i--) {
            elements[i] = elements[i-1];
        }
        elements[index] = value;
        size++;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(int position) {
        checkBounds(position);
        return (T) elements[position];
    }

    public void set(int position, T value) {
        checkBounds(position);
        elements[position] = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T remove(int position) {
        checkBounds(position);
        T removed = (T) elements[position];
        for  (int i = position; i < size - 1; i++) {
            elements[i] = elements[i+1];
        }
        elements[--size] = null;
        return removed;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(T value) {
        for (int i = 0; i <= size; i++) {
            if (Objects.equals(elements[i], value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = elements.length == 0 ? DEFAULT_CAPACITY : elements.length * 2;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            Object[] newElements = new Object[newCapacity];
            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }
            elements = newElements;
        }
    }

    private void checkBounds(int position) {
        if (position < 0 || position >= size) {
            throw new IndexOutOfBoundsException("Indeks tidak valid: " + position + ", size: " + size);
        }
    }

    public boolean validateInvariant() {
        if (elements == null) return false;
        if (size < 0 || size > elements.length) return false;
        for (int i = 0; i < size; i++) {
            if (elements[i] == null) return false;
        }
        return true;
    }

    public int capacity() {
        return elements.length;
    }
}
