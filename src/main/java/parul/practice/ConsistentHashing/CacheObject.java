package parul.practice.ConsistentHashing;

public class CacheObject {
    private String key;
    public CacheObject(String k){
        key = k;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "CacheObject{" +
                "key='" + key + '\'' +
                '}';
    }
}
