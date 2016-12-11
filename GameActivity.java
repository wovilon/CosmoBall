package wovilon.pingpong1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.SENSOR_SERVICE;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static wovilon.pingpong1.R.drawable.ball;

public class GameActivity extends Activity implements SensorEventListener {
    public int x=0;
    Sensor mySensor;
    SensorManager SM;
    public double phoneRotation;

    void rotTranslate(float dat){this.phoneRotation=dat; //if (this.phoneRotation>2) System.exit(0);
        }

    @Override
    public void onSensorChanged(SensorEvent event) {
        rotTranslate(event.values[0]);
        //rotTranslate(event.values[0]);
        //if (this.phoneRotation>5)System.exit(0);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new GameView(this,this.phoneRotation));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SM=(SensorManager)getSystemService(SENSOR_SERVICE);
        mySensor=SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);


    }

    class GameView extends SurfaceView implements SurfaceHolder.Callback{
        Context context;
        double phoneRotation;
        private SurfaceHolder mSurfaceHolder;
        private GameManager gameManager;

        public GameView (Context context,double phoneRotation){
            super(context); this.context=context; this.phoneRotation=phoneRotation;
            mSurfaceHolder=getHolder();
            mSurfaceHolder.addCallback(this);

        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            gameManager = new GameManager(getHolder(),context,this.phoneRotation);
            gameManager.setRunning(true);
            gameManager.start();


        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        }
    }
}

class GameManager extends Thread {

    double i=0;
    private boolean running = false;
    private SurfaceHolder surfaceHolder;
    private Context context;
    private double phoneRotation;
    private int displayHeight, displayWidth;
    private boolean [][] gameField;
    int defeatedBricks=0;
    MediaPlayer sound;


    public GameManager(SurfaceHolder surfaceHolder, Context context,double phoneRotation) {
        this.surfaceHolder = surfaceHolder;
        this. context=context;
        this.phoneRotation=phoneRotation;


        Resources resources=context.getResources(); //get screen size and create boolean gameField
        DisplayMetrics displayMetrics=resources.getDisplayMetrics();
        this.displayWidth=displayMetrics.widthPixels;
        this.displayHeight=displayMetrics.heightPixels;
        this.gameField=new boolean [this.displayWidth] [this.displayHeight];
        for (int i=0;i<this.displayWidth;i++){
            for (int j=0;j<this.displayHeight;j++){gameField[i] [j]=false;} }

        for (int i=0;i<10;i++){
            for (int j=0;j<this.displayHeight;j++){gameField[i] [j]=true;} }
        for (int i=this.displayWidth-10;i<this.displayWidth;i++){
            for (int j=0;j<this.displayHeight;j++){gameField[i] [j]=true;} }
        for (int i=0;i<this.displayWidth;i++){
            for (int j=0;j<10;j++){gameField[i] [j]=true;} }
        //for (int i=0;i<this.displayWidth;i++){
         //   for (int j=this.displayHeight-50;j<this.displayHeight;j++){gameField[i] [j]=true;} }

    }

    public void setRunning(boolean running) {this.running = running;
            }

    @Override
    public void run() {
        Canvas canvas=null;
        Paint mPaint=new Paint();
        canvas = surfaceHolder.lockCanvas(null);
        BackGround backGround=new BackGround(canvas);
        Ball ball=new Ball(canvas,context);
        Pad pad=new Pad(canvas,context,this.displayWidth,this.displayHeight,this.phoneRotation);
        Brick [] bricks=new Brick[this.createBricks().length];
        int[][] brickPoints=new int[this.createBricks().length][2];
        for (int i=0;i<this.createBricks().length;i++){
            bricks[i]=new Brick(canvas,context,gameField);  }
        Intent intent=new Intent(context, WinLooseActivity.class);
        sound=MediaPlayer.create(context,R.raw.gamesound);
        sound.start();

        surfaceHolder.unlockCanvasAndPost(canvas);
        while (running) {
            canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas(null);
                if (canvas == null)
                    continue;
                backGround.draw(0,0);

                for (int i=0;i<this.createBricks().length;i++){
                    if (bricks[i].state==true)
                    bricks[i].draw(this.createBricks()[i][0],createBricks()[i][1]);
                }


                ball.update();
                ball.draw();



                //if(ball.y>700){if (ball.x<pad.x+10) pad.x-=5; else if (ball.x>pad.x+pad.bitmap.getWidth()-40) pad.x+=5;}
                pad.update();
                pad.draw();


                //if (ball.x<1) {ball.alfa=0;}
                //if (ball.x>720-40) {ball.alfa=180*0.017;};
                ball.alfa=collisionFinder(ball,bricks);
                pad.undraw();
                if(ball.y>pad.y+pad.bitmap.getHeight()){
                    intent.putExtra("winloose",false);
                    context.startActivity(intent); sound.stop();
                }

                refreshCanvas(canvas);
                sleep(10);

            }catch(Exception e) {}
            finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

    }
     double collisionFinder(Ball ball,Brick [] bricks){
         double alfa=ball.alfa;
         int mmax=0; //numder of interferred pixels
         double xsr=0,ysr=0; //middle point of interferred pixels
         double normal; //angle of normal to surface
         int intx=(int)ball.x; int inty=(int)ball.y; //ball coordinates
         Point intpix=new Point(-1,-1); //one of all interferred pixels to find object of collision

         for (int n=0;n<ball.pix.length;n++) {
             if (this.gameField[(int)ball.x+ball.pix[n][0] ] [(int)ball.y+ball.pix[n][1] ]){
              mmax++; intpix.x=(int)ball.x+ball.pix[n][0]; intpix.y=(int)ball.y+ball.pix[n][1] ; }
                }
            String tx="",ty="";

         if (mmax>0) {
             MediaPlayer btSound=MediaPlayer.create(context,R.raw.click6);
             btSound.start();

             for (int n=0;n<ball.pix.length;n++) {
                 if (gameField[intx+ball.pix[n][0]][inty+ball.pix[n][1]]) {
                     xsr+=intx+ball.pix[n][0];
                     ysr+=inty+ball.pix[n][1];
                     tx+=intx+ball.pix[n][0]+" "; ty+=inty+ball.pix[n][1]+" ";}
             }
             xsr=xsr/mmax+1; ysr=ysr/mmax+1;
             normal=Math.atan((ball.y+ball.bitmap.getHeight()/2-ysr) / (ball.x+ball.bitmap.getWidth()/2-xsr));

             alfa=2*normal-(alfa-Math.toRadians(180));
            alfa=alfa;
         }
         if (intpix.x!=(-1)){
             boolean padTouched=true;
             for (int i=0;i<bricks.length;i++){
                 if (intpix.x>=bricks[i].x & intpix.x<=bricks[i].x+bricks[i].bitmap.getWidth()
                   & intpix.y>=bricks[i].y & intpix.y<=bricks[i].y+bricks[i].bitmap.getHeight()) {
                     bricks[i].state=false;
                     bricks[i].delete();
                     padTouched=false;
                     this.defeatedBricks+=1;
                 }
             } int x;
             if (this.defeatedBricks>=bricks.length) {
                 running = false;
                 Intent intent=new Intent(context, WinLooseActivity.class);
                 intent.putExtra("winloose",true);
                 context.startActivity(intent);
                 }

         }
         return alfa;
    }

    int[][] createBricks(){
        int X=60, Y=260, w=60, h=60;
        int [][] brickPoints=new int[22][2];

        brickPoints[0][0]=X; brickPoints[0][1]=Y;
        brickPoints[1][0]=X+w*1; brickPoints[1][1]=Y;
        brickPoints[2][0]=X+w*2; brickPoints[2][1]=Y;
        brickPoints[3][0]=X+w*3; brickPoints[3][1]=Y;
        brickPoints[4][0]=X+w*4; brickPoints[4][1]=Y;
        brickPoints[5][0]=X+w*5; brickPoints[5][1]=Y;
        brickPoints[6][0]=X+w*6; brickPoints[6][1]=Y;
        brickPoints[7][0]=X+w*7; brickPoints[7][1]=Y;
        brickPoints[8][0]=X+w*8; brickPoints[8][1]=Y;
        brickPoints[9][0]=X+w*9; brickPoints[9][1]=Y;

        brickPoints[10][0]=X+w*2; brickPoints[10][1]=Y+h*1;
        brickPoints[11][0]=X+w*3; brickPoints[11][1]=Y+h*1;
        brickPoints[12][0]=X+w*4; brickPoints[12][1]=Y+h*1;
        brickPoints[13][0]=X+w*5; brickPoints[13][1]=Y+h*1;
        brickPoints[14][0]=X+w*6; brickPoints[14][1]=Y+h*1;
        brickPoints[15][0]=X+w*7; brickPoints[15][1]=Y+h*1;

        brickPoints[16][0]=X+w*3; brickPoints[16][1]=Y+h*2;
        brickPoints[17][0]=X+w*4; brickPoints[17][1]=Y+h*2;
        brickPoints[18][0]=X+w*5; brickPoints[18][1]=Y+h*2;
        brickPoints[19][0]=X+w*6; brickPoints[19][1]=Y+h*2;

        brickPoints[20][0]=X+w*4; brickPoints[20][1]=Y+h*3;
        brickPoints[21][0]=X+w*5; brickPoints[21][1]=Y+h*3;

        return brickPoints;
    }


    void updateObjects() {


    }
    void refreshCanvas(Canvas canvas) {

    }


    class Ball {
        Bitmap bitmap;
        Canvas canv;
        Paint mPaint;
        double x=300,y=900;
        double alfa=Math.toRadians(-90),velocity=10;
        int [] [] pix;


        public Ball(Canvas canvas,Context context) {
            canv=canvas;
            Resources resources=context.getResources();
            bitmap= BitmapFactory.decodeResource(resources,R.drawable.ball);

            int n=0,nmax=0; //get ball pixels
            for (int i=0;i<bitmap.getWidth();i++) {
                for (int j=0;j<bitmap.getHeight();j++) {
                    if (bitmap.getPixel(i,j)!=0) nmax++;
                }
            }
            pix=new int[nmax] [2];
            for (int i=0;i<bitmap.getWidth();i++) {
                for (int j=0;j<bitmap.getHeight();j++) {
                    if (bitmap.getPixel(i,j)!=0) {pix[n][0]=i; pix[n][1]=j; n++;}
                }
            }
        }
        void draw(){
            canv.drawBitmap(bitmap,(int)Math.round(x),(int)Math.round(y),mPaint);
        }
        void update(){
            this.x+=velocity*cos(alfa);
            this.y+=velocity*sin(alfa);
        }
    }

    class Brick{
        Bitmap bitmap;
        Canvas canv;
        Context context;
        Paint mPaint;
        int x,y;
        boolean state=true; //true - object is visible, false - non visible
        boolean[][] gameField;
      public Brick(Canvas canvas, Context context,boolean[][] gameField){
            canv=canvas; this.context=context; this.gameField=gameField;
            Resources resources=context.getResources();
            bitmap= BitmapFactory.decodeResource(resources,R.drawable.brick);
        }

        void draw(int x, int y){
            canv.drawBitmap(bitmap,x,y,mPaint);
            this.x=x; this.y=y;
            for (int i=0;i<bitmap.getWidth();i++) {
                for (int j=0;j<bitmap.getHeight();j++)
                    gameField[x+i][y+j] = true;
            }}
        void delete(){
            for (int i=0;i<bitmap.getWidth();i++) {
                for (int j=0;j<bitmap.getHeight();j++)
                    gameField[x+i][y+j] = false;
            }
        }

          }

    class Pad {
        Bitmap bitmap;
        Canvas canv;
        Context context;
        Paint mPaint;
        double x=300,y;
        double velocity=5;
        double phoneRotation;
        int [] [] pix;
        Sens sens;

        public Pad(Canvas canvas,Context context,int displayWidth, int displayHeight, double phoneRotation) {
            canv=canvas; this.context=context; this.phoneRotation=phoneRotation;
            Resources resources=context.getResources();
            bitmap= BitmapFactory.decodeResource(resources,R.drawable.pad);
            this.sens=new Sens(context);

            int n=0,nmax=0; //get ball pixels
            for (int i=0;i<bitmap.getWidth();i++) {
                for (int j=0;j<bitmap.getHeight();j++) {
                    if (bitmap.getPixel(i,j)!=0) nmax++;
                }
            }
            pix=new int[nmax] [2];
            for (int i=0;i<bitmap.getWidth();i++) {
                for (int j=0;j<bitmap.getHeight();j++) {
                    if (bitmap.getPixel(i,j)!=0) {pix[n][0]=i; pix[n][1]=j; n++;}
                }
            }
            this.y=displayHeight-this.bitmap.getHeight()-50;
            this.x=displayWidth/2-this.bitmap.getWidth()/2;
            }
        void draw(){
            canv.drawBitmap(bitmap,(int)Math.round(x),(int)Math.round(y),mPaint);
            for (int i=0;i<bitmap.getWidth();i++) {
                for (int j=0;j<bitmap.getHeight();j++)
                    gameField[(int)this.x+i][(int)this.y+j] = true;
        }}
        void undraw(){
            for (int i=0;i<bitmap.getWidth();i++) {
                for (int j=0;j<bitmap.getHeight();j++)
                    gameField[(int)this.x+i][(int)this.y+j] = false;
            }}
        void update(){
            if (x>10&x<displayWidth-bitmap.getWidth()-10) x-=sens.ph*5;
            else {if(x<=10&sens.ph<=0)x-=sens.ph*5;
                if (x>=displayWidth-bitmap.getWidth()-10&sens.ph>=0)x-=sens.ph*5;}

        }
    }

    class BackGround{
        Bitmap bitmap,inputBitmap;
        Canvas canv;
        Paint mPaint;

        public BackGround(Canvas canvas){
            canv=canvas;
            Resources resources=context.getResources();
            inputBitmap= BitmapFactory.decodeResource(resources,R.drawable.field4);
            bitmap = Bitmap.createScaledBitmap(inputBitmap, displayWidth, displayHeight, true);
        }

        void draw(int x, int y){
            canv.drawBitmap(bitmap,x,y,mPaint);
            }

    }



}
 class Sens implements SensorEventListener{
     float ph;
     Sensor mySensor;
     SensorManager SM;
     Context context;
    public Sens(Context context){
        this.context=context;
        SM=(SensorManager)this.context.getSystemService(SENSOR_SERVICE);
        mySensor=SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
     void ex(float d){this.ph=d; }
     @Override
     public void onSensorChanged(SensorEvent event) {
         //if (event.values[0]>2)System.exit(0);
         //this.ph=event.values[0];
         ex(event.values[0]);
     }

     @Override
     public void onAccuracyChanged(Sensor sensor, int i) {

     }
 }



