package com.android.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
    private Texture[] birds;
    private Texture bg;
    private Texture bottomTube;
    private Texture topTube;
    private Texture gameOver;
    private Random randomNumber;
    private BitmapFont font;
    private BitmapFont message;
    private Circle birdCircle;
    private Rectangle rectangleTopTube;
    private Rectangle rectangleBottomTube;
    //private ShapeRenderer shapeRender;

    //Settings parameters
    private float widthDevice;
    private float heightDevice;
    private int stateGame = 0;//0 = Game not started yet.
    private int score = 0;


    //Logic parameters
    private float variable = 0;
    private float fallingSpeed = 0;
    private float birdBeginPosition = 0;
    private float tubeMovimentX = 0;
    private float distanceBetweenTubes;
    private float deltaTime;
    private float randomicHeightTubes;
    private boolean pointed;

    //Camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;


    @Override
	public void create () {

        batch = new SpriteBatch();
        randomNumber = new Random();
        birdCircle = new Circle();
        /*rectangleTopTube = new Rectangle();
        rectangleBottomTube = new Rectangle();
        shapeRender = new ShapeRenderer();*/

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(6);

        message = new BitmapFont();
        message.setColor(Color.WHITE);
        message.getData().setScale(3);


        birds = new Texture[3];
        birds [0] = new Texture("passaro1.png");
        birds [1] = new Texture("passaro2.png");
        birds [2] = new Texture("passaro3.png");

        bg = new Texture("fundo.png");
        bottomTube = new Texture("cano_baixo.png");
        topTube = new Texture("cano_topo.png");
        gameOver = new Texture("game_over.png");

        //Camera Configs
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        /*widthDevice = Gdx.graphics.getWidth();//Now is using viewport dimensions
        heightDevice = Gdx.graphics.getHeight();*/
        widthDevice = VIRTUAL_WIDTH;
        heightDevice = VIRTUAL_HEIGHT;

        birdBeginPosition = heightDevice/2;
        tubeMovimentX = widthDevice;
        distanceBetweenTubes = 300;



	}

	@Override
	public void render () {

        camera.update();

        //Cleanner oldder frames
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();
        variable += deltaTime * 10;
        if(variable > 2)
            variable = 0;

        if(stateGame == 0){ //Game not started
            if(Gdx.input.justTouched()){
                stateGame = 1;
            }
        }else{//Game started
            fallingSpeed ++;
            if(birdBeginPosition > 0 || fallingSpeed < 0)
                birdBeginPosition -= fallingSpeed;

            if(stateGame == 1){
                tubeMovimentX -= deltaTime * 250;

                if(Gdx.input.justTouched())
                    fallingSpeed = -15;

                //Check If tube exited totally of screen
                if(tubeMovimentX < - topTube.getWidth()){
                    tubeMovimentX = widthDevice;
                    randomicHeightTubes = randomNumber.nextInt(600) - 300;
                    pointed = false;
                }
                //Check score
                if(tubeMovimentX < 120)
                    if(!pointed){
                        score++;
                        pointed = true;
                    }
            }else{//Screen game over
                if(Gdx.input.justTouched()){
                    stateGame = 0;
                    score = 0;
                    fallingSpeed = 0;
                    birdBeginPosition = heightDevice / 2;
                    tubeMovimentX = widthDevice;
                }
            }
        }


        //Setting camera projection parameters
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(bg, 0, 0, widthDevice, heightDevice);
        batch.draw(topTube, tubeMovimentX, heightDevice/2 + distanceBetweenTubes/2 + randomicHeightTubes);
        batch.draw(bottomTube, tubeMovimentX, (heightDevice/2 - bottomTube.getHeight()) - distanceBetweenTubes/2 + randomicHeightTubes);
        batch.draw(birds[ (int) variable], 120, birdBeginPosition); //X and Y //Height determined by first bird pixel.
        font.draw(batch, String.valueOf(score), widthDevice / 2, heightDevice - 50);

        if(stateGame == 2){
            batch.draw(gameOver, widthDevice/2 - gameOver.getWidth() / 2, heightDevice / 2);
            message.draw(batch, String.valueOf("Touch to restart"), widthDevice / 2, heightDevice / 2 - gameOver.getHeight() / 2);

        }


        batch.end();
        birdCircle.set(120 + birds[0].getWidth() / 2, birdBeginPosition + birds[0].getHeight() / 2, birds[0].getWidth() / 2);
        rectangleBottomTube = new Rectangle(
                tubeMovimentX,
                heightDevice/2 - bottomTube.getHeight() - distanceBetweenTubes/2 + randomicHeightTubes,
                bottomTube.getWidth(),
                bottomTube.getHeight()
        );
        rectangleTopTube = new Rectangle(
                tubeMovimentX,
                heightDevice/2 + distanceBetweenTubes/2 + randomicHeightTubes,
                topTube.getWidth(),
                topTube.getHeight()
        );

        //Drawing forms
        /*shapeRender.begin(ShapeRenderer.ShapeType.Filled);//Shape type will be used - Filled = preenchido.
        shapeRender.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
        shapeRender.rect(rectangleBottomTube.x, rectangleBottomTube.y, rectangleBottomTube.width, rectangleBottomTube.height);
        shapeRender.rect(rectangleTopTube.x, rectangleTopTube.y, rectangleTopTube.width, rectangleTopTube.height);
        shapeRender.setColor(Color.RED);
        shapeRender.end();*/

        //Collision Test
        if (Intersector.overlaps(birdCircle, rectangleBottomTube) ||
                Intersector.overlaps(birdCircle, rectangleTopTube) ||
                birdBeginPosition <= 0 ||
                birdBeginPosition >= heightDevice){
            stateGame = 2;
        }
	}

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

	/*@Override
	public void dispose () {
		batch.dispose();
        birds.dispose();
	}*/
}
