package com.yc.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.yc.util.YcUtil;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * 多人在线聊天室SWT实现
 * @author 沈俊羽
 *
 */
public class Client implements Runnable {

	protected Shell shell;
	private Text text_localhost;/*服务器地址*/
	private Text text_1;/*端口号*/
	private Text text_2;/*历史消息*/
	private Text text_3;/*待发送的消息*/
	private Button button;/*确认与服务器连接的按钮*/
	private Button button_1;/*退出系统按钮*/
	private Button button_2;/*发送消息按钮*/
	private Button button_3;/*在线用户列表*/
	private Combo combo;/*心情列表*/
	private Label label_time;/*显示当前*/
	private Label users;/*用户列表*/
	
	private Display display;
	
	private Socket s=null;/*连接服务器的套接字对象*/
	private DataInputStream dis=null;/*用于从服务器读数据的流*/
	private DataOutputStream dos=null;/*用于向服务器写数据的流*/
	
	private boolean connected=false;/*标识是否与服务器连接  ，如果连接 则为true 若未连接则为false*/
	private String msg=null;/*用于记录服务器回传信息的字符串*/
	private Thread t=null;/*线程对象*/
	
	/**
	 * 用于与服务器建立连接的方法
	 */
	public void connect(){
		try {
			String ipAddr=text_localhost.getText().trim();
			int port=Integer.parseInt(text_1.getText().trim());
			s=new Socket(ipAddr,port);//建立连接
			dis=new DataInputStream(s.getInputStream());
			dos=new DataOutputStream(s.getOutputStream());
			System.out.println("客户端日志：已经连接了服务器，时间:"+YcUtil.getCurrentTime());
			
			connected=true;/*改变标识量的状态*/
			
			text_2.setText("\n操作记录:连接服务器成功...\n\t\t\t时间:"+YcUtil.getCurrentTime());
		} catch (NumberFormatException e) {
			System.out.println("客户端日志:String转Integer失败");
			e.printStackTrace();
		} catch (UnknownHostException e) {
			System.out.println("客户端日志:建立连接失败");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("客户端日志:通过socket获取InputStream或OutoutStream失败");
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭的方法	1.关闭连接 socket  connected=false 
	 */
	public void disconnect(){
		if(null!=s){
			try {
				connected=false;/*改变标识量 的状态*/
				s.close();/*关闭连接*/
			} catch (IOException e) {
				e.printStackTrace();
				YcUtil.showMessage(shell, "关闭连接失败了...", e.getMessage());
			}
		}
	}
	

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Client window = new Client();
			window.open();
		} catch (Exception e) {
			System.out.println("客户端异常:窗口打开异常");
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(886, 763);
		shell.setText("多人在线聊天系统");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(shell, SWT.VERTICAL);
		
		Composite composite = new Composite(sashForm, SWT.NONE);
		
		Label label = new Label(composite, SWT.RIGHT);
		label.setBounds(39, 10, 98, 20);
		label.setText("服务器地址：");
		
		text_localhost = new Text(composite, SWT.BORDER);
		text_localhost.setBounds(143, 7, 164, 26);
		
		Label label_1 = new Label(composite, SWT.RIGHT);
		label_1.setBounds(333, 10, 64, 20);
		label_1.setText("端口号：");
		
		text_1 = new Text(composite, SWT.BORDER);
		text_1.setBounds(403, 7, 123, 26);
		
		button = new Button(composite, SWT.NONE);
		
		button.setBounds(554, 5, 98, 30);
		button.setText("确定");
		
		button_1 = new Button(composite, SWT.NONE);
		button_1.setBounds(690, 5, 98, 30);
		button_1.setText("退出系统");
		
		Composite composite_1 = new Composite(sashForm, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm_1 = new SashForm(composite_1, SWT.NONE);
		
		Composite composite_4 = new Composite(sashForm_1, SWT.NONE);
		composite_4.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm_2 = new SashForm(composite_4, SWT.VERTICAL);
		
		Label lblNewLabel = new Label(sashForm_2, SWT.NONE);
		lblNewLabel.setText("聊天记录");
		
		text_2 = new Text(sashForm_2, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		sashForm_2.setWeights(new int[] {29, 335});
		
		Composite composite_5 = new Composite(sashForm_1, SWT.NONE);
		composite_5.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm_3 = new SashForm(composite_5, SWT.VERTICAL);
		
		Label label1 = new Label(sashForm_3, SWT.NONE);
		label1.setText("在线用户");
		
		users = new Label(sashForm_3, SWT.NONE);
		sashForm_3.setWeights(new int[] {22, 375});
		sashForm_1.setWeights(new int[] {603, 244});
		
		Composite composite_2 = new Composite(sashForm, SWT.NONE);
		
		Label label_2 = new Label(composite_2, SWT.NONE);
		label_2.setAlignment(SWT.RIGHT);
		label_2.setBounds(10, 10, 48, 20);
		label_2.setText("心情：");
		
		combo = new Combo(composite_2, SWT.NONE);
		combo.setItems(new String[] {"开心的", "欣喜的", "傲娇的", "愤怒的", "害羞的"});
		combo.setBounds(64, 7, 92, 28);
		combo.select(0);
		
		Label label_3 = new Label(composite_2, SWT.RIGHT);
		label_3.setBounds(10, 43, 76, 20);
		label_3.setText("发送消息：");
		
		text_3 = new Text(composite_2, SWT.BORDER | SWT.H_SCROLL | SWT.CANCEL | SWT.MULTI);
		text_3.setBounds(64, 69, 525, 139);
		
		button_2 = new Button(composite_2, SWT.NONE);
		button_2.setBounds(607, 166, 98, 30);
		button_2.setText("发送");
		
		button_3 = new Button(composite_2, SWT.NONE);
		button_3.setBounds(736, 166, 98, 30);
		button_3.setText("清空");
		
		Composite composite_3 = new Composite(sashForm, SWT.NONE);
		
		label_time = new Label(composite_3, SWT.NONE);
		label_time.setBounds(62, 10, 531, 20);
		sashForm.setWeights(new int[] {50, 401, 219, 37});
		
		//确定与服务器连接的按钮
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				connect();//获取连接
				//禁止修改文本框
				text_localhost.setEnabled(false);
				text_1.setEnabled(false);
				//启动监听线程  会调用 run()方法
				t=new Thread(Client.this);//Client.this  表示客户端对象  这样这个对象的run()方法就会被请求
				t.start();//run()方法中写入从服务器中读取服务器信息的操作
			}
		});
		
		//退出系统的按钮    停止线程 (线程在监听服务器给客户端的信息)  断开连接   System.exit(0);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				t.stop();//停止线程
				disconnect();
				System.exit(0);
			}
		});	

		//发送消息的按钮
		/**
		 * 发送：1、取出要发送的内容   判断是否为空     2、拼接要发送的信息    协议：第一行 info\n
		 * 第二行：用户<ip地址>说:\n\t<内容>
		 * 3、通过dos流发送给服务器
		 * 4、清空文本框，重新获得焦点
		 */
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					//取出IP地址
					String ip=InetAddress.getLocalHost().getHostAddress();
					//取出内容
					String content=text_3.getText().trim();
					//取出表情
					String emoji=combo.getText().trim();
					if(null==content||"".equals(content)){
						YcUtil.showMessage(shell, "发送信息失败", "内容不能为空");
					}else{
						String info="info\n用户"+ip+" "+emoji+"说：\n\t"+content;
						dos.writeUTF(info);
						dos.flush();
						text_3.setText("");
						text_3.setFocus();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					YcUtil.showMessage(shell, "发送信息失败", e1.getMessage());
				} 
			}
		});

		//清空消息的按钮
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text_3.setText("");
				text_3.setFocus();/*获取焦点*/
			}
		});
		
		//显示当前系统时间
		showTime();
		
	}
	
	private void showTime(){
		new Thread(new Runnable() {
				
			@Override
			public void run() {
				while(true){
					display.syncExec(new Runnable() {
						
						@Override
						public void run() {
							label_time.setText("当前系统时间："+YcUtil.getCurrentTime());
						}
					});
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						System.out.println("客户端日志:线程睡眠异常");
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	//从服务器读取信息
	@Override
	public void run() {
		while(true){
			try {
				if(connected==true && s.isConnected()==true && s.isClosed()==false){
					//从服务器读取信息
					msg=dis.readUTF();
					if(msg.startsWith("info\n")){//传递信息
						msg=msg.substring(msg.indexOf("info\n")+4);
						display.asyncExec(new Runnable() {
							
							@Override
							public void run() {
								text_2.append("\n"+msg+"\n\t\t\t"+YcUtil.getCurrentTime());
							}
						});
					}else if(msg.startsWith("users\n")){//用户列表
						//后期扩展
						msg=msg.substring(msg.indexOf("users\n")+5);
						System.out.println(msg+"----");
						display.asyncExec(new Runnable() {
							
							@Override
							public void run() {
								users.setText("");
								String usersStr="";
								String [] user=msg.split(" ");
								for (String string : user) {
									usersStr+=string+"\n";
								}
								users.setText(usersStr);
							}
						});
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				YcUtil.showMessage(shell, "接收信息失败", e.getMessage());
			}
		}
	}
}
