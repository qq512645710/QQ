package com.mingrisoft;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.mingrisoft.dao.Dao;
import com.mingrisoft.frame.TelFrame;
import com.mingrisoft.system.Resource;
import com.mingrisoft.userList.ChatTree;
import com.mingrisoft.userList.User;

public class EQ extends Dialog {
	public static EQ frame = null;// �����屾�����
	private JTextField ipEndTField;// IP������Χ����ֵ
	private JTextField ipStartTField;// IP������Χ��ʼֵ
	private ChatTree chatTree;// �û��б���
	private JPopupMenu popupMenu;// ����Ҽ��˵�������ʽ�˵���
	private JTabbedPane tabbedPane;// ����ǩ���
	private JToggleButton searchUserButton;//
	private JProgressBar progressBar;// ����Ҽ��˵�������ʽ�˵���
	private JList faceList;// �����񼯺�
	private JButton selectInterfaceOKButton;// ȷ������Ч����ť
	private DatagramSocket ss;// UDP�׽���
	private final JLabel stateLabel;// �ײ�״̬����ǩ
	private Rectangle location;// ����λ�ö���
	public static TrayIcon trayicon;// ϵͳ����ͼ��
	private Dao dao;// ���ݿ�ӿ�
	public final static Preferences preferences = Preferences.systemRoot();// ������ѡ�����ʹ��ϵͳ�ĸ���ѡ��ڵ㡣�˶�����Ա�������ƫ������
	private JButton userInfoButton;// �û���Ϣ��ť

	public static void main(String args[]) {
		try {
			String laf = preferences.get("lookAndFeel", "javaĬ��");
			if (laf.contains("��ǰϵͳ"))// ����ַ�����������ǰϵͳ������
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());// ��ȡ�����Ĵ������
			EQ frame = new EQ();
			frame.setVisible(true);
			frame.SystemTrayInitial();// ��ʼ��ϵͳ��
			frame.server();// ����������
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���๹�췽��
	 */
	public EQ() {
		super(new Frame());// ���ø��෽��������һ���ո��ര��
		frame = this;
		dao = Dao.getDao();// ��ȡ���ݿ�ӿڶ���
		location = dao.getLocation();// ��ȡ���ݿ��е�λ��
		setTitle("��������ϵͳ");
		setBounds(location);// ָ�����ڴ�С������λ��
		progressBar = new JProgressBar();
		progressBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		tabbedPane = new JTabbedPane();
		popupMenu = new JPopupMenu();
		chatTree = new ChatTree(this);
		stateLabel = new JLabel(); // ״̬����ǩ
		addWindowListener(new FrameWindowListener());// ��Ӵ��������
		addComponentListener(new ComponentAdapter() {// ������������
			public void componentResized(final ComponentEvent e) {// ����ı��Сʱ
				saveLocation();// ����������λ�õķ���
			}

			public void componentMoved(final ComponentEvent e) {// �����ƶ�ʱ
				saveLocation();// ����������λ�õķ���
			}
		});
		try {
			ss = new DatagramSocket(1111);// ����ͨѶ����˿�
		} catch (SocketException e2) {
			if (e2.getMessage().startsWith("Address already in use"))
				JOptionPane.showMessageDialog(this, "����˿ڱ�ռ��,���߱�����Ѿ����С�");
			System.exit(0);
		}
		final JPanel BannerPanel = new JPanel();
		BannerPanel.setLayout(new BorderLayout());
		add(BannerPanel, BorderLayout.NORTH);
		userInfoButton = new JButton();
		BannerPanel.add(userInfoButton, BorderLayout.WEST);
		userInfoButton.setMargin(new Insets(0, 0, 0, 10));
		initUserInfoButton();// ��ʼ�������û�ͷ��ť

		add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.setTabPlacement(SwingConstants.LEFT);
		ImageIcon userTicon = new ImageIcon(EQ.class.getResource("/image/tabIcon/tabLeft.PNG"));
		tabbedPane.addTab(null, userTicon, createUserList(), "�û��б�");
		ImageIcon sysOTicon = new ImageIcon(EQ.class.getResource("/image/tabIcon/tabLeft2.PNG"));
		tabbedPane.addTab(null, sysOTicon, createSysToolPanel(), "ϵͳ����");
		setAlwaysOnTop(true);
	}

	/**
	 * �û��б����
	 * 
	 * @return
	 */

	private JScrollPane createUserList() {// �û��б����
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		addUserPopup(chatTree, getPopupMenu());// Ϊ�û���ӵ����˵�
		scrollPane.setViewportView(chatTree);// ���û�����ӵ����������
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		chatTree.addMouseListener(new ChatTreeMouseListener());
		return scrollPane;
	}

	/**
	 * ϵͳ�������
	 * 
	 * @return
	 */
	private JScrollPane createSysToolPanel() {
		JPanel sysToolPanel = new JPanel(); // ϵͳ�������
		sysToolPanel.setLayout(new BorderLayout());
		JScrollPane sysToolScrollPanel = new JScrollPane();
		sysToolScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sysToolScrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		sysToolScrollPanel.setViewportView(sysToolPanel);
		sysToolPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

		JPanel interfacePanel = new JPanel();
		sysToolPanel.add(interfacePanel, BorderLayout.NORTH);
		interfacePanel.setLayout(new BorderLayout());
		interfacePanel.setBorder(new TitledBorder("����ѡ��-�ٴ�������Ч"));
		faceList = new JList(new String[] { "��ǰϵͳ", "javaĬ��" });
		interfacePanel.add(faceList);
		faceList.setBorder(new BevelBorder(BevelBorder.LOWERED));
		final JPanel interfaceSubPanel = new JPanel();
		interfaceSubPanel.setLayout(new FlowLayout());
		interfacePanel.add(interfaceSubPanel, BorderLayout.SOUTH);
		selectInterfaceOKButton = new JButton("ȷ��");
		selectInterfaceOKButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (faceList.getSelectedValue() == null) {
					JOptionPane.showMessageDialog(EQ.this, "��δѡ���κ�����", "��ʾ", JOptionPane.ERROR_MESSAGE);
					return;
				}
				preferences.put("lookAndFeel", faceList.getSelectedValue().toString());
				JOptionPane.showMessageDialog(EQ.this, "�������б��������Ч");
			}
		});
		interfaceSubPanel.add(selectInterfaceOKButton);

		JPanel searchUserPanel = new JPanel(); // �û��������
		sysToolPanel.add(searchUserPanel);
		searchUserPanel.setLayout(new BorderLayout());
		final JPanel searchControlPanel = new JPanel();
		searchControlPanel.setLayout(new GridLayout(0, 1));
		searchUserPanel.add(searchControlPanel, BorderLayout.SOUTH);
		final JList searchUserList = new JList(new String[] { "����û��б�" });// ������û��б�
		final JScrollPane scrollPane_2 = new JScrollPane(searchUserList);
		scrollPane_2.setDoubleBuffered(true);
		searchUserPanel.add(scrollPane_2);
		searchUserList.setBorder(new BevelBorder(BevelBorder.LOWERED));
		searchUserButton = new JToggleButton();
		searchUserButton.setText("�������û�");
		searchUserButton.addActionListener(new SearchUserActionListener(searchUserList));
		searchControlPanel.add(progressBar);
		searchControlPanel.add(searchUserButton);
		searchUserPanel.setBorder(new TitledBorder("�����û�"));

		final JPanel ipPanel = new JPanel();
		final GridLayout gridLayout_2 = new GridLayout(0, 1);
		gridLayout_2.setVgap(5);// ���֮����Ϊ5����
		ipPanel.setLayout(gridLayout_2);
		ipPanel.setMaximumSize(new Dimension(600, 90));
		ipPanel.setBorder(new TitledBorder("IP������Χ"));
		final JPanel panel_5 = new JPanel();
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.X_AXIS));
		ipPanel.add(panel_5);
		panel_5.add(new JLabel("��ʼIP��"));
		ipStartTField = new JTextField("192.168.0.1");
		panel_5.add(ipStartTField);
		final JPanel panel_6 = new JPanel();
		panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.X_AXIS));
		ipPanel.add(panel_6);
		panel_6.add(new JLabel("��ֹIP��"));
		ipEndTField = new JTextField("192.168.1.255");
		panel_6.add(ipEndTField);
		sysToolPanel.add(ipPanel, BorderLayout.SOUTH);

		stateLabel.setText("��������" + chatTree.getRowCount());
		return sysToolScrollPanel;
	}

	/**
	 * ��ʼ���û���Ϣ��ť
	 */
	private void initUserInfoButton() {
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();// ��ȡ���ص�ַ����
			User user = dao.getUser(ip);// �����ݿ��л�ȡ�����û�����
			userInfoButton.setIcon(user.getIconImg());
			userInfoButton.setText(user.getName());
			userInfoButton.setIconTextGap(JLabel.RIGHT);
			userInfoButton.setToolTipText(user.getTipText());
			userInfoButton.getParent().doLayout();// ���������²���
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * �����û��¼�
	 *
	 */
	class SearchUserActionListener implements ActionListener {
		private final JList list;// ����û��б�

		SearchUserActionListener(JList list) {
			this.list = list;
		}

		public void actionPerformed(ActionEvent e) {
			// IP��ַ��������ʽ
			String regex = "(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
			String ipStart = ipStartTField.getText().trim();// ��ÿ�ʼIP�ĵ�ַ
			String ipEnd = ipEndTField.getText().trim();// ��ý���IP�ĵ�ַ
			if (ipStart.matches(regex) && ipEnd.matches(regex)) {// �������IP��ַ�����ϸ�ʽҪ��
				if (searchUserButton.isSelected()) {// �����ť��ѡ��״̬
					searchUserButton.setText("ֹͣ����");// ��ť�ı���Ϊ
					new Thread(new Runnable() {
						public void run() {
							// �����û�
							Resource.searchUsers(chatTree, progressBar, list, searchUserButton, ipStart, ipEnd);
						}
					}).start();// �����߳�
				} else {
					searchUserButton.setText("�������û�");
				}
			} else {
				JOptionPane.showMessageDialog(EQ.this, "����IP��ַ��ʽ", "ע��", JOptionPane.WARNING_MESSAGE);
				searchUserButton.setSelected(false);// ����ť��Ϊδѡ��״̬
			}
		}
	}

	/**
	 * �û��б�ļ�����
	 * 
	 * @author Administrator
	 *
	 */
	private class ChatTreeMouseListener extends MouseAdapter {
		public void mouseClicked(final MouseEvent e) {
			if (e.getClickCount() == 2) {// �����ʱ˫��ʱ
				TreePath path = chatTree.getSelectionPath();// ����û����ϱ�ѡ�еĽڵ�·��
				if (path == null)// ����ǿյ�
					return;// ֹͣ�˷���
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();// ���ش�·���ϵ��������ת��Ϊ�ڵ�
				User user = (User) node.getUserObject();// ���ڵ��е��û���Ϣȡ����
				try {
					DatagramPacket packet = new DatagramPacket(new byte[0], 0, InetAddress.getByName(user.getIp()),
							1111);// ����һ���յ�UDP���ݰ�
					TelFrame.getInstance(ss, packet, chatTree);// �����������촰��
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * ����������
	 */
	private void server() {// ��������������
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (ss != null) {
						byte[] buf = new byte[4096];// �������ݰ��ֽ�����
						DatagramPacket dp = new DatagramPacket(buf, buf.length);// �������ݰ�
						try {
							ss.receive(dp);// �������ݰ�
							TelFrame.getInstance(ss, dp, chatTree);// �������촰��
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}
			}
		}).start();// �����߳�
	}

	/**
	 * �Ҽ������˵�����û�
	 * 
	 * @param component
	 *            - �����Ҽ������
	 * @param popup
	 *            - ����ʽ�˵�
	 */
	private void addUserPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {// ���̧��ʱ
				if (e.isPopupTrigger())// ����������ǵ���ʽ�˵�
					showMenu(e);// չʾ�Ҽ��˵���
			}

			/**
			 * չʾ�Ҽ��˵���
			 */
			private void showMenu(MouseEvent e) {
				if (chatTree.getSelectionPaths() == null) {// ���û��ѡ���κ��û�
					popupMenu.getComponent(0).setEnabled(false);// �رո�������
					popupMenu.getComponent(2).setEnabled(false);// �ر�ɾ������
					popupMenu.getComponent(3).setEnabled(false);// �ر�Ⱥ������
					popupMenu.getComponent(4).setEnabled(false);// �رշ���������Դ����
				} else {
					if (chatTree.getSelectionPaths().length < 2) {// ���ѡ�е��û�����������
						popupMenu.getComponent(3).setEnabled(false);
					} else {
						popupMenu.getComponent(3).setEnabled(true);// ����Ⱥ������
					}
					popupMenu.getComponent(0).setEnabled(true);// ������������
					popupMenu.getComponent(2).setEnabled(true);// ����ɾ������
					popupMenu.getComponent(4).setEnabled(true);// ��������������Դ����
				}
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	/**
	 * ����������λ�õķ���
	 */
	private void saveLocation() {
		location = getBounds();
		dao.updateLocation(location);// ���浱ǰ����λ��
	}

	/**
	 * �����Ҽ������˵�
	 * 
	 * @return
	 */
	protected JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.setOpaque(false);
		}
		final JMenuItem rename = new JMenuItem();
		popupMenu.add(rename);
		rename.addActionListener(new RenameActionListener());
		rename.setText("����");
		final JMenuItem addUser = new JMenuItem();
		addUser.addActionListener(new AddUserActionListener());
		popupMenu.add(addUser);
		addUser.setText("����û�");
		final JMenuItem delUser = new JMenuItem();
		delUser.addActionListener(new delUserActionListener());
		popupMenu.add(delUser);
		delUser.setText("ɾ���û�");
		final JMenuItem messagerGroupSend = new JMenuItem();
		messagerGroupSend.addActionListener(new messagerGroupSendActionListener());
		messagerGroupSend.setText("��ʹȺ��");
		popupMenu.add(messagerGroupSend);
		final JMenuItem accessComputerFolder = new JMenuItem("����������Դ");
		accessComputerFolder.setActionCommand("computer");
		popupMenu.add(accessComputerFolder);
		accessComputerFolder.addActionListener(new accessFolderActionListener());
		return popupMenu;
	}

	/**
	 * ����״̬����Ϣ
	 * 
	 * @param str
	 */
	public void setStatic(String str) {
		if (stateLabel != null)
			stateLabel.setText(str);
	}

	/**
	 * ��ʾ�����Ի���
	 * 
	 * @param str
	 *            - ������
	 * @return ������
	 */
	private String showInputDialog(String str) { // ��ʾ����Ի���
		String newName = JOptionPane.showInputDialog(this, "<html>����<font color=red>" + str + "</font>��������</html>");// ������Ի������ó��ı����Կ���������ɫ
		return newName;
	}

	/**
	 * ���ʶԷ�������Դ�ļ���
	 *
	 */
	private class accessFolderActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {
			TreePath path = chatTree.getSelectionPath();// ��ȡѡ�еĽڵ�·��
			if (path == null)// ����ڵ�·����Ϊ��
				return;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();// ��ȡ�ڵ�·���ϵĽڵ�
			User user = (User) node.getUserObject();// ��ȡ�ڵ��б�����û�
			String ip = "\\\\" + user.getIp();// ƴд�����ļ��е�ַ
			String command = e.getActionCommand();// ��ȡ�������ָ��
			if (command.equals("computer")) {// ����Ƿ��ʶԷ���Դ
				Resource.startFolder(ip);// �����������ļ�
			}
		}
	}

	/**
	 * �����û����¼�
	 *
	 */
	private class RenameActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {
			TreePath path = chatTree.getSelectionPath();// ��ȡѡ�еĽڵ�·��
			if (path == null)// ����ڵ�·����Ϊ��
				return;
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();// ��ȡ�ڵ�·���ϵĽڵ�
			User user = (User) node.getUserObject();// ��ȡ�ڵ��б�����û�
			String newName = showInputDialog(user.getName());// �����û�������������û���
			if (newName != null && !newName.isEmpty()) {// ��������ֲ�Ϊ��
				user.setName(newName);// �����û���Ϣ
				dao.updateUser(user);// �������ݿ����û�����
				DefaultTreeModel model = (DefaultTreeModel) chatTree.getModel();// ��ȡ�û���ģ��
				model.reload();// ���¼����û���
				chatTree.setSelectionPath(path);// �ָ�ѡ��״̬
				initUserInfoButton();// ��ʼ���û���Ϣ��ť
			}
		}
	}

	/**
	 * ����ر��¼�
	 * 
	 */
	private class FrameWindowListener extends WindowAdapter {
		public void windowClosing(final WindowEvent e) {// ����ر�ʱ
			setVisible(false);// ���ش���
		}
	}

	/**
	 * ����û������¼�
	 *
	 */
	private class AddUserActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {
			String ip = JOptionPane.showInputDialog(EQ.this, "�������û�IP��ַ");
			if (ip != null)
				chatTree.addUser(ip, "add");
		}
	}

	/**
	 * ɾ���û������¼�
	 *
	 */
	private class delUserActionListener implements ActionListener {
		public void actionPerformed(final ActionEvent e) {
			chatTree.delUser();
		}
	}

	/**
	 * Ⱥ����Ϣ�����¼�
	 *
	 */
	private class messagerGroupSendActionListener implements ActionListener {// ��ʹȺ��
		public void actionPerformed(final ActionEvent e) {
			String systemName = System.getProperty("os.name");
			if (systemName != null && systemName.equals("Windows 7")) {
				String message = JOptionPane.showInputDialog(EQ.this, "������Ⱥ����Ϣ", "��ʹȺ��",
						JOptionPane.INFORMATION_MESSAGE);
				if (message != null && !message.equals("")) {
					TreePath[] selectionPaths = chatTree.getSelectionPaths();// ��ȡ�û�����ͬʱѡ�е��û��ڵ�
					Resource.sendGroupMessenger(ss, selectionPaths, message);
				} else if (message != null && message.isEmpty()) {
					JOptionPane.showMessageDialog(EQ.this, "���ܷ��Ϳ���Ϣ��");
				}
			} else {
				JOptionPane.showMessageDialog(EQ.this, "�˹���ֻ����Windows 7����ʹ�ã�", "��ʾ", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	/**
	 * ϵͳ����ʼ��
	 */
	private void SystemTrayInitial() {
		if (!SystemTray.isSupported()) // �жϵ�ǰϵͳ�Ƿ�֧��ϵͳ��
			return;
		try {
			String title = "��ҵQQ";// ϵͳ��֪ͨ����
			String company = "����ʡXXX�Ƽ����޹�˾";// ϵͳ֪ͨ������
			SystemTray sysTray = SystemTray.getSystemTray();// ��ȡϵͳĬ������
			Image image = Toolkit.getDefaultToolkit().getImage(EQ.class.getResource("/icons/sysTray.png"));// ����ϵͳ��ͼ��
			trayicon = new TrayIcon(image, title + "\n" + company, createMenu());
			trayicon.setImageAutoSize(true);
			trayicon.addActionListener(new SysTrayActionListener());
			sysTray.add(trayicon);
			trayicon.displayMessage(title, company, MessageType.INFO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ������С���˵���
	 * 
	 * @return
	 */
	private PopupMenu createMenu() {
		PopupMenu menu = new PopupMenu();
		MenuItem exitItem = new MenuItem("�˳�");
		exitItem.addActionListener(new ActionListener() { // ϵͳ���˳��¼�
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		MenuItem openItem = new MenuItem("��");
		openItem.addActionListener(new ActionListener() {// ϵͳ���򿪲˵����¼�
			public void actionPerformed(ActionEvent e) {
				if (!isVisible()) {
					setVisible(true);
					toFront();
				} else
					toFront();
			}
		});
		menu.add(openItem);
		menu.addSeparator();
		menu.add(exitItem);
		return menu;
	}

	/**
	 * ��С��ϵͳ��˫���¼�
	 *
	 */
	class SysTrayActionListener implements ActionListener {// ϵͳ��˫���¼�
		public void actionPerformed(ActionEvent e) {
			setVisible(true);
			toFront();
		}
	}
}