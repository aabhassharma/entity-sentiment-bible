package runner;


import common.BibleReader;
import common.StanfordPipeline;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by aabhas on 7/22/14.
 */
public class AnalyzeBible {

  static Logger logger = Logger.getLogger(AnalyzeBible.class);

  public static void main(String a[]){
    BasicConfigurator.configure();
    logger.info("Starting bible analysis...");
    BibleReader bibleReader = null;
    StanfordPipeline pipeline = new StanfordPipeline();
    try {
      bibleReader = new BibleReader();
    }
    catch (IOException e) {
      logger.error("Uh-oh. Today isn't the best day to read the bible...");
    }
    finally {
      try {
        if (bibleReader != null) {
          bibleReader.closeTheBible();
        }
      }
      catch (Exception e) {

      }
    }
    logger.info("The bible is now officially closed.");

  }
}
