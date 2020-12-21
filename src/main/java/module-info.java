module org.schumiwildeprojects {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.schumiwildeprojects.kck2 to javafx.fxml;
    exports org.schumiwildeprojects.kck2;
}