/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package triway;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Font;
//import org.apache.log4j.PropertyConfigurator;
//import java.util.logging.Logger;

/**
 *
 * @author DELL
 */
public class Triway {
    //final static Logger logger = Logger.getLogger(Triway.class);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //PropertyConfigurator.configure("log4j.properties"); 
        
        // TODO code application logic here
        
        try {
             // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
            UIManager.getLookAndFeelDefaults().put("defaultFont", new Font("Verdana", Font.BOLD, 14));

        } catch (InstantiationException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(splash.class.getName()).log(Level.SEVERE, null, ex);
        }

//        try{
//            UIManager.put("TabbedPane.selected", Color.red);
//
//            UIManager.put("TabbedPane.unselectedTabBackground", Color.black);
//        } catch(Exception e){
//            e.printStackTrace();
//        }

        new Triway();
        
        splash Splash = new splash();
        Splash.setVisible(true);
        Login login = new Login();
        try{
            Thread.sleep(5000);
            //Splash.loadingnum.setText(Integer.toString(i)+"%");
            //Splash.loadingbar.setValue(i);

            login.setVisible(true);
            Splash.setVisible(false);
              
        }catch(Exception e){}
    }
}
