package model;

public class Entry<T> {
    private T value;
    private long expiryTime; // -1 = no expiry
    
    public Entry(T value, long expiryTime) {
        this.value = value;
        this.expiryTime = expiryTime;
    }
    
    public T getValue() {
        return value;
    }
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public long getExpiryTime() {
        return expiryTime;
    }
    
    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }
    
    public boolean isExpired() {
        return expiryTime != -1 && System.currentTimeMillis() > expiryTime;
    }
}
