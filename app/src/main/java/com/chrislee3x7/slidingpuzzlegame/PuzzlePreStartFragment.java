package com.chrislee3x7.slidingpuzzlegame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PuzzlePreStartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PuzzlePreStartFragment extends Fragment implements View.OnClickListener {
    private static final int PICK_IMAGE = 100;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    private View rootView;
    private ImageButton cancelButton;
    private ImageButton confirmButton;
    private Spinner difficultyDropdown;
    private SeekBar difficultySlider;
    private Button chooseAnImageButton;
    private Button takeAPhotoButton;
    private ImageView imagePreview;
    private TextView imagePreviewText;
    final int REQUEST_IMAGE_CAPTURE = 1;
    final int RESULT_OK = -1;
    private Uri selectedImageUri = null;

    private String currentPhotoPath = "";

    //puzzle configs
    private int difficulty;
    private String imageUri;

    public PuzzlePreStartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PuzzlePreStartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PuzzlePreStartFragment newInstance(String param1, String param2) {
        PuzzlePreStartFragment fragment = new PuzzlePreStartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_bottom));
        setExitTransition(inflater.inflateTransition(R.transition.slide_bottom));
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_puzzle_pre_start, container, false);
        cancelButton = (ImageButton) rootView.findViewById(R.id.cancel_button);
        confirmButton = (ImageButton) rootView.findViewById(R.id.confirm_button);
        difficultyDropdown = (Spinner) rootView.findViewById(R.id.difficulty_dropdown);
//        difficultySlider = (SeekBar) rootView.findViewById(R.id.difficulty_slider);

        imagePreview = rootView.findViewById(R.id.image_preview);
        imagePreviewText = rootView.findViewById(R.id.image_preview_text);
//        imagePreview.setImageBitmap(
//                BitmapFactory.decodeResource(getResources(), R.drawable.goku_test_image));

        cancelButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);

        //no longer using button
//        chooseAnImageButton = rootView.findViewById(R.id.choose_photo_button);
//        chooseAnImageButton.setOnClickListener(this);

        imagePreviewText.setOnClickListener(this);

        //not using this for now
//        takeAPhotoButton = rootView.findViewById(R.id.take_photo_button);
//        takeAPhotoButton.setOnClickListener(this);
        return rootView;
    }

    public void close() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    public void openPuzzleFragment(int puzzleDifficulty) {
        PuzzleFragment puzzleFragment = new PuzzleFragment();
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        Bundle bundle = new Bundle();
        difficulty = puzzleDifficulty;
        bundle.putInt("Difficulty", difficulty);
        if (selectedImageUri == null) {
            imageUri = null;
        } else {
            imageUri = selectedImageUri.toString();
        }
        bundle.putString("Selected Image", imageUri);
        puzzleFragment.setArguments(bundle);
        // Add the fragment to the 'fragment_container' FrameLayout
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main, puzzleFragment).commit();
        close();
    }

    public Bundle getPuzzleConfigurations() {
        Bundle puzzleConfigs = new Bundle();
        puzzleConfigs.putString("Selected Image", imageUri);
        puzzleConfigs.putInt("Difficulty", difficulty);
        return puzzleConfigs;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_button:
                SoundPlayer.playLiquidDropClick(getContext());
                //close this fragment and go back to main menu/home
                ((MainActivity) getActivity()).showMainMenu();
                close();
                break;
            case R.id.confirm_button:
                SoundPlayer.playLiquidDropClick(getContext());
                //close this fragment and go to the puzzle of the specified info
                int difficulty = Integer.parseInt((Character.toString(difficultyDropdown.getSelectedItem().toString().charAt(0))));
                openPuzzleFragment(difficulty);
                close();
                break;

            case R.id.image_preview_text:
                SoundPlayer.playLiquidDropClick(getContext());
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            imagePreview.setImageURI(selectedImageUri);
            imagePreviewText.setText("");
            imagePreview.setVisibility(View.VISIBLE);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imagePreview.setImageURI(getImageUri(getContext(), bitmap));
//            imagePreview.setImageBitmap(bitmap);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}