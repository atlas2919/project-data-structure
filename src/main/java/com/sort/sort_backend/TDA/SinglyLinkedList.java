package com.sort.sort_backend.TDA;

import java.util.Iterator;

public class SinglyLinkedList<E> implements Iterable<E> {
    // clase interna Node, difiere de clases nodo en otras estructuras
    protected static class Node <E>{
        private E element;
        private Node <E> next;
        public Node(E e, Node<E> n){
            element=e;
            next=n;
        }

        
    
        //metodos de la clase Node
        public E getElement(){
        return element;
        }

        public Node<E> getNext(){
        return next;
        }

        public void setNext(Node<E> n){
        next =n;
        }
    }
        // fin de clase nodo
    
    //Variable de la clase SinlgyLinkedList
    private Node<E> head = null;
    private Node<E> tail = null;
    private  int size = 0;
    
    //constructor de lista vacia  
    public SinglyLinkedList(){}

    //METODOS DE ACESO
    public int size(){
        return size;
    }
    public boolean isEmpty(){
        return size==0;
    }
    //retorna el primero sin eliminarlo
    public E first(){
        if (isEmpty()) return null;
        return head.getElement();
    }
    //retorna el ultimo sin eliminarlo
    public E last(){
        if(isEmpty()) return null;
        return tail.getElement();
    }


    //METODOS DE ACTUALIZACION DE DATOS
    public void addFirst(E e){
        head= new Node<>(e,head); //agarra el elemento(objeto) y lo pone al inicio creando la referecia nueva al nodo previo
        if(size==0)
            tail=head;
        size++;
    }

    public void addLast(E e){
        Node<E> newest= new Node<>(e,null);
        if(isEmpty())
            head =newest;
        else 
            tail.setNext(newest);
        tail=newest;
        size++;
    }

    public E removeFirst(){
        if(isEmpty()) return null;
        E answer = head.getElement();
        head= head.getNext();
        size--;
        if(size==0)
            tail=null;
        return answer;
    }

    @Override
    public Iterator<E> iterator() {
        return new SinglyLinkedListIterator();
    }

    private class SinglyLinkedListIterator implements Iterator<E> {
        private Node<E> current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            if (!hasNext()) throw new java.util.NoSuchElementException();
            E element = current.getElement();
            current = current.getNext();
            return element;
        }
    }
}
