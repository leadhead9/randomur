package com.ewisnor.randomur.ui.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ewisnor.randomur.R;
import com.ewisnor.randomur.application.RandomurApp;
import com.ewisnor.randomur.application.RandomurLogger;
import com.ewisnor.randomur.iface.OnImageDownloadedListener;
import com.ewisnor.randomur.task.FetchFullImageTask;

/**
 * DialogFragment that displays a full size image as selected from the thumbnail grid.
 * Will download the image using the FetchFullImageTask during onCreateDialog.
 *
 * Created by evan on 2015-01-04.
 */
public class FullImageDialogFragment extends DialogFragment implements OnImageDownloadedListener {
    public static final String IMAGE_ID_ARGUMENT = "imageIdArgument";
    public static final String STATE_IMAGE = "stateImage";

    private View view;
    private Bitmap image;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Integer imageId = getArguments().getInt(IMAGE_ID_ARGUMENT);

        Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = layoutInflater.inflate(R.layout.dialogfragment_full_image, null);
        dialog.setContentView(view);


        if (savedInstanceState == null) {
            RandomurApp appContext = (RandomurApp) getActivity().getApplication();
            new FetchFullImageTask(appContext, this).execute(imageId);
        }
        else {
            onRestoreInstanceState(savedInstanceState);
        }

        return dialog;
    }

    /**
     * To be called when savedInstanceState is not null -- Probably because the device screen orientation
     * changed.
     * @param savedInstanceState State bundle
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        image = savedInstanceState.getParcelable(STATE_IMAGE);
        showImage();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_IMAGE, image);
    }

    /**
     * Display the downloaded image by hiding the progress bar spinner, setting the bitmap
     * to the imageview and then showing the imageview.
     */
    public void showImage() {
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        progressBar.setVisibility(View.INVISIBLE);
        imageView.setImageBitmap(image);
        imageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnImageDownloaded(Bitmap image) {
        if (image == null) {
            RandomurLogger.error("Failed to show full size image");
        }
        else {
            this.image = image;
            showImage();
        }
    }
}
