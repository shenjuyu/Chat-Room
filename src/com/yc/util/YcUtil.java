package com.yc.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class YcUtil {

	public static String getCurrentTime(){
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		return format.format(date);
	}
	
	/**
	 * 弹窗
	 * @param shell
	 * @param title
	 * @param message
	 */
	public static void showMessage(Shell shell,String title,String message){
		MessageBox mb=new MessageBox(shell, SWT.DIALOG_TRIM);
		mb.setMessage(message);
		mb.setText(title);
		mb.open();
	}
}
