import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;

public class KeyPressTest extends Application {
    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 400, 400);

        scene.setOnKeyPressed(event -> {
            System.out.println("Key Pressed: " + event.getCode());
            if (event.getCode() == KeyCode.ENTER) {
                System.out.println("Enter Key Detected");
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

