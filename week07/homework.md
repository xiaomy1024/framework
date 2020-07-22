# 性能测试的时候，随着并发的增加，系统响应时间和吞吐量如何变化，为什么？

- 在系统性能范围内，所有请求能够立刻被处理，吞吐量随着并发增加而现行增加，响应时间基本上没有变化
- 当超过系统的处理能力时，由于系统处理不过来，部分请求处于等待状态，响应时间增加，TPS下降
- 当系统负载过大，并发继续增加，大量请求超时，TPS持续下降直到系统崩溃




# 简单压力测试工具

```
import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xiaomengyun on 2020/7/22.
 */
public class TestTools {

    public static final String AVGTIME_KEY = "avgTime";
    public static final String TIME95_KEY = "time95";

    /**
     * 任务加队列
     */
    private static LinkedBlockingQueue workQueue = new LinkedBlockingQueue(1000);
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(10,50,10, TimeUnit.SECONDS,workQueue);

    private  AtomicInteger reqCount;
    private int concurrentCount;
    private  String url;

    //时间记录
    private List<Integer> totalCost = new ArrayList<>();

    /**
     * 初始化
     * @param reqCount
     * @param concurrentCount
     */
    public TestTools(int reqCount,int concurrentCount,String url){
        workQueue = new LinkedBlockingQueue(concurrentCount * 5);
        executor = new ThreadPoolExecutor(concurrentCount,concurrentCount * 2
                ,10, TimeUnit.SECONDS,workQueue);
        this.reqCount = new AtomicInteger(reqCount);
        this.concurrentCount = concurrentCount;
        this.url = url;
    }

    /**
     * 获取执行结果
     * @return
     */
    public Map<String,String> doExecute() throws InterruptedException
    {
        List<Long> timeList = Collections.synchronizedList(new ArrayList<>());
        while(reqCount.intValue() > 0){
            //检查
            AtomicInteger checker = new AtomicInteger(0);

            for(int i = 0;i < this.concurrentCount;i++){
                executor.submit(()->{
                    long start = System.currentTimeMillis();
                    if(doGet()){
                        timeList.add(System.currentTimeMillis() - start);
                    }
                    checker.incrementAndGet();
                });
                //减少
                reqCount.decrementAndGet();
            }

            //检查是否执行完
            while(checker.intValue() != concurrentCount){
                Thread.sleep(10);
            }
        }
        executor.shutdown();
        return calCostTime(timeList);
    }

    /**
     * 计算
     * @param timeList
     * @return
     */
    private Map<String,String> calCostTime(List<Long> timeList){
        Collections.sort(timeList);
        //95%
        int total_95 = (int)(timeList.size() * 0.95);
        long sum_95 = 0;
        long sum = 0;
        for(int i = 0 ;i < timeList.size();i++){
            if(i < total_95){
                sum_95 += timeList.get(i);
            }
            sum += timeList.get(i);
        }
        //
        Map<String,String> result = new HashMap<>();
        result.put(AVGTIME_KEY,new BigDecimal(sum).divide(new BigDecimal(timeList.size()),
                2,BigDecimal.ROUND_DOWN).intValue()+"");
        result.put(TIME95_KEY,new BigDecimal(sum_95).divide(new BigDecimal(total_95),
                2,BigDecimal.ROUND_DOWN).intValue()+"");

        return result;
    }


    /**
     * 远程请求
     * @return
     */
    private boolean  doGet(){
        HttpURLConnection connection = null;
        try{
            URL url = new URL(this.url);
            // 通过远程url连接对象打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接请求方式
            connection.setRequestMethod("GET");
            // 设置连接主机服务器超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取主机服务器返回数据超时时间：60000毫秒
            connection.setReadTimeout(60000);
            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
            connection.setDoOutput(true);
            // 默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
            connection.setDoInput(true);

            if(connection.getResponseCode() == 200){
                //获取返回数据
               InputStream inputStream = connection.getInputStream();
               return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) throws Exception{
        TestTools t = new TestTools(100,10,"https://www.baidu.com/");
        Map<String,String> map = t.doExecute();
        System.out.println("95%耗时："+map.get(TIME95_KEY)+"毫秒");
        System.out.println("平均耗时："+map.get(AVGTIME_KEY)+"毫秒");
    }
}




```