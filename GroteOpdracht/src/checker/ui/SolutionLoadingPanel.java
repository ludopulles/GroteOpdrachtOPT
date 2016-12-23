
package checker.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import checker.validator.DefaultProblem;
import checker.validator.Solution;
import checker.validator.WarningCollector;

public class SolutionLoadingPanel extends JPanel implements ActionListener, DocumentListener {

	private static final long serialVersionUID = -7610449508243232732L;
	App app;
	JButton check;
	JButton load;
	JTextArea area;
	JTextPane lineNumbers;
	String latestDir;
	int currentLines = 1;

	public SolutionLoadingPanel(App app) {
		super(new BorderLayout());
		this.app = app;
		JPanel upperPanel = new JPanel(new BorderLayout());
		add(upperPanel, "North");
		JPanel buttons = new JPanel();
		this.check = new JButton("Check solution");
		this.check.setPreferredSize(new Dimension(120, 35));
		this.check.addActionListener(this);
		this.load = new JButton("Load solution");
		this.load.setPreferredSize(new Dimension(120, 35));
		this.load.addActionListener(this);
		buttons.add(this.load);
		buttons.add(this.check);
		upperPanel.add(buttons, "East");
		upperPanel.add(new JLabel("Copy your solution file here:"), "Center");
		this.area = new JTextArea();
		JScrollPane areaScroll = new JScrollPane(this.area);
		this.area.getDocument().addDocumentListener(this);
		this.lineNumbers = new JTextPane();
		this.lineNumbers.setBackground(Color.lightGray);
		String tekst = "1: ";
		this.lineNumbers.setText(tekst);
		StyledDocument doc = this.lineNumbers.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, 2);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		areaScroll.setRowHeaderView(this.lineNumbers);
		add(areaScroll, "Center");
	}

	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		if (source == this.check) {
			checkAction();
		} else if (source == this.load) {
			try {
				loadAction();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadAction() throws IOException {
		FileDialog d = new FileDialog(this.app, "open solutionfile", 0);
		if (this.latestDir == null) {
			d.setDirectory("");
		} else {
			d.setDirectory(this.latestDir);
		}
		d.setVisible(true);
		String fileName = d.getFile();
		if (fileName == null) {
			return;
		}
		this.latestDir = d.getDirectory();
		File f = new File(this.latestDir + fileName);
		FileReader fr = new FileReader(f);
		String data = "";
		try (BufferedReader br = new BufferedReader(fr)) {
			String line = br.readLine();
			while (line != null) {
				data += line + "\n";
				line = br.readLine();
			}
			data = data == null ? "" : data;
		}
		this.area.setText(data);
	}

	private void checkAction() {
		WarningCollector wc = this.app.console.warningCollector;
		wc.clearWarnings();
		this.app.console.clearConsole();
		this.app.console.addLine("> Reading solution from textarea.");
		
		Solution solution = new Solution(this.area.getText(), DefaultProblem.getDefaultProblem(), wc);
		this.setSolution(solution);
	}
	
	public void setSolution(Solution solution) {
		WarningCollector wc = this.app.console.warningCollector;
		this.app.solution = solution;
		if (wc.gotWarnings()) {
			this.app.console.addLine(
					"> Reading solution terminated because of an error,\n> please fix it before handing in the assignment.");
			this.app.solution = null;
		} else {
			boolean feasible = solution.checkScore(wc);
			if (feasible) {
				if (wc.gotWarnings()) {
					this.app.console.addLine(
							"\n> Found feasible solution with warnings.\n> You might want to fix them before handing in.");
				} else {
					this.app.console.addLine("\n> Found feasible solution.");
				}
			} else {
				this.app.console.addLine(
						"\n> The solution is infeasible, see above errors why.\n> please make sure the resulting solution is feasible in your program.");
			}
			this.app.console.addLine("\n> Done Checking solution.");
		}
		this.app.visualiser.visualiser.setSolution(this.app.solution);
		this.app.showPanel(this.app.console);
		this.app.repaint();
	}

	public void updateLines() {
		int lines = this.area.getLineCount();
		if (lines != this.currentLines) {
			this.currentLines = lines;
			String s = "1: ";
			for (int i = 2; i <= lines; i++) {
				s = s + "\n" + i + ": ";
			}
			this.lineNumbers.setText(s);
		}
	}

	public void changedUpdate(DocumentEvent arg0) {
		updateLines();
	}

	public void insertUpdate(DocumentEvent arg0) {
		updateLines();
	}

	public void removeUpdate(DocumentEvent arg0) {
		updateLines();
	}
}
