package xbrl.script;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private Stage primaryStage;
    private AnchorPane windowLayout;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Script");

        initWindowLayout();
    }

    private void initWindowLayout() {
        try{
            //Загружаем корневой макет из MainWindowLayout.fxml
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/MainWindowLayout.fxml"));
            windowLayout = (AnchorPane) loader.load();

            MainWindowController mainWindowController = loader.getController();
            mainWindowController.setMainApp(this);

            //Отображаем корневной макет на сцене
            Scene scene = new Scene(windowLayout);
            primaryStage.setScene(scene);

            primaryStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
