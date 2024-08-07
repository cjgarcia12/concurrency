package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class SharedBuffer {
    private final Queue<Integer> buffer;
    private final int maxSize;

    public SharedBuffer(int maxSize) {
        this.buffer = new LinkedList<>();
        this.maxSize = maxSize;
    }

    public synchronized void add(int value) throws InterruptedException {
        while (buffer.size() == maxSize) {
            wait();
        }
        buffer.add(value);
        notifyAll();
    }

    public synchronized int remove() throws InterruptedException {
        while (buffer.isEmpty()) {
            wait();
        }
        int value = buffer.poll();
        notifyAll();
        return value;
    }
}

class Producer implements Runnable {
    private final SharedBuffer buffer;
    private final Random random;

    public Producer(SharedBuffer buffer) {
        this.buffer = buffer;
        this.random = new Random();
    }

    @Override
    public void run() {
        try {
            while (true) {
                int number = random.nextInt(100);
                buffer.add(number);
                System.out.println("Produced: " + number);
                Thread.sleep(500); // Simulate time taken to produce a number
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    private final SharedBuffer buffer;
    private int sum;

    public Consumer(SharedBuffer buffer) {
        this.buffer = buffer;
        this.sum = 0;
    }

    @Override
    public void run() {
        try {
            while (true) {
                int number = buffer.remove();
                sum += number;
                System.out.println("Consumed: " + number + ", Sum: " + sum);
                Thread.sleep(1000); // Simulate time taken to consume a number
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getSum() {
        return sum;
    }
}

public class Main {
    public static void main(String[] args) {
        SharedBuffer buffer = new SharedBuffer(10);

        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);

        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);

        producerThread.start();
        consumerThread.start();
    }
}
