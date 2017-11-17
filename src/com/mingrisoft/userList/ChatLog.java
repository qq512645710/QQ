package com.mingrisoft.userList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * �����¼��д������
 *
 */
public class ChatLog {

	/**
	 * ��¼������־
	 * 
	 * @param userIP
	 *            - �Է�IP��ַ
	 * @param message
	 *            - ��¼����Ϣ
	 */
	static public void writeLog(String userIP, String message) {
		File rootdir = new File("db_EQ/chatdata/");// �������ݿ�Ŀ¼����Ŀ¼����
		if (!rootdir.exists()) {// �������ļ��в�����
			rootdir.mkdirs();// ��������ļ���
		}
		File log = new File(rootdir, userIP + ".chat");// ������־�ļ���������Ϊ���û�IP.chat��
		if (!log.exists()) {// ����ļ�������
			try {
				log.createNewFile();// �������ļ�
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("�����¼�ļ��޷�������" + log.getAbsolutePath());
			}
		}
		// ����try���鴴��IO������
		try (FileOutputStream fos = new FileOutputStream(log, true);// �����ļ�����ֽ���������Դ�ļ�֮��׷��������
				OutputStreamWriter os = new OutputStreamWriter(fos);// ���ֽ���תΪ�ַ���
				BufferedWriter bw = new BufferedWriter(os);// ���������ַ���
		) {
			bw.write(message);// д����־��Ϣ
			bw.newLine();// ���һ������
			bw.flush();// �ַ���ˢ��
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ�����¼
	 * 
	 * @param userIP
	 *            - ��ȡ����һ��IP��ص������¼
	 * @return - �����¼����
	 */
	static public List<String> readAllLog(String userIP) {
		List<String> logs = new LinkedList<String>();// ������¼����
		File rootdir = new File("db_EQ/chatdata/");// �������ݿ�Ŀ¼����Ŀ¼����
		if (!rootdir.exists()) {// �������ļ��в�����
			rootdir.mkdirs();// ��������ļ���
		}
		File log = new File(rootdir, userIP + ".chat");// ������־�ļ���������Ϊ���û�IP.chat��
		if (!log.exists()) {// ����ļ�������
			return logs;// ���ؿռ���
		}
		// ����try���鴴��IO������
		try (FileInputStream fis = new FileInputStream(log);// �����ļ������ֽ���
				InputStreamReader is = new InputStreamReader(fis);// ���ֽ���תΪ�ַ���
				BufferedReader br = new BufferedReader(is);// ���������ַ���
		) {
			String oneLine = null;// ���������ȡ����ʱ�ַ���
			while ((oneLine = br.readLine()) != null) {// ��ȡ�ַ�����һ�����ݣ������һ�����ݲ�Ϊnull�Ļ�
				logs.add(oneLine);// ��Ϣ������Ӵ˼�¼
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logs;// ������Ϣ����
	}
}
