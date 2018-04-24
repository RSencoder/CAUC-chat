package login;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import java.awt.Font;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import java.awt.Component;

import com.cauc.chat.Client;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.SwingConstants;
import java.awt.GridLayout;

/*登陆界面*/

public class RegistDemo extends JFrame {
	private JFrame jf;
	private JTextField uname;
	private JPasswordField pw1;

	public RegistDemo() {
		setTitle("\u7528\u6237\u767B\u5F55");
		jf = this;
		// new JFrame("用户登陆");
		Container container = jf.getContentPane();
		jf.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		JPanel JUsrNamePanel = new JPanel();
		jf.getContentPane().add(JUsrNamePanel);
		JUsrNamePanel.setLayout(null);
		
		/* 标签 用户名 */
		JLabel jl0 = new JLabel(" \u7528\u6237\u540D  ");
		jl0.setBounds(21, 10, 146, 30);
		jl0.setHorizontalAlignment(SwingConstants.CENTER);
		JUsrNamePanel.add(jl0);
		jl0.setFont(new Font("仿宋", Font.BOLD, 18));

		Component horizontalStrut = Box.createHorizontalStrut(100);
		horizontalStrut.setBounds(145, 10, 100, 30);
		JUsrNamePanel.add(horizontalStrut);

		uname = new JTextField();
		uname.setBounds(268, 16, 124, 29);
		uname.setColumns(20);
		JUsrNamePanel.add(uname);

		JPanel JPassPanel = new JPanel();
		jf.getContentPane().add(JPassPanel);
		JPassPanel.setLayout(null);
		/* 标签 密码 */
		JLabel jl1 = new JLabel("  \u5BC6   \u7801   ");
		jl1.setBounds(21, 0, 146, 40);
		jl1.setHorizontalAlignment(SwingConstants.CENTER);
		JPassPanel.add(jl1);
		jl1.setFont(new Font("仿宋", Font.BOLD, 18));

		Component horizontalStrut_1 = Box.createHorizontalStrut(100);
		horizontalStrut_1.setBounds(146, 0, 100, 40);
		JPassPanel.add(horizontalStrut_1);
		
		pw1 = new JPasswordField();
		pw1.setBounds(268, 10, 124, 30);
		pw1.setColumns(20);
		JPassPanel.add(pw1);
		pw1.setEchoChar('*');

		JPanel JControlPanel = new JPanel();
		jf.getContentPane().add(JControlPanel);
		JControlPanel.setLayout(null);
		JButton bl = new JButton("\u767B\u5F55");
		bl.setBounds(96, 17, 91, 23);
		JControlPanel.add(bl);
		JButton b2 = new JButton("注册");
		b2.setBounds(247, 17, 91, 23);
		JControlPanel.add(b2);
		b2.addActionListener((e) -> {
			UserDatabase dao = new UserDatabase();
			boolean ok = dao.insertUser(uname.getText().trim(), pw1.getText().trim());
			if (!ok) {
     			JOptionPane.showMessageDialog(new Frame(), "用户已存在！！！", null, JOptionPane.INFORMATION_MESSAGE);
				return;
			} else if (ok) {
				/* 登陆后跳转的界面 */
				JOptionPane.showConfirmDialog(new JFrame(), "注册成功", "jh", 2);
			}
		});
		bl.addActionListener((e) -> {
			// TODO Auto-generated method stub
			// new MyJDialog().setVisible(true);
			// JOptionPane.showConfirmDialog(new JFrame(), "确定登录", "jh", 2);
			UserDatabase dao = new UserDatabase();
			boolean ok = dao.checkUserPassword(uname.getText().trim(), pw1.getText().trim());
			if (!ok) {
				JOptionPane.showMessageDialog(new Frame(), "不存在该用户", null, JOptionPane.INFORMATION_MESSAGE);
				return;
			} else if (ok) {
				/* 登陆后跳转的界面 */
				JOptionPane.showConfirmDialog(new JFrame(), "确定登录", "jh", 2);
				Client client = new Client();
				jf.setVisible(false);
				client.setVisible(true);
			}
		});
		jf.setSize(470, 190);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				// RegistDemo frame =
				new RegistDemo().setLocation(400, 500);
				// frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}