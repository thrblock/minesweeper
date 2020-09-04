package com.thrblock.sweeper.component;

import java.awt.Color;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thrblock.cino.annotation.BootComponent;
import com.thrblock.cino.component.CinoComponent;
import com.thrblock.cino.glshape.GLRect;
import com.thrblock.cino.gltexture.GLIOTexture;
import com.thrblock.cino.gltexture.GLTexture;
import com.thrblock.cino.util.math.CRand;
import com.thrblock.cino.util.structure.IntBoxer;
import com.thrblock.sweeper.SweeperConstant;
import com.thrblock.sweeper.entity.Block;
import com.thrblock.sweeper.entity.Flip;
import com.thrblock.sweeper.entity.Predict;
import com.thrblock.sweeper.entity.SweeperTexture;

@Component
@BootComponent
public class SweeperComponent extends CinoComponent {
    
    @Autowired
    private BlockFactory blockFactory;
    
    public enum SweeperEvent {
        FAIL, CLEAR, START, RELOAD, SCORE
    }

    @Override
    public void init() throws Exception {
        autoShowHide();
        buildBackGround();
        initArea(SweeperConstant.W_NUM, SweeperConstant.H_NUM);
    }

    private void buildBackGround() {
        GLRect r = shapeFactory.buildGLRect(0, 0, SweeperConstant.W_NUM * SweeperConstant.AREA_WH + 20, screenH);
        r.setFill(true);
        r.setAllPointColor(Color.GRAY);
        shapeFactory.buildGLRect(0, 0, SweeperConstant.W_NUM * SweeperConstant.AREA_WH + 20, screenH);
    }

    private void initArea(int w, int h) {
        SweeperTexture sTexture = new SweeperTexture();
        sTexture.setDigs(loadDigs());
        sTexture.setUnshow(new GLIOTexture(new File("./mine/field/unshow.png")));
        sTexture.setMine(new GLIOTexture(new File("./mine/field/mine.png")));
        sTexture.setMineExplode(new GLIOTexture(new File("./mine/field/mineExplode.png")));
        sTexture.setFlag(new GLIOTexture(new File("./mine/field/flag.png")));
        sTexture.setWrongFlag(new GLIOTexture(new File("./mine/field/wrongFlag.png")));
        Block[][] blocks = loadBlocks(w, h, sTexture);
        randomlize(blocks);
        initLogic(blocks);
        autoMapEvent(SweeperEvent.RELOAD, () -> randomlize(blocks));
    }

    private void randomlize(Block[][] area) {
        earse(area);
        Stream.iterate(0, i -> i + 1).limit(SweeperConstant.MINE_NUM).forEach(i -> placeAMine(area));
        foreachBlock(area, a -> {
            if (a.getNum() != -1) {
                loopCount(a, area);
            }
        });
    }

    private void earse(Block[][] area) {
        foreachBlock(area, a -> a.setNum(0));
    }

    private void placeAMine(Block[][] area) {
        int i = CRand.getRandomNum(0, area.length - 1);
        int j = CRand.getRandomNum(0, area[i].length - 1);
        if (area[i][j].getNum() == -1) {
            placeAMine(area);
        } else {
            area[i][j].setNum(-1);
        }
    }

    private void initLogic(Block[][] area) {
        IntBoxer flipCount = new IntBoxer();
        eventBus.mapEvent(Flip.class, e -> {
            if (e.num == -1) {
                eventBus.pushEvent(SweeperEvent.FAIL);
            } else if (e.num == 0) {
                flipAround(area, e.i, e.j);
            }
        });
        eventBus.mapEvent(Predict.class, p -> predict(area, p));
        eventBus.mapEvent(SweeperEvent.SCORE, () -> {
            flipCount.setValue(flipCount.getValue() + 1);
            if (flipCount.getValue() == 1) {
                eventBus.pushEvent(SweeperEvent.START);
            }
            if (flipCount.getValue() == (SweeperConstant.W_NUM * SweeperConstant.H_NUM) - SweeperConstant.MINE_NUM) {
                eventBus.pushEvent(SweeperEvent.CLEAR);
            }
        });
        eventBus.mapEvent(SweeperEvent.RELOAD, () -> flipCount.setValue(0));
    }

    private void loopAround(Block[][] area, int ei, int ej, Consumer<Block> cons) {
        for (int i = ei - 1; i <= ei + 1; i++) {
            for (int j = ej - 1; j <= ej + 1; j++) {
                if (isInsideRange(area, i, j) && !(ei == i && ej == j)) {
                    cons.accept(area[i][j]);
                }
            }
        }
    }

    private void foreachBlock(Block[][] area, Consumer<Block> cons) {
        for (int i = 0; i < area.length; i++) {
            for (int j = 0; j < area[i].length; j++) {
                cons.accept(area[i][j]);
            }
        }
    }

    private void predict(Block[][] area, Predict p) {
        List<Block> unTouched = new LinkedList<>();
        IntBoxer markNum = new IntBoxer();
        loopAround(area, p.i, p.j, a -> {
            if (a.isMarked()) {
                markNum.setValue(markNum.getValue() + 1);
            } else if (!a.isFliped()) {
                unTouched.add(a);
            }
        });
        unTouched.forEach(markNum.getValue() == p.num ? Block::flip : Block::flash);
    }

    private void flipAround(Block[][] area, int i, int j) {
        loopAround(area, i, j, block -> {
            if (!block.isMarked()) {
                block.flip();
            }
        });
    }

    private void loopCount(Block e, Block[][] area) {
        loopAround(area, e.getI(), e.getJ(), a -> {
            if (a.getNum() == -1) {
                e.setNum(e.getNum() + 1);
            }
        });
    }

    private Block[][] loadBlocks(int w, int h, SweeperTexture stexture) {
        Block[][] result = new Block[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                result[i][j] = blockFactory.block(i, j, stexture);
                result[i][j].placeAtScale(w, h);
            }
        }
        return result;
    }

    private GLTexture[] loadDigs() {
        GLTexture[] result = new GLTexture[9];
        Stream.iterate(0, i -> i + 1).limit(9)
                .forEach(i -> result[i] = new GLIOTexture(new File("./mine/field/dig/0" + i + ".png")));
        return result;
    }

    private boolean isInsideRange(Block[][] area, int i, int j) {
        return (i >= 0 && i < area.length) && (j >= 0 && j < area[i].length);
    }
}
