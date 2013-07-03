package com.coship.ott.transport.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductInfo implements Parcelable{
	private String productcode;
	private String productname;
	private String price;
	private String lastprice;
	private String deposit;
	private String productdesc;

	public ProductInfo() {
	}

	public ProductInfo(String productCode, String productName, String price,
			String lastprice,String deposit,String productdesc) {
		super();
		this.productcode = productCode;
		this.productname = productName;
		this.price = price;
		this.setLastprice(lastprice);
		this.setDeposit(deposit);
		this.setProductdesc(productdesc);
	}

	public String getProductCode() {
		return productcode;
	}

	public void setProductCode(String productCode) {
		this.productcode = productCode;
	}

	public String getProductName() {
		return productname;
	}

	public void setProductName(String productName) {
		this.productname = productName;
	}

	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	/**
	 * @return the deposit
	 */
	public String getDeposit() {
		return deposit;
	}

	/**
	 * @param deposit the deposit to set
	 */
	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}

	/**
	 * @return the productdesc
	 */
	public String getProductdesc() {
		return productdesc;
	}

	/**
	 * @param productdesc the productdesc to set
	 */
	public void setProductdesc(String productdesc) {
		this.productdesc = productdesc;
	}

	/**
	 * @return the lastprice
	 */
	public String getLastprice() {
		return lastprice;
	}

	/**
	 * @param lastprice the lastprice to set
	 */
	public void setLastprice(String lastprice) {
		this.lastprice = lastprice;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<ProductInfo> CREATOR = new Creator<ProductInfo>() {  
		
		public ProductInfo createFromParcel(Parcel source) {  
   
			ProductInfo p = new ProductInfo();   			
			p.productcode = source.readString();
			p.productname = source.readString();
			p.price = source.readString();
			p.lastprice = source.readString();
			p.deposit = source.readString();
			p.productdesc = source.readString();
			return p;  
		}  
		
		public ProductInfo[] newArray(int size) {  
			return new ProductInfo[size];  
		}  
	};  
	
	public void writeToParcel(Parcel dest, int flags) {  
		
		dest.writeString(productcode);  
		dest.writeString(productname);
		dest.writeString(price); 
		
		dest.writeString(lastprice); 
		dest.writeString(deposit); 
		dest.writeString(productdesc); 
	}  
	

}
