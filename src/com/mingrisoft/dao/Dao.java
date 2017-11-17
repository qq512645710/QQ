package com.mingrisoft.dao;

import java.awt.Rectangle;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import com.mingrisoft.userList.User;

/**
 * ���ݿ�ӿ��� ����ʹ��΢�����ݿ⡪��Derby�����������ļ��ᱣ������Ŀ��Ŀ¼���Զ����ɵġ�db_EQ���ļ�����
 *
 */
public class Dao {
	private static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";// ���ݿ�������ʹ��derby���ݿ�
	private static String url = "jdbc:derby:db_EQ";// ���ݿ�URL
	private static Connection conn = null;// ���ݿ�����
	private static Dao dao = null;// ���ݿ�ӿڶ��󣬲��õ���ģʽ

	private Dao() {
		try {
			Class.forName(driver);// �������ݿ�����
			if (!dbExists()) {// ������ݿ����
				conn = DriverManager.getConnection(url + ";create=true");// ���Ӳ��������ݿ�
				createTable();// �������ݱ��
			} else
				conn = DriverManager.getConnection(url);// ֱ���������ݿ�
			addDefUser();// ���������û�
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "���ݿ������쳣�����߱�����Ѿ����С�");
			System.exit(0);// ������ֹ
		}
	}

	/**
	 * �������ݿ��Ƿ����
	 * 
	 * @return ���ݿ��Ƿ����
	 */
	private boolean dbExists() {
		boolean bExists = false;// Ĭ�����ݿⲻ����
		File dbFileDir = new File("db_EQ");// ���ݿ��ļ���
		if (dbFileDir.exists()) {// ������ڴ��ļ���
			bExists = true;// ���ݿ����
		}
		return bExists;
	}

	/**
	 * ��ȡDAO����
	 * 
	 * @return DAOʵ������
	 */
	public static Dao getDao() {
		if (dao == null)// ������ݿ�����ǿյ�
			dao = new Dao();// �����µ����ݿ����
		return dao;
	}

	/**
	 * ��ȡ�����û�
	 * 
	 * @return ��ȡ�����û��ļ���
	 */
	public List<User> getUsers() {
		List<User> users = new ArrayList<User>();
		try {
			String sql = "select * from tb_users";
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while (rs.next()) {
				User user = new User();
				user.setIp(rs.getString(1));
				user.setHost(rs.getString(2));
				user.setName(rs.getString(3));
				user.setTipText(rs.getString(4));
				user.setIcon(rs.getString(5));
				users.add(user);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}

	/**
	 * ��ȡָ��IP���û�
	 * 
	 * @param ip
	 *            - IP��ַ
	 * @return �鵽���û���Ϣ
	 */
	public User getUser(String ip) {
		String sql = "select * from tb_users where ip=?";
		User user = null;
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, ip);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				user = new User();
				user.setIp(rs.getString(1));
				user.setHost(rs.getString(2));
				user.setName(rs.getString(3));
				user.setTipText(rs.getString(4));
				user.setIcon(rs.getString(5));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return user;
	}

	/**
	 * ����û�
	 * 
	 * @param user
	 *            - ����ӵ��û�
	 */
	public void addUser(User user) {
		try {
			String sql = "insert into tb_users values(?,?,?,?,?)";
			PreparedStatement ps = null;
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getIp());
			ps.setString(2, user.getHost());
			ps.setString(3, user.getName());
			ps.setString(4, user.getTipText());
			ps.setString(5, user.getIcon());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �޸��û�
	 * 
	 * @param user
	 *            - ���޸ĵ��û�
	 */
	public void updateUser(User user) {
		try {
			String sql = "update tb_users set host=?,name=?,tooltip=?,icon=? where ip='"
					+ user.getIp() + "'";
			PreparedStatement ps = null;
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getHost());
			ps.setString(2, user.getName());
			ps.setString(3, user.getTipText());
			ps.setString(4, user.getIcon());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ɾ���û�
	 * 
	 * @param user
	 *            -��ɾ�����û�
	 */
	public void delUser(User user) {
		try {
			String sql = "delete from tb_users where ip=?";
			PreparedStatement ps = null;
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getIp());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��������������Ļ����ʾ��λ��
	 * 
	 * @param location
	 *            - ��Ļλ�ö���
	 */
	public void updateLocation(Rectangle location) {
		String sql = "update tb_location set xLocation=?,yLocation=?,width=?,height=?";
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, location.x);
			pst.setInt(2, location.y);
			pst.setInt(3, location.width);
			pst.setInt(4, location.height);
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ����λ��
	 * 
	 * @return ��Ļλ�ö���
	 */
	public Rectangle getLocation() {
		Rectangle rec = new Rectangle(100, 0, 240, 500);
		String sql = "select * from tb_location";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				rec.x = rs.getInt(1);
				rec.y = rs.getInt(2);
				rec.width = rs.getInt(3);
				rec.height = rs.getInt(4);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rec;
	}

	/**
	 * �������ݱ��
	 */
	private void createTable() {
		String createUserSql = "CREATE TABLE tb_users ("
				+ "ip varchar(16) primary key," + "host varchar(30),"
				+ "name varchar(20)," + "tooltip varchar(50),"
				+ "icon varchar(50))";
		String createLocationSql = "CREATE TABLE tb_location ("
				+ "xLocation int," + "yLocation int," + "width int,"
				+ "height int)";
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(createUserSql);
			stmt.execute(createLocationSql);
			addDefLocation();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���������û�
	 */
	public void addDefUser() {
		try {
			InetAddress local = InetAddress.getLocalHost();// ����������������
			User user = new User();// ���������û�
			user.setIp(local.getHostAddress());
			user.setHost(local.getHostName());
			user.setName(local.getHostName());
			user.setTipText(local.getHostAddress());
			user.setIcon("1.gif");// ����ͷ��
			if (getUser(user.getIp()) == null) {
				addUser(user);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���Ĭ�ϴ���λ��
	 */
	public void addDefLocation() {
		String sql = "insert into tb_location values(?,?,?,?)";
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, 100);
			pst.setInt(2, 0);
			pst.setInt(3, 240);
			pst.setInt(4, 500);
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
