module app.milestone {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;

    opens app to javafx.fxml;
    opens utils to javafx.base;

    exports app;
    exports controllers;
    opens controllers;
}
