package com.bruce.raeasy.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bruce.raeasy.R;
import com.bruce.raeasy.models.Payment;
import com.bruce.raeasy.utils.CodeGenerator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import static com.bruce.raeasy.utils.Constants.PAYMENT_REF;
import static com.bruce.raeasy.utils.Constants.RATING_REF;

public class RatingDialog extends AppCompatDialogFragment {

    private static final String TAG = "PaymentDialog";
    private AlertDialog mAlertDialog;
    private String userId;
    private RatingListener mRatingListener;
    private ProgressBar ratingLoader;

    //Firebase
    private CollectionReference ratingRef;
    private TextView mTotalNo, feedbackMsg;
    private RatingBar mRatingBar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.rating_layout, null);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        ratingRef = database.collection(RATING_REF);

        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString("userId");
        } else {
            Log.d(TAG, "onCreateDialog: Bundle is probably empty");
        }

        mRatingBar = view.findViewById(R.id.ratingBar);
        Button submitRating = view.findViewById(R.id.btnSubmit);
        mTotalNo = view.findViewById(R.id.totalNo);
        feedbackMsg = view.findViewById(R.id.feedback_msg);
        ratingLoader = view.findViewById(R.id.ratingProgress);

        fetchTotalRatings();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);

        mAlertDialog = builder.create();

        submitRating.setOnClickListener(v -> {
            submitRating.setEnabled(false);
            doRate();
        });
        mRatingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            int mRating = (int) rating;
            String message = null;
            switch (mRating){
                case 1:
                    message = "Sorry to hear that";
                    break;
                case 2:
                    message = "We are happy to hear your Suggestions";
                    break;
                case 3:
                    message = "Good enough";
                    break;
                case 4:
                    message = "Great! Thank you";
                    break;
                case 5:
                    message = "Awesome! You are the best";
            }
            feedbackMsg.setText(message);
        });

        return mAlertDialog;
    }

    private void doRate() {
        ratingLoader.setVisibility(View.VISIBLE);
        Map<String, Object> ratingMap = new HashMap<>();
        ratingMap.put("userId", userId);
        ratingMap.put("rating", String.valueOf(mRatingBar.getRating()));
        ratingRef.document(userId).set(ratingMap, SetOptions.merge()).addOnSuccessListener(aVoid -> {
            ratingLoader.setVisibility(View.GONE);
            mRatingListener.ratingMade("You gave a " + mRatingBar.getRating(), mAlertDialog);
        });
    }

    private void fetchTotalRatings() {
        ratingLoader.setVisibility(View.VISIBLE);
        ratingRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
           if (queryDocumentSnapshots != null){
               mTotalNo.setText(String.valueOf(queryDocumentSnapshots.size()));
               ratingLoader.setVisibility(View.GONE);
           } else {
               mTotalNo.setText("0");
               ratingLoader.setVisibility(View.GONE);
           }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mRatingListener = (RatingListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + " must implement Payment");
        }
    }

    public interface RatingListener {
        void ratingMade(String message, AlertDialog dialog);
    }
}
