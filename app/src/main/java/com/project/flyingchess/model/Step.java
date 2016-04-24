package com.project.flyingchess.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by Administrator on 2016/4/13.
 */
@JsonObject
public class Step {
    //private int random;
    @JsonField
    private int planeTag;

    @JsonField
    private int shapeTag;

    public Step() {
    }

    public Step(int planeTag, int shapeTag) {
        this.planeTag = planeTag;
        this.shapeTag = shapeTag;
    }

    public int getPlaneTag() {
        return planeTag;
    }

    public void setPlaneTag(int planeTag) {
        this.planeTag = planeTag;
    }

    public int getShapeTag() {
        return shapeTag;
    }

    public void setShapeTag(int shapeTag) {
        this.shapeTag = shapeTag;
    }

    @Override
    public String toString() {
        return "Step{" +
                "planeTag=" + planeTag +
                ", shapeTag=" + shapeTag +
                '}';
    }
}
