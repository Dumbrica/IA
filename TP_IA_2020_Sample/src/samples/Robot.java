package samples;

import Genetic.AG;
import Genetic.ConfAG;
import hex.genmodel.MojoModel;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;
import hex.genmodel.easy.prediction.BinomialModelPrediction;
import impl.Point;
import impl.UIConfiguration;
import interf.IPoint;
import performance.EvaluateFire;
import robocode.*;
import utils.Utils;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Robot extends AdvancedRobot {
    private class Dados{
        String nome;

        public Dados(String nome) {
            this.nome = nome;
        }
    }
    EvaluateFire ef;
    private List<Rectangle> obstacles;
    public static UIConfiguration conf;
    private List<IPoint> points;
    private HashMap<String, Rectangle> inimigos; //utilizada par associar inimigos a retângulos e permitir remover retângulos de inimigos já desatualizados

    //variável que contém o ponto atual para o qual o robot se está a dirigir
    private int currentPoint = -1;
    HashMap<Bullet, Dados> balasNoAr = new HashMap<>();
    EasyPredictModelWrapper model;
    @Override
    public void run()
    {
        super.run();
        ef = new EvaluateFire("omae wa mou shindeiru");
        try {
            model=new EasyPredictModelWrapper(MojoModel.load("H2oModels/gbm_94ce1cbd_1e62_481b_976a_68fd4f94b301.zip"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        obstacles = new ArrayList<>();
        inimigos = new HashMap<>();
        conf = new UIConfiguration((int) getBattleFieldWidth(), (int) getBattleFieldHeight() , obstacles);
        while(true){
            /*setAhead(100);
            setTurnLeft(100);*/
            setTurnGunLeft(360);
            //se se está a dirigir para algum ponto
            if (currentPoint >= 0)
            {
                IPoint ponto = points.get(currentPoint);
                //se já está no ponto ou lá perto...
                if (Utils.getDistance(this, ponto.getX(), ponto.getY()) < 2){
                    currentPoint++;
                    //se chegou ao fim do caminho
                    if (currentPoint >= points.size())
                        currentPoint = -1;
                }

                advancedRobotGoTo(this, ponto.getX(), ponto.getY());
            }

            this.execute();
        }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        super.onMouseClicked(e);

        conf.setStart(new Point((int) this.getX(), (int) this.getY()));
        conf.setEnd(new Point(e.getX(), e.getY()));

        /*
         * TODO: Implementar a chamada ao algoritmo genético!
         *
         * */
        AG ag = new AG(conf);

        try {
            ag.run();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        points = ag.getPoints();


        for (int i=0;i<points.size();i++) {
            //Utils.robotGoTo(this, points.get(i).getX(), points.get(i).getY());
            advancedRobotGoTo(this, points.get(i).getX(), points.get(i).getY());
        }

        currentPoint = 0;
    }

    /**
     * ******** TODO: Necessário selecionar a opção Paint na consola do Robot *******
     * @param g
     */
    @Override
    public void onPaint(Graphics2D g) {
        super.onPaint(g);

        g.setColor(Color.RED);
        obstacles.stream().forEach(x -> g.drawRect(x.x, x.y, (int) x.getWidth(), (int) x.getHeight()));

        if (points != null)
        {
            for (int i=1;i<points.size();i++)
                drawThickLine(g, points.get(i-1).getX(), points.get(i-1).getY(), points.get(i).getX(), points.get(i).getY(), 2, Color.green);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);
        ef.addScanned(event);

        RowData row= new RowData();
        row.put("Robot",event.getName());
        row.put("Distancia",event.getDistance());
        row.put("Velocidade",event.getVelocity());

        BinomialModelPrediction p= null;
        try {
            p = model.predictBinomial(row);
        } catch (PredictException e) {
            e.printStackTrace();
        }
        if(p.classProbabilities[1]>0.60){
            Bullet b = this.fireBullet(3);
            if(b!=null){
                System.out.println(event.getName()+"->"+Arrays.toString(p.classProbabilities));
                balasNoAr.put(b, new Dados(event.getName()));
            }
        }
        else if(p.classProbabilities[1]>0.70){


            Bullet b = this.fireBullet(4);
            if(b!=null){
                System.out.println(event.getName()+"->"+Arrays.toString(p.classProbabilities));
                balasNoAr.put(b, new Dados(event.getName()));
            }

        }
        else if(p.classProbabilities[1]>0.80){


            Bullet b = this.fireBullet(6);
            if(b!=null){
                System.out.println(event.getName()+"->"+Arrays.toString(p.classProbabilities));
                balasNoAr.put(b, new Dados(event.getName()));
            }

        }
        else if(p.classProbabilities[1]>0.90){


            Bullet b = this.fireBullet(8);
            if(b!=null){
                System.out.println(event.getName()+"->"+Arrays.toString(p.classProbabilities));
                balasNoAr.put(b, new Dados(event.getName()));
            }

        }




        Point2D.Double ponto = getEnemyCoordinates(this, event.getBearing(), event.getDistance());
        ponto.x -= this.getWidth()*2.5 / 2;
        ponto.y -= this.getHeight()*2.5 / 2;

        Rectangle rect = new Rectangle((int)ponto.x, (int)ponto.y, (int)(this.getWidth()*2.5), (int)(this.getHeight()*2.5));

        if (inimigos.containsKey(event.getName())) //se já existe um retângulo deste inimigo
            obstacles.remove(inimigos.get(event.getName()));//remover da lista de retângulos

        obstacles.add(rect);
        inimigos.put(event.getName(), rect);
        //conf.setObstacles(obstacles);

        //System.out.println("Enemies at:");
        //obstacles.forEach(x -> System.out.println(x));
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        super.onBulletHit(event);
        ef.addHit(event);
        Dados d = balasNoAr.get(event.getBullet());
            //testar se acertei em quem era suposto
            if (event.getName().equals(event.getBullet().getVictim()))
                System.out.println("acertou");

        balasNoAr.remove(event.getBullet());
    }

    /**
     * Devolve as coordenadas de um alvo
     *
     * @param robot o meu robot
     * @param bearing ângulo para o alvo, em graus
     * @param distance distância ao alvo
     * @return coordenadas do alvo
     * */
    public static Point2D.Double getEnemyCoordinates(robocode.Robot robot, double bearing, double distance){
        double angle = Math.toRadians((robot.getHeading() + bearing) % 360);

        return new Point2D.Double((robot.getX() + Math.sin(angle) * distance), (robot.getY() + Math.cos(angle) * distance));
    }


    private void drawThickLine(Graphics g, int x1, int y1, int x2, int y2, int thickness, Color c) {

        g.setColor(c);
        int dX = x2 - x1;
        int dY = y2 - y1;

        double lineLength = Math.sqrt(dX * dX + dY * dY);

        double scale = (double) (thickness) / (2 * lineLength);

        double ddx = -scale * (double) dY;
        double ddy = scale * (double) dX;
        ddx += (ddx > 0) ? 0.5 : -0.5;
        ddy += (ddy > 0) ? 0.5 : -0.5;
        int dx = (int) ddx;
        int dy = (int) ddy;

        int xPoints[] = new int[4];
        int yPoints[] = new int[4];

        xPoints[0] = x1 + dx;
        yPoints[0] = y1 + dy;
        xPoints[1] = x1 - dx;
        yPoints[1] = y1 - dy;
        xPoints[2] = x2 - dx;
        yPoints[2] = y2 - dy;
        xPoints[3] = x2 + dx;
        yPoints[3] = y2 + dy;

        g.fillPolygon(xPoints, yPoints, 4);
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        super.onRobotDeath(event);

        Rectangle rect = inimigos.get(event.getName());
        obstacles.remove(rect);
        inimigos.remove(event.getName());

    }


    /**
     * Dirige o robot (AdvancedRobot) para determinadas coordenadas
     *
     * @param robot o meu robot
     * @param x coordenada x do alvo
     * @param y coordenada y do alvo
     * */
    public static void advancedRobotGoTo(AdvancedRobot robot, double x, double y) {
        x -= robot.getX();
        y -= robot.getY();

        double angleToTarget = Math.atan2(x, y);
        double targetAngle = robocode.util.Utils.normalRelativeAngle(angleToTarget - Math.toRadians(robot.getHeading()));
        double distance = Math.hypot(x, y);
        double turnAngle = Math.atan(Math.tan(targetAngle));
        robot.setTurnRight(Math.toDegrees(turnAngle));
        if (targetAngle == turnAngle)
            robot.setAhead(distance);
        else
            robot.setBack(distance);
        robot.execute();
    }
    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        super.onBattleEnded(event);
        ef.submit(event.getResults());
    }
    /*
    public static void advancedRobotGoTo(robocode.Robot robot, double x, double y)
    {
        x -= robot.getX();
        y -= robot.getY();

        double angleToTarget = Math.atan2(x, y);
        double targetAngle = robocode.util.Utils.normalRelativeAngle(angleToTarget - Math.toRadians(robot.getHeading()));
        double distance = Math.hypot(x, y);
        double turnAngle = Math.atan(Math.tan(targetAngle));
        robot.turnRight(Math.toDegrees(turnAngle));
        if (targetAngle == turnAngle)
            robot.ahead(distance);
        else
            robot.back(distance);
    }*/

}
