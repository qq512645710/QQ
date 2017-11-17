package com.mingrisoft.frame;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.mingrisoft.userList.ChatLog;
import com.mingrisoft.userList.User;

/**
 * �����¼�Ի���
 *
 */
public class ChatDialog extends JDialog {
	/**
	 * ���췽��
	 * 
	 * @param owner
	 *            - չʾ���ĸ���������
	 * @param user
	 *            - չʾ�ĸ��û��������¼
	 */
	public ChatDialog(Frame owner, User user) {
		super(owner, true);// ���ø��๹�췽��
		int x = owner.getX();// ��ȡ����������
		int y = owner.getY();
		setBounds(x + 20, y + 20, 400, 350);// �趨�Ի�������ʹ�С
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);// ����水ť���ٴ���
		setTitle("�롺" + user + "�� ��Ϣ��¼");// �������

		JTextArea area = new JTextArea();// ��ʾ���ݵ��ı���
		area.setEditable(false);// ���ɱ༭
		area.setLineWrap(true);// �Զ�����
		area.setWrapStyleWord(true);// ������в�����

		List<String> logs = ChatLog.readAllLog(user.getIp());// ��ȡ���û�����Ϣ��¼�ļ�
		if (logs.size() == 0) {// ���û���κ���Ϣ
			area.append("(��)");// ��ʾ��
		} else {// ��������Ϣ
			for (String log : logs) {// ������Ϣ����
				area.append(log + "\n");// ���д�ӡ
			}
		}

		JScrollPane scro = new JScrollPane(area);// �����ı���Ĺ������
		scro.doLayout();// ���������²��������ʹ���������ȷ�ж���ײ�λ��
		JScrollBar scroBar = scro.getVerticalScrollBar();// ��ȡ��ֱ����
		scroBar.setValue(scroBar.getMaximum());// ������������

		JPanel mainPanel = new JPanel();// �������������
		mainPanel.setLayout(new BorderLayout());// ���������ñ߽粼��
		mainPanel.add(scro, BorderLayout.CENTER);// �����������������м�
		setContentPane(mainPanel);// ����������������������

		setResizable(false);// ���岻�ɸı�
		setVisible(true);// ��ʾ����
	}
}