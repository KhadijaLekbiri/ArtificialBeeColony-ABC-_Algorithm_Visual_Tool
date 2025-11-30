module com.example.abc_algorithm {
    requires javafx.controls;
    requires javafx.fxml;
//    requires com.example.abc_algorithm;


    opens com.example.abc_algorithm to javafx.fxml;
    exports com.example.abc_algorithm;
    exports com.example.abc_algorithm.algo;

}