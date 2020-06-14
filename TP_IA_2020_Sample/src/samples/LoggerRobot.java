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
        Double temperatura;
        Boolean isMoving;

        public Dados(String nome, Double distancia,Double temperatura,Boolean isMoving) {
            this.nome = nome;
            this.distancia = distancia;
            this.temperatura = temperatura;
            this.isMoving = isMoving;
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
            if(getRoundNum()%2==0){
                setAhead(100);
                isMoving=true;
            }else{
                isMoving=false;
            }

            setTurnLeft(100);
            Random rand = new Random();
            setAllColors(new Color(rand.nextInt(3), rand.nextInt(3), rand.nextInt(3)));
            execute();
        }

    }


    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

            Bullet b = fireBullet(3);

            if (b!=null){
                balasNoAr.put(b, new Dados(event.getName(), event.getDistance(),this.getGunHeat(),isMoving));
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
                fw.write(d.nome+","+d.distancia+","+d.temperatura+","+d.isMoving+",acertei\n");
            else
                fw.write(d.nome+","+d.distancia+","+d.temperatura+","+d.isMoving+",falhei\n");

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
            fw.write(d.nome+","+d.distancia+","+d.temperatura+","+d.isMoving+",falhei\n");
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
            fw.write(d.nome+","+d.distancia+","+d.temperatura+","+d.isMoving+",falhei\n");
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
