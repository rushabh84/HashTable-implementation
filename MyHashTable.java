/**
 * 08-722 Data Structures for Application Programmers.
 *
 * Homework Assignment 4
 * HashTable Implementation with linear probing
 *
 * Andrew ID: rushabhs
 * @author
 */
public class MyHashTable implements MyHTInterface {
    /**
     * hashArray is an array of type DataItem to store elements.
     */
    private DataItem[] hashArray;

    /**
     * DEFAULT_CAPACITY is the default capacity assigned to hashArray.
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * DELETED is a static final variable to track DELETED elements in DataItem.
     */
    private static final DataItem DELETED = new DataItem("#DEL#", -1);

    /**
     * loadFactor is to track the load factor.
     */
    private double loadFactor;

    /**
     * elements is to track the number of elements present in the hashArray.
     */
    private int elements;

    /**
     * capacity is to track the size of the hashArray.
     */
    private int capacity;

    /**
     * collisions is to track the number of collisions while inserting data.
     */
    private int collisions;

    // TODO implement constructor with no initial capacity

    /**
     * MyHashTable is the constructor to initialize the hashArray with the DEFAULT_CAPACITY.
     * elements is set to 0 and capacity is set to DEFAULT_CAPACITY
     * worst-case running Time complexity: O(1)
     */
    public MyHashTable() {
        hashArray = new DataItem[DEFAULT_CAPACITY];
        capacity = DEFAULT_CAPACITY;
        elements = 0;
    }

    // TODO implement constructor with initial capacity
    /**
     * MyHashTable is the constructor to initialize the hashArray with the initialCapacity.
     * elements is set to 0 and capacity is set to initialCapacity
     * @param initialCapacity is the initial capacity to be assigned to the hashArray
     * worst-case running Time complexity: O(1)
     */
    public MyHashTable(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new RuntimeException("Unsufficient Capacity");
        } else {
            hashArray = new DataItem[initialCapacity];
            capacity = initialCapacity;
            elements = 0;
        }
    }

    // TODO implement required methods

    /**
     * Instead of using String's hashCode, you are to implement your own here.
     * You need to take the table length into your account in this method.
     *
     * In other words, you are to combine the following two steps into one step.
     * 1. converting Object into integer value
     * 2. compress into the table using modular hashing (division method)
     *
     * Helper method to hash a string for English lowercase alphabet and blank,
     * we have 27 total. But, you can assume that blank will not be added into
     * your table. Refer to the instructions for the definition of words.
     *
     * For example, "cats" : 3*27^3 + 1*27^2 + 20*27^1 + 19*27^0 = 60,337
     *
     * But, to make the hash process faster, Horner's method should be applied as follows;
     *
     * var4*n^4 + var3*n^3 + var2*n^2 + var1*n^1 + var0*n^0 can be rewritten as
     * (((var4*n + var3)*n + var2)*n + var1)*n + var0
     *
     * Note: You must use 27 for this homework.
     *
     * However, if you have time, I would encourage you to try with other
     * constant values than 27 and compare the results but it is not required.
     * @param input input string for which the hash value needs to be calculated
     * @return int hash value of the input string
     */
    private int hashFunc(String input) {
        // TODO implement this
      int total = 0;
        for (int i = 0; i <= input.length() - 1; i++) {
            int in = input.charAt(i);
            total = ((total * 27) + (in - 96)) % capacity;
        }
        return total;
    }

    /**
     * doubles array length and rehash items whenever the load factor is reached.
     */
    private void rehash() {
        // TODO implement this
        int oldCapacity = hashArray.length;  //capacity
        capacity = oldCapacity * 2;
        if (!isPrime(capacity)) {
            capacity = nextPrime(capacity);
        }
        System.out.println("Rehashing " + elements + " items, new length is " + capacity);
        elements = 0;  //reset elements
        collisions = 0;  //reset collisions
        DataItem[] temp = hashArray;
        hashArray = new DataItem[capacity];
        for (int i = 0; i < oldCapacity; i++) {
            if (temp[i] != DELETED && temp[i] != null) {
                insert(temp[i].value);
            }
        }

    }


    /**
     * private static data item nested class.
     */
    private static class DataItem {
        /**
         * String value.
         */
        private String value;

        /**
         * String value's frequency.
         */
        private int frequency;

        // TODO implement constructor and methods

        /**
         * DataItem is the constructor to initialize the member variables.
         * @param v is the value to assigned to the DataItem object
         * @param f is the frequency of the string
         * worst-case running Time complexity: O(1)
         */
        DataItem(String v, int f) {
            value = v;
            frequency = f;
        }
    }

    @Override
    public void insert(String value) {
        // TODO Auto-generated method stub
//        loadFactor = (double) elements / capacity;
//        if (loadFactor > 0.5) {
//            rehash();
//        }
        boolean isvalid = validate(value);
        if (isvalid) {
            int hashIndex = hashValue(value);
            int dummy = hashIndex;
            if (contains(value)) {
                //if hashArray contains key, we need to find it's position and increase its frequency
                while (!hashArray[hashIndex].value.equals(value)) {
                    hashIndex++;
                    hashIndex = hashIndex % hashArray.length;
                }
                hashArray[hashIndex].frequency++;
            } else {
                boolean collide = true;
                while (hashArray[hashIndex] != null && hashArray[hashIndex] != DELETED) {
                    int currDummy = hashValue(hashArray[hashIndex].value);
                    if (currDummy == dummy && collide) {
                        collisions += 1;
                        collide = false;
                    }
                    hashIndex++;
                    hashIndex = hashIndex % hashArray.length;
                }
                hashArray[hashIndex] = new DataItem(value, 1);
                elements++;
            }
            loadFactor = (double) elements / capacity;
            if (loadFactor > 0.5) {
                rehash();
            }
        } else {
            return;
        }
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return elements;
    }

    @Override
    public void display() {
        // TODO Auto-generated method stub
        if (elements == 0) {
            return;
        }
        for (int i = 0; i < hashArray.length; i++) {
            if (hashArray[i] == DELETED) {
                System.out.print(" " + hashArray[i].value);
            } else if (hashArray[i] == null) {
                System.out.print(" **");
            } else {
                System.out.print(" [" + hashArray[i].value + ", " + hashArray[i].frequency + "]");
            }
        }
        System.out.println("");

    }

    @Override
    public boolean contains(String key) {
        // TODO Auto-generated method stub
        int hash = hashValue(key);
        while (hashArray[hash] != null) {
            if (hashArray[hash].value.equals(key)) {
                return true;
            }
            hash++;
            hash = hash % hashArray.length;
        }
        return false;
    }

    @Override
    public int numOfCollisions() {
        // TODO Auto-generated method stub
        return collisions;
    }

    @Override
    public int hashValue(String value) {
        // TODO Auto-generated method stub
        if (value == "") {
            return 0;
        }
        int hashval = hashFunc(value);
        return hashval;
    }

    @Override
    public int showFrequency(String key) {
        // TODO Auto-generated method stub
        if (key == null) {
            return 0;
        }
        int hashIndex = hashValue(key);
        if (hashIndex < 0) {
            return 0;
        }
        while (hashArray[hashIndex] != null) {
            if (hashArray[hashIndex].value.equals(key)) {
                return hashArray[hashIndex].frequency;
            }
            hashIndex++;
            hashIndex = hashIndex % hashArray.length;
        }
        return 0;
    }

    @Override
    public String remove(String key) {
        // TODO Auto-generated method stub
        if (key == null) {
            return "";
        }
        int hashIndex = hashValue(key);
        while (hashArray[hashIndex] != null) {
            if (hashArray[hashIndex].value.equals(key)) {
                String result = hashArray[hashIndex].value;
                hashArray[hashIndex] = DELETED;
                elements--;
                return result;
            }
            hashIndex++;
            hashIndex = hashIndex % hashArray.length;
        }
        return "";
    }

    /**
     * nextPrime method is a helper method to find the immediate prime number to new capacity.
     * worst case Time complexity: O(1)
     * @param ncapacity the new capacity of the hashArray
     * @return p
     */
    private int nextPrime(int ncapacity) {
        if (ncapacity <= 1) {
            return 2;
        }
        if (ncapacity == 2) {
            return 2;
        }
        int p = ncapacity;
        boolean b;
        while (true) {
            b = true;
            p = p + 1;
            for (int i = 2; i <= p / 2; i++) {
                if (p % i == 0) {
                    b = false;
                }
            }
            if (b) {
                return p;
            }
        }
    }

    /**
     * isPrime method is a helper method to check if the new capacity is prime number.
     * worst case Time complexity: O(1)
     * @param ncapacity the new capacity of the hashArray
     * @return boolean value
     */
    private boolean isPrime(int ncapacity) {
        if (ncapacity <= 1) {
            return false;
        }
        for (int i = 2; i <= ncapacity / 2; i++) {
            if (ncapacity % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * validate method is a helper method to check if a string is a valid string.
     * worst case Time complexity: O(1)
     * @param text is the string which needs to be validated
     * @return check
     */
    private boolean validate(String text) {
        if (text == null) {
            return false;
        }
        boolean check = text.matches("[a-zA-Z]+");
        return check;
    }
}
