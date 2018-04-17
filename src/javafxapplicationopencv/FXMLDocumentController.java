package javafxapplicationopencv;

import com.jfoenix.controls.JFXButton;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import static org.opencv.videoio.VideoWriter.fourcc;
import org.opencv.videoio.Videoio;
import sun.audio.*;

public class FXMLDocumentController {

    
    //helper onjects for fetching data from settings page
    private int motionSensitivity,fps,cameraNo;
    private boolean boriginal,bthreshold,bgrayscale,bcontours,bdifference;
    
    
    
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

    VideoWriter videoWriter;
    //opencv declarations
    VideoCapture capture;
    Mat webcamMatImage = new Mat();
    Image i1;
    

    //program variables
    boolean cameraActive;
    
    
    public void setInitialSettings(){
     motionSensitivity = 500;
       fps= 30;
       cameraNo = 0;
       
           boriginal=true;
           bdifference=false;
           bthreshold = false;
           bgrayscale= false;
           bcontours= false;
    }

    public void setSettings(int[] settings ){
       this.setInitialSettings();
       motionSensitivity = settings[0];
       fps= settings[1];
       cameraNo = settings[2];
       if(settings[3]==1)
           boriginal=true;
       if(settings[4]==1)
           bdifference=true;
       if(settings[5]==1)
           bthreshold = true;
       if(settings[6]==1)
           bgrayscale= true;
       if(settings[7]==1)
           bcontours= true;
    }
    

    public void alarm() throws Exception {
        InputStream in = new FileInputStream("./resources/A.wav");
        // create an audiostream from the inputstream
        AudioStream audioStream;
        audioStream = new AudioStream(in);
        // play the audio clip with the audioplayer class
        AudioPlayer.player.start(audioStream);

    }

 
    
    public void initialize() {
        this.setInitialSettings();
        this.capture = new VideoCapture();
        this.cameraActive = false;
       
        
    }
    
    private void preprocess() {

        Mat frame = new Mat(480,640,CvType.CV_8UC3,Scalar.all(127));
        Mat firstFrame = new Mat(480,640,CvType.CV_8UC3,Scalar.all(127));
        Mat gray = new Mat(480,640,CvType.CV_8UC3,Scalar.all(127));
        Mat frameDelta = new Mat(480,640,CvType.CV_8UC3,Scalar.all(127));
        Mat thresh = new Mat(480,640,CvType.CV_8UC3,Scalar.all(127));
        Mat originalframe = new Mat(480,640,CvType.CV_8UC3,Scalar.all(127));
        List<MatOfPoint> cnts = new ArrayList<>();

        if (!this.cameraActive) {
            // start the video capture
            this.capture.open(0);

            // is the video stream available?
            if (this.capture.isOpened()) {
                this.cameraActive = true;
               
                //Size frameSize = new Size((int) capture.get(Videoio.CAP_PROP_FRAME_WIDTH),(int) capture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
                
                videoWriter = new VideoWriter("test.avi", VideoWriter.fourcc('M', 'J','P','G'),20, frame.size(), true);
                if(!videoWriter.isOpened()){
                    System.out.println("Cannot open videwriter");
                }
               
 
                // grab a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = () -> {
                    int j = 0;

                    //convert to grayscale and set the first frame
                    while (true) {

                        capture.read(frame);
                        Imgproc.cvtColor(frame, firstFrame, Imgproc.COLOR_BGR2GRAY);
                        Imgproc.GaussianBlur(firstFrame, firstFrame, new Size(21, 21), 0);

                        capture.read(frame);
                        capture.read(originalframe);

                        //convert to grayscale
                        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
                        //show grayscale
                        if (bgrayscale) {

                            if (!gray.empty()) {
                                i1 = mat2Image(gray);
                                imagevw.setImage(i1);

                            }

                        }


                        Imgproc.GaussianBlur(gray, gray, new Size(21, 21), 0);
                        // show image
                        if (false) {
                            
                            if (!gray.empty()) {
                                i1 = mat2Image(gray);
                                imagevw.setImage(i1);

                            }

                        }

                       //compute difference between first frame and current frame
                        Core.absdiff(firstFrame, gray, frameDelta);
                       
                        if (bdifference) {

                            if (!frameDelta.empty()) {
                                i1 = mat2Image(frameDelta);
                                imagevw.setImage(i1);

                            }

                        }

                                 
                        Imgproc.threshold(frameDelta, thresh, 25, 255, Imgproc.THRESH_BINARY);

                        Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 2);

                        if (bthreshold) {

                            if (!thresh.empty()) {
                                i1 = mat2Image(thresh);
                                imagevw.setImage(i1);

                            }

                        }

                        
                        
                        Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                        for (int i = 0; i < cnts.size(); i++) {
                            if (Imgproc.contourArea(cnts.get(i)) > 500) {

                                if(boriginal){
                                if (!originalframe.empty()) {
                                    i1 = mat2Image(originalframe);
                                    imagevw.setImage(i1);
                                    //Imgcodecs.imwrite("pic.jph",frame);
                                }
                                }
                                videoWriter.write(originalframe);
                                
                                Imgproc.drawContours(frame, cnts, i, new Scalar(0, 255, 0), 2);
                                System.out.println("Motion detected:" + j);
                               
                                try {
                                    this.alarm();
                                } catch (Exception e) {
                                    System.out.println(e);
                                }
                                j++;

                                //to show contours image
                                if(bcontours){
                                if (!frame.empty()) {
                                    i1 = mat2Image(frame);
                                    imagevw.setImage(i1);
                                    //Imgcodecs.imwrite("pic.jph",frame);
                                }
                                }

                            }

                        }

                        cnts.clear();

                    }

                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 20, TimeUnit.MILLISECONDS);

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

       
        this.initialize();
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
    
    
    @FXML
    void onSettings(ActionEvent event) {

     try {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("videosettings.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root1));
                stage.setTitle("settings");
                stage.show();
        } catch(Exception e) {
           e.printStackTrace();
      }
    
    
    }

    

    @FXML
    void onVideos(ActionEvent event) {

    
    
    }

    

}
