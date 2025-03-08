package View;
import javafx.util.Pair;
public class PairReturnMassaging {
    public String massage;
    public Double value;
    public Pair<String, Double> myMethod() {
        return new Pair<>(massage,value);
    }
}
