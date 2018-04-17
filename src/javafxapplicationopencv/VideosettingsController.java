/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplicationopencv;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;


/**
 * FXML Controller class
 *
 * @author Vivek
 */
public class VideosettingsController implements Initializable {
    
  
    int settings[] = new int[8]; //used for storing the settings 
    /*
    Index 0 - Motion Sensitivity
    Index 1 - FPS
    Index 2 - Camera Number
    Index 3 - original 
    Index 4 - difference
    Index 5 - threshold
    Index 6 - grayscale
    Index 7 - contours
    
    */
    
    @FXML
    private JFXTextField fpsTextField;
    
    @FXML
    private JFXTextField camnoTextField;
     
    @FXML
    private JFXSlider motionSense;
    
    @FXML
    private JFXRadioButton original;
    
     
    @FXML
    private JFXRadioButton difference;
     
    @FXML
    private JFXRadioButton threshold;
     
    @FXML
    private JFXRadioButton grayscale;
     
    @FXML
    private JFXRadioButton contours;
    
    @FXML
    private Button save;
    
    @FXML
    private Button reset;
    
    @FXML
    private FXMLDocumentController mainpage;
    
    @FXML
    private Label testLabel1;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    } 
   
    public void setInitialSetting(){
    for(int i=0;i<8;i++){
        settings[i]=0;
    }
    
    settings[0]=500; //default for motion sensitivity 
    settings[1]=30; //default fps
    
    }
     
   public void sendData(){
   
       Stage sta = (Stage) save.getScene().getWindow();
     sta.close();
     
     FXMLLoader Loader = new FXMLLoader();
     
     Loader.setLocation(getClass().getResource("FXMLDocument.fxml"));
     
     try{
         Loader.load();
     }
     catch(IOException e){
         Logger.getLogger(VideosettingsController.class.getName()).log(Level.SEVERE,null,e);
     }
     
     FXMLDocumentController mp = Loader.getController();
     mp.setSettings(settings);
     
     Parent p = Loader.getRoot();
     Stage stage = new Stage();
     stage.setScene(new Scene(p));
     stage.showAndWait();
     
       
   }
     
    @FXML
public void saveSetting(ActionEvent event) {
        
       this.setInitialSetting();
    
        if(original.isSelected())
            settings[3]=1;
        if(difference.isSelected())
            settings[4]=1;
        if(threshold.isSelected())
            settings[5]=1;
        if(grayscale.isSelected())
            settings[6]=1;
        if(contours.isSelected())
            settings[7]=1;
        
        settings[2]= Integer.parseInt(fpsTextField.getText());
        
        settings[1]= Integer.parseInt(camnoTextField.getText());
        
        settings[0]= (int) motionSense.getValue();
        
        //sending data back to the home page
        
         this.sendData();
     
          
    }
    
    @FXML
 public void resetSetting(ActionEvent event) {
        

     this.setInitialSetting();
     this.sendData();
       
    }
    
}
