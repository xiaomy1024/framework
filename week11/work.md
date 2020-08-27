# 架构第11周作业

* 导致系统不可用的原因有哪些？保障系统稳定高可用的方案有哪些？请分别列举并简述。

```
1.网络问题，运营商线路故障、网络设备故障（同城多活架构）
2.机房断电、火灾、水灾等自然灾害（异地多活架构）
3.服务器故障：服务器的磁盘故障（F5/LVS/nginx等均衡软件）
4.黑客攻击：最常见的是黑客通过软件漏洞控制服务器进行破坏或者DDOS攻击(防火墙)
5.应用软件bug、性能不足:应用系统bug导致的对外服务不可用（降级）
6.数据库故障（数据库集群方案）

```

* 请用你熟悉的编程语言写一个用户密码验证函数，Boolean checkPW（String 用户 ID，String 密码明文，String 密码密文）返回密码是否正确 boolean 值，密码加密算法使用你认为合适的加密算法。


```
import java.security.MessageDigest;

/**
 * Created by xiaomengyun on 2020/8/26.
 */
public class LoginUtil {

    private static final String[] hexDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static final String USER_PWD = MD5Encode("123456");

    /**
     * 密码校验
     * @param pwd
     * @return
     */
    public static boolean checkPwd(String pwd){
        if(USER_PWD.equals(MD5Encode(pwd))){
            return true;
        }

        return false;
    }


    /**
     * 同Api项目encode一致
     * @param params
     * @return
     */
    public static String encode(String params){
        //用于加密的字符
        char md5String[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            //使用平台的默认字符集将此 String 编码为 byte序列，并将结果存储到一个新的 byte数组中
            byte[] btInput = params.getBytes();

            //信息摘要是安全的单向哈希函数，它接收任意大小的数据，并输出固定长度的哈希值。
            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            //MessageDigest对象通过使用 update方法处理数据， 使用指定的byte数组更新摘要
            mdInst.update(btInput);

            // 摘要更新之后，通过调用digest（）执行哈希计算，获得密文
            byte[] md = mdInst.digest();

            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {   //  i = 0
                byte byte0 = md[i];  //95
                str[k++] = md5String[byte0 >>> 4 & 0xf];    //    5
                str[k++] = md5String[byte0 & 0xf];   //   F
            }

            //返回经过加密后的字符串
            return new String(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();

        for(int i = 0; i < b.length; ++i) {
            resultSb.append(byteToHexString(b[i]));
        }

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if(b < 0) {
            n = b + 256;
        }

        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;

        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if(charsetname != null && !"".equals(charsetname)) {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
            } else {
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            }
        } catch (Exception var4) {
            ;
        }
        return resultString.toUpperCase();
    }

    public static String MD5Encode(String origin) {
        return MD5Encode(origin,"UTF-8");
    }

    public static void main(String[] args) {
        System.out.println(MD5Encode("123456"));
    }
}


```


