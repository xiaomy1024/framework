import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xiaomengyun on 2020/7/6.
 */
public class Node {

    public Node(String name,String ip){
        this.name = name;
        this.ip = ip;
    }

    private String name;
    private String ip;
    private AtomicInteger count = new AtomicInteger();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 使用IP当做hash的Key
     *
     * @return String
     */
    @Override
    public String toString() {
        return ip;
    }

    public void increment() {
        count.incrementAndGet();
    }

    public int getCount() {
        return this.count.intValue();
    }
}
