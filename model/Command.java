package model;

public class Command {
    private CommandType type;
    private Integer key;
    private String rawValue;
    private Long ttl;
    
    public Command(CommandType type) {
        this.type = type;
    }
    
    public Command(CommandType type, Integer key, String rawValue, Long ttl) {
        this.type = type;
        this.key = key;
        this.rawValue = rawValue;
        this.ttl = ttl;
    }
    
    public CommandType getType() {
        return type;
    }
    
    public void setType(CommandType type) {
        this.type = type;
    }
    
    public Integer getKey() {
        return key;
    }
    
    public void setKey(Integer key) {
        this.key = key;
    }
    
    public String getRawValue() {
        return rawValue;
    }
    
    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }
    
    public Long getTtl() {
        return ttl;
    }
    
    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }
    
    @Override
    public String toString() {
        return "Command{type=" + type + ", key=" + key + 
               ", value='" + rawValue + "', ttl=" + ttl + "}";
    }
}
