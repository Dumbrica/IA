package Genetic;

import impl.Point;
import impl.UIConfiguration;
import interf.IPoint;
import interf.IUIConfiguration;

import java.util.Random;

public class Gene {
    private IPoint point;
    private UIConfiguration uiConf;

    public Gene(IPoint point, UIConfiguration uiConf) {
        this.point = point;
        this.uiConf = uiConf;
    }

    public Gene(UIConfiguration uiConf) {
        this.uiConf = uiConf;
        this.point = createPoint(uiConf);

    }

    private IPoint createPoint(IUIConfiguration uiConf){
        int width = uiConf.getWidth();
        int heigth = uiConf.getHeight();

        IPoint point = new Point((new Random().nextInt((width - 0) + 1) + 0),(new Random().nextInt((heigth - 0) + 1) + 0));

        return point;
    }

    public IPoint getPoint() {
        return point;
    }
}
