/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * L3View.java
 *
 * Created on Dec 26, 2009, 11:45:00 AM
 */

package maggi_1;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.netbeans.lib.awtextra.AbsoluteConstraints;


/**
 *
 * @author tomer
 */
public class L3View extends javax.swing.JFrame {

    int l1id,l2id;    
    int imgcnt,linkcnt;
    int lastimg_cnt;

    JFileChooser fileChooser;
    Timer timer_image = null;

    JCheckBox checkbox_link[];
    JLabel label_link[];
    int x,y;

    JCheckBox prevL2checkbox = null;
    JButton prevL2deletebutton = null;

    LinkListener l3linklistener;
    ItemListener itemchanged_checkbox;

    String folderpath_l3image = Main.curdir + "/CACHE/L3IMAGES/";


    /** Creates new form L3View */
    public L3View(){

        initComponents();
      
        initMouseListener();  //to add images externally
        initButtonListener();

        inititemlistener();        

        label_videoURL.addMouseListener(l3linklistener.hyperlinkListener);
    }

    /****** INIT LABEL LISTENER  & MOUSE LISTENER FOR ADDING IMAGES EXTERNALLY ********/
    void initMouseListener(){

        l3linklistener = new LinkListener(getContentPane());

        label_addimage.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent me){
                fileChooser = new JFileChooser();
                FileFilter fileFilter= new FileNameExtensionFilter("All Supported Image Files", "jpg","jpeg","png","gif");
                fileChooser.addChoosableFileFilter(fileFilter);

                fileChooser.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        try{
                            FileInputStream fin;
                            File f;

                            String querystr = "insert into IMAGE values (" + l1id + "," + l2id + ",?)";
                            PreparedStatement ps = Main.con.prepareStatement(querystr);

                            if(fileChooser.getSelectedFile()==null)
                                return;

                            f = new File(fileChooser.getSelectedFile().toString().replace((char)(92),'/'));

                            if(f.exists()){
                                fin = new FileInputStream(f);
                                ps.setBinaryStream(1,fin);
                                ps.executeUpdate();

                                querystr = "update L2 set image = ? where l1id = " + l1id + " and l2id = " + l2id;
                                ps = Main.con.prepareStatement(querystr);
                                fin = new FileInputStream(f);
                                ps.setBinaryStream(1,fin);
                                ps.executeUpdate();
                               
                            }
                            else{
                                JOptionPane.showMessageDialog(Main.gridview.l3view,"Sorry Image Can't Be Added");
                            }
                            ps.close();
                        }
                        catch(SQLException se){
                            System.out.println("SQL Exception in updatetableIMAGE():: " + se.getMessage());
                        }
                        catch(FileNotFoundException fe){
                            System.out.println("FILENOTFOUND Exception in updatetableIMAGE():: " + fe.getMessage());
                        }
                    }
                });

                fileChooser.showOpenDialog(Main.gridview.l3view);

                setData(l1id, l2id, x, y, prevL2checkbox, prevL2deletebutton);
                
            }
        });

        label_img.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent me){
                label_addimage.setVisible(true);
            }

            @Override
            public void mouseExited(MouseEvent me){
                label_addimage.setVisible(false);
            }


        });
    }

    /********** INITIALIZE BUTTON LISTENER ***************/
    void initButtonListener(){

        btn_close.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                btn_closeACTION();                
            }
        });   
    }


    /************ INITIALIZE ITEM LISTENER FOR CHECK BOXES ********/
    public void inititemlistener(){
        itemchanged_checkbox = new ItemListener() {

            public void itemStateChanged(ItemEvent e) {

                try{
                    Statement st = Main.con.createStatement();
                    ResultSet rst;

                    for(int i=0;i<linkcnt;i++){

                        if(checkbox_link[i].equals(((JCheckBox)e.getSource()))){
                            if(((JCheckBox)e.getSource()).isSelected()){
                                st.executeUpdate("update LINK set linkdisabled = 0 where link = '" + label_link[i].getText() + "'");                                                            
                            }

                            else{
                                st.executeUpdate("update LINK set linkdisabled = 1 where link = '" + label_link[i].getText() + "'");
                                if(MySQLDatabase.checkAllLinksDisabled(l1id, l2id)){ //if yes all are disabled
                                    prevL2checkbox.setEnabled(true);
                                    prevL2checkbox.setSelected(false);
                                    prevL2checkbox.setEnabled(false);
                                }
                            }
                        }
                    }
                }
                catch(SQLException se){
                    System.out.println("SQLException in inititemlistener():: " + se.getMessage());
                }
            }
        };
    }

    void refreshL3LINKS(){
        removeOldLinks();
        readLINKS();
        Main.gridview.l3view.repaint();
    }

    /********** SET DATA ON THE L3view **********/
    public void setData(int l1id,int l2id,int xpos,int ypos,JCheckBox currentL2checkbox,JButton currentL2deletebutton){
        deleteIMAGES();
        label_img.setIcon(null);
        stopprevioustimer();

        if(prevL2checkbox!=null)
            prevL2checkbox.setEnabled(true);

        currentL2checkbox.setEnabled(false);
        prevL2checkbox = currentL2checkbox;

        if(prevL2deletebutton!=null)
            prevL2deletebutton.setEnabled(true);

        currentL2deletebutton.setEnabled(false);
        prevL2deletebutton = currentL2deletebutton;
        
        x = xpos;
        y = ypos;

        Main.gridview.l3view.l1id = l1id;
        Main.gridview.l3view.l2id = l2id;

        removeOldLinks();

        readtopic_subtopic();
        readLINKS();
        readIMAGES();
        
        startAnimation_Images();

        setVisible(true);
    }

    /******** REMOVE THE OLDER LINKS *******/
    void removeOldLinks(){
        for(int i=0;i<linkcnt;i++){
            Main.gridview.l3view.remove(label_link[i]);
            Main.gridview.l3view.remove(checkbox_link[i]);
        }
    }

    /******** READ THE TOPIC & SUBTOPIC *******/
    public void readtopic_subtopic(){

        label_topic.setText(MySQLDatabase.getTopic(l1id));
        label_subtopic.setText(MySQLDatabase.getSubTopic(l1id, l2id));
        label_videoURL.setText(MySQLDatabase.getVideoURL(l1id, l2id));

        if(label_videoURL.getText().contains("www.") || label_videoURL.getText().contains("http"))
            label_videoURL.setForeground(Color.BLUE);
        else{
            label_videoURL.setText("No Video URL present.");
            label_videoURL.setForeground(Color.RED);
        }
    }

    /******** READ IMAGES FROM THE DATABASE ********/
    public void readIMAGES(){

        try{
            InputStream imgstream;
            FileOutputStream foutimage;
            byte b[] = new byte[1000];
            imgcnt = 0;

            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select image from IMAGE where l1id = " + l1id + " and l2id = " + l2id);

            while(rst.next()){
                imgstream = rst.getBinaryStream("image");

                foutimage = new FileOutputStream(folderpath_l3image + (++imgcnt) +".jpg");

                while(imgstream.read(b)>0){
                    foutimage.write(b);
                }
                foutimage.close();
            }

            if(imgcnt == 0)
                label_img.setText("No Image Available.");

            else{
                label_img.setText(null);
                setlabelIMAGE(1);
                lastimg_cnt = 1;
            }

            st.close();
        }

        catch(SQLException se){
            System.out.println("SQLException in readIMAGES():: " + se.getMessage());
        }
        catch(FileNotFoundException fe){
            System.out.println("FileNotFoundException in readIMAGES():: " + fe.getMessage());
        }
        catch(IOException ie){
            System.out.println("IOException in readIMAGES():: " + ie.getMessage());
        }
    }

    /******** STOP PREVIOUS TIMERS *******/
    void stopprevioustimer(){
        if(timer_image!=null){
            timer_image.stop();
            timer_image = null;
        }
    }

    /********** ANIMATION OF THE IMAGES **********/
    void startAnimation_Images(){
        
        if(imgcnt == 0 || imgcnt == 1)
            return;

        timer_image =  new Timer(2800,new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                lastimg_cnt = ((lastimg_cnt + 1)%imgcnt);

                System.out.println(lastimg_cnt);
                if(lastimg_cnt == 0)
                    setlabelIMAGE(imgcnt);
                else
                    setlabelIMAGE(lastimg_cnt);
            }
        });

        timer_image.start();
    }
    
    /****** READ THE LINK FROM THE DATABASE ***********/
    public void readLINKS(){
        try{
            Statement st = Main.con.createStatement();

            ResultSet rst1 = st.executeQuery("select count(link) as cnt from LINK where l1id = " + l1id + " and l2id = " + l2id);

            if(rst1.next())
             linkcnt = rst1.getInt("cnt");

            else
                return;

            ResultSet rst2 = st.executeQuery("select link,linkdisabled from LINK where l1id = " + l1id + " and l2id = " + l2id);
            positionLINKS(rst2);

            st.close();            
        }
        catch(SQLException se){
            System.out.println("SQLException in readLINKS():: " + se.getMessage());
        }
    }


    /****** POSITION THE LINKS ON THE FRAME **********/
    @SuppressWarnings("static-access")
    public void positionLINKS(ResultSet rst){

        try{
            int htdifference = 15,prevX,prevY;
            int disabled[] = new int[linkcnt];
            
            checkbox_link = new JCheckBox[linkcnt];
            label_link = new JLabel[linkcnt];

            prevX = label_videoURL.getX();
            prevY = jLabel2.getY();
            
            for(int i=0;i<linkcnt;i++){
                rst.next();

                checkbox_link[i] = new JCheckBox("");
                label_link[i] = new JLabel(rst.getString("link"));

                if(rst.getInt("linkdisabled")==0)
                    disabled[i] = 0;
                else
                    disabled[i] = 1;

                label_link[i].addMouseListener(l3linklistener.hyperlinkListener);
                label_link[i].setForeground(Color.BLUE);
                label_link[i].setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/link.png")));
                checkbox_link[i].addItemListener(itemchanged_checkbox);
                
                getContentPane().add(checkbox_link[i],new AbsoluteConstraints(prevX,prevY+ htdifference + 5,-1,20));
                getContentPane().add(label_link[i],new AbsoluteConstraints(prevX + 20,prevY + htdifference + 5,label_videoURL.getWidth()-checkbox_link[i].getWidth(),20));

                prevY += htdifference;                
            }

            for(int i =0;i<linkcnt;i++){
                if(disabled[i]==0){
                    checkbox_link[i].setSelected(true);
                }
            }
            setBounds(x,y,getWidth(),prevY+30);

            btn_close.setBounds(prevX+getContentPane().getWidth()-btn_close.getWidth()-10,prevY+btn_close.getHeight()+30,-1,-1);
        }
        catch(SQLException se){
            System.out.println("SQLException in positionLINKS():: " + se.getMessage());
        }        

    }

    /******** SET THE IMAGE ON THE LABEL ***********/
    public void setlabelIMAGE(int val){
           
        ImageIcon imgicon = new ImageIcon(folderpath_l3image + val + ".jpg");

        Image img = imgicon.getImage();

        imgicon.setImage(img.getScaledInstance(label_img.getWidth()-2,label_img.getHeight()-2,Image.SCALE_SMOOTH));

        label_img.setIcon(imgicon);
    }


    /****** DELETE THE IMAGES **********/
    public void deleteIMAGES(){

        File f;
        for(int i=1;i<=imgcnt;i++){
            
            f = new File(folderpath_l3image + i + ".jpg");

            if(f.exists()){
                f.delete();
            }
        }
        
    }

    /***** ACTION LISTNER FOR CLOSE BUTTON *******/
    void btn_closeACTION(){
        deleteIMAGES();
        prevL2checkbox.setEnabled(true);
        prevL2deletebutton.setEnabled(true);
        prevL2deletebutton = null;
        prevL2checkbox = null;

        stopprevioustimer();
        setVisible(false);
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        label_topic = new javax.swing.JLabel();
        label_subtopic = new javax.swing.JLabel();
        label_videoURL = new javax.swing.JLabel();
        btn_close = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        label_addimage = new javax.swing.JLabel();
        label_img = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        label_topic.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        label_topic.setText("TOPIC");
        getContentPane().add(label_topic, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 250, 30));

        label_subtopic.setText("SubTopic");
        getContentPane().add(label_subtopic, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 250, -1));

        label_videoURL.setFont(new java.awt.Font("DejaVu Sans", 0, 12));
        label_videoURL.setText("Video URL is not available.");
        getContentPane().add(label_videoURL, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 320, 260, -1));

        btn_close.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/close.png"))); // NOI18N
        getContentPane().add(btn_close, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, 30, 30));

        jLabel1.setText("Video URL:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, -1, -1));

        jLabel2.setText("Reference Link/s:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, -1, -1));

        label_addimage.setText("Add Image");
        label_addimage.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        label_addimage.setOpaque(true);
        getContentPane().add(label_addimage, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 50, 80, 20));

        label_img.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        getContentPane().add(label_img, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 270, 250));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new L3View().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_close;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel label_addimage;
    private javax.swing.JLabel label_img;
    private javax.swing.JLabel label_subtopic;
    private javax.swing.JLabel label_topic;
    private javax.swing.JLabel label_videoURL;
    // End of variables declaration//GEN-END:variables

}
