/**
   Name: Tony Nguyen
   Assignment: Final Project
   Due Date: Thursday, December 14, 11:59pm

   Grade report:

   Comments:
   no comment
   
   Total estimated grade: 100/100
 
 */

import javafx.application.*;
import javafx.stage.*;
import javafx.stage.FileChooser.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.beans.value.*;
import javafx.event.*; 
import javafx.animation.*;
import javafx.geometry.*;
import java.io.*;
import java.util.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.media.*;

/**
 *   
 *   JavaFX API: https://openjfx.io/javadoc/11/
 */ 
public class RhythmGame extends Application 
{
    //declare global variables
    int missed = 0;
    int perfect = 0;
    int good = 0;
    int bad = 0;
    int health = 0;
    int combo = 0;
    double FPS = 60.0;
    double score = 0.0;
    String result = "";
    
    double timeCounter = 0.0;
    double roundedTime = 0.0;
    double speedMod = 3.0;
    double volume = 0.2;
    
    int Q = 0;
    int W = 0;
    int O = 0;
    int P = 0;
    
    boolean Qable = true; 
    boolean Wable = true; 
    boolean Oable = true; 
    boolean Pable = true; 
    
    boolean autoPlay = false;
    
    String note1 = "Q";
    String note2 = "W";
    String note3 = "O";
    String note4 = "P";
    
    ArrayList<Sprite> NoteQList = new ArrayList<Sprite>();
    ArrayList<Sprite> NoteWList = new ArrayList<Sprite>();
    ArrayList<Sprite> NoteOList = new ArrayList<Sprite>();
    ArrayList<Sprite> NotePList = new ArrayList<Sprite>();
    
    GraphicsContext context;
    AudioClip music;
    AudioClip noteSfx;
    AudioClip missedSfx;
    AudioClip deadSfx;

    AnimationTimer timer;
    
    // KEEP TRACK OF ALL KEY NAMES PRESSED, STARTING THE INSTANT THEY ARE PRESSED
    public ArrayList<String> keyNameList;
    
    
    // run the application
    public static void main(String[] args) 
    {
        try
        {
            // creates Stage, calls the start method
            launch(args);
        }
        catch (Exception error)
        {
            error.printStackTrace();
        }
        finally
        {
            System.exit(0);
        }
    }

    // use when starting a new game
    public void reset(String name, double bpm)
    {
        timeCounter = 0.0;
        roundedTime = 0.0;
        
        NoteQList.clear();
        NoteWList.clear();
        NoteOList.clear();
        NotePList.clear();
        
        missed = 0;
        perfect = 0;
        good = 0;
        bad = 0;
        health = 100;
        combo = 0;
        result = "";
        
        double calDistance = 300.0 * speedMod * (1.0/((bpm * (60.0/FPS))  / 60.0));
        int distance = (int) Math.floor(calDistance);
        
        int x = 100;
        int y = 450;
        
        try {
        Scanner scanner = new Scanner(new File("map/"+name+".txt"));
        
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            char[] chara = line.toCharArray();
            x = 100;
            for (int i = 0; i < 4; i++) {

               if (chara[i] == '0') {
                   x += 100; 
               } else {
                   Sprite fallingNote = new Sprite(x, y, 80, 80, "img/clicked_note.png");
                   
                   switch (i) {
                       case 0:
                           NoteQList.add(fallingNote);
                           break;
                       case 1:
                           NoteWList.add(fallingNote);
                           break;
                       case 2:
                           NoteOList.add(fallingNote);
                           break;
                       case 3:
                           NotePList.add(fallingNote);
                           break;
                   }
                   
                   x += 100; 
                           }
                        }
            y -= distance;
        }

        scanner.close();
        
    } catch (FileNotFoundException e) {
            e.printStackTrace();
    }
    
    
    }
    
    public void checkNote(ArrayList<Sprite> noteList) {
        Sprite note = noteList.get(0);
        double distance = Math.abs(note.y + 40 - 490);
        
        if (distance < 20.0 * speedMod ) {
             perfect += 1;
             combo += 1;
             noteList.remove(note);
             result = "PERFECT!!";

        } else if (distance < 35.0 * speedMod ) {
            good += 1;
            combo += 1;
            noteList.remove(note);
            result = "GOOD!";

        } else if (distance < 70.0 * speedMod ) {
            bad += 1;
            combo += 1;
            noteList.remove(note);
            result = "BAD";

        } else {
            noteSfx.play();

        }
    }
    
    public boolean autoPlay(ArrayList<Sprite> noteList, Sprite note) {
        if (note.y >= 450 && autoPlay) {
            perfect += 1;
            result = "PERFECT!";
            noteSfx.play();
            noteList.remove(note);
            combo += 1;
            return true;
        } else {
            return false;
        } 
    }
    
    // Application is an abstract class,
    //  requires the method: public void start(Stage s)
    public void start(Stage mainStage) 
    {
        // set the text that appears in the title bar
        mainStage.setTitle("Cat Rhythm Game");
        mainStage.setResizable(true);

        // layout manager: organize window contents
        BorderPane root = new BorderPane();

        // set font size of objects
        root.setStyle(  "-fx-font-size: 18;"  );

        // May want to use a Box to add multiple items to a region of the screen
        VBox box = new VBox();
        // add padding/margin around area
        box.setPadding( new Insets(16) );
        // add space between objects
        box.setSpacing( 16 );
        // set alignment of objects (default: Pos.TOP_LEFT)
        box.setAlignment( Pos.CENTER );
        // Box objects store contents in a list
        List<Node> boxList = box.getChildren();
        // if you choose to use this, add it to one of the BorderPane regions

        // Scene: contains window content
        // parameters: layout manager; width window; height window
        Scene mainScene = new Scene(root);
        // attach/display Scene on Stage (window)
        mainStage.setScene( mainScene );

        // custom application code below -------------------

        // set menu bar and menu items
        MenuBar menuBar = new MenuBar();
        root.setTop(menuBar);
        
        Menu helpMenu = new Menu("Menu");
        menuBar.getMenus().add( helpMenu );
        
        Menu songMenu = new Menu("Select Song");
        helpMenu.getItems().add(songMenu);
        
        MenuItem easy = new MenuItem("Easy Song");
        MenuItem medium = new MenuItem("Medium Song");
        MenuItem hard = new MenuItem("Hard Song");
        
        MenuItem settingMenu = new MenuItem("Settings");
        MenuItem about    = new MenuItem("About");
        MenuItem quit     = new MenuItem("Quit");
        
        songMenu.getItems().add( easy );
        songMenu.getItems().add( medium );
        songMenu.getItems().add( hard );
        
        helpMenu.getItems().add( settingMenu );
        helpMenu.getItems().add( about );
        helpMenu.getItems().add( quit );
        
        Canvas canvas = new Canvas(1000,600);
        GraphicsContext context = canvas.getGraphicsContext2D();

        root.setCenter( canvas );
        
        // Dedicated Setting Menu
        VBox settingPage = new VBox();
        settingPage.setSpacing( 16 );
        HBox row1 = new HBox();
        HBox row2 = new HBox();
        HBox row3 = new HBox();
        HBox row4 = new HBox();
        HBox row5 = new HBox();
        HBox row6 = new HBox();
        HBox row7 = new HBox();
        HBox row8 = new HBox();
        HBox row9 = new HBox();
        
        Label inputQ = new Label("Input 1: ");
        Label inputW = new Label("Input 2: ");
        Label inputO = new Label("Input 3: ");
        Label inputP = new Label("Input 4: ");
        
        TextField setQ = new TextField(note1);
        TextField setW = new TextField(note2);
        TextField setO = new TextField(note3);
        TextField setP = new TextField(note4);
        
        row1.getChildren().addAll(inputQ,setQ);
        row2.getChildren().addAll(inputW,setW);
        row3.getChildren().addAll(inputO,setO);
        row4.getChildren().addAll(inputP,setP);
        
        Label volumeLabel = new Label("Volume: ");
        Slider volumeSlider = new Slider();
        volumeSlider.setMin(0);
        volumeSlider.setMax(100);
        volumeSlider.setValue(volume*100);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(20);
        volumeSlider.setMinorTickCount(10);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setPrefWidth(800);
        row5.getChildren().addAll(volumeLabel,volumeSlider);
        
        Label speedLabel = new Label("Scroll Speed: ");
        Slider speedSlider = new Slider();
        speedSlider.setMin(100);
        speedSlider.setMax(500);
        speedSlider.setValue(300);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(50);
        speedSlider.setShowTickLabels(true);
        speedSlider.setPrefWidth(800);
        row7.getChildren().addAll(speedLabel,speedSlider);
        
        Button saveButton = new Button("Save");
        Button backButton = new Button("Back");
        row8.getChildren().addAll(backButton,saveButton);
        
        CheckBox autoPlayBox = new CheckBox("Auto Play  ");
        CheckBox HiddenBox = new CheckBox("Hidden Mode");
        
        row9.getChildren().addAll(autoPlayBox, HiddenBox);
        
        Label fpsLabel = new Label("Refresh Rate: ");
        TextField setFps = new TextField(FPS+"");
        
        row6.getChildren().addAll(fpsLabel, setFps);
        
        Label cautionLabel = new Label("*Music will only sync up with chart when the monitor refresh rate is 60Hz");
        
        settingPage.getChildren().addAll(row1,row2,row3,row4,row5,row7,row6,row9,row8, cautionLabel);
        
        //loading image assets
        
        Image cat = new Image("img/base.png", 400,226, false, true);
        Image cat2 = new Image("img/base2.png", 400,226, false, true);
        Image cat3 = new Image("img/base3.png", 400,226, false, true);
        Image catLeft = new Image("img/base_right.png", 400,226, false, true);
        Image catRight = new Image("img/base_left.png", 400,226, false, true);
        Image cat1000 = new Image("img/base_1000.png", 400,226, false, true);
        Image cat0100 = new Image("img/base_0100.png", 400,226, false, true);
        Image cat1100 = new Image("img/base_1100.png", 400,226, false, true);
        Image cat0001 = new Image("img/base_0001.png", 400,226, false, true);
        Image cat0010 = new Image("img/base_0010.png", 400,226, false, true);
        Image cat0011 = new Image("img/base_0011.png", 400,226, false, true);

        Image note = new Image("img/note.png", 80, 80, false, true);
        Image PressedNote = new Image("img/clicked_note.png", 80, 80, false, true);
        
        //loading audios
        File testFile = new File("audio/menu.wav");
        File clickFile = new File("audio/click.wav");
        File missedFile = new File("audio/missed.wav");
        File deadFile = new File("audio/dead.wav");

        music = new AudioClip( testFile.toURI().toString() );
        music.play(0.2);
        
        noteSfx = new AudioClip( clickFile.toURI().toString() );
        noteSfx.setVolume(0.2);
        missedSfx = new AudioClip( missedFile.toURI().toString() );
        missedSfx.setVolume(1);
        deadSfx = new AudioClip( deadFile.toURI().toString() );
        deadSfx.setVolume(0.5);
        
        
        // draw homescreen
        context.setFill(Color.DARKGREY);
        context.fillRect(0,0, 1000,600);
        context.setFill(Color.BLACK);
        context.fillRect(40,0, 500,600);
        context.setFill(Color.WHITE);
        context.fillRect(50,0, 480,600);
        
        context.setFill(Color.BLACK);
        context.fillRect(560,290, 420,246);
        
        context.setFont( new Font("Impact", 100) );
        context.fillText("RHYTHM", 100, 200);
        context.fillText("CAT GAME", 100, 300);
                    
        context.drawImage(cat, 570,300);
        context.drawImage(note, 100,450);
        context.drawImage(note, 200,450);
        context.drawImage(note, 300,450);
        context.drawImage(note, 400,450);
        
        // initialize key name list
        keyNameList = new ArrayList<String>();
        
        // animation timer redraws the game 60 times per second
        timer = new AnimationTimer()
            {
                public void handle(long nanoSeconds)
                {                
                    // clear the canvas
                    context.setFill(Color.DARKGREY);
                    context.fillRect(0,0, 1000,600);
                    context.setFill(Color.BLACK);
                    context.fillRect(40,0, 500,600);
                    context.setFill(Color.WHITE);
                    context.fillRect(50,0, 480,600);
                    
                    //clear input
                    int Q = 0;
                    int W = 0;
                    int O = 0;
                    int P = 0;
                    
                    //draw background
                    context.setFill(Color.BLACK);
                    context.fillRect(560,290, 420,246);

                    if (health <= 50) {
                        context.drawImage(cat2, 570,300);
                    } else {
                        context.drawImage(cat, 570,300);
                    }
                    
                    context.drawImage(note, 100,450);
                    context.drawImage(note, 200,450);
                    context.drawImage(note, 300,450);
                    context.drawImage(note, 400,450);

                    
                    // input solver and visual
                    if (keyNameList.contains(note1)) {
                        Q = 1;
                        context.drawImage(PressedNote, 100,450);
                        
                        if (Qable) {
                            checkNote(NoteQList);
                            Qable = false;
                        }
                    } else {
                        
                            Qable = true;
                
                    }

                    if (keyNameList.contains(note2)){
                        W = 10;
                        context.drawImage(PressedNote, 200,450);
                        
                        if (Wable) {
                            checkNote(NoteWList);         
                            Wable = false;
                        }
                    } else {
                
                            Wable = true;
                    
                    }
                        
                    if (keyNameList.contains(note3)){
                        O = 100;
                        context.drawImage(PressedNote, 300,450);
                        
                        if (Oable) {
                            checkNote(NoteOList);
                            Oable = false;
                        }
                    } else {
           
                            Oable = true;
                
                    }
                        
                    if (keyNameList.contains(note4)){
                        P = 1000;
                        context.drawImage(PressedNote, 400,450);
                        
                        if (Pable) {
                            checkNote(NotePList);
                            Pable = false;
                        }
                    } else {
                   
                            Pable = true;
                
                    }    
                    
                    
                    // falling note code
                    
                    for (int i = 0; i < NoteQList.size(); i++)
                    {
                        Sprite note = NoteQList.get(i);   
                        note.draw( context );              
                        //falling notes
                        if (note.y > 600) {
                            NoteQList.remove(note);
                            missed += 1;
                            health -= 20;
                            combo = 0;
                            result = "MISSED";
                            missedSfx.play();
                        }
                        if (autoPlay(NoteQList,note)) {
                            Q = 1;
                        }
                       
                        note.move( 0, 5.0 * speedMod );
                     }
                    for (int i = 0; i < NoteWList.size(); i++)
                    {
                        Sprite note = NoteWList.get(i);   
                        note.draw( context );              
                        //falling notes
                        if (note.y > 600) {
                            NoteWList.remove(note);
                            missed += 1;
                            health -= 20;
                            combo = 0;
                            result = "MISSED";
                            missedSfx.play();
                        }
                        if (autoPlay(NoteWList,note)) {
                            W = 10;
                        }
        
                        note.move( 0, 5.0 * speedMod );
                     }
                    for (int i = 0; i < NoteOList.size(); i++)
                    {
                        Sprite note = NoteOList.get(i);   
                        note.draw( context );              
                        //falling notes
                        if (note.y > 600) {
                            NoteOList.remove(note);
                            missed += 1;
                            health -= 20;
                            combo = 0;
                            result = "MISSED";
                            missedSfx.play();
                        }
                        if (autoPlay(NoteOList,note)) {
                            O = 100;
                        }
                        
                        note.move( 0, 5.0 * speedMod );
                     }
                    for (int i = 0; i < NotePList.size(); i++)
                    {
                        Sprite note = NotePList.get(i);   
                        note.draw( context );              
                        //falling notes
                        if (note.y > 600) {
                            NotePList.remove(note);
                            missed += 1;
                            health -= 20;
                            combo = 0;
                            result = "MISSED";
                            missedSfx.play();
                        }
                        if (autoPlay(NotePList,note)) {
                            P = 1000;
                        }
                       
                        note.move( 0, 5.0 * speedMod );
                     }
                    
                    // hidden mode code
                    
                    if (HiddenBox.isSelected()) {
                        context.setFill(Color.BLACK);
                        context.fillRect(40,400, 500,200);
                    }
                     
                    // cat animation
                    int QW = Q + W;
                    int OP = O + P;
                    
                    switch (QW) {
                        case 1:
                            context.drawImage(cat0001, 570,300);
                            break;
                        case 10:
                            context.drawImage(cat0010, 570,300);
                            break;
                        case 11:
                            context.drawImage(cat0011, 570,300);
                            break;
                        default:
                            context.drawImage(catLeft, 570,300);
                    }
                    
                    switch (OP) {
                        case 1000:
                            context.drawImage(cat1000, 570,300);
                            break;
                        case 100:
                            context.drawImage(cat0100, 570,300);
                            break;
                        case 1100:
                            context.drawImage(cat1100, 570,300);
                            break;
                        default:
                            context.drawImage(catRight, 570,300);
                    } 
                     
                    // drawing text
                         
                    context.setStroke(Color.BLACK);
                    context.setLineWidth(10);
                    
                    context.setFont( new Font("Impact", 40) );
                    context.strokeText("Health: " + health, 650, 580); 
                    context.strokeText("Missed: " + missed, 650, 50);
                    context.strokeText("Bad: " + bad, 650, 100);
                    context.strokeText("Good: " + good, 650, 150);
                    context.strokeText("Perfect: " + perfect, 650, 200); 
                    context.setFont( new Font("Impact", 50) );
                    context.strokeText(result, 670, 270); 
                    context.strokeText(combo+"", 270, 200);
                    
                    context.setFill(Color.WHITE);
                    
                    context.setFont( new Font("Impact", 40) );    
                    context.fillText("Health: " + health, 650, 580); 
                    context.fillText("Missed: " + missed, 650, 50);
                    context.fillText("Bad: " + bad, 650, 100);
                    context.fillText("Good: " + good, 650, 150);
                    context.fillText("Perfect: " + perfect, 650, 200); 
                    context.setFont( new Font("Impact", 50) );
                    context.fillText(result, 670, 270);
                    context.fillText(combo+"", 270, 200);
                    
                    if (health <=0) {
                        timer.stop();
                        context.setFill(Color.BLACK);
                        context.fillRect(50,0, 480,600);
                        context.drawImage(cat3, 570,300);
                        
                        context.setFill(Color.WHITE);
                        context.setFont( new Font("Impact", 80) );
                        context.fillText("YOU FAILED", 100, 200);
                        music.stop();
                        deadSfx.play();
                    }
                    
                    if (NoteQList.size() == 0 && NoteWList.size() == 0 && NoteOList.size() == 0 && NotePList.size() == 0) {
                        timer.stop();
                        context.setFill(Color.BLACK);
                        context.fillRect(50,0, 480,600);
                        
                        score = (perfect*100 + good*90 + bad*80 + missed*50)/(perfect + good + bad + missed);
                        
                        String processedScore = String.format("%,.2f",score);
                        
                        String grade;
                        
                        if (score > 95) {
                            grade = "S";
                        } else if (score > 90) {
                            grade = "A";
                        } else if (score > 80) {
                            grade = "B";
                        } else if (score > 70) {
                            grade = "C";
                        } else if (score > 60) {
                            grade = "D";
                        } else {
                            grade = "F";
                        }
                        
                        context.setFill(Color.WHITE);
                        context.setFont( new Font("Impact", 80) );
                        context.fillText(grade, 100, 200);
                        context.fillText(processedScore, 100, 300);
                        }
                        
                    timeCounter += 1.0/60.0;
                    roundedTime = Math.round(timeCounter * 100.0) / 100.0;
                }
            };

        
        // add a key press event listener to move the kitten
        
        mainScene.setOnKeyPressed(
            (event) ->
            {
                String key = event.getCode().toString();
                
                if ( !keyNameList.contains(key) )
                    keyNameList.add(key);
            }
        );
        
        // also need to remove key names from list when key is released
        mainScene.setOnKeyReleased(
            (event) ->
            {
                String key = event.getCode().toString();
                
                keyNameList.remove(key);
            }
        );
                
        easy.setOnAction(
            (event) ->
            {   
                
                music.stop();
                reset("map1", 138.0);
                root.setCenter( canvas );
                File easyFile = new File("audio/easy.wav");
                music = new AudioClip( easyFile.toURI().toString() );
                timer.start();
                music.play(volume);
            }
        );
        
        medium.setOnAction(
            (event) ->
            {
                
                music.stop();
                reset("map2", 584.0);
                root.setCenter( canvas );
                File medFile = new File("audio/med.wav");
                music = new AudioClip( medFile.toURI().toString() );
                timer.start();
                music.play(volume);
            }
        );
        
        hard.setOnAction(
            (event) ->
            {   
                
                music.stop();
                reset("map3", 760.0);
                root.setCenter( canvas );
                File hardFile = new File("audio/hard.wav");
                music = new AudioClip( hardFile.toURI().toString() );
                timer.start();
                music.play(volume);
            }
        );
        
        
        settingMenu.setOnAction(
            (event) ->
            {
                root.setCenter( settingPage );
            }
        );
        
        backButton.setOnAction(
            (event) ->
            {
                root.setCenter( canvas );
            }
        );
        
        saveButton.setOnAction(
            (event) ->
            {
                note1 = setQ.getText().toUpperCase();
                note2 = setW.getText().toUpperCase();
                note3 = setO.getText().toUpperCase();
                note4 = setP.getText().toUpperCase();
                
                FPS = Double.parseDouble(setFps.getText());
                
                volume = volumeSlider.getValue() * 0.01;
                speedMod = speedSlider.getValue() * 0.01 * (60.0/FPS);
                
                if (autoPlayBox.isSelected()) {
                    autoPlay = true;
                } else {
                    autoPlay = false;
                }
            }
        );
        
        about.setOnAction(
            (event) ->
            {
                Alert info = new Alert(AlertType.INFORMATION);
                info.setHeaderText(null);
                info.setContentText("Cat Rythm Game\n Created by Tony Nguyen");
                info.showAndWait();
            }
        );
        
        quit.setOnAction(
            (event) ->
            {
                mainStage.close();
            }
        );
        
        
        
        // custom application code above -------------------


        
        // after adding all content, make the Stage visible
        mainStage.show();
        mainStage.sizeToScene();
    }
    
}