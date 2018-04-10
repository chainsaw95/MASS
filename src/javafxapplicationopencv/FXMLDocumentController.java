package javafxapplicationopencv;

import com.jfoenix.controls.JFXButton;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class FXMLDocumentController {

    @FXML
    private JFXButton startbutton;

    @FXML
    private ImageView imagevw;

    @FXML
    private JFXButton stopbutton;

    
    @FXML
    private JFXButton settings;

    
    @FXML
    private JFXButton videos;

    @FXML
    private JFXButton clock;

    
    private ScheduledExecutorService timer;

    //opencv declarations 
    VideoCapture capture;
    Mat webcamMatImage = new Mat();
    Image i1;
    
    //program variables
    boolean cameraActive;

    
    public void initialize() {
        this.capture = new VideoCapture();
        this.cameraActive = false;
        capture.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 1280);
        capture.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 720);
    }
    
   
    
    private void preprocess() {
        
         Mat frame = new Mat();
	 Mat firstFrame = new Mat();
	 Mat gray = new Mat();
	 Mat frameDelta = new Mat();
	 Mat thresh = new Mat();
	 List<MatOfPoint> cnts = new ArrayList<MatOfPoint>();
        
       
        if (!this.cameraActive) {
            // start the video capture
            this.capture.open(0);

            // is the video stream available?
            if (this.capture.isOpened()) {
                this.cameraActive = true;

                // grab a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = () -> {
                int j=0;
                
                //convert to grayscale and set the first frame
		
		while(true) {
                        
                        capture.read(frame);
                        Imgproc.cvtColor(frame, firstFrame, Imgproc.COLOR_BGR2GRAY);
                        Imgproc.GaussianBlur(firstFrame, firstFrame, new Size(21, 21), 0);
		
                        
                        
                        capture.read(frame);

                        //convert to grayscale
			Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
			Imgproc.GaussianBlur(gray, gray, new Size(21, 21), 0);
			
			//compute difference between first frame and current frame
			Core.absdiff(firstFrame, gray, frameDelta);
			Imgproc.threshold(frameDelta, thresh, 25, 255, Imgproc.THRESH_BINARY);
			
                        
                        
			Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 2);
			
                        
                        if(false){
                            
                            if (!thresh.empty()) {
                                    i1 = mat2Image(thresh);
                                    imagevw.setImage(i1);
                        
                            }
                               
                        }
                        
                     
                        Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

			for(int i=0; i < cnts.size(); i++) {
				if(Imgproc.contourArea(cnts.get(i)) > 500) {
			
                                    Imgproc.drawContours(frame, cnts, i,new Scalar(0,255,0) , 2);
                               
                                    System.out.println("Motion detected:"+j);
                                    j++;
                                  
                                    if (!frame.empty()) {
                                    i1 = mat2Image(frame);
                                    imagevw.setImage(i1);
                                    }
                              
                            }
         
                        }
                        
                        cnts.clear();
          
                    }
                          
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 60, TimeUnit.MILLISECONDS);

            } else {
                
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            this.cameraActive = false;
            this.stopAcquisition();
        }

    }
    
    
    
    
 
    
   // converting an opencv mat object to an image
    private static Image mat2Image(Mat frame) {
        try {
            return SwingFXUtils.toFXImage(matToBufferedImage(frame), null);
        } catch (Exception e) {
            System.err.println("Cannot convert the Mat obejct: " + e);
            return null;
        }
    }

    //called by opencv mat2Image for mat to bufferedImage
    private static BufferedImage matToBufferedImage(Mat original) {

        BufferedImage image = null;
        int width = original.width(), height = original.height(), channels = original.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        if (original.channels() > 1) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    
    }

    
    private void stopAcquisition() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                // stop the timer
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                // log any exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }
        if (this.capture.isOpened()) {
            // release the camera
            this.capture.release();
        }
    }

    private void start() {

        Image i1 = new Image("content/aa.jpg");

        imagevw.setImage(i1);

    }

    @FXML

    void startButton(ActionEvent event) {

        //  this.start();
        this.initialize();
       // this.motion();
        this.preprocess();

    }

    @FXML
    void stopButton(ActionEvent event) {

        this.stopAcquisition();
        
    }

    @FXML
    void fa1111(ActionEvent event) {

    }

    @FXML
    void db2f21(ActionEvent event) {

    }

}
