package com.yc.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.yc.util.YcUtil;

/**
 * 多人在线聊天室服务器
 * 
 * @author 俊羽
 *
 */
public class Server {
	private boolean started = false;// 标识 是否启动
	private ServerSocket ss = null;// 服务器套接字对象
	private List<ClientCon> clients = new ArrayList<ClientCon>();// 客户端的集合 一个服务器可以有多个客户端

	public static void main(String[] args) {
		new Server().startServer();
	}

	/**
	 * 1、创建服务器套接字对象 2、设置启动状态 started=true 3、设置死循环接收客户端的连接
	 * 4、每接收一个客户机，这个客户机就会形成一个ClientCon对象，加入到clients集合中---->创建一个线程，用于操作ClientCon
	 */
	public void startServer() {
		try {
			ss = new ServerSocket(7777);
			started = true;
			System.out.println("服务器日志：服务器启动时间" + YcUtil.getCurrentTime());
		} catch (IOException e) {
			System.out.println("服务器错误日志：端口号被通用，启动失败......时间" + YcUtil.getCurrentTime());
			e.printStackTrace();
		}

		try {
			while (started) {
				Socket s = ss.accept();
				System.out.println(
						"服务器日志:有一个客户机" + s.getInetAddress().getHostAddress() + "连接上了服务器，时间" + YcUtil.getCurrentTime());
				ClientCon cc = new ClientCon(s);
				Thread t = new Thread(cc);
				t.start();

				clients.add(cc);

				String list = "";
				// 得到目前连接到服务器的每个客户端的IP地址
				for (ClientCon con : clients) {// 循环集合 取出每一个clinetcon对象
					list += con.s.getInetAddress().getHostAddress() + " ";
					System.out.println("*****");
				}
				// 发送给每个客户端
				for (ClientCon con : clients) {// 循环集合 取出每一个clinetcon对象
					con.dos.writeUTF("users\n" + list);// 调用cc中send()方法 发送信息
					System.out.println(".....");
				}

			}
		} catch (IOException e) {
			System.out.println("服务器日志：客户端连接异常");
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				System.out.println("服务器日志：服务器套接字关闭异常");
				e.printStackTrace();
			}
		}
	}

	// 内部类 表示与一个客户端的连接ennn
	public class ClientCon implements Runnable {
		Socket s = null;// 通过套接字与客户端进行连接
		DataInputStream dis = null;
		DataOutputStream dos = null;
		boolean connected = false;// 表示客户机与服务器连接的标志

		// 构造方法
		public ClientCon(Socket s) {
			try {
				this.s = s;
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				// 建立连接
				connected = true;

//				String list="";
//				//得到目前连接到服务器的每个客户端的IP地址
//				for(ClientCon con:clients){//循环集合  取出每一个clinetcon对象
//					list+=con.s.getInetAddress().getHostAddress()+"|";
//					System.out.println("*****");
//				}
//				//发送给每个客户端
//				for(ClientCon con:clients){//循环集合  取出每一个clinetcon对象
//					dos.writeUTF("users\n"+list);//调用cc中send()方法 发送信息
//					System.out.println(".....");
//				}
			} catch (IOException e) {
				System.out.println("建立与客户机" + s.getInetAddress().getHostAddress() + "的连接失败");
				e.printStackTrace();
			}
		}

		/**
		 * 服务器向客户机发送信息
		 * 
		 * @param info
		 */
		public void send(String info) {
			try {
				dos.writeUTF(info);
			} catch (IOException e) {
				System.out.println(
						"服务器日志：客户端" + s.getInetAddress().getHostAddress() + "已经掉线了  时间:" + YcUtil.getCurrentTime());
				e.printStackTrace();
			}

		}

		// 这个表示服务器端被动的接收客户端传过来的数据
		// 群发消息 分发给每个客户端
		@Override
		public void run() {
			while (connected) {
				try {
					String info = dis.readUTF();
					System.out.println("服务器日志：客户端" + s.getInetAddress().getHostAddress() + "说：" + info + " 时间："
							+ YcUtil.getCurrentTime());
					for (ClientCon cc : clients) {// 循环集合 取出每一个clinetcon对象
						cc.send(info);// 调用cc中send()方法 发送信息
					}
				} catch (IOException e) {
					System.out.println(
							"服务器日志：客户端" + s.getInetAddress().getHostAddress() + "已经掉线了,时间：" + YcUtil.getCurrentTime());
					e.printStackTrace();
					connected = false;
					clients.remove(this);// 如果出现异常，说明当前的这个客户端已经掉线，所以请从clients中移除
				}
			}
		}
	}

}
