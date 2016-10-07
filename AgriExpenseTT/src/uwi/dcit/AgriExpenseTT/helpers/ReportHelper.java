package uwi.dcit.AgriExpenseTT.helpers;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import uwi.dcit.AgriExpenseTT.R;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;
import uwi.dcit.AgriExpenseTT.models.LocalCycleUse;
import uwi.dcit.AgriExpenseTT.models.LocalResourcePurchase;
import uwi.dcit.agriexpensesvr.resourcePurchaseApi.model.ResourcePurchase;

//import com.dcit.agriexpensett.rPurchaseApi.model.RPurchase;
//import org.apache.poi.hssf.util.CellRangeAddress;

@SuppressWarnings("deprecation")
public class ReportHelper {
	public static final String folderLocation = "AgriExpense";
	private static final String TAG = "ReportHelper";
	private static final String defaultName = "AgriExpenseReport";
	private final OnReportSuccess successHandler;
	private SQLiteDatabase db;
	private DbHelper dbh;
	private Activity activity;
	
	public ReportHelper(Activity act, OnReportSuccess successHandler) {
		dbh = new DbHelper(act.getBaseContext());
		db = dbh.getWritableDatabase();
		activity = act;
		this.successHandler = successHandler;
	}

	public static void createReportDirectory(){
		File path = new File(Environment.getExternalStorageDirectory()+"/"+folderLocation);
		if (!path.exists()) {
			if (path.mkdirs()) Log.d(TAG, "Path was created");
			else Log.d(TAG, "Unable to create Path");
		}
	}
	
	/**
	 * Create a report using a combination of the default filename and the current time
	 */
	public void createReport(long start, long end){
		//Create a default name for the report file based on current date
		Calendar c = Calendar.getInstance();
		StringBuilder stb = new StringBuilder();
		stb.append(defaultName)
			.append("-")
			.append(c.get(Calendar.DAY_OF_MONTH))
			.append("-")
			.append(c.get(Calendar.MONTH))
			.append("-")
			.append(c.get(Calendar.YEAR))
			.append(".xls");

		Log.d(TAG, "Attempting to create a report called: " + stb.toString());
		createReport(stb.toString(), start, end);
	}
	
	/**
	 * Create a report with the supplied filename up to the current time frame
	 * @param filename This string will identify the name of the file that will be generated
	 */
	public void createReport(String filename, long start, long end){
		File path = new File(Environment.getExternalStorageDirectory()+"/"+folderLocation);
		Log.d(TAG, "Attempting to create a report " + filename + " at " + path.toString());
		if (!path.exists()) {
			Log.d(TAG, "Path does not exist ... Attempting to Create at " + path);
			if (path.mkdirs()) Log.d("ReportHelper", "Creating Folders was successful");
			else Log.e(TAG, "Unable to create Folder");
		}
		writeExcel(path, filename, start, end);
	}
	
	private void writeExcel(File path, String filename, long start, long end){
		Log.d(TAG, "Write Excel was executed");
		ArrayList<LocalCycle> cycleList = new ArrayList<>();
		try {
			DbQuery.getCycles(db, dbh, cycleList); //TODO Develop Query based on the timeframe entered as parameters

			Log.d(TAG, "Received " + cycleList.size() + " Cycles");
			// Generate Excel File
			HSSFWorkbook agriWrkbk = new HSSFWorkbook();
			HSSFSheet useSheet = agriWrkbk.createSheet("Crop Cycle");
			Log.i("Start-End", "" + start + " " + end);
			int rowNum = 0;
			for (LocalCycle cycle : cycleList) {
				Log.d(TAG, "Found: " + cycle.getCycleName());
//				if (cycle.getTime() >= start && cycle.getTime() <= end) {
				Log.d(TAG, "Finding the following as in the criteria " + cycle.getCycleName());
				HSSFRow row = useSheet.createRow(rowNum++);
				HSSFCell a0 = row.createCell(0);
				a0.setCellValue("Cycle#" + cycle.getCropId() + ": " + DbQuery.findResourceName(db, dbh, cycle.getCropId()));
				HSSFCell a1 = row.createCell(1);
				a1.setCellValue(cycle.getTotalSpent());
				rowNum = writeCategories(filename, path, agriWrkbk, useSheet, cycle.getId(), rowNum);
				rowNum += 3;
				Log.i("TIME", "BETWEEN TIME");
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (successHandler != null)
				successHandler.handleResult(false, "Unable to Create Excel Report");
		}
	}

	private int writeCategories(String filename, File path, HSSFWorkbook agriWrkbk, HSSFSheet useSheet, int cycId, int rowNum) {
		Log.d(TAG, "Calling Write Categories");
		HSSFCellStyle styleGen = agriWrkbk.createCellStyle();

	    int c=0;
	    rowNum++;
		HSSFRow rowHead=useSheet.createRow(rowNum++);
		HSSFCell cellHeadres=rowHead.createCell(c++);
		cellHeadres.setCellValue("Resource");
		HSSFCell cellHeadamtUsed=rowHead.createCell(c++);
		cellHeadamtUsed.setCellValue("Quantity Used");
		HSSFCell cellHeadcostUse=rowHead.createCell(c++);
		cellHeadcostUse.setCellValue("Cost of Use");
		HSSFCell cellHeadamtPur=rowHead.createCell(c++);
		cellHeadamtPur.setCellValue("Amount Purchased");
		HSSFCell cellHeadcostPur = rowHead.createCell(c);
		cellHeadcostPur.setCellValue("Cost of Purchase");
		///rowNum++;
		
		HSSFCellStyle stylePm = agriWrkbk.createCellStyle();
		stylePm.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		rowNum=writeCategory(DHelper.cat_plantingMaterial,cycId, useSheet, rowNum,stylePm);
		HSSFCellStyle styleFer = agriWrkbk.createCellStyle();
		styleFer.setFillForegroundColor(HSSFColor.CORNFLOWER_BLUE.index);
		rowNum=writeCategory(DHelper.cat_fertilizer,cycId, useSheet, rowNum, styleFer);
		HSSFCellStyle styleSA = agriWrkbk.createCellStyle();
		styleSA.setFillForegroundColor(HSSFColor.BROWN.index);
		rowNum=writeCategory(DHelper.cat_soilAmendment,cycId, useSheet, rowNum, styleSA);
		HSSFCellStyle styleChem = agriWrkbk.createCellStyle();
		styleChem.setFillForegroundColor(HSSFColor.VIOLET.index);
		rowNum=writeCategory(DHelper.cat_chemical,cycId, useSheet, rowNum, styleChem);
		HSSFCellStyle styleLbr = agriWrkbk.createCellStyle();
		styleLbr.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
		rowNum=writeCategory(DHelper.cat_labour,cycId, useSheet, rowNum, styleLbr);
		HSSFCellStyle styleOtr = agriWrkbk.createCellStyle();
		styleOtr.setFillForegroundColor(HSSFColor.MAROON.index);
		rowNum=writeCategory(DHelper.cat_other,cycId, useSheet, rowNum, styleOtr);
		
	    styleGen.setWrapText(true);
		rowHead.setRowStyle(styleGen);
		//useSheet.autoSizeColumn(1,false);
		Log.i(null, "almost");  
		try {
			FileOutputStream out=new FileOutputStream(path+"/"+filename);
			agriWrkbk.write(out);
			notify(filename,path);
		} catch (IOException e1) {e1.printStackTrace();}
	    return rowNum;

	}

	private int writeCategory(String type, int cycId,HSSFSheet useSheet, int rowNum, HSSFCellStyle style){
		ArrayList<LocalCycleUse> useList = new ArrayList<>();
		ArrayList<LocalResourcePurchase> purList = new ArrayList<>();
		populate(useList,purList,type,cycId);
		if(useList.isEmpty())
			return rowNum;
		rowNum++;
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		HSSFRow rowHeadMain=useSheet.createRow(rowNum);
		HSSFCell cl= rowHeadMain.createCell(0,Cell.CELL_TYPE_STRING);
		cl.setCellValue(type);
		useSheet.addMergedRegion(new CellRangeAddress(rowNum,rowNum, 0,4));
		cl.setCellStyle(style);
		//HSSFFont fnt=new HSSFFont((short) 2);
	
		for( LocalCycleUse lcu:useList){
			rowNum++;
			int c = 0;
			HSSFRow row = useSheet.createRow(rowNum);
			ResourcePurchase p = DbQuery.getARPurchase(db, dbh, lcu.getPurchaseId());
			 
			 HSSFCell resCell=row.createCell(c++);
			 //resCell.setCellType(Cell.CELL_TYPE_STRING);
			resCell.setCellValue(DbQuery.findResourceName(db, dbh, p.getResourceId()) + " " + p.getQuantifier());
			
			 
			 HSSFCell amtUsedCell=row.createCell(c++);
			 //amtUsedCell.setCellType(Cell.CELL_TYPE_NUMERIC);
			 amtUsedCell.setCellValue(lcu.getAmount());
			 
			 HSSFCell costUseCell=row.createCell(c++);
			 //costUseCell.setCellType(Cell.CELL_TYPE_NUMERIC);
			 costUseCell.setCellValue(lcu.getUseCost());
			 
			 HSSFCell amtPurCell=row.createCell(c++);
			 //amtPurCell.setCellType(Cell.CELL_TYPE_NUMERIC);
			 amtPurCell.setCellValue(p.getQty());

			HSSFCell costPurCell = row.createCell(c);
			 //costPurCell.setCellType(Cell.CELL_TYPE_NUMERIC);
			 costPurCell.setCellValue(p.getCost());
		}
		return ++rowNum;
	}

	private void populate(ArrayList<LocalCycleUse> useList, ArrayList<LocalResourcePurchase> purList, String type, int cycId) {
		DbQuery.getPurchases(db, dbh, purList, type, null,true);
		DbQuery.getCycleUse(db, dbh, cycId, useList, type);
	}
	
	private void notify(String name, File path) {
		//controls what activity is called when notification is clicked
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		
		File file = new File(path +File.separator + name);
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
		PendingIntent pIntent = PendingIntent.getActivity(activity.getBaseContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		//notification details
        NotificationCompat.Builder noti = new NotificationCompat.Builder(activity);
		noti.setContentTitle("Excel generated")
		    .setContentText("Your Report "+ name +" has been generated") //TODO Create Notification String Value
		    .setSmallIcon(R.drawable.money_bag_down)
		    .setAutoCancel(true)
		    .setOnlyAlertOnce(true)
		    .setTicker("AgriExpense excel file")//TODO Create Notification String Value
		    .setContentIntent(pIntent);

        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, noti.build()); 
		//activity.finish();
	}

	public interface OnReportSuccess {
		void handleResult(boolean result, String msg);
	}
}