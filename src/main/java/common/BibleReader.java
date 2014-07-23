package common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by aabhas on 7/22/14.
 * Line by line bible reader
 */
public class BibleReader {
  private final String BIBLE_TEXT = "data/AV1611Bible.txt";
  BufferedReader in = null;

  public BibleReader() throws IOException
  {
    this.in = new BufferedReader(new FileReader(BIBLE_TEXT));
  }

  public String readBible() throws IOException
  {
    if (this.in.ready()) {
      return this.in.readLine();
    }
    return null;
  }

  public void closeTheBible() throws IOException
  {
    this.in.close();
  }
}
