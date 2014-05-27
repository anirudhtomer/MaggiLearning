/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * gridview.java
 *
 * Created on 23 Dec, 2009, 12:15:36 PM
 */



/**
 *
 * @author toshish
 */
package maggi_1;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.netbeans.lib.awtextra.AbsoluteConstraints;


class l1object {
    public JPanel jPanel,jPanelImage;
    public JLabel jLabel,jLabelImageIcon,jLabelImage;
    public JTextField jTextField;
    public JCheckBox jCheckBox;
    
    public l1object() {
         jPanel = new JPanel();
         jPanelImage=new JPanel();
         jLabel=new JLabel();
         jLabelImage=new JLabel();
         jLabelImageIcon=new JLabel();
         jTextField=new JTextField();
      
    }
}

class l2Gridobject extends JPanel {
    Statement stm;
    ResultSet rs;

    public JPanel jPanelImageL2;
    public JLabel jLabelImageIconL2;
    public JLabel jLabelSubTopic;    
    public JButton jButtonSetPriority;
    public JButton jButtonDelete;
    
    public JCheckBox jCheckBoxL2;
    l2Gridobject(final int i,final int j)
    {

        MouseListener mouseListenerL2 = new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));

            }
            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            }
            @Override
            public void mouseClicked(MouseEvent e) {

                if(e.getButton()!=1)
                    return;

                if(e.getSource().equals(jCheckBoxL2))
                {
                    
                    try {
                        stm = Main.con.createStatement();
                        if(jCheckBoxL2.isSelected()==true)
                        {
                            checkBox_L2ACTION(i+1,j+1,true);
                        }
                        else
                        {
                            checkBox_L2ACTION(i+1,j+1,false);
                        }
                        
                    } catch (SQLException ex) {
                        Logger.getLogger(l2Gridobject.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                else
                {
                    if(e.getSource().equals(jButtonSetPriority))
                    {
                        try{
                        Statement st = Main.con.createStatement();
                        ResultSet rs = st.executeQuery("select priority from PRIORITY where l1id="+(i+1)+" and l2id="+(j+1));

                        if(!rs.next()){
                            JOptionPane.showMessageDialog(Main.gridview.getContentPane(),"NOT PRESENT IN PRIORITY LIST");
                            return;
                        }
//For Anirudh
                        if(Main.priorityview!=null){
                            if(Main.priorityview.getExtendedState() == JFrame.ICONIFIED)
                                Main.priorityview.setExtendedState(JFrame.NORMAL);

                        }
                        else{
                                Main.priorityview = new PriorityView();
                                Main.priorityview.setVisible(true);
                        }
                        Main.priorityview.btn_searchL2ACTION(rs.getInt("priority"));
                        }
                        catch(Exception ex)
                        {
                            System.out.println(ex);
                        }
                    }
                    else
                        //OPEN GRID VIEW
                        Main.gridview.l3view.setData(i+1,j+1,e.getXOnScreen(),e.getYOnScreen(),jCheckBoxL2,jButtonDelete);
                }

            }

        };




        jPanelImageL2=new JPanel();
        jLabelImageIconL2= new JLabel();
        jLabelSubTopic = new JLabel();
        
        jCheckBoxL2 = new JCheckBox();
        jButtonSetPriority = new JButton();
        jButtonDelete = new JButton();

        /******** ADD ACTION TO LISTENER *******/
        jButtonDelete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btndeleteACTION(e,i+1,j+1);
            }
        });


            setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
            setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
            add(jCheckBoxL2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 20, -1));

            jPanelImageL2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
            jPanelImageL2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

            jLabelImageIconL2.setLabelFor(jPanelImageL2);
            jPanelImageL2.add(jLabelImageIconL2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 40, 40));
            add(jPanelImageL2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 40, 40));
            try
            {
                stm = Main.con.createStatement();
                rs=stm.executeQuery("select subtopic,image,l2disabled from L2 where l1id="+(i+1)+" and l2id="+(j+1));
                rs.next();
                if(rs.getBytes("image")!=null)
                {
                    ImageIcon icon = new ImageIcon(rs.getBytes("image"));
                    Image img=icon.getImage().getScaledInstance(39, 39, Image.SCALE_SMOOTH);
                    icon.setImage(img);
                    jLabelImageIconL2.setIcon(icon);
                }

                jLabelSubTopic.setText(rs.getString("subtopic"));
                jLabelSubTopic.setFont(new Font(jLabelSubTopic.getFont().getFontName(),Font.BOLD,jLabelSubTopic.getFont().getSize()));
                add(jLabelSubTopic, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 160, -1));

                jButtonSetPriority.setText("Priority");
                jButtonSetPriority.setToolTipText("Set priority for " + rs.getString("subtopic")+".");
                add(jButtonSetPriority,new org.netbeans.lib.awtextra.AbsoluteConstraints(160,30,-1,-1));
                jButtonDelete.setText("");
                jButtonDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/bin_closed.png")));
                jButtonDelete.setContentAreaFilled(false);
                jButtonDelete.setToolTipText("Remove " + rs.getString("subtopic")+" permenently.");
                add(jButtonDelete,new org.netbeans.lib.awtextra.AbsoluteConstraints(230,40,16,16));

                if(rs.getInt("l2disabled")==0)
                {
                    jCheckBoxL2.setSelected(true);
                }
                else
                {
                    jCheckBoxL2.setSelected(false);
                }

            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                System.out.println("Exception in constructor L2Grid(): "+ex+" j="+j);
            }

            addMouseListener(mouseListenerL2);
            jCheckBoxL2.addMouseListener(mouseListenerL2);
            jButtonSetPriority.addMouseListener(mouseListenerL2);
    }

    /********* anirudh:: DELETE ACTION ON BUTTONS *********/
    @SuppressWarnings("static-access")
    void btndeleteACTION(ActionEvent e,int l1id,int l2id){

        int choice = JOptionPane.showInternalConfirmDialog(Main.gridview.getContentPane(), "This will remove this topic permenently.\n Do you want to delete it?");

        if(choice==JOptionPane.CANCEL_OPTION || choice==JOptionPane.NO_OPTION)
            return;        
            
        try{

            if(Main.popupcontrol.popupview.isVisible()==true){
                Main.popupcontrol.popupview.btncloseACTION();                
            }

            
            Statement st = Main.con.createStatement();

            String topic = MySQLDatabase.getTopic(l1id);
            String subtopic = MySQLDatabase.getSubTopic(l1id, l2id);

            st.executeUpdate("delete from L2 where l1id = " + l1id + " and l2id = " + l2id);
            st.executeUpdate("delete from TEXT where l1id = " + l1id + " and l2id = " + l2id);
            st.executeUpdate("delete from IMAGE where l1id = " + l1id + " and l2id = " + l2id);
            st.executeUpdate("delete from LINK where l1id = " + l1id + " and l2id = " + l2id);
            st.executeUpdate("delete from POPUP where l1id = " + l1id + " and l2id = " + l2id);

            MySQLDatabase.removeFromPriority(l1id, l2id);

            st.executeUpdate("update L2 set l2id = l2id - 1 where l1id = " + l1id + " and l2id > " + l2id);
            st.executeUpdate("update TEXT set l2id = l2id - 1 where l1id = " + l1id + " and l2id > " + l2id);
            st.executeUpdate("update IMAGE set l2id = l2id - 1 where l1id = " + l1id + " and l2id > " + l2id);
            st.executeUpdate("update LINK set l2id = l2id - 1 where l1id = " + l1id + " and l2id > " + l2id);
            st.executeUpdate("update POPUP set l2id = l2id - 1 where l1id = " + l1id + " and l2id > " + l2id);
            st.executeUpdate("update PRIORITY set l2id = l2id - 1 where l1id = " + l1id + " and l2id > " + l2id);

            Main.gridview.refreshL2(l1id);

            if(Main.timelineview!=null){
                Main.timelineview.dispose();
                Main.timelineview = null;
                Main.timelineview =  new TimelineView();
                Main.timelineview.setVisible(true);
            }

            MaggiTray.updateTrayIcon();
            Main.popupcontrol.setnewIDS(-1,-1,true);

            JOptionPane.showMessageDialog(null,topic + " " + subtopic + " is Removed Permanently");
        }
        catch(SQLException se){
            System.out.println("SQLException in btndeleteACTION()::" + se.getMessage());
        }
    }

    /*********anirudh:: CHECK BOX ACTION ON L2 VIEW *******/
     void checkBox_L2ACTION(int l1id,int l2id,boolean selected)
     {
            try{
                Statement st = Main.con.createStatement();
                ResultSet rst;

                if(selected){  //IF CHECK BOX IS SELECTED
                    st.executeUpdate("update L2 set l2disabled = 0 where l1id = " + l1id + " and l2id = " + l2id);

                    //check if entry is already present in priority
                    rst = st.executeQuery("select * from PRIORITY where l1id = " + l1id + " and l2id = " + l2id);

                    if(!rst.next()){
                        if(MySQLDatabase.checkAllLinksDisabled(l1id, l2id)){
                            JOptionPane.showMessageDialog(null,"SELECT ATLEAST ONE URL FOR THIS TOPIC");
                            jCheckBoxL2.setSelected(false);
                        }
                        else{
                            rst = st.executeQuery("select count(priority) as cntp from PRIORITY");

                            rst.next();

                            st.executeUpdate("insert into PRIORITY values (" + l1id + "," + l2id + "," + (rst.getInt("cntp")+1) + ",0,1)");
                        }
                    }
                }
                else{//IF CHECKBOX IS NOT SELECTED
                    st.executeUpdate("update L2 set l2disabled = 1 where l1id = " + l1id + " and l2id = " + l2id);
                    MySQLDatabase.removeFromPriority(l1id, l2id);
                }
                
            }

            catch(SQLException se){
                System.out.println("SQLException in checkBox_L2ACTION()::" + se.getMessage());
            }
     }}

public class GridView extends javax.swing.JFrame {
    Image img;
    SearchResultView searchview;
    L3View l3view;
    Process process_search = null;
    Thread thread_search = null;
    ProgressBar progressbar = null;

    File f =  new File(Main.curdir + "/CACHE/SEARCHIMAGES/erroutput.txt");

    /** Creates new form gridview */
    public GridView() {

        initComponents();

        l3view = new L3View();
        initWindowListener();
        initComponentsManual();

        txtfield_search.requestFocusInWindow();

        checkbox_searchtype.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if(searchview==null){
                    if(checkbox_searchtype.isSelected()){
                        txtfield_search.setEnabled(false);
                        jList1.setEnabled(false);
                    }
                    else{
                        txtfield_search.setEnabled(true);
                        jList1.setEnabled(true);
                    }
                }
            }
        });

        initFromDatabase();
        listmodel = (DefaultListModel) jList1.getModel();

        initTextBoxListener();

        jScrollPane1.setPreferredSize(new Dimension(jScrollPane1.getWidth(),0));

        mouseListener = new MouseListener() {

            public void mouseClicked(MouseEvent arg0) {

                if(Main.gridview.l3view.isVisible())
                    Main.gridview.l3view.btn_closeACTION();

                if(arg0.getButton()!=1)
                    return;

                for(int i=0;i<9;i++)
                {
                    if(arg0.getSource().equals(l1gridobjects[i].jLabel))
                    {
                        l1gridobjects[i].jTextField.setVisible(true);
                        l1gridobjects[i].jTextField.setText(l1gridobjects[i].jLabel.getText());
                        l1gridobjects[i].jTextField.setBounds(l1gridobjects[i].jLabel.getX(),l1gridobjects[i].jLabel.getY(),l1gridobjects[i].jLabel.getWidth() ,l1gridobjects[i].jLabel.getHeight());

                        l1gridobjects[i].jTextField.setCaretPosition(0);
                        l1gridobjects[i].jTextField.selectAll();
                        l1gridobjects[i].jTextField.requestFocus();
                   //jTextField1.grabFocus();

                    }
                    if(arg0.getSource().equals(l1gridobjects[i].jPanel))
                    {
                        if(l1gridobjects[i].jTextField.isVisible()==true)
                        {
                            l1gridobjects[i].jLabel.setText(l1gridobjects[i].jTextField.getText());
                            l1gridobjects[i].jTextField.setVisible(false);
                            l1gridobjects[i].jLabel.setBounds(l1gridobjects[i].jLabel.getX(),l1gridobjects[i].jLabel.getY(),l1gridobjects[i].jTextField.getWidth() , l1gridobjects[i].jLabel.getHeight());
                            currentPanelNo=i;
                            updateTopic();
                            RefreshOtherViews_L1(i+1);
                        }
                        else
                        {
                            if(l1gridobjects[i].jTextField.isVisible()==false)
                            {

                                if(getWidth()==440)
                                {
                                    setBounds(getX(), getY(), getWidth()+jPanelgridL2.getWidth()+10, getHeight());
                                }
                                currentPanelNo=i;
                                refreshL2(i+1);
                                l1gridobjects[i].jLabelImage.setVisible(true);
                                l1gridobjects[i].jLabelImage.setVisible(false);


                                //setGridL2Visible(i);
                            }
                        }
                    }
                    if(arg0.getSource().equals(l1gridobjects[i].jLabelImage))
                    {
                        showFileOpenDialog();
                    }
                    if(arg0.getSource().equals(l1gridobjects[i].jCheckBox))
                    {
                        currentPanelNo=i;
                        updateCheckBox();
                    }
                }
            }

            public void mousePressed(MouseEvent arg0) {

            }

            public void mouseReleased(MouseEvent arg0) {

            }

            public void mouseEntered(MouseEvent arg0) {
              for(int i=0;i<9;i++)
              {
                if(arg0.getSource().equals(l1gridobjects[i].jLabelImage))
                {
                    l1gridobjects[i].jLabelImage.setVisible(true);
                    currentPanelNo=i;
                    getContentPane().setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                else
                {
                    if(arg0.getSource().equals(l1gridobjects[i].jLabelImageIcon))
                    {
                        l1gridobjects[i].jLabelImage.setVisible(true);
                        currentPanelNo=i;
                        getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                    else
                    {
                        if(arg0.getSource().equals(l1gridobjects[i].jPanel))
                        {
                            if(l1gridobjects[i].jTextField.isVisible()==false)
                                getContentPane().setCursor(new Cursor(Cursor.HAND_CURSOR));
                            else
                                getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                        else
                        if(arg0.getSource().equals(l1gridobjects[i].jLabel))
                        {

                        getContentPane().setCursor(new Cursor(Cursor.TEXT_CURSOR));
                        }

                }
              }

              }         

            }

            public void mouseExited(MouseEvent arg0) {
                for(int i=0;i<9;i++){

                    if(arg0.getSource().equals(l1gridobjects[i].jLabelImageIcon)){
                        l1gridobjects[i].jLabelImage.setVisible(false);
                    }
                    else
                    {
                                  getContentPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        
                    }
                }
            }
        };


        /******* CLICK EVENT FOR "search" BUTTON **********/
        btn_search.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btn_searchACTION();

            }
        });



        /********TOSHISH STARTS HERE ***********/


        int i;
        for(i=0;i<9;i++)
        {
            l1gridobjects[i].jPanel.addMouseListener(mouseListener);
        l1gridobjects[i].jLabel.addMouseListener(mouseListener);
        l1gridobjects[i].jLabelImageIcon.addMouseListener(mouseListener);
        l1gridobjects[i].jPanelImage.addMouseListener(mouseListener);
        l1gridobjects[i].jLabelImage.addMouseListener(mouseListener);

//        l1gridobjects[i].jCheckBox.addMouseListener(mouseListener);
    }
    }
    public void initTextBoxListener(){
        txtfield_search.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode()==8){

                    last_keycode = 8;

                    if(currentText.equalsIgnoreCase("")){
                        if(lastindex_arr_str>-1){
                            currentText = arr_str[lastindex_arr_str];
                            arr_str[lastindex_arr_str] = "";
                            lastindex_arr_str--;
                            currentText = currentText.substring(0,currentText.length()-1);
                        }
                    }

                    else{
                        currentText = currentText.substring(0,currentText.length()-1);
                        System.out.println("killa--" + currentText );
                    }

                    System.out.println(lastindex_arr_str + "  " +currentText);
                }
                else
                    if(e.getKeyChar()==10)
                    {
                        btn_searchACTION();
                    }
            }

            @Override
            @SuppressWarnings("static-access")
            public void keyReleased(KeyEvent e) {

                int current_keycode = e.getKeyCode();

                switch(current_keycode){
                    case 32:    if(last_keycode!=32)
                                    ++lastindex_arr_str;

                                currentText += " ";
                                arr_str[lastindex_arr_str] += currentText;

                                currentText = "";
                                break;

                    case 40:  //down arrow key
                                if(listmodel.getSize()!=0){
                                    totaltext="";
                                    for(int i=0;i<=lastindex_arr_str;i++){
                                        totaltext += arr_str[i];
                                    }

                                    jList1.requestFocusInWindow();
                                }
                                break;

                    case 8:   //BACKSPACE
                                keypressedACTION();
                                break;

                    default:  //any other letter
                                currentText += e.getKeyText(current_keycode);
                                keypressedACTION();
                                break;
                }

                last_keycode = current_keycode;
            }
        });

        jList1.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {

                if(jList1.getSelectedIndex()==-1)
                    return;

                currentText = (String) listmodel.getElementAt(jList1.getSelectedIndex());

                System.out.println(totaltext + "___" + currentText + "___" + lastindex_arr_str);
                txtfield_search.setText(totaltext + currentText);
            }
        });

        jList1.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e){
                if(e.getKeyCode()==10){ //to check if enter is pressed
                    txtfield_search.requestFocusInWindow();
                }
            }
        });
    }

    public void keypressedACTION(){

        try{
            Statement st = Main.con.createStatement();

            listmodel.removeAllElements();

            if(currentText.equalsIgnoreCase("")){

                jScrollPane1.setBounds(jScrollPane1.getX(),jScrollPane1.getY(),jScrollPane1.getWidth(),0);

                return;
            }

            ResultSet rst = st.executeQuery("select * from DICTIONARY where word like '" + currentText + "%' LIMIT 10" );

            if(rst.next()){

                int cnt = 0;
                do{
                    listmodel.add(cnt++,rst.getString("word"));
                }while(rst.next());

                if(cnt>10){
                    jScrollPane1.setBounds(jScrollPane1.getX(),jScrollPane1.getY(),jScrollPane1.getWidth(),100);
                   // jPanel13.setBounds(jPanel13.getX(),jPanel13.getY(),jPanel13.getWidth(),180+jScrollPane1.getHeight());
                    //setBounds(getX(),getY(),getWidth(),520+jScrollPane1.getHeight());




                }
                else{
                    jScrollPane1.setBounds(jScrollPane1.getX(),jScrollPane1.getY(),jScrollPane1.getWidth(),cnt*20);
                   // jPanel13.setBounds(jPanel13.getX(),jPanel13.getY(),jPanel13.getWidth(),180+jScrollPane1.getHeight());
                   // setBounds(getX(),getY(),getWidth(),520+jScrollPane1.getHeight());
                }

            }

            else{
                jScrollPane1.setBounds(jScrollPane1.getX(),jScrollPane1.getY(),jScrollPane1.getWidth(),0);
                //jPanel13.setBounds(jPanel13.getX(),jPanel13.getY(),jPanel13.getWidth(),180);
                //setBounds(getX(),getY(),getWidth(),520);
            }
        }
        catch(SQLException se){
            System.out.println("SQL EXCEPTION in keypressedACTION()" + se.getMessage());
        }

    }
    private void showFileOpenDialog()
    {
        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                if(jFileChooser.getSelectedFile()!=null)
                    updateImage(jFileChooser.getSelectedFile().toString().replace((char)(92),'/'));
           }
        };
        jFileChooser = new JFileChooser();
        FileFilter fileFilter= new FileNameExtensionFilter("All Supported Image Files", "jpg","jpeg","png","gif");
        jFileChooser.addChoosableFileFilter(fileFilter);

        jFileChooser.addActionListener(al);
        jFileChooser.showOpenDialog(GridView.this);


    }


    private void updateTopic()
    {
        try
        {
        Statement stm = Main.con.createStatement();
        stm.executeUpdate("update L1 set topic='" + l1gridobjects[currentPanelNo].jLabel.getText().replaceAll("'","")+"' where l1id="+ (currentPanelNo+1));
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }

    }
    private void updateCheckBox()
    {
        try
        {
        Statement stm = Main.con.createStatement();
        if(l1gridobjects[currentPanelNo].jCheckBox.isSelected()==true)
        {
            stm.executeUpdate("update L1 set l1disabled=" + 0 + " where l1id=" + (currentPanelNo+1));
        }
        else
        {
            stm.executeUpdate("update L1 set l1disabled=" + 1 + " where l1id=" + (currentPanelNo+1));
        }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }
private void updateImage(String imagepath)
{
        try
        {
            PreparedStatement ps;
            Statement stm;
            ResultSet rs;

            File fimg=new File(imagepath);
            FileInputStream fis = new FileInputStream(fimg);

            ps = Main.con.prepareStatement("update L1 set image=? where l1id=" + (currentPanelNo+1));
            ps.setBinaryStream(1, fis, (int)fimg.length());
            ps.executeUpdate();
            System.out.println("Image stored in database:" + imagepath + " to l1id:" + currentPanelNo+1);
            stm = Main.con.createStatement();
            rs = stm.executeQuery("select image from L1 where l1id=" + ((int)currentPanelNo+1));
            rs.next();
            if(rs.getBytes("image")!=null)
            {
                ImageIcon icon = new ImageIcon(rs.getBytes("image"));

                img = icon.getImage().getScaledInstance(78, 78, 1);
                icon.setImage(img);
                l1gridobjects[currentPanelNo].jLabelImageIcon.setIcon(icon);
            }
            else
            {
                System.out.println("Problem in loading image.");
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
}
private void initFromDatabase()
{
    Statement stm=null;
    ResultSet rs=null;

    try
    {
    stm = Main.con.createStatement();
    rs = stm.executeQuery("select * from L1");
    int i;
    for(i=0;i<9;i++)
    {
        if(rs.next())
        {
            l1gridobjects[i].jLabel.setText(rs.getString("topic"));


            if(rs.getBytes("image")!=null)
            {
            ImageIcon imgico1 = new ImageIcon(rs.getBytes("image"));
            img = imgico1.getImage().getScaledInstance(78, 78, Image.SCALE_SMOOTH);
            imgico1.setImage(img);
            l1gridobjects[i].jLabelImageIcon.setIcon(imgico1);
            l1gridobjects[i].jLabelImageIcon.setBounds(1,1,78,78);
            }
            l1gridobjects[i].jTextField.setVisible(false);
        }
        else
        {
            System.out.println("Reached to the end of result set.");
        }
    }
    }
    catch(Exception ex)
    {
        System.out.println(ex);
    }
}

public void initComponentsManual()
{
       setBounds(100,0,440,660);
       l1gridobjects = new l1object[9];

        int i;
        setPanelPoint();
        for(i=0;i<9;i++)
        {

        l1gridobjects[i]=new l1object();
        l1gridobjects[i].jPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        l1gridobjects[i].jPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        l1gridobjects[i].jLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        l1gridobjects[i].jLabel.setLabelFor(l1gridobjects[i].jPanel);
        l1gridobjects[i].jLabel.setText("nothing");
        l1gridobjects[i].jLabel.setToolTipText("Click to Edit.");
        l1gridobjects[i].jLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        //l1gridobjects[i].jLabel.setMaximumSize(new java.awt.Dimension(45, 25));
        l1gridobjects[i].jLabel.setMinimumSize(new java.awt.Dimension(20, 25));
       // l1gridobjects[i].jLabel.setPreferredSize(new java.awt.Dimension(45, 25));


        l1gridobjects[i].jPanel.add(l1gridobjects[i].jLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 25));
        l1gridobjects[i].jPanel.add(l1gridobjects[i].jTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        l1gridobjects[i].jPanelImage.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        l1gridobjects[i].jPanelImage.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        l1gridobjects[i].jLabelImage.setFont(new java.awt.Font("Tahoma", 0, 10));
        l1gridobjects[i].jLabelImage.setText("Change");
        l1gridobjects[i].jLabelImage.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        l1gridobjects[i].jLabelImage.setOpaque(true);
        l1gridobjects[i].jLabelImage.setVisible(false);
        l1gridobjects[i].jPanelImage.add(l1gridobjects[i].jLabelImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, 14));

        l1gridobjects[i].jLabelImageIcon.setLabelFor(l1gridobjects[i].jPanelImage);
        l1gridobjects[i].jPanelImage.add(l1gridobjects[i].jLabelImageIcon, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 80, 80));

        l1gridobjects[i].jPanel.add(l1gridobjects[i].jPanelImage, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 80, 80));
        //l1gridobjects[i].jPanel.add(l1gridobjects[i].jCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 100, -1, -1));


        Point p = new Point(getPanelPoint(i));

        AbsoluteConstraints absconstr = new AbsoluteConstraints(p.x,p.y,130,130);

        getContentPane().add(l1gridobjects[i].jPanel,absconstr );
        System.out.println(p.x + " " + p.y);

        }
        jPanelgridL2 = new JPanel();
    jPanelgridL2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    jPanelgridL2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
    jScrollpaneL2=new JScrollPane();
    jScrollpaneL2.setViewportView(jPanelgridL2);
    getContentPane().add(jPanelgridL2, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 20, 270, 609));



}


private void setPanelPoint()
{
    panelPoints=new Point[9];
    int i;
    for(i=0;i<9;i++)
    {
        panelPoints[i]=new Point();
    }
    panelPoints[0].setLocation(20, 20);
    panelPoints[1].setLocation(160, 20);
    panelPoints[2].setLocation(300, 20);
    panelPoints[3].setLocation(20, 160);
    panelPoints[4].setLocation(160, 160);
    panelPoints[5].setLocation(300, 160);
    panelPoints[6].setLocation(20, 300);
    panelPoints[7].setLocation(160, 300);
    panelPoints[8].setLocation(300, 300);
}
private Point getPanelPoint(int i)
{
    return(panelPoints[i]);
}

boolean refreshL2(int l1id)
{

    boolean retrefresh = false;
    clearL2Pane();

    try {

        Statement  st1 = Main.con.createStatement();
        Statement st = Main.con.createStatement();
        ResultSet rs = st.executeQuery("select count(*) from L2 where l1id="+l1id);
        ResultSet rstl2 = st1.executeQuery("select * from L2 where l1id = " + l1id);

        rs.next();
        currentL2cnt = rs.getInt(1);

        if(currentL2cnt == 0)
            return retrefresh;
        
        l2objects = new l2Gridobject[currentL2cnt];
        
        for(int j=0;j<currentL2cnt;j+=1)
        {
            rstl2.next();
            l2objects[j]=new l2Gridobject(l1id-1,rstl2.getInt("l2id")-1);
            jPanelgridL2.add(l2objects[j], new org.netbeans.lib.awtextra.AbsoluteConstraints(10, (60*j)+(10*(j+1)), 250, 60) );
            l2objects[j].repaint();
            l2objects[j].setVisible(true);
            
        }
        jPanelgridL2.repaint();
        jPanelgridL2.setVisible(true);
        retrefresh = true;
    }
    catch(Exception ex){
        System.out.println(ex);
    }
    finally{
        return retrefresh;
    }

}

void clearL2Pane()
{

    if(currentL2cnt==0)
        return;

    for(int i=0;i<currentL2cnt;i++)
    {
        l2objects[i].removeAll();
        l2objects[i]=null;

    }
    jPanelgridL2.removeAll();
    jPanelgridL2.repaint();

}


/******* ACTION LISTENER FOR BUTTON SEARCH ********/
    @SuppressWarnings("empty-statement")
    void btn_searchACTION(){

        if(searchview!=null){
           JOptionPane.showMessageDialog(Main.gridview.getContentPane(),"Another search is going on\nPlease search once it gets over");
           return;
        }

        if(checkbox_searchtype.isSelected()){
            searchview = new SearchResultView("",true);
            searchview.setVisible(true);
            searchview.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        }

        else if(NetworkSettings.checkNetworkConnection()){
            try{
                Statement st = Main.con.createStatement();
                ResultSet rst = st.executeQuery("select * from NETWORK_SETTINGS");
                rst.next();

                if(txtfield_search.getText().equals("")){
                    JOptionPane.showMessageDialog(Main.gridview.getContentPane(),"Please enter a string to search");
                    return;
                }

                String cmdline[] = {"java","OnlineSearch",Main.curdir + "/CACHE/SEARCHIMAGES/",txtfield_search.getText().replaceAll(" ","+"),"","",""};

                if(rst.getInt("proxyenabled")==1){
                    cmdline[4] = "true";
                    cmdline[5] = rst.getString("proxyaddress");
                    cmdline[6] = rst.getString("portaddress");
                }
                else{
                    cmdline[4] = "false";
                    cmdline[5] = "null";
                    cmdline[6] = "null";
                }

                if(f.exists())
                    f.delete();

                process_search = Runtime.getRuntime().exec(cmdline);

                searchview = new SearchResultView("" + txtfield_search.getText(),false);
                searchview.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                searchview.setVisible(false);

                progressbar = new ProgressBar();
                progressbar.setVisible(true);
                progressbar.startProgress();

                thread_search = new Thread(new Runnable() {

                    @SuppressWarnings("static-access")
                    public void run() {
                        try{
                            while(thread_search.isAlive()){
                                if(f.exists()){
                                    process_search.destroy();
                                    progressbar.thread_progress = null;
                                    progressbar.dispose();
                                    progressbar = null;
                                    thread_search = null;
                                }
                                thread_search.sleep(2000);
                            }                            
                        }
                        catch(NullPointerException ne){
                            if(searchview.getErrorStatus())
                                searchview.btncancelACTION();
                            else
                                searchview.setVisible(true);
                        }
                        catch(InterruptedException ie){
                            
                        }
                    }
                });

                thread_search.start();                
            }
            catch(IOException ie){
                System.out.println("IOException in btnsrchAction()" + ie.getMessage());
            }
            catch(SQLException se){
                System.out.println("SQLException in btnsrchAction()" + se.getMessage());
            }
        }
        else{
            JOptionPane.showMessageDialog(null,"There Was Some Problem With The Internet Connection\nPlease Make Sure The Right Network Settings\nOr Search Again");
        }
    }

    public boolean btnCloseACTION(){
        if(Main.gridview.searchview!= null){
            int choice = JOptionPane.showInternalConfirmDialog(getContentPane(),"Search Is Going On. Do You Want to Close it ?\nClick Yes to Abort Search.");

            if(choice==JOptionPane.YES_OPTION){
                Main.gridview.searchview.btncancelACTION();
                if(Main.gridview.progressbar!=null){
                    Main.gridview.progressbar.dispose();
                    Main.gridview.progressbar = null;
                }
            }
            else
                return false;
        }

        Main.gridview.l3view.deleteIMAGES();

        if(process_search!=null)
            Main.gridview.process_search.destroy();

        dispose();
        Main.gridview = null;

        return true;

    }
    //INITIALIZE WINDOW LISTENER

    private void initWindowListener() {
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e){
                btnCloseACTION();
            }
        });
    }

    /********* ON CHANGES IN L1 view make corresponding changes in the other views opened******/
    void RefreshOtherViews_L1(int refreshl1id){
        //CHECK IF SEARCH IS OPEN & act correspondingly

        if(searchview!=null)
            searchview.fillComboBox_Topic();

        if(Main.priorityview!=null){
            Main.priorityview.fillComboBox_Topic();
            Main.priorityview.refreshPanels_Topic(refreshl1id);
        }

        if(Main.gridview.l3view.isVisible())
            Main.gridview.l3view.readtopic_subtopic();

    }
   
/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_search = new javax.swing.JPanel();
        btn_search = new javax.swing.JButton();
        txtfield_search = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        checkbox_searchtype = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Grid View");
        setBounds(new java.awt.Rectangle(100, 100, 0, 0));
        setName("Grid View"); // NOI18N
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panel_search.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Search"));
        panel_search.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_search.setText("Search");
        panel_search.add(btn_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 50, 86, -1));
        panel_search.add(txtfield_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 280, -1));

        jList1.setModel(new javax.swing.DefaultListModel() {

        });
        jScrollPane1.setViewportView(jList1);

        panel_search.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, 100));

        checkbox_searchtype.setText("Search Offline");
        panel_search.add(checkbox_searchtype, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        getContentPane().add(panel_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 440, 410, 190));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GridView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_search;
    private javax.swing.JCheckBox checkbox_searchtype;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel panel_search;
    private javax.swing.JTextField txtfield_search;
    // End of variables declaration//GEN-END:variables

private l1object l1gridobjects[];
private Point []panelPoints;
private JFileChooser jFileChooser;
private int currentPanelNo;

private l2Gridobject l2objects[];
private JPanel []jPanelGridL2;
private JPanel jPanelgridL2;
private JScrollPane []jScrollPaneL2;
private JScrollPane jScrollpaneL2;
private int []currentL2Count;
private int currentL2cnt;



MouseListener mouseListener;


DefaultListModel listmodel;
    String currentText = "",totaltext = "";

    int lastindex_arr_str = -1;
    int last_keycode = 0;

    String arr_str[] = {"","","","","","","","","","",""};

}
