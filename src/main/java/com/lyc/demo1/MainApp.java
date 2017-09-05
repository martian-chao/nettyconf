package com.lyc.demo1;


import com.lyc.demo1.vo.MyCommConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by yanChaoLiu on 2017/9/1.
 */
public class MainApp {
    private static Logger logger = LoggerFactory.getLogger(MainApp.class);
    // 同一台应用服务器中的服务编号，初始值为-1，代表还没有加载配置文件中的参数
    public static int payServerId = -1;

//    InputStream in = Object.getClass().getResourceAsStream("netty/app/a.properties");
    public static void main(String[] args) {

        try {
//            initMyCommConfig("a.properties");
//            System.out.println("-----------");
//            start();



           /* System.out.println("-----------");
            String localServerPort= MyCommConfig.localServerPort;
//        int localServerPort=Integer.parseInt(MyCommConfig.localServerPort);
            String localServerAcceptIp=MyCommConfig.localServerAcceptIp;
            String localServerRefuseIp = MyCommConfig.localServerRefuseIp;
//        HelloServer server = new HelloServer();
            System.out.println(localServerAcceptIp+"--"+localServerPort+"--"+localServerRefuseIp);

            System.out.println("本地服务器接受地址:"+MyCommConfig.localServerAcceptIp);
            System.out.println("下一机器服务器Ip+端口:"+MyCommConfig.nextServerIpPort);*/




        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * //初始化MyCommConfig数据
     */
    public static void initMyCommConfig(String cfgName){
        Properties pro =null;
        FileInputStream in=null;
        try {
            //读取配置文件
           pro=new Properties();
           in= new FileInputStream(cfgName);
            pro.load(in);

            Iterator<String> it = pro.stringPropertyNames().iterator();
            //配置文件数据的临时存储容器
            Map parameters = Collections.synchronizedMap(new HashMap());
            while(it.hasNext())
            {
                String keyStr = it.next();
                parameters.put(keyStr, pro.getProperty(keyStr));
            }
            //初始化MyCommConfig数据
            MyCommConfig.refresh(parameters);


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    in=null;
                }
            }
        }


    }

    /**
     * 启动服务器
     */
    public static void start(){
        //本机服务器的端口号和回连客户端端口号数组
        String[] localPortGroups = MyCommConfig.localServerPort.split("，");
//        String[] clientPortGroups = MyCommConfig.lastClientPort.split("，");
        for (int i = 0; i < localPortGroups.length; i++) {
            int port=Integer.parseInt(localPortGroups[i].split(",")[i]);
            System.out.println(localPortGroups[i].split(",")[i]+"!!");

            try {
                //启动服务器
                new HelloServer().bind(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //启动客户端
       /* for (int i=0;i< clientPortGroups.length;i++){
            int port=Integer.parseInt(clientPortGroups[i].split(",")[i]);

            String localServerAcceptIp = MyCommConfig.localServerAcceptIp;
            String[] localServerAcceptIps = localServerAcceptIp.split(";");
            //取第一个
            localServerAcceptIp = localServerAcceptIps[i];

            try {
                new HelloClient().connect(localServerAcceptIp,port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        //Get group idx
        /*for(int i=0;i<localPortGroups.length;i++)
        {
            try
            {
                srvSocket = new ServerSocket(Integer.valueOf(localPortGroups[i].split(",")[0]).intValue());
                groupIdx = i;
                AppServiceRun.payServerId = groupIdx + 1;
                break;
            }
            catch(Exception e)
            {
                srvSocket = null;
            }
        }

        if(srvSocket == null)
        {
            logger.error("未能获取本机服务端口号。");
            return;
        }

        String type = MyCommConfig.type;

        startMonitor(type);

        if(type.equalsIgnoreCase("as") || type.equalsIgnoreCase("r"))
        {
            String[] localClientPorts = localClientPortGroups[groupIdx].split(",");
            String[] nextIpPorts = nextIpPortGroups[groupIdx].split(",");
            for(int i=0;i<localClientPorts.length;i++)
            {
                String[] ipPort = nextIpPorts[i].split(":");
                ClientServer clientServer = new ClientServer();
                clientServer.start(this, type, localClientPorts[i], ipPort[0], ipPort[1]);
            }

            try
            {
                Thread.sleep(500);
            }
            catch(Exception e)
            {
            }


            ClientClient client = new ClientClient();
            client.start(this, type, nextIpPortGroups[groupIdx], MyCommConfig.maxClientSendConnCountPerPort);

        }

        if(type.equalsIgnoreCase("as") || type.equalsIgnoreCase("s") || type.equalsIgnoreCase("r"))
        {
            String[] localPorts = localPortGroups[groupIdx].split(",");
            String[] clientPorts = clientPortGroups[groupIdx].split(",");

            for(int i=0;i<localPorts.length;i++)
            {
                Server server = new Server();

                if(i == 0)
                {
                    server.start(this, srvSocket, type, Integer.valueOf(localPorts[i]).intValue(), Integer.valueOf(clientPorts[i]).intValue());
                }
                else
                {
                    server.start(this, null, type, Integer.valueOf(localPorts[i]).intValue(), Integer.valueOf(clientPorts[i]).intValue());
                }
            }
        }*/

    }


}
