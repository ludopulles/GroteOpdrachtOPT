/*     */ package UI;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.util.ArrayList;
/*     */ import javax.swing.DefaultListModel;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class VisualiserPanel
/*     */   extends JPanel
/*     */   implements ListSelectionListener
/*     */ {
/*     */   private static final long serialVersionUID = 876059617297885756L;
/*     */   JComboBox showMode;
/*     */   Visualiser visualiser;
/*     */   JLabel location;
/*     */   JList showTrips;
/*     */   DefaultListModel showTripsModel;
/*     */   App main;
/*     */   ArrayList<VisualiserRoute> routes;
/*     */   
/*     */   public VisualiserPanel(App app)
/*     */   {
/*  42 */     super(new BorderLayout());
/*     */     
/*  44 */     this.main = app;
/*     */     
/*  46 */     this.visualiser = new Visualiser(app);
/*     */     
/*  48 */     JScrollPane scrollPane = new JScrollPane(this.visualiser);
/*     */     
/*  50 */     add(scrollPane);
/*     */     
/*  52 */     JPanel lowerPanel = new JPanel();
/*  53 */     add(lowerPanel, "South");
/*     */     
/*     */ 
/*  56 */     this.location = new JLabel("zoom: 1x");
/*  57 */     lowerPanel.add(this.location);
/*     */     
/*  59 */     JPanel sidePanel = new JPanel(new BorderLayout());
/*  60 */     add(sidePanel, "East");
/*     */     
/*  62 */     this.showTripsModel = new DefaultListModel();
/*  63 */     this.showTrips = new JList(this.showTripsModel);
/*  64 */     ListSelectionModel selectModel = this.showTrips.getSelectionModel();
/*  65 */     selectModel.setSelectionMode(2);
/*  66 */     sidePanel.add(new JScrollPane(this.showTrips), "Center");
/*     */     
/*  68 */     this.showTrips.addListSelectionListener(this);
/*     */     
/*  70 */     JLabel l = new JLabel("Show Selected Trips:");
/*  71 */     sidePanel.add(l, "North");
/*     */     
/*  73 */     this.visualiser.setJScrollPane(scrollPane);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setRoutes(ArrayList<VisualiserRoute> routes)
/*     */   {
/*  82 */     this.routes = routes;
/*     */     
/*  84 */     this.showTripsModel.removeAllElements();
/*     */     
/*  86 */     for (VisualiserRoute route : routes)
/*     */     {
/*  88 */       this.showTripsModel.addElement(route);
/*  89 */       route.visible = true;
/*     */     }
/*     */     
/*  92 */     this.showTrips.setModel(this.showTripsModel);
/*  93 */     this.showTrips.getSelectionModel().setSelectionInterval(0, this.showTripsModel.getSize() - 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void valueChanged(ListSelectionEvent e)
/*     */   {
/* 101 */     Object[] selected = this.showTrips.getSelectedValues();
/*     */     
/* 103 */     for (VisualiserRoute route : this.routes)
/*     */     {
/* 105 */       route.visible = false;
/*     */     }
/*     */     Object[] arrayOfObject1;
/* 108 */     int j = (arrayOfObject1 = selected).length; for (int i = 0; i < j; i++) { Object object = arrayOfObject1[i];
/* 109 */       if ((object instanceof VisualiserRoute))
/*     */       {
/* 111 */         VisualiserRoute route = (VisualiserRoute)object;
/* 112 */         route.visible = true;
/*     */       }
/*     */     }
/* 115 */     this.visualiser.repaintBufferedImage();
/*     */   }
/*     */ }


/* Location:              D:\Downloads\Checker.jar!\UI\VisualiserPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */