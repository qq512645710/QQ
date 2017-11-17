package com.mingrisoft.system;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

/**
 * Ⱥ����Ϣ����
 *
 */
public class MessageFrame extends JFrame {
	private final ImageIcon successIcon = new ImageIcon(
			MessageFrame.class.getResource("/messSendIcon/Success.gif"));

	private final ImageIcon failIcon = new ImageIcon(
			MessageFrame.class.getResource("/messSendIcon/Fail.gif"));

	private JTextPane textPane;

	private final JLabel stateLabel = new JLabel();// ״̬��

	private final JScrollPane scrollPane = new JScrollPane();

	public MessageFrame() {
		setBounds(100, 100, 307, 383);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);// ���ڶ�����ʾ
		setTitle("Ⱥ����Ϣ");
		setVisible(true);

		getContentPane().add(stateLabel, BorderLayout.SOUTH);
		stateLabel.setText("��ȴ���Ϣ�����");

		getContentPane().add(scrollPane);
		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		textPane.setFont(new Font("", Font.PLAIN, 14));
		textPane.setDragEnabled(true);
		textPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
	}

	/**
	 * �����Ϣ����
	 * 
	 * @param message
	 *            ��Ϣ����
	 * @param success
	 *            �Ƿ�ɹ�������Ϣ
	 */
	public void addMessage(String message, boolean success) {
		textPane.setEditable(true);// �ı��򲻿ɱ༭
		textPane.setCaretPosition(textPane.getDocument().getLength());
		if (success)// �ɹ�������Ϣ
			textPane.insertIcon(successIcon);// ������Ϊ�ɹ�ͼƬ
		else
			textPane.insertIcon(failIcon);// ����ʧ��ͼƬ
		textPane.setCaretPosition(textPane.getDocument().getLength());// ���ı�����λ����Ϊ��ײ�
		textPane.replaceSelection(message + "\n");// �����ı�
		if (!isVisible())
			setVisible(true);
		textPane.setEditable(false);
	}

	/**
	 * ����״̬����Ϣ
	 * 
	 * @param str
	 *            -״̬����Ϣ
	 */
	public void setStateBarInfo(String str) {
		stateLabel.setText(str);
	}
}
