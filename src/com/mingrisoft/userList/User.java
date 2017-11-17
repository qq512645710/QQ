package com.mingrisoft.userList;

import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * �û���
 *
 */
public class User {
	private String ip;// �û�IP
	private String host;// �û���ַ����
	private String tipText;// ��ʾ����
	private String name;// �û���
	private String icon;// �û�ͷ���ַ
	private Icon iconImg = null;// �û�ͷ��ͼ��

	/**
	 * �չ��췽��
	 */
	public User() {

	}

	public User(String host, String ip) {
		this.ip = ip;
		this.host = host;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String toString() {
		String strName = getName() == null ? getHost() : getName();
		return strName;
	}

	public String getIcon() {
		return icon;
	}

	/**
	 * ��ȡͷ��ͼƬ
	 * 
	 * @return ͷ��ͼ�����
	 */
	public Icon getIconImg() {
		int faceNum = 1;// ͷ���ţ���ʼֵΪ1
		if (ip != null && !ip.isEmpty()) {
			String[] num = ip.split("\\.");// ���ա�.���ָ��ַ���
			if (num.length == 4) {// �����������4��������ɵ�IP
				Integer num1 = Integer.parseInt(num[2]) + 1;// ��ȡ�ڶ�������+1
				Integer num2 = Integer.parseInt(num[3]);// ��ȡ����������
				faceNum = (num1 * num2) % 11 + 1;// ��������ͨ����ʽ����ó�ͷ��ֵ
			}
		}
		File imageFile = new File("res/NEWFACE/" + faceNum + ".png");// ���������ͷ���ļ�����
		if (!imageFile.exists()) {// ����ļ�������
			imageFile = new File("res/NEWFACE/1.png");// ʹ��Ĭ��ͷ��
		}
		iconImg = new ImageIcon(imageFile.getAbsolutePath());// ���û�ʹ�ô��ڵ�ͷ��
		return iconImg;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTipText() {
		return tipText;
	}

	public void setTipText(String tipText) {
		this.tipText = tipText;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
