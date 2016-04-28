package com.project.flyingchess.widget;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.project.flyingchess.R;

import java.util.Random;

/**
 * Created by youzh on 2015/4/23.
 */
public class ShakeLayout extends FrameLayout {

    private Context ctx;
    private ShakeView shakeView;

    private Random mRandom = new Random();
    private int mImgs[] = {R.mipmap.dice1, R.mipmap.dice2, R.mipmap.dice3, R.mipmap.dice4, R.mipmap.dice5, R.mipmap.dice6};
    private int mImgsAnim[] = {R.mipmap.dice_f1, R.mipmap.dice_f2, R.mipmap.dice_f3, R.mipmap.dice_f4};
    private int mCount;
    private boolean isStopAnim = false;
    private View view;
    private AnimListener mAnimListener;
    public static final  int LOCAL = 0;
    private int model = LOCAL;

    public interface AnimListener {
        void onAnimFinish();
    }

    public void setAnimListener (AnimListener listener) {
        if (listener != null) {
            mAnimListener = listener;
        }
    }

    public ShakeLayout(Context context) {
        super(context, null);
    }

    public ShakeLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.ctx = context;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        view = LayoutInflater.from(ctx).inflate(R.layout.layout_shake, null);
        shakeView = new ShakeView(view);
        LayoutParams layoutParams=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(view, layoutParams);
    }

    private void randomImg(ImageView imageView,int random) {
        imageView.setImageResource(mImgs[random-1]);
    }

    private void randomImgAnim(final ImageView imageView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStopAnim) {
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            int num = mRandom.nextInt(4);
                            imageView.setImageResource(mImgsAnim[num]);
                        }
                    });
                    SystemClock.sleep(80);
                }
            }
        }).start();
    }

    public void setModel (int model) {
        this.model = model;
    }

    public void anim(final int random) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 1.0f);
        alphaAnimation.setDuration(1000);
        view.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (model == LOCAL) {
                    isStopAnim = false;
                    randomImgAnim(shakeView.shakeImg);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isStopAnim = true;
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        showRadom(random);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void showRadom(int random) {
        randomImg(shakeView.shakeImg,random);
        mAnimListener.onAnimFinish();
    }

    static class ShakeView {
        ImageView shakeImg;
        ShakeView(View view) {
            shakeImg = (ImageView) view.findViewById(R.id.shake_img);
        }
    }
}
