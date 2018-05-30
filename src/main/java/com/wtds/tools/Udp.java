package com.wtds.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.net.util.Base64;


/**
 * UDP帮助类
 * @author wyj
 */
public class Udp {
	
	private Udp(){}
	
	public Udp(int port){
		this.PORT = port;
		System.out.println("UDP使用端口:"+port);
	}
	
	private Charset charset = Charset.forName("UTF-8");
	
	private int PORT = 10010;
	/**
	 * 发送报文间隔时间
	 */
	private int intervalTime = 1000;

	// 发送报文状态控制，用停止循环发送报文
	private boolean sendFlag = true;

	/**
	 * 关闭发送报文
	 */
	public void close(){
		sendFlag = false;
	}
	
	/**
	 * 设置发送报文的间隔时间（毫秒）
	 * @param time 时间（毫秒），1000表示1秒
	 */
	public void setIntervalTime(int time){
		intervalTime = time;
	}
	
	//线程池
	static ExecutorService fixedThreadPool = Executors.newCachedThreadPool();
	
	/**
	 * udp循环发送消息(消息长度超过512会自动分片)<br>
	 * 接收消息需要使用 receiveMessage() 方法
	 * @param address 需要接收报文的地址
	 * @param port	端口
	 * @param packet 报文
	 */
	public void sendMessage(final String address,final String msg) {
		this.sendMessage(address, msg, 0);
	}
	
	/**
	 * 将要发送的消息
	 */
	static Map<String, String> willMssage = new ConcurrentHashMap<String, String>();
	
	/**
	 * udp发送消息(消息长度超过512会自动分片)<br>
	 * 接收消息需要使用 receiveMessage() 方法
	 * @param address 需要接收报文的地址
	 * @param port	端口
	 * @param 消息
	 * @param sendCount 发送报文的次数，单此值为0时循环发送
	 */
	public void sendMessage(final String address,final String msg,final int sendCount) {
		final String md5 = MD5Util.getMd5(msg);
		//willMssage.put(MD5Util.getMd5(msg), msg);
		fixedThreadPool.execute(new Runnable() {
			private DatagramSocket sendSocket;
			public void run() {
				//System.out.println("发送UDP(MD5:"+md5+")消息至["+address+":"+PORT+"]("+sendCount+")");
				List<String> codes = msgEncodeAndSplit(msg);
				try {
					sendSocket = new DatagramSocket();
					sendSocket.setBroadcast(true);
					int i = 1;
					while(sendFlag){
						int codesSize = codes.size();
						for(int j=0;j<codesSize;j++){
							//System.out.println("开始发送["+md5+"]_第"+i+"遍，(count:"+codesSize+",index:"+j+")...");
							byte [] packet = codes.get(j).getBytes(charset);
							// 发送广播start(按段发送)
							DatagramPacket sendPacket = new DatagramPacket(packet,packet.length,InetAddress.getByName(address), PORT);
							sendSocket.send(sendPacket);
							//System.out.println("发送:"+codes.get(j));
							Thread.sleep(1);
						}
						Thread.sleep(intervalTime);
						
						if(sendCount > 0 && sendCount <= i){
							break;
						}
						i++;
					}
					//System.out.println("发送完毕["+md5+"]");
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					//异常重启
					if(sendFlag && sendCount == 0){
						try {
							System.out.println("报文发送异常:2秒后重启...");
							Thread.sleep(2000);
						} catch (Exception e2) {
						}
						run();
					}
				}
				
			}
		});
	}
	
	DatagramSocket callbackSocket;
	
	/**
	 * 接收UDP消息（回调）
	 * @param port 监听端口号
	 * @param callback 回调函数
	 * @throws IOException 
	 */
	public void receiveMessage(final UdpCallback callback) throws IOException {
		fixedThreadPool.execute(new Runnable() {
			Udp udp = new Udp(PORT);
			/**
			 * Map<?md5?，Map<?第几条?,?消息内容?>>
			 */
			Map<String, Map<Integer,String>> indexMap = new ConcurrentHashMap<String, Map<Integer,String>>();
			
			public void run() {
				try {
					while (true) {
						udp.receiveFragmentMessageMessage(new UdpCallback() {
							public void getDatagramPacket(String base64, DatagramPacket pack) {
								msgBack(indexMap, callback, base64, pack);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 解码并且拼接消息
	 * @param indexMap
	 * @param callback
	 * @param base64
	 */
	private void msgBack(Map<String, Map<Integer,String>> indexMap,UdpCallback callback,String base64,DatagramPacket pack){
		try {
			if (!StringUtil.isEmpty(base64)) {
				String md5 = "";
				String[] sMsg = base64.split("\\|");
				int count = Integer.parseInt(sMsg[0]); //消息总数
				int index = Integer.parseInt(sMsg[1]); //消息下标
				md5 = sMsg[2]; //被切分的消息内容
				String base64Msg = sMsg[3];
				//去除补全码
				base64Msg = base64Msg.replace("0000000000000000000000000000000000000000000000","");
				//如果（count==1 && index==0）消息只有一条
				if(count == 1 && index == 0){
					String msg = new String(Base64.decodeBase64(base64Msg),charset);
					callback.getDatagramPacket(msg,pack);
				}else{
					if(indexMap.get(md5) == null){
						Map<Integer,String> iMap = new ConcurrentHashMap<Integer, String>();
						iMap.put(index, base64Msg);
						indexMap.put(md5, iMap);
					}else{
						Map<Integer,String> iMap = indexMap.get(md5);
						iMap.put(index, base64Msg);
						if(count == iMap.size()){
							String [] base64Arr = new String[count];
							for (Integer i : iMap.keySet()) {
								base64Arr[i] = iMap.get(i);
							}
							String base64m = "";
							for(int i=0;i<base64Arr.length;i++){
								base64m += base64Arr[i];
							}
							indexMap.remove(md5);
							callback.getDatagramPacket(new String(Base64.decodeBase64(base64m),charset),pack);
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("["+DateUtil.getSysDateByHHmmss()+"]消息解码错误");
			System.out.println("=========================消息内容=========================");
			System.out.println(base64);
			System.out.println("=======================================================");
			e.printStackTrace();
		}
	}
	
	private void receiveFragmentMessageMessage(final UdpCallback callback) throws IOException{
			// 1、创建DatagramSocket;
			if(callbackSocket==null)callbackSocket = new DatagramSocket(PORT);
			// 2、创建数据包，用于接收内容。
			byte[] buf = new byte[512];
			final DatagramPacket packet = new DatagramPacket(buf, buf.length);

			// 3、接收数据
			callbackSocket.receive(packet);
			fixedThreadPool.execute(new Runnable() {
				public void run() {
					callback.getDatagramPacket(new String(packet.getData(),charset),packet);
				}
			});
	}
	
	/**
	 * 发送报文（非组塞）
	 * @param address 需要接收报文的地址
	 * @param port	端口
	 * @param packet 报文
	 * @param sendCount 发送报文的次数，单此值为0时循环发送
	 */
	public void sendBroadcast(final String address,final byte [] packet,final int sendCount) {
		new Thread(new Runnable() {
			
			private DatagramSocket sendSocket;
			private DatagramPacket sendPacket;
			
			public void run() {
				//System.out.println("发送UDP至["+address+"]");
				
				try {
					sendSocket = new DatagramSocket();
					sendSocket.setBroadcast(true);
					// 发送广播start(按段发送)
					sendPacket = new DatagramPacket(packet,packet.length,InetAddress.getByName(address), PORT);
					int i = 0;
					while(sendFlag){
						sendSocket.send(sendPacket);
						Thread.sleep(intervalTime);
						if(sendCount > 0 && sendCount <= i){
							break;
						}
						i++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					//异常重启
					if(sendFlag && sendCount == 0){
						try {
							System.out.println("报文发送异常:2秒后重启...");
							Thread.sleep(2000);
						} catch (Exception e2) {
						}
						run();
					}
				}
			}
		}).start();
	}
	
	/**
	 * 接收UDP报文（阻塞）
	 * @param port 监听端口号
	 * @param byteCache 缓存长度
	 * @return
	 * @throws IOException
	 */
	public DatagramPacket receiveBroadcast() throws IOException{
		//1、创建DatagramSocket;  
        DatagramSocket socket = new DatagramSocket(PORT);  
        
        //2、创建数据包，用于接收内容。  
        byte[] buf = new byte[512];  
        DatagramPacket packet = new DatagramPacket(buf, buf.length);  
          
        //3、接收数据          
        socket.receive(packet);  
        //System.out.println("DatagramPacket===>"+packet.getAddress().getHostAddress()+":"+packet.getPort());
        
        //4、关闭连接。  
        socket.close(); 
        return packet;
	}
	
	
	DatagramSocket socket;
	/**
	 * 接收UDP报文（阻塞）
	 * @param port 监听端口号
	 * @param byteCache 缓存长度
	 * @return
	 * @throws IOException
	 */
	public DatagramPacket receiveBroadCast(int byteCache) throws IOException{
		//1、创建DatagramSocket;  
        if(socket == null)socket = new DatagramSocket(PORT);  
        
        //2、创建数据包，用于接收内容。  
        byte[] buf = new byte[byteCache];  
        DatagramPacket packet = new DatagramPacket(buf, buf.length);  
          
        //3、接收数据          
        socket.receive(packet);  
        //System.out.println("DatagramPacket===>"+packet.getAddress().getHostAddress()+":"+packet.getPort());
        
        //4、关闭连接。  
        //socket.close(); 
        return packet;
	}
	/**
	 * 获取本地广播地址(不推荐使用,后续版本可能去除)
	 * @return
	 */
	@Deprecated
	public static String getLocalBroadCast(){
        String broadCastIp = null;
        try {
            Enumeration<?> netInterfaces = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) netInterfaces.nextElement();
                if (!netInterface.isLoopback()&& netInterface.isUp()) {
                    List<InterfaceAddress> interfaceAddresses = netInterface.getInterfaceAddresses();
                    for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                        //只有 IPv4 网络具有广播地址，因此对于 IPv6 网络将返回 null。 
                        if(interfaceAddress.getBroadcast()!= null){
                            broadCastIp =interfaceAddress.getBroadcast().getHostAddress();
                        }
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        } 
        System.out.println("==>REPORT MENU 发送菜单广播地址：" + broadCastIp);
        return broadCastIp;
    }
	
	/**
	 * 消息编码并分片<br>
	 * 分片规则为|切分(消息数量|第几条|md5|消息内容,base64,最长440)<br>
	 * 最后一条数据如果不满46字节，则补全46个*
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public List<String> msgEncodeAndSplit(String msg){
		int packCount = 440;
		List<String> list = null;
		if(!StringUtil.isEmpty(msg)){
			list = new ArrayList<String>();
			
			StringBuilder base64Str = new StringBuilder(Base64.encodeBase64String(msg.getBytes(charset)));
			
			String md5 = MD5Util.getMd5(base64Str.toString());
			
			if(base64Str.length() > packCount){
				int count = base64Str.length() / packCount;
				for(int j=0;j<=count;j++){
					if(j==count){
						if(base64Str.length()<46)base64Str.append("0000000000000000000000000000000000000000000000");
						list.add((count+1)+"|"+j+"|"+md5+"|"+base64Str);
					}else{
						list.add((count+1)+"|"+j+"|"+md5+"|"+base64Str.substring(0, packCount));
						base64Str.delete(0, packCount);
					}
				}
			}else{
				if(base64Str.length()<46)base64Str.append("0000000000000000000000000000000000000000000000");
				list.add("1|0|"+md5+"|"+base64Str);
			}
			
		}
		return list;
	}
	
	public static void main(String[] args) throws Exception {
		
		
		
		String json = "{'1id':'com.wtds.portal.manager','name':'系统管理,用户管理，添加用户','serverAddress':'192.168.43.8','serverHomeUrl':'http://192.168.0.100'}";
			  json += "{'2id':'com.wtds.portal.manager','name':'系统管理,用户管理，添加用户','serverAddress':'192.168.43.8','serverHomeUrl':'http://192.168.0.100'}";
			  json += "{'3id':'com.wtds.portal.manager','name':'系统管理,用户管理，添加用户','serverAddress':'192.168.43.8','serverHomeUrl':'http://192.168.0.100'}";
			  json += "{'4id':'com.wtds.portal.manager','name':'系统管理,用户管理，添加用户','serverAddress':'192.168.43.8','serverHomeUrl':'http://192.168.0.100'}";
			  json += "{'5id':'com.wtds.portal.manager','name':'系统管理,用户管理，添加用户','serverAddress':'192.168.43.8','serverHomeUrl':'http://192.168.0.100'}";
			  json += "{'6id':'com.wtds.portal.manager','name':'系统管理,用户管理，添加用户','serverAddress':'192.168.43.8','serverHomeUrl':'http://192.168.0.100'}";
			  json += "{'7id':'com.wtds.portal.manager','name':'系统管理,用户管理，添加用户','serverAddress':'192.168.43.8','serverHomeUrl':'http://192.168.0.100'}";
			  json += "{'8id':'com.wtds.portal.manager','name':'系统管理,用户管理，添加用户','serverAddress':'192.168.43.8','serverHomeUrl':'http://192.168.0.100'}";
			  json += "{'9id':'com.wtds.portal.manager','name':'系统管理,用户管理，添加用户','serverAddress':'192.168.43.8','serverHomeUrl':'http://192.168.0.100'}";
			  json += "{'10id':'com.wtds.portal.manager','name':'系统管理,用户管理，添加用户','serverAddress':'192.168.43.8','serverHomeUrl':'http://192.168.0.100'}";
		
		for(int i=0;i<2;i++){
			json+=json+"============【"+i+"】";
		}
		
		//String json2 = "小弟最近项目需要，要做个类似于P2P架构的服务器来登记客户端IP和端口实现打洞。因为客户端数据量大概有三千个，所以要求服务器能撑住三千个并发（因为有心跳机制）。因为从没做过socket开发，加上网上的UDP资料也讲得不够深入，所以小弟将实现思路写出来请各路大侠帮忙看看是否合理。先讲个小插曲，小弟曾以为异步就是并发，所以用udpclient一下子BeginReceive了三次，以为这样就能一下子处理三个客户端发来的数据。测试时故意让第一次回调先sleep10秒再EndReceive。测试结果，当三个客户端（分别是为A、";
		//System.out.println(json.length()+"|"+Test.bytes2kb(json.getBytes("UTF-8").length));
		Udp udp = new Udp(22002);
		udp.setIntervalTime(1000);
		udp.sendMessage(getLocalBroadCast(), json,0);
		
		
//		String json2 = "小弟最近项目需要，要做个类似于P2P架构的服务器来登记客户端IP和端口实现打洞。因为客户端数据量大概有三千个，所以要求服务器能撑住三千个并发（因为有心跳机制）。因为从没做过socket开发，加上网上的UDP资料也讲得不够深入，所以小弟将实现思路写出来请各路大侠帮忙看看是否合理。先讲个小插曲，小弟曾以为异步就是并发，所以用udpclient一下子BeginReceive了三次，以为这样就能一下子处理三个客户端发来的数据。测试时故意让第一次回调先sleep10秒再EndReceive。测试结果，当三个客户端（分别是为A、";
//		Udp udp2 = new Udp();
//		udp2.setIntervalTime(1);
//		udp2.sendMessage(getLocalBroadCast(), 22002, json2,1);
		
//		List<String> list = msgEncodeAndSplit("需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'需要解码的数据123~!@#$%^&*()||234?><{}:<  ,./;'[]\\//.123~~~\\'...");
//		System.out.println(msgDecodeAndJoin(list));
		
	}
}
