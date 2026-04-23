package com.sort.sort_backend;

public class SortStep {
    public int[]    arr;
    public String[] colors;
    public int      cmp;
    public int      swp;
    public String   msg;
    public int      phase;
    public boolean  done;

    public SortStep(int[] arr, String[] colors,
                    int cmp, int swp, String msg,
                    int phase, boolean done) {
        this.arr    = arr.clone();
        this.colors = colors.clone();
        this.cmp    = cmp;
        this.swp    = swp;
        this.msg    = msg;
        this.phase  = phase;
        this.done   = done;
    }
}
