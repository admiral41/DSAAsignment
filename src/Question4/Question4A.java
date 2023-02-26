package Question4;//LFU Caching-->LFU caching (Least Frequently Used caching) is a caching algorithm
// used in computer science to manage the contents of a cache.
// The main idea behind LFU caching is to discard the least frequently
// used items in the cache when the cache becomes full and new items need to be added.
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


//Design and Implement LFU caching
class Question4A {

    private Map<Integer, Node> valueMap = new HashMap<>();
    private Map<Integer, Integer> countMap = new HashMap<>();
    private TreeMap<Integer, DoubleLinkedList> frequencyMap = new TreeMap<>();
    private final int size;

    public Question4A(int n) {
        size = n;
    }

    public int get(int key) {
        if (!valueMap.containsKey(key) || size == 0) {
            return -1;
        }

        // Remove element from current frequency list and move it to the next one in O(1) time
        Node nodeTodelete = valueMap.get(key);
        Node node = new Node(key, nodeTodelete.value());
        int frequency = countMap.get(key);
        frequencyMap.get(frequency).remove(nodeTodelete);
        removeIfListEmpty(frequency);
        valueMap.remove(key);
        countMap.remove(key);
        valueMap.put(key, node);
        countMap.put(key, frequency + 1);
        frequencyMap.computeIfAbsent(frequency + 1, k -> new DoubleLinkedList()).add(node);
        return valueMap.get(key).value;
    }

    public void put(int key, int value) {
        if (!valueMap.containsKey(key) && size > 0){

            Node node = new Node(key, value);

            if (valueMap.size() == size) {
                // remove the first node(LRU) from the list the of smallest frequency(LFU)
                int lowestCount = frequencyMap.firstKey();
                Node nodeTodelete = frequencyMap.get(lowestCount).head();
                frequencyMap.get(lowestCount).remove(nodeTodelete);
                removeIfListEmpty(lowestCount);

                int keyToDelete = nodeTodelete.key();
                valueMap.remove(keyToDelete);
                countMap.remove(keyToDelete);
            }
            frequencyMap.computeIfAbsent(1, k -> new DoubleLinkedList()).add(node);
            valueMap.put(key, node);
            countMap.put(key, 1);

        }
        else if(size > 0){
            Node node = new Node(key, value);
            Node nodeTodelete = valueMap.get(key);
            int frequency = countMap.get(key);
            frequencyMap.get(frequency).remove(nodeTodelete);
            removeIfListEmpty(frequency);
            valueMap.remove(key);
            countMap.remove(key);
            valueMap.put(key, node);
            countMap.put(key, frequency + 1);
            frequencyMap.computeIfAbsent(frequency + 1, k -> new DoubleLinkedList()).add(node);


        }
    }

    private void removeIfListEmpty(int frequency) {
        // remove from map if list is empty
        if (frequencyMap.get(frequency).len() == 0) {
            frequencyMap.remove(frequency);
        }
    }

    private class Node {
        private int key;
        private int value;
        Node next;
        Node prev;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }

        public int key() {
            return key;
        }

        public int value() {
            return value;
        }
    }

    private class DoubleLinkedList {
        private int n;
        private Node head;
        private Node tail;

        public void add(Node node) {
            if (head == null) {
                head = node;
            } else {
                tail.next = node;
                node.prev = tail;
            }
            tail = node;
            n++;
        }

        public void remove(Node node) {

            if (node.next == null) tail = node.prev;
            else node.next.prev = node.prev;

            if (node.prev == null) head = node.next;
            else node.prev.next = node.next;

            n--;
        }

        public Node head() {
            return head;
        }

        public int len() {
            return n;
        }
    }
    public static void main(String[] args) {
        Question4A cache = new Question4A(2); // create a cache with capacity of 2

        // add elements to cache
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1));    // returns 1
        cache.put(3, 3);                     // evicts key 2
        System.out.println(cache.get(2));    // returns -1 (not found)
        System.out.println(cache.get(3));    // returns 3
        cache.put(4, 4);                     // evicts key 1
        System.out.println(cache.get(1));    // returns -1 (not found)
        System.out.println(cache.get(3));    // returns 3
        System.out.println(cache.get(4));    // returns 4
    }

}
