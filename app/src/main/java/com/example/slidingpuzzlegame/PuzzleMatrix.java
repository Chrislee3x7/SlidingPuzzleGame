package com.example.slidingpuzzlegame;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.GridLayout;

import java.util.Stack;

public class PuzzleMatrix {

    private PuzzlePiece[] puzzlePieces;
    private int difficulty;
    private int pieceCount;
    private Bitmap baseImage;
    private int pieceHeight;
    private int pieceWidth;
    private GridLayout puzzleBoard;

    private int frameWidth;
    private int frameHeight;

    private static final long pieceMovetime = 200;


    public PuzzleMatrix(int difficulty, Resources resources, Context context, GridLayout puzzleBoard,
                        View rootView, int frameWidth, int frameHeight) {
        this.puzzleBoard = puzzleBoard;
        this.difficulty = difficulty;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        //minus 1 for open hole at the end
        pieceCount = (difficulty * difficulty) - 1;
        baseImage = BitmapFactory.decodeResource(resources, R.drawable.silicon_chip_test_picture);
        baseImage = resize(baseImage);
        pieceWidth = baseImage.getWidth() / difficulty;
        pieceHeight = baseImage.getHeight() / difficulty;
        puzzlePieces = new PuzzlePiece[difficulty * difficulty];
        puzzleBoard.setColumnCount(difficulty);
        puzzleBoard.setRowCount(difficulty);
        //GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        PuzzlePiece pTest = null;
        for (int i = 0; i < pieceCount; i++) {
            PuzzlePiece p = new PuzzlePiece(i, baseImage, (int) (pieceWidth), (int) (pieceHeight), difficulty, context, this, rootView);
            puzzlePieces[i] = p;
            puzzleBoard.addView(p);
        }
    }


    public int getDifficulty() {
        return difficulty;
    }

    public void setPuzzlePiece(int position, PuzzlePiece piece) {
        puzzlePieces[position] = piece;
        if (piece != null) {
            piece.setPieceLocationIndex(position);
        }
    }

    public PuzzlePiece getPuzzlePiece(int position) {
        return puzzlePieces[position];
    }

    public boolean isSolved() {
        for (int i = 0; i < pieceCount; i++) {
            if (getPuzzlePiece(i).getPieceNumber() != i) {
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
        translate.setDuration(pieceMovetime);
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

    }

    public void playCompletionAnimation() {
        //make final puzzle piece fade in
        //then whole puzzle blinks
        //maybe some sound
        //open finish screen fragment
        //  has time, and moves taken.
    }
}
