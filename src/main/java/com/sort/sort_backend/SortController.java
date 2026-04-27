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
    private static final String OUT  = "#444441";

    private int[] currentArr = generateArr(14);

    @GetMapping("/nuevo")
    public Map<String, int[]> nuevo(@RequestParam(defaultValue = "14") int size) {
        size = Math.min(1000, Math.max(2, size));
        currentArr = generateArr(size);
        return Map.of("arr", currentArr);
    }

    @GetMapping("/pasos")
    public List<SortStep> pasos(
            @RequestParam(defaultValue = "heap") String algo,
            @RequestParam(defaultValue = "0") int size) {

        int skip = size > 500 ? 20 : size > 100 ? 5 : 1;

        List<SortStep> todos = switch (algo) {
            case "bucket" -> bucketSort(currentArr);
            case "quick"  -> quickSort(currentArr);
            default       -> heapSort(currentArr);
        };

        if (skip == 1) return todos;

        List<SortStep> reducidos = new ArrayList<>();
        for (int i = 0; i < todos.size(); i++) {
            if (i % skip == 0 || todos.get(i).done) reducidos.add(todos.get(i));
        }
        return reducidos;
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

    // ── BucketSort ────────────────────────────────────────────────────────────
    private List<SortStep> bucketSort(int[] input) {
        List<SortStep> steps = new ArrayList<>();
        int[] work = copy(input);
        int n = work.length;
        int cmp = 0, swp = 0;

        steps.add(make(work, allColor(n, IDLE), cmp, swp,
                "inicio bucketsort: distribuyendo en cubetas", 1, false));

        int max = work[0];
        for (int v : work) if (v > max) max = v;

        int bucketCount = n;
        ArrayList<ArrayList<Integer>> buckets = new ArrayList<>();
        for (int i = 0; i < bucketCount; i++) buckets.add(new ArrayList<>());

        for (int i = 0; i < n; i++) {
            int bi = (int)((double) work[i] / (max + 1) * bucketCount);
            buckets.get(bi).add(work[i]);
            String[] c = allColor(n, IDLE);
            c[i] = HEAP;
            cmp++;
            steps.add(make(work, c, cmp, swp,
                    "coloca " + work[i] + " en cubeta " + bi, 1, false));
        }

        int pos = 0;
        for (int bi = 0; bi < bucketCount; bi++) {
            ArrayList<Integer> bucket = buckets.get(bi);
            for (int i = 1; i < bucket.size(); i++) {
                int key = bucket.get(i);
                int j = i - 1;
                while (j >= 0 && bucket.get(j) > key) {
                    bucket.set(j + 1, bucket.get(j));
                    j--;
                    cmp++;
                }
                bucket.set(j + 1, key);
            }
            for (int val : bucket) {
                work[pos] = val;
                swp++;
                String[] c = new String[n];
                for (int k = 0; k < n; k++)
                    c[k] = k < pos ? DONE : k == pos ? SWP : IDLE;
                steps.add(make(work, c, cmp, swp,
                        "coloca " + val + " en posición " + pos + " desde cubeta " + bi,
                        2, false));
                pos++;
            }
        }

        steps.add(make(work, allColor(n, DONE), cmp, swp,
                "bucketsort completo", 0, true));
        return steps;
    }

    // ── QuickSort ─────────────────────────────────────────────────────────────
    private List<SortStep> quickSort(int[] input) {
        List<SortStep> steps = new ArrayList<>();
        int[] work = copy(input);
        int[] cmp = {0}, swp = {0};
        boolean[] sorted = new boolean[work.length];

        steps.add(make(work, allColor(work.length, IDLE), cmp[0], swp[0],
                "inicio quicksort: eligiendo pivote", 0, false));

        quickHelper(work, 0, work.length - 1, steps, cmp, swp, sorted);

        steps.add(make(work, allColor(work.length, DONE), cmp[0], swp[0],
                "quicksort completo", 0, true));
        return steps;
    }

    private void quickHelper(int[] a, int lo, int hi,
                              List<SortStep> steps, int[] cmp, int[] swp,
                              boolean[] sorted) {
        if (lo >= hi) {
            if (lo == hi) sorted[lo] = true;
            return;
        }

        int pivot = a[hi];
        int i = lo;

        steps.add(make(a, buildColors(a, lo, hi, -1, -1, -1, sorted, hi),
                cmp[0], swp[0],
                "pivote = " + pivot + " rango [" + lo + "," + hi + "]", 0, false));

        for (int j = lo; j < hi; j++) {
            cmp[0]++;
            steps.add(make(a, buildColors(a, lo, hi, j, -1, -1, sorted, hi),
                    cmp[0], swp[0],
                    "comparando " + a[j] + " con pivote " + pivot, 0, false));

            if (a[j] <= pivot) {
                swp[0]++;
                int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
                steps.add(make(a, buildColors(a, lo, hi, -1, i, j, sorted, hi),
                        cmp[0], swp[0],
                        "intercambia " + a[j] + "↔" + a[i], 0, false));
                i++;
            }
        }

        // colocar pivote en posición final
        swp[0]++;
        int tmp = a[i]; a[i] = a[hi]; a[hi] = tmp;
        sorted[i] = true;

        steps.add(make(a, buildColors(a, lo, hi, -1, -1, -1, sorted, -1),
                cmp[0], swp[0],
                "pivote " + a[i] + " en posición final " + i, 0, false));

        // recursión izquierda
        quickHelper(a, lo, i - 1, steps, cmp, swp, sorted);

        // marcar subarray izquierdo completo como ordenado
        for (int k = lo; k < i; k++) sorted[k] = true;

        // paso visual — izquierda en verde antes de procesar derecha
        steps.add(make(a, buildColors(a, i + 1, hi, -1, -1, -1, sorted, -1),
                cmp[0], swp[0],
                "subarray izquierdo ordenado, procesando derecha...", 0, false));

        // recursión derecha
        quickHelper(a, i + 1, hi, steps, cmp, swp, sorted);

        // marcar subarray derecho completo como ordenado
        for (int k = i + 1; k <= hi; k++) sorted[k] = true;
    }

    private String[] buildColors(int[] a, int lo, int hi,
                                  int cmpIdx, int swp1, int swp2,
                                  boolean[] sorted, int pivotIdx) {
        String[] c = new String[a.length];
        for (int k = 0; k < a.length; k++) {
            if      (sorted[k])               c[k] = DONE;  // sorted tiene prioridad
            else if (k < lo || k > hi)        c[k] = OUT;   // luego fuera de rango
            else if (k == pivotIdx)           c[k] = HEAP;
            else if (k == swp1 || k == swp2)  c[k] = SWP;
            else if (k == cmpIdx)             c[k] = CMP;
            else                              c[k] = IDLE;
        }
        return c;
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

    private int[] generateArr(int size) {
        Random rnd = new Random();
        int[] arr = new int[size];
        for (int i = 0; i < arr.length; i++) arr[i] = rnd.nextInt(90) + 5;
        return arr;
    }
}