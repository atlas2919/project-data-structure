package com.sort.sort_backend.TDA;

public class Bucketsort {

    public static double[] bucketSort(double[] arr) {
        int n = arr.length;
        if (n <= 1) return arr;

        // Crea los n buckets vacíos usando SinglyLinkedList
        SinglyLinkedList<Double>[] buckets = new SinglyLinkedList[n];
        for (int i = 0; i < n; i++) {
            buckets[i] = new SinglyLinkedList<>();
        }

        // Distribuye cada elemento en su bucket correspondiente
        for (double valor : arr) {
            int indice = (int) (valor * n); 
            insertarOrdenado(buckets[indice], valor);
        }

        // concatena todos los buckets en el arreglo resultado
        int pos = 0;
        for (SinglyLinkedList<Double> bucket : buckets) {
            for (double val : bucket) {    
                arr[pos++] = val;
            }
        }
        return arr;
    }

    //Inserta un valor manteniendo el bucket ordenado insertion sort inline
    private static void insertarOrdenado(SinglyLinkedList<Double> bucket, double valor) {
        // Extraer todo, insertar en posición correcta, recolocar
        SinglyLinkedList<Double> temp = new SinglyLinkedList<>();
        boolean insertado = false;

        while (!bucket.isEmpty()) {
            double actual = bucket.removeFirst();
            if (!insertado && valor <= actual) {
                temp.addLast(valor);
                insertado = true;
            }
            temp.addLast(actual);
        }
        if (!insertado) temp.addLast(valor);

        // Recargar en el bucket original
        while (!temp.isEmpty()) {
            bucket.addLast(temp.removeFirst());
        }
    }
}