package com.lyc.protocol.thread;

import com.lyc.protocol.vo.MyCommConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.*;

/**
 * 配置文件管理
 */
public class ConfigManager extends Thread{
    private Logger logger= LoggerFactory.getLogger(ConfigManager.class);
    String cfgName = "conf/conf.properties";
    public void start(String cfgName)
    {
        if(cfgName != null && !cfgName.equalsIgnoreCase("")){
            this.cfgName = cfgName;
        }
        super.start();
    }
    public void run()
    {
//        InputStream in = null;
        Properties pro =null;
        FileInputStream in=null;
        String keyStr;
        logger.info("ConfigManager start");
        while(true)
        {
            try
            {
//                in = ConfigManager.class.getResourceAsStream(cfgName);
                Properties p = new Properties();
                in= new FileInputStream(cfgName);
                p.load(in);
                Map parameters = Collections.synchronizedMap(new HashMap());
                Iterator iter = p.entrySet().iterator();
                while(iter.hasNext())
                {
                    Map.Entry entry = (Map.Entry)iter.next();
                    if(entry != null)
                    {
                        keyStr = (String)entry.getKey();
                        parameters.put(keyStr, p.getProperty(keyStr));
                    }
                }
                //将配置文件中数据写入到MyCommConfig中
                MyCommConfig.refresh(parameters);
                logger.info("配置文件刷新完成");
            }
            catch(Exception e)
            {
                logger.error("ConfigManager exception: "+e.getMessage());
            }
            finally
            {
                if(in != null)
                {
                    try
                    {
                        in.close();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }finally {
                        in=null;
                    }
                }
            }

            synchronized(this)
            {
                try
                {
                    //配置文件刷新时间1分钟
                    wait(MyCommConfig.intervalConfigManagerRefresh);
                    logger.info("配置文件开始刷新");
                }
                catch(Exception e)
                {
                    if(MyCommConfig.DEBUG)
                    {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
    }
}
