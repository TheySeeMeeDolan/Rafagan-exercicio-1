/**
 * Copyright (c) 2001-2016 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.htmls
 */
package sample;


import robocode.DeathEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.*;


/**
 * Corners - a sample robot by Mathew Nelson.
 * <p/>
 * This robot moves to a corner, then swings the gun back and forth.
 * If it dies, it tries a new corner in the next round.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */

/*Basico robo de luta que busca ficar em cantos durante a partida.
  Caso o robo aviste um inimigo irá parar e apenas quando 
  não mais avistar o inimigo irá se mover.*/


public class Corners extends Robot {
	int others; // Number of other robots in the game
	static int corner = 0; // Which corner we are currently using
	// static so that it keeps it between rounds.
	boolean stopWhenSeeRobot = false; // See goCorner()

	/**
	 * run:  Corners' main run function.
	 */
	public void run() {
		// Set colors
		setBodyColor(Color.red);
		setGunColor(Color.black);
		setRadarColor(Color.yellow);
		setBulletColor(Color.green);
		setScanColor(Color.green);

		// Save # of other bots
		others = getOthers();

		// Move to a corner
		goCorner();

		// Initialize gun turn speed to 3
		int gunIncrement = 3;

		// Spin gun back and forth
		while (true) {
			for (int i = 0; i < 30; i++) {
				turnGunLeft(gunIncrement);
			}
			gunIncrement *= -1;
		}
	}

	/*Metodo principal do Robo Corner, o robo irá ir para um canto do mapa.*/
	public void goCorner() {
		// We don't want to stop when we're just turning...
		stopWhenSeeRobot = false;
		
		/*Parte do código onde tem a escolha do canto,é subtraido do 
		heading(para onde o robo olha o corner desejado que ele vá */		
		turnRight(normalRelativeAngleDegrees(corner - getHeading()));
		
		/* O robo não deve colidir com outros robos, 
		então com essa variavel como verdadeira o robo irá parar de se mover e então atirar
		e só voltará a se mover quando não ouver nenhum robo na frente*/
		stopWhenSeeRobot = true;
		
		// Move to that wall
		ahead(5000);
		// Turn to face the corner
		turnLeft(90);
		// Move to the corner
		ahead(5000);
		// Turn gun to starting point
		turnGunLeft(90);
	}

	/**
	 * onScannedRobot:  Stop and fire!
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Should we stop, or just fire?
		if (stopWhenSeeRobot) {
			// Stop everything!  You can safely call stop multiple times.
			stop();
			// Call our custom firing method
			smartFire(e.getDistance());
			
			/*Faz com que o robo não ande e fique apenas atirando no robo a sua frente,
			porque chama a função para verificar se tem um robo na visão*/
			scan();
		
			resume();
		} else {
			smartFire(e.getDistance());
		}
	}
	
	/*Atira no Robo visto de acordo com a distancia e com a sua própria energia*/
		
	public void smartFire(double robotDistance) {
		if (robotDistance > 200 || getEnergy() < 15) {
			fire(1);
		} else if (robotDistance > 50) {
			fire(2);
		} else {
			fire(3);
		}
	}

	/**
	 * onDeath:  We died.  Decide whether to try a different corner next game.
	 */
	public void onDeath(DeathEvent e) {
		// Well, others should never be 0, but better safe than sorry.
		if (others == 0) {
			return;
		}

		/*Caso o robo inimigo tenha sobrevivido com mais the 75% da vida
		 o robo corner irá mudar o canto que ele fica*/
		if ((others - getOthers()) / (double) others < .75) {
			corner += 90;
			if (corner == 270) {
				corner = -90;
			}
			out.println("I died and did poorly... switching corner to " + corner);
		} else {
			out.println("I died but did well.  I will still use corner " + corner);
		}
	}
}
