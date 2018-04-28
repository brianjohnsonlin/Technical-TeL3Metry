import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Driver {

    public static void main(String[] args) throws FileNotFoundException{
        Pandemic.instance = new Pandemic(4, 4);
    }

}
