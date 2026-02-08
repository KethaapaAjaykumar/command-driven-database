package database;

import model.Entry;
import exceptions.DatabaseStoppedException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDatabase<T> {
    private final ConcurrentHashMap<Integer, Entry<T>> store;
    private volatile boolean running;
    private final Thread cleanupThread;
    
    public InMemoryDatabase() {
        this.store = new ConcurrentHashMap<>();
        this.running = true;
        this.cleanupThread = new Thread(this::cleanupExpiredKeys);
        this.cleanupThread.setDaemon(true);
        this.cleanupThread.start();
    }
    
    public void put(Integer key, T value, Long ttl) throws DatabaseStoppedException {
        if (!running) {
            throw new DatabaseStoppedException("Database is stopped");
        }
        
        long expiryTime = (ttl != null) ? 
            System.currentTimeMillis() + ttl : -1;
        
        Entry<T> entry = new Entry<>(value, expiryTime);
        store.put(key, entry);
        System.out.println("PUT successful for key: " + key);
    }
    
    public T get(Integer key) throws DatabaseStoppedException {
        if (!running) {
            throw new DatabaseStoppedException("Database is stopped");
        }
        
        Entry<T> entry = store.get(key);
        
        if (entry == null) {
            System.out.println("GET: Key " + key + " not found");
            return null;
        }
        
        // Lazy expiration on GET
        if (entry.isExpired()) {
            store.remove(key);
            System.out.println("GET: Key " + key + " expired and removed");
            return null;
        }
        
        System.out.println("GET successful for key: " + key);
        return entry.getValue();
    }
    
    public boolean delete(Integer key) throws DatabaseStoppedException {
        if (!running) {
            throw new DatabaseStoppedException("Database is stopped");
        }
        
        boolean removed = store.remove(key) != null;
        System.out.println("DELETE " + (removed ? "successful" : "failed - key not found") + 
                          " for key: " + key);
        return removed;
    }
    
    public void stop() {
        running = false;
        System.out.println("Database stopped");
    }
    
    public void start() {
        running = true;
        System.out.println("Database started");
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public void shutdown() {
        running = false;
        cleanupThread.interrupt();
    }
    
    private void cleanupExpiredKeys() {
        while (running) {
            try {
                Thread.sleep(1000);
                
                if (!running) break;
                
                Iterator<Map.Entry<Integer, Entry<T>>> iterator = store.entrySet().iterator();
                int expiredCount = 0;
                
                while (iterator.hasNext()) {
                    Map.Entry<Integer, Entry<T>> mapEntry = iterator.next();
                    Entry<T> entry = mapEntry.getValue();
                    
                    if (entry.isExpired()) {
                        iterator.remove();
                        expiredCount++;
                    }
                }
                
                if (expiredCount > 0) {
                    System.out.println("Background cleanup removed " + expiredCount + " expired entries");
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error in cleanup thread: " + e.getMessage());
            }
        }
    }
    
    public int size() {
        return store.size();
    }
}
