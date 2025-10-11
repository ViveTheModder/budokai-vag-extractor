package gui;
//Budokai VAG Extractor by ViveTheJoestar
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import cmd.Main;

public class App {
	private static File getFileFromChooser(String title, Toolkit defToolkit) {
		File folder = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Choose a folder...");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		while (folder == null) {
			int result = chooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				File tmp = chooser.getSelectedFile();
				if (tmp.isDirectory()) folder = tmp;
				else {
					errorBeep(defToolkit);
					JOptionPane.showMessageDialog(chooser, 
					"Invalid folder path!", title, JOptionPane.ERROR_MESSAGE);
				}
			}
			else if (result == JFileChooser.CANCEL_OPTION) return null;
		}
		return folder;
	}
	private static void errorBeep(Toolkit defToolkit) {
		Runnable runWinErrorSnd = (Runnable) defToolkit.getDesktopProperty("win.sound.exclamation");
		if (runWinErrorSnd!=null) runWinErrorSnd.run();
	}
	public static void setApp() {
		Toolkit defToolkit = Toolkit.getDefaultToolkit();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			File[] folders = new File[2];
			String title = "Budokai VAG Extractor";
			String[] dirTypes = {"Input", "Output"};
			//set components
			Box titleBox = Box.createHorizontalBox();
			Box[] boxes = new Box[2];
			Dimension openBtnSize = new Dimension(48, 24);
			Dimension txtFieldSize = new Dimension(300, 24);
			Font boldFont = new Font("Tahoma", Font.BOLD, 16);
			Font regFont = new Font("Tahoma", Font.PLAIN, 12);
			Font titleFont = new Font("Tahoma", Font.BOLD, 26);
			GridBagConstraints gbc = new GridBagConstraints();
			Image img = defToolkit.getImage(ClassLoader.getSystemResource("img/icon.png"));
			ImageIcon imgIcon = new ImageIcon(img);
			JFrame frame = new JFrame("Budokai VAG Extractor");
			JPanel panel = new JPanel(new GridBagLayout());
			JLabel icon = new JLabel(" "), titleLabel = new JLabel(title);
			JLabel[] labels = new JLabel[2];
			JTextField[] fields = new JTextField[2];
			JButton extract = new JButton("Extract VAGs from AMBs");
			JButton[] openBtns = new JButton[2];
			//set component properties and add components
			extract.setBackground(new Color(188, 172, 215));
			extract.setContentAreaFilled(false);
			extract.setForeground(new Color(98, 82, 127));
			extract.setFont(boldFont);
			extract.setOpaque(true);
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			icon.setIcon(imgIcon);
			titleLabel.setForeground(new Color(78, 86, 136));
			titleLabel.setFont(titleFont);
			titleBox.add(icon);
			titleBox.add(titleLabel);
			panel.add(titleBox,gbc);
			panel.add(new JLabel(" "),gbc);
			panel.setBackground(new Color(180, 205, 219));
			for (int i=0; i<2; i++) {
				final int index = i;
				boxes[i] = Box.createHorizontalBox();
				labels[i] = new JLabel(dirTypes[i]+" Directory");
				labels[i].setForeground(new Color(46, 46, 101));
				labels[i].setFont(boldFont);
				String placeholder = "Enter path to "+labels[i].getText()+" or click [+] button...";
				fields[i] = new JTextField(placeholder);
				fields[i].setFont(regFont);;
				fields[i].setForeground(Color.GRAY);
				fields[i].setMinimumSize(txtFieldSize);
				fields[i].setMaximumSize(txtFieldSize);
				fields[i].setPreferredSize(txtFieldSize);
				fields[i].addFocusListener(new FocusListener() {
					@Override
					public void focusGained(FocusEvent e) {
						if (fields[index].getText().equals(placeholder)) {
							fields[index].setText("");
							fields[index].setForeground(Color.BLACK);
						}
					}
					@Override
					public void focusLost(FocusEvent e) {
						if (fields[index].getText().isEmpty()) {
							fields[index].setText(placeholder);
							fields[index].setForeground(Color.GRAY);
						}
					}
				});
				openBtns[i] = new JButton("+");
				openBtns[i].setBackground(new Color(64, 134, 174));
				openBtns[i].setContentAreaFilled(false);
				openBtns[i].setForeground(new Color(29, 54, 68));
				openBtns[i].setFont(boldFont);
				openBtns[i].setMinimumSize(openBtnSize);
				openBtns[i].setMaximumSize(openBtnSize);
				openBtns[i].setPreferredSize(openBtnSize);
				openBtns[i].setOpaque(true);
				openBtns[i].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							folders[index] = getFileFromChooser(title, defToolkit);
							if (folders[index] != null) {
								fields[index].setBackground(Color.LIGHT_GRAY);
								fields[index].setEditable(false);
								fields[index].setForeground(Color.BLACK);
								fields[index].setText(folders[index].getCanonicalPath());
							}
							else {
								fields[index].setBackground(Color.WHITE);
								fields[index].setEditable(true);
								fields[index].setText("");
							}
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				});
				boxes[i].add(fields[i]);
				boxes[i].add(Box.createHorizontalStrut(16));
				boxes[i].add(openBtns[i]);
				panel.add(labels[i], gbc);
				panel.add(new JLabel(" "), gbc);
				panel.add(boxes[i],gbc);
				panel.add(new JLabel(" "), gbc);
			}
			//add one last action listener
			extract.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String err = "";
					String[] dirTypes = {"input", "output"};
					/* since JTextFields can be edited (without clicking the [+] button),
					   it is likely for the edited text to lead to an invalid path */
					for (int i=0; i<2; i++) {
						String text = fields[i].getText();
						File tmp = new File(text);
						if (!tmp.isDirectory()) 
							err += "Invalid (or no) " + dirTypes[i] + " directory!\n";
						else folders[i] = tmp;
					}
					File[] dirs = Main.getAmbDirs(folders[0]);
					if (dirs != null && dirs.length == 0) err += "No AMBs found in input directory!";
					if (err.equals("")) setProgress(frame, defToolkit, title, folders, dirs);
					else {
						errorBeep(defToolkit);
						JOptionPane.showMessageDialog(null, err, title, JOptionPane.ERROR_MESSAGE);
					}
				}
			});
			//add remaining components
			panel.add(new JLabel(" "),gbc);
			panel.add(extract, gbc);
			frame.add(panel);
			//set frame properties
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setIconImage(img);
			frame.setSize(512, 384);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		} catch (Exception e) {
			errorBeep(defToolkit);
			JOptionPane.showMessageDialog(null, e.getClass().getSimpleName() + ": "
			+ e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);		
		}
	}
	public static void setProgress(JFrame f, Toolkit tk, String title, File[] folders, File[] dirs) {
		String[] labelText = {"AMB Progress", "VAG Progress", "VAG Extraction Log"};
		//change progress bar background color and text color
		UIManager.put("ProgressBar.background", new Color(188, 188, 188));
		UIManager.put("ProgressBar.foreground", new Color(1, 155, 68));
		UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
		UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
		//set components
		Box scrollBox = Box.createHorizontalBox();
		Dimension barSize = new Dimension(300, 30);
		Font boldFont = new Font("Tahoma", Font.BOLD, 16);
		Font regFont = new Font("Tahoma", Font.PLAIN, 12);
		JDialog dialog = new JDialog();
		JLabel[] labels = new JLabel[3];
		for (int i=0; i<3; i++) {
			labels[i] = new JLabel(labelText[i]);
			labels[i].setFont(boldFont);
			labels[i].setForeground(new Color(78, 86, 136));
			labels[i].setHorizontalAlignment(JLabel.CENTER);
			labels[i].setAlignmentX(Component.CENTER_ALIGNMENT);
		}
		JPanel panel = new JPanel();
		JProgressBar[] bars = {new JProgressBar(), new JProgressBar()};
		JTextArea textArea = new JTextArea(20, 20);
		JScrollPane scroll = new JScrollPane(textArea);
		//set component properties
		bars[0].setMaximum(dirs.length);
		for (int i=0; i<2; i++) {
			bars[i].setMinimumSize(barSize);
			bars[i].setMaximumSize(barSize);
			bars[i].setPreferredSize(barSize);
			bars[i].setStringPainted(true);
			bars[i].setFont(regFont);
		}
		panel.setBackground(new Color(180, 205, 219));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollBox.add(Box.createHorizontalGlue());
        scrollBox.add(scroll);
        scrollBox.add(Box.createHorizontalGlue());
		textArea.setEditable(false);
		textArea.setFont(regFont);
		//add components
		panel.add(labels[0]);
		panel.add(bars[0]);
		panel.add(labels[1]);
		panel.add(bars[1]);
		panel.add(labels[2]);
		panel.add(scrollBox);
		panel.add(Box.createVerticalGlue());
		dialog.add(panel);
		//set dialog properties
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setSize(512, 512);
		dialog.setLocationRelativeTo(f);
		dialog.setTitle(title);
		dialog.setVisible(true);
		//set and execute background worker
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				f.setEnabled(false);
				double time = Main.extract(folders[1], dirs, bars, textArea, false);
				f.setEnabled(true);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				String msg = dirs.length + " files have had their VAGs extracted in " + time + " s!";
				tk.beep();
				JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
				return null;
			}
		};
		worker.execute();
		//add window listener (in case the user wants to interrupt VAG extraction)
		dialog.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				if (!f.isEnabled()) {
					String msg = "Aborting the VAG extraction process. Is this OK?";
					String[] options = {"Yes", "No"};
					int result = JOptionPane.showOptionDialog(
						null, msg, title, JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
					if (result == JOptionPane.YES_OPTION) System.exit(3);
				}
			}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}	
		});
	}
}