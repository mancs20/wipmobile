package com.products.qc;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Picture;

import com.products.qc.IcfactorDataReaderContract.IcfactorDataEntry;
import com.products.qc.IcfactorReaderContract.IcfactorEntry;
import com.products.qc.PalletReaderContract.PalletEntry;
import com.products.qc.PictureReaderContract.PictureEntry;
import com.products.qc.ProductReaderContract.ProductEntry;
import com.products.qc.SamplingReaderContract.SamplingEntry;

public class QueryRepository {
	
	public static int getPalletIdByCode(Activity activity, int code)
	{
		PalletReaderDbHelper mDbPalletHelper = new PalletReaderDbHelper(activity);
		SQLiteDatabase palletDb = mDbPalletHelper.getReadableDatabase();
		
		String[] projection = {
				PalletEntry.COLUMN_NAME_ENTRY_ID
		};
		
		String where = PalletEntry.COLUMN_NAME_CODE + "=" + code;
		
		Cursor c = palletDb.query(
				PalletEntry.TABLE_NAME, projection, where,
				null, null, null, null);
		
		c.moveToFirst();
		int id = c.getInt(c.getColumnIndexOrThrow(PalletEntry.COLUMN_NAME_ENTRY_ID));
		palletDb.close();
		mDbPalletHelper.close();
		return id;
	}

	public static boolean updateSamplingByTempGrowerPlus(Activity activity, int samplingId, Float temp, String grower, Integer plus) {
		SamplingReaderDbHelper mDbSamplingHelper = new SamplingReaderDbHelper(activity);
		SQLiteDatabase samplingDb = mDbSamplingHelper.getReadableDatabase();
		
		// New value for one column
		ContentValues values = new ContentValues();
		values.put(SamplingEntry.COLUMN_NAME_TEMPERATURE, temp);
		values.put(SamplingEntry.COLUMN_NAME_GROWER, grower);
		values.put(SamplingEntry.COLUMN_NAME_PLUS, plus);

		// Which row to update, based on the ID
		String selection = SamplingEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(samplingId) };
		
		try {
			samplingDb.update(
					SamplingEntry.TABLE_NAME,
				    values,
				    selection,
				    selectionArgs);
		} 
		catch(Exception e) {
			return false;
		}
		samplingDb.close();
		mDbSamplingHelper.close();
		return true;
	}
	
	public static Cursor getSamplingTempGrowerPlus(Activity activity, int samplingId) {
		SamplingReaderDbHelper mDbSamplingHelper = new SamplingReaderDbHelper(activity);
		SQLiteDatabase samplingDb = mDbSamplingHelper.getReadableDatabase();
		
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				SamplingEntry.COLUMN_NAME_TEMPERATURE,
				SamplingEntry.COLUMN_NAME_GROWER,
				SamplingEntry.COLUMN_NAME_PLUS
		    };
		String selection = SamplingEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
		String[] selectionArgs = {String.valueOf(samplingId)};

		Cursor sampling = samplingDb.query(
				SamplingEntry.TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    selection,                                // The columns for the WHERE clause
		    selectionArgs,                            // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 // The sort order
		    );
		//mDbPalletHelper.close();
		sampling.moveToFirst();
		mDbSamplingHelper.close();
		samplingDb.close();
		return sampling;
	}
	
	public static Cursor getAllProducts(Activity activity) {
		ProductReaderDbHelper mDbProductHelper = new ProductReaderDbHelper(activity);
		SQLiteDatabase productDb = mDbProductHelper.getReadableDatabase();
		
		
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection2 = {
				ProductEntry.COLUMN_NAME_ENTRY_ID,
				ProductEntry.COLUMN_NAME_NAME,
				ProductEntry.COLUMN_NAME_NAMESN,
				ProductEntry.COLUMN_NAME_VARIETY,
				ProductEntry.COLUMN_NAME_SIZE,
				ProductEntry.COLUMN_NAME_STYLE,
				ProductEntry.COLUMN_NAME_LABEL,
				ProductEntry.COLUMN_NAME_VARIETYSN,
				ProductEntry.COLUMN_NAME_SIZESN,
				ProductEntry.COLUMN_NAME_STYLESN,
				ProductEntry.COLUMN_NAME_LABELSN,
				ProductEntry.COLUMN_NAME_MIN
		    };
		

		Cursor product = productDb.query(
			ProductEntry.TABLE_NAME,                   // The table to query
		    projection2,                               // The columns to return
		    null,                                      // The columns for the WHERE clause
		    null,                                      // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 // The sort order
		    );
		product.moveToFirst();
		return product;
	}

	public static Cursor getVarietySizeByProductId(Activity activity, int productId) {
		ProductReaderDbHelper mDbProductHelper = new ProductReaderDbHelper(activity);
		SQLiteDatabase productDb = mDbProductHelper.getReadableDatabase();
		
		
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection2 = {
				ProductEntry.COLUMN_NAME_VARIETY,
				ProductEntry.COLUMN_NAME_SIZE
		    };
		String where = ProductEntry.COLUMN_NAME_ENTRY_ID + "=" + productId;

		Cursor product = productDb.query(
			ProductEntry.TABLE_NAME,                   // The table to query
		    projection2,                               // The columns to return
		    where,                                      // The columns for the WHERE clause
		    null,                                      // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 // The sort order
		    );
		product.moveToFirst();
		return product;
	}
	
	public static int getSampledSamplingCountByProduct(Activity activity, int productId){
		SamplingReaderDbHelper mDbSamplingHelper = new SamplingReaderDbHelper(activity);
		SQLiteDatabase samplingDb = mDbSamplingHelper.getReadableDatabase();
		
		int count = 0;
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		Cursor pallets = getAllPalletsByProduct(activity, productId);
		for (int i = 0; i < pallets.getCount(); i++) {
			int palletid = pallets.getInt(pallets.getColumnIndexOrThrow(PalletEntry.COLUMN_NAME_ENTRY_ID));
			String selection = SamplingEntry.COLUMN_NAME_PALLET + "=" + String.valueOf(palletid) + 
					" AND " + SamplingEntry.COLUMN_NAME_SAMPLED + "=" + String.valueOf(1);

			Cursor sampling = samplingDb.query(
				SamplingEntry.TABLE_NAME,  // The table to query
			    null,                               // The columns to return
			    selection,                                // The columns for the WHERE clause
			    null,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    null                                 // The sort order
			    );
			//mDbPalletHelper.close();
			sampling.moveToFirst();
			count += sampling.getCount();
			pallets.moveToNext();
		}
		
		return count;
	}

	private static Cursor getAllPalletsByProduct(Activity activity, int productId) {
		PalletReaderDbHelper mDbPalletHelper = new PalletReaderDbHelper(activity);
		SQLiteDatabase palletDb = mDbPalletHelper.getReadableDatabase();
		
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection2 = {
				PalletEntry.COLUMN_NAME_ENTRY_ID
		    };
		String where = PalletEntry.COLUMN_NAME_PRODUCT + "=" + productId;

		Cursor pallets = palletDb.query(
				PalletEntry.TABLE_NAME,                   // The table to query
		    projection2,                               // The columns to return
		    where,                                      // The columns for the WHERE clause
		    null,                                      // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 // The sort order
		    );
		pallets.moveToFirst();
		return pallets;
	}

	public static Cursor getIcFactor(Activity activity, int tableNumber){
		
		SharedPreferences sharedPref = activity.getSharedPreferences(
				activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
		String currentSamplingId = String.valueOf(sharedPref.getInt(activity.getString(R.string.saved_current_sampling), 0));

		IcfactorDataReaderDbHelper mDbIcFactorDataHelper = new IcfactorDataReaderDbHelper(activity);
		SQLiteDatabase icFactorDataDb = mDbIcFactorDataHelper.getReadableDatabase();
		
		String[] projection1 = {
				IcfactorDataEntry._ID,
				IcfactorDataEntry.COLUMN_NAME_NAME,
				IcfactorDataEntry.COLUMN_NAME_FACTOR,
				IcfactorDataEntry.COLUMN_NAME_TABLE,
				IcfactorDataEntry.COLUMN_NAME_BUTTON,
				IcfactorDataEntry.COLUMN_NAME_SL,
				IcfactorDataEntry.COLUMN_NAME_M,
				IcfactorDataEntry.COLUMN_NAME_S
		};

		String selection = IcfactorDataEntry.COLUMN_NAME_SAMPLING + 
				"=" + String.valueOf(currentSamplingId) + " AND " + IcfactorDataEntry.COLUMN_NAME_TABLE + 
				"=" + String.valueOf(tableNumber);
		
		Cursor icFactors = icFactorDataDb.query(
				IcfactorDataEntry.TABLE_NAME, projection1, 
				selection, 
				null, null, null, null);
		
		icFactors.moveToFirst();
		return icFactors;
	}

	public static Cursor getIcFactorQ(Activity activity, int tableNumber){
		
		SharedPreferences sharedPref = activity.getSharedPreferences(
				activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
		String currentSamplingId = String.valueOf(sharedPref.getInt(activity.getString(R.string.saved_current_sampling), 0));

		IcfactorDataReaderDbHelper mDbIcFactorDataHelper = new IcfactorDataReaderDbHelper(activity);
		SQLiteDatabase icFactorDataDb = mDbIcFactorDataHelper.getReadableDatabase();
		
		String[] projection1 = {
				IcfactorDataEntry._ID,
				IcfactorDataEntry.COLUMN_NAME_NAME,
				IcfactorDataEntry.COLUMN_NAME_FACTOR,
				IcfactorDataEntry.COLUMN_NAME_TABLE,
				IcfactorDataEntry.COLUMN_NAME_BUTTON,
				IcfactorDataEntry.COLUMN_NAME_SL,
				IcfactorDataEntry.COLUMN_NAME_M,
				IcfactorDataEntry.COLUMN_NAME_S
		};

		String selection = IcfactorDataEntry.COLUMN_NAME_SAMPLING + 
				"=" + String.valueOf(currentSamplingId) + " AND " + IcfactorDataEntry.COLUMN_NAME_TABLE + 
				"=" + String.valueOf(tableNumber + " AND " +
						IcfactorDataEntry.COLUMN_NAME_FACTOR + "=" + "'Quality'");
		
		Cursor icFactors = icFactorDataDb.query(
				IcfactorDataEntry.TABLE_NAME, projection1, 
				selection, 
				null, null, null, null);
		
		icFactors.moveToFirst();
		return icFactors;
	}

	public static Cursor getIcFactorQC(Activity activity, int tableNumber){

		SharedPreferences sharedPref = activity.getSharedPreferences(
				activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
		String currentSamplingId = String.valueOf(sharedPref.getInt(activity.getString(R.string.saved_current_sampling), 0));

		IcfactorDataReaderDbHelper mDbIcFactorDataHelper = new IcfactorDataReaderDbHelper(activity);
		SQLiteDatabase icFactorDataDb = mDbIcFactorDataHelper.getReadableDatabase();
		
		String[] projection1 = {
				IcfactorDataEntry._ID,
				IcfactorDataEntry.COLUMN_NAME_NAME,
				IcfactorDataEntry.COLUMN_NAME_FACTOR,
				IcfactorDataEntry.COLUMN_NAME_TABLE,
				IcfactorDataEntry.COLUMN_NAME_BUTTON,
				IcfactorDataEntry.COLUMN_NAME_SL,
				IcfactorDataEntry.COLUMN_NAME_M,
				IcfactorDataEntry.COLUMN_NAME_S
		};

		String selection = IcfactorDataEntry.COLUMN_NAME_SAMPLING + 
				"=" + String.valueOf(currentSamplingId) + " AND " + IcfactorDataEntry.COLUMN_NAME_TABLE + 
				"=" + String.valueOf(tableNumber + " AND " +
						IcfactorDataEntry.COLUMN_NAME_FACTOR + "=" + "'Quality & Condition'");
		
		Cursor icFactors = icFactorDataDb.query(
				IcfactorDataEntry.TABLE_NAME, projection1, 
				selection, 
				null, null, null, null);
		
		icFactors.moveToFirst();
		return icFactors;
	}
	
	public static Cursor getIcFactorC(Activity activity, int tableNumber){

		SharedPreferences sharedPref = activity.getSharedPreferences(
				activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
		String currentSamplingId = String.valueOf(sharedPref.getInt(activity.getString(R.string.saved_current_sampling), 0));

		IcfactorDataReaderDbHelper mDbIcFactorDataHelper = new IcfactorDataReaderDbHelper(activity);
		SQLiteDatabase icFactorDataDb = mDbIcFactorDataHelper.getReadableDatabase();
		
		String[] projection1 = {
				IcfactorDataEntry._ID,
				IcfactorDataEntry.COLUMN_NAME_NAME,
				IcfactorDataEntry.COLUMN_NAME_FACTOR,
				IcfactorDataEntry.COLUMN_NAME_TABLE,
				IcfactorDataEntry.COLUMN_NAME_BUTTON,
				IcfactorDataEntry.COLUMN_NAME_SL,
				IcfactorDataEntry.COLUMN_NAME_M,
				IcfactorDataEntry.COLUMN_NAME_S
		};

		String selection = IcfactorDataEntry.COLUMN_NAME_SAMPLING + 
				"=" + String.valueOf(currentSamplingId) + " AND " + IcfactorDataEntry.COLUMN_NAME_TABLE + 
				"=" + String.valueOf(tableNumber + " AND " +
						IcfactorDataEntry.COLUMN_NAME_FACTOR + "=" + "'Condition'");
		
		Cursor icFactors = icFactorDataDb.query(
				IcfactorDataEntry.TABLE_NAME, projection1, 
				selection, 
				null, null, null, null);
		
		icFactors.moveToFirst();
		return icFactors;
	}
	
	public static boolean updateSlMAndS(Activity activity, int icFactorId, int sl, int m, int s){
		IcfactorDataReaderDbHelper mDbIcFactorDataHelper = new IcfactorDataReaderDbHelper(activity);
		SQLiteDatabase icFactorDataDb = mDbIcFactorDataHelper.getReadableDatabase();
		
		// New value for one column
		ContentValues values = new ContentValues();
		values.put(IcfactorDataEntry.COLUMN_NAME_SL, sl);
		values.put(IcfactorDataEntry.COLUMN_NAME_M, m);
		values.put(IcfactorDataEntry.COLUMN_NAME_S, s);

		// Which row to update, based on the ID
		String selection = IcfactorDataEntry._ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(icFactorId) };
		
		try {
			icFactorDataDb.update(
					IcfactorDataEntry.TABLE_NAME,
				    values,
				    selection,
				    selectionArgs);
		} 
		catch(Exception e) {
			return false;
		}
		icFactorDataDb.close();
		mDbIcFactorDataHelper.close();
		return true;
	}

	public static Cursor getIcFactorSlMAndS(Activity activity, int icFactorId){
		IcfactorDataReaderDbHelper mDbIcFactorDataHelper = new IcfactorDataReaderDbHelper(activity);
		SQLiteDatabase icFactorDataDb = mDbIcFactorDataHelper.getReadableDatabase();
		
		String[] projection1 = {
				IcfactorDataEntry.COLUMN_NAME_SL,
				IcfactorDataEntry.COLUMN_NAME_M,
				IcfactorDataEntry.COLUMN_NAME_S
		};

		String selection = IcfactorDataEntry._ID + 
				"=" + String.valueOf(icFactorId);
		
		Cursor icFactors = icFactorDataDb.query(
				IcfactorDataEntry.TABLE_NAME, projection1, 
				selection, 
				null, null, null, null);
		
		icFactors.moveToFirst();
		return icFactors;
	}

	public static boolean updateIcFactor(Activity activity, String factor, int icFactorId){
		
		IcfactorReaderDbHelper mDbIcFactorHelper = new IcfactorReaderDbHelper(activity);
		SQLiteDatabase icFactorDb = mDbIcFactorHelper.getReadableDatabase();
		
		// New value for one column
		ContentValues values = new ContentValues();
		values.put(IcfactorEntry.COLUMN_NAME_FACTOR, factor);

		// Which row to update, based on the ID
		String selection = IcfactorEntry._ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(icFactorId) };
		
		try {
			int count = icFactorDb.update(
					IcfactorEntry.TABLE_NAME,
				    values,
				    selection,
				    selectionArgs);
		} 
		catch(Exception e) {
			return false;
		}
		icFactorDb.close();
		mDbIcFactorHelper.close();
		return true;
	}

	public static Cursor getAllQcFactorTables(Activity activity){
		
		int productId = getProductIdByPalletId(activity);
		
		IcfactorReaderDbHelper mDbIcFactorHelper = new IcfactorReaderDbHelper(activity);
		SQLiteDatabase icFactorDb = mDbIcFactorHelper.getReadableDatabase();
		
		String[] projection1 = {"DISTINCT " + IcfactorEntry.COLUMN_NAME_TABLE
		};
		
		String selection = IcfactorEntry.COLUMN_NAME_PRODUCT + 
				"=" + String.valueOf(productId);
		
		Cursor icFactors = icFactorDb.query(
				IcfactorEntry.TABLE_NAME, projection1, 
				selection, 
				null, null, null, null);
		
		icFactors.moveToFirst();
		
		return icFactors;
	}
	
	public static int getQcFactorTable(Activity activity, int icFactorId){
		IcfactorReaderDbHelper mDbIcFactorHelper = new IcfactorReaderDbHelper(activity);
		SQLiteDatabase icFactorDb = mDbIcFactorHelper.getReadableDatabase();
		
		String[] projection1 = {
				IcfactorEntry.COLUMN_NAME_TABLE
		};

		String selection = IcfactorEntry._ID + 
				"=" + String.valueOf(icFactorId);
		
		Cursor icFactors = icFactorDb.query(
				IcfactorEntry.TABLE_NAME, projection1, 
				selection, 
				null, null, null, null);
		
		icFactors.moveToFirst();
		int icFactorTable = icFactors.getInt(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_TABLE));
		return icFactorTable;
	}

	public static int getProductIdByPalletId(Activity activity){
		PalletReaderDbHelper mDbPalletHelper = new PalletReaderDbHelper(activity);
		SQLiteDatabase palletDb = mDbPalletHelper.getReadableDatabase();
		
		SharedPreferences sharedPref = activity.getSharedPreferences(
				activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
		
		String[] projection = {
				PalletEntry.COLUMN_NAME_PRODUCT
		};
		
		String currentPallet = String.valueOf(sharedPref.getInt(activity.getString(R.string.saved_current_pallet), 0));
		String where = PalletEntry.COLUMN_NAME_CODE + "=" + currentPallet;
		
		Cursor c = palletDb.query(
				PalletEntry.TABLE_NAME, projection, where,
				null, null, null, null);
		
		c.moveToFirst();
		int productId = c.getInt(c.getColumnIndexOrThrow(PalletEntry.COLUMN_NAME_PRODUCT));
		
		return productId;
	}

	public static int getProductIdByPalletId(Activity activity, int palletId){
		PalletReaderDbHelper mDbPalletHelper = new PalletReaderDbHelper(activity);
		SQLiteDatabase palletDb = mDbPalletHelper.getReadableDatabase();
		
		String[] projection = {
				PalletEntry.COLUMN_NAME_PRODUCT
		};
		
		String where = PalletEntry.COLUMN_NAME_ENTRY_ID + "=" + palletId;
		
		Cursor c = palletDb.query(
				PalletEntry.TABLE_NAME, projection, where,
				null, null, null, null);
		
		c.moveToFirst();
		int productId = c.getInt(c.getColumnIndexOrThrow(PalletEntry.COLUMN_NAME_PRODUCT));
		
		return productId;
	}
	
	public static boolean updateSamplingBrixPressureMeasurements(Activity activity, int palletId, Float brix, Float pressure, String measurements) {
		SamplingReaderDbHelper mDbSamplingHelper = new SamplingReaderDbHelper(activity);
		SQLiteDatabase samplingDb = mDbSamplingHelper.getReadableDatabase();
		
		// New value for one column
		ContentValues values = new ContentValues();
		values.put(SamplingEntry.COLUMN_NAME_BRIX, brix);
		values.put(SamplingEntry.COLUMN_NAME_PRESSURE, pressure);
		values.put(SamplingEntry.COLUMN_NAME_MEASUREMENTS, measurements);

		// Which row to update, based on the ID
		String selection = SamplingEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(palletId) };
		
		try {
			samplingDb.update(
					SamplingEntry.TABLE_NAME,
				    values,
				    selection,
				    selectionArgs);
		} 
		catch(Exception e) {
			return false;
		}
		samplingDb.close();
		mDbSamplingHelper.close();
		return true;
	}

	public static Cursor getSamplingBrixPressureMeasurements(Activity activity, int palletId)
	{
		SamplingReaderDbHelper mDbSamplingHelper = new SamplingReaderDbHelper(activity);
		SQLiteDatabase samplingDb = mDbSamplingHelper.getReadableDatabase();
		
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				SamplingEntry.COLUMN_NAME_BRIX,
				SamplingEntry.COLUMN_NAME_PRESSURE,
				SamplingEntry.COLUMN_NAME_MEASUREMENTS
		    };
		String selection = SamplingEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
		String[] selectionArgs = {String.valueOf(palletId)};

		Cursor sampling = samplingDb.query(
				SamplingEntry.TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    selection,                                // The columns for the WHERE clause
		    selectionArgs,                            // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 // The sort order
		    );
		//mDbPalletHelper.close();
		sampling.moveToFirst();
		return sampling;
	}

	public static boolean updateSamplingSampled(Activity activity, int samplingId) {
		SamplingReaderDbHelper mDbSamplingHelper = new SamplingReaderDbHelper(activity);
		SQLiteDatabase samplingDb = mDbSamplingHelper.getReadableDatabase();
		
		// New value for one column
		ContentValues values = new ContentValues();
		values.put(SamplingEntry.COLUMN_NAME_SAMPLED, 1);

		// Which row to update, based on the ID
		String selection = SamplingEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(samplingId) };
		
		try {
			samplingDb.update(
					SamplingEntry.TABLE_NAME,
				    values,
				    selection,
				    selectionArgs);
		} 
		catch(Exception e) {
			return false;
		}
		samplingDb.close();
		mDbSamplingHelper.close();
		return true;
	}

	public static int getPicturesCountByPallet(Activity activity, int samplingId) {
		PictureReaderDbHelper mDbPictureHelper = new PictureReaderDbHelper(activity);
		SQLiteDatabase pictureDb = mDbPictureHelper.getReadableDatabase();
		
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection2 = {
		    };
		String where = PictureEntry.COLUMN_NAME_SAMPLING + "=" + samplingId;

		Cursor picture = pictureDb.query(
			PictureEntry.TABLE_NAME,                   // The table to query
		    null,                               // The columns to return
		    where,                                      // The columns for the WHERE clause
		    null,                                      // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 // The sort order
		    );
		picture.moveToFirst();
		return picture.getCount();
	}

	public static void savePicture(Activity activity, String name, int samplingId) {
		PictureReaderDbHelper mDbPictureHelper = new PictureReaderDbHelper(activity);
		SQLiteDatabase pictureDb = mDbPictureHelper.getReadableDatabase();
		
		ContentValues values = new ContentValues();
        values.put(PictureEntry.COLUMN_NAME_NAME, name);
        values.put(PictureEntry.COLUMN_NAME_SAMPLING, samplingId);

        // Insert the new row, returning the primary key value of the new row
        pictureDb.insert(PictureEntry.TABLE_NAME, null, values);
	}

	public static Cursor getAllPictureBySampling(Activity activity, int samplingId) {
		PictureReaderDbHelper mDbPictureHelper = new PictureReaderDbHelper(activity);
		SQLiteDatabase pictureDb = mDbPictureHelper.getReadableDatabase();
		
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection2 = {
				PictureEntry.COLUMN_NAME_NAME,
				PictureEntry.COLUMN_NAME_ENTRY_ID
		    };
		String where = PictureEntry.COLUMN_NAME_SAMPLING + "=" + samplingId;

		Cursor picture = pictureDb.query(
			PictureEntry.TABLE_NAME,                   // The table to query
			projection2,                               // The columns to return
		    where,                                      // The columns for the WHERE clause
		    null,                                      // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 // The sort order
		    );
		picture.moveToFirst();
		return picture;
	}

	public static void deletePictureByName(Activity activity, String name) {
		PictureReaderDbHelper mDbPictureHelper = new PictureReaderDbHelper(activity);
		SQLiteDatabase pictureDb = mDbPictureHelper.getReadableDatabase();
		
		// Define 'where' part of query.
		String selection = PictureEntry.COLUMN_NAME_NAME + " LIKE ?";
		// Specify arguments in placeholder order.
		String[] selectionArgs = { name };
		// Issue SQL statement.
		pictureDb.delete(PictureEntry.TABLE_NAME, selection, selectionArgs);
	}

	public static Cursor getAllPallets(Activity activity) {
		PalletReaderDbHelper mDbPalletHelper = new PalletReaderDbHelper(activity);
		SQLiteDatabase palletDb = mDbPalletHelper.getReadableDatabase();
		
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection2 = {
				PalletEntry.COLUMN_NAME_ENTRY_ID,
				PalletEntry.COLUMN_NAME_CODE
		    };
		
		Cursor pallets = palletDb.query(
				PalletEntry.TABLE_NAME,                   // The table to query
		    projection2,                               // The columns to return
		    null,                                      // The columns for the WHERE clause
		    null,                                      // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 // The sort order
		    );
		pallets.moveToFirst();
		return pallets;
	}
	
	public static Cursor getAllSamplingByPallet(Activity activity, int palletId) {
		SamplingReaderDbHelper mDbSamplingHelper = new SamplingReaderDbHelper(activity);
		SQLiteDatabase samplingDb = mDbSamplingHelper.getReadableDatabase();
		
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection2 = {
				SamplingEntry.COLUMN_NAME_ENTRY_ID,
				SamplingEntry.COLUMN_NAME_PALLET,
				SamplingEntry.COLUMN_NAME_PRESSURE,
				SamplingEntry.COLUMN_NAME_GROWER,
				SamplingEntry.COLUMN_NAME_MEASUREMENTS,
				SamplingEntry.COLUMN_NAME_PLUS,
				SamplingEntry.COLUMN_NAME_BRIX,
				SamplingEntry.COLUMN_NAME_TEMPERATURE,
		    };
		String where = SamplingEntry.COLUMN_NAME_SAMPLED + "=" + "1" + " AND " + SamplingEntry.COLUMN_NAME_PALLET + "=" + palletId;

		Cursor samplings = samplingDb.query(
				SamplingEntry.TABLE_NAME,                   // The table to query
		    projection2,                               // The columns to return
		    where,                                      // The columns for the WHERE clause
		    null,                                      // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 // The sort order
		    );
		samplings.moveToFirst();
		return samplings;
	}

	public static Cursor getAllQcFactorNumbersByPalletId(Activity activity, int palletId){
		
		int productId = getProductIdByPalletId(activity, palletId);
		
		IcfactorReaderDbHelper mDbIcFactorHelper = new IcfactorReaderDbHelper(activity);
		SQLiteDatabase icFactorDb = mDbIcFactorHelper.getReadableDatabase();
		
		String[] projection1 = {"DISTINCT " + IcfactorEntry.COLUMN_NAME_TABLE
		};
		
		String selection = IcfactorEntry.COLUMN_NAME_PRODUCT + 
				"=" + String.valueOf(productId);
		
		Cursor icFactors = icFactorDb.query(
				IcfactorEntry.TABLE_NAME, projection1, 
				selection, 
				null, null, null, null);
		
		icFactors.moveToFirst();
		
		return icFactors;
	}

	public static Cursor getSamplingById(Activity activity, int samplingId) {
		SamplingReaderDbHelper mDbSamplingHelper = new SamplingReaderDbHelper(activity);
		SQLiteDatabase samplingDb = mDbSamplingHelper.getReadableDatabase();
		
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				SamplingEntry.COLUMN_NAME_ENTRY_ID,
				SamplingEntry.COLUMN_NAME_PALLET,
				SamplingEntry.COLUMN_NAME_PRESSURE,
				SamplingEntry.COLUMN_NAME_GROWER,
				SamplingEntry.COLUMN_NAME_MEASUREMENTS,
				SamplingEntry.COLUMN_NAME_PLUS,
				SamplingEntry.COLUMN_NAME_BRIX,
				SamplingEntry.COLUMN_NAME_TEMPERATURE,
		    };
		String selection = SamplingEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
		String[] selectionArgs = {String.valueOf(samplingId)};

		Cursor sampling = samplingDb.query(
				SamplingEntry.TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    selection,                                // The columns for the WHERE clause
		    selectionArgs,                            // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 // The sort order
		    );
		//mDbPalletHelper.close();
		sampling.moveToFirst();
		mDbSamplingHelper.close();
		samplingDb.close();
		return sampling;
	}
	
	public static Cursor getPalletById(Activity activity, int palletId) {
		PalletReaderDbHelper mDbPalletHelper = new PalletReaderDbHelper(activity);
		SQLiteDatabase palletDb = mDbPalletHelper.getReadableDatabase();
		
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				PalletEntry.COLUMN_NAME_ENTRY_ID,
				PalletEntry.COLUMN_NAME_PRODUCT,
				PalletEntry.COLUMN_NAME_CODE
		    };
		String selection = PalletEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
		String[] selectionArgs = {String.valueOf(palletId)};

		Cursor pallet = palletDb.query(
				PalletEntry.TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    selection,                                // The columns for the WHERE clause
		    selectionArgs,                            // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 // The sort order
		    );
		//mDbPalletHelper.close();
		pallet.moveToFirst();
		mDbPalletHelper.close();
		palletDb.close();
		return pallet;
	}

	public static Cursor getQCFactorTablesByNumberAndSamplingId(Activity activity, int samplingId, int tableNumber) {
		
		IcfactorDataReaderDbHelper mDbIcFactorDataHelper = new IcfactorDataReaderDbHelper(activity);
		SQLiteDatabase icFactorDataDb = mDbIcFactorDataHelper.getReadableDatabase();
		
		String[] projection1 = {
				IcfactorDataEntry._ID,
				IcfactorDataEntry.COLUMN_NAME_NAME,
				IcfactorDataEntry.COLUMN_NAME_FACTOR,
				IcfactorDataEntry.COLUMN_NAME_TABLE,
				IcfactorDataEntry.COLUMN_NAME_BUTTON,
				IcfactorDataEntry.COLUMN_NAME_SL,
				IcfactorDataEntry.COLUMN_NAME_M,
				IcfactorDataEntry.COLUMN_NAME_S
		};

		String selection = IcfactorDataEntry.COLUMN_NAME_SAMPLING + 
				"=" + String.valueOf(samplingId) + " AND " + IcfactorEntry.COLUMN_NAME_TABLE + 
				"=" + String.valueOf(tableNumber);
		
		Cursor icFactors = icFactorDataDb.query(
				IcfactorDataEntry.TABLE_NAME, projection1, 
				selection, 
				null, null, null, null);
		
		icFactors.moveToFirst();
		return icFactors;
	}

	public static boolean containPalletIdInDataBase(Activity activity, int palletId){
		PalletReaderDbHelper mDbPalletHelper = new PalletReaderDbHelper(activity);
		SQLiteDatabase palletDb = mDbPalletHelper.getReadableDatabase();
		
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
				PalletEntry.COLUMN_NAME_CODE
		    };
		String selection = PalletEntry.COLUMN_NAME_CODE + " LIKE ?";
		String[] selectionArgs = {String.valueOf(palletId)};

		Cursor pallet = palletDb.query(
			PalletEntry.TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    selection,                                // The columns for the WHERE clause
		    selectionArgs,                            // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 // The sort order
		    );
		pallet.moveToFirst();
		if(pallet.getCount()>0)return true;
		return false;
	}

	public static boolean updateSamplingQcFactorTableNumber(Activity activity, int selectedItem, int samplingId) {
		SamplingReaderDbHelper mDbSamplingHelper = new SamplingReaderDbHelper(activity);
		SQLiteDatabase samplingDb = mDbSamplingHelper.getReadableDatabase();
		
		// New value for one column
		ContentValues values = new ContentValues();
		values.put(SamplingEntry.COLUMN_NAME_TABLENUMBER, selectedItem);

		// Which row to update, based on the ID
		String selection = SamplingEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
		String[] selectionArgs = { String.valueOf(samplingId) };
		
		try {
			samplingDb.update(
					SamplingEntry.TABLE_NAME,
				    values,
				    selection,
				    selectionArgs);
		} 
		catch(Exception e) {
			return false;
		}
		samplingDb.close();
		mDbSamplingHelper.close();
		return true;
		
	}

	public static int getTableNumberBySamplingId(Activity activity, int samplingId) {
		SamplingReaderDbHelper mDbSamplingHelper = new SamplingReaderDbHelper(activity);
		SQLiteDatabase samplingDb = mDbSamplingHelper.getReadableDatabase();
		
		String[] projection = {
				SamplingEntry.COLUMN_NAME_TABLENUMBER
		};
		
		
		String where = SamplingEntry.COLUMN_NAME_ENTRY_ID + "=" + samplingId;
		
		Cursor c = samplingDb.query(
				SamplingEntry.TABLE_NAME, projection, where,
				null, null, null, null);
		
		c.moveToFirst();
		int tableNumber = c.getInt(c.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_TABLENUMBER));
		samplingDb.close();
		mDbSamplingHelper.close();
		return tableNumber;
	}
	
	public static int getSamplingIdByCode(Activity activity, int code) {
		SamplingReaderDbHelper mDbSamplingHelper = new SamplingReaderDbHelper(activity);
		SQLiteDatabase samplingDb = mDbSamplingHelper.getReadableDatabase();
		
		String[] projection = {
				SamplingEntry.COLUMN_NAME_ENTRY_ID
		};
		int palletId = QueryRepository.getPalletIdByCode(activity, code);
		String selection = SamplingEntry.COLUMN_NAME_PALLET + "=" + String.valueOf(palletId) + 
				" AND " + SamplingEntry.COLUMN_NAME_SAMPLED + "=" + String.valueOf(0);

		Cursor c = samplingDb.query(
			SamplingEntry.TABLE_NAME,  // The table to query
			projection,                               // The columns to return
		    selection,                                // The columns for the WHERE clause
		    null,                            // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 // The sort order
		    );
		
		c.moveToFirst();
		int samplingId = -1;
		if (c.getCount() == 0) {
			ContentValues values = new ContentValues();
	        values.put(SamplingEntry.COLUMN_NAME_PALLET, palletId);
	        values.put(SamplingEntry.COLUMN_NAME_SAMPLED, 0);
	        samplingId = (int) samplingDb.insert(SamplingEntry.TABLE_NAME, null, values);
	        
	        int productId = getProductIdByPalletId(activity, palletId);
	        Cursor icFactors = getIcFactorsByProduct(activity, productId);
	        
	        for (int i = 0; i < icFactors.getCount(); i++) {
	        	
	        	String factor = icFactors.getString(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_FACTOR));
	        	String name = icFactors.getString(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_NAME));
	        	int button = icFactors.getInt(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_BUTTON));
	        	int table = icFactors.getInt(icFactors.getColumnIndexOrThrow(IcfactorEntry.COLUMN_NAME_TABLE));
	        	
	        	values = new ContentValues();

		        values.put(IcfactorDataEntry.COLUMN_NAME_SAMPLING, samplingId);
		        values.put(IcfactorDataEntry.COLUMN_NAME_FACTOR, factor);
		        values.put(IcfactorDataEntry.COLUMN_NAME_NAME, name);
		        values.put(IcfactorDataEntry.COLUMN_NAME_BUTTON, button);
		        values.put(IcfactorDataEntry.COLUMN_NAME_TABLE, table);
		        samplingDb.insert(IcfactorDataEntry.TABLE_NAME, null, values);
		        
				icFactors.moveToNext();
			}
		} else {
			samplingId = c.getInt(c.getColumnIndexOrThrow(SamplingEntry.COLUMN_NAME_ENTRY_ID));
		}
		
		samplingDb.close();
		mDbSamplingHelper.close();
		return samplingId;
	}

	public static Cursor getIcFactorsByProduct(Activity activity, int productId) {
		IcfactorReaderDbHelper mDbIcFactorHelper = new IcfactorReaderDbHelper(activity);
		SQLiteDatabase icFactorDb = mDbIcFactorHelper.getReadableDatabase();
		
		String[] projection1 = {
				IcfactorEntry._ID,
				IcfactorEntry.COLUMN_NAME_NAME,
				IcfactorEntry.COLUMN_NAME_FACTOR,
				IcfactorEntry.COLUMN_NAME_TABLE,
				IcfactorEntry.COLUMN_NAME_BUTTON,
				IcfactorEntry.COLUMN_NAME_QCFACTORID,
				IcfactorEntry.COLUMN_NAME_TABLE,
				IcfactorEntry.COLUMN_NAME_ORDER
		};

		String selection = IcfactorEntry.COLUMN_NAME_PRODUCT + 
				"=" + String.valueOf(productId);
		
		Cursor icFactors = icFactorDb.query(
				IcfactorEntry.TABLE_NAME, projection1, 
				selection, 
				null, null, null, null);
		
		icFactors.moveToFirst();
		return icFactors;
	}
}