package com.lyc.protocol;

import com.lyc.protocol.thread.ConfigManager;
import com.lyc.protocol.vo.MyCommConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yanChaoLiu on 2017/9/1.
 */
public class MainApp {
    private static Logger logger = LoggerFactory.getLogger(MainApp.class);
    private ThreadPoolExecutor executor =
            new ThreadPoolExecutor(1, 1, 60, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
//    InputStream in = Object.getClass().getResourceAsStream("netty/app/conf.properties");
    public static void main(String[] args) {

        try {
            ConfigManager configManager = new ConfigManager();
            configManager.start("conf/conf.properties");
            Thread.sleep(500);

            new MainApp().serverStart();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 启动服务器
     */
    public  void serverStart(){
        //本机服务器的端口号和回连客户端端口号数组
        System.out.println(MyCommConfig.localServerPort+"本地端口！");
        String[] localPortGroups = MyCommConfig.localServerPort.split(",");
//        String[] clientPortGroups = MyCommConfig.lastClientPort.split("，");
        System.out.println(Arrays.toString(localPortGroups)+"--------长度："+localPortGroups.length);
        for (int i = 0; i < localPortGroups.length; i++) {
            System.out.println(localPortGroups[i]+"!!!!!");
            int port=Integer.parseInt(localPortGroups[i].split(",")[i]);
            System.out.println(localPortGroups[i].split(",")[i]+"!!");
            //用线程完成
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    //启动服务器
                    try {
                        new ProtocolServer().bind(port);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

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
        /*String type = MyCommConfig.type;
        if(type.equalsIgnoreCase("as") || type.equalsIgnoreCase("r"))
        {   //本地客户端端口
            String[] localClientPorts = MyCommConfig.localClientPort.split(",");
            //服务器ip和端口
            String[] nextIpPorts = MyCommConfig.nextServerIpPort.split(",");
            for(int i=0;i<localClientPorts.length;i++)
            {
                String[] ipPort = nextIpPorts[i].split(":");
//                ClientServer clientServer = new ClientServer();
                //客户端的服务器本地端口号，要连接的ip和端口号
//                clientServer.start(this, type, localClientPorts[i], ipPort[0], ipPort[1]);
            }

            try
            {
                Thread.sleep(500);
            }
            catch(Exception e)
            {
            }*/


//            ClientClient client = new ClientClient();
//            client.start(this, type, nextIpPortGroups[groupIdx], MyCommConfig.maxClientSendConnCountPerPort);

        }


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

