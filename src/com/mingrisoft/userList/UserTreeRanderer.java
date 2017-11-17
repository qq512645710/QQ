package com.mingrisoft.userList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.mingrisoft.EQ;

/**
 * �û��������ڵ������
 *
 */
public class UserTreeRanderer extends JPanel implements TreeCellRenderer {
	private Icon openIcon;// �ڵ�չ����ʱ��ͼ��
	private Icon closedIcon;// �ڵ�ر�ʱ��ͼ��
	private Icon leafIcon;// �ڵ�Ĭ��ͼ��
	private String tipText = "";// ��ʾ����
	private final JCheckBox label = new JCheckBox();// ��ѡ���ı��û���ʾ�û���
	private final JLabel headImg = new JLabel();// ͷ��
	private static User user;// �û�

	public UserTreeRanderer() {
		super();
		user = null;
	}

	/**
	 * �û��������ڵ�����๹�췽��
	 * 
	 * @param open
	 *            - �ڵ�չ����ʱ��ͼ��
	 * @param closed
	 *            - �ڵ�ر�ʱ��ͼ��
	 * @param leaf
	 *            - �ڵ�Ĭ��ͼ��
	 */
	public UserTreeRanderer(Icon open, Icon closed, Icon leaf) {
		openIcon = open;// �ڵ�չ����ʱ��ͼ��
		closedIcon = closed;// �ڵ�ر�ʱ��ͼ��
		leafIcon = leaf;// �ڵ�Ĭ��ͼ��
		setBackground(new Color(0xF5B9BF));// ���ñ�����ɫ
		label.setFont(new Font("����", Font.BOLD, 14));// ���õ�ѡ������
		URL trueUrl = EQ.class
				.getResource("/image/chexkBoxImg/CheckBoxTrue.png");// ѡ��ͼ��
		label.setSelectedIcon(new ImageIcon(trueUrl));// ���õ�ѡ���ѡ��ͼ��
		URL falseUrl = EQ.class
				.getResource("/image/chexkBoxImg/CheckBoxFalse.png");// δѡ��ͼ��
		label.setIcon(new ImageIcon(falseUrl));// ��ѡ������δѡ��ͼƬ
		label.setForeground(new Color(0, 64, 128));// ��ѡ��������ɫ
		final BorderLayout borderLayout = new BorderLayout();// �����߽粼��
		setLayout(borderLayout);// �ڵ����ʹ�ñ߽粼��
		user = null;// ����û�����
	}

	/**
	 * @param tree
	 * @param value
	 *            - ��ǰ����Ԫ���ֵ
	 * @param selected
	 *            - ��Ԫ����ѡ��
	 * @param expanded
	 *            - �Ƿ�ǰ��չ�ýڵ�
	 * @param leaf
	 *            - �ýڵ��Ƿ�ΪҶ�ڵ�
	 * @param row
	 *            - ����λ��
	 * @param hasFocus
	 *            - �ýڵ��Ƿ�ӵ�н���
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (value instanceof DefaultMutableTreeNode) {// �����Ԫ���ֵ���ڽڵ�ֵ
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;// תΪ�ڵ����
			Object uo = node.getUserObject();// ��ȡ�ڵ��е��û�����
			if (uo instanceof User)// ������ڱ���Ŀ�е��û���
				user = (User) uo;// תΪ�û�����
		} else if (value instanceof User)// �����Ԫ���ֵ���ڱ���Ŀ�е��û���
			user = (User) value;// תΪ�û�����
		if (user != null && user.getIcon() != null) {// ����û�Ϊ�ջ����û�û��ͷ��
			int width = EQ.frame.getWidth();// ��ô�����
			if (width > 0)// �����ȴ���0
				setPreferredSize(new Dimension(width, user.getIconImg()
						.getIconHeight()));// �����û��ڵ������
			headImg.setIcon(user.getIconImg());// ����ͷ��ͼƬ
			tipText = user.getName();// ������ʾ����
		} else {
			if (expanded)// ��չ�ýڵ�ʱ
				headImg.setIcon(openIcon);// ʹ�ýڵ�չ����ʱ��ͼ��
			else if (leaf)// �����Ҷ�ӽڵ�ʱ
				headImg.setIcon(leafIcon);// �ڵ�Ĭ��ͼ��
			else
				// ����
				headImg.setIcon(closedIcon);// �ڵ�ر�ʱ��ͼ��

		}
		add(headImg, BorderLayout.WEST);// ͷ��ŵ��������
		label.setText(value.toString());// ���õ�ѡ���ֵ
		label.setOpaque(false);// �����Ʊ߽�
		add(label, BorderLayout.CENTER);
		if (selected) {// ����ڵ㱻ѡ��
			label.setSelected(true);// ��ѡ��ѡ��
			setBorder(new LineBorder(new Color(0xD46D73), 2, false));// �����߲���
			setOpaque(true);// ���Ʊ߽�
		} else {
			setOpaque(false);// �����Ʊ߽�
			label.setSelected(false);// ��ѡ��δѡ��
			setBorder(new LineBorder(new Color(0xD46D73), 0, false));// �����߲���
		}
		return this;
	}

	/**
	 * ��ȡ��ʾ����
	 */
	public String getToolTipText() {
		return tipText;
	}
}
