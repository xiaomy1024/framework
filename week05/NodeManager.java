import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by xiaomengyun on 2020/7/6.
 */
public class NodeManager {

    private SortedMap<Long,Node> serverMap = new TreeMap<Long,Node>();
    //虚拟节点数
    private int numberOfReplicas;
    //服务器列表
    private List<Node> serverList = new ArrayList<>();

    public NodeManager(int numberOfReplicas){
        this.numberOfReplicas = numberOfReplicas;
    }

    /**
     * 添加节点
     * @param server
     */
    public void  addNode(Node server){
        serverList.add(server);
        for(int i = 1;i <= numberOfReplicas;i++){
            String key = server.toString() + "@" + leftPad(i,3);
            serverMap.put(hash(key),server);
        }
    }

    /**
     * 获取节点
     * @param key
     * @return
     */
    public Node getNode(String key){
        long hash = hash(key);
        if(!serverMap.containsKey(hash)){
            SortedMap<Long,Node> tailMap = serverMap.tailMap(hash);
            hash = tailMap.isEmpty()?serverMap.firstKey():tailMap.firstKey();
        }
        return serverMap.get(hash);
    }


    /**
     * 删除真实机器节点
     * @param server T
     */
    public void remove(Node server) {
        for (int i = 0; i < this.numberOfReplicas; i++) {
            String key = server.toString() + "@" + leftPad(i,3);
            Node node = serverMap.remove(hash(key));
            serverList.remove(node);
        }
    }

    /**
     * 填充
     * @param num
     * @param len
     * @return
     */
    private String leftPad(int num,int len){
        String value = num + "";
        int count = len - value.length();
        for(int i = 0;i < count;i++){
            value = "0" + value;
        }
        return value;
    }


    //使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
    /**
     * MurMurHash算法,性能高,碰撞率低
     *
     * @param key String
     * @return Long
     */
    public Long hash(String key) {
        ByteBuffer buf = ByteBuffer.wrap(key.getBytes());
        int seed = 0x1234ABCD;

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;

    }

    public List<Node> getServerList() {
        return serverList;
    }
}
