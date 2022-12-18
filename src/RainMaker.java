import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.Node;
import javafx.scene.Group;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;

interface Updatable {
    void update();
}
interface Observer {
    void updateObserve();

}
class GameText extends GameObject implements Updatable{
    private Text text;
    public GameText(String text, Color color) {
        this.text = new Text(text);
        this.text.setScaleY(-1);
        this.text.setFill(color);
        this.text.setFont(Font.font(20));
        this.getChildren().add(this.text);
    }
    public void updateText(String s) {
        text.setText(s);
    }
}

class GameBoundingBox implements Updatable{
    private Rectangle visibleBoundingBox;
    private BoundingBox invisibleBoundingBox;
    private boolean displayBoundingBox = false;

    public GameBoundingBox (Rectangle bounds) {
        this.visibleBoundingBox = bounds;
        System.out.println(bounds.getBoundsInLocal());
        this.invisibleBoundingBox =
                new BoundingBox(bounds.getBoundsInLocal().getMinX(),
                        bounds.getBoundsInLocal().getMinY(),
                        bounds.getBoundsInLocal().getWidth(),
                        bounds.getBoundsInLocal().getHeight());
        this.visibleBoundingBox.setFill(Color.TRANSPARENT);
        this.visibleBoundingBox.setStroke(Color.TRANSPARENT);

    }
    public Rectangle getVisibleBoundingBox() {
        return visibleBoundingBox;
    }

    public BoundingBox getInvisibleBoundingBox() {
        return invisibleBoundingBox;
    }

    public boolean isDisplayBoundingBox() {
        return displayBoundingBox;
    }

    @Override
    public void update() {

    }

    public void toggle() {
        //System.out.println("toggle called for " + this);
        displayBoundingBox = !displayBoundingBox;
        if(displayBoundingBox) {
            visibleBoundingBox.setStroke(Color.RED);
        }
        else {
            visibleBoundingBox.setStroke(Color.TRANSPARENT);
        }
    }

    public void setPosition(Position newPosition) {
        this.visibleBoundingBox.setTranslateX(newPosition.xPos());
        this.visibleBoundingBox.setTranslateY(newPosition.yPos());


        this.invisibleBoundingBox =
                new BoundingBox(visibleBoundingBox.getTranslateX()-(visibleBoundingBox.getWidth()/2),
                        visibleBoundingBox.getTranslateY()-(visibleBoundingBox.getHeight()/2),
                        visibleBoundingBox.getBoundsInLocal().getWidth(),
                        visibleBoundingBox.getBoundsInLocal().getHeight());

    }
}
abstract class HeliState{
    Helicopter helicopter;
    int MaxSpeed = 20;
    int delayCounter = 0;

    HeliState(Helicopter helicopter){

        this.helicopter = helicopter;

    }
    abstract void Ignition();

    abstract int BladeState(int bladeSpeed);

}

class OffState extends HeliState{

    OffState(Helicopter helicopter) {
        super(helicopter);

    }

    @Override
    void Ignition() {
        helicopter.changeState(new StartingState(helicopter));

    }

    @Override
    int BladeState(int bladeSpeed) {
        return 0;
    }
} // end off

class StartingState extends HeliState{


    StartingState(Helicopter helicopter) {
        super(helicopter);
    }

    @Override
    void Ignition() {
        helicopter.changeState(new StoppingState(helicopter));
    }

    @Override
    int BladeState(int bladeSpeed) {

        if (bladeSpeed < MaxSpeed)
            bladeSpeed++;

        if (bladeSpeed == MaxSpeed)
            helicopter.changeState(new ReadyState(helicopter));

        return bladeSpeed;
    }
}// end Starting

class StoppingState extends HeliState{


    StoppingState(Helicopter helicopter) {
        super(helicopter);
    }

    @Override
    void Ignition() {
        helicopter.changeState(new StartingState(helicopter));
    }

    @Override
    int BladeState(int bladeSpeed) {
        if (bladeSpeed > 0)
            bladeSpeed--;

        if (bladeSpeed == 0)
            helicopter.changeState(new OffState(helicopter));

        return 0;
    }
}

class ReadyState extends HeliState{


    ReadyState(Helicopter heli) {
        super(heli);
    }

    @Override
    void Ignition() {
        helicopter.changeState(new StoppingState(helicopter));

    }

    @Override
    int BladeState(int bladeSpeed) {

        return MaxSpeed;
    }
}

class HeliBody extends Group{

    private static final int LEGX = -25;
    private static final int LEGY = -25;
    private static final int BODY = 25;

    private static final int TAIL = 25;
    private static final int ROTOR = 25;

    HeliLeg rightLeg = new HeliLeg(Color.GRAY);
    HeliLeg leftLeg = new HeliLeg(Color.GRAY);

    HeliCabin cabin = new HeliCabin(Color.RED);
    HeliCockpit cockpit = new HeliCockpit();
    HeliTail tail = new HeliTail(Color.RED);
    HeliRotor rotor = new HeliRotor(Color.GRAY);

    public HeliBody(){
        super();

        rightLeg.setTranslateX(-LEGX -25 );
        rightLeg.setTranslateY(-LEGY-23);
        rightLeg.setRotate(90);

        leftLeg.setTranslateX(LEGX - 20);
        leftLeg.setTranslateY(LEGY + 30);
        leftLeg.setRotate(90);

        cabin.setTranslateY(BODY -25);
        cockpit.setTranslateY(BODY-5);
        cockpit.setRotate(180);

        tail.setTranslateX(TAIL-48);
        tail.setTranslateY(TAIL-70);
        tail.setRotate(90);

        rotor.setTranslateX(ROTOR-43);
        rotor.setTranslateY(ROTOR-75);
        rotor.setRotate(90);

        getChildren().addAll(rightLeg,leftLeg,cabin,cockpit, tail, rotor);
    }
}
class HeliCabin extends Circle {
    public HeliCabin(Color componentColor){
        super(20,componentColor);
    }
}
class HeliCockpit extends Arc {

    public HeliCockpit() {
        super(
                0,
                0,
                13,
                13,
                0,
                180);
        setFill(Color.GRAY);
    }
}
class HeliLeg extends Rectangle{
    public HeliLeg(Color componentColor){
        super(45, 3);
        setFill(componentColor);
        setTranslateX(-getWidth()/2);
    }
}
class HeliTail extends Rectangle{
    public HeliTail(Color componentColor){
        super(45,10);
        setFill(componentColor);
        setTranslateX(-getWidth()/2);

    }
}
class HeliRotor extends Rectangle{
    public HeliRotor(Color componentColor){
        super(20,3);
        setFill(componentColor);
        setTranslateX(-getWidth()/2);

    }
}
class HeliBlade extends GameObject implements Updatable{
    double rotation = 0;
    int counter = 0;
    int bladeSpeed = 0;
    private double rotateBlade;
    public HeliBlade(){
        Line blade1 = new Line();
        blade1.setStartX(0);
        blade1.setStartY(0);
        blade1.setEndX(-25);
        blade1.setEndY(25);
        blade1.setStrokeWidth(2);
        Line blade2 = new Line();
        blade2.setStartX(0);
        blade2.setStartY(0);
        blade2.setEndX(25);
        blade2.setEndY(-25);
        blade2.setStrokeWidth(2);
        this.getChildren().addAll(blade1,blade2);

       AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                HeliBlade.this.setRotate(HeliBlade.this.getRotate() + bladeSpeed);
            }
        };
        timer.start();
    }
    public void setRotateBlade(double r){
        this.rotateBlade = r;
    }

    public double getRotate(double v) {
        return getRotate();
    }
}
class Helicopter extends GameObject {
    private GameBoundingBox boundingBox;
    private GameText fuelText;
    private Position startPosition;
    private Position currentPosition;
    private int fuel = 250000;
    private double direction = 0;
    private double posX = 0;
    private double posY = 0;
    double rotation = 0;
    private double speed = 0;
    private boolean onHelipad = true;
    private boolean ignition = false;
    private boolean onCloud;
    private int currentCloudNumber = -1;
    double bladeRotation = 0;
    HeliBlade blades = new HeliBlade();
    HeliBody heliBody = new HeliBody();
    HeliState heliState;
    int bladeSpeed = 0;

    public Helicopter(Position startPosition) {
        this.startPosition = startPosition;
        this.currentPosition = startPosition;
        buildHelicopter();
        initializeHelicopterPosition(startPosition);
        this.boundingBox.setPosition(currentPosition);
        heliState = new OffState(this);

    }
    private void buildHelicopter() {
        add(heliBody);
        add(blades);

        this.fuelText = new GameText("F:" + fuel, Color.YELLOW);

        Rectangle heliBounds = new Rectangle(this.getBoundsInLocal().getMinX(),
                this.getBoundsInLocal().getMinY(),
                this.getBoundsInLocal().getWidth(),
                this.getBoundsInLocal().getHeight());
        this.boundingBox = new GameBoundingBox(heliBounds);

        add(this.fuelText);
        this.fuelText.setTranslateY(this.fuelText.getTranslateY() - 30);
        this.fuelText.setTranslateX(this.fuelText.getTranslateX() - 30);
    }

    private void initializeHelicopterPosition(Position startPosition) {
        this.setTranslateX(startPosition.xPos());
        this.setTranslateY(startPosition.yPos());
    }

    @Override
    public void update() {

        blades.setRotateBlade(bladeRotation);
        bladeRotation = heliState.BladeState(bladeSpeed);

        if(fuel > 0) {
            moveHelicopter();
            if (ignition) {
                fuelText.updateText("F:" + fuel);
            }
        }
        checkCollision();
        this.boundingBox.setPosition(currentPosition);

    }

    public void HeliBladeTest(){
        blades.update();
    }
    public void changeState(HeliState heliState){
        this.heliState = heliState;
    }
    public void startEngine() {
        ignition = !ignition;
        speed = 0;
    }

    public double spinBlades(double speed){
        if(blades.getRotate()<2000){
            blades.getRotate(blades.getRotate()+speed);
        }else{
            blades.setRotate(2000);
        }
        System.out.println(bladeRotation);
        return bladeRotation;
    }

    public void speedUp() {
        speed += .1;
        if(speed > 10) {
            speed = 10;
        }

    }
    public void slowDown() {
        speed -= .1;
        if(speed < -2) {
            speed = -2;
        }

    }
    private void moveHelicopter() {
        if(fuel > 0) {
            setTranslateX(this.getTranslateX() + Math.sin(direction) * speed);
            setTranslateY(this.getTranslateY() + Math.cos(direction) * speed);
            updateBoundingBoxPosition();
            runFuel();

        }
    }
    public void runFuel() {
        if(fuel > 0) {
            int fuelBurned = 5 + (int)(speed * 10);
            fuel -= fuelBurned;
        }
        if(fuel <= 0) {
            fuel = 0;
            fuelText.updateText("F:" + fuel);
            ignition = false;
        }
    }

    private void updateBoundingBoxPosition() {
        this.currentPosition = new Position(
                this.getTranslateX(),
                this.getTranslateY());
        this.boundingBox.setPosition(currentPosition);
    }

    public void turnHelicopter(double turnAmount) {
        if(heliState instanceof ReadyState)
        setRotate(getRotate() - turnAmount);
        direction = (direction + Math.toRadians(turnAmount)) % 360;
        reshapeBoundingBox(turnAmount);

    }
    private void reshapeBoundingBox(double turnAmount) {

    }
    private void checkCollision() {
        for(Cloud cloud : Clouds.getCloudList()) {
            boolean collidingWithCloud = boundingBox.getInvisibleBoundingBox().
                    intersects(cloud.getBoundingBox().getInvisibleBoundingBox());
            if (collidingWithCloud) {
                this.onCloud = true;
                this.currentCloudNumber = cloud.getCloudNumber();
                return;
            }
        }
        this.onCloud = false;
        this.currentCloudNumber = -1;
    }
    public boolean getIgnition() {
        return ignition;
    }
    public void Ignition(){
        heliState.Ignition();
    }

    public double getSpeed() {
        return speed;
    }

    public void toggleBoundingBoxDisplay() {
        boundingBox.toggle();
    }

    public void seedCloud() {
        if(this.onCloud) {

            for(Cloud cloud : Clouds.getCloudList()) {
                System.out.println("test 1");
                if(cloud.getCloudNumber() == this.currentCloudNumber) {
                    System.out.println("test 2");
                    cloud.activateSeeding();
                    break;
                }
            }
        }
    }

    public void reset() {
        initializeHelicopterPosition(this.startPosition);
        this.setRotate(0);
        this.speed = 0;
        this.direction = 0;
        this.fuel = 250000;
        fuelText.updateText("F:" + fuel);
        this.ignition = false;

    }

    public GameBoundingBox getBoundingBox() {
        return boundingBox;
    }
}
class HeliPad extends GameObject {
    private Rectangle helipadOutline = new Rectangle(100,100);
    private Ellipse helipadCircle = new Ellipse(40,40);
    public HeliPad(Position startPosition) {
        initializeHelipadPosition(startPosition);

        this.helipadOutline.setFill(Color.TRANSPARENT);
        this.helipadOutline.setStroke(Color.WHITE);
        this.helipadOutline.setStrokeWidth(2);
        this.helipadCircle.setFill(Color.TRANSPARENT);
        this.helipadCircle.setStroke(Color.WHITE);
        this.helipadCircle.setStrokeWidth(2);


        this.helipadOutline.setTranslateX(this.helipadOutline.getX() -
                this.helipadOutline.getWidth() / 2);
        this.helipadOutline.setTranslateY(this.helipadOutline.getY() -
                this.helipadOutline.getHeight() / 2);

        add(this.helipadOutline);
        add(this.helipadCircle);
    }

    private void initializeHelipadPosition(Position startPosition) {
        this.setTranslateX(startPosition.xPos());
        this.setTranslateY(startPosition.yPos());
    }

}
class Clouds extends GameObject implements Updatable {
    private static Clouds clouds = new Clouds(5);
    private static ArrayList<Cloud> cloudList;


    private Clouds(int numberOfClouds) {
        cloudList = new ArrayList<>(numberOfClouds);
        for (int i = 0; i < numberOfClouds; i++) {
            Position cloudStartPosition = new Position(Math.random()*RainMaker.APP_WIDTH,
                    Math.random()*RainMaker.APP_WIDTH*(2.0/3.0)
                            + RainMaker.APP_HEIGHT/3.0);
            Cloud c = new Cloud(cloudStartPosition);
            cloudList.add(c);
            this.getChildren().add(c);
        }
    }
    public static ArrayList<Cloud> getCloudList() {
        return cloudList;
    }

    public static void reset() {
        for(Cloud cloud : cloudList) {
            cloud.reset();
        }
    }

    public static void toggleBoundingBoxDisplay() {
        for (Cloud cloud:cloudList) {
            cloud.toggleBoundingBoxDisplay();
        }
    }

    public static Clouds getClouds() {
        return clouds;
    }
    public static void toggleDistanceLines() {
        for(Cloud cloud: cloudList) {
            cloud.toggleDistanceLines();
        }
    }
    public void update() {
        for (Cloud cloud : this.cloudList)
            cloud.update();
    }
}
class Cloud extends GameObject implements Updatable, Observer{
    private ArrayList<Line> distanceLines;
    private GameText cloudText;
    private Ellipse cloudShape;
    private GameBoundingBox boundingBox;
    private Color cloudColor;
    private Position currentPosition;
    private int seedValue;
    private int cloudNumber;
    private Pond closestPond;
    private boolean showDistanceLines;
    public Cloud(Position cloudStartPosition) {
        this.cloudNumber = Clouds.getCloudList().size();
        this.currentPosition = cloudStartPosition;
        this.seedValue = 0;
        this.showDistanceLines = false;
        initializeCloudPosition(cloudStartPosition);
        buildCloud();
        distanceLines = new ArrayList<>(Clouds.getCloudList().size());
        for (Pond pond : Ponds.getPondList()) {
            Line cloudToPond = new Line(0,
                    0,
                    pond.getTranslateX()-this.getTranslateX(),
                    pond.getTranslateY()-this.getTranslateY());
            cloudToPond.setStroke(Color.TRANSPARENT);
            cloudToPond.setStrokeWidth(1);
            distanceLines.add(cloudToPond);
            this.getChildren().add(cloudToPond);
        }
    }

    private void buildCloud() {
        this.cloudColor = Color.WHITE;
        this.cloudShape = new Ellipse(0,0, 40,40);
        this.cloudShape.setFill(this.cloudColor);
        this.cloudText = new GameText(this.seedValue + "%", Color.BLACK);
        add(this.cloudShape);


        Rectangle cloudBounds = new Rectangle(this.getBoundsInLocal().getMinX(),
                this.getBoundsInLocal().getMinY(),
                this.getBoundsInLocal().getWidth(),
                this.getBoundsInLocal().getHeight());
        this.boundingBox = new GameBoundingBox(cloudBounds);
        add(this.cloudText);
        this.cloudText.setTranslateX(-20);
        this.cloudText.setTranslateY(10);
    }

    private void initializeCloudPosition(Position cloudStartPosition) {
        this.setTranslateX(cloudStartPosition.xPos());
        this.setTranslateY(cloudStartPosition.yPos());
    }

    @Override
    public void update() {
        if(seedValue > 30) {
            this.rain();
        }
        cloudText.updateText(seedValue + "%");
        this.boundingBox.setPosition(currentPosition);
        this.cloudColor = Color.rgb(255 - seedValue,255 - seedValue,
                255 - seedValue);
        this.cloudShape.setFill(cloudColor);

        int cloudIndex = 0;
        for(Line distanceLine : this.distanceLines) {
            distanceLine.setEndX(Ponds.getPondList().get(cloudIndex).getTranslateX()-this.getTranslateX());
            distanceLine.setEndY(Ponds.getPondList().get(cloudIndex).getTranslateY()-this.getTranslateY());
            cloudIndex++;
        }
    }

    private void rain() {
        double closestPondDistance = Double.MAX_VALUE;
        for(Pond pond : Ponds.getPondList()) {
            if(getDistanceToPond(pond) < closestPondDistance && !pond.full()) {
                closestPondDistance = getDistanceToPond(pond);
                this.closestPond = pond;
            }
        }
        if(closestPond != null) {
            closestPond.fillPond(this.seedValue);
        }
        else {
            System.out.println("closest pond is null");
        }
    }
    private double getDistanceToPond(Pond pond) {
        double xDistance = this.getTranslateX() -
                pond.getTranslateX();
        double yDistance = this.getTranslateY() -
                pond.getTranslateY();
        double hypotenuseSqrd = Math.pow(xDistance, 2) + Math.pow(yDistance, 2);
        double distance = Math.sqrt(hypotenuseSqrd);
        return distance;
    }

    public Ellipse getCloudShape() {
        return cloudShape;
    }
    public int getCloudNumber() {
        return this.cloudNumber;
    }

    public void toggleBoundingBoxDisplay() {
        this.boundingBox.toggle();
    }

    public void activateSeeding() {

        if (seedValue < 100) {
            seedValue++;
        }
    }
    @Override
    public void updateObserve() {

    }

    public void reset() {
        this.seedValue = 0;
        cloudText.updateText(seedValue + "%");
        this.cloudColor = Color.WHITE;
        Position cloudStartPosition = new Position(Math.random()*RainMaker.APP_WIDTH,
                Math.random()*RainMaker.APP_HEIGHT*(2.0/3.0)
                        + RainMaker.APP_HEIGHT/3.0);
        currentPosition = cloudStartPosition;
        initializeCloudPosition(cloudStartPosition);
        this.boundingBox.setPosition(cloudStartPosition);
    }

    public GameBoundingBox getBoundingBox() {
        return boundingBox;
    }
    public int getSeedValue() {
        return seedValue;
    }
    public void decrementSeedValue() {
        this.seedValue--;
    }

    public ArrayList<Line> getDistanceLines() {
        return distanceLines;
    }
    public void toggleDistanceLines() {
        showDistanceLines = !showDistanceLines;
        for (Line distanceLine : distanceLines) {
            if(showDistanceLines) {
                distanceLine.setStroke(Color.GREEN);
            }
            else {
                distanceLine.setStroke(Color.TRANSPARENT);
            }

        }

    }
}

class Ponds extends GameObject implements Updatable {
    private static Ponds ponds = new Ponds(3);
    private static ArrayList<Pond> pondList;

    private Ponds(int numberOfPonds) {
        pondList = new ArrayList<>(numberOfPonds);
        for (int i = 0; i < numberOfPonds; i++) {
            Position pondStartPosition =
                    new Position(Math.random()*RainMaker.APP_WIDTH,
                            Math.random()*RainMaker.APP_WIDTH);
            Pond p = new Pond(pondStartPosition);
            pondList.add(p);
            this.getChildren().add(p);
        }
    }

    public static ArrayList<Pond> getPondList() {
        return pondList;
    }

    public static void reset() {
        for (Pond pond : pondList) {
            pond.reset();
        }
    }
    public static Ponds getPonds() {
        return ponds;
    }

    public void update() {
        for (Pond pond : this.pondList)
            pond.update();
    }
}
class Pond extends GameObject implements Updatable{
    private GameText pondText;
    private Ellipse pondShape;
    //private Color pondColor;
    private int pondFill;
    private int pondFillClock;
    private boolean isFull = false;

    public Pond(Position startPosition) {
        initializePondPosition(startPosition);
        buildPond();
        this.pondFill = (int)(Math.random()*50);
    }
    private void buildPond() {
        this.pondFill = 0;
        this.pondFillClock = 0;
        this.pondShape = new Ellipse(0,0, 20,20);
        this.pondText = new GameText(this.pondFill + "%", Color.WHITE);
        this.pondShape.setFill(Color.BLUE);
        add(this.pondShape);
        add(this.pondText);

        this.pondText.setTranslateX(-10);
        this.pondText.setTranslateY(10);
    }

    private void initializePondPosition(Position startPosition) {
        this.setTranslateX(startPosition.xPos());
        this.setTranslateY(startPosition.yPos());
    }

    @Override
    public void update() {
        if(true) { // if being filled maybe?
            pondText.updateText(pondFill + "%");
        }
    }

    public void fillPond(int cloudSeedValue) {
        if(this.pondFill < 100) {
            pondFillClock += cloudSeedValue / 10;
            if(pondFillClock % 100 < pondFillClock) {
                this.pondFill += 1;
                pondFillClock = pondFillClock % 100;
            }
        }
        else {
            pondFill = 100;
            isFull = true;
        }
    }

    public void reset() {
        this.pondFill = 0;
        this.isFull = false;
        pondText.updateText(pondFill + "%");
        Position pondStartPosition =
                new Position(Math.random()*RainMaker.APP_WIDTH,
                        Math.random()*RainMaker.APP_HEIGHT*(2.0/3.0)
                                + RainMaker.APP_HEIGHT/3.0);
        initializePondPosition(pondStartPosition);
    }

    public boolean full() {
        return this.isFull;
    }
}

class BackgroundObject extends GameObject {
    private static BackgroundObject backgroundObject= new BackgroundObject();
    private static BackgroundImage backgroundImage;

    private BackgroundObject() {
        backgroundImage = new BackgroundImage(new Image("a2_background.png",
                RainMaker.APP_WIDTH,RainMaker.APP_HEIGHT,false,true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

    }

    public static BackgroundObject getBackgroundObject() {
        return backgroundObject;
    }
    public static Background getBackground() {
        return new Background(backgroundImage);
    }
}
class Game extends Pane implements Updatable {
    private static Game game = new Game();
    Helicopter helicopter;

    boolean OnHelipad = false;
    boolean overCloud = false;
    boolean showBorders = false;
    double bladeSpeed = 0;
    int counter = 0;
    int counter2 = 0;
    private Game () {

    }

    public static Game getGame() {
        return game;
    }
    public void increaseBladeSpeed(){
        counter2++;
        if(counter2 % 5 == 0){
            bladeSpeed+=2;
            helicopter.spinBlades(bladeSpeed);
        }
    }
    public void spinBlades() {
        if (GameApp.EngineState()) {
            helicopter.HeliBladeTest();
        }
    }
    @Override
    public void update() {
        for (Node n : getChildren()) {
            if (n instanceof Updatable) {
                ((Updatable) n).update();
            }
        }
    }
    public void reset() {
        System.out.println("reset game");
    }
}
abstract class GameObject extends Group implements Updatable {
    private Translate translate;
    private Rotate rotation;
    private Scale scale;
    public GameObject(){
        translate = new Translate();
        rotation = new Rotate();
        scale = new Scale();
        this.getTransforms().addAll(translate, rotation, scale);
    }
    void add (Node node) {
        this.getChildren().add(node);
    }

    @Override
    public void update() {
    }

}
public class RainMaker extends Application {
    public static final int APP_WIDTH = 800;
    public static final int APP_HEIGHT = 800;

    public void start(Stage stage) {
        GameApp.getGameApp().start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
class GameApp extends Application {
    private static final GameApp gameApp = new GameApp();
    private Game game;
    private Scene scene;
    private Helicopter helicopter;
    private HeliPad heliPad;
    private static boolean heliEngineOn = false;

    private GameApp () {
        initializeGameAndScene();
        InitializeGameObjects();
    }
    private void initializeGameAndScene() {
        game = Game.getGame();
        scene = new Scene(game, RainMaker.APP_WIDTH,
                RainMaker.APP_HEIGHT);
    }
    private void InitializeGameObjects() {
        Position heliStartPosition =
                new Position((double)RainMaker.APP_WIDTH/2, (double)RainMaker.APP_WIDTH/5);
        helicopter = new Helicopter(heliStartPosition);
        heliPad = new HeliPad(heliStartPosition);
    }
    public static GameApp getGameApp() {
        return gameApp;
    }
    public void reset() {
        helicopter.reset();
        Ponds.reset();
        Clouds.reset();
        game.reset();
    }
    @Override
    public void start(Stage stage) {
        setUpStage(stage);
        createInputHandlers();
        addObjectsToGame();

        AnimationTimer loop = new AnimationTimer() {
            double old = -1;
            double elapsedTime = 0;
            public void handle(long nano) {
                if (old < 0) old = nano;
                double delta = (nano - old) / 1e9;
                old = nano;
                elapsedTime += delta;
                game.update();
            }
        };
        loop.start();
        stage.show();
    }
    public static boolean EngineState(){
        return heliEngineOn;
    }
    private void addObjectsToGame() {
        game.setBackground(BackgroundObject.getBackground());
        game.getChildren().add(Ponds.getPonds());
        game.getChildren().add(Clouds.getClouds());
        for (Cloud cloud : Clouds.getCloudList()) {
            game.getChildren().add(cloud.getBoundingBox().getVisibleBoundingBox());
        }
        game.getChildren().add(heliPad);
        game.getChildren().add(helicopter);
        game.getChildren().add(helicopter.getBoundingBox().getVisibleBoundingBox());
    }
    private void setUpStage(Stage stage) {
        game.setScaleY(-1);
        stage.setScene(scene);
        stage.setTitle("RainMaker");
        scene.setFill(Color.BLACK);
    }

    private void createInputHandlers() {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (helicopter.getIgnition()) {
                    if (event.getCode() == KeyCode.UP) {
                        helicopter.speedUp();
                    }
                    if (event.getCode() == KeyCode.DOWN) {
                        helicopter.slowDown();
                    }
                    if (event.getCode() == KeyCode.LEFT) {
                        helicopter.turnHelicopter(-15);
                    }
                    if (event.getCode() == KeyCode.RIGHT) {
                        helicopter.turnHelicopter(15);
                    }
                    if (event.getCode() == KeyCode.SPACE) {
                        helicopter.seedCloud();
                    }
                }
                if (event.getCode() == KeyCode.I) {
                    if (Math.abs(helicopter.getSpeed()) <= 0.1) {
                        helicopter.Ignition();
                        helicopter.startEngine();
                    }
                }
                if (event.getCode() == KeyCode.B) {
                    helicopter.toggleBoundingBoxDisplay();
                    Clouds.toggleBoundingBoxDisplay();
                }
                if (event.getCode() == KeyCode.R) {
                    reset();
                }
                if (event.getCode() == KeyCode.D) {
                    Clouds.toggleDistanceLines();
                }
            }
        });
    }
}

record Position(double xPos, double yPos) {}