package io.doppelspalt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class DoppelspaltSimulation implements Screen {

    private final Main app;
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;

    // Physikalische Parameter
    private static final float WAVELENGTH = 550e-9f; // Wellenlänge in Metern
    private static final float SLIT_DISTANCE = 0.0002f; // Abstand zwischen den Spalten
    private static final float SLIT_WIDTH = 0.00005f; // Breite eines Spalts
    private static final float SCREEN_DISTANCE = 1.0f; // Abstand zur Projektionsfläche

    private final int screenPixelWidth = 1280;
    private final int screenPixelHeight = 720;

    public DoppelspaltSimulation(Main app) {
        this.app = app;
        camera = new OrthographicCamera();
        viewport = new FitViewport(screenPixelWidth, screenPixelHeight, camera);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderInterferencePattern();
        shapeRenderer.end();
    }

    private void renderInterferencePattern() {
        float screenCenterY = screenPixelHeight / 2f;
        float scale = 1e6f; // Maßstab für Umrechnung von Metern zu Pixeln

        for (int x = 0; x < screenPixelWidth; x++) {
            // Umrechnung der x-Position in Meter (relativ zur Mitte)
            float screenX = ((float) x / screenPixelWidth - 0.5f);
            float position = screenX * 0.01f; // ca. ±0.005 m auf dem Schirm

            // Berechne Gangunterschied Δs = d * sin(θ) ≈ d * tan(θ) = d * y / L
            float deltaS = SLIT_DISTANCE * position / SCREEN_DISTANCE;

            // Interferenz: I ∝ cos²(π Δs / λ)
            float intensity = (float) Math.pow(Math.cos(Math.PI * deltaS / WAVELENGTH), 2);

            // Diffraction envelope (Einzelspalt)
            float beta = (float) ((Math.PI * SLIT_WIDTH * position) / (WAVELENGTH * SCREEN_DISTANCE));
            float envelope = beta != 0 ? (float) Math.pow(Math.sin(beta) / beta, 2) : 1f;

            float totalIntensity = intensity * envelope;

            // Zeichne vertikale Linie proportional zur Intensität
            float lineHeight = totalIntensity * screenPixelHeight;
            float y = screenCenterY - lineHeight / 2f;

            shapeRenderer.setColor(totalIntensity, totalIntensity, totalIntensity, 1);
            shapeRenderer.rect(x, y, 1, lineHeight);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        shapeRenderer.dispose();
    }
}

