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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bruce.raeasy.R;
import com.bruce.raeasy.models.Payment;
import com.bruce.raeasy.utils.CodeGenerator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.bruce.raeasy.utils.Constants.PAYMENT_REF;

public class PaymentDialog extends AppCompatDialogFragment {

    private static final String TAG = "PaymentDialog";
    private AlertDialog mAlertDialog;
    private String type, phone, amount, code, userId, date, itemId;
    private RelativeLayout mpesa, airtel;
    private PaymentListener mPaymentListener;
    private TextView paymentCode;
    private ProgressBar paymentLoader;
    private Button btnMpesa, btnAirtel, btnOk;
    private EditText safPhone, airtelPhone;

    //Firebase
    private CollectionReference paymentRef;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.payment_dialog, null);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        paymentRef = database.collection(PAYMENT_REF);
        code = CodeGenerator.randomAlphaNumeric(10);

        mpesa = view.findViewById(R.id.mpesa_layout);
        airtel = view.findViewById(R.id.airtel_money_layout);
        paymentCode = view.findViewById(R.id.paymentCode);
        paymentLoader = view.findViewById(R.id.paymentLoader);
        btnMpesa = view.findViewById(R.id.makePayment);
        btnAirtel = view.findViewById(R.id.btnAirtel);
        safPhone = view.findViewById(R.id.edPhone);
        airtelPhone = view.findViewById(R.id.edAirtelPhone);
        btnOk = view.findViewById(R.id.btnOk);

        Bundle bundle = getArguments();
        if (bundle != null) {
            type = bundle.getString("type");
            phone = bundle.getString("phone");
            amount = bundle.getString("amount");
            date = bundle.getString("date");
            userId = bundle.getString("userId");
            itemId = bundle.getString("itemId");
            displayLayout();
        } else {
            Log.d(TAG, "onCreateDialog: Bundle is probably empty");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);

        mAlertDialog = builder.create();

        btnMpesa.setOnClickListener(v -> makePayment("mpesa", btnMpesa));

        btnAirtel.setOnClickListener(v -> makePayment("airtel", btnAirtel));

        btnOk.setOnClickListener(v -> {
            mAlertDialog.dismiss();
            mPaymentListener.paymentMade(code, mAlertDialog);
        });

        return mAlertDialog;
    }

    private void displayLayout() {
        if (type.equals("mpesa")) {
            airtel.setVisibility(View.GONE);
            mpesa.setVisibility(View.VISIBLE);
            safPhone.setText(phone);
        } else {
            mpesa.setVisibility(View.GONE);
            airtel.setVisibility(View.VISIBLE);
            airtelPhone.setText(phone);
        }
    }

    private void makePayment(String type, Button btn) {
        paymentLoader.setVisibility(View.VISIBLE);
        btnOk.setVisibility(View.VISIBLE);
        btnOk.setEnabled(false);
        btn.setEnabled(false);
        Payment payment = new Payment(code, itemId, userId, amount, type, date, phone);
        paymentRef.document(code).set(payment).addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               btnOk.setEnabled(true);
               paymentCode.setText(code);
               paymentLoader.setVisibility(View.GONE);
           } else {
               mPaymentListener.paymentMade("", mAlertDialog);
               paymentCode.setText("");
               paymentLoader.setVisibility(View.GONE);
           }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mPaymentListener = (PaymentListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + " must implement Payment");
        }
    }

    public interface PaymentListener {
        void paymentMade(String code, AlertDialog dialog);
    }
}
