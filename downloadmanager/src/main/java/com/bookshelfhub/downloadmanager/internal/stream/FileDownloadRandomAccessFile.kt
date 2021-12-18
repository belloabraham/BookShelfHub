package com.bookshelfhub.downloadmanager.internal.stream

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;


class FileDownloadRandomAccessFile(file: File) : FileDownloadOutputStream {

    private var out: BufferedOutputStream
    private var fd: FileDescriptor
    private var randomAccess: RandomAccessFile = RandomAccessFile(file, "rw")


    init {
        fd = randomAccess.getFD()
        out = BufferedOutputStream(FileOutputStream(randomAccess.getFD()))
    }

    @Throws(IOException::class)
    override fun write(b: ByteArray?, off: Int, len: Int) {
        out.write(b, off, len)
    }

    @Throws(IOException::class)
    override fun flushAndSync() {
        out.flush()
        fd.sync()
    }

    @Throws(IOException::class)
    override fun close() {
        out.close()
        randomAccess.close()
    }

    @Throws(IOException::class)
    override fun seek(offset: Long) {
        randomAccess.seek(offset)
    }

    @Throws(IOException::class)
    override fun setLength(newLength: Long) {
        randomAccess.setLength(newLength)
    }

    companion object{
        @Throws(IOException::class)
        fun create(file: File): FileDownloadOutputStream {
            return FileDownloadRandomAccessFile(file)
        }
    }

}