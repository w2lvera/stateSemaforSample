package model.semafor;

import model.semafor.ColorEnum;
import model.graphicsModel.GraphicsModel;
import java.util.logging.Level;
import java.util.logging.Logger;
import static model.semafor.ColorEnum.GreenTYellowRed;
import static model.semafor.ColorEnum.GreenYellowTRed;
import static model.semafor.ColorEnum.TGreenYellowRed;

public class StateSemaphor implements Runnable {

    ChangeColor green;
    ChangeColor red;
    ChangeColor yellow;
    ChangeColor state;
    ChangeColor oldState;
    GraphicsModel gm;
    boolean suspendFlag = false;
    

    public StateSemaphor(GraphicsModel model) {
        green = new Green();
        red = new Red();
        yellow = new Yellow();
        state = green;
        oldState = green;
        gm = model;
        suspendFlag = false;
    }

    public void changeState() {
        gm.setColor(state.getColorEnum());
        try {
                Thread.sleep(state.count);
            } catch (InterruptedException ex) {
                Logger.getLogger(Green.class.getName()).log(Level.SEVERE, null, ex);
            }
        state.changeColor();
    }

    @Override
    public void run() {
        for (int i = 0; i < 200; i++) {
            changeState();
            stop();
        }

    }

    private void stop() {
        try {
            Thread.sleep(200);
            synchronized (this) {
                while (suspendFlag) {
                    wait();
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(StateSemaphor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void mysuspend() {
        suspendFlag = true;
    }

    public synchronized void myresume() {
        suspendFlag = false;
        notify();
    }

    public abstract class ChangeColor {
        public int count=500;
        ColorEnum colorEnum;

        public ColorEnum getColorEnum() {
            return colorEnum;
        }
        
        public abstract void changeColor();
    }

    public class Green extends ChangeColor {

        public Green() {
           colorEnum = TGreenYellowRed; 
        }

        @Override
        public void changeColor() {
            oldState = green;
            state = yellow;
        }
    }

    public class Red extends ChangeColor {

        public Red() {
            colorEnum = GreenYellowTRed; 
        }

        @Override
        public void changeColor() {
            oldState = red;
            state = yellow;
        }

    }

    public class Yellow extends ChangeColor {

        public Yellow() {
            count = 100;
            colorEnum = GreenTYellowRed; 
        }

        @Override
        public void changeColor() {
            if (oldState == red) {
                state = green;
                oldState = yellow;
            } 
            else {
                state = red;
                oldState = yellow;
            }
           
        }
    }
}
