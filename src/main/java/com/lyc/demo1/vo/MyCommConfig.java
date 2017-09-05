package com.lyc.demo1.vo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * 配置文件的model数据
 */
public class MyCommConfig
{
	public static boolean DEBUG = true;								//是否输出调试信息
	public static String type = "";									//类型
	
	public static String localServerPort = "";						//本地服务器端口
	public static String localClientPort = "";						//本地客户端端口
	public static String lastClientPort = "";						//上一机器客户端端口
	public static String nextServerIpPort = "";						//下一机器服务器Ip+端口
	public static String localServerAcceptIp = "";					//本地服务器接受地址
	public static String localServerRefuseIp = "";					//本地服务器拒绝地址
	
	
	public static int retryCountServerSend = 10;					//服务器发送数据包失败重试次数
	public static int retryCountClientSend = 10;					//客户端发送数据包失败重试次数
	
	public static int maxSrvRecvConnCountPerPort = 20;				//每个本地服务器端口接收上一客户机对应端口的最大连接数
	public static int maxClientSendConnCountPerPort = 20;			//本地客户机至下一机器每个Ip+端口最大发送连接数
	
	public static long aliveIdleTimeSrvSend = 30000;				//本地服务器发送连接健康检测空闲时间 30秒
	public static long aliveIdleTimeClientSend = 30000;				//本地客户机发送连接健康检测空闲时间 30秒
	public static int aliveTimeoutSrvSend = 5000;					//本地服务器发送连接健康检测应答超时时间 5秒
	public static int aliveTimeoutClientSend = 5000;				//本地客户机发送连接健康检测应答超时时间 5秒
	
	public static long intervalConfigManagerRefresh = 60000;		//参数刷新间隔时间 1分钟
	public static long intervalStatusMonitor = 600000;				//状态查询间隔时间 10分钟
	public static long intervalServerConnector = 30000;				//服务器连接器运行间隔时间 30秒
	public static long intervalClientConnector = 30000;				//客户端连接器运行间隔时间 30秒
	
	public static int localClientRecvConnTimeout = 60000;		//本机客户机接收连接超时时间
	public static int localServerRecvConnTimeout = 60000;		//本地服务器接收连接超时时间
	
	public static String localClientAcceptIp ="";//本地客户端接受地址
	
	static Map oldParas = Collections.synchronizedMap(new HashMap());
	static Logger logger = LoggerFactory.getLogger(MyCommConfig.class);


    /**
     * 初始化MyCommConfig中的数据
     * @param newParas
     */
	public static void refresh(Map newParas)
	{
		String key;
		String value;
		boolean curDebug;
		
		List<String> keyList = new LinkedList();
		Iterator iter = newParas.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry entry = (Map.Entry)iter.next();
			if(entry != null)
			{
				keyList.add((String) entry.getKey());
			}
		}
		
		curDebug = DEBUG;
		
		for(int i=0;i<keyList.size();i++)
		{   //获取配置文件中的键值对
			key = (String)keyList.get(i);
			value = ((String)newParas.get(key)).trim();
			
			if(!oldParas.containsKey(key) || !value.equals(((String)oldParas.get(key)).trim()))
			{				
				try
				{
					if(key.equalsIgnoreCase("DEBUG"))
						DEBUG = getBoolean(value);
					else if(type.equals("") && key.equalsIgnoreCase("Type"))
						type = getType(value);
					else if(key.equalsIgnoreCase("Local.Server.Port"))
					{
						if(isGroupIntList(value, 1, 65535))
							localServerPort = value;
					}
					else if(key.equalsIgnoreCase("Local.Server.Accept.Ip"))
					{
						if(isIpList(value))
							localServerAcceptIp = value;
					}
					else if(key.equalsIgnoreCase("Local.Server.Refuse.Ip"))
					{
						if(isIpList(value))
							localServerRefuseIp = value;
					}
					else if(key.equalsIgnoreCase("Local.Server.MaxRecvConnCountPerPort"))
					{
						maxSrvRecvConnCountPerPort = getInt(value, 1, 1000);
					}					
					else if(key.equalsIgnoreCase("Local.Client.Port"))
					{
						if(isGroupIntList(value, 1, 65535))
							localClientPort = value;
					}
					else if(key.equalsIgnoreCase("Last.Client.Port"))
					{
						if(isGroupIntList(value, 1, 65535))
							lastClientPort = value;
					}
					else if(key.equalsIgnoreCase("Next.Server.IpPort"))
					{
						if(isGroupIpPortList(value))
							nextServerIpPort = value;
					}
					else if(key.equalsIgnoreCase("Local.Client.MaxSendConnCountPerPort"))
					{
						maxClientSendConnCountPerPort = getInt(value, 1);
					}
					else if(key.equalsIgnoreCase("Interval.ConfigManagerRefresh"))
					{
						intervalConfigManagerRefresh = getLong(value, 10000);
					}
					else if(key.equalsIgnoreCase("Interval.StatusMonitor"))
					{
						intervalStatusMonitor = getLong(value, 10000);
					}
					else if(key.equalsIgnoreCase("Interval.ClientConnector"))
					{
						intervalClientConnector = getLong(value, 10000);
					}
					else if(key.equalsIgnoreCase("Interval.ServerConnector"))
					{
						intervalServerConnector = getLong(value, 10000);
					}
					else if(key.equalsIgnoreCase("RetryCount.ServerSend"))
					{
						retryCountServerSend = getInt(value, 0);
					}
					else if(key.equalsIgnoreCase("RetryCount.ClientSend"))
					{
						retryCountClientSend = getInt(value, 0);
					}
					else if(key.equalsIgnoreCase("Alive.IdleTime.SrvSend"))
					{
						aliveIdleTimeSrvSend = getLong(value, 10000);
					}
					else if(key.equalsIgnoreCase("Alive.Timeout.SrvSend"))
					{
						aliveTimeoutSrvSend = getInt(value, 1000);
					}
					else if(key.equalsIgnoreCase("Alive.IdleTime.ClientSend"))
					{
						aliveIdleTimeClientSend = getLong(value, 10000);
					}
					else if(key.equalsIgnoreCase("Alive.Timeout.ClientSend"))
					{
						aliveTimeoutClientSend = getInt(value, 1000);
					}
					else if(key.equalsIgnoreCase("Local.Server.RecvConnTimeout"))
					{
						localServerRecvConnTimeout = getInt(value, 1000);
					}
					else if(key.equalsIgnoreCase("Local.Client.RecvConnTimeout"))
					{
						localClientRecvConnTimeout = getInt(value, 1000);
					}
					else if(key.equalsIgnoreCase("Local.Client.Accept.Ip"))
					{
						if(isIpList(value))
							localClientAcceptIp = value;
					}
					oldParas.put(key, value);
					
					if(curDebug)
						logger.info("updated "+key+"="+value);
                }
				catch(Exception e)
				{
					logger.error(key+"="+value);
					logger.error(e.getMessage());
                }
			}				
		}
	}
	
	
	private static boolean getBoolean(String strValue)
		throws Exception
	{
		if(strValue.equalsIgnoreCase("true"))
			return(true);
		else if(strValue.equalsIgnoreCase("false"))
			return(false);
		
		throw new Exception("Transform boolean error.");
	}
	
	
	private static String getType(String strValue)
		throws Exception
	{
		if(strValue.equalsIgnoreCase("r"))
			return("r");
		else if(strValue.equalsIgnoreCase("s"))
			return("s");
		else if(strValue.equalsIgnoreCase("as"))
			return("as");
		
		throw new Exception("Transform boolean error.");
	}
	
	
	private static long getLong(String strValue, long min, long max)
		throws Exception
	{
		try
		{
			long count = Long.valueOf(strValue).longValue();
			if(count >= min && count <= max)
				return(count);
			else
				throw new Exception("Transform long error.");
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	
	private static long getLong(String strValue, long min)
		throws Exception
	{
		try
		{
			long count = Long.valueOf(strValue).longValue();
			if(count >= min)
				return(count);
			else
				throw new Exception("Transform long2 error.");
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	
	private static int getInt(String strValue, int min, int max)
		throws Exception
	{
		try
		{
			int count = Integer.valueOf(strValue).intValue();
			if(count >= min && count <= max)
				return(count);
			else
				throw new Exception("Transform int error.");
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	
	private static int getInt(String strValue, int min)
		throws Exception
	{
		try
		{
			int count = Integer.valueOf(strValue).intValue();
			if(count >= min)
				return(count);
			else
				throw new Exception("Transform int2 error.");
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	
	private static boolean isInt(String strValue, int min, int max)
		throws Exception
	{
		try
		{
			int count = Integer.valueOf(strValue).intValue();
			if(count >= min && count <= max)
				return(true);
			else
				return(false);
		}
		catch(Exception e)
		{
			throw new Exception("isInt2 error: "+e.getMessage());
		}
	}
	
	
	private static boolean isInt(String strValue, int min)
		throws Exception
	{
		try
		{
			int count = Integer.valueOf(strValue).intValue();
			if(count >= min)
				return(true);
			else
				return(false);
		}
		catch(Exception e)
		{
			throw new Exception("isInt error: "+e.getMessage());
		}
	}
	
	
	private static boolean isIntList(String strValue, int min, int max)
		throws Exception
	{
		try
		{
			String[] strList = strValue.split(";");
			for(int i=0;i<strList.length;i++)
			{
				if(!isInt(strList[i], min, max))
				{
					return(false);
				}
			}
			
			return(true);
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	
	private static boolean isGroupIntList(String strValue, int min, int max)
		throws Exception
	{
		try
		{
			String[] grpList = strValue.split(";");
			for(int i=0;i<grpList.length;i++)
			{
				String[] strList = grpList[i].split(",");
				for(int j=0;j<strList.length;j++)
				{
					if(!isInt(strList[j], min, max))
					{
						return(false);
					}
				}
			}
			
			return(true);
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	
	private static boolean isIntList(String strValue, int min)
		throws Exception
	{
		try
		{
			String[] strList = strValue.split(";");
			for(int i=0;i<strList.length;i++)
			{
				if(!isInt(strList[i], min))
				{
					return(false);
				}
			}
			
			return(true);
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	
	private static boolean isIpList(String strValue)
		throws Exception
	{
		try
		{
			String[] strList = strValue.split(";");
			//if(strList.length == 0)
				//throw new Exception("isIpList error: length=0");
			
			for(int i=0;i<strList.length;i++)
			{
				if(!strList[i].equals("") && !isIp(strList[i]))
					throw new Exception("isIpList error: '"+strList[i]+"' is not a ip.");				
			}
			
			return(true);
		}
		catch(Exception e)
		{
			throw new Exception("isIpList error: "+e.getMessage());
		}
	}
	
	
	private static boolean isIp(String strValue)
		throws Exception
	{
		try
		{
			String[] strList = strValue.split("\\.");
			if(strList.length != 4)
				throw new Exception("isIp error: '"+strValue+"' length != 4.");
			
			for(int i=0;i<strList.length;i++)
			{
				if(!isInt(strList[i], 0, 255))
					throw new Exception("isIp error: invalid int(0,255) in '"+strList[i]+"'.");
			}
			
			return(true);
		}
		catch(Exception e)
		{
			throw e;
		}
	}
	
	
	private static boolean isIpPortList(String strValue)
		throws Exception
	{
		try
		{
			String[] strList = strValue.split(";");
			//if(strList.length == 0)
				//throw new Exception("isIpPortList error: length=0");
			
			for(int i=0;i<strList.length;i++)
			{
				if(!strList[i].equals("") && !isIpPort(strList[i]))
					throw new Exception("isIpPortList error: '"+strList[i]+"' is not a ip:port.");				
			}
			
			return(true);
		}
		catch(Exception e)
		{
			throw new Exception("isIpPortList error: "+e.getMessage());
		}
	}
	
	
	private static boolean isGroupIpPortList(String strValue)
		throws Exception
	{
		try
		{
			String[] grpList = strValue.split(";");
			for(int i=0;i<grpList.length;i++)
			{
				String[] strList = grpList[i].split(",");				
				for(int j=0;j<strList.length;j++)
				{
					if(!strList[j].equals("") && !isIpPort(strList[j]))
						throw new Exception("isIpPortList error: '"+strList[j]+"' is not a ip:port.");				
				}
			}
			
			
			
			return(true);
		}
		catch(Exception e)
		{
			throw new Exception("isIpPortList error: "+e.getMessage());
		}
	}
	
	
	private static boolean isIpPort(String strValue)
		throws Exception
	{
		try
		{
			String[] strList = strValue.split(":");
			if(strList.length != 2)
				throw new Exception("isIpPort error: '"+strValue+"' length != 2.");
			
			if(!isIp(strList[0]))
				throw new Exception("isIpPort error: '"+strList[0]+"' is not a ip.");
			
			if(!isInt(strList[1], 1, 65535))
				throw new Exception("isIpPort error: '"+strList[1]+"' is not a int[1, 65535].");
			
			return(true);
		}
		catch(Exception e)
		{
			throw e;
		}
	}
}
