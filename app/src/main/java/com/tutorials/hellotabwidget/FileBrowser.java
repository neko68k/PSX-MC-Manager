package com.tutorials.hellotabwidget;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.neko68k.psxmc.R;
 
public class FileBrowser extends ListActivity {
       
        private enum DISPLAYMODE{ ABSOLUTE, RELATIVE; }
 
        private final DISPLAYMODE displayMode = DISPLAYMODE.RELATIVE;
        private List<String> directoryEntries = new ArrayList<String>();
        private File currentDirectory = Environment.getExternalStorageDirectory();
        private boolean dirpick;
        private int savenum;
        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle icicle) {
        		
                super.onCreate(icicle);
                
                // setContentView() gets called within the next line,
                // so we do not need it here.
                
                Intent intent = getIntent();
                dirpick = intent.getBooleanExtra("dirpick", false);
                String title = intent.getStringExtra("title");
                savenum = intent.getIntExtra("savenum", 0);
                if(title!=null)
                	this.setTitle(title);
                final ListView lv = getListView();
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                	public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                        onListItemClick(lv, v,pos,id);
                    }
                });
                if(dirpick){
	                lv.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener(){
		                public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) {
	                        onListItemLongClick(lv, v,pos,id);
	                        return false;
	    		        }
	        		});
                }

        
                browseToRoot();
        }
       
        /**
         * This function browses to the
         * root-directory of the file-system.
         */
        private void browseToRoot() {
                //browseTo(Environment.getExternalStorageDirectory());
        	browseTo(new File("/mnt"));
    }
       
        /**
         * This function browses up one level
         * according to the field: currentDirectory
         */
        private void upOneLevel(){
                if(this.currentDirectory.getParent() != null)
                        this.browseTo(this.currentDirectory.getParentFile());
        }
       
        private void browseTo(final File aDirectory){
                if (aDirectory.isDirectory()){
                        this.currentDirectory = aDirectory;
                        fill(aDirectory.listFiles());
                }else{   
                	if(!dirpick){
	                	Intent i = new Intent();
	                	String fn = new String(aDirectory.getAbsolutePath());
	                	i.putExtra("com.tutorials.hellotabwidget.FN", fn);
	                	//startActivity(i);
	                	setResult(RESULT_OK, i);                	
	                	finish();
                	}
                	
                }
        }
 
        private void fill(File[] files) {
                this.directoryEntries.clear();
               
                // Add the "." and the ".." == 'Up one level'
                try {
                        Thread.sleep(10);
                } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                }
                //this.directoryEntries.add(".");
               
               // if(this.currentDirectory.getParent() != null)
                //        this.directoryEntries.add("..");
               
                switch(this.displayMode){
                        case ABSOLUTE:
                                for (File file : files){
                                	int extOfs = file.getName().lastIndexOf(".");
                            		String ext = file.getName().substring(extOfs, file.getName().length());                                
                                    this.directoryEntries.add(file.getPath());
                                }
                                break;
                        case RELATIVE: // On relative Mode, we have to add the current-path to the beginning
                                int currentPathStringLenght = this.currentDirectory.getAbsolutePath().length();
                                for (File file : files){
                                	if(file.isDirectory())
                                	{
                                		this.directoryEntries.add(file.getAbsolutePath().substring(currentPathStringLenght)+"/");
                                	}
                                	else
                                	{      
                                		if(!dirpick){
                                			
	                                		int extOfs = file.getName().lastIndexOf(".");
	                                		if(extOfs != 0){
		                                		String ext = file.getName().substring(extOfs, file.getName().length());          
		                                		if(ext.contentEquals(".mcd")||ext.contentEquals(".MCD")||ext.contentEquals(".mcr")||ext.contentEquals(".MCR")||
		                                				ext.contentEquals(".GME")||ext.contentEquals(".gme")||ext.contentEquals(".srm")||ext.contentEquals(".SRM")){
		                                			this.directoryEntries.add(file.getAbsolutePath().substring(currentPathStringLenght));
		                                		}
	                                		}
                                		}
                                	}
                                }
                                break;
                }
                Collections.sort(this.directoryEntries);
               
                ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
                                R.layout.file_row, this.directoryEntries);
               
                this.setListAdapter(directoryList);
        }    
        
        @Override
        public void onBackPressed() {    
        	if(this.currentDirectory.getAbsolutePath().contentEquals("/mnt")){
        		setResult(RESULT_CANCELED);
        		finish();
        	}
        	else
        		upOneLevel();
        }
                
        protected void onListItemLongClick(ListView l, View v, int position, long id){
        	        	
        	String fn = new String(this.currentDirectory.getAbsolutePath());
        	
    		int selectionRowID = (int)position;//(int) this.getSelectedItemPosition();
            String selectedFileString = this.directoryEntries.get(selectionRowID);
           
            if(!selectedFileString.equals("..")&&!selectedFileString.equals(".")){
                File clickedFile = null;
                switch(this.displayMode){
                        case RELATIVE:
                                clickedFile = new File(this.currentDirectory.getAbsolutePath()
                                     + this.directoryEntries.get(selectionRowID));
                                break;
                        case ABSOLUTE:
                                clickedFile = new File(this.directoryEntries.get(selectionRowID));
                                break;
                }
                if(clickedFile!=null&&clickedFile.isDirectory()){
                	fn = clickedFile.getAbsolutePath();
                	Intent i = new Intent().putExtra("com.tutorials.hellotabwidget.FN", fn);
                	i.putExtra("savenum", savenum);
                	//startActivity(i);
                	setResult(RESULT_OK, i);                	
                	finish();
                }
            }
        }
        
        
        
        @Override
        protected void onListItemClick(ListView l, View v, int position, long id) {
                int selectionRowID = (int)position;//(int) this.getSelectedItemPosition();
                String selectedFileString = this.directoryEntries.get(selectionRowID);
                if (selectedFileString.equals(".")) {
                        // Refresh
                        this.browseTo(this.currentDirectory);
                } else if(selectedFileString.equals("..")){
                        this.upOneLevel();
                } else {
                        File clickedFile = null;
                        switch(this.displayMode){
                                case RELATIVE:
                                        clickedFile = new File(this.currentDirectory.getAbsolutePath()
                                             + this.directoryEntries.get(selectionRowID));
                                        break;
                                case ABSOLUTE:
                                        clickedFile = new File(this.directoryEntries.get(selectionRowID));
                                        break;
                        }
                        if(clickedFile != null)
                                this.browseTo(clickedFile);
                }
        }
}