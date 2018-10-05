package com.job.atdonate.ui;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.chip.Chip;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.africastalking.AfricasTalking;
import com.africastalking.models.airtime.AirtimeResponse;
import com.africastalking.models.payment.checkout.CheckoutResponse;
import com.africastalking.models.payment.checkout.MobileCheckoutRequest;
import com.africastalking.models.sms.Recipient;
import com.africastalking.services.AirtimeService;
import com.africastalking.services.PaymentService;
import com.africastalking.services.SmsService;
import com.africastalking.utils.Logger;
import com.job.atdonate.R;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Job on Thursday : 10/4/2018.
 */
public class DonateFragment extends BottomSheetDialogFragment {


    public static final String TAG = "DonateFrag";
    //For the emulator, connecting to local host
    private final String host = "192.168.8.101";
    private final int port = 8080;

    @BindView(R.id.contr_groupname)
    TextView contrGroupname;
    @BindView(R.id.contr_textamount)
    TextView contrTextamount;
    @BindView(R.id.contr_editImg)
    ImageButton contrEditImg;
    @BindView(R.id.contr_amountinput)
    TextInputLayout contrAmountinput;
    @BindView(R.id.contr_contrbtn)
    MaterialButton contrContrbtn;
    Unbinder unbinder;
    @BindView(R.id.dnt_p)
    TextView dntP;
    @BindView(R.id.dnt_e_img)
    ImageButton dntEImg;
    @BindView(R.id.dnt_phone_num)
    TextInputLayout dntPhoneNum;
    @BindView(R.id.mpesachip)
    Chip mpesachip;
    @BindView(R.id.airtimechip)
    Chip airtimechip;


    //starter progress
    private SweetAlertDialog pDialog;


    public DonateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donate, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        connectToServer();
    }


    private boolean validateOnPay() {

        boolean valid = true;

        String am = contrAmountinput.getEditText().getText().toString();
        String pn = dntPhoneNum.getEditText().getText().toString();

        if (pn.isEmpty()) {
            dntPhoneNum.setError("Phone is not valid");

            dntP.setVisibility(View.GONE);
            dntEImg.setVisibility(View.GONE);
            dntPhoneNum.setVisibility(View.VISIBLE);

            valid = false;
        } else {
            dntP.setVisibility(View.VISIBLE);
            dntEImg.setVisibility(View.VISIBLE);
            dntPhoneNum.setVisibility(View.GONE);
            dntPhoneNum.setError(null);
        }

        if (am.isEmpty() || am.equals("0")) {
            contrAmountinput.setError("Amount is not valid");

            contrTextamount.setVisibility(View.GONE);
            contrEditImg.setVisibility(View.GONE);
            contrAmountinput.setVisibility(View.VISIBLE);

            valid = false;
        } else {
            contrTextamount.setVisibility(View.VISIBLE);
            contrEditImg.setVisibility(View.VISIBLE);
            contrAmountinput.setVisibility(View.GONE);
            contrAmountinput.setError(null);
        }

        if (!am.isEmpty()) {
            if (Double.parseDouble(am) < 10) {
                contrAmountinput.setError("Amount must be greater than 10");

                contrTextamount.setVisibility(View.GONE);
                contrEditImg.setVisibility(View.GONE);
                contrAmountinput.setVisibility(View.VISIBLE);

                valid = false;
            } else {
                contrTextamount.setVisibility(View.VISIBLE);
                contrEditImg.setVisibility(View.VISIBLE);
                contrAmountinput.setVisibility(View.GONE);
                contrAmountinput.setError(null);
            }
        }

        return valid;
    }

    public String formatMyMoney(Double money) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        Log.d(TAG, "formatMyMoney: " + formatter.format(money));
        return String.format("KES %,.0f", money);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {

        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }

        super.onDestroy();
    }


    @OnClick({R.id.contr_editImg, R.id.contr_textamount})
    public void onContrEditIcon() {
        contrAmountinput.setVisibility(View.VISIBLE);
        String am = contrTextamount.getText().toString();
        String newstr = am.replaceAll("KES ", "")
                .replaceAll("/-", "")
                .replaceAll(",", "");
        contrAmountinput.getEditText().setText(newstr);
        contrTextamount.setVisibility(View.GONE);
        contrEditImg.setVisibility(View.GONE);

        dntPhoneNum.setVisibility(View.GONE);
        dntP.setVisibility(View.VISIBLE);
        dntEImg.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.main_kjhgf, R.id.textView3, R.id.airtimechip,
            R.id.contr_groupname, R.id.asdsgroup, R.id.mpesachip,
            R.id.textView6contr})
    public void onHideContrInputField() {
        contrAmountinput.setVisibility(View.GONE);
        contrTextamount.setVisibility(View.VISIBLE);
        contrEditImg.setVisibility(View.VISIBLE);

        dntPhoneNum.setVisibility(View.GONE);
        dntP.setVisibility(View.VISIBLE);
        dntEImg.setVisibility(View.VISIBLE);

        String am = contrAmountinput.getEditText().getText().toString();
        String pn = dntPhoneNum.getEditText().getText().toString();

        double temp = 0;
        try {
            temp = Double.parseDouble(am);
        } catch (Exception e) {
            Log.e(TAG, "onHideInputField: ", e);
        }
        contrTextamount.setText(formatMyMoney(temp) + "/-");
        dntP.setText(pn);
    }

    @OnClick(R.id.contr_contrbtn)
    public void onContrContrbtnClicked() {

        if (validateOnPay()) {

            String am = contrAmountinput.getEditText().getText().toString();
            String pn = dntPhoneNum.getEditText().getText().toString();

            //sendMessage(pn, "Donor test app");

            //starter progress
            SweetAlertDialog pDialog;

            if (airtimechip.isChecked()) {

                pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.setCancelable(false);
                pDialog.setContentText("Sending airtime");
                pDialog.show();

                //send airtime
                HashMap<String, String> recipient = new HashMap<>();
                recipient.put("0708440184", "KES " + am);
                sendAirtime(recipient, "Charity", pDialog);
                sendMessage(pn, "Airtime donated to charity");

            }


            if (mpesachip.isChecked()) {

                pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.setCancelable(false);
                pDialog.setContentText("Sending money");
                pDialog.show();
                //send money

                sendPayment("0708440184", am, pDialog);
                sendMessage(pn, "Money donated to charity");

            }

        }
    }

    @OnClick({R.id.dnt_p, R.id.dnt_e_img})
    public void onPhoneEditIcon() {
        dntPhoneNum.setVisibility(View.VISIBLE);
        String ph = dntP.getText().toString();
        String phstr = ph.replaceAll("Phone number ", "");


        dntPhoneNum.getEditText().setText(phstr);

        dntP.setVisibility(View.GONE);
        dntEImg.setVisibility(View.GONE);

        contrAmountinput.setVisibility(View.GONE);
        contrTextamount.setVisibility(View.VISIBLE);
        contrEditImg.setVisibility(View.VISIBLE);
    }


    //DO THIS LOGIC IN VIEWMODEL

    /*
   Implementation of our sendMessage method
    */
    private void sendMessage(final String number, final String message) {

        /*
        get our sms service and use it to send the message
         */
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, String, Void> smsTask = new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                /*
                put it in try catch block
                 */
                try {

                    //Log this
                    Log.e("SMS INFO", "Attempting to send SMS");

                    //get the sms service
                    SmsService smsService = AfricasTalking.getSmsService();

                    //Send the sms, get the response
                    List<Recipient> recipients = smsService.send(message, new String[]{number});

                    /*
                    Log the response
                     */
                    Log.e("SMS RESPONSE", recipients.get(0).messageId + " " + recipients.get(0).status);

                    if (recipients.get(0).status == "Success") {

                    }

                } catch (IOException e) {

                    Log.e("SMS FAILURE", e.toString());
                }
                return null;
            }
        };

        smsTask.execute();
    }

    /*
   Implementation of sendPayment()
    */
    private void sendPayment(final String number, final String amount, final SweetAlertDialog pD) {

        /*
        implement a demo checkout to this app
        */
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, String, Void> paymentTask = new AsyncTask<Void, String, Void>() {

            CheckoutResponse response;

            @Override
            protected Void doInBackground(Void... voids) {

                /*
                same try catch block
                 */
                try {

                    //Log it
                    Log.e("PAYMENT ALERT", "Trying to checkout");

                    //get our payment service
                    PaymentService paymentService = AfricasTalking.getPaymentService();

                    //Create a checkout request
                    MobileCheckoutRequest checkoutRequest = new MobileCheckoutRequest("Donation with AT", "KES " + amount, number);

                    //Initiate the checkout, using the checkoutrequest
                    response = paymentService.checkout(checkoutRequest);

                    //Log the response
                    Log.e("PAYMENT RESPONSE", response.transactionId + " " + response.status + " " + response.description);


                } catch (IOException e) {

                    Log.e("PAYMENT FAILURE", "Failed to check out with exception " + e.toString());
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                pD.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                pD.setCancelable(false);
                pD.setContentText("Successful Donated" + "\n" + "Transaction id" + "\n" + response.transactionId);
                pD.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        dismiss();

                    }
                });
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();

                pD.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                pD.setCancelable(false);
                pD.setContentText("Failed to donate");
                pD.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        dismiss();
                        //navHome(1);
                    }
                });
            }
        };

        //Execute our task
        paymentTask.execute();


    }

    /*
    implementation of sendAirtime()
     */
    private void sendAirtime(final HashMap<String, String> recipient, final String sendToName, final SweetAlertDialog pD) {

        /*
        Run our code in a separate thread from the UI thread, using AsyncTask. Required by Android for all Network calls
         */

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, String, Void> taskSendAirtime = new AsyncTask<Void, String, Void>() {

            AirtimeResponse response;

            @Override
            protected Void doInBackground(Void... voids) {

                /*
                Where we put our code. This is the code that will be executed in the thread
                 */
                try {

                /*
                Log that we are trying to get service
                 */
                    Log.e("AIRTIME NOTICE", "Trying to get airtime service");
                    AirtimeService service = AfricasTalking.getAirtimeService();

                    //Now that we have the service, send the airtime, get the response
                    response = service.send(recipient);

                    //Log our success message
                    Log.e("AIRTIME SUCCESS", "Sent airtime worth " + response.totalAmount + " to " + sendToName);


                } catch (IOException e) {

                    /*
                    Log our failure
                     */
                    Log.e("AIRTIME FAILURE", "Failed to send airtime with exception " + e.toString());
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                pD.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                pD.setCancelable(false);
                pD.setContentText("Successfully donated \n" + response.totalAmount + "\n to" + sendToName);
                pD.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        dismiss();
                        //navHome(1);
                    }
                });
            }

            @Override
            protected void onCancelled(Void aVoid) {
                super.onCancelled(aVoid);

                pD.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                pD.setCancelable(false);
                pD.setContentText("Failed to donate \n to " + sendToName);
                pD.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        dismiss();
                        //navHome(1);
                    }
                });
            }
        };


        //Execute our task
        taskSendAirtime.execute();
    }

    /*
    implementation of connectToServer()
     */
    private void connectToServer() {

        //Initialize te sdk, and connect to our server. Do this in a try catch block
        try {



            /*
            Put a notice in our log that we are attempting to initialize
             */
            Log.e("NOTICE", "Attempting to intialize server");
            AfricasTalking.initialize(host, port, true);

            //Use AT's Logger to get any message
            AfricasTalking.setLogger(new Logger() {
                @Override
                public void log(String message, Object... args) {

                    /*
                    Log this too
                     */
                    Log.e("FROM AT LOGGER", message + " " + args.toString());
                }
            });

            /*
            Final log to tell us if successful
             */
            Log.e("SERVER SUCCESS", "Managed to connect to server");
        } catch (IOException e) {

            /*
            Log our failure to connect
             */
            Log.e("SERVER ERROR", "Failed to connect to server");
        }
    }

    @OnClick(R.id.mpesachip)
    public void onMpesachipClicked() {

        if (airtimechip.isChecked()) {
            airtimechip.setChecked(false);
        }
    }

    @OnClick(R.id.airtimechip)
    public void onAirtimechipClicked() {
        if (mpesachip.isChecked()) {
            mpesachip.setChecked(false);
        }
    }
}