import java.util.Iterator;

public class test {

    private static class Node<T>{
        T value;
        Node<T> next;
        Node <T> prev;

        public Node(T value, Node<T> prev) {
            this.value = value;
            this.prev = prev;
            next = null;
        }
        public Node() {
            this.value = null;
            this.prev = null;
            next = null;
        }
    }
    static class MyLinkedList<T> implements Iterable<T>{
        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                Node<T> current = start;
                @Override
                public boolean hasNext() {
                    return start != current.next ;
                }

                @Override
                public T next() {
                    current = current.next;
                    return current.value;
                }

            };
        }


        private Node<T> start;
        private Node<T> last;
        private int size;

        public MyLinkedList()
        {
            start = new Node<>();
            last = start;
            start.next = last;
            start.prev = last;


            size = 0;
        }

        public Node<T> add(T value)
        {
            // add the new node, and set its previous to last
            Node<T> node = new Node<>(value, last);
            //set the last node next to the new node
            last.next = node;
            // make the new node the last node
            last = node;
            last.next = start;
            start.prev = last;


            size++;
            return last;
        }

        public void remove(Node<T> node)
        {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }

        public int size(){
            return size;
        }



    }
    public static void main(String[] args) {

        MyLinkedList<Integer> m = new MyLinkedList<>();
        Node<Integer> l1 =  m.add(1);
        Node<Integer> l2 =  m.add(2);
        Node<Integer> l3 =  m.add(3);

        System.out.println("Size: "+m.size());
        for(Integer i: m)
        {
            System.out.println(i);
        }

        m.remove(l1);
        System.out.println("remove 1, size: " + m.size);
        for(Integer i: m)
        {
            System.out.println(i);
        }

        m.remove(l2);
        System.out.println("remove 2, size: " +m.size);
        for(Integer i: m)
        {
            System.out.println(i);
        }

        m.remove(l3);
        System.out.println("remove 3, size: " +m.size);
        for(Integer i: m)
        {
            System.out.println(i);
        }
    }
}
