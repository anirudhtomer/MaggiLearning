package maggi_1;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JLabel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.netbeans.lib.awtextra.AbsoluteConstraints;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MoreInfoView.java
 *
 * Created on Dec 29, 2009, 7:54:36 PM
 */

/**
 *
 * @author tomer
 */
public class MoreInfoView extends javax.swing.JFrame {

    static int more_X,more_Y;

    static int more_height;
    static int init_height;
    int red,green,blue;
    String font;

    LinkListener more_linklistener;
    int linkcnt;
    JLabel[] label_link;
    StyledDocument doc;
    Style regular;

    /** Creates new form MoreInfoView */
    @SuppressWarnings("static-access")
    public MoreInfoView(){
        initComponents();

        setPaneStyle();
        addListeners();
        setwidth();
        more_X = Main.popupcontrol.popupview.popup_X;
        this.setLocation(more_X,this.getY());
        init_height = this.getHeight();
    }

    @SuppressWarnings("static-access")
    void setwidth(){
        this.setSize(Main.popupcontrol.popupview.getWidth(),this.getHeight());
    }

    public void setPaneStyle(){
        Style def;

        doc = textpane_more.getStyledDocument();
        def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        regular = doc.addStyle("regular",def);
    }

    void addListeners(){
        initmouselistener();
        initbuttonlistener();
    }

    void initmouselistener(){
        more_linklistener = new LinkListener(getContentPane());
    }

    void initbuttonlistener(){
        btn_close.addActionListener(new ActionListener() {

            @SuppressWarnings("static-access")
            public void actionPerformed(ActionEvent e) {
                Main.popupcontrol.moreinfoview.setVisible(false);
            }
        });
    }


    void showMore(){
        setVisible(true);
        scrollpane_moreinfo.getVerticalScrollBar().setValue(0);
    }


    @SuppressWarnings("static-access")
    void setData() {

        for(int i=0;i<linkcnt;i++){
            Main.popupcontrol.moreinfoview.remove(label_link[i]);
        }

        more_height = init_height;
        setPANEDATA();
        readLINKS();
        this.setSize(this.getWidth(),more_height);
        this.setLocation(this.getX(), (int) (Main.screensize.height - more_height - PopUpController.popupview.getHeight() - MaggiTray.maggitray.getTrayIconSize().getHeight()-2));

    }

    public void readLINKS(){

        @SuppressWarnings("static-access")
        PopUpItems popupitem = Main.popupcontrol.arr_popupobject[Main.popupcontrol.current_popupitemid];

        try{
            Statement st = Main.con.createStatement();

            ResultSet rst1 = st.executeQuery("select count(link) as cnt from LINK where l1id = " + popupitem.l1id + " and l2id = " + popupitem.l2id);

            if(rst1.next())
             linkcnt = rst1.getInt("cnt");

            else
                return;

            ResultSet rst2 = st.executeQuery("select link from LINK where l1id = " + popupitem.l1id + " and l2id = " + popupitem.l2id);
            positionLINKS(rst2);

            st.close();

        }
        catch(SQLException se){
            System.out.println("SQLException in readLINKS():: " + se.getMessage());
        }
    }

    @SuppressWarnings("static-access")
    public void positionLINKS(ResultSet rst){

        try{
            int htdifference = 25,prevX,prevY;

            label_link = new JLabel[linkcnt];

            prevX = label_refer.getX()+20;
            prevY = label_refer.getY();// + label_refer.getHeight() + 5;
            more_height += 10;

            for(int i=0;i<linkcnt;i++){
                rst.next();

                label_link[i] = new JLabel(rst.getString("link"));

                label_link[i].addMouseListener(more_linklistener.hyperlinkListener);
                label_link[i].setForeground(Color.BLUE);
                label_link[i].setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/link.png")));

                getContentPane().add(label_link[i],new AbsoluteConstraints(prevX,prevY + htdifference,-1,-1));
                prevY += htdifference;

                more_height += htdifference;
            }

           // more_height +=htdifference;

        }
        catch(SQLException se){
            System.out.println("SQLException in positionLINKS():: " + se.getMessage());
        }

    }
    
    @SuppressWarnings({"static-access","static-access"})
    void setPANEDATA(){

        try {

            getFont_Colour();

            StyleConstants.setFontFamily(regular,font);
            StyleConstants.setForeground(regular,new Color(red,green,blue));

            doc.remove(0,doc.getLength());
            doc.insertString(0,Main.popupcontrol.arr_popupobject[Main.popupcontrol.current_popupitemid].str_moreinfo,regular);
        } catch (BadLocationException ex) {
            System.out.println("BadLocationException in setPANEDATA::" + ex.getMessage());
        }
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


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollpane_moreinfo = new javax.swing.JScrollPane();
        textpane_more = new javax.swing.JTextPane();
        btn_close = new javax.swing.JButton();
        label_refer = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        textpane_more.setOpaque(false);
        scrollpane_moreinfo.setViewportView(textpane_more);

        getContentPane().add(scrollpane_moreinfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 410, 250));

        btn_close.setIcon(new javax.swing.ImageIcon(getClass().getResource("/maggi_1/icons/close.png"))); // NOI18N
        getContentPane().add(btn_close, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 0, 20, 20));

        label_refer.setText("Reference Links:");
        getContentPane().add(label_refer, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 290, 110, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MoreInfoView();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_close;
    private javax.swing.JLabel label_refer;
    private javax.swing.JScrollPane scrollpane_moreinfo;
    private javax.swing.JTextPane textpane_more;
    // End of variables declaration//GEN-END:variables

}
