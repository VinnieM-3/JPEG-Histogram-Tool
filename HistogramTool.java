import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;

import java.io.*;
import java.net.URL;

import javax.imageio.plugins.jpeg.JPEGImageWriteParam;


public class HistogramTool implements ActionListener, ItemListener, ComponentListener {
	
	static Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
	static Cursor defaultCursor = null;

	int fileCollectionID = 0;
	static JFrame frame;
	
	JMenuItem menuSave;
	
	JButton butBeginning;
	JButton butTop;
	JButton butBottom;
	JButton butEnd;
	JButton butOneUp;
	JButton butOneDown;
	JButton butOneLeft;
	JButton butOneRight;
	JButton butJumpLeft;
	JButton butJumpRight;
	JButton butHome;
	JButton butSave;
	JButton butPrint;
	JButton butUndo;	

	JRadioButtonMenuItem rbHome;
    JRadioButtonMenuItem rbView1x;
    JRadioButtonMenuItem rbView2x;
    JRadioButtonMenuItem rbView3x;
    JRadioButtonMenuItem rbViewDetail;
    JRadioButtonMenuItem rbFilterOn;
    JRadioButtonMenuItem rbFilterOff;
  
	//Keep track of which view is active.
    View activeView;
    JScrollPane activeScrollPane;    
 
    JFileChooser fileChooser;
    JFileChooser fileAppend;
    
    Settings settings;
    
    JComboBox<String> imageList;
     
	JPanel contentPane;
	CardLayout cardLayout;
	
	View viewHome;
    JScrollPane scrollPaneHome;
 
    ListImgViewer listImgViewer;
    JScrollPane scrollPane1;
    
	View viewDetail;
    JScrollPane scrollPane4;
    
  
    public HistogramTool() {
       	settings = Settings.getInstance();
       	settings.mainFrame = frame;
     }
    
    public JMenuBar createMenuBar() {
    	JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("File Menu");
        menuBar.add(menu);

        menuItem = new JMenuItem("Open...");
        menuItem.setMnemonic(KeyEvent.VK_O); 
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Open Folder or Folder MenuItem");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Append...");
        menuItem.setMnemonic(KeyEvent.VK_A); 
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Append Folder or Folder MenuItem");
        menuItem.addActionListener(this);
        menu.add(menuItem);
     
        menu.addSeparator();
        
        menuSave = new JMenuItem("Save...");
        menuSave.setMnemonic(KeyEvent.VK_S); 
        menuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
        menuSave.getAccessibleContext().setAccessibleDescription("Save File");
        menuSave.addActionListener(this);
        menu.add(menuSave);
        menuSave.setEnabled(false);

        menu.addSeparator();
         
        menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuItem.addActionListener(this);
        menu.add(menuItem);
            
        //Build the second menu.
        menu = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription("View Menu");
        menuBar.add(menu);
        
        //a group of radio button menu items
        ButtonGroup group = new ButtonGroup();
 
        rbHome = new JRadioButtonMenuItem("Home");
        rbHome.setMnemonic(KeyEvent.VK_H);
        rbHome.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.ALT_MASK));
        rbHome.setSelected(true);
        group.add(rbHome);
        rbHome.addActionListener(this);
        menu.add(rbHome);

        menu.addSeparator();
        
        rbView1x = new JRadioButtonMenuItem("1 Wide");
        rbView1x.setMnemonic(KeyEvent.VK_L);
        rbView1x.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
        rbView1x.setSelected(true);
        group.add(rbView1x);
        rbView1x.addActionListener(this);
        menu.add(rbView1x);

        rbView2x = new JRadioButtonMenuItem("2 Wide");
        rbView2x.setMnemonic(KeyEvent.VK_S);
        rbView2x.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
        group.add(rbView2x);
        rbView2x.addActionListener(this);
        menu.add(rbView2x);
        
        rbView3x = new JRadioButtonMenuItem("3 Wide");
        rbView3x.setMnemonic(KeyEvent.VK_X);
        rbView3x.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK));
        group.add(rbView3x);
        rbView3x.addActionListener(this);
        menu.add(rbView3x);
  
        rbViewDetail = new JRadioButtonMenuItem("Detail");
        rbViewDetail.setMnemonic(KeyEvent.VK_Z);
        rbViewDetail.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.ALT_MASK));
        group.add(rbViewDetail);
        rbViewDetail.addActionListener(this);
        menu.add(rbViewDetail);
        
   
        //Build the second menu.
        menu = new JMenu("Tools");
        menu.setMnemonic(KeyEvent.VK_T);
        menu.getAccessibleContext().setAccessibleDescription("Tool Menu");
        menuBar.add(menu);
        
        
        ButtonGroup groupFilter = new ButtonGroup();
        
        rbFilterOff = new JRadioButtonMenuItem("Filter OFF");
        rbFilterOff.setMnemonic(KeyEvent.VK_F);
        rbFilterOff.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.ALT_MASK));
        rbFilterOff.setSelected(true);
        groupFilter.add(rbFilterOff);
        rbFilterOff.addActionListener(this);
        menu.add(rbFilterOff);

        rbFilterOn = new JRadioButtonMenuItem("Filter ON");
        rbFilterOn.setMnemonic(KeyEvent.VK_U);
        rbFilterOn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, ActionEvent.ALT_MASK));
        groupFilter.add(rbFilterOn);
        rbFilterOn.addActionListener(this);
        menu.add(rbFilterOn);
               
        return menuBar;
    }
    
    public void componentHidden(ComponentEvent e) {}
    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}

    public void componentResized(ComponentEvent e) {
    	activeView.FrameSizeChange();
    }
    
    public Container createContentPane() {
    	
        //Create the content Pane.
        contentPane = new JPanel(new CardLayout());
        contentPane.setOpaque(true);
 
        //Create HomePage Scroll Panel.
        viewHome = new HomePage(0);
        scrollPaneHome = new JScrollPane((JPanel)viewHome);
        scrollPaneHome.getVerticalScrollBar().setUnitIncrement(30);
        contentPane.add(scrollPaneHome, "Home");
        
        //Create ListImgViewer Scroll Panel.
        listImgViewer = new ListImgViewer();
        scrollPane1 = new JScrollPane((JPanel)listImgViewer);
        scrollPane1.getVerticalScrollBar().setUnitIncrement(30);
        contentPane.add(scrollPane1, "View1x");
        
         //Create Detail Scroll Panel.
        viewDetail = new DynoHistogram();
        scrollPane4 = new JScrollPane((JPanel)viewDetail);
        scrollPane4.getVerticalScrollBar().setUnitIncrement(30);
        contentPane.add(scrollPane4, "Detail");
 
        //Show Home Page
        cardLayout = (CardLayout)(contentPane.getLayout());
        cardLayout.show(contentPane, "Home");
        activeView = viewHome;
    	activeScrollPane = scrollPaneHome;
      	setNavButVert();
        
         //Create a directory/file chooser
        fileChooser = new JFileChooser();
        fileAppend = new JFileChooser();        
          
        //need to add a component listener to listen and capture frame size changes.
        frame.addComponentListener(this);
	       
        return contentPane;
    }
    
    public void actionPerformed(ActionEvent e) {
    	if (e.getSource().getClass().getName().equals("javax.swing.JButton")) {
    		JButton source = (JButton)(e.getSource());
    		
 	        if ( source.getToolTipText().equals("Filter Off") ) {
	        	settings.FilterState = false;
	        	rbFilterOff.setSelected(true);
	        	loadDropDown();
	        	activeView.FilterStateChange();
	        }else if ( source.getToolTipText().equals("Filter On") ) {
	        	settings.FilterState = true;
	        	rbFilterOn.setSelected(true);
	        	loadDropDown();
	        	activeView.FilterStateChange();
	        }else if ( source.getToolTipText().equals("Home") ) {
  	        	setNavButVert();
	        	cardLayout.show(contentPane, "Home");
	        	rbHome.setSelected(true);
	            menuSave.setEnabled(false);
	        	activeView.leaveView();
	        	activeView = viewHome;
	        	activeScrollPane = scrollPaneHome;
	        	activeScrollPane.getVerticalScrollBar().setValue(0);
	        	activeView.displayView(fileCollectionID);
	        }else if ( source.getToolTipText().equals("1 Wide") ) {
  	        	setNavButVert();
	        	cardLayout.show(contentPane, "View1x");
	        	rbView1x.setSelected(true);
	            menuSave.setEnabled(false);
	        	activeView.leaveView();
	        	activeView = listImgViewer;
	        	activeScrollPane = scrollPane1;
	        	activeScrollPane.getVerticalScrollBar().setValue(0);
	        	listImgViewer.setLayoutType(1);
	        	activeView.displayView(fileCollectionID);
	        }else if ( source.getToolTipText().equals("2 Wide") ) {
  	        	setNavButVert();
	        	cardLayout.show(contentPane, "View1x");
	        	rbView2x.setSelected(true);
	            menuSave.setEnabled(false);
	        	activeView.leaveView();
	        	activeView = listImgViewer;
	        	activeScrollPane = scrollPane1;
	        	activeScrollPane.getVerticalScrollBar().setValue(0);
	        	listImgViewer.setLayoutType(2);
	        	activeView.displayView(fileCollectionID);
	        }else if ( source.getToolTipText().equals("3 Wide") ) {
  	        	setNavButVert();
	        	cardLayout.show(contentPane, "View1x");
	        	rbView3x.setSelected(true);
	            menuSave.setEnabled(false);
	        	activeView.leaveView();
	        	activeView.leaveView();
	        	activeView = listImgViewer;
	        	activeScrollPane = scrollPane1;
	        	activeScrollPane.getVerticalScrollBar().setValue(0);
	        	listImgViewer.setLayoutType(3);
	        	activeView.displayView(fileCollectionID);
	        }else if ( source.getToolTipText().equals("Detail") ) {
  	        	setNavButHorz();
	        	cardLayout.show(contentPane, "Detail");
	        	rbViewDetail.setSelected(true);
	            menuSave.setEnabled(true);
	        	activeView.leaveView();
	        	activeView = viewDetail;
	        	activeScrollPane = scrollPane4;
	        	activeScrollPane.getVerticalScrollBar().setValue(0);
	        	activeView.displayView(fileCollectionID);
	        }else if ( source.getToolTipText().equals("Save") ) {
  	        	saveImage();
	        }else if ( source.getToolTipText().equals("Print...") ) {
  	        	printImage();
	        }else if ( source.getToolTipText().equals("Undo") ) {
  	        	UndoImage();
 	        }else if (source.getToolTipText().equals("Top")) {
	        	activeView.buttonClickEvent(activeScrollPane, View.TOP);
	        }else if (source.getToolTipText().equals("Bottom")) {
	        	activeView.buttonClickEvent(activeScrollPane, View.BOTTOM);
	        }else if (source.getToolTipText().equals("Beginning")) {
	        	activeView.buttonClickEvent(activeScrollPane, View.BEGINNING);
	        }else if (source.getToolTipText().equals("End")) {
	        	activeView.buttonClickEvent(activeScrollPane, View.END);
	        }else if (source.getToolTipText().equals("One Up")) {
	        	activeView.buttonClickEvent(activeScrollPane, View.ONE_UP);
	        }else if (source.getToolTipText().equals("One Down")) {
	        	activeView.buttonClickEvent(activeScrollPane, View.ONE_DOWN);
	        }else if (source.getToolTipText().equals("One Right")) {
	        	activeView.buttonClickEvent(activeScrollPane, View.ONE_RIGHT);
	        }else if (source.getToolTipText().equals("One Left")) {
	        	activeView.buttonClickEvent(activeScrollPane, View.ONE_LEFT);
	        }else if (source.getToolTipText().equals("Jump Left")) {
	        	activeView.buttonClickEvent(activeScrollPane, View.JUMP_LEFT);
	        }else if (source.getToolTipText().equals("Jump Right")) {
	        	activeView.buttonClickEvent(activeScrollPane, View.JUMP_RIGHT);
	        }else if (source.getToolTipText().equals("GO")) {
	        	activeView.buttonClickEvent(activeScrollPane, imageList.getSelectedIndex());
	        }	
     	}else if (e.getSource().getClass().getName().equals("javax.swing.JMenuItem")) {
    		JMenuItem source = (JMenuItem)(e.getSource());
 	        if ( source.getText().equals("Open...") ) {
 	        	fileChooser.resetChoosableFileFilters();
 	        	fileChooser.setAcceptAllFileFilterUsed(false);
 	        	fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
 	        	fileChooser.setFileFilter(new ImgTypeFileFilter());
 	        	fileChooser.setMultiSelectionEnabled(true);
 	        	fileChooser.setAccessory(new FileDialogSmallImage(fileChooser));

  	            int returnVal = fileChooser.showDialog(frame, "Open");
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                settings.FilterState = false;
	                settings.ImageAnalysisCol.clear();
	                
	                FilenameFilter filter = new FilenameFilter() {
	                    public boolean accept(File dir, String name) {
	                    	boolean ret = false;
	                    	String lower = name.toLowerCase();
	                    	if ( (lower.endsWith(".jpg")) || (lower.endsWith(".jpeg")) ) {
	                    		ret = true;
	                    	}
	                        return ret;
	                    }
	                };
	                
	                File[] files = fileChooser.getSelectedFiles();
	                for (int x=0;x<files.length;x++) {
	                	if ( files[x].isFile() ) {
	                		String fileName = files[x].getName();
	                		int loc = files[x].getAbsolutePath().indexOf(fileName)-1;
	                		String path = files[x].getAbsolutePath().substring(0, loc);
	                		ImageAnalysis IA = new ImageAnalysis(path, files[x].getName());
	                		settings.ImageAnalysisCol.add(IA);
	                	}else if ( files[x].isDirectory() ) {
	                    	File[] files2 = files[x].listFiles(filter);
			                for (int y=0;y<files2.length;y++) {
		                		String fileName = files2[y].getName();
		                		int loc = files2[y].getAbsolutePath().indexOf(fileName)-1;
		                		String path = files2[y].getAbsolutePath().substring(0, loc);
			                	ImageAnalysis IA = new ImageAnalysis(path, files2[y].getName());
			                	settings.ImageAnalysisCol.add(IA);
			                }
	                	}
	                }
	                if ( activeView == viewHome ) {
	                	setNavButVert();
	    	        	cardLayout.show(contentPane, "View1x");
	    	            menuSave.setEnabled(false);
	    	        	activeView.leaveView();
	    	        	activeView = listImgViewer;
	    	        	activeScrollPane = scrollPane1;
	    	        	activeScrollPane.getVerticalScrollBar().setValue(0);
	    	        	listImgViewer.setLayoutType(1);
	                }
	                
	                activeView.displayView(++fileCollectionID);
	                loadDropDown();
	            }
	        }else if ( source.getText().equals("Append...") ) {
	        	fileAppend.setAcceptAllFileFilterUsed(false);
	        	fileAppend.resetChoosableFileFilters();
 	        	fileAppend.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	        	fileAppend.setFileFilter(new ImgTypeFileFilter());
	        	fileAppend.setMultiSelectionEnabled(true);
	        	fileAppend.setAccessory(new FileDialogSmallImage(fileAppend));
 	            int returnVal = fileAppend.showDialog(frame, "Append");
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                settings.FilterState = false;
	                
	                FilenameFilter filter = new FilenameFilter() {
	                    public boolean accept(File dir, String name) {
	                    	boolean ret = false;
	                    	String lower = name.toLowerCase();
	                    	if ( (lower.endsWith(".jpg")) || (lower.endsWith(".jpeg")) ) {
	                    		ret = true;
	                    	}
	                        return ret;
	                    }
	                };
	                
	                File[] files = fileAppend.getSelectedFiles();
	                for (int x=0;x<files.length;x++) {
	                	if ( files[x].isFile() ) {
	                		String fileName = files[x].getName();
	                		int loc = files[x].getAbsolutePath().indexOf(fileName)-1;
	                		String path = files[x].getAbsolutePath().substring(0, loc);
	                		ImageAnalysis IA = new ImageAnalysis(path, files[x].getName());
	                		settings.ImageAnalysisCol.add(IA);
	                	}else if ( files[x].isDirectory() ) {
	                    	File[] files2 = files[x].listFiles(filter);
			                for (int y=0;y<files2.length;y++) {
		                		String fileName = files2[y].getName();
		                		int loc = files2[y].getAbsolutePath().indexOf(fileName)-1;
		                		String path = files2[y].getAbsolutePath().substring(0, loc);
			                	ImageAnalysis IA = new ImageAnalysis(path, files2[y].getName());
			                	settings.ImageAnalysisCol.add(IA);
			                }
	                	}
	                }
	                if ( activeView == viewHome ) {
	                	setNavButVert();
	    	        	cardLayout.show(contentPane, "View1x");
	    	            menuSave.setEnabled(false);
	    	        	activeView.leaveView();
	    	        	activeView = listImgViewer;
	    	        	activeScrollPane = scrollPane1;
	    	        	activeScrollPane.getVerticalScrollBar().setValue(0);
	    	        	listImgViewer.setLayoutType(1);
	                }
	                activeView.displayView(fileCollectionID);
	                loadDropDown();
	            }
	        }else if ( source.getText().equals("Save...") ) {
	        	saveImage();
	        }else if ( source.getText().equals("Exit") ) {
	        	System.exit( 0 );
	        }
    	}else if (e.getSource().getClass().getName().equals("javax.swing.JRadioButtonMenuItem")) {
    		JRadioButtonMenuItem source = (JRadioButtonMenuItem)(e.getSource());
	        if ( source.getText().equals("Filter OFF") ) {
	        	settings.FilterState = false;
	        	loadDropDown();
	        	activeView.FilterStateChange();
	        }else if ( source.getText().equals("Filter ON") ) {
	        	settings.FilterState = true;
	        	loadDropDown();
	        	activeView.FilterStateChange();
	        }else if ( source.getText().equals("Home") ) {
 	        	setNavButVert();
	        	cardLayout.show(contentPane, "Home");
	            menuSave.setEnabled(false);
	        	activeView.leaveView();
	        	activeView = viewHome;
	        	activeScrollPane = scrollPaneHome;
	        	activeScrollPane.getVerticalScrollBar().setValue(0);
	        	activeView.displayView(fileCollectionID);
	        }else if ( source.getText().equals("1 Wide") ) {
 	        	setNavButVert();
	        	cardLayout.show(contentPane, "View1x");
	            menuSave.setEnabled(false);
	        	activeView.leaveView();
	        	activeView = listImgViewer;
	        	activeScrollPane = scrollPane1;
	        	activeScrollPane.getVerticalScrollBar().setValue(0);
	        	listImgViewer.setLayoutType(1);
	        	activeView.displayView(fileCollectionID);
	        }else if ( source.getText().equals("2 Wide") ) {
 	        	setNavButVert();
	        	cardLayout.show(contentPane, "View1x");
	            menuSave.setEnabled(false);
	        	activeView.leaveView();
	        	activeView = listImgViewer;
	        	activeScrollPane = scrollPane1;
	        	activeScrollPane.getVerticalScrollBar().setValue(0);
	        	listImgViewer.setLayoutType(2);
	        	activeView.displayView(fileCollectionID);
	        }else if ( source.getText().equals("3 Wide") ) {
 	        	setNavButVert();
	        	cardLayout.show(contentPane, "View1x");
	            menuSave.setEnabled(false);
	        	activeView.leaveView();
	        	activeView = listImgViewer;
	        	activeScrollPane = scrollPane1;
	        	activeScrollPane.getVerticalScrollBar().setValue(0);
	        	listImgViewer.setLayoutType(3);
	        	activeView.displayView(fileCollectionID);
	        }else if ( source.getText().equals("Detail") ) {
 	        	setNavButHorz();
	        	cardLayout.show(contentPane, "Detail");
	            menuSave.setEnabled(true);
	        	activeView.leaveView();
	        	activeView = viewDetail;
	        	activeScrollPane = scrollPane4;
	        	activeScrollPane.getVerticalScrollBar().setValue(0);
	        	activeView.displayView(fileCollectionID);
	        }
    	}
    }
    
    public void itemStateChanged(ItemEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        System.out.println("itemStateChanged: " + source.getText()); 
    }
    
    private void setNavButHorz() {
    	butSave.setVisible(true);
    	butPrint.setVisible(true);
    	butUndo.setVisible(true);   	
    	butBeginning.setVisible(true);
    	butTop.setVisible(false);
    	butBottom.setVisible(false);
    	butEnd.setVisible(true);
    	butOneUp.setVisible(false);
    	butOneDown.setVisible(false);
    	butOneLeft.setVisible(true);
    	butOneRight.setVisible(true);
    	butJumpLeft.setVisible(true);
    	butJumpRight.setVisible(true);
    }
    
    private void setNavButVert() {
    	butSave.setVisible(false);
    	butPrint.setVisible(false);
    	butUndo.setVisible(false);   	
    	butBeginning.setVisible(false);
    	butTop.setVisible(true);
    	butBottom.setVisible(true);
    	butEnd.setVisible(false);
    	butOneUp.setVisible(true);
    	butOneDown.setVisible(true);
    	butOneLeft.setVisible(false);
    	butOneRight.setVisible(false);
    	butJumpLeft.setVisible(false);
    	butJumpRight.setVisible(false);
    }
   
    private JComponent createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
  
        JButton button;
        String imgLocation;
        URL imageURL;
         
        button = new JButton();
        button.setActionCommand("Home");
        button.setToolTipText("Home");
        imgLocation = "images/Home16.gif";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	button.setIcon(new ImageIcon(imageURL, "Home"));
        else 
        	button.setText("Home");
        button.addActionListener(this);
        toolBar.add(button);

        toolBar.addSeparator(new Dimension(50,16));

        toolBar.setFloatable(false);
 
        button = new JButton();
        button.setActionCommand("FilterOff");
        button.setToolTipText("Filter Off");
        imgLocation = "images/FilterOff.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	button.setIcon(new ImageIcon(imageURL, "FilterOff"));
        else 
        	button.setText("Filt Off");
        button.addActionListener(this);
        toolBar.add(button);
         
        button = new JButton();
        button.setActionCommand("FilterOn");
        button.setToolTipText("Filter On");
        imgLocation = "images/FilterOn.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	button.setIcon(new ImageIcon(imageURL, "FilterOn"));
        else 
        	button.setText("Filt On");
        button.addActionListener(this);
        toolBar.add(button);
 
        toolBar.addSeparator(new Dimension(50,16));
      
        button = new JButton();
        button.setActionCommand("View1x");
        button.setToolTipText("1 Wide");
        imgLocation = "images/View1x.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	button.setIcon(new ImageIcon(imageURL, "View1x"));
        else 
        	button.setText("View1x");
        button.addActionListener(this);
        toolBar.add(button);
 
        button = new JButton();
        button.setActionCommand("View2x");
        button.setToolTipText("2 Wide");
        imgLocation = "images/View2x.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	button.setIcon(new ImageIcon(imageURL, "View2x"));
        else 
        	button.setText("View2x");
        button.addActionListener(this);
        toolBar.add(button);
   
        button = new JButton();
        button.setActionCommand("View3x");
        button.setToolTipText("3 Wide");
        imgLocation = "images/View3x.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	button.setIcon(new ImageIcon(imageURL, "View3x"));
        else 
        	button.setText("View3x");
        button.addActionListener(this);
        toolBar.add(button);
        
        button = new JButton();
        button.setActionCommand("Detail");
        button.setToolTipText("Detail");
        imgLocation = "images/ViewD.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	button.setIcon(new ImageIcon(imageURL, "Detail"));
        else 
        	button.setText("Detail");
        button.addActionListener(this);
        toolBar.add(button);
 
        
        toolBar.addSeparator(new Dimension(50,16));

        butSave = new JButton();
        butSave.setActionCommand("Save");
        butSave.setToolTipText("Save");
        imgLocation = "images/Save16.gif";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	butSave.setIcon(new ImageIcon(imageURL, "Save"));
        else 
        	butSave.setText("Save");
        butSave.addActionListener(this);
        toolBar.add(butSave);
        
        toolBar.addSeparator(new Dimension(50,16));
        
        butPrint = new JButton();
        butPrint.setActionCommand("Print...");
        butPrint.setToolTipText("Print...");
        imgLocation = "images/Print16.gif";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	butPrint.setIcon(new ImageIcon(imageURL, "Print..."));
        else 
        	butPrint.setText("Print...");
        butPrint.addActionListener(this);
        toolBar.add(butPrint);
        
        toolBar.addSeparator(new Dimension(50,16));
        
        butUndo = new JButton();
        butUndo.setActionCommand("Undo");
        butUndo.setToolTipText("Undo");
        imgLocation = "images/Undo.gif";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	butUndo.setIcon(new ImageIcon(imageURL, "Undo"));
        else 
        	butUndo.setText("Undo");
        butUndo.addActionListener(this);
        toolBar.add(butUndo);
        
        toolBar.addSeparator(new Dimension(50,16));


        butBeginning = new JButton();
        butBeginning.setActionCommand("Beginning");
        butBeginning.setToolTipText("Beginning");
        imgLocation = "images/beginning.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	butBeginning.setIcon(new ImageIcon(imageURL, "Beginning"));
        else 
        	butBeginning.setText("Beginning");
        butBeginning.addActionListener(this);
        toolBar.add(butBeginning);
       
        butTop = new JButton();
        butTop.setActionCommand("Top");
        butTop.setToolTipText("Top");
        imgLocation = "images/top.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	butTop.setIcon(new ImageIcon(imageURL, "Top"));
        else 
        	butTop.setText("Top");
        butTop.addActionListener(this);
        toolBar.add(butTop);
  
        butJumpLeft = new JButton();
        butJumpLeft.setActionCommand("JumpLeft");
        butJumpLeft.setToolTipText("Jump Left");
        imgLocation = "images/jumpLeft.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	butJumpLeft.setIcon(new ImageIcon(imageURL, "JumpLeft"));
        else 
        	butJumpLeft.setText("JumpLeft");
        butJumpLeft.addActionListener(this);
        toolBar.add(butJumpLeft);
        
        butOneLeft = new JButton();
        butOneLeft.setActionCommand("OneLeft");
        butOneLeft.setToolTipText("One Left");
        imgLocation = "images/oneLeft.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	butOneLeft.setIcon(new ImageIcon(imageURL, "OneLeft"));
        else 
        	butOneLeft.setText("OneLeft");
        butOneLeft.addActionListener(this);
        toolBar.add(butOneLeft);

        butOneRight = new JButton();
        butOneRight.setActionCommand("OneRight");
        butOneRight.setToolTipText("One Right");
        imgLocation = "images/oneRight.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	butOneRight.setIcon(new ImageIcon(imageURL, "OneRight"));
        else 
        	butOneRight.setText("OneRight");
        butOneRight.addActionListener(this);
        toolBar.add(butOneRight);
        
        butJumpRight = new JButton();
        butJumpRight.setActionCommand("JumpRight");
        butJumpRight.setToolTipText("Jump Right");
        imgLocation = "images/jumpRight.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	butJumpRight.setIcon(new ImageIcon(imageURL, "JumpRight"));
        else 
        	butJumpRight.setText("JumpRight");
        butJumpRight.addActionListener(this);
        toolBar.add(butJumpRight);

        butOneUp = new JButton();
        butOneUp.setActionCommand("OneUp");
        butOneUp.setToolTipText("One Up");
        imgLocation = "images/oneUp.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	butOneUp.setIcon(new ImageIcon(imageURL, "OneUp"));
        else 
        	butOneUp.setText("OneUp");
        butOneUp.addActionListener(this);
        toolBar.add(butOneUp);

        butOneDown = new JButton();
        butOneDown.setActionCommand("OneDown");
        butOneDown.setToolTipText("One Down");
        imgLocation = "images/oneDown.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	butOneDown.setIcon(new ImageIcon(imageURL, "OneDown"));
        else 
        	butOneDown.setText("OneDown");
        butOneDown.addActionListener(this);
        toolBar.add(butOneDown);
  
        butBottom = new JButton();
        butBottom.setActionCommand("Bottom");
        butBottom.setToolTipText("Bottom");
        imgLocation = "images/bottom.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	butBottom.setIcon(new ImageIcon(imageURL, "Bottom"));
        else 
        	butBottom.setText("Bottom");
        butBottom.addActionListener(this);
        toolBar.add(butBottom);
 
        butEnd = new JButton();
        butEnd.setActionCommand("End");
        butEnd.setToolTipText("End");
        imgLocation = "images/end.jpg";
        imageURL = HistogramTool.class.getResource(imgLocation);
        if ( imageURL != null )
        	butEnd.setIcon(new ImageIcon(imageURL, "End"));
        else 
        	butEnd.setText("End");
        butEnd.addActionListener(this);
        toolBar.add(butEnd);
        
        toolBar.addSeparator(new Dimension(20,16));

    	imageList = new JComboBox<String>();
        imageList.setSize(new Dimension(200,16));
        imageList.addActionListener(this);
        toolBar.add(imageList);

        toolBar.addSeparator(new Dimension(5,16));
        
        button = new JButton();
        button.setActionCommand("GO");
        button.setToolTipText("GO");
        button.setText("GO");
        button.addActionListener(this);
        toolBar.add(button);
        
        return toolBar;
    }
    

	private void writeFile(String completePath, BufferedImage img) {
		frame.setCursor(hourglassCursor);
		activeView.setWaitCursor();
		try {
			ImageWriter writer = ImageIO.getImageWritersBySuffix("jpeg").next();
			JPEGImageWriteParam iwp = (JPEGImageWriteParam)writer.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(1);
			File file = new File(completePath);
			ImageOutputStream ios = ImageIO.createImageOutputStream(new FileOutputStream(file));
			writer.setOutput(ios);
		    writer.write(null, new IIOImage(img, null, null), iwp);
			ios.close();
					
			ImageAnalysis IA = null;
    		String fileName = file.getName();
    		int loc = file.getAbsolutePath().indexOf(fileName)-1;
    		String filepath = file.getAbsolutePath().substring(0, loc);
    		boolean found = false;
         	for (int x=0;x<settings.ImageAnalysisCol.size();x++) {
        		IA = settings.ImageAnalysisCol.get(x);
        		if ( (IA.filePath.equals(filepath)) && (IA.fileName.equals(fileName)) ) {
        			found = true;
        			IA.loadNewImage(img, filepath, fileName);
        		}
        	}
         	if (!found ) {
           		IA = new ImageAnalysis(filepath, file.getName());
	        	settings.ImageAnalysisCol.add(IA);
	        	loadDropDown();
         	}
        	JOptionPane.showMessageDialog(frame, "File Saved", "Save Operation", JOptionPane.INFORMATION_MESSAGE);
          	if ( settings.FilterState == true ) {
            	IA.Hidden=false;          		
          	}
    		DynoHistogram dh = (DynoHistogram)viewDetail;
    		dh.newImageSaved(IA);
        	activeView.displayView(fileCollectionID);
		} catch (Exception e) {
			frame.setCursor(defaultCursor);
			activeView.setDefaultCursor();
	       	JOptionPane.showMessageDialog(frame, "Error: File not Saved", "Save Operation", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		frame.setCursor(defaultCursor);
		activeView.setDefaultCursor();
	}
	
	private void saveImage() {
   		DynoHistogram dh = (DynoHistogram)viewDetail;
		ImageAnalysis leftImage = dh.getCurrentImageAnalysis();
		if ( leftImage != null ) {
	 		File newFile = new File(leftImage.filePath + "/" + leftImage.fileName);
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(newFile);
			int rval = chooser.showSaveDialog(frame);
			if (rval == JFileChooser.APPROVE_OPTION) {
				newFile = chooser.getSelectedFile();
				if ( newFile.isFile() ) {
					Object[] options = {"Overwrite", "Cancel"};
					int n = JOptionPane.showOptionDialog(frame,
					    "Warning: File already exists!  Are you sure you want to overwrite the file?",
					    "Warning: File already exists",
					    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
					    null, options, options[1]);
					if ( n == 0 ) {
						writeFile(newFile.getPath(), leftImage.getFullSizeImage());
					}
				} else {
					writeFile(newFile.getPath(), leftImage.getFullSizeImage());
					loadDropDown();
				}
			}
		}
 	}
	
	private void UndoImage() {
 		DynoHistogram dh = (DynoHistogram)viewDetail;
  		dh.UndoImage();
	}
	
	private void printImage() {
  		DynoHistogram dh = (DynoHistogram)viewDetail;
  		dh.showPrintDialog();
	}
	

	public void loadDropDown() {
		imageList.removeAllItems();
		if ( settings.FilterState == true ){
			for (int x=0;x<settings.ImageAnalysisCol.size();x++) {
				if ( !settings.ImageAnalysisCol.get(x).Hidden) {
					imageList.addItem(getComboFileName(settings.ImageAnalysisCol.get(x).fileName));
				}
			}
		}else {
			for (int x=0;x<settings.ImageAnalysisCol.size();x++) {
				imageList.addItem(getComboFileName(settings.ImageAnalysisCol.get(x).fileName));
			}
		}
	}

	   
    private String getComboFileName(String name) {
    	int cnt = 0;
    	for (int x=0;x<imageList.getItemCount();x++) {
    		if (imageList.getItemAt(x).equals(name)) {
    			cnt++;
    		}
    	}
    	if ( cnt > 0 ) {
    		cnt++;
    		name = name + " [" + cnt + "]"; 
    	}
    	return name;
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("JPEG Histogram Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Create and set up the content pane.
        HistogramTool mainfrm = new HistogramTool();
        frame.setJMenuBar(mainfrm.createMenuBar());
        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mainfrm.createToolBar(), BorderLayout.NORTH);
        contentPane.add(mainfrm.createContentPane());
        
        //Display the window.
        frame.setSize(1024, 700);
        frame.setVisible(true);
        
        //add GlassPane
        frame.setGlassPane(Settings.overlay);
        
	    defaultCursor = frame.getCursor();
   
     }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
}