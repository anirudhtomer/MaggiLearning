/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PopupView.java
 *
 * Created on Dec 27, 2009, 9:47:07 AM
 */

/**
 *
 * @author tomer
 */
package maggi_1;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;


public class PopupView extends javax.swing.JFrame {

    static int popup_X,popup_Y;
    static int vertical;
    
    int charptr,textlength;
    String strinfotext,strmoretext;
    String strlink = "",strtext = "";

    String folderpath_popupview = Main.curdir + "/CACHE/POPUPVIEWIMAGES/";
        
    int red,green,blue;
    String font = "",videoURL = "";

    StyledDocument doc;
    Style regular;

    ImageEnlargedFrameView imgframe;
        
    /** Creates new form PopupView */
    public PopupView(){

        initComponents();

        setPaneStyle();
        addListeners();

        setCornerPosition();
    }

    @SuppressWarnings("static-access")
    void showPopup(){
        Main.popupcontrol.popupview.setVisible(true);
        Main.popupcontrol.popupview.scrollpane_info.getVerticalScrollBar().setValue(0);
    }

    void addListeners(){

        initmouselistener();
        initBUTTON_LISTENER();
        initSLIDER_LISTENER();
    }

    void setCornerPosition(){

        popup_X = Main.screensize.width - this.getWidth();
        popup_Y = Main.screensize.height - this.getHeight();
        
        setLocation(popup_X,popup_Y);
    }
    
    void getFont_Colour(){
        try{
            Statement st = Main.con.createStatement();
            ResultSet rst1 = st.executeQuery("select * from POPUP_SETTINGS");

            rst1.next();
            
            red = Integer.parseInt(rst1.getString("colour").substring(0,3));
            green = Integer.parseInt(rst1.getString("colour").substring(3,6));
            blue = Integer.parseInt(rst1.getString("colour").substring(6));

            font = rst1.getString("font");
        }
        catch(SQLException se){
            System.out.println("SQLException in getFont_Colour()::" + se.getMessage() );
        }

    }
    
    void initSLIDER_LISTENER(){
        slider_image.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                setlabelIMAGE(((JSlider)e.getSource()).getValue());
            }

        });

    }

    public void initmouselistener(){

            label_image.addMouseListener(new MouseAdapter() {

            @Override
            @SuppressWarnings("static-access")
            public void mouseEntered(MouseEvent e){
                if(Main.popupcontrol.arr_popupobject[Main.popupcontrol.current_popupitemid].imagecount == 0)
                    return;
                imgframe = new ImageEnlargedFrameView(slider_image.getValue());
                imgframe.setLocation(popup_X - imgframe.getWidth(),getY());
                imgframe.setVisible(true);
            }

            @Override
            @SuppressWarnings("static-access")
            public void mouseExited(MouseEvent e){
                if(Main.popupcontrol.arr_popupobject[Main.popupcontrol.current_popupitemid].imagecount == 0)
                    return;
                imgframe.dispose();
            }
        });
    }

    public void initBUTTON_LISTENER(){

        btn_close.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btncloseACTION();
            }
        });

        btn_more.addActionListener(new ActionListener() {

            @SuppressWarnings("static-access")
            public void actionPerformed(ActionEvent e) {
                Main.popupcontrol.moreinfoview.showMore();
            }
        });

        btn_videoURL.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try{
                    Statement st  = Main.con.createStatement();
                    ResultSet rst = st.executeQuery("select * from GENERAL_SETTINGS");
                    rst.next();
                    @SuppressWarnings("static-access")
                    String[] cmdline = {rst.getString("defaultbrowser"),Main.popupcontrol.arr_popupobject[Main.popupcontrol.current_popupitemid].videoURL};
                    Process prs = Runtime.getRuntime().exec(cmdline);
                }
                catch(SQLException se){
                    System.out.println("SQL Exception in ntiButtonListener::btn)videoURl() " + se.getMessage());
                }
                catch(IOException ie){
                    System.out.println("IO EXCEPTION in intiButtonListener::btn)videoURl() " + ie.getMessage());
                }
            }
        });

        btn_prevpending.addActionListener(new ActionListener() {

            @SuppressWarnings("static-access")
            public void actionPerformed(ActionEvent e) {

                MySQLDatabase.setReadStatus(Main.popupcontrol.arr_popupobject[Main.popupcontrol.current_popupitemid++].DateTime);
                
                Main.popupcontrol.pending_count--;

                if(Main.popupcontrol.pending_count<=0){
                    btn_prevpending.setEnabled(false);
                    Main.popupcontrol.popupview.btn_prevpending.setIcon(new ImageIcon(Main.curdir + "/src/maggi_1/icons/p_stat.gif"));
                }
                
                else{
                    btn_prevpending.setEnabled(true);                    
                }

                setData();
                Main.popupcontrol.moreinfoview.setData();
                
            }
        });
    }

    @SuppressWarnings("static-access")
    public void btncloseACTION(){
        deleteIMAGES(Main.popupcontrol.arr_popupobject[Main.popupcontrol.current_popupitemid]);
        MySQLDatabase.setReadStatus(Main.popupcontrol.arr_popupobject[Main.popupcontrol.current_popupitemid].DateTime);
        PopUpController.popupview.setVisible(false);
        PopUpController.moreinfoview.setVisible(false);
        MaggiTray.updateTrayIcon();
    }
    
    void storePopup(int l1id,int l2id){
        
        selectLINK_TEXT(l1id,l2id);
        getTextLine_MoreLine(l1id,l2id);
    }

    @SuppressWarnings("static-access")
    void setData(){
        
        label_topic.setText(MySQLDatabase.getTopic(Main.popupcontrol.arr_popupobject[Main.popupcontrol.current_popupitemid].l1id));
        label_subtopic.setText(MySQLDatabase.getSubTopic(Main.popupcontrol.arr_popupobject[Main.popupcontrol.current_popupitemid].l1id,Main.popupcontrol.arr_popupobject[Main.popupcontrol.current_popupitemid].l2id));
        
        setPANEDATA();
        readIMAGES();
        label_image.repaint();
        set_SliderImage_Values();
        scrollpane_info.getVerticalScrollBar().setValue(0);
    }

    @SuppressWarnings("static-access")
    void set_SliderImage_Values(){
        slider_image.setMinimum(1);
        slider_image.setMaximum(Main.popupcontrol.arr_popupobject[Main.popupcontrol.current_popupitemid].imagecount);
        slider_image.setValue(1);
    }

    public void setPaneStyle(){
        Style def;

        doc = textpane_info.getStyledDocument();
        def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        regular = doc.addStyle("regular",def);
        
    }
    
    @SuppressWarnings("static-access")
    void setPANEDATA(){
        
        try {
            
            getFont_Colour();

            StyleConstants.setFontFamily(regular,font);
            StyleConstants.setForeground(regular,new Color(red,green,blue));

            doc.remove(0,doc.getLength());
            doc.insertString(0,Main.popupcontrol.arr_popupobject[Main.popupcontrol.current_popupitemid].str_textinfo,regular);
            textpane_info.repaint();
        } catch (BadLocationException ex) {
            System.out.println("BadLocationException in setPANEDATA::" + ex.getMessage());
        }
    }
    
    void readIMAGES(){
        try{

            @SuppressWarnings("static-access")
            PopUpItems popupitem = Main.popupcontrol.arr_popupobject[Main.popupcontrol.current_popupitemid];

            InputStream imgstream;
            FileOutputStream foutimage;
            byte b[] = new byte[1000];
            
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select image from IMAGE where l1id = " + popupitem.l1id + " and l2id = " + popupitem.l2id);

            while(rst.next()){
                imgstream = rst.getBinaryStream("image");

                foutimage = new FileOutputStream(folderpath_popupview + (++popupitem.imagecount) +".jpg");

                while(imgstream.read(b)>0){
                    foutimage.write(b);
                }
                foutimage.close();
            }

            if(popupitem.imagecount == 0)
                label_image.setText("NO IMAGES AVAILABLE");

            else{
                label_image.setText(null);
                setlabelIMAGE(1);
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

    public void selectLINK_TEXT(int l1id,int l2id){

        try{
            boolean genflag;
            int cnt;

            Statement st1 = Main.con.createStatement();
            Statement st2 = Main.con.createStatement();
            Statement st3 = Main.con.createStatement();

            ResultSet rst1 = st1.executeQuery("select count(*) as cnttext from TEXT where l1id = " + l1id + " and l2id = " + l2id);
            rst1.next();
            cnt = rst1.getInt("cnttext");

            rst1 = st1.executeQuery("select * from TEXT where l1id = " + l1id + " and l2id = " + l2id);
            rst1.next();

            genflag = false;
            while(cnt>0){

                strlink = rst1.getString("link");
                strtext = rst1.getString("text");
                charptr = rst1.getInt("charptr");

                ResultSet rst2 = st2.executeQuery("select linkdisabled from LINK where link = '" + strlink + "'");
                rst2.next();

                if(genflag == true && rst2.getInt("linkdisabled")==0)
                    break;

                if(rst2.getInt("linkdisabled")==1 && rst1.getInt("genpop")==1){
                    st3.executeUpdate("update TEXT set genpop = 0  where link = '" + strlink + "' and l1id = " + l1id + " and l2id = " + l2id);
                    genflag = true;
                }

                else if(rst2.getInt("linkdisabled")==0 && rst1.getInt("genpop")==1)
                    break;

                if(!rst1.next())
                    rst1.absolute(1);

                if(genflag)
                    cnt--;
            }

            if(cnt==0)
                strlink = "";  //next thing wont produce error even if strlink==""

            System.out.println("strlink:" + strlink);
            st3.executeUpdate("update TEXT set genpop = 0  where link = '" + strlink + "' and l1id = " + l1id + " and l2id = " + l2id);

            if(rst1.next()){
               st3.executeUpdate("update TEXT set genpop = 1  where link = '" + rst1.getString("link") + "' and l1id = " + l1id + " and l2id = " + l2id);
            }

            else{
                rst1.absolute(1);
                st3.executeUpdate("update TEXT set genpop = 1  where link = '" + rst1.getString("link") + "' and l1id = " + l1id + " and l2id = " + l2id);
            }

            st1.close();
            st2.close();
            st3.close();

        }
        catch(SQLException se){
            se.printStackTrace();
            System.out.println("SQL EXCEPTION IN readTEXTSTRING:: " + se.getMessage());
        }
    }

    public void getTextLine_MoreLine(int l1id,int l2id){
      
        try{
            Statement st1 = Main.con.createStatement();

            ResultSet rst = st1.executeQuery("select textlength from POPUP_SETTINGS");
            rst.next();
            textlength = rst.getInt("textlength");

            strinfotext = new String("");

            int i;
            for( i = charptr;i<strtext.length() && textlength!=0;i++){
                strinfotext += strtext.charAt(i);

                if(strtext.charAt(i)=='.')
                    textlength--;
                    charptr++;
            }

            if(i==strtext.length()){
                charptr = 0;
                st1.executeUpdate("update LINK set linkdisabled = 1 where link = '" + strlink + "' and l1id = " + l1id + " and l2id = " + l2id);

                //CHECK IF ALL THE LINKS ARE DISABLED...
                if(MySQLDatabase.checkAllLinksDisabled(l1id, l2id)){
                    //UPDATE L2View if opened
                    if(Main.gridview!=null){
                        Main.gridview.refreshL2(l1id);                        
                    }
                    
                }

                if(Main.gridview!=null){
                    if(Main.gridview.l3view.isVisible())
                        Main.gridview.l3view.refreshL3LINKS();
                }
            }

            st1.executeUpdate("update TEXT set charptr = " + charptr + " where link = '" + strlink + "' and l1id = " + l1id + " and l2id = " + l2id);
            
            strmoretext = new String("");

            int startindex =0 ,endindex =strtext.length();
            int cnt = 10;

            for(i=charptr;i>0 && cnt>=0;i--){
                if(strtext.charAt(i)=='.')
                cnt--;
            }

            if(i==0)
                startindex = 0;
            else
                startindex = i+1;

            cnt = 10;
            for(i=charptr;i<strtext.length() && cnt>=0;i++){
                if(strtext.charAt(i)=='.')
                    cnt--;
            }

            endindex = i;

            strmoretext = strtext.substring(startindex,endindex);

            st1.executeUpdate("insert into POPUP values('" +new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "','" + strlink + "','" + strinfotext + "','" + strmoretext + "',0," + l1id + "," + l2id + ")");
           
        }
        catch(SQLException se){
            System.out.println("SQLException in getTextLine_MoreLine():: " + se.getMessage());
        }
    }

    public void setlabelIMAGE(int val){

        ImageIcon imgicon = new ImageIcon(folderpath_popupview + val + ".jpg");

        Image img = imgicon.getImage();

        imgicon.setImage(img.getScaledInstance(label_image.getWidth(),label_image.getHeight(),Image.SCALE_SMOOTH));

        label_image.setIcon(imgicon);
    }

    void deleteIMAGES(PopUpItems popupitem){

        File f;
        for(int i=1;i<=popupitem.imagecount;i++){

            f = new File(folderpath_popupview + i + ".jpg");

            if(f.exists()){
                f.delete();
            }
        }

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
        label_image = new javax.swing.JLabel();
        slider_image = new javax.swing.JSlider();
        scrollpane_info = new javax.swing.JScrollPane();
        textpane_info = new javax.swing.JTextPane();
        btn_close = new javax.swing.JButton();
        btn_more = new javax.swing.JButton();
        btn_videoURL = new javax.swing.JButton();
        btn_prevpending = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        label_topic.setFont(new java.awt.Font("DejaVu Sans", 1, 18));
        label_topic.setText("Topic");
        getContentPane().add(label_topic, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 200, 20));

        label_subtopic.setFont(new java.awt.Font("DejaVu Sans", 0, 12));
        label_subtopic.setText("Subtopic");
        getContentPane().add(label_subtopic, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 200, -1));

        label_image.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        getContentPane().add(label_image, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 100, 100));

        slider_image.setPaintTicks(false);
        slider_image.setPaintTrack(false);
        slider_image.setToolTipText("Scroll horizantally to explore more images");
        getContentPane().add(slider_image, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 100, 30));

        textpane_info.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        textpane_info.setEditable(false);
        textpane_info.setOpaque(false);
        scrollpane_info.setViewportView(textpane_info);

        getContentPane().add(scrollpane_info, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, 280, 120));

        btn_close.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        btn_close.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/cancel.png"))); // NOI18N
        btn_close.setText("Close");
        getContentPane().add(btn_close, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 180, -1, 30));

        btn_more.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        btn_more.setText("More");
        getContentPane().add(btn_more, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 180, 70, 30));

        btn_videoURL.setFont(new java.awt.Font("DejaVu Sans", 1, 13)); // NOI18N
        btn_videoURL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/television.png"))); // NOI18N
        btn_videoURL.setText("Video");
        btn_videoURL.setToolTipText("View Video on Internet. (youtube.com)");
        getContentPane().add(btn_videoURL, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, -1, 30));

        btn_prevpending.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/view_oldpop.gif"))); // NOI18N
        getContentPane().add(btn_prevpending, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 0, 50, 40));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new PopupView();
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_close;
    private javax.swing.JButton btn_more;
    javax.swing.JButton btn_prevpending;
    private javax.swing.JButton btn_videoURL;
    private javax.swing.JLabel label_image;
    private javax.swing.JLabel label_subtopic;
    private javax.swing.JLabel label_topic;
    private javax.swing.JScrollPane scrollpane_info;
    private javax.swing.JSlider slider_image;
    private javax.swing.JTextPane textpane_info;
    // End of variables declaration//GEN-END:variables
}


class PopUpController{

    int l1id,l2id;
    
    static PopupView popupview;
    static MoreInfoView moreinfoview;
    static PopUpItems arr_popupobject[] = null;
    
    static int pending_count = 0;
    static int current_popupitemid = 0;

    Timer timer_popupanimation = null;
    
    enum GENERATETYPE{
        GENERATE_PENDING,GENERATE_NEW_DIRECT,GENERATE_NEW_FLAG;
    }

    GENERATETYPE current_gen_type;

    //Constructor
    public PopUpController() {
        popupview = new PopupView();
        popupview.setVisible(false);

        moreinfoview = new MoreInfoView();
        moreinfoview.setVisible(false);
    }

    public void setnewIDS(int l1id,int l2id,boolean direct_gen){
        this.l1id = l1id;
        this.l2id = l2id;

        if(l1id == -1 && l2id == -1)
            current_gen_type = GENERATETYPE.GENERATE_PENDING;
        else if(direct_gen == true)
            current_gen_type = GENERATETYPE.GENERATE_NEW_DIRECT;
        else if(direct_gen == false)
            current_gen_type = GENERATETYPE.GENERATE_NEW_FLAG;

        update_PendingCount();

        if(pending_count==0 && current_gen_type==GENERATETYPE.GENERATE_PENDING){
            JOptionPane.showMessageDialog(null,"No Pending Popups are available");
            return;
        }
        
        GeneratePopup();
    }

    void GeneratePopup(){

        switch(current_gen_type){
            case GENERATE_PENDING    :  generate_Pending();
                                        showPopup();
                                        break;
            case GENERATE_NEW_DIRECT :  popupview.storePopup(l1id, l2id);
                                        generate_Pending();
                                        showPopup();
                                        break;
            case GENERATE_NEW_FLAG   :  popupview.storePopup(l1id, l2id);
                                        generate_Pending();
                                        popupview.setVisible(false);
                                        break;
        }
    }

    void update_PendingCount(){
        pending_count = MySQLDatabase.getPendingCount();
        if(pending_count==1)
            popupview.btn_prevpending.setEnabled(false);
        else
            popupview.btn_prevpending.setEnabled(true);
    }

    void generate_Pending(){
        getPendingData();
        popupview.setData();
        moreinfoview.setData();
    }

    void getPendingData(){
        try{
            update_PendingCount();
            current_popupitemid = 0;
            arr_popupobject = new PopUpItems[pending_count];

            System.out.println(pending_count);
            Statement st = Main.con.createStatement();
            ResultSet rst1 = st.executeQuery("select * from POPUP where rdstatus = 0 order by datetime desc");
            ResultSet rst2;
            
            for(int i=0;i<pending_count;i++){

                arr_popupobject[i] = new PopUpItems();
                rst1.absolute(i+1);
                arr_popupobject[i].l1id = rst1.getInt("l1id");
                arr_popupobject[i].l2id = rst1.getInt("l2id");
                
                arr_popupobject[i].DateTime = rst1.getTimestamp("datetime");

                arr_popupobject[i].topic = MySQLDatabase.getTopic(arr_popupobject[i].l1id);
                arr_popupobject[i].subtopic =MySQLDatabase.getSubTopic(arr_popupobject[i].l1id, arr_popupobject[i].l2id);
                arr_popupobject[i].videoURL = MySQLDatabase.getVideoURL(arr_popupobject[i].l1id,arr_popupobject[i].l2id);

                arr_popupobject[i].mainlink = rst1.getString("mainlink");
                arr_popupobject[i].str_moreinfo = rst1.getString("moreline");
                arr_popupobject[i].str_textinfo = rst1.getString("textline");

            }
            
        }
        catch(SQLException ex){
            System.out.println("SQLException in getPendingData()  " + ex.getMessage());
        }
        
    }

    @SuppressWarnings("static-access")
    void showPopup(){
        
        if(pending_count>1){
            Main.popupcontrol.popupview.btn_prevpending.setIcon(new ImageIcon(Main.curdir + "/src/maggi_1/icons/view_oldpop.gif"));
        }
        else{
            Main.popupcontrol.popupview.btn_prevpending.setIcon(new ImageIcon(Main.curdir + "/src/maggi_1/icons/p_stat.gif"));
        }

        MaggiTray.updateTrayIcon();
        pending_count--;
        startAnimation();
    }

    @SuppressWarnings("static-access")
    void startAnimation(){

        popupview.vertical = Main.screensize.height;

        timer_popupanimation = new Timer(30,new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                popupview.showPopup();

                if(popupview.vertical>((Main.screensize.height-MaggiTray.maggitray.getTrayIconSize().getHeight()-14)-popupview.getHeight()))
                    popupview.setLocation(popupview.popup_X,popupview.vertical-=5);
                 
                else
                    timer_popupanimation.stop();
            }
        });

        timer_popupanimation.start();
    }

}

class PopUpItems{

    int l1id,l2id;
    int imagecount = 0;
    String videoURL = "";
    String str_textinfo = "",str_moreinfo = "";
    String topic="",subtopic="";
    String mainlink = "";
    Timestamp DateTime;
}