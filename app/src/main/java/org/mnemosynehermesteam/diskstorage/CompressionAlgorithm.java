package org.mnemosynehermesteam.diskstorage;

import org.xerial.snappy.Snappy;
import net.jpountz.lz4.LZ4FrameInputStream;
import net.jpountz.lz4.LZ4FrameOutputStream;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public enum CompressionAlgorithm {
  NONE {
    public byte[] compress(byte[] data) {
      return data;
    }
    public byte[] decompress(byte[] data) {
      return data;
    }
  },
  SNAPPY {
    public byte[] compress(byte[] data) throws IOException {
      return Snappy.compress(data);
    }
    public byte[] decompress(byte[] data) throws IOException {
      return Snappy.uncompress(data);
    }
  },
  GZIP {
    public byte[] compress(byte[] data) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
        gzip.write(data);
      }
      return out.toByteArray();
    }
    public byte[] decompress(byte[] data) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data))) {
        gzip.transferTo(out);
      }
      return out.toByteArray();
    }
  },
  LZ4 {
    public byte[] compress(byte[] data) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      try (LZ4FrameOutputStream lz4 = new LZ4FrameOutputStream(out)) {
        lz4.write(data);
      }
      return out.toByteArray();
    }
    public byte[] decompress(byte[] data) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      try (LZ4FrameInputStream lz4 = new LZ4FrameInputStream(new ByteArrayInputStream(data))) {
        lz4.transferTo(out);
      }
      return out.toByteArray();
    }
  };
  public abstract byte[] compress(byte[] data) throws IOException;
  public abstract byte[] decompress(byte[] data) throws IOException;
}