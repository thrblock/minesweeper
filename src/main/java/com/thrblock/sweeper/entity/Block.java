package com.thrblock.sweeper.entity;

import com.thrblock.cino.component.CinoInstance;
import com.thrblock.cino.glanimate.GLAnimate;
import com.thrblock.cino.glshape.GLImage;
import com.thrblock.cino.io.MouseEvent;
import com.thrblock.sweeper.SweeperConstant;
import com.thrblock.sweeper.component.SweeperComponent.SweeperEvent;

public class Block extends CinoInstance {
    private GLImage img;
    private int num;
    private int i;
    private int j;
    private SweeperTexture stexture;
    private boolean fliped;
    private boolean marked;
    private boolean unavailable;
    public enum MarkEvent{MARK,UNMARK}
    
    private GLAnimate flashAni;
    public Block(int i, int j, SweeperTexture stexture) {
        this.i = i;
        this.j = j;
        this.stexture = stexture;
    }

    @Override
    public void init() {
        img = shapeFactory.buildGLImage(0, 0, SweeperConstant.AREA_WH, SweeperConstant.AREA_WH, stexture.getUnshow());
        flashAni = animateFactory.build()
                .addOnce(() -> img.setTexture(stexture.getDigs()[0]))
                .add(() -> !(mouseIO.isMouseButtonDown(1) && mouseIO.isMouseButtonDown(3)))
                .addOnce(() -> img.setTexture(stexture.getUnshow()));
        
        autoShapePressed(img, this::mouseLogic);
        eventBus.mapEvent(SweeperEvent.FAIL, () -> {
            unavailable = true;
            if (!fliped && marked && num != -1) {
                img.setTexture(stexture.getWrongFlag());
            } else if (!fliped && num == -1 && !marked) {
                img.setTexture(stexture.getMine());
            }
        });
        eventBus.mapEvent(SweeperEvent.CLEAR, () -> {
            unavailable = true;
            if(!marked && num == -1) {
                img.setTexture(stexture.getFlag());
            }
        });
        eventBus.mapEvent(SweeperEvent.RELOAD, () -> {
            fliped = false;
            marked = false;
            unavailable = false;
            img.setTexture(stexture.getUnshow());
        });
    }

    public void flash() {
        flashAni.enable();
    }

    private void mouseLogic(MouseEvent e) {
        if (unavailable) {
            return;
        }
        if (fliped) {
            if (doubleClicked(e)) {
                eventBus.pushEvent(new Predict());
            }
            return;
        }
        if (e.getButton() == 1 && !marked) {
            flip();
        } else if (e.getButton() == 3) {
            markFlag();
        }
    }

    private boolean doubleClicked(MouseEvent e) {
        return (e.getButton() == 1 && mouseIO.isMouseButtonDown(3))
                || (e.getButton() == 3 && mouseIO.isMouseButtonDown(1));
    }

    private void markFlag() {
        if (marked) {
            eventBus.pushEvent(MarkEvent.UNMARK);
            img.setTexture(stexture.getUnshow());
        } else {
            eventBus.pushEvent(MarkEvent.MARK);
            img.setTexture(stexture.getFlag());
        }
        marked = !marked;
    }

    public void flip() {
        if(fliped) {
            return;
        }
        fliped = true;
        if (num == -1) {
            img.setTexture(stexture.getMineExplode());
        } else {
            img.setTexture(stexture.getDigs()[num]);
            eventBus.pushEvent(new Score());
        }
        eventBus.pushEvent(this);
    }

    public boolean isFliped() {
        return fliped;
    }

    public void placeAtScale(int w, int h) {
        float sep = SweeperConstant.AREA_WH + SweeperConstant.LINE_SP;
        float halfH = (screenH - h * SweeperConstant.AREA_WH) / 2;
        float baseX = -(w / 2f) * sep;
        float baseY = (h / 2f) * sep - halfH;
        img.setCentralX(baseX + i * sep + SweeperConstant.AREA_WH / 2);
        img.setCentralY(baseY - j * sep + SweeperConstant.AREA_WH / 2);
    }

    public boolean isMarked() {
        return marked;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public class Predict {
        public int getNum() {
            return num;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }
    }

    public class Score {
    }
}