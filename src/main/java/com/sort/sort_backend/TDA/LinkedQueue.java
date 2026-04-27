package com.sort.sort_backend.TDA;

public class LinkedQueue<E> extends SinglyLinkedList<E> implements Queue<E> {

    public LinkedQueue() {
        super(); // lista inicialmente vacía
    }

    @Override
    public void enqueue(E element) {
        addLast(element);   // entra por el final
    }

    @Override
    public E first() {
        return super.first(); // mira el primero
    }

    @Override
    public E dequeue() {
        return removeFirst(); // sale por el frente
    }
}
