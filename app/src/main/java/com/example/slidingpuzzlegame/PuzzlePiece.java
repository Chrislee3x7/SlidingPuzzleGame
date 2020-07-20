package com.example.slidingpuzzlegame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.view.MotionEventCompat;

public class PuzzlePiece extends androidx.appcompat.widget.AppCompatImageView implements View.OnClickListener {

    private final PuzzleMatrix puzzleMatrix;
    private int pieceNumber;
    private Bitmap pieceImage;
    private int baseRow;
    private int baseColumn;
    private Context context;
    private int pieceLocationIndex;
    private int w;
    private int h;
    private View rootView;

    private float x1, x2, y1, y2;
    static final int MIN_DISTANCE = 1;
//    private Bitmap baseImage;
//    private int difficulty;
    private int pieceWidth;
    private int pieceHeight;

    private boolean isMoving = false;


    @SuppressLint("ResourceAsColor")
    public PuzzlePiece(int pieceNumber, Bitmap baseImage, int pieceWidth, int pieceHeight, int difficulty, Context context, PuzzleMatrix puzzleMatrix, View rootView) {
        super(context);
        this.context = context;
        this.puzzleMatrix = puzzleMatrix;
        this.pieceNumber = pieceNumber;
        this.pieceWidth = pieceWidth;
        this.pieceHeight = pieceHeight;
        pieceLocationIndex = pieceNumber;

        baseRow = pieceNumber / difficulty;
        baseColumn = pieceNumber % difficulty;
        //this.pieceWidth = pieceWidth;
        //this.pieceHeight = pieceHeight;
        pieceImage = Bitmap.createBitmap(baseImage, baseColumn * pieceWidth, baseRow * pieceHeight, pieceWidth, pieceHeight);
        //pieceImage = resize(pieceImage, rootView.getWidth() / difficulty, rootView.getHeight()/difficulty);
        setScaleType(ScaleType.CENTER_INSIDE);
        setAdjustViewBounds(true);
        //setPadding(4, 4, 4, 4);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED),
                GridLayout.spec(GridLayout.UNDEFINED));
        params.width = pieceWidth;
        params.height = pieceHeight;
        //params.width = rootView.getWidth() - 16 / difficulty;
//        params.height = 200;
        setLayoutParams(params);
        setBackgroundColor(R.color.white);
        setDrawingCacheEnabled(true);
        Drawable d = new BitmapDrawable(getResources(), pieceImage);
        setImageBitmap(pieceImage);
        //setBackgroundDrawable(d);
        int a = getWidth();
    }

    public void setIsMoving(boolean newIsMoving) {
        isMoving = newIsMoving;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public int getPieceNumber() {
        return pieceNumber;
    }

    public int getPieceLocationIndex() {
        return pieceLocationIndex;
    }

    public void setPieceLocationIndex(int newLocationIndex) {
        pieceLocationIndex = newLocationIndex;
    }

    @Override
    public void onClick(View view) {

    }

    private Bitmap resize(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
            return bitmap;
        } else {
            return bitmap;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case (MotionEvent.ACTION_DOWN):
//                x1 = event.getX();
//                y1 = event.getY();
                return true;
            case (MotionEvent.ACTION_UP):
                        //Toast.makeText(context, "clicked " + pieceNumber, Toast.LENGTH_SHORT).show();
                        int a = getWidth();
                        puzzleMatrix.pieceClicked(pieceLocationIndex);
//                x2 = event.getX();
//                y2 = event.getY();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public int getPieceHeight() {
        return pieceHeight;
    }

    public int getPieceWidth() {
        return pieceWidth;
    }
}

//