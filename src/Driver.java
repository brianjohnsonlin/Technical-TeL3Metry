import java.io.FileNotFoundException;

public class Driver {

    public static void main(String[] args) throws FileNotFoundException{
        TTL.instance = new TTL(4, 4);
    }

}
