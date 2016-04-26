//�C���|�[�g
import com.sun.imageio.spi.RAFImageInputStreamSpi;
import gameCanvasUtil.*;
import gameCanvasUtil.Collision.Collision;
import gameCanvasUtil.Collision.MyVector2D;

import java.util.ArrayList;

/** �Q�[���N���X�B
 *
 * �w�����ҏW���ׂ��\�[�X�R�[�h�ł��B
 */
public class Game extends GameBase
{

	private PinballGame pinball;
	private int gameState;

    /********* �������̎菇�͂������ *********/
    public void initGame()
    {
	    gameState = 0;


	    /* �Q�[���̏����� */
	    pinball = new PinballGame();
	    gc.setWindowTitle("�s���{�[���Q�[��");

    }

    /********* ���̂̈ړ����̍X�V�����͂������ *********/
    public void updateGame()
    {
	    if (gameState == 0)
	    {
		    if (gc.isKeyPushed(gc.KEY_ENTER))
		    {
			    gameState = 1;
		    }
	    }
	    else if (gameState == 1)
	    {
		    if (gc.isKeyPushed(gc.KEY_ENTER))
		    {
			    gameState = 2;
		    }

		    pinball.update();

		    if (gc.isKeyPress(gc.KEY_Z))
			    pinball.updateP1LeftPaddle(-10);
		    if (gc.isKeyPress(gc.KEY_X))
			    pinball.updateP1RightPaddle(10);
		    if (gc.isKeyPress(gc.KEY_LEFT))
			    pinball.updateP2LeftPaddle(-10);
		    if (gc.isKeyPress(gc.KEY_RIGHT))
			    pinball.updateP2RightPaddle(10);
		    if (gc.isKeyReleased(gc.KEY_Z))
			    pinball.resetP1LeftPaddle();
		    if (gc.isKeyReleased(gc.KEY_X))
			    pinball.resetP1RightPaddle();
		    if (gc.isKeyReleased(gc.KEY_LEFT))
		    {
			    pinball.resetP2LeftPaddle();
		    }
		    if (gc.isKeyReleased(gc.KEY_RIGHT))
			    pinball.resetP2RightPaddle();

		    if (pinball.getP1DroppedBalls() == 2)
			    gameState = 3;
		    if (pinball.getP2DroppedBalls() == 2)
			    gameState = 4;

	    }
	    else if (gameState == 2)
	    {
		    if (gc.isKeyPushed(gc.KEY_ENTER))
		    {
			    gameState = 1;
		    }
	    }
	    else if (gameState == 3)
	    {
		    if (gc.isKeyPushed(gc.KEY_ENTER))
		    {
			    pinball.reset();
			    gameState = 1;
		    }
	    }
	    else if (gameState == 4)
	    {
		    if (gc.isKeyPushed(gc.KEY_ENTER))
		    {
			    pinball.reset();
			    gameState = 1;
		    }
	    }
    }

    /********* �摜�̕`��͂������ *********/
    public void drawGame()
    {
	    gc.clearScreen();

	    if(gameState == 0)
	    {
		    gc.setColor(0, 0, 0);
		    gc.drawCenterString("Pinball Battle", GameCanvas.WIDTH / 2, GameCanvas.HEIGHT / 2 - 90);
		    gc.drawCenterString("Press Enter to Play", GameCanvas.WIDTH / 2, GameCanvas.HEIGHT / 2 - 30);
		    gc.drawCenterString("Player 1: Use Z and X", GameCanvas.WIDTH / 2, GameCanvas.HEIGHT / 2 + 30);
		    gc.drawCenterString("Player 2: Use �� and ��", GameCanvas.WIDTH / 2, GameCanvas.HEIGHT / 2 + 90);
	    }
	    else if (gameState == 1)
	    {
		    pinball.draw(gc);
	    }
	    else if (gameState == 2)
	    {
		    gc.setColor(0, 0, 0);
		    gc.drawCenterString("Game Paused", GameCanvas.WIDTH / 2, GameCanvas.HEIGHT / 2 - 30);
		    gc.drawCenterString("Press Enter to Resume", GameCanvas.WIDTH / 2, GameCanvas.HEIGHT / 2 + 30);
	    }
	    else if (gameState == 3)
	    {
		    gc.setColor(gc.COLOR_BLACK);
		    gc.drawCenterString("Player 2 Wins!", gc.WIDTH / 2, gc.HEIGHT / 2 - 30);
		    gc.drawCenterString("Press Enter to Play Again", gc.WIDTH / 2, gc.HEIGHT / 2 + 30);
	    }
	    else if (gameState == 4)
	    {
		    gc.setColor(gc.COLOR_BLACK);
		    gc.drawCenterString("Player 1 Wins!", gc.WIDTH / 2, gc.HEIGHT / 2 - 30);
		    gc.drawCenterString("Press Enter to Play Again", gc.WIDTH / 2, gc.HEIGHT / 2 + 30);
	    }
    }

    /********* �I�����̏����͂������ *********/
    public void finalGame() {}

	private class PinballGame
	{
		/******************/
		private final int GRAVITY = 1; // �d�́i���̍��W�̕����傫�����ߏd�͂��v���X�ɂ���j
		private final int INITIAL_SPEED = -30;

		private int player1DroppedBalls;
		private int player2DroppedBalls;
		private int top = 0, bottom = gc.HEIGHT;
		private int left = 0, right = gc.WIDTH;
		private int leftShooterWall = Ball.SIZE + 1, rightShooterWall = gc.WIDTH - Ball.SIZE - 1;
		private int bouncerBottom = gc.HEIGHT / 3;
		private int shooterTop = gc.HEIGHT / 3 + Ball.SIZE;
		private int collectorTop = gc.HEIGHT * 9/10;
		private int middle = gc.WIDTH / 2;
		private int leftMiddle = (leftShooterWall + middle) / 2, rightMiddle = (middle + rightShooterWall) / 2; // �V���[�^�[�̕ǂƒ��S�̕ǂ̒��S


		private Paddle p1Left, p1Right, p2Left, p2Right;

		private ArrayList<Ball> ballList;
		private ArrayList<Obstacle> obstacleList;
		private ArrayList<Paddle> paddleList;


		PinballGame()
		{
			ballList = new ArrayList<Ball>();
			obstacleList = new ArrayList<Obstacle>();
			paddleList = new ArrayList<Paddle>();
			player1DroppedBalls = 0;
			player2DroppedBalls = 0;

			// �����ŕǁA�p�h���A��Q��������������

			/* �ǂ���� */
			obstacleList.add(new Wall(left, top, right, top)); // �V��
			obstacleList.add(new Wall(left, top, left, bottom)); // ���̕�
			obstacleList.add(new Wall(leftShooterWall, shooterTop, leftShooterWall, bottom)); // ���̃V���[�^�[
			obstacleList.add(new Wall(middle, shooterTop, middle, bottom)); // �^�񒆂̕�
			obstacleList.add(new Wall(rightShooterWall, shooterTop, rightShooterWall, bottom)); // �E�̃V���[�^�[
			obstacleList.add(new Wall(right, top, right, bottom)); // �E�̕�
			// �p�x�������̕� �����ӁF������E�ɐ�����邱��
			obstacleList.add(new Wall(left, shooterTop-Ball.SIZE, shooterTop-Ball.SIZE, top)); // ���̊p�x������ǁi���p�񓙕ӎO�p�`�j
			obstacleList.add(new Wall(right-(shooterTop-Ball.SIZE), top, right, shooterTop-Ball.SIZE)); // �E�̊p�������
			// ���̃R���N�^�[ �����ӁF�E���獶�ɐ�����邱��
			obstacleList.add(new Wall(leftMiddle - Ball.SIZE, bottom, leftShooterWall, collectorTop)); // ���̍��R���N�^�[
			obstacleList.add(new Wall(middle, collectorTop, leftMiddle + Ball.SIZE, bottom)); // ���̉E�R���N�^�[
			obstacleList.add(new Wall(rightMiddle - Ball.SIZE, bottom, middle, collectorTop)); // �E�̍��R���N�^�[
			obstacleList.add(new Wall(rightShooterWall, collectorTop, rightMiddle + Ball.SIZE, bottom)); // �E�̉E�R���N�^�[

			/* ��Q�� */
			// ���̃o�E���T�[
			obstacleList.add(new Bouncer(leftMiddle,bouncerBottom-Ball.SIZE*3));
			obstacleList.add(new Bouncer(leftMiddle + Ball.SIZE*195/100, bouncerBottom));
			obstacleList.add(new Bouncer(leftMiddle - Ball.SIZE*195/100, bouncerBottom));
			// �E�̃o�E���T�[
			obstacleList.add(new Bouncer(rightMiddle,bouncerBottom-Ball.SIZE*3));
			obstacleList.add(new Bouncer(rightMiddle+Ball.SIZE*195/100, bouncerBottom));
			obstacleList.add(new Bouncer(rightMiddle-Ball.SIZE*195/100, bouncerBottom));
			// ���̃s��
			obstacleList.add(new Pin(leftMiddle, shooterTop+Ball.SIZE*3));
			obstacleList.add(new Pin(leftMiddle-Ball.SIZE*4, shooterTop+Ball.SIZE*5));
			obstacleList.add(new Pin(leftMiddle+Ball.SIZE*4, shooterTop+Ball.SIZE*5));
			obstacleList.add(new Pin(leftMiddle+Ball.SIZE*3/2, shooterTop+Ball.SIZE*7));
			obstacleList.add(new Pin(leftMiddle-Ball.SIZE*3/2, shooterTop+Ball.SIZE*7));
			// �E�̃s��
			obstacleList.add(new Pin(rightMiddle, shooterTop+Ball.SIZE*3));
			obstacleList.add(new Pin(rightMiddle-Ball.SIZE*4, shooterTop+Ball.SIZE*5));
			obstacleList.add(new Pin(rightMiddle+Ball.SIZE*4, shooterTop+Ball.SIZE*5));
			obstacleList.add(new Pin(rightMiddle+Ball.SIZE*3/2, shooterTop+Ball.SIZE*7));
			obstacleList.add(new Pin(rightMiddle-Ball.SIZE*3/2, shooterTop+Ball.SIZE*7));

			/* �p�h�� */
			paddleList.add(p1Left = new LeftPaddle(leftShooterWall + Ball.SIZE * 25 / 10, collectorTop - Ball.SIZE / 2));
			paddleList.add(p1Right = new RightPaddle(middle-Ball.SIZE*25/10, collectorTop-Ball.SIZE/2));
			paddleList.add(p2Left = new LeftPaddle(middle+Ball.SIZE*25/10, collectorTop-Ball.SIZE/2));
			paddleList.add(p2Right = new RightPaddle(rightShooterWall-Ball.SIZE*25/10, collectorTop-Ball.SIZE/2));


			/* �{�[�� */
			ballList.add(new Ball(left + Ball.RADIUS + 1, bottom - (Ball.RADIUS + 1), 0, INITIAL_SPEED, 1)); // �v���C���[�P�̃{�[��
			ballList.add(new Ball(right - (Ball.RADIUS + 1), bottom - (Ball.RADIUS + 1), 0, INITIAL_SPEED, 2)); // �v���C���[�Q�̃{�[��

		}

		public int getP1DroppedBalls() { return player1DroppedBalls; }
		public int getP2DroppedBalls() { return player2DroppedBalls; }

		public void reset() {
			ballList.clear();
			player1DroppedBalls = 0;
			player2DroppedBalls = 0;
			for (Paddle p : paddleList) {
				p.reset();
			}
		}
		public void draw(GameCanvas gc)
		{
			for (Obstacle o : obstacleList)
			{
				o.draw(gc);
			}
			for (Paddle p : paddleList)
			{
				p.draw(gc);
			}
			for (Ball b : ballList)
			{
				b.draw(gc);
			}
			gc.setColor(gc.COLOR_BLACK);
			gc.drawCenterString("" + this.getP1DroppedBalls(), 35, 35);
			gc.drawCenterString("" + this.getP2DroppedBalls(), gc.WIDTH - 35, 35);
		}

		public void updateP1LeftPaddle(int angle) { p1Left.move(angle); }
		public void updateP1RightPaddle(int angle) { p1Right.move(angle); }
		public void updateP2LeftPaddle(int angle) { p2Left.move(angle); }
		public void updateP2RightPaddle(int angle) { p2Right.move(angle); }
		public void resetP1LeftPaddle() { p1Left.reset(); }
		public void resetP1RightPaddle() { p1Right.reset(); }
		public void resetP2LeftPaddle() { p2Left.reset(); }
		public void resetP2RightPaddle() { p2Right.reset(); }

		public void update()
		{
			boolean p1HasBall = false, p2HasBall = false;
			for (int i = ballList.size() - 1; i >= 0; i--)
			{
				Ball b = ballList.get(i);
				for (Obstacle o : obstacleList)
				{
					o.checkCollision(b);
				}
				for (Paddle p : paddleList)
				{
					p.checkCollision(b);
				}
				if (b.updatePositionAndCheckDropped()) // true���Ԃ�Ɨ�����
				{
					if (b.getPID() == 1)
						player1DroppedBalls++;
					else if (b.getPID() == 2)
						player2DroppedBalls++;
					ballList.remove(i);
				}

				b.updateVelocity(0,GRAVITY);

				if (b.getPID() == 1)
					p1HasBall = true;
				else if (b.getPID() == 2)
					p2HasBall = true;
			}
			if (p1HasBall == false)
			{
				ballList.add(new Ball(Ball.RADIUS + 1, bottom - (Ball.RADIUS + 1), 0, INITIAL_SPEED, 1)); // �v���C���[�P�̃{�[��
			}
			if (p2HasBall == false)
			{
				ballList.add(new Ball(right - (Ball.RADIUS + 1), bottom - (Ball.RADIUS + 1), 0, INITIAL_SPEED, 2)); // �v���C���[�Q�̃{�[��
			}
		}

		private abstract class Obstacle
		{
			protected double bounceMultiplier;

			public abstract void checkCollision(Ball ball);
			public abstract void draw(GameCanvas gc);
		}

		/**
		 * �����̏�Q���i�ǁj
		 */
		private class Wall extends Obstacle
		{
			private int x1, y1, x2, y2;

			/**
			 * �ǂ̃R���X�g���N�^
			 * @param x1 �X�^�[�gX
			 * @param y1 �X�^�[�gY
			 * @param x2 �t�B�j�b�V��X
			 * @param y2 �t�B�j�b�V��Y
			 */
			Wall(int x1, int y1,
			     int x2, int y2)
			{
				bounceMultiplier = .9;
				this.x1 = x1;
				this.y1 = y1;
				this.x2 = x2;
				this.y2 = y2;
			}

			public void draw(GameCanvas gc)
			{
				gc.setColor(GameCanvas.COLOR_BLACK);
				gc.drawLine(x1, y1, x2, y2);
			}

			public void checkCollision(Ball ball)
			{
				int vx = ball.getVX();
				int vy = ball.getVY();
				int bx = ball.getX();
				int by = ball.getY();
				int bx2, by2;
				MyVector2D myV;

				if (vx == 0) // Y���݂̂��ړ���
				{
					bx2 = bx;
					by2 = by + vy;
				}
				else if (vy == 0) // X���݂̂��ړ���
				{
					by2 = by;
					bx2 = bx + vx;
				}
				else
				{
					double hypotenuse = Math.sqrt(vx*vx+vy*vy);
					double scale = hypotenuse / ball.RADIUS;
					by2 = by + vy;
					bx2 = bx + vx;
				}

				myV  = Collision.checkHitVector(this.x1, this.y1, this.x2, this.y2, bx, by, bx2, by2);
				if (myV != null)
				{
					if (this.y2 == this.y1) // �ǂ��n�ʂƕ��s
					{
						ball.setVelocity((int) (vx * bounceMultiplier), (int) (vy * -1 * bounceMultiplier));
					}
					else if (this.x2 == this.x1) // �ǂ��n�ʂƐ���
					{
						ball.setVelocity((int) (vx * -1 * bounceMultiplier), (int) (vy * bounceMultiplier));
					}
					else
					{
						/**
						 * n = �ǂ̖@���x�N�g��
						 * v = ���x�x�N�g��
						 * w = �ǂƕ��s�̑��x�x�N�g��
						 * u = �ǂƐ����̑��x�x�N�g��
						 * v' = ���˃x�N�g��
						 *
						 * u = (v�En/n�En)n
						 * w = v - u
						 * v' = w - u
						 */

						int dx = this.x2 - this.x1;
						int dy = this.y2 - this.y1;
						int nx = -dy;
						int ny = dx;
						double ux = ((vx*nx*1.0+vy*ny*1.0)/(nx*nx*1.0+ny*ny*1.0)) * nx*1.0;
						double uy = ((vx*nx*1.0+vy*ny*1.0)/(nx*nx*1.0+ny*ny*1.0)) * ny*1.0;
						double wx = vx * 1.0 - ux;
						double wy = vy * 1.0 - uy;
						double vxNew = wx - ux;
						double vyNew = wy - uy;
						ball.setVelocity((int) (vxNew * bounceMultiplier), (int) (vyNew * bounceMultiplier));
					}
				}

			}
		}

		/**
		 * �~�`�̋������˕Ԃ��Q��
		 */
		private class Bouncer extends Obstacle
		{
			public static final int RADIUS = Ball.SIZE;
			private int centerX, centerY;

			Bouncer(int centerX, int centerY)
			{
				bounceMultiplier = 1;
				this.centerX = centerX;
				this.centerY = centerY;
			}

			public void draw(GameCanvas gc)
			{
				gc.setColor(GameCanvas.COLOR_GREEN);
				gc.fillCircle(centerX, centerY, RADIUS);
			}

			public void checkCollision(Ball ball)
			{
				if (gc.checkHitCircle(centerX, centerY, RADIUS, ball.getX(), ball.getY(), ball.RADIUS))
				{
					ball.setVelocity((int) (ball.getVX() * -1 * bounceMultiplier), (int) (ball.getVY() * -1 * bounceMultiplier));
					if (ball.getVX() < 0)
						ball.setPositionX(ball.getPositionX() - this.RADIUS);
					else
						ball.setPositionX(ball.getPositionX() + this.RADIUS);
					if (ball.getVY() < 0)
						ball.setPositionY(ball.getPositionY() - this.RADIUS);
					else
						ball.setPositionY(ball.getPositionY() + this.RADIUS);
				}
			}
		}

		/**
		 * �������~�`�̏�Q��
		 */
		private class Pin extends Obstacle
		{
			private final int RADIUS = 5;
			private int centerX, centerY;

			Pin(int centerX, int centerY)
			{
				bounceMultiplier = .85;
				this.centerX = centerX;
				this.centerY = centerY;
			}

			public void draw(GameCanvas gc)
			{
				gc.setColor(GameCanvas.COLOR_BLACK);
				gc.fillCircle(centerX, centerY, RADIUS);
			}
			public void checkCollision(Ball ball)
			{
				if (gc.checkHitCircle(centerX, centerY, RADIUS, ball.getX(), ball.getY(), ball.RADIUS))
				{
					ball.setVelocity((int) (ball.getVX() * -1 * bounceMultiplier), (int) (ball.getVY() * -1 * bounceMultiplier));
					if (ball.getVX() < 0)
						ball.setPositionX(ball.getPositionX() - this.RADIUS);
					else
						ball.setPositionX(ball.getPositionX() + this.RADIUS);
					if (ball.getVY() < 0)
						ball.setPositionY(ball.getPositionY() - this.RADIUS);
					else
						ball.setPositionY(ball.getPositionY() + this.RADIUS);
				}
			}
		}

		/**
		 *
		 */
		private abstract class Paddle extends Obstacle
		{
			public static final int MAX_ANGLE = 40;
			protected final int AXIS_RADIUS = 2;
			protected final int OFFSET = 20;
			protected int axisX, axisY;
			protected int topX1, topY1, topX2, topY2;
			protected int angle;

			Paddle(int axisX, int axisY)
			{
				this.axisX = axisX;
				this.axisY = axisY;
				angle = 0;

				bounceMultiplier = 1.2;
			}

			public void checkCollision(Ball ball)
			{
				int vx = ball.getVX();
				int vy = ball.getVY();
				int bx = ball.getX();
				int by = ball.getY();
				int bx2, by2;
				MyVector2D myV;

				if (vx == 0) // Y���݂̂��ړ���
				{
					bx2 = bx;
					by2 = by + vy;
				}
				else if (vy == 0) // X���݂̂��ړ���
				{
					by2 = by;
					bx2 = bx + vx;
				}
				else
				{
					double hypotenuse = Math.sqrt(vx*vx+vy*vy);
					double scale = hypotenuse / ball.RADIUS;
					by2 = by + vy;
					bx2 = bx + vx;
				}

				myV  = Collision.checkHitVector(this.topX1, this.topY1, this.topX2, this.topY2, bx, by, bx2, by2);
				if (myV != null)
				{
					/**
					 * n = �p�h���̖@���x�N�g��
					 * v = ���x�x�N�g��
					 * w = �p�h���ƕ��s�̑��x�x�N�g��
					 * u = �p�h���Ɛ����̑��x�x�N�g��
					 * v' = ���˃x�N�g��
					 *
					 * u = (v�En/n�En)n
					 * w = v - u
					 * v' = w - u
					 */

					int dx = this.topX2 - this.topX1;
					int dy = this.topY2 - this.topY1;
					int nx = -dy;
					int ny = dx;
					double ux = ((vx*nx*1.0+vy*ny*1.0)/(nx*nx*1.0+ny*ny*1.0)) * nx*1.0;
					double uy = ((vx*nx*1.0+vy*ny*1.0)/(nx*nx*1.0+ny*ny*1.0)) * ny*1.0;
					double wx = vx * 1.0 - ux;
					double wy = vy * 1.0 - uy;
					double vxNew = wx - ux;
					double vyNew = wy - uy;
					ball.setVelocity((int) (vxNew * bounceMultiplier), (int) (vyNew * bounceMultiplier));

				}

			}

			public abstract void move(int angle);
			public void reset() { angle = 0; }
		}

		private class LeftPaddle extends Paddle
		{
			LeftPaddle(int axisX, int axisY)
			{
				super(axisX, axisY);
				this.topX1 = axisX + ((int) (95 * Math.cos(Math.toRadians(angle+OFFSET))));
				this.topY1 = axisY + ((int) (95 * Math.sin(Math.toRadians(angle+OFFSET))));
				this.topX2 = axisX - ((int) (5 * Math.cos(Math.toRadians(angle + OFFSET))));
				this.topY2 = axisY - ((int) (5 * Math.sin(Math.toRadians(angle + OFFSET))));
			}

			public void draw(GameCanvas gc)
			{
				gc.setColor(GameCanvas.COLOR_BLUE);
				gc.fillRect(axisX-5, axisY-5, 100, 10, axisX, axisY, angle+OFFSET);

				this.topX1 = axisX + ((int) (95 * Math.cos(Math.toRadians(angle+OFFSET))));
				this.topY1 = axisY + ((int) (95 * Math.sin(Math.toRadians(angle+OFFSET))));
				this.topX2 = axisX - ((int) (5 * Math.cos(Math.toRadians(angle+OFFSET))));
				this.topY2 = axisY - ((int) (5 * Math.sin(Math.toRadians(angle+OFFSET))	));

				gc.setColor(GameCanvas.COLOR_WHITE);
				gc.fillCircle(axisX, axisY, AXIS_RADIUS);
//				gc.setColor(GameCanvas.COLOR_RED);
//				gc.drawLine(topX1, topY1, topX2, topY2);
			}

			// �����v���
			public void move(int angle)
			{
				if (Math.abs(this.angle + angle) < MAX_ANGLE)
					this.angle += angle;
			}
		}

		private class RightPaddle extends Paddle
		{
			RightPaddle(int axisX, int axisY)
			{
				super(axisX, axisY);

				this.topX1 = axisX + ((int) (5 * Math.cos(Math.toRadians(angle+OFFSET))));
				this.topY1 = axisY - ((int) (5 * Math.sin(Math.toRadians(angle+OFFSET))));
				this.topX2 = axisX - ((int) (95 * Math.cos(Math.toRadians(angle+OFFSET))));
				this.topY2 = axisY + ((int) (95 * Math.sin(Math.toRadians(angle+OFFSET))));
			}

			public void draw(GameCanvas gc)
			{
				gc.setColor(GameCanvas.COLOR_BLUE);
				gc.fillRect(axisX-95, axisY-5, 100, 10, axisX, axisY, angle-OFFSET);

				this.topX1 = axisX + ((int) (5 * Math.cos(Math.toRadians(OFFSET-angle))));
				this.topY1 = axisY - ((int) (5 * Math.sin(Math.toRadians(OFFSET-angle))));
				this.topX2 = axisX - ((int) (95 * Math.cos(Math.toRadians(OFFSET-angle))));
				this.topY2 = axisY + ((int) (95 * Math.sin(Math.toRadians(OFFSET-angle))));

				gc.setColor(GameCanvas.COLOR_WHITE);
				gc.fillCircle(axisX, axisY, AXIS_RADIUS);
//				gc.setColor(GameCanvas.COLOR_RED);
//				gc.drawLine(topX1, topY1, topX2, topY2);
			}

			// ���v���
			public void move(int angle)
			{
				if (Math.abs(this.angle + angle) < MAX_ANGLE)
					this.angle += angle;
			}
		}

		private class Ball
		{
			public static final int SIZE = 25; // �傫��
			public static final int RADIUS = (SIZE - 1) / 2; // ���a�i1�̌��Z�͒��S�̃s�N�Z���p�j

			private int velocityX, velocityY; // ���x
			private int positionX, positionY; // �ʒu
			private int playerID;

			Ball(int positionX, int positionY,
			     int velocityX, int velocityY, int playerID)
			{
				this.positionX = positionX;
				this.positionY = positionY;
				this.velocityX = velocityX;
				this.velocityY = velocityY;
				this.playerID = playerID;
			}

			public int getX() { return positionX; }
			public int getY() { return positionY; }
			public int getVX() { return velocityX; }
			public int getVY() { return velocityY; }
			public int getPID() { return playerID; }

			public void draw(GameCanvas gc)
			{
				gc.setColor(GameCanvas.COLOR_YELLOW);
				gc.fillCircle(positionX, positionY, RADIUS);
			}

			public boolean updatePositionAndCheckDropped()
			{
				positionX += velocityX;
				positionY += velocityY;

				if (positionX < middle)
					playerID = 1;
				else
					playerID = 2;

				// �{�[��������������Ԃ�
				if (positionY - Ball.RADIUS > bottom)
					return true;
				else
					return false;
			}

			public void updateVelocity(int accelerationX, int accelerationY)
			{
				velocityX += accelerationX;
				velocityY += accelerationY;
			}

			public void setVelocity(int X, int Y)
			{
				velocityX = X;
				velocityY = Y;
			}

			public int getPositionX() { return positionX; }
			public int getPositionY() { return positionY; }
			public void setPositionX(int x) { positionX = x; }
			public void setPositionY(int y) { positionY = y; }

		}
	}
}
