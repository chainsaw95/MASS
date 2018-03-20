/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplicationopencv;

import com.jfoenix.controls.JFXButton;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
//import org.opencv.videoio.VideoCapture;
//import javafx.scene.image.Image;
/**
 *
 * @author Vivek
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label;
    
    
    @FXML
    private JFXButton startbutton;

    @FXML
    private JFXButton stopbutton;
    private ImageView imagevw;
    
    

        // a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that realizes the video capture
	private VideoCapture capture = new VideoCapture();
	// a flag to change the button behavior
	private boolean cameraActive = false;
	// the id of the camera to be used
	private static int cameraId = 0;
        BufferedImage out;
        
        Mat matrix=new Mat();
        
    public BufferedImage mat2BufferedImg(Mat in) {
        
        byte[] data = new byte[320 * 240 * (int) in.elemSize()];
        int type;
        in.get(0, 0, data);
        if (in.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        out = new BufferedImage(320, 240, type);
        out.getRaster().setDataElements(0, 0, 320, 240, data);
        return out;
    }
        
        
             
    @FXML
    private void startButton(ActionEvent event) {
        System.out.println("You clicked me!");
        
        
        if (!this.cameraActive)
		{
			// start the video capture
			this.capture.open(cameraId);
			
			// is the video stream available?
			if (this.capture.isOpened())
			{
				this.cameraActive = true;
				
                                if(capture.read(matrix)){
                                
                                BufferedImage bfimage=this.mat2BufferedImg(matrix);
                                Image image = SwingFXUtils.toFXImage(bfimage, null);
                                imagevw.setImage(image);
                                
                                  
                               }
                                  
                                
                        }
        
                }
        
    }
   
    @FXML
    private void stopButton(ActionEvent event){
        System.out.println("You stopped me ");
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
