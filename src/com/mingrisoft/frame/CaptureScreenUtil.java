package com.mingrisoft.frame;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import com.mingrisoft.EQ;

/**
 * ��ͼ������ ���౾����һ��JWindow���壬����Ḳ��������Ļ������֮ǰ�Ჶ���������Ӱ��ͼƬ�������еĲ�����ʵ���ڴ˴����ϵġ�
 *
 */
public class CaptureScreenUtil extends JWindow {
	private int startX, startY;// ��궨λ�Ŀ�ʼ����
	private int endX, endy;// ��궨λ�Ľ�������
	private BufferedImage screenImage = null;// ����ȫ��ͼƬ
	private BufferedImage tempImage = null;// �޸ĺ�ɰ���ɫ��ȫ��ͼƬ
	private BufferedImage saveImage = null;// ��ͼ
	private ToolsWindow toolWindow = null;// ����������
	private Toolkit tool = null;// ������߰�

	public CaptureScreenUtil() {
		tool = Toolkit.getDefaultToolkit();// ����ϵͳ��Ĭ��������߰�
		Dimension d = tool.getScreenSize();// ��ȡ��Ļ�ߴ磬����һ����ά�������
		setBounds(0, 0, d.width, d.height);// ���ý�ͼ��������ʹ�С

		Robot robot;// ����Java�Զ���������
		try {
			robot = new Robot();
			Rectangle fanwei = new Rectangle(0, 0, d.width, d.height);// ��������Χ��
			screenImage = robot.createScreenCapture(fanwei);// ��׽�������������������ɵ�ͼ��
			addAction();// ��Ӷ�������
			setVisible(true);// ����ɼ�
		} catch (AWTException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "��ͼ�����޷�ʹ��", "����",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * ��Ӷ�������
	 */
	private void addAction() {
		// ��ͼ�����������¼�����
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {// ��갴��ʱ
				startX = e.getX();// ��¼��ʱ��������
				startY = e.getY();// ��¼��ʱ���������

				if (toolWindow != null) {// �����������������Ѵ���
					toolWindow.setVisible(false);// �ù�������������
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {// ����ɿ�ʱ
				if (toolWindow == null) {// ������������������null
					toolWindow = new ToolsWindow(e.getX(), e.getY());// �����µĹ���������
				} else {
					toolWindow.setLocation(e.getX(), e.getY());// ָ����������������Ļ�ϵ�λ��
				}
				toolWindow.setVisible(true);// ����������ʾ
				toolWindow.toFront();// �����������ö�
			}
		});
		// ��ͼ������������ק�¼�����
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {// ����걻���²���קʱ
				// ��¼����϶��켣
				endX = e.getX();// ������
				endy = e.getY();// ������

				// ��ʱͼ�����ڻ�����Ļ���������Ļ��˸
				Image backgroundImage = createImage(getWidth(), getHeight());// ��������ͼ��
				Graphics g = backgroundImage.getGraphics();// ��ñ���ͼ��Ļ�ͼ����
				g.drawImage(tempImage, 0, 0, null);// �ڱ����л��ư���ɫ����ĻͼƬ
				int x = Math.min(startX, endX);// �������ʼλ�úͽ���λ����һ����С��
				int y = Math.min(startY, endy);// �������ʼλ�úͽ���λ����һ����С��
				int width = Math.abs(endX - startX) + 1;// ͼƬ��С���Ϊ1����
				int height = Math.abs(endy - startY) + 1;// ͼƬ��С�߶�Ϊ1����
				g.setColor(Color.BLUE);// ʹ����ɫ���ʻ��߿�
				g.drawRect(x - 1, y - 1, width + 1, height + 1);// ��һ�����Σ�����һ�����صľ����ñ߿������ʾ
				saveImage = screenImage.getSubimage(x, y, width, height);// ��ͼȫ��ͼƬ
				g.drawImage(saveImage, x, y, null);// �ڱ����л��ƽ�ȡ����ͼƬ
				getGraphics().drawImage(backgroundImage, 0, 0,
						CaptureScreenUtil.this);// ����ͼ��
			}
		});
	}

	/**
	 * �������
	 */
	public void paint(Graphics g) {
		RescaleOp ro = new RescaleOp(0.5f, 0, null);// ����RescaleOp����������ͼƬÿ����ɫ����ɫƫ���ɫƫ����Ϊ0.5f(��ɫ)
		tempImage = ro.filter(screenImage, null);// ����ĻͼƬ��ÿ�����ؽ�����ɫ������������ʱ��ͼƬ����
		g.drawImage(tempImage, 0, 0, this);// ���»��ư���ɫ����ĻͼƬ
	}

	/**
	 * ����ǰ��ͼ���浽����
	 */
	public void saveImage() {
		JFileChooser jfc = new JFileChooser();// �����ļ�������
		jfc.setDialogTitle("����ͼƬ");// �����ļ�ѡ��������
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG",
				"jpg");// �����ļ���������ֻ��ʾ.jpg��׺��ͼƬ
		jfc.setFileFilter(filter);// �ļ�ѡ����ʹ�ù�����
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");// �������ڸ�ʽ����
		String fileName = sdf.format(new Date());// ����ǰ������Ϊ�ļ���
		FileSystemView view = FileSystemView.getFileSystemView();// ��ȡϵͳ�ļ���ͼ��
		File filePath = view.getHomeDirectory();// ��ȡ����·��
		File saveFile = new File(filePath, fileName + ".jpg");// ����Ҫ�������ͼƬ�ļ�
		jfc.setSelectedFile(saveFile);// ���ļ�ѡ������Ĭ��ѡ���ļ���ΪsaveFile
		int flag = jfc.showSaveDialog(this);// ���������е����ļ�ѡ��������ȡ�û�������
		if (flag == JFileChooser.APPROVE_OPTION) {// ���ѡ�е��Ǳ��水ť
			try {
				ImageIO.write(saveImage, "jpg", saveFile);// ����jpg��ʽ��ͼƬ�ļ�
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "�ļ��޷����棡", "����",
						JOptionPane.ERROR);
			} finally {
				disposeAll();// �������н�ͼ����
			}
		}
	}

	/**
	 * �ѽ�ͼ������а�
	 */
	private void imagetoClipboard() {
		// ��������ӿڵĶ���ʹ�ýӿڱ������ڲ���
		Transferable trans = new Transferable() {
			@Override
			/**
			 * ���ؽ�Ҫ������Ķ����������������
			 */
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}

			@Override
			/**
			 * �жϲ�������������Ƿ��������Ҫ�������
			 */
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);// ����Ĳ����Ƿ����ͼƬ����
			}

			@Override
			/**
			 * ���ؽ�Ҫ������Ķ���
			 */
			public Object getTransferData(DataFlavor flavor)
					throws UnsupportedFlavorException, IOException {
				if (isDataFlavorSupported(flavor)) {// ����������������ͼƬ����
					return saveImage;// ���ؽ�ͼ
				}
				return null;
			}
		};
		Clipboard clipboard = tool.getSystemClipboard();// ���ϵͳ���а����
		clipboard.setContents(trans, null);// ����ǰ��ͼ������а�
	}

	/**
	 * ����������
	 */
	private class ToolsWindow extends JWindow {
		/**
		 * ���������幹�췽��
		 * 
		 * @param x
		 *            - ��������ʾ�ĺ�����
		 * @param y
		 *            - ��������ʾ�ĺ�����
		 */
		public ToolsWindow(int x, int y) {
			setLocation(x, y);// �趨��������Ļ����ʾ��λ��

			JPanel mainPanel = new JPanel();// ���������
			mainPanel.setLayout(new BorderLayout());// ����ʹ�ñ߽粼��

			JToolBar toolBar = new JToolBar();// ������
			toolBar.setFloatable(false); // �����������϶�
			JButton saveButton = new JButton();// ���水ť
			Icon saveIcon = new ImageIcon(
					EQ.class.getResource("/image/telFrameImage/CaptureScreen/save.png"));
			saveButton.setIcon(saveIcon);
			saveButton.addActionListener(new ActionListener() {// ��ť����¼�
						@Override
						public void actionPerformed(ActionEvent e) {
							saveImage();// ���浱ǰ��ͼ
						}
					});
			toolBar.add(saveButton);// ��������Ӱ��o

			JButton closeButton = new JButton();// �رհ�ť
			Icon closeIcon = new ImageIcon(
					EQ.class.getResource("/image/telFrameImage/CaptureScreen/close.png"));
			closeButton.setIcon(closeIcon);
			closeButton.addActionListener(new ActionListener() {// ��ť����¼�
						@Override
						public void actionPerformed(ActionEvent e) {
							disposeAll();// ����ȫ������
						}
					});
			toolBar.add(closeButton);

			JButton copyButton = new JButton();// ��ͼƬ������а尴ť
			Icon copyIcon = new ImageIcon(
					EQ.class.getResource("/image/telFrameImage/CaptureScreen/copy.png"));
			copyButton.setIcon(copyIcon);// ����ͼ��
			copyButton.addActionListener(new ActionListener() {// ��ť����¼�
						public void actionPerformed(ActionEvent e) {
							imagetoClipboard();// ����ǰ��ͼ������а�
							disposeAll();// ����ȫ������
						}
					});
			toolBar.add(copyButton);// ��������Ӵ˰�ť

			mainPanel.add(toolBar, BorderLayout.NORTH);// �����߷��������ϲ�
			setContentPane(mainPanel);// ����������������
			pack();// �Զ����������С
			setVisible(true);// ��ʾ����

		}

	}

	/**
	 * �������н�ͼ����
	 */
	public void disposeAll() {
		toolWindow.dispose();// ���ٹ���������
		dispose();// ���ٹ����ര��
	}

	public static void main(String[] args) {
		new CaptureScreenUtil();
	}
}
