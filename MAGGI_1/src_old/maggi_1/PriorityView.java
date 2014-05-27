/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PriorityView.java
 *
 * Created on Jan 6, 2010, 11:28:46 AM
 */

package maggi_1;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

/**
 *
 * @author tomer
 */

class l2objects_priority{

    public JLabel label_topic,label_subtopic,label_image;
    public JRadioButton optionbtn_priority;
    public JSpinner spinner_totalcnt;
    public int l1id = 0 ,l2id = 0;

    public l2objects_priority() {

        label_topic = new JLabel();
        label_subtopic = new JLabel();
        label_image = new JLabel();
        optionbtn_priority = new JRadioButton();
        spinner_totalcnt = new JSpinner(new SpinnerNumberModel(1,0,null,1));
    }
}

public class PriorityView extends javax.swing.JFrame {

    l2objects_priority l2object[];

    int l2count = 0;
    JPanel panel_l2items[];
    JScrollBar vertical_scroll;
    ChangeListener changlistnr_spinnertotalcnt;
    ItemListener itemlistnr_priority;

    /** Creates new form PriorityView */
    public PriorityView() {

        initComponents();
        setBounds(100,100,410,620);
        initWindowListener();
        initbtnlistener();
        initSpinnerListener();
        initRadioButtonListener();
        initComboBoxListener();

        if(initpanel_priority()){
            
            btn_defaultpriority.setEnabled(false);
            btn_down.setEnabled(false);
            btn_up.setEnabled(false);
            btn_fastforward.setEnabled(false);
            btn_search.setEnabled(false);
            spinner_maxtopic.setEnabled(false);
            
            return;
        }

        initStatus();

        fillSpinner_maxtopic();
        fillComboBox_fastforward();
        fillComboBox_Topic();

        vertical_scroll = scrollpane_priority.getVerticalScrollBar();
    }

    //INITIALIZE WINDOW LISTENER
    private void initWindowListener(){
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e){
                dispose();
                Main.priorityview = null;
            }

        });
    }

    //INITIALIZE BUTTON LISTENER
    private void initbtnlistener(){

        btn_defaultpriority.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try{
                    Statement st = Main.con.createStatement();
                    for(int i=1;i<=5 && i<=l2count;i++){
                        st.executeUpdate("update PRIORITY set totalcount = " + (6-i) + ",currentcount = 0 where priority = " + i);
                        l2object[i-1].spinner_totalcnt.setValue(6-i);
                    }
                }
                catch(SQLException se){
                    System.out.println("SQLException in initbtnlistener::btn_defaultpriority() " + se.getMessage());
                }
            }
        });

        btn_down.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btn_updownACTION(e,"down");
            }
        });

        btn_up.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btn_updownACTION(e,"up");
            }
        });

        btn_fastforward.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btn_fastforwardACTION(e);
            }
        });

        btn_search.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                try{
                    Statement st = Main.con.createStatement();

                    ResultSet rst = st.executeQuery("select * from PRIORITY where l1id = " + (combobox_topic.getSelectedIndex() + 1) + " order by priority" );

                    if(!rst.next())
                        return;

                    rst.absolute(combobox_subtopic.getSelectedIndex() + 1);

                    btn_searchL2ACTION(rst.getInt("priority"));
                }
                catch(SQLException se){
                    System.out.println("SQLException in btn_search.addActionListener()::" + se.getMessage());
                }
            }
        });
    }


    //INITIALIZE SPINNER LISTENER
    private void initSpinnerListener(){
        changlistnr_spinnertotalcnt = new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                try{
                    Statement st = Main.con.createStatement();
                    for(int i=0;i<5;i++){
                        if(((JSpinner)e.getSource()).equals(l2object[i].spinner_totalcnt)){
                            st.executeUpdate("update PRIORITY set totalcount = " + l2object[i].spinner_totalcnt.getValue() + " where l1id = " + l2object[i].l1id + " and l2id = " + l2object[i].l2id);
                            break;
                        }
                    }
                }
                catch(SQLException se){
                    System.out.println("SQLException in initchangelistenr_spinner_totalcnt::stateChanged() - " + se.getMessage());
                }

            }
        };

        spinner_maxtopic.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                try{
                    Statement st = Main.con.createStatement();

                    st.executeUpdate("update POPUP_SETTINGS set maxtopics = " + spinner_maxtopic.getValue());
                }
                catch(SQLException se){
                    System.out.println("SQLException in spinner_maxtopic.changelistener()::" + se.getMessage());
                }
            }
        });

    }

    //INITIALIZE RADIO BUTTON LISTENER
    private void initRadioButtonListener(){

        itemlistnr_priority = new ItemListener() {

            public void itemStateChanged(ItemEvent e) {

                if(l2object[0].optionbtn_priority.isSelected()){
                    btn_up.setEnabled(false);
                    btn_down.setEnabled(true);

                }

                else if(l2object[l2count-1].optionbtn_priority.isSelected()){
                    btn_down.setEnabled(false);
                    btn_up.setEnabled(true);

                }

                else{
                    btn_up.setEnabled(true);
                    btn_down.setEnabled(true);

                }
            }
        };

    }

    /******* INITIALIZE COMBO-BOX LISTENER ***********/
    void initComboBoxListener(){
        combobox_topic.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                fillComboBox_Subtopic(combobox_topic.getSelectedIndex() +1);
            }
        });

    }

    /******* INITIALIZE STATUS OF THE PRIORITY *******/
    void initStatus(){
        if(l2count<=1){
            btn_down.setEnabled(false);
            btn_fastforward.setEnabled(false);
        }

        if(l2count==0)
            spinner_maxtopic.setEnabled(false);
        else{
            if(l2count>5)
                ((SpinnerNumberModel)spinner_maxtopic.getModel()).setMaximum(5);
            else
                ((SpinnerNumberModel)spinner_maxtopic.getModel()).setMaximum(l2count);
        }
    }

    /******* FILL SPINNER_MAXTOPIC *********/
    void fillSpinner_maxtopic(){
        try{
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select * from POPUP_SETTINGS");
            rst.next();

            spinner_maxtopic.setValue(rst.getInt("maxtopics"));
        }
        catch(SQLException se){
            System.out.println("SQLEXception in fillSpinner_Maxtopic():: " + se.getMessage());
        }
    }

    /******** FILL COMBO-BOX FAST-FORWARD *******/
    void fillComboBox_fastforward(){
        combobox_fastforward.removeAllItems();

        try{
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select count(*) as cntp from PRIORITY");
            rst.next();

            if(rst.getInt("cntp")<5){
                for(int i=1;i<=rst.getInt("cntp");i++){
                    combobox_fastforward.addItem("" + i);
                }
            }
            else{
                combobox_fastforward.addItem("1");
                combobox_fastforward.addItem("2");
                combobox_fastforward.addItem("3");
                combobox_fastforward.addItem("4");
                combobox_fastforward.addItem("5");
            }
        }
        catch(SQLException se){
            System.out.println("SQLException in fillComboBox_fastforward::() " + se.getMessage());
        }
    }

    /******** FILL COMBO-BOX TOPIC ************/
    void fillComboBox_Topic(){

        int lastselectedIndex = 0;

        if(combobox_topic.getItemCount()!=0)
            lastselectedIndex = combobox_topic.getSelectedIndex();
        else
            lastselectedIndex = l2object[0].l1id - 1;

        combobox_topic.removeAllItems();

        try{
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select topic from L1 order by l1id");

            while(rst.next()){
                combobox_topic.addItem(rst.getString("topic"));
            }

            //SET THE INITIAL TOPIC
            combobox_topic.setSelectedIndex(lastselectedIndex);

        }
        catch(SQLException se){
            System.out.println("SQLException in fillComboBox_Topic()::" + se.getMessage());
        }
    }

    /******* FILL COMBO-BOX SUBTOPIC ********/
    void fillComboBox_Subtopic(int l1id){

        combobox_subtopic.removeAllItems();
        try{
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select * from PRIORITY where l1id = " + l1id + " order by priority");

            if(!rst.next()){
                btn_search.setEnabled(false);
                return;
            }

            do{
                combobox_subtopic.addItem(MySQLDatabase.getSubTopic(rst.getInt("l1id"),rst.getInt("l2id")));
            }while(rst.next());

            btn_search.setEnabled(true);
            combobox_subtopic.setSelectedIndex(0);
        }
        catch(SQLException se){
            System.out.println("SQLException in fillComboBox_Subtopic()::" + se.getMessage());
        }
    }

    /******* INITIALIZE THE PANELS *********/
    private boolean initpanel_priority(){
        //GET SETTINGS FROM THE DATABASE

        boolean priorityempty = false;
        try{
            Statement st1 = Main.con.createStatement();
            ResultSet rst1 = st1.executeQuery("select count(*) as cnt from PRIORITY");

            rst1.next();

            l2count = rst1.getInt("cnt");

            if(l2count==0){
                JOptionPane.showMessageDialog(null,"There is no topic selected on which Popups can be generated. \nPlease select at least one topic from Grid view.");
                priorityempty = true;
            }

            else{
                priorityempty = false;
                panel_l2items = new JPanel[l2count];

                l2object = new l2objects_priority[l2count];

                addl2panels();
                initl2objects();

                for(int i =0;i<l2count;i++){
                    setl2object_onl2panel(i,i);
                }
            }

        }
        catch(SQLException se){
            System.out.println("SQLException in initpanel_priority():: " + se.getMessage());
        }
        finally{
            return priorityempty;
        }

    }


    @SuppressWarnings("static-access")
    private void addl2panels(){

        int prevx = 20,prevy = 10;

        for(int i = 0;i<l2count;i++){

            panel_l2items[i] = new JPanel();
            panel_l2items[i].setVisible(true);
            panel_l2items[i].setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
            panel_l2items[i].setLayout(new AbsoluteLayout());

            panel_l2container.add(panel_l2items[i],new AbsoluteConstraints(prevx,prevy,330,70));
            prevy = prevy + 80;

        }
    }

    private void initl2objects(){

        int l1id = 0,l2id = 0;

        try{
            Statement st1 = Main.con.createStatement();
            Statement st2 = Main.con.createStatement();
            Statement st3 = Main.con.createStatement();

            ResultSet rstPRIORITY = st2.executeQuery("select * from PRIORITY order by priority");
            ResultSet rstL2;

            for(int i=0;i<l2count;i++){

                if(rstPRIORITY.next()){
                    l1id = rstPRIORITY.getInt("l1id");
                    l2id = rstPRIORITY.getInt("l2id");
                }

                rstL2 = st1.executeQuery("select * from L2 where l1id = " + l1id + " and l2id = " + l2id);
                rstL2.next();

                l2object[i] = new l2objects_priority();
                l2object[i].l1id = l1id;
                l2object[i].l2id = l2id;
                l2object[i].label_topic.setText(MySQLDatabase.getTopic(l1id));
                l2object[i].label_subtopic.setText(MySQLDatabase.getSubTopic(l1id, l2id));
                l2object[i].label_topic.setFont(new Font(l2object[i].label_topic.getFont().getFontName(),Font.BOLD,l2object[i].label_topic.getFont().getSize()));

                l2object[i].spinner_totalcnt.setValue(rstPRIORITY.getInt("totalcount"));
                btngroup_priority.add(l2object[i].optionbtn_priority);

                l2object[i].spinner_totalcnt.addChangeListener(changlistnr_spinnertotalcnt);
                l2object[i].optionbtn_priority.addItemListener(itemlistnr_priority);

                ImageIcon imgicon = new ImageIcon(rstL2.getBytes("image"));
                Image img = imgicon.getImage();
                imgicon.setImage(img.getScaledInstance(49,49,Image.SCALE_SMOOTH));
                l2object[i].label_image.setIcon(imgicon);

            }

            l2object[0].optionbtn_priority.setSelected(true);

        }
        catch(SQLException se){
            System.out.println("SQLException in addlabel_l2panels():: " + se.getMessage());
        }

    }

    private void setl2object_onl2panel(int panelindex, int objectindex){
        panel_l2items[panelindex].add(l2object[objectindex].optionbtn_priority,new AbsoluteConstraints(10,10,-1,-1));
        panel_l2items[panelindex].add(l2object[objectindex].spinner_totalcnt,new AbsoluteConstraints(220,30,45,-1));
        panel_l2items[panelindex].add(l2object[objectindex].label_topic,new AbsoluteConstraints(30, 10, 180, -1));
        panel_l2items[panelindex].add(l2object[objectindex].label_subtopic,new AbsoluteConstraints(30,30,180,-1));
        panel_l2items[panelindex].add(l2object[objectindex].label_image,new AbsoluteConstraints(270,10,50,50));

        if(panelindex>4)  //disable spinner for totalcount
            l2object[objectindex].spinner_totalcnt.setEnabled(false);
        else{
            l2object[objectindex].spinner_totalcnt.setEnabled(true);
            ((SpinnerNumberModel)(l2object[objectindex].spinner_totalcnt.getModel())).setMinimum(1);
        }

    }

    private void btn_fastforwardACTION(ActionEvent e){
        int priority = 0,i,index;

        for(i=0;i<l2count;i++)
            if(l2object[i].optionbtn_priority.isSelected())
                break;
        try{
            Statement st =  Main.con.createStatement();

            ResultSet rst =  st.executeQuery("select priority from PRIORITY where l1id = " + l2object[i].l1id + " and l2id = " + l2object[i].l2id);
            rst.next();


            priority = rst.getInt("priority");
            index = combobox_fastforward.getSelectedIndex() + 1;
            System.out.println(index + "  " + priority);

            if(priority<index){
                for(int j=priority;j<index;j++)
                    btn_updownACTION(e,"down");
            }

            else{ //priority is > index
                for(int j = index;j<priority;j++)
                    btn_updownACTION(e,"up");
            }

            btn_searchL2ACTION(index);
        }
        catch(SQLException se){
            System.out.println("SQLException in btn_fastforwardACTION()::" + se.getMessage());
        }
    }

    private void btn_updownACTION(ActionEvent e,String upordown){

        int i;
        l2objects_priority tempobj;

        for(i=0;i<l2count;i++)
            if(l2object[i].optionbtn_priority.isSelected())
                break;
        tempobj = l2object[i];

        try{
            Statement st = Main.con.createStatement();

            if(upordown.equalsIgnoreCase("up")){

                panel_l2items[i].removeAll();
                panel_l2items[i-1].removeAll();

                l2object[i] = l2object[i-1];
                l2object[i-1] = tempobj;

                l2object[i].optionbtn_priority.setSelected(true);
                l2object[i-1].optionbtn_priority.setSelected(true);

                setl2object_onl2panel(i,i);
                setl2object_onl2panel(i-1,i-1);

                panel_l2items[i].repaint();
                panel_l2items[i-1].repaint();

                //set currentcount = 0 of the 2 things exchanged
                st.executeUpdate("update PRIORITY set currentcount = 0 where l1id = " +l2object[i].l1id + " and l2id = " + l2object[i].l2id);
                st.executeUpdate("update PRIORITY set currentcount = 0 where l1id = " +l2object[i-1].l1id + " and l2id = " + l2object[i-1].l2id);

                //change the database entries now....3phase as u can see
                st.executeUpdate("update PRIORITY set priority = 0 where l1id = " + l2object[i-1].l1id + " and l2id = " + l2object[i-1].l2id);
                st.executeUpdate("update PRIORITY set priority = " + (i+1) +" where l1id = " + l2object[i].l1id + " and l2id = " + l2object[i].l2id);
                st.executeUpdate("update PRIORITY set priority = " + (i-1 + 1) +" where l1id = " + l2object[i-1].l1id + " and l2id = " + l2object[i-1].l2id);

                //dont think of me as a stupid to see (i-1+1)...its just to understand later

            }

            else{
                panel_l2items[i].removeAll();
                panel_l2items[i+1].removeAll();

                l2object[i] = l2object[i+1];
                l2object[i+1] = tempobj;

                l2object[i].optionbtn_priority.setSelected(true);
                l2object[i+1].optionbtn_priority.setSelected(true);

                setl2object_onl2panel(i,i);
                setl2object_onl2panel(i+1,i+1);

                panel_l2items[i].repaint();
                panel_l2items[i+1].repaint();

                //set currentcount = 0 of the 2 things exchanged
                st.executeUpdate("update PRIORITY set currentcount = 0 where l1id = " +l2object[i].l1id + " and l2id = " + l2object[i].l2id);
                st.executeUpdate("update PRIORITY set currentcount = 0 where l1id = " +l2object[i+1].l1id + " and l2id = " + l2object[i+1].l2id);

                //change the database entries now....3phase as u can see
                st.executeUpdate("update PRIORITY set priority = 0 where l1id = " + l2object[i+1].l1id + " and l2id = " + l2object[i+1].l2id);
                st.executeUpdate("update PRIORITY set priority = " + (i+1) +" where l1id = " + l2object[i].l1id + " and l2id = " + l2object[i].l2id);
                st.executeUpdate("update PRIORITY set priority = " + (i + 1 + 1) +" where l1id = " + l2object[i+1].l1id + " and l2id = " + l2object[i+1].l2id);

                //dont think of me as a stupid to see (i+1+1)...its just to understand later
            }
        }
        catch(SQLException se){
            System.out.println("SQLException in btn_upordownACTION():: " + se.getMessage());
        }
    }

    public void btn_searchL2ACTION(int priorityno){
        int scrollvalue = 0;

        scrollvalue = priorityno * 80 * (vertical_scroll.getMaximum() - vertical_scroll.getModel().getExtent()) / vertical_scroll.getMaximum();
        vertical_scroll.setValue(scrollvalue);

        l2object[priorityno-1].optionbtn_priority.setSelected(true);

        try{
            Statement st = Main.con.createStatement();
            ResultSet rst = st.executeQuery("select l1id,l2id from PRIORITY where priority = " + priorityno);
            rst.next();

            int l1id = rst.getInt("l1id");
            int l2id = rst.getInt("l2id");

            combobox_topic.setSelectedIndex(l1id - 1);
            rst = st.executeQuery("select subtopic from L2 where l1id = " + l1id + " and l2id = " + l2id);
            rst.next();
            combobox_subtopic.setSelectedItem(rst.getString("subtopic"));
        }
        catch(SQLException se){
            System.out.println("SQLException in btn_searchACTION()::" + se.getMessage());
        }
    }

    //REFRESH ALL PANELS if the TOPIC NAME IS CHANGED IN GRID VIEW
    void refreshPanels_Topic(int refresh_l1id){

        System.out.println(refresh_l1id);
        for(int i=0;i<l2count;i++){

            if(l2object[i].l1id == refresh_l1id)
                l2object[i].label_topic.setText(MySQLDatabase.getTopic(refresh_l1id));

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

        btngroup_priority = new javax.swing.ButtonGroup();
        btn_defaultpriority = new javax.swing.JButton();
        btn_down = new javax.swing.JButton();
        btn_up = new javax.swing.JButton();
        combobox_topic = new javax.swing.JComboBox();
        combobox_subtopic = new javax.swing.JComboBox();
        scrollpane_priority = new javax.swing.JScrollPane();
        panel_l2container = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btn_search = new javax.swing.JButton();
        spinner_maxtopic = new javax.swing.JSpinner(new SpinnerNumberModel(1,1,5,1));
        label_maxtopic = new javax.swing.JLabel();
        combobox_fastforward = new javax.swing.JComboBox();
        btn_fastforward = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Priority ");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_defaultpriority.setText("Default Count");
        getContentPane().add(btn_defaultpriority, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 520, -1, -1));

        btn_down.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/arrow_down.png"))); // NOI18N
        getContentPane().add(btn_down, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 520, 30, -1));

        btn_up.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/arrow_up.png"))); // NOI18N
        getContentPane().add(btn_up, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 520, 30, -1));

        combobox_topic.setModel(new javax.swing.DefaultComboBoxModel(new String[] {  }));
        getContentPane().add(combobox_topic, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 140, -1));

        combobox_subtopic.setModel(new javax.swing.DefaultComboBoxModel(new String[] {  }));
        getContentPane().add(combobox_subtopic, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, 150, -1));

        panel_l2container.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        scrollpane_priority.setViewportView(panel_l2container);

        getContentPane().add(scrollpane_priority, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 400, 420));

        jLabel1.setText("Topic:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel2.setText("Subtopic:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, -1, -1));

        btn_search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/find.png"))); // NOI18N
        btn_search.setText("Search");
        getContentPane().add(btn_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 30, 90, -1));
        getContentPane().add(spinner_maxtopic, new org.netbeans.lib.awtextra.AbsoluteConstraints(159, 60, 40, -1));

        label_maxtopic.setText("Maximum No. of Active Topics:");
        getContentPane().add(label_maxtopic, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 150, 20));

        combobox_fastforward.setModel(new javax.swing.DefaultComboBoxModel(new String[] {  }));
        getContentPane().add(combobox_fastforward, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 520, 160, -1));

        btn_fastforward.setText(">>");
        getContentPane().add(btn_fastforward, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 520, 50, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PriorityView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_defaultpriority;
    private javax.swing.JButton btn_down;
    private javax.swing.JButton btn_fastforward;
    private javax.swing.JButton btn_search;
    private javax.swing.JButton btn_up;
    private javax.swing.ButtonGroup btngroup_priority;
    private javax.swing.JComboBox combobox_fastforward;
    private javax.swing.JComboBox combobox_subtopic;
    private javax.swing.JComboBox combobox_topic;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel label_maxtopic;
    private javax.swing.JPanel panel_l2container;
    private javax.swing.JScrollPane scrollpane_priority;
    private javax.swing.JSpinner spinner_maxtopic;
    // End of variables declaration//GEN-END:variables

}
