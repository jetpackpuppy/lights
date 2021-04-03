import javafx.application.*;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.*;
import java.io.*;
import java.util.*;

public class Lights extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        File folder = new File("files/");

        File[] charList = folder.listFiles(new FilenameFilter() {
            public boolean accept(File folder, String name) {
                return name.toLowerCase().endsWith(".mp4");
            }
        });

        Integer[] params = new Integer[charList.length];

        for (int i = 0; i < charList.length; i++) {
            if (charList[i].isFile()) {
                params[i] = Integer.parseInt(charList[i].getName().substring(0, 1));
            }
        }

        Stage externalStage = new Stage();
        Scene[] scenes = getScene(true, params);
        primaryStage.setScene(scenes[0]);
        primaryStage.setTitle("Crown Controller");
        primaryStage.setX(100);
        primaryStage.setY(300);
        primaryStage.show();

        externalStage.initStyle(StageStyle.UNDECORATED);
        externalStage.setScene(scenes[1]);
        externalStage.setX(0);
        externalStage.setY(0);
        externalStage.show();

        new Thread(() -> {
            while (true){
                Scene newScenes[] = getScene(false, params);
                Platform.runLater(() -> {
                    primaryStage.setScene(newScenes[0]);
                    primaryStage.show();
                    externalStage.setScene(newScenes[1]);
                    externalStage.show();
                });
            }
        }).start();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                Platform.exit();
                System.exit(0);
            }
        });

        externalStage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public Scene[] getScene(boolean init, Integer[] params){
        int num = 0;
        Scene[] scenes = new Scene[2];

        BorderPane internalRoot = new BorderPane();

        scenes[0] = new Scene(internalRoot, 800, 256);

        if (init == false) {
            Scanner input = new Scanner(System.in);
            System.out.println("Choose a crown face: ");

            try {
                num = input.nextInt();
            }
            catch (InputMismatchException e){
                System.out.print("[E]: Numbers only. ");
            }
            finally {
                boolean doesExist = Arrays.asList(params).contains(num);
                if (!doesExist){
                    System.out.print("[E]: " + num + ".mp4 does not exist. Reverting to default. ");
                    num = 0;
                }
            }
                
        }

        Media media = new Media(new File("files/" + num + ".mp4").toURI().toString());

        MediaPlayer player = new MediaPlayer(media);
        player.setAutoPlay(true);
        player.setCycleCount(MediaPlayer.INDEFINITE);
        MediaView internalMediaView = new MediaView(player);
        MediaView mediaView = new MediaView(player);
        Group root = new Group(mediaView);
        internalRoot.setCenter(internalMediaView);

        player.setOnReady(new Runnable(){
            @Override
            public void run() {
                if (media.getWidth() == 256){
                    MediaView mediaView2 = new MediaView(player);
                    MediaView mediaView3 = new MediaView(player);
                    MediaView mediaView4 = new MediaView(player);
                    mediaView2.setX(256);
                    root.getChildren().add(mediaView2);
                    mediaView3.setX(512);
                    root.getChildren().add(mediaView3);
                    mediaView4.setX(768);
                    root.getChildren().add(mediaView4);
                }
            }
        });
        
        scenes[1] = new Scene(root, 1024, 256);
        return scenes;
    }
}