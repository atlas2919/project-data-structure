package com.sort.sort_backend.TDA;

import java.util.Comparator;

public class QuickSort {

  public static <K> void quickSort(Queue<K> queue, Comparator<K> comp) {
    int n = queue.size();
    if (n < 2) return;

    K pivot = queue.first();

    Queue<K> Less = new LinkedQueue<>();
    Queue<K> Equal = new LinkedQueue<>();
    Queue<K> Greater = new LinkedQueue<>();

    while (!queue.isEmpty()) {
      K element = queue.dequeue();
      int c = comp.compare(element, pivot);
      if (c < 0) Less.enqueue(element);
      else if (c == 0) Equal.enqueue(element);
      else Greater.enqueue(element);
    }

    quickSort(Less, comp);
    quickSort(Greater, comp);

    while (!Less.isEmpty()) queue.enqueue(Less.dequeue());
    while (!Equal.isEmpty()) queue.enqueue(Equal.dequeue());
    while (!Greater.isEmpty()) queue.enqueue(Greater.dequeue());
  }

  public static <K> void quickSortInPlace(K[] S, Comparator<K> comp) {
    quickSortInPlace(S, comp, 0, S.length - 1);
  }

  private static <K> void quickSortInPlace(K[] arr, Comparator<K> comp, int a, int b) {
    if (a >= b) return;

    int left = a;
    int right = b - 1;
    K pivot = arr[b];
    K temp;

    while (left <= right) {
      while (left <= right && comp.compare(arr[left], pivot) < 0) left++;
      while (left <= right && comp.compare(arr[right], pivot) > 0) right--;

      if (left <= right) {
        temp = arr[left];
        arr[left] = arr[right];
        arr[right] = temp;
        left++;
        right--;
      }
    }

    temp = arr[left];
    arr[left] = arr[b];
    arr[b] = temp;

    quickSortInPlace(arr, comp, a, left - 1);
    quickSortInPlace(arr, comp, left + 1, b);
  }
}