package com.coship.ott.utils;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.coship.ott.constant.Constant;
import com.coship.ott.transport.dto.Favourite;

public class DbHelper {
	private static String TAG = "DbHelper";
	private Context mContext;
	private SQLiteDatabase mDb;
	private final static String FAVOURITE_TABLE = "mulcreen_favourite";
	private final static String USERCODE = "userCode";
	private final static String RESOURCECODE = "resourceCode";
	private boolean result = true;

	public DbHelper(Context mContext) {
		super();
		this.mContext = mContext;
		initDb();
	}

	private void initDb() {
		// 创建数据库
		mDb = SQLiteDatabase.openOrCreateDatabase(mContext.getFilesDir()
				.toString() + "/MulScreen.db3", null);
		// 创建用户收藏表
		mDb.execSQL("create table if not exists  " + FAVOURITE_TABLE
				+ "( _id integer primary key autoincrement," + USERCODE
				+ " varchar(20)," + RESOURCECODE + " varchar(20))");
	}

	// 插入单个数据
	public boolean insertData(String mUserCode, String mResourceCode) {
		try {
			result = true;
			mDb.execSQL(
					" insert into " + FAVOURITE_TABLE + " values(null,?,?)",
					new String[] { mUserCode, mResourceCode });
		} catch (SQLException e) {
			LogUtils.trace(Log.ERROR, TAG, e.toString());
			result = false;
		}
		return result;
	}

	// 插入多个数据
	public boolean insertAllData(ArrayList<Favourite> mAllData) {
		result = true;
		if (mAllData.size() == 0) {
			return false;
		}
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("");
			for (Favourite far : mAllData) {
				buf.append("  select  null,'" + far.getUserCode() + "',"
						+ far.getResourceCode() + " union ");
			}
			// 删除最后的 “union”
			String allData = "";
			if (!TextUtils.isEmpty(buf.toString())) {
				allData = buf.toString().substring(0,
						buf.toString().length() - 6);
			}
			mDb.execSQL(" insert into " + FAVOURITE_TABLE + " " + allData);
			// 同时将db转存到sd卡上
			// copyFile2(mContext.getFilesDir().toString() + "/MulScreen.db3",
			// Constant.ROOT_ADDR + "log/MulScreen.db3");
		} catch (Exception e) {
			LogUtils.trace(Log.ERROR, TAG, e.toString());
			result = false;
		}
		return result;
	}

	// private void copyFile2(String source, String dest) {
	// try {
	// File in = new File(source);
	// File out = new File(dest);
	// FileInputStream inFile = new FileInputStream(in);
	// FileOutputStream outFile = new FileOutputStream(out);
	// byte[] buffer = new byte[1024];
	// int i = 0;
	// while ((i = inFile.read(buffer)) != -1) {
	// outFile.write(buffer, 0, i);
	// }
	// inFile.close();
	// outFile.close();
	// } catch (Exception e) {
	// Log.e("copyfiledb", "复制单个文件操作出错:" + e.toString());
	// }
	// }

	// 查询单个数据是否存在
	public boolean queryData(String mUserCode, String mResourceCode) {
		Cursor cursor = null;
		result = true;
		String[] args = { mUserCode, mResourceCode };

		try {
			cursor = mDb.rawQuery(" SELECT * FROM  " + FAVOURITE_TABLE
					+ " where " + USERCODE + " = ? and " + RESOURCECODE
					+ " = ? ", args);
		} catch (Exception e) {
			Log.e("sql", e.toString());
		}
		if (cursor.getCount() < 1) {
			result = false;
		}
		cursor.close();
		return result;
	}

	// 查询单个用户的所有的收藏记录
	public boolean queryAllData(String mUserCode) {
		result = true;
		String[] args = { mUserCode };
		Cursor cursor = mDb.rawQuery(" select * from  " + FAVOURITE_TABLE
				+ " where " + USERCODE + " = ?", args);
		if (cursor.getCount() < 1) {
			result = false;
		}
		cursor.close();

		return result;

	}

	// 清空数据表
	public boolean deleteAllData() {
		result = true;
		try {
			mDb.execSQL(" delete from " + FAVOURITE_TABLE);
		} catch (SQLException e) {
			LogUtils.trace(Log.ERROR, TAG, e.toString());
			result = false;
		}
		return result;
	}

	// 删除单个数据
	public boolean deleteData(String mUserCode, String mResourceCode) {
		result = true;
		String[] args = { mUserCode, mResourceCode };
		try {
			mDb.execSQL(" delete from " + FAVOURITE_TABLE + " where "
					+ USERCODE + " = ? and " + RESOURCECODE + " = ?", args);
		} catch (SQLException e) {
			LogUtils.trace(Log.ERROR, TAG, e.toString());
			result = false;
		}
		return result;
	}

	// 删除多个数据
	public boolean deleteDatas(ArrayList<Integer> deleteids) {
		result = true;
		String ids = "";
		for (int i : deleteids) {
			ids += i + ",";
		}
		ids = ids.substring(0, ids.length() - 1);
		try {
			mDb.execSQL(" delete from " + FAVOURITE_TABLE + " where _id in ("
					+ ids + ")");
		} catch (SQLException e) {
			LogUtils.trace(Log.ERROR, TAG, e.toString());
			result = false;
		}
		return result;
	}

	public void closeConn() {
		mDb.close();
	}
}
