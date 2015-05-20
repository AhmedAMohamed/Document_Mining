import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by karim on 4/30/15.
 */
public class View {
	private JFrame frame;
	private Controller controller;

	public View() {
		controller = new Controller();
		setupGUI();

	}

	private void setupGUI() {
		frame = new JFrame("Preprocessing");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		final JButton chooseDocumentButton = new JButton("Choose Documents");
		final JButton outputDirectory = new JButton("Choose Output Directory");
		final JButton startPreprocess = new JButton("Start Preprocessing");

		panel.add(chooseDocumentButton);

		frame.add(panel, BorderLayout.CENTER);

		chooseDocumentButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose documents");
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Txt files", "txt");
				chooser.setFileFilter(filter);
				chooser.setMultiSelectionEnabled(true);
				int returnVal = chooser.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					controller.setFile(chooser.getSelectedFiles());
					panel.remove(chooseDocumentButton);
					panel.add(outputDirectory);
					frame.validate();
					frame.repaint();

				}
			}
		});

		outputDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose output directory");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					controller.setOutPutDir(chooser.getSelectedFile()
							.getAbsolutePath());
					panel.remove(outputDirectory);
					panel.add(startPreprocess);
					frame.validate();
					frame.repaint();

				}
			}
		});

		startPreprocess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				panel.remove(startPreprocess);
				JLabel load = new JLabel("Processing ...");
				panel.add(load);
				frame.validate();
				frame.repaint();
				controller.startPreprocessing();
				panel.remove(load);
				panel.add(new JLabel("Done."));
				frame.validate();
				frame.repaint();

			}
		});

	}

	public void show() {
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		View view = new View();
		view.show();
	}
}
