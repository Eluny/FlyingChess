package com.project.flyingchess.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.project.flyingchess.R;
import com.project.flyingchess.utils.Color;
import com.project.flyingchess.widget.shape.Circle;
import com.project.flyingchess.widget.shape.MyShape;
import com.project.flyingchess.widget.shape.Rectangle;
import com.project.flyingchess.widget.shape.Triangle;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wzm on 2016/3/25.
 */
public class ChessBoard extends View {
    private Context mContext;

    private static final int BOARD_MARGIN = 40;
    private static final int LINE_COUNT = 18;
    private int mLineCount;
    private float mGridWidth;
    private float mGridHeight;

    //private int theSelectedPlaneTag = 1;
    private int theSelectedColor = Color.NONE;

    //public boolean isPaint;
    private Paint[] paints = new Paint[5];
    private Bitmap[] plane_normal = new Bitmap[4];
    //private Bitmap[] plane_select = new Bitmap[4];

    private Map<Integer, Integer> planeNum = new HashMap<Integer, Integer>();//棋子编号->格子编号
    public static Map<Integer, MyShape> planePosition = new HashMap<Integer, MyShape>();//格子编号->图形对象（即对应的矩形或三角形）

    private Rectangle[] rectangles;
    private Rectangle[] square;//正方形
    private Triangle[] triangle;//三角形
    private Circle[] circles;

    private OnSelectPlaneListener onSelectPlaneListener;

    //标志坐标~
    public static int TAG_BLUE_START = 0;public static int TAG_BLUE_JUMP = 2;
    public static int TAG_BLUE_CORNER = 50;public static int TAG_BLUE_CORNER_START = 70;
    public static int TAG_BLUE_END = 76;

    public static int TAG_YELLOW_START = 13;public static int TAG_YELLOW_JUMP = 15;
    public static int TAG_YELLOW_CORNER = 11;public static int TAG_YELLOW_CORNER_START = 52;
    public static int TAG_YELLOW_END = 58;

    public static int TAG_RED_START = 26;public static int TAG_RED_JUMP = 28;
    public static int TAG_RED_CORNER = 24;public static int TAG_RED_CORNER_START = 58;
    public static int TAG_RED_END = 64;

    public static int TAG_GREEN_START = 39;public static int TAG_GREEN_JUMP = 41;
    public static int TAG_GREEN_CORNER = 37;public static int TAG_GREEN_CORNER_START = 64;
    public static int TAG_GREEN_END = 70;

    public static int TAG_SMALL = 0;
    public static int TAG_LARGE = 77;
    public static int TAG_RECTANGLE_LARGE = 52;

    public ChessBoard(Context context) {
        super(context);
        init(context);
    }

    public ChessBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChessBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /*
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChessBoard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    */

    private void init(Context context) {
        mContext = context;

        mLineCount = LINE_COUNT;

        initPaints();

        initPlaneInfo();

        initShape();

        initBitmap(context);
    }

    private void initPlaneInfo() {
        planeNum.clear();
        for (int i = 0; i < 4; i++) {
            planeNum.put(i + 1, i + 77);
        }
        for (int i = 0; i < 4; i++) {
            planeNum.put(i + 5, i + 81);
        }
        for (int i = 0; i < 4; i++) {
            planeNum.put(i + 9, i + 85);
        }
        for (int i = 0; i < 4; i++) {
            planeNum.put(i + 13, i + 89);
        }

        planePosition.clear();
    }

    private void initBitmap(Context context) {
        plane_normal[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane_blue_b);
        plane_normal[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane_yellow_b);
        plane_normal[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane_red_b);
        plane_normal[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane_green_b);

        /*
        plane[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane_blue_normal);
        plane[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane_yellow_normal);
        plane[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane_red_normal);
        plane[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane_green_normal);

        plane_select[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane_blue_selected);
        plane_select[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane_yellow_selected);
        plane_select[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane_red_selected);
        plane_select[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.plane_green_selected);
        */
    }

    private void initShape() {
        rectangles = new Rectangle[36];
        for(int i = 0; i < rectangles.length; i++) {
            rectangles[i] = new Rectangle();
        }

        triangle = new Triangle[20];
        for(int i = 0; i < triangle.length; i++) {
            triangle[i] = new Triangle();
        }

        square = new Rectangle[36];
        for (int i = 0; i < square.length; i++) {
            square[i] = new Rectangle();
        }

        circles = new Circle[93];
        for (int i = 0; i < circles.length; i++) {
            circles[i] = new Circle();
        }
    }

    private void initPaints() {
        //isPaint = false;
        for (int i = 0; i < paints.length; i++) {
            paints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            paints[i].setStyle(Paint.Style.FILL);
            paints[i].setAntiAlias(true);
        }

        paints[0].setColor(Color.BLUE_);
        paints[1].setColor(Color.YELLOW_);
        paints[2].setColor(Color.RED_);
        paints[3].setColor(Color.GREEN_);

        paints[4].setStyle(Paint.Style.FILL);
        paints[4].setColor(0xb9fce701);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //if (!isPaint) {
            drawRect(canvas);
            drawTri(canvas);
            drawCircle(canvas);
            //isPaint = true;
        //}
        drawChess(canvas);
    }

    private void drawChess(Canvas canvas) {
        for (int i = 1; i <= 16; i++) {
            int tmp = planeNum.get(i);
            /*MyShape shape = planePosition.get(tmp);
            if (shape instanceof Rectangle) {
                Rectangle rectangle = new Rectangle();
                rectangle = (Rectangle) shape;
                RectF rectF = new RectF(rectangle.left, rectangle.top, rectangle.right, rectangle.bottom);
                canvas.drawBitmap(plane[(i-1)/4], null, rectF, null);
            }*/
            Circle circle = circles[tmp];
            RectF rectF = new RectF(circle.getX() - circle.getRadius(), circle.getY() - circle.getRadius(), circle.getX() + circle.getRadius(), circle.getY() + circle.getRadius());
            if(theSelectedColor == (i-1) / 4 && tmp != TAG_BLUE_END && tmp != TAG_RED_END && tmp != TAG_YELLOW_END && tmp != TAG_RED_END)
                canvas.drawCircle(circle.getX(),circle.getY(),circle.getRadius()+5,paints[4]);
            canvas.drawBitmap(plane_normal[(i-1)/4], null, rectF, null);
            //canvas.drawBitmap(plane_select[(i-1)/4], null, rectF, null);
        }
    }

    private void drawCircle(Canvas canvas) {
        /*
         *不包括三角形里面的
         */
        for (int i = 1; i < circles.length; i++) {
            circles[i].draw(canvas);
            circles[i].drawEdge(canvas);
        }
    }

    private void drawTri(Canvas canvas) {
        for (int i = 0; i < 20; i++) {
            triangle[i].draw(canvas);
        }
    }

    private void drawRect(Canvas canvas) {
        for (int i = 0; i < rectangles.length; i++) {
            rectangles[i].draw(canvas);
        }

        for (int i = 0; i < square.length; i++) {
            square[i].draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width);

        calcLinePoints();
        calcShape();
        calcCircle();
    }

    private void calcLinePoints() {
        float boardWidth = getMeasuredWidth() - BOARD_MARGIN * 2;
        float boardHeight = getMeasuredHeight() - BOARD_MARGIN * 2;

        mGridWidth = boardWidth / (mLineCount - 1);
        mGridHeight = boardHeight / (mLineCount - 1);
    }

    private void calcCircle() {

        for (int i = 0; i < rectangles.length; i++) {
            circles[rectangles[i].getNum()].setPara((rectangles[i].left + rectangles[i].right)/2, (rectangles[i].top + rectangles[i].bottom)/2);
        }

        for (int i = 0; i < square.length; i++) {
            circles[square[i].getNum()].setPara((square[i].left + square[i].right)/2, (square[i].top + square[i].bottom)/2);
        }

        //剩下画三角形中的圆圈
        circles[triangle[0].getNum()].setPara(triangle[0].x2 - mGridWidth / 2, triangle[0].y2 + mGridHeight / 2);
        circles[triangle[1].getNum()].setPara(triangle[1].x2 - mGridWidth / 2, triangle[1].y2 - mGridHeight / 2);
        circles[triangle[2].getNum()].setPara(triangle[2].x1 + mGridWidth / 2, triangle[2].y2 + mGridHeight / 2);
        circles[triangle[3].getNum()].setPara(triangle[3].x2 - mGridWidth / 2, triangle[3].y2 + mGridHeight / 2);
        circles[triangle[4].getNum()].setPara(triangle[4].x2 - mGridWidth / 2, triangle[4].y2 - mGridHeight / 2);
        circles[triangle[5].getNum()].setPara(triangle[5].x1 + mGridWidth / 2, triangle[5].y2 - mGridHeight / 2);
        circles[triangle[6].getNum()].setPara(triangle[6].x2 - mGridWidth / 2, triangle[6].y2 + mGridHeight / 2);
        circles[triangle[7].getNum()].setPara(triangle[7].x2 - mGridWidth / 2, triangle[7].y2 - mGridHeight / 2);
        circles[triangle[8].getNum()].setPara(triangle[8].x1 + mGridWidth / 2, triangle[8].y2 - mGridHeight / 2);
        circles[triangle[9].getNum()].setPara(triangle[9].x1 + mGridWidth / 2, triangle[9].y2 + mGridHeight / 2);
        circles[triangle[10].getNum()].setPara(triangle[10].x2 - mGridWidth / 2, triangle[10].y2 - mGridHeight / 2);
        circles[triangle[11].getNum()].setPara(triangle[11].x1 + mGridWidth / 2, triangle[11].y2 - mGridHeight / 2);
        circles[triangle[12].getNum()].setPara(triangle[12].x1 + mGridWidth / 2, triangle[12].y2 + mGridHeight / 2);
        circles[triangle[13].getNum()].setPara(triangle[13].x2 - mGridWidth / 2, triangle[13].y2 + mGridHeight / 2);
        circles[triangle[14].getNum()].setPara(triangle[14].x1 + mGridWidth / 2, triangle[14].y2 - mGridHeight / 2);
        circles[triangle[15].getNum()].setPara(triangle[15].x1 + mGridWidth / 2, triangle[15].y2 + mGridHeight / 2);
        //circles[88].setPara((triangle[16].x1 + triangle[16].x2) / 2, triangle[16].y2);
        //circles[89].setPara(triangle[17].x2, (triangle[17].y1 + triangle[17].y2) / 2);
        //circles[90].setPara((triangle[18].x1 + triangle[18].x2) / 2, triangle[18].y1);
        //circles[91].setPara(triangle[19].x3, (triangle[19].y1 + triangle[19].y3) / 2);
        circles[triangle[16].getNum()].setPara(triangle[16].x1 + mGridWidth / 2, triangle[16].y2);
        circles[triangle[17].getNum()].setPara(triangle[17].x2, triangle[17].y1 + mGridHeight / 2);
        circles[triangle[18].getNum()].setPara(triangle[18].x2 - mGridWidth / 2, triangle[18].y1);
        circles[triangle[19].getNum()].setPara(triangle[19].x3, triangle[19].y1 - mGridHeight / 2);

    }

    private void calcShape() {
        triangle[0].setCoord(4 * mGridWidth + BOARD_MARGIN, 15 * mGridHeight + BOARD_MARGIN, 6 * mGridWidth + BOARD_MARGIN, 15 * mGridHeight + BOARD_MARGIN, 6 * mGridWidth + BOARD_MARGIN, 17 * mGridHeight + BOARD_MARGIN, 1, paints[3]);
        rectangles[0].setCoord(4 * mGridWidth + BOARD_MARGIN, 14 * mGridHeight + BOARD_MARGIN, 6 * mGridWidth + BOARD_MARGIN, 15 * mGridHeight + BOARD_MARGIN, 2, paints[0]);
        rectangles[1].setCoord(4 * mGridWidth + BOARD_MARGIN, 13 * mGridHeight + BOARD_MARGIN, 6 * mGridWidth + BOARD_MARGIN, 14 * mGridHeight + BOARD_MARGIN, 3, paints[1]);
        triangle[1].setCoord(4 * mGridWidth + BOARD_MARGIN, 13 * mGridHeight + BOARD_MARGIN, 6 * mGridWidth + BOARD_MARGIN, 13 * mGridHeight + BOARD_MARGIN, 6 * mGridWidth + BOARD_MARGIN, 11 * mGridHeight + BOARD_MARGIN, 4, paints[2]);
        triangle[2].setCoord(4 * mGridWidth + BOARD_MARGIN, 11 * mGridHeight + BOARD_MARGIN, 6 * mGridWidth + BOARD_MARGIN, 11 * mGridHeight + BOARD_MARGIN, 4 * mGridWidth + BOARD_MARGIN, 13 * mGridHeight + BOARD_MARGIN, 5, paints[3]);
        rectangles[2].setCoord(3 * mGridWidth + BOARD_MARGIN, 11 * mGridHeight + BOARD_MARGIN, 4 * mGridWidth + BOARD_MARGIN, 13 * mGridHeight + BOARD_MARGIN, 6, paints[0]);
        rectangles[3].setCoord(2 * mGridWidth + BOARD_MARGIN, 11 * mGridHeight + BOARD_MARGIN, 3 * mGridWidth + BOARD_MARGIN, 13 * mGridHeight + BOARD_MARGIN, 7, paints[1]);
        triangle[3].setCoord(BOARD_MARGIN, 11 * mGridHeight + BOARD_MARGIN, 2 * mGridWidth + BOARD_MARGIN, 11 * mGridHeight + BOARD_MARGIN, 2 * mGridWidth + BOARD_MARGIN, 13 * mGridHeight + BOARD_MARGIN, 8, paints[2]);
        for (int i = 0; i < 5; i++) {
            rectangles[i+4].setCoord(BOARD_MARGIN, getMeasuredHeight() - ((7 + i) * mGridHeight + BOARD_MARGIN), 2 * mGridWidth + BOARD_MARGIN, getMeasuredHeight() - ((6 + i) * mGridHeight + BOARD_MARGIN), i + 9, paints[(i + 3) % 4]);
        }
        triangle[4].setCoord(BOARD_MARGIN, 6 * mGridHeight + BOARD_MARGIN, 2 * mGridWidth + BOARD_MARGIN, 6 * mGridHeight + BOARD_MARGIN, 2 * mGridWidth + BOARD_MARGIN, 4 * mGridHeight + BOARD_MARGIN, 14, paints[0]);
        rectangles[9].setCoord(2 * mGridWidth + BOARD_MARGIN, 4 * mGridHeight + BOARD_MARGIN, 3 * mGridWidth + BOARD_MARGIN, 6 * mGridHeight + BOARD_MARGIN, 15, paints[1]);
        rectangles[10].setCoord(3 * mGridWidth + BOARD_MARGIN, 4 * mGridHeight + BOARD_MARGIN, 4 * mGridWidth + BOARD_MARGIN, 6 * mGridHeight + BOARD_MARGIN, 16, paints[2]);
        triangle[5].setCoord(4 * mGridWidth + BOARD_MARGIN, 6 * mGridHeight + BOARD_MARGIN, 6 * mGridWidth + BOARD_MARGIN, 6 * mGridHeight + BOARD_MARGIN, 4 * mGridWidth + BOARD_MARGIN, 4 * mGridHeight + BOARD_MARGIN, 17, paints[3]);
        triangle[6].setCoord(4 * mGridWidth + BOARD_MARGIN, 4 * mGridHeight + BOARD_MARGIN, 6 * mGridWidth + BOARD_MARGIN, 4 * mGridHeight + BOARD_MARGIN, 6 * mGridWidth + BOARD_MARGIN, 6 * mGridHeight + BOARD_MARGIN, 18, paints[0]);
        rectangles[11].setCoord(4 * mGridWidth + BOARD_MARGIN, 3 * mGridHeight + BOARD_MARGIN, 6 * mGridWidth + BOARD_MARGIN, 4 * mGridHeight + BOARD_MARGIN, 19, paints[1]);
        rectangles[12].setCoord(4 * mGridWidth + BOARD_MARGIN, 2 * mGridHeight + BOARD_MARGIN, 6 * mGridWidth + BOARD_MARGIN, 3 * mGridHeight + BOARD_MARGIN, 20, paints[2]);
        triangle[7].setCoord(4 * mGridWidth + BOARD_MARGIN, 2 * mGridHeight + BOARD_MARGIN, 6 * mGridWidth + BOARD_MARGIN, 2 * mGridHeight + BOARD_MARGIN, 6 * mGridWidth + BOARD_MARGIN, BOARD_MARGIN, 21, paints[3]);
        for (int i = 0; i < 5; i++) {
            rectangles[i+13].setCoord((6 + i) * mGridWidth + BOARD_MARGIN, BOARD_MARGIN, (7 + i) * mGridWidth + BOARD_MARGIN, 2 * mGridHeight + BOARD_MARGIN, i + 22, paints[i % 4]);
        }
        triangle[8].setCoord(11 * mGridWidth + BOARD_MARGIN, 2 * mGridHeight + BOARD_MARGIN, 13 * mGridWidth + BOARD_MARGIN, 2 * mGridHeight + BOARD_MARGIN, 11 * mGridWidth + BOARD_MARGIN, BOARD_MARGIN, 27, paints[1]);
        rectangles[18].setCoord(11 * mGridWidth + BOARD_MARGIN, 2 * mGridHeight + BOARD_MARGIN, 13 * mGridWidth + BOARD_MARGIN, 3 * mGridHeight + BOARD_MARGIN, 28, paints[2]);
        rectangles[19].setCoord(11 * mGridWidth + BOARD_MARGIN, 3 * mGridHeight + BOARD_MARGIN, 13 * mGridWidth + BOARD_MARGIN, 4 * mGridHeight + BOARD_MARGIN, 29, paints[3]);
        triangle[9].setCoord(11 * mGridWidth + BOARD_MARGIN, 4 * mGridHeight + BOARD_MARGIN, 13 * mGridWidth + BOARD_MARGIN, 4 * mGridHeight + BOARD_MARGIN, 11 * mGridWidth + BOARD_MARGIN, 6 * mGridHeight + BOARD_MARGIN, 30, paints[0]);
        triangle[10].setCoord(11 * mGridWidth + BOARD_MARGIN, 6 * mGridHeight + BOARD_MARGIN, 13 * mGridWidth + BOARD_MARGIN, 6 * mGridHeight + BOARD_MARGIN, 13 * mGridWidth + BOARD_MARGIN, 4 * mGridHeight +BOARD_MARGIN, 31, paints[1]);
        rectangles[20].setCoord(13 * mGridWidth + BOARD_MARGIN, 4 * mGridHeight + BOARD_MARGIN, 14 * mGridWidth + BOARD_MARGIN, 6 * mGridHeight + BOARD_MARGIN, 32, paints[2]);
        rectangles[21].setCoord(14 * mGridWidth + BOARD_MARGIN, 4 * mGridHeight + BOARD_MARGIN, 15 * mGridWidth + BOARD_MARGIN, 6 * mGridHeight + BOARD_MARGIN, 33, paints[3]);
        triangle[11].setCoord(15 * mGridWidth + BOARD_MARGIN, 6 * mGridHeight + BOARD_MARGIN, 17 * mGridWidth + BOARD_MARGIN, 6 * mGridHeight + BOARD_MARGIN, 15 * mGridWidth + BOARD_MARGIN, 4 * mGridHeight + BOARD_MARGIN, 34, paints[0]);
        for (int i = 0; i < 5; i++) {
            rectangles[i+22].setCoord(15 * mGridWidth + BOARD_MARGIN, (6 + i) * mGridHeight + BOARD_MARGIN, 17 * mGridWidth + BOARD_MARGIN, (7 + i) * mGridHeight + BOARD_MARGIN, i + 35, paints[(i + 1) % 4]);
        }
        triangle[12].setCoord(15 * mGridWidth + BOARD_MARGIN, 11 * mGridHeight + BOARD_MARGIN, 17 * mGridWidth + BOARD_MARGIN, 11 * mGridHeight + BOARD_MARGIN, 15 * mGridWidth + BOARD_MARGIN, 13 * mGridHeight + BOARD_MARGIN, 40, paints[2]);
        rectangles[27].setCoord(14 * mGridWidth + BOARD_MARGIN, 11 * mGridHeight + BOARD_MARGIN, 15 * mGridWidth + BOARD_MARGIN, 13 * mGridHeight + BOARD_MARGIN, 41, paints[3]);
        rectangles[28].setCoord(13 * mGridWidth + BOARD_MARGIN, 11 * mGridHeight + BOARD_MARGIN, 14 * mGridWidth + BOARD_MARGIN, 13 * mGridHeight + BOARD_MARGIN, 42, paints[0]);
        triangle[13].setCoord(11 * mGridWidth + BOARD_MARGIN, 11 * mGridHeight + BOARD_MARGIN, 13 * mGridWidth + BOARD_MARGIN, 11 * mGridHeight + BOARD_MARGIN, 13 * mGridWidth + BOARD_MARGIN, 13 * mGridHeight +BOARD_MARGIN, 43, paints[1]);
        triangle[14].setCoord(11 * mGridWidth + BOARD_MARGIN, 13 * mGridHeight + BOARD_MARGIN, 13 * mGridWidth + BOARD_MARGIN, 13 * mGridHeight + BOARD_MARGIN, 11 * mGridWidth + BOARD_MARGIN, 11 * mGridHeight + BOARD_MARGIN, 44, paints[2]);
        rectangles[29].setCoord(11 * mGridWidth + BOARD_MARGIN, 13 * mGridHeight + BOARD_MARGIN, 13 * mGridWidth + BOARD_MARGIN, 14 * mGridHeight + BOARD_MARGIN, 45, paints[3]);
        rectangles[30].setCoord(11 * mGridWidth + BOARD_MARGIN, 14 * mGridHeight + BOARD_MARGIN, 13 * mGridWidth + BOARD_MARGIN, 15 * mGridHeight + BOARD_MARGIN, 46, paints[0]);
        triangle[15].setCoord(11 * mGridWidth + BOARD_MARGIN, 15 * mGridHeight + BOARD_MARGIN, 13 * mGridWidth + BOARD_MARGIN, 15 * mGridHeight + BOARD_MARGIN, 11 * mGridWidth + BOARD_MARGIN, 17 * mGridHeight +BOARD_MARGIN, 47, paints[1]);

        for (int i = 0; i < 5; i++) {
            rectangles[i + 31].setCoord(getMeasuredWidth() - ((7 + i) * mGridWidth + BOARD_MARGIN), 15 * mGridHeight + BOARD_MARGIN, getMeasuredWidth() - ((6 + i) * mGridWidth + BOARD_MARGIN), 17 * mGridHeight + BOARD_MARGIN, i + 48, paints[(i+2)%4]);
        }

        for (int i = 0; i < 5; i++) {
            square[i].setCoord((2 + i ) * mGridWidth +BOARD_MARGIN, 8 * mGridHeight + BOARD_MARGIN, (3 + i) * mGridWidth + BOARD_MARGIN, 9 * mGridHeight +BOARD_MARGIN, i + 53, paints[1]);
        }

        for (int i = 0; i < 5; i++) {
            square[i+5].setCoord(8 * mGridWidth + BOARD_MARGIN, (i + 2) * mGridHeight + BOARD_MARGIN, 9 * mGridWidth + BOARD_MARGIN, (i + 3) * mGridHeight + BOARD_MARGIN, i + 59, paints[2]);
        }

        for (int i = 0; i < 5; i++) {
            square[i+10].setCoord(getMeasuredWidth() - ((3 + i) * mGridWidth + BOARD_MARGIN), 8 * mGridHeight + BOARD_MARGIN, getMeasuredWidth() - ((2 + i) * mGridWidth + BOARD_MARGIN), 9 * mGridHeight + BOARD_MARGIN, i + 65, paints[3]);
        }

        for (int i = 0; i < 5; i++) {
            square[i+15].setCoord(8 * mGridWidth + BOARD_MARGIN, getMeasuredHeight() - ((3 + i) * mGridHeight + BOARD_MARGIN), 9 * mGridWidth + BOARD_MARGIN, getMeasuredHeight() - ((i + 2) * mGridHeight + BOARD_MARGIN), i + 71, paints[0]);
        }

        triangle[16].setCoord(7 * mGridWidth + BOARD_MARGIN, 10 * mGridHeight + BOARD_MARGIN, getMeasuredWidth()/2, getMeasuredHeight()/2, 7 * mGridWidth + BOARD_MARGIN, 7 * mGridHeight +BOARD_MARGIN, 58, paints[1]);
        triangle[17].setCoord(7 * mGridWidth + BOARD_MARGIN, 7 * mGridHeight + BOARD_MARGIN, getMeasuredWidth()/2, getMeasuredHeight()/2, 10 * mGridWidth + BOARD_MARGIN, 7 * mGridHeight +BOARD_MARGIN, 64, paints[2]);
        triangle[18].setCoord(getMeasuredWidth()/2, getMeasuredHeight()/2, 10 * mGridWidth + BOARD_MARGIN, 10 * mGridHeight + BOARD_MARGIN, 10 * mGridWidth + BOARD_MARGIN, 7 * mGridHeight +BOARD_MARGIN, 70, paints[3]);
        triangle[19].setCoord(7 * mGridWidth + BOARD_MARGIN, 10 * mGridHeight + BOARD_MARGIN, 10 * mGridWidth + BOARD_MARGIN, 10 * mGridHeight + BOARD_MARGIN, getMeasuredWidth()/2, getMeasuredHeight()/2, 76, paints[0]);

        square[20].setCoord(0, getMeasuredHeight() - 4 * mGridHeight, 2 * mGridWidth, getMeasuredHeight() - 2 * mGridHeight, 77, paints[0]);
        square[21].setCoord(2 * mGridWidth, getMeasuredHeight() - 4 * mGridHeight, 4 * mGridWidth, getMeasuredHeight() - 2 * mGridHeight, 78, paints[0]);
        square[22].setCoord(0, getMeasuredHeight() - 2 * mGridHeight, 2 * mGridWidth, getMeasuredHeight(), 79, paints[0]);
        square[23].setCoord(2 * mGridWidth, getMeasuredHeight() - 2 * mGridHeight, 4 * mGridWidth, getMeasuredHeight(), 80, paints[0]);

        square[24].setCoord(0, 0, 2 * mGridWidth, 2 * mGridHeight, 81, paints[1]);
        square[25].setCoord(2 * mGridWidth, 0, 4 * mGridWidth, 2 * mGridHeight, 82, paints[1]);
        square[26].setCoord(0, 2 * mGridHeight, 2 * mGridWidth, 4 * mGridHeight, 83, paints[1]);
        square[27].setCoord(2 * mGridWidth, 2 * mGridHeight, 4 * mGridWidth, 4 * mGridHeight, 84, paints[1]);

        square[28].setCoord(getMeasuredWidth() - 4 * mGridWidth, 0, getMeasuredWidth() - 2 * mGridWidth, 2 * mGridHeight, 85, paints[2]);
        square[29].setCoord(getMeasuredWidth() - 2 * mGridWidth, 0, getMeasuredWidth(), 2 * mGridHeight, 86, paints[2]);
        square[30].setCoord(getMeasuredWidth() - 4 * mGridWidth, 2 * mGridHeight, getMeasuredWidth() - 2 * mGridWidth, 4 * mGridHeight, 87, paints[2]);
        square[31].setCoord(getMeasuredWidth() - 2 * mGridWidth, 2 * mGridHeight, getMeasuredWidth(), 4 * mGridHeight, 88, paints[2]);

        square[32].setCoord(getMeasuredWidth() - 4 * mGridWidth, getMeasuredHeight() - 4 * mGridHeight, getMeasuredWidth() - 2 * mGridWidth, getMeasuredHeight() - 2 * mGridHeight, 89, paints[3]);
        square[33].setCoord(getMeasuredWidth() - 2 * mGridWidth, getMeasuredHeight() - 4 * mGridHeight, getMeasuredWidth(), getMeasuredHeight() - 2 * mGridHeight, 90, paints[3]);
        square[34].setCoord(getMeasuredWidth() - 4 * mGridWidth, getMeasuredHeight() - 2 * mGridHeight, getMeasuredWidth() - 2 * mGridWidth, getMeasuredHeight(), 91, paints[3]);
        square[35].setCoord(getMeasuredWidth() - 2 * mGridWidth, getMeasuredHeight() - 2 * mGridHeight, getMeasuredWidth(), getMeasuredHeight(), 92, paints[3]);
    }

    /*
     *
     */
    public boolean moveChess(int planeTag, int shapeTag) {
        if (planeNum.containsKey(planeTag)) {
            planeNum.remove(planeTag);
            planeNum.put(planeTag, shapeTag);
            invalidate();
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if(theSelectedColor == Color.NONE) return false;
                for (int i = theSelectedColor * 4 + 1; i <= theSelectedColor * 4 + 4; i++) {
                    int tmp = planeNum.get(i);
                    MyShape shape = planePosition.get(tmp);
                    if(shape.isPointInRegion(x,y)&& tmp != TAG_BLUE_END && tmp != TAG_RED_END && tmp != TAG_YELLOW_END && tmp != TAG_RED_END){
                        //theSelectedPlaneTag = i;
                        if(onSelectPlaneListener != null) {
                            setSelectFromColor(Color.NONE);
                            onSelectPlaneListener.OnSelectPlane(i,tmp);
                        }
                        invalidate();
                        break;
                    }
                }
                break;
        }
        return true;
    }

    public void setSelectFromColor(int color){
        this.theSelectedColor = color;
        invalidate();
    }

    public void restart() {
        initPlaneInfo();
        invalidate();
    }

    public interface OnSelectPlaneListener {
        void OnSelectPlane(int planeTag,int theSelectedPlaneTag);
    }

    public void setOnSelectPlaneListener(OnSelectPlaneListener onSelectPlaneListener) {
        this.onSelectPlaneListener = onSelectPlaneListener;
    }

    //先这样写着呗~虽然不是很好。但是我钟意...
    /*
    public int gettheSelectedPlaneTag(){
        return theSelectedPlaneTag;
    }

    public int gettheSelectedPlaneShapeTag(){
        return planeNum.get(theSelectedPlaneTag);
    }
    */

}
