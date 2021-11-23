package edu.sysu.pmglab.suranyi.unifyIO.options;

import static edu.sysu.pmglab.suranyi.unifyIO.options.OperateOptions.*;

/**
 * @author suranyi
 */

public enum FileOptions {
    /**
     * 默认读方法
     */
    DEFAULT_READER(CACHE, REOPEN, PATH_OPERA),

    /**
     * 默认写方法
     */
    DEFAULT_WRITER(PATH_OPERA),
    /**
     * 默认追加方法
     */
    DEFAULT_APPEND(REOPEN, PATH_OPERA),

    /**
     * GZ 格式读取方法
     */
    GZIP_READER(CACHE, REOPEN, PATH_OPERA),

    /**
     * BGZIP 格式写方法
     */
    GZIP_WRITER(PATH_OPERA),

    /**
     * BGZIP 格式读取方法
     */
    BGZIP_READER(CACHE, REOPEN, PATH_OPERA),

    /**
     * BGZIP 格式写方法
     */
    BGZIP_WRITER(PATH_OPERA),

    /**
     * BGZIP 格式并行写方法
     */
    PARALLEL_BGZIP_WRITER_2(PATH_OPERA),
    PARALLEL_BGZIP_WRITER_3(PATH_OPERA),
    PARALLEL_BGZIP_WRITER_4(PATH_OPERA),
    PARALLEL_BGZIP_WRITER_5(PATH_OPERA),
    PARALLEL_BGZIP_WRITER_6(PATH_OPERA),
    PARALLEL_BGZIP_WRITER_7(PATH_OPERA),
    PARALLEL_BGZIP_WRITER_8(PATH_OPERA),
    PARALLEL_BGZIP_WRITER_9(PATH_OPERA),
    PARALLEL_BGZIP_WRITER_10(PATH_OPERA),

    /**
     * 管道读取方法
     */
    CHANNEL_READER(CACHE, REOPEN, PATH_OPERA),

    /**
     * 管道写入方法
     */
    CHANNEL_WRITER(PATH_OPERA),

    /**
     * 管道追加方法
     */
    CHANNEL_APPEND(REOPEN, PATH_OPERA),

    /**
     * 外部实现 IFileStream, 默认不具有任何权限
     */
    IFILESTREAM_ACCEPT(CACHE);

    private boolean supportReopen = false;
    private boolean needCache = false;
    private boolean pathOpera = false;

    private static final FileOptions[] PARALLEL_BGZIP_WRITER_ARRAY = {BGZIP_WRITER, PARALLEL_BGZIP_WRITER_2, PARALLEL_BGZIP_WRITER_3, PARALLEL_BGZIP_WRITER_4,
            PARALLEL_BGZIP_WRITER_5, PARALLEL_BGZIP_WRITER_6, PARALLEL_BGZIP_WRITER_7, PARALLEL_BGZIP_WRITER_8, PARALLEL_BGZIP_WRITER_9, PARALLEL_BGZIP_WRITER_10};

    FileOptions() {
    }

    FileOptions(OperateOptions... options) {
        for (OperateOptions option : options) {
            switch (option) {
                case CACHE:
                    this.needCache = true;
                    break;
                case REOPEN:
                    this.supportReopen = true;
                    break;
                case PATH_OPERA:
                    this.pathOpera = true;
                default:
                    break;
            }
        }
    }

    public boolean isSupportReopen() {
        return this.supportReopen;
    }

    public boolean isNeedCache() {
        return this.needCache;
    }

    public boolean isPathOpera() {
        return this.pathOpera;
    }

    public static FileOptions getParallelBgzipWriter(int threads) {
        return PARALLEL_BGZIP_WRITER_ARRAY[threads - 1];
    }

    public static FileOptions[] getParallelBgzipWriters() {
        return PARALLEL_BGZIP_WRITER_ARRAY;
    }
}
