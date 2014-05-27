/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ProgressBar.java
 *
 * Created on Feb 13, 2010, 2:37:08 PM
 */

package maggi_1;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author MARQUIS_CS
 */
public class ProgressBar extends javax.swing.JFrame {

    Thread thread_progress;
    File file_image;
    int j,cnt,imgCnt=0,imgCntVer,x,y=10,flag=1;
    String imgPaths[];
    
            
    /** Creates new form ProgressBar */
    public ProgressBar() {
        initComponents();

       imgPaths =new String[15];
       for(int i=0;i<15;i++)
           imgPaths[i]=new String();
        
        setLocation( (int) Main.screensize.getWidth()/2 - (int) getWidth()/2,(int) Main.screensize.getHeight()/2 - (int) getHeight()/2);
       
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we){
                setExtendedState(JFrame.ICONIFIED);
            }
        });

    }

    public void startProgress(){
        
        label_status.setText("Searching on Internet...");
        progressbar_search.setValue(10);

        thread_progress = new Thread(new Runnable() {

            @SuppressWarnings("static-access")
            public void run() {
                try{
                   while(thread_progress.isAlive()){
                        thread_progress.sleep(2000);

                        j=0;
                        cnt = 0;
                        for(int i=0;i<15;i++){
                            file_image = new File(Main.curdir + "/CACHE/SEARCHIMAGES/" + i + ".jpg");
                            //file_image = new File("F:\\amdocs\\MAGGI_1\\CACHE\\SEARCHIMAGES\\" + i + ".jpg");

                            if(file_image.exists()){
                                j=i;
                                addImagetoSearchBox(file_image.getPath());
                                //jPanelProgressImages.add(new javax.swing.JLabel(file_image.getPath()));
                                cnt++;
                            }
                        }

                        label_status.setText("Images Downloaded: " + cnt);

                        progressbar_search.setValue(10 + (j+1) * 6);
                   }
                }
                catch(NullPointerException ne){
                    
                }
                catch(InterruptedException ie){

                }
            }
        });

        thread_progress.start();
    }
    void addImagetoSearchBox(String filePath)
    {
     
        for(int i=0;i<imgCnt;i++)
        {
            if(imgPaths[i].equals(filePath))
            {
                return;
               
            }
            else
            {
                flag=1;
                
               
            }
        }
         System.out.println(filePath + " " + imgCnt + " " + flag + " " +imgPaths.length);
        if(flag==1)
        {
        jLabelImageProgress = new javax.swing.JLabel();
           x=((imgCnt%7)*60)+(10*((imgCnt%7)+1));
        if((imgCnt%7)==0 && imgCnt!=0)
        {
            y=y+70;
        }
        
        jPanelProgressImages.add(jLabelImageProgress,new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, 60, 60));
        
        jLabelImageProgress.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        ImageIcon imgico = new ImageIcon(filePath);
        Image img = imgico.getImage();
        imgico.setImage(img.getScaledInstance(jLabelImageProgress.getWidth()-1, jLabelImageProgress.getHeight()-1, Image.SCALE_SMOOTH));
        jLabelImageProgress.setIcon(imgico);
        jLabelImageProgress.setVisible(true);
        System.out.println(filePath + " is added.");
        
        jPanelProgressImages.repaint();
        jScrollPanelProgressImages.setViewportView(jPanelProgressImages);
        imgPaths[imgCnt]=filePath;
        imgCnt++;
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

        progressbar_search = new javax.swing.JProgressBar();
        label_status = new javax.swing.JLabel();
        jScrollPanelProgressImages = new javax.swing.JScrollPane();
        jPanelProgressImages = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Maggi Search");
        setMinimumSize(new java.awt.Dimension(520, 284));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        getContentPane().add(progressbar_search, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, 500, -1));

        label_status.setText("SEARCH STATUS");
        getContentPane().add(label_status, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 160, -1));

        jScrollPanelProgressImages.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jScrollPanelProgressImages.setViewportBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jPanelProgressImages.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jScrollPanelProgressImages.setViewportView(jPanelProgressImages);

        getContentPane().add(jScrollPanelProgressImages, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 500, 160));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProgressBar().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanelProgressImages;
    private javax.swing.JScrollPane jScrollPanelProgressImages;
    private javax.swing.JLabel label_status;
    private javax.swing.JProgressBar progressbar_search;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JLabel jLabelImageProgress;
}
