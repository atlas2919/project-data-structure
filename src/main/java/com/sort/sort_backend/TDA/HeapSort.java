package com.sort.sort_backend.TDA;

import java.util.Comparator;

public class HeapSort {
  // ── Con Comparator externo ────────────────────────────────────────────────
  public static <K> void sort(K[] data, Comparator<K> comp) {
    int n = data.length;
    HeapPriorityQueue<K, K> pq = new HeapPriorityQueue<>(comp);

    // Fase 1: insertar todos → O(n log n)
    for (int i = 0; i < n; i++) pq.insert(data[i], data[i]);

    // Fase 2: extraer en orden → O(n log n)
    for (int i = 0; i < n; i++) data[i] = pq.removeMin().getKey();
  }

  // ── Con Comparable (DefaultComparator implícito) ──────────────────────────
  public static <K extends Comparable<K>> void sort(K[] data) {
    sort(data, new DefaultComparator<>());
  }
}
