

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 *
 * @author tomer
 */

public class OnlineSearch {

    
    String user_str;
    String textlinks[],videolink,imagelinks[];
    int textlinkindex,imagelinkindex;
    
    static int imagedownloadproblemcount = 0;
    boolean searchforVIDEO,searchforWIKI,searchforOTHER,searchforIMAGE;
    boolean stopsearch = false;
    public static String proxyenabled,proxyaddress,portaddress;

    String folderpath_search;
    Reader rd;
    HTMLEditorKit.Parser parser;
    HTMLEditorKit.ParserCallback parserlistener;

    FileOutputStream searchoutputfile,erroutputfile;

    /** Creates new form SearchResultView */
    public OnlineSearch(String [] args) {

        initparserlistener();
		
        this.folderpath_search = args[0];
        
        try{
            searchoutputfile =  new FileOutputStream(folderpath_search + "output.txt");            
        }
        catch(FileNotFoundException fe){
            System.out.println(fe.getMessage());
        }

        proxyenabled = args[2];
        proxyaddress = args[3];
        portaddress = args[4];
		
		NetworkSettings.initNetworkSettings();

        initstr(args[1]);		
        startSEARCH();
    }

    public void startSEARCH(){
        searchWIKIURL();
        searchOTHERURL();
        searchVIDEOURL();
        searchIMAGES();

		if(stopsearch)
			return;
		
        try{
            erroutputfile =  new FileOutputStream(folderpath_search + "erroutput.txt");
            erroutputfile.write('D');
        }
        catch(IOException ie){
            System.out.println(ie.getMessage());
        }
    }

    public void initstr(String srch){

        user_str = new String(srch);
        user_str = user_str.trim();  //remove leading & trailing spaces
        user_str = user_str.replace(" ","+");

        textlinks = new String[4];

        for(int i=0;i<4;i++)
            textlinks[i] = new String("");

        videolink = new String("");
        imagelinks = new String[15];

        searchforOTHER = false;
        searchforWIKI = false;
        searchforVIDEO = false;
        searchforIMAGE = false;

        textlinkindex = 0;
        imagelinkindex = 0;
    }

    /************ PARSER LISTENER STARTS HERE ***********/
    public void initparserlistener(){

        parserlistener = new HTMLEditorKit.ParserCallback(){

        @Override
        public void handleStartTag(HTML.Tag t,MutableAttributeSet a,int pos){

            if(stopsearch == true)
                return;

            /********* FOR "a" tags to store video,wikipedia,images & other urls ************/
            if(t.toString().equalsIgnoreCase("a") && a.getAttribute(HTML.Attribute.HREF)!=null){

                //storing video urls
                if(searchforVIDEO == true){
                    if(a.getAttribute(HTML.Attribute.HREF).toString().contains("www.youtube.com")){
                       videolink = a.getAttribute(HTML.Attribute.HREF).toString();
                       searchforVIDEO = false;
                    }
                }

                else if(textlinkindex<2 && searchforWIKI == true){   //storing wikipedia urls
                     if(a.getAttribute(HTML.Attribute.HREF).toString().contains("en.wikipedia.org")){
                        textlinks[textlinkindex++] = a.getAttribute(HTML.Attribute.HREF).toString();
                     }
                }

                else if(textlinkindex<4 && searchforOTHER == true){  //storing other urls
                    String hrefstr = a.getAttribute(HTML.Attribute.HREF).toString();
                    if(hrefstr.contains("cc.bingj.com") && !hrefstr.contains("wikipedia")){
                           textlinks[textlinkindex++] = hrefstr;
                    }
                }

                else if(imagelinkindex < 15 && searchforIMAGE == true){  //storing images
                    String imgstr = a.getAttribute(HTML.Attribute.HREF).toString();

                    if(imgstr.contains("furl=") && imgstr.contains(".jpg")){
                        imgstr = imgstr.substring(imgstr.indexOf("furl="));
                        imgstr = imgstr.replaceAll("furl=","");
                        imgstr = imgstr.replaceAll("%2f","/");
                        imgstr = imgstr.replaceAll("%3a",":");
                        imagelinks[imagelinkindex++] = imgstr;

                        try{
                            URL uimg = new URL(imgstr);

                            BufferedImage bfrdimg = ImageIO.read(uimg);
                            File f1 = new File(folderpath_search + (imagelinkindex-1) +".jpg");
                            ImageIO.write(bfrdimg,"jpg",f1);

                        }
                        catch(MalformedURLException me){
                            System.out.println("MALFORMED URL EXCEPTION in saving images" + me.getMessage());
                            imagedownloadproblemcount++;
                        }
                        catch(IOException io){
                            System.out.println("IO EXCEPTION in saving images " + io.getMessage());
                            imagedownloadproblemcount++;
                        }
                        finally{
                              if(imagedownloadproblemcount==4){
                                  try{
                                      erroutputfile =  new FileOutputStream(folderpath_search + "erroutput.txt");
                                      erroutputfile.write('I');
                                      erroutputfile.close();
									  
									  stopsearch = true;
									  
                                  }
                                  catch(IOException ie){
                                      System.out.println(ie.getMessage());  									  
                                  }
                                  
                            }
                        }
                    }
                }

            }
            /******* SEARCHING FOR video,wikipedia,images & other urls ends here ************/
        }

        @Override
        public void handleText(char []data,int pos){

        }

        @Override
        public void handleSimpleTag(HTML.Tag t,MutableAttributeSet a,int pos){

        }
        @Override
        public void handleEndTag(HTML.Tag t,int pos){

        }

        };
    }

    /******** ACTION LISTENER FOR BUTTONS ************/
    @SuppressWarnings("static-access")
    public void cancelACTION(){
        deletetemp_files();        
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

            if(cnt==4 && prematureEOF==true){
                try{
                    erroutputfile =  new FileOutputStream(folderpath_search + "erroutput.txt");
                    erroutputfile.write('T');
                    erroutputfile.close();
                    stopsearch = true;
                }
                catch(IOException ie){
                    System.out.println(ie.getMessage());
                }
            }
        }while(prematureEOF);
    }

    public void getatag(){
        try{

            int temp_a[] = new int[4];

            int a_clos[] = {60,47,97,62};//</a>

            FileInputStream fin = new FileInputStream(folderpath_search + "page.html");
            FileOutputStream fout = new FileOutputStream(folderpath_search + "atag.html");

            int c;
            boolean flag = false;

            while((c=fin.read())!=-1){

                for(int i=0;i<temp_a.length-1;i++){
                    temp_a[i] = temp_a[i+1];
                }

                temp_a[temp_a.length-1] = c;

                if(flag){
                    fout.write(c);
                }

                if(temp_a[1] == 60 && temp_a[2] == 97 && temp_a[3] == 32){   //to check "<a "
                    flag = true;

                    fout.write(60);
                    fout.write(97);
                    fout.write(32);

                }

                else if(Arrays.equals(temp_a, a_clos)){
                    flag = false;
                }
            }

            fout.close();
            fin.close();

        }
        catch(IOException ie){
            System.out.println("IO EXCEPTION in getatag()" + ie.getMessage());
        }

    }

    public void getlinks(){
        textlinkindex = 0;
        imagelinkindex = 0;
        try{
           URL u1 = new URL("file:///" + folderpath_search + "atag.html");

           rd = new InputStreamReader(u1.openConnection().getInputStream());

           parser = new ParserDelegator();

           parser.parse(rd,parserlistener,false);

        }
        catch(MalformedURLException me){
           System.out.println("MALFORMED URL EXCEPTION IN getlinks()" + me.getMessage());
        }
        catch (IOException io) {
           System.out.println("IO EXCEPTION in getlinks()" + io.getMessage());
        }
        textlinkindex = 0;
        imagelinkindex = 0;
   }


   public void searchIMAGES(){
       String netquery = "http://www.bing.com/images/search?q=" + user_str + "+images";
       searchforIMAGE = true;
       try{
          URL imageurl = new URL(netquery);

          storepagefromnet(imageurl);
          getatag();
          getlinks();
       }
       catch(MalformedURLException me){
           System.out.println("URL EXECPTION AT searchIMAGES()" + me.getMessage());
       }
       searchforIMAGE = false;
   }

   public void searchVIDEOURL(){

       String netquery = "http://www.bing.com/search?q=" + user_str + "+youtube";
       searchforVIDEO = true;
       try{
          URL youtubeurl = new URL(netquery);

          storepagefromnet(youtubeurl);
          getatag();
          getlinks();

          if(!(videolink.contains("www.") || videolink.contains("http")))
            videolink = "NO LINK PRESENT";

          for(int i=0;i<videolink.length();i++)
              searchoutputfile.write(videolink.charAt(i));
          searchoutputfile.write(10);
       }
       catch(MalformedURLException me){
           System.out.println("URL EXECPTION AT searchVIDEOURL()" + me.getMessage());
       }
       catch(IOException ie){
           System.out.println("IOEXECPTION AT searchVIDEOURL()" + ie.getMessage());
       }

       searchforVIDEO = false;

       deletetemp_files();
   }

   public void searchWIKIURL(){

       String netquery = "http://www.bing.com/search?q=" + user_str + "+wikipedia";
       searchforWIKI = true;
       try{
          URL wikiurl = new URL(netquery);

          storepagefromnet(wikiurl);
          getatag();
          getlinks();

          String str = "http://en.wikipedia.org/wiki/Special:Search?search=" + user_str.replaceAll(" ","+") +"&go=Go";

          for(int i=0;i<str.length();i++)
              searchoutputfile.write(str.charAt(i));
          searchoutputfile.write(10);
          for(int i=0;i<textlinks[1].length();i++)
              searchoutputfile.write(textlinks[1].charAt(i));
          searchoutputfile.write(10);
       }
       catch(MalformedURLException me){
           System.out.println("URL EXECPTION AT searchWIKIURL()" + me.getMessage());
       }
       catch(IOException ie){
           System.out.println("IOEXECPTION AT searchWIKIURL()" + ie.getMessage());
       }

       searchforWIKI = false;

       deletetemp_files();
   }

   public void searchOTHERURL(){

       String netquery = "http://www.bing.com/search?q=" + user_str;
       searchforOTHER = true;
       try{
          URL otherurl = new URL(netquery);

          storepagefromnet(otherurl);
          getatag();
          getlinks();

          for(int i=0;i<textlinks[2].length();i++)
              searchoutputfile.write(textlinks[2].charAt(i));
          searchoutputfile.write(10);

          for(int i=0;i<textlinks[3].length();i++)
              searchoutputfile.write(textlinks[3].charAt(i));
          searchoutputfile.write(10);

       }
       catch(MalformedURLException me){
           System.out.println("URL EXECPTION AT searchOTHERURL()" + me.getMessage());
       }
       catch(IOException ie){
           System.out.println("IOEXECPTION AT searchOTHERURL()" + ie.getMessage());
       }

       searchforOTHER = false;

       deletetemp_files();
   }
   
   
   public void deletetemp_files(){

        String filenames[] = {"page.html","ptag.html","atag.html"};
        File f1;

        for(int i =0;i<filenames.length;i++){
            f1 = new File(folderpath_search + filenames[i]);

            if(f1.exists()){
                f1.delete();
            }
        }

    }


   public static void main(String args[]){

       /* args[0] = "folderpath"
        * args[1] = "string to be searched"
        * args[2] = "ProxyEnabled"
        * args[3] = "ProxyAddress"
        * args[4] = "PortAddress"
        */
	
       new OnlineSearch(args);
   }
   
}

class NetworkSettings{

    static Timer timer_testnet = null;

    public static void initNetworkSettings(){
        setTimeout();
        setProxy(OnlineSearch.proxyenabled,OnlineSearch.proxyaddress,OnlineSearch.portaddress,"4");        
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
