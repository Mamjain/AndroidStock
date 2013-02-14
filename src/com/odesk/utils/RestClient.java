package com.odesk.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.odesk.stockquotesapp.vo.QuoteVO;

import android.util.Log;

public class RestClient {

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public List<QuoteVO> getQuotes(String[] data) {

		String requestedQuotes = "";

		Date endDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.add(Calendar.DAY_OF_YEAR, -1);

		// Use the date formatter to produce a formatted date string
		Date startDate = calendar.getTime();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String startDateStr = dateFormat.format(startDate);
		String endDateStr = dateFormat.format(endDate);

		for (int i = 0; i < data.length; i++) {
			if (data.length != i + 1) {
				requestedQuotes += "\"" + data[i] + "\",";
			} else {
				requestedQuotes += "\"" + data[i] + "\"";
			}
		}

		HttpClient httpclient = new DefaultHttpClient();
		String yqlQuery = "select * from yahoo.finance.historicaldata where symbol in ("
				+ requestedQuotes
				+ ") and startDate = \""
				+ startDateStr
				+ "\" and endDate = \"" + endDateStr + "\"";
		String request = "http://query.yahooapis.com/v1/public/yql?q="
				+ URLEncoder.encode(yqlQuery)
				+ "&format=json&diagnostics=true&env="
				+ URLEncoder.encode("store://datatables.org/alltableswithkeys")
				+ "";
		HttpGet httpget = new HttpGet(request);

		// Execute the request
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);

			Log.i("Praeda", response.getStatusLine().toString());

			HttpEntity entity = response.getEntity();

			if (entity != null) {

				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);

				JSONObject json = new JSONObject(result);
				JSONObject queryData = json.getJSONObject("query");

				List<QuoteVO> quotesList = new ArrayList<QuoteVO>();
				Log.i("Praeda", queryData.toString());
				JSONObject results = queryData.getJSONObject("results");
				JSONArray valArray = results.getJSONArray("quote");
				// Log.i("Praeda", valArray.toString());
				for (int i = 0; i < valArray.length(); i++) {
					JSONObject tempObject = (JSONObject) valArray.get(i);
					String open = tempObject.getString("Open");
					String close = tempObject.getString("Close");
					String low = tempObject.getString("Low");
					String volume = tempObject.getString("Volume");
					QuoteVO quote = new QuoteVO();
					if (data != null) {
						try {
							quote.setStockName(data[i]);
						} catch (Exception e) {
							break;
						}
					}
					quote.setOpen(open);
					quote.setClose(close);
					quote.setLow(low);
					quote.setVolume(volume);
					quotesList.add(quote);

				}

				instream.close();

				return quotesList;
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

}
