package tw.org.iii.android201920;

import java.io.Serializable;

public class Bike implements Serializable {
    public String name;
    public double speed;

    void upSpeed(){
        speed = speed<1? 1: speed*1.2;
    }

    public void setName(String name){
        this.name = name;
    }
}
