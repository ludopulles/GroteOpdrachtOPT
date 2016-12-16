package checker.ui;

import java.awt.BorderLayout;
import java.awt.Panel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import checker.validator.WarningCollector;

public class ConsolePanel extends Panel {
	private static final long serialVersionUID = 8229545165107754493L;
	public JTextArea console;
	public final WarningCollector warningCollector;
	App app;

	public ConsolePanel(App app) {
		super(new BorderLayout());

		this.app = app;

		this.console = new JTextArea();
		this.console.setEditable(false);

		this.console.setText("=== Grote Opdracht Checker ===");

		this.warningCollector = new WarningCollector();
		this.warningCollector.console = this.console;

		add(new JScrollPane(this.console));
	}

	public void addLine(String line) {
		this.console.append("\n" + line);
	}

	public void clearConsole() {
		this.console.setText("=== Grote Opdracht Checker ===");
	}
}
