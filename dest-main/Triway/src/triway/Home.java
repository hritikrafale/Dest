package triway;

import triway.CopyWorker.PausableSwingWorker;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import static triway.Login.uniqueID;
import java.awt.geom.RoundRectangle2D;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import java.util.Properties;  
import java.io.FileReader;  
//import java.util.logging.Logger;

//import java.awt.Graphics2D;


/**
 * @author DELL
 */
public class Home extends javax.swing.JFrame {
    
    //final static Logger logger = Logger.getLogger(Home.class);
    boolean currupted = true;

    ImageIcon pauseIcon = new ImageIcon(new ImageIcon(this.getClass().getResource("/resources/pause.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    ImageIcon playIcon = new ImageIcon(new ImageIcon(this.getClass().getResource("/resources/play.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
    
    

    //initializing isAdmin and username
    private int isAdmin = 0;
    public String username = "";
    int userID = 0;

    public static int Row = -1;

    //History table
    public static DefaultTableModel historymodel;
    public static DefaultTableModel userManagementModel;

    protected Map<String, PausableSwingWorker> buttonThreadMap = new HashMap<>();

    protected Map<String, Boolean> clickedMap = new HashMap<>();

    public Home(int user_id) {
        
        initComponents();
        
        Properties p = new Properties();;
        
        try{
            //System.out.println("Path : " + cwd);
            
            FileReader reader = new FileReader("C:\\Program Files (x86)\\Dest\\db_config\\db_config.properties");
            
            p.load(reader);
            
        }catch(Exception e){
            e.printStackTrace();
        }

        //getting the user_id of the current user
        userID = user_id;

        //creating user management model
        userManagementModel = (DefaultTableModel) jTable2.getModel();
        
        jLabel4.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/resources/profile.png")).getImage().getScaledInstance(60, 50, Image.SCALE_SMOOTH)));
        jLabel11.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/resources/profile.png")).getImage().getScaledInstance(60, 50, Image.SCALE_SMOOTH)));
        jLabel14.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/resources/profile.png")).getImage().getScaledInstance(60, 50, Image.SCALE_SMOOTH)));
        
        jButton2.setBorder(new RoundedBorder(10));
        
        jLabel7.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/resources/emi_logo.png")).getImage().getScaledInstance(130, 150, Image.SCALE_SMOOTH)));
        jLabel12.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/resources/emi_logo.png")).getImage().getScaledInstance(130, 150, Image.SCALE_SMOOTH)));
        jLabel9.setIcon(new ImageIcon(new ImageIcon(this.getClass().getResource("/resources/emi_logo.png")).getImage().getScaledInstance(130, 150, Image.SCALE_SMOOTH)));
        
        //creating history model
        DefaultTableModel historymodel = (DefaultTableModel) historyTable.getModel();
        // Clear the existing table
        int historyrows = historymodel.getRowCount();
        if (historyrows > 0) {
            for (int i = 0; i < historyrows; i++) {
                historymodel.removeRow(0);
            }
        }
        
        

        int userManagementRows = userManagementModel.getRowCount();
        if (userManagementRows > 0) {
            for (int i = 0; i < userManagementRows; i++) {
                userManagementModel.removeRow(0);
            }
        }
        
        //querying data to find isAdmin and username values for the current session user
        try {
            String query = "SELECT * FROM users WHERE user_id = " + userID;

            //step1 load the driver class
            Class.forName("oracle.jdbc.driver.OracleDriver");
//            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "root");
            Connection con = DriverManager.getConnection("jdbc:"+p.getProperty("db_name")+":thin:@"+p.getProperty("db_host")+":"+p.getProperty("db_port")+":xe",p.getProperty("db_username"),p.getProperty("db_password"));
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                isAdmin = rs.getInt(8);
                username = rs.getString(2);
            }

            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        if (isAdmin == 1) {
            try {
                String query = "SELECT * FROM reports ORDER BY copy_date DESC";
                //step1 load the driver class
                Class.forName("oracle.jdbc.driver.OracleDriver");
//                Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "root");
                Connection con = DriverManager.getConnection("jdbc:"+p.getProperty("db_name")+":thin:@"+p.getProperty("db_host")+":"+p.getProperty("db_port")+":xe",p.getProperty("db_username"),p.getProperty("db_password"));
                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery(query);

                int results = 0;

                while (rs.next()) {
                    results++;
                    historymodel.addRow(new Object[]{rs.getString(10), rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16), "View"});
                }

                jLabel3.setText("Showing " + results + " results");


                query = "SELECT * FROM users ";
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                rs = stmt.executeQuery(query);
                
                results = 0;

                while (rs.next()) {
                    results++;
                    String secondName = rs.getString(4);
                    String thirdName = rs.getString(5);
                    String fourthName = rs.getString(6);
                    String lastLogIn = rs.getString(13);
                    String lastLogOut = rs.getString(14);
                    
                    String isLocked = "";
                    String isAdmin = "";

                    if (secondName == null) {
                        secondName = "...";
                    }

                    if (thirdName == null) {
                        thirdName = "...";
                    }

                    if (fourthName == null) {
                        fourthName = "...";
                    }

                    if (lastLogIn == null) {
                        lastLogIn = "...";
                    }

                    if (lastLogOut == null) {
                        lastLogOut = "...";
                    }
                    
                    if(rs.getInt(12) == 1){
                        isLocked = "Yes";
                    } else {
                        isLocked = "No";
                    }
                    
                    if(rs.getInt(8) == 1){
                        isAdmin = "Yes";
                    } else {
                        isAdmin = "No";
                    }

                    userManagementModel.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), secondName, thirdName, fourthName, isAdmin, rs.getString(9), rs.getString(10), rs.getString(11), isLocked, lastLogIn, lastLogOut , "Edit" , "Delete"});
                }
                jLabel8.setText("Showing " + results + " results");
                
                con.close();

            } catch (Exception e) {
                System.out.println(e);
            }
            
            


        } else {
            jTabbedPane1.setEnabledAt(2, false);
            jLabel3.setText("Showing 0 results");
        }

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getContentPane().setBackground(Color.white);
        jPanel1.setSize(JFrame.WIDTH, JFrame.HEIGHT);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        JTabbedPane jTabbedPane1 = new JTabbedPane();

        jTabbedPane1.setFocusable(true);
     
        jTabbedPane1.setSize(jPanel1.WIDTH, jPanel1.HEIGHT);
        jPanel3.setSize(JFrame.WIDTH, JFrame.HEIGHT);
        jPanel4.setSize(JFrame.WIDTH, JFrame.HEIGHT);
        jPanel7.setSize(JFrame.WIDTH, JFrame.HEIGHT);

        Font f = new Font("Helvetica", Font.PLAIN, 15);

        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.getTableHeader().setDefaultRenderer(new HeaderColor());
        jTable2.getTableHeader().setReorderingAllowed(false);
        jTable2.getTableHeader().setDefaultRenderer(new HeaderColor());
        jTable3.getTableHeader().setReorderingAllowed(false);
        jTable3.getTableHeader().setDefaultRenderer(new HeaderColor());
        historyTable.getTableHeader().setReorderingAllowed(false);
        historyTable.getTableHeader().setDefaultRenderer(new HeaderColor());

        int[] alignments = new int[]{ JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER};
        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(new HeaderRenderer(jTable1, alignments[i]));
        }

        int[] historyAlignments = new int[]{JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER};
        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(new HeaderRenderer(historyTable, historyAlignments[i]));
        }

        int[] userManagementAlignments = new int[]{JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER};
        for (int i = 0; i < jTable2.getColumnCount(); i++) {
            jTable2.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(new HeaderRenderer(jTable2, userManagementAlignments[i]));
        }
        
        int[] editTableAlignments = new int[]{JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER, JLabel.CENTER};
        for (int i = 0; i < jTable3.getColumnCount(); i++) {
            jTable3.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(new HeaderRenderer(jTable3, editTableAlignments[i]));
        }
        

        jTable1.setFont(f);
        jTable1.setRowHeight(jTable1.getRowHeight() + 25);
        jTable1.getTableHeader().setFont(f);

        historyTable.setFont(f);
        historyTable.setRowHeight(historyTable.getRowHeight() + 25);
        historyTable.getTableHeader().setFont(f);

        jTable2.setFont(f);
        jTable2.setRowHeight(jTable2.getRowHeight() + 25);
        jTable2.getTableHeader().setFont(f);

        jTable3.setFont(f);
       
        jTable3.getTableHeader().setFont(f);

        Font fl = new Font("Helvetica", Font.PLAIN, 12);

        Font fll = new Font("Helvetica", Font.PLAIN, 15);

        jLabel6.setText("> " + username);
        jLabel10.setText("> " + username);
        jLabel13.setText("> " + username);

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        // Clear the existing table
        int rows = model.getRowCount();
        if (rows > 0) {
            for (int i = 0; i < rows; i++) {
                model.removeRow(0);
            }
        }
        

        //add progress bar to table
        ProgressCellRender progressCellRender = new ProgressCellRender();
        progressCellRender.setValue(35);
        jTable1.getColumn("Progress").setCellRenderer(progressCellRender);

        jTable1.getColumn("Stop/Play").setCellRenderer(new ButtonRenderer());
        jTable1.getColumn("Stop/Play").setCellEditor(new ButtonEditor(new JCheckBox()));

        ArrayList<String> drivesName = new ArrayList<String>();
        
        FileSystemView fsvv = FileSystemView.getFileSystemView();
        ArrayList<String> localDrivesName = new ArrayList<String>();
         
        File[] drives2 = File.listRoots();
        if (drives2 != null && drives2.length > 0) {
            for (File aDrive : drives2) {
                if(fsvv.getSystemTypeDescription(aDrive).equals("Local Disk")){
                    localDrivesName.add(""+aDrive);
                }
            }
        }

        FileSystemView fsv = FileSystemView.getFileSystemView();
        File[] drives = File.listRoots();
        if (drives != null && drives.length > 0) {
            for (File aDrive : drives) {
                //String s1 = FileSystemView.getFileSystemView().getSystemDisplayName(aDrive);
                //System.out.println("");
                if (!(fsv.getSystemTypeDescription(aDrive)).equalsIgnoreCase("Local Disk") && !(fsv.getSystemTypeDescription(aDrive)).equalsIgnoreCase("CD Drive")) {
                    drivesName.add("" + aDrive);
                    File files[] = new File(aDrive.toString()).listFiles();
                    
                    String localDrive = "";
                    
                    if(localDrivesName.size() == 1){
                        localDrive = localDrivesName.get(0);
                    } else {
                        localDrive = localDrivesName.get(1);
                    }
                    
                    model.addRow(new Object[]{localDrive, Math.round(aDrive.getTotalSpace() / 1000000000.00 * 100.0) / 100.0 + " GB", ((aDrive.getTotalSpace() - aDrive.getFreeSpace()) / 1000000 > 1024 ? Math.round((aDrive.getTotalSpace() - aDrive.getFreeSpace()) / 1000000000.00 * 100.0) / 100.0 + " GB" : Math.round((aDrive.getTotalSpace() - aDrive.getFreeSpace()) / 1000000.00) + " MB"), Math.round(aDrive.getFreeSpace() / 1000000000.00 * 100.0) / 100.0 + " GB", fsv.getSystemTypeDescription(aDrive), "External", files.length, "Choose", "Select", "Choose", "Select", "Download", "Verify", "", 0});

                }
            }
        }

        File[] drives1 = File.listRoots();
        if (drives1 != null && drives1.length > 0) {
            for (File aDrive : drives1) {
                File files = new File(aDrive.toString());
                
                if (Files.isReadable(files.toPath())) {
                    currupted = false;
                    driveCurrupted(jTable1, jTable1.getColumn("Drive").getModelIndex());
                    driveCurrupted(jTable3, jTable3.getColumn("Drive").getModelIndex());
                } else {
                    currupted = true;
                }
            }
        }
        changeTableNotCorrupted(jTable1);
        changeTableNotCorrupted(jTable2);
        changeTableNotCorrupted(historyTable);
        changeTableNotCorrupted(jTable3);
        changeTable(jTable1, jTable1.getColumn("Action").getModelIndex());
        changeTableFileChooserS(jTable1, jTable1.getColumn("Source").getModelIndex());
        changeTableFileChooserD(jTable1, jTable1.getColumn("Destination").getModelIndex());
        changeTableVerify(jTable1, jTable1.getColumn("Check").getModelIndex());
        changeHistoryTableView(historyTable, historyTable.getColumn("View").getModelIndex());
        changeUserTableEditDelete(jTable2, jTable2.getColumn("Edit").getModelIndex());
        changeUserTableEditDelete(jTable2, jTable2.getColumn("Delete").getModelIndex());

        jTable1.getColumn("S").setMinWidth(0); // Must be set before maxWidth!!
        jTable1.getColumn("S").setMaxWidth(0);
        jTable1.getColumn("S").setWidth(0);
        jTable1.getColumn("D").setMinWidth(0); // Must be set before maxWidth!!
        jTable1.getColumn("D").setMaxWidth(0);
        jTable1.getColumn("D").setWidth(0);


        //creating history model
        DefaultTableModel driveModel = (DefaultTableModel) jTable3.getModel();
        // Clear the existing table
        int driveRows = driveModel.getRowCount();
        if (driveRows > 0) {
            for (int i = 0; i < driveRows; i++) {
                driveModel.removeRow(0);
            }
        }

        int i, driveTableRowCount = drivesName.size();

        for (i = 0; i < driveTableRowCount; i++) {
            driveModel.addRow(new Object[]{drivesName.get(i), "...", "...", "...", "...", "...", "...", "...", "Edit"});
        }
        changeTable3Edit(jTable3, jTable3.getColumn("Edit").getModelIndex());
        
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {

        private JButton button;

        public ButtonRenderer() {
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(Color.white);
            button.setIcon(pauseIcon);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            button.setBackground(Color.white);
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(Color.white);
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(Color.white);
            }

            String btnName = "btn" + row;
            if (clickedMap.getOrDefault(btnName, false)) {
                button.setIcon(playIcon);
            } else {
                button.setIcon(pauseIcon);
            }

            return button;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;

        private String label;

        private boolean isPushed;


        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFocusPainted(false);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int row = jTable1.getSelectedRow();
                    
                    button.setName("btn" + row);
                    String btnName = "btn" + row;

                    DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
                    button.setBackground(Color.white);
                    if (clickedMap.getOrDefault(btnName, false)) {
                        clickedMap.put(btnName, false);
                        button.setIcon(pauseIcon);
                        tableModel.setValueAt(button, row, jTable1.getColumn("Stop/Play").getModelIndex());
                        if(buttonThreadMap.get(btnName) != null)
                            buttonThreadMap.get(btnName).resume();
                    } else {
                        clickedMap.put(btnName, true);
                        button.setIcon(playIcon);
                        tableModel.setValueAt(button, row, jTable1.getColumn("Stop/Play").getModelIndex());
                        if(buttonThreadMap.get(btnName) != null)
                            buttonThreadMap.get(btnName).pause();
                    }
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }

            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                JOptionPane.showMessageDialog(button, label + ": Ouch!");
            }
            isPushed = false;
            return new String(label);
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    public class ProgressCellRender extends JProgressBar implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            int progress = 0;
            if (value instanceof Float) {
                progress = Math.round(((Float) value) * 100f);
            } else if (value instanceof Integer) {
                progress = (int) value;
            }
            setValue(progress);
            setStringPainted(true);
            return this;
            
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        historyTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel17 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel19 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton6 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        uuidLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setAutoscrolls(true);
        jPanel1.setLayout(null);

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setAutoscrolls(true);
        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jTabbedPane1.setOpaque(true);
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setAutoscrolls(true);
        jPanel3.setLayout(null);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTable1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Drive", "Total Space", "Used Space", "Free Space", "Drive Type", "Storage Type", "Files", "Source", "S", "Destination", "D", "Action", "Check", "Stop/Play", "Progress"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setColumnSelectionAllowed(true);
        jTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable1.setFocusable(false);
        jTable1.setGridColor(new java.awt.Color(204, 204, 204));
        jTable1.setInheritsPopupMenu(true);
        jTable1.setRowSelectionAllowed(false);
        jTable1.setSelectionBackground(new java.awt.Color(51, 102, 255));
        jTable1.setShowHorizontalLines(false);
        jTable1.setShowVerticalLines(false);
        jTable1.setSurrendersFocusOnKeystroke(true);
        jTable1.setUpdateSelectionOnSort(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(5);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(10);
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(7).setPreferredWidth(35);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(9).setResizable(false);
            jTable1.getColumnModel().getColumn(9).setPreferredWidth(35);
            jTable1.getColumnModel().getColumn(10).setResizable(false);
            jTable1.getColumnModel().getColumn(10).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(11).setResizable(false);
            jTable1.getColumnModel().getColumn(11).setPreferredWidth(50);
            jTable1.getColumnModel().getColumn(12).setResizable(false);
            jTable1.getColumnModel().getColumn(12).setPreferredWidth(20);
            jTable1.getColumnModel().getColumn(13).setMinWidth(20);
            jTable1.getColumnModel().getColumn(13).setPreferredWidth(20);
            jTable1.getColumnModel().getColumn(14).setMinWidth(120);
            jTable1.getColumnModel().getColumn(14).setPreferredWidth(120);
        }

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 47, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel5);
        jPanel5.setBounds(0, 450, 1540, 290);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jTable3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTable3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Drive", "Aircraft Tail Number", "Flight Number", "Aircraft Type", "Date Of Flight", "Departure Time", "Departure Location", "Arrival Location", "Edit"
            }
        ));
        jTable3.setFocusable(false);
        jTable3.setRowHeight(40);
        jTable3.setSelectionBackground(new java.awt.Color(240, 240, 240));
        jTable3.setShowHorizontalLines(false);
        jTable3.setShowVerticalLines(false);
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(jTable3);
        if (jTable3.getColumnModel().getColumnCount() > 0) {
            jTable3.getColumnModel().getColumn(0).setMinWidth(30);
            jTable3.getColumnModel().getColumn(0).setPreferredWidth(30);
            jTable3.getColumnModel().getColumn(1).setMinWidth(150);
            jTable3.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable3.getColumnModel().getColumn(2).setMinWidth(150);
            jTable3.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable3.getColumnModel().getColumn(3).setMinWidth(150);
            jTable3.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTable3.getColumnModel().getColumn(4).setMinWidth(100);
            jTable3.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTable3.getColumnModel().getColumn(5).setMinWidth(100);
            jTable3.getColumnModel().getColumn(5).setPreferredWidth(100);
            jTable3.getColumnModel().getColumn(6).setMinWidth(200);
            jTable3.getColumnModel().getColumn(6).setPreferredWidth(200);
            jTable3.getColumnModel().getColumn(7).setMinWidth(200);
            jTable3.getColumnModel().getColumn(7).setPreferredWidth(200);
            jTable3.getColumnModel().getColumn(8).setMinWidth(30);
            jTable3.getColumnModel().getColumn(8).setPreferredWidth(30);
        }

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1498, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel8);
        jPanel8.setBounds(0, 160, 1540, 220);

        jButton2.setBackground(new java.awt.Color(198, 12, 48));
        jButton2.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Log Out");
        jButton2.setBorder(null);
        jButton2.setBorderPainted(false);
        jButton2.setFocusPainted(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton2);
        jButton2.setBounds(1240, 10, 140, 40);

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setOpaque(true);
        jPanel3.add(jLabel4);
        jLabel4.setBounds(1040, 0, 60, 60);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jPanel3.add(jLabel6);
        jLabel6.setBounds(1120, 10, 100, 40);
        jPanel3.add(jLabel7);
        jLabel7.setBounds(1400, 0, 130, 150);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel5.setText("Flight Information");
        jPanel3.add(jLabel5);
        jLabel5.setBounds(20, 110, 150, 40);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        jLabel2.setText("Drive Information");
        jPanel3.add(jLabel2);
        jLabel2.setBounds(20, 400, 150, 40);

        jTabbedPane1.addTab("Home", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setAutoscrolls(true);
        jPanel4.setLayout(null);

        historyTable.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        historyTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "User Name", "Flight Number", "Drive", "Source", "Destination", "Total Files Copied", "Total Size", "Time Taken", "Copy Date", "Copy Time", "Aircraft Tail Number", "Aircraft Type", "Date of Flight", "Departure Tiime", "Departure Location", "Arrival Location", "View"
            }
        ));
        historyTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        historyTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                historyTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(historyTable);
        if (historyTable.getColumnModel().getColumnCount() > 0) {
            historyTable.getColumnModel().getColumn(0).setMinWidth(125);
            historyTable.getColumnModel().getColumn(0).setPreferredWidth(125);
            historyTable.getColumnModel().getColumn(1).setMinWidth(125);
            historyTable.getColumnModel().getColumn(1).setPreferredWidth(125);
            historyTable.getColumnModel().getColumn(2).setMinWidth(125);
            historyTable.getColumnModel().getColumn(2).setPreferredWidth(125);
            historyTable.getColumnModel().getColumn(3).setMinWidth(150);
            historyTable.getColumnModel().getColumn(3).setPreferredWidth(150);
            historyTable.getColumnModel().getColumn(4).setMinWidth(500);
            historyTable.getColumnModel().getColumn(4).setPreferredWidth(500);
            historyTable.getColumnModel().getColumn(5).setMinWidth(150);
            historyTable.getColumnModel().getColumn(5).setPreferredWidth(150);
            historyTable.getColumnModel().getColumn(6).setMinWidth(125);
            historyTable.getColumnModel().getColumn(6).setPreferredWidth(125);
            historyTable.getColumnModel().getColumn(7).setMinWidth(125);
            historyTable.getColumnModel().getColumn(7).setPreferredWidth(125);
            historyTable.getColumnModel().getColumn(8).setMinWidth(125);
            historyTable.getColumnModel().getColumn(8).setPreferredWidth(125);
            historyTable.getColumnModel().getColumn(9).setMinWidth(160);
            historyTable.getColumnModel().getColumn(9).setPreferredWidth(160);
            historyTable.getColumnModel().getColumn(10).setMinWidth(175);
            historyTable.getColumnModel().getColumn(10).setPreferredWidth(175);
            historyTable.getColumnModel().getColumn(11).setMinWidth(125);
            historyTable.getColumnModel().getColumn(11).setPreferredWidth(125);
            historyTable.getColumnModel().getColumn(12).setMinWidth(125);
            historyTable.getColumnModel().getColumn(12).setPreferredWidth(125);
            historyTable.getColumnModel().getColumn(13).setMinWidth(150);
            historyTable.getColumnModel().getColumn(13).setPreferredWidth(150);
            historyTable.getColumnModel().getColumn(14).setMinWidth(150);
            historyTable.getColumnModel().getColumn(14).setPreferredWidth(150);
            historyTable.getColumnModel().getColumn(15).setMinWidth(150);
            historyTable.getColumnModel().getColumn(15).setPreferredWidth(150);
        }

        jPanel4.add(jScrollPane2);
        jScrollPane2.setBounds(20, 210, 1500, 460);

        jLabel3.setFont(new java.awt.Font("Tahoma", 2, 15)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 153, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jPanel4.add(jLabel3);
        jLabel3.setBounds(1350, 150, 170, 50);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setLabelFor(jTextField1);
        jLabel1.setText("Flight No");
        jLabel1.setToolTipText("");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jTextField1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jDateChooser1.setDateFormatString("dd-MM-yyyy");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("From Date");

        jDateChooser2.setDateFormatString("dd-MM-yyyy");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("To Date");

        jButton1.setBackground(new java.awt.Color(198, 12, 48));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Search");
        jButton1.setBorder(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(198, 12, 48));
        jButton5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Reset");
        jButton5.setBorder(null);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(50, 50, 50)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(50, 50, 50)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(50, 50, 50)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel2);
        jPanel2.setBounds(30, 80, 950, 100);
        jPanel4.add(jLabel9);
        jLabel9.setBounds(1400, 0, 130, 150);

        jButton3.setBackground(new java.awt.Color(198, 12, 48));
        jButton3.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Log Out");
        jButton3.setToolTipText("");
        jButton3.setBorder(null);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton3);
        jButton3.setBounds(1240, 10, 140, 40);

        jLabel10.setFont(new java.awt.Font("Verdana", 1, 13)); // NOI18N
        jPanel4.add(jLabel10);
        jLabel10.setBounds(1120, 10, 100, 40);
        jPanel4.add(jLabel11);
        jLabel11.setBounds(1040, 0, 60, 60);

        jTabbedPane1.addTab("Reports", jPanel4);

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setAutoscrolls(true);

        jTable2.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "User ID", "User Name", "First Name", "Second Name", "Third Name", "Fourth Name", "Is Admin", "Gender", "Department", "User Role", "Is Locked", "Last LogIn", "Last Log Out", "Edit", "Delete"
            }
        ));
        jTable2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setMinWidth(125);
            jTable2.getColumnModel().getColumn(0).setPreferredWidth(125);
            jTable2.getColumnModel().getColumn(1).setMinWidth(125);
            jTable2.getColumnModel().getColumn(1).setPreferredWidth(125);
            jTable2.getColumnModel().getColumn(2).setMinWidth(150);
            jTable2.getColumnModel().getColumn(2).setPreferredWidth(150);
            jTable2.getColumnModel().getColumn(3).setMinWidth(150);
            jTable2.getColumnModel().getColumn(3).setPreferredWidth(150);
            jTable2.getColumnModel().getColumn(4).setMinWidth(125);
            jTable2.getColumnModel().getColumn(4).setPreferredWidth(125);
            jTable2.getColumnModel().getColumn(5).setMinWidth(125);
            jTable2.getColumnModel().getColumn(5).setPreferredWidth(125);
            jTable2.getColumnModel().getColumn(6).setMinWidth(125);
            jTable2.getColumnModel().getColumn(6).setPreferredWidth(125);
            jTable2.getColumnModel().getColumn(7).setMinWidth(125);
            jTable2.getColumnModel().getColumn(7).setPreferredWidth(125);
            jTable2.getColumnModel().getColumn(8).setMinWidth(200);
            jTable2.getColumnModel().getColumn(8).setPreferredWidth(200);
            jTable2.getColumnModel().getColumn(9).setMinWidth(125);
            jTable2.getColumnModel().getColumn(9).setPreferredWidth(125);
            jTable2.getColumnModel().getColumn(10).setMinWidth(125);
            jTable2.getColumnModel().getColumn(10).setPreferredWidth(125);
            jTable2.getColumnModel().getColumn(11).setMinWidth(200);
            jTable2.getColumnModel().getColumn(11).setPreferredWidth(200);
            jTable2.getColumnModel().getColumn(12).setMinWidth(200);
            jTable2.getColumnModel().getColumn(12).setPreferredWidth(200);
            jTable2.getColumnModel().getColumn(13).setMinWidth(100);
            jTable2.getColumnModel().getColumn(13).setPreferredWidth(100);
            jTable2.getColumnModel().getColumn(14).setMinWidth(100);
            jTable2.getColumnModel().getColumn(14).setPreferredWidth(100);
        }

        jButton6.setBackground(new java.awt.Color(198, 12, 48));
        jButton6.setFont(new java.awt.Font("Verdana", 0, 15)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("New User");
        jButton6.setBorder(null);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Tahoma", 2, 15)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 153, 0));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        jLabel13.setFont(new java.awt.Font("Verdana", 1, 13)); // NOI18N

        jButton4.setBackground(new java.awt.Color(198, 12, 48));
        jButton4.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Log Out");
        jButton4.setBorder(null);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1495, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(13, 13, 13)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                .addGap(63, 63, 63))
        );

        jLabel12.setBounds(1400, 0, 130, 150);
        jLabel13.setBounds(1120, 10, 100, 40);
        jLabel14.setBounds(1040, 0, 60, 60);
        jButton4.setBounds(1240, 10, 140, 40);

        jTabbedPane1.addTab("User Management", jPanel7);

        jPanel1.add(jTabbedPane1);
        jTabbedPane1.setBounds(0, 0, 1540, 700);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(null);
        jPanel1.add(jPanel6);
        jPanel6.setBounds(30, 140, 1310, 470);
        jPanel1.add(uuidLabel);
        uuidLabel.setBounds(1390, 80, 0, 0);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 1, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1533, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 1, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 769, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        //int setIndex = jTabbedPane1.getSelectedIndex();
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        Properties p = new Properties();;

        try{
            //System.out.println("Path : " + cwd);

            FileReader reader = new FileReader("C:\\Program Files (x86)\\Dest\\db_config\\db_config.properties");

            p.load(reader);

        }catch(Exception e){
            e.printStackTrace();
        }

        //getting current time
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        String currentDateTime = "" + formatter.format(date);

        try {
            String updateQuery = "UPDATE users SET last_log_out = '" + currentDateTime + "' WHERE user_name = '" + username + "'";
            //step1 load the driver class
            Class.forName("oracle.jdbc.driver.OracleDriver");
            //            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "root");
            Connection con = DriverManager.getConnection("jdbc:"+p.getProperty("db_name")+":thin:@"+p.getProperty("db_host")+":"+p.getProperty("db_port")+":xe",p.getProperty("db_username"),p.getProperty("db_password"));

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.executeUpdate(updateQuery);
            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        new Login().setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        new NewUser().setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        Properties p = new Properties();;

        try{
            //System.out.println("Path : " + cwd);

            FileReader reader = new FileReader("C:\\Program Files (x86)\\Dest\\db_config\\db_config.properties");

            p.load(reader);

        }catch(Exception e){
            e.printStackTrace();
        }

        int col = jTable2.getSelectedColumn();
        int row = jTable2.getSelectedRow();

        if (jTable2.getModel().getValueAt(row, col).toString().equals("Delete")) {

            String userID = jTable2.getModel().getValueAt(row, 0).toString();

            System.out.println("User ID : " + userID);

            try{
                String deleteQuery = "DELETE FROM users WHERE user_id = '" + userID + "'";

                Class.forName("oracle.jdbc.driver.OracleDriver");
                //                    Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","root");
                Connection con = DriverManager.getConnection("jdbc:"+p.getProperty("db_name")+":thin:@"+p.getProperty("db_host")+":"+p.getProperty("db_port")+":xe",p.getProperty("db_username"),p.getProperty("db_password"));

                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                stmt.executeUpdate(deleteQuery);

                int userManagementRows = userManagementModel.getRowCount();
                if (userManagementRows > 0) {
                    for (int i = 0; i < userManagementRows; i++) {
                        userManagementModel.removeRow(0);
                    }
                }

                String query = "SELECT * FROM users ";
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery(query);

                int results = 0;

                while (rs.next()) {
                    results++;
                    String secondName = rs.getString(4);
                    String thirdName = rs.getString(5);
                    String fourthName = rs.getString(6);
                    String lastLogIn = rs.getString(13);
                    String lastLogOut = rs.getString(14);

                    if (secondName == null) {
                        secondName = "...";
                    }

                    if (thirdName == null) {
                        thirdName = "...";
                    }

                    if (fourthName == null) {
                        fourthName = "...";
                    }

                    if (lastLogIn == null) {
                        lastLogIn = "...";
                    }

                    if (lastLogOut == null) {
                        lastLogOut = "...";
                    }

                    userManagementModel.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), secondName, thirdName, fourthName, rs.getInt(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getInt(12), lastLogIn, lastLogOut , "Edit" , "Delete"});
                }

                System.out.println("Results : " + results);

                jLabel8.setText("Showing " + results + " results");

                con.close();

            }catch(Exception e){
                System.out.println(e);
            }

        } else if (jTable2.getModel().getValueAt(row, col).toString().equals("Edit")) {

            new EditUser().setVisible(true);

        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        //getting current time
        Properties p = new Properties();;

        try{
            //System.out.println("Path : " + cwd);

            FileReader reader = new FileReader("C:\\Program Files (x86)\\Dest\\db_config\\db_config.properties");

            p.load(reader);

        }catch(Exception e){
            e.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        String currentDateTime = "" + formatter.format(date);

        try {
            String updateQuery = "UPDATE users SET last_log_out = '" + currentDateTime + "' WHERE user_name = '" + username + "'";
            //step1 load the driver class

            Class.forName("oracle.jdbc.driver.OracleDriver");
            //            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "root");
            Connection con = DriverManager.getConnection("jdbc:"+p.getProperty("db_name")+":thin:@"+p.getProperty("db_host")+":"+p.getProperty("db_port")+":xe",p.getProperty("db_username"),p.getProperty("db_password"));

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.executeUpdate(updateQuery);
            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        new Login().setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        Properties p = new Properties();;

        try{
            //System.out.println("Path : " + cwd);

            FileReader reader = new FileReader("C:\\Program Files (x86)\\Dest\\db_config\\db_config.properties");

            p.load(reader);

        }catch(Exception e){
            e.printStackTrace();
        }

        if (jTextField1.getText().isEmpty() && jDateChooser1.getDate() == null && jDateChooser2.getDate() == null) {

        } else {
            //on pressing reset button all the three fields should be empty and default query should run
            jTextField1.setText("");
            jDateChooser1.setCalendar(null);
            jDateChooser2.setCalendar(null);

            String queryString = "";

            if (isAdmin == 1) {
                queryString = "SELECT * FROM reports ORDER BY copy_date DESC";
            } else {
                queryString = "SELECT * FROM reports WHERE session_id = '" + uniqueID + "' AND user_name = '" + username + "' ORDER BY copy_date DESC";
            }

            try {
                String query = queryString;
                //step1 load the driver class
                Class.forName("oracle.jdbc.driver.OracleDriver");
                //                Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "root");
                Connection con = DriverManager.getConnection("jdbc:"+p.getProperty("db_name")+":thin:@"+p.getProperty("db_host")+":"+p.getProperty("db_port")+":xe",p.getProperty("db_username"),p.getProperty("db_password"));
                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery(query);

                // Clear the existing table
                int historyrows = historymodel.getRowCount();
                if (historyrows > 0) {
                    for (int i = 0; i < historyrows; i++) {
                        historymodel.removeRow(0);
                    }
                }

                int results = 0;

                while (rs.next()) {
                    results++;
                    historymodel.addRow(new Object[]{rs.getString(10), rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16), "View"});
                }

                jLabel3.setText("Showing " + results + " results  ");

                con.close();

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Properties p = new Properties();

        try{
            //System.out.println("Path : " + cwd);

            FileReader reader = new FileReader("C:\\Program Files (x86)\\Dest\\db_config\\db_config.properties");

            p.load(reader);

        }catch(Exception e){
            e.printStackTrace();
        }

        //initializing history table
        historymodel = (DefaultTableModel) historyTable.getModel();

        //Removing rows from table
        //storing row count in historyrows
        int historyrows = historymodel.getRowCount();

        //if rows exist then we will remove each row from table
        if (historyrows > 0) {
            for (int i = 0; i < historyrows; i++) {
                historymodel.removeRow(0);
            }
        }

        //getting text entered in jTextField1 i.e. FLIGHT_NO
        String fltNo = jTextField1.getText();

        //getting text entered in jDateChooser1 i.e. COPY_DATE

        //        String fromDate = "";
        //        String toDate = "";

        //dateformat to convert date format to desired one
        //DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        //        if (jDateChooser1.getDate() != null)
        //        fromDate = df.format(jDateChooser1.getDate());
        //
        //        if (jDateChooser2.getDate() != null)
        //        toDate = df.format(jDateChooser2.getDate());

        //user have not entered both FLIGHT_NO and COPY_DATE
        //therefore queryString will be to fetch all data
        //        String queryString = "SELECT * FROM reports ORDER BY copy_date DESC";
        //
        //        //subquery is for adding the username to the query
        //        // admin can see for all users
        //        //non-admin users can see only there history
        //        String subQuery = "";
        //
        //        if (username.equals("admin") == false) {
            //            subQuery = " session_id = '" + uniqueID + "' AND user_name = '" + username + "' AND ";
            //            queryString = "SELECT * FROM reports WHERE session_id = '" + uniqueID + "' AND  user_name = '" + username + "' ORDER BY copy_date DESC";
            //        }
        //
        //        //creating queryString
        //        //there are four constraints for query string
        //        //First :- if user have entered  FLIGHT_NO and fromDate and toDate
        //        //SECOND :- if user have entered FLIGHT_NO and fromDate
        //        //THIRD :- if user have entered Flight No and toDate
        //        //FOURTH :- if user have entered only Flight NO
        //        //FIFTH :-  if user have entered fromDate and toDate
        //        //SIXTH :- if user have entered Flight No and toDate
        //        //SEVENTH :- if user have entered only toDate
        //        //EIGHT :- if user have entered nothing
        //
        //
        //
        //        String currentDate = java.time.LocalDate.now().toString();
        //
        //        if (fltNo.equals("") == false && fromDate.equals("") == false && toDate.equals("") == false) {
            //            queryString = "SELECT * FROM reports WHERE" + subQuery + " flight_no = '" + fltNo + "'" + " AND copy_date >= '" + fromDate + "' AND copy_date <= '" + toDate + "' ORDER BY copy_date DESC";
            //        } else if (fltNo.equals("") == false && fromDate.equals("") == false && toDate.equals("") == true) {
            //            queryString = "SELECT * FROM reports WHERE" + subQuery + " flight_no = '" + fltNo + "'" + " AND copy_date >= '" + fromDate + "' ORDER BY copy_date DESC";
            //        } else if (fltNo.equals("") == false && fromDate.equals("") == true && toDate.equals("") == false) {
            //            queryString = "SELECT * FROM reports WHERE" + subQuery + " flight_no = '" + fltNo + "'" + " AND copy_date <= '" + toDate + "' ORDER BY copy_date DESC";
            //        } else if (fltNo.equals("") == false && fromDate.equals("") == true && toDate.equals("") == true) {
            //            queryString = "SELECT * FROM reports WHERE" + subQuery + " flight_no = '" + fltNo + "' ORDER BY copy_date DESC";
            //        } else if (fltNo.equals("") == true && fromDate.equals("") == false && toDate.equals("") == false) {
            //            queryString = "SELECT * FROM reports WHERE" + subQuery + " copy_date >= '" + fromDate + "' AND copy_date <= '" + toDate + "' ORDER BY copy_date DESC";
            //        } else if (fltNo.equals("") == true && fromDate.equals("") == false && toDate.equals("") == true) {
            //            queryString = "SELECT * FROM reports WHERE" + subQuery + " copy_date >= '" + fromDate + "' ORDER BY copy_date DESC";
            //        } else if (fltNo.equals("") == true && fromDate.equals("") == true && toDate.equals("") == false) {
            //            queryString = "SELECT * FROM reports WHERE" + subQuery + " copy_date <= '" + toDate + "' ORDER BY copy_date DESC";
            //        }
        //
        //        try {
            //            String query = queryString;
            //            //step1 load the driver class
            //            Class.forName("oracle.jdbc.driver.OracleDriver");
            //            //            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "root");
            //            Connection con = DriverManager.getConnection("jdbc:"+p.getProperty("db_name")+":thin:@"+p.getProperty("db_host")+":"+p.getProperty("db_port")+":xe",p.getProperty("db_username"),p.getProperty("db_password"));
            //            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            //            ResultSet rs = stmt.executeQuery(query);
            //
            //            int results = 0;
            //
            //            while (rs.next()) {
                //                results++;
                //                historymodel.addRow(new Object[]{rs.getString(10), rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16), "View"});
                //            }
            //
            //            jLabel3.setText("Showing " + results + " results");
            //
            //            con.close();
            //
            //        } catch (Exception e) {
            //            System.out.println(e);
            //        }

        SimpleDateFormat sdfo = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        String fDate = "";
        String tDate = "";

        if(jDateChooser1.getDate() != null)
        fDate = df.format(jDateChooser1.getDate());
        else
        fDate = "";
        if(jDateChooser2.getDate() != null)
        tDate = df.format(jDateChooser2.getDate());
        else
        tDate = "";

        //user have not entered both FLIGHT_NO and COPY_DATE
        //therefore queryString will be to fetch all data
        String queryString = "SELECT * FROM reports ORDER BY copy_date DESC";

        //subquery is for adding the username to the query
        // admin can see for all users
        //non-admin users can see only there history
        String subQuery = "";

        if (username.equals("admin") == false) {
            subQuery = " session_id = '" + uniqueID + "' AND user_name = '" + username + "' AND ";
            queryString = "SELECT * FROM reports WHERE session_id = '" + uniqueID + "' AND  user_name = '" + username + "' ORDER BY copy_date DESC";
        }

        try {
            String query = queryString;
            //step1 load the driver class
            Class.forName("oracle.jdbc.driver.OracleDriver");
            //Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "root");
            Connection con = DriverManager.getConnection("jdbc:"+p.getProperty("db_name")+":thin:@"+p.getProperty("db_host")+":"+p.getProperty("db_port")+":xe",p.getProperty("db_username"),p.getProperty("db_password"));
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = stmt.executeQuery(query);

            int results = 0;

            if (fltNo.equals("") == false && fDate.equals("") == false && tDate.equals("") == false) {
                while (rs.next()) {
                    Date fromDate = sdfo.parse(fDate);
                    Date toDate = sdfo.parse(tDate);
                    String copyD = rs.getString(8);

                    Date copyDate = sdfo.parse(copyD);

                    if(rs.getString(1).equals(fltNo) && copyDate.compareTo(fromDate) >= 0 && copyDate.compareTo(toDate) <= 0){
                        historymodel.addRow(new Object[]{rs.getString(10), rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16), "View"});
                        results++;
                    }
                }
            } else if (fltNo.equals("") == false && fDate.equals("") == false && tDate.equals("") == true) {
                while (rs.next()) {
                    Date fromDate = sdfo.parse(fDate);
                    //Date toDate = sdfo.parse(tDate);
                    String copyD = rs.getString(8);
                    Date copyDate = sdfo.parse(copyD);
                    if(rs.getString(1).equals(fltNo) && copyDate.compareTo(fromDate) >= 0 ){
                        historymodel.addRow(new Object[]{rs.getString(10), rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16), "View"});
                        results++;
                    }
                }
            } else if (fltNo.equals("") == false && fDate.equals("") == true && tDate.equals("") == false) {
                while (rs.next()) {
                    //Date fromDate = sdfo.parse(fDate);
                    Date toDate = sdfo.parse(tDate);
                    String copyD = rs.getString(8);
                    Date copyDate = sdfo.parse(copyD);
                    if(rs.getString(1).equals(fltNo)  && copyDate.compareTo(toDate) <= 0){
                        historymodel.addRow(new Object[]{rs.getString(10), rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16), "View"});
                        results++;
                    }
                }
            } else if (fltNo.equals("") == false && fDate.equals("") == true && tDate.equals("") == true) {
                while (rs.next()) {
                    //Date fromDate = sdfo.parse(fDate);
                    //Date toDate = sdfo.parse(tDate);
                    System.out.println("Flt No : " + rs.getString(1));
                    if(rs.getString(1).equals(fltNo) ){
                        historymodel.addRow(new Object[]{rs.getString(10), rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16), "View"});
                        results++;
                    }
                }
            } else if (fltNo.equals("") == true && fDate.equals("") == false && tDate.equals("") == false) {
                while (rs.next()) {
                    Date fromDate = sdfo.parse(fDate);
                    Date toDate = sdfo.parse(tDate);
                    String copyD = rs.getString(8);
                    Date copyDate = sdfo.parse(copyD);
                    if( copyDate.compareTo(fromDate) >= 0 && copyDate.compareTo(toDate) <= 0){
                        historymodel.addRow(new Object[]{rs.getString(10), rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16), "View"});
                        results++;
                    }
                }
            } else if (fltNo.equals("") == true && fDate.equals("") == false && tDate.equals("") == true) {
                while (rs.next()) {
                    Date fromDate = sdfo.parse(fDate);

                    String copyD = rs.getString(8);
                    Date copyDate = sdfo.parse(copyD);
                    if( copyDate.compareTo(fromDate) >= 0 ) {
                        historymodel.addRow(new Object[]{rs.getString(10), rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16), "View"});
                        results++;
                    }
                }
            } else if (fltNo.equals("") == true && fDate.equals("") == true && tDate.equals("") == false) {
                while (rs.next()) {
                    //Date fromDate = sdfo.parse(fDate);
                    Date toDate = sdfo.parse(tDate);
                    String copyD = rs.getString(8);
                    Date copyDate = sdfo.parse(copyD);
                    if( copyDate.compareTo(toDate) <= 0){
                        historymodel.addRow(new Object[]{rs.getString(10), rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16), "View"});
                        results++;
                    }
                }
            } else {
                while (rs.next()) {
                    results++;
                    historymodel.addRow(new Object[]{rs.getString(10), rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16), "View"});
                }
            }

            jLabel3.setText("Showing " + results + " results");

            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed

        //setting the jTextField1 for FLIGHT_NO to empty
        jTextField1.setText("Hello");
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void historyTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_historyTableMouseClicked
        // TODO add your handling code here:

        int col = historyTable.getSelectedColumn();
        int row = historyTable.getSelectedRow();

        if (historyTable.getModel().getValueAt(row, col).toString().equals("View")) {
            String path = (historyTable.getModel().getValueAt(row, 4).toString());

            try {
                Runtime.getRuntime().exec("explorer.exe /select," + path);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }//GEN-LAST:event_historyTableMouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        Properties p = new Properties();;

        try{
            //System.out.println("Path : " + cwd);

            FileReader reader = new FileReader("C:\\Program Files (x86)\\Dest\\db_config\\db_config.properties");

            p.load(reader);

        }catch(Exception e){
            e.printStackTrace();
        }

        //getting current time
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        String currentDateTime = "" + formatter.format(date);

        try {
            String updateQuery = "UPDATE users SET last_log_out = '" + currentDateTime + "' WHERE user_name = '" + username + "'";
            //step1 load the driver class
            Class.forName("oracle.jdbc.driver.OracleDriver");
            //            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "root");
            Connection con = DriverManager.getConnection("jdbc:"+p.getProperty("db_name")+":thin:@"+p.getProperty("db_host")+":"+p.getProperty("db_port")+":xe",p.getProperty("db_username"),p.getProperty("db_password"));

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.executeUpdate(updateQuery);
            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        new Login().setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked

        int col = jTable3.getSelectedColumn();
        int row = jTable3.getSelectedRow();

        if (jTable3.getModel().getValueAt(row, col).toString().equals("Edit")) {
            Row = row;

            //new EditFrame(jTable3.getModel().getValueAt(row,0).toString()).setVisible(true);
            EditFrame editFrame = new EditFrame();
            editFrame.setTitle("Edit Drive " + jTable3.getModel().getValueAt(row,0).toString());
            editFrame.setVisible(true);
        }
    }//GEN-LAST:event_jTable3MouseClicked

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        int col = jTable1.getSelectedColumn();
        int row = jTable1.getSelectedRow();

        String driveName = "";

        if (jTable1.getModel().getValueAt(row, col).toString().equals("Choose")) {
            JFileChooser chooser = new JFileChooser();
            try {
                //                File dummy_file = new File(new File(jTable1.getModel().getValueAt(row, 0).toString()).getCanonicalPath());
                File dummy_file = new File(new File(jTable3.getModel().getValueAt(row, 0).toString()).getCanonicalPath());
                chooser.setCurrentDirectory(new File(dummy_file.toString()));
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(Home.this);
                if (result != JFileChooser.APPROVE_OPTION) {
                } else if (result == chooser.APPROVE_OPTION) {
                    try {
                        File f = chooser.getSelectedFile();
                        jTable1.setValueAt(f.getAbsolutePath(), jTable1.getSelectedRow(), jTable1.getSelectedColumn() + 1);
                        driveName =  f.getAbsolutePath();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                //                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                e.printStackTrace();
            }

            if(col == 9){
                System.out.println("Drive Name : " + driveName);

                if(driveName.equals("...") == false && driveName.equals("") == false){
                    int slashIndex = driveName.indexOf(":") + 1;

                    String drivePath = driveName.substring(0,slashIndex+1);

                    jTable1.getModel().setValueAt(drivePath , row , 0);
                }
            }
        }

        if (jTable1.getModel().getValueAt(row, col).toString().equals("Download")) {

            String aircraftTailNumber = jTable3.getModel().getValueAt(row, 1).toString();
            String flightNumber = jTable3.getModel().getValueAt(row, 2).toString();
            String aircraftType = jTable3.getModel().getValueAt(row, 3).toString();
            String dateOfFlight = jTable3.getModel().getValueAt(row, 4).toString();
            String departureTime = jTable3.getModel().getValueAt(row, 5).toString();
            String arrivalLocation = jTable3.getModel().getValueAt(row, 7).toString();
            String departureLocation = jTable3.getModel().getValueAt(row, 6).toString();

            if (aircraftTailNumber.isEmpty() || aircraftTailNumber.equals("...") || flightNumber.isEmpty() || flightNumber.equals("...") || aircraftType.isEmpty() || aircraftType.equals("...") || dateOfFlight.isEmpty() || dateOfFlight.equals("...") || departureTime.isEmpty() || departureTime.equals("...") || departureLocation.isEmpty() || departureLocation.equals("...") || arrivalLocation.isEmpty() || arrivalLocation.equals("...")) {
                String drive = jTable1.getModel().getValueAt(row, jTable1.getColumn("Drive").getModelIndex()).toString();
                JOptionPane.showMessageDialog(rootPane, "Please enter the flight information to initiate download ");
            } else {
                if (!jTable1.getModel().getValueAt(row, jTable1.getColumn("S").getModelIndex()).toString().equals("Select") && !jTable1.getModel().getValueAt(row, jTable1.getColumn("D").getModelIndex()).toString().equals("Select")) {
                    String destinationDriveName = jTable1.getModel().getValueAt(row , 0).toString();
                    String sourceFolderName = jTable1.getModel().getValueAt(row, jTable1.getColumn("S").getModelIndex()).toString();

                    File file = new File(destinationDriveName);
                    File file2 = new File(sourceFolderName);

                    Double destinationDriveSpace = file.getFreeSpace() / (1024.0 * 1024 * 1024);

                    double sourceFolderSize = folderSize(file2)/ (1024.0 * 1024 * 1024);

                    if(sourceFolderSize >= destinationDriveSpace){
                        JOptionPane.showMessageDialog(rootPane, "Insufficient space. Please choose another drive.");
                    } else {

                        try {

                            String sourceDirectory = jTable1.getModel().getValueAt(row, jTable1.getColumn("S").getModelIndex()).toString();

                            String destinationDirectory = jTable1.getModel().getValueAt(row, jTable1.getColumn("D").getModelIndex()).toString();

                            File theDir = new File(destinationDirectory + "/" + sourceDirectory.substring(0, 1) + "_" + jTable3.getModel().getValueAt(row, 3).toString() + "_" + jTable3.getModel().getValueAt(row, 2).toString() + "_" + jTable3.getModel().getValueAt(row, 4).toString());
                            if (!theDir.exists()) {
                                theDir.mkdirs();
                            }

                            File srcDir = new File(sourceDirectory);
                            File destDir = null;

                            if (srcDir.exists() && (srcDir.listFiles() != null && srcDir.listFiles().length > 0)) {
                                destDir = new File(theDir.toString());
                            }

                            PausableSwingWorker workerThread = CopyWorker.createAndShowGUI(row, jTable1.getColumn("Progress").getModelIndex(), srcDir, destDir);
                            String btnName = "btn" + row;
                            buttonThreadMap.put(btnName, workerThread);

                        } catch (Exception e) {
                            System.out.println("Exception");
                        }}
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Please choose Source and Destination");
                    }
                }}

                if (jTable1.getModel().getValueAt(row, col).toString().equals("Verify")) {
                    String s = jTable1.getModel().getValueAt(row, 0).toString();
                    driveName = s.substring(0, s.indexOf(":"));
                    if (!jTable1.getModel().getValueAt(row, jTable1.getColumn("S").getModelIndex()).toString().equals("Select") && !jTable1.getModel().getValueAt(row, jTable1.getColumn("D").getModelIndex()).toString().equals("Select")) {
                        try {
                            HashingE.main(null);
                        } catch (Exception e) {
                            System.out.println("Exception");
                        }
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Please Download then Verify");
                    }
                }
    }//GEN-LAST:event_jTable1MouseClicked

    String parseDate(String date){
       int i = 0;
       
       String newD = "";
       
       for(i=0;i<date.length();i++){
           char ch = date.charAt(i);
           if(ch == '-'){
               newD = newD + "/";
           } else {
             newD = newD + date.charAt(i);  
           }
           
       }
        
       return newD;
    }
    
    /**
     * History Filter
     * History can be filtered using two data FLIGHT_NO and COPY_DATE
     * given text field will ask user for FLIGHT_NO
     *
     */
    /**
     *
     */
    /**
     * log out feature
     * on clicking will be redirected to login form
     */
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws ClassNotFoundException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (InstantiationException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>

        try{
            UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
            UIManager.put("TabbedPane.selected", Color.gray);

            UIManager.put("TabbedPane.unselectedTabBackground", Color.black);
        } catch(Exception e){
            e.printStackTrace();
        }
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Home(1).setVisible(true);
            }

        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JTable historyTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    public static javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    public static javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    public static javax.swing.JTable jTable1;
    public static javax.swing.JTable jTable2;
    public static javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    public static javax.swing.JLabel uuidLabel;
    // End of variables declaration//GEN-END:variables


    static public class HeaderColor extends DefaultTableCellRenderer {
        public HeaderColor() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable mytable, Object value, boolean selected, boolean focused, int row, int column) {
            super.getTableCellRendererComponent(mytable, value, selected, focused, row, column);
            setBackground(new java.awt.Color(0, 0, 0));
            setForeground(new java.awt.Color(255, 255, 255));
            setFont(new Font("Helvetica", Font.PLAIN, 15));
            setPreferredSize(new Dimension(120, 45));
            return this;
        }
    }

    private static class HeaderRenderer implements TableCellRenderer {
        DefaultTableCellRenderer renderer;
        int horAlignment;

        public HeaderRenderer(JTable table, int horizontalAlignment) {
            horAlignment = horizontalAlignment;
            renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            JLabel label = (JLabel) c;
            label.setHorizontalAlignment(horAlignment);
            return label;
        }
    }


    public void changeTable(JTable table, int column_index) {
        table.getColumnModel().getColumn(column_index).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String st_val = table.getModel().getValueAt(row, (jTable1.getColumn("Action").getModelIndex())).toString();

                if (st_val.equalsIgnoreCase("Download") && row % 2 == 0) {
                    c.setBackground(new Color(198, 13, 47));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    //label.setBorder(new RoundedBorder(Color.black, 10));
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(236, 236, 236)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

                } else if (st_val.equalsIgnoreCase("Download") && row % 2 != 0) {
                    c.setBackground(new Color(198, 13, 47));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    //label.setBorder(new RoundedBorder(Color.black, 10));
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(222, 222, 222)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

                } else if (st_val.equalsIgnoreCase("") && row % 2 == 0) {
                    c.setBackground(new Color(236, 236, 236));
                    c.setForeground(new Color(236, 236, 236));
                } else if (st_val.equalsIgnoreCase("") && row % 2 != 0) {
                    c.setBackground(new Color(222, 222, 222));
                    c.setForeground(Color.white);
                } else {
                    c.setBackground(Color.BLUE);
                }
                return c;
            }
        });
    }

    public void changeTable3Edit(JTable table, int column_index) {
        table.getColumnModel().getColumn(column_index).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String st_val = table.getModel().getValueAt(row, (jTable3.getColumn("Edit").getModelIndex())).toString();

                if (st_val.equalsIgnoreCase("Edit") && row % 2 == 0) {
                    c.setBackground(new Color(198, 13, 47));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    //label.setBorder(new RoundedBorder(Color.black, 10));
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(236, 236, 236)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

                } else if (st_val.equalsIgnoreCase("Edit") && row % 2 != 0) {
                    c.setBackground(new Color(198, 13, 47));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    //label.setBorder(new RoundedBorder(Color.black, 10));
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(222, 222, 222)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

                } else if (st_val.equalsIgnoreCase("") && row % 2 == 0) {
                    c.setBackground(new Color(236, 236, 236));
                    c.setForeground(new Color(236, 236, 236));
                } else if (st_val.equalsIgnoreCase("") && row % 2 != 0) {
                    c.setBackground(new Color(222, 222, 222));
                    c.setForeground(Color.white);
                } else {
                    c.setBackground(Color.BLUE);
                }
                return c;
            }
        });
    }

    public void changeHistoryTableView(JTable table, int column_index) {
        table.getColumnModel().getColumn(column_index).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String st_val = table.getModel().getValueAt(row, (historyTable.getColumn("View").getModelIndex())).toString();

                if (st_val.equalsIgnoreCase("View") && row % 2 == 0) {
                    c.setBackground(new Color(198, 13, 47));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    //label.setBorder(new RoundedBorder(Color.black, 10));
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(236, 236, 236)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

                } else if (st_val.equalsIgnoreCase("View") && row % 2 != 0) {
                    c.setBackground(new Color(198, 13, 47));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    //label.setBorder(new RoundedBorder(Color.black, 10));
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(222, 222, 222)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

                } else if (st_val.equalsIgnoreCase("") && row % 2 == 0) {
                    c.setBackground(new Color(236, 236, 236));
                    c.setForeground(new Color(236, 236, 236));
                } else if (st_val.equalsIgnoreCase("") && row % 2 != 0) {
                    c.setBackground(new Color(222, 222, 222));
                    c.setForeground(Color.white);
                } else {
                    c.setBackground(Color.BLUE);
                }
                return c;
            }
        });
    }

    public void changeTableFileChooserS(JTable table, int column_index) {
        table.getColumnModel().getColumn(column_index).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String st_val = table.getModel().getValueAt(row, column_index).toString();
                if (st_val.equalsIgnoreCase("Choose") && row % 2 == 0) {
                    c.setBackground(new Color(198, 13, 47));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(236, 236, 236)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                    label.setToolTipText(jTable1.getModel().getValueAt(row, jTable1.getColumn("S").getModelIndex()).toString());
                } else if (st_val.equalsIgnoreCase("Choose") && row % 2 != 0) {
                    c.setBackground(new Color(198, 13, 47));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(222, 222, 222)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                    label.setToolTipText(jTable1.getModel().getValueAt(row, jTable1.getColumn("S").getModelIndex()).toString());
                } else if (st_val.equalsIgnoreCase("") && row % 2 == 0 /*&& currupted == false*/) {
                    c.setBackground(new Color(236, 236, 236));
                    c.setForeground(new Color(236, 236, 236));
                } else if (st_val.equalsIgnoreCase("") && row % 2 != 0 /*&& currupted == false*/) {
                    c.setBackground(new Color(222, 222, 222));
                    c.setForeground(Color.white);
                } else {
                    c.setBackground(Color.blue);
                }
                return c;
            }
        });
    }

    public void changeTableFileChooserD(JTable table, int column_index) {
        table.getColumnModel().getColumn(column_index).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String st_val = table.getModel().getValueAt(row, column_index).toString();
                if (st_val.equalsIgnoreCase("Choose") && row % 2 == 0) {
                    c.setBackground(new Color(198, 13, 47));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(236, 236, 236)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                    if (!(jTable1.getModel().getValueAt(row, jTable1.getColumn("D").getModelIndex()).toString().equals("Select"))) {
                        label.setToolTipText(jTable1.getModel().getValueAt(row, jTable1.getColumn("D").getModelIndex()).toString() + "\\" + jTable3.getModel().getValueAt(row, 0).toString().substring(0, 1) + "_" + jTable3.getModel().getValueAt(row, 3).toString() + "_" + jTable3.getModel().getValueAt(row, 4).toString() + "_" + jTable3.getModel().getValueAt(row, 2).toString());
                    } else {
                        label.setToolTipText(jTable1.getModel().getValueAt(row, jTable1.getColumn("D").getModelIndex()).toString());
                    }

                } else if (st_val.equalsIgnoreCase("Choose") && row % 2 != 0) {
                    c.setBackground(new Color(198, 13, 47));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(222, 222, 222)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                    label.setToolTipText(jTable1.getModel().getValueAt(row, jTable1.getColumn("D").getModelIndex()).toString());
                    if (!(jTable1.getModel().getValueAt(row, jTable1.getColumn("D").getModelIndex()).toString().equals("Select"))) {
                        label.setToolTipText(jTable1.getModel().getValueAt(row, jTable1.getColumn("D").getModelIndex()).toString() + "\\" + jTable3.getModel().getValueAt(row, 0).toString().substring(0, 1) + "_" + jTable3.getModel().getValueAt(row, 3).toString() + "_" + jTable3.getModel().getValueAt(row, 4).toString() + "_" + jTable3.getModel().getValueAt(row, 2).toString());
                    } else {
                        label.setToolTipText(jTable1.getModel().getValueAt(row, jTable1.getColumn("D").getModelIndex()).toString());
                    }
                } else if (st_val.equalsIgnoreCase("") && row % 2 == 0 /*&& currupted == false*/) {
                    c.setBackground(new Color(236, 236, 236));
                    c.setForeground(new Color(236, 236, 236));
                } else if (st_val.equalsIgnoreCase("") && row % 2 != 0 /*&& currupted == false*/) {
                    c.setBackground(new Color(222, 222, 222));
                    c.setForeground(Color.white);
                } else {
                    c.setBackground(Color.blue);
                }
                return c;
            }
        });
    }

    public void changeTableVerify(JTable table, int column_index) {
        table.getColumnModel().getColumn(column_index).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String st_val = table.getModel().getValueAt(row, (jTable1.getColumn("Check").getModelIndex())).toString();

                if (st_val.equalsIgnoreCase("Verify") && row % 2 == 0) {
                    c.setBackground(new Color(198, 13, 47));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(236, 236, 236)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                } else if (st_val.equalsIgnoreCase("Verify") && row % 2 != 0) {
                    c.setBackground(new Color(198, 13, 47));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(222, 222, 222)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                } else if (st_val.equalsIgnoreCase("") && row % 2 == 0) {
                    c.setBackground(new Color(236, 236, 236));
                    c.setForeground(new Color(236, 236, 236));
                } else if (st_val.equalsIgnoreCase("") && row % 2 != 0) {
                    c.setBackground(new Color(222, 222, 222));
                    c.setForeground(Color.white);
                } else {
                    c.setBackground(Color.BLUE);
                }
                return c;
            }
        });
    }

    public void driveCurrupted(JTable table, int column_index) {
        table.getColumnModel().getColumn(column_index).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (currupted == false && row % 2 == 0) {
                    c.setBackground(new Color(0, 128, 0));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(236, 236, 236)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                } else if (currupted == false && row % 2 != 0) {
                    c.setBackground(new Color(0, 128, 0));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(222, 222, 222)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                } else if (currupted == false && row % 2 == 0) {
                    c.setBackground(new Color(236, 236, 236));
                    c.setForeground(new Color(236, 236, 236));
                } else if (currupted == false && row % 2 != 0) {
                    c.setBackground(new Color(222, 222, 222));
                    c.setForeground(Color.white);
                } else {
                    c.setBackground(Color.red);
                    c.setForeground(Color.white);
                }
                return c;
            }
        });
    }

    public void changeTableNotCorrupted(JTable table) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 2 == 0) {
                    c.setBackground(new Color(236, 236, 236));
                    c.setForeground(Color.black);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                } else if (row % 2 != 0) {
                    c.setBackground(new Color(222, 222, 222));
                    c.setForeground(Color.black);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                } else {
                    c.setBackground(Color.BLUE);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                }
                return c;
            }
        });
    }
    
    public void changeUserTableEditDelete(JTable table, int column_index) {
        table.getColumnModel().getColumn(column_index).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                //String st_val = table.getModel().getValueAt(row, (jTable2.getColumn("Edit").getModelIndex())).toString();
                String st_val = table.getModel().getValueAt(row, column_index).toString();
                
                if ((st_val.equalsIgnoreCase("Edit") || st_val.equalsIgnoreCase("Delete")) && row % 2 == 0) {
                    c.setBackground(new Color(198, 13, 47));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    //label.setBorder(new RoundedBorder(Color.black, 10));
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(236, 236, 236)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

                } else if ((st_val.equalsIgnoreCase("Edit") || st_val.equalsIgnoreCase("Delete")) && row % 2 != 0) {
                    c.setBackground(new Color(198, 13, 47));
                    c.setForeground(Color.white);
                    JLabel label = (JLabel) c;
                    label.setHorizontalAlignment(JLabel.CENTER);
                    //label.setBorder(new RoundedBorder(Color.black, 10));
                    label.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, new Color(222, 222, 222)), BorderFactory.createEmptyBorder(8, 8, 8, 8)));

                } else if (st_val.equalsIgnoreCase("") && row % 2 == 0) {
                    c.setBackground(new Color(236, 236, 236));
                    c.setForeground(new Color(236, 236, 236));
                } else if (st_val.equalsIgnoreCase("") && row % 2 != 0) {
                    c.setBackground(new Color(222, 222, 222));
                    c.setForeground(Color.white);
                } else {
                    c.setBackground(Color.BLUE);
                }
                return c;
            }
        });
    }

    class MyTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Color getBackground() {
            return super.getBackground();
        }

        @Override
        public Color getForeground() {
            return super.getForeground();
        }
    }
    
//    public class RoundJTextField extends JTextField {
//        private Shape shape;
//        
//        public RoundJTextField(int size) {
//            super(size);
//            setOpaque(false); 
//        }
//        
//        protected void paintComponent(Graphics g) {
//             g.setColor(getBackground());
//             g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
//             super.paintComponent(g);
//        }
//        
//        protected void paintBorder(Graphics g) {
//             g.setColor(getForeground());
//             g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
//        }
//        
//        public boolean contains(int x, int y) {
//             if (shape == null || !shape.getBounds().equals(getBounds())) {
//                 shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 15, 15);
//             }
//             return shape.contains(x, y);
//        }
//    }
    

    
    public static double folderSize(File directory) {
    double length = 0;
    for (File file : directory.listFiles()) {
        if (file.isFile())
            length += file.length();
        else
            length += folderSize(file);
    }
    return length;
}
    
    private static class RoundedBorder implements Border {

    private int radius;


    RoundedBorder(int radius) {
        this.radius = radius;
    }


    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
    }


    public boolean isBorderOpaque() {
        return true;
    }


    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.drawRoundRect(x, y, width-1, height-1, radius, radius);
    }
}
}
