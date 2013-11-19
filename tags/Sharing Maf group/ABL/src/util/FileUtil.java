package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.JTextArea;

import org.apache.log4j.Logger;


public class FileUtil {
	private static Logger log = Logger.getLogger(FileUtil.class);
	private static final int BUFFER = 2048;

	public static void main(String[] args) throws Exception {
		zipDir("/home/rishi.das/ABL_wrkspc/untitled folder/test.zip", "/home/rishi.das/ABL_wrkspc/test");
//		unzip("/home/rishi.das/ABL_wrkspc/untitled folder/test.zip");
	}
	
	public static String getNewTmpFile() {
		return System.getProperty("java.io.tmpdir") + File.separator
				+ SystemUtil.getDateTime();
	}

	public static String getNewTmpDir() throws IOException {
		File tmpDir = File.createTempFile(SystemUtil.getDateTime(), "");
		if (tmpDir.exists()) {
			tmpDir.delete();
		}
		tmpDir.mkdir();
		tmpDir.deleteOnExit();
		return tmpDir.getAbsolutePath();
	}

	public static String saveTextFasta(JTextArea pTextSeq) throws Exception {
		File tmpFasta = File.createTempFile(SystemUtil.getDateTime(), "");
		if (tmpFasta.exists()) {
			tmpFasta.delete();
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(tmpFasta);
			pTextSeq.write(writer);
		} catch (IOException exception) {
			throw new Exception("Save oops");
	
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException exception) {
					throw new Exception("Error closing writer");
	
				}
			}
		}
		return tmpFasta.getAbsolutePath();
	}

	public static void delete(String pFileName) {
		File f = new File(pFileName);
	
	    // Make sure the file or directory exists and isn't write protected
	    if (!f.exists())
	      throw new IllegalArgumentException(
	          "Delete: no such file or directory: " + pFileName);
	
	    if (!f.canWrite())
	      throw new IllegalArgumentException("Delete: write protected: "
	          + pFileName);
	
	    // If it is a directory, make sure it is empty
	    if (f.isDirectory()) {
	      String[] files = f.list();
	      if (files.length > 0)
	        throw new IllegalArgumentException(
	            "Delete: directory not empty: " + pFileName);
	    }
	
	    // Attempt to delete it
	    boolean success = f.delete();
	
	    if (!success)
	      throw new IllegalArgumentException("Delete: deletion failed");
	}

	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	
	    // The directory is now empty so delete it
	    return dir.delete();
	}
	
	public static void compress(String pInPath,String pOutPath) throws IOException{
		BufferedInputStream origin = null;
        FileOutputStream dest = new FileOutputStream(pOutPath);
        ZipOutputStream out = new ZipOutputStream(new 
          BufferedOutputStream(dest));
        //out.setMethod(ZipOutputStream.DEFLATED);
        byte data[] = new byte[BUFFER];
        // get a list of files from current directory
        File f = new File(pInPath);
        File files[] = f.listFiles();

        for (int i=0; i<files.length; i++) { if (files[i].isFile()){
	           
	           FileInputStream fi = new 
	             FileInputStream(files[i]);
	           origin = new 
	             BufferedInputStream(fi, BUFFER);
	           ZipEntry entry = new ZipEntry(files[i].getName());
	           out.putNextEntry(entry);
	           int count;
	           while((count = origin.read(data, 0, 
	             BUFFER)) != -1) {
	              out.write(data, 0, count);
	           }
	           origin.close();
        }//if
        }
        out.close();

	}
	
	 public static void zipDir(String pZipFileName, String pDest) throws Exception {
	    File dirObj = new File(pDest);
	    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(pZipFileName));
	    addDir(dirObj, out,"");
	    out.close();
	  }

	 private  static void addDir(File dirObj, ZipOutputStream out, String pBase) throws IOException {
	    File[] files = dirObj.listFiles();
	    byte[] tmpBuf = new byte[1024];

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				if (pBase.length() < 1)
					addDir(files[i], out,
							pBase + "/" + files[i].getName());
				continue;
			}else if(files[i].getName().endsWith("xml") || files[i].getName().endsWith("fasta")){
				FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
				// "/" should be hard coded otherwise problem in windows
				out.putNextEntry(new ZipEntry(pBase + "/"
						+ files[i].getName()));
				int len;
				while ((len = in.read(tmpBuf)) > 0) {
					out.write(tmpBuf, 0, len);
				}
				out.closeEntry();
				in.close();
			}
		}
	  }
	  
	public static void unzip(String strZipFile,String pDest) throws ZipException, IOException {

		
			/*
			 * STEP 1 : Create directory with the name of the zip file
			 * 
			 * For e.g. if we are going to extract c:/demo.zip create c:/demo
			 * directory where we can extract all the zip entries
			 */
			File fSourceZip = new File(strZipFile);
			//String zipPath = strZipFile.substring(0, strZipFile.length() - 4);
			String zipPath = pDest+File.separator+fSourceZip.getName().substring(0, fSourceZip.getName().length() - 4);
			
			File temp = new File(zipPath);
			temp.mkdir();

			/*
			 * STEP 2 : Extract entries while creating required sub-directories
			 */
			ZipFile zipFile = new ZipFile(fSourceZip);
			Enumeration<? extends ZipEntry> e = zipFile.entries();

			while (e.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				File destinationFilePath = new File(zipPath, entry.getName());

				// create directories if required.
				destinationFilePath.getParentFile().mkdirs();

				// if the entry is directory, leave it. Otherwise extract it.
				if (entry.isDirectory()) {
					continue;
				} else {
					
					/*
					 * Get the InputStream for current entry of the zip file
					 * using
					 * 
					 * InputStream getInputStream(Entry entry) method.
					 */
					BufferedInputStream bis = new BufferedInputStream(
							zipFile.getInputStream(entry));

					int b;
					byte buffer[] = new byte[1024];

					/*
					 * read the current entry from the zip file, extract it and
					 * write the extracted file.
					 */
					FileOutputStream fos = new FileOutputStream(
							destinationFilePath);
					BufferedOutputStream bos = new BufferedOutputStream(fos,
							1024);

					while ((b = bis.read(buffer, 0, 1024)) != -1) {
						bos.write(buffer, 0, b);
					}

					// flush the output stream and close it.
					bos.flush();
					bos.close();

					// close the input stream.
					bis.close();
				}
			}
	}
	
	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

}

