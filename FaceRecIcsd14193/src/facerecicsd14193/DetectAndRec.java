package facerecicsd14193;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.face.Face;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.face.FaceRecognizer;

/**
 *
 * @author giorgos
 */

//icsd14193
//Giorgos SYrrakos



public class DetectAndRec {

   

    // enas timer gia na travaw video me kapoio ms
    ScheduledExecutorService timer;
    // to ergaelio tou opencv to opoio travaei binteo
    VideoCapture capture;
    // ta flags ta opoia xreiazontai gia na allazw leitourgies 
    boolean flagcm;
    boolean UserFlag;
    JTextField newUserName;
    // face cascade classifiers
    CascadeClassifier faceCascade;
    CascadeClassifier cascadeEyeClassifier;
    //metavliti gia to megethos tou proswpou
    private int absFaceSize;

    public int index = 0;
    public int ind = 0;

   //to onmoma pou tha eisagei o xrhsth kata tin eggrafi
    String Username;
BufferedImage imge;
    Mat img;

    //ta onomata twn xrhstwn pou exounme apothikeysi
    public HashMap<Integer, String> onomata = new HashMap<Integer, String>();

    // tyxaios arithmos gia anagnwristiko
    public int random = (int) (Math.random() * 20 + 3);
//orisma twn vasikwn leitourgiwn
    public void init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        capture = new VideoCapture();
        this.faceCascade = new CascadeClassifier();
        cascadeEyeClassifier = new CascadeClassifier(
                "C:/Users/giorgos/Music/opencv/build/etc/haarcascades/haarcascade_eye.xml");
        this.absFaceSize = 0;

        setupModel();

        SetupFrame();
    }

   
    

    protected void startcam() {
        //kalw to setup gia ton classifier
        this.Setupclassifier("resources/lbpcascades/lbpcascade_frontalface.xml");
        if (this.flagcm) {
            System.out.println("mpike edv");
            //arxizw na travaw vindeo
            this.capture.open(0);

            //stin periptwsi pou einai anoixto ki mono proxwraw
            if (this.capture.isOpened()) {
                this.flagcm = true;

                // travaei  a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = new Runnable() {

                    @Override
                    public void run() {
                        //Image imageToShow = grabFrame();

                        imge = ConvertMat2Image(grab1Frame());
                        RefreshFrame(imge);
                       
                        System.out.println("mpike edv11");
                    }
                };
//to exoume sto 33 dioti bgainoun pio kathares oi eikones
                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

                
            } else {
                // log the error
                System.err.println("den anoigei h kamera");
            }
        } else {
            // i kamera den einai energi 
            this.flagcm = false;
            

            // stamataei ton timer
            try {
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
           
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }

            // kleinw tin cam
            this.capture.release();
            
            index = 0;
            Username = "";
        }
    }

//pairnw frames apo tin videocam 
    //ki epistrefw arxeio typou mat
    //==================================
    private Mat grab1Frame() {
        // analisw ta panta
        Image imageToShow = null;
        Mat frame = new Mat();

        // tsekarw an to capture einai anoixto
        if (this.capture.isOpened()) {
            try {
                // diavazw to frame
                this.capture.read(frame);
//stin periptwsi pou den einai null proswraw
                if (!frame.empty()) {
                    //kanw face   detection
                    this.detectAndDisplay(frame);

                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        return frame;
    }

    //==========================================
    private static BufferedImage ConvertMat2Image(Mat frame) {

        MatOfByte bytemat = new MatOfByte();

        Imgcodecs.imencode(".jpg", frame, bytemat);
        //dimiourgw to byter aaray to opoio einaia anagkaio gia na metaferw tin eikona se bufferdImage oste na tin topothetisw sto Jframe
        byte[] byteArray = bytemat.toArray();
        BufferedImage imgbuf = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            imgbuf = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return imgbuf;
    }
    JFrame frame1;
    JLabel lbl;
    ImageIcon icon;


    public void SetupFrame() {
        JPanel panel = new JPanel();
        JButton Start = new JButton("Start Cam");
        JButton Stop = new JButton("Stop Cam");
        JButton SignUp = new JButton("Sign Up");
        newUserName = new JTextField(10);

        //to koumpi gia tin eggrafi neou xrhsth
        SignUp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {

                UserFlag = true;
                Username = newUserName.getText();
                System.out.println(Username);

                System.out.println("mpike 128");
            }
        });
        Start.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                flagcm = true;
                startcam();
                System.out.println("mpike 128");
            }
        });
        Stop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                flagcm = false;
                UserFlag = false;
                startcam();
                System.out.println("mpike edv256");
            }
        });
        panel.add(SignUp);
        panel.add(newUserName);
        panel.add(Stop);
        panel.add(Start);

        frame1 = new JFrame();
        frame1.add(panel);
        frame1.setLayout(new FlowLayout());
        frame1.setSize(700, 600);
        frame1.setVisible(true);
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void RefreshFrame(java.awt.Image img2) {

        if (frame1 == null) {
            SetupFrame();
        }

        if (lbl != null) {
            frame1.remove(lbl);
        }
        icon = new ImageIcon(img2);
        lbl = new JLabel();
        lbl.setIcon(icon);
        frame1.add(lbl);
        //ananeosi frame
        frame1.revalidate();
    }
    //==========================================

    private void setupModel() {
        // diavazei ta arxeia apo ton fakelo mas
        File fi_le = new File("resources/trainingset/data/");
//elexos arxeiwn na teleiwnoun .png
        FilenameFilter filtimg = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".png");
            }
        };

        File[] fileimg = fi_le.listFiles(filtimg);

        List<Mat> images = new ArrayList<Mat>();

        System.out.println("mpike edw: ");

        List<Integer> trainingLabels = new ArrayList<>();

        Mat labels = new Mat(fileimg.length, 1, CvType.CV_32SC1);

        int counter = 0;

        for (File image : fileimg) {
            //pairnw tis eikones
            Mat img = Imgcodecs.imread(image.getAbsolutePath());
            //allazw se apoxrwsi tou gkri ki rithmizw to istograma
            Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(img, img);
            // pairnw to onom a tis eikonas
            int label = Integer.parseInt(image.getName().split("\\-")[0]);
            //topothetw to onoma se ena hashmap
            String labnname = image.getName().split("\\_")[0];
            String name = labnname.split("\\-")[1];
            onomata.put(label, name);
            // bazw tis eikones se mat
            images.add(img);

            labels.put(counter, 0, label);
            counter++;
        }
        //LBPH classifer
        FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
    
        //'mathenoume' ston lbph to proswpomas dinontas tou  enan arithmo fwto ki apothikeyi tis pliroforiew stos arxeio tranied 
        faceRecognizer.train(images, labels);
        faceRecognizer.save("tdata");
    }

    //methodos anagnwrishs
    // pairnei tin eikona apo tin webcam ki tin elegxei me to synolo eikonwn apo to fakelo mas
    //  an angnwristei emfanizei to onoma tou xrhsth
    private double[] faceRecognition(Mat currentFace) {

        // predict the label
        int[] predLabel = new int[1];
        double[] confidence = new double[1];
        int result = -1;

        FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
        faceRecognizer.load("tdata");
        faceRecognizer.predict(currentFace, predLabel, confidence);

        result = predLabel[0];

        return new double[]{result, confidence[0]};
    }
//methodos entopismou tou proswpou

    private void detectAndDisplay(Mat frame) {
        MatOfRect faces = new MatOfRect();//gia ta proswpa 
        MatOfRect eyes = new MatOfRect(); //matia

        Mat grayFrame = new Mat();

        // metropi se apoxrwsi tou gkri
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // isoropoume to istogramra oste na petyxoume kaluteri analisi
        Imgproc.equalizeHist(grayFrame, grayFrame);

        //upologizoume to mikrotero megethos prosopou gia tin periptwsi maw
        if (this.absFaceSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                this.absFaceSize = Math.round(height * 0.2f);
            }
        }

        // entopismos prosopwn
        this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absFaceSize, this.absFaceSize), new Size());

        // sxediazei tetragwna se kathe proswpo
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);

            //=========================
            cascadeEyeClassifier.detectMultiScale(grayFrame, eyes);
            for (Rect rect : eyes.toArray()) {

                Imgproc.putText(frame, "Eye", new Point(rect.x, rect.y - 5), 1, 2, new Scalar(0, 0, 255));
                //dimoirourgei se kathe mati ena pio mikro tetragwnaki
                Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(200, 200, 100), 2);
            }

            //kovei ta prosopa pou entopise
            Rect cropped = new Rect(facesArray[i].tl(), facesArray[i].br());
            Mat croppedimg = new Mat(frame, cropped);
            // ta allazei se apoxrwsi tou gkri
            Imgproc.cvtColor(croppedimg, croppedimg, Imgproc.COLOR_BGR2GRAY);
            // isoropei to istogramma
            Imgproc.equalizeHist(croppedimg, croppedimg);
            //megalwnei pali tin eikona sta kanonika epipeda
            Mat resizeimg = new Mat();
            Size size = new Size(250, 250);
            Imgproc.resize(croppedimg, resizeimg, size);

            // //chekarei an einai eggrafi neou xrhsth oste na apothikeysei to proswpou tou i apla einai entopismos 
            //
            if (UserFlag && !Username.isEmpty()) {
                if (index < 20) {
                    System.out.println("egine");
                    Imgcodecs.imwrite("resources/trainingset/data/"
                            + random + "-" + Username + "_" + (index++) + ".png", resizeimg);
                }
            }
//			int prediction = faceRecognition(resizeimg);

            double[] results = faceRecognition(resizeimg);
            double prediction = results[0];
            double confidence = results[1];

            System.out.println("H Provlepsi einai: " + prediction);
            int label = (int) prediction;
            String name = "";
            if (onomata.containsKey(label)) {
                name = onomata.get(label);
            } else {
                name = "Agnwstos";
            }

//           //dimiiourgei to koutaki pou tha anagraFEI panw prediction =onoma arithmos sigourias 
            String box_text = "Provlepsi = " + name + " Pososto sigourias = " + confidence;

            //upologizei pou tha topothetithei ayto to koutaki me ta stoixeia
            double pos_x = Math.max(facesArray[i].tl().x - 10, 0);
            double pos_y = Math.max(facesArray[i].tl().y - 10, 0);
            // ki to topothetei stin eikona
            Imgproc.putText(frame, box_text, new Point(pos_x, pos_y),
                    Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));

            img = frame;

        }
    }

    private void Setupclassifier(String classifierPath) {
        // fortwnw ton classifier(s)
        this.faceCascade.load(classifierPath);

    }

    public void start() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        init();

    }

}
