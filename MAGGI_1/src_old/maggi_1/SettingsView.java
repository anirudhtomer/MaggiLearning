/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SettingsView.java
 *
 * Created on Dec 23, 2009, 2:59:40 PM
 */

/**
 *
 * @author tomer
 */
package maggi_1;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SettingsView extends JFrame {
    
    LinkListener settingslinklistener;
    JFileChooser fileChooser;

    /** Creates new form SettingsView */
    public SettingsView() {
     
        initComponents();

        initWindowListener();
        initCheckBoxListener();
        initButtonListener();
        initSpinnerListener();
        initRadioButtonListener();
        initComboBoxListener();
        initColorBoxListener();

        settingslinklistener =  new LinkListener(getContentPane());

        initpanel_general();
        initpanel_popup();
        initpanel_network();


    }

    //INTIALIZE WINDOW LISTENER
    public void initWindowListener(){
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e){
                dispose();
                Main.settingsview = null;
            }
        });
    }
       
    //INITIALIZE CHECK BOX LISTENER
    public void initCheckBoxListener(){

        checkbox_startrsswithmaggi.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                chkbox_updatersswithmaggiACTION(e);
            }
        });

        checkbox_popupdisable.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                chkbox_popupdisableACTION(e);
            }
        });

        checkbox_autogeneration.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                chkbox_directgenerationACTION(e);
            }
        });
    }

    //INITIALIZE BUTTON LISTENER
    public void initButtonListener(){
        btn_resetTimers.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btnResetTimerACTION(e);
            }
        });

        btn_setProxy.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btnSetProxyACTION(e);
            }
        });

        btn_close.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
                Main.settingsview = null;
            }
        });


        btn_browse.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                fileChooser = new JFileChooser();
                fileChooser.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        try{

                            Statement st = Main.con.createStatement();

                            if(fileChooser.getSelectedFile()==null)
                                return;

                            st.executeUpdate("update GENERAL_SETTINGS set defaultbrowser = '" + fileChooser.getSelectedFile().toString().replace((char)(92),'/') + "'");

                            label_defaultbrowser.setText(fileChooser.getSelectedFile().toString().replace((char)(92),'/'));
                            
                        }
                        catch(SQLException se){
                            System.out.println("SQL Exception in updatetableIMAGE():: " + se.getMessage());
                        }
                    }
                });

                fileChooser.showOpenDialog(Main.settingsview);
            }
        });

        togglebtn_rss.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    togglebtn_rss.setText("Ok");
                    label_rss.setVisible(false);
                    txtfield_rss.setVisible(true);
                    txtfield_rss.setText(label_rss.getText());
                }

                else{
                    togglebtn_rss.setText("Change");
                    label_rss.setVisible(true);
                    txtfield_rss.setVisible(false);
                    label_rss.setText(txtfield_rss.getText());

                    try{
                        Statement st = Main.con.createStatement();
                        st.executeUpdate("update GENERAL_SETTINGS set rsssite = '" + label_rss.getText() + "'");
                    }
                    catch(SQLException se){
                        System.out.println("SQLException in ItemListener_btnrss()::" + se.getMessage());
                    }

                    XMLParser.updateRSSFeeds();
                }
            }
        });
    }

    //INITIALIZE SPINNER LISTENERS
    public void initSpinnerListener(){

        spinner_txtlen.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                spinnertxtlenACTION(e);
            }

        });

        spinner_portadrs.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                spinnerportadrsACTION(e);
            }

        });
    }

    //INITIALIZE RADIO BUTTON LISTENER
    public void initRadioButtonListener(){
        radiobtn_manualproxy.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {

                if(radiobtn_manualproxy.isSelected()){
                    txtfield_proxyadrs.setEnabled(true);
                    label_proxyadrs.setEnabled(true);
                    label_portadrs.setEnabled(true);
                    spinner_portadrs.setEnabled(true);
                }

            }

        });

        radiobtn_noProxy.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if(radiobtn_noProxy.isSelected()){
                    txtfield_proxyadrs.setEnabled(false);
                    label_proxyadrs.setEnabled(false);
                    label_portadrs.setEnabled(false);
                    spinner_portadrs.setEnabled(false);
                }
            }

        });

    }

    //INITIALIZE COMBO BOX LISTENER
    public void initComboBoxListener(){
        combobox_font.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                combobox_fontACTION(e);
            }
        });
    }

    //INITIALIZE COLORBOX LISTENER
    public void initColorBoxListener(){
        colorchooser.getSelectionModel().addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                colorchooserACTION(e);
            }
        });
    }

    //************* INTERFACE CODES **************//
    public void set_tabbedpane_settingsIndex(int index){
        tabbedpane_settings.setSelectedIndex(index);
    }

    public void update_CheckBox_popupdisabledSTATE(){
       if(checkbox_popupdisable.isSelected()!=Main.hotmaggitray.mnuitem_popupdisabled.getState())
           checkbox_popupdisable.setSelected(Main.hotmaggitray.mnuitem_popupdisabled.getState());
    }

    public void update_CheckBox_enableautogenartionSTATE(){
        if(checkbox_autogeneration.isSelected()!=Main.hotmaggitray.mnuitem_autogeneration.getState())
           checkbox_autogeneration.setSelected(Main.hotmaggitray.mnuitem_autogeneration.getState());
    }

    /*********** INITIALIZE PANELS *************/
    private void initpanel_general(){

        //GET CURRENT SETTINGS FROM THE DATABASE
        try{
            Statement st = Main.con.createStatement();
            ResultSet rst = st.executeQuery("select * from GENERAL_SETTINGS");
            rst.next();

            if(rst.getInt("autostartrss")==1){                    
                checkbox_startrsswithmaggi.setSelected(true);
            }

            if(rst.getInt("popupdisable")==1){
                checkbox_popupdisable.setSelected(true);
            }

            label_rss.setText(rst.getString("rsssite"));            
            label_rss.addMouseListener(settingslinklistener.hyperlinkListener);
            txtfield_rss.setVisible(false);

            label_defaultbrowser.setText(rst.getString("defaultbrowser"));
            
            st.close();
            
        }
        catch(SQLException se){
            System.out.println("SQL EXCEPTION IN initpanel_general()  " + se.getMessage());
        }
    }

    private void initpanel_popup(){
        //GET CURRENT SETTINGS FROM THE DATABASE
        try{

            String strcolor="";
            Statement st = Main.con.createStatement();
            ResultSet rst = st.executeQuery("select * from POPUP_SETTINGS");
            rst.next();

            if(rst.getInt("directgeneration")==1){
                checkbox_autogeneration.setSelected(true);
            }

            strcolor = rst.getString("colour");
            
            spinner_delay.setValue(rst.getInt("delaymin"));
            spinner_txtlen.setValue(rst.getInt("textlength"));
            spinner_starttime.setValue(rst.getTime("starttime"));
            spinner_endtime.setValue(rst.getTime("endtime"));
            combobox_font.setSelectedItem(rst.getString("font"));

            colorchooser.setColor(Integer.parseInt(strcolor.substring(0,3)),Integer.parseInt(strcolor.substring(3,6)),Integer.parseInt(strcolor.substring(6,9)));
            txtfield_font.setForeground(colorchooser.getColor());
            txtfield_font.setFont(new Font(rst.getString("font"),Font.PLAIN,txtfield_font.getFont().getSize()));
            st.close();
        }
        catch(SQLException se){
            System.out.println("SQL EXCEPTION IN initpanel_popup()  " + se.getMessage());
        }
    }


    private void initpanel_network(){
        //GET CURRENT SETTINGS FROM THE DATABASE
        try{
            Statement st = Main.con.createStatement();
            ResultSet rst = st.executeQuery("select * from NETWORK_SETTINGS");
            rst.next();

            spinner_portadrs.setValue(rst.getInt("portaddress"));
            txtfield_proxyadrs.setText(rst.getString("proxyaddress"));
            
            if(rst.getInt("proxyenabled")==0){
                radiobtn_noProxy.setSelected(true);
                NetworkSettings.setProxy("false","null","null","null");
            }

            else{
                radiobtn_manualproxy.setSelected(true);
                NetworkSettings.setProxy("true",txtfield_proxyadrs.getText(),spinner_portadrs.getValue().toString(),"4");
            }
            
            st.close();

        }
        catch(SQLException se){
            System.out.println("SQL EXCEPTION IN initpanel_network()  " + se.getMessage());
        }
    }



    /*********** ACTIONS TO BE PERFORMED FOR CHECKBOXES **********/

    private void chkbox_updatersswithmaggiACTION(ItemEvent e){
        
        try{
            Statement st = Main.con.createStatement();

            if(checkbox_startrsswithmaggi.isSelected())
                st.executeUpdate("update GENERAL_SETTINGS set autostartrss = 1");
                
            else
                st.executeUpdate("update GENERAL_SETTINGS set autostartrss = 0");
  
            st.close();
        }
        catch(SQLException se){
            System.out.println("SQL Exception in chkbox_startwithosACTION  " + se.getMessage());
        }

    }

    private void chkbox_popupdisableACTION(ItemEvent e){
        try{
            Statement st = Main.con.createStatement();

            if(checkbox_popupdisable.isSelected())
                st.executeUpdate("update GENERAL_SETTINGS set popupdisable = 1");
               
            else
                st.executeUpdate("update GENERAL_SETTINGS set popupdisable = 0");

            if(Main.hotmaggitray.mnuitem_popupdisabled.getState()!=checkbox_popupdisable.isSelected())
                Main.hotmaggitray.mnuitem_popupdisabled.setState(checkbox_popupdisable.isSelected());
            
            st.close();
        }
        catch(SQLException se){
            System.out.println("SQL Exception in chkbox_popupdisableACTION  " + se.getMessage());
        }        
    }

    private void chkbox_directgenerationACTION(ItemEvent e){
        try{
            Statement st = Main.con.createStatement();

            if(checkbox_autogeneration.isSelected())
                st.executeUpdate("update POPUP_SETTINGS set directgeneration = 1");

            else
                st.executeUpdate("update POPUP_SETTINGS set directgeneration = 0");

            if(Main.hotmaggitray.mnuitem_autogeneration.getState()!=checkbox_autogeneration.isSelected())
                Main.hotmaggitray.mnuitem_autogeneration.setState(checkbox_autogeneration.isSelected());

            st.close();
        }
        catch(SQLException se){
            System.out.println("SQL Exception in chkbox_directgenerationACTION  " + se.getMessage());
        }
        
    }

    /******* ACTION TO BE PERFORMED FOR BUTTONS *********/
    private void btnResetTimerACTION(ActionEvent e){
        try{
            Statement st = Main.con.createStatement();

            st.executeUpdate("update POPUP_SETTINGS set delaymin = " + spinner_delay.getValue());
            st.executeUpdate("update POPUP_SETTINGS set starttime = '" + spinner_starttime.getValue().toString().substring(11,20) +"'");
            st.executeUpdate("update POPUP_SETTINGS set endtime = '" + spinner_endtime.getValue().toString().substring(11,20) +"'");
            st.close();
        }
        catch(SQLException se){
            System.out.println("SQL Exception in btnResetTimerACTION()::" + se.getMessage());
        }
        finally{
            PopupTimers.updateTimers();
        }
    }

    private void btnSetProxyACTION(ActionEvent e){
        try{
            Statement st = Main.con.createStatement();

            if(radiobtn_noProxy.isSelected()){
                
                st.executeUpdate("update NETWORK_SETTINGS set proxyenabled = 0");
                NetworkSettings.setProxy("false","null","null","null");
            }
            else if(radiobtn_manualproxy.isSelected()){
                
                st.executeUpdate("update NETWORK_SETTINGS set proxyenabled = 1");
                st.executeUpdate("update NETWORK_SETTINGS set portaddress = " + spinner_portadrs.getValue());
                st.executeUpdate("update NETWORK_SETTINGS set proxyaddress = '" + txtfield_proxyadrs.getText() + "'");

                NetworkSettings.setProxy("true",txtfield_proxyadrs.getText(),spinner_portadrs.getValue().toString(),"4");
            }
        }
        catch(SQLException se){
            System.out.println("SQL EXception in btnSetProxy()::" + se.getMessage());
        }
    }

    /*********   ACTIONS TO BE PERFORMED FOR SPINNERS *************/
    
    private void spinnertxtlenACTION(ChangeEvent e){
        try{
            Statement st = Main.con.createStatement();

            st.executeUpdate("update POPUP_SETTINGS set textlength = " + spinner_txtlen.getValue());
            st.close();
        }
        catch(SQLException se){
            System.out.println("SQL Exception in spinnertxtlenACTION  " + se.getMessage());
        }
    }

    private void spinnerportadrsACTION(ChangeEvent e){

        try{
            Statement st = Main.con.createStatement();

            st.executeUpdate("update NETWORK_SETTINGS set portaddress = '" + spinner_portadrs.getValue() + "'" );
            st.close();
        }
        catch(SQLException se){
            System.out.println("SQL Exception in spinnerportadrsACTION  " + se.getMessage());
        }
    }

    
    /********** ACTION FOR COMBOBOXES ***********/

    private void combobox_fontACTION(ItemEvent e){

        txtfield_font.setFont(new Font((String) e.getItem(),Font.PLAIN,txtfield_font.getFont().getSize()));
        try{
            Statement st = Main.con.createStatement();

            st.executeUpdate("update POPUP_SETTINGS set font = '" + ((String)e.getItem()) + "'");
            st.close();
        }
        catch(SQLException se){
            System.out.println("SQL Exception in combobox_fontACTION  " + se.getMessage());
        }
    }


    /********** ACTIONS FOR COLORCHOOSER *************/

    private void colorchooserACTION(ChangeEvent e){
        txtfield_font.setForeground(colorchooser.getColor());
        try{
            
            Statement st = Main.con.createStatement();
            Color c1 = colorchooser.getColor();
            String strcolor[] = {"" + c1.getRed(),"" + c1.getGreen(),"" + c1.getBlue()};
            String savecolor="";

            for(String x:strcolor){

                switch(x.length()){
                    case 1: savecolor += "00" + x;
                            break;
                    case 2: savecolor += "0" +x;
                            break;
                    case 3: savecolor += x;
                            break;
                }
            }

            st.executeUpdate("update POPUP_SETTINGS set colour = '" + savecolor + "'");
            st.close();
        }
        catch(SQLException se){
            System.out.println("SQL Exception in colorchooserACTION  " + se.getMessage());
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        tabbedpane_settings = new javax.swing.JTabbedPane();
        panel_general = new javax.swing.JPanel();
        checkbox_popupdisable = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        label_rss = new javax.swing.JLabel();
        txtfield_rss = new javax.swing.JTextField();
        togglebtn_rss = new javax.swing.JToggleButton();
        checkbox_startrsswithmaggi = new javax.swing.JCheckBox();
        label_defaultbrowserlogo = new javax.swing.JLabel();
        label_defaultbrowser = new javax.swing.JLabel();
        btn_browse = new javax.swing.JButton();
        panel_network = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        label_proxyadrs = new javax.swing.JLabel();
        txtfield_proxyadrs = new javax.swing.JTextField();
        radiobtn_manualproxy = new javax.swing.JRadioButton();
        radiobtn_noProxy = new javax.swing.JRadioButton();
        spinner_portadrs = new javax.swing.JSpinner(new SpinnerNumberModel(8080,0,65535,1));
        label_portadrs = new javax.swing.JLabel();
        btn_setProxy = new javax.swing.JButton();
        panel_popup = new javax.swing.JPanel();
        colorchooser = new javax.swing.JColorChooser();
        txtfield_font = new javax.swing.JTextField();
        checkbox_autogeneration = new javax.swing.JCheckBox();
        spinner_txtlen = new javax.swing.JSpinner(new SpinnerNumberModel(2,2,6,1));
        label_txtlength = new javax.swing.JLabel();
        GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String envfonts[] = genv.getAvailableFontFamilyNames();
        combobox_font = new javax.swing.JComboBox();
        label_font = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        label_starttime = new javax.swing.JLabel();
        label_endtime = new javax.swing.JLabel();
        spinner_starttime = new javax.swing.JSpinner(new SpinnerDateModel(new java.util.Date(),null,null,Calendar.MONTH));
        spinner_endtime = new javax.swing.JSpinner(new SpinnerDateModel(new java.util.Date(),null,null,Calendar.MONTH));
        label_delay = new javax.swing.JLabel();
        spinner_delay = new javax.swing.JSpinner(new SpinnerNumberModel(1,1,null,1));
        btn_resetTimers = new javax.swing.JButton();
        btn_close = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Settings");
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panel_general.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        checkbox_popupdisable.setText("Disable the Popups.");
        panel_general.add(checkbox_popupdisable, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 490, 30));

        jLabel1.setText("GET RSS INFORMATION FROM");
        panel_general.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 190, -1));

        label_rss.setForeground(new java.awt.Color(39, 51, 206));
        panel_general.add(label_rss, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 90, 270, 20));
        panel_general.add(txtfield_rss, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 90, 270, -1));

        togglebtn_rss.setText("Change");
        panel_general.add(togglebtn_rss, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 90, 70, -1));

        checkbox_startrsswithmaggi.setText("Update RSS On Maggi Startup");
        panel_general.add(checkbox_startrsswithmaggi, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

        label_defaultbrowserlogo.setText("Default Browser");
        panel_general.add(label_defaultbrowserlogo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 110, -1));

        label_defaultbrowser.setFont(new java.awt.Font("DejaVu Sans", 1, 13));
        label_defaultbrowser.setText("jLabel2");
        panel_general.add(label_defaultbrowser, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 160, 330, -1));

        btn_browse.setText("Browse");
        panel_general.add(btn_browse, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 150, -1, -1));

        tabbedpane_settings.addTab("General", panel_general);

        panel_network.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "HTTP Proxy Settings"));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        label_proxyadrs.setText("HTTP Proxy:");
        jPanel2.add(label_proxyadrs, new org.netbeans.lib.awtextra.AbsoluteConstraints(41, 85, -1, -1));
        jPanel2.add(txtfield_proxyadrs, new org.netbeans.lib.awtextra.AbsoluteConstraints(128, 79, 124, -1));

        radiobtn_manualproxy.setText("Proxy Configuration");
        buttonGroup1.add(radiobtn_manualproxy);
        jPanel2.add(radiobtn_manualproxy, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 47, -1, -1));

        radiobtn_noProxy.setText("No Proxy");
        buttonGroup1.add(radiobtn_noProxy);
        jPanel2.add(radiobtn_noProxy, new org.netbeans.lib.awtextra.AbsoluteConstraints(18, 21, -1, -1));
        jPanel2.add(spinner_portadrs, new org.netbeans.lib.awtextra.AbsoluteConstraints(306, 79, 63, -1));

        label_portadrs.setText("Port:");
        jPanel2.add(label_portadrs, new org.netbeans.lib.awtextra.AbsoluteConstraints(264, 85, -1, -1));

        btn_setProxy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/accept.png"))); // NOI18N
        btn_setProxy.setText("Ok");
        btn_setProxy.setToolTipText("Set currently selected proxy values.");
        jPanel2.add(btn_setProxy, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 80, 70, 30));

        panel_network.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 12, 450, 120));

        tabbedpane_settings.addTab("Network", panel_network);

        panel_popup.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        colorchooser.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel_popup.add(colorchooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 260, -1, 300));

        txtfield_font.setEditable(false);
        txtfield_font.setText("Maggi Fast Learning");
        panel_popup.add(txtfield_font, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 220, 150, -1));

        checkbox_autogeneration.setText("Enable \"AutoGeneration Mode\"");
        panel_popup.add(checkbox_autogeneration, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, -1, -1));
        panel_popup.add(spinner_txtlen, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 10, 40, 30));

        label_txtlength.setText("Length of text contents in Pop-up:");
        panel_popup.add(label_txtlength, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        combobox_font.setModel(new javax.swing.DefaultComboBoxModel(envfonts));
        panel_popup.add(combobox_font, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 220, 152, -1));

        label_font.setText("Font:");
        panel_popup.add(label_font, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, 40, 30));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        label_starttime.setText("Starting time of Pop-up generation:");
        jPanel1.add(label_starttime, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        label_endtime.setText("Ending time of Pop-up generation:");
        jPanel1.add(label_endtime, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, -1, -1));

        JSpinner.DateEditor de_start = new JSpinner.DateEditor(spinner_starttime,"HH:mm");
        spinner_starttime.setEditor(de_start);
        jPanel1.add(spinner_starttime, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 50, 65, -1));

        JSpinner.DateEditor de_end = new JSpinner.DateEditor(spinner_endtime,"HH:mm");
        spinner_endtime.setEditor(de_end);
        jPanel1.add(spinner_endtime, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 90, 67, 30));

        label_delay.setText("Time Delay b/w 2 Popups(min)");
        jPanel1.add(label_delay, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));
        jPanel1.add(spinner_delay, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, 40, -1));

        btn_resetTimers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/accept.png"))); // NOI18N
        btn_resetTimers.setText("Set Time");
        jPanel1.add(btn_resetTimers, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 50, -1, -1));

        panel_popup.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 460, 130));

        tabbedpane_settings.addTab("Pop-up", panel_popup);

        getContentPane().add(tabbedpane_settings, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 5, 580, 620));

        btn_close.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/cancel.png"))); // NOI18N
        btn_close.setText("Close");
        getContentPane().add(btn_close, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 630, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                new SettingsView().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_browse;
    private javax.swing.JButton btn_close;
    private javax.swing.JButton btn_resetTimers;
    private javax.swing.JButton btn_setProxy;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox checkbox_autogeneration;
    private javax.swing.JCheckBox checkbox_popupdisable;
    private javax.swing.JCheckBox checkbox_startrsswithmaggi;
    private javax.swing.JColorChooser colorchooser;
    private javax.swing.JComboBox combobox_font;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel label_defaultbrowser;
    private javax.swing.JLabel label_defaultbrowserlogo;
    private javax.swing.JLabel label_delay;
    private javax.swing.JLabel label_endtime;
    private javax.swing.JLabel label_font;
    private javax.swing.JLabel label_portadrs;
    private javax.swing.JLabel label_proxyadrs;
    private javax.swing.JLabel label_rss;
    private javax.swing.JLabel label_starttime;
    private javax.swing.JLabel label_txtlength;
    private javax.swing.JPanel panel_general;
    private javax.swing.JPanel panel_network;
    private javax.swing.JPanel panel_popup;
    private javax.swing.JRadioButton radiobtn_manualproxy;
    private javax.swing.JRadioButton radiobtn_noProxy;
    private javax.swing.JSpinner spinner_delay;
    private javax.swing.JSpinner spinner_endtime;
    private javax.swing.JSpinner spinner_portadrs;
    private javax.swing.JSpinner spinner_starttime;
    private javax.swing.JSpinner spinner_txtlen;
    private javax.swing.JTabbedPane tabbedpane_settings;
    private javax.swing.JToggleButton togglebtn_rss;
    private javax.swing.JTextField txtfield_font;
    private javax.swing.JTextField txtfield_proxyadrs;
    private javax.swing.JTextField txtfield_rss;
    // End of variables declaration//GEN-END:variables

}
