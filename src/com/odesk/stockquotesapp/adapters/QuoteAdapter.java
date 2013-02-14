package com.odesk.stockquotesapp.adapters;

import java.util.List;

import com.odesk.stockquote.R;
import com.odesk.stockquotesapp.vo.QuoteVO;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class QuoteAdapter extends BaseAdapter {

	private Context context;

	private List<QuoteVO> quoteHistory;

	public QuoteAdapter(Context ctx, List<QuoteVO> list) {

		this.context = ctx;
		this.quoteHistory = list;

	}

	@Override
	public int getCount() {
		return this.quoteHistory.size();
	}

	@Override
	public Object getItem(int position) {
		return this.quoteHistory.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {

			LayoutInflater inflator = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(
					com.odesk.stockquote.R.layout.quote_item, null);

		}

		QuoteVO quoteVO = this.quoteHistory.get(position);

		if (quoteVO != null) {

			TextView stockName = (TextView) convertView
					.findViewById(R.id.stock_name);
			TextView openStock = (TextView) convertView
					.findViewById(R.id.open_stock);
			TextView closeStock = (TextView) convertView
					.findViewById(R.id.close_stock);

			stockName.setText(quoteVO.getStockName());
			openStock.setText(quoteVO.getOpen());
			closeStock.setText(quoteVO.getClose());

		}

		return convertView;

	}

}
