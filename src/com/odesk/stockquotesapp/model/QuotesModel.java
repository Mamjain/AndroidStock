package com.odesk.stockquotesapp.model;

import java.util.List;

import com.odesk.stockquotesapp.vo.QuoteVO;

public class QuotesModel {

	private static QuotesModel instance;

	private List<QuoteVO> quotes;

	private String[] quotsData;

	private QuotesModel() {

	}

	public static synchronized QuotesModel getInstance() {
		if (instance == null)
			instance = new QuotesModel();

		return instance;
	}

	public List<QuoteVO> getQuotes() {
		return quotes;
	}

	public void setQuotes(List<QuoteVO> quotes) {
		this.quotes = quotes;
	}

	public String[] getQuotsData() {
		return quotsData;
	}

	public void setQuotsData(String[] quotsData) {
		this.quotsData = quotsData;
	}

}
