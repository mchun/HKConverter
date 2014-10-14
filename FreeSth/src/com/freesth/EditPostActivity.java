package com.freesth;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/* Activity to view individual post
 */

public class EditPostActivity extends ActionBarActivity {

	Post parceledPost;
	EditText postDesc,price;
	ImageButton Pic1, Pic2, Pic3;
	String fotolk1, fotolk2, fotolk3;
	byte[] foto1, foto2, foto3;
	int scaledWidth = 480;
	String TAG = "EditPostActivity";
	String postType = "free";
	Spinner tagChooser, statusChooser, traderChooser;
	RadioButton wantedRadio, freeRadio, saleRadio;
	//	Button closeBtn;
	ProgressBar pb1, pb2, pb3;
	TextView tvTrader;
	int screenWidth;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display display = getWindowManager().getDefaultDisplay();
		setContentView(R.layout.activity_write_post);
		//		closeBtn = (Button) findViewById(R.id.close_button);
		postDesc = (EditText) findViewById(R.id.et_itemDesc);
		Pic1 = (ImageButton) findViewById(R.id.btnPick01);
		Pic2 = (ImageButton) findViewById(R.id.btnPick02);
		Pic3 = (ImageButton) findViewById(R.id.btnPick03);
		pb1 = (ProgressBar) findViewById(R.id.progressBar1);
		pb2 = (ProgressBar) findViewById(R.id.progressBar2);
		pb3 = (ProgressBar) findViewById(R.id.progressBar3);
		price = (EditText)findViewById(R.id.et_price);
		tvTrader = (TextView) findViewById(R.id.postTradeWith);
		tagChooser = (Spinner) findViewById(R.id.spnTag);
		statusChooser = (Spinner) findViewById(R.id.spnStatus);
		traderChooser = (Spinner) findViewById(R.id.spnTrader);
		wantedRadio = (RadioButton) findViewById(R.id.radio_wanted);
		saleRadio = (RadioButton) findViewById(R.id.radio_sale);
		freeRadio = (RadioButton) findViewById(R.id.radio_free);
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
		pb1.getLayoutParams().width = screenWidth / 3;
		pb1.getLayoutParams().height = screenWidth / 3;
		pb1.requestLayout();
		pb2.getLayoutParams().width = screenWidth / 3;
		pb2.getLayoutParams().height = screenWidth / 3;
		pb2.requestLayout();
		pb3.getLayoutParams().width = screenWidth / 3;
		pb3.getLayoutParams().height = screenWidth / 3;
		pb3.requestLayout();
		parceledPost = this.getParceledPostFromIntent();
		loadPostAgain(parceledPost);
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

	private Post getParceledPostFromIntent() {
		Intent i = this.getIntent();
		if (i == null) {
			throw new RuntimeException("Sorry no intent found");
		}
		// intent is available
		String postId = i.getStringExtra(Post.PARCELABLE_POST_ID);
		if (postId == null) {
			throw new RuntimeException("post id not found");
		}

		ParseObjectWrapper pow = (ParseObjectWrapper) i
				.getParcelableExtra(Post.t_tablename);

		if (pow == null) {
			throw new RuntimeException("ParceledPost not found");
		}
		Post parceledPost = new Post(pow);
		if (i.getStringExtra("Pic1") != null)
			fotolk1 = i.getStringExtra("Pic1");
		if (i.getStringExtra("Pic2") != null)
			fotolk2 = i.getStringExtra("Pic2");
		if (i.getStringExtra("Pic3") != null)
			fotolk3 = i.getStringExtra("Pic3");
		return parceledPost;
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

	private void loadPostAgain(Post post) {
		Log.d("loadPostAgain", "start");
		if (fotolk1 != null) {
			pb1.setVisibility(View.VISIBLE);
			Picasso.with(getBaseContext()).load(fotolk1).fit().centerInside()
					.into(Pic1, new Callback() {

						@Override
						public void onSuccess() {
							pb1.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onError() {
							pb1.setVisibility(View.INVISIBLE);
						}
					});
			Pic2.setVisibility(View.VISIBLE);
		}
		if (fotolk2 != null) {
			pb2.setVisibility(View.VISIBLE);
			Picasso.with(getBaseContext()).load(fotolk2).fit().centerInside()
					.into(Pic2, new Callback() {

						@Override
						public void onSuccess() {
							pb2.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onError() {
							pb2.setVisibility(View.INVISIBLE);
						}
					});
			Pic3.setVisibility(View.VISIBLE);
		}
		if (fotolk3 != null) {
			pb3.setVisibility(View.VISIBLE);
			Picasso.with(getBaseContext()).load(fotolk3).fit().centerInside()
					.into(Pic3, new Callback() {

						@Override
						public void onSuccess() {
							pb3.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onError() {
							pb3.setVisibility(View.INVISIBLE);
						}
					});
		}
		if (post.getPostType().equals("free")) {
			freeRadio.setChecked(true);
			postType = "free";
		} else if (post.getPostType().equals("sale")) {
			saleRadio.setChecked(true);
			postType = "sale";
		} else {
			wantedRadio.setChecked(true);
			postType = "wanted";
		}

		List<String> spinnerArray = ParseConfig.getCurrentConfig().getList(
				"Category");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tagChooser.setAdapter(adapter);
		tagChooser.setSelection(adapter.getPosition(post.getPostTag()));
		tagChooser.setVisibility(View.VISIBLE);
		ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter
				.createFromResource(this, R.array.status_array,
						android.R.layout.simple_spinner_item);
		TextView postStatus = (TextView) findViewById(R.id.postStatus);
		postStatus.setVisibility(View.VISIBLE);
		statusAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		statusChooser.setAdapter(statusAdapter);
		statusChooser.setSelection(statusAdapter.getPosition(post
				.getPostStatus()));
		statusChooser.setVisibility(View.VISIBLE);
		statusChooser.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				if (parent.getItemAtPosition(pos).toString()
						.equals("Completed")) {
					ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
							Follow.t_tablename);
					query.whereEqualTo(Follow.f_post, parceledPost.po);
					query.orderByAscending(Follow.f_createdAt);
					query.setCachePolicy(CachePolicy.CACHE_ELSE_NETWORK);
					query.setMaxCacheAge(30000L);
					query.include(Follow.f_createdBy);
					//					pd.setCancelable(true);
					query.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> objects,
								ParseException e) {
							if (e == null) {
								// The query was successful.
								ArrayList<Follow> followArrayList = new ArrayList<Follow>();
								Follow otherUser = new Follow(
										(ParseObject) null);
								followArrayList.add(otherUser);
								for (ParseObject po : objects) {
									Follow puw = new Follow(po);
									followArrayList.add(puw);
								}
								ArrayAdapter<Follow> traderAdapter = new ArrayAdapter<Follow>(
										EditPostActivity.this,
										android.R.layout.simple_spinner_item,
										followArrayList);
								traderAdapter
										.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								traderChooser.setAdapter(traderAdapter);
								traderChooser.setVisibility(View.VISIBLE);
								tvTrader.setVisibility(View.VISIBLE);
							} else {
								// Something went wrong.
								Failed(e);
							}
						}
					});
				} else {
					traderChooser.setVisibility(View.GONE);
					tvTrader.setVisibility(View.GONE);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});

		postDesc.setText(post.getPostDesc());
	}

	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();
		Log.d("EditPostActivity", "inside radiobuttonclicked");
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

	//	private void cfmDeletePost() {
	//		Log.e(TAG, "enter deletePost");
	//		final ProgressDialog pd = ProgressDialog.show(this, "Deleting",
	//				"Please wait...");
	//		parceledPost.getParseObject().deleteInBackground(new DeleteCallback() {
	//			@Override
	//			public void done(ParseException e) {
	//				pd.cancel();
	//				if (e == null) {
	//					// deleteSuccessful(parceledPost);
	//					finish();
	//				} else {
	//					Failed(e);
	//				}
	//			}
	//		});
	//	}

	private void Failed(ParseException x) {
		new AlertDialog.Builder(EditPostActivity.this)
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

	//	public void closePost() {
	//		new AlertDialog.Builder(EditPostActivity.this)
	//				.setTitle("Close current post?")
	//				.setMessage("Your post will not be seen by the others.")
	//				.setPositiveButton(android.R.string.yes,
	//						new DialogInterface.OnClickListener() {
	//							public void onClick(DialogInterface dialog,
	//									int which) {
	//								// continue with delete
	//								cfmClosePost();
	//							}
	//						})
	//				.setNegativeButton(android.R.string.no,
	//						new DialogInterface.OnClickListener() {
	//							public void onClick(DialogInterface dialog,
	//									int which) {
	//								// do nothing
	//							}
	//						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	//	}

	//	private void cfmClosePost() {
	//		pd = ProgressDialog
	//				.show(this, "Updating Post", "We will be right back");
	//		ParseObject p = new ParseObject(Post.t_tablename);
	//		p.setObjectId(parceledPost.po.getObjectId());
	//		p.put(Post.f_postStatus, "Closed");
	//		p.saveInBackground(new SaveCallback() {
	//			@Override
	//			public void done(ParseException e) {
	//				pd.cancel();
	//				if (e == null) {
	//					// no exception
	//					// setResult(RESULT_OK);
	//					finish();
	//				} else {
	//					Failed(e);
	//				}
	//			}
	//		});
	//
	//	}

	//	private void cfmRePost() {
	//		pd = ProgressDialog
	//				.show(this, "Updating Post", "We will be right back");
	//		ParseObject p = new ParseObject(Post.t_tablename);
	//		p.setObjectId(parceledPost.po.getObjectId());
	//		p.put(Post.f_postStatus, "Open");
	//		p.saveInBackground(new SaveCallback() {
	//			@Override
	//			public void done(ParseException e) {
	//				pd.cancel();
	//				if (e == null) {
	//					// no exception
	//					// setResult(RESULT_OK);
	//					finish();
	//				} else {
	//					Failed(e);
	//				}
	//			}
	//		});
	//
	//	}

	public void savePost() {
		Log.d(TAG, "enter savePost");

		final ParseObject p = new ParseObject(Post.t_tablename);
		p.setObjectId(parceledPost.po.getObjectId());
		p.put(Post.f_postDesc, postDesc.getText().toString());
		p.put(Post.f_postType, postType);
		p.put(Post.f_postTag, tagChooser.getSelectedItem().toString());
		p.put(Post.f_postStatus, statusChooser.getSelectedItem().toString());
		if (foto1 != null) {
			ParseFile photoFile1 = new ParseFile(
					parceledPost.getCreatedByUser().username + "1.jpg", foto1);
			p.put("photo1", photoFile1);
		}
		if (foto2 != null) {
			ParseFile photoFile2 = new ParseFile(
					parceledPost.getCreatedByUser().username + "2.jpg", foto2);
			p.put("photo2", photoFile2);
		}
		if (foto3 != null) {
			ParseFile photoFile3 = new ParseFile(
					parceledPost.getCreatedByUser().username + "3.jpg", foto3);
			p.put("photo3", photoFile3);
		}
		if (parceledPost.getPostStatus().equals("Open")
				&& statusChooser.getSelectedItem().toString().equals("Closed")) {
			new AlertDialog.Builder(EditPostActivity.this)
					.setTitle("Close this post?")
					.setMessage("Others will not see this post.")
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {// do nothing			
								public void onClick(DialogInterface dialog,
										int which) {
									p.saveInBackground(new SaveCallback() {
										@Override
										public void done(ParseException e) {
											if (e == null) {
												// no exception
												// setResult(RESULT_OK);
												pushNewUpdate();
												finish();
											} else {
												Failed(e);
											}
										}
									});
								}
							})
					.setNegativeButton(android.R.string.no,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
								}
							}).setIcon(android.R.drawable.ic_dialog_alert)
					.show();
		} else {
			if (statusChooser.getSelectedItem().toString().equals("Completed")) {
				if (!traderChooser.getSelectedItem().toString()
						.equals("Other user")) {
					p.put(Post.f_trader, ((Follow) traderChooser
							.getSelectedItem()).getCreatedBy());
					Log.d("EditPost", "put trader");
				} else {
					//					p.put(Post.f_trader, (ParseUser)null); //TODO how to remove
					p.remove(Post.f_trader);
					Log.d("EditPost", "trader field should be deleted.");
				}

			}
			p.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					if (e == null) {
						// no exception
						// setResult(RESULT_OK);
						pushNewUpdate();
						finish();
					} else {
						Failed(e);
					}
				}
			});
		}
	}

	private void pushNewUpdate() {
		// send push message with parceledpost
		JSONObject data = getJSONDataMessage("New Update");
		ParsePush push = new ParsePush();
		push.setChannel("post" + parceledPost.po.getObjectId());
		push.setData(data);
		push.sendInBackground();
		Log.d("EditPost", "send push for update");
	}

	private JSONObject getJSONDataMessage(String type) {
		try {
			JSONObject data = new JSONObject();
			data.put(Post.PARCELABLE_POST_ID, parceledPost.po.getObjectId());
			data.put("action", PushBroadcastReceiver.ACTION);
			data.put("author", ParseUser.getCurrentUser().getString("name"));
			data.put("oriAuthor", parceledPost.getCreatedByUser().username);
			data.put("type", type);
			return data;
		} catch (JSONException x) {
			throw new RuntimeException("Something wrong with JSON", x);
		}
	}
}
