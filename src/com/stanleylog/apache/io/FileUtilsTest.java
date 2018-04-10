package com.stanleylog.apache.io;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by stanley on 16/10/2017.
 */
public class FileUtilsTest {
    public static void main(String[] args) throws IOException {
        long a = 10000L;
        File f = new File("/tmp/test");
        System.out.println(org.apache.commons.io.FileUtils.checksumCRC32(f));
        System.out.println(FileUtils.byteCountToDisplaySize(new BigInteger("10240000")));
    }
}

