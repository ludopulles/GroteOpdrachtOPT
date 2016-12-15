 package Validator;
 
 import java.util.ArrayList;
 import java.util.Collection;
 import javax.swing.JTextArea;
 
 
 
 
 
 
 
 
 
 public class WarningCollector
 {
   ArrayList<String> warnings = new ArrayList();
   
 
   public JTextArea console;
   
 
 
   public void addWarning(String warning)
   {
     this.warnings.add(warning);
     
     if (this.console != null)
     {
       this.console.append("\n" + warning);
     }
   }
   
 
 
 
 
 
   public void addMessage(String message)
   {
     if (this.console != null)
     {
       this.console.append("\n" + message);
       this.console.repaint();
     }
   }
   
 
 
 
 
   public boolean gotWarnings()
   {
     return this.warnings.size() > 0;
   }
   
 
 
 
   public void clearWarnings()
   {
     this.warnings.clear();
   }
   
 
 
 
 
   public Collection<String> getWarnings()
   {
     return this.warnings;
   }
 }


