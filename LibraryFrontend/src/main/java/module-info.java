module org.example.libraryfrontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    opens org.example.libraryfrontend to javafx.fxml;
    exports org.example.libraryfrontend;
}