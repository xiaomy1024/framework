import java.util.List;
import java.util.UUID;

/**
 * Created by xiaomengyun on 2020/7/6.
 */
public class Main {

    /**
     * key 的个数
     */
    private static int KEY_NUMS = 1_000_000;

    private static String[] servers = {
            "192.168.1.100",
            "192.168.1.101",
            "192.168.1.102",
            "192.168.1.103",
            "192.168.1.104",
            "192.168.1.105",
            "192.168.1.106",
            "192.168.1.107",
            "192.168.1.108",
            "192.168.1.109"
    };


    public static void main(String[] args) {
        for(int i = 0;i < 5; i++){
            findOptimal();
        }
    }

    /**
     * 最优标准差
     */
    public  static  void findOptimal(){

        double min = Double.MAX_VALUE;
        int virtualCount = -1;

        //虚拟节点格式
        long startTime = System.currentTimeMillis();
        for(int i = 190;i <= 200;i++){
            NodeManager nodeManager = new NodeManager(i);
            for(String server :servers){
                Node serverNode = new Node("Node_"+server,server);
                nodeManager.addNode(serverNode);
            }

            //生成测试数据
            createData(nodeManager);

            //计算标准差
            double value = standardDeviation(nodeManager);
            if(min > value){
                min = value;virtualCount = i;
            }

        }
        //System.out.println("耗时："+((System.currentTimeMillis() - startTime)/1000)+" 秒");
        String result = String.format("150~200个虚拟节点中, 最优标准差:%s, 最优标准差的虚拟节点数:%s",min+"",virtualCount + "");
        System.out.println(result);
    }


    /**
     * 存放数据
     * @param nodeManager
     */
    public static void createData(NodeManager nodeManager){
        for(int i = 0;i < KEY_NUMS;i++){
            String key = UUID.randomUUID().toString();
            nodeManager.getNode(key).increment();
        }
    }

    /**
     * 标准差计算
     * @param nodeManager
     * @return
     */
    public static double standardDeviation(NodeManager nodeManager) {
        /**
         * 计算平均数 = 100w 个 key / 10 台服务器 = 10w
         */
        double avg = KEY_NUMS/servers.length;

        //获取所有服务器
        List<Node> serverList = nodeManager.getServerList();

        /**
         * 计算标准差
         */
        int nsum = 0;
        for (Node server:serverList) {
            int d = (int)Math.abs(server.getCount() - avg);
            int x = (int)Math.pow(d, 2);
            nsum += x;
        }

        //方差
        double s = (double)nsum/(double)servers.length;
        //标准差
        double sd = Math.sqrt(s);
        return sd;
    }
}
