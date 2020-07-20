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

    private float x1, x2, y1, y2;
    static final int MIN_DISTANCE = 1;
//    private Bitmap baseImage;
//    private int difficulty;
//    private int pieceWidth;
//    private int pieceHeight;

    private boolean isMoving = false;


    @SuppressLint("ResourceAsColor")
    public PuzzlePiece(int pieceNumber, Bitmap baseImage, int pieceWidth, int pieceHeight, int difficulty, Context context, PuzzleMatrix puzzleMatrix) {
        super(context);
        this.context = context;
        this.puzzleMatrix = puzzleMatrix;
        this.pieceNumber = pieceNumber;
        pieceLocationIndex = pieceNumber;
        //this.baseImage = baseImage;
        //this.difficulty = difficulty;
        baseRow = pieceNumber / difficulty;
        baseColumn = pieceNumber % difficulty;
        //this.pieceWidth = pieceWidth;
        //this.pieceHeight = pieceHeight;
        pieceImage = Bitmap.createBitmap(baseImage, baseColumn * pieceWidth, baseRow * pieceHeight, pieceWidth, pieceHeight);
        setScaleType(ScaleType.CENTER_CROP);
        setAdjustViewBounds(false);
        //setPadding(4, 4, 4, 4);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f));
        params.width = GridLayout.LayoutParams.WRAP_CONTENT;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        //params.width = 200;
        //params.height = 200;
        setLayoutParams(params);
        setBackgroundColor(R.color.white);
        setDrawingCacheEnabled(true);
        Drawable d = new BitmapDrawable(getResources(), pieceImage);
        //setBackgroundColor(R.color.black);
        setBackgroundDrawable(d);
        //Bitmap b = ((BitmapDrawable) getBackground()).getBitmap();
//        w = b.getWidth();
//        h = b.getHeight();
        int right = getRight();
        int left = getLeft();
        w = right - left;
        w = 241;
        h = 241;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
//                x1 = event.getX();
//                y1 = event.getY();
                return true;
            case (MotionEvent.ACTION_UP):
                        //Toast.makeText(context, "clicked " + pieceNumber, Toast.LENGTH_SHORT).show();
                        puzzleMatrix.pieceClicked(pieceLocationIndex);
//                x2 = event.getX();
//                y2 = event.getY();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

}

//