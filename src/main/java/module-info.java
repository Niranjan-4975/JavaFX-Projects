module MaziBachatBank {
    requires java.sql;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive org.xerial.sqlitejdbc;

    opens main to javafx.fxml;
    opens Controllers to javafx.fxml;

    exports Controllers;
    exports main;
    exports Models;
    exports View;
}