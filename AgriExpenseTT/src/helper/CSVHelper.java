package helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

import uwi.dcit.AgriExpenseTT.R;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.agriexpensett.rpurchaseendpoint.model.RPurchase;

import dataObjects.localCycle;
import dataObjects.localCycleUse;
import dataObjects.localResourcePurchase;

@SuppressWarnings("deprecation")
public class CSVHelper {
	SQLiteDatabase db;
	DbHelper dbh;
	Activity activity;
	
	
	public CSVHelper(Activity act){
		dbh=new DbHelper(act.getBaseContext());
		db=dbh.getReadableDatabase();
		activity=act;
	}
	public void stuff(Context context){
		File path = new File(Environment.getExternalStorageDirectory()+"/Agrinet");
		if (!path.exists()) {
			path.mkdirs();
			Log.i("Foler", " Folder does not exist");
		}else{	
		}
		Log.i("file path", path.getPath());
		writeExcel(path);
		
	}
	private void writeExcel(File path){
		ArrayList<localCycle> cList=new ArrayList<localCycle>();
		DbQuery.getCycles(db, dbh, cList);
		HSSFWorkbook agriWrkbk = new HSSFWorkbook();
		HSSFSheet useSheet = agriWrkbk.createSheet("Use Sheet");
		int rowNum=0;
		for(localCycle lc:cList){
			HSSFRow row = useSheet.createRow(rowNum++);
			HSSFCell a0 = row.createCell(0);
			a0.setCellValue("Cycle#"+lc.getCropId()+": "+DbQuery.findResourceName(db, dbh, lc.getCropId()));
			HSSFCell a1 = row.createCell(1);
			a1.setCellValue(lc.getTotalSpent());
			rowNum=writeCategories(path,agriWrkbk,useSheet,lc.getId(),rowNum);
			rowNum+=3;
		}
	}

	private int writeCategories(File path, HSSFWorkbook agriWrkbk,
			HSSFSheet useSheet, int cycId, int rowNum) {
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
		HSSFCell cellHeadcostPur=rowHead.createCell(c++);
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
			FileOutputStream out=new FileOutputStream(path+"/test.xls");
			agriWrkbk.write(out);
			notify("meh.xls",path);
		} catch (IOException e1) {e1.printStackTrace();}
	    return rowNum;

	}

	private int writeCategory(String type, int cycId,HSSFSheet useSheet, int rowNum, HSSFCellStyle style){
		ArrayList<localCycleUse> useList = new ArrayList<localCycleUse>();
		ArrayList<localResourcePurchase> purList = new ArrayList<localResourcePurchase>();
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
	
		for( localCycleUse lcu:useList){
			 rowNum++;int c=0;
			 HSSFRow row=useSheet.createRow(rowNum);
			 RPurchase p=DbQuery.getARPurchase(db, dbh, lcu.getPurchaseId());
			 
			 HSSFCell resCell=row.createCell(c++);
			 //resCell.setCellType(Cell.CELL_TYPE_STRING);
			 resCell.setCellValue(DbQuery.findResourceName(db, dbh, p.getResourceId())
					 +" "+p.getQuantifier());
			
			 
			 HSSFCell amtUsedCell=row.createCell(c++);
			 //amtUsedCell.setCellType(Cell.CELL_TYPE_NUMERIC);
			 amtUsedCell.setCellValue(lcu.getAmount());
			 
			 HSSFCell costUseCell=row.createCell(c++);
			 //costUseCell.setCellType(Cell.CELL_TYPE_NUMERIC);
			 costUseCell.setCellValue(lcu.getUseCost());
			 
			 HSSFCell amtPurCell=row.createCell(c++);
			 //amtPurCell.setCellType(Cell.CELL_TYPE_NUMERIC);
			 amtPurCell.setCellValue(p.getQty());
			 
			 HSSFCell costPurCell=row.createCell(c++);
			 //costPurCell.setCellType(Cell.CELL_TYPE_NUMERIC);
			 costPurCell.setCellValue(p.getCost());
		}
		return ++rowNum;
	}

	private void populate(ArrayList<localCycleUse> useList,
			ArrayList<localResourcePurchase> purList, String type, int cycId) {
		DbQuery.getPurchases(db, dbh, purList, type, null,true);
		DbQuery.getCycleUse(db, dbh, cycId, useList, type);
	}
	
	public void notify(String name,File path){
		//controls what activity is called when notification is clicked
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(path+File.separator+"test.xls");
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.ms-excel");
		PendingIntent pIntent = PendingIntent.getActivity(activity.getBaseContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		//notification details
		Notification.Builder noti = new Notification.Builder(activity);
		noti.setContentTitle("Excel generated");
		noti.setContentText("Your excel file "+name+" has been generated");
		noti.setSmallIcon(R.drawable.money_bag_down);
		noti.setAutoCancel(true);
		noti.setOnlyAlertOnce(true);
		noti.setTicker("Agrinet excel file");
		noti.setContentIntent(pIntent);
		NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, noti.build()); 
		//activity.finish();
	}
	
}