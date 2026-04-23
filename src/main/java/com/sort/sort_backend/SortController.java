package com.sort.sort_backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class SortController {

    private static final String IDLE = "#B4B2A9";
    private static final String HEAP = "#EF9F27";
    private static final String CMP  = "#1D9E75";
    private static final String SWP  = "#378ADD";
    private static final String DONE = "#5DCAA5";

    private int[] currentArr = generateArr();

    @GetMapping("/nuevo")
    public Map<String, int[]> nuevo() {
        currentArr = generateArr();
        return Map.of("arr", currentArr);
    }

    @GetMapping("/pasos")
    public List<SortStep> pasos(@RequestParam(defaultValue = "heap") String algo) {
        return switch (algo) {
            case "bubble" -> bubbleSort(currentArr);
            case "select" -> selectionSort(currentArr);
            default       -> heapSort(currentArr);
        };
    }

    // ── HeapSort ──────────────────────────────────────────────────────────────
    private List<SortStep> heapSort(int[] input) {
        List<SortStep> steps = new ArrayList<>();
        int[] work = copy(input);
        int n = work.length;
        int[] cmp = {0}, swp = {0};

        steps.add(make(work, allColor(n, IDLE), cmp[0], swp[0],
                "inicio: construyendo max-heap", 1, false));

        for (int j = n / 2 - 1; j >= 0; j--)
            downheapSteps(work, j, n, cmp, swp, steps, true);

        steps.add(make(work, allColor(n, HEAP), cmp[0], swp[0],
                "max-heap listo — fase 2: extracción", 1, false));

        for (int end = n - 1; end > 0; end--) {
            swp[0]++;
            int tmp = work[0]; work[0] = work[end]; work[end] = tmp;
            steps.add(make(work, colorsSwap(work, end + 1, n, 0, end),
                    ++cmp[0], swp[0],
                    "extrae máximo " + work[end] + " → posición " + end, 2, false));
            downheapSteps(work, 0, end, cmp, swp, steps, false);
        }

        steps.add(make(work, allColor(n, DONE), cmp[0], swp[0],
                "heapsort completo", 0, true));
        return steps;
    }

    private void downheapSteps(int[] a, int j, int size,
                                int[] cmp, int[] swp,
                                List<SortStep> steps, boolean fase1) {
        while (true) {
            int l = 2*j+1, r = 2*j+2, big = j;
            if (l < size) { cmp[0]++; if (a[l] > a[big]) big = l; }
            if (r < size) { cmp[0]++; if (a[r] > a[big]) big = r; }
            if (big == j) break;
            swp[0]++;
            int tmp = a[j]; a[j] = a[big]; a[big] = tmp;
            steps.add(make(a, colorsSwap(a, size, a.length, j, big),
                    cmp[0], swp[0],
                    "downheap: intercambia pos " + j + "↔" + big,
                    fase1 ? 1 : 2, false));
            j = big;
        }
    }

    // ── BubbleSort ────────────────────────────────────────────────────────────
    private List<SortStep> bubbleSort(int[] input) {
        List<SortStep> steps = new ArrayList<>();
        int[] work = copy(input);
        int n = work.length, cmp = 0, swp = 0;

        steps.add(make(work, allColor(n, IDLE), cmp, swp,
                "inicio bubblesort", 0, false));

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                cmp++;
                String[] c = new String[n];
                for (int k = 0; k < n; k++)
                    c[k] = k >= n-i ? DONE : (k==j||k==j+1) ? CMP : IDLE;
                steps.add(make(work, c, cmp, swp,
                        "comparando " + work[j] + " y " + work[j+1], 0, false));
                if (work[j] > work[j+1]) {
                    swp++;
                    int tmp = work[j]; work[j] = work[j+1]; work[j+1] = tmp;
                    String[] c2 = new String[n];
                    for (int k = 0; k < n; k++)
                        c2[k] = k >= n-i ? DONE : (k==j||k==j+1) ? SWP : IDLE;
                    steps.add(make(work, c2, cmp, swp,
                            "intercambia " + work[j+1] + "↔" + work[j], 0, false));
                }
            }
        }
        steps.add(make(work, allColor(n, DONE), cmp, swp,
                "bubblesort completo", 0, true));
        return steps;
    }

    // ── SelectionSort ─────────────────────────────────────────────────────────
    private List<SortStep> selectionSort(int[] input) {
        List<SortStep> steps = new ArrayList<>();
        int[] work = copy(input);
        int n = work.length, cmp = 0, swp = 0;

        steps.add(make(work, allColor(n, IDLE), cmp, swp,
                "inicio selectionsort", 0, false));

        for (int i = 0; i < n - 1; i++) {
            int mi = i;
            for (int j = i + 1; j < n; j++) {
                cmp++;
                String[] c = new String[n];
                for (int k = 0; k < n; k++)
                    c[k] = k < i ? DONE : k == mi ? CMP : k == j ? HEAP : IDLE;
                steps.add(make(work, c, cmp, swp,
                        "buscando mínimo: " + work[j] + " vs " + work[mi], 0, false));
                if (work[j] < work[mi]) mi = j;
            }
            if (mi != i) {
                swp++;
                int tmp = work[i]; work[i] = work[mi]; work[mi] = tmp;
                String[] c2 = new String[n];
                for (int k = 0; k < n; k++) c2[k] = k <= i ? DONE : IDLE;
                steps.add(make(work, c2, cmp, swp,
                        "coloca mínimo " + work[i] + " en pos " + i, 0, false));
            }
        }
        steps.add(make(work, allColor(n, DONE), cmp, swp,
                "selectionsort completo", 0, true));
        return steps;
    }

    // ── Utilidades ────────────────────────────────────────────────────────────
    private SortStep make(int[] arr, String[] colors,
                          int cmp, int swp, String msg,
                          int phase, boolean done) {
        return new SortStep(arr, colors, cmp, swp, msg, phase, done);
    }

    private String[] allColor(int n, String col) {
        String[] c = new String[n];
        Arrays.fill(c, col);
        return c;
    }

    private String[] colorsSwap(int[] a, int heapSize, int total, int i1, int i2) {
        String[] c = new String[total];
        for (int i = 0; i < total; i++)
            c[i] = i >= heapSize ? DONE : (i == i1 || i == i2) ? SWP : HEAP;
        return c;
    }

    private int[] copy(int[] src) {
        return Arrays.copyOf(src, src.length);
    }

    private int[] generateArr() {
        Random rnd = new Random();
        int[] arr = new int[14];
        for (int i = 0; i < arr.length; i++) arr[i] = rnd.nextInt(90) + 5;
        return arr;
    }
}