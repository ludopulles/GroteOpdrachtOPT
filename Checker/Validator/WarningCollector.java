/*    */ package Validator;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
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
/*    */ public class WarningCollector
/*    */ {
/* 17 */   ArrayList<String> warnings = new ArrayList();
/*    */   
/*    */ 
/*    */   public JTextArea console;
/*    */   
/*    */ 
/*    */ 
/*    */   public void addWarning(String warning)
/*    */   {
/* 26 */     this.warnings.add(warning);
/*    */     
/* 28 */     if (this.console != null)
/*    */     {
/* 30 */       this.console.append("\n" + warning);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void addMessage(String message)
/*    */   {
/* 41 */     if (this.console != null)
/*    */     {
/* 43 */       this.console.append("\n" + message);
/* 44 */       this.console.repaint();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean gotWarnings()
/*    */   {
/* 54 */     return this.warnings.size() > 0;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void clearWarnings()
/*    */   {
/* 62 */     this.warnings.clear();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Collection<String> getWarnings()
/*    */   {
/* 71 */     return this.warnings;
/*    */   }
/*    */ }


/* Location:              D:\Downloads\Checker.jar!\Validator\WarningCollector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */