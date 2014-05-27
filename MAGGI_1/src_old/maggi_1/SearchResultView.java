/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SearchResultView.java
 *
 * Created on Dec 25, 2009, 8:59:47 AM
 */

package maggi_1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author tomer
 */

public class SearchResultView extends javax.swing.JFrame {

    LinkListener linklistener;
    ItemListener itemchanged_checkbox,itemchanged_usercheckbox;
    String textfiles[] = {"","","","","","",""};   // "" are replaced by original http links name later
    String folderpath_search = Main.curdir + "/CACHE/SEARCHIMAGES/";

    static int imagedownloadproblemcount = 0;
    int atleast_1checkbox_checked;

    boolean searchforVIDEO,searchforWIKI,searchforOTHER,searchforIMAGE;
    boolean stopsearch = false;
    boolean offline = false;

    Reader rd;
    HTMLEditorKit.Parser parser;
    HTMLEditorKit.ParserCallback parserlistener;

    /** Creates new form SearchResultView */
    public SearchResultView(String srch,boolean offline) {
        
        initComponents();

        this.offline = offline;

        NetworkSettings.setTimeout();

        initWindowListener();
        initButtonListener();
        initToggleButtonListener();
        inititemlistener();
        initmouselistener();

        addmouselistenertolabel();
        additemlistenertocheckbox();

        fillComboBox_Topic();
        txtfield_L2name.setText(srch);

        checkbox_userurl1.setEnabled(false);
        checkbox_userurl2.setEnabled(false);
        checkbox_userurl3.setEnabled(false);

        label_userurl1.setVisible(false);
        label_userurl2.setVisible(false);
        label_userurl3.setVisible(false);

        if(offline){
            checkbox_otherurl1.setEnabled(false);
            checkbox_otherurl2.setEnabled(false);
            checkbox_wikiurl1.setEnabled(false);
            checkbox_wikiurl2.setEnabled(false);

            label_otherurl1.setEnabled(false);
            label_otherurl2.setEnabled(false);
            label_wikiurl1.setEnabled(false);
            label_wikiurl2.setEnabled(false);

            label_videoURL.setText("");
        }

        atleast_1checkbox_checked = 0;
    }

   /******* INITIALIZE WINDOW LISTENER **********/
    void initWindowListener(){
       addWindowListener(new WindowAdapter() {

            @Override
           public void windowClosing(WindowEvent e){
               btncancelACTION();
           }
       });
    }

   /****** INITIALIZE BUTTON LISTENER *********/
    void initButtonListener(){

       btn_cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btncancelACTION();
            }

        });

        btn_ok.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                btnokACTION(e);
            }

        });
    }

    /****** INITIALIZE TOGGLE BUTTON LISTENER **********/

    void initToggleButtonListener(){
        togglebtn_userurl1.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    togglebtn_userurl1.setText("Disselect");
                    checkbox_userurl1.setEnabled(true);
                    checkbox_userurl1.setSelected(true);
                    label_userurl1.setVisible(true);
                    label_userurl1.setText(txtbox_userurl1.getText());
                    txtbox_userurl1.setVisible(false);
                }
                else{
                    togglebtn_userurl1.setText("Select");
                    checkbox_userurl1.setEnabled(false);
                    checkbox_userurl1.setSelected(false);
                    label_userurl1.setVisible(false);
                    txtbox_userurl1.setVisible(true);
                }
            }
        });

        togglebtn_userurl2.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    togglebtn_userurl2.setText("Disselect");
                    checkbox_userurl2.setEnabled(true);
                    checkbox_userurl2.setSelected(true);
                    label_userurl2.setVisible(true);
                    label_userurl2.setText(txtbox_userurl2.getText());
                    txtbox_userurl2.setVisible(false);
                }
                else{
                    togglebtn_userurl2.setText("Select");
                    checkbox_userurl2.setEnabled(false);
                    checkbox_userurl2.setSelected(false);
                    label_userurl2.setVisible(false);
                    txtbox_userurl2.setVisible(true);
                }
            }
        });

        togglebtn_userurl3.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED){
                    togglebtn_userurl3.setText("Disselect");
                    checkbox_userurl3.setEnabled(true);
                    checkbox_userurl3.setSelected(true);
                    label_userurl3.setVisible(true);
                    label_userurl3.setText(txtbox_userurl3.getText());
                    txtbox_userurl3.setVisible(false);
                }
                else{
                    togglebtn_userurl3.setText("Select");
                    checkbox_userurl3.setEnabled(false);
                    checkbox_userurl3.setSelected(false);
                    label_userurl3.setVisible(false);
                    txtbox_userurl3.setVisible(true);
                }
            }
        });
    }
   /******** INITIALIZE ITEM LISTENER **************/
    public void inititemlistener(){
        itemchanged_checkbox = new ItemListener() {

            public void itemStateChanged(ItemEvent e) {

                if(((JCheckBox)e.getSource()).isSelected())
                    atleast_1checkbox_checked += 1;
                else
                    atleast_1checkbox_checked -=1;
            }
        };

        itemchanged_usercheckbox = new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if(((JCheckBox)e.getSource()).isSelected())
                    atleast_1checkbox_checked += 1;
                else
                    atleast_1checkbox_checked -=1;
            }
        };
    }

    /******** INITIALIZE MOUSE LISTENER *************/
    public void initmouselistener(){
        linklistener = new LinkListener(getContentPane());
    }

    /******** ADD MOUSE LISTENER TO LABEL ************/
    public void addmouselistenertolabel(){

        label_videoURL.addMouseListener(linklistener.hyperlinkListener);

        label_wikiurl1.addMouseListener(linklistener.hyperlinkListener);
        label_wikiurl2.addMouseListener(linklistener.hyperlinkListener);

        label_otherurl1.addMouseListener(linklistener.hyperlinkListener);
        label_otherurl2.addMouseListener(linklistener.hyperlinkListener);

        label_userurl1.addMouseListener(linklistener.hyperlinkListener);
        label_userurl2.addMouseListener(linklistener.hyperlinkListener);
        label_userurl3.addMouseListener(linklistener.hyperlinkListener);
    }

    /**** ADD ITEM LISTENER TO CHECKBOX ************/
    public void additemlistenertocheckbox(){
        checkbox_wikiurl1.addItemListener(itemchanged_checkbox);
        checkbox_wikiurl2.addItemListener(itemchanged_checkbox);

        checkbox_otherurl1.addItemListener(itemchanged_checkbox);
        checkbox_otherurl2.addItemListener(itemchanged_checkbox);

        checkbox_userurl1.addItemListener(itemchanged_usercheckbox);
        checkbox_userurl2.addItemListener(itemchanged_usercheckbox);
        checkbox_userurl3.addItemListener(itemchanged_usercheckbox);
    }


    public void fillComboBox_Topic(){

        int last_selectedItemIndex = 0;
        //read database & fill the combo-box with L1 items
        if(combobox_topic.getItemCount()!=0)
            last_selectedItemIndex = combobox_topic.getSelectedIndex();

        combobox_topic.removeAllItems();
        try{

            Statement st = Main.con.createStatement();
            ResultSet rst = st.executeQuery("select * from L1");

            while(rst.next()){
                combobox_topic.addItem(rst.getString("topic"));
            }

            combobox_topic.setSelectedIndex(last_selectedItemIndex);

            st.close();
        }
        catch(SQLException se){
            System.out.println("SQL Exception in addL1entriesToComboBox():: " + se.getMessage());
        }

    }

    /******** ACTION LISTENER FOR BUTTONS ************/
    @SuppressWarnings("static-access")
    public void btncancelACTION(){
        delete_txtfiles();
        deletetemp_files();
        deletetmp_images();
        dispose();
        Main.gridview.searchview = null;
    }

    public void btnokACTION(ActionEvent e){

        int l1id = 0,l2id;

        if(txtfield_L2name.getText().equalsIgnoreCase("")){
            JOptionPane.showMessageDialog(this.getContentPane(),"Subtopic cannot be empty. Please enter appropriate Subtopic.");
            txtfield_L2name.requestFocus();
            return;
        }

        if(atleast_1checkbox_checked == 0){
            JOptionPane.showMessageDialog(this.getContentPane(),"Select at least one URL should be selected in order to retrive information for generation of Popups.");
            return;
        }

        try{
            Statement st = Main.con.createStatement();

            ResultSet rst1 = st.executeQuery("select l1id from L1 where topic = '" + combobox_topic.getSelectedItem().toString() + "'");

            if(rst1.next()){
                l1id = rst1.getInt("l1id");
            }

            ResultSet rst2 = st.executeQuery("select count(l2id) as cntl2id from L2 where l1id = " + l1id);

            if(rst2.next())
                l2id = rst2.getInt("cntl2id") + 1;
            else
                l2id = 1;

            st.close();

            if(download_Parse_Pages()){
                updatetableLINK(l1id,l2id);
                updatetableL2(l1id,l2id);
                int priority = updatetablePRIORITY(l1id,l2id);
                updatetableIMAGE(l1id,l2id);
                updatetableTEXT(l1id,l2id);
                
                if(Main.priorityview!=null){
                    Main.priorityview.dispose();
                    Main.priorityview = null;
                }

                Main.priorityview = new PriorityView();
                Main.priorityview.setVisible(true);
                Main.priorityview.btn_searchL2ACTION(priority);

                btncancelACTION();
            }
            else{
                JOptionPane.showMessageDialog(null,"It is detected that no useful data is available on the URL you have selected. Please try selecting another URLs.");
                for(int i =0;i<textfiles.length;i++)
                    textfiles[i]= "";
            }
        }
        catch(SQLException se){
            System.out.println("SQL EXCEPTION in btnokACTION:" + se.getMessage());
        }
    }


    /************ DOWNLOAD & PARSE ALL PAGES ***********/
    public boolean download_Parse_Pages(){
        int cnt = 0;

        if(checkbox_wikiurl1.isSelected())
            cnt += searchpage(label_wikiurl1.getText(),0);
        if(checkbox_wikiurl2.isSelected())
            cnt += searchpage(label_wikiurl2.getText(),1);
        if(checkbox_otherurl1.isSelected())
            cnt += searchpage(label_otherurl1.getText(),2);
        if(checkbox_otherurl2.isSelected())
            cnt += searchpage(label_otherurl2.getText(),3);
        if(checkbox_userurl1.isSelected())
            cnt += searchpage(label_userurl1.getText(),4);
        if(checkbox_userurl2.isSelected())
            cnt += searchpage(label_userurl2.getText(),5);
        if(checkbox_userurl3.isSelected())
            cnt += searchpage(label_userurl3.getText(),6);

        System.out.println(cnt);

        if(cnt==0)
            return false;
        else
            return true;
    }

    public void updatetableLINK(int l1id,int l2id){

        try{

            Statement st = Main.con.createStatement();

            for(int i=0;i<textfiles.length;i++){
                if(!textfiles[i].equalsIgnoreCase("")){
                    st.executeUpdate("insert into LINK values (" + l1id + "," + l2id + ",'" + textfiles[i].replace((char)(92),'/') + "',0)");
                }
            }

            st.close();

        }
        catch(SQLException se){
            System.out.println("SQL Exception in updatetableLINK():: " + se.getMessage());
        }

    }

    public void updatetableL2(int l1id,int l2id){

        try{

            String querystr = "insert into L2 values(" + l1id + "," + l2id +  ",'" + txtfield_L2name.getText().replaceAll("'","") + "','";

            if(checkbox_videoURL.isSelected())
                querystr += label_videoURL.getText();

            querystr += "',?,0)";

            PreparedStatement ps = Main.con.prepareStatement(querystr);
            File f;
            int i;
            for(i=0;i<15;i++){
                f = new File(folderpath_search + i + ".jpg");
                if(f.exists())
                    break;
            }

            if(i<15){
                FileInputStream fin = new FileInputStream(folderpath_search + i + ".jpg");
                ps.setBinaryStream(1,fin);
                ps.executeUpdate();
            }
            else
                Main.con.createStatement().executeUpdate(querystr.replace("?","0"));

            ps.close();

        }
        catch(SQLException se){
            System.out.println("SQL Exception in updatetableL2():: " + se.getMessage());
        }
        catch(FileNotFoundException fe){
            System.out.println("FILENOTFOUND Exception in updatetableL2():: " + fe.getMessage());
        }

    }

    public int updatetablePRIORITY(int l1id,int l2id){

        int priority = 0;
        try{
            Statement st = Main.con.createStatement();
            ResultSet rst = st.executeQuery("select count(priority) as cntp from PRIORITY");

            rst.next();

            priority = rst.getInt("cntp") + 1;
            st.executeUpdate("insert into PRIORITY values (" + l1id + "," + l2id + "," + priority + ",0,1)");

            st.close();

        }
        catch(SQLException se){
            System.out.println("SQL Exception in updatetablePRIORITY():: " + se.getMessage());
        }
        finally{
            return priority;
        }
    }

    public void updatetableIMAGE(int l1id,int l2id){
        try{
            FileInputStream fin;
            File f;
            String querystr = "insert into IMAGE values (" + l1id + "," + l2id + ",?)";
            PreparedStatement ps = Main.con.prepareStatement(querystr);

            for(int i=0;i<15;i++){
                f = new File(folderpath_search + i +".jpg");

                if(f.exists()){
                    fin = new FileInputStream(folderpath_search + i +".jpg");
                    ps.setBinaryStream(1,fin);
                    ps.executeUpdate();
                }
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

    public void updatetableTEXT(int l1id,int l2id){

        try{
            FileInputStream fin;
            String filedata;
            int ch,cnt;

            Statement st = Main.con.createStatement();

            cnt=0;
            for(int i=0;i<textfiles.length;i++){

                if(!textfiles[i].equalsIgnoreCase("")){
                    filedata = new String("");
                    fin = new FileInputStream(folderpath_search + i + ".txt");

                    while((ch=fin.read())!=-1){
                        filedata += ((char)ch);
                    }

                    if(cnt==0)
                        st.executeUpdate("insert into TEXT values(" + l1id + "," + l2id + ",'" + filedata.toString() + "','" + textfiles[i].replace((char)(92),'/') + "',0,1)");
                    else
                        st.executeUpdate("insert into TEXT values(" + l1id + "," + l2id + ",'" + filedata.toString() + "','" + textfiles[i].replace((char)(92),'/') + "',0,0)");

                    cnt++;
                }

            }
            st.close();
        }
        catch(SQLException se){
            System.out.println("SQL Exception in updatetableTEXT():: " + se.getMessage());
        }
        catch(FileNotFoundException fe){
            System.out.println("FILENOTFOUND Exception in updatetableTEXT():: " + fe.getMessage());
        }
        catch(IOException ie){
            System.out.println("IO Exception in updatetableTEXT():: " + ie.getMessage());
        }
        delete_txtfiles();
    }

    public void storepagefromnet(URL neturl){

        boolean prematureEOF;
        int cnt = 0;

        do{
            if(stopsearch == true)
                return;

            prematureEOF = false;
            try{
                FileOutputStream fout = new FileOutputStream(folderpath_search + "page.html");
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
                if(offline){
                    JOptionPane.showMessageDialog(getContentPane(),"The System cannot find the file you specified");
                    stopsearch = true;
                }
                else{

                    int choice = JOptionPane.showInternalConfirmDialog(getContentPane(),"Unable to connect to Internet\nTo stop search operation, click on 'Yes'\nor reconnect and press Cancel button to continue.");

                    if(choice == JOptionPane.YES_OPTION){
                        stopsearch =true;
                        btn_ok.setEnabled(false);
                    }
                    else
                        cnt = 0;
                }
            }
        }while(prematureEOF);
    }

   public int searchpage(String strurl,int numbr){

       String str_doc = "";
       try{
          URL pageurl = new URL(strurl);

          storepagefromnet(pageurl);
          getptag();
          str_doc += parsedocument();

          if(remove_sqrbrackets(numbr,str_doc)<200)  //200 characters are worth nothing
            return 0;

          textfiles[numbr] = strurl;

          return 1;
       }
       catch(MalformedURLException me1){
           System.out.println("URL EXECPTION AT searchpage()");
           return 0;
       }
       finally{

           deletetemp_files();
       }

   }

   public void getptag(){
        try{

            int temp_p[] = new int[4];

            int p_clos1[] = {60,47,112,62};//</p>
            int p_clos2[] = {60,47,80,62};//</P>

            FileInputStream fin = new FileInputStream(folderpath_search + "page.html");
            FileOutputStream fout = new FileOutputStream(folderpath_search + "ptag.html");

            int c;
            boolean flag = false;

            while((c=fin.read())!=-1){

                for(int i=0;i<temp_p.length-1;i++){
                    temp_p[i] = temp_p[i+1];
                }

                temp_p[temp_p.length-1] = c;

                if(flag){
                    
                    fout.write(c);
                }

                if(temp_p[2] == 60 && (temp_p[3] == 112 || temp_p[3]==80)){   //to check <p or <P
                    flag = true;

                    fout.write(60);
                    fout.write(112);
                    fout.write(62);

                    while((c = fin.read())!=62){
                        
                    }                
                }

                else if(Arrays.equals(temp_p, p_clos1) || Arrays.equals(temp_p,p_clos2)){
                    flag = false;

                }

            }

            fout.close();
            fin.close();

        }
        catch(IOException ie){
            System.out.println("IO EXCEPTION in getptag()");
        }finally{
            
        }

    }

   public void setData(){
       try{
          FileInputStream fin = new FileInputStream(folderpath_search + "output.txt");

          int ch;
          int cnt = 0;
          String srch;
          
          while((ch = fin.read())!=-1){

              srch = "";
              do{
                  srch += (char)ch;
              }while((ch=fin.read())!=10);

              switch(++cnt){
                  case 1:label_wikiurl1.setText(srch);
                         break;
                  case 2:label_wikiurl2.setText(srch);
                         break;
                  case 3:label_otherurl1.setText(srch);
                         break;
                  case 4:label_otherurl2.setText(srch);
                         break;
                  case 5:label_videoURL.setText(srch);
                         
              }
          }
       }
       catch(IOException ie){
            System.out.println("IOException in setData()" + ie.getMessage());
       }
   }

   public boolean getErrorStatus(){
       
       boolean errcode = false;

       setData();

       try{
           FileInputStream fin = new FileInputStream(folderpath_search + "erroutput.txt");

           char ch = (char) fin.read();

           switch(ch){
               case 'D': 
                        break;

               case 'T':JOptionPane.showMessageDialog(Main.gridview.searchview.getContentPane(),"Search Interrupted\nConnection to Internet Can't be Established\nTry to search again");
                        errcode = true;
                        break;

               case 'I':int choice = JOptionPane.showInternalConfirmDialog(Main.gridview.searchview.getContentPane(),"Connection to Interenet Interrupted\nSome Images Could be Downloaded\nDo U want to continue\nPress Yes to continue\nPress No to abort");

                        if(choice==JOptionPane.YES_OPTION){
                            errcode = false;
                        }
                        else{
                            errcode = true;
                        }                        
           }
       }
       catch(IOException ie){
           System.out.println("IO Exception in getErrorStatus()" + ie.getMessage());
       }
       finally{
           return errcode;
       }
   }

   /********* this parsing is done using old style **********/
    public String parsedocument(){

        HTMLEditorKit kit = new HTMLEditorKit();

        HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();

        ElementIterator it;
        Element elem = null;

        Document d1 = null;

        String str_docparse = new String("");

        try{

            System.out.println("file:///" + folderpath_search + "ptag.html");
            URL u1 = new URL("file:///" + folderpath_search + "ptag.html");
            rd = new InputStreamReader(u1.openConnection().getInputStream());

            kit.read(rd,doc,0);

            it = new ElementIterator(doc);

            elem = it.next();

            if(elem!=null){
                d1 = elem.getDocument();
            }

            str_docparse = d1.getText(0,d1.getLength());

        }
        catch(IOException ie){
            ie.printStackTrace();
            System.out.println("IO excpetion in parsedocument()" + ie.getMessage());
        }
        catch(BadLocationException be){
            System.out.println("bad location exception in parsedocument()");
        }
        finally{
            return str_docparse;
        }

    }

    /********* the finaldoc contains numbers written in square bracktes that are to be removed ******/
    public int remove_sqrbrackets(int val,String str_doc){

        int cnt = 0;
        FileOutputStream fout = null;
        
        try{
            fout = new FileOutputStream(folderpath_search + val + ".txt");

            int char_present,char_prev = 10;

            for(int i=0;i<str_doc.length();i++){

                char_present = (int) str_doc.charAt(i);
                if(char_present==91){   //for square brackets
                    while((char_present=str_doc.charAt(++i))!=93){

                    }
                    char_prev = 93;
                    continue;
                }

                if(char_present == 32 && char_prev==32)
                    continue;

                if( (char_present<14 || char_present>31) && char_present!=34 && char_present!=39 && char_present!=10 && char_present!=13 && char_present!=46){   //for special characters & for ' & ""
                    fout.write(char_present);
                    
                    char_prev = char_present;
                    cnt++;
                    continue;
                }

                if(char_present == 46 ){
                    if(char_prev!=46 && char_prev!=10){
                        fout.write(46);
                    
                        char_prev = 46;
                    }
                    continue;
                }

                if(char_present==10 && char_prev!=10 && char_prev!=32 && char_prev!=9){  //replace ENTER with "."

                    if(char_prev!=46){
                        fout.write(46);
                    
                    }

                    fout.write(10);
                    char_prev = 10;
                    cnt++;
                    
                }

            }

            fout.close();

        }
        catch(IOException ie){
            System.out.println("IO exception in remove_sqrbrackets()::" + ie.getMessage());
        }
        catch(Exception e){
            System.out.println("OTHER EXCEPTIONS IN remove_sqrbrackets():: " +e.getMessage());
        }
        finally{
            return cnt;
        }
    }

   public void deletetemp_files(){

        String filenames[] = {"page.html","ptag.html","atag.html","erroutput.txt","output.txt"};
        File f1;

        for(int i =0;i<filenames.length;i++){
            f1 = new File(folderpath_search + filenames[i]);

            if(f1.exists()){
                System.out.println(filenames[i]);
                f1.delete();
            }
        }

    }

   public void delete_txtfiles(){
       File f1;

        for(int i =0;i<textfiles.length;i++){
            f1 = new File(folderpath_search + i + ".txt");

            if(f1.exists()){
                f1.delete();
            }
        }
   }

   public void deletetmp_images(){
       File f1;

       for(int i=0;i<15;i++){
            f1 = new File(folderpath_search + i + ".jpg");

            if(f1.exists()){
                f1.delete();
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

        panel_wikiurl = new javax.swing.JPanel();
        checkbox_wikiurl2 = new javax.swing.JCheckBox();
        checkbox_wikiurl1 = new javax.swing.JCheckBox();
        label_wikiurl1 = new javax.swing.JLabel();
        label_wikiurl2 = new javax.swing.JLabel();
        panel_other_userurl = new javax.swing.JPanel();
        checkbox_otherurl1 = new javax.swing.JCheckBox();
        label_otherurl1 = new javax.swing.JLabel();
        checkbox_otherurl2 = new javax.swing.JCheckBox();
        label_otherurl2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        checkbox_userurl1 = new javax.swing.JCheckBox();
        togglebtn_userurl1 = new javax.swing.JToggleButton();
        togglebtn_userurl2 = new javax.swing.JToggleButton();
        txtbox_userurl2 = new javax.swing.JTextField();
        label_userurl2 = new javax.swing.JLabel();
        checkbox_userurl2 = new javax.swing.JCheckBox();
        checkbox_userurl3 = new javax.swing.JCheckBox();
        txtbox_userurl3 = new javax.swing.JTextField();
        label_userurl3 = new javax.swing.JLabel();
        togglebtn_userurl3 = new javax.swing.JToggleButton();
        label_userurl1 = new javax.swing.JLabel();
        txtbox_userurl1 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        checkbox_videoURL = new javax.swing.JCheckBox();
        label_videoURL = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        combobox_topic = new javax.swing.JComboBox();
        txtfield_L2name = new javax.swing.JTextField();
        label_selectL1 = new javax.swing.JLabel();
        label_L2name = new javax.swing.JLabel();
        btn_cancel = new javax.swing.JButton();
        btn_ok = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Search Result");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panel_wikiurl.setBorder(javax.swing.BorderFactory.createTitledBorder("Wikipedia URLs"));
        panel_wikiurl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panel_wikiurl.add(checkbox_wikiurl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));
        panel_wikiurl.add(checkbox_wikiurl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        label_wikiurl1.setForeground(new java.awt.Color(0, 0, 255));
        label_wikiurl1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/link.png"))); // NOI18N
        label_wikiurl1.setText("wikiurl1");
        panel_wikiurl.add(label_wikiurl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 480, -1));

        label_wikiurl2.setForeground(new java.awt.Color(0, 0, 255));
        label_wikiurl2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/link.png"))); // NOI18N
        label_wikiurl2.setText("wikiurl2");
        panel_wikiurl.add(label_wikiurl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 480, -1));

        getContentPane().add(panel_wikiurl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 530, 80));

        panel_other_userurl.setBorder(javax.swing.BorderFactory.createTitledBorder("Other URLs"));
        panel_other_userurl.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        panel_other_userurl.add(checkbox_otherurl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        label_otherurl1.setForeground(new java.awt.Color(0, 0, 255));
        label_otherurl1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/link.png"))); // NOI18N
        label_otherurl1.setText("otherurl1");
        panel_other_userurl.add(label_otherurl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 480, -1));
        panel_other_userurl.add(checkbox_otherurl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, -1, -1));

        label_otherurl2.setForeground(new java.awt.Color(0, 0, 255));
        label_otherurl2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/link.png"))); // NOI18N
        label_otherurl2.setText("otherurl2");
        panel_other_userurl.add(label_otherurl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 50, 480, -1));

        getContentPane().add(panel_other_userurl, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 530, 80));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Custom URLs"));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(checkbox_userurl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        togglebtn_userurl1.setText("Select");
        jPanel1.add(togglebtn_userurl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 20, 70, -1));

        togglebtn_userurl2.setText("Select");
        jPanel1.add(togglebtn_userurl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 60, 70, -1));
        jPanel1.add(txtbox_userurl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 410, -1));

        label_userurl2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/link.png"))); // NOI18N
        label_userurl2.setText("userurl2");
        jPanel1.add(label_userurl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 410, -1));
        jPanel1.add(checkbox_userurl2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));
        jPanel1.add(checkbox_userurl3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, -1, -1));
        jPanel1.add(txtbox_userurl3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 410, -1));

        label_userurl3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/link.png"))); // NOI18N
        label_userurl3.setText("userurl3");
        jPanel1.add(label_userurl3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 410, -1));

        togglebtn_userurl3.setText("Select");
        jPanel1.add(togglebtn_userurl3, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 100, 70, -1));

        label_userurl1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/link.png"))); // NOI18N
        jPanel1.add(label_userurl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 410, -1));
        jPanel1.add(txtbox_userurl1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 410, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 530, 140));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Video URL"));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel2.add(checkbox_videoURL, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        label_videoURL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/television.png"))); // NOI18N
        label_videoURL.setText("www.bing.com");
        jPanel2.add(label_videoURL, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 480, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 310, 530, 50));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Add As"));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        combobox_topic.setModel(new javax.swing.DefaultComboBoxModel(new String[] {  }));
        jPanel3.add(combobox_topic, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 20, 170, 30));
        jPanel3.add(txtfield_L2name, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60, 170, -1));

        label_selectL1.setText("Select Main Topic:");
        jPanel3.add(label_selectL1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 120, 20));

        label_L2name.setText("Enter Subtopic as:");
        jPanel3.add(label_L2name, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 120, 20));

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 360, 530, 100));

        btn_cancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/cancel.png"))); // NOI18N
        btn_cancel.setText("Cancel");
        getContentPane().add(btn_cancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 470, 100, 30));

        btn_ok.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/accept.png"))); // NOI18N
        btn_ok.setText("Ok");
        getContentPane().add(btn_ok, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 470, 70, 30));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                new SearchResultView("HUBBLE SPACE TELESCOPE",false).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_cancel;
    private javax.swing.JButton btn_ok;
    private javax.swing.JCheckBox checkbox_otherurl1;
    private javax.swing.JCheckBox checkbox_otherurl2;
    private javax.swing.JCheckBox checkbox_userurl1;
    private javax.swing.JCheckBox checkbox_userurl2;
    private javax.swing.JCheckBox checkbox_userurl3;
    private javax.swing.JCheckBox checkbox_videoURL;
    private javax.swing.JCheckBox checkbox_wikiurl1;
    private javax.swing.JCheckBox checkbox_wikiurl2;
    private javax.swing.JComboBox combobox_topic;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel label_L2name;
    private javax.swing.JLabel label_otherurl1;
    private javax.swing.JLabel label_otherurl2;
    private javax.swing.JLabel label_selectL1;
    private javax.swing.JLabel label_userurl1;
    private javax.swing.JLabel label_userurl2;
    private javax.swing.JLabel label_userurl3;
    private javax.swing.JLabel label_videoURL;
    private javax.swing.JLabel label_wikiurl1;
    private javax.swing.JLabel label_wikiurl2;
    private javax.swing.JPanel panel_other_userurl;
    private javax.swing.JPanel panel_wikiurl;
    private javax.swing.JToggleButton togglebtn_userurl1;
    private javax.swing.JToggleButton togglebtn_userurl2;
    private javax.swing.JToggleButton togglebtn_userurl3;
    private javax.swing.JTextField txtbox_userurl1;
    private javax.swing.JTextField txtbox_userurl2;
    private javax.swing.JTextField txtbox_userurl3;
    private javax.swing.JTextField txtfield_L2name;
    // End of variables declaration//GEN-END:variables

}


