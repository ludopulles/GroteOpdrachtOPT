 package UI;
 
 import java.awt.BorderLayout;
 import java.util.ArrayList;
 import javax.swing.DefaultListModel;
 import javax.swing.JComboBox;
 import javax.swing.JLabel;
 import javax.swing.JList;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.ListSelectionModel;
 import javax.swing.event.ListSelectionEvent;
 import javax.swing.event.ListSelectionListener;
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class VisualiserPanel
   extends JPanel
   implements ListSelectionListener
 {
   private static final long serialVersionUID = 876059617297885756L;
   JComboBox showMode;
   Visualiser visualiser;
   JLabel location;
   JList showTrips;
   DefaultListModel showTripsModel;
   App main;
   ArrayList<VisualiserRoute> routes;
   
   public VisualiserPanel(App app)
   {
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
     
     this.showTripsModel = new DefaultListModel();
     this.showTrips = new JList(this.showTripsModel);
     ListSelectionModel selectModel = this.showTrips.getSelectionModel();
     selectModel.setSelectionMode(2);
     sidePanel.add(new JScrollPane(this.showTrips), "Center");
     
     this.showTrips.addListSelectionListener(this);
     
     JLabel l = new JLabel("Show Selected Trips:");
     sidePanel.add(l, "North");
     
     this.visualiser.setJScrollPane(scrollPane);
   }
   
 
 
 
 
   public void setRoutes(ArrayList<VisualiserRoute> routes)
   {
     this.routes = routes;
     
     this.showTripsModel.removeAllElements();
     
     for (VisualiserRoute route : routes)
     {
       this.showTripsModel.addElement(route);
       route.visible = true;
     }
     
     this.showTrips.setModel(this.showTripsModel);
     this.showTrips.getSelectionModel().setSelectionInterval(0, this.showTripsModel.getSize() - 1);
   }
   
 
 
 
   public void valueChanged(ListSelectionEvent e)
   {
     Object[] selected = this.showTrips.getSelectedValues();
     
     for (VisualiserRoute route : this.routes)
     {
       route.visible = false;
     }
     Object[] arrayOfObject1;
     int j = (arrayOfObject1 = selected).length; for (int i = 0; i < j; i++) { Object object = arrayOfObject1[i];
       if ((object instanceof VisualiserRoute))
       {
         VisualiserRoute route = (VisualiserRoute)object;
         route.visible = true;
       }
     }
     this.visualiser.repaintBufferedImage();
   }
 }


