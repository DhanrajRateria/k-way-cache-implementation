package cache;
public class CacheBlock {
    private int key;
    private int data;
    private boolean valid;

    public CacheBlock(){
        this.valid = false;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key){
        this.key = key;
    }

    public int getData(){
        return data;
    }

    public int setData(int data){
        this.data = data;
    }

    public boolean isValid(){
        return valid;
    }

    public void setValid(boolean valid){
        this.valid = valid;
    }
}