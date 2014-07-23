package runner;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import common.BibleReader;
import common.StanfordPipeline;
import decompositions.Entity;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * Created by aabhas on 7/22/14.
 */
public class AnalyzeBible {

  static Logger logger = Logger.getLogger(AnalyzeBible.class);

  public static void main(String a[]){
    BasicConfigurator.configure();
    logger.setLevel(Level.OFF);
    logger.info("Starting bible analysis...");
    BibleReader bibleReader = null;
    // dont really need a map containing a map, could just use a map containing a pair (apache commons), but I got lazy
    Map<String, Map<Integer, Integer>> entitySentimenFrequencytMap = Maps.newHashMap();
    StanfordPipeline pipeline = new StanfordPipeline();
    try {
      bibleReader = new BibleReader();
      for (int i = 0; i < 20; i++) {
        String text = bibleReader.readBible().trim();
        if (!StringUtils.isEmpty(text)) {
          analyze(text, entitySentimenFrequencytMap, pipeline);
        }
      }
      for (Map.Entry<String, Map<Integer, Integer>> entry : entitySentimenFrequencytMap.entrySet()) {
        logger.debug(entry.getKey());
        for (Map.Entry<Integer, Integer> entry2 : entry.getValue().entrySet()) {
          double sentiment = entry2.getKey()/entry2.getValue();
          logger.debug("sentiment-" + sentiment);
        }
      }
      writeToFile(entitySentimenFrequencytMap);
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

  private static void analyze(String text, Map<String, Map<Integer, Integer>> entitySentimentFrequencyMap, StanfordPipeline pipeline) {
    Annotation document = new Annotation(text);
    pipeline.getPipeline().annotate(document);
    List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
    for (CoreMap sentence : sentences) {
      List<Entity> entities = Lists.newArrayList();
      for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
        String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
        String word = token.get(CoreAnnotations.TextAnnotation.class);
        int startOffset = token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
        int endOffset = token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
        if (StringUtils.equals("PERSON", ner) || StringUtils.equals("LOCATION", ner)) {
          Entity entity = new Entity(word, ner, startOffset, endOffset);
          if (entitiesOverLap(entity, entities)) {
            mergeEntities(entity, entities);
          }
          else {
            entities.add(entity);
          }
        }
      }
      if (!entities.isEmpty()) {
        Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
        int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
        storeEntitiesAndSentiment(entities, sentiment, entitySentimentFrequencyMap);
      }
    }

  }

  /**
   * Honetly, this is overly complicated. I jsut got lazy and didn't feel like dealing with refactoring the code...
   * @param entities
   * @param sentiment
   * @param entitySentimentFrequencyMap
   */
  private static void storeEntitiesAndSentiment(List<Entity> entities, int sentiment, Map<String, Map<Integer, Integer>> entitySentimentFrequencyMap) {
    for (Entity entity : entities) {
      if (entitySentimentFrequencyMap.containsKey(entity.getWord())) {
        int frequency = 0;
        Map<Integer, Integer> sentimentAndFrequency = entitySentimentFrequencyMap.get(entity.getWord());
        for (Map.Entry<Integer, Integer> entry : sentimentAndFrequency.entrySet()) {
          frequency = 1 + entry.getValue();
          sentiment = sentiment + entry.getKey();
        }
        // clear out the senti-frequency map and insert a new value with one incremented frequency and new summation of senti
        sentimentAndFrequency.clear();
        sentimentAndFrequency.put(sentiment, frequency);
        entitySentimentFrequencyMap.remove(entity.getWord());
        // add the new word sentiment frequency entry in to the map
        entitySentimentFrequencyMap.put(entity.getWord(), sentimentAndFrequency);
      }
      else {
        Map<Integer, Integer> sentimentAndFrequency = Maps.newHashMap();
        sentimentAndFrequency.put(sentiment, 1);
        entitySentimentFrequencyMap.put(entity.getWord(), sentimentAndFrequency);
      }
    }
  }

  /**
   * Check if the newly detected entity is actualy a part of the last entity - this is useful for multi-word entities.
   * @param entity
   * @param entities
   * @return
   */
  public static boolean entitiesOverLap(Entity entity, List<Entity> entities) {
    if (entities.size() == 0) {
      return false;
    }
    Entity previousEntity = entities.get(entities.size() - 1);
    if (entity.getStartOffset() - previousEntity.getEndOffset() <= 1) {
      if (StringUtils.equals(entity.getNer(), previousEntity.getNer())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Merge the newly found entity in to its actual multi-word entity
   * @param entity
   * @param entities
   */
  public static void mergeEntities(Entity entity, List<Entity> entities) {
    logger.debug("Merging entities...");
    Entity previous = entities.get(entities.size() - 1);
    StringBuilder sb = new StringBuilder(previous.getWord());
    if (entity.getStartOffset() - previous.getEndOffset() == 1) {
      sb.append(" ");
    }
    sb.append(entity.getWord());
    String newEntityWord = sb.toString();
    entities.remove(previous);
    previous.setWord(newEntityWord);
    previous.setEndOffset(entity.getEndOffset());
    entities.add(previous);
  }

  private static void writeToFile(Map<String, Map<Integer, Integer>> entitySentimenFrequencytMap){
    PrintWriter writer = null;
    try {
      writer = new PrintWriter("data/results.txt", "UTF-8");
      for (Map.Entry<String, Map<Integer, Integer>> entry : entitySentimenFrequencytMap.entrySet()) {
        for (Map.Entry<Integer, Integer> entry2 : entry.getValue().entrySet()) {
          double sentiment = entry2.getKey()/entry2.getValue();
          writer.println(entry.getKey() + "-" + sentiment);
        }
      }
    }
    catch (Exception e) {

    }
    finally {
      if (writer != null) {
        writer.close();
      }
    }

  }
}
