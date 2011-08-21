package osmo.miner.gui;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;

public class MainForm extends JFrame {
	
	public static void main(String[] args) {
		new MainForm();
	}
	
	public MainForm() throws HeadlessException {
		super();
		// TODO Auto-generated constructor stub
		setTitle("name");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.WEST);
		
		JList list = new JList();
		scrollPane.setViewportView(list);
		
		setVisible(true);
	}
}
