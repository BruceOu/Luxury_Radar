package haifanou.com.luxuryradar;

/**
 * Created by Weicheng Huang on 2017/4/9.
 */


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Shirt {

    public String shirtId;
    public String name;
    public String brand;
    public String price;
    public String img;

    public Shirt() {
        shirtId = "0";
        name = "";
        brand = "";
        price = "";
        img = "";
    }

    public Shirt(String id, String name, String brand, String price, String img) {
        this.shirtId = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.img = img;
    }
    public void save(){


    }

    public static Shirt[] read(){
        Shirt[] shirts = new Shirt[2];
        shirts[0] = new Shirt("359131344","fjaiwefn awef","afjwiejf","143","f23jf2");
        shirts[0] = new Shirt("359131344","fjaiwefn awef","afjwiejf","143","f23jf2");
        return shirts;
    }

}
