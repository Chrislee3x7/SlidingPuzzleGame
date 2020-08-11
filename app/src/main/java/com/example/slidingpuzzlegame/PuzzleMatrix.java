package com.example.slidingpuzzlegame;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;


public class PuzzleMatrix extends GestureDetector.SimpleOnGestureListener implements View.OnClickListener, View.OnTouchListener {

    private PuzzlePiece[] puzzlePieces;
    private PuzzlePiece[] solvedPuzzlePieces;
    private int difficulty;
    private int pieceCount;
    private int pieceSlots;
    private Bitmap baseImage;
    private int pieceHeight;
    private int pieceWidth;
    private GridLayout puzzleBoard;
    private PuzzleFragment puzzleFragment;
    private Context context;
    private View rootView;
    private ImageView sheenEffect;

    private boolean puzzleSolved = false;
    private boolean finalPieceAdded = false;
    private int frameWidth;
    private int frameHeight;

    private static final long pieceMoveTime = 200;
    private static final long pieceScrambleTime = 500;
    private static final int horizontalMargin = 16;
    private static final int verticalStopwatchMarginAndBoardMargin = 350;

    //for when game is paused and basically when game is not in foreground;
    private boolean movementLocked = false;


    public PuzzleMatrix(int difficulty, Resources resources, Context context, GridLayout puzzleBoard,
                        View rootView, int frameWidth, int frameHeight, Bitmap userImage,
                        PuzzleFragment puzzleFragment) {
        this.puzzleBoard = puzzleBoard;
        puzzleBoard.setOnClickListener(this);
        puzzleBoard.setOnTouchListener(this);
        this.difficulty = difficulty;
        this.frameWidth = frameWidth - horizontalMargin;
        this.frameHeight = frameHeight - verticalStopwatchMarginAndBoardMargin;
        this.context = context;
        this.rootView = rootView;
        this.puzzleFragment = puzzleFragment;
        sheenEffect = rootView.findViewById(R.id.sheen_effect);

        rootView.findViewById(R.id.scramble_button).setOnClickListener(this);
        //minus 1 for open hole at the end
        pieceCount = (difficulty * difficulty) - 1;
        pieceSlots = difficulty * difficulty;
        puzzlePieces = new PuzzlePiece[pieceSlots];
        solvedPuzzlePieces = new PuzzlePiece[pieceSlots];
        puzzleBoard.setColumnCount(difficulty);
        puzzleBoard.setRowCount(difficulty);
        if (userImage == null) {
            //this.baseImage = BitmapFactory.decodeResource(resources, R.drawable.goku_test_image);
            puzzleBoard.setBackgroundResource(R.drawable.puzzle_display_background);
            for (int i = 0; i < pieceCount; i++) {
                pieceWidth = 300 * 3 / difficulty;
                pieceHeight = 300 * 3 / difficulty;
                PuzzlePiece p = new PuzzlePiece(i, null, pieceWidth, pieceHeight,
                        difficulty, context, this, rootView);
                pieceWidth += 8;
                pieceHeight += 8;
                puzzlePieces[i] = p;
                solvedPuzzlePieces[i] = p;
                puzzleBoard.addView(p);
//                puzzleBoard.setPadding(2, 2, 2, 0);
            }
            //make the default puzzle style (same as the one on the home screen)
        } else {
            this.baseImage = userImage;
            this.baseImage = resize(this.baseImage);
            pieceWidth = baseImage.getWidth() / difficulty;
            pieceHeight = baseImage.getHeight() / difficulty;
            for (int i = 0; i < pieceCount; i++) {
                PuzzlePiece p = new PuzzlePiece(i, baseImage, (int) (pieceWidth), (int) (pieceHeight),
                        difficulty, context, this, rootView);
                puzzlePieces[i] = p;
                solvedPuzzlePieces[i] = p;
                puzzleBoard.addView(p);
            }
        }
    }

    public void addFinalPiece() {
        SoundPlayer.playHollowShimmerSound(context);
        if (baseImage == null) {
            //dont want to add an extra "tile" to the regular version
            performSheenEffect();
            return;
        }
        PuzzlePiece p = new PuzzlePiece(pieceCount, baseImage, (int) (pieceWidth),
                (int) (pieceHeight), difficulty, context, this, rootView);
        puzzlePieces[pieceCount] = p;
        //solvedPuzzlePieces[pieceCount] = p;
        p.setVisibility(View.INVISIBLE);
        AlphaAnimation fadeInFinalPiece = new AlphaAnimation(0.0f, 1.0f);
        fadeInFinalPiece.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                performSheenEffect();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        puzzleBoard.addView(p);
        p.setAnimation(fadeInFinalPiece);
        fadeInFinalPiece.setDuration(1800);
        fadeInFinalPiece.setFillAfter(false);
        fadeInFinalPiece.start();
        p.setVisibility(View.VISIBLE);
        finalPieceAdded = true;
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
        puzzleSolved = true;
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
        if (currentPiece.isMoving() || puzzleSolved || movementLocked) {
            return;
        }
        int distance;
        Direction openAdjacentDirection = getOpenAdjacentDirection(pieceLocationIndex);
        //if no piece open directly adjacent, check pieces in the same row and column
        Stack<PuzzlePiece> piecesToMove = getPiecesToMove(pieceLocationIndex);
        if (piecesToMove == null) {
            SoundPlayer.playHardRefuseClick(context);
            return;
        }
        SoundPlayer.playGlassClick(context);
        if (!puzzleFragment.stopwatchIsRunning()) {
            puzzleFragment.startStopwatch();
        }
        openAdjacentDirection = getOpenAdjacentDirection(piecesToMove.peek()
                .getPieceLocationIndex());
        if (openAdjacentDirection == Direction.UP || openAdjacentDirection == Direction.DOWN) {
//            distance = (int) currentPiece.getPieceHeight();
            distance = pieceHeight;
        } else {
//            distance = (int) currentPiece.getPieceWidth();
            distance = pieceWidth;
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
        if (currentBitmapWidth != frameWidth) {
            int newHeight = currentBitmapHeight * frameWidth / currentBitmapWidth;
            newBitmap = Bitmap.createScaledBitmap(bitmap, frameWidth, newHeight, true);
            currentBitmapWidth = newBitmap.getWidth();
            currentBitmapHeight = newBitmap.getHeight();
        }
        if (currentBitmapHeight > frameHeight) {
            int newWidth = currentBitmapWidth * frameHeight / currentBitmapHeight;
            newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, frameHeight, true);
            currentBitmapWidth = newBitmap.getWidth();
            currentBitmapHeight = newBitmap.getHeight();
        }
        frameWidth = currentBitmapWidth;
        frameHeight = currentBitmapHeight;

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
        puzzleFragment.countOneMove();
        piece.setIsMoving(true);
        ViewPropertyAnimator translate = piece.animate();
        translate.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                piece.setIsMoving(false);
                if (isSolved()) {
                    playCompletionAnimation();
                }
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
        //setAllPuzzleNumbers();
        for (int i = 0; i < pieceSlots; i++) {
            swapPieces(i, (int) (Math.random() * pieceSlots));
        }
        if (!isSolvableState()) {
            int pieceLocation1 = 0;
            int pieceLocation2 = 1;
            if (getPuzzlePiece(pieceLocation1) == null) {
                pieceLocation1 = 3;
            } else if (getPuzzlePiece(pieceLocation2) == null) {
                pieceLocation2 = 3;
            }
            swapPieces(pieceLocation1, pieceLocation2);
        }
        animateScrambleAll();
    }

    private boolean isSolvableState() {
        int inversions = 0;
        for (int i = 0; i < pieceSlots; i++) {
            PuzzlePiece p1 = getPuzzlePiece(i);
            if (p1 == null) {
                if (difficulty % 2 == 0) {
                    inversions += (difficulty - 1) - (i / difficulty);
                }
                continue;
            }
            int puzzleNumber1 = p1.getPieceNumber();
            for (int j = i; j < pieceSlots; j++) {
                PuzzlePiece p2 = getPuzzlePiece(j);
                if (p2 == null) {
                    continue;
                }
                int puzzleNumber2 = p2.getPieceNumber();
                if (puzzleNumber1 > puzzleNumber2) {
                    inversions++;
                }
            }
        }
        return inversions % 2 == 0;

    }

    public void setAllPuzzleNumbers() {
        for (int i = 0; i < pieceSlots; i++) {
            PuzzlePiece p = getPuzzlePiece(i);
            if (p != null) {
                p.setPieceNumber(p.getPieceLocationIndex());
            }
        }
    }

    public void animateLinearMove(final PuzzlePiece piece1, int newLocation) {
        int originalLocation = piece1.getPieceNumber();
        int distanceX = ((newLocation % difficulty) - (originalLocation % difficulty)) * pieceWidth;
        int distanceY = ((newLocation / difficulty) - (originalLocation / difficulty)) * pieceHeight;

        ViewPropertyAnimator linearScrambleMove = piece1.animate();
        linearScrambleMove.translationXBy(distanceX);
        linearScrambleMove.translationYBy(distanceY);
        linearScrambleMove.setDuration(pieceScrambleTime);
        piece1.setIsMoving(true);
        linearScrambleMove.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                piece1.setIsMoving(false);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
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
                continue;
            }
            int newLocation = piece.getPieceLocationIndex();
            animateLinearMove(piece, newLocation);
        }
    }

    public void playCompletionAnimation() {
        if (finalPieceAdded) {
            return;
        }
        puzzleFragment.pauseStopwatch();
        addFinalPiece();
        //for now, just a toast
        //Toast.makeText(context, "Congratulations", Toast.LENGTH_SHORT).show();
        //make final puzzle piece fade in
        //then whole puzzle blinks
        //maybe some sound
        //open finish screen fragment
        //  has time, and moves taken.
    }

    public void setMovementLocked(boolean movementLocked) {
        this.movementLocked = movementLocked;
    }

    public void performSheenEffect() {
        sheenEffect.getLayoutParams().width = puzzleBoard.getWidth();
        sheenEffect.getLayoutParams().height = puzzleBoard.getHeight();
        TranslateAnimation animation = new TranslateAnimation
                (-frameWidth, (frameWidth + sheenEffect.getWidth()), 0, 0);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                puzzleFragment.puzzleEnd(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setDuration(1000);
        animation.setFillAfter(false);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        sheenEffect.setVisibility(View.INVISIBLE);
        sheenEffect.startAnimation(animation);
        SoundPlayer.playSheenSound(context);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.scramble_button:
                break;
            case R.id.puzzle_board:
                break;

        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        //SoundPlayer.playOptionsClick(context);
        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            return true;
        }
        for (int i = 0; i < pieceSlots; i++) {

            PuzzlePiece piece = getPuzzlePiece(i);
            if (piece == null || getOpenAdjacentDirection(piece.getPieceLocationIndex()) == null) {
                continue;
            }
            int boardLocation = translateCoordinatesToPieceIndex((int) motionEvent.getX(), (int) motionEvent.getY());
            if (boardLocation != -1 && getPuzzlePiece(boardLocation) != null && boardLocation == piece.getPieceLocationIndex()) {
                // over a View
                if (piece.isMoving()) {
                    return false;
                }
                pieceClicked(piece.getPieceLocationIndex());
            }
        }
        return false;
    }

    public int translateCoordinatesToPieceIndex(int coordX, int coordY) {
        int fieldWidth = puzzleBoard.getWidth();
        int fieldHeight = puzzleBoard.getHeight();
        int locX = coordX * difficulty / fieldWidth;
        int locY = coordY * difficulty / fieldHeight;
        int boardLocation = locX + locY * difficulty;
        if (!isPieceLocationInRange(boardLocation)){
            return -1;
        }
        return boardLocation;
    }



    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
        float finalIndexX;
        float finalIndexY;
        float startIndexX = e1.getX();
        float startIndexY = e1.getY();
        // we know already coordinates of first touch
        // we know as well a scroll distance
        finalIndexX = startIndexX - distanceX;
        finalIndexY = startIndexY - distanceY;

        // when the user scrolls within our side index
        // we can show for every position in it a proper
        // item in the country list
        if (finalIndexX >= 0 && finalIndexY >= 0) {
            int i = 0;
        }

        return super.onScroll(e1, e2, distanceX, distanceY);
    }
}
