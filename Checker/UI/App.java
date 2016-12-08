/*    */ package UI;
/*    */ 
/*    */ import Validator.Solution;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Component;
/*    */ import java.awt.event.WindowAdapter;
/*    */ import java.awt.event.WindowEvent;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JTabbedPane;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class App
/*    */   extends JFrame
/*    */ {
/*    */   private static final long serialVersionUID = -6881859037929489424L;
/*    */   JTabbedPane tabbedPane;
/*    */   static final String header = "=== Grote Opdracht Checker ===";
/*    */   static final String consoleString = "console";
/*    */   static final String solutionTextString = "solution text";
/*    */   static final String visuliserString = "visualiser";
/*    */   public ConsolePanel console;
/*    */   public SollutionLoadingPanel solutionText;
/*    */   public VisualiserPanel visualiser;
/*    */   Solution solution;
/*    */   
/*    */   public static void main(String[] args)
/*    */   {
/* 40 */     new App().setVisible(true);
/*    */   }
/*    */   
/*    */   public App()
/*    */   {
/* 45 */     setLayout(new BorderLayout());
/*    */     
/* 47 */     this.tabbedPane = new JTabbedPane();
/* 48 */     add(this.tabbedPane, "Center");
/*    */     
/* 50 */     this.console = new ConsolePanel(this);
/* 51 */     this.tabbedPane.addTab("console", this.console);
/*    */     
/* 53 */     this.solutionText = new SollutionLoadingPanel(this);
/* 54 */     this.tabbedPane.addTab("solution text", this.solutionText);
/*    */     
/* 56 */     this.visualiser = new VisualiserPanel(this);
/* 57 */     this.tabbedPane.addTab("visualiser", this.visualiser);
/*    */     
/* 59 */     setSize(600, 500);
/*    */     
/*    */ 
/* 62 */     addWindowListener(new WindowAdapter()
/*    */     {
/*    */ 
/*    */       public void windowClosing(WindowEvent e)
/*    */       {
/* 67 */         System.exit(0);
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void showPanel(Component c)
/*    */   {
/* 78 */     this.tabbedPane.setSelectedComponent(c);
/*    */   }
/*    */ }


/* Location:              D:\Downloads\Checker.jar!\UI\App.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */