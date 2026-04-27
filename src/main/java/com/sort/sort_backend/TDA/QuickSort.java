package com.sort.sort_backend.TDA;

import java.util.Comparator;

public class QuickSort {

    /* Ordenar una Pila */
    public static <K> void quickSort(Queue<K> queue, Comparator<K> comp) {
        int n = queue.size();

        if (n < 2) return; // trivialmente ordenado
            
        // divide
        K pivot = queue.first(); // utilizaremos al primer elemento como pivote

        System.out.println("Pivot: " + pivot.toString());

        //dividimos la pila en Less, Equal y Greater
        Queue<K> Less = new LinkedQueue<>();
        Queue<K> Equal = new LinkedQueue<>();
        Queue<K> Greater = new LinkedQueue<>();

        while (!queue.isEmpty()) { 
            K element = queue.dequeue();
            int c = comp.compare(element, pivot);
                
            if (c < 0) {
                Less.enqueue(element); //element es menor al pivote
            }                             
            else if (c == 0) {
                Equal.enqueue(element); //element es igual al pivote
            }             
            else {
                Greater.enqueue(element); //element es mayor al pivote
            } 
        }

        // conquer
        quickSort(Less, comp);      //ordenar los elementos menores al pivote
        quickSort(Greater, comp);   //ordenar los elementos mayores al pivote

        // concatenar resultados
        while (!Less.isEmpty()) {
            queue.enqueue(Less.dequeue());
        }
        while (!Equal.isEmpty()) {
            queue.enqueue(Equal.dequeue());
        }
        while (!Greater.isEmpty()) {
            queue.enqueue(Greater.dequeue());
        }
    }

  //-------Ordenar arrays "in-place"---------
    public static <K> void quickSortInPlace(K[] S, Comparator<K> comp) {
        quickSortInPlace(S, comp, 0, S.length - 1);
    }

    /* ordenar un sub-array [a...b] incluyendo bordes*/
    //a, b son punteros


    private static <K> void quickSortInPlace(K[] arr, Comparator<K> comp, int a, int b) {
        if (a >= b) return; //ordenado trivialmente
       
        System.out.println("Entering qs");        


        int left = a;
        System.out.println("left: " + left);
        int right = b - 1;
        System.out.println("right: " + right);
        K pivot = arr[b];   //eleccion arbitraria
        System.out.println("pivot: " + pivot.toString());
        K temp;             // objeto temporario para intercambiar valores
        
        while (left <= right) {
            System.out.println("Entering big loop");
            System.out.println("left: " + left);
            System.out.println("right: " + right);

            //escanear hasta llegar a un valor igual o mayor al pivote (o el marcador derecho) 
            while (left <= right && comp.compare(arr[left], pivot) < 0) {
                left++;
            }
            
            //escanear hasta llegar a un valor igual o menor al pivote (o el marcador izquierdo) 
            while (left <= right && comp.compare(arr[right], pivot) > 0) {
                right--;
            }

            if (left <= right) { // los indices no cruzaron estrictamente
                // asi que se intercambian los valores y se disminuye el rango
                temp = arr[left];
                arr[left] = arr[right];
                arr[right] = temp;

                left++;
                right--;
            }
        }

        // poner al pivote en su sitio final (marcado actualmente por el marcador izquierdo)
        temp = arr[left];
        arr[left] = arr[b];
        arr[b] = temp;

        // llamadas recursivas
        quickSortInPlace(arr, comp, a, left - 1);
        quickSortInPlace(arr, comp, left + 1, b);
    }
}