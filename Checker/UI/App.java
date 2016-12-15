 package UI;
 
 import Validator.Solution;
 import java.awt.BorderLayout;
 import java.awt.Component;
 import java.awt.event.WindowAdapter;
 import java.awt.event.WindowEvent;
 import javax.swing.JFrame;
 import javax.swing.JTabbedPane;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class App
   extends JFrame
 {
   private static final long serialVersionUID = -6881859037929489424L;
   JTabbedPane tabbedPane;
   static final String header = "=== Grote Opdracht Checker ===";
   static final String consoleString = "console";
   static final String solutionTextString = "solution text";
   static final String visuliserString = "visualiser";
   public ConsolePanel console;
   public SolutionLoadingPanel solutionText;
   public VisualiserPanel visualiser;
   Solution solution;
   
   public static void main(String[] args)
   {
     new App().setVisible(true);
   }
   
   public App()
   {
     setLayout(new BorderLayout());
     
     this.tabbedPane = new JTabbedPane();
     add(this.tabbedPane, "Center");
     
     this.console = new ConsolePanel(this);
     this.tabbedPane.addTab("console", this.console);
     
     this.solutionText = new SolutionLoadingPanel(this);
     this.tabbedPane.addTab("solution text", this.solutionText);
     
     this.visualiser = new VisualiserPanel(this);
     this.tabbedPane.addTab("visualiser", this.visualiser);
     
     setSize(600, 500);
     
 
     addWindowListener(new WindowAdapter()
     {
 
       public void windowClosing(WindowEvent e)
       {
         System.exit(0);
       }
     });
   }
   
 
 
 
 
   public void showPanel(Component c)
   {
     this.tabbedPane.setSelectedComponent(c);
   }
 }


