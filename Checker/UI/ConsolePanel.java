/*    */ package UI;
/*    */ 
/*    */ import Validator.WarningCollector;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Panel;
/*    */ import javax.swing.JScrollPane;
/*    */ import javax.swing.JTextArea;
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
/*    */ public class ConsolePanel
/*    */   extends Panel
/*    */ {
/*    */   private static final long serialVersionUID = 8229545165107754493L;
/*    */   public JTextArea console;
/*    */   public final WarningCollector warningCollector;
/*    */   App app;
/*    */   
/*    */   public ConsolePanel(App app)
/*    */   {
/* 28 */     super(new BorderLayout());
/*    */     
/* 30 */     this.app = app;
/*    */     
/* 32 */     this.console = new JTextArea();
/* 33 */     this.console.setEditable(false);
/*    */     
/* 35 */     this.console.setText("=== Grote Opdracht Checker ===");
/*    */     
/* 37 */     this.warningCollector = new WarningCollector();
/* 38 */     this.warningCollector.console = this.console;
/*    */     
/* 40 */     add(new JScrollPane(this.console));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void addLine(String line)
/*    */   {
/* 49 */     this.console.append("\n" + line);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void clearConsole()
/*    */   {
/* 57 */     this.console.setText("=== Grote Opdracht Checker ===");
/*    */   }
/*    */ }


/* Location:              D:\Downloads\Checker.jar!\UI\ConsolePanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */