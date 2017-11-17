package com.mingrisoft.frame;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.*;

import com.mingrisoft.EQ;
import com.mingrisoft.dao.Dao;
import com.mingrisoft.userList.ChatLog;
import com.mingrisoft.userList.ChatTree;
import com.mingrisoft.userList.User;

/**
 * ���촰��
 *
 */

public class TelFrame extends JFrame {
	private Dao dao;// ���ݿ�ӿ�
	private User user;// �Է��û���
	private JTextPane receiveText = new JTextPane();// �����¼�ı���
	private JTextPane sendText = new JTextPane();// ���������
	private JButton sendButton = new JButton();// ���Ͱ�ť
	private final JButton messageButton = new JButton();// ��Ϣ��¼

	private final static Map<String, TelFrame> instance = new HashMap<String, TelFrame>();
	private JToolBar toolBar = new JToolBar();// ������
	private JToggleButton toolFontButton = new JToggleButton();// ��������
	private JButton toolFaceButton = new JButton();// ʹ�ñ���
	private JButton toolbarSendFile = new JButton();// �����ļ���ť
	private JButton toolbarShakeFrame = new JButton();// ѡ�񷢶���������
	private JButton toolbarCaptureScreen = new JButton();// ��ͼ
	private final JButton hideBtn = new JButton();// ���ز������ť

	private JLabel otherSideInfo = new JLabel();// �Է���Ϣ��ǩ
	// private final JScrollPane infoScrollPane = new JScrollPane();
	private final JLabel label_1 = new JLabel();
	private JPanel panel_3 = new JPanel();// �Ҳ������Ϣ���
	private byte[] buf;// ���ݰ�����
	private DatagramSocket ss;// UDP�׽���
	private String ip;// �Է�IP��ַ
	private DatagramPacket dp;// ���ݱ�
	private TelFrame frame;// ���촰�ڶ���
	private ChatTree tree;// �û�������
	private int rightPanelWidth = 148;

	private final String SHAKING = "c)3a^1]g0";// ������������

	/**
	 * �����촰��
	 * 
	 * @param ssArg
	 *            - UDP���ݰ�
	 * @param dp
	 *            - UDP�׽���
	 * @param treeArg
	 *            - �û���
	 * @return
	 */
	public static synchronized TelFrame getInstance(DatagramSocket ssArg,
			DatagramPacket dp, ChatTree treeArg) {
		InetAddress packetAddress = dp.getAddress();// ��ȡ���ݰ��еĵ�ַ
		String tmpIp = packetAddress.getHostAddress();// ��ȡ��ַ�е�IP�ַ���
		TelFrame frame;// �������촰�ڶ���
		if (!instance.containsKey(tmpIp)) {// ��������ڴ�IP��ַ
			frame = new TelFrame(ssArg, dp, treeArg);// �����µ����촰��
			instance.put(tmpIp, frame);// ��¼��IP��ַ��Ӧ�����촰��
		} else {
			frame = instance.get(tmpIp);// ��ȡ��IP��Ӧ�����촰��
			frame.setBufs(dp.getData());// ��������UDP�׽��ִ�������Ϣ
		}
		frame.receiveInfo();// ��UDP�׽������ݰ��л�ȡ��������
		if (!frame.isVisible()) {// ������岻�ɼ�
			frame.setVisible(true);// ��Ϊ����ɼ�
		}
		frame.setState(JFrame.NORMAL);// ����״̬Ϊ��ͼ��
		frame.toFront();// ������ǰ����ʾ
		return frame;
	}

	/**
	 * ���๹�췽��
	 * 
	 * @param ssArg
	 *            - UDP���ݰ�
	 * @param dpArg
	 *            - UDP�׽���
	 * @param treeArg
	 *            - �û���
	 */
	private TelFrame(DatagramSocket ssArg, DatagramPacket dpArg,
			final ChatTree treeArg) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.tree = treeArg;
		ip = dpArg.getAddress().getHostAddress();//������ݰ����ĸ�IP������
		dao = Dao.getDao();//��ȡ���ݿ�ӿ�
		user = dao.getUser(ip);
		frame = this;
		ss = ssArg;//����UDP�׽���
		dp = dpArg;// �������ݰ�
		buf = dp.getData();// ��ȡUDP�׽��ַ�������Ϣ
		try {
			setBounds(200, 100, 521, 424);
			JSplitPane splitPane = new JSplitPane();
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(splitPane);
			splitPane.setDividerSize(2);
			splitPane.setResizeWeight(0.8);
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setLeftComponent(scrollPane);
			scrollPane.setViewportView(receiveText);
			receiveText.setFont(new Font("����", Font.PLAIN, 12));
			receiveText.setInheritsPopupMenu(true);
			receiveText.setVerifyInputWhenFocusTarget(false);
			receiveText.setDragEnabled(true);
			receiveText.setMargin(new Insets(0, 0, 0, 0));
			receiveText.setEditable(false);
			receiveText.addComponentListener(new ComponentAdapter() {
				public void componentResized(final ComponentEvent e) {
					scrollPane.getVerticalScrollBar().setValue(
							receiveText.getHeight());
				}
			});
			receiveText.setDoubleBuffered(true);

			JPanel receiveTextPanel = new JPanel();

			splitPane.setRightComponent(receiveTextPanel);
			receiveTextPanel.setLayout(new BorderLayout());

			final FlowLayout flowLayout = new FlowLayout();
			flowLayout.setHgap(4);
			flowLayout.setAlignment(FlowLayout.LEFT);
			flowLayout.setVgap(0);
			JPanel buttonPanel = new JPanel();
			receiveTextPanel.add(buttonPanel, BorderLayout.SOUTH);
			final FlowLayout flowLayout_1 = new FlowLayout();
			flowLayout_1.setVgap(3);
			flowLayout_1.setHgap(20);
			buttonPanel.setLayout(flowLayout_1);

			buttonPanel.add(sendButton);
			sendButton.setMargin(new Insets(0, 14, 0, 14));
			sendButton.addActionListener(new sendActionListener());
			sendButton.setText("����");

			buttonPanel.add(messageButton);
			messageButton.setMargin(new Insets(0, 14, 0, 14));
			messageButton.addActionListener(new MessageButtonActionListener());
			messageButton.setText("��Ϣ��¼");

			JPanel toolbarPanel = new JPanel();
			receiveTextPanel.add(toolbarPanel, BorderLayout.NORTH);
			toolbarPanel.setLayout(new BorderLayout());

			ToolbarActionListener toolListener = new ToolbarActionListener();
			toolbarPanel.add(toolBar);
			toolBar.setBorder(new BevelBorder(BevelBorder.RAISED));
			toolBar.setFloatable(false);
			toolBar.add(toolFontButton);
			toolFontButton.addActionListener(toolListener);
			toolFontButton.setFocusPainted(false);
			toolFontButton.setMargin(new Insets(0, 0, 0, 0));
			ImageIcon toolbarFontIcon = new ImageIcon(
					EQ.class.getResource("/image/telFrameImage/toolbarImage/ToolbarFont.png"));
			toolFontButton.setIcon(toolbarFontIcon);
			toolFontButton.setToolTipText("����������ɫ�͸�ʽ");
			toolBar.add(toolFaceButton);
			toolFaceButton.addActionListener(toolListener);
			toolFaceButton.setToolTipText("ѡ�����");
			toolFaceButton.setFocusPainted(false);
			toolFaceButton.setMargin(new Insets(0, 0, 0, 0));

			ImageIcon toolbarFaceIcon = new ImageIcon(
					EQ.class.getResource("/image/telFrameImage/toolbarImage/ToolbarFace.png"));
			toolFaceButton.setIcon(toolbarFaceIcon);
			toolBar.add(toolbarSendFile);

			toolbarSendFile.addActionListener(toolListener);
			toolbarSendFile.setToolTipText("�����ļ�");
			toolbarSendFile.setFocusPainted(false);
			toolbarSendFile.setMargin(new Insets(0, 0, 0, 0));
			ImageIcon toolbarPictureIcon = new ImageIcon(
					EQ.class.getResource("/image/telFrameImage/toolbarImage/ToolbarPicture.png"));
			toolbarSendFile.setIcon(toolbarPictureIcon);

			toolBar.add(toolbarShakeFrame);
			toolbarShakeFrame.setActionCommand("shaking");// ��Ӱ�ť����ָ��Ϊ��������
			toolbarShakeFrame.addActionListener(toolListener);
			toolbarShakeFrame.setToolTipText("���ʹ��ڶ���");
			toolbarShakeFrame.setFocusPainted(false);
			toolbarShakeFrame.setMargin(new Insets(0, 0, 0, 0));
			ImageIcon toolbarShakeIcon = new ImageIcon(
					EQ.class.getResource("/image/telFrameImage/toolbarImage/ToolbarShake.png"));
			 toolbarShakeFrame.setIcon(toolbarShakeIcon);

			toolbarCaptureScreen.setActionCommand("CaptureScreen");// ��Ӱ�ť����ָ��Ϊ��������
			toolbarCaptureScreen.addActionListener(toolListener);
			toolbarCaptureScreen.setToolTipText("��ͼ");
			toolbarCaptureScreen.setFocusPainted(false);
			toolbarCaptureScreen.setMargin(new Insets(0, 0, 0, 0));
			ImageIcon toolbarCaptureScreenIcon = new ImageIcon(
					EQ.class.getResource("/image/telFrameImage/toolbarImage/ToolbarSceneCaptureScreen.png"));
			 toolbarCaptureScreen.setIcon(toolbarCaptureScreenIcon);
			toolBar.add(toolbarCaptureScreen);

			JScrollPane scrollPane_1 = new JScrollPane();
			toolbarPanel.add(hideBtn, BorderLayout.EAST);
			hideBtn.addActionListener(new hideBtnActionListener());
			hideBtn.setMargin(new Insets(0, 0, 0, 0));
			hideBtn.setText(">");

			JPanel sendTextPanel = new JPanel();
			receiveTextPanel.add(sendTextPanel);
			sendTextPanel.setLayout(new BorderLayout());
			sendTextPanel.add(scrollPane_1);
			scrollPane_1
					.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			sendText.setInheritsPopupMenu(true);
			sendText.addKeyListener(new SendTextKeyListener());
			sendText.setVerifyInputWhenFocusTarget(false);
			sendText.setFont(new Font("����", Font.PLAIN, 12));
			sendText.setMargin(new Insets(0, 0, 0, 0));
			sendText.setDragEnabled(true);// �ı����϶�
			sendText.requestFocus();// ��ȡ����
			scrollPane_1.setViewportView(sendText);

			addWindowListener(new TelFrameClosing(tree));

			JScrollPane infoScrollPane = new JScrollPane();
			add(panel_3, BorderLayout.EAST);
			panel_3.setLayout(new BorderLayout());
			panel_3.add(infoScrollPane);
			infoScrollPane
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			infoScrollPane.setViewportView(otherSideInfo);
			otherSideInfo.setIconTextGap(-1);// ͼ����ı�֮��ļ��Ϊ-1����
			String imgPath = EQ.class
					.getResource("/image/telFrameImage/telUserInfo.png") + "";
			// ��ǩʹ��HTML���ı���ʽ��ʾ�Է���Ϣ
			otherSideInfo.setText("<html><body background='" + imgPath
					+ "'><table width='" + rightPanelWidth
					+ "'><tr><td>�û�����<br>&nbsp;&nbsp;" + user.getName()
					+ "</td></tr><tr><td>��������<br>&nbsp;&nbsp;" + user.getHost()
					+ "</td></tr>" + "<tr><td>IP��ַ��<br>&nbsp;&nbsp;"
					+ user.getIp() + "</td></tr><tr><td colspan='2' height="
					+ this.getHeight() * 2
					+ "></td></tr></table></body></html>");

			panel_3.add(label_1, BorderLayout.NORTH);
			label_1.setIcon(new ImageIcon(EQ.class
					.getResource("/image/telFrameImage/telUserImage.png")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		setVisible(true);
		setTitle("�롺" + user + "��ͨѶ��");
	}

	/**
	 * �ô��ڶ���
	 */
	private void shaking() {
		int x = getX();// ��ȡ���������
		int y = getY();// ��ȡ����������
		for (int i = 0; i < 10; i++) {// ѭ��ʮ��
			if (i % 2 == 0) {// ���iΪż��
				x += 5;// �������5
				y += 5;// �������5
			} else {
				x -= 5;// �������5
				y -= 5;// �������5
			}
			setLocation(x, y);// �������ô���λ��
			try {
				Thread.sleep(50);// ����50����
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ȡ�������ݣ�����ӵ������¼�����
	 * 
	 */
	private void receiveInfo() {
		if (buf.length > 0) {
			String rText = new String(buf).replace("" + (char) 0, "");// ��UDP�׽��ֻ�ȡ������תΪ�ַ���
			String hostAddress = dp.getAddress().getHostAddress();// ��ȡ�������ݰ���IP����
			String info = dao.getUser(hostAddress).getName();// ��ȡ��IP��ַ��Ӧ���û���
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ����
			info = info + "  (" + sdf.format(new Date()) + ")";// ����Ϣ�����������
			appendReceiveText(info, Color.BLUE);// ���û���Ϣ׷�ӵ����������
			if (rText.equals(SHAKING)) {
				appendReceiveText("[�Է�������һ����������]\n", Color.RED);// ���û����͵���Ϣ׷�ӵ����������
				shaking();// ���Լ��Ĵ��ڶ���
			} else {
				appendReceiveText(rText + "\n", null);// ���û����͵���Ϣ׷�ӵ����������
				ChatLog.writeLog(user.getIp(), info);// ��¼���͵������¼(�û���)
				ChatLog.writeLog(user.getIp(), rText);// ��¼���͵������¼(��Ϣ)
			}

		}
	}

	/**
	 * ���Ͱ�ť�����¼�
	 *
	 */
	class sendActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {
			String sendInfo = getSendInfo();// ��ȡҪ���͵���Ϣ
			if (sendInfo == null)// �����ϢΪ��
				return;
			insertUserInfoToReceiveText();// �����¼���ڲ��뵱ǰ�û���
			appendReceiveText(sendInfo + "\n", null);// �����¼�������ɫ�ı�
			byte[] tmpBuf = sendInfo.getBytes();// ���ַ�����Ϊ�ֽ�����
			DatagramPacket tdp = null;// ����UDP���ݰ�
			try {
				// ��ʼ�����ݰ����������������飬���鳤�ȣ�Ҫ���͵ĵ�ַ
				tdp = new DatagramPacket(tmpBuf, tmpBuf.length,
						new InetSocketAddress(ip, 1111));
				ss.send(tdp);// UDP�׽��ַ������ݰ�
				ChatLog.writeLog(user.getIp(), sendInfo);// ��¼���͵������¼��������Ϣ�����û�����¼��insertUserInfoToReceiveText()�����У�
			} catch (SocketException e2) {
				e2.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(TelFrame.this, e1.getMessage());
			}
			sendText.setText(null);// ������������
			sendText.requestFocus();// ������ý���
		}
	}

	/**
	 * ��������������
	 *
	 */
	class ToolbarActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {
			String command = e.getActionCommand();// ��ȡ��ť����ָ��
			switch (command) {// �жϰ�ť����ָ��
			case "shaking":// �����ťָ��Ϊ����
				insertUserInfoToReceiveText();// �����¼���ڲ��뵱ǰ�û���
				appendReceiveText("[��������һ���������ڣ�3��֮����ٴη���]\n", Color.GRAY);// �����¼�������ɫ�ı�
				ChatLog.writeLog(user.getIp(), "[���ʹ��嶶������]");// ��¼���͵������¼(�û�)
				sendShakeCommand(e);// ���Ͷ���ָ��
				break;
			case "CaptureScreen":
				new CaptureScreenUtil();
				break;
			default:
				JOptionPane.showMessageDialog(TelFrame.this, "�˹������ڽ����С�");
			}
		}
	}

	/**
	 * ���ʹ��嶶��ָ��
	 * 
	 * @param e
	 *            - ��������ָ������
	 */
	private void sendShakeCommand(ActionEvent e) {
		Thread t = new Thread() {// ���������߳��ڲ���
			public void run() {// ��дrun����
				Component c = (Component) e.getSource();// ��ȡ��������ָ������
				try {
					byte[] tmpBuf = SHAKING.getBytes();// �����������Ϊ�ֽ�����
					// ����UDP���ݰ��� ��ʼ�����ݰ����������������飬���鳤�ȣ�Ҫ���͵ĵ�ַ
					DatagramPacket tdp = new DatagramPacket(tmpBuf,
							tmpBuf.length, new InetSocketAddress(ip, 1111));
					ss.send(tdp);// UDP�׽��ַ������ݰ�;
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					c.setEnabled(false);// ��ֹʹ�ô�������ָ������
					try {
						Thread.sleep(3000);// 3��֮��
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					} finally {
						c.setEnabled(true);// ��������ָ�������ظ���ʹ��״̬
					}
				}
			};
		};
		t.start();// �����߳�
	}

	/**
	 * ����ر��¼�
	 *
	 */
	private final class TelFrameClosing extends WindowAdapter {
		private final JTree tree;

		private TelFrameClosing(JTree tree) {
			this.tree = tree;
		}

		public void windowClosing(final WindowEvent e) {
			tree.setSelectionPath(null);// ����û���ѡ��״̬
			TelFrame.this.setState(ICONIFIED);// ����ͼ�껯
			TelFrame.this.dispose();// ��������
		}
	}

	/**
	 * ��Ϣ��¼��ť�����¼�
	 *
	 */
	private class MessageButtonActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {
			new ChatDialog(frame, user);// ����Ϣ��¼����
		}
	}

	/**
	 * ��ݼ����̼�����
	 */
	private class SendTextKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {// ������¡�Ctrl+Enter����ϼ�
				sendButton.doClick();// �򴥷����Ͱ�ť����
			}
		}
	}

	/**
	 * ���غ�����Ϣ����¼�
	 *
	 */
	private class hideBtnActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (">".equals(hideBtn.getText())) {// �����ť��ʾ>
				hideBtn.setText("<");// �ð�ť��ʾ<
			} else {
				hideBtn.setText(">");// �ð�ť��ʾ>
			}
			panel_3.setVisible(!panel_3.isVisible());// ��������Ϣ��������״̬��Ϊ�෴��״̬
			TelFrame.this.setVisible(true);// �����ʼ�ղ�����
		}
	}


	/**
	 * ���ô����ȡ����Ϣ����
	 * 
	 * @param bufs
	 *            - ��Ϣ����
	 */
	public void setBufs(byte[] bufs) {
		this.buf = bufs;
	}

	/**
	 * ��ȡҪ���͵���Ϣ
	 * 
	 * @return Ҫ���͵���Ϣ
	 */
	public String getSendInfo() {
		String sendInfo = "";
		Document doc = sendText.getDocument();// ��ȡ�������ı�����
		try {
			sendInfo = doc.getText(0, doc.getLength());// ��ȡ�ı�������ַ�������
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		if (sendInfo.equals("")) {// �������Ϊ��
			JOptionPane.showMessageDialog(TelFrame.this, "���ܷ��Ϳ���Ϣ��");
			return null;
		}
		return sendInfo;
	}

	/**
	 * �����¼���ڲ��뵱ǰ�û���
	 */
	private void insertUserInfoToReceiveText() {
		String info = null;
		try {
			String hostAddress = InetAddress.getLocalHost().getHostAddress();// ��ȡ���ص�ַ����
			info = dao.getUser(hostAddress).getName();// ���Ҵ˵�ַ���ƶ�Ӧ���û���
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ����
		info = info + "  (" + sdf.format(new Date()) + ")";// ����Ϣ�����������
		appendReceiveText(info, new Color(68, 184, 29));// �����¼�������ɫ�ı�
		ChatLog.writeLog(user.getIp(), info);// ��¼���͵������¼(�û�)
	}

	public JTextPane getSendText() {
		return sendText;
	}

	/**
	 * �����¼��׷���ı�
	 * 
	 * @param sendInfo
	 *            - ׷���ı�����
	 * @param color
	 *            - �ı���ɫ
	 */
	public void appendReceiveText(String sendInfo, Color color) {
		Style style = receiveText.addStyle("title", null);// ʹ��title����
		if (color != null) {
			StyleConstants.setForeground(style, color);
		} else {
			StyleConstants.setForeground(style, Color.BLACK);
		}
		receiveText.setEditable(true);// �����¼��Ϊ�ɱ༭״̬
		receiveText.setCaretPosition(receiveText.getDocument().getLength());// �趨�ı�����λ��Ϊ�������ݵĵײ�
		receiveText.setCharacterAttributes(style, false);// �����ݵ���ʽ��Ϊstyle��ʽ�����ı�ȫ��
		receiveText.replaceSelection(sendInfo + "\n");// �ڲ����λ���滻�ı�����
		receiveText.setEditable(false);// �����¼��Ϊ���ɱ༭״̬
	}
}
