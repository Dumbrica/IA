package samples;

import robocode.*;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import utils.Utils;
public class LoggerRobot extends AdvancedRobot {

    private class Dados{
        String nome;
        Double distancia;
        Double velocidade;

        public Dados(String nome, Double distancia,Double velocidade) {
            this.nome = nome;
            this.distancia = distancia;
            this.velocidade=velocidade;
        }
    }
    Boolean isMoving;
    FileWriter fw;

    HashMap<Bullet, Dados> balasNoAr = new HashMap<>();


    @Override
    public void run()
    {
        super.run();

        try {
            fw = new FileWriter("log_robocode.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){
            setAhead(100);
            setTurnLeft(100);
            execute();
        }

    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

            Bullet b = fireBullet(3);
            if (b!=null){
                balasNoAr.put(b, new Dados(event.getName(), event.getDistance(),event.getVelocity()));
            }
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        super.onBulletHit(event);
        Dados d = balasNoAr.get(event.getBullet());
        try
        {
            //testar se acertei em quem era suposto
            if (event.getName().equals(event.getBullet().getVictim()))
                fw.write(d.nome+","+d.distancia+","+d.velocidade+",1\n");
            else
                fw.write(d.nome+","+d.distancia+","+d.velocidade+",0\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
        balasNoAr.remove(event.getBullet());
    }

    @Override
    public void onBulletMissed(BulletMissedEvent event) {
        super.onBulletMissed(event);
        Dados d = balasNoAr.get(event.getBullet());
        try {
            fw.write(d.nome+","+d.distancia+","+d.velocidade+",0\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        balasNoAr.remove(event.getBullet());
    }

    @Override
    public void onBulletHitBullet(BulletHitBulletEvent event) {
        super.onBulletHitBullet(event);
        Dados d = balasNoAr.get(event.getBullet());
        try {
            fw.write(d.nome+","+d.distancia+","+d.velocidade+",0\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        balasNoAr.remove(event.getBullet());
    }
    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        super.onRoundEnded(event);

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
