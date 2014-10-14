package com.freesth;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.PushService;
import com.parse.SaveCallback;

public class WritePostActivity extends ActionBarActivity {
	private String TAG = "WritePostActivity";
	EditText itemDesc, price;
	ImageButton Pic1, Pic2, Pic3;
	//	Button closePost, deletePost;
	Spinner tagChooser;
	int SELECT_PICTURE;
	int scaledWidth = 480;
	int screenWidth;
	byte[] foto1, foto2, foto3;
	String postType = "free";

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display display = getWindowManager().getDefaultDisplay();
		setContentView(R.layout.activity_write_post);
		itemDesc = (EditText) findViewById(R.id.et_itemDesc);
		Pic1 = (ImageButton) findViewById(R.id.btnPick01);
		Pic2 = (ImageButton) findViewById(R.id.btnPick02);
		Pic3 = (ImageButton) findViewById(R.id.btnPick03);
		price = (EditText) findViewById(R.id.et_price);
		tagChooser = (Spinner) findViewById(R.id.spnTag);
		List<String> spinnerArray = ParseConfig.getCurrentConfig().getList(
				"Category");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tagChooser.setAdapter(adapter);
		if (android.os.Build.VERSION.SDK_INT >= 13) {
			Point size = new Point();
			display.getSize(size);
			screenWidth = size.x;
		} else {
			screenWidth = display.getWidth(); // deprecated
		}
		Pic1.getLayoutParams().width = screenWidth / 3;
		Pic1.getLayoutParams().height = screenWidth / 3;
		Pic1.requestLayout();
		Pic2.getLayoutParams().width = screenWidth / 3;
		Pic2.getLayoutParams().height = screenWidth / 3;
		Pic2.requestLayout();
		Pic3.getLayoutParams().width = screenWidth / 3;
		Pic3.getLayoutParams().height = screenWidth / 3;
		Pic3.requestLayout();

		//		closePost = (Button) findViewById(R.id.close_button);
		//		closePost.setVisibility(View.GONE);
		//
		//		deletePost = (Button) findViewById(R.id.delete_button);
		//		deletePost.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_post, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_save)
			savePost();
		return super.onOptionsItemSelected(item);
	}

	public void addNewPhoto(View view) {
		int RequestCode = 0; // not appropiate to use view.getID as requestCode
								// as it may overflow integer range
		switch (view.getId()) {
		case R.id.btnPick01:
			RequestCode = 1;
			break;
		case R.id.btnPick02:
			RequestCode = 2;
			break;
		case R.id.btnPick03:
			RequestCode = 3;
			break;
		}
		Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
		pickIntent.setType("image/*");
		startActivityForResult(pickIntent, RequestCode);
	}

	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();
		Log.d("WritePostActivity", "inside radiobuttonclicked");
		// Check which radio button was clicked
		switch (view.getId()) {
		case R.id.radio_free:
			if (checked) {
				postType = "free";
				price.setVisibility(View.GONE);
			}
			break;
		case R.id.radio_sale:
			if (checked) {
				postType = "sale";
				price.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.radio_wanted:
			if (checked) {
				postType = "wanted";
				price.setVisibility(View.GONE);
			}
			break;
		}
	}

	public void savePost() {
		Log.d(TAG, "enter savePost");
		String sItemDesc = itemDesc.getText().toString();
		String sItemTag = tagChooser.getSelectedItem().toString();
		final Post p = new Post(sItemDesc, postType, sItemTag); // code abit7
		if (foto1 != null) {
			ParseFile photoFile1 = new ParseFile(p.getAuthor() + "1.jpg", foto1);
			p.setPhoto(photoFile1, 1);
		}
		if (foto2 != null) {
			ParseFile photoFile2 = new ParseFile(p.getAuthor() + "2.jpg", foto2);
			p.setPhoto(photoFile2, 2);
		}
		if (foto3 != null) {
			ParseFile photoFile3 = new ParseFile(p.getAuthor() + "3.jpg", foto3);
			p.setPhoto(photoFile3, 3);
		}

		p.po.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					// no exception
					PushService.subscribe(getApplicationContext(), "post"
							+ p.po.getObjectId(), ViewPostActivity.class); //default response activity for channel push
					setResult(RESULT_OK);
					finish();
				} else {
					Failed(e);
				}
			}
		});
	}

	private void Failed(ParseException x) {
		new AlertDialog.Builder(WritePostActivity.this)
				.setTitle("Error")
				.setMessage(x.getMessage())
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			try {
				Uri uri = data.getData();
				InputStream stream = getContentResolver().openInputStream(uri);
				Bitmap mBitmap = BitmapFactory.decodeStream(stream);
				// reduce the resolution
				Bitmap scaleImage = Bitmap.createScaledBitmap(mBitmap,
						scaledWidth, scaledWidth * mBitmap.getHeight()
								/ mBitmap.getWidth(), false);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				// save as JPG
				scaleImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);

				switch (requestCode) {
				case 1:
					Pic1.setImageBitmap(scaleImage);
					foto1 = bos.toByteArray();
					Pic2.setVisibility(ImageButton.VISIBLE);
					break;
				case 2:
					Pic2.setImageBitmap(scaleImage);
					foto2 = bos.toByteArray();
					Pic3.setVisibility(ImageButton.VISIBLE);
					break;
				case 3:
					Pic3.setImageBitmap(scaleImage);
					foto3 = bos.toByteArray();
					break;
				default:
					break;
				}

			} catch (Exception e) {
				Log.e(TAG, "Decode Images", e);
			}
		}
	}
}
