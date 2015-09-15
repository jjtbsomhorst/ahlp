package nl.nc.ahlp.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import nl.nc.ahlp.LogParser;
import nl.nc.ahlp.ObtainLogParserException;
import nl.nc.ahlp.controller.LogController;
import nl.nc.ahlp.controller.LogControllerException;
import nl.nc.ahlp.controller.UpdateResult;
import nl.nc.ahlp.impl.LogEntry;
import nl.nc.ahlp.util.StringUtil;
import nl.nc.ahlp.util.SwtUtil;

/**
 * Main View.
 * 
 * @author Nino Camdzic
 *
 */
public class MainUi implements Observer {
	private static final String GFX_ICON_PATH = "/gfx/farmfresh/information.ico";
	public static final String APP_VER_STR = "0.0.1";
	public static final String APP_NAME = "AHLP";
	
	private Shell shlMain = null;
	private Button btnSelectLogsDir = null;
	private ScrolledComposite tableContainer = null;
	private Table table = null;
	private Text textFilterValue = null;
	private Combo comboFilter = null;
	private Button btnFilter = null;
	private Button btnClearFilter = null;
	private Button btnInfo = null;
	private Label lblState = null;
	private Combo comboLogFiles = null;
	private Button checkRegExpr = null;
	
	private LogController logController = null;
	private UpdateResult entries = null;
	private Clipboard clipboard = null;
	
	private int tableMouseDownX = -1;
	private int tableMouseDownY = -1;
	
	public static void main(String[] args) throws Exception {
		new MainUi().open();
	}
	
	public MainUi() {
		clipboard = new Clipboard(Display.getCurrent());
	}

	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlMain.open();
		shlMain.layout();
		
		while (!shlMain.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create the dialog controls.
	 */
	protected void createContents() {
		shlMain = new Shell();
		shlMain.setImage(SWTResourceManager.getImage(MainUi.class, "/gfx/ahlp.ico"));
		shlMain.setSize(616, 450);
		shlMain.setText(APP_NAME);
		shlMain.setMinimumSize(shlMain.getBounds().width, shlMain.getBounds().height);
		
		Display display = shlMain.getDisplay();
		int mw = display.getPrimaryMonitor().getBounds().width;
		int mh = display.getPrimaryMonitor().getBounds().height;
		shlMain.setLocation(mw / 2 - shlMain.getSize().x / 2, mh / 2 - shlMain.getSize().y / 2);
		
		shlMain.addListener (SWT.Resize,  new Listener () {
		    public void handleEvent(Event e) {
		    	doShellResize();
		    }
		});
		
		Label lblLogFile = new Label(shlMain, SWT.NONE);
		lblLogFile.setBounds(10, 14, 44, 15);
		lblLogFile.setText("Logs:");
		
		comboLogFiles = new Combo(shlMain, SWT.READ_ONLY);
		comboLogFiles.setBounds(60, 11, 448, 23);
		
		btnSelectLogsDir = new Button(shlMain, SWT.NONE);
		btnSelectLogsDir.setToolTipText("Open folder");
		btnSelectLogsDir.setImage(SWTResourceManager.getImage(MainUi.class, "/gfx/farmfresh/folder.ico"));
		btnSelectLogsDir.setBounds(514, 11, 36, 24);
		btnSelectLogsDir.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				selectLogFiles();
			}
		});
		
		Label seperatorFilter = new Label(shlMain, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.SHADOW_IN);
		seperatorFilter.setText("Filter");
		seperatorFilter.setBounds(10, 37, 591, 11);
		
		Label lblFilter = new Label(shlMain, SWT.NONE);
		lblFilter.setBounds(10, 53, 44, 15);
		lblFilter.setText("Filter:");
		
		comboFilter = new Combo(shlMain, SWT.READ_ONLY);
		comboFilter.setBounds(60, 50, 169, 23);
		comboFilter.select(0);
		
		Label lblFilterValue = new Label(shlMain, SWT.NONE);
		lblFilterValue.setText("Value:");
		lblFilterValue.setBounds(246, 53, 36, 15);
		
		textFilterValue = new Text(shlMain, SWT.BORDER);
		textFilterValue.setBounds(288, 50, 220, 22);
		
		btnFilter = new Button(shlMain, SWT.NONE);
		btnFilter.setToolTipText("Apply filter");
		btnFilter.setImage(SWTResourceManager.getImage(MainUi.class, "/gfx/farmfresh/filter.ico"));
		btnFilter.setBounds(514, 49, 36, 24);
		btnFilter.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				doFilter();
			}
		});
		
		btnClearFilter = new Button(shlMain, SWT.NONE);
		btnClearFilter.setToolTipText("Clear filter");
		btnClearFilter.setImage(SWTResourceManager.getImage(MainUi.class, "/gfx/farmfresh/filter_clear.ico"));
		btnClearFilter.setBounds(556, 49, 36, 24);
		btnClearFilter.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				enableControls(false);
				lblState.setText("Clearing filter...");
				logController.clearFilter();
				textFilterValue.setText("");
				comboFilter.select(0);
			}
		});
		
		btnInfo = new Button(shlMain, SWT.CASCADE);
		btnInfo.setToolTipText("Multitool");
		btnInfo.setImage(SWTResourceManager.getImage(MainUi.class, GFX_ICON_PATH));
		btnInfo.setBounds(556, 11, 36, 24);
		btnInfo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				InfoUi info = new InfoUi(shlMain, SWT.DIALOG_TRIM | SWT.CLOSE | SWT.APPLICATION_MODAL);
				info.open();
			}
		});
		
		tableContainer = new ScrolledComposite(shlMain, SWT.H_SCROLL | SWT.V_SCROLL);
		tableContainer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		tableContainer.setBounds(0, 102, 601, 291);
		tableContainer.setExpandHorizontal(true);
		tableContainer.setExpandVertical(true);
		
		lblState = new Label(shlMain, SWT.BORDER);
		lblState.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));
		lblState.setText("Ready...");
		lblState.setBounds(0, 394, 610, 18);
		
		checkRegExpr = new Button(shlMain, SWT.CHECK);
		checkRegExpr.setBounds(288, 79, 138, 16);
		checkRegExpr.setText("Regular expression");
		
		Link btnHelp = new Link(shlMain, SWT.NONE);
		btnHelp.setBounds(482, 78, 25, 15);
		btnHelp.setText("<a>Help</a>");
		
		btnHelp.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				doHelpRegex();
			}
		});
		
		enableFilterControls(false);
	}
	
	private void enableFilterControls(boolean enabled) {
		comboFilter.setEnabled(enabled);
		textFilterValue.setEnabled(enabled);
		btnFilter.setEnabled(enabled);
		btnClearFilter.setEnabled(enabled);
		checkRegExpr.setEnabled(enabled);
	}
	
	/**
	 * Enables/disables controls.
	 * 
	 * @param enabled true/false.
	 */
	private void enableControls(boolean enabled) {
		btnSelectLogsDir.setEnabled(enabled);
		btnInfo.setEnabled(enabled);
		
		enableFilterControls(enabled);
	}
	
	/**
	 * Display directory selection dialog and load all access logs from
	 * that directory.
	 */
	private void selectLogFiles() {
		FileDialog selectLogs = new FileDialog(shlMain, SWT.MULTI);
		selectLogs.setText("Select log files...");
		selectLogs.open();
		
		if(selectLogs.getFileNames().length > 0) {
			List<File> logFiles = new ArrayList<File>();
			comboLogFiles.removeAll();
			comboLogFiles.add(new StringBuilder().append(selectLogs.getFileNames().length).append(" file(s) selected.").toString());
			comboLogFiles.select(0);
			for(String fileName : selectLogs.getFileNames()) {
				comboLogFiles.add(new StringBuilder().append(selectLogs.getFilterPath()).append("\\").append(fileName).toString());
				logFiles.add(new File(selectLogs.getFilterPath(), fileName));
			}
			
			lblState.setText("Reading access logs...");
			
			try {
				LogParser parser = LogParser.getInstances().get(0);
				
				postLogsLoad(parser);
				
				logController = new LogController(parser);
				logController.addObserver(this);
				logController.loadLogs(logFiles.toArray(new File[logFiles.size()]));
			} catch (ObtainLogParserException e) {
				SwtUtil.showError(shlMain, "Failed to retrieve the parser.");
			} catch (IOException e) {
				SwtUtil.showError(shlMain, "Failed to load the log files.");
			}
		} else {
			// Nothing needs to happen.
		}
	}
	
	private void postLogsLoad(LogParser parser) {
		// Clear filter combo.
		comboFilter.removeAll();
		textFilterValue.setText("");
		
		// Remove the old table and create a new one.
		if(table != null) {
			table.clearAll();
			tableContainer.setContent(table);
		}else{
			// Initialize a new table.
			table = new Table(tableContainer, SWT.VIRTUAL | SWT.FULL_SELECTION);
			table.setHeaderVisible(true);
			table.setLinesVisible(false);
			table.addListener(SWT.SetData, new Listener() {
				public void handleEvent(Event e) {
					
					if(entries != null && !entries.isEmpty()) {
						List<LogEntry> logEntries= entries.getEntries();
						
						if(e.index < logEntries.size()) {
							TableItem item = (TableItem) e.item;
							LogEntry entry = logEntries.get(e.index);
							item.setText(new String[]{"" + (e.index + 1), entry.getHostName(),entry.getDateAsString(), entry.getMethod(),entry.getRequest(), entry.getProtocol(), entry.getReponse()});
						}
					}
				}
			});
			table.addListener(SWT.MouseDoubleClick, new Listener() {
				public void handleEvent(Event e) {
					doSetFilterValue(e.x, e.y);
				}
			 });
			table.addListener(SWT.MouseDown, new Listener() {
				public void handleEvent(Event e) {
					tableMouseDownX = e.x;
					tableMouseDownY = e.y;
				}
			});
		}
		
		
		
		tableContainer.setContent(table);
		tableContainer.setMinSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		// Add popup menu.
		Menu menu = new Menu(shlMain);
		shlMain.setMenu(menu);
		
		MenuItem menuItemCopy = new MenuItem(menu, SWT.NONE);
		menuItemCopy.setText("Copy");
		menuItemCopy.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			
			public void widgetSelected(SelectionEvent e) {
				doCopy();
			}
		});

		MenuItem menuItemCopyRow = new MenuItem(menu, SWT.NONE);
		menuItemCopyRow.setText("Copy Row");
		menuItemCopyRow.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			
			public void widgetSelected(SelectionEvent e) {
				doCopyRow();
			}
		});
		
		// Seperator.
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem menuItemExport = new MenuItem(menu, SWT.NONE);
		menuItemExport.setText("Export to CSV");
		menuItemExport.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				doExportCsv();
			}
		});
		
		table.setMenu(menu);
		
		TableColumn nrColumn = new TableColumn(table, SWT.NONE);
		nrColumn.setText("Nr");
		nrColumn.setWidth(50);
		
		// Add the fields to the filter combo and create the table columns.
		String[] fields = parser.getFields();
		comboFilter.add("None");
		comboFilter.select(0);
		for(int i = 0; i < fields.length; i++) {
			comboFilter.add(fields[i]);
		
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(fields[i]);
			column.setWidth(50);
		}
	}
	
	private void doShellResize() {
    	int stcX = tableContainer.getBounds().x;
    	int stcY = tableContainer.getBounds().y;
    	
    	tableContainer.setBounds(stcX, stcY, shlMain.getClientArea().width - stcX, shlMain.getClientArea().height - stcY - lblState.getBounds().height);
    	lblState.setBounds(lblState.getBounds().x, shlMain.getClientArea().height - lblState.getBounds().height, tableContainer.getBounds().width, lblState.getBounds().height);
	}
	
	private void doFilter() {
		if(comboFilter.getSelectionIndex() > 0) {
			if(!StringUtil.isEmpty(textFilterValue.getText())) {
				enableControls(false);
				lblState.setText("Applying filter...");
				
				try {
					logController.filter(comboFilter.getItem(comboFilter.getSelectionIndex()), textFilterValue.getText(), checkRegExpr.getSelection());
				} catch (LogControllerException e) {
					SwtUtil.showError(shlMain, e.getMessage());
				}
				
				enableControls(true);
			} else {
				SwtUtil.showError(shlMain, "No filter vaue specified.");
			}
		} else {
			SwtUtil.showError(shlMain, "No filter selected.");
		}
	}
	
	private void doHelpRegex() {
		if(Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(new URI("http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html"));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void doSetFilterValue(int x, int y) {
		Point pt = new Point(x, y);
		TableItem item = table.getItem(pt);
		
		if (item == null) {
			return;
		}
		
		for (int i = 0; i < table.getColumnCount(); i++) {
			Rectangle rect = item.getBounds(i);
			if (rect.contains(pt)) {
				textFilterValue.setText(item.getText(i));
				comboFilter.select(i);
			}
		}
	}
	
	private void doCopy() {
		Point pt = new Point(tableMouseDownX, tableMouseDownY);
		TableItem item = table.getItem(pt);
		
		if (item == null) {
			return;
		}
		
		for (int i = 0; i < table.getColumnCount(); i++) {
			Rectangle rect = item.getBounds(i);
			if (rect.contains(pt)) {
				String text = item.getText(i);
				
				TextTransfer t = TextTransfer.getInstance();
				clipboard.setContents(new Object[]{text}, new Transfer[]{t});
			}
		}
	}
	
	private void doCopyRow() {
		Point pt = new Point(tableMouseDownX, tableMouseDownY);
		TableItem item = table.getItem(pt);
		
		if (item == null) {
			return;
		}
		
		StringBuilder strb = new StringBuilder();
		
		for (int i = 1; i < table.getColumnCount(); i++) {
			strb.append(item.getText(i));
			
			if(i < table.getColumnCount() - 1) {
				strb.append(",");
			}
		}
		
		TextTransfer t = TextTransfer.getInstance();
		clipboard.setContents(new Object[]{strb.toString()}, new Transfer[]{t});
	}
	
	private void doExportCsv() {
		FileDialog selectLogs = new FileDialog(shlMain, SWT.SAVE);
		selectLogs.setText("Export to...");
		selectLogs.setFilterExtensions(new String[]{"*.csv", "*.txt", "*.*"});
		selectLogs.setFilterNames(new String[]{"CSV file", "Text file", "All"});
		selectLogs.open();
		
		if(selectLogs.getFileName() != null && selectLogs.getFileName().length() > 0) {
			File outputFile = new File(selectLogs.getFilterPath(), selectLogs.getFileName());
			try {
				if(logController.exportCsv(outputFile)) {
					SwtUtil.showInfo(shlMain, "Export to CSV", "Successfully exported to: " + outputFile.getAbsolutePath());
				}
			} catch (IOException e) {
				SwtUtil.showError(shlMain, "Export to CSV failed.");
			}
		}
	}
	
	/**
	 * Is called when LogController changes.
	 */
	public void update(Observable observable, Object result) {
		final UpdateResult updateResult = (UpdateResult) result;
		entries = updateResult;
		Color filterValueColor = null;
		Device device = Display.getCurrent ();
		StringBuilder statusBuilder = new StringBuilder();
	
		// Apply filter colors.
		if(updateResult.isFiltered()) {
			filterValueColor = new Color(device, 243, 241, 152);
			statusBuilder.append("Filter: ");
		} else {
			filterValueColor = new Color(device, 255, 255, 255);
		}
		
		textFilterValue.setBackground(filterValueColor);
		
		// Add everything to the table.
		if(entries != null && entries.size() > 0) {
			table.clearAll();
			table.setItemCount(entries.size());
			
			statusBuilder.append("Showing ").append(entries.size()).append(" of ").append(updateResult.getTotalEntries());
		} else {
			table.removeAll();
			statusBuilder.append("Nothing found.");
		}
		
		lblState.setText(statusBuilder.toString());
		enableControls(true);
	}
}
