package samples;

import performance.EvaluateFire;
import robocode.AdvancedRobot;

public class robot extends AdvancedRobot {
    @Override
    public void run()
    {
        super.run();


        while(true)
        {
            this.setAhead(100);
            this.setTurnRight(100);

            this.execute();
        }
    }
}
