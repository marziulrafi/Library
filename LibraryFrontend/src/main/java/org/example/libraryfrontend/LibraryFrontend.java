package org.example.libraryfrontend;  // Match your package

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.util.Scanner;

public class LibraryFrontend extends Application {

    private ListView<String> bookList = new ListView<>();
    private TextField titleField = new TextField();
    private TextField authorField = new TextField();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("Book Library");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        titleField.setPromptText("Book Title");
        authorField.setPromptText("Author Name");

        Button addBtn = new Button("Add Book");
        Button refreshBtn = new Button("Refresh List");

        addBtn.setOnAction(e -> addBook());
        refreshBtn.setOnAction(e -> loadBooks());

        VBox root = new VBox(10,
                titleLabel, bookList,
                titleField, authorField,
                addBtn, refreshBtn);
        root.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(root, 400, 500);
        stage.setScene(scene);
        stage.setTitle("My Library App");
        stage.show();

        loadBooks(); // Load on start
    }

    private void loadBooks() {
        bookList.getItems().clear();
        try {
            URL url = new URL("http://localhost:8080/books");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) response.append(scanner.nextLine());
            scanner.close();

            JsonNode books = mapper.readTree(response.toString());
            for (JsonNode book : books) {
                String item = book.get("id") + ": " + book.get("title") + " by " + book.get("author");
                bookList.getItems().add(item);
            }
        } catch (Exception e) {
            bookList.getItems().add("Error: " + e.getMessage());
        }
    }

    private void addBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        if (title.isEmpty() || author.isEmpty()) {
            alert("Please fill both fields!");
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/books");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{\"title\":\"" + title + "\",\"author\":\"" + author + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            if (conn.getResponseCode() == 200) {
                titleField.clear();
                authorField.clear();
                loadBooks();
            }
        } catch (Exception e) {
            alert("Failed to add: " + e.getMessage());
        }
    }

    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}