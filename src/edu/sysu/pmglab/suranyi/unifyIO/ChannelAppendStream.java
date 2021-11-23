package edu.sysu.pmglab.suranyi.unifyIO;

import java.io.IOException;

import static java.nio.file.StandardOpenOption.*;

/**
 * @Data        :2021/02/22
 * @Author      :suranyi
 * @Contact     :suranyi.sysu@gamil.com
 * @Description :
 */

class ChannelAppendStream extends ChannelWriterStream {
    ChannelAppendStream(String fileName) throws IOException {
        super(fileName, WRITE, APPEND, CREATE);
    }
}
