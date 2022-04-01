package edu.sysu.pmglab.check.ioexception;

import java.io.IOException;

/**
 * @author suranyi
 * @description 文件被占用异常
 */

public class FileOccupiedException extends IOException {
    public FileOccupiedException() {
        super();
    }

    public FileOccupiedException(String s) {
        super(s);
    }
}
