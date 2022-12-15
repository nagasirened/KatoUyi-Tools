package test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestA {

    public static void main(String[] args) {
        System.out.println(Thread.interrupted());
        Thread.currentThread().interrupt();
        System.out.println(Thread.currentThread().isInterrupted());
        try {
            TimeUnit.MINUTES.sleep( 1 );
        } catch ( InterruptedException e ) {
            System.out.println("inter");
        }
    }

}
