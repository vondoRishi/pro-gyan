package util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import util.constants.FileName;


public class CustomFilter extends FileFilter {
	 //Accept all directories and all erg files.

	private static CustomFilter mFastaFilter = new CustomFilter(FileName.FASTA);
	private static CustomFilter mFAAFilter = new CustomFilter(FileName.FAA);
	private static CustomFilter mPGCFilter = new CustomFilter(FileName.PGC);
	private String mExtension;
	private CustomFilter(){

	}

	private CustomFilter(String pExtension){
		mExtension = pExtension;
	}
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = FileUtil.getExtension(f);
        if (extension != null) {
            if (extension.equals(mExtension)) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "*."+mExtension;
    }

    public static CustomFilter getFastaFilter() {
		return mFastaFilter;
	}

	public static CustomFilter getFaaFilter() {
		return mFAAFilter;
	}

	public static CustomFilter getPgcFilter() {
		return mPGCFilter;
	}

	
}

