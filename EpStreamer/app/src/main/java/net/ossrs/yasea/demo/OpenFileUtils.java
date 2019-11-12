package net.ossrs.yasea.demo;

import java.io.File;

import android.content.Intent;
import android.net.Uri;

public class OpenFileUtils {

	public static Intent openFile(String filePath){  
		  
        File file = new File(filePath);  
        if(!file.exists()) return null;  
        String end=file.getName().substring(file.getName().lastIndexOf(".") + 1,file.getName().length()).toLowerCase();   
        if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||  
                end.equals("xmf")||end.equals("ogg")||end.equals("wav")){  
            return getAudioFileIntent(filePath);  
        }else if(end.equals("3gp")||end.equals("mp4")){  
            return getAudioFileIntent(filePath);  
        }else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||  
                end.equals("jpeg")||end.equals("bmp")){  
            return getImageFileIntent(filePath);  
        }else if(end.equals("apk")){  
            return getApkFileIntent(filePath);  
        }else if(end.equals("ppt") || end.equals("pptx")){  
            return getPptFileIntent(filePath);  
        }else if(end.equals("xls")){  
            return getExcelFileIntent(filePath);  
        }else if(end.equals("doc") || end.equals("docx")){  
            return getWordFileIntent(filePath);  
        }else if(end.equals("pdf")){  
            return getPdfFileIntent(filePath);  
        }else if(end.equals("chm")){  
            return getChmFileIntent(filePath);  
        }else if(end.equals("txt")){  
            return getTextFileIntent(filePath,false);  
        }else{  
            return getAllIntent(filePath);  
        }  
    }  
      
	/**
     * Open file 
     * @return intent
     */
    public static Intent getAllIntent( String param ) {  
  
        Intent intent = new Intent();    
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param ));  
        intent.setDataAndType(uri,"*/*");   
        return intent;  
    }  
    
    /**
     * Open apk 
     * @return intent
     */
    public static Intent getApkFileIntent( String param ) {  
  
        Intent intent = new Intent();    
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param ));  
        intent.setDataAndType(uri,"application/vnd.android.package-archive");   
        return intent;  
    }  
  
    /**
     * Open video 
     * @return intent
     */
    public static Intent getVideoFileIntent( String param ) {  
  
        Intent intent = new Intent("android.intent.action.VIEW");  
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
        intent.putExtra("oneshot", 0);  
        intent.putExtra("configchange", 0);  
        Uri uri = Uri.fromFile(new File(param ));  
        intent.setDataAndType(uri, "video/*");  
        return intent;  
    }  
  
    /**
     * Open audio 
     * @return intent
     */
    public static Intent getAudioFileIntent( String param ){  
  
        Intent intent = new Intent("android.intent.action.VIEW");  
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
        intent.putExtra("oneshot", 0);  
        intent.putExtra("configchange", 0);  
        Uri uri = Uri.fromFile(new File(param ));  
        intent.setDataAndType(uri, "audio/*");  
        return intent;  
    }  
  
    /**
     * Open html 
     * @return intent
     */
    public static Intent getHtmlFileIntent( String param ){  
  
        Uri uri = Uri.parse(param ).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param ).build();  
        Intent intent = new Intent("android.intent.action.VIEW");  
        intent.setDataAndType(uri, "text/html");  
        return intent;  
    }  
  
    /**
     * Open image 
     * @return intent
     */
    public static Intent getImageFileIntent( String param ) {  
  
        Intent intent = new Intent("android.intent.action.VIEW");  
        intent.addCategory("android.intent.category.DEFAULT");  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        Uri uri = Uri.fromFile(new File(param ));  
        intent.setDataAndType(uri, "image/*");  
        return intent;  
    }  
  
    /**
     * Open ppt 
     * @return intent
     */
    public static Intent getPptFileIntent( String param ){    
  
        Intent intent = new Intent("android.intent.action.VIEW");     
        intent.addCategory("android.intent.category.DEFAULT");     
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
        Uri uri = Uri.fromFile(new File(param ));     
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");     
        return intent;     
    }     
  
    /**
     * Open excel 
     * @return intent
     */
    public static Intent getExcelFileIntent( String param ){    
  
        Intent intent = new Intent("android.intent.action.VIEW");     
        intent.addCategory("android.intent.category.DEFAULT");     
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
        Uri uri = Uri.fromFile(new File(param ));     
        intent.setDataAndType(uri, "application/vnd.ms-excel");     
        return intent;     
    }     
  
    /**
     * Open word 
     * @return intent
     */
    public static Intent getWordFileIntent( String param ){    
  
        Intent intent = new Intent("android.intent.action.VIEW");     
        intent.addCategory("android.intent.category.DEFAULT");     
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
        Uri uri = Uri.fromFile(new File(param ));     
        intent.setDataAndType(uri, "application/msword");     
        return intent;     
    }     
  
    /**
     * Open chm 
     * @return intent
     */
    public static Intent getChmFileIntent( String param ){     
  
        Intent intent = new Intent("android.intent.action.VIEW");     
        intent.addCategory("android.intent.category.DEFAULT");     
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
        Uri uri = Uri.fromFile(new File(param ));     
        intent.setDataAndType(uri, "application/x-chm");     
        return intent;     
    }     
  
    /**
     * Open txt 
     * @return intent
     */
    public static Intent getTextFileIntent( String param, boolean paramBoolean){     
  
        Intent intent = new Intent("android.intent.action.VIEW");     
        intent.addCategory("android.intent.category.DEFAULT");     
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
        if (paramBoolean){     
            Uri uri1 = Uri.parse(param );     
            intent.setDataAndType(uri1, "text/plain");     
        }else{     
            Uri uri2 = Uri.fromFile(new File(param ));     
            intent.setDataAndType(uri2, "text/plain");     
        }     
        return intent;     
    }    
    
    /**
     * Open PDF 
     * @return intent
     */
    public static Intent getPdfFileIntent( String param ){     
  
        Intent intent = new Intent("android.intent.action.VIEW");     
        intent.addCategory("android.intent.category.DEFAULT");     
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
        Uri uri = Uri.fromFile(new File(param ));     
        intent.setDataAndType(uri, "application/pdf");     
        return intent;     
    }  
}
