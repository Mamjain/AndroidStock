package com.odesk.stockquote;

import java.util.List;

import com.odesk.stockquote.R;
import com.odesk.stockquotesapp.adapters.QuoteAdapter;
import com.odesk.stockquotesapp.model.QuotesModel;
import com.odesk.stockquotesapp.vo.QuoteVO;
import com.odesk.utils.RestClient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

public class MainActivity extends Activity implements Runnable {

	public Handler mainHandler;
	public Handler quotesHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.mainHandler = new Handler() {

			public void handleMessage(Message msg) {

				String messageStr = (String) msg.obj;

				if (messageStr.equals("success")) {

					ListView list = (ListView) findViewById(R.id.quotesList);
					QuoteAdapter adapter = new QuoteAdapter(MainActivity.this,
							QuotesModel.getInstance().getQuotes());

					list.setAdapter(adapter);

				} else {
					showDialog(1);

				}

			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public void run() {
		Looper.prepare();

		this.quotesHandler = new Handler() {

			public void handleMessage(Message msg) {

				RestClient client = new RestClient();
				List<QuoteVO> quotesList = client.getQuotes(QuotesModel
						.getInstance().getQuotsData());
				removeDialog(0);
				Message toMain = mainHandler.obtainMessage();
				if (quotesList != null) {
					toMain.obj = "success";
				} else {
					toMain.obj = "fail";
				}

				QuotesModel.getInstance().setQuotes(quotesList);
				mainHandler.sendMessage(toMain);
				Looper.myLooper().quit();

			}

		};

		Looper.loop();

	}

	public void searchQuotes(View view) {
		showDialog(0);
		EditText quotesText = (EditText) findViewById(R.id.editText1);
		QuotesModel.getInstance().setQuotsData(
				quotesText.getText().toString().split(","));
		Thread qutesSearchThread = new Thread(this);
		qutesSearchThread.start();
		this.sendAuthenticationMessage();
	}

	private void sendAuthenticationMessage() {
		while (true) {
			try {
				Thread.sleep(100);
				if (this.quotesHandler != null) {
					this.quotesHandler.sendEmptyMessage(0);
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		Builder builder = null;

		switch (id) {

		case 0: {
			dialog = new ProgressDialog(this);
			((ProgressDialog) dialog).setTitle("Searching Quotes");
			((ProgressDialog) dialog)
					.setMessage("Please wait while searching for Quotes...");
			((ProgressDialog) dialog).setIndeterminate(true);
			break;
		}
		case 1: {

			builder = new AlertDialog.Builder(MainActivity.this);
			builder.setCancelable(true);
			builder.setMessage("No Quotes Found");
			builder.setPositiveButton(R.string.alert_dialog_ok,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// showDialog(0);

						}
					});

			dialog = builder.create();

		}

		}

		return dialog;
	}

}
