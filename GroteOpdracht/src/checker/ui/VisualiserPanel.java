package checker.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class VisualiserPanel extends JPanel implements ListSelectionListener {
	private static final long serialVersionUID = 876059617297885756L;
	// JComboBox showMode;
	Visualiser visualiser;
	JLabel location;
	JList<VisualiserRoute> showTrips;
	DefaultListModel<VisualiserRoute> showTripsModel;
	App main;
	ArrayList<VisualiserRoute> routes;

	public VisualiserPanel(App app) {
		super(new BorderLayout());

		this.main = app;

		this.visualiser = new Visualiser(app);

		JScrollPane scrollPane = new JScrollPane(this.visualiser);

		add(scrollPane);

		JPanel lowerPanel = new JPanel();
		add(lowerPanel, "South");

		this.location = new JLabel("zoom: 1x");
		lowerPanel.add(this.location);

		JPanel sidePanel = new JPanel(new BorderLayout());
		add(sidePanel, "East");

		this.showTripsModel = new DefaultListModel<VisualiserRoute>();
		this.showTrips = new JList<VisualiserRoute>(this.showTripsModel);
		ListSelectionModel selectModel = this.showTrips.getSelectionModel();
		selectModel.setSelectionMode(2);
		sidePanel.add(new JScrollPane(this.showTrips), "Center");

		this.showTrips.addListSelectionListener(this);

		JLabel l = new JLabel("Show Selected Trips:");
		sidePanel.add(l, "North");
		
		final JToggleButton toggleText = new JToggleButton("Text invisible", false);
		toggleText.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				if (toggleText.isSelected()) {
					toggleText.setText("Text visible");
				} else {
					toggleText.setText("Text invisible");
				}
				VisualiserPanel.this.visualiser.setTextVisible(toggleText.isSelected());
				VisualiserPanel.this.visualiser.repaintBufferedImage();
			}
		});
		sidePanel.add(toggleText, "South");
		this.visualiser.setJScrollPane(scrollPane);
	}

	public void setRoutes(ArrayList<VisualiserRoute> routes) {
		this.routes = routes;
		this.showTripsModel.removeAllElements();

		for (VisualiserRoute route : routes) {
			this.showTripsModel.addElement(route);
			route.visible = true;
		}

		this.showTrips.setModel(this.showTripsModel);
		this.showTrips.getSelectionModel().setSelectionInterval(0, this.showTripsModel.getSize() - 1);
	}

	public void valueChanged(ListSelectionEvent e) {
		List<VisualiserRoute> selected = this.showTrips.getSelectedValuesList();
		for (VisualiserRoute route : this.routes) {
			route.visible = false;
		}
		for (VisualiserRoute route : selected) {
			route.visible = true;
		}
		this.visualiser.repaintBufferedImage();
	}
}
