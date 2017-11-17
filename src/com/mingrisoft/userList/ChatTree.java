package com.mingrisoft.userList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.mingrisoft.EQ;
import com.mingrisoft.dao.Dao;

/**
 * �û���
 * 
 * @author Administrator
 *
 */
public class ChatTree extends JTree {
	private DefaultMutableTreeNode root;// �û����ĸ��ڵ�
	private DefaultTreeModel treeModel;// �û�������ģ��
	private List<User> userMap;
	private Dao dao;// ���ݿ�ӿ�
	private EQ eq;// ���������

	public ChatTree(EQ eq) {
		super();
		root = new DefaultMutableTreeNode("root");// �������ڵ�
		treeModel = new DefaultTreeModel(root);// ����ģ����Ӹ��ڵ�
		userMap = new ArrayList<User>();
		dao = Dao.getDao();// ��ʼ�����ݿ�ӿ�
		addMouseListener(new ThisMouseListener());// ����Զ�������¼�
		setRowHeight(50);// ÿһ���߶�Ϊ50����
		setToggleClickCount(2);// ���ýڵ�չ����ر�֮ǰ���ĵ�����Ϊ����
		setRootVisible(false);// ���ڵ㲻�ɼ�
		DefaultTreeCellRenderer defaultRanderer = new DefaultTreeCellRenderer();
		// �����û��������ڵ�����࣬����Ĭ�ϵ�����ͼ��
		UserTreeRanderer treeRanderer = new UserTreeRanderer(
				defaultRanderer.getOpenIcon(), defaultRanderer.getClosedIcon(),
				defaultRanderer.getLeafIcon());
		setCellRenderer(treeRanderer);// �������ڵ���Ⱦ���󣬼������û��������ڵ������
		setModel(treeModel);// �������ڵ�����ģ��
		sortUsers();// �����û��б�
		this.eq = eq;
	}

	/**
	 * �����û��б�
	 */
	private synchronized void sortUsers() {
		new Thread(new Runnable() {// �����߳��ڲ���
					public void run() {
						try {
							Thread.sleep(100);
							root.removeAllChildren();// ���ڵ�ɾ�������û��ڵ�
							String ip = InetAddress.getLocalHost()
									.getHostAddress();// ��ȡ����IP
							User localUser = dao.getUser(ip);// ��ȡ���ϱ���IP���û�
							if (localUser != null) {// ��������û���Ϊ��
								DefaultMutableTreeNode node = new DefaultMutableTreeNode(
										localUser);// ���������û��ڵ�
								root.add(node);// ���Լ���ʾ����λ
							}
							userMap = dao.getUsers();// ��ȡ�����û�
							Iterator<User> iterator = userMap.iterator();// ��ȡ�����û��ĵ�����
							while (iterator.hasNext()) { // ���������ε���
								User user = iterator.next();// ��ȡ�û�����
								if (user.getIp().equals(localUser.getIp())) {// ����뱾���û�IP��ͬ
									continue;// �����˴�ѭ��
								}
								root.add(new DefaultMutableTreeNode(user));// �û������ڵ�����û��ڵ�
							}
							treeModel.reload();// �û���ģ�����¼���
							ChatTree.this.setSelectionRow(0);
							if (eq != null)// ������������Ϊ��
								eq.setStatic("������������" + getRowCount());// ����״̬����Ϣ
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();// �����߳�
	}

	/**
	 * ɾ���û�
	 */
	public void delUser() {
		TreePath path = getSelectionPath();// ������ѡ�ڵ��·����
		if (path == null)// �˽ڵ㲻����
			return;
		User user = (User) ((DefaultMutableTreeNode) path
				.getLastPathComponent()).getUserObject();// ��ȡ�˽ڵ���û�����
		int operation = JOptionPane.showConfirmDialog(this, "ȷ��Ҫɾ���û���" + user
				+ "?", "ɾ���û�", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);// ����ȷ�϶Ի���
		if (operation == JOptionPane.YES_OPTION) {// ѡ��ȷ��
			dao.delUser(user);// ���ݿ�ɾ���û�����
			root.remove((DefaultMutableTreeNode) path.getLastPathComponent());// ���ڵ�ɾ����Ӧ���û��ڵ�
			treeModel.reload();// ���ݿ�ģ��������������
		}
	}

	/**
	 * ����û�
	 * 
	 * @param ip
	 *            - �û�IP
	 * @param opration
	 *            - ���ô˷�������������ҵ��
	 * @return -�Ƿ���ӳɹ�
	 */
	public boolean addUser(String ip, String opration) {
		try {
			if (ip == null)// ���IP���ǿ�
				return false;
			User oldUser = dao.getUser(ip);// ���Ҵ�IP�Ƿ���ڹ�
			if (oldUser == null) {// ������ݿ��в����ڸ��û�
				InetAddress addr = InetAddress.getByName(ip);// ����IP��ַ����
				if (addr.isReachable(1500)) {// �����1500�����ڿ��Ե���õ�ַ
					String host = addr.getHostName();// ��ȡ�����ַ������
					// �����µ��û��ڵ�
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
							new User(host, ip));
					root.add(newNode);// �½ڵ���ӵ��û�����
					User newUser = new User();// �������û�����
					newUser.setIp(ip);// ��¼IP
					newUser.setHost(host);// ��¼�û���ַ��
					newUser.setName(host);// ��¼�û���
					newUser.setIcon("1.gif");// ��¼�û�ͷ��
					dao.addUser(newUser);// �����ݿ�����Ӵ��û���Ϣ
					sortUsers();// �û��б���������
					if (!opration.equals("search"))// ������õĴ˷������ǲ�ѯҵ��
						JOptionPane.showMessageDialog(EQ.frame, "�û�" + host
								+ "��ӳɹ�", "����û�",
								JOptionPane.INFORMATION_MESSAGE);
					return true;

				} else {
					if (!opration.equals("search"))// ������õĴ˷������ǲ�ѯҵ��
						// ��������Ի���
						JOptionPane.showMessageDialog(EQ.frame, "��ⲻ���û�IP��"
								+ ip, "��������û�", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			} else {
				if (!opration.equals("search"))// ������õĴ˷������ǲ�ѯҵ��
					// ��������Ի���
					JOptionPane.showMessageDialog(EQ.frame, "�Ѿ������û�IP" + ip,
							"��������û�", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ��ȡ�û����ڵ�����ģ��
	 * 
	 * @return �û����ڵ�����ģ��
	 */
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	/**
	 * �Զ����������¼�
	 * 
	 */
	private class ThisMouseListener extends MouseAdapter {
		public void mousePressed(final MouseEvent e) {// ��갴���¼�
			if (e.getButton() == 3) {
				TreePath path = getPathForLocation(e.getX(), e.getY());// ��ô������ϵĽڵ�
				if (!isPathSelected(path))// ����˽ڵ�û�б�ѡ��
					setSelectionPath(path);// ѡ�д˽ڵ�
			}
		}
	}
}
