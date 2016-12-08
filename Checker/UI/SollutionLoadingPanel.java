/*     */ package UI;
/*     */ 
/*     */ import Validator.DefaultProblem;
/*     */ import Validator.Solution;
/*     */ import Validator.WarningCollector;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FileDialog;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.JTextPane;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.text.Document;
/*     */ import javax.swing.text.SimpleAttributeSet;
/*     */ import javax.swing.text.StyleConstants;
/*     */ import javax.swing.text.StyledDocument;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SollutionLoadingPanel
/*     */   extends JPanel
/*     */   implements ActionListener, DocumentListener
/*     */ {
/*     */   private static final long serialVersionUID = -7610449508243232732L;
/*     */   App app;
/*     */   JButton check;
/*     */   JButton load;
/*     */   JTextArea area;
/*     */   JTextPane lineNumbers;
/*     */   String latestDir;
/*  48 */   int currentLines = 1;
/*     */   
/*     */   public SollutionLoadingPanel(App app)
/*     */   {
/*  52 */     super(new BorderLayout());
/*  53 */     this.app = app;
/*     */     
/*  55 */     JPanel upperPanel = new JPanel(new BorderLayout());
/*  56 */     add(upperPanel, "North");
/*     */     
/*  58 */     JPanel buttons = new JPanel();
/*     */     
/*  60 */     this.check = new JButton("Check solution");
/*  61 */     this.check.setPreferredSize(new Dimension(120, 35));
/*  62 */     this.check.addActionListener(this);
/*     */     
/*     */ 
/*  65 */     this.load = new JButton("Load solution");
/*  66 */     this.load.setPreferredSize(new Dimension(120, 35));
/*  67 */     this.load.addActionListener(this);
/*     */     
/*  69 */     buttons.add(this.load);
/*  70 */     buttons.add(this.check);
/*     */     
/*  72 */     upperPanel.add(buttons, "East");
/*     */     
/*  74 */     upperPanel.add(new JLabel("Copy your sollution file here:"), "Center");
/*     */     
/*  76 */     this.area = new JTextArea();
/*  77 */     JScrollPane areaScroll = new JScrollPane(this.area);
/*     */     
/*  79 */     this.area.getDocument().addDocumentListener(this);
/*     */     
/*  81 */     this.lineNumbers = new JTextPane();
/*  82 */     this.lineNumbers.setBackground(Color.lightGray);
/*     */     
/*  84 */     String tekst = "1: ";
/*     */     
/*  86 */     this.lineNumbers.setText(tekst);
/*  87 */     StyledDocument doc = this.lineNumbers.getStyledDocument();
/*  88 */     SimpleAttributeSet center = new SimpleAttributeSet();
/*  89 */     StyleConstants.setAlignment(center, 2);
/*  90 */     doc.setParagraphAttributes(0, doc.getLength(), center, false);
/*     */     
/*  92 */     areaScroll.setRowHeaderView(this.lineNumbers);
/*  93 */     add(areaScroll, "Center");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void actionPerformed(ActionEvent ae)
/*     */   {
/* 101 */     Object source = ae.getSource();
/*     */     
/* 103 */     if (source == this.check)
/*     */     {
/* 105 */       checkAction();
/*     */     }
/* 107 */     else if (source == this.load) {
/*     */       try
/*     */       {
/* 110 */         loadAction();
/*     */       }
/*     */       catch (IOException e) {
/* 113 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void loadAction()
/*     */     throws IOException
/*     */   {
/* 125 */     FileDialog d = new FileDialog(this.app, "open solutionfile", 0);
/*     */     
/* 127 */     if (this.latestDir == null)
/*     */     {
/* 129 */       d.setDirectory("");
/*     */     }
/*     */     else
/*     */     {
/* 133 */       d.setDirectory(this.latestDir);
/*     */     }
/*     */     
/* 136 */     d.setVisible(true);
/*     */     
/* 138 */     String fileName = d.getFile();
/*     */     
/* 140 */     if (fileName == null)
/*     */     {
/* 142 */       return;
/*     */     }
/*     */     
/*     */ 
/* 146 */     this.latestDir = d.getDirectory();
/*     */     
/* 148 */     File f = new File(this.latestDir + fileName);
/*     */     
/* 150 */     FileReader fr = new FileReader(f);
/* 151 */     BufferedReader br = new BufferedReader(fr);
/*     */     
/* 153 */     String line = br.readLine();
/* 154 */     String data = null;
/* 155 */     while (line != null)
/*     */     {
/* 157 */       data = data + "\n" + line;
/* 158 */       line = br.readLine();
/*     */     }
/*     */     
/* 161 */     data = data == null ? "" : data;
/*     */     
/* 163 */     this.area.setText(data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkAction()
/*     */   {
/* 172 */     WarningCollector wc = this.app.console.warningCollector;
/*     */     
/*     */ 
/* 175 */     wc.clearWarnings();
/* 176 */     this.app.console.clearConsole();
/*     */     
/*     */ 
/* 179 */     this.app.console.addLine("> Reading sollution from textarea.");
/* 180 */     Solution solution = new Solution(this.area.getText(), DefaultProblem.getDefaultProblem(), wc);
/* 181 */     this.app.solution = solution;
/*     */     
/*     */ 
/* 184 */     if (wc.gotWarnings())
/*     */     {
/* 186 */       this.app.console.addLine("> Reading sollution terminated because of an error,\n> please fix it before handing in the assignment.");
/*     */       
/*     */ 
/*     */ 
/* 190 */       this.app.solution = null;
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 195 */       boolean feasible = solution.checkScore(wc);
/*     */       
/* 197 */       if (feasible)
/*     */       {
/*     */ 
/* 200 */         if (wc.gotWarnings())
/*     */         {
/* 202 */           this.app.console.addLine("\n> Found feasible sollution with warnings.\n> You might want to fix them before handing in.");
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 207 */           this.app.console.addLine("\n> Found feasible sollution.");
/*     */         }
/*     */         
/*     */       }
/*     */       else {
/* 212 */         this.app.console.addLine("\n> The sollution is infeasible, see above errors why.\n> please make sure the resulting sollution is feasible in your program.");
/*     */       }
/*     */       
/* 215 */       this.app.console.addLine("\n> Done Checking sollution.");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 220 */     this.app.visualiser.visualiser.setSollution(this.app.solution);
/* 221 */     this.app.showPanel(this.app.console);
/* 222 */     this.app.repaint();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateLines()
/*     */   {
/* 230 */     int lines = this.area.getLineCount();
/* 231 */     if (lines != this.currentLines)
/*     */     {
/* 233 */       this.currentLines = lines;
/* 234 */       String s = "1: ";
/* 235 */       for (int i = 2; i <= lines; i++)
/*     */       {
/* 237 */         s = s + "\n" + i + ": ";
/*     */       }
/* 239 */       this.lineNumbers.setText(s);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void changedUpdate(DocumentEvent arg0)
/*     */   {
/* 246 */     updateLines();
/*     */   }
/*     */   
/*     */ 
/*     */   public void insertUpdate(DocumentEvent arg0)
/*     */   {
/* 252 */     updateLines();
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeUpdate(DocumentEvent arg0)
/*     */   {
/* 258 */     updateLines();
/*     */   }
/*     */ }


/* Location:              D:\Downloads\Checker.jar!\UI\SollutionLoadingPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */