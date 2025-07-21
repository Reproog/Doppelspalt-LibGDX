package io.doppelspalt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Main implements ApplicationListener {

    private Viewport viewport;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private Stage stage;
    private Skin skin;
    private Table table;

    private int lambda = 50;
    private int gitterD = 75;
    private int winkel = 90;

    private Label fpsLabel;
    private Slider lambdaSlider, dSlider, winkelSlider;
    private Label ratioLabel;

    private FullscreenButton fullButton;

    @Override
    public void create() {
        font = new BitmapFont(Gdx.files.internal("bitmap/c059.fnt"));

        camera = new OrthographicCamera();
        table = new Table();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        shapeRenderer = new ShapeRenderer();

        //viewport = new FitViewport(1920, 1080, camera);
        viewport = new ScreenViewport();
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        fullButton = new FullscreenButton(stage, skin);
        setupUI();
    }

    private void setupUI() {
        lambdaSlider = new Slider(10, 85, 1, false, skin);
        dSlider = new Slider(0, 150, 1, false, skin);
        winkelSlider = new Slider(0, 180, 1, false, skin);

        lambdaSlider.setValue(lambda);
        dSlider.setValue(gitterD);
        winkelSlider.setValue(winkel);

        Label.LabelStyle style = new Label.LabelStyle(skin.getFont("font"), Color.BLACK);
        style.font = font;

        ratioLabel = new Label("d/lambda = " + formatRatio(), style);

        lambdaSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                lambda = (int) lambdaSlider.getValue();
                updateRatio();
            }
        });

        dSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gitterD = (int) dSlider.getValue();
                updateRatio();
            }
        });

        winkelSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                winkel = (int) winkelSlider.getValue();
            }
        });


        table.top().left();
        table.pack();

        table.setPosition(10, viewport.getWorldHeight() - 10);

        if (viewport.getWorldHeight() > viewport.getWorldWidth()) {
            table.setWidth(viewport.getWorldWidth() - 20); 
        }

        fpsLabel = new Label("FPS: 0", style);

        //table.row();
        //table.add(fpsLabel).left().colspan(2).padTop(10);
        //table.row();



        Label lambdaLabel = new Label("Lambda", style);
        table.add(lambdaLabel).left().pad(5);

        table.add(lambdaSlider).width(300).pad(5);
        table.row();

        //Label dLabel = new Label("Slit distance d", style);
        Label dLabel = new Label("D", style);
        table.add(dLabel).left().pad(5);

        table.add(dSlider).width(300).pad(5);
        table.row();

        //Label winkelLabel = new Label("Angle of incidence", style);
        Label winkelLabel = new Label("Winkel", style);
        table.add(winkelLabel).left().pad(5);

        table.add(winkelSlider).width(300).pad(5);
        table.row();

        table.add(ratioLabel).colspan(2).padTop(10);
        table.row();

        TextButton resetButton = new TextButton("Reset", skin);
        resetButton.setSize(10, 10);
        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                lambdaSlider.setValue(50);   
                dSlider.setValue(75);       
                winkelSlider.setValue(90);   

                lambda = 50;
                gitterD = 75;
                winkel = 90;

                updateRatio();
            }
        });

        table.add(resetButton)
            .colspan(2)
            .padTop(10)
            .width(100)
            .height(30);
        table.row();


        stage.addActor(table);
        //stage.setDebugAll(true);



        //fullButton.addToStage(stage);
        //fullButton.toFront();
    }

    private void updateRatio() {
        ratioLabel.setText("d/lambda = " + formatRatio());
    }


    private String formatRatio() {
        double value = (double) gitterD / lambda;
        return formatDouble(value, 2);
    }

    private String formatDouble(double value, int decimals) {
        double scale = Math.pow(10, decimals);
        return String.valueOf(Math.round(value * scale) / scale);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        drawInterference();

        stage.act(delta);
        stage.draw();
    }

    private void drawInterference() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        int xSpalt = width / 3;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        int y1 = (height - gitterD) / 2;
        int y2 = (height + gitterD) / 2;

        drawCircularWaves(xSpalt, y1, width, height);
        drawCircularWaves(xSpalt, y2, width, height);
        shapeRenderer.end();




        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawBackgroundForSinus(xSpalt, height);
        drawWallWithSlits(xSpalt, height);

        shapeRenderer.setColor(Color.RED);
        drawIntersectionPoints(xSpalt, width, height);
        shapeRenderer.end();


        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        drawSinusWave(xSpalt, height);
        drawIncidentWave(xSpalt, height);
        shapeRenderer.end();
    }

    private void drawSinusWave(int breite, int hoehe) {
        double phi = Math.toRadians(winkel - 90);
        double amplitude = hoehe / 12.0;

        int lSinus;
        if (Math.abs(phi) < Math.atan(0.5 * hoehe / (double) breite)) {
            lSinus = (int) (breite / Math.cos(phi));
        } else {
            lSinus = (int) (0.5 * hoehe / Math.abs(Math.sin(phi)));
        }

        float[] points = new float[(lSinus - 1) * 4];

        for (int i = 0; i < lSinus - 1; i++) {
            double x0 = (i - lSinus);
            double y0 = amplitude * Math.sin(1.5 * Math.PI + 2 * Math.PI * ((i - lSinus) % lambda) / lambda);
            double x1 = (i + 1 - lSinus);
            double y1 = amplitude * Math.sin(1.5 * Math.PI + 2 * Math.PI * ((i + 1 - lSinus) % lambda) / lambda);

            float xStart = (float) (x0 * Math.cos(phi) + y0 * Math.sin(phi)) + breite;
            float yStart = (float) (-x0 * Math.sin(phi) + y0 * Math.cos(phi)) + hoehe / 2f;

            float xEnd = (float) (x1 * Math.cos(phi) + y1 * Math.sin(phi)) + breite;
            float yEnd = (float) (-x1 * Math.sin(phi) + y1 * Math.cos(phi)) + hoehe / 2f;

            int idx = i * 4;
            points[idx] = xStart;
            points[idx + 1] = yStart;
            points[idx + 2] = xEnd;
            points[idx + 3] = yEnd;
        }

        shapeRenderer.setColor(Color.RED);
        for (int i = 0; i < points.length; i += 4) {
            shapeRenderer.line(points[i], points[i + 1], points[i + 2], points[i + 3]);
        }
    }


    private void drawWallWithSlits(int xSpalt, int height) {
        int loch = 5;
        int ySpalt1 = (height - gitterD) / 2;
        int ySpalt2 = (height + gitterD) / 2;
        int balkenBreite = 5;

        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(xSpalt - 2, 0, balkenBreite, ySpalt1 - loch);
        shapeRenderer.rect(xSpalt - 2, ySpalt1 + loch, balkenBreite, gitterD - 2 * loch);
        shapeRenderer.rect(xSpalt - 2, ySpalt2 + loch, balkenBreite, height - ySpalt2 - loch);
    }


    private void drawBackgroundForSinus(int xSpalt, int height) {
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(0, 0, xSpalt - 5, height);
    }


    private void drawIncidentWave(int xSpalt, int height) {
        shapeRenderer.setColor(Color.BLACK);

        double phi = Math.toRadians(winkel);
        double sinPhi = Math.sin(phi);
        double cosPhi = Math.cos(phi);

        if (winkel == 90) {
            for (int i = xSpalt; i > 0; i -= lambda) {
                shapeRenderer.line(i, 0, i, height);
            }
        } else if (winkel == 0 || winkel == 180) {
            for (int i = (height / 2) % lambda; i <= height; i += lambda) {
                shapeRenderer.line(0, i, xSpalt, i);
            }
        } else {
            double dMin, dMax, d0;

            if (winkel < 90) {
                dMin = 0.0;
                double d1 = xSpalt * sinPhi + (height / 2.0) * cosPhi;
                dMax = xSpalt * sinPhi + height * cosPhi;
                d0 = dMin + (d1 - dMin) % lambda;
            } else {
                dMin = height * cosPhi;
                double d1 = xSpalt * sinPhi + (height / 2.0) * cosPhi;
                dMax = xSpalt * sinPhi;
                d0 = dMin + (d1 - dMin) % lambda;
            }

            for (double d = d0; d < dMax; d += lambda) {
                double schnittY_Achse = d / sinPhi;
                double schnittX_Achse = d / cosPhi;
                double schnittX_breite = (d - xSpalt * sinPhi) / cosPhi;
                double schnittY_hoehe = (d - height * cosPhi) / sinPhi;

                int x0, y0, x1, y1;

                if (schnittY_Achse < 0.0) {
                    x0 = 0;
                    y0 = (int) schnittX_Achse;
                } else if (schnittY_Achse < xSpalt) {
                    x0 = (int) schnittY_Achse;
                    y0 = 0;
                } else {
                    x0 = xSpalt;
                    y0 = (int) schnittX_breite;
                }

                if (schnittY_hoehe < 0.0) {
                    x1 = 0;
                    y1 = (int) schnittX_Achse;
                } else if (schnittY_hoehe < xSpalt) {
                    x1 = (int) schnittY_hoehe;
                    y1 = height;
                } else {
                    x1 = xSpalt;
                    y1 = (int) schnittX_breite;
                }

                shapeRenderer.line(x0, y0, x1, y1);
            }
        }
    }

    private void drawCircularWaves(int x, int y, int width, int height) {
        shapeRenderer.setColor(Color.BLACK);

        double phi = Math.toRadians(winkel - 90);
        int s0;

        if (y > height / 2) {
            s0 = (int) ((gitterD / 2.0) * Math.sin(phi)) % lambda;
        } else {
            s0 = -(int) ((gitterD / 2.0) * Math.sin(phi)) % lambda;
        }

        if (s0 < 0) s0 += lambda;

        for (int i = 0; i * lambda + s0 < Math.max(width, height) * 2; i++) {
            int r = i * lambda + s0;
            if (r > 0) {

                shapeRenderer.circle(x, y, r, 64);
            }
        }
    }


    private void drawIntersectionPoints(int x, int width, int height) {
        double phi = Math.toRadians(winkel - 90);
        double s0 = (gitterD / 2.0) * Math.sin(phi) % lambda;

        int n = (int)(Math.max(width, height) * 1.5 / lambda);

        int radius = 12;
        if (lambda <= 20) radius = 9;
        if (lambda <= 15) radius = 7;
        if (radius < 5) radius = 5;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double r1 = i * lambda - s0;
                double r2 = j * lambda + s0;

                if (r1 + r2 > gitterD && r1 <= r2 + gitterD && r2 <= r1 + gitterD) {
                    if (gitterD != 0) {
                        double y = (r1 * r1 - r2 * r2 + gitterD * gitterD) / (2.0 * gitterD);
                        double xRel = Math.sqrt(Math.abs(r1 * r1 - y * y)); 

                        float drawX = (float) (x + xRel);  
                        float drawY = (float) ((height - gitterD) / 2.0 + y);


                        if (drawY >= 0 && drawY <= height) {
                            shapeRenderer.circle(drawX, drawY, radius);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
                
        viewport.update(width, height, true);
        table.setPosition(10, viewport.getWorldHeight() - 10);
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
        skin.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
}

