/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * timeline.java
 *
 * Created on Dec 29, 2009, 3:40:51 PM
 */

package maggi_1;
import com.toedter.calendar.*;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;


/**
 *
 * @author Toshish
 */

/********* TIME LINE OBEJCTS ARE MADE HERE **********/
class PopupObject{

    public JLabel label_image,label_topic,label_subtopic,label_time;
    public JCheckBox checkbox_deleteselected;
    int l1id = 0,l2id = 0;
    Timestamp datetime;
    
        
    public PopupObject(){
        label_image = new JLabel();
        label_topic = new JLabel();
        label_subtopic = new JLabel();
        label_time =  new JLabel();
        
        checkbox_deleteselected = new JCheckBox();
    }
}


public class TimelineView extends javax.swing.JFrame {

    PopupObject popupobject[];
    JPanel panel_popupobject[];
    public TimeLineTextInfoView textinfoview;
    MouseAdapter mouselistener_panel,mouselistener_radbtn;
    boolean prevstate_radbtn_keepMonth = false,prevstate_radbtn_keepLast7Days = false,prevstate_radbtn_keepToday = false,prevstate_radbtn_keepNothing = false;
    ItemListener itemlistener_checkbox;
    Timer timer_timeline = null;
    
    int object_count = 0;
    JCalendar calendar_timeline;
    Date date;

    DateFormat dfdate;
    DateFormat dfmonth;
    DateFormat dfyear;

    int atleastonecheckbox_checked = 0;

    /** Creates new form timeline */
    public TimelineView() {
       
        initComponents();

        addButtons_tosamegroup();
        initWindowListener();
        initButtonListener();
        initMouseListener();
        initItemListener();
        initDateFormat();
        initCalendar_and_its_Listener();
        textinfoview = new TimeLineTextInfoView();
        rad_btn_falsebtn.setSelected(true);

        setData(Integer.parseInt(dfdate.format(date)),Integer.parseInt(dfmonth.format(date)),Integer.parseInt(dfyear.format(date)));
    }

    /***** ADD BUTTONS TO SAME GROUP ******/
    void addButtons_tosamegroup(){
        btngroup_deleteItems.add(rad_btn_keepNothing);
        btngroup_deleteItems.add(rad_btn_keepThisMonth);
        btngroup_deleteItems.add(rad_btn_keepLast7Days);
        btngroup_deleteItems.add(rad_btn_keepToday);
        btngroup_deleteItems.add(rad_btn_falsebtn);
    }

    /********* INITIALIZE WINDOW LISTENER *********/
    public void initWindowListener(){
        addWindowListener(new WindowAdapter() {

            @Override
            @SuppressWarnings("static-access")
            public void windowClosing(WindowEvent e) {
                
                Main.timelineview.textinfoview.dispose();
                Main.timelineview.textinfoview = null;

                dispose();
                Main.timelineview = null;

            }
        });
    }

    /******* INITIALIZE BUTTON LISTENER **********/
    public void initButtonListener(){
       btn_close.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                
                textinfoview.dispose();
                textinfoview = null;
                dispose();
                Main.timelineview = null;
            }
        });

        btn_deleteSelected.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btn_deleteACTION(e);
            }
        });

    }
    
    /***** INITIALIZE PANEL CLICK LISTENER **********/
    public void initMouseListener(){

        mouselistener_panel = new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseEnteredPanelACTION(e);
            }

            @Override
            public void mouseExited(MouseEvent e){
                timer_timeline.stop();                
            }
        };

        mouselistener_radbtn = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e){
                if(((JRadioButton)e.getSource()).equals(rad_btn_keepNothing)){
                    if(rad_btn_keepNothing.isSelected()==prevstate_radbtn_keepNothing){
                        rad_btn_falsebtn.setSelected(true);
                    }

                    prevstate_radbtn_keepNothing = rad_btn_keepNothing.isSelected();
                }

                if(((JRadioButton)e.getSource()).equals(rad_btn_keepThisMonth)){
                    if(rad_btn_keepThisMonth.isSelected()==prevstate_radbtn_keepMonth)
                        rad_btn_falsebtn.setSelected(true);
                    
                    prevstate_radbtn_keepMonth = rad_btn_keepThisMonth.isSelected();
                }

                if(((JRadioButton)e.getSource()).equals(rad_btn_keepLast7Days)){
                    if(rad_btn_keepLast7Days.isSelected()==prevstate_radbtn_keepLast7Days)
                        rad_btn_falsebtn.setSelected(true);

                    prevstate_radbtn_keepLast7Days = rad_btn_keepLast7Days.isSelected();
                }

                if(((JRadioButton)e.getSource()).equals(rad_btn_keepToday)){
                    if(rad_btn_keepToday.isSelected()==prevstate_radbtn_keepToday)
                        rad_btn_falsebtn.setSelected(true);

                    prevstate_radbtn_keepToday = rad_btn_keepToday.isSelected();
                }
            }
        };

        rad_btn_falsebtn.setVisible(false);
        rad_btn_keepNothing.addMouseListener(mouselistener_radbtn);
        rad_btn_keepThisMonth.addMouseListener(mouselistener_radbtn);
        rad_btn_keepLast7Days.addMouseListener(mouselistener_radbtn);
        rad_btn_keepToday.addMouseListener(mouselistener_radbtn);
    }

    /****** INITIALIZE ITEM LISTENER ********/
    void initItemListener(){
        itemlistener_checkbox = new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if(((JCheckBox)e.getSource()).isSelected())
                    atleastonecheckbox_checked++;
                else
                    atleastonecheckbox_checked--;
            }
        };
    }


    /****** INITIALIZE DATE FORMATS *********/
    public void initDateFormat(){
        date = new java.util.Date();

        dfdate = new SimpleDateFormat("dd");
        dfmonth = new SimpleDateFormat("MM");
        dfyear = new SimpleDateFormat("yyyy");
    }

    /******** INITIALIZE CALENDAR AND ITS LISTENER **********/
    public void initCalendar_and_its_Listener(){

        calendar_timeline = new JCalendar();
        calendar_timeline.getDayChooser().setMaxDayCharacters(1);

        panel_calendar.add(calendar_timeline);
        calendar_timeline.setVisible(true);
        calendar_timeline.setBounds(5,15,240,180);

        calendar_timeline.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                atleastonecheckbox_checked = 0;
                setData(Integer.parseInt(dfdate.format(calendar_timeline.getDate())),Integer.parseInt(dfmonth.format(calendar_timeline.getDate())),Integer.parseInt(dfyear.format(calendar_timeline.getDate())));               
            }
        });
    }

    /************ SET DATA *************/
    private void setData(int objdate,int objmonth,int objyear)
    {
        //clear the timeline
        clearTimeline();

        try{
            Statement st = Main.con.createStatement();
            
            ResultSet rst = st.executeQuery("select count(*) as popcnt from POPUP where date(datetime)=date('"+objyear+"-"+objmonth+"-"+objdate+"')");
            rst.next();
            
            object_count=rst.getInt("popcnt");
            if(object_count==0)
                return;

            panel_popupobject = new JPanel[object_count];
            popupobject = new PopupObject[object_count];

            addpanel_popupobject();
            initpopupobjects(objdate,objmonth,objyear);

            for(int i=0;i<object_count;i++){
                set_popupobject_on_panelpopup(i);
                panel_popupobject[i].repaint();
                panel_popupobject[i].addMouseListener(mouselistener_panel);
            }

            panel_timeline.repaint();
            panel_timeline.setVisible(true);
            scrollpane_timeline.setViewportView(panel_timeline);
            scrollpane_timeline.repaint();
            
        }
        catch(SQLException ex){
            ex.printStackTrace();
            System.out.println("SQLException in setData()::" + ex.getMessage());
        }
    }


    /******* ADD PANELS TO TIMELINE *********/
    void addpanel_popupobject(){

        for(int i=0;i<object_count;i++){
            panel_popupobject[i] = new JPanel();
            panel_popupobject[i].setVisible(true);
            panel_popupobject[i].setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
            panel_popupobject[i].setLayout(new AbsoluteLayout());

            panel_timeline.add(panel_popupobject[i], new AbsoluteConstraints((180*i)+(10*(i+1)), 10, 180, 120));
        }
    }

    /********* INITIALIZE POPUP OBJECTS *********/
    void initpopupobjects(int objdate,int objmonth,int objyear){
        try{
            
            Statement st1 = Main.con.createStatement();
            Statement st2 = Main.con.createStatement();
            
            ResultSet rst = st1.executeQuery("select * from POPUP where date(datetime)=date('"+objyear+"-"+objmonth+"-"+objdate+"')");
            ResultSet rstL2;

            SimpleDateFormat dftime = new SimpleDateFormat("hh:mm a");
            String time;
        
            for(int i=0;i<object_count;i++){
                rst.absolute(i+1);

                popupobject[i] = new PopupObject();
                popupobject[i].l1id = rst.getInt("l1id");
                popupobject[i].l2id = rst.getInt("l2id");

                popupobject[i].label_topic.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
                popupobject[i].label_topic.setText(MySQLDatabase.getTopic(popupobject[i].l1id));
                popupobject[i].label_subtopic.setText(MySQLDatabase.getSubTopic(popupobject[i].l1id,popupobject[i].l2id));

                popupobject[i].checkbox_deleteselected.addItemListener(itemlistener_checkbox);

                popupobject[i].datetime =  rst.getTimestamp("datetime");
                time = new String(dftime.format(popupobject[i].datetime));

                popupobject[i].label_time.setText("Time: " + time);
                
                rstL2 = st2.executeQuery("select * from L2 where l1id = " + popupobject[i].l1id + " and l2id = " + popupobject[i].l2id);
                
                if(rstL2.next()){
                    ImageIcon imgicon = new ImageIcon(rstL2.getBytes("image"));
                    Image img = imgicon.getImage();
                    imgicon.setImage(img.getScaledInstance(78,78,Image.SCALE_SMOOTH));
                    popupobject[i].label_image.setIcon(imgicon);
                }
                else{
                    popupobject[i].label_image.setText("NO IMAGE AVAILABLE");
                }
            }
        }
        catch(SQLException se){
            System.out.println("SQLException in initpopupobjects()::" + se.getMessage());
        }
    }

    /********** ADD OBJECT ON PANEL ***********/
    void set_popupobject_on_panelpopup(int object_panel_id){
        //SET IMAGE
        popupobject[object_panel_id].label_image.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        panel_popupobject[object_panel_id].add(popupobject[object_panel_id].label_image, new AbsoluteConstraints(10, 10, 80, 80));

        panel_popupobject[object_panel_id].add(popupobject[object_panel_id].label_time, new AbsoluteConstraints(10, 100, 130, -1));
        panel_popupobject[object_panel_id].add(popupobject[object_panel_id].label_topic, new AbsoluteConstraints(100, 20, 70, 30));
        panel_popupobject[object_panel_id].add(popupobject[object_panel_id].label_subtopic, new AbsoluteConstraints(100, 50, 70, 40));
        panel_popupobject[object_panel_id].add(popupobject[object_panel_id].checkbox_deleteselected, new AbsoluteConstraints(150, 90, -1, -1));
    }

    
    /********** CLEAR TIMELINE **************/
    private void clearTimeline(){

        if(object_count==0)
            return;
        
        for(int i=0;i<object_count;i++){
            panel_popupobject[i].removeAll();
            panel_popupobject[i].repaint();
            popupobject[i] = null;
            panel_popupobject[i]=null;
        }
        panel_timeline.removeAll();
        panel_timeline.repaint();

    }

    
    /******* DELETE BUTTON *********/
    public void btn_deleteACTION(ActionEvent e){

        if(atleastonecheckbox_checked==0 && rad_btn_falsebtn.isSelected()){
            JOptionPane.showMessageDialog(null,"THERE IS NOTHING TO DELETE");
            return;
        }
            
        int choice=JOptionPane.showInternalConfirmDialog(getContentPane(), "This will delete all the selected Popup entries permenently from the database.\n Do you want to delete all entries?");

        if(choice==JOptionPane.YES_OPTION){
            textinfoview.setVisible(false);
            try{
                Statement st = Main.con.createStatement();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                for(int i=0;i<object_count;i++){
                    if(popupobject[i].checkbox_deleteselected.isSelected()==true)
                        st.executeUpdate("delete from POPUP where datetime='"+df.format(popupobject[i].datetime)+"'");
                }

                if(rad_btn_keepNothing.isSelected()){
                    st.executeUpdate("delete from POPUP");
                }
                
                if(rad_btn_keepThisMonth.isSelected()){
                    st.executeUpdate("delete from POPUP where datetime not like '" + dfyear.format(new Date()) + "-" + dfmonth.format(new Date()) + "%'");
                }

                if(rad_btn_keepToday.isSelected()){
                    st.executeUpdate("delete from POPUP where datetime not like '" + dfyear.format(new Date()) + "-" + dfmonth.format(new Date()) + "-" + dfdate.format(new Date()) + "%'");
                }

                if(rad_btn_keepLast7Days.isSelected())                   
                    st.executeUpdate("delete from POPUP where datetime < '" + df.format(new Date((new Date().getTime()-7*24*60*60*1000))) + "'");
                
            }
            catch(SQLException ex){
                System.out.println("SQLException in bt_deleteACTION()::" + ex.getMessage());
            }
            finally{
                atleastonecheckbox_checked = 0;
                setData(Integer.parseInt(dfdate.format(calendar_timeline.getDate())),Integer.parseInt(dfmonth.format(calendar_timeline.getDate())),Integer.parseInt(dfyear.format(calendar_timeline.getDate())));
            }
        }        
    }

    /******* MOUSE ENTERS ON PANEL *********/
     public void mouseEnteredPanelACTION(final MouseEvent me){

        textinfoview.setVisible(false);    
        if(timer_timeline!=null) {
            timer_timeline.stop();
            timer_timeline = null;
        }

        timer_timeline = new Timer(1000,new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                timer_timeline.stop();
                
                for(int i=0;i<object_count;i++){
                   if(me.getSource().equals(panel_popupobject[i])){
                         textinfoview.setData(popupobject[i].datetime, me.getXOnScreen(), me.getYOnScreen()-me.getY()-textinfoview.getHeight());
                         textinfoview.setVisible(true);                         
                    }                   
                }
            }
        });

        timer_timeline.start();        
     }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btngroup_deleteItems = new javax.swing.ButtonGroup();
        panel_calendar = new javax.swing.JPanel();
        btn_close = new javax.swing.JButton();
        btn_deleteSelected = new javax.swing.JButton();
        panel_deleteoption = new javax.swing.JPanel();
        rad_btn_keepNothing = new javax.swing.JRadioButton();
        rad_btn_keepToday = new javax.swing.JRadioButton();
        rad_btn_keepLast7Days = new javax.swing.JRadioButton();
        rad_btn_keepThisMonth = new javax.swing.JRadioButton();
        rad_btn_falsebtn = new javax.swing.JRadioButton();
        scrollpane_timeline = new javax.swing.JScrollPane();
        panel_timeline = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Timeline");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panel_calendar.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Calendar", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        javax.swing.GroupLayout panel_calendarLayout = new javax.swing.GroupLayout(panel_calendar);
        panel_calendar.setLayout(panel_calendarLayout);
        panel_calendarLayout.setHorizontalGroup(
            panel_calendarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 236, Short.MAX_VALUE)
        );
        panel_calendarLayout.setVerticalGroup(
            panel_calendarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 181, Short.MAX_VALUE)
        );

        getContentPane().add(panel_calendar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 250, 210));

        btn_close.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/cancel.png"))); // NOI18N
        btn_close.setText("Close");
        getContentPane().add(btn_close, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 400, -1, -1));

        btn_deleteSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/bin_closed.png"))); // NOI18N
        btn_deleteSelected.setText("Delete Selected");
        getContentPane().add(btn_deleteSelected, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 400, -1, -1));

        panel_deleteoption.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel_deleteoption.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        rad_btn_keepNothing.setText("Delete all Pop-ups");
        panel_deleteoption.add(rad_btn_keepNothing, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, -1, -1));

        rad_btn_keepToday.setText("Keep Today's Pop-ups Only");
        panel_deleteoption.add(rad_btn_keepToday, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        rad_btn_keepLast7Days.setText("Keep this week's Pop-ups Only");
        panel_deleteoption.add(rad_btn_keepLast7Days, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        rad_btn_keepThisMonth.setText("Keep this Month's Pop-ups Only");
        panel_deleteoption.add(rad_btn_keepThisMonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        rad_btn_falsebtn.setText("falsebtn");
        panel_deleteoption.add(rad_btn_falsebtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, -1, -1));

        getContentPane().add(panel_deleteoption, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, 350, 110));

        panel_timeline.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        scrollpane_timeline.setViewportView(panel_timeline);

        getContentPane().add(scrollpane_timeline, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 610, 160));

        jLabel1.setText("<html>\nAbove Options are for immediate deletion of Popups. <br>Select one of the above options and click on \"Delete Selected\" button at the bottom.\n");
        jLabel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Note:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 140, 350, 80));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TimelineView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_close;
    private javax.swing.JButton btn_deleteSelected;
    private javax.swing.ButtonGroup btngroup_deleteItems;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel panel_calendar;
    private javax.swing.JPanel panel_deleteoption;
    private javax.swing.JPanel panel_timeline;
    private javax.swing.JRadioButton rad_btn_falsebtn;
    private javax.swing.JRadioButton rad_btn_keepLast7Days;
    private javax.swing.JRadioButton rad_btn_keepNothing;
    private javax.swing.JRadioButton rad_btn_keepThisMonth;
    private javax.swing.JRadioButton rad_btn_keepToday;
    private javax.swing.JScrollPane scrollpane_timeline;
    // End of variables declaration//GEN-END:variables

    
}
