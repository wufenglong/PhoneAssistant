package com.hwttnet.mobileassistant.util;

import java.io.File;
import java.io.FileFilter;

public class FilesFilter implements FileFilter {
	public boolean accept(File file) {
		if (file.isHidden()) {
			return false;
		}

		if (file.isFile()) {
			return true;
		}
		return false;
	}
}
