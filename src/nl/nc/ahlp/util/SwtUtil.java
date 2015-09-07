package nl.nc.ahlp.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * SWT utility class.
 * 
 * 
 * @author Nino Camdzic
 */
public class SwtUtil {
	public static void showError(Shell shell, String title, String message) {
		MessageBox msgBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		msgBox.setText(title);
		msgBox.setMessage(message);
		msgBox.open();
	}
	
	public static void showError(Shell shell, String message) {
		showError(shell, "Error", message);
	}
	
	public static void showInfo(Shell shell, String title, String message) {
		MessageBox msgBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
		msgBox.setText(title);
		msgBox.setMessage(message);
		msgBox.open();
	}
	
	public static void showInfo(Shell shell, String message) {
		showInfo(shell, shell.getText(), message);
	}
}