package com.example.slidingpuzzlegame;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.core.view.MotionEventCompat;

public class PuzzlePiece extends androidx.appcompat.widget.AppCompatButton
        implements View.OnClickListener, View.OnTouchListener, View.OnDragListener {

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

    private int pieceWidth;
    private int pieceHeight;

    private float x1, x2, y1, y2;
    static final int MIN_DISTANCE = 1;
    //    private Bitmap baseImage;
//    private int difficulty;

    private boolean isMoving = false;


    @SuppressLint("ResourceAsColor")
    public PuzzlePiece(int pieceNumber, Bitmap baseImage, int pieceWidth,
                       int pieceHeight, int difficulty, Context context,
                       PuzzleMatrix puzzleMatrix, View rootView) {
        super(context);
        setOnTouchListener(this);
        setOnDragListener(this);
        this.context = context;
        this.puzzleMatrix = puzzleMatrix;
        this.pieceNumber = pieceNumber;
        pieceLocationIndex = pieceNumber;

        baseRow = pieceNumber / difficulty;
        baseColumn = pieceNumber % difficulty;
        //this.pieceWidth = pieceWidth;
        //this.pieceHeight = pieceHeight;
        if (baseImage != null) {
            pieceImage = Bitmap.createBitmap(baseImage, baseColumn * pieceWidth, baseRow * pieceHeight, pieceWidth, pieceHeight);
            //pieceImage = resize(pieceImage, rootView.getWidth() / difficulty, rootView.getHeight()/difficulty);
//        setScaleType(ScaleType.CENTER_INSIDE);
//        setAdjustViewBounds(true);
            //setPadding(4, 4, 4, 4);
            this.pieceHeight = pieceHeight;
            this.pieceWidth = pieceWidth;

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
//        setImageBitmap(pieceImage);
            setBackground(d);
        }

        else {
            //if base image is null
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f));
            params.width = pieceWidth;
            params.height = pieceHeight;
            params.bottomMargin = 4;
            params.leftMargin = 4;
            params.topMargin = 4;
            params.rightMargin = 4;
            setLayoutParams(params);

            setText(String.valueOf(pieceNumber + 1));
            setTextSize(30f);
            setBackgroundResource(R.drawable.puzzle_piece_background);
        }
    }

    public void setIsMoving(boolean newIsMoving) {
        isMoving = newIsMoving;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setPieceNumber(int newPieceNumber) {
        pieceNumber = newPieceNumber;
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

    private boolean touchIsDown;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                //puzzleMatrix.pieceClicked(pieceLocationIndex);
                //startDragAndDrop()
//                x1 = event.getX();
//                y1 = event.getY();
                break;
            case (MotionEvent.ACTION_UP):
                //Toast.makeText(context, "clicked " + pieceNumber, Toast.LENGTH_SHORT).show();
                puzzleMatrix.pieceClicked(pieceLocationIndex);
//                x2 = event.getX();
//                y2 = event.getY();
                break;
            case (MotionEvent.ACTION_MOVE):
//                ClipData data = ClipData.newPlainText("", "");
//                DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
//                        this);
//                startDragAndDrop(data, shadowBuilder, this, 0 );
//                if () {
//                    break;
//                }
                //puzzleMatrix.pieceClicked(pieceLocationIndex);
                return true;
                //case (MotionEvent.ACTIO)
        }
        return true;
        //return super.onTouchEvent(event);
    }

    public int getPieceHeight() {
        //return getHeight();

        return pieceHeight;
    }

    public int getPieceWidth() {
        //return getWidth();

        return pieceWidth;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Rect outRect = new Rect(getLeft(), getTop(), getRight(), getBottom());
        if (outRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {

        }
        return false;
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
//        switch (dragEvent.getAction()) {
//            case DragEvent.ACTION_DRAG_ENTERED:
//                setVisibility(View.INVISIBLE);
//                break;
//            case DragEvent.ACTION_DROP:
//                setVisibility(View.INVISIBLE);
//                break;
//
//        }
        return false;
    }
}

//