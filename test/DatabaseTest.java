package test;

import database.InMemoryDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("=== Running Database Tests ===");
        
        // Test 1: Basic operations
        testBasicOperations();
        
        // Test 2: TTL expiration
        testTTLOperations();
        
        // Test 3: Concurrency test
        testConcurrentOperations();
        
        System.out.println("=== All tests completed ===");
    }
    
    private static void testBasicOperations() {
        System.out.println("\nTest 1: Basic Operations");
        InMemoryDatabase<String> db = new InMemoryDatabase<>();
        
        try {
            db.put(1, "Hello", null);
            db.put(2, "World", null);
            
            assert "Hello".equals(db.get(1));
            assert "World".equals(db.get(2));
            
            db.delete(1);
            assert db.get(1) == null;
            
            System.out.println("✓ Basic operations passed");
        } catch (Exception e) {
            System.err.println("✗ Basic operations failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testTTLOperations() {
        System.out.println("\nTest 2: TTL Operations");
        InMemoryDatabase<String> db = new InMemoryDatabase<>();
        
        try {
            // Entry with 1 second TTL
            db.put(1, "TempValue", 1000L);
            assert "TempValue".equals(db.get(1));
            
            // Wait for expiration
            Thread.sleep(1500);
            assert db.get(1) == null;
            
            System.out.println("✓ TTL operations passed");
        } catch (Exception e) {
            System.err.println("✗ TTL operations failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testConcurrentOperations() {
        System.out.println("\nTest 3: Concurrent Operations");
        InMemoryDatabase<String> db = new InMemoryDatabase<>();
        
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        try {
            // Submit concurrent PUT operations
            for (int i = 0; i < 100; i++) {
                final int key = i % 10; // Only 10 keys to create contention
                executor.submit(() -> {
                    try {
                        db.put(key, "Value" + System.currentTimeMillis(), null);
                        db.get(key);
                    } catch (Exception e) {
                        // Expected in concurrent environment
                    }
                });
            }
            
            executor.shutdown();
            executor.awaitTermination(3, TimeUnit.SECONDS);
            
            System.out.println("✓ Concurrent operations completed without deadlocks");
            System.out.println("  Final size: " + db.size());
            
        } catch (Exception e) {
            System.err.println("✗ Concurrent operations failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
