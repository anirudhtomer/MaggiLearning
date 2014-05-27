/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package maggi_1;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author tomer
 */

class PopupTimers{

    static Timer timerstartpop = null,timerendpop = null,timerdelaypop = null,timerintermediate = null;
    static String strtimestart,strtimeend,strtimecurrent;
    static int timedelaysec,timestartsec,timeendsec,timecurrentsec,time24hrssec;
    static int cachepopup[],checkprev[],currentcount[],totalcount[];
    static int cachpopup_count,limit;
    

    //clear the cache of POPUPS
    static void clearCachepopup(){
        cachepopup = new int[10];
        cachpopup_count = -1;

        for(int i = 0;i<10;i++)
            cachepopup[i] = -1;
    }

    //SET THE COUNT OF ATLEAST THESE MANY...in LAST THESE MANY...
    static  void setCheckprev(){
        checkprev = new int[5];

        checkprev[0] = 3;
        checkprev[1] = 4;
        checkprev[2] = 5;
        checkprev[3] = 6;
        checkprev[4] = 7;
    }

    //RESETS THE CURRENT COUNT ARRAY
    static void resetCurrentCount(){
        currentcount = new int[5];

        try{
            Statement st = Main.con.createStatement();
            ResultSet rst1 = st.executeQuery("select count(*) as rowcnt from PRIORITY");
            rst1.next();

            limit = rst1.getInt("rowcnt");

            if(limit==0)
                return;
            if(limit>5)
                limit = 5;

            rst1  = st.executeQuery("select * from PRIORITY order by PRIORITY LIMIT 5");

            for(int i=0;i<limit;i++){
                rst1.absolute(i+1);
                currentcount[i] = rst1.getInt("currentcount");
            }

        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"SQL Exception ---- TimerFunction:resetcurrentcount() ------ "  + ex.getMessage());
        }
    }

    //GETS THE TOTAL COUNT
    static void getTotalCount(){

        totalcount = new int[5];

        try{
            Statement st = Main.con.createStatement();
            ResultSet rst1 = st.executeQuery("select count(*) as rowcnt from PRIORITY");
            rst1.next();

            limit = rst1.getInt("rowcnt");

            if(limit==0)
                return;
            
            if(limit>5){
                limit = 5;                
            }

            rst1  = st.executeQuery("select * from PRIORITY order by PRIORITY LIMIT 5");

            for(int i=0;i<limit;i++){
                rst1.absolute(i+1);
                totalcount[i] = rst1.getInt("totalcount");
            }

        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"SQL Exception ---- TimerFunction:getTotalcount() ------ "  + ex.getMessage());
        }
    }

    //return the time in sec for a string like "12:03:55"
    public static int StringToTime(String str){

        int hrs,min,sec;

        hrs = Integer.parseInt(str.substring(0,2));
        min = Integer.parseInt(str.substring(3,5));
        sec = Integer.parseInt(str.substring(6));

        return (hrs*60*60 + min*60 + sec);
    }

    //reads the database to get the start time & end time & starts all timers again
    public static void updateTimers(){

        clearCachepopup();
        setCheckprev();
        resetCurrentCount();
                
        try{
            
            Statement st = Main.con.createStatement();

            ResultSet rst = st.executeQuery("select * from POPUP_SETTINGS");
            rst.next();
            timedelaysec = rst.getInt("delaymin") * 60;
            strtimeend = rst.getString("endtime");
            strtimestart = rst.getString("starttime");
            strtimecurrent = new SimpleDateFormat("HH:mm:ss").format(new java.util.Date());

            stopPreviousTimers();
            startNewTimers();
        }
        catch(SQLException ex){
            JOptionPane.showMessageDialog(null,"SQL Exception ---- TimerFunction:updateTimer() ------ "  + ex.getMessage());
        }
    }

    //stop all the previous timers if running
    static void stopPreviousTimers(){
        try{
            if(timerstartpop!=null){
                timerstartpop.stop();
                timerstartpop = null;
            }
            
            if(timerendpop!=null){
                timerendpop.stop();
                timerendpop = null;
            }
            
            if(timerdelaypop!=null){
                timerdelaypop.stop();
                timerdelaypop = null;
            }

            if(timerintermediate!=null){
                timerintermediate.stop();
                timerintermediate = null;
            }
        }
        catch(NullPointerException ne){
            JOptionPane.showMessageDialog(null,"NULL POINTER Exception ---- TimerFunction:stopPreviousTimers() ------ "  + ne.getMessage());
        }
    }

    //start new Timers
    static  void startNewTimers(){
       
       timecurrentsec = StringToTime(strtimecurrent);
       timestartsec = StringToTime(strtimestart);
       timeendsec = StringToTime(strtimeend);
       time24hrssec = 24*60*60;

       //its like pop-up in one day only
       if(timestartsec < timeendsec){

            if(timecurrentsec < timestartsec){
                initTIMERSTART(timestartsec-timecurrentsec);
                initTIMEREND(timeendsec-timecurrentsec);

            }

            else if(timecurrentsec > timeendsec){
                initTIMERSTART(time24hrssec - timecurrentsec + timestartsec);
                initTIMEREND(time24hrssec - timecurrentsec + timeendsec);

            }

            else if(timecurrentsec > timestartsec  &&  timecurrentsec <timeendsec){
                
                initTIMEREND(timeendsec - timecurrentsec);
                startPOPUPS();

            }
       }

       else{
            if(timecurrentsec < timestartsec ){
                initTIMERSTART(timestartsec-timecurrentsec);
                initTIMEREND(time24hrssec-timecurrentsec + timeendsec);
            }

            else if(timecurrentsec > timestartsec  &&  timecurrentsec > timeendsec){
                initTIMEREND(time24hrssec - timecurrentsec + timeendsec);
                startPOPUPS();
            }
            else if(timecurrentsec < timestartsec  &&  timecurrentsec < timeendsec){
                initTIMEREND(timeendsec - timecurrentsec);
                startPOPUPS();
            }
       }

    }

    //INITIALIZE THE TIMER-DELAY
    static void initTIMERDELAY(int timeinsec){
        timerdelaypop = new Timer(timeinsec * 1000,new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateAndShowPOPUP();
            }
        });

        timerdelaypop.start();
    }

    //INITIALIZE THE TIMER-START
    static void initTIMERSTART(int timeinsec){
        timerstartpop = new Timer(timeinsec*1000,new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                timerstartpop.stop();
                startPOPUPS();
            }
        });

        timerstartpop.start();
    }

    //INITIALIZE THE TIMER-END
    static void initTIMEREND(int timeinsec){
        timerendpop = new Timer(timeinsec*1000,new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                timerendpop.stop();
                endPOPUPS();
                initTIMERINTERMEDIATE(60);
            }
        });

        timerendpop.start();
    }

    //INITIALIZE INTERMEDIATE TIMER
    static void initTIMERINTERMEDIATE(int timeinsec){
        timerintermediate = new Timer(timeinsec*1000,new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                timerintermediate.stop();
                updateTimers();
            }
        });

        timerintermediate.start();
    }

    //START THE GENERATION OF POP-UPS
    static void startPOPUPS(){
        initTIMERDELAY(timedelaysec);
        generateAndShowPOPUP();
    }

    //END THE GENERATION OF POP-UPS
    static void endPOPUPS(){
        timerdelaypop.stop();
    }

    //PROCESS THE CACHE
    static int processCache(int gen_num){

        int cnt1=0,cnt2=0;

        //check for "PRIORITY 1"
        for(int j=0;j<limit;j++){
            cnt1 = checkprev[j];
            cnt2 = checkprev[j];

            for(int i = 9;i>=0 && cnt2>0 && cnt1>0 && totalcount[j]>currentcount[j];i--,cnt2--){
                if(cachepopup[i]!=(j+1))
                    cnt1--;
            }
            
            if(cnt1==0 && totalcount[j]>currentcount[j]){
                gen_num = j+1;
                break;
            }
        }

        for(int k=0;k<9;k++)
            cachepopup[k] = cachepopup[k+1];
        
        cachepopup[9] = gen_num;

        if(cachpopup_count<9)
            cachpopup_count++;

        return gen_num;
    }


    //GENERATE THE POP-UP
    static void generateAndShowPOPUP(){

        int gen_num = 0,i,l1id = 1,l2id = 1;

        try{
            Statement st = Main.con.createStatement();
            ResultSet rst1 = st.executeQuery("select popupdisable from GENERAL_SETTINGS");

            rst1.next();

            if(rst1.getInt("popupdisable")==1)
                return;

            getTotalCount();

            if(limit==0){
                //JOptionPane.showMessageDialog(null,"THE PRIORITY TABLE IS EMPTY..SO U WON'T GET POPUPS");
                return;
            }

            rst1 = st.executeQuery("select maxtopics from POPUP_SETTINGS");
            rst1.next();

            if(limit > rst1.getInt("maxtopics"))
                limit = rst1.getInt("maxtopics");

            //GENERATE RANDOMLY
            do{
                gen_num = (int) (Math.random() * 10);                
            }while(gen_num <1 || gen_num>limit);

            
            //PROCESS CACHE
            gen_num = processCache(gen_num);            
            
            ResultSet rst2 = st.executeQuery("select * from PRIORITY order by priority LIMIT 5");

            if(!rst2.next()){
                JOptionPane.showMessageDialog(null,"YOUR PRIORITY TABLE IS EMPTY NO MORE POPUPS CAN BE GENERATED");
                return;
            }
            
            rst2.absolute(gen_num);

            for(i=limit;i>0;i--){

                if(rst2.getInt("totalcount") > rst2.getInt("currentcount")){
                    l1id = rst2.getInt("l1id");
                    l2id = rst2.getInt("l2id");
                    st.executeUpdate("update PRIORITY set currentcount = currentcount + 1 where priority = " + gen_num);
                    currentcount[gen_num-1] += 1;
                    break;
                }

                gen_num = (gen_num+limit-1)%limit;

                if(gen_num == 0)
                    gen_num = limit;

               rst2.absolute(gen_num);

            }

            if(i==0){
                rst2.absolute(1);
                l1id = rst2.getInt("l1id");
                l2id = rst2.getInt("l2id");
                st.executeUpdate("update PRIORITY set currentcount = 0 where priority <= " + limit);
                st.executeUpdate("update PRIORITY set currentcount = currentcount + 1 where priority = 1");
                resetCurrentCount();
            }

            Main.l1id = l1id;
            Main.l2id = l2id;

        }
        catch(SQLException se){
            JOptionPane.showMessageDialog(null,"SQLException ---- TimerFunction:generate_showPOPUP() ------ "  + se.getMessage());
        }
    }
}

