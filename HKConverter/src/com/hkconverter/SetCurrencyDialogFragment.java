package com.hkconverter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SetCurrencyDialogFragment extends DialogFragment {

	public interface NoticeDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	NoticeDialogListener mListener;

	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (NoticeDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement NoticeDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(getActivity()
						.getApplicationContext());

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View promptsView = inflater.inflate(R.layout.pop_up, null);
		final EditText userIn = (EditText) promptsView.findViewById(R.id.usrIn);
		final EditText userInQty = (EditText) promptsView
				.findViewById(R.id.usrInQty);
		final EditText userOutQty = (EditText) promptsView
				.findViewById(R.id.usrOutQty);
		final EditText userOut = (EditText) promptsView
				.findViewById(R.id.usrOut);
		userIn.setText(settings.getString("userIn", ""));
		userInQty.setText(settings.getString("userInQty", ""));
		userOutQty.setText(settings.getString("userOutQty", ""));
		userOut.setText(settings.getString("userOut", ""));
		builder.setTitle("Set Currency")
				.setView(promptsView)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (userInQty.getText().toString().equals("")
										|| (userInQty.getText().toString()
												.indexOf(".") != userInQty
												.getText().toString()
												.lastIndexOf("."))
										|| userInQty.getText().toString()
												.indexOf(".") == 0)
									Toast.makeText(getActivity(),
											"Invalid Input Qty", Toast.LENGTH_SHORT)
											.show();
								else if (userOutQty.getText().toString()
										.equals("")
										|| (userOutQty.getText().toString()
												.indexOf(".") != userOutQty
												.getText().toString()
												.lastIndexOf("."))
										|| userOutQty.getText().toString()
												.indexOf(".") == 0)
									Toast.makeText(getActivity(),
											"Invalid Output Qty", Toast.LENGTH_SHORT)
											.show();
								else {
									SharedPreferences.Editor editor = settings
											.edit();
									editor.putString("userIn", userIn.getText()
											.toString());
									editor.putString("userInQty", userInQty
											.getText().toString());
									editor.putString("userOutQty", userOutQty
											.getText().toString());
									editor.putString("userOut", userOut
											.getText().toString());
									editor.commit();
									mListener
											.onDialogPositiveClick(SetCurrencyDialogFragment.this);
								}
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
