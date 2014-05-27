/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maggi_1;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author tomer
 */


public class Main{

    /**
     * @param args the command line arguments
     */

    //THESE TWO VARIABLES ARE NEEDED ALL OVER THE SOFTWARE

    static String curdir = System.getProperty("user.dir");
    static boolean startupdone = false;
    public static FileLock lock_Main,lock_GridView;
    public static Connection con;

    public static SplashView splashview = null;
    public static GridView gridview = null;
    public static TimelineView timelineview = null;
    public static PriorityView priorityview = null;
    public static SettingsView settingsview = null;
    public static AboutDialog about_us = null;
    
    public static Toolkit toolkit = Toolkit.getDefaultToolkit();
    
    public static Thread mainthread;
    public static int l1id = -1,l2id = -1;
    public static int new_l1id,new_l2id;
    public static int count_pending = 0;
    static MaggiTray hotmaggitray;
    static PopUpController popupcontrol;

    public static Dimension screensize = toolkit.getScreenSize();
    
    Main(){

        //set System's default LOOK & FEEL

        
        UISettingsSetter.setOSUI();
       
        if(!getLockOnFiles()){
            JOptionPane.showMessageDialog(null,"Only one instance of this program can be run at one time. \nOne instance of Maggi Learning is already running.");
            return;
        }

        splashview =  new SplashView();
        splashview.setVisible(true);

        /******** START THE DATABASE ***********/
        con = MySQLDatabase.startDatabase();
        
        /******* INITIALIZE POPUP CONTROLLER **********/
        popupcontrol = new PopUpController();

        /***** INITIALIZE THE SYSTEM TRAY ***********/
        hotmaggitray = new MaggiTray();

        /******* INITIALIZE NETWORK SETTINGS **********/
        NetworkSettings.initNetworkSettings();

        /******* START THE MAIN THREAD ********/
        startMainThread();
        
        /******** START THE TIMERS TO GENERATE POP-UPS *********/
        PopupTimers.updateTimers();

        /***** UPDATE RSS DATA *****/
        XMLParser.updateRSSFeeds();

        
        startupdone = true;

    }

    /****** STARTING MAIN THREAD ********/
    private void startMainThread(){
        
        mainthread = new Thread(new Runnable() {

            @SuppressWarnings("static-access")
            public void run() {
                try{
                    while(mainthread.isAlive()){
                        
                    //JUST TO RUN THE THREAD INFINITELY
                        if(l1id!=-1 && l2id !=-1){                                

                            //l1id = 4;
                            //l2id = 1;
                            new_l1id = l1id;
                            new_l2id = l2id;
                            generatePopup();

                            l1id = -1;
                            l2id = -1;

                            Main.mainthread.sleep(2000);
                        }
                    }
                }
                catch(NullPointerException ne){
                    //ne.printStackTrace();
                    JOptionPane.showMessageDialog(null,"Thank you for using Maggi Learning!");
                    System.exit(0);
                }
                catch(InterruptedException ie){
                    
                }
            }
        });

        mainthread.start();
    }
    
    //TO GET LOCK ON some FILES & later check the multiple instances of the same program
    private static boolean getLockOnFiles(){

        //GET LOCK ON GridView.java
        lock_GridView = FileLocker.getLock("./GridView.java");
        if(lock_GridView==null)
            return false;

        //GET LOCK ON Main.java
        lock_Main = FileLocker.getLock("./Main.java");
        if(lock_Main==null)
            return false;

        return true;
       
    }

    /******* TO CHECK THE SETTINGS & GENERATE THE POPUPS *********/
    @SuppressWarnings("static-access")
    void generatePopup(){
        try{
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select directgeneration from POPUP_SETTINGS");
            rst.next();

            MaggiTray.updateTrayIcon();

            if(popupcontrol.popupview.isVisible()){
                MaggiTray.setCurrentIconMode(MaggiTray.ICONNAME.NEWPOPUPICON);
                popupcontrol.popupview.storePopup(new_l1id,new_l2id);
            }
            else{
                if(rst.getInt("directgeneration")==0  && MaggiTray.traySupported){
                    MaggiTray.setCurrentIconMode(MaggiTray.ICONNAME.NEWPOPUPICON);
                    popupcontrol.setnewIDS(new_l1id,new_l2id,false);
                }
                else{
                    popupcontrol.setnewIDS(new_l1id,new_l2id,true);
                }
            }
        }
        catch(SQLException se){
            JOptionPane.showMessageDialog(null,"SQL Exception ---- Main::generatePopup() ------ "  + se.getMessage());
        }
    }
    
    /**************   main() METHOD of the SOFTWARE **************/
    public static void main(String[] args) {
        new Main();
    }
}

//SYSTEM TRAY CLASS
class MaggiTray{

    static TrayIcon maggitrayicon;
    static SystemTray maggitray;
    static boolean traySupported;
    
    static enum ICONNAME{
        NORMALICON,NEWPOPUPICON,VIEWOLDPOPUPICON
    }

    static ICONNAME currenticonmode = ICONNAME.NORMALICON;
    final static String str_imgpath_normalmode = Main.curdir + "/src/maggi_1/icons/normal_mode.gif";
    final static String str_imgpath_newpopup = Main.curdir + "/src/maggi_1/icons/new_popup.gif";
    final static String str_imgpath_viewoldpop = Main.curdir + "/src/maggi_1/icons/view_oldpop.gif";

    PopupMenu maggiMenu;
    MenuItem mnuitem_about,mnuitem_priority,mnuitem_gridview,mnuitem_timeline,mnuitem_popupview,mnuitem_exit,mnuitem_generatenewtip,mnuitem_updaterss;
    MenuItem mnuitem_generalsettings,mnuitem_popupsettings,mnuitem_networksettings;
    CheckboxMenuItem mnuitem_autogeneration,mnuitem_popupdisabled;
    Menu mnu_settings,mnu_rssfeeds;

    public MaggiTray() {

        traySupported = checkSysTraySupport();

        if(traySupported){
            initmaggiMenu();
            initMenuItemListeners();
            initmaggiTRAY();
            updateTrayIcon();            
        }

        else
            return;
    }

    //TO CHECK IF SYSTEM TRAY IS SUPPORTED
    private boolean checkSysTraySupport(){

        if(SystemTray.isSupported()){
            return true;
        }

        else{
            JOptionPane.showMessageDialog(null,"YOUR SYSTEM DOES NOT SUPPORT THE SYSTEM TRAY\n\nPOP UPS WILL BE GENERATED WITHOUT PRIOR NOTIFICATION");

            try{
                Statement st = Main.con.createStatement();
                st.executeUpdate("update POPUP_SETTINGS set directgeneration = 1");
            }
            catch(SQLException se){
                JOptionPane.showMessageDialog(null,"SQL Exception ---- maggiTRAY::maggiTRAY() ------ "  + se.getMessage());
            }
            finally{
                return false;
            }
        }
    }

    //CHANGE THE IMAGE ICON ON THE TRAY
    public static void setCurrentIconMode(ICONNAME mode){

        currenticonmode = mode;
        switch(mode){
            
            case NEWPOPUPICON     :  maggitrayicon.setImage(Main.toolkit.getImage(str_imgpath_newpopup));
                                     break;
            case NORMALICON       :  maggitrayicon.setImage(Main.toolkit.getImage(str_imgpath_normalmode));
                                     break;
            case VIEWOLDPOPUPICON :  maggitrayicon.setImage(Main.toolkit.getImage(str_imgpath_viewoldpop));
                                     break;
        }
                
    }


    /********* UPDATE TRAY ICONS ***************/
    public static void updateTrayIcon(){
        Main.count_pending = MySQLDatabase.getPendingCount();

        if(Main.count_pending>0){
            setCurrentIconMode(ICONNAME.VIEWOLDPOPUPICON);
        }

        else if(Main.count_pending==0){
            setCurrentIconMode(ICONNAME.NORMALICON);
        }
    }

    //INITIALIZE THE MENU TO BE SHOWN ON CLICK OF SYSTEM TRAY
    void initmaggiMenu(){

        //INITIALIZE CHECKBOX MENU ITEM
        try{
            Statement st = Main.con.createStatement();

            //INITIALIZE AUTOGENERATION
            ResultSet rst = st.executeQuery("select directgeneration from POPUP_SETTINGS");
            rst.next();
            
            if(rst.getInt("directgeneration") == 0)
                mnuitem_autogeneration = new CheckboxMenuItem("Enable Autogeneration",false);
            else
                mnuitem_autogeneration = new CheckboxMenuItem("Enable Autogeneration",true);

            //INITIALIZE POPUPDISABLED
            rst = st.executeQuery("select popupdisable from GENERAL_SETTINGS");
            rst.next();
            if(rst.getInt("popupdisable")==1)
                mnuitem_popupdisabled = new CheckboxMenuItem("Disable Popups",true);
            else
                mnuitem_popupdisabled = new CheckboxMenuItem("Disable Popups",false);
        }
        catch(SQLException se){
            JOptionPane.showMessageDialog(null,"SQL Exception ---- maggiTRAY::initmaggiMenu() ------ "  + se.getMessage());
        }

        mnuitem_about = new MenuItem("About");
        mnuitem_exit = new MenuItem("Exit");
        mnuitem_gridview = new MenuItem("Add New Topic / View Previous Topics");
        mnuitem_popupview = new MenuItem("View Current Popup / Pending Popups");
        mnuitem_generatenewtip = new MenuItem("Generate a Tip");                
        mnuitem_priority = new MenuItem("Set Priority");
        mnuitem_timeline = new MenuItem("ReView Previously Generated Popups On TIMELINE");

        mnuitem_generalsettings = new MenuItem("General Settings");
        mnuitem_networksettings = new MenuItem("Network Settings");
        mnuitem_popupsettings = new MenuItem("PopUp Settings");

        mnu_settings = new Menu("Settings");
        mnu_settings.add(mnuitem_generalsettings);
        mnu_settings.add(mnuitem_popupsettings);
        mnu_settings.add(mnuitem_networksettings);

        mnuitem_updaterss =  new MenuItem("Update RSS");
        mnu_rssfeeds =  new Menu("RSS Feeds");
        
        //ADD THESE MENU ITEMS TO maggiMenu
        maggiMenu = new PopupMenu();
        
        maggiMenu.add(mnuitem_about);
        maggiMenu.addSeparator();

        maggiMenu.add(mnuitem_autogeneration);
        maggiMenu.add(mnuitem_popupdisabled);
        maggiMenu.addSeparator();

        maggiMenu.add(mnuitem_gridview);
        maggiMenu.add(mnuitem_timeline);
        maggiMenu.add(mnuitem_priority);
        maggiMenu.addSeparator();

        maggiMenu.add(mnu_settings);
        maggiMenu.addSeparator();

        maggiMenu.add(mnuitem_popupview);
        maggiMenu.add(mnuitem_generatenewtip);
        maggiMenu.addSeparator();

        maggiMenu.add(mnu_rssfeeds);
        maggiMenu.add(mnuitem_updaterss);
        maggiMenu.addSeparator();

        maggiMenu.add(mnuitem_exit);

    }

    //ADD LISTENERS TO MENU ITEMS
    void initMenuItemListeners(){
        
        //ABOUT
        mnuitem_about.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(Main.about_us==null){
                    Main.about_us = new AboutDialog();
                    Main.about_us.setVisible(true);                    
                }
                else{
                    if(Main.about_us.getExtendedState()==JFrame.ICONIFIED)
                        Main.about_us.setExtendedState(JFrame.NORMAL);
                }
            }
        });

        //ENABLE DIRECT GENERATION
        mnuitem_autogeneration.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                try{
                    Statement st = Main.con.createStatement();

                    if(mnuitem_autogeneration.getState()){
                        st.executeUpdate("update POPUP_SETTINGS set directgeneration = 1");
                    }
                    else{
                        st.executeUpdate("update POPUP_SETTINGS set directgeneration = 0");
                    }

                    if(Main.settingsview!=null){
                        Main.settingsview.update_CheckBox_enableautogenartionSTATE();
                    }
                }
                catch(SQLException se){
                    JOptionPane.showMessageDialog(null,"SQL Exception ---- maggiTRAY::initMenuItemListeners():mnuitem_autogeneration::itemStateChanged() ------ "  + se.getMessage());
                }
            }
        });


        //DISABLE POPUPS
        mnuitem_popupdisabled.addItemListener(new ItemListener() {

            @SuppressWarnings("static-access")
            public void itemStateChanged(ItemEvent e) {
                try{
                    Statement st = Main.con.createStatement();

                    if(mnuitem_popupdisabled.getState()){
                        st.executeUpdate("update GENERAL_SETTINGS set popupdisable = 1");
                    }
                    else{
                        st.executeUpdate("update GENERAL_SETTINGS set popupdisable = 0");
                    }

                    if(Main.settingsview!=null){
                        Main.settingsview.update_CheckBox_popupdisabledSTATE();
                    }
                }
                catch(SQLException se){
                    JOptionPane.showMessageDialog(null,"SQL Exception ---- maggiTRAY::initMenuItemListeners():mnuitem_autogeneration::itemStateChanged() ------ "  + se.getMessage());
                }
                
            }
        });


        //GRID VIEW
        mnuitem_gridview.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                if(Main.gridview!=null){
                    if(Main.gridview.getExtendedState() == JFrame.ICONIFIED)
                        Main.gridview.setExtendedState(JFrame.NORMAL);
                }

                else{
                    Main.gridview = new GridView();
                    Main.gridview.setVisible(true);                    
                }
            }
        });

        //TIMELINE
        mnuitem_timeline.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(Main.timelineview!=null){
                    if(Main.timelineview.getExtendedState() == JFrame.ICONIFIED)
                        Main.timelineview.setExtendedState(JFrame.NORMAL);
                }
                else{
                    Main.timelineview = new TimelineView();
                    Main.timelineview.setVisible(true);
                }
            }
        });

        //PRIORITY
        mnuitem_priority.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(Main.priorityview!=null){
                    if(Main.priorityview.getExtendedState() == JFrame.ICONIFIED)
                        Main.priorityview.setExtendedState(JFrame.NORMAL);

                }
                else{
                    Main.priorityview = new PriorityView();
                    Main.priorityview.setVisible(true);                    
                }
            }
        });
        
        //GENERAL SETTINGS
        mnuitem_generalsettings.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(Main.settingsview!=null){
                    if(Main.settingsview.getExtendedState() == JFrame.ICONIFIED)
                        Main.settingsview.setExtendedState(JFrame.NORMAL);
                        
                    Main.settingsview.set_tabbedpane_settingsIndex(0);
                }
                else{
                    Main.settingsview = new SettingsView();
                    Main.settingsview.setVisible(true);
                    
                    Main.settingsview.set_tabbedpane_settingsIndex(0);
                }
            }
        });

        //POPUP SETTINGS
        mnuitem_popupsettings.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(Main.settingsview!=null){
                    if(Main.settingsview.getExtendedState() == JFrame.ICONIFIED)
                        Main.settingsview.setExtendedState(JFrame.NORMAL);

                    Main.settingsview.set_tabbedpane_settingsIndex(2);
                }
                else{
                    Main.settingsview = new SettingsView();
                    Main.settingsview.setVisible(true);

                    Main.settingsview.set_tabbedpane_settingsIndex(2);
                }
            }
        });

        //NETWORK SETTINGS
        mnuitem_networksettings.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(Main.settingsview!=null){
                    if(Main.settingsview.getExtendedState() == JFrame.ICONIFIED)
                        Main.settingsview.setExtendedState(JFrame.NORMAL);

                    Main.settingsview.set_tabbedpane_settingsIndex(1);
                }
                else{
                    Main.settingsview = new SettingsView();
                    Main.settingsview.setVisible(true);

                    Main.settingsview.set_tabbedpane_settingsIndex(1);
                }
            }
        });
        

        //POPUP VIEW
        mnuitem_popupview.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                leftClickOnTRAYICON_ACTION();
            }
        });

        //GENERATE A NEW TIP
        mnuitem_generatenewtip.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(mnuitem_popupdisabled.getState())
                    JOptionPane.showMessageDialog(null,"You have disabled the popups. Please, enable popups by unchecking \nthe 'Disable Popups' option by right clicking on tray icon.");
                else
                    PopupTimers.generateAndShowPOPUP();
            }
        });

        //UPDATE RSS
        mnuitem_updaterss.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                XMLParser.updateRSSFeeds();
            }
        });

        //EXIT
        mnuitem_exit.addActionListener(new ActionListener() {

            @SuppressWarnings("static-access")
            public void actionPerformed(ActionEvent e) {
                
                //check if search is going on

                if(Main.gridview!=null){
                    if(!Main.gridview.btnCloseACTION())
                        return;
                }

                //stop all timers
                PopupTimers.stopPreviousTimers();

                if(Main.popupcontrol.popupview.isVisible()){
                    Main.popupcontrol.popupview.btncloseACTION();
                }
                
                if(Main.timelineview!=null)
                    Main.timelineview.dispose();
                if(Main.settingsview!=null)
                    Main.settingsview.dispose();
                if(Main.about_us!=null)
                    Main.about_us.dispose();


                Main.mainthread = null;
            }
        });

    }

    /******** INIT LISTENER FOR RSS FEEDS MENU *******/
    public void initListenerRSS(){

        
        for(int i=0;i<mnu_rssfeeds.getItemCount();i++){
            mnu_rssfeeds.getItem(i).addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    try{
                        Statement st = Main.con.createStatement();
                        Statement st2 = Main.con.createStatement();


                        ResultSet rst2 = st2.executeQuery("select defaultbrowser from GENERAL_SETTINGS");
                        rst2.next();
                        ResultSet rst = st.executeQuery("select link from RSS where itemname = '" + ((MenuItem)(e.getSource())).getLabel() + "'");

                        if(rst.next()){
                            String[] cmdline = {rst2.getString("defaultbrowser"),rst.getString("link")};

                            Process prs = Runtime.getRuntime().exec(cmdline);
                        }
                    }
                    catch(SQLException se){
                        System.out.println("SQLException in actionlistener of rss feeds::" + se.getMessage());
                    }
                    catch(IOException ie){
                        System.out.println("IO EXCEPTION in actionlistener of rss feeds::" + ie.getMessage());
                    }
                }
            });
        }
    }


    //INITIALIZE MAGGI TRAY
    void initmaggiTRAY(){
        maggitray = SystemTray.getSystemTray();

        maggitrayicon = new TrayIcon(Main.toolkit.getImage(str_imgpath_normalmode));
        
        maggitrayicon.setToolTip("Right Click for Menu or Left click to see for any pending popups.");
        maggitrayicon.setPopupMenu(maggiMenu);
        
        maggitrayicon.setImageAutoSize(true);

        

        //ADD THE ICON TO THE TRAY
        try{
            maggitray.add(maggitrayicon);
        }
        catch(AWTException ex){
            JOptionPane.showMessageDialog(null,"AWTException ---- maggiTRAY::initmaggiTRAY() ------ "  + ex.getMessage());
        }

        // ADD ACTION LISTENER TO MAGGI TRAY
        maggitrayicon.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if(e.getButton()==1)
                    leftClickOnTRAYICON_ACTION();              
            }
        });

    }

    /******* ON LEFT CLICK DISPLAY PENDING POP-UPS *********/
    @SuppressWarnings("static-access")
    void leftClickOnTRAYICON_ACTION(){

        switch(currenticonmode){
            case NEWPOPUPICON      :if(Main.popupcontrol.popupview.isVisible()==true){
                                        Main.popupcontrol.popupview.btncloseACTION();
                                        Main.popupcontrol.setnewIDS(-1,-1,true);
                                    }
                                    else{
                                        Main.popupcontrol.showPopup();
                                    }
            
                                    break;
            case NORMALICON        :JOptionPane.showMessageDialog(null,"There are no pending popups available.");
                                    return;
            case VIEWOLDPOPUPICON  :if(Main.popupcontrol.popupview.isVisible()==false)
                                        Main.popupcontrol.setnewIDS(-1,-1,true);
                                    break;
        }

    }

}