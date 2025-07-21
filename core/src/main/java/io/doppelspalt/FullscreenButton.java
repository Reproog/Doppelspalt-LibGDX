package io.doppelspalt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FullscreenButton extends ImageButton {
    private ImageButton fullscreenButton;
    private boolean isFullscreen;
    private Stage stage;
    private Skin skin;
    private int FULL_BUTTON_SIZE = 50;

    public FullscreenButton(Stage stage, Skin skin) {
        super(skin);
        this.stage = stage;
        this.skin = skin;
        this.isFullscreen = false;

        // Statt Icons verwenden wir jetzt farbige Rechtecke aus der Skin:
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = skin.newDrawable("rect", Color.RED);        // sichtbar in Rot
        style.imageDown = skin.newDrawable("rect", Color.DARK_GRAY); // beim Drücken dunkelgrau

        fullscreenButton = new ImageButton(style);
        fullscreenButton.addListener(new TextTooltip("Toggle fullscreen", skin));


        // Sichtbar positionieren:
        fullscreenButton.setSize(100, 100);
        fullscreenButton.setPosition(10, 10);

        fullscreenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleFullscreen();
            }
        });
    }

    public void addToStage(Stage stage) {
        stage.addActor(fullscreenButton);
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            Gdx.graphics.setWindowedMode(1920, 1080);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
        isFullscreen = !isFullscreen;
        // Icon-Update entfällt hier, da wir keine Icons verwenden
    }

    public void toFront() {
        fullscreenButton.toFront();
    }

    public void dispose() {
        // keine Texturen zum Entsorgen
    }
}




/*
package io.doppelspalt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;

public class FullscreenButton extends Button {
    private ImageButton fullscreenButton;
    private boolean isFullscreen;
    private Stage stage;
    private Skin skin;
    private Texture fullscreenIcon;
    private Texture windowedIcon;
    private static int FULL_BUTTON_SIZE = 50;

    public FullscreenButton(Stage stage, Skin skin) {
        super(skin);
        this.stage = stage;
        this.skin = skin;
        this.isFullscreen = false;

        windowedIcon = new Texture(Gdx.files.internal("windowed-1.png"));
        fullscreenIcon = new Texture(Gdx.files.internal("fullscreen-1.png"));


        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(new TextureRegion(fullscreenIcon));
        style.imageDown = new TextureRegionDrawable(new TextureRegion(fullscreenIcon));

        fullscreenButton = new ImageButton(style);
        fullscreenButton.addListener(new TextTooltip("Toggle fullscreen", skin));
    

        fullscreenButton.setSize(FULL_BUTTON_SIZE, FULL_BUTTON_SIZE);
        fullscreenButton.setPosition(0, 0);

        fullscreenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleFullscreen();
            }
        });

    }

    public void addToStage(Stage stage) {
        stage.addActor(fullscreenButton);
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            Gdx.graphics.setWindowedMode(1920, 1080);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }

        isFullscreen = !isFullscreen;
        updateButtonIcon();
    }

    private void updateButtonIcon() {
        Texture newTexture = isFullscreen ? windowedIcon : fullscreenIcon;
        ((TextureRegionDrawable) fullscreenButton.getStyle().imageUp).setRegion(new TextureRegion(newTexture));
    }

    public void dispose() {
        fullscreenIcon.dispose();
        windowedIcon.dispose();
    }
}

public class FullscreenButton extends ImageButton {
    private boolean isFullscreen;
    private Texture fullscreenIcon;
    private Texture windowedIcon;
    private static final int FULL_BUTTON_SIZE = 50;

    public FullscreenButton(Stage stage, Skin skin) {
        super(createStyle(skin));
        this.isFullscreen = false;

        windowedIcon = new Texture(Gdx.files.internal("windowed-1.png"));
        fullscreenIcon = new Texture(Gdx.files.internal("fullscreen-1.png"));

        setSize(FULL_BUTTON_SIZE, FULL_BUTTON_SIZE);

        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleFullscreen();
            }
        });
    }

    private static ImageButtonStyle createStyle(Skin skin) {
        Texture fullscreenIcon = new Texture(Gdx.files.internal("fullscreen-1.png"));
        ImageButtonStyle style = new ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(new TextureRegion(fullscreenIcon));
        style.imageDown = new TextureRegionDrawable(new TextureRegion(fullscreenIcon));
        return style;
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            Gdx.graphics.setWindowedMode(1920, 1080);
        } else {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }
        isFullscreen = !isFullscreen;
        updateButtonIcon();
    }

    private void updateButtonIcon() {
        Texture newTexture = isFullscreen ? windowedIcon : fullscreenIcon;
        ((TextureRegionDrawable)getStyle().imageUp).setRegion(new TextureRegion(newTexture));
        ((TextureRegionDrawable)getStyle().imageDown).setRegion(new TextureRegion(newTexture));
    }


    public void addToStage(Stage stage) {
        stage.addActor(this);
    }

    public void dispose() {
        fullscreenIcon.dispose();
        windowedIcon.dispose();
    }
}
*/


