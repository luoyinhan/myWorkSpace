package com.coship.ott.transport.dto;

import java.io.Serializable;

public class PosterSet implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5479006737678701808L;
	private String displayURL;

	public PosterSet(String displayURL) {
		this.displayURL = displayURL;
	}

	public String getDisplayURL() {
		return displayURL;
	}

	public void setDisplayURL(String displayURL) {
		this.displayURL = displayURL;
	}

}
