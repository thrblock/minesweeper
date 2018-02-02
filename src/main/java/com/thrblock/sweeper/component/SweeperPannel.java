package com.thrblock.sweeper.component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.thrblock.cino.annotation.SubCompOf;
import com.thrblock.cino.component.CinoComponent;
import com.thrblock.cino.glanimate.GLAnimate;
import com.thrblock.cino.glshape.GLCharArea;
import com.thrblock.cino.glshape.GLImage;
import com.thrblock.cino.gltexture.GLFont;
import com.thrblock.cino.gltexture.GLIOTexture;
import com.thrblock.cino.gltexture.GLTexture;
import com.thrblock.cino.util.BufferedImageUtil;
import com.thrblock.cino.util.charprocess.CharAreaConfig;
import com.thrblock.cino.util.charprocess.CharArrayInt;
import com.thrblock.sweeper.SweeperConstant;
import com.thrblock.sweeper.component.SweeperComponent.SweeperEvent;
import com.thrblock.sweeper.entity.Block.MarkEvent;

@Component
@SubCompOf(SweeperComponent.class)
public class SweeperPannel extends CinoComponent {

    @Value("${cino.frame.fps:60}")
    private int fps;
    
    @Override
    public void init() throws Exception {
        autoShowHide();
        buildFace();
    }

    private void buildFace() {
        shapeFactory.setLayer(SweeperConstant.LAYER_UI);
        GLTexture faceNormal = new GLIOTexture(new File("./mine/ui/faceNormal.png"));
        GLTexture faceFail = new GLIOTexture(new File("./mine/ui/faceFail.png"));
        GLTexture faceClear = new GLIOTexture(new File("./mine/ui/faceClear.png"));
        GLTexture faceDown = new GLIOTexture(new File("./mine/ui/faceDown.png"));
        GLImage face = shapeFactory.buildGLImage(0, 0, SweeperConstant.FACT_WH, SweeperConstant.FACT_WH, faceNormal);
        eventBus.mapEvent(SweeperEvent.FAIL, () -> face.setTexture(faceFail));
        eventBus.mapEvent(SweeperEvent.RELOAD, () -> face.setTexture(faceNormal));
        eventBus.mapEvent(SweeperEvent.CLEAR, () -> face.setTexture(faceClear));
        
        face.setCentralY(screenH / 2f - (10 + SweeperConstant.FACT_WH / 2f));
        autoShapePressed(face, e -> face.setTexture(faceDown));
        autoShapeReleased(face, e -> eventBus.pushEvent(SweeperEvent.RELOAD));
        
        GLFont redDigs = new GLFont(this::genDig, "0123456789".toCharArray());
        buildTiime(face, redDigs);
        buildMineNotice(face, redDigs);
    }

    private void buildMineNotice(GLImage face, GLFont redDigs) {
        CharArrayInt mine = new CharArrayInt("000");
        CharAreaConfig mineConfig = new CharAreaConfig(mine.getArray());
        mineConfig.setFont(redDigs);
        GLCharArea mineArea = shapeFactory.buildGLCharArea(0, 0, SweeperConstant.RED_DIG_W * 3f, mineConfig);
        mineArea.leftOf(face, 50);
        mine.setByInt(SweeperConstant.MINE_NUM);
        eventBus.mapEvent(MarkEvent.MARK, () -> {
            int currentMine = mine.getCurrent() - 1;
            mine.setByInt(currentMine >= 0?currentMine:0);
        });
        eventBus.mapEvent(MarkEvent.UNMARK, () -> mine.setByInt(mine.getCurrent() + 1));
        eventBus.mapEvent(SweeperEvent.RELOAD, () -> mine.setByInt(SweeperConstant.MINE_NUM));
    }

    private void buildTiime(GLImage face, GLFont redDigs) {
        CharArrayInt time = new CharArrayInt("000");
        CharAreaConfig timeConfig = new CharAreaConfig(time.getArray());
        timeConfig.setFont(redDigs);
        GLCharArea timeArea = shapeFactory.buildGLCharArea(0, 0, SweeperConstant.RED_DIG_W * 3f, timeConfig);
        timeArea.rightOf(face, 50);
        
        GLAnimate timing = animateFactory.build();
        timing
        .addDelay(fps)
        .addOnce(() -> time.setByInt(time.getCurrent() + 1))
        .add(timing::loop);
        
        eventBus.mapEvent(SweeperEvent.START,() -> {
            time.setByInt(0);
            timing.enable();
        });
        
        eventBus.mapEvent(SweeperEvent.FAIL,timing::disable);
        eventBus.mapEvent(SweeperEvent.CLEAR,timing::disable);
        eventBus.mapEvent(SweeperEvent.RELOAD,() -> {
            time.setByInt(0);
            timing.disable();
        });
    }
    
    private BufferedImage genDig(char c) {
        try {
            return ImageIO.read(new File("./mine/ui/digs/" + c + ".png"));
        } catch (IOException e) {
            return BufferedImageUtil.genEmptyImage(SweeperConstant.RED_DIG_W,SweeperConstant.RED_DIG_H);
        }
    }
}
