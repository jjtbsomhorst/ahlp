package nl.nc.ahlp.view;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * Info UI.
 * 
 * @author Nino Camdzic
 *
 */
public class InfoUi extends Dialog {
	protected Object result;
	protected Shell shlInfo;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public InfoUi(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlInfo.open();
		shlInfo.layout();
		Display display = getParent().getDisplay();
		while (!shlInfo.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlInfo = new Shell(getParent(), getStyle());
		shlInfo.setSize(304, 159);
		shlInfo.setLocation(shlInfo.getParent().getLocation().x + (shlInfo.getParent().getSize().x / 2) - (shlInfo.getSize().x /2), shlInfo.getParent().getLocation().y + (shlInfo.getParent().getSize().y / 2) - (shlInfo.getSize().y / 2));
		shlInfo.setText("Info");
		
		Button btnOk = new Button(shlInfo, SWT.NONE);
		btnOk.setBounds(213, 97, 75, 25);
		btnOk.setText("OK");
		btnOk.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				shlInfo.dispose();
			}
		});
		
		Composite composite = new Composite(shlInfo, SWT.NONE);
		composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite.setBounds(0, 0, 298, 89);
		
		Label lblAppNameVer = new Label(composite, SWT.NONE);
		lblAppNameVer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblAppNameVer.setText("AHLP (Apache HTTPD Log Parser) v" + MainUi.APP_VER_STR);
		lblAppNameVer.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		lblAppNameVer.setBounds(37, 10, 250, 15);
		
		Label lblCopyright = new Label(composite, SWT.NONE);
		lblCopyright.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblCopyright.setText("Copyright(c) Nino Camdzic 2014");
		lblCopyright.setBounds(37, 31, 182, 15);
		
		Label lblToolbarIconsInfo = new Label(composite, SWT.NONE);
		lblToolbarIconsInfo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblToolbarIconsInfo.setText("Farm-Fresh Web Icons by:");
		lblToolbarIconsInfo.setBounds(37, 59, 143, 15);
		
		Link linkToolbarIconsLink = new Link(composite, 0);
		linkToolbarIconsLink.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		linkToolbarIconsLink.setText("<a>FatCow</a>");
		linkToolbarIconsLink.setBounds(181, 59, 46, 15);
		linkToolbarIconsLink.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				openLinkInBrowser("http://www.fatcow.com/free-icons");
			}
		});
		
		
		Label lblIcon = new Label(composite, SWT.NONE);
		lblIcon.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblIcon.setImage(SWTResourceManager.getImage(InfoUi.class, "/gfx/ahlp.ico"));
		lblIcon.setBounds(10, 10, 24, 22);

	}
	
	private void openLinkInBrowser(String url) {
		if(Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}
}
