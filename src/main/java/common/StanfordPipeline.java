package common;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created by aabhas on 7/22/14.
 * Creates a primed and ready pipeline for all your NLP needs.
 */
public class StanfordPipeline {

  private StanfordCoreNLP pipeline = null;
  static Logger logger = Logger.getLogger(StanfordPipeline.class);

  public StanfordPipeline() {
    logger.info("Building NLP Pipeline");
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, sentiment");
    this.pipeline = new StanfordCoreNLP(props);
    logger.info("NLP Pipeline is ready to use...");
  }

  public StanfordCoreNLP getPipeline() {
    return this.pipeline;
  }
}
