package com.thrblock.sweeper.entity;

import java.awt.event.MouseEvent;

import com.thrblock.cino.component.CinoComponent;
import com.thrblock.cino.glanimate.fragment.IPureFragment;
import com.thrblock.cino.glanimate.fragment.SwitchFragment;
import com.thrblock.cino.glshape.GLImage;
import com.thrblock.sweeper.SweeperConstant;
import com.thrblock.sweeper.component.SweeperComponent.SweeperEvent;

public class Block extends CinoComponent {
    private GLImage img;
    private int num;
    private int i;
    private int j;
    private SweeperTexture stexture;
    private boolean fliped;
    private boolean marked;
    private boolean unavailable;
    private boolean pressing = false;
    
    private SwitchFragment flashAni;

    public enum MarkEvent {
        MARK, UNMARK
    }

    public Block(int i, int j, SweeperTexture stexture) {
        this.i = i;
        this.j = j;
        this.stexture = stexture;
    }

    @Override
    public void init() {
        img = shapeFactory.buildGLImage(0, 0, SweeperConstant.AREA_WH, SweeperConstant.AREA_WH, stexture.getUnshow());
        
        flashAni = IPureFragment.of(() -> img.setTexture(stexture.getDigs()[0]))
        .runOnece()
        .andThen(
                IPureFragment.of(() -> img.setTexture(stexture.getUnshow()))
                .runOnece()
                .whenThen(() -> !(mouseIO.isMouseButtonDown(1) && mouseIO.isMouseButtonDown(3)))
                )
        .wrapSwitch();
        
        auto(flashAni);
        
        initMouse();
        initEvent();
    }

    private void initEvent() {
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
            if (!marked && num == -1) {
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

    private void initMouse() {
        autoShapePressed(img, e -> {
            if (!unavailable) {
                if (!fliped && e.getButton() == 1) {
                    pressing = true;
                    img.setTexture(stexture.getDigs()[0]);
                } else if (fliped && doubleClicked(e)) {
                    eventBus.pushEvent(new Predict(num, i, j));
                }
            }
        });
        auto(() -> !unavailable && !fliped && pressing && !img.isMouseInside(), () -> {
            pressing = false;
            img.setTexture(stexture.getUnshow());
        });
        autoShapeReleased(img, this::mouseLogic);
    }

    public void flash() {
        flashAni.reset();
        flashAni.enable();
    }

    private void mouseLogic(MouseEvent e) {
        if (unavailable || fliped) {
            return;
        }
        if (e.getButton() == 1 && !marked && pressing) {
            pressing = false;
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
        if (fliped) {
            return;
        }
        fliped = true;
        if (num == -1) {
            img.setTexture(stexture.getMineExplode());
        } else {
            img.setTexture(stexture.getDigs()[num]);
            eventBus.pushEvent(SweeperEvent.SCORE);
        }
        eventBus.pushEvent(new Flip(num, i, j));
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
}