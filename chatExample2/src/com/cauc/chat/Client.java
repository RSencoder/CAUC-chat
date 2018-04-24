package com.cauc.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Client extends JFrame {
	private final int port = 9999;
	private Socket socket;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	private String localUserName;
	// “在线用户列表ListModel”,用于维护“在线用户列表”中显示的内容
	private final DefaultListModel<String> onlinUserDlm = new DefaultListModel<String>();
	// 用于控制时间信息显示格式
	// private final SimpleDateFormat dateFormat = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private final JPanel contentPane;
	private final JTextField textFieldUserName;
	private final JPasswordField passwordFieldPwd;
	private final JTextField textFieldMsgToSend;
	private final JTextPane textPaneMsgRecord;
	private final JList<String> listOnlineUsers;
	private final JButton btnLogon;
	private final JButton btnSendMsg;
	private final JButton btnSendFile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(()-> {
			try {
				Client frame = new Client();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	
		
	}

	/**
	 * Create the frame.
	 */
	/**
	 * 
	 */
	public Client() {
		setTitle("\u5BA2\u6237\u7AEF");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 597, 402);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panelNorth = new JPanel();
		panelNorth.setBorder(new EmptyBorder(0, 0, 5, 0));
		contentPane.add(panelNorth, BorderLayout.NORTH);
		panelNorth.setLayout(new BoxLayout(panelNorth, BoxLayout.X_AXIS));

		JLabel lblUserName = new JLabel("\u7528\u6237\u540D\uFF1A");
		panelNorth.add(lblUserName);

		textFieldUserName = new JTextField();
		panelNorth.add(textFieldUserName);
		textFieldUserName.setColumns(10);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		panelNorth.add(horizontalStrut);

		JLabel lblPwd = new JLabel("\u53E3\u4EE4\uFF1A");
		panelNorth.add(lblPwd);

		passwordFieldPwd = new JPasswordField();
		passwordFieldPwd.setColumns(10);
		panelNorth.add(passwordFieldPwd);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		panelNorth.add(horizontalStrut_1);

		btnLogon = new JButton("\u767B\u5F55"); // “登录”按钮
		
		
		panelNorth.add(btnLogon);

		JSplitPane splitPaneCenter = new JSplitPane();
		splitPaneCenter.setResizeWeight(1.0);
		contentPane.add(splitPaneCenter, BorderLayout.CENTER);

		JScrollPane scrollPaneMsgRecord = new JScrollPane();
		scrollPaneMsgRecord.setViewportBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "\u6D88\u606F\u8BB0\u5F55",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPaneCenter.setLeftComponent(scrollPaneMsgRecord);

		textPaneMsgRecord = new JTextPane();
		scrollPaneMsgRecord.setViewportView(textPaneMsgRecord);

		JScrollPane scrollPaneOnlineUsers = new JScrollPane();
		scrollPaneOnlineUsers.setViewportBorder(new TitledBorder(null,
				"\u5728\u7EBF\u7528\u6237", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		splitPaneCenter.setRightComponent(scrollPaneOnlineUsers);

		listOnlineUsers = new JList<String>(onlinUserDlm);
		scrollPaneOnlineUsers.setViewportView(listOnlineUsers);

		JPanel panelSouth = new JPanel();
		panelSouth.setBorder(new EmptyBorder(5, 0, 0, 0));
		contentPane.add(panelSouth, BorderLayout.SOUTH);
		panelSouth.setLayout(new BoxLayout(panelSouth, BoxLayout.X_AXIS));

		textFieldMsgToSend = new JTextField();
		panelSouth.add(textFieldMsgToSend);
		textFieldMsgToSend.setColumns(10);

		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		panelSouth.add(horizontalStrut_2);

		btnSendMsg = new JButton("\u53D1\u9001\u6D88\u606F"); // “发送消息”按钮
		
		panelSouth.add(btnSendMsg);

		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		panelSouth.add(horizontalStrut_3);

		btnSendFile = new JButton("\u53D1\u9001\u6587\u4EF6");
		panelSouth.add(btnSendFile);

		// 将发送文件按钮设为不可用状态
		btnSendFile.setEnabled(false);
		// 将发送消息按钮设为不可用状态
		btnSendMsg.setEnabled(false);
		
		btnLogon.addActionListener((e) -> {
			if (btnLogon.getText().equals("登录")) {
				localUserName = textFieldUserName.getText().trim();
				if (localUserName.length() > 0) {
					// 与服务器端建立Socket连接，如果抛出异常，则弹出对话框通知用户，并退出
					try {
						socket = new Socket("localhost", port);
						// 将socket的输入流和输出流分别封装成对象输入流和对象输出流
						oos = new ObjectOutputStream(socket.getOutputStream());
						ois = new ObjectInputStream(socket.getInputStream());
					} catch (UnknownHostException e1) {
						JOptionPane.showMessageDialog(null, "找不到服务器主机");
						e1.printStackTrace();
						System.exit(0);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "服务器I/O错误，服务器未启动？");
						e1.printStackTrace();
						System.exit(0);
					}
					// 向服务器发送用户上线信息，将自己的用户名和IP地址发送给服务器
					UserStateMessage userStateMessage = new UserStateMessage(localUserName, "", true);
					try {
						oos.writeObject(userStateMessage);
						oos.flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					// 在“消息记录”文本框中用红色添加“XX时间登录成功”的信息
					String msgRecord = dateFormat.format(new Date()) + " 登录成功\r\n";
					addMsgRecord(msgRecord, Color.red, 12, false, false);
					// 创建并启动“后台监听线程”,监听并处理服务器传来的信息
					new Thread(new ListeningHandler()).start();
					// 将“登录”按钮设为“退出”按钮
					btnLogon.setText("退出");
					// 将发送文件按钮设为可用状态
					btnSendFile.setEnabled(true);
					// 将发送消息按钮设为可用状态
					btnSendMsg.setEnabled(true);
				}
			} else if (btnLogon.getText().equals("退出")) {
				if (JOptionPane.showConfirmDialog(null, "是否退出?", "退出确认",
						JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
					// 向服务器发送用户下线消息
					UserStateMessage userStateMessage = new UserStateMessage(localUserName, "", false);
					try {
						synchronized (oos) {
							oos.writeObject(userStateMessage);
							oos.flush();
						}
						System.exit(0);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}

		});
		btnSendMsg.addActionListener((e)->  {
				String msgContent = textFieldMsgToSend.getText();
				if (msgContent.length() > 0) {
					// 将消息文本框中的内容作为公聊消息发送给服务器
					ChatMessage chatMessage = new ChatMessage(localUserName,
							"", msgContent);
					try {
						synchronized (oos) {
							oos.writeObject(chatMessage);
							oos.flush();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					// 在“消息记录”文本框中用蓝色显示发送的消息及发送时间
					String msgRecord = dateFormat.format(new Date()) + "向大家说:"
							+ msgContent + "\r\n";
					addMsgRecord(msgRecord, Color.blue, 12, false, false);
				}
			});
	}

	// 向消息记录文本框中添加一条消息记录
	private void addMsgRecord(final String msgRecord, Color msgColor,
			int fontSize, boolean isItalic, boolean isUnderline) {
		final SimpleAttributeSet attrset = new SimpleAttributeSet();
		StyleConstants.setForeground(attrset, msgColor);
		StyleConstants.setFontSize(attrset, fontSize);
		StyleConstants.setUnderline(attrset, isUnderline);
		StyleConstants.setItalic(attrset, isItalic);
		SwingUtilities.invokeLater(()->{
			Document docs = textPaneMsgRecord.getDocument();
				try {
					docs.insertString(docs.getLength(), msgRecord, attrset);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			});
	}

	// 后台监听线程
	class ListeningHandler implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					Message msg = null;
					synchronized (ois) {
						msg = (Message) ois.readObject();
					}
					if (msg instanceof UserStateMessage) {
						// 处理用户状态消息
						processUserStateMessage((UserStateMessage) msg);
					} else if (msg instanceof ChatMessage) {
						// 处理聊天消息
						processChatMessage((ChatMessage) msg);
					} else {
						// 这种情况对应着用户发来的消息格式 错误，应该发消息提示用户，这里从略
						System.err.println("用户发来的消息格式错误!");
					}
				}
			} catch (IOException e) {
				if (e.toString().endsWith("Connection reset")) {
					System.out.println("服务器端退出");
				} else {
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// 处理用户状态消息
		private void processUserStateMessage(UserStateMessage msg) {
			String srcUser = msg.getSrcUser();
			String dstUser = msg.getDstUser();
			if (msg.isUserOnline()) {
				if (msg.isPubUserStateMessage()) { // 新用户上线消息
					// 用绿色文字将用户名和用户上线时间添加到“消息记录”文本框中
					final String msgRecord = dateFormat.format(new Date())
							+ " " + srcUser + "上线了!\r\n";
					addMsgRecord(msgRecord, Color.green, 12, false, false);
					// 在“在线用户”列表中增加新上线的用户名
					onlinUserDlm.addElement(srcUser);
				}
				if (dstUser.equals(localUserName)) { // 用户在线消息
					onlinUserDlm.addElement(srcUser);
				}
			} else if (msg.isUserOffline()) { // 用户下线消息
				if (onlinUserDlm.contains(srcUser)) {
					// 用绿色文字将用户名和用户下线时间添加到“消息记录”文本框中
					final String msgRecord = dateFormat.format(new Date())
							+ " " + srcUser + "下线了!\r\n";
					addMsgRecord(msgRecord, Color.green, 12, false, false);
					// 在“在线用户”列表中删除下线的用户名
					onlinUserDlm.removeElement(srcUser);
				}
			}
		}

		// 处理服务器转发来的公聊消息
		private void processChatMessage(ChatMessage msg) {
			String srcUser = msg.getSrcUser();
			String dstUser = msg.getDstUser();
			String msgContent = msg.getMsgContent();
			if (onlinUserDlm.contains(srcUser)) {
				if (msg.isPubChatMessage() || dstUser.equals(localUserName)) {
					// 用黑色文字将收到消息的时间、发送消息的用户名和消息内容添加到“消息记录”文本框中
					final String msgRecord = dateFormat.format(new Date())
							+ " " + srcUser + "说: " + msgContent + "\r\n";
					addMsgRecord(msgRecord, Color.black, 12, false, false);
				}
			}
		}
	}
}
