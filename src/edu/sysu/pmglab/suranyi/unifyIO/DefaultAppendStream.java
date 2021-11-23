package edu.sysu.pmglab.suranyi.unifyIO;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @Data        :2020/07/02
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :默认文件追写
 */

class DefaultAppendStream extends DefaultWriterStream {
    DefaultAppendStream(String fileName) throws IOException {
        super();
        file = new RandomAccessFile(fileName, "rw");

        // 将文件定位到最后
        file.seek(size());
    }
}
