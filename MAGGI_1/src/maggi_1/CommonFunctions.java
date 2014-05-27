/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maggi_1;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;


/**
 *
 * @author tomer
 */
class MySQLDatabase{


    //START DATABASE
    public static Connection startDatabase(){

        Connection con = null;
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(null,"E$xception ---- common_function_1::startDATABASE() ------ "  + ex.getMessage());
        }

        try{
             con = DriverManager.getConnection("jdbc:mysql://localhost/TEAM R1", "root","root");
        }

        catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"SQL Exception ---- common_function_1::startDATABASE() ------ "  + ex.getMessage());
        }

        finally{
            return con;
        }
    }

    //GET TOPIC
    public static String getTopic(int l1id){

        String topic = new String("");

        try{
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select topic from L1 where l1id = " + l1id);
            rst.next();

            topic = new String(rst.getString("topic"));
        }
        catch(SQLException ex){
            ex.printStackTrace();
            System.out.println("SQL Exception ---- common_function_1::getTopic() ------ "  + ex.getMessage());
        }
        finally{
            return topic;
        }
    }

    //GET SUBTOPIC
    public static String getSubTopic(int l1id,int l2id){

        String subtopic = new String("");

        try{
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select subtopic from L2 where l1id = " + l1id + " and l2id = " + l2id);
            rst.next();

            subtopic = new String(rst.getString("subtopic"));
        }
        catch(SQLException ex){
            ex.printStackTrace();
            System.out.println("SQL Exception ---- common_function_1::getSubTopic() ------ "  + ex.getMessage());
        }
        finally{
            return subtopic;
        }
    }

    //GET VIDEO URL
    public static String getVideoURL(int l1id,int l2id){
        String videourl = new String("");

        try{
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select videourl from L2 where l1id = " + l1id + " and l2id = " + l2id);
            rst.next();

            videourl = new String(rst.getString("videourl"));
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"SQL Exception ---- common_function_1::getVideoURL() ------ "  + ex.getMessage());
        }
        finally{
            return videourl;
        }
    }

    public static int getPendingCount(){
        int pendingcount = 0;

        try{
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select count(*) as pendingcount from POPUP where rdstatus = 0");
            rst.next();

            pendingcount = rst.getInt("pendingcount");
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"SQL Exception ---- common_function_1::getPendingCount() ------ "  + ex.getMessage());
        }
        finally{
            return pendingcount;
        }
    }

    public static void setReadStatus(Timestamp datetime){
        try{
            Statement st = Main.con.createStatement();
            st.executeUpdate("update POPUP set rdstatus = 1 where datetime = '" + datetime+"'");
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"SQL Exception ---- common_function_1::set_rdstatus() ------ "  + ex.getMessage());
        }
    }

    /*CHECK IF ALL THE LINKS OF A PARTICULAR l1,l2 combo ID are disabled...if so remove it from the
    priority table & check if after that all links of L1 are disabled */

    public static boolean checkAllLinksDisabled(int l1id,int l2id){
        int totalcnt = 0,disabledcnt = 0;
        boolean retr = false;

        try{
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select count(*) as totalcnt from LINK where l1id = " + l1id + " and l2id = " + l2id);
            rst.next();

            totalcnt = rst.getInt("totalcnt");

            rst = st.executeQuery("select count(*) as disabledcnt from LINK where l1id = " + l1id + " and l2id = " + l2id + " and linkdisabled = 1");
            rst.next();

            disabledcnt = rst.getInt("disabledcnt");

            if(totalcnt==disabledcnt){
                st.executeUpdate("update L2 set l2disabled = 1 where l1id = " + l1id  + " and l2id = " + l2id );
                retr = removeFromPriority(l1id,l2id);
            }
            
        }
        catch(SQLException ex){
            ex.printStackTrace();
            System.out.println("SQL Exception ---- common_function_1::check_allLINKSdisabled() ------ "  + ex.getMessage());
        }
        finally{
            return retr;
        }
    }

    public static boolean removeFromPriority(int l1id,int l2id){
        
        int currentpriority = 0,cntp = 0;
        boolean retr = false;

        try{
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select count(*) as cntp from PRIORITY");
            rst.next();

            cntp = rst.getInt("cntp");
            rst = st.executeQuery("select priority from PRIORITY where l1id = " + l1id + " and l2id = " + l2id);

            if(!rst.next())
                retr = true;

            else if(cntp!=0){
                currentpriority = rst.getInt("priority");

                //UPDATE PRIORITY TABLE

                st.executeUpdate("delete from PRIORITY where l1id = " + l1id + " and l2id = " + l2id);

                if(cntp ==1)
                    JOptionPane.showMessageDialog(null,"It is found that there are no popups enabled. \nHence, no further popups can be generated. To start generating popups,\nenable at least one topic from Grid view.");

                else
                    st.executeUpdate("update PRIORITY set priority = priority-1 where priority > " + currentpriority);

                if(Main.priorityview!=null){
                    Main.priorityview.dispose();
                    Main.priorityview = null;
                    Main.priorityview = new PriorityView();
                    Main.priorityview.setVisible(true);
                }
                retr = true;
            }
            
        }
        catch(SQLException ex){
            ex.printStackTrace();
            System.out.println("SQL Exception ---- common_function_1::removeFromPriority() ------ "  + ex.getMessage());
        }
        finally{
            return retr;
        }
    }

}

class FileLocker{

    public static FileLock getLock(String pathoffile){
        
        FileChannel chnl = null;
        FileLock lock_gridview = null;
        try {
             chnl = (new RandomAccessFile(pathoffile, "rw")).getChannel();
             lock_gridview = chnl.tryLock();
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(null,"Exception ---- common_function_1::fileLOCK() ------ "  + ex.getMessage());
        }
        finally{
            return lock_gridview;
        }
    }
}

class UISettingsSetter{

    public static void setOSUI(){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(null,"Exception ---- common_function_1::setOSUI() ------ "  + ex.getMessage());
        }
    }
  
}

class LinkListener{

    String strtemp_labelText = "";
    MouseAdapter hyperlinkListener;

    Container contentpane;

    public LinkListener(Container contentpane) {

        
        this.contentpane = contentpane;

        hyperlinkListener = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e){
               mouseCLICKACTION();
            }

            @Override
            public void mouseEntered(MouseEvent e){
                mouseENTERACTION(e);
            }

            @Override
            public void mouseExited(MouseEvent e){
                mouseEXITACTION(e);
            }
        };
    }

    public void mouseCLICKACTION(){

        try{
            Statement st = Main.con.createStatement();
            ResultSet rst = st.executeQuery("select defaultbrowser from GENERAL_SETTINGS");
            rst.next();

            String[] cmdline = {rst.getString("defaultbrowser"),strtemp_labelText};

            Process prs = Runtime.getRuntime().exec(cmdline);
        }
        catch(SQLException se){
            System.out.println("SQLException in mouseCLICKACTION LINKLISTNER()" + se.getMessage());
        }
        catch(IOException ie){
             System.out.println("IO EXCEPTION in LinkListener::mouseCLICKACTION() " + ie.getMessage());
        }      
    }

    public void mouseENTERACTION(MouseEvent e){
        contentpane.setCursor(new Cursor(Cursor.HAND_CURSOR));
        strtemp_labelText = new String(((JLabel)e.getSource()).getText());
        ((JLabel)e.getSource()).setText("<html><u>" + strtemp_labelText + "</u></html>");
    }

    public void mouseEXITACTION(MouseEvent e){
        contentpane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        ((JLabel)e.getSource()).setText(strtemp_labelText);
    }

}

class NetworkSettings{

    static Timer timer_testnet = null;

    public static void initNetworkSettings(){

        setTimeout();

        try{            
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select * from NETWORK_SETTINGS");
            rst.next();

            if(rst.getInt("proxyenabled")==1){
                System.out.println("killa");
                setProxy("true",rst.getString("proxyaddress"),"" + rst.getInt("portaddress"),"4");
            }
            else{
                setProxy("false","null","null","null");
            }               
        }
        catch(SQLException se){
            System.out.println("SQL Exception in initNetworkSettings() :: " + se.getMessage());
        }
    }

    public static void setProxy(String proxySet,String proxyHost,String proxyPort,String portType){

        System.setProperty("http.proxySet",proxySet);
        System.setProperty("http.proxyHost",proxyHost);
        System.setProperty("http.proxyPort",proxyPort);
        System.setProperty("http.proxyType",portType);  //I donno wat this "4" represents

                /******* IF USER NAME & PASSWORD TOO IS REQUIRED *********/
                // System.setProperty("http.proxyUser", "myuser");
                // System.setProperty("http.proxyPassword", "mypassword");
    }

    public static void setTimeout(){
        System.setProperty("sun.net.client.defaultReadTimeout", "5000");
        System.setProperty("sun.net.client.defaultConnectTimeout", "5000");
    }

    public static boolean checkNetworkConnection(){
       boolean netPresent = false;

       setTimeout();
       
       timer_testnet = new Timer(4500,new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                timer_testnet.stop();
            }
       });

       timer_testnet.start();

       try{
           URL testurl = new URL("http://www.bing.com");

           testurl.openConnection().getInputStream();
           if(timer_testnet.isRunning())
               netPresent = true;
       }
       catch (MalformedURLException ex) {
           System.out.println("MalformedURLException in checkNetworkConnection()::" + ex.getMessage());
       }
       catch(IOException ie){           
           System.out.println("IOEXCEPTION");
           netPresent = false;
       }
       finally{
           System.out.println(netPresent);
           return netPresent;
       }        
   }
}

class XMLParser{

    static String folderpath_RSS = Main.curdir + "/CACHE/RSS/";
    static boolean stopsearch = false;
    static URL neturl;

    public static void updateRSSFeeds(){

        try{
            Statement st = Main.con.createStatement();

            ResultSet rst1 = st.executeQuery("select * from GENERAL_SETTINGS");

            rst1.next();
            neturl =  new URL(rst1.getString("rsssite"));

            if(rst1.getInt("autostartrss")==0 && Main.startupdone==false){
                updateTrayRSSMenuFromDatabase();
                return;
            }

            if(NetworkSettings.checkNetworkConnection()){
                downloadRSSPage();

                if(stopsearch==false){
                    parseXMLPass1();
                    parseXMLPass2();
                    updateTrayRSSMenuFromDatabase();
                }
            }
            else{
                updateTrayRSSMenuFromDatabase();
                JOptionPane.showMessageDialog(null,"RSS Feeds Can't Be Updated From Net\nThere Was Some Problem With The Internet Connection\nUpdating It From The Database Now");
            }
        }
        catch(MalformedURLException me){
            System.out.println("MalformedURLException in updateRssFeeds::" + me.getMessage());
        }
        catch(SQLException se){
            System.out.println("SQLException in updateRSSFeeds()::" + se.getMessage());
        }
        
    }

    public static void parseXMLPass1(){

        int temparr[] =  new int[7];
        int itemarr[] = {60,47,108,105,110,107,62};

        try{
            FileInputStream fin = new FileInputStream(folderpath_RSS + "page.xml");
            FileOutputStream fout = new FileOutputStream(folderpath_RSS + "itemtag.xml");

            int c;
            boolean flag = false;

            while((c=fin.read())!=-1){
                for(int i=0;i<temparr.length-1;i++){
                    temparr[i] = temparr[i+1];
                }

                temparr[temparr.length-1] = c;

                if(flag){

                    if(c!=91 && c!=93 && c!=10)
                        fout.write(c);
                }

                if(temparr[1] == 60 && temparr[2] == 105  && temparr[3] == 116 && temparr[4]==101 && temparr[5]==109 && temparr[6]==62){   //to check "<item>"
                    flag = true;
                }

                else if(Arrays.equals(temparr,itemarr)){
                    flag = false;
                    fout.write(10);
                }

            }

            fin.close();
            fout.close();
        }
        catch(IOException ie){
            System.out.println("IOException in parseXMLPass1()" + ie.getMessage());
        }
    }

    public static void parseXMLPass2(){

        String str_itemname = "",str_link="";

        int temparr[] = new int[7];
        int title[] = {60,116,105,116,108,101,62}; //<title>
        int link[] = {60,108,105,110,107,62}; //<link>

        try{
            Statement st = Main.con.createStatement();
            st.executeUpdate("delete from RSS");

            FileInputStream fin = new FileInputStream(folderpath_RSS + "itemtag.xml");
            
            int c;
            boolean flag = false;
            
            while((c=fin.read())!=-1){
                for(int i=0;i<temparr.length-1;i++){
                    temparr[i] = temparr[i+1];
                }

                temparr[temparr.length-1] = c;

                if(c==10){

                    flag=false;
                    int j;
                    for(j=0;j<temparr.length-1;j++)
                        if(temparr[j]==10){
                            flag = true;
                            break;
                        }

                    if(!flag){

                        str_itemname = str_itemname.replaceAll("!CDATA","");
                        str_itemname = str_itemname.replaceAll("'","");
                     
                        str_link = str_link.replaceAll("amp;","");
                        str_link = str_link.replaceAll("'","");

                        if(!str_itemname.equalsIgnoreCase("") && !str_link.equalsIgnoreCase(""))
                            st.executeUpdate("insert into RSS values('" +str_itemname + "','" + str_link + "')");
                    }
                }

                if(Arrays.equals(temparr,title)){
                    str_itemname = "";

                    int prevchar = c,curchar;
                    while(true){
                        curchar = fin.read();

                        if(curchar==47 && prevchar ==60)
                            break;

                        if(curchar!=60 && curchar!=62)
                            str_itemname += (char)curchar;
                        prevchar = curchar;
                    }                    
                }

                else if(temparr[0]==60 && temparr[1]==108 && temparr[2]==105 && temparr[3]==110 && temparr[4]==107 && temparr[5]==62){  //60,108,105,110,107,62
                    str_link = "";

                    str_link += (char)c;

                    int prevchar = c,curchar;
                    while(true){
                        curchar = fin.read();

                        if(curchar==47 && prevchar ==60)
                            break;

                        if(curchar!=60 && curchar!=62)
                            str_link += (char)curchar;
                        prevchar = curchar;
                    }
                }
                
            }

            fin.close();            
        }
        catch(IOException ie){
            System.out.println("IOException in parseXMLPass1()" + ie.getMessage());
        }
        catch(SQLException se){
            
            System.out.println("SQLException in parseXMLPass2()" + se.getMessage());
        }
    }


    public static void updateTrayRSSMenuFromDatabase(){
                
        try{
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select * from RSS");

            Main.hotmaggitray.mnu_rssfeeds.removeAll();
            
            while(rst.next()){
                Main.hotmaggitray.mnu_rssfeeds.add(new MenuItem(rst.getString("itemname")));
            }
        }
        catch(SQLException se){
            System.out.println("SQLException on updateTrayMenu()::" + se.getMessage());
        }finally{

            /******* ADD LISTENER FOR THE RSS MENU ITEMS *****/
            Main.hotmaggitray.initListenerRSS();

        }
    }

    public static void downloadRSSPage(){
        boolean prematureEOF;
        int cnt = 0;

        do{
            if(stopsearch == true)
                return;

            prematureEOF = false;
            try{
                FileOutputStream fout = new FileOutputStream(folderpath_RSS + "page.xml");
                InputStream ipstream = neturl.openConnection().getInputStream();

                int c;

                while((c= ipstream.read())!=-1){
                    fout.write(c);
                }
                fout.close();
            }
            catch(IOException ie){
                prematureEOF = true;
                System.out.println("IOException in storePageFromNet()::" + ie.getMessage());
            }
            finally{
                cnt++;
            }

            if(cnt>5 && prematureEOF==true){
                JOptionPane.showMessageDialog(null,"Unable to connect to Internet\nPlease Check Your Internet Connection");

                stopsearch =true;                    
                
            }
        }while(prematureEOF);     
    }
}