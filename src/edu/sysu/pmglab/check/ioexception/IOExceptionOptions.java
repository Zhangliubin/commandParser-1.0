package edu.sysu.pmglab.check.ioexception;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

/**
 * @author suranyi
 * @description IO 异常参数
 */
public enum IOExceptionOptions implements IIOExceptionOptions {
    /**
     * IO 异常类型
     */
    IOException,
    FileNotFoundException,
    NoSuchFileException,
    FileAlreadyExistsException,
    FileOccupiedException;

    @Override
    public void throwException(String reason) throws IOException {
        switch (this) {
            case FileAlreadyExistsException:
                throw new FileAlreadyExistsException(reason);
            case FileNotFoundException:
                throw new FileNotFoundException(reason);
            case NoSuchFileException:
                throw new NoSuchFileException(reason);
            case FileOccupiedException:
                throw new FileOccupiedException(reason);
            default:
                throw new IOException(reason);
        }
    }
}