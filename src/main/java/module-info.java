module org.schumiwildeprojects {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.schumiwildeprojects to javafx.fxml;
    exports org.schumiwildeprojects;
}