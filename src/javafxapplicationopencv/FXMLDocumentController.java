/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplicationopencv;

import com.jfoenix.controls.JFXButton;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
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
import javafx.scene.image.WritableImage;
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
       
       public WritableImage capureSnapShot() {
       WritableImage WritableImage = null;

         // Instantiating the VideoCapture class (camera:: 0)
        VideoCapture capture = new VideoCapture(0);

       // Reading the next video frame from the camera
         Mat matrix = new Mat();
        capture.read(matrix);

      // If camera is opened
       if( capture.isOpened()) {
         // If there is next video frame
          if (capture.read(matrix)) {
            // Creating BuffredImage from the matrix
             BufferedImage image = new BufferedImage(matrix.width(), 
               matrix.height(), BufferedImage.TYPE_3BYTE_BGR);
            
            WritableRaster raster = image.getRaster();
            DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
            byte[] data = dataBuffer.getData();
            matrix.get(0, 0, data);
            //this.matrix = matrix;
            
            // Creating the Writable Image
            WritableImage = SwingFXUtils.toFXImage(image, null);
         }
      }
      return WritableImage;
    }
       
       
       
       
    @FXML
    private void startButton(ActionEvent event) {
     
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
