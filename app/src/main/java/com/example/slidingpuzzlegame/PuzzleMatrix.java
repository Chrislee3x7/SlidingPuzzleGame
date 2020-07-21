package com.example.slidingpuzzlegame;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.Stack;

public class PuzzleMatrix {

    private PuzzlePiece[] puzzlePieces;
    private PuzzlePiece[] solvedPuzzlePieces;
    private int difficulty;
    private int pieceCount;
    private int pieceSlots;
    private Bitmap baseImage;
    private int pieceHeight;
    private int pieceWidth;
    private GridLayout puzzleBoard;
    private Context context;

    private int frameWidth;
    private int frameHeight;

    private static final long pieceMoveTime = 200;
    private static final long pieceScrambleTime = 500;


    public PuzzleMatrix(int difficulty, Resources resources, Context context, GridLayout puzzleBoard,
                        View rootView, int frameWidth, int frameHeight) {
        this.puzzleBoard = puzzleBoard;
        this.difficulty = difficulty;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.context = context;
        //minus 1 for open hole at the end
        pieceCount = (difficulty * difficulty) - 1;
        pieceSlots = difficulty * difficulty;
        baseImage = BitmapFactory.decodeResource(resources, R.drawable.goku_test_image);
        baseImage = resize(baseImage);
        pieceWidth = baseImage.getWidth() / difficulty;
        pieceHeight = baseImage.getHeight() / difficulty;
        puzzlePieces = new PuzzlePiece[pieceSlots];
        solvedPuzzlePieces = new PuzzlePiece[pieceSlots];
        puzzleBoard.setColumnCount(difficulty);
        puzzleBoard.setRowCount(difficulty);
        //GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        PuzzlePiece pTest = null;
        for (int i = 0; i < pieceCount; i++) {
            PuzzlePiece p = new PuzzlePiece(i, baseImage, (int) (pieceWidth), (int) (pieceHeight), difficulty, context, this, rootView);
            puzzlePieces[i] = p;
            solvedPuzzlePieces[i] = p;
            puzzleBoard.addView(p);
        }
    }


    public int getDifficulty() {
        return difficulty;
    }

    public void setPuzzlePiece(int position, PuzzlePiece piece) {
        //change the location of the piece in the array
        puzzlePieces[position] = piece;
        //change the piece's location field
        if (piece != null) {
            piece.setPieceLocationIndex(position);
        }
    }

    public PuzzlePiece getPuzzlePiece(int position) {
        return puzzlePieces[position];
    }

    public boolean isSolved() {
        for (int i = 0; i < pieceCount; i++) {
            PuzzlePiece p = getPuzzlePiece(i);
            if (p == null || p.getPieceNumber() != i) {
                return false;
            }
        }
        return true;
    }

    public Direction getOpenAdjacentDirection(int pieceLocationIndex) {
        int upLocation = pieceLocationIndex - difficulty;
        int downLocation = pieceLocationIndex + difficulty;
        //these two must be checked for same line
        int rightLocation = pieceLocationIndex + 1;
        int leftLocation = pieceLocationIndex - 1;

        if (upLocation >= 0 && getPuzzlePiece(upLocation) == null) {
            return Direction.UP;
        } else if (downLocation <= pieceCount && getPuzzlePiece(downLocation) == null) {
            return Direction.DOWN;
        } else if (rightLocation <= pieceCount && rightLocation / difficulty == pieceLocationIndex / difficulty
                && getPuzzlePiece(rightLocation) == null) {
            return Direction.RIGHT;
        } else if (leftLocation >= 0 && leftLocation / difficulty == pieceLocationIndex / difficulty
                && getPuzzlePiece(leftLocation) == null) {
            return Direction.LEFT;
        } else {
            return null;
        }
    }

    public void pieceClicked(int pieceLocationIndex) {
        PuzzlePiece currentPiece = getPuzzlePiece(pieceLocationIndex);
        if (currentPiece.isMoving()) {
            return;
        }
        int distance;
        Direction openAdjacentDirection = getOpenAdjacentDirection(pieceLocationIndex);
        //if no piece open directly adjacent, check pieces in the same row and column
        Stack<PuzzlePiece> piecesToMove = getPiecesToMove(pieceLocationIndex);
        if (piecesToMove == null) {
            return;
        }
        openAdjacentDirection = getOpenAdjacentDirection(piecesToMove.peek()
                .getPieceLocationIndex());
        if (openAdjacentDirection == Direction.UP || openAdjacentDirection == Direction.DOWN) {
            distance = (int) currentPiece.getPieceHeight();
        } else {
            distance = (int) currentPiece.getPieceWidth();
        }
        while (!piecesToMove.empty()) {
            PuzzlePiece p = piecesToMove.pop();
            if (p.isMoving()) {
                return;
            }
            movePiece(p, distance, openAdjacentDirection);
        }
        if (isSolved()) {
            playCompletionAnimation();
        }

    }

    private Bitmap resize(Bitmap bitmap) {
        int currentBitmapWidth = bitmap.getWidth();
        int currentBitmapHeight = bitmap.getHeight();
        Bitmap newBitmap = bitmap;
        if (currentBitmapWidth > frameWidth) {
            int newHeight = currentBitmapHeight * frameWidth / currentBitmapWidth;
            newBitmap = Bitmap.createScaledBitmap(bitmap, frameWidth, newHeight, true);
            currentBitmapWidth = newBitmap.getWidth();
            currentBitmapHeight = newBitmap.getHeight();
        }
        if (currentBitmapHeight > frameHeight) {
            int newWidth = currentBitmapWidth * frameHeight / currentBitmapHeight;
            newBitmap = Bitmap.createScaledBitmap(bitmap, frameWidth, newWidth, true);
        }
        return newBitmap;
    }

    public boolean isSameRow(int pieceLocationIndex1, int pieceLocationIndex2) {
        return pieceLocationIndex1 / difficulty == pieceLocationIndex2 / difficulty;
    }

    public Stack<PuzzlePiece> getPiecesToMove(int pieceLocationIndex) {
        PuzzlePiece nextUpPiece;
        PuzzlePiece nextDownPiece;
        PuzzlePiece nextLeftPiece;
        PuzzlePiece nextRightPiece;
        int nextUpLocation;
        int nextDownLocation;
        int nextLeftLocation;
        int nextRightLocation;
        Stack<PuzzlePiece> upPieces = new Stack<>();
        Stack<PuzzlePiece> downPieces = new Stack<>();
        Stack<PuzzlePiece> leftPieces = new Stack<>();
        Stack<PuzzlePiece> rightPieces = new Stack<>();
        for (int i = 0; i < difficulty; i++) {
            nextUpLocation = pieceLocationIndex - difficulty * i;
            nextDownLocation = pieceLocationIndex + difficulty * i;
            nextLeftLocation = pieceLocationIndex - i;
            nextRightLocation = pieceLocationIndex + i;
            if (isPieceLocationInRange(nextUpLocation)) {
                nextUpPiece = getPuzzlePiece(nextUpLocation);
                if (nextUpPiece != null) {
                    upPieces.add(nextUpPiece);
                } else {
                    return upPieces;
                }
            }
            if (isPieceLocationInRange(nextDownLocation)) {
                nextDownPiece = getPuzzlePiece(nextDownLocation);
                if (nextDownPiece != null) {
                    downPieces.add(nextDownPiece);
                } else {
                    return downPieces;
                }
            }
            if (isSameRow(pieceLocationIndex, nextLeftLocation)
                    && isPieceLocationInRange(nextLeftLocation)) {
                nextLeftPiece = getPuzzlePiece(nextLeftLocation);
                if (nextLeftPiece != null) {
                    leftPieces.add(nextLeftPiece);
                } else {
                    return leftPieces;
                }
            }
            if (isSameRow(pieceLocationIndex, nextRightLocation)
                    && isPieceLocationInRange(nextRightLocation)) {
                nextRightPiece = getPuzzlePiece(nextRightLocation);
                if (nextRightPiece != null) {
                    rightPieces.add(nextRightPiece);
                } else {
                    return rightPieces;
                }
            }
        }
        return null;
    }

    public boolean isPieceLocationInRange(int pieceLocationIndex) {
        return pieceLocationIndex >= 0 && pieceLocationIndex <= pieceCount;
    }

    public int getAdjacentPieceIndexByDirection(int pieceLocationIndex, Direction direction) {
        int newPieceLocationIndex = -1;
        switch (direction) {
            case UP:
                newPieceLocationIndex = pieceLocationIndex - difficulty;
                break;
            case DOWN:
                newPieceLocationIndex = pieceLocationIndex + difficulty;
                break;
            case LEFT:
                newPieceLocationIndex = pieceLocationIndex - 1;
                break;
            case RIGHT:
                newPieceLocationIndex = pieceLocationIndex + 1;
                break;
        }
//        if (!isPieceLocationInRange(newPieceLocationIndex)) {
//            return 0;
//        }
        return newPieceLocationIndex;
    }

    public void movePiece(final PuzzlePiece piece, int distance, Direction direction) {
        if (piece.isMoving()) {
            //basically if animation for this piece is still going
            return;
        }
        piece.setIsMoving(true);
        ViewPropertyAnimator translate = piece.animate();
        translate.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                piece.setIsMoving(false);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        translate.setDuration(pieceMoveTime);
        int newPieceLocationIndex = -1;
        switch (direction) {
            case UP:
                newPieceLocationIndex = piece.getPieceLocationIndex() - difficulty;
                translate.translationYBy(-distance);
                break;
            case DOWN:
                newPieceLocationIndex = piece.getPieceLocationIndex() + difficulty;
                translate.translationYBy(distance);
                break;
            case LEFT:
                newPieceLocationIndex = piece.getPieceLocationIndex() - 1;
                translate.translationXBy(-distance);
                break;
            case RIGHT:
                newPieceLocationIndex = piece.getPieceLocationIndex() + 1;
                translate.translationXBy(distance);
                break;
        }
        updatePuzzleBoardLocations(piece, newPieceLocationIndex);
        translate.start();
    }

    public void updatePuzzleBoardLocations(PuzzlePiece piece, int newPieceLocationIndex) {
        setPuzzlePiece(piece.getPieceLocationIndex(), null);
        setPuzzlePiece(newPieceLocationIndex, piece);
    }

    public void scramblePuzzle() {
        setAllPuzzleNumbers();
        for (int i = 0; i < pieceSlots; i++) {
            swapPieces(i, (int) (Math.random() * pieceSlots));
        }
        animateScrambleAll();
    }

    public void setAllPuzzleNumbers() {
        for (int i = 0; i < pieceSlots; i++) {
            PuzzlePiece p = getPuzzlePiece(i);
            if (p != null) {
                p.setPieceNumber(p.getPieceLocationIndex());
            }
        }
    }

    public void animateLinearMove(PuzzlePiece piece1, int newLocation) {
        int originalLocation = piece1.getPieceNumber();
        int distanceX = ((newLocation % difficulty) - (originalLocation % difficulty)) * pieceWidth;
        int distanceY = ((newLocation / difficulty) - (originalLocation / difficulty)) * pieceHeight;
        int finalPositionX = piece1.getLeft() + distanceX;
        int finalPositionY = piece1.getTop() + distanceY;
//        final SpringAnimation moveX = new SpringAnimation(piece1, DynamicAnimation.TRANSLATION_X, finalPositionX);
//        final SpringAnimation moveY = new SpringAnimation(piece1, DynamicAnimation.TRANSLATION_Y, finalPositionY);
//        moveX.animateToFinalPosition(finalPositionX);
//        moveY.animateToFinalPosition(finalPositionY);

        ViewPropertyAnimator linearScrambleMove = piece1.animate();
        linearScrambleMove.translationXBy(distanceX);
        linearScrambleMove.translationYBy(distanceY);
        linearScrambleMove.setDuration(pieceScrambleTime);
        linearScrambleMove.start();
        //piece1.requestFocus();
    }

    public void swapPieces(int pieceLocation1, int pieceLocation2) {
        PuzzlePiece piece1 = getPuzzlePiece(pieceLocation1);
        PuzzlePiece piece2 = getPuzzlePiece(pieceLocation2);
        //CHECK NULL do not need to animate null spot
        setPuzzlePiece(pieceLocation2, piece1);
        setPuzzlePiece(pieceLocation1, piece2);
    }

    public void animateScrambleAll() {
        for (int i = 0; i < pieceSlots; i++) {
            PuzzlePiece piece = solvedPuzzlePieces[i];
            if (piece == null) {
                break;
            }
            int newLocation = piece.getPieceLocationIndex();
            animateLinearMove(piece, newLocation);
        }
    }

    public void playCompletionAnimation() {
        //for now, just a toast
        Toast.makeText(context, "Congratulations", Toast.LENGTH_SHORT).show();
        //make final puzzle piece fade in
        //then whole puzzle blinks
        //maybe some sound
        //open finish screen fragment
        //  has time, and moves taken.
    }
}
