import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import java.io.IOException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;


public class Jordle extends Application {
    private int width = 800;
    private int height = 600;
    private static final int CELL_SIZE = 50;
    private Text[][] cellTexts = new Text[6][5];
    private Rectangle[][] cells = new Rectangle[6][5];


    private int currentRow = 0; //Used for registering the characters
    private int currentCol = 0; //Used for registering the characters
    private String currentGuess = new String();

    private StackPane stackPane;
    private Stage primaryStage;
    private Backend backend;
    private Label messageLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        welcomeScreen();
    }

    public void welcomeScreen() {
        //setting up the background image
        Image background = new Image(getClass().getResource("/jordleImage.jpg").toExternalForm());
        ImageView imageView = new ImageView(background);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setPreserveRatio(true);

        //setting up the label
        Label title = new Label("Jordle");
        title.setStyle("-fx-font-size: 40px; -fx-text-fill: white; -fx-font-weight: bold;");

        //setting up play button
        Button play = new Button("Play");
        Button cancel = new Button("Cancel");
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.getChildren().addAll(play, cancel);
        buttons.setPadding(new Insets(0, 0, 100, 0));

        //Buttons actions
        play.setOnAction(e -> gameScreen());
        cancel.setOnAction(e -> primaryStage.close());

        //putting title in vbox (so it is on the top)
        VBox titleLayout = new VBox(title);
        titleLayout.setAlignment(Pos.TOP_CENTER);
        titleLayout.setPadding(new Insets(100, 0, 0, 0));

        //creating a StackPane
        stackPane = new StackPane(imageView, titleLayout, buttons);

        //Setting up scene
        Scene scene = new Scene(stackPane, width, height);
        primaryStage.setTitle("Jordle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void gameScreen() {
        backend = new Backend();

        //messageLabel
        messageLabel = new Label("Try guessing a word!");
        messageLabel.setStyle(("-fx-font-size: 18px; -fx-text-fill: black; -fx-font-weight: bold;"));
        HBox messageBox = new HBox(messageLabel);
        messageBox.setPadding(new Insets(515, 0, 0, 40));

        Button instructions = new Button("Instructions");
        Button restart = new Button("Restart");

        //instructions and restart set on action implementations
        instructions.setOnAction(e -> {
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setHeaderText("How to play Jordle");
            infoAlert.setContentText("Welcome to Jordle! Guess the correct 5-letter word within 6 tries.\n"
                    + "Green: Correct letter in the correct position.\n"
                    + "Yellow: Correct letter in the wrong position.\n Grey: Letter not in the word. \n Good Luck! \n\t");
            infoAlert.showAndWait();
        });

        restart.setOnAction(e -> {
            gameScreen();
            backend.reset();
            currentRow = 0;
            currentCol = 0;
            currentGuess = new String();
        });

        //adding to Hbox Pane
        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.BOTTOM_CENTER);
        buttons.setPadding(new Insets(0, 0, 60, 0));
        buttons.getChildren().addAll(messageBox, instructions, restart);

        //gameScreen title
        Label title = new Label("Jordle");
        title.setStyle("-fx-font-size: 40px; -fx-text-fill: black; -fx-font-weight: bold;");
        VBox titleLayout = new VBox(title);
        titleLayout.setAlignment(Pos.TOP_CENTER);
        titleLayout.setPadding(new Insets(60, 0, 0, 0));

        //Creating Jordle Layout
        GridPane gPane = new GridPane();

        for (int row = 0; row < 6; row++) { // 6 rows
            for (int col = 0; col < 5; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE); // 5 columns
                cell.setFill(Color.WHITE);
                cell.setStroke(Color.BLACK);
                cells[row][col] = cell;

                Text text = new Text("");
                text.setFont(Font.font("Times New Roman",
                        FontWeight.BOLD, FontPosture.REGULAR, 20));
                text.setTextAlignment(TextAlignment.CENTER);
                cellTexts[row][col] = text;

                StackPane cellPane = new StackPane(cell, text); // Center the text within the rectangle
                gPane.add(cellPane, col, row);
                gPane.setHgap(5);
                gPane.setVgap(5);
            }
        }

        gPane.setAlignment(Pos.CENTER);

        stackPane = new StackPane(titleLayout, gPane, buttons);
        Scene scene = new Scene(stackPane, width, height);

        stackPane.setOnMouseClicked(e -> stackPane.requestFocus());
        stackPane.requestFocus();
        scene.setOnKeyPressed(this :: handleKeyPress);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleKeyPress(KeyEvent event) {
        // Handle alphabetic characters
        if (event.getCode().isLetterKey() && currentCol < 5) {
            char letter = event.getText().toUpperCase().charAt(0);
            cellTexts[currentRow][currentCol].setText(String.valueOf(letter));
            currentGuess += letter;
            currentCol++;
        }

        // Handle backspace
        else if (event.getCode() == KeyCode.BACK_SPACE && currentCol > 0) {
            currentCol--;
            currentGuess = currentGuess.substring(0, currentCol);
            cellTexts[currentRow][currentCol].setText("");
        }

        // Handle Enter key
        else if (event.getCode() == KeyCode.ENTER) {
            evaluateGuess();
        }
    }


    private void evaluateGuess() {
        String result = new String();

        try {
            result = backend.check(currentGuess);
            for (int i = 0; i < result.length(); ++i) {
                if (result.charAt(i) == 'g') {
                    cells[currentRow][i].setFill(Color.GREEN);
                } else if (result.charAt(i) == 'y') {
                    cells[currentRow][i].setFill(Color.YELLOW);
                } else {
                    cells[currentRow][i].setFill(Color.GRAY);
                }
            }

            if (result.equals("ggggg")) {
                messageLabel.setText("Congratulations, you have guessed the word!");

            } else if (currentRow == 5) {
                messageLabel.setText("Game over. The word was " + backend.getTarget().toUpperCase());
            } else {
                currentRow++;
                currentCol = 0;
                currentGuess = new String();
                messageLabel.setText("Try guessing a word!");
            }

        } catch (InvalidGuessException e) {
            Alert invalid = new Alert(Alert.AlertType.WARNING);
            invalid.setHeaderText("Invalid Guess");
            invalid.setContentText(e.getMessage());
            invalid.showAndWait();
        }
    }
}
