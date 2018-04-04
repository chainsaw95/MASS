package javafxapplicationopencv;

import com.jfoenix.controls.JFXButton;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class FXMLDocumentController {

    @FXML
    private JFXButton startbutton;

    @FXML
    private ImageView imagevw;

    @FXML
    private JFXButton stopbutton;

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
    }

    private void preprocess() {

        if (!this.cameraActive) {
            // start the video capture
            this.capture.open(0);

            // is the video stream available?
            if (this.capture.isOpened()) {
                this.cameraActive = true;

                // grab a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = new Runnable() {

                    @Override
                    public void run() {
                        capture.read(webcamMatImage);

                        if (!webcamMatImage.empty()) {
                            i1 = mat2Image(webcamMatImage);
                            imagevw.setImage(i1);

                        }
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

            } else {
                // log the error
                System.err.println("Impossible to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            this.cameraActive = false;

            // stop the timer
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
