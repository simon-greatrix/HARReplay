package replay;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class AppWindow {

	private JFrame frmHarReplay;

	/**
	 * Launch the application.
	 */
	public static void startup() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppWindow window = new AppWindow();
					window.frmHarReplay.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AppWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmHarReplay = new JFrame();
		frmHarReplay.setTitle("HAR Replay");
		frmHarReplay.setBounds(100, 100, 450, 300);
		frmHarReplay.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmHarReplay.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JMenuBar menuBar = new JMenuBar();
		frmHarReplay.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNewSuite = new JMenuItem("New Suite");
		mnFile.add(mntmNewSuite);
		
		JMenuItem mntmLoadSuite = new JMenuItem("Load Suite");
		mnFile.add(mntmLoadSuite);
		
		JMenuItem mntmSaveSuite = new JMenuItem("Save Suite");
		mnFile.add(mntmSaveSuite);
		
		mnFile.addSeparator();
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		
		JMenu mnRun = new JMenu("Run");
		menuBar.add(mnRun);
		
		JMenuItem mntmConfiguration_1 = new JMenuItem("Configuration");
		mnRun.add(mntmConfiguration_1);
		mnRun.addSeparator();
		
		JMenuItem mntmStart = new JMenuItem("Start");
		mnRun.add(mntmStart);
		
		JMenuItem mntmPause = new JMenuItem("Pause");
		mnRun.add(mntmPause);
		
		JMenuItem mntmRestart = new JMenuItem("Restart");
		mntmRestart.setEnabled(false);
		mnRun.add(mntmRestart);
		
		JMenuItem mntmStop = new JMenuItem("Stop");
		mnRun.add(mntmStop);
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		JMenuItem mntmSummary = new JMenuItem("Summary");
		mnView.add(mntmSummary);
		
		JMenuItem mntmDetails = new JMenuItem("Details");
		mnView.add(mntmDetails);
		
		JMenuItem mntmConfiguration = new JMenuItem("Alerts");
		mnView.add(mntmConfiguration);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmHelp = new JMenuItem("Help");
		mnHelp.add(mntmHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
	}

}
