import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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

/*
This is my RainMaker game where the helicopter is to go to each cloud and
* seed it so that it can rain in the dry land. This will allow ponds to grow
* to 100% so that the dry land can flourish.
* Unfortunately Win and Loss state was not working, and thus I took it out since
* it conflicted with the code. States were not working either, however I left it
* in due to it not conflicting and show that there was an attempt. I was not
* able to ge to wind and cloud states, but I am glad that my game works and that
* this project allowed me to get a better understanding of how OOP works.
* */

//---------------------------interfaces------------------//
interface Updatable {
    void update();
}

interface Observer {
    void updateObserve();

}

//---------------------------game text--------------------------//
class GameText extends GameObject implements Updatable {
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

//---------------------------Helicopter States-------------------------//
/* Make it so that Helicopter is able to move only in Ready state. States
* can only be changed on the HeliPad*/
interface HeliState {
    void Ignition();

    int BladeState(int bladeSpeed);

    int fuel(int fuel);

    double turnLeft(double rotateHeli);

    double turnRight(double rotateHeli);

    double moveUp(double speed);

    double slowDown(double speed);

}

class OffState implements HeliState {
    private Helicopter helicopter;

    public OffState(Helicopter helicopter) {
        this.helicopter = helicopter;
    }

    public void Ignition() {
        helicopter.changeState(new StartingState(helicopter));

    }

    public int BladeState(int bladeSpeed) {
        return bladeSpeed;
    }

    public int fuel(int fuel) {
        return fuel;
    }

    @Override
    public double turnLeft(double rotateHeli) {
        return rotateHeli;
    }

    @Override
    public double turnRight(double rotateHeli) {
        return rotateHeli;
    }


    public double moveUp(double speed) {
        return speed;
    }

    public double slowDown(double speed) {
        return speed;
    }
}

class StartingState implements HeliState {

    private Helicopter helicopter;

    StartingState(Helicopter helicopter) {
        this.helicopter = helicopter;
    }


    public void Ignition() {
        helicopter.changeState(new StoppingState(helicopter));
    }

    public int BladeState(int bladeSpeed) {

        if (bladeSpeed < 20)
            bladeSpeed++;

        if (bladeSpeed == 20)
            helicopter.changeState(new ReadyState(helicopter));

        return bladeSpeed;
    }

    public int fuel(int fuel) {
        fuel = fuel - 2;
        return fuel;
    }

    @Override
    public double turnLeft(double rotateHeli) {
        return rotateHeli;
    }

    @Override
    public double turnRight(double rotateHeli) {
        return rotateHeli;
    }


    @Override
    public double moveUp(double speed) {
        return speed;
    }

    @Override
    public double slowDown(double speed) {
        return speed;
    }
}

class StoppingState implements HeliState {

    private Helicopter helicopter;

    StoppingState(Helicopter helicopter) {
        this.helicopter = helicopter;
    }

    public void Ignition() {
        helicopter.changeState(new StartingState(helicopter));
    }

    public int BladeState(int bladeSpeed) {
        if (bladeSpeed > 0)
            bladeSpeed--;

        if (bladeSpeed == 0)
            helicopter.changeState(new OffState(helicopter));

        return bladeSpeed;
    }

    @Override
    public int fuel(int fuel) {
        return fuel;
    }

    @Override
    public double turnLeft(double rotateHeli) {
        return rotateHeli;
    }

    @Override
    public double turnRight(double rotateHeli) {
        return rotateHeli;
    }


    @Override
    public double moveUp(double speed) {
        return speed;
    }

    @Override
    public double slowDown(double speed) {
        return speed;
    }
}

class ReadyState implements HeliState {
    private Helicopter helicopter;

    ReadyState(Helicopter helicopter) {
        this.helicopter = helicopter;
    }

    public void Ignition() {
        helicopter.changeState(new StoppingState(helicopter));

    }

    public int BladeState(int bladeSpeed) {
        bladeSpeed = 20;
        return bladeSpeed;
    }

    @Override
    public int fuel(int fuel) {
        if (fuel > 0) {
            fuel = fuel - 2;
        }
        return fuel;
    }

    @Override
    public double turnLeft(double rotateHeli) {
        rotateHeli += 15;
        return rotateHeli;
    }

    @Override
    public double turnRight(double rotateHeli) {
        rotateHeli -= 15;
        return rotateHeli;
    }

    @Override
    public double moveUp(double speed) {
        speed += .1;
        if (speed > 10) {
            speed = 10;
        }
        return speed;
    }

    @Override
    public double slowDown(double speed) {
        speed -= .1;
        if (speed < -2) {
            speed = -2;
        }
        return speed;
    }
}

//-------------------Creating the Helicopter-----------------------//
/*Created the helicopter using JavaFx, where each shape was made and the helibody
* is used to call of the parts together */
class HeliBody extends Group {

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

    public HeliBody() {
        super();

        rightLeg.setTranslateX(-LEGX - 25);
        rightLeg.setTranslateY(-LEGY - 23);
        rightLeg.setRotate(90);

        leftLeg.setTranslateX(LEGX - 20);
        leftLeg.setTranslateY(LEGY + 30);
        leftLeg.setRotate(90);

        cabin.setTranslateY(BODY - 25);
        cockpit.setTranslateY(BODY - 5);
        cockpit.setRotate(180);

        tail.setTranslateX(TAIL - 48);
        tail.setTranslateY(TAIL - 70);
        tail.setRotate(90);

        rotor.setTranslateX(ROTOR - 43);
        rotor.setTranslateY(ROTOR - 75);
        rotor.setRotate(90);

        getChildren().addAll(rightLeg, leftLeg, cabin, cockpit, tail, rotor);
    }
}

class HeliCabin extends Circle {
    public HeliCabin(Color componentColor) {
        super(20, componentColor);
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

class HeliLeg extends Rectangle {
    public HeliLeg(Color componentColor) {
        super(45, 3);
        setFill(componentColor);
        setTranslateX(-getWidth() / 2);
    }
}

class HeliTail extends Rectangle {
    public HeliTail(Color componentColor) {
        super(45, 10);
        setFill(componentColor);
        setTranslateX(-getWidth() / 2);

    }
}

class HeliRotor extends Rectangle {
    public HeliRotor(Color componentColor) {
        super(20, 3);
        setFill(componentColor);
        setTranslateX(-getWidth() / 2);

    }
}

//------------------------helicopter blade---------------------//
/*Blade is able to spin, but since State is not working, blade wont spin*/
class HeliBlade extends GameObject implements Updatable {

    int bladeSpeed = 0;
    private double rotateBlade;

    public HeliBlade() {
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
        this.getChildren().addAll(blade1, blade2);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                HeliBlade.this.setRotate(HeliBlade.this.getRotate() + bladeSpeed);
            }
        };
        timer.start();
    }

    public void setRotateBlade(double r) {
        this.rotateBlade = r;
    }
}

//-------------------------Helicopter----------------------//
/* Helicopter is able to move and seed cloud*/
class Helicopter extends GameObject {
    double bladeRotation = 0;
    HeliBlade blades = new HeliBlade();
    HeliBody heliBody = new HeliBody();
    HeliState heliState;
    int bladeSpeed = 0;
    private boolean onHelipad = true;
    private boolean ignition = false;
    private boolean onCloud;
    private double direction = 0;
    private double rotateHeli;
    private double speed = 0;
    private int currentCloudNumber = -1;
    private int fuel = 250000;
    private GameBoundBox boundingBox;
    private GameText fuelText;
    private Position currentPosition;
    private Position startPosition;

    public Helicopter(Position startPosition) {
        this.startPosition = startPosition;
        this.currentPosition = startPosition;
        buildHelicopter();
        initHelicopterPosition(startPosition);
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
        this.boundingBox = new GameBoundBox(heliBounds);

        add(this.fuelText);
        this.fuelText.setTranslateY(this.fuelText.getTranslateY() - 30);
        this.fuelText.setTranslateX(this.fuelText.getTranslateX() - 30);
    }

    private void initHelicopterPosition(Position startPosition) {
        this.setTranslateX(startPosition.xPos());
        this.setTranslateY(startPosition.yPos());
    }

    @Override
    public void update() {


        blades.setRotateBlade(bladeRotation);
        bladeRotation = heliState.BladeState(bladeSpeed);

        if (fuel > 0) {
            moveHelicopter();
            if (ignition) {
                fuelText.updateText("F:" + fuel);
            }
        }
        checkCollision();
        this.boundingBox.setPosition(currentPosition);

    }

    public void changeState(HeliState heliState) {
        this.heliState = heliState;
    }

    public void startEngine() {
        ignition = !ignition;
        speed = 0;
    }

    public void speedUp() {
        speed += .1;
        if (speed > 10) {
            speed = 10;
        }
        //speed = heliState.moveUp(speed);
    }

    public void slowDown() {
        speed -= .1;
        if (speed < -2) {
            speed = -2;
        }
        //speed = heliState.slowDown(speed);
    }

    public void turnLeft() {
        rotateHeli = heliState.turnLeft(rotateHeli);
    }

    public void startingHeli() {
        if (speed >= -0.05 || speed <= 0.05) {
            heliState.Ignition();
        }
    }

    public void turnHelicopter(double turnAmount) {
        setRotate(getRotate() - turnAmount);
        direction = (direction + Math.toRadians(turnAmount)) % 360;
        reshapeBoundBox(turnAmount);

    }

    private void moveHelicopter() {
        if (fuel > 0) {
            setTranslateX(this.getTranslateX() + Math.sin(direction) * speed);
            setTranslateY(this.getTranslateY() + Math.cos(direction) * speed);
            updateBoundBoxPosition();
            runFuel();

        }
    }

    public void runFuel() {
        if (fuel > 0) {
            int fuelBurned = 5 + (int) (speed * 10);
            fuel -= fuelBurned;
        }
        if (fuel <= 0) {
            fuel = 0;
            fuelText.updateText("F:" + fuel);
            ignition = false;
        }
    }

    public boolean getIgnition() {
        return ignition;
    }

    public double getSpeed() {
        return speed;
    }

    private void updateBoundBoxPosition() {
        this.currentPosition = new Position(
                this.getTranslateX(),
                this.getTranslateY());
        this.boundingBox.setPosition(currentPosition);
    }

    public void toggleBoundBoxDisplay() {
        boundingBox.toggle();
    }

    public GameBoundBox getBoundingBox() {
        return boundingBox;
    }

    private void reshapeBoundBox(double turnAmount) {

    }

    private void checkCollision() {
        for (Cloud cloud : CloudArrayList.getCloudList()) {
            boolean collidingWithCloud = boundingBox.getInvisibleBoundBox().
                    intersects(cloud.getBoundingBox().getInvisibleBoundBox());
            if (collidingWithCloud) {
                this.onCloud = true;
                this.currentCloudNumber = cloud.getCloudNumber();
                return;
            }
        }
        this.onCloud = false;
        this.currentCloudNumber = -1;
    }

    public void seedCloud() {
        if (this.onCloud) {

            for (Cloud cloud : CloudArrayList.getCloudList()) {
                if (cloud.getCloudNumber() == this.currentCloudNumber) {
                    cloud.activateSeeding();
                    break;
                }
            }
        }
    }

    public void reset() {
        initHelicopterPosition(this.startPosition);
        this.setRotate(0);
        this.speed = 0;
        this.direction = 0;
        this.fuel = 250000;
        fuelText.updateText("F:" + fuel);
        this.ignition = false;

    }


}

//-------------------------------HeliPad---------------------------------//
/* Helicopter will always start on Helipad. This is where states can be
* changed*/
class HeliPad extends GameObject {
    private Ellipse helipadCircle = new Ellipse(40, 40);
    private Rectangle helipadOutline = new Rectangle(100, 100);

    public HeliPad(Position startPosition) {
        initHelipadPosition(startPosition);

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

    private void initHelipadPosition(Position startPosition) {
        this.setTranslateX(startPosition.xPos());
        this.setTranslateY(startPosition.yPos());
    }

}

//--------------------------------Cloud----------------------//
/* Clouds will spawn 4 at a time. Helicopter is able to seed cloud so that
* when cloud is seeded, rain will start, allowing to fill ponds. We are also
* able to have it that when cloud is seeded, the closes pond will be the
* first to be filled. Pressing D will allow user to see the distance from cloud
* to pond*/
class Cloud extends GameObject implements Updatable, Observer {
    private ArrayList<Line> distanceLines;
    private boolean showDistanceLines;
    private Color cloudColor;
    private Ellipse cloudArrayListShape;
    private GameText cloudText;
    private GameBoundBox boundingBox;
    private int seedValue;
    private int cloudNumber;
    private Pond closestPond;
    private Position currentPosition;

    public Cloud(Position cloudArrayListStartPos) {
        this.cloudNumber = CloudArrayList.getCloudList().size();
        this.currentPosition = cloudArrayListStartPos;
        this.seedValue = 0;
        this.showDistanceLines = false;
        initCloudPosition(cloudArrayListStartPos);
        buildCloud();
        distanceLines = new ArrayList<>(CloudArrayList.getCloudList().size());
        for (Pond pond : PondArrayList.getPondList()) {
            Line cloudToPond = new Line(0,
                    0,
                    pond.getTranslateX() - this.getTranslateX(),
                    pond.getTranslateY() - this.getTranslateY());
            cloudToPond.setStroke(Color.TRANSPARENT);
            cloudToPond.setStrokeWidth(1);
            distanceLines.add(cloudToPond);
            this.getChildren().add(cloudToPond);
        }
    }

    private void buildCloud() {
        this.cloudColor = Color.WHITE;
        this.cloudArrayListShape = new Ellipse(0, 0, 40, 40);
        this.cloudArrayListShape.setFill(this.cloudColor);
        this.cloudText = new GameText(this.seedValue + "%", Color.BLACK);
        add(this.cloudArrayListShape);


        Rectangle cloudBounds = new Rectangle(this.getBoundsInLocal().getMinX(),
                this.getBoundsInLocal().getMinY(),
                this.getBoundsInLocal().getWidth(),
                this.getBoundsInLocal().getHeight());
        this.boundingBox = new GameBoundBox(cloudBounds);
        add(this.cloudText);
        this.cloudText.setTranslateX(-20);
        this.cloudText.setTranslateY(10);
    }

    private void initCloudPosition(Position cloudArrayListStartPos) {
        this.setTranslateX(cloudArrayListStartPos.xPos());
        this.setTranslateY(cloudArrayListStartPos.yPos());
    }

    @Override
    public void update() {
        if (seedValue > 30) {
            this.rain();
        }
        cloudText.updateText(seedValue + "%");
        this.boundingBox.setPosition(currentPosition);
        this.cloudColor = Color.rgb(255 - seedValue, 255 - seedValue,
                255 - seedValue);
        this.cloudArrayListShape.setFill(cloudColor);

        int cloudIndex = 0;
        for (Line distanceLine : this.distanceLines) {
            distanceLine.setEndX(PondArrayList.getPondList().get(cloudIndex).getTranslateX() - this.getTranslateX());
            distanceLine.setEndY(PondArrayList.getPondList().get(cloudIndex).getTranslateY() - this.getTranslateY());
            cloudIndex++;
        }
    }

    private void rain() {
        double closestPondDistance = Double.MAX_VALUE;
        for (Pond pond : PondArrayList.getPondList()) {
            if (getDistanceToPond(pond) < closestPondDistance && !pond.full()) {
                closestPondDistance = getDistanceToPond(pond);
                this.closestPond = pond;
            }
        }
        if (closestPond != null) {
            closestPond.fillPond(this.seedValue);
        } else {
            System.out.println("closest pond is null");
        }
    }

    private double getDistanceToPond(Pond pond) {
        double xDistance = this.getTranslateX() -
                pond.getTranslateX();
        double yDistance = this.getTranslateY() -
                pond.getTranslateY();
        double hypotenuseSqr = Math.pow(xDistance, 2) + Math.pow(yDistance, 2);
        double distance = Math.sqrt(hypotenuseSqr);
        return distance;
    }

    public int getCloudNumber() {
        return this.cloudNumber;
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
        Position cloudArrayListStartPos = new Position(Math.random() * RainMaker.APP_WIDTH,
                Math.random() * RainMaker.APP_HEIGHT * (2.0 / 3.0)
                        + RainMaker.APP_HEIGHT / 3.0);
        currentPosition = cloudArrayListStartPos;
        initCloudPosition(cloudArrayListStartPos);
        this.boundingBox.setPosition(cloudArrayListStartPos);
    }

    public GameBoundBox getBoundingBox() {
        return boundingBox;
    }

    public void toggleBoundBoxDisplay() {
        this.boundingBox.toggle();
    }

    public void toggleDistanceLines() {
        showDistanceLines = !showDistanceLines;
        for (Line distanceLine : distanceLines) {
            if (showDistanceLines) {
                distanceLine.setStroke(Color.GREEN);
            } else {
                distanceLine.setStroke(Color.TRANSPARENT);
            }

        }

    }
}

class CloudArrayList extends GameObject implements Updatable {
    private static ArrayList<Cloud> cloudList;
    private static CloudArrayList cloudArrayList = new CloudArrayList(4);

    private CloudArrayList(int numberOfCloudArrayList) {
        cloudList = new ArrayList<>(numberOfCloudArrayList);
        for (int i = 0; i < numberOfCloudArrayList; i++) {
            Position cloudArrayListStartPos = new Position(
                    Math.random() * RainMaker.APP_WIDTH,
                    Math.random() * RainMaker.APP_WIDTH * (2.0 / 3.0)
                            + RainMaker.APP_HEIGHT / 3.0);
            Cloud c = new Cloud(cloudArrayListStartPos);
            cloudList.add(c);
            this.getChildren().add(c);
        }
    }

    public static ArrayList<Cloud> getCloudList() {
        return cloudList;
    }

    public static void reset() {
        for (Cloud cloud : cloudList) {
            cloud.reset();
        }
    }

    public static void toggleBoundBoxDisplay() {
        for (Cloud cloud : cloudList) {
            cloud.toggleBoundBoxDisplay();
        }
    }

    public static CloudArrayList getCloudArrayList() {
        return cloudArrayList;
    }

    public static void toggleDistanceLines() {
        for (Cloud cloud : cloudList) {
            cloud.toggleDistanceLines();
        }
    }

    public void update() {
        for (Cloud cloud : this.cloudList)
            cloud.update();
    }
}

//------------------------------Pond-------------------------------------//
/* Pond will need to be filled so that the dry land will not be dry anymore*/
class Pond extends GameObject implements Updatable {
    int pondFill;
    private boolean isFull = false;
    private Ellipse pondArrayListShape;
    private GameText pondText;
    private int pondFillClock;


    public Pond(Position startPosition) {
        initPondPosition(startPosition);
        buildPond();
        this.pondFill = (int) (Math.random() * 50);
    }

    private void buildPond() {
        this.pondFill = 0;
        this.pondFillClock = 0;
        this.pondArrayListShape = new Ellipse(0, 0, 20, 20);
        this.pondText = new GameText(this.pondFill + "%", Color.WHITE);
        this.pondArrayListShape.setFill(Color.BLUE);
        add(this.pondArrayListShape);
        add(this.pondText);

        this.pondText.setTranslateX(-10);
        this.pondText.setTranslateY(10);
    }

    private void initPondPosition(Position startPosition) {
        this.setTranslateX(startPosition.xPos());
        this.setTranslateY(startPosition.yPos());
    }
    public void fillPond(int cloudArrayListSeedValue) {
        if (this.pondFill < 100) {
            pondFillClock += cloudArrayListSeedValue / 10;
            if (pondFillClock % 100 < pondFillClock) {
                this.pondFill += 1;
                pondFillClock = pondFillClock % 100;
            }
        } else {
            pondFill = 100;
            isFull = true;
        }
    }
    @Override
    public void update() {
        if (true) {
            pondText.updateText(pondFill + "%");
        }
    }
    public void reset() {
        this.pondFill = 0;
        this.isFull = false;
        pondText.updateText(pondFill + "%");
        Position pondArrayListStartPos =
                new Position(Math.random() * RainMaker.APP_WIDTH,
                        Math.random() * RainMaker.APP_HEIGHT * (2.0 / 3.0)
                                + RainMaker.APP_HEIGHT / 3.0);
        initPondPosition(pondArrayListStartPos);
    }

    public boolean full() {
        return this.isFull;
    }
}

class PondArrayList extends GameObject implements Updatable {
    private static ArrayList<Pond> pondList;
    private static PondArrayList pondArrayList = new PondArrayList(3);

    PondArrayList(int numberOfPondArrayList) {
        pondList = new ArrayList<>(numberOfPondArrayList);
        for (int i = 0; i < numberOfPondArrayList; i++) {
            Position pondArrayListStartPos =
                    new Position(Math.random() * RainMaker.APP_WIDTH,
                            Math.random() * RainMaker.APP_WIDTH);
            Pond p = new Pond(pondArrayListStartPos);
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

    public static PondArrayList getPondArrayList() {
        return pondArrayList;
    }

    public void update() {
        for (Pond pond : this.pondList)
            pond.update();
    }
}

//------------------------------Bound Box for Objects----------------------//
/* By pressing B, users can see the bounds of the game objects. Allows users
* to know where they need to be to press space bar and start seeding the clouds*/
class GameBoundBox implements Updatable {
    private boolean displayBoundBox = false;
    private BoundingBox invisibleBoundBox;
    private Rectangle visibleBoundBox;

    public GameBoundBox(Rectangle bounds) {
        this.visibleBoundBox = bounds;
        this.invisibleBoundBox =
                new BoundingBox(bounds.getBoundsInLocal().getMinX(),
                        bounds.getBoundsInLocal().getMinY(),
                        bounds.getBoundsInLocal().getWidth(),
                        bounds.getBoundsInLocal().getHeight());
        this.visibleBoundBox.setFill(Color.TRANSPARENT);
        this.visibleBoundBox.setStroke(Color.TRANSPARENT);

    }
    @Override
    public void update() {

    }
    public void toggle() {
        displayBoundBox = !displayBoundBox;
        if (displayBoundBox) {
            visibleBoundBox.setStroke(Color.RED);
        } else {
            visibleBoundBox.setStroke(Color.TRANSPARENT);
        }
    }
    public void setPosition(Position newPosition) {
        this.visibleBoundBox.setTranslateX(newPosition.xPos());
        this.visibleBoundBox.setTranslateY(newPosition.yPos());


        this.invisibleBoundBox =
                new BoundingBox(visibleBoundBox.getTranslateX() - (visibleBoundBox.getWidth() / 2),
                        visibleBoundBox.getTranslateY() - (visibleBoundBox.getHeight() / 2),
                        visibleBoundBox.getBoundsInLocal().getWidth(),
                        visibleBoundBox.getBoundsInLocal().getHeight());

    }
    public Rectangle getVisibleBoundBox() {
        return visibleBoundBox;
    }

    public BoundingBox getInvisibleBoundBox() {
        return invisibleBoundBox;
    }
}

//-----------------------------Background Object------------------------------//
/* Gives game the dry desert background*/
class BackgroundObject extends GameObject {
    private static BackgroundImage backgroundImage;
    private static BackgroundObject backgroundObject = new BackgroundObject();

    private BackgroundObject() {
        backgroundImage = new BackgroundImage(new Image("a2_background.png",
                RainMaker.APP_WIDTH, RainMaker.APP_HEIGHT, false, true),
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
record Position(double xPos, double yPos) {
}
//-------------------Game Logic-----------------------------------//
class Game extends Pane implements Updatable {
    private static Game game = new Game();
    HeliPad heliPad;
    Helicopter helicopter;
    Pond pond;


    AnimationTimer timer;
    int counter = 0;
    StringBuilder msg = new StringBuilder();
    Alert alert;

    private Game() {

    }

    public static Game getGame() {
        return game;
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

//----------------------------------Game Object-----------------------------//
abstract class GameObject extends Group implements Updatable {
    private Translate translate;
    private Rotate rotation;
    private Scale scale;

    public GameObject() {
        translate = new Translate();
        rotation = new Rotate();
        scale = new Scale();
        this.getTransforms().addAll(translate, rotation, scale);
    }

    void add(Node node) {
        this.getChildren().add(node);
    }

    @Override
    public void update() {
    }

}

//----------------------------Rain Maker------------------------------------//
/* This class extends to JavaFX application and thus allowing the users to see
* the initial start of our game.*/
public class RainMaker extends Application {
    public static final int APP_WIDTH = 800;
    public static final int APP_HEIGHT = 800;

    public void start(Stage stage) {
        GameWorld.getGameApp().start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class GameWorld extends Application {
    private static final GameWorld gameApp = new GameWorld();
    private Game game;
    private Scene scene;
    private Helicopter helicopter;
    private HeliPad heliPad;
    private static boolean heliEngineOn = false;

    private static Alert alert;
    private static AnimationTimer loop;

    private GameWorld() {
        initGameAndScene();
        InitObjects();
    }

    private void initGameAndScene() {
        game = Game.getGame();
        scene = new Scene(game, RainMaker.APP_WIDTH,
                RainMaker.APP_HEIGHT);
    }

    private void InitObjects() {
        Position heliStartPosition =
                new Position((double) RainMaker.APP_WIDTH / 2, (double) RainMaker.APP_WIDTH / 5);
        helicopter = new Helicopter(heliStartPosition);
        heliPad = new HeliPad(heliStartPosition);
    }

    public static GameWorld getGameApp() {
        return gameApp;
    }

    @Override
    public void start(Stage stage) {
        Stage(stage);
        createInputHandlers();
        callObjectsToGame();

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
    public void reset() {
        helicopter.reset();
        PondArrayList.reset();
        CloudArrayList.reset();
        game.reset();
    }

    public static boolean EngineState() {
        return heliEngineOn;
    }

    private void callObjectsToGame() {
        game.setBackground(BackgroundObject.getBackground());
        game.getChildren().add(PondArrayList.getPondArrayList());
        game.getChildren().add(CloudArrayList.getCloudArrayList());
        for (Cloud cloud : CloudArrayList.getCloudList()) {
            game.getChildren().add(cloud.getBoundingBox().getVisibleBoundBox());
        }
        game.getChildren().add(heliPad);
        game.getChildren().add(helicopter);
        game.getChildren().add(helicopter.getBoundingBox().getVisibleBoundBox());
    }

    private void Stage(Stage stage) {
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
                if (event.getCode() == KeyCode.B) {
                    helicopter.toggleBoundBoxDisplay();
                    CloudArrayList.toggleBoundBoxDisplay();
                }
                if (event.getCode() == KeyCode.I) {
                    if (Math.abs(helicopter.getSpeed()) <= 0.1) {
                        helicopter.startingHeli();
                        helicopter.startEngine();
                    }
                }
                if (event.getCode() == KeyCode.R) {
                    reset();
                }
                if (event.getCode() == KeyCode.D) {
                    CloudArrayList.toggleDistanceLines();
                }
            }
        });
    }
}

