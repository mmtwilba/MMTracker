package com.meixi;

import java.io.File;
import java.io.FileFilter;

public class CustomFileFilter implements FileFilter {
    private String extension;
    private boolean ignore_dirs;

    public CustomFileFilter(String ext, boolean ignoredirs) {
        this.extension = ext.toUpperCase();
        this.ignore_dirs = ignoredirs;
    }

    public boolean accept(File pathname) {
        if (pathname.isHidden()) {
            return false;
        }
        if (pathname.isDirectory() && !this.ignore_dirs) {
            return true;
        }
        if (pathname.getName().toUpperCase().endsWith(this.extension)) {
            return true;
        }
        return false;
    }
}
