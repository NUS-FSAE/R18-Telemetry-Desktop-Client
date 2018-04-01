package sample;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.udpListener;
import javafx.animation.AnimationTimer;
import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class Main extends Application {
    private static final double TILE_SIZE = 200;
    private static final double MAX_OIL_PRESSURE = 550;
    private static final double LOW_OIL_PRESSURE = 70;
    private static final double MAX_BRAKE_PRESSURE = 5.0;

    private double rpm;
    private double oilPressure;
    private double fuelPressure;
    private double engTemp;
    private double oilTemp;
    private double battVolts;
    private double speed;
    private double throttle;
    private double brake;
    private String gear;
    private double brakeTempFront;
    private double brakeTempRear;

    udpListener listener;
    private byte[] data1 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private byte[] data2 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private byte[] data3 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private byte[] data4 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private byte[] data5 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private Label oilPressureLabel;

    private Gauge vehicleSpeedGauge;
    private Gauge rpmGauge;
    private Gauge engTempGauge;
    private Gauge oilTempGauge;
    private Gauge fuelGauge;
    private Gauge throttlePositionGauge;
    private Gauge brakePressureGauge;
    private Gauge brakeTempFrontGauge;
    private Gauge brakeTempRearGauge;

    private Tile vehicleSpeedTile;
    private Tile rpmTile;
    private Tile gearTile;
    private Tile oilPressTile;
    private Tile throttlePositionTile;
    private Tile fuelTile;
    private Tile engTempTile;
    private Tile batteryTile;
    private Tile oilTempTile;
    private Tile brakePressureTile;
    private Tile brakeTempFrontTile;
    private Tile brakeTempRearTile;

    @Override
    public void init() {

        //Vehicle Speed
        //
        //
        vehicleSpeedGauge = createGauge(Gauge.SkinType.SIMPLE_DIGITAL);
        vehicleSpeedGauge.setUnit("km/h");
        vehicleSpeedGauge.setMinValue(0.0);
        vehicleSpeedGauge.setMaxValue(120.0);
        vehicleSpeedGauge.setDecimals(0);
        vehicleSpeedTile = TileBuilder.create()
                .prefSize(TILE_SIZE, TILE_SIZE)
                .skinType(Tile.SkinType.CUSTOM)
                .title("Speed")
                .graphic(vehicleSpeedGauge)
                .build();

        // RPM
        //
        //
        rpmGauge = createGauge(Gauge.SkinType.DIGITAL);
        rpmGauge.setUnit("rpm");
        rpmGauge.setMinValue(0.0);
        rpmGauge.setMaxValue(12000.0);
        rpmGauge.setDecimals(0);
        rpmTile = TileBuilder.create()
                .prefSize(TILE_SIZE, TILE_SIZE)
                .skinType(Tile.SkinType.CUSTOM)
                .title("RPM")
                .graphic(rpmGauge)
                .build();

        // Gear Number
        //
        //
        gearTile = TileBuilder.create()
                .prefSize(TILE_SIZE, TILE_SIZE)
                .skinType(Tile.SkinType.CHARACTER)
                .title("Gear")
                .description("R")
                .build();

        // Oil Pressure
        oilPressTile = TileBuilder.create()
                .skinType(Tile.SkinType.GAUGE_SPARK_LINE)
                .prefSize(TILE_SIZE, TILE_SIZE)
                .title("Oil Pressure")
                .maxValue(MAX_OIL_PRESSURE)
                .animated(true)
                .textVisible(false)
                .unit("kPa")
                .unitColor(Color.GREEN)
                .averagingPeriod(25)
                .autoReferenceValue(true)
                .barColor(Tile.YELLOW_ORANGE)
                .barBackgroundColor(Color.rgb(255, 255, 255, 0.1))
                .sections(new eu.hansolo.tilesfx.Section(0, LOW_OIL_PRESSURE, Tile.RED),
                        new eu.hansolo.tilesfx.Section(LOW_OIL_PRESSURE, MAX_OIL_PRESSURE, Tile.GREEN))
                .sectionsVisible(true)
                .highlightSections(true)
                .strokeWithGradient(true)
                .gradientStops(new Stop(0.0, Tile.LIGHT_GREEN),
                        new Stop(0.33, Tile.LIGHT_GREEN),
                        new Stop(0.33, Tile.YELLOW),
                        new Stop(0.67, Tile.YELLOW),
                        new Stop(0.67, Tile.LIGHT_RED),
                        new Stop(1.0, Tile.LIGHT_RED))
                .value(252)
                .build();

        brakeTempFrontGauge = createGauge(Gauge.SkinType.SIMPLE_SECTION);
        brakeTempFrontGauge.setBarColor(Color.BLUE);
        brakeTempFrontGauge.setSections(new Section(300, 450, Tile.RED));
        brakeTempFrontGauge.setMaxValue(450);
        brakeTempFrontGauge.setMinValue(0);
        brakeTempFrontGauge.setDecimals(0);
        brakeTempFrontTile = TileBuilder.create()
                .prefSize(TILE_SIZE, TILE_SIZE)
                .skinType(Tile.SkinType.CUSTOM)
                .text("Brake Temperature Front")
                .graphic(brakeTempFrontGauge)
                .build();
        brakeTempFrontGauge.setValue(20);

        brakeTempRearGauge = createGauge(Gauge.SkinType.SIMPLE_SECTION);
        brakeTempRearGauge.setBarColor(Color.BLUE);
        brakeTempRearGauge.setSections(new Section(300, 450, Tile.RED));
        brakeTempRearGauge.setMaxValue(450);
        brakeTempRearGauge.setMinValue(0);
        brakeTempRearGauge.setDecimals(0);
        brakeTempRearTile = TileBuilder.create()
                .prefSize(TILE_SIZE, TILE_SIZE)
                .skinType(Tile.SkinType.CUSTOM)
                .text("Brake Temperature Rear")
                .graphic(brakeTempRearGauge)
                .build();
        brakeTempRearGauge.setValue(20);


        //Throttle position
        //
        //
        throttlePositionGauge = createGauge(Gauge.SkinType.CHARGE);
        throttlePositionGauge.setValue(0.5);
        throttlePositionTile = TileBuilder.create()
                .prefSize(TILE_SIZE * 2, TILE_SIZE)
                .skinType(Tile.SkinType.CUSTOM)
                .title("Throttle Position")
                .graphic(throttlePositionGauge)
                .build();

        // BRAKE PRESSURE
        //
        //
        brakePressureGauge = createGauge(Gauge.SkinType.CHARGE);
        brakePressureGauge.setValue(0.5);
        brakePressureTile = TileBuilder.create()
                .prefSize(TILE_SIZE * 2, TILE_SIZE)
                .skinType(Tile.SkinType.CUSTOM)
                .title("Brake Pressure")
                .graphic(brakePressureGauge)
                .build();

        // FUEL PRESSURE
        //
        //
        fuelGauge = createGauge(Gauge.SkinType.SLIM);
        fuelGauge.setBarColor(Color.YELLOWGREEN);
        //     FuelGauge.setSections(new Section(0, 280, Tile.RED));
        fuelGauge.setMaxValue(360);
        fuelGauge.setMinValue(0);
        fuelGauge.setUnit("kPa");
        fuelGauge.setDecimals(0);
        fuelTile = TileBuilder.create()
                .prefSize(TILE_SIZE, TILE_SIZE)
                .skinType(Tile.SkinType.CUSTOM)
                .title("Fuel Pressure")
                .graphic(fuelGauge)
                .build();
        fuelGauge.setValue(355);

        // OIL TEMPERATURE
        //
        //
        oilTempGauge = createGauge(Gauge.SkinType.SIMPLE_SECTION);
        oilTempGauge.setBarColor(Color.BLUE);
        oilTempGauge.setSections(new Section(100, 150, Tile.RED));
        oilTempGauge.setMaxValue(130);
        oilTempGauge.setMinValue(0);
        oilTempGauge.setDecimals(0);
        oilTempTile = TileBuilder.create()
                .prefSize(TILE_SIZE, TILE_SIZE)
                .skinType(Tile.SkinType.CUSTOM)
                .text("Oil Temperature")
                .graphic(oilTempGauge)
                .build();
        oilTempGauge.setValue(105);

        // ENGINE TEMPERATURE
        //
        //
        engTempGauge = createGauge(Gauge.SkinType.SIMPLE_SECTION);
        engTempGauge.setBarColor(Color.BLUE);
        engTempGauge.setSections(new Section(100, 150, Tile.RED));
        engTempGauge.setMaxValue(130);
        engTempGauge.setMinValue(0);
        engTempGauge.setDecimals(0);
        engTempTile = TileBuilder.create()
                .prefSize(TILE_SIZE, TILE_SIZE)
                .skinType(Tile.SkinType.CUSTOM)
                .text("Engine Temperature")
                .graphic(engTempGauge)
                .build();
        engTempGauge.setValue(98);

        // BATTERY VOLTS
        //
        //
        batteryTile = TileBuilder.create()
                .prefSize(TILE_SIZE, TILE_SIZE)
                .skinType(Tile.SkinType.GAUGE)
                .title("Battery Volts")
                .unit("V")
                .threshold(12.0)
                .maxValue(15)
                .minValue(8)
                .build();
        batteryTile.setValue(12.9);

        oilPressureLabel = new Label("nil");

        listener = new udpListener();
        Thread thread = new Thread(listener);
        thread.setDaemon(true);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                synchronized (udpListener.lock) {
                    if (listener.buffer1_isReceived()) {
                        data1 = listener.getBuffer1();
                        listener.setBuffer1_read();
                    }
                    if (listener.buffer2_isReceived()) {
                        data2 = listener.getBuffer2();
                        listener.setBuffer2_read();
                    }
                    if (listener.buffer3_isReceived()) {
                        data3 = listener.getBuffer3();
                        listener.setBuffer3_read();
                    }
                    if (listener.buffer4_isReceived()) {
                        data4 = listener.getBuffer4();
                        listener.setBuffer4_read();
                    }
                    if (listener.buffer5_isReceived()) {
                        data5 = listener.getBuffer5();
                        listener.setBuffer5_read();
                    }

                    rpm = ((data1[1] << 8) | data1[2] & 0xFF);
                    oilPressure = (((data1[3] << 8) | data1[4] & 0xFF )/10);
                    fuelPressure = ((data1[5] << 8 | data1[6] & 0xFF) / 10);
                    throttle = data1[7] / 100.0;
                    speed = data1[8];
                    brake = data2[1] / MAX_BRAKE_PRESSURE;
                    brakeTempFront = (data2[3] << 8 | data2[4] & 0xFF);
                    brakeTempRear = (data2[5] << 8 | data2[6] & 0xFF);
                    engTemp = (double) data3[1];
                    oilTemp = (double) data3[2];
                    gear = Integer.toString((int) data2[7]);
                    battVolts = (data3[3] & 0x000000FF) / 10.0;

                    rpmGauge.setValue(rpm);
                    oilPressTile.setValue(oilPressure);
                    fuelGauge.setValue(fuelPressure);
                    brakeTempFrontGauge.setValue(brakeTempFront);
                    brakeTempRearGauge.setValue(brakeTempRear);
                    throttlePositionGauge.setValue(throttle);
                    vehicleSpeedGauge.setValue(speed);
                    brakePressureGauge.setValue(brake);
                    engTempGauge.setValue(engTemp);
                    gearTile.setDescription(gear);
                    oilTempGauge.setValue(oilTemp);
                    batteryTile.setValue(battVolts); //convert to unsigned int
                }
            }

        };

        timer.start();

//        new Timer().schedule(
//                listener,10,10);
//                new TimerTask() {
//                    @Override
//                    public void run() {
//                        synchronized (udpListener.lock) {
//                            if(listener.buffer1_isReceived()) {
//                                data1 = listener.getBuffer1();
//                                listener.setBuffer1_read();
//                            }
//                            if(listener.buffer2_isReceived()) {
//                                data2 = listener.getBuffer2();
//                                listener.setBuffer2_read();
//                            }
//                            if(listener.buffer3_isReceived()) {
//                                data3 = listener.getBuffer3();
//                                listener.setBuffer3_read();
//                            }
//                            if(listener.buffer4_isReceived()) {
//                                data4 = listener.getBuffer4();
//                                listener.setBuffer4_read();
//                            }
//                            if(listener.buffer5_isReceived()) {
//                                data5 = listener.getBuffer5();
//                                listener.setBuffer5_read();
//                            }
//                            try {
//                                rpm = data1[1] << 8 | data1[2];
//                                oilPressure = data1[3] << 8 | data1[4];
//                                fuelPressure = (data1[5] << 8 | data1[6])/10.0;
//                                throttle = data1[7] / 100.0;
//                                speed = data1[8];
//                                brake = data2[1] / MAX_BRAKE_PRESSURE;
//                                engTemp = (double) data3[1];
//                                oilTemp = (double) data3[2];
//                                gear = Integer.toString((int)data2[6]);
//                                battVolts = (data3[3] & 0x000000FF) / 10.0;
//                            } catch (Exception e) {
//                                System.console().printf("No!!!!");
//                                //e.printStackTrace();
//                            }
//                            rpmGauge.setValue(rpm);
//                            oilPressTile.setValue(oilPressure);
////                            oilPressureLabel.setText(Double.toString(battVolts));
//                            fuelGauge.setValue(fuelPressure);
//                            throttlePositionGauge.setValue(throttle);
//                            vehicleSpeedGauge.setValue(speed);
//                            brakePressureGauge.setValue(brake);
//                            engTempGauge.setValue(engTemp);
////                            gearTile.setDescription(gear);
//                            oilTempGauge.setValue(oilTemp);
//                            batteryTile.setValue(battVolts); //convert to unsigned int
//                        }
//                    }
//                }, 10,10);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox rootNode = new VBox();
        HBox row_1 = new HBox();
        HBox row_2 = new HBox();
        HBox row_3 = new HBox();
        HBox row_4 = new HBox();

        row_1.getChildren().addAll(vehicleSpeedTile, rpmTile, gearTile);
        row_2.getChildren().addAll(brakeTempFrontTile, throttlePositionTile);
        row_3.getChildren().addAll(brakeTempRearTile, brakePressureTile);
        row_4.getChildren().addAll(batteryTile, engTempTile, oilTempTile);

        rootNode.getChildren().addAll(row_1, row_2, row_3, row_4);
        Scene scene = new Scene(rootNode, TILE_SIZE * 3, TILE_SIZE * 4);
        primaryStage.setTitle("Sample Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Gauge createGauge(final Gauge.SkinType TYPE) {
        return GaugeBuilder.create()
                .skinType(TYPE)
                .prefSize(TILE_SIZE, TILE_SIZE)
                .animated(true)
                //.title("")
                .unit("\u00B0C")
                .valueColor(Tile.FOREGROUND)
                .titleColor(Tile.FOREGROUND)
                .unitColor(Tile.FOREGROUND)
                .barColor(Tile.BLUE)
                .needleColor(Tile.FOREGROUND)
                .barColor(Tile.BLUE)
                .barBackgroundColor(Tile.BACKGROUND.darker())
                .tickLabelColor(Tile.FOREGROUND)
                .majorTickMarkColor(Tile.FOREGROUND)
                .minorTickMarkColor(Tile.FOREGROUND)
                .mediumTickMarkColor(Tile.FOREGROUND)
                .build();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
