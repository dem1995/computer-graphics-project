
import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import drawables.tree.BasicTree;


/**
 * A class that gets hooked into the GLCanvas that tracks updates and draws to the screen on refreshes.
 * @author DEMcKnight
 */
public class EventManager implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{

	private int virtualWidth=1920;
	private int virtualHeight=1080;
	
	private int screenWidth = 1920;
	private int screenHeight = 1080;	
	
	private static int horizon = 467;
	
	
	//The points in the galaxy (modeled by a Lorenz attractor) that will be drawn
	public static BasicTree theTree = new BasicTree(960, horizon, 2, 2, new Color(166, 129, 62));
	
	float targetAspectRatio = virtualWidth/virtualHeight;
	
	int[] viewport = new int[4];
	private double[] projectionMatrix = new double[16];
	private double[] modelMatrix = new double[16];
		
	private Point2D.Double cameraOrigin = new Point2D.Double(0, 0);	
	private Point2D.Double mousePosition = new Point2D.Double(0, 0);

	/******************************************/
	/*GLEventListener methods*/
	/******************************************/
	
	//Called by the drawable to initiate OpenGL rendering by the client.
	@Override
	public void display(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();		
	
		update();	
		updateProjectionMatrix(drawable);
		
		render(drawable);		
	}
	
	private void update()
	{	
	}
	
	private void updateProjectionMatrix(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();		
		
		//Project to the window
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();	
		
		//Scale what's being drawn to account for changes to the window
		float scaleX = screenWidth/(float)virtualWidth;
		float scaleY = screenHeight/(float)virtualHeight;		
		if (scaleX < scaleY)
			scaleY = scaleX;
		else
			scaleX = scaleY;
		
		gl.glOrtho(cameraOrigin.x, (screenWidth + cameraOrigin.x)/scaleX, cameraOrigin.y, (screenHeight + cameraOrigin.y)/scaleY, 0, 1);		
	
		//Update projection matrices
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
        gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projectionMatrix, 0);
        gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX,  modelMatrix, 0);
	
	}

    //Called by the drawable when the display mode or the display device associated with the GLAutoDrawable has changed.
	@Override
	public void dispose(GLAutoDrawable arg0)
	{		}
	
	//Called by the drawable immediately after the OpenGL context is initialized.
	@Override
	public void init(GLAutoDrawable canvas)
	{
		//Prepare the Lorenz attractor information for drawing the galaxy later (this way it's not called every update or reshape)
		
	}

	//Called by the drawable during the first repaint after the component has been resized.
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{		
		screenWidth = width;
		screenHeight = height;	
	}
	
	//Actually does the rendering
	public static void render(GLAutoDrawable drawable)
	{
		GL2 gl = drawable.getGL().getGL2();

		//Draw sky background
		Drawers.drawSkyRect(gl, new Color[]{new Color(2, 125, 254), new Color(82, 192, 255), new Color(188, 245, 255)}, 0, 1920, horizon, 1080);

		//Draw the ground
		Drawers.drawGroundRect(gl,  new Color(82, 63, 63), new Color(97, 143, 81), 0, 1920, 196, horizon-1);
		
		//Draw the tree
		Drawers.drawTree(gl,  theTree);
		
	
	}


	@Override
	public void keyPressed(KeyEvent e)
	{
		
		switch (e.getKeyCode())
		{
			//TODO
		}
		
		return;
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		updateMousePosition(e);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		updateMousePosition(e);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		updateMousePosition(e);
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		updateMousePosition(e);
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		updateMousePosition(e);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		updateMousePosition(e);
		//mouseIsPressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		updateMousePosition(e);
		//mouseIsPressed = false;	
	}
	
	public void updateMousePosition(MouseEvent e)
	{
		double wCoord[] = new double[4];// wx, wy, wz;// returned xyz coords

		new GLU().gluUnProject(e.getX(), e.getY(), 0.0, //
	              modelMatrix, 0,
	              projectionMatrix, 0, 
	              viewport, 0, 
	              wCoord, 0);
			mousePosition = new Point2D.Double(wCoord[0], virtualHeight-wCoord[1]);	
	}
}
